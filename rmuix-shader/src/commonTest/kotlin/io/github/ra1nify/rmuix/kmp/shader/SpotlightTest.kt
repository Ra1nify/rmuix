// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.shader

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SpotlightTest {
    @Test
    fun opaqueMediumParametersAreTheSourceValuesNotDensityScaledOrAlphaMasks() {
        val light = spotlightParameters(SpotlightStyle.OpaqueMedium, false)
        val dark = spotlightParameters(SpotlightStyle.OpaqueMedium, true)
        assertEquals(BlendMode.Overlay, light.blendMode)
        assertEquals(0.6f, light.endIntensity)
        assertEquals(0.8f, dark.endIntensity)
        assertEquals(100f, light.startRadius)
        assertEquals(500f, light.endRadius)
        assertEquals(200f, light.lightHeight)
        assertEquals(0.4f, light.ambient)
        assertEquals(0.4f, light.diffuse)
        assertEquals(0.08f, light.noise)
        assertEquals(0f, light.dither)
    }

    @Test
    fun presetsKeepTheirDifferentBlendModesAndNightParameters() {
        assertEquals(BlendMode.Plus, spotlightParameters(SpotlightStyle.OpaqueSecondary, true).blendMode)
        assertEquals(0.15f, spotlightParameters(SpotlightStyle.OpaqueSecondary, true).endIntensity)
        assertEquals(BlendMode.SrcOver, spotlightParameters(SpotlightStyle.ChipTab, true).blendMode)
        assertEquals(1f, spotlightParameters(SpotlightStyle.RoundedRectangle, true).dither)
        assertEquals(0.02f, spotlightParameters(SpotlightStyle.RoundedRectangle, true).noise)
        assertEquals(0f, spotlightParameters(SpotlightStyle.RoundedRectangle, true).startRadius)
    }

    @Test
    fun releaseExpandsTheRadiusAndZeroProgressFullyClearsTheEffect() {
        val parameters = spotlightParameters(SpotlightStyle.OpaqueMedium, false)
        val pressed = spotlightFrame(0.5f, false, Offset(0.25f, 0.5f), Size(400f, 80f), parameters)
        val released = spotlightFrame(0.5f, true, Offset(0.25f, 0.5f), Size(400f, 80f), parameters)
        assertEquals(Offset(100f, 40f), pressed.center)
        assertEquals(300f, pressed.radius)
        assertEquals(800f, released.radius)
        assertEquals(0.3f, pressed.intensity)
        assertEquals(0f, spotlightFrame(0f, true, Offset.Zero, Size(400f, 80f), parameters).radius)
    }

    @Test
    fun normalizedPointerCoordinatesAreNotClampedToTheViewOrReversedByRtl() {
        val frame =
            spotlightFrame(1f, false, Offset(-0.25f, 1.5f), Size(400f, 80f), spotlightParameters(SpotlightStyle.OpaqueMedium, false))
        assertEquals(Offset(-100f, 120f), frame.center)
    }

    @Test
    fun sourceSpringsKeepSeparateGlowReleaseAndPositionResponses() {
        assertEquals(1754.5963f, spotlightProgressSpring(true).stiffness, 0.001f)
        assertEquals(246.7401f, spotlightProgressSpring(false).stiffness, 0.001f)
        assertEquals(322.2728f, spotlightPositionSpring().stiffness, 0.001f)
        assertEquals(0.85f, spotlightPositionSpring().dampingRatio)
        assertEquals(0.000075f, spotlightProgressSpring(true).visibilityThreshold)
        assertEquals(0.000075f, spotlightPositionSpring().visibilityThreshold)
    }

    @Test
    fun newestPointerIsTrackedWithoutRetriggeringGlowAndPartialReleaseKeepsTheOtherPointer() {
        val tracker = ComposedSpotlightPointer()
        assertEquals(SpotlightAction.Down, tracker.sample(PointerEventType.Press, listOf(change(1, true, false, 10f))).action)
        val additional = tracker.sample(PointerEventType.Press, listOf(change(1, true, true, 10f), change(2, true, false, 60f)))
        assertEquals(SpotlightAction.Move, additional.action)
        assertEquals(Offset(60f, 10f), additional.position)
        val partial = tracker.sample(PointerEventType.Release, listOf(change(1, true, true, 15f), change(2, false, true, 60f)))
        assertEquals(SpotlightAction.Move, partial.action)
        assertEquals(Offset(15f, 10f), partial.position)
        assertEquals(SpotlightAction.Up, tracker.sample(PointerEventType.Release, listOf(change(1, false, true, 20f))).action)
    }

    @Test
    fun shaderUsesThePackedNativeLightingModelAndHasNoGenericPowerGlow() {
        assertTrue(STOCK_SPOTLIGHT_SHADER.contains("u_lightingParams"))
        assertTrue(STOCK_SPOTLIGHT_SHADER.contains("smoothstep(1.0, 0.0, lightDistance / radius)"))
        assertTrue(STOCK_SPOTLIGHT_SHADER.contains("return half4(lighting, alpha)"))
        assertFalse(STOCK_SPOTLIGHT_SHADER.contains("pow("))
    }

    private fun change(
        id: Long,
        pressed: Boolean,
        previousPressed: Boolean,
        position: Float,
    ): PointerInputChange =
        PointerInputChange(
            id = PointerId(id),
            uptimeMillis = 20L,
            position = Offset(position, 10f),
            pressed = pressed,
            previousUptimeMillis = 10L,
            previousPosition = Offset(position, 10f),
            previousPressed = previousPressed,
            isInitiallyConsumed = false,
        )
}
