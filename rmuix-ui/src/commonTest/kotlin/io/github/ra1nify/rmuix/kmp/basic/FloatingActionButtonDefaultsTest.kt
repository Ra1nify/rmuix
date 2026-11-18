// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class FloatingActionButtonDefaultsTest {
    @Test
    fun buttonMetricsMatchTheFabTokens() {
        // coui_floating_button_fab_normal_size
        assertEquals(56.dp, FloatingActionButtonDefaults.MinWidth)
        assertEquals(56.dp, FloatingActionButtonDefaults.MinHeight)
        // coui_floating_button_fab_mini_size (not the Material 40dp)
        assertEquals(54.dp, FloatingActionButtonDefaults.MiniSize)
        // coui_floating_button_fab_side_margin
        assertEquals(12.dp, FloatingActionButtonDefaults.SideMargin)
        // coui_shadow_elevation_caustic_fab
        assertEquals(8.dp, FloatingActionButtonDefaults.ShadowElevation)
    }
}
