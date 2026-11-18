// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/** `coui_chip_default_min_height`: the base/suggestion chip height. */
internal val ChipStockMinHeight = 28.dp

/** `coui_chip_selection_style_height`: the selection chip is 4dp taller. */
internal val ChipStockSelectionHeight = 32.dp

/** `coui_chip_default_min_width`. */
internal val ChipStockMinWidth = 52.dp

/** `coui_chip_default_max_width`. */
internal val ChipStockMaxWidth = 300.dp

/** `coui_chip_default_max_text_width`: the label truncates past this width. */
internal val ChipStockMaxTextWidth = 200.dp

/** `coui_chip_selection_style_chip_horizontal_padding`: used for every style (see the research note). */
internal val ChipStockHorizontalPadding = 12.dp

/** `coui_chip_group_default_spacing_horizontal` / `_vertical`: both 8dp. */
internal val ChipStockGroupSpacing = 8.dp

/** `?attr/couiTextBodyXS` for the base/suggestion style: 12sp. */
internal val ChipStockSuggestionTextSize = 12.sp

/** `?attr/couiTextBodyM` for the selection style: 14sp. */
internal val ChipStockSelectionTextSize = 14.sp

/** `?attr/couiColorContainer8`: the unchecked container, `#14000000` / `#1AFFFFFF`. */
internal fun chipUncheckedContainerColor(dark: Boolean): Color = if (dark) Color(0x1AFFFFFF) else Color(0x14000000)

/** `?attr/couiColorLabelPrimary`: the unchecked label, `#E6000000` / `#E6FFFFFF`. */
internal fun chipUncheckedContentColor(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/** `chip_unchecked_text_disable_color`: `#42000000` / `#29FFFFFF`. */
internal fun chipUncheckedDisabledContentColor(dark: Boolean): Color = if (dark) Color(0x29FFFFFF) else Color(0x42000000)

/** `chip_checked_text_disable_color`: `#8AFFFFFF` / `#29FFFFFF`. */
internal fun chipCheckedDisabledContentColor(dark: Boolean): Color = if (dark) Color(0x29FFFFFF) else Color(0x8AFFFFFF)

/** `?attr/couiColorLabelOnColor`: `#FFFFFF`. */
internal fun chipCheckedContentColor(): Color = Color(0xFFFFFFFF)

/** `?attr/couiColorDisable` is the primary at 30% alpha (`#4D0080FF` / `#4D1A8CFF`). */
internal const val CHIP_STOCK_DISABLED_ALPHA = 0.3f

/** `coui_chip_suggest_style_close_icon_size`: the suggestion style's `ic_chip_close` glyph. */
internal val ChipStockCloseIconSize = 12.dp

/** `coui_chip_suggest_style_close_icon_start_padding`: 8dp. */
internal val ChipStockCloseIconStartPadding = 8.dp

/** `coui_chip_suggest_style_close_icon_end_padding`: 8dp. */
internal val ChipStockCloseIconEndPadding = 8.dp

/** `coui_chip_suggest_style_close_icon_touch_bounds_size`: the hit area around the 12dp glyph. */
internal val ChipStockCloseIconTouchBounds = 24.dp

/** `?attr/couiColorSecondNeutral`: the `ic_chip_close` fill, `#8A000000` / `#8AFFFFFF`. */
internal fun chipCloseIconColor(dark: Boolean): Color = if (dark) Color(0x8AFFFFFF) else Color(0x8A000000)

/**
 * `ic_chip_close.xml` (14-unit viewport), verbatim: the stock suggestion chip's close glyph. The style
 * scales it to `coui_chip_suggest_style_close_icon_size` (12dp).
 */
internal const val CHIP_CLOSE_PATH_DATA =
    "M12.286,2.54C12.4,2.426 12.4,2.242 12.286,2.128L11.874,1.715C11.76,1.601 11.575,1.601 11.461,1.715L7.001,6.176" +
        "L2.54,1.715C2.426,1.601 2.242,1.601 2.128,1.715L1.715,2.128C1.601,2.242 1.601,2.426 1.715,2.54L6.176,7.001" +
        "L1.715,11.461C1.601,11.575 1.601,11.76 1.715,11.874L2.128,12.286C2.242,12.4 2.426,12.4 2.54,12.286L7.001,7.826" +
        "L11.461,12.286C11.575,12.4 11.76,12.4 11.874,12.286L12.286,11.874C12.4,11.76 12.4,11.575 12.286,11.461" +
        "L7.826,7.001L12.286,2.54Z"

/** Viewport of `ic_chip_close.xml`. */
internal const val CHIP_CLOSE_VIEWPORT = 14f

/** `coui_chip_suggest_style_chip_icon_size`: the suggestion icon slot is 14dp. */
internal val ChipStockSuggestionIconSize = 14.dp

/** `coui_chip_selection_style_chip_icon_size`: the selection icon slot is 16dp. */
internal val ChipStockSelectionIconSize = 16.dp

/** `coui_chip_suggest_style_chip_icon_end_padding` / `_selection_...`: 4dp between icon and label. */
internal val ChipStockIconEndPadding = 4.dp

/** `coui_chip_suggest_style_chip_icon_start_padding`: 12dp on top of the 12dp chip start padding. */
internal val ChipStockSuggestionIconStartPadding = 12.dp

/** `coui_chip_selection_style_chip_icon_start_padding`: 0dp, so the icon starts at the chip padding. */
internal val ChipStockSelectionIconStartPadding = 0.dp

/** `coui_chip_group_collapsable_button_size`: the collapse / expand chip is 28dp. */
internal val ChipGroupStockButtonSize = 28.dp

/** `Widget.COUI.Chip.Suggestion.Collapsable`: `chipIconSize` of the chevron. */
internal val ChipGroupStockGlyphSize = 16.dp

/** `DEFAULT_MINIMUM_COLLAPSED_MAX_ROWS` / the `collapsedMaxRows` default: 3. */
internal const val CHIP_GROUP_STOCK_COLLAPSED_MAX_ROWS = 3

/** `ic_chip_collapse.xml` / `ic_chip_expand.xml` viewport: 16 units. */
internal const val CHIP_GROUP_CHEVRON_VIEWPORT = 16f

/** Both chevrons use `android:strokeWidth="0.9333"` on the 16-unit viewport. */
internal const val CHIP_GROUP_CHEVRON_STROKE = 0.9333f

@Composable
internal fun isChipInDarkTheme(): Boolean = RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
