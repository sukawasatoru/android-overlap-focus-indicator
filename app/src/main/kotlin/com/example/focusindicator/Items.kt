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

import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

suspend fun requestItems(widthPx: Int = 392): List<CardItem> = withContext(Dispatchers.IO) {
    (1..10).map { i ->
        yield()
        when (i) {
            2 ->
                CardItem(
                    id = "item-$i",
                    "https://placehold.co/${widthPx}x221.png?text=${widthPx}%20x%20221\\n$i".toUri(),
                    type = CardItem.Type.DefStyleAttr,
                )

            3 ->
                CardItem(
                    id = "item-$i",
                    "https://placehold.co/${widthPx}x221.png?text=${widthPx}%20x%20221\\n$i".toUri(),
                    type = CardItem.Type.LayoutXMLStyle,
                )

            else ->
                CardItem(
                    id = "item-$i",
                    "https://placehold.co/${widthPx}x221.png?text=${widthPx}%20x%20221\\n$i".toUri(),
                    type = CardItem.Type.DefStyleRes,
                )
        }
    }
}
