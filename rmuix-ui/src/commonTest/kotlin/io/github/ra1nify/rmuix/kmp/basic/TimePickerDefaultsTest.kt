// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class TimePickerDefaultsTest {
    @Test
    fun columnsMatchCouiTimePicker() {
        // coui_time_picker.xml: hour, minute and AM/PM columns are all
        // coui_number_picker_width_small (62dp).
        assertEquals(62.dp, TimePickerDefaults.HourWidth)
        assertEquals(62.dp, TimePickerDefaults.MinuteWidth)
        assertEquals(62.dp, TimePickerDefaults.AmPmWidth)
        // coui_number_picker_view_padding_vertical, carried by the row.
        assertEquals(12.dp, TimePickerDefaults.VerticalPadding)
        // The three 45dp rows are the 135dp `coui_time_picker_height`.
        assertEquals(
            135.dp,
            NumberPickerDefaults.ItemHeight * 3,
            "the columns are exactly coui_time_picker_height tall",
        )
        assertEquals(1, TimePickerDefaults.MinuteStep)
    }

    @Test
    fun amPmLabelsComeFromMorningAndAfternoon() {
        // R.array.coui_time_picker_ampm -> @string/morning / @string/afternoon.
        assertEquals("\u4e0a\u5348", TimePickerDefaults.AmLabel)
        assertEquals("\u4e0b\u5348", TimePickerDefaults.PmLabel)
    }

    @Test
    fun onlyThe24HourWheelIsZeroPaddedButMinutesAlwaysAre() {
        // COUITimePicker calls setTwoDigitFormatter() on the hour wheel in 24-hour mode only, and on
        // the minute wheel unconditionally.
        assertEquals("08", TimePickerDefaults.labels(is24Hour = true).hour(8))
        assertEquals("00", TimePickerDefaults.labels(is24Hour = true).hour(0))
        assertEquals("23", TimePickerDefaults.labels(is24Hour = true).hour(23))
        assertEquals("8", TimePickerDefaults.labels(is24Hour = false).hour(8))
        assertEquals("12", TimePickerDefaults.labels(is24Hour = false).hour(12))
        assertEquals("05", TimePickerDefaults.labels(is24Hour = true).minute(5))
        assertEquals("00", TimePickerDefaults.labels(is24Hour = true).minute(0))
        assertEquals("05", TimePickerDefaults.labels(is24Hour = false).minute(5))
    }
}
