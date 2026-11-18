// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import io.github.ra1nify.rmuix.kmp.utils.hairlinePx

/**
 * Lays out several [NumberPicker]s as one multi-column picker, like the stock date and time pickers.
 *
 * `coui_date_picker.xml` places three `COUINumberPicker`s in a centred horizontal `LinearLayout` whose
 * `android:layoutDirection` is **ltr** (column order must not mirror in RTL locales) and gives every
 * column `coui:couiIsDrawBackground="false"`. The pickers therefore draw no hairlines of their own:
 * the row draws one pair across its whole width, so the lines run through the gaps between columns
 * instead of stopping at each column's 16dp inset.
 *
 * Column widths are the caller's: `coui_datepicker_width_year` / `_month` = 96dp,
 * `coui_number_picker_width_small` (day) = 62dp, and the time picker's columns are 62dp each.
 *
 * @param modifier The modifier to be applied to the row.
 * @param dividerInset Inline inset of the shared hairline pair; stock uses 16dp except the 12-hour
 *   time picker, whose `mBackgroundLeft` is 0.
 * @param content The pickers, laid out left to right.
 */
@Composable
fun NumberPickerGroup(
    modifier: Modifier = Modifier,
    dividerInset: Dp = NumberPickerDefaults.DividerInset,
    content: @Composable RowScope.() -> Unit,
) {
    val dividerColor = numberPickerDividerColor(isNumberPickerInDarkTheme())
    val dividerHeight = NumberPickerDefaults.DividerHeight
    val halfItem = NumberPickerDefaults.DividerOffset
    CompositionLocalProvider(
        // The row layout itself must be LTR, so the provider wraps `Row`, not its content.
        LocalLayoutDirection provides LayoutDirection.Ltr,
        LocalNumberPickerShowSelectionLines provides false,
    ) {
        Row(
            modifier =
                modifier.drawBehind {
                    val stroke = hairlinePx(dividerHeight)
                    val centerY = size.height / 2f
                    val half = halfItem.toPx()
                    val inset = dividerInset.toPx()
                    drawLine(
                        color = dividerColor,
                        start = Offset(inset, centerY - half),
                        end = Offset(size.width - inset, centerY - half),
                        strokeWidth = stroke,
                    )
                    drawLine(
                        color = dividerColor,
                        start = Offset(inset, centerY + half),
                        end = Offset(size.width - inset, centerY + half),
                        strokeWidth = stroke,
                    )
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}
