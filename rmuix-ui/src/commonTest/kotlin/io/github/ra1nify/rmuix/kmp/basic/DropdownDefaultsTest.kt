// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks the dropdown sizing to the `coui_popup_list_*` dimens that were read out of the Settings APK.
 *
 * Only tokens that could be located by name are asserted here. `CheckIconSize`, `IconMinSize`,
 * `MaxItemTextWidth` and `DialogHorizontalPadding` have no dedicated stock dimen in this APK, so they
 * stay visual defaults with no numeric claim attached.
 */
class DropdownDefaultsTest {
    @Test
    fun rowIndicatorsMatchTheStockVectors() {
        // coui_list_expandable_indicator: 24dp x 24dp, two stacked 1.4dp round-cap chevrons.
        assertEquals(24.dp, DropdownDefaults.ChevronSize.width)
        assertEquals(24.dp, DropdownDefaults.ChevronSize.height)
        // coui_menu_ic_checkbox_selected_new: 24dp viewport (a stroked tick, not a filled box).
        assertEquals(24.dp, DropdownDefaults.CheckIconSize)
    }

    @Test
    fun menuSurfaceMatchesThePopupListWindowTokens() {
        // coui_popup_list_window_min_width / coui_popup_list_window_max_width
        assertEquals(168.dp, DropdownDefaults.MinWidth)
        assertEquals(256.dp, DropdownDefaults.MaxWidth)
        // coui_popup_list_window_width_with_icon (menus whose rows carry icons)
        assertEquals(196.dp, DropdownDefaults.WidthWithIcon)
        // coui_round_corner_m_radius: 9dp menu surface radius.
        assertEquals(9.dp, DropdownDefaults.ContentCornerRadius)
        // coui_popup_list_window_default_vertical_gap_to_anchor
        assertEquals(8.dp, DropdownDefaults.VerticalGapToAnchor)
    }

    @Test
    fun rowsMatchThePopupListItemTokens() {
        // coui_popup_list_window_item_min_height
        assertEquals(40.dp, DropdownDefaults.MinHeight)
        // coui_popup_list_window_item_padding_top_and_bottom
        assertEquals(8.dp, DropdownDefaults.FirstLastVerticalPadding)
        assertEquals(8.dp, DropdownDefaults.MiddleVerticalPadding)
        // coui_popup_list_window_item_icon_margin_left
        assertEquals(20.dp, DropdownDefaults.InsideHorizontalPadding)
        // 12dp; the same-named dimen is never referenced
        assertEquals(12.dp, DropdownDefaults.IconEndPadding)
        // 18dp; the same-named dimen is never referenced, so this is a library spacing
        assertEquals(18.dp, DropdownDefaults.CheckIconStartPadding)
    }

    @Test
    fun dividersMatchThePopupListDividerTokens() {
        // coui_popup_list_divider_height
        assertEquals(0.5.dp, DropdownDefaults.DividerThickness)
        // coui_popup_list_default_divider_margin_horizontal
        assertEquals(20.dp, DropdownDefaults.DividerStartIndent)
        // coui_popup_list_default_divider_margin_start_with_icon
        assertEquals(52.dp, DropdownDefaults.DividerStartIndentWithIcon)
        // coui_popup_list_group_divider_height / coui_popup_list_group_divider_total_height
        assertEquals(4.dp, DropdownDefaults.GroupDividerHeight)
        assertEquals(12.dp, DropdownDefaults.GroupDividerTotalHeight)
    }
}
