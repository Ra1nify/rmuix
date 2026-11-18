// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class ProgressIndicatorDefaultsTest {
    @Test
    fun circularMetricsMatchTheCircularPresets() {
        // Widget.COUI.COUICircularProgressBar declares couiCircularProgressBarSize = large_size.
        assertEquals(5.dp, ProgressIndicatorDefaults.DefaultCircularProgressIndicatorStrokeWidth)
        assertEquals(40.dp, ProgressIndicatorDefaults.DefaultCircularProgressIndicatorSize)
        assertEquals(3.dp, ProgressIndicatorStockCircularStrokeWidth)
        assertEquals(30.dp, ProgressIndicatorStockCircularMediumSize)
        assertEquals(40.dp, ProgressIndicatorStockCircularLargeSize)
        assertEquals(5.dp, ProgressIndicatorStockCircularLargeStrokeWidth)
        // The large preset is public so callers can opt into the 40dp / 5dp pair.
        assertEquals(40.dp, ProgressIndicatorDefaults.LargeCircularSize)
        assertEquals(5.dp, ProgressIndicatorDefaults.LargeCircularStrokeWidth)
    }

    @Test
    fun linearFillIsOneTrackSizedRectClippedAtTheProgressPoint() {
        // COUIHorizontalProgressBar: left = paddingLeft - (1 - progress) * width (LTR), and
        // right = (width - paddingRight) - progress * width (RTL); the rect is full track width and is
        // intersected with the rounded track, so 0 paints nothing and 1 covers the track exactly.
        val w = 200f
        assertEquals(-w, linearProgressFillLeft(0f, w, rtl = false))
        assertEquals(-w / 2f, linearProgressFillLeft(0.5f, w, rtl = false))
        assertEquals(0f, linearProgressFillLeft(1f, w, rtl = false))
        assertEquals(w, linearProgressFillLeft(0f, w, rtl = true))
        assertEquals(w / 2f, linearProgressFillLeft(0.5f, w, rtl = true))
        assertEquals(0f, linearProgressFillLeft(1f, w, rtl = true))
    }

    @Test
    fun pauseAndErrorIconsUseTheDrawableTokens() {
        // COUICircularProgressDrawable.drawPauseIcon() draws two 4 x 16dp rounded bars, r=1dp, gap 4dp.
        assertEquals(4.dp, ProgressIndicatorDefaults.PauseRectWidth)
        assertEquals(16.dp, ProgressIndicatorDefaults.PauseRectHeight)
        assertEquals(1.dp, ProgressIndicatorDefaults.PauseRectRadius)
        assertEquals(4.dp, ProgressIndicatorDefaults.PauseGap)
        // drawErrorIcon(): a 19dp ring at 1.5dp plus a 1.6 x 5.25dp stem and an r=1.1dp dot.
        assertEquals(19.dp, ProgressIndicatorDefaults.ErrorDiameter)
        assertEquals(1.5.dp, ProgressIndicatorDefaults.ErrorStrokeWidth)
        assertEquals(1.6.dp, ProgressIndicatorDefaults.ErrorStemWidth)
        assertEquals(5.25.dp, ProgressIndicatorDefaults.ErrorStemHeight)
        assertEquals(4.25.dp, ProgressIndicatorDefaults.ErrorStemBias)
        assertEquals(1.1.dp, ProgressIndicatorDefaults.ErrorDotRadius)
        assertEquals(3.1.dp, ProgressIndicatorDefaults.ErrorDotBias)
        // coui_circular_progress_shadow_*: 4dp blur, 0dp x, 2dp y.
        assertEquals(4.dp, ProgressIndicatorDefaults.ShadowRadius)
        assertEquals(2.dp, ProgressIndicatorDefaults.ShadowYBias)
    }

    @Test
    fun trackAndRingFollowTheStockNeutrals() {
        assertEquals(Color(0x1F000000), progressIndicatorTrack(false))
        assertEquals(Color(0x33FFFFFF), progressIndicatorTrack(true))
        assertEquals(Color(0x42000000), progressIndicatorRing(false))
        assertEquals(Color(0x4DFFFFFF), progressIndicatorRing(true))
    }
}
