// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SliderMotionTest {
    @Test
    fun unchangedOwnerDoesNotRestartAnInternalPreview() {
        val owner = SliderOwnerValue(0.25f)
        assertFalse(owner.reconcile(0.25f))
        assertFalse(owner.reconcile(0.25f))
        assertEquals(0.25f, owner.observedValue)
    }

    @Test
    fun externalAssignmentsRequestAnImmediateReset() {
        val owner = SliderOwnerValue(0.25f)
        assertTrue(owner.reconcile(0.75f))
        assertEquals(0.75f, owner.observedValue)
        assertFalse(owner.reconcile(0.75f))
    }

    @Test
    fun acceptedValueEchoDoesNotCancelTheFollowHandAnimation() {
        val owner = SliderOwnerValue(0.25f)
        owner.expect(0.6f)
        assertEquals(0.6f, owner.expectedValue)
        assertFalse(owner.reconcile(0.6f))
        assertNull(owner.expectedValue)
        assertEquals(0.6f, owner.observedValue)
        assertFalse(owner.reconcile(0.6f))
    }

    @Test
    fun rejectedOrTransformedRequestsReturnToTheOwnerValue() {
        val rejected = SliderOwnerValue(0.25f)
        rejected.expect(0.6f)
        assertTrue(rejected.reconcile(0.25f))
        assertNull(rejected.expectedValue)
        val rounded = SliderOwnerValue(0.25f)
        rounded.expect(0.62f)
        assertTrue(rounded.reconcile(0.6f))
        assertEquals(0.6f, rounded.observedValue)
    }

    @Test
    fun clickThresholdIsConvertedFromRawPixelsNotAPercentageOfValue() {
        assertEquals(0.003f, sliderClickThreshold(1f, 250f), 0.000001f)
        assertEquals(0.3f, sliderClickThreshold(100f, 250f), 0.000001f)
        assertEquals(0.00075f, sliderFollowThreshold(1f), 0.000001f)
        assertEquals(0.075f, sliderFollowThreshold(100f), 0.000001f)
        assertEquals(0.0075f, sliderProgressSpring(true, 0.0075f).visibilityThreshold)
    }

    @Test
    fun horizontalSlopDoesNotSubtractTheThresholdOrUseRadialDistance() {
        assertFalse(sliderStartsHorizontalDrag(0f, 8f))
        assertFalse(sliderStartsHorizontalDrag(8f, 8f))
        assertFalse(sliderStartsHorizontalDrag(-8f, 8f))
        assertTrue(sliderStartsHorizontalDrag(8.01f, 8f))
        assertTrue(sliderStartsHorizontalDrag(-8.01f, 8f))
    }
}
