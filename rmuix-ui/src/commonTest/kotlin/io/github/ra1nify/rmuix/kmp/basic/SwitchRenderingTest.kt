// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SwitchRenderingTest {
    @Test
    fun thumbIsRoundAtRestAndStretchesAtTheStockBreakpoint() {
        assertEquals(1f, switchThumbScaleX(0f), 0.000001f)
        assertEquals(1.3f, switchThumbScaleX(0.34725848f), 0.000001f)
        assertEquals(1f, switchThumbScaleX(1f), 0.000001f)
        assertEquals(1.15f, switchThumbScaleX(0.34725848f / 2f), 0.000001f)
        assertEquals(1.15f, switchThumbScaleX(0.34725848f + 0.65274155f / 2f), 0.000001f)
    }

    @Test
    fun springOvershootDoesNotFreezeThumbDeformationAtAnEndpoint() {
        assertTrue(switchThumbScaleX(-0.01f) < 1f)
        assertTrue(switchThumbScaleX(1.01f) < 1f)
    }

    @Test
    fun trackColorUsesStockArgbChannelsInsteadOfAnIndependentAnimation() {
        assertEquals(Color(0xFF7F7F7F), switchTrackColor(Color.Black, Color.White, 0.5f))
        assertEquals(Color(0x947F7F7F), switchTrackColor(Color(0x29000000), Color.White, 0.5f))
        assertEquals(Color.Black, switchTrackColor(Color.Black, Color.White, -0.2f))
        assertEquals(Color.White, switchTrackColor(Color.Black, Color.White, 1.2f))
    }

    @Test
    fun disabledTrackUsesItsStateColorWithoutInterpolating() {
        val colors =
            SwitchColors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = Color.White,
                disabledCheckedThumbColor = Color(0x8AFFFFFF),
                disabledUncheckedThumbColor = Color(0x8AFFFFFF),
                checkedTrackColor = Color.Blue,
                uncheckedTrackColor = Color(0x29000000),
                disabledCheckedTrackColor = Color(0x4D0000FF),
                disabledUncheckedTrackColor = Color(0x14000000),
            )

        assertEquals(Color(0x4D0000FF), colors.trackColor(enabled = false, checked = true, progress = 0.2f))
        assertEquals(Color(0x14000000), colors.trackColor(enabled = false, checked = false, progress = 0.8f))
        assertEquals(Color.Blue, colors.trackColor(enabled = true, checked = false, progress = 1f))
        assertEquals(Color(0x29000000), colors.trackColor(enabled = true, checked = true, progress = 0f))
    }

    @Test
    fun dragUsesTheFullPointerDisplacementAndClampsOnlyTheTarget() {
        assertEquals(12f, switchDragPosition(0f, 12f, 20f, isRtl = false))
        assertEquals(8f, switchDragPosition(20f, 12f, 20f, isRtl = true))
        assertEquals(20f, switchDragPosition(0f, 30f, 20f, isRtl = false))
        assertEquals(0f, switchDragPosition(20f, 30f, 20f, isRtl = true))
        assertEquals(0f, switchDragPosition(0f, -12f, 20f, isRtl = false))
    }

    @Test
    fun attachmentHasItsOwnSpringAndRawPixelVelocityLimit() {
        val toggle = switchMotionSpring(SwitchMotionPhase.Toggle)
        val drag = switchMotionSpring(SwitchMotionPhase.Drag)
        val attach = switchMotionSpring(SwitchMotionPhase.Attach)
        // `COUISpringForce.setBounce` = 1 - bounce: toggle bounce 0.3 -> 0.7, attach 0.2 -> 0.8.
        // (The half-bounce `SpringData.getDampingRatio()` has no caller in the stock switch.)
        assertEquals(0.7f, toggle.dampingRatio)
        assertEquals(1f, drag.dampingRatio)
        assertEquals(0.8f, attach.dampingRatio)
        // Stiffness is `(2pi / response)^2`, the same number as the framework's `39.47 / response^2`.
        assertEquals(246.6875f, toggle.stiffness, 0.001f)
        assertEquals(438.5555f, drag.stiffness, 0.001f)
        assertEquals(246.6875f, attach.stiffness, 0.001f)
        assertEquals(0.075f, toggle.visibilityThreshold)
        assertEquals(0.75f, attach.visibilityThreshold)
        assertEquals(1000f, switchAttachmentVelocity(5000f))
        assertEquals(-1000f, switchAttachmentVelocity(-5000f))
        assertEquals(650f, switchAttachmentVelocity(650f))
    }

    @Test
    fun attachmentUsesVelocityOnlyAfterTheStockDistanceThreshold() {
        assertEquals(false, switchTargetChecked(0.2f, 1200f, 3f, isRtl = false))
        assertEquals(true, switchTargetChecked(0.2f, 1000f, 4f, isRtl = false))
        assertEquals(false, switchTargetChecked(0.8f, -1000f, -4f, isRtl = false))
        assertEquals(true, switchTargetChecked(0.8f, -999f, -4f, isRtl = false))
        assertEquals(false, switchTargetChecked(0.5f, 0f, 0f, isRtl = false))
        assertEquals(true, switchTargetChecked(0.5f, 0f, 0f, isRtl = true))
        assertEquals(true, switchTargetChecked(0.2f, 0f, 4f, isRtl = false, verticalVelocity = 1000f))
        assertEquals(false, switchTargetChecked(0.8f, 0f, 4f, isRtl = true, verticalVelocity = -1000f))
    }

    @Test
    fun whiteStateMaskUsesAndroidArgbEvaluatorGamma() {
        assertEquals(Color(0x1A808080), switchStateMaskColor(Color(0x33FFFFFF), 0.5f))
        assertEquals(Color.Transparent, switchStateMaskColor(Color(0x33FFFFFF), 0f))
        assertEquals(Color(0x33FFFFFF), switchStateMaskColor(Color(0x33FFFFFF), 1f))
    }
}
