/*
 * Copyright 2023 sukawasatoru
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

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.focusindicator.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        binding = findViewById<ConstraintLayout>(R.id.container).let(ActivityMainBinding::bind)

        val adapter = CardListAdapter().apply {
            onItemClick = ::onItemClick
        }
        lifecycleScope.launch { adapter.submitList(requestItems()) }
        binding.list.adapter = adapter

        val adapter2 = FocusIndicatorCardListAdapter().apply {
            onItemClick = ::onItemClick
        }
        lifecycleScope.launch { adapter2.submitList(requestItems()) }
        binding.list2.adapter = adapter2

        val adapter3 = FocusIndicatorCarouselCardAdapter().apply {
            onItemClick = ::onItemClick
        }
        lifecycleScope.launch { adapter3.submitList(requestItems(1792).take(3)) }
        binding.list3.adapter = adapter3
    }

    private fun onItemClick(item: CardItem) {
        Toast.makeText(this, "Clicked: ${item.id}", Toast.LENGTH_SHORT).show()
    }
}
