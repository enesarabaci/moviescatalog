package com.example.moviescatalog.presentation.extension

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible

fun View.fade(
    fadeIn: Boolean,
    duration: Int = 200,
    completion: (() -> Unit)? = null,
    makeInvisible: Boolean = false,
    maxAlpha: Float = 1f
) {
    if (fadeIn)
        fadeIn(duration, completion, maxAlpha)
    else
        fadeOut(duration, completion, makeInvisible)
}

fun View.fadeIn(duration: Int = 200, completion: (() -> Unit)? = null, maxAlpha: Float = 1f) {
    if (isVisible && alpha == maxAlpha) {
        completion?.invoke()
        return
    }

    visibility = View.VISIBLE
    val animator = ObjectAnimator.ofFloat(this, "alpha", maxAlpha)
    animator.interpolator = AccelerateDecelerateInterpolator()
    animator.duration = duration.toLong()
    animator.doOnEnd {
        completion?.invoke()
    }
    animator.start()
}

fun View.fadeOut(
    duration: Int = 200,
    completion: (() -> Unit)? = null,
    makeInvisible: Boolean = false
) {
    val requiredVisibility = if (makeInvisible) View.INVISIBLE else View.GONE
    if (visibility == requiredVisibility && alpha == 0f) {
        completion?.invoke()
        return
    }

    val animator = ObjectAnimator.ofFloat(this, "alpha", 0f)
    animator.interpolator = AccelerateDecelerateInterpolator()
    animator.duration = duration.toLong()
    animator.doOnEnd {
        visibility = requiredVisibility
        completion?.invoke()
    }
    animator.start()
}