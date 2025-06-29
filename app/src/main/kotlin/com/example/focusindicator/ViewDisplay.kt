/*
 * Copyright 2024, 2025 sukawasatoru
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

import android.content.res.Resources
import android.view.View

@OptIn(ExperimentalStdlibApi::class)
@JvmInline
value class ViewDisplay(private val view: View?) {
    override fun toString(): String {
        if (view == null) return "null"

        val builder = StringBuilder(view.javaClass.simpleName)

        builder.append("{id: ")
        when (val id = view.id) {
            View.NO_ID -> builder.append("NO_ID")
            else -> {
                try {
                    builder.append(view.resources.getResourceEntryName(id))
                } catch (e: Resources.NotFoundException) {
                    builder
                        .append("0x")
                        .append(id.toHexString())
                }
            }
        }

        view.getTag(R.id.view_display_description)?.let { data ->
            builder.append(", ").append(data)
        }

        builder.append('}')
        return builder.toString()
    }
}
