package com.example.moviescatalog.presentation.extension

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.example.ui.R

fun NavController.navigatePush(
    @IdRes resId: Int,
    args: Bundle? = null
) {
    val navOptions = navOptions {
        anim {
            enter = R.anim.screen_push_enter_horizontal
            exit = R.anim.screen_push_exit_horizontal
            popEnter = R.anim.screen_push_pop_enter_horizontal
            popExit = R.anim.screen_push_pop_exit_horizontal
        }
    }

    navigate(resId, args, navOptions)
}