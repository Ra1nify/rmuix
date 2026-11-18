// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import kotlin.test.Test
import kotlin.test.assertEquals

class RangeSliderDragLogicTest {
    private val range = 0f..100f

    @Test
    fun pressBetweenTheThumbsDragsTheWholeRange() {
        // Start thumb at x=100, end thumb at x=300, touch in the middle.
        assertEquals(RangeDragMode.Track, rangeDragModeFor(offsetX = 200f, startThumbX = 100f, endThumbX = 300f, touchRadius = 24f))
    }

    @Test
    fun pressNearAThumbGrabsThatThumb() {
        assertEquals(RangeDragMode.Start, rangeDragModeFor(offsetX = 105f, startThumbX = 100f, endThumbX = 300f, touchRadius = 24f))
        assertEquals(RangeDragMode.End, rangeDragModeFor(offsetX = 295f, startThumbX = 100f, endThumbX = 300f, touchRadius = 24f))
    }

    @Test
    fun pressOutsideTheRangeGrabsTheNearestThumbInsteadOfTheTrack() {
        // Left of the start thumb: must grab Start (roui's fourth branch), not drag the whole range.
        assertEquals(RangeDragMode.Start, rangeDragModeFor(offsetX = 10f, startThumbX = 100f, endThumbX = 300f, touchRadius = 24f))
        // Right of the end thumb: must grab End.
        assertEquals(RangeDragMode.End, rangeDragModeFor(offsetX = 380f, startThumbX = 100f, endThumbX = 300f, touchRadius = 24f))
        // Still outside, but between the two thumbs horizontally in RTL (start drawn on the right).
        assertEquals(RangeDragMode.Start, rangeDragModeFor(offsetX = 380f, startThumbX = 300f, endThumbX = 100f, touchRadius = 24f))
    }

    @Test
    fun startIsClampedToTheRangeAndToTheEndThumb() {
        // Cannot pass the end thumb...
        assertEquals(70f, clampRangeStart(candidate = 90f, end = 70f, valueRange = range, minGap = 0f))
        // ...but may touch it when minGap is zero.
        assertEquals(70f, clampRangeStart(candidate = 70f, end = 70f, valueRange = range, minGap = 0f))
        // minGap keeps them apart.
        assertEquals(65f, clampRangeStart(candidate = 90f, end = 70f, valueRange = range, minGap = 5f))
        // And the range start is respected even when the end thumb sits at the very start.
        assertEquals(0f, clampRangeStart(candidate = -10f, end = 0f, valueRange = range, minGap = 0f))
    }

    @Test
    fun endIsClampedToTheRangeAndToTheStartThumb() {
        assertEquals(30f, clampRangeEnd(candidate = 10f, start = 30f, valueRange = range, minGap = 0f))
        assertEquals(35f, clampRangeEnd(candidate = 10f, start = 30f, valueRange = range, minGap = 5f))
        assertEquals(100f, clampRangeEnd(candidate = 130f, start = 30f, valueRange = range, minGap = 0f))
    }

    @Test
    fun trackShiftKeepsTheSpanAndStaysInsideTheRange() {
        // Window 20..40 pushed far left: it lands at the range start, span 20 preserved.
        assertEquals(0f..20f, shiftRangeIntoBounds(start = -50f, span = 20f, valueRange = range))
        // Pushed far right: the end pins at the range end.
        assertEquals(80f..100f, shiftRangeIntoBounds(start = 200f, span = 20f, valueRange = range))
        // Inside the range: unchanged.
        assertEquals(30f..50f, shiftRangeIntoBounds(start = 30f, span = 20f, valueRange = range))
    }
}
