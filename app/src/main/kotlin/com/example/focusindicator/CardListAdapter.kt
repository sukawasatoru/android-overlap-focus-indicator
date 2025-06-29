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

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.focusindicator.databinding.ListItemCardBinding

open class CardListAdapter : ListAdapter<CardItem, CardListAdapter.ViewHolder>(ItemCallback) {
    var onItemClick: ((CardItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ViewHolder(binding, binding.image)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.itemView.setOnClickListener { onItemClick?.invoke(item) }
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Log.i("CardListAdapter", "focus: ${ViewDisplay(holder.itemView)}")
            }
        }
        holder.itemView.setTag(
            R.id.view_display_description,
            "{pos: $position, id: ${item.id}, uri: ${item.uri}}",
        )

        Glide.with(holder.binding.root)
            .load(item.uri)
            .into(holder.image)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        Glide.with(holder.binding.root).clear(holder.image)
    }

    class ViewHolder(
        val binding: ViewBinding,
        val image: ImageView,
    ) : RecyclerView.ViewHolder(binding.root)
}

object ItemCallback : DiffUtil.ItemCallback<CardItem>() {
    override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
        return oldItem == newItem
    }
}
