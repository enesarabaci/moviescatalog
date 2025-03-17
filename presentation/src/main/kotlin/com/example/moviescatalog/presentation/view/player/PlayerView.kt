package com.example.moviescatalog.presentation.view.player

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.OptIn
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import com.example.moviescatalog.presentation.extension.dpToPx
import com.example.moviescatalog.presentation.extension.fade
import com.example.ui.databinding.ViewPlayerBinding

@OptIn(UnstableApi::class)
internal class PlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewPlayerBinding.inflate(LayoutInflater.from(context), this)

    // region Player

    var mcPlayer: MCPlayer? = null
        set(value) {
            if (field == value)
                return

            field?.clear()

            if (value == null)
                return

            field = value

            mcPlayer?.updatePlayerViews(
                binding.videoSurfaceView,
                binding.subtitleView
            )

            mcPlayer?.addMCPlayerListener(playerListener)
        }

    // endregion

    // region Listener

    private val playerListener = object : MCPlayer.MCPlayerListener {

        override fun onPlaybackStateChanged(state: MCPlayer.State) {
            super.onPlaybackStateChanged(state)

            binding.bufferingView.fade(state == MCPlayer.State.Buffering)
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)

            if (videoSize.height == 0 || videoSize.width == 0)
                return

            val videoWidth = videoSize.width
            val videoHeight = videoSize.height
            val ratio = videoSize.pixelWidthHeightRatio

            val videoAspectRatio = "${videoWidth * ratio}:${videoHeight}"

            binding.aspectRatioView.updateLayoutParams<LayoutParams> {
                dimensionRatio = videoAspectRatio
            }
        }

        override fun onCues(cueGroup: CueGroup) {
            super.onCues(cueGroup)

            binding.subtitleView.setCues(cueGroup.cues)
        }

        override fun onCurrentTimeChanged(currentTime: Long) {
            super.onCurrentTimeChanged(currentTime)

            binding.playerControlsView.updateCurrentTime(currentTime)

            mcPlayer?.maxTime?.let { maxTime ->
                binding.playerControlsView.updateRemainingTime(maxTime - currentTime)
            }
        }

        override fun onMaxTimeChanged(maxTime: Long) {
            super.onMaxTimeChanged(maxTime)

            binding.playerControlsView.updateMaxTime(maxTime)
        }

        override fun onBufferedTimeChanged(bufferedTime: Long) {
            super.onBufferedTimeChanged(bufferedTime)

            binding.playerControlsView.updateBufferedTime(bufferedTime)
        }
    }

    private val playerControlsViewListener =
        object : PlayerControlsView.PlayerControlsViewListener {

            override fun onScrubEnd(time: Long) {
                mcPlayer?.seekTo(time)
            }
        }

    // endregion

    // region update UI

    fun updateOrientation(isLandscape: Boolean) {
        val playerControlsViewPadding = if (isLandscape)
            context.dpToPx(28)
        else
            context.dpToPx(8)

        binding.playerControlsView.setPadding(playerControlsViewPadding)
    }

    // endregion

    // region Initialize

    init {
        binding.playerControlsView.setPlayerControlsViewListener(playerControlsViewListener)
    }

    // endregion
}