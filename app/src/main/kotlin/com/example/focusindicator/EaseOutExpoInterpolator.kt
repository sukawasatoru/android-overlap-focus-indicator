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

import android.animation.TimeInterpolator
import kotlin.math.pow


/**
 * https://easings.net/#easeOutExpo
 */
class EaseOutExpoInterpolator : TimeInterpolator {
    override fun getInterpolation(input: Float): Float {
        return if (input == 1f) {
            1f
        } else {
            1f - 2f.pow(-10.0f * input)
        }
    }
}
