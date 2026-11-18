// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.HistoricalChange
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SliderPointerSamplingTest {
    @Test
    fun additionalPressesAndPartialReleasesDoNotEndOrMoveTheGesture() {
        assertTrue(sliderIgnoresPointerChange(PointerEventType.Press, pointersStillPressed = true))
        assertTrue(sliderIgnoresPointerChange(PointerEventType.Release, pointersStillPressed = true))
        assertFalse(sliderIgnoresPointerChange(PointerEventType.Release, pointersStillPressed = false))
        assertFalse(sliderIgnoresPointerChange(PointerEventType.Move, pointersStillPressed = true))
        assertFalse(sliderIgnoresPointerChange(PointerEventType.Unknown, pointersStillPressed = false))
    }

    @Test
    fun aMetadataLessReleaseOrAdditionalPressCannotClearTheExistingVelocityWindow() {
        assertTrue(sliderSamplesComposedVelocity(PointerEventType.Press, pointersPreviouslyPressed = false))
        assertTrue(sliderSamplesComposedVelocity(PointerEventType.Move, pointersPreviouslyPressed = true))
        assertFalse(sliderSamplesComposedVelocity(PointerEventType.Press, pointersPreviouslyPressed = true))
        assertFalse(sliderSamplesComposedVelocity(PointerEventType.Release, pointersPreviouslyPressed = true))
        assertFalse(sliderSamplesComposedVelocity(PointerEventType.Unknown, pointersPreviouslyPressed = true))
    }

    @Test
    fun nativePointerIndexSelectionDoesNotAssumeIdsAreTheSameOrSorted() {
        val identity = SliderPointerIdentity()
        val first = PointerId(101L)
        val second = PointerId(305L)
        identity.down(nativeId = 8, pointerId = first)
        identity.down(nativeId = 3, pointerId = second)
        assertEquals(first, identity.pointer(8))
        assertEquals(second, identity.pointer(3))
        identity.up(8)
        assertNull(identity.pointer(8))
        assertEquals(second, identity.pointer(3))
        identity.down(nativeId = 8, pointerId = PointerId(402L))
        assertEquals(PointerId(402L), identity.pointer(8))
        identity.clear()
        assertNull(identity.pointer(3))
        assertNull(identity.pointer(8))
    }

    @Test
    fun fallbackFeedsHistoricalTimesBeforeTheCurrentSampleWithoutNormalizingCoordinates() {
        val samples = mutableListOf<Pair<Long, Offset>>()
        val historical =
            listOf(
                HistoricalChange(uptimeMillis = 10L, position = Offset(-20f, 5f)),
                HistoricalChange(uptimeMillis = 15L, position = Offset(30f, 8f)),
            )
        sampleSliderHistory(historical, 20L, Offset(120f, -2f)) { time, position -> samples.add(time to position) }
        assertEquals(listOf(10L, 15L, 20L), samples.map { it.first })
        assertEquals(listOf(Offset(-20f, 5f), Offset(30f, 8f), Offset(120f, -2f)), samples.map { it.second })
    }

    @Test
    fun anEmptyHistoryStillEmitsTheCurrentSampleExactlyOnce() {
        val samples = mutableListOf<Pair<Long, Offset>>()
        sampleSliderHistory(emptyList(), 40L, Offset(75f, 20f)) { time, position -> samples.add(time to position) }
        assertEquals(listOf(40L to Offset(75f, 20f)), samples)
    }
}
