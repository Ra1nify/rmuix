// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.state.ToggleableState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StockToggleSelectorTest {
    @Test
    fun enabledCheckboxHasAllSixDirectedTransitions() {
        for (from in ToggleableState.entries) {
            for (to in ToggleableState.entries) {
                val transition = stockToggleTransition(from, to, wasEnabled = true, enabled = true, animateDisabledPartTransitions = true)
                if (from == to) {
                    assertNull(transition)
                } else {
                    assertNotNull(transition)
                    assertEquals(from, transition.from)
                    assertEquals(to, transition.to)
                }
            }
        }
    }

    @Test
    fun enabledChangesSelectStaticItemsWithoutAnAnimation() {
        for (from in ToggleableState.entries) {
            for (to in ToggleableState.entries) {
                assertNull(stockToggleTransition(from, to, wasEnabled = true, enabled = false, animateDisabledPartTransitions = true))
                assertNull(stockToggleTransition(from, to, wasEnabled = false, enabled = true, animateDisabledPartTransitions = true))
            }
        }
    }

    @Test
    fun disabledCheckboxOnlyAnimatesBetweenPartAndSelected() {
        assertNotNull(stockToggleTransition(ToggleableState.Indeterminate, ToggleableState.On, false, false, true))
        assertNotNull(stockToggleTransition(ToggleableState.On, ToggleableState.Indeterminate, false, false, true))
        for (state in ToggleableState.entries) {
            assertNull(stockToggleTransition(ToggleableState.Off, state, false, false, true))
            assertNull(stockToggleTransition(state, ToggleableState.Off, false, false, true))
        }
    }

    @Test
    fun radioDisabledItemsNeverAnimate() {
        assertNull(stockToggleTransition(ToggleableState.Off, ToggleableState.On, false, false, false))
        assertNull(stockToggleTransition(ToggleableState.On, ToggleableState.Off, false, false, false))
    }

    @Test
    fun directedPartAndSelectedTransitionsUseTheirOwnFullDuration() {
        val entering = assertNotNull(stockToggleTransition(ToggleableState.Off, ToggleableState.Indeterminate, true, true, true))
        val interrupted = assertNotNull(stockToggleTransition(entering.to, ToggleableState.On, true, true, true))
        assertEquals(ToggleableState.Indeterminate, interrupted.from)
        assertEquals(351, interrupted.durationMillis)
        val reversed = assertNotNull(stockToggleTransition(interrupted.to, ToggleableState.Off, true, true, true))
        assertEquals(ToggleableState.On, reversed.from)
        assertEquals(300, reversed.durationMillis)
    }
}
