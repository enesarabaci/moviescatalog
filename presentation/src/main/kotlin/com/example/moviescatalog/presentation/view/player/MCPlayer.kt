package com.example.moviescatalog.presentation.view.player

import android.content.Context
import android.view.SurfaceView
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.COMMAND_GET_TEXT
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.drm.DefaultDrmSessionManagerProvider
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.SubtitleView

@OptIn(UnstableApi::class)
internal class MCPlayer(val context: Context) {

    private var exoPlayer: ExoPlayer? = null

    // region Initialization

    fun initializePlayer() {
        exoPlayer = createPlayer()
    }

    private fun createPlayer(): ExoPlayer {
        val builder = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)

        setRenderersFactory(builder)

        val exoPlayer = builder.build()

        exoPlayer.addListener(exoPlayerListener)
        exoPlayer.setAudioAttributes(audioFocus, true)

        return exoPlayer
    }

    private fun setRenderersFactory(
        playerBuilder: ExoPlayer.Builder,
        preferExtensionDecoders: Boolean = true
    ) {
        val extensionRendererMode =
            if (preferExtensionDecoders)
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
            else
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON

        val renderersFactory: RenderersFactory = DefaultRenderersFactory(context)
            .setEnableDecoderFallback(true)
            .setExtensionRendererMode(extensionRendererMode)

        playerBuilder.setRenderersFactory(renderersFactory)
    }

    private val httpDataSourceFactory: DefaultHttpDataSource.Factory by lazy {
        DefaultHttpDataSource.Factory()
    }

    private val mediaSourceFactory by lazy {
        val drmSessionManagerProvider = DefaultDrmSessionManagerProvider()
        drmSessionManagerProvider.setDrmHttpDataSourceFactory(httpDataSourceFactory)

        DefaultMediaSourceFactory(context)
            .setDataSourceFactory(DefaultDataSource.Factory(context, httpDataSourceFactory))
            .setDrmSessionManagerProvider(drmSessionManagerProvider)
    }

    private var minBufferDurationMs: Int? = null
    private var maxBufferDurationMs: Int? = null

    private val loadControl: LoadControl
        get() {
            return DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    minBufferDurationMs ?: DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                    maxBufferDurationMs ?: DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
                )
                .build()
        }

    private val audioFocus by lazy {
        AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()
    }

    // endregion

    // region State

    val playerState: State
        get() {
            if (!firstFrameRendered) {
                return State.Buffering
            }

            return when (exoPlayer?.playbackState) {
                STATE_IDLE -> {
                    if (exoPlayer?.playerError != null)
                        State.Error
                    else
                        State.Idle
                }

                STATE_BUFFERING -> State.Buffering
                STATE_READY -> if (exoPlayer?.isPlaying == true) State.Playing else State.Paused
                STATE_ENDED -> State.Ended
                else -> State.Nothing
            }
        }

    enum class State {
        Nothing,
        Idle,
        Buffering,
        Playing,
        Paused,
        Ended,
        Error;
    }

    var firstFrameRendered = false
        private set

    private var firstFrameLoaded = false
        set(value) {
            if (field == value)
                return

            field = value

            firstFrameControl()
        }

    private var videoDataLoaded = false
        set(value) {
            if (field == value)
                return

            field = value

            firstFrameControl()
        }

    private fun firstFrameControl() {
        if (firstFrameLoaded && videoDataLoaded) {
            firstFrameRendered = true
        }
    }

    // endregion

    // region Listener

    fun triggerListeners(invoke: (MCPlayerListener) -> Unit) {
        val listeners = mutableListOf<MCPlayerListener>()
        listeners.addAll(this.listeners)
        listeners.forEach { invoke(it) }
    }

    private val exoPlayerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)

            // added for handling playing and paused states
            exoPlayer?.playbackState?.let { playbackState ->
                onPlaybackStateChanged(playbackState)
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)

            if (playerState == State.Idle) {
                firstFrameRendered = false
                triggerListeners { it.onRenderedFirstFrame(false) }
            }

            triggerListeners { it.onPlaybackStateChanged(playerState) }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)

            triggerListeners { it.onPlayerError(error) }
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)

            triggerListeners { it.onVideoSizeChanged(videoSize) }
        }

        override fun onCues(cueGroup: CueGroup) {
            super.onCues(cueGroup)

            triggerListeners { it.onCues(cueGroup) }
        }

        override fun onRenderedFirstFrame() {
            super.onRenderedFirstFrame()

            firstFrameLoaded = true
        }
    }

    internal interface MCPlayerListener {
        fun onRenderedFirstFrame(firstFrameRendered: Boolean) {}
        fun onPlaybackStateChanged(state: State) {}
        fun onPlayerError(error: PlaybackException) {}
        fun onVideoSizeChanged(videoSize: VideoSize) {}
        fun onCues(cueGroup: CueGroup) {}
        fun onCurrentTimeChanged(currentTime: Long) {}
        fun onMaxTimeChanged(maxTime: Long) {}
        fun onBufferedTimeChanged(bufferedTime: Long) {}
    }

    private var listeners = mutableListOf<MCPlayerListener>()

    fun addMCPlayerListener(listener: MCPlayerListener) {
        listeners.add(listener)
    }

    fun removeMCPlayerListener(listener: MCPlayerListener) {
        listeners.remove(listener)
    }

    // endregion

    // region Time & Duration

    val isLiveStream get() = exoPlayer?.isCurrentMediaItemLive == true

    val minTime: Long = 0

    val maxTime get() = exoPlayer?.duration?.coerceAtLeast(0) ?: 0L

    val currentTime get() = exoPlayer?.currentPosition?.coerceAtLeast(0) ?: 0L

    val bufferedDuration get() = if (!isLiveStream) (exoPlayer?.totalBufferedDuration ?: 0) else 0L

    val bufferedTime get() = currentTime + bufferedDuration

    val duration get() = maxTime - minTime

    // endregion

    // region Timer

    private var lastCurrentTime: Long? = null
    private var lastMaxTime: Long? = null
    private var lastBufferedTime: Long? = null

    private val secondsTimer by lazy {
        Timer(1000) {

            if (lastCurrentTime != currentTime) {
                lastCurrentTime = currentTime
                triggerListeners { it.onCurrentTimeChanged(currentTime) }
            }

            if (lastMaxTime != maxTime && maxTime != 0L) {
                lastMaxTime = maxTime
                triggerListeners { it.onMaxTimeChanged(maxTime) }
            }

            if (lastBufferedTime != bufferedTime && bufferedTime != 0L) {
                lastBufferedTime = bufferedTime
                triggerListeners { it.onBufferedTimeChanged(bufferedTime) }
            }
        }
    }

    // endregion

    // region Playback Control Methods

    fun clear() {
        exoPlayer?.release()
        exoPlayer = null
        secondsTimer.stop()
    }

    fun start(videoData: VideoData) {
        val player = exoPlayer ?: return

        val mediaItem = videoData.mediaItem ?: return

        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true

        videoDataLoaded = true

        secondsTimer.start()
    }

    private var surfaceView: SurfaceView? = null
    private var subtitleView: SubtitleView? = null

    fun updatePlayerViews(
        surfaceView: SurfaceView,
        subtitleView: SubtitleView
    ) {
        this.surfaceView = surfaceView
        this.subtitleView = subtitleView

        applyPlayerViews()
    }

    private fun applyPlayerViews() {
        if (surfaceView != null)
            exoPlayer?.setVideoSurfaceView(surfaceView)

        if (exoPlayer?.isCommandAvailable(COMMAND_GET_TEXT) == true)
            subtitleView?.setCues(exoPlayer?.currentCues?.cues)
    }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun seekTo(timeMs: Long) {
        exoPlayer?.seekTo(timeMs)
    }

    private var isAutoPaused = false

    fun autoPause() {
        if (exoPlayer?.playWhenReady == true) {
            isAutoPaused = true
            pause()
        }
    }

    fun autoResume() {
        if (isAutoPaused) {
            isAutoPaused = false
            play()
        }
    }

    // endregion
}