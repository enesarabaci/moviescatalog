package com.example.moviescatalog.presentation.extension

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.navOptions
import com.example.ui.R

fun NavController.navigatePush(directions: NavDirections) {
    val navOptions = navOptions {
        anim {
            enter = R.anim.screen_push_enter_horizontal
            exit = R.anim.screen_push_exit_horizontal
            popEnter = R.anim.screen_push_pop_enter_horizontal
            popExit = R.anim.screen_push_pop_exit_horizontal
        }
    }

    navigate(directions, navOptions)
}