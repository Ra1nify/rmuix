// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import kotlin.test.Test
import kotlin.test.assertEquals

/** Records the haptic types a slider asks for, so the timing model can be asserted without a device. */
private class RecordingHapticFeedback : HapticFeedback {
    val types = mutableListOf<HapticFeedbackType>()

    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
        types += hapticFeedbackType
    }
}

class SliderHapticStateTest {
    private val range = 0f..1f
    private val continuous = SliderDefaults.SliderHapticEffect.Continuous

    @Test
    fun continuousTicksOncePerChangedValue() {
        // ColorOS `VibrationFeedbackHandler.performFeedback`: one feedback per new progress value.
        val haptic = RecordingHapticFeedback()
        val state = SliderHapticState()

        state.handleHapticFeedback(0.1f, range, continuous, haptic)
        // A stationary finger keeps reporting the same value; the stock handler de-dupes it.
        state.handleHapticFeedback(0.1f, range, continuous, haptic)
        state.handleHapticFeedback(0.2f, range, continuous, haptic)

        assertEquals(2, haptic.types.size)
        assertEquals(HapticFeedbackType.SegmentFrequentTick, haptic.types[0])
        assertEquals(HapticFeedbackType.SegmentFrequentTick, haptic.types[1])
    }

    @Test
    fun reachingAnEndFiresTheHeavierTypeOnceAndResetsAfterLeaving() {
        val haptic = RecordingHapticFeedback()
        val state = SliderHapticState()

        state.handleHapticFeedback(0.5f, range, continuous, haptic)
        state.handleHapticFeedback(1f, range, continuous, haptic)
        // Still at the end: the edge feedback must not repeat.
        state.handleHapticFeedback(1f, range, continuous, haptic)
        state.handleHapticFeedback(0.9f, range, continuous, haptic)
        state.handleHapticFeedback(1f, range, continuous, haptic)

        assertEquals(2, haptic.types.count { it == HapticFeedbackType.GestureThresholdActivate })
        assertEquals(HapticFeedbackType.SegmentFrequentTick, haptic.types.first())
    }

    @Test
    fun stepTicksOnCrossingInsteadOfEveryFrameInTheBand() {
        val haptic = RecordingHapticFeedback()
        val state = SliderHapticState()
        val keyPoints = floatArrayOf(0.5f)
        val step = SliderDefaults.SliderHapticEffect.Step

        state.handleHapticFeedback(0.40f, range, step, haptic, keyPoints)
        // Walking up to the key point must not tick per event.
        state.handleHapticFeedback(0.45f, range, step, haptic, keyPoints)
        state.handleHapticFeedback(0.49f, range, step, haptic, keyPoints)
        // Crossing it ticks exactly once.
        state.handleHapticFeedback(0.51f, range, step, haptic, keyPoints)
        state.handleHapticFeedback(0.52f, range, step, haptic, keyPoints)

        assertEquals(2, haptic.types.size)
        assertEquals(HapticFeedbackType.SegmentTick, haptic.types[0])
        assertEquals(HapticFeedbackType.SegmentTick, haptic.types[1])
    }

    @Test
    fun edgeEffectOnlyFiresAtTheEnds() {
        val haptic = RecordingHapticFeedback()
        val state = SliderHapticState()

        state.handleHapticFeedback(0.2f, range, SliderDefaults.SliderHapticEffect.Edge, haptic)
        state.handleHapticFeedback(0.4f, range, SliderDefaults.SliderHapticEffect.Edge, haptic)
        state.handleHapticFeedback(0f, range, SliderDefaults.SliderHapticEffect.Edge, haptic)

        assertEquals(1, haptic.types.size)
        assertEquals(HapticFeedbackType.GestureThresholdActivate, haptic.types.single())
    }

    @Test
    fun noneIsSilent() {
        val haptic = RecordingHapticFeedback()
        val state = SliderHapticState()

        state.handleHapticFeedback(0.1f, range, SliderDefaults.SliderHapticEffect.None, haptic)
        state.handleHapticFeedback(1f, range, SliderDefaults.SliderHapticEffect.None, haptic)

        assertEquals(0, haptic.types.size)
    }

    @Test
    fun resetSuppressesTheInitialEdgeRepeat() {
        val haptic = RecordingHapticFeedback()
        val state = SliderHapticState()

        // A drag that starts already parked at the end must not fire the edge feedback on its first event.
        state.reset(currentValue = 1f, valueRange = range)
        state.handleHapticFeedback(1f, range, continuous, haptic)

        assertEquals(0, haptic.types.size)
    }
}
