// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * `COUISimpleLock` metrics: `mRectanglesWidth = drawableWidth * count + couiRectanglePadding * (count - 1)`
 * and `mCodeImageStart = (measuredWidth - mRectanglesWidth) / 2`.
 */
class SimpleLockTest {
    @Test
    fun defaultsMatchTheStockResources() {
        // coui_simple_lock_*_rectangle_icon: <size> 10dp with 6dp padding on each side
        assertEquals(10.dp, SimpleLockDefaults.DotSize)
        assertEquals(6.dp, SimpleLockDefaults.DotInset)
        assertEquals(22.dp, SimpleLockDefaults.DotBoxSize)
        assertEquals(SimpleLockDefaults.DotSize + SimpleLockDefaults.DotInset * 2, SimpleLockDefaults.DotBoxSize)
        // couiRectanglePadding in Widget.COUI.COUISimpleLock
        assertEquals(10.dp, SimpleLockDefaults.DotGap)
        // FOURCIRCLE / SIXCIRCLE
        assertEquals(4, SimpleLockDefaults.FourCircles)
        assertEquals(6, SimpleLockDefaults.SixCircles)
        // COUISimpleLock.SPRING_RESPONSE / SPRING_BOUNCE / CIRCLE_MAX_SCALE
        assertEquals(0.35f, SimpleLockDefaults.FillSpringResponse)
        assertEquals(0.5f, SimpleLockDefaults.FillSpringBounce)
        assertEquals(1.2f, SimpleLockDefaults.FillMaxScale)
        // MORPHING_OUTLINED_TO_FILLED_TIME / MORPHING_FILLED_TO_OUTLINED_TIME
        assertEquals(230, SimpleLockDefaults.MorphMillis)
    }

    @Test
    fun coloursMatchTheStockTokens() {
        // coui_simple_lock_filled_rectangle_icon_color / _dark_color
        assertEquals(Color(0xFF000000), simpleLockFilled(dark = false))
        assertEquals(Color(0xE6FFFFFF), simpleLockFilled(dark = true))
        // coui_simple_lock_outlined_rectangle_icon_color / _dark_color
        assertEquals(Color(0x19000000), simpleLockOutlined(dark = false))
        assertEquals(Color(0x66FFFFFF), simpleLockOutlined(dark = true))
    }

    @Test
    fun fourDotRowMatchesTheStockWidthFormula() {
        // (22 * 4) + (10 * 3) = 118
        val row = simpleLockMetrics(width = 0f, count = 4, dotBoxSize = 22f, gap = 10f)
        assertEquals(118f, row.totalWidth)
        assertEquals(0f, row.startX)
        assertEquals(11f, row.dotCenterX(0))
        assertEquals(43f, row.dotCenterX(1))
        assertEquals(75f, row.dotCenterX(2))
        assertEquals(107f, row.dotCenterX(3))
    }

    @Test
    fun sixDotRowMatchesTheStockWidthFormula() {
        // (22 * 6) + (10 * 5) = 182
        val row = simpleLockMetrics(width = 0f, count = 6, dotBoxSize = 22f, gap = 10f)
        assertEquals(182f, row.totalWidth)
        assertEquals(11f, row.dotCenterX(0))
        assertEquals(43f, row.dotCenterX(1))
        assertEquals(171f, row.dotCenterX(5))
    }

    @Test
    fun failedAnimationMatchesTheStockKeyframes() {
        // createFailedAnimator(): 800ms linear, shake 0/30/-28/14/-8/4/-3/0 and a 0..250 fall.
        assertEquals(800, SimpleLockDefaults.FailureMillis)
        assertEquals(250f, SimpleLockDefaults.FallDistancePx)
        assertEquals(150f, SimpleLockDefaults.FallFadeDistancePx)
        assertEquals(150f, SimpleLockDefaults.FallRoomPx)
        assertEquals(
            listOf(0f, 30f, -28f, 14f, -8f, 4f, -3f, 0f),
            SimpleLockDefaults.ShakePointsPx.toList(),
        )
        assertEquals(listOf(0f, 38.5f, 91f, 63f), SimpleLockDefaults.DelayFour.toList())
        assertEquals(listOf(0f, 38.5f, 91f, 63f, 38.5f, 70f), SimpleLockDefaults.DelaySix.toList())
        // The 8 keyframes are spread evenly by the linear interpolator.
        assertEquals(0f, simpleLockShakeX(0f))
        assertEquals(30f, simpleLockShakeX(1f / 7f), 0.01f)
        assertEquals(-28f, simpleLockShakeX(2f / 7f), 0.01f)
        assertEquals(0f, simpleLockShakeX(1f), 0.01f)
        assertEquals(15f, simpleLockShakeX(0.5f / 7f), 0.01f)
        assertEquals(0f, simpleLockFallDistancePx(0f))
        assertEquals(125f, simpleLockFallDistancePx(0.5f))
        assertEquals(250f, simpleLockFallDistancePx(1f))
        // getDelayFallHeight(): DELAY_FOUR / DELAY_SIX, other lengths fall together.
        assertEquals(100f, simpleLockDelayFallPx(0, 100f, 4))
        assertEquals(61.5f, simpleLockDelayFallPx(1, 100f, 4), 0.01f)
        assertEquals(9f, simpleLockDelayFallPx(2, 100f, 4), 0.01f)
        assertEquals(37f, simpleLockDelayFallPx(3, 100f, 4), 0.01f)
        assertEquals(61.5f, simpleLockDelayFallPx(4, 100f, 6), 0.01f)
        assertEquals(30f, simpleLockDelayFallPx(5, 100f, 6), 0.01f)
        assertEquals(100f, simpleLockDelayFallPx(2, 100f, 5))
        // drawFilledRectangleShakeAndFall(): alpha = 1 - delayFall / FALL_HEIGHT.
        assertEquals(1f, simpleLockFallAlpha(0f))
        assertEquals(0.5f, simpleLockFallAlpha(75f), 0.01f)
        assertEquals(0f, simpleLockFallAlpha(150f))
        assertEquals(0f, simpleLockFallAlpha(200f))
    }

    @Test
    fun rowIsCentredInsideTheView() {
        // mCodeImageStart = (measuredWidth - mRectanglesWidth) / 2
        val row = simpleLockMetrics(width = 200f, count = 4, dotBoxSize = 22f, gap = 10f)
        assertEquals(41f, row.startX) // (200 - 118) / 2
        assertEquals(52f, row.dotCenterX(0))
        assertEquals(148f, row.dotCenterX(3))
        val six = simpleLockMetrics(width = 200f, count = 6, dotBoxSize = 22f, gap = 10f)
        assertEquals(9f, six.startX) // (200 - 182) / 2
        assertEquals(20f, six.dotCenterX(0))
        assertEquals(180f, six.dotCenterX(5))
    }
}
