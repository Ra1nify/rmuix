// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ButtonRubberTest {
    @Test
    fun limitUsesNativeTiesToEvenPixelConversion() {
        assertEquals(28f, buttonRubberLimit(1f))
        assertEquals(66f, buttonRubberLimit(2.375f))
        assertEquals(88f, buttonRubberLimit(3.125f))
    }

    @Test
    fun nonlinearOffsetIsSignedAndBounded() {
        assertEquals(0f, buttonRubberOffset(0f, 1f))
        assertEquals(4.242424f, buttonRubberOffset(100f, 1f), 0.0001f)
        assertEquals(-buttonRubberOffset(100f, 1f), buttonRubberOffset(-100f, 1f), 0.00001f)
        assertTrue(buttonRubberOffset(10000f, 1f) < 28f)
        assertEquals(0f, buttonRubberOffset(100f, 0f))
    }

    @Test
    fun inverseRestoresTheEffectiveDragWhenRegrabbing() {
        for (distance in listOf(-500f, -50f, 0f, 50f, 500f)) {
            val offset = buttonRubberOffset(distance, 1f)
            assertEquals(distance, buttonRubberInverse(offset, 1f), 0.001f)
        }
        assertEquals(28f, buttonRubberInverse(28f, 1f))
    }

    @Test
    fun deadZoneIsFiveRawPixelsOnEitherAxisWithoutSubtractingIt() {
        assertFalse(buttonDragPassesDeadZone(Offset(4.9f, 4.9f)))
        assertTrue(buttonDragPassesDeadZone(Offset(5f, 0f)))
        assertTrue(buttonDragPassesDeadZone(Offset(0f, -5f)))
    }

    @Test
    fun pointerSwitchPreservesTheExistingDisplacement() {
        val origin = Offset(10f, 20f)
        val previous = Offset(110f, 60f)
        val next = Offset(250f, 90f)
        val rebased = buttonRubberRebaseOrigin(origin, previous, next)
        assertEquals(previous - origin, next - rebased)
    }

    @Test
    fun returnResponseAttenuatesByAreaAndKeepsTheNativeVelocityUnits() {
        val small = buttonRubberHandUp(56f, 56f, 1f, 1000f, 14f)
        val large = buttonRubberHandUp(100f, 100f, 1f, 1000f, 14f)
        val middle = buttonRubberHandUp(132f, 44f, 1f, 0f, 14f)
        assertEquals(0.42f, small.response, 0.000001f)
        assertEquals(0.45f, large.response, 0.000001f)
        assertTrue(middle.response > small.response && middle.response < large.response)
        assertEquals(0.15f, small.velocity, 0.000001f)
        assertEquals(0.65f, small.bounce)
        val densityCase = buttonRubberHandUp(132f * 2.375f, 44f * 2.375f, 2.375f, 1000f, 28f * 2.375f / 2f)
        assertEquals(0.15f, densityCase.velocity, 0.000001f)
    }

    @Test
    fun normalizedVelocityClampAndCancellationFollowTheSource() {
        val clamped = buttonRubberHandUp(132f, 44f, 1f, -100000000f, 28f)
        assertEquals(-1000f, clamped.velocity)
        assertEquals(1f, clamped.bounce)
        val cancelled = buttonRubberHandUp(132f, 44f, 1f, 0f, 14f)
        assertEquals(0f, cancelled.velocity)
        assertEquals(0.65f, cancelled.bounce)
        assertEquals(0.375f, buttonRubberDragSpring().visibilityThreshold)
        assertEquals(0.85f, buttonRubberDragSpring().dampingRatio, 0.000001f)
    }
}
