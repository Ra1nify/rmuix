// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import kotlin.math.abs
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SliderFlingPhysicsTest {
    @Test
    fun rawPixelVelocityUsesTheSourceInclusiveGateAndLimit() {
        assertEquals(0f, sliderFlingVelocity(99.99f))
        assertEquals(0f, sliderFlingVelocity(-99.99f))
        assertEquals(100f, sliderFlingVelocity(100f))
        assertEquals(-100f, sliderFlingVelocity(-100f))
        assertEquals(8000f, sliderFlingVelocity(9000f))
        assertEquals(-8000f, sliderFlingVelocity(-9000f))
        assertEquals(0f, sliderFlingVelocity(Float.NaN))
        assertEquals(0f, sliderFlingVelocity(Float.POSITIVE_INFINITY))
    }

    @Test
    fun freeMotionUsesDampingBeforePositionIntegration() {
        val period = 1f / 120f
        val physics = SliderFlingPhysics(50f, 120f, 10000f, 1f, period)
        physics.step()
        val expectedVelocity = 120f / (1f + 15f * period)
        assertEquals(expectedVelocity, physics.velocityPixels, 0.0001f)
        assertEquals(50f + expectedVelocity * period, physics.positionPixels, 0.0001f)
        assertFalse(physics.isSteady)
    }

    @Test
    fun multipleFreeStepsFollowTheDiscreteFormulaNotAnExponentialDecay() {
        val period = 1f / 120f
        val physics = SliderFlingPhysics(1000f, 1000f, 10000f, 3.125f, period)
        repeat(60) { physics.step() }
        val factor = 1f / (1f + 15f * period)
        assertEquals(1000f * factor.pow(60), physics.velocityPixels, 0.001f)
        assertEquals(1000f + 1000f / 15f * (1f - factor.pow(60)), physics.positionPixels, 0.01f)
    }

    @Test
    fun boundaryMomentumIsNotErasedByClampingThePhysicalBody() {
        val physics = SliderFlingPhysics(99f, 1200f, 100f, 1f, 1f / 120f)
        physics.step()
        assertTrue(physics.positionPixels > 100f)
        assertTrue(physics.velocityPixels > 0f)
        assertFalse(physics.isSteady)
        val mapping = SliderProgressMapping(0f..1f, 0..100)
        assertEquals(1f, mapping.flingValueAtPixels(physics.positionPixels, 100f, reverse = false))
        repeat(5000) { if (!physics.isSteady) physics.step() }
        assertTrue(physics.isSteady)
        assertTrue(abs(physics.positionPixels - 100f) < 0.1f)
        assertTrue(abs(physics.velocityPixels) < 0.1f)
    }

    @Test
    fun theStopGateIsRawPointOnePixelsPerSecondRatherThanDpOrOnePixel() {
        val slow = SliderFlingPhysics(50f, 0.09f, 100f, 3.125f, 1f / 60f)
        val moving = SliderFlingPhysics(50f, 0.11f, 100f, 3.125f, 1f / 60f)
        assertTrue(slow.isSteady)
        assertFalse(moving.isSteady)
    }

    @Test
    fun flingUsesPhysicalPointerDirectionAndPreservesTheSourceNonzeroMinFormula() {
        val normalized = SliderProgressMapping(0f..1f, 0..100)
        assertEquals(50f, normalized.flingStartPixels(0.25f, 200f, reverse = false))
        assertEquals(150f, normalized.flingStartPixels(0.25f, 200f, reverse = true))
        assertEquals(0.75f, normalized.flingValueAtPixels(150f, 200f, reverse = false))
        assertEquals(0.25f, normalized.flingValueAtPixels(150f, 200f, reverse = true))
        val nonzeroMin = SliderProgressMapping(0f..1f, 10..110)
        assertEquals(170f, nonzeroMin.flingStartPixels(0.25f, 200f, reverse = true))
    }
}
