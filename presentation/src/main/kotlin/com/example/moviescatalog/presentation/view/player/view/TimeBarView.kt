package com.example.moviescatalog.presentation.view.player.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toRectF
import com.example.moviescatalog.presentation.extension.dpToPx
import com.example.ui.R
import kotlin.math.min
import androidx.core.content.withStyledAttributes

class TimeBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private fun triggerListeners(invoke: (TimeBarViewListener) -> Unit) {
        val listeners = mutableListOf<TimeBarViewListener>()
        listeners.addAll(this.timeBarViewListeners)
        listeners.forEach { invoke(it) }
    }

    var minTime: Long = 0
        set(value) {
            field = value
            invalidate()
        }

    var maxTime: Long = 1000
        set(value) {
            if (value <= 0)
                return

            field = value
            invalidate()
        }

    private val totalTime get() = maxTime - minTime

    var currentTime: Long = 0
        set(value) {
            field = value
            invalidate()
        }

    var bufferedTime: Long = 0
        set(value) {
            field = value
            invalidate()
        }

    private var scrubTime: Long? = null
        set(value) {
            field = value
            invalidate()
        }

    var scrubberCenter = 0
        private set

    private val isScrubbing get() = scrubTime != null

    private var barHeight: Int = context.dpToPx(4)

    private var scrubberSize: Int = context.dpToPx(24)
        set(value) {
            field = value

            scrubberAnimator = ValueAnimator.ofInt(0, scrubberSize).also { animator ->
                animator.setDuration(300)
            }
            scrubberDynamicSize = 0
        }

    private var scrubberDynamicSize: Int? = null

    private val scrubberActiveSize: Int
        get() = scrubberDynamicSize ?: scrubberSize

    private var mainBarRect = Rect()

