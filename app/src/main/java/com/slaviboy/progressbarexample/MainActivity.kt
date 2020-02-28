package com.slaviboy.progressbarexample

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.Animatable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import com.slaviboy.progressbar.ProgressBar

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

class MainActivity : AppCompatActivity() {

    lateinit var progressBars: ArrayList<ProgressBar>     // reference to all progress bars
    lateinit var animator: ValueAnimator                  // animator for updating loading percentage for all progress bars

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hideSystemUI()

        // get all progress bars
        progressBars = arrayListOf(
            findViewById(R.id.progress_bar0),
            findViewById(R.id.progress_bar1),
            findViewById(R.id.progress_bar2),
            findViewById(R.id.progress_bar3),
            findViewById(R.id.progress_bar4),
            findViewById(R.id.progress_bar5),
            findViewById(R.id.progress_bar6),
            findViewById(R.id.progress_bar7),
            findViewById(R.id.progress_bar8)
        )

        // start all stripe animations for each progressbar
        progressBars.forEach {
            it.start()
        }
    }

    /**
     * Update load percentage fro all progress bars, using value animator
     * for float values.
     */
    fun updatePercentage(v: View) {

        if (::animator.isInitialized) {
            animator.cancel()
        }

        animator = ValueAnimator.ofFloat(0f, 100.0f)
        animator.addUpdateListener { animation ->

            val percentage = animation.animatedValue as Float
            progressBars.forEach {
                it.percentage = percentage
            }

        }
        animator.duration = 3500
        animator.start()
    }

    /**
     * Hide the system UI, for full screen
     */
    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}