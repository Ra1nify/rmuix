// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

// Values from the Settings APK picker (`com.coui.appcompat.picker.COUINumberPicker`), the
// `couiPickerBackgroundColor` attribute and the `coui_number_picker_*` / `coui_selected_*` dimens.

/** `couiPickerBackgroundColor` → `couiColorDivider`: `#1F000000` / `#33FFFFFF`. */
internal fun numberPickerDividerColor(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

/** `coui_number_picker_background_divider_height`: 0.33dp, clamped to one pixel by the widget. */
internal val NumberPickerStockDividerHeight = 0.33.dp

/** `coui_selected_background_radius`: the hairlines sit 24dp above/below the picker centre. */
internal val NumberPickerStockDividerOffset = 24.dp

internal val NumberPickerStockDividerInset = 16.dp

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isNumberPickerInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/** `couiPickerRowNumber`: the widget shows three rows. */
internal val NumberPickerStockRowCount = 3

/**
 * One row is **45dp**: `coui_datepicker_height` / `coui_timepicker_height` / `coui_time_picker_height`
 * are all 135dp and `couiPickerRowNumber` is 3, so 135 / 3 = 45dp.
 *
 * This supersedes the earlier 61dp: that value came from `coui_numberpicker_height` (207dp), which is
 * only ever bound to the AOSP `internalMaxHeight` attribute and is **not** used as a layout height by
 * any of the nine picker layouts. Every real layout sizes the picker to 135dp.
 */
internal val NumberPickerStockItemHeight = 45.dp

/**
 * `coui_number_picker_view_padding_vertical`: 12dp, applied by the **container**.
 *
 * `coui_time_picker.xml` (and the four sibling picker layouts) put
 * `android:paddingVertical="@dimen/coui_number_picker_view_padding_vertical"` on the parent
 * `LinearLayout`, i.e. **outside** the 135dp picker - which is exactly where this port applies it.
 */
internal val NumberPickerStockVerticalPadding = 12.dp

/**
 * `coui_numberpicker_textSize_big`, bound to the `focusTextSize` attribute: the centered row is
 * 20sp (`mFocusTextSize` in `COUINumberPicker`). The dimen is declared in dp, so this sp value is a
 * deliberate unit conversion, not a typo.
 */
internal val NumberPickerStockFocusTextSize = 20.sp

/**
 * `coui_numberpicker_textSize_small`, bound to the `startTextSize` attribute: every non-centered row
 * is 16sp (`mNormalTextSize`; the dimen is dp, converted to sp here). Note the style also declares
 * `numberTextSize` (20dp / `textSize_big`) and `endTextSize` (14dp / `textSize_middle`), but `COUINumberPicker` never reads either attribute - `mFocusTextSize` comes from `focusTextSize` and `mNormalTextSize` from `startTextSize`.
 */
internal val NumberPickerStockNormalTextSize = 16.sp

/** `couiFocusTextColor` → `couiColorPrimaryNeutral`: `#E6000000` / `#E6FFFFFF`. */
internal fun numberPickerSelectedTextColor(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/** `couiNormalTextColor` → `couiColorHintNeutral`: `#42000000` / `#4DFFFFFF`. */
internal fun numberPickerUnselectedTextColor(dark: Boolean): Color = if (dark) Color(0x4DFFFFFF) else Color(0x42000000)

/** `coui_numberpicker_unit_textSize`: the unit label next to the selected value renders at 14dp. */
internal val NumberPickerStockUnitTextSize = 14.sp

/** `coui_number_picker_text_margin_start`: 4dp between the selected value and the unit. */
internal val NumberPickerStockUnitMarginStart = 4.dp

/**
 * `coui_number_picker_text_width`: 18dp, read as `mSelectedValueWidth`.
 *
 * **Not applied to the layout**: the stock class is a canvas wheel, where the unit label is placed at
 * `mTextMargin + mSelectedValueWidth / 2` and the *digits* are allowed to overflow that 18dp column
 * symmetrically. This port draws each row as a `Row` of `Text`s, so forcing an 18dp-wide column would
 * make a two-digit value ellipsise. Kept here as the documented right-hand side of the stock
 * `mTextMargin` formula; see the unit-label `widthIn` below for the part that *is* reproducible.
 */
internal val NumberPickerStockSelectedValueWidth = 18.dp

/** `coui_number_picker_unit_min_width`: the unit label never shrinks below 21dp. */
internal val NumberPickerStockUnitMinWidth = 21.dp

/** `coui_numberpicker_unit_margin_bottom`: the unit baseline sits 2dp above the value baseline. */
internal val NumberPickerStockUnitMarginBottom = 2.dp
