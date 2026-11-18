// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks [CheckboxDefaults] to the two stock vectors.
 *
 * Checked (`coui_btn_check_on_normal.xml`): a 24dp viewport whose filled box runs 3 → 21 (18dp) with a
 * 4dp corner radius, plus the checkmark path the component copies verbatim.
 *
 * Unchecked (`coui_btn_check_off_normal.xml`): the same 24dp viewport, but the box is a **stroke**, not a
 * fill — 3.8 → 20.2 (16.4dp), radius 3.2dp, `strokeWidth="1.3"`. The two states are therefore not scaled
 * copies of one another, which is why both geometries are asserted here.
 */
class CheckboxDefaultsTest {
    @Test
    fun touchTargetAndCheckedBoxMatchTheOnVector() {
        assertEquals(24.dp, CheckboxDefaults.Size)
        assertEquals(18.dp, CheckboxDefaults.BoxSize)
        assertEquals(4.dp, CheckboxDefaults.CornerRadius)
    }

    @Test
    fun uncheckedBoxMatchesTheOffVector() {
        assertEquals(16.4.dp, CheckboxDefaults.UncheckedBoxSize)
        assertEquals(3.2.dp, CheckboxDefaults.UncheckedCornerRadius)
        assertEquals(3.8.dp, CheckboxDefaults.UncheckedBoxTopLeft)
    }
}
