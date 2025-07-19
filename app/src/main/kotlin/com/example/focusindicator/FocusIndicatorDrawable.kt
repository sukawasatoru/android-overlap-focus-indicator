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

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.DrawableWrapper
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import androidx.annotation.Px
import org.xmlpull.v1.XmlPullParser

class FocusIndicatorDrawable : DrawableWrapper {
    private val interpolator = EaseOutExpoInterpolator()

    private var isSuperFinished = false

    @Px
    var indicatorGap: Float = 0f
        set(value) {
            field = value
            focusIndicatorState.indicatorGap = value

            updateIndicatorBounds()
        }

    var duration = 300L
        set(value) {
            field = value
            focusIndicatorState.duration = value
        }

    private var focusIndicatorState: FocusIndicatorState

    @Suppress("unused")
    constructor() : super(null) {
        Log.i("FocusIndicatorDrawable", "constructor")

        isSuperFinished = true
        focusIndicatorState = FocusIndicatorState(
            bounds = Rect(),
            indicatorGap = indicatorGap,
            duration = duration,
            isVisible = isVisible,
        )
    }

    private constructor(
        focusIndicatorState: FocusIndicatorState,
    ) : super(focusIndicatorState.drawable) {
        Log.i("FocusIndicatorDrawable", "constructor state: $focusIndicatorState")

        isSuperFinished = true
        this.focusIndicatorState = focusIndicatorState
        indicatorGap = focusIndicatorState.indicatorGap
        duration = focusIndicatorState.duration
        setVisible(visible = focusIndicatorState.isVisible, restart = false, immediate = true)
    }

    override fun setDrawable(dr: Drawable?) {
        Log.i("FocusIndicatorDrawable", "setDrawable $dr")
        super.setDrawable(dr)

        if (!isSuperFinished) {
            return
        }

        focusIndicatorState.isVisible = isVisible
        setVisible(visible = focusIndicatorState.isVisible, restart = false, immediate = true)
        updateIndicatorBounds()
    }

    override fun canApplyTheme(): Boolean = true

    override fun draw(canvas: Canvas) {
        // for stacktrace.

        super.draw(canvas)
    }

    override fun getChangingConfigurations(): Int {
        return super.getChangingConfigurations() or focusIndicatorState.changingConfigurations
    }

    override fun setChangingConfigurations(configs: Int) {
        Log.i("FocusIndicatorDrawable", "setChangingConfigurations $configs")
        super.setChangingConfigurations(configs)
        focusIndicatorState.changingConfigurations = configs
    }

    private var animator: ValueAnimator? = null

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        return setVisible(visible, restart, false)
    }

    private fun setVisible(
        visible: Boolean,
        restart: Boolean,
        immediate: Boolean,
    ): Boolean {
        Log.i("FocusIndicatorDrawable", "setVisible $visible, $restart, $immediate")

        fun animate(visible: Boolean): ValueAnimator {
            return if (visible) {
                createAnimator(isFadeIn = true, restart = restart).also(ValueAnimator::start)
            } else {
                createAnimator(isFadeIn = false, restart = restart).also(ValueAnimator::start)
            }
        }

        return super.setVisible(visible, restart).also { changed ->
            if (immediate) {
                animator?.cancel()
                animator = null
                drawable?.alpha = if (visible) {
                    255
                } else {
                    0
                }
            } else {
                if (changed) {
                    animator?.cancel()
                    animator = animate(visible)
                }
            }
        }
    }

    override fun applyTheme(t: Resources.Theme) {
        Log.i("FocusIndicatorDrawable", "applyTheme: $t")

        super.applyTheme(t)

        val value = TypedValue()
        for ((attr, resId) in focusIndicatorState.themeAttr) {
            t.resolveAttribute(resId, value, true)
            when (attr) {
                R.styleable.FocusIndicatorDrawable_android_drawable -> {
                    drawable = t.getDrawable(value.resourceId)
                    Log.i("FocusIndicatorDrawable", "  android_drawable: $drawable")
                }

                R.styleable.FocusIndicatorDrawable_android_duration -> {
                    duration = value.data.toLong()
                    Log.i("FocusIndicatorDrawable", "  android_duration: $duration")
                }

                R.styleable.FocusIndicatorDrawable_indicatorGap -> {
                    indicatorGap = value.getDimension(t.resources.displayMetrics)
                    Log.i("FocusIndicatorDrawable", "  indicatorGap: $indicatorGap")
                }
            }
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        Log.i("FocusIndicatorDrawable", "onBoundsChange ${bounds.toShortString()}")

        // ignore super.

        focusIndicatorState.bounds = bounds
        updateIndicatorBounds()
    }

    override fun mutate(): Drawable {
        super.mutate()

        focusIndicatorState = focusIndicatorState.copy(
            themeAttr = focusIndicatorState.themeAttr.toMutableList(),
        )

        return this
    }

    override fun inflate(
        r: Resources,
        parser: XmlPullParser,
        attrs: AttributeSet,
        theme: Resources.Theme?,
    ) {
        Log.i("FocusIndicatorDrawable", "inflate theme: $theme")

        super.inflate(r, parser, attrs, theme)
        focusIndicatorState.isVisible = isVisible
        setVisible(visible = focusIndicatorState.isVisible, restart = false, immediate = true)

        var drawable: Drawable? = null

        theme?.obtainStyledAttributes(
            attrs,
            R.styleable.FocusIndicatorDrawable,
            0,
            0,
        ) ?: r.obtainAttributes(
            attrs,
            R.styleable.FocusIndicatorDrawable,
        ).use { a ->
            focusIndicatorState.changingConfigurations =
                focusIndicatorState.changingConfigurations or a.changingConfigurations

            val value = TypedValue()
            for (at in 0 until a.indexCount) {
                val attr = a.getIndex(at)
                a.getValue(attr, value)
                Log.i("FocusIndicatorDrawable", "  value: ${value.coerceToString()}")
                if (value.type == TypedValue.TYPE_ATTRIBUTE) {
                    focusIndicatorState.themeAttr.add(attr to value.data)
                    continue
                }

                when (attr) {
                    R.styleable.FocusIndicatorDrawable_android_drawable -> {
                        drawable = a.getDrawable(attr)
                        Log.i("FocusIndicatorDrawable", "  android_drawable: $drawable")
                    }

                    R.styleable.FocusIndicatorDrawable_android_duration -> {
                        duration = a.getInt(attr, duration.toInt()).toLong()
                        Log.i("FocusIndicatorDrawable", "  android_duration: $duration")
                    }

                    R.styleable.FocusIndicatorDrawable_indicatorGap -> {
                        indicatorGap = a.getDimension(attr, indicatorGap)
                        Log.i("FocusIndicatorDrawable", "  indicatorGap: $indicatorGap")
                    }
                }
            }
        }

        if (drawable != null) {
            focusIndicatorState.drawable = drawable
            this.drawable = drawable
        }
    }

    override fun getConstantState(): ConstantState {
        focusIndicatorState.changingConfigurations =
            focusIndicatorState.changingConfigurations or
                    (drawable?.let { super.changingConfigurations } ?: 0)
        return focusIndicatorState
    }

    private fun updateIndicatorBounds() {
        val drawable = drawable ?: return
        val bounds = focusIndicatorState.bounds

        Log.i("FocusIndicatorDrawable", "updateIndicatorBounds $bounds")

        val gap = indicatorGap.toInt()
        drawable.bounds = Rect(
            bounds.left - gap,
            bounds.top - gap,
            bounds.right + gap,
            bounds.bottom + gap,
        )
    }

    private fun createAnimator(isFadeIn: Boolean, restart: Boolean): ValueAnimator {
        val that = this
        return if (isFadeIn) {
            ObjectAnimator.ofInt(
                drawable,
                "alpha",
                if (!restart) {
                    drawable?.alpha ?: 0
                } else {
                    0
                },
                255,
            ).apply {
                duration = that.duration
                interpolator = that.interpolator
                if (!restart) {
                    currentPlayTime = animator?.currentPlayTime ?: 0
                }
            }
        } else {
            ObjectAnimator.ofInt(
                drawable,
                "alpha",
                if (!restart) {
                    drawable?.alpha ?: 255
                } else {
                    255
                },
                0,
            ).apply {
                duration = that.duration
                interpolator = that.interpolator
                if (!restart) {
                    currentPlayTime = animator?.currentPlayTime ?: 0
                }
            }
        }
    }

    data class FocusIndicatorState(
        @get:JvmName("changingConfigurations")
        var changingConfigurations: Int = 0,

        val themeAttr: MutableList<Pair<Int, Int>> = mutableListOf(),
        var drawable: Drawable? = null,
        var bounds: Rect,
        @Px var indicatorGap: Float,
        var duration: Long,
        var isVisible: Boolean,
    ) : ConstantState() {

        override fun newDrawable(): Drawable {
            return newDrawable(null)
        }

        override fun newDrawable(res: Resources?): Drawable {
            return newDrawable(res, null)
        }

        override fun newDrawable(res: Resources?, theme: Resources.Theme?): Drawable {
            Log.i("FocusIndicatorDrawable", "newDrawable")

            drawable = drawable?.constantState?.newDrawable(res, theme)
            return FocusIndicatorDrawable(this)
        }

        override fun getChangingConfigurations(): Int = changingConfigurations

        override fun canApplyTheme(): Boolean = true
    }
}
