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
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
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
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding = CarouselHorizontalGridViewContainerViewBinding.inflate(
        LayoutInflater.from(context),
        this,
    )
    val list: HorizontalGridView = binding.list
    val indicators: LinearLayout = binding.indicators

    private val adapterObserver = createAdapterObserver()
    private val childSelectedListener = createChildSelectedListener()

    var adapter: RecyclerView.Adapter<*>?
        get() = list.adapter
        set(value) {
            list.adapter?.unregisterAdapterDataObserver(adapterObserver)
            list.adapter = value
            if (value == null) {
                return
            }

            value.registerAdapterDataObserver(adapterObserver)
            updateIndicator()
        }

    init {
        list.addOnChildViewHolderSelectedListener(childSelectedListener)
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

    private fun createAdapterObserver(): RecyclerView.AdapterDataObserver {
        return object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                updateIndicator()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                updateIndicator()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                updateIndicator()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                updateIndicator()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                updateIndicator()
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
            }
        }
    }
}
