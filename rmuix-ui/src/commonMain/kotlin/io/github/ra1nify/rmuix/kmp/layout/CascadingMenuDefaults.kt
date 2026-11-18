// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.layout

import androidx.compose.ui.unit.dp

/**
 * Divider geometry shared by the cascading (mixed) popup menus.
 *
 * `MixedPopupListAdapter.DividerViewHolder` inflates a full-width row of
 * `coui_popup_list_group_divider_total_height` (**12dp**) and paints a
 * `coui_popup_list_divider_height` (**0.5dp**) line inside it with an `InsetDrawable` of
 * `coui_popup_list_default_divider_margin_horizontal` (**20dp**) on each side and
 * `(12 - 0.5) / 2 = 5.75dp` above and below. The library drew a 1.5dp line with 4dp vertical padding
 * (7dp total), which is both thicker and shorter than the stock row.
 */
internal object CascadingMenuDefaults {
    /** Visible line thickness (`coui_popup_list_divider_height`): 0.5dp. */
    val DividerThickness = 0.5.dp

    /** Horizontal inset of the line (`coui_popup_list_default_divider_margin_horizontal`): 20dp. */
    val DividerHorizontalInset = 20.dp

    /** Vertical inset that makes the row 12dp tall: (12 - 0.5) / 2 = 5.75dp. */
    val DividerVerticalInset = 5.75.dp
}
