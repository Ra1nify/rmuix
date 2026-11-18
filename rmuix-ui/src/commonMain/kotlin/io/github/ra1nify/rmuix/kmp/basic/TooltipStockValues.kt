
// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/** `COUIToolTips` style: `couiToolTipsContainerLayoutMarginStart` 12dp. */
internal val TooltipStockContainerMarginStart = 12.dp

/** `COUIToolTips` style: `couiToolTipsContainerLayoutMarginEnd` 42dp. */
internal val TooltipStockContainerMarginEnd = 42.dp

/** `COUIToolTips` style: `couiToolTipsContainerLayoutMarginTop` 12dp. */
internal val TooltipStockContainerMarginTop = 12.dp

/** `COUIToolTips` style: `couiToolTipsContainerLayoutMarginBottom` 12dp. */
internal val TooltipStockContainerMarginBottom = 12.dp

/** `COUIToolTips.DetailFloating`: `?attr/couiColorBackgroundTop` = #FFFFFF (light) / #333333 (dark). */
internal val TooltipStockDetailFloatingContainerLight = Color(0xFFFFFFFF)
internal val TooltipStockDetailFloatingContainerDark = Color(0xFF333333)

/** `COUIToolTips.DetailFloating`: `?attr/couiColorSecondNeutral` = #8A000000 (light) / #8AFFFFFF (dark). */
internal val TooltipStockDetailFloatingContentLight = Color(0x8A000000)
internal val TooltipStockDetailFloatingContentDark = Color(0x8AFFFFFF)

/** `detail_floating_text_margin`: the DetailFloating container keeps 12dp on all four sides. */
internal val TooltipStockDetailFloatingMargin = 12.dp

/** `coui_tooltips_floating_background_padding_left` / `_right`: 16dp horizontal. */
internal val TooltipStockDetailFloatingPaddingHorizontal = 16.dp

/** `detail_floating_min_width`: 80dp. */
internal val TooltipStockDetailFloatingMinWidth = 80.dp

// Values from the Settings APK tool tips
// (`com.coui.appcompat.tooltip.COUIToolTips`, the `COUIToolTips` style and the `coui_tooltips_*` /
// `tool_tips_*` resources).

/** `couiToolTipsBackgroundColor` → `couiColorTips`: `#0080FF` / `#1A8CFF`. */
internal fun tooltipBackground(dark: Boolean): Color = if (dark) Color(0xFF1A8CFF) else Color(0xFF0080FF)

/** `couiToolTipsContentTextColor` = `coui_color_white` in both themes. */
internal val TooltipStockContentColor = Color.White

/** `coui_tooltips_default_background_padding_*`: 13dp on every side. */
internal val TooltipStockInsideMargin = 13.dp

/** `coui_tooltips_os_16_1_radius_17_dp`: the smooth-corner branch the current OS uses. */
internal val TooltipStockCornerRadius = 17.dp

/** `tool_tips_max_width`: the bubble never grows past 328dp. */
internal val TooltipStockMaxWidth = 328.dp

/** `tool_tips_content_text_size`: 13sp. */
internal val TooltipStockTextSize = 13.sp

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isTooltipInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/** `?attr/couiColorBackgroundTop` = `coui_color_background_top`: `#FFFFFF` / `#333333`. */
internal fun tooltipIconBackground(dark: Boolean): Color = if (dark) Color(0xFF333333) else Color(0xFFFFFFFF)

/** `couiColorLabelSecondary`: `#8A000000` / `#8AFFFFFF`. */
internal fun tooltipIconContent(dark: Boolean): Color = if (dark) Color(0x8AFFFFFF) else Color(0x8A000000)

/**
 * `coui_tool_tips_icon_style_layout.xml`: the icon slot is **40dp x 40dp**, `fitCenter`, with a 2dp top
 * margin and a 12dp start margin.
 */
internal val TooltipStockIconSize = 40.dp

/** `layout_marginTop` of the icon slot: 2dp. */
internal val TooltipStockIconMarginTop = 2.dp

/** `layout_marginStart` of the icon slot: 12dp. */
internal val TooltipStockIconMarginStart = 12.dp

/** `coui_tooltips_icon_scroll_margin_top`: 12dp above the scrolling text area. */
internal val TooltipStockIconScrollMarginTop = 12.dp

/**
 * Image variant (`COUIToolTips.Image`): `getMaxWidth()` returns `tool_tips_image_style_max_width` = **260dp**
 * instead of the default `tool_tips_max_width` = 328dp.
 */
internal val TooltipStockImageMaxWidth = 260.dp

/**
 * `coui_tool_tips_image_no_edges_style_layout.xml`: the image area is **132dp** tall and 260dp wide.
 * The layout sets `android:scaleType="centerCrop"` - the image is cropped to fill, not letterboxed.
 */
internal val TooltipStockImageHeight = 132.dp

/** `coui_tooltips_imageview_radius`: 8dp corner radius on the image itself. */
internal val TooltipStockImageRadius = 8.dp

/**
 * `coui_tool_tips_layout.xml` sizes the dismiss `ImageView` (`@+id/dismissIv`) with
 * `coui_tool_tips_delete_background_size` = 18dp, at `end|center_vertical` with a 12dp end margin
 * (`coui_tool_tips_delete_icon_padding`). `coui_tool_tips_delete_icon` is the 18dp glyph drawn
 * inside it, and the similarly named coui_tool_tips_delete_icon_size dimen is 44dp but never read.
 */
internal val TooltipStockDismissSize = 18.dp

/**
 * `coui_tool_tips_delete_icon_size` = 44dp - **declared but never read**.
 *
 * `coui_tool_tips_layout.xml` sizes the dismiss `ImageView` with `coui_tool_tips_delete_background_size`
 * (18dp) and never references this dimen or the padding one, so the stock control has **no 44dp touch
 * target** - the 18dp glyph is the whole hit area.
 */
internal val TooltipStockDismissTouchSize = 44.dp

/** `coui_tool_tips_delete_icon_padding` = 12dp - **declared but never read** (same layout evidence). */
internal val TooltipStockDismissPadding = 12.dp

/** `layout_marginEnd` of the dismiss control: 12dp. */
internal val TooltipStockDismissMarginEnd = 12.dp

/**
 * `coui_tool_tips_delete_icon.xml`: an 18dp viewport with a **30% white** disc
 * (`M9,9m-9,0a9,9 0,1 1,18 0a9,9 0,1 1,-18 0`) and a solid white cross whose arms are 0.81 wide.
 */
internal const val TOOLTIP_DISMISS_DISC_PATH_DATA =
    "M9,9m-9,0a9,9 0,1 1,18 0a9,9 0,1 1,-18 0"

/** The cross of `coui_tool_tips_delete_icon.xml`, drawn at full white. */
internal const val TOOLTIP_DISMISS_CROSS_PATH_DATA =
    "M9,7.87 L6.6,5.46a0.81,0.81 0,0 0,-1.14 0h0a0.81,0.81 0,0 0,0 1.14L7.87,9 5.46,11.4" +
        "a0.81,0.81 0,0 0,0 1.14h0a0.81,0.81 0,0 0,1.14 0L9,10.13l2.4,2.41a0.81,0.81 0,0 0,1.14 0h0" +
        "a0.81,0.81 0,0 0,0 -1.14L10.13,9l2.41,-2.4a0.81,0.81 0,0 0,0 -1.14h0a0.81,0.81 0,0 0,-1.14 0Z"
