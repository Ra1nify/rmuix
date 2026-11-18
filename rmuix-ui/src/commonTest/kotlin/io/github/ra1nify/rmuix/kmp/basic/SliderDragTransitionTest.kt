// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SliderDragTransitionTest {
    @Test
    fun sourceThresholdIsStrictlyGreaterThanOneProgressUnit() {
        val mapping = SliderProgressMapping(0f..100f, 0..100)
        assertFalse(mapping.shouldStartTransition(50f, 50f))
        assertFalse(mapping.shouldStartTransition(50f, 51f))
        assertFalse(mapping.shouldStartTransition(50f, 49f))
        assertTrue(mapping.shouldStartTransition(50f, 51.01f))
        assertTrue(mapping.shouldStartTransition(50f, 48.99f))
    }

    @Test
    fun normalizedValuesUseTheExplicitNativeResolutionWithoutGuessingAPercentage() {
        val mapping = SliderProgressMapping(-1f..1f, 10..110)
        assertFalse(mapping.shouldStartTransition(0f, 0.01f))
        assertTrue(mapping.shouldStartTransition(0f, 0.021f))
        assertEquals(-0.26f, mapping.valueAtProgress(-0.253f, round = false), 0.00001f)
        assertEquals(-0.24f, mapping.valueAtProgress(-0.247f, round = true), 0.00001f)
        assertEquals(-1f, mapping.valueAtProgress(-2f, round = false))
        assertEquals(1f, mapping.valueAtProgress(2f, round = true))
    }

    @Test
    fun transitionCallbacksTruncateButReleaseAndClickRoundToNearest() {
        val mapping = SliderProgressMapping(0f..100f, 0..100)
        assertEquals(36f, mapping.valueAtProgress(36.9f, round = false))
        assertEquals(37f, mapping.valueAtProgress(36.9f, round = true))
        assertEquals(37f, mapping.valueAtProgress(37.5f, round = false))
        assertEquals(38f, mapping.valueAtProgress(37.5f, round = true))
    }

    @Test
    fun smallFirstMovementDoesNotStartATransitionLaterInTheSameDrag() {
        val mapping = SliderProgressMapping(0f..100f, 0..100)
        val transition = SliderDragTransition()
        transition.begin()
        assertFalse(transition.route(50f, 51f, mapping))
        assertFalse(transition.route(51f, 80f, mapping))
        assertFalse(transition.active)
    }

    @Test
    fun firstDragRouteStaysLatchedEvenAfterTheSpringReachesItsTarget() {
        val mapping = SliderProgressMapping(0f..100f, 0..100)
        val transition = SliderDragTransition()
        transition.begin()
        assertTrue(transition.route(50f, 80f, mapping))
        assertTrue(transition.route(80f, 80f, mapping))
        assertTrue(transition.route(80f, 80.5f, mapping))
        transition.stop()
        assertFalse(transition.active)
        assertFalse(transition.route(80f, 20f, mapping))
        transition.begin()
        assertFalse(transition.route(80f, 80f, mapping))
    }

    @Test
    fun onlySubsequentTransitionTargetsRetainTheOutOfBoundsFraction() {
        val mapping = SliderProgressMapping(0f..100f, 0..100)
        assertEquals(100f, mapping.transitionTarget(100f, 2f, alreadyActive = false))
        assertEquals(0f, mapping.transitionTarget(0f, -1f, alreadyActive = false))
        assertEquals(150f, mapping.transitionTarget(100f, 1.5f, alreadyActive = true))
        assertEquals(-50f, mapping.transitionTarget(0f, -0.5f, alreadyActive = true))
        assertEquals(200f, mapping.transitionTarget(100f, 100f, alreadyActive = true))
        assertEquals(-100f, mapping.transitionTarget(0f, -100f, alreadyActive = true))
    }

    @Test
    fun outOfBoundsStateDoesNotLeakIntoReportedValuesOrReleaseEndpoints() {
        val mapping = SliderProgressMapping(-1f..1f, 10..110)
        val above = mapping.transitionTarget(1f, 2f, alreadyActive = true)
        val below = mapping.transitionTarget(-1f, -1f, alreadyActive = true)
        assertEquals(3f, above)
        assertEquals(-3f, below)
        assertEquals(1f, mapping.valueAtProgress(above, round = false))
        assertEquals(-1f, mapping.valueAtProgress(below, round = false))
        assertEquals(1f, mapping.valueAtProgress(above, round = true))
        assertEquals(-1f, mapping.valueAtProgress(below, round = true))
    }

    @Test
    fun returningInsideTheTrackRetainsExtensionSnappingRatherThanTheOldOvershoot() {
        val mapping = SliderProgressMapping(0f..1f, null)
        assertEquals(1.75f, mapping.transitionTarget(1f, 1.75f, alreadyActive = true))
        assertEquals(0.5f, mapping.transitionTarget(0.5f, 0.49f, alreadyActive = true))
        assertEquals(0f, mapping.transitionTarget(0f, 0f, alreadyActive = true))
        assertEquals(1f, mapping.transitionTarget(1f, 1f, alreadyActive = true))
    }

    @Test
    fun floatExtensionDoesNotIntroduceAnImplicitIntegerGrid() {
        val mapping = SliderProgressMapping(0f..1f, null)
        assertEquals(0.369f, mapping.valueAtProgress(0.369f, round = false))
        assertEquals(0.369f, mapping.valueAtProgress(0.369f, round = true))
        assertFalse(mapping.shouldStartTransition(0.5f, 0.5f))
        assertTrue(mapping.shouldStartTransition(0.5f, 0.5001f))
    }
}
