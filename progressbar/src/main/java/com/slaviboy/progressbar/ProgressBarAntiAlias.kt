package com.slaviboy.progressbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet

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

    private lateinit var backgroundCanvas: Canvas   // canvas for getting the animated background
    private lateinit var backgroundBitmap: Bitmap   // bitmap attached to the background canvas
    private lateinit var clipCanvas: Canvas         // canvas for drawing the clip path
    private lateinit var clipBitmap: Bitmap         // bitmap attached to the clip canvas
    private lateinit var renderCanvas: Canvas       // canvas to clip the background from the clip bitmap, using PorterDuffXfermode
    private lateinit var renderBitmap: Bitmap       // bitmap attached to the render canvas
    private var xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    private var paint: Paint = Paint().apply {
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

    override fun onDraw(canvas: Canvas?) {

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
        canvas?.apply {
            drawBitmap(renderBitmap, 0.0f, 0.0f, paint)
        }

    }
}

