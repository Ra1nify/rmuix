// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SliderRenderingTest {
    @Test
    fun backgroundGrowthDoesNotMoveThumbEndpoints() {
        val resting = sliderTrackGeometry(300f, 20f, 20f)
        val pressed = sliderTrackGeometry(300f, 20f, 28f)
        assertEquals(36.dp, SliderDefaults.MinHeight)
        assertEquals(20.dp, SliderDefaults.CapsuleTrackHeight)
        assertEquals(1.4f, SLIDER_BACKGROUND_ENLARGE_SCALE)
        assertEquals(14f, resting.start)
        assertEquals(272f, resting.length)
        assertEquals(resting.start, pressed.start)
        assertEquals(resting.length, pressed.length)
        assertEquals(4f, resting.backgroundStart)
        assertEquals(296f, resting.backgroundEnd)
        assertEquals(0f, pressed.backgroundStart)
        assertEquals(300f, pressed.backgroundEnd)
    }

    @Test
    fun touchAndArtworkUseTheSameTravelInBothDirections() {
        val geometry = sliderTrackGeometry(300f, 20f, 28f)
        for (reverse in listOf(false, true)) {
            for (fraction in listOf(0f, 0.25f, 0.5f, 0.75f, 1f)) {
                val position = geometry.position(fraction, reverse)
                assertEquals(fraction, geometry.fraction(position, reverse), 0.00001f)
            }
        }
        assertEquals(0f, geometry.fraction(-100f, reverse = false))
        assertEquals(1f, geometry.fraction(400f, reverse = false))
        assertEquals(1f, geometry.fraction(-100f, reverse = true))
        assertEquals(0f, geometry.fraction(400f, reverse = true))
    }

    @Test
    fun nativeLengthTruncationIsPreservedAtFractionalDensity() {
        val geometry = sliderTrackGeometry(941f, 52.5f, 73.5f)
        assertEquals(36.75f, geometry.start)
        assertEquals(867f, geometry.length)
        assertEquals(903.75f, geometry.position(1f, reverse = false))
        assertEquals(0.5f, geometry.fraction(470.25f, reverse = false))
        val narrow = sliderTrackGeometry(12f, 20f, 20f)
        assertEquals(0f, narrow.length)
        assertEquals(6f, narrow.position(1f, reverse = false))
        assertEquals(0f, narrow.fraction(6f, reverse = true))
    }

    @Test
    fun pointerStateCanLeaveTheTrackWhileDrawingAndReleaseStayClamped() {
        val geometry = sliderTrackGeometry(300f, 20f, 28f)
        assertEquals(-1f, geometry.rawFraction(-258f, reverse = false))
        assertEquals(2f, geometry.rawFraction(558f, reverse = false))
        assertEquals(2f, geometry.rawFraction(-258f, reverse = true))
        assertEquals(-1f, geometry.rawFraction(558f, reverse = true))
        assertEquals(0f, geometry.fraction(-258f, reverse = false))
        assertEquals(1f, geometry.fraction(558f, reverse = false))
        assertEquals(14f, geometry.position(-1f, reverse = false))
        assertEquals(286f, geometry.position(2f, reverse = false))
        assertEquals(14f, geometry.position(2f, reverse = true))
        assertEquals(286f, geometry.position(-1f, reverse = true))
    }

    @Test
    fun rtlTouchUsesTheUntruncatedRightAnchorRatherThanMirroringTheDrawnLength() {
        val geometry = sliderTrackGeometry(941f, 52.5f, 73.5f)
        assertEquals(904.25f, geometry.touchEnd)
        assertEquals(903.75f, geometry.position(0f, reverse = true))
        assertEquals(0f, geometry.rawFraction(904.25f, reverse = true))
        assertEquals(1f, geometry.rawFraction(37.25f, reverse = true))
        assertEquals(0.5f / 867f, geometry.rawFraction(903.75f, reverse = true), 0.000001f)
        assertEquals(0.5f + 0.5f / 867f, geometry.rawFraction(470.25f, reverse = true), 0.000001f)
        assertEquals(0f, geometry.fraction(950f, reverse = true))
        assertEquals(1f, geometry.fraction(-50f, reverse = true))
    }

    @Test
    fun backgroundPaintingSnapsOnlyIntermediateHeights() {
        assertEquals(52.5f, sliderPaintedBackgroundHeight(52.5f, 52.5f, 73.5f))
        assertEquals(73.5f, sliderPaintedBackgroundHeight(73.5f, 52.5f, 73.5f))
        assertEquals(61f, sliderPaintedBackgroundHeight(60.5f, 52.5f, 73.5f))
        assertEquals(60f, sliderPaintedBackgroundHeight(60.49f, 52.5f, 73.5f))
    }

    @Test
    fun stockColorsDoNotDimTheDisabledBackground() {
        assertEquals(Color(0x1F000000), sliderContainer12(dark = false))
        assertEquals(Color(0x33FFFFFF), sliderContainer12(dark = true))
        assertEquals(Color(0x8AFFFFFF), sliderDisabledThumb(dark = false))
        assertEquals(Color(0x29FFFFFF), sliderDisabledThumb(dark = true))
        assertEquals(Color(0x1A000000), sliderThumbShadow(dark = false))
        assertEquals(Color(0x40000000), sliderThumbShadow(dark = true))
    }

    @Test
    fun springsAndShortPressGateUseTheSourceParameters() {
        assertEquals(157.91367f, SliderHeightStiffness, 0.0001f)
        assertEquals(1754.5963f, sliderProgressSpring(dragging = true).stiffness, 0.001f)
        assertEquals(438.6491f, sliderProgressSpring(dragging = false).stiffness, 0.001f)
        assertEquals(1f, sliderProgressSpring(dragging = true).dampingRatio)
        assertEquals(1f, sliderProgressSpring(dragging = false).dampingRatio)
        assertFalse(sliderCanReleaseBackground(0.4999f))
        assertTrue(sliderCanReleaseBackground(0.5f))
        assertTrue(sliderCanReleaseBackground(1f))
    }
}
