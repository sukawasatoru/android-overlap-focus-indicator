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

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.focusindicator.databinding.ListItemFocusIndicatorCardBinding

open class FocusIndicatorCardListAdapter : CardListAdapter() {
    init {
        tag = "FocusIndicatorCardListAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemFocusIndicatorCardBinding.inflate(
            LayoutInflater.from(
                when (viewType) {
                    1 -> ContextThemeWrapper(
                        parent.context,
                        R.style.ThemeOverlay_ListItemFocusIndicatorCard_DefStyleAttr
                    )

                    2 -> ContextThemeWrapper(
                        parent.context,
                        R.style.ThemeOverlay_ListItemFocusIndicatorCard_XMLStyle
                    )

                    // defStyleRes.
                    else -> parent.context
                },
            ),
            parent,
            false,
        )
        return ViewHolder(binding.root, binding.image)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            CardItem.Type.DefStyleRes -> 0
            CardItem.Type.DefStyleAttr -> 1
            CardItem.Type.LayoutXMLStyle -> 2
        }
    }
}
