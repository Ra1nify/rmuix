// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks [SnackbarDefaults] to the `coui_snack_bar_*` dimens read out of the Settings APK.
 *
 * `CornerRadius` is a branch value, not a dimen: `COUISnackBar.getOutline` picks it from
 * `RoundCornerUtil.getSmoothStyleType()` and the single-line flag, and 13dp is the smooth OS15
 * multi-line branch this port draws.
 */
class SnackbarDefaultsTest {
    @Test
    fun surfaceMatchesTheSnackBarTokens() {
        // coui_snack_bar_max_width / coui_snack_bar_margin_bottom
        assertEquals(328.dp, SnackbarDefaults.MaxWidth)
        assertEquals(32.dp, SnackbarDefaults.MarginBottom)
        // coui_snack_bar_shadow_size
        assertEquals(26.dp, SnackbarDefaults.ShadowRadius)
        // Legacy placeholder: the single-line radius is now derived from the measured bar height
        // (`height / 2` in COUISnackBar.getOutline); the constant is kept for source compatibility.
        assertEquals(24.dp, SnackbarDefaults.CornerRadiusSingleLine)
        // ColorOS 17 first branch: wrapped -> ?attr/couiRoundCornerL = 16dp
        assertEquals(16.dp, SnackbarDefaults.CornerRadius)
    }

    @Test
    fun contentMatchesTheChildMarginTokens() {
        // coui_snack_bar_child_margin_horizontal_start / _end
        assertEquals(16.dp, SnackbarDefaults.ContentHorizontalPadding)
        // coui_snack_bar_child_margin_vertical
        assertEquals(10.dp, SnackbarDefaults.ContentVerticalPadding)
        // coui_snack_bar_child_margin_vertical_multi_lines / _bottom
        assertEquals(12.dp, SnackbarDefaults.ContentVerticalPaddingMultiline)
        assertEquals(8.dp, SnackbarDefaults.ContentVerticalPaddingMultilineBottom)
        // coui_snack_bar_icon_width / coui_snack_bar_icon_height
        assertEquals(30.dp, SnackbarDefaults.IconSize)
        // coui_snack_bar_icon_margin_top_horizontal
        assertEquals(16.dp, SnackbarDefaults.IconVerticalMargin)
    }

    @Test
    fun actionMatchesTheActionTokens() {
        // coui_snack_bar_action_max_width
        assertEquals(110.dp, SnackbarDefaults.ActionMaxWidth)
        // coui_snack_bar_action_margin_horizontal_end
        assertEquals(4.dp, SnackbarDefaults.ActionMarginEnd)
        // coui_snack_bar_action_margin_vertical
        assertEquals(10.dp, SnackbarDefaults.ActionVerticalMargin)
    }

    @Test
    fun deprecatedPillDefaultsStayFrozen() {
        // Kept for source compatibility; the ColorOS action is a plain label with no pill.
        assertEquals(50.dp, SnackbarDefaults.ActionCornerRadius)
    }

    @Test
    fun iconSlotMatchesTheBaseSnackBarTokens() {
        // coui_snack_bar_icon_width / coui_snack_bar_icon_height
        assertEquals(30.dp, SnackbarDefaults.IconWidth)
        assertEquals(30.dp, SnackbarDefaults.IconHeight)
        assertEquals(30.dp, SnackbarDefaults.IconSize)
        // coui_snack_bar_child_margin_horizontal_start: the gap after the icon
        assertEquals(16.dp, SnackbarDefaults.IconEndPadding)
        // coui_snack_bar_icon_margin_top_horizontal
        assertEquals(16.dp, SnackbarDefaults.IconVerticalMargin)
    }

    @Test
    fun actionLabelUsesPrimaryTextOnPopup() {
        // coui_snack_bar_action_text_color -> ?attr/couiColorPrimaryTextOnPopup: #0080FF / #5C9DFF.
        assertEquals(Color(0xFF0080FF), snackbarActionContentColor(false))
        assertEquals(Color(0xFF5C9DFF), snackbarActionContentColor(true))
    }
}
