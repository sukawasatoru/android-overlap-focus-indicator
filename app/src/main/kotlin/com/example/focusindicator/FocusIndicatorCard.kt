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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.withSave

@SuppressLint("UseCompatLoadingForDrawables")
class FocusIndicatorCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.focusIndicatorCardStyle,
    defStyleRes: Int = R.style.Widget_FocusIndicatorCard,
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val indicator = resources.getDrawable(R.drawable.card_indicator, context.theme)!!

    private var gap: Int = 0
    private val indicatorWidth: Float = resources.getDimension(R.dimen.card_indicator_width)
    var indicatorGap: Float = 0f
        set(value) {
            field = value
            gap = (indicatorWidth + value).toInt()
        }

    private val path = Path()
    var clipContentCornerRadius: Float = 0f
        set(value) {
            field = value
            updatePath()
            invalidateOutline()
            invalidate()
        }

    init {
        // for onDraw for indicator.
        setWillNotDraw(false)

        outlineProvider = PathOutlineProvider(path)

        context.withStyledAttributes(
            attrs,
            R.styleable.FocusIndicatorCard,
            defStyleAttr,
            defStyleRes,
        ) {
            for (index in 0 until indexCount) {
                when (getIndex(index)) {
                    R.styleable.FocusIndicatorCard_indicatorGap -> {
                        indicatorGap = getDimension(index, 0f)
                    }

                    R.styleable.FocusIndicatorCard_clipContentCornerRadius -> {
                        clipContentCornerRadius = getDimension(index, 0f)
                    }
                }
            }
        }
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)

        // update indicator.
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        updatePath()
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (clipChildren) {
            canvas.withSave {
                clipPath(path)
                super.dispatchDraw(this)
            }
        } else {
            super.dispatchDraw(canvas)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (isFocused) {
            indicator.setBounds(-gap, -gap, width + gap, height + gap)
            indicator.draw(canvas)
        }
    }

    private fun updatePath() {
        path.reset()
        path.addRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            clipContentCornerRadius,
            clipContentCornerRadius,
            Path.Direction.CW,
        )
    }

    class PathOutlineProvider(val path: Path) : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setPath(path)
        }
    }
}
