// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CheckboxRenderingTest {
    @Test
    fun artworkFitsTheStockViewport() {
        assertEquals(24.dp, CheckboxDefaults.Size)
        assertEquals(18.dp, CheckboxDefaults.BoxSize)
        assertEquals(4.dp, CheckboxDefaults.CornerRadius)
        assertEquals(1.3.dp, CheckboxDefaults.StrokeWidth)
    }

    @Test
    fun allTransitionsFinishWithTheirTargetLayers() {
        for (initialState in ToggleableState.entries) {
            for (targetState in ToggleableState.entries) {
                if (initialState == targetState) continue
                val duration = stockToggleTransitionDuration(initialState, targetState)
                val frame = checkboxTransitionFrame(initialState, targetState, duration.toFloat())

                assertEquals(1f, frame.boxScale, 0.000001f)
                assertEquals(1f, frame.dashScaleX, 0.000001f)
                assertEquals(1f, frame.dashScaleY, 0.000001f)
                assertEquals(if (targetState == ToggleableState.Off) 0f else 1f, frame.fillAlpha, 0.000001f)
                assertEquals(if (targetState == ToggleableState.On) 1f else 0f, frame.trimEnd, 0.000001f)
                assertEquals(if (targetState == ToggleableState.Indeterminate) 1f else 0f, frame.dashClip, 0.000001f)
                assertEquals(if (targetState == ToggleableState.Indeterminate) 1f else 0f, frame.dashWidth, 0.000001f)
            }
        }
    }

    @Test
    fun deselectionRetainsItsFillForTheFirst150Milliseconds() {
        for (initialState in listOf(ToggleableState.On, ToggleableState.Indeterminate)) {
            val frame = checkboxTransitionFrame(initialState, ToggleableState.Off, 150f)
            assertEquals(1f, frame.fillAlpha, 0.000001f)
            assertEquals(1.08f, frame.boxScale, 0.000001f)
        }
    }

    @Test
    fun selectingPartStateStartsTheDashAfter100Milliseconds() {
        val before = checkboxTransitionFrame(ToggleableState.On, ToggleableState.Indeterminate, 100f)
        val after = checkboxTransitionFrame(ToggleableState.On, ToggleableState.Indeterminate, 200f)

        assertEquals(0f, before.dashClip, 0.000001f)
        assertEquals(0f, before.dashWidth, 0.000001f)
        assertEquals(0.5f, after.dashWidth, 0.000001f)
        assertTrue(after.dashClip > after.dashWidth)
    }

    @Test
    fun partToSelectedRetainsTheNestedNonUniformDashScale() {
        assertEquals(351, stockToggleTransitionDuration(ToggleableState.Indeterminate, ToggleableState.On))
        val peak = checkboxTransitionFrame(ToggleableState.Indeterminate, ToggleableState.On, 150f)
        val returning = checkboxTransitionFrame(ToggleableState.Indeterminate, ToggleableState.On, 225f)

        assertEquals(1.08f * 1.08f, peak.dashScaleX, 0.000001f)
        assertEquals(1.08f * 1.08f, peak.dashScaleY, 0.000001f)
        assertTrue(returning.dashScaleX > returning.dashScaleY)
    }
}
