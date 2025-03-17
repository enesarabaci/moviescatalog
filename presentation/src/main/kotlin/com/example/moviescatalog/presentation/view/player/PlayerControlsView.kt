package com.example.moviescatalog.presentation.view.player

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.moviescatalog.presentation.extension.dpToPx
import com.example.moviescatalog.presentation.extension.fadeIn
import com.example.moviescatalog.presentation.extension.fadeOut
import com.example.ui.R
import com.example.ui.databinding.ViewPlayerControlsBinding
import java.util.Locale
import java.util.concurrent.TimeUnit

class PlayerControlsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), TimeBarView.TimeBarViewListener {

    private val binding = ViewPlayerControlsBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    // region Update UI

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

    // endregion

    // region Listener

    interface PlayerControlsViewListener {
        fun onScrubEnd(time: Long)
    }

    private var playerControlsViewListener: PlayerControlsViewListener? = null
    fun setPlayerControlsViewListener(listener: PlayerControlsViewListener) {
        playerControlsViewListener = listener
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
    }

    // endregion

    // region Initialize

    init {
        setBackgroundColor(resources.getColor(R.color.black_o50, context.theme))

        binding.timeBarView.addListener(this)
    }

    // endregion
}