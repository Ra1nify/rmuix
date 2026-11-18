// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import io.github.ra1nify.rmuix.kmp.utils.stockPressTargetScale
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * `COUIPressAnimatorUnit.initTargetScale()` -> `PressCalc.calculateUniformScaleBreakdown()` with the
 * `PressParamsExtKt` defaults (`scaleRangeMin` 0.85 / `max` 0.97, 20dp..150dp area mapping,
 * `BezierData(0,0,0,1)`, aspect channel 2..15).
 */
class PressFeedbackTargetScaleTest {
    @Test
    fun areaMappingUsesTheStockRange() {
        assertEquals(1f, stockPressTargetScale(0f, 32f))
        assertEquals(1f, stockPressTargetScale(32f, -1f))
        assertEquals(0.85f, stockPressTargetScale(20f, 20f), 0.0001f)
        assertEquals(0.97f, stockPressTargetScale(150f, 150f), 0.0001f)
        assertEquals(0.876604f, stockPressTargetScale(32f, 32f), 0.0005f)
        assertEquals(0.888590f, stockPressTargetScale(40f, 40f), 0.0005f)
        assertEquals(0.952232f, stockPressTargetScale(100f, 100f), 0.0005f)
    }

    @Test
    fun aspectRatioChannelBlendsAboveTheActivationThreshold() {
        // 150x10 has max(w/h, h/w) = 15 -> aspectScale 1.0, blended with the area scale.
        assertEquals(0.943383f, stockPressTargetScale(150f, 10f), 0.0005f)
        // 50x20 = 2.5, just above the 2.0 activation threshold.
        assertEquals(0.882870f, stockPressTargetScale(50f, 20f), 0.0005f)
    }
}
