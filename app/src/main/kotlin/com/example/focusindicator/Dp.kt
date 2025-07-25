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

import android.content.Context
import android.view.View
import kotlin.math.roundToInt

@JvmInline
value class Dp(val value: Float) {
    fun toPx(context: Context): Float {
        return value * context.resources.displayMetrics.density
    }

    fun toPx(view: View): Float {
        return toPx(view.context)
    }

    fun roundToPx(context: Context): Int {
        return toPx(context).roundToInt()
    }

    fun roundToPx(view: View): Int {
        return roundToPx(view.context)
    }
}

inline val Int.dp: Dp get() = Dp(this.toFloat())

inline val Float.dp: Dp get() = Dp(this)
