package com.example.moviescatalog.presentation.view.player

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.moviescatalog.presentation.extension.fadeIn
import com.example.moviescatalog.presentation.extension.fadeOut
import com.example.ui.R
import com.example.ui.databinding.ViewPlayerControlsBinding
import java.util.Locale
import java.util.concurrent.TimeUnit

internal class PlayerControlsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), TimeBarView.TimeBarViewListener {

    private val binding = ViewPlayerControlsBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    // region Update UI

    fun setTitle(title: String?) {
        binding.titleTextView.text = title
    }

    fun updateCurrentTime(currentTime: Long) {
        binding.timeBarView.currentTime = currentTime
        binding.currentTimeTextView.text = formatTime(currentTime)
    }

    fun updateRemainingTime(remainingTime: Long) {
        binding.remainingTimeTextView.text = formatTime(remainingTime)
    }

    fun updateMaxTime(maxTime: Long) {
        binding.timeBarView.maxTime = maxTime
    }

    fun updateBufferedTime(bufferedTime: Long) {
        binding.timeBarView.bufferedTime = bufferedTime
    }

    private fun formatTime(milliseconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60

        return if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    fun playbackStateChanged(state: MCPlayer.State) {
        binding.playPauseButton.visibility = if (state == MCPlayer.State.Buffering)
            View.INVISIBLE
        else
            View.VISIBLE

        val drawable = if (state == MCPlayer.State.Playing)
            R.drawable.ic_pause
        else
            R.drawable.ic_play

        binding.playPauseButton.icon = ResourcesCompat.getDrawable(
            resources,
            drawable,
            context.theme
        )
    }

    fun updateZoomButton(isFullScreen: Boolean) {
        val drawable = if (isFullScreen)
            R.drawable.ic_zoom_out
        else
            R.drawable.ic_zoom_in

        binding.zoomButton.icon = ResourcesCompat.getDrawable(
            resources,
            drawable,
            context.theme
        )
    }

    private val hideTimer = Timer(3000, false) {
        hide()
    }

    fun show() {
        fadeIn()
        hideTimer.start()
    }

    fun hide() {
        fadeOut()
        hideTimer.stop()
    }

    // endregion

    // region Listener

    interface PlayerControlsViewListener {
        fun onScrubEnd(time: Long)
        fun onPlayPauseButtonClicked()
        fun onSeekBackButtonClicked()
        fun onSeekForwardButtonClicked()
        fun onZoomButtonClicked()
        fun onCloseButtonClicked()
    }

    private var playerControlsViewListener: PlayerControlsViewListener? = null
    fun setPlayerControlsViewListener(listener: PlayerControlsViewListener) {
        playerControlsViewListener = listener
    }

    override fun onScrubStart(timeBar: TimeBarView, time: Long) {
        super.onScrubStart(timeBar, time)

        hideTimer.stop()
    }

    override fun onScrubMove(timeBar: TimeBarView, time: Long) {
        super.onScrubMove(timeBar, time)

        val hintView = binding.scrubHintTimeTextView

        hintView.text = formatTime(time)
        hintView.isVisible = true

        val translationX = timeBar.x + timeBar.scrubberCenter - (hintView.width / 2)
        hintView.translationX = translationX.coerceIn(0f, (right - hintView.width).toFloat())
    }

    override fun onScrubEnd(timeBar: TimeBarView, time: Long) {
        super.onScrubEnd(timeBar, time)

        binding.scrubHintTimeTextView.isVisible = false

        playerControlsViewListener?.onScrubEnd(time)

        hideTimer.start()
    }

    // endregion

    // region Initialize

    init {
        setBackgroundColor(resources.getColor(R.color.black_o50, context.theme))

        hideTimer.start()

        binding.timeBarView.addListener(this)

        binding.playPauseButton.setOnClickListener {
            hideTimer.reset()
            playerControlsViewListener?.onPlayPauseButtonClicked()
        }

        binding.seekBackButton.setOnClickListener {
            hideTimer.reset()
            playerControlsViewListener?.onSeekBackButtonClicked()
        }

        binding.seekForwardButton.setOnClickListener {
            hideTimer.reset()
            playerControlsViewListener?.onSeekForwardButtonClicked()
        }

        binding.zoomButton.setOnClickListener {
            playerControlsViewListener?.onZoomButtonClicked()
        }

        binding.closeButton.setOnClickListener {
            playerControlsViewListener?.onCloseButtonClicked()
        }
    }

    // endregion
}