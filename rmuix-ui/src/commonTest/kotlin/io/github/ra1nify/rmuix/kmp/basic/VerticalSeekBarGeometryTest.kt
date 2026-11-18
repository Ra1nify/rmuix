// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Geometry of `COUIVerticalSeekBar`: `onMeasure()` width floor, `setBackgroundRect()`, `draw()` f5/f3
 * travel math and `drawProgress()`'s thumb expansion, at density 2 so dp values are doubled.
 */
class VerticalSeekBarGeometryTest {
    private fun metrics(
        height: Float = 600f,
        width: Float = 72f,
        progressFull: Boolean = false,
        showThumb: Boolean = true,
        showProgress: Boolean = true,
        fraction: Float = 0f,
        pressed: Float = 0f,
        style: VerticalSeekBarStyle = VerticalSeekBarDefaults.style(),
    ): VerticalSeekBarMetrics =
        verticalSeekBarMetrics(
            width = width,
            height = height,
            style = style,
            progressFull = progressFull,
            showThumb = showThumb,
            showProgress = showProgress,
            fraction = fraction,
            pressedFraction = pressed,
            density = 2f,
        )

    @Test
    fun widthFloorAndDefaultsMatchTheStockResources() {
        // coui_vertical_seekbar_view_min_width / coui_seekbar_background_radius / progress_radius
        assertEquals(36.dp, VerticalSeekBarDefaults.MinWidth)
        assertEquals(2.dp, VerticalSeekBarDefaults.BackgroundRadius)
        assertEquals(2.dp, VerticalSeekBarDefaults.ProgressRadius)
        // coui_vertical_seekbar_progress_padding_vertical / _pressed_padding_vertical
        assertEquals(10.dp, VerticalSeekBarDefaults.ProgressPaddingVertical)
        assertEquals(4.dp, VerticalSeekBarDefaults.PressedPaddingVertical)
        // couiVerticalSeekBarBackGroundEnlargeScale / ProgressEnlargeScale defaults
        assertEquals(6f, VerticalSeekBarDefaults.BackgroundEnlargeScale)
        assertEquals(4f, VerticalSeekBarDefaults.ProgressEnlargeScale)
        // TOUCH_ANIMATION_ENLARGE_DURATION and RELEASE_ANIM_DURATION
        assertEquals(183, VerticalSeekBarDefaults.TouchEnlargeDurationMillis)
        // width default is radius * 2 when no couiVerticalSeekBarBackgroundWidth is given
        val geometry = VerticalSeekBarDefaults.style()
        assertEquals(4.dp, geometry.backgroundWidth)
        assertEquals(4.dp, geometry.progressWidth)
    }

    @Test
    fun backgroundRectInsetIsPaddingMinusRadius() {
        val m = metrics()
        // setBackgroundRect(): top = paddingVertical - radius, bottom = height - paddingVertical + radius
        assertEquals(16f, m.backgroundTop) // 20 - 4
        assertEquals(584f, m.backgroundBottom) // 600 - 20 + 4
        assertEquals(32f, m.backgroundLeft) // 36 - 4
        assertEquals(40f, m.backgroundRight)
        assertEquals(4f, m.backgroundRadius)
    }

    @Test
    fun thumbTravelAndProgressUseTheStockFormulas() {
        val bottom = metrics(fraction = 0f)
        // draw(): thumbWidth = progressWidth * progressEnlargeScale = 8 * 4; radius = 4 * 4.
        assertEquals(32f, bottom.thumbWidth)
        assertEquals(16f, bottom.thumbRadius)
        // travel = (height - 2 * padding) - (thumbWidth - 2 * thumbRadius) = 560 - 0
        assertEquals(560f, bottom.travelLength)
        // travelBottom = height - padding - thumbWidth / 2 + thumbRadius
        assertEquals(580f, bottom.travelBottom)
        assertEquals(580f, bottom.thumbCenterY)
        // progress rect is expanded by thumbWidth / 2 on both ends when the thumb is visible
        assertEquals(564f, bottom.progressTop) // 580 - 16
        assertEquals(596f, bottom.progressBottom) // 580 + 16

        val top = metrics(fraction = 1f)
        assertEquals(20f, top.thumbCenterY) // 580 - 560
        assertEquals(4f, top.progressTop) // 20 - 16
        assertEquals(596f, top.progressBottom)
    }

    @Test
    fun noThumbGrowsFromTheProgressPadding() {
        val m = metrics(showThumb = false, fraction = 0f)
        // travel = height - 2 * padding + 2 * progressRadius
        assertEquals(568f, m.travelLength)
        // travelBottom = height - padding + progressRadius
        assertEquals(584f, m.travelBottom)
        assertEquals(584f, m.progressTop)
        assertEquals(584f, m.progressBottom)
        val full = metrics(showThumb = false, fraction = 1f)
        assertEquals(16f, full.progressTop) // 584 - 568
    }

    @Test
    fun pressStreakAnimatesTheEnlargeTargets() {
        val pressed = metrics(pressed = 1f)
        // padding target = pressedPaddingVertical + backgroundRadius * backgroundRadiusEnlargeScale
        assertEquals(8f, pressed.backgroundTop) // padding 32 - radius 24
        assertEquals(592f, pressed.backgroundBottom) // 600 - 32 + 24
        assertEquals(12f, pressed.backgroundLeft) // 36 - 48 / 2
        assertEquals(60f, pressed.backgroundRight)
        assertEquals(24f, pressed.backgroundRadius) // radius 4 * scale 6
        assertEquals(32f, pressed.thumbWidth) // thumb keeps progressWidth * progressEnlargeScale
        assertEquals(16f, pressed.thumbRadius)
        assertEquals(536f, pressed.travelLength) // (600 - 64) - 0
        assertEquals(568f, pressed.travelBottom) // 600 - 32 - 16 + 16
    }

    @Test
    fun progressFullInheritsTheBackgroundMetrics() {
        val m = metrics(progressFull = true)
        // resetProgressSize(): progressWidth = backgroundWidth, radius = backgroundRadius, scales = background
        assertEquals(8f, m.progressRight - m.progressLeft) // backgroundWidth at rest
        assertEquals(4f, m.progressRadius)
        // ... and thumbWidth = backgroundWidth * backgroundEnlargeScale = 8 * 6
        assertEquals(48f, m.thumbWidth)
        assertEquals(24f, m.thumbRadius)
        val pressed = metrics(progressFull = true, pressed = 1f)
        assertEquals(48f, pressed.progressRight - pressed.progressLeft) // 8 * 6
        assertEquals(24f, pressed.progressRadius)
    }

    @Test
    fun hiddenProgressCollapsesTheProgressHeight() {
        val m = metrics(showProgress = false)
        assertEquals(m.progressTop, m.progressBottom)
    }
}
