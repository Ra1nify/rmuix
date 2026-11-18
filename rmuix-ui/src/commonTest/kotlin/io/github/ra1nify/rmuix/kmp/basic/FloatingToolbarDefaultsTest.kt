// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks the group margins of [FloatingToolbar] to the `coui_floating_toolbar_group_margin_*` dimens.
 *
 * `COUIBottomFloatingToolbar.changePadding()` selects them through `COUIResponsiveUtils`, which
 * delegates to material's `WindowWidthSizeClass` (Compact < 600dp, Medium 600..840dp, Expanded).
 */
class FloatingToolbarDefaultsTest {
    @Test
    fun groupMarginsMatchTheStockDimens() {
        assertEquals(16.dp, FloatingToolbarDefaults.GroupMarginSmallScreen)
        assertEquals(24.dp, FloatingToolbarDefaults.GroupMarginMediumScreen)
        assertEquals(40.dp, FloatingToolbarDefaults.GroupMarginLargeScreen)
    }

    @Test
    fun groupMarginFollowsTheWindowWidthClass() {
        assertEquals(16.dp, FloatingToolbarDefaults.groupMarginFor(412.dp))
        assertEquals(16.dp, FloatingToolbarDefaults.groupMarginFor(599.dp))
        assertEquals(24.dp, FloatingToolbarDefaults.groupMarginFor(600.dp))
        assertEquals(24.dp, FloatingToolbarDefaults.groupMarginFor(839.dp))
        assertEquals(40.dp, FloatingToolbarDefaults.groupMarginFor(840.dp))
        assertEquals(40.dp, FloatingToolbarDefaults.groupMarginFor(1024.dp))
    }
}
