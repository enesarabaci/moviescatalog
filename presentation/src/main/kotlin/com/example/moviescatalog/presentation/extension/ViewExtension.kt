package com.example.moviescatalog.presentation.extension

import android.animation.ObjectAnimator
import android.graphics.Rect
import android.view.MotionEvent
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

fun View.showCustomTouchEffect(pressed: Boolean) {
    val alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", if (pressed) 0.5f else 1f)
    val scaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", if (pressed) 0.95f else 1f)
    val scaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", if (pressed) 0.95f else 1f)

    alphaAnimator.interpolator = AccelerateDecelerateInterpolator()
    scaleXAnimator.interpolator = AccelerateDecelerateInterpolator()
    scaleYAnimator.interpolator = AccelerateDecelerateInterpolator()

    alphaAnimator.duration = 75
    scaleXAnimator.duration = 75
    scaleYAnimator.duration = 75

    alphaAnimator.start()
    scaleXAnimator.start()
    scaleYAnimator.start()
}

fun View.enableCustomTouchEffect() {
    setOnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (view.isClickable) showCustomTouchEffect(true)
            MotionEvent.ACTION_CANCEL -> {
                if (view.isClickable)
                    showCustomTouchEffect(false)

                view.isClickable = true
            }

            MotionEvent.ACTION_UP -> {
                if (view.isClickable) {
                    showCustomTouchEffect(false)
                    view.performClick()
                }

                view.isClickable = true
            }

            MotionEvent.ACTION_MOVE -> {
                val clickable = Rect(0, 0, view.width, view.height).contains(
                    motionEvent.x.toInt(),
                    motionEvent.y.toInt()
                )
                if (view.isClickable != clickable) {
                    view.isClickable = clickable
                    showCustomTouchEffect(clickable)
                }
            }
        }

        view.isClickable
    }
}