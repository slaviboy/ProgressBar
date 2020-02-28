package com.slaviboy.progressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver

// Copyright (C) 2020 Stanislav Georgiev
//  https://github.com/slaviboy
//
//	This program is free software: you can redistribute it and/or modify
//	it under the terms of the GNU Affero General Public License as
//	published by the Free Software Foundation, either version 3 of the
//	License, or (at your option) any later version.
//
//	This program is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU Affero General Public License for more details.
//
//	You should have received a copy of the GNU Affero General Public License
//	along with this program.  If not, see <http://www.gnu.org/licenses/>.

/**
 * Simple Progress Bar class that uses vector and animation, the properties
 * like animation speed and progress bar color are set using the xml files.
 */
open class ProgressBar : androidx.appcompat.widget.AppCompatImageView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setAttributes(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setAttributes(context, attrs, defStyleAttr)
    }

    /**
     * Method called to get the xml attributes and then used them, as properties
     * for the class
     */
    protected fun setAttributes(context: Context?, attrs: AttributeSet?, defStyleAttr: Int = 0) {
        val attributes =
            context!!.obtainStyledAttributes(attrs, R.styleable.ProgressBar, defStyleAttr, 0)

        // set initial percentage
        percentage = attributes.getFloat(R.styleable.ProgressBar_percentage, 0.0f)

        // set initial corner radii
        val cornerRadius = attributes.getDimension(R.styleable.ProgressBar_corner_radius, 0.0f)
        topLeftRadius = attributes.getDimension(R.styleable.ProgressBar_top_left_corner_radius, cornerRadius)
        topRightRadius = attributes.getDimension(R.styleable.ProgressBar_top_right_corner_radius, cornerRadius)
        bottomRightRadius = attributes.getDimension(R.styleable.ProgressBar_bottom_right_corner_radius, cornerRadius)
        bottomLeftRadius = attributes.getDimension(R.styleable.ProgressBar_bottom_left_corner_radius, cornerRadius)

        // if stripe animation should auto start
        isStarted = attributes.getBoolean(R.styleable.ProgressBar_start_animation, false)
        attributes.recycle()
    }

    // loading percentage
    var percentage: Float = 0.0f
        set(value) {
            require(value in 0.0..100.0) { "Percentage must be in range between 0 and 100" }
            field = value
            onUpdate()
            invalidate()
        }

    var topLeftRadius: Float = 0.0f
        set(value) {
            require(value >= 0.0f) { "Top-Left radius must be positive!" }
            cornerRadii[0] = value
            cornerRadii[1] = value
            field = value
        }

    var topRightRadius: Float = 0.0f
        set(value) {
            require(topRightRadius >= 0.0f) { "Top-Right radius must be positive!" }
            cornerRadii[2] = value
            cornerRadii[3] = value
            field = value
        }

    var bottomRightRadius: Float = 0.0f
        set(value) {
            require(bottomRightRadius >= 0.0f) { "Bottom-Right radius must be positive!" }
            cornerRadii[4] = value
            cornerRadii[5] = value
            field = value
        }

    var bottomLeftRadius: Float = 0.0f
        set(value) {
            require(bottomLeftRadius >= 0.0f) { "Bottom-Left radius must be positive!" }
            cornerRadii[6] = value
            cornerRadii[7] = value
            field = value
        }

    // corner radii as a float array
    private var cornerRadii: FloatArray = floatArrayOf(
        0f, 0f,   // Top left radius in px
        0f, 0f,   // Top right radius in px
        0f, 0f,   // Bottom right radius in px
        0f, 0f    // Bottom left radius in px
    )

    protected lateinit var clipPathBound: RectF        // the bound for the clip path
    protected lateinit var backgroundPathBound: RectF  // the bound for the background path
    protected lateinit var clipPath: Path              // path that will be used to clip the canvas
    protected lateinit var backgroundPath: Path        // the whole path on 100% load, used to merge paths
    protected lateinit var animatable: Animatable      // animatable object for the progressbar stripes
    protected var isStarted: Boolean = false           // whether animation is started
    protected var applyClipping: Boolean = true        // whether clipping should be applied

    companion object {

        /**
         * Inline function that is called, when the final measurement is made and
         * the view is about to be draw.
         */
        inline fun View.afterMeasured(crossinline f: View.() -> Unit) {
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (measuredWidth > 0 && measuredHeight > 0) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        f()
                    }
                }
            })
        }
    }

    init {
        this.afterMeasured {
            onUpdate()
            initBackgroundPath()

            // start the stripe animation
            animatable = drawable as Animatable
            if (isStarted) {
                start()
            }
        }
    }

    /**
     * Called only once, when the final measurement is made to initialize
     * the background path
     */
    protected fun initBackgroundPath() {

        backgroundPathBound = RectF(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            width.toFloat() - paddingRight.toFloat(),
            height.toFloat() - paddingBottom.toFloat()
        )

        backgroundPath = Path()
        backgroundPath.addRoundRect(
            backgroundPathBound,
            cornerRadii,
            Path.Direction.CW
        )
    }

    /**
     * Method called when the percentage is changed, to update the
     * clip bound and path, before redrawing.
     */
    protected fun onUpdate() {

        // update the bound for the clip path
        clipPathBound = RectF(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            width.toFloat() * (percentage / 100.0f) - paddingRight.toFloat(),
            height.toFloat() - paddingBottom.toFloat()
        )

        // create round rectangle for the clip path, API >= 21
        clipPath = Path()
        clipPath.addRoundRect(
            clipPathBound,
            cornerRadii,
            Path.Direction.CW
        )
    }

    /**
     * Start the stripe animation, if the animatable is initialized
     */
    fun start() {
        if (::animatable.isInitialized && !animatable.isRunning) {
            animatable.start()
        }
        isStarted = true
    }

    /**
     * Stop the stripe animation, if the animatable is initialized
     */
    fun stop() {
        if (::animatable.isInitialized && animatable.isRunning) {
            animatable.stop()
        }
        isStarted = false
    }



    override fun onDraw(canvas: Canvas?) {

        if (applyClipping) {
            canvas?.save()

            // make path logical operation to combine background path and clip path
            if (percentage < 100) {
                clipPath.op(backgroundPath, Path.Op.INTERSECT)
            }

            canvas?.clipPath(clipPath)
            super.onDraw(canvas)
            canvas?.restore()
        } else {
            super.onDraw(canvas)
        }
    }
}

