// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

// Values from the Settings APK red dot: `Widget.COUI.COUIHintRedDot` and the
// `coui_dot_diameter` / `coui_height` / `coui_hint_*` resources.

/** `?attr/couiHintRedDotColor` resolves to `@color/coui_color_additional_red`: `#EB3B2F` / `#EB493D`. */
internal fun redDotBackground(dark: Boolean): Color = if (dark) Color(0xFFEB493D) else Color(0xFFEB3B2F)

/** `couiHintRedDotTextColor` = `coui_color_white`. */
internal val RedDotStockTextColor = Color.White

/** `coui_height`: the number/text badge is 16dp tall (all three width tiers share it). */
internal val RedDotStockBadgeHeight = 16.dp

/** `coui_small_width`: one character (or the small style) is 16dp wide — a circle. */
internal val RedDotStockSmallWidth = 16.dp

/** `coui_medium_width`: two characters are 20dp wide. */
internal val RedDotStockMediumWidth = 20.dp

/** `coui_large_width`: three or more characters use 26dp. */
internal val RedDotStockLargeWidth = 26.dp

/** `couiEllipsisDiameter`: 2dp per ellipsis dot. A count of 1000 or more renders as three dots. */
internal val RedDotStockEllipsisDotSize = 2.dp

/** `coui_hint_red_dot_ellipsis_spacing`: 2dp between the three ellipsis dots. */
internal val RedDotStockEllipsisSpacing = 2.dp

/**
 * `couiCornerRadius` = `?attr/couiRoundCornerFULL`, i.e. a pill: the badge radius is half its height.
 *
 * `coui_hint_red_dot_rect_radius` (2.67dp) is **not** the source: `COUIHintRedDotHelper` assigns it to
 * `mDotCornerRadius` and never reads that field, and the point dot is drawn as
 * `drawCircle(..., (bottom - top) / 2f, ...)` - radius from the box height, no token involved. No layout
 * or style references the dimen either.
 */
internal val RedDotStockBadgeRadius = 8.dp

/** `couiDotDiameter`: the point-only dot is 6dp. */
internal val RedDotStockPointSize = 6.dp

/**
 * `couiHintTextSize` / `coui_hint_red_dot_large_text_size` = 10dp. The stock dimens are **dp**, so the
 * digits do not follow the system font scale; Compose can only express font size in sp, so this port
 * scales with the font setting — a known, accepted deviation.
 */
internal val RedDotStockTextSize = 10.sp

/** `coui_hint_red_dot_small_text_size`: 9dp. Not read at runtime; the widget always draws 10sp. */
internal val RedDotStockSmallTextSize = 9.sp

/** `coui_dot_stroke_width`: the optional ring around a point dot is 1dp, in `coui_color_white`. */
internal val RedDotStockStrokeWidth = 1.dp

/**
 * `coui_hint_red_dot_mode_stroke_extra_diameter`: the struck/outline modes (`POINT_ONLY_MODE_STROKE` and
 * `POINT_NUM_MODE_STROKE`) grow the dot by 2dp so the `coui_dot_stroke_width` ring fits around it.
 */
internal val RedDotStockStrokeExtraDiameter = 2.dp

/** `coui_hint_red_dot_medium_icon_size`: anchors below 36dp use the small bucket. */
internal val RedDotStockMediumAnchorSize = 36.dp

/** `coui_hint_red_dot_large_icon_size`: anchors of 50dp and above use the large bucket. */
internal val RedDotStockLargeAnchorSize = 50.dp

/** `coui_hint_red_dot_small_icon_topend_margin_circle`: small circle anchors inset the dot by 1dp. */
internal val RedDotStockSmallCircleMargin = 1.dp

/** `coui_hint_red_dot_medium_icon_topend_margin_circle`: medium circle anchors inset the dot by 2dp. */
internal val RedDotStockMediumCircleMargin = 2.dp

/** `coui_hint_red_dot_large_icon_topend_margin_circle`: large circle anchors inset the dot by 2dp. */
internal val RedDotStockLargeCircleMargin = 2.dp

/** `coui_hint_red_dot_small_icon_topend_margin_rectangle`: small rectangle anchors grow by 1dp. */
internal val RedDotStockSmallRectangleMargin = 1.dp

/** `coui_hint_red_dot_medium_icon_topend_margin_rectangle`: medium rectangle anchors grow by 2dp. */
internal val RedDotStockMediumRectangleMargin = 2.dp

/** `coui_hint_red_dot_large_icon_topend_margin_rectangle`: large rectangle anchors grow by 4dp. */
internal val RedDotStockLargeRectangleMargin = 4.dp

/** `coui_hint_red_dot_small_number_topend_margin_rectangle`: small number badges grow the container by 6dp. */
internal val RedDotStockSmallNumberMargin = 6.dp

/** `coui_hint_red_dot_medium_number_topend_margin_rectangle`: medium number badges grow the container by 3dp. */
internal val RedDotStockMediumNumberMargin = 3.dp

/** `coui_hint_red_dot_large_number_topend_margin_rectangle`: large number badges grow the container by 6dp. */
internal val RedDotStockLargeNumberMargin = 6.dp

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isRedDotInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/** Default badge colour for the current theme. */
@Composable
internal fun redDotDefaultColor(): Color = redDotBackground(isRedDotInDarkTheme())
