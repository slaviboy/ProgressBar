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
import android.graphics.*
import android.util.AttributeSet
 
/**
 * Simple Progress Bar class that uses vector and animation, the properties
 * like animation speed and progress bar color are set using the xml files.
 * This class applies antialias by rendering three bitmaps, the first  is the
 * backgroundBitmap it has the background animation, from the super class. The
 * second is the clip bitmap, it has the clip path drawn on it. And the third
 * one it for rendering the result of clipping between the background and clip
 * bitmaps.
 */
class ProgressBarAntiAlias : ProgressBar {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    internal lateinit var backgroundCanvas: Canvas   // canvas for getting the animated background
    internal lateinit var backgroundBitmap: Bitmap   // bitmap attached to the background canvas
    internal lateinit var clipCanvas: Canvas         // canvas for drawing the clip path
    internal lateinit var clipBitmap: Bitmap         // bitmap attached to the clip canvas
    internal lateinit var renderCanvas: Canvas       // canvas to clip the background from the clip bitmap, using PorterDuffXfermode
    internal lateinit var renderBitmap: Bitmap       // bitmap attached to the render canvas
    internal var xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    internal var paint: Paint = Paint().apply {
        isAntiAlias = true
    }

    init {

        // force stop clipping
        applyClipping = false

        this.afterMeasured {

            // init canvases and bitmaps
            backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            backgroundCanvas = Canvas(backgroundBitmap)

            clipBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            clipCanvas = Canvas(clipBitmap)

            renderBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            renderCanvas = Canvas(renderBitmap)
        }
    }

    override fun onDraw(canvas: Canvas) {

        // draw the animated background
        super.onDraw(backgroundCanvas)
        renderCanvas.drawBitmap(backgroundBitmap, 0.0f, 0.0f, paint)

        // draw clip path
        if (percentage < 100) {
            clipPath.op(backgroundPath, Path.Op.INTERSECT)
        }
        clipBitmap.eraseColor(Color.TRANSPARENT)
        clipCanvas.drawPath(clipPath, paint)

        // draw the clip with DST_IN mode, to make the path clipping from the background
        paint.xfermode = xfermode
        renderCanvas.drawBitmap(clipBitmap, 0.0f, 0.0f, paint)
        paint.xfermode = null

        // draw bitmap with the clipped path
        canvas.apply {
            drawBitmap(renderBitmap, 0.0f, 0.0f, paint)
        }

    }
}

