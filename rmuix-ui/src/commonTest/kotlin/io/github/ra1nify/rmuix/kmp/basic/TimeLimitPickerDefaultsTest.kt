// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeLimitPickerDefaultsTest {
    @Test
    fun columnsMatchCouiTimeLimitPicker() {
        // coui_time_limit_picker.xml: both wheels use coui_number_picker_width_middle (76dp) as their
        // couiPickerVisualWidth; the AM/PM wheel uses coui_number_picker_width_small (62dp).
        assertEquals(76.dp, TimeLimitPickerDefaults.HourWidth)
        assertEquals(76.dp, TimeLimitPickerDefaults.MinuteWidth)
        assertEquals(62.dp, TimeLimitPickerDefaults.AmPmWidth)
        // coui_number_picker_view_padding_vertical, carried by the row.
        assertEquals(12.dp, TimeLimitPickerDefaults.VerticalPadding)
        // The three 45dp rows are the 135dp `coui_timepicker_height`.
        assertEquals(
            135.dp,
            NumberPickerDefaults.ItemHeight * 3,
            "the wheels are exactly coui_timepicker_height tall",
        )
    }

    @Test
    fun unitLabelsMatchTheLayout() {
        // @id/coui_timepicker_hour_text / @id/coui_timepicker_minute_text
        assertEquals(16.sp, TimeLimitPickerDefaults.UnitTextSize)
        assertEquals(50.dp, TimeLimitPickerDefaults.HourUnitWidth)
        assertEquals((-10).dp, TimeLimitPickerDefaults.HourUnitOffsetStart)
        assertEquals(56.dp, TimeLimitPickerDefaults.MinuteUnitPaddingStart)
        // R.string.coui_hour / coui_minute
        assertEquals("\u5c0f\u65f6", TimeLimitPickerDefaults.HourUnit)
        assertEquals("\u5206\u949f", TimeLimitPickerDefaults.MinuteUnit)
        // R.string.morning / afternoon
        assertEquals("\u4e0a\u5348", TimeLimitPickerDefaults.AmLabel)
        assertEquals("\u4e0b\u5348", TimeLimitPickerDefaults.PmLabel)
    }

    @Test
    fun bothWheelsAreZeroPadded() {
        // COUITimeLimitPicker calls setTwoDigitFormatter() on the hour wheel in 24-hour mode AND on the
        // minute wheel unconditionally - the same padding rule COUITimePicker applies.
        val labels24 = TimeLimitPickerDefaults.labels(is24Hour = true)
        assertEquals("00", labels24.hour(0))
        assertEquals("23", labels24.hour(23))
        assertEquals("05", labels24.minute(5))
        assertEquals("00", labels24.minute(0))
        assertEquals("\u5c0f\u65f6", labels24.hourUnit)
        assertEquals("\u5206\u949f", labels24.minuteUnit)

        val labels12 = TimeLimitPickerDefaults.labels(is24Hour = false)
        assertEquals("9", labels12.hour(9))
        assertEquals("12", labels12.hour(12))
        assertEquals("05", labels12.minute(5), "minutes stay padded in 12-hour mode too")
    }
}
