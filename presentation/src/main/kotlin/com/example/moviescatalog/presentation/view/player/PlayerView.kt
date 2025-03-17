package com.example.moviescatalog.presentation.view.player

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.OptIn
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import com.example.ui.databinding.ViewPlayerBinding

@OptIn(UnstableApi::class)
internal class PlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val viewBinding = ViewPlayerBinding.inflate(LayoutInflater.from(context), this)

    var mcPlayer: MCPlayer? = null
        set(value) {
            if (field == value)
                return

            field?.clear()

            if (value == null)
                return

            field = value

            mcPlayer?.updatePlayerViews(
                viewBinding.videoSurfaceView,
                viewBinding.subtitleView
            )

            mcPlayer?.addMCPlayerListener(playerListener)

//            mcPlayer?.let { player ->
//                playerControlsManager = PlayerControlsManager(player, controlsProvider)
//                playerControlsManager?.addListener(controlsListener)
//            }
        }

    private val playerListener = object : MCPlayer.MCPlayerListener {

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)

            if (videoSize.height == 0 || videoSize.width == 0)
                return

            val videoWidth = videoSize.width
            val videoHeight = videoSize.height
            val ratio = videoSize.pixelWidthHeightRatio

            val videoAspectRatio = "${videoWidth * ratio}:${videoHeight}"

            viewBinding.aspectRatioView.updateLayoutParams<LayoutParams> {
                dimensionRatio = videoAspectRatio
            }
        }

        override fun onCues(cueGroup: CueGroup) {
            super.onCues(cueGroup)

            viewBinding.subtitleView.setCues(cueGroup.cues)
        }

        override fun onCurrentTimeChanged(currentTime: Long) {
            super.onCurrentTimeChanged(currentTime)


        }
    }
}