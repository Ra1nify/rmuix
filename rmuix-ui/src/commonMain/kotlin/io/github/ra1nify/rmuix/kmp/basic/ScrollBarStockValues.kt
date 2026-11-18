// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

// Values from the Settings APK scroll bar
// (`com.coui.appcompat.scrollbar.COUIScrollBar` and the `coui_scrollbar_*` resources).

/**
 * `coui_scrollbar_wight` (14dp) minus the two `coui_scrollbar_drawable_default_inset` (5.5dp) sides:
 * the resting thumb is 3dp wide.
 */
internal val ScrollBarStockThumbWidth = 3.dp

/** `coui_scrollbar_wight` (14dp) minus the two `coui_scrollbar_drawable_pressed_inset` (4dp) sides. */
internal val ScrollBarStockDragThumbWidth = 6.dp

/**
 * `coui_scrollbar_margin_top`: the stock bar wraps its drawable in an `InsetDrawable`, so the track
 * never touches the top edge.
 */
internal val ScrollBarStockTrackTopMargin = 8.dp

/** `coui_scrollbar_drawable_default_inset`: the thumb sits 5.5dp from the bar's end edge. */
internal val ScrollBarStockEndInset = 5.5.dp

/** `coui_scrollbar_drawable_pressed_inset`: while highlighted or dragging the thumb moves 1.5dp closer. */
internal val ScrollBarStockPressedEndInset = 4.dp

/** `coui_scrollbar_margin_bottom`: 16dp of inset below the track. */
internal val ScrollBarStockTrackBottomMargin = 16.dp

/** `coui_scrollbar_min_height`: the shortest thumb rect the stock bar lays out. */
internal val ScrollBarStockThumbMinLength = 48.dp

/**
 * The shortest *visible* thumb: the stock thumb rect is wrapped in an `InsetDrawable` whose vertical insets
 * are `coui_scrollbar_margin_top` (8dp) and `coui_scrollbar_margin_bottom` (16dp), so 48 - 8 - 16 = 24dp.
 *
 * This port applies those insets as [ScrollBarDefaults.VerticalTrackPadding] around the track instead, so
 * `ThumbMinLength` must use the visible 24dp to keep the drawn pill identical.
 */
internal val ScrollBarStockVisibleThumbMinLength =
    ScrollBarStockThumbMinLength - ScrollBarStockTrackTopMargin - ScrollBarStockTrackBottomMargin

/**
 * `coui_scrollbar_color` is `#29000000` (light) and `#40FFFFFF` (dark) — 41/255 ≈ 0.16 and
 * 64/255 ≈ 0.25 of the neutral. The pressed state only widens the thumb, so the alpha is constant.
 */
internal fun scrollBarThumbAlpha(dark: Boolean): Float = if (dark) 64f / 255f else 41f / 255f

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isScrollBarInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
