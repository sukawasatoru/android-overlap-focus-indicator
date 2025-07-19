/*
 * Copyright 2025 sukawasatoru
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.focusindicator

import android.graphics.Canvas
import android.view.View
import androidx.leanback.widget.BaseGridView
import androidx.recyclerview.widget.RecyclerView

class ClipDecoration : RecyclerView.ItemDecoration() {
    private val startGap = 16f

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val parent = parent as BaseGridView
        val infinityVertical = parent.resources.displayMetrics.heightPixels.toFloat()
        val infinityHorizontal = parent.resources.displayMetrics.widthPixels.toFloat()

        if (parent.selectedPosition == 0) {
            // clip `startPadding - gap` size.
            if (parent.layoutDirection == View.LAYOUT_DIRECTION_LTR) {
                c.clipRect(
                    parent.paddingLeft.toFloat() - startGap,
                    -infinityVertical,
                    infinityHorizontal,
                    infinityVertical,
                )
            } else {
                c.clipRect(
                    -infinityHorizontal,
                    -infinityVertical,
                    parent.width - parent.paddingRight.toFloat() + startGap,
                    infinityVertical,
                )
            }
            return
        }

        // clip `startPadding` size.
        if (parent.layoutDirection == View.LAYOUT_DIRECTION_LTR) {
            c.clipRect(
                parent.paddingLeft.toFloat(),
                -infinityVertical,
                infinityHorizontal,
                infinityVertical,
            )
        } else {
            c.clipRect(
                -infinityHorizontal,
                -infinityVertical,
                parent.width - parent.paddingRight.toFloat(),
                infinityVertical,
            )
        }
    }
}
