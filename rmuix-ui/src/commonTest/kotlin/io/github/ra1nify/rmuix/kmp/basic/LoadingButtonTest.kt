// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Pins the `COUILoadingButton` dot keyframes: four `ValueAnimator`s per dot, staggered by 333ms over a
 * 1332ms cycle.
 */
class LoadingButtonTest {
    @Test
    fun dotsComeFromTheStockResources() {
        assertEquals(1.dp, LoadingButtonDefaults.DotRadius)
        assertEquals(2.dp, LoadingButtonDefaults.DotSpacing)
        assertEquals(51f, LoadingButtonDefaults.DOT_START_ALPHA)
        assertEquals(127.5f, LoadingButtonDefaults.DOT_MID_ALPHA)
        assertEquals(255f, LoadingButtonDefaults.DOT_END_ALPHA)
        assertEquals(333, LoadingButtonDefaults.DOT_STAGGER_MILLIS)
        assertEquals(1332, LoadingButtonDefaults.CYCLE_MILLIS)
    }

    @Test
    fun firstDotFollowsItsFourSegments() {
        val d = 0
        assertEquals(51f, loadingDotAlpha(0f, d), 0.01f, "starts dim")
        assertEquals(127.5f, loadingDotAlpha(133f, d), 0.01f, "START -> MID ends")
        assertEquals(255f, loadingDotAlpha(200f, d), 0.01f, "MID -> END ends")
        // 200ms..467ms holds at full brightness: that hold is what makes it read as a highlight.
        assertEquals(255f, loadingDotAlpha(300f, d), 0.01f)
        assertEquals(255f, loadingDotAlpha(467f, d), 0.01f)
        assertEquals(127.5f, loadingDotAlpha(533f, d), 0.01f, "END -> MID ends")
        assertEquals(51f, loadingDotAlpha(666f, d), 0.01f, "MID -> START ends")
        assertEquals(51f, loadingDotAlpha(1331f, d), 0.01f, "and holds until the cycle restarts")
    }

    @Test
    fun dotsAreStaggeredByOneThirdOfASecond() {
        // At t = 333ms dot 0 is at full brightness while dot 1 is just starting.
        assertEquals(255f, loadingDotAlpha(333f, 0), 0.01f)
        assertEquals(51f, loadingDotAlpha(333f, 1), 0.01f)
        assertEquals(51f, loadingDotAlpha(333f, 2), 0.01f)
        // 666ms: dot 0 has finished its pass, dot 1 is at full, dot 2 is starting.
        assertEquals(51f, loadingDotAlpha(666f, 0), 0.01f)
        assertEquals(255f, loadingDotAlpha(666f, 1), 0.01f)
        assertEquals(51f, loadingDotAlpha(666f, 2), 0.01f)
    }

    @Test
    fun alphaWrapsAndStaysInRange() {
        assertEquals(loadingDotAlpha(0f, 0), loadingDotAlpha(1332f, 0), 0.01f)
        for (t in 0..1400 step 7) {
            val alpha = loadingDotAlpha(t.toFloat(), 1)
            assertTrue(alpha in 51f..255f, "alpha must stay inside the stock range, got $alpha at $t")
        }
    }
}
