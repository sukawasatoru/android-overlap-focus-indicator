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
import android.graphics.Rect
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.focusindicator.databinding.CarouselHorizontalGridViewContainerViewBinding
import com.example.focusindicator.databinding.CarouselIndicatorItemBinding

class CarouselHorizontalGridViewContainerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding = CarouselHorizontalGridViewContainerViewBinding.inflate(
        LayoutInflater.from(context),
        this,
    )
    val title: TextView = binding.title
    val subtitle: TextView = binding.subtitle
    val button: TextView = binding.button
    val list: HorizontalGridView = binding.list
    val indicators: LinearLayout = binding.indicators

    private val adapterObserver = createAdapterObserver()
    private val childSelectedListener = createChildSelectedListener()

    var adapter: RecyclerView.Adapter<*>?
        get() = list.adapter
        set(value) {
            list.adapter?.unregisterAdapterDataObserver(adapterObserver)
            value?.registerAdapterDataObserver(adapterObserver)
            list.adapter = value

            updateIndicator()
            updateTitles(list.selectedPosition)
        }

    init {
        list.addOnChildViewHolderSelectedListener(childSelectedListener)

        val startOffset = list.horizontalSpacing
        list.fadingLeftEdge = true
        list.fadingLeftEdgeLength = startOffset
        list.fadingLeftEdgeOffset = -startOffset
        list.smoothScrollSpeedFactor = 4f // measured by hand
        list.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State,
            ) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    // use offset instead of the padding to avoid the fading edge.
                    if (parent.layoutDirection == LAYOUT_DIRECTION_LTR) {
                        outRect.left = startOffset
                    } else {
                        outRect.right = startOffset
                    }
                }
            }
        })
        list.itemAlignmentOffset = -startOffset / 2
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (super.dispatchKeyEvent(event)) {
            return true
        }

        return when (event.keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (event.action == KeyEvent.ACTION_UP || event.repeatCount != 0) {
                    // consume events to prevent scrolling.
                    return true
                }

                if (isRtl) {
                    list.selectNext()
                    true
                } else {
                    list.selectPrev()
                }
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (event.action == KeyEvent.ACTION_UP || event.repeatCount != 0) {
                    // consume events to prevent scrolling.
                    return true
                }

                if (isRtl) {
                    list.selectPrev()
                } else {
                    list.selectNext()
                    true
                }
            }

            // KeyEvent.isConfirmKey
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_SPACE,
            KeyEvent.KEYCODE_NUMPAD_ENTER -> {
                when (event.action) {
                    KeyEvent.ACTION_DOWN -> {
                        if (button.isDuplicateParentStateEnabled) {
                            isPressed = true
                        } else {
                            button.isPressed = true
                        }
                        true
                    }

                    KeyEvent.ACTION_UP -> {
                        if (button.isDuplicateParentStateEnabled) {
                            if (isPressed) {
                                button.performClick()
                            }
                            isPressed = false
                        } else {
                            if (button.isPressed) {
                                button.performClick()
                            }
                            button.isPressed = false
                        }
                        true
                    }

                    else -> false
                }
            }

            else -> false
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (super.dispatchTouchEvent(ev)) {
            return true
        }

        return when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                button.isPressed = true
                true
            }

            MotionEvent.ACTION_UP -> {
                if (button.isPressed) {
                    button.performClick()
                }
                button.isPressed = false
                true
            }

            MotionEvent.ACTION_CANCEL -> {
                button.isPressed = false
                true
            }

            else -> false
        }
    }

    private fun updateIndicator() {
        val adapter = list.adapter ?: run {
            indicators.removeAllViews()
            return
        }

        val isAdd = if (indicators.childCount != adapter.itemCount) {
            indicators.removeAllViews()
            true
        } else {
            false
        }

        val selectedPosition = list.selectedPosition
        val inflater = LayoutInflater.from(indicators.context)
        for (index in 0 until adapter.itemCount) {
            val binding = if (isAdd) {
                CarouselIndicatorItemBinding.inflate(
                    inflater,
                    indicators,
                    true,
                )
            } else {
                CarouselIndicatorItemBinding.bind(indicators.getChildAt(index))
            }
            binding.root.isActivated = index == selectedPosition
        }
        TransitionManager.beginDelayedTransition(indicators)
    }

    private fun updateTitles(position: Int) {
        fun setEmpty() {
            title.text = ""
            subtitle.text = ""
        }

        if (position == RecyclerView.NO_POSITION) {
            setEmpty()
            return
        }

        val adapter = list.adapter as? FocusIndicatorCarouselCardAdapter ?: run {
            setEmpty()
            return
        }

        val item = adapter.currentList[position]
        title.text = item.id
        subtitle.text = "subtitle ${item.id}"
    }

    private fun createAdapterObserver(): RecyclerView.AdapterDataObserver {
        return object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                updateIndicator()
                updateTitles(list.selectedPosition)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                updateIndicator()
                updateTitles(list.selectedPosition)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                updateIndicator()
                updateTitles(list.selectedPosition)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                updateIndicator()
                updateTitles(list.selectedPosition)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                updateIndicator()
                updateTitles(list.selectedPosition)
            }
        }
    }

    private fun createChildSelectedListener(): OnChildViewHolderSelectedListener {
        return object : OnChildViewHolderSelectedListener() {
            override fun onChildViewHolderSelected(
                parent: RecyclerView,
                child: RecyclerView.ViewHolder?,
                position: Int,
                subposition: Int,
            ) {
                updateIndicator()
                updateTitles(position)
            }
        }
    }

    private fun HorizontalGridView.selectPrev(): Boolean {
        val adapter = list.adapter as? FocusIndicatorCarouselCardAdapter ?: return false

        if (adapter.itemCount == 0) {
            return false
        }

        val candidatePosition = list.selectedPosition - 1
        if (candidatePosition < 0) {
            return false
        }

        list.setSelectedPositionSmooth(candidatePosition)
        return true
    }

    private fun HorizontalGridView.selectNext(): Boolean {
        val adapter = list.adapter as? FocusIndicatorCarouselCardAdapter ?: return false

        val candidatePosition = list.selectedPosition + 1
        if (adapter.itemCount <= candidatePosition) {
            return false
        }

        list.setSelectedPositionSmooth(candidatePosition)
        return true
    }
}