//    private var seekRect = Rect()

    private var scrubberDrawable =
        ResourcesCompat.getDrawable(
            resources,
            R.drawable.bg_timebar_scrubber,
            context.theme
        )

    private fun timeAtSeekPosition(position: Float): Long {
        val x = position.coerceIn(mainBarRect.left.toFloat(), mainBarRect.right.toFloat())
        val ratio = (x - mainBarRect.left) / mainBarRect.width()
        return minTime + (ratio * totalTime).toLong()
    }

    // region Measure / Layout / Draw

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val height = when (heightMode) {
            MeasureSpec.UNSPECIFIED -> scrubberSize
            MeasureSpec.EXACTLY -> heightSize
            else -> min(scrubberSize, heightSize)
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = right - left
        val height = bottom - top
        val barLeft = paddingLeft + (scrubberSize / 2)
        val barRight = width - paddingRight - (scrubberSize / 2)
        val barTop = (height - barHeight) / 2
        mainBarRect.set(barLeft, barTop, barRight, barTop + barHeight)

        invalidate()
    }

    private val playedBarPaint = Paint().also {
        it.setXfermode(PorterDuffXfermode(PorterDuff.Mode.MULTIPLY))

        it.color = resources.getColor(R.color.primary, context.theme)
    }

    private val bufferedBarPaint = Paint().also {
        it.color = resources.getColor(R.color.white, context.theme)
    }

    private val unPlayedBarPaint = Paint().also {
        it.color = resources.getColor(R.color.white_o50, context.theme)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val playedRatio = if (totalTime > 0) (currentTime - minTime) / totalTime.toFloat() else 0f
        val playedRight = mainBarRect.left + (mainBarRect.width() * playedRatio)

        // Draw bar background

        canvas.drawRoundRect(
            mainBarRect.toRectF(),
            barHeight / 2f,
            barHeight / 2f,
            unPlayedBarPaint
        )

        // Draw bar buffered

        val bufferedRatio = (bufferedTime - minTime) / totalTime.toFloat()
        val bufferedRight = maxOf(
            mainBarRect.left + (mainBarRect.width() * bufferedRatio),
            playedRight
        )

        canvas.drawRoundRect(
            mainBarRect.left.toFloat(),
            mainBarRect.top.toFloat(),
            bufferedRight,
            mainBarRect.bottom.toFloat(),
            barHeight / 2f,
            barHeight / 2f,
            bufferedBarPaint
        )

        // Draw bar played

        canvas.drawRoundRect(
            mainBarRect.left.toFloat() - (barHeight / 2f),
            mainBarRect.top.toFloat(),
            playedRight,
            mainBarRect.bottom.toFloat(),
            barHeight / 2f,
            barHeight / 2f,
            playedBarPaint
        )

        // Draw scrubber

        val scrubberTime = scrubTime ?: currentTime

        val scrubberRatio =
            if (totalTime > 0) (scrubberTime - minTime) / totalTime.toFloat() else 0f

        scrubberCenter = (mainBarRect.left + (mainBarRect.width() * scrubberRatio)).toInt()

        val scrubberLeft = scrubberCenter - scrubberActiveSize / 2
        val scrubberTop = mainBarRect.centerY() - scrubberActiveSize / 2

        scrubberDrawable?.setBounds(
            scrubberLeft,
            scrubberTop,
            scrubberLeft + scrubberActiveSize,
            scrubberTop + scrubberActiveSize
        )

        scrubberDrawable?.draw(canvas)
    }

    // endregion

    // region Touch

    private var scrubberAnimator: ValueAnimator? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnabled || maxTime <= minTime)
            return false

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                scrubTime = timeAtSeekPosition(event.x)
                isPressed = true
                parent?.requestDisallowInterceptTouchEvent(true)
                triggerListeners { it.onScrubStart(this, scrubTime ?: currentTime) }

                scrubberAnimator?.start()

                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isScrubbing) {
                    scrubTime = timeAtSeekPosition(event.x)

                    triggerListeners { it.onScrubMove(this, scrubTime ?: currentTime) }
                    return true
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                if (isScrubbing) {
                    currentTime = scrubTime ?: currentTime
                    scrubTime = null
                    isPressed = false
                    parent?.requestDisallowInterceptTouchEvent(false)
                    triggerListeners { it.onScrubEnd(this, scrubTime ?: currentTime) }

                    scrubberAnimator?.reverse()

                    return true
                }
            }
        }

        return super.onTouchEvent(event)
    }

    // endregion

    // region Listener

    interface TimeBarViewListener {
        fun onScrubStart(timeBar: TimeBarView, time: Long)
        fun onScrubMove(timeBar: TimeBarView, time: Long)
        fun onScrubEnd(timeBar: TimeBarView, time: Long)
    }

    private val timeBarViewListeners = mutableListOf<TimeBarViewListener>()

    fun addListener(timeBarViewListener: TimeBarViewListener) {
        if (timeBarViewListeners.any { it == timeBarViewListener })
            return
        timeBarViewListeners.add(timeBarViewListener)
    }

    fun removeListener(timeBarViewListener: TimeBarViewListener) {
        timeBarViewListeners.removeAll { it == timeBarViewListener }
    }

    // endregion

    // region Lifecycle

    init {
        // read attributes from layout xml
        context.withStyledAttributes(attrs, R.styleable.TimeBarView) {
            minTime =
                getInt(R.styleable.TimeBarView_timeBar_minTime, minTime.toInt()).toLong()
            maxTime =
                getInt(R.styleable.TimeBarView_timeBar_maxTime, maxTime.toInt()).toLong()
            currentTime =
                getInt(R.styleable.TimeBarView_timeBar_currentTime, currentTime.toInt())
                    .toLong()
            bufferedTime =
                getInt(R.styleable.TimeBarView_timeBar_bufferedTime, bufferedTime.toInt())
                    .toLong()
            barHeight =
                getDimensionPixelSize(R.styleable.TimeBarView_timeBar_barHeight, barHeight)
            scrubberSize = getDimensionPixelSize(
                R.styleable.TimeBarView_timeBar_scrubberSize,
                scrubberSize
            )
        }

        scrubberAnimator?.addUpdateListener { valueAnimator ->
            scrubberDynamicSize = valueAnimator.animatedValue as Int
            invalidate()
        }
    }

    // endregion
}