package com.example.moviescatalog.presentation.extension

import android.content.Context
import com.example.ui.R

fun Context.dpToPx(dp: Int): Int {
    return dp * resources.displayMetrics.density.toInt()
}

fun Context.isTablet(): Boolean {
    return resources.getBoolean(R.bool.isTablet)
}