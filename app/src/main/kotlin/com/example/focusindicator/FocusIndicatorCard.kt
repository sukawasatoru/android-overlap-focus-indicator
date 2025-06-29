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

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.example.focusindicator.databinding.FocusIndicatorCardBinding

class FocusIndicatorCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.focusIndicatorCardStyle,
    defStyleRes: Int = R.style.Widget_FocusIndicatorCard,
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding = FocusIndicatorCardBinding.inflate(
        LayoutInflater.from(context),
        this,
    )
    val image: ImageView = binding.image

    private val indicatorWidth = resources.getDimension(R.dimen.card_indicator_width)
    private var indicatorGap: Float = 0f

    init {
        // for onDraw for indicator.
        setWillNotDraw(false)

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
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isFocused) {
            val indicator = ContextCompat.getDrawable(context, R.drawable.card_indicator)!!
            val gap = (indicatorGap + indicatorWidth).toInt()
            indicator.setBounds(-gap, -gap, width + gap, height + gap)
            indicator.draw(canvas)
        }
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)

        // for indicator.
        invalidate()
    }
}
