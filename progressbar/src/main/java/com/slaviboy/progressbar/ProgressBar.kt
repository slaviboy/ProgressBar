/*
* Copyright (C) 2020 Stanislav Georgiev
* https://github.com/slaviboy
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.slaviboy.progressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver

/**
 * Simple Progress Bar class that uses vector and animation, the properties
 * like animation speed and progress bar color are set using the xml files.
 */
open class ProgressBar : androidx.appcompat.widget.AppCompatImageView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setAttributes(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setAttributes(context, attrs, defStyleAttr)
    }

    // loading percentage
    var percentage: Float = 0.0f
        set(value) {
            require(value in 0.0..100.0) { "Percentage must be in range between 0 and 100" }
            field = value
            onUpdate()
            invalidate()
        }

    var cornerRadiusUpperLeft: Float = 0.0f
        set(value) {
            require(value >= 0.0f) { "Top-Left radius must be positive!" }
            cornerRadii[0] = value
            cornerRadii[1] = value
            field = value
        }

    var cornerRadiusUpperRight: Float = 0.0f
        set(value) {
            require(value >= 0.0f) { "Top-Right radius must be positive!" }
            cornerRadii[2] = value
            cornerRadii[3] = value
            field = value
        }

    var cornerRadiusLowerLeft: Float = 0.0f
        set(value) {
            require(value >= 0.0f) { "Bottom-Left radius must be positive!" }
            cornerRadii[6] = value
            cornerRadii[7] = value
            field = value
        }

    var cornerRadiusLowerRight: Float = 0.0f
        set(value) {
            require(value >= 0.0f) { "Bottom-Right radius must be positive!" }
            cornerRadii[4] = value
            cornerRadii[5] = value
            field = value
        }

    // corner radii as a float array
    internal var cornerRadii: FloatArray = floatArrayOf(
        0f, 0f,   // upper left radius in px
        0f, 0f,   // upper right radius in px
        0f, 0f,   // lower right radius in px
        0f, 0f    // lower left radius in px
    )

    internal lateinit var clipPathBound: RectF             // the bound for the clip path
    internal lateinit var backgroundPathBound: RectF       // the bound for the background path
    internal lateinit var clipPath: Path                   // path that will be used to clip the canvas
    internal lateinit var backgroundPath: Path             // the whole path on 100% load, used to merge paths
    internal lateinit var animatable: Animatable           // animatable object for the progressbar stripes
    internal var isStarted: Boolean = false                // whether animation is started
    internal var applyClipping: Boolean = true             // whether clipping should be applied
    internal lateinit var unitsString: Array<String>       // string array containing string unit values, from xml properties
    internal lateinit var displayMetrics: DisplayMetrics   // used when getting the xml units as pixel, for - dp and sp conversions to px

    /**
     * Method called to get the xml attributes and then used them, as properties
     * for the class
     */
    internal fun setAttributes(context: Context?, attrs: AttributeSet?, defStyleAttr: Int = 0) {
        val attributes =
            context!!.obtainStyledAttributes(attrs, R.styleable.ProgressBar, defStyleAttr, 0)

        // set initial percentage
        percentage = attributes.getFloat(R.styleable.ProgressBar_percentage, 0.0f)

        // get units as string, and after final measurement get the sizes in pixels
        unitsString = arrayOf(
            attributes.getString(R.styleable.ProgressBar_corner_radius) ?: "5px",
            attributes.getString(R.styleable.ProgressBar_corner_radius_upper_left) ?: "0px",
            attributes.getString(R.styleable.ProgressBar_corner_radius_upper_right) ?: "0px",
            attributes.getString(R.styleable.ProgressBar_corner_radius_lower_left) ?: "0px",
            attributes.getString(R.styleable.ProgressBar_corner_radius_lower_right) ?: "0px"
        )

        // get metrics used when converting dp and sp to px
        displayMetrics = context.resources.displayMetrics

        // if stripe animation should auto start
        isStarted = attributes.getBoolean(R.styleable.ProgressBar_start_animation, false)
        attributes.recycle()
    }


    /**
     * Get unit value in pixels, by passing unit string value, supported unit types are:
     * dp, sp, px, vw(view width) and vh(view height)
     * @param unitStr unit string
     * @return unit value in pixels
     */
    internal fun getUnit(unitStr: String?): Float {
        if (unitStr == null) {
            return 0.0f
        }

        // get unit value
        val value = unitStr
            .substring(0, unitStr.length - 2)
            .replace("[^0-9?!\\.]".toRegex(), "").toFloat()

        // get unit type(last two characters) from the string
        val unit = unitStr.substring(unitStr.length - 2)

        // return the unit value as pixels
        return when (unit) {
            "dp" -> {
                // dp to px
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, displayMetrics)
            }
            "sp" -> {
                // sp to px
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, displayMetrics)
            }
            "px" -> {
                value
            }
            "vw" -> {
                // as percentage from view width (1.5 = 150%, 2 = 200% ...)
                width * value
            }
            "vh" -> {
                // as percentage from view height (1.5 = 150%, 2 = 200% ...)
                height * value
            }
            else -> {
                0.0f
            }
        }
    }

    /**
     * Init attributes that need current width or height of tha view,
     * and it is only possible after final measurement is made.
     */
    internal fun initAfterMeasure() {

        // xml corner radius attribute
        val cornerRadiusAll = getUnit(unitsString[0])
        var cornerRadiusUpperLeft = getUnit(unitsString[1])
        var cornerRadiusUpperRight = getUnit(unitsString[2])
        var cornerRadiusLowerLeft = getUnit(unitsString[3])
        var cornerRadiusLowerRight = getUnit(unitsString[4])

        // set corner radii for each corner
        if (cornerRadiusUpperLeft == 0f) {
            cornerRadiusUpperLeft = cornerRadiusAll
        }
        if (cornerRadiusUpperRight == 0f) {
            cornerRadiusUpperRight = cornerRadiusAll
        }
        if (cornerRadiusLowerLeft == 0f) {
            cornerRadiusLowerLeft = cornerRadiusAll
        }
        if (cornerRadiusLowerRight == 0f) {
            cornerRadiusLowerRight = cornerRadiusAll
        }

        cornerRadii = floatArrayOf(
            cornerRadiusUpperLeft, cornerRadiusUpperLeft,     // upper left radius in px
            cornerRadiusUpperRight, cornerRadiusUpperRight,   // upper right radius in px
            cornerRadiusLowerRight, cornerRadiusLowerRight,   // lower right radius in px
            cornerRadiusLowerLeft, cornerRadiusLowerLeft      // lower left radius in px
        )
    }

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

        // called when final measurement is made
        this.afterMeasured {
            initAfterMeasure()
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
    internal fun initBackgroundPath() {

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
    internal fun onUpdate() {

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


    override fun onDraw(canvas: Canvas) {

        if (applyClipping) {
            canvas.save()

            // make path logical operation to combine background path and clip path
            if (percentage < 100) {
                clipPath.op(backgroundPath, Path.Op.INTERSECT)
            }

            canvas.clipPath(clipPath)
            super.onDraw(canvas)
            canvas.restore()
        } else {
            super.onDraw(canvas)
        }
    }
}

