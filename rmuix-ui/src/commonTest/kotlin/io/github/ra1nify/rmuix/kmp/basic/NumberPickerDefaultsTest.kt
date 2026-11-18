// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberPickerDefaultsTest {
    @Test
    fun unitLabelUsesTheStockUnitTokens() {
        // coui_numberpicker_unit_textSize / coui_number_picker_text_margin_start /
        // coui_numberpicker_unit_margin_bottom.
        assertEquals(14.sp, NumberPickerDefaults.UnitTextSize)
        assertEquals(4.dp, NumberPickerDefaults.UnitTextMarginStart)
        assertEquals(2.dp, NumberPickerDefaults.UnitTextMarginBottom)
    }

    @Test
    fun selectionFrameUsesTheStockHairlines() {
        // coui_number_picker_background_divider_height / coui_selected_background_horizontal_padding
        assertEquals(0.33.dp, NumberPickerDefaults.DividerHeight)
        assertEquals(16.dp, NumberPickerDefaults.DividerInset)
        // coui_selected_background_radius: the lines are 48dp apart, not at the 45dp row edges.
        assertEquals(24.dp, NumberPickerDefaults.DividerOffset)
        // couiPickerBackgroundColor -> couiColorDivider
        assertEquals(Color(0x1F000000), numberPickerDividerColor(false))
        assertEquals(Color(0x33FFFFFF), numberPickerDividerColor(true))
    }

    @Test
    fun rowMetricsMatchTheStockPicker() {
        // couiPickerRowNumber = 3 rows; every picker layout is 135dp tall
        // (`coui_datepicker_height` / `coui_timepicker_height` / `coui_time_picker_height`),
        // and the parent LinearLayout adds the 12dp `coui_number_picker_view_padding_vertical`.
        assertEquals(3, NumberPickerStockRowCount)
        assertEquals(45.dp, NumberPickerDefaults.ItemHeight)
        assertEquals(12.dp, NumberPickerDefaults.VerticalPadding)
        assertEquals(135.dp, NumberPickerDefaults.ItemHeight * 3)
        // focusTextSize -> coui_numberpicker_textSize_big; startTextSize -> ..._small
        assertEquals(20.sp, NumberPickerDefaults.FocusTextSize)
        assertEquals(16.sp, NumberPickerDefaults.NormalTextSize)
    }

    @Test
    fun rowTextColorsMatchTheStockNeutrals() {
        // couiFocusTextColor -> couiColorPrimaryNeutral, couiNormalTextColor -> couiColorHintNeutral
        assertEquals(Color(0xE6000000), numberPickerSelectedTextColor(false))
        assertEquals(Color(0xE6FFFFFF), numberPickerSelectedTextColor(true))
        assertEquals(Color(0x42000000), numberPickerUnselectedTextColor(false))
        assertEquals(Color(0x4DFFFFFF), numberPickerUnselectedTextColor(true))
    }

    /**
     * `COUINumberPicker` derives the unit label's x offset from three dimens:
     * `coui_number_picker_text_width` (18dp), `coui_number_picker_unit_min_width` (21dp) and
     * `coui_number_picker_text_margin_start` (4dp). Only the first two are recorded here; the 18dp
     * column itself cannot be reproduced by a `Row` of `Text`s without ellipsising two-digit values.
     */
    @Test
    fun unitLabelWidthsMatchTheStockDimens() {
        assertEquals(18.dp, NumberPickerStockSelectedValueWidth)
        assertEquals(21.dp, NumberPickerStockUnitMinWidth)
        assertEquals(21.dp, NumberPickerDefaults.UnitMinWidth)
    }
}
