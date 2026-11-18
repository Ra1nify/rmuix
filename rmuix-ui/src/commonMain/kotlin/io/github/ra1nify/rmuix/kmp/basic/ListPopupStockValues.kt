// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

// Values from the Settings APK list popup window (the `coui_popup_list_window_*` dimens).

/** `coui_popup_list_window_min_width`: the window never measures narrower than 168dp. */
internal val ListPopupStockMinWidth = 168.dp

/** `coui_popup_list_window_max_width`: and never wider than 256dp. */
internal val ListPopupStockMaxWidth = 256.dp

/**
 * `coui_popup_list_window_width_with_icon`: `COUIPopupListWindow.getMainMenuMinWidth()` uses 196dp
 * when the menu **has a submenu** (the submenu chevron is the "icon"), and 168dp otherwise. The list
 * popup measures its items, so this preset is documented rather than applied automatically — it is
 * **not read at runtime**; the matching unit test keeps the value from drifting.
 */
internal val ListPopupStockWidthWithIcon = 196.dp

/**
 * `coui_popup_window_background.xml` corner radius: `@dimen/coui_round_corner_m` = 12dp. (The
 * separately declared `coui_popup_list_window_content_radius` of 12dp and the smooth-corner
 * `coui_round_corner_m_radius` of 9dp are not what the drawable references.)
 */
internal val ListPopupStockCornerRadius = 12.dp

/** `coui_popup_list_padding_vertical`: the menu applies 8dp top/bottom padding to its list. */
internal val ListPopupStockVerticalPadding = 8.dp

/** `coui_popup_list_divider_height`: the hairline between rows is 0.5dp. */
internal val ListPopupStockDividerHeight = 0.5.dp

/** `coui_popup_list_default_divider_margin_horizontal`: 20dp inset on both sides by default. */
internal val ListPopupStockDividerMarginHorizontal = 20.dp

/** `coui_popup_list_default_divider_margin_start_with_icon`: 52dp start inset when rows have icons. */
internal val ListPopupStockDividerStartWithIcon = 52.dp

/** `coui_popup_list_group_divider_height`: the gap between groups is a 4dp block. */
internal val ListPopupStockGroupDividerHeight = 4.dp

/** `coui_popup_list_group_divider_color`: `#14000000` in both themes (no night variant). */
internal val ListPopupStockGroupDividerColor = Color(0x14000000)

/**
 * `coui_popup_list_group_divider_total_height`: `MixedPopupListAdapter` gives the group divider row a
 * 12dp height and insets the `coui_popup_list_divider_height` (0.5dp) line by
 * `(12 - 0.5) / 2 = 5.75dp` at the top and bottom, with `coui_popup_list_default_divider_margin_horizontal`
 * (20dp) at both sides.
 */
internal val ListPopupStockMixedGroupDividerTotalHeight = 12.dp

/** `(coui_popup_list_group_divider_total_height - coui_popup_list_divider_height) / 2`: 5.75dp. */
internal val ListPopupStockMixedGroupDividerVerticalInset = 5.75.dp

/** `?attr/couiColorDivider` for the row hairline: `#1F000000` / `#33FFFFFF`. */
internal fun listPopupDividerColor(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

/** `coui_popup_window_background.xml` fill: `couiColorBackgroundTop` = `#FFFFFF` / `#333333`. */
internal fun listPopupBackground(dark: Boolean): Color = if (dark) Color(0xFF333333) else Color(0xFFFFFFFF)

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isListPopupInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

// The shared mask of `COUITouchRecyclerView` (`poplist` package): a highlight that slides between rows.
// Its geometry is fully recorded below; the slide itself is **not** implemented because it needs the
// measured bounds of the highlighted row, which this library does not track.

/**
 * `coui_popup_list_window_vertical_mask_radius_16_1`: 28dp on OS > 37 with blur effects, else the 20dp
 * `coui_popup_list_window_vertical_mask_radius`.
 */
internal val ListPopupStockSharedMaskRadius16_1 = 28.dp

/** `coui_popup_list_window_vertical_mask_radius`: 20dp. */
internal val ListPopupStockSharedMaskRadius = 20.dp

/** `coui_popup_list_window_horizontal_mask_radius_icon_only_16_1`: 17dp. */
internal val ListPopupStockSharedMaskRadiusIconOnly16_1 = 17.dp

/** `coui_popup_list_window_horizontal_mask_radius_icon_only`: 12dp. */
internal val ListPopupStockSharedMaskRadiusIconOnly = 12.dp

/** `coui_popup_list_window_horizontal_mask_radius_with_text_16_1`: 23dp. */
internal val ListPopupStockSharedMaskRadiusWithText16_1 = 23.dp

/** `coui_popup_list_window_horizontal_mask_radius_with_text`: 16dp. */
internal val ListPopupStockSharedMaskRadiusWithText = 16.dp

/** `coui_popup_list_window_mask_margin_horizontal`: the mask is inset 8dp from the panel edges. */
internal val ListPopupStockSharedMaskMarginHorizontal = 8.dp

/**
 * `coui_popup_list_window_mask_margin_vertical`: 8dp - **declared but never read**.
 *
 * `COUITouchRecyclerView` reads the horizontal sibling but never this one.
 */
internal val ListPopupStockSharedMaskMarginVertical = 8.dp

/** `coui_popup_list_window_shared_mask_smooth_round_corner_weight`: 3, read as a float. */
internal const val LIST_POPUP_SHARED_MASK_SMOOTH_WEIGHT = 3f

/** `coui_popup_list_window_horizontal_item_icon_only_height`: a horizontal icon-only row is 40dp tall. */
internal val ListPopupStockHorizontalIconOnlyHeight = 40.dp

/** `?attr/couiColorPress`: `#1F000000` light / `#33FFFFFF` dark - the shared mask while pressed. */
internal fun listPopupSharedMaskPressedColor(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

/** `?attr/couiColorHover`: `#14000000` / `#26FFFFFF` - the shared mask under a hovering pointer. */
internal fun listPopupSharedMaskHoverColor(dark: Boolean): Color = if (dark) Color(0x26FFFFFF) else Color(0x14000000)
