package com.example.moviescatalog.presentation.view.player

import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.example.ui.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityPlayerBinding
    private var landscapeViewBinding: ActivityPlayerBinding? = null
    private var portraitViewBinding: ActivityPlayerBinding? = null

    private val playerView by lazy {
        PlayerView(this)
    }

    private val mcPlayer by lazy {
        MCPlayer(this)
    }

    private fun setupViewBinding() {

        fun setLandscapeViewBinding() {
            viewBinding = landscapeViewBinding ?: ActivityPlayerBinding.inflate(layoutInflater)
            landscapeViewBinding = viewBinding
        }

        fun setPortraitViewBinding() {
            viewBinding = portraitViewBinding ?: ActivityPlayerBinding.inflate(layoutInflater)
            portraitViewBinding = viewBinding
        }

        val orientation = resources.configuration.orientation

        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                setLandscapeViewBinding()
            }

            Configuration.ORIENTATION_PORTRAIT -> {
                setPortraitViewBinding()
            }

            else -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewBinding()
        setContentView(viewBinding.root)

        initializePlayerView()

        mcPlayer.start(
            VideoData(
                id = "id",
                drmScheme = VideoData.DrmScheme.Widevine,
                url = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd",
                title = "title",
                mediaId = "mediaId",
                licenseUrl = "https://proxy.uat.widevine.com/proxy?video_id=2015_tears&provider=widevine_test"
            )
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        viewBinding.playerContainer.removeView(playerView)
        setupViewBinding()
        viewBinding.playerContainer.addView(playerView)
        setContentView(viewBinding.root)
    }

    private fun initializePlayerView() {
        mcPlayer.initializePlayer()
        playerView.mcPlayer = mcPlayer

        viewBinding.playerContainer.addView(playerView)

        playerView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            width = MATCH_PARENT
            height = MATCH_PARENT
        }


//        playerView.requestListener = this
//        playerView.responseListener = this
//        playerView.listener = this

        // Configure parameters for the picture-in-picture mode. We do this at the first layout of
        // the PlayerView because we use its layout position and size.
//        playerView.doOnLayout { updatePictureInPictureParams() }
//
//        playerView.videoDataPlayState = viewModel.videoDataPlayState
    }

    override fun onDestroy() {
        super.onDestroy()

        mcPlayer.clear()
    }
}