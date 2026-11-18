// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DatePickerDefaultsTest {
    @Test
    fun columnsMatchCouiDatePicker() {
        // coui_date_picker.xml: year 76dp (coui_numberpicker_width_big), month 96dp
        // (coui_number_picker_width_big), day 62dp (coui_number_picker_width_small).
        assertEquals(76.dp, DatePickerDefaults.YearWidth)
        assertEquals(96.dp, DatePickerDefaults.MonthWidth)
        assertEquals(62.dp, DatePickerDefaults.DayWidth)
        // coui_number_picker_view_padding_vertical, carried by the row.
        assertEquals(12.dp, DatePickerDefaults.VerticalPadding)
        // The three 45dp rows are the 135dp `coui_datepicker_height`.
        assertEquals(
            135.dp,
            NumberPickerDefaults.ItemHeight * 3,
            "the columns are exactly coui_datepicker_height tall",
        )
        assertEquals(1900..2100, DatePickerDefaults.YearRange)
    }

    @Test
    fun labelsUseTheChineseSuffixes() {
        // R.string.coui_year / coui_month / coui_day, which only exist in values-zh-rCN.
        val labels = DatePickerDefaults.labels()
        assertEquals("2026\u5e74", labels.year(2026))
        assertEquals("12\u6708", labels.month(12))
        assertEquals("31\u65e5", labels.day(31))
        assertEquals("\u5e74", DatePickerDefaults.YearSuffix)
        assertEquals("\u6708", DatePickerDefaults.MonthSuffix)
        assertEquals("\u65e5", DatePickerDefaults.DaySuffix)
    }

    @Test
    fun dayColumnFollowsTheGregorianMonthLength() {
        assertEquals(31, daysInMonth(2026, 1))
        assertEquals(28, daysInMonth(2026, 2))
        assertEquals(31, daysInMonth(2026, 3))
        assertEquals(30, daysInMonth(2026, 4))
        assertEquals(31, daysInMonth(2026, 12))
        assertEquals(29, daysInMonth(2024, 2), "leap year")
        assertEquals(28, daysInMonth(1900, 2), "century that is not a leap year")
        assertEquals(29, daysInMonth(2000, 2), "century that is a leap year")
    }

    @Test
    fun dateBoundsClipTheYearMonthAndDayColumns() {
        val min = DatePickerDate(2020, 3, 15)
        val max = DatePickerDate(2024, 6, 10)
        // setMinDate/setMaxDate clip the year column to the bound years.
        assertEquals(2020..2024, datePickerYearRange(1900..2100, min, max))
        assertEquals(1900..2100, datePickerYearRange(1900..2100, null, null))
        assertEquals(1900..2024, datePickerYearRange(1900..2100, null, max))
        assertEquals(2020..2100, datePickerYearRange(1900..2100, min, null))
        // Same-year bounds clip the month column.
        assertEquals(3..12, datePickerMonthRange(2020, min, max))
        assertEquals(1..12, datePickerMonthRange(2022, min, max))
        assertEquals(1..6, datePickerMonthRange(2024, min, max))
        assertEquals(4..9, datePickerMonthRange(2022, DatePickerDate(2022, 4, 1), DatePickerDate(2022, 9, 1)))
        // Same-year+month bounds clip the day column; otherwise the month length is the max.
        assertEquals(15..31, datePickerDayRange(2020, 3, min, max))
        assertEquals(1..30, datePickerDayRange(2020, 4, min, max))
        assertEquals(1..10, datePickerDayRange(2024, 6, min, max))
        assertEquals(1..31, datePickerDayRange(2024, 5, min, max))
        assertEquals(
            10..20,
            datePickerDayRange(2022, 5, DatePickerDate(2022, 5, 10), DatePickerDate(2022, 5, 20)),
        )
        // updateSpinners(): requested dates are clamped into the bounds.
        assertEquals(DatePickerDate(2020, 3, 15), datePickerClampDate(2019, 1, 1, 1900..2100, min, max))
        assertEquals(DatePickerDate(2024, 6, 10), datePickerClampDate(2030, 12, 31, 1900..2100, min, max))
        assertEquals(DatePickerDate(2022, 2, 28), datePickerClampDate(2022, 2, 31, 1900..2100, null, null))
        // DatePickerDate compares year, then month, then day.
        assertTrue(DatePickerDate(2020, 3, 15) < DatePickerDate(2020, 3, 16))
        assertTrue(DatePickerDate(2020, 4, 1) > DatePickerDate(2020, 3, 31))
        assertTrue(DatePickerDate(2021, 1, 1) > DatePickerDate(2020, 12, 31))
    }

    @Test
    fun leapYearRuleMatchesTheGregorianCalendar() {
        assertEquals(true, isLeapYear(2024))
        assertEquals(false, isLeapYear(2026))
        assertEquals(false, isLeapYear(1900))
        assertEquals(true, isLeapYear(2000))
    }
}
