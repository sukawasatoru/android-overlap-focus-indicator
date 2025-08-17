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

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.focusindicator.databinding.CarouselItemBinding

class FocusIndicatorCarouselCardAdapter :
    ListAdapter<CardItem, FocusIndicatorCarouselCardAdapter.ViewHolder>(ItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CarouselItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ).root,
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.itemView.setTag(
            R.id.view_display_description,
            "{pos: $position, id: ${item.id}, uri: ${item.uri}}",
        )

        Glide.with(holder.itemView).load(item.uri).into(holder.view)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        Glide.with(holder.itemView).clear(holder.view)
    }

    class ViewHolder(val view: ImageView) : RecyclerView.ViewHolder(view)
}
