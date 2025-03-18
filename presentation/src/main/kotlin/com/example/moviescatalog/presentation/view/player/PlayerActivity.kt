package com.example.moviescatalog.presentation.view.player

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import com.example.ui.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
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
            binding = landscapeViewBinding ?: ActivityPlayerBinding.inflate(layoutInflater)
            landscapeViewBinding = binding
        }

        fun setPortraitViewBinding() {
            binding = portraitViewBinding ?: ActivityPlayerBinding.inflate(layoutInflater)
            portraitViewBinding = binding
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
        setContentView(binding.root)

        initializePlayerView()

        updateOrientation()

        setOrientationChangeListener()

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

        binding.playerContainer.removeView(playerView)
        setupViewBinding()
        binding.playerContainer.addView(playerView)
        setContentView(binding.root)

        updateOrientation()
        updateSystemNavigationVisibility()
    }

    private fun updateSystemNavigationVisibility() {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> showSystemNavigation()
            Configuration.ORIENTATION_LANDSCAPE -> hideSystemNavigation()
            else -> Unit
        }
    }

    private fun hideSystemNavigation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Pre R
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun showSystemNavigation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                controller.show(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        } else {
            // Pre R
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }

        WindowCompat.setDecorFitsSystemWindows(window, true)
    }

    private var orientationEventListener: OrientationEventListener? = null
    private var ignoreNextConfigurationChange: Int? = null

    private fun setOrientationChangeListener() {
        orientationEventListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (ignoreNextConfigurationChange == null) {
                    disable()
                    return
                }

                if (orientation == ORIENTATION_UNKNOWN)
                    return

                if ((orientation >= 350 || orientation < 10) || (orientation in 170..189) && isRotationEnabled()) {
                    // PORTRAIT

                    if (ignoreNextConfigurationChange == Configuration.ORIENTATION_LANDSCAPE) {
                        requestedOrientation = SCREEN_ORIENTATION_UNSPECIFIED
                        disable()
                    }
                } else if ((orientation in 80..99) || (orientation in 260..279) && isRotationEnabled()) {
                    // LANDSCAPE

                    if (ignoreNextConfigurationChange == Configuration.ORIENTATION_PORTRAIT) {
                        requestedOrientation = SCREEN_ORIENTATION_UNSPECIFIED
                        disable()
                    }
                }
            }
        }
    }

    private fun isRotationEnabled(): Boolean {
        return Settings.System.getInt(
            contentResolver,
            Settings.System.ACCELEROMETER_ROTATION, 0
        ) == 1
    }

    private fun updateOrientation() {
        playerView.updateOrientation(
            isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        )
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private val playerViewListener = object : PlayerView.PlayerViewListener {
        override fun onZoomButtonClicked() {

            val orientation = resources.configuration.orientation

            when (orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> {
                    requestedOrientation = SCREEN_ORIENTATION_SENSOR_PORTRAIT
                    ignoreNextConfigurationChange = Configuration.ORIENTATION_LANDSCAPE
                }

                Configuration.ORIENTATION_PORTRAIT -> {
                    requestedOrientation = SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    ignoreNextConfigurationChange = Configuration.ORIENTATION_PORTRAIT
                }

                else -> {}
            }

            orientationEventListener?.enable()
        }

        override fun onCloseButtonClicked() {
            finish()
        }
    }

    private fun initializePlayerView() {
        mcPlayer.initializePlayer()
        playerView.mcPlayer = mcPlayer

        binding.playerContainer.addView(playerView)

        playerView.setPlayerViewListener(playerViewListener)

        playerView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            width = MATCH_PARENT
            height = MATCH_PARENT
        }
    }

    override fun onStart() {
        super.onStart()

        mcPlayer.autoResume()
    }

    override fun onStop() {
        super.onStop()

        mcPlayer.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        mcPlayer.clear()
        orientationEventListener?.disable()
    }
}