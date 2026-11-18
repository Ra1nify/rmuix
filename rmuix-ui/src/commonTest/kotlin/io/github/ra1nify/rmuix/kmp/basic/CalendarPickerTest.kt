// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * `COUICalendarPicker` metrics and the Gregorian math of `COUIDateMonthView` / `COUIPickerMathUtils`.
 */
class CalendarPickerTest {
    @Test
    fun selectedCircleAnimationUsesTheStockRadiusMapping() {
        // COUIDateMonthView.configAnimator(): radius fraction = 0.8 + 0.2 * animatedFraction
        assertEquals(280, CalendarPickerDefaults.SelectionAnimationMillis)
        assertEquals(150, CalendarPickerDefaults.DeselectionAnimationMillis)
        assertEquals(0.8f, calendarPickerSelectedCircleScale(0f), 0.0001f)
        assertEquals(0.9f, calendarPickerSelectedCircleScale(0.5f), 0.0001f)
        assertEquals(1f, calendarPickerSelectedCircleScale(1f), 0.0001f)
        assertEquals(0.8f, calendarPickerSelectedCircleScale(-1f), 0.0001f)
        assertEquals(1f, calendarPickerSelectedCircleScale(2f), 0.0001f)
    }

    @Test
    fun defaultsMatchTheStockResources() {
        // calendar_picker_max_width / _min_width / header row
        assertEquals(724.dp, CalendarPickerDefaults.MaxWidth)
        assertEquals(300.dp, CalendarPickerDefaults.MinWidth)
        assertEquals(16.dp, CalendarPickerDefaults.VerticalMargin)
        assertEquals(32.dp, CalendarPickerDefaults.HeaderHeight)
        assertEquals(24.dp, CalendarPickerDefaults.HeaderPaddingHorizontal)
        // coui_ic_chevron_start/end: 24dp icons with 4dp padding and an 8dp gap
        assertEquals(24.dp, CalendarPickerDefaults.HeaderIconSize)
        assertEquals(4.dp, CalendarPickerDefaults.HeaderIconPadding)
        assertEquals(8.dp, CalendarPickerDefaults.HeaderIconGap)
        // calendar_picker_title_icon_size / calendar_picker_month_text_size
        assertEquals(16.dp, CalendarPickerDefaults.TitleIconSize)
        assertEquals(16.sp, CalendarPickerDefaults.TitleTextSize)
        // calendar_picker_padding_horizontal
        assertEquals(16.dp, CalendarPickerDefaults.MonthPaddingHorizontal)
        // calendar_picker_day_of_week_height / _text_size
        assertEquals(40.dp, CalendarPickerDefaults.WeekdayHeight)
        assertEquals(14.sp, CalendarPickerDefaults.WeekdayTextSize)
        // calendar_picker_day_height / TextAppearance.Material.Widget.Calendar.Day (headline6)
        assertEquals(40.dp, CalendarPickerDefaults.DayHeight)
        assertEquals(16.sp, CalendarPickerDefaults.DayTextSize)
        // calendar_picker_day_min_col_padding
        assertEquals(8.dp, CalendarPickerDefaults.RowExtraSpacing)
        // calendar_picker_current_day_radius (32dp diameter) / _stroke_radius
        assertEquals(32.dp, CalendarPickerDefaults.SelectedCircleSize)
        assertEquals(1.8.dp, CalendarPickerDefaults.TodayStrokeWidth)
        // COUICalendarPicker android:firstDayOfWeek default
        assertEquals(1, CalendarPickerDefaults.FirstDayOfWeek)
        // Material default range
        assertEquals(CalendarPickerDate(1900, 1, 1), CalendarPickerDefaults.MinDate)
        assertEquals(CalendarPickerDate(2100, 12, 31), CalendarPickerDefaults.MaxDate)
    }

    @Test
    fun coloursMatchTheStockTokens() {
        // ?attr/couiColorPrimaryNeutral
        assertEquals(Color(0xE6000000), calendarPickerPrimaryNeutral(dark = false))
        assertEquals(Color(0xE6FFFFFF), calendarPickerPrimaryNeutral(dark = true))
        // ?attr/couiColorHintNeutral
        assertEquals(Color(0x42000000), calendarPickerHintNeutral(dark = false))
        assertEquals(Color(0x4DFFFFFF), calendarPickerHintNeutral(dark = true))
        // ?attr/couiColorDisabledNeutral
        assertEquals(Color(0x42000000), calendarPickerDisabledNeutral(dark = false))
        assertEquals(Color(0x4DFFFFFF), calendarPickerDisabledNeutral(dark = true))
        // ?attr/couiColorLabelSecondary (the collapsed expand arrow)
        assertEquals(Color(0x8A000000), calendarPickerLabelSecondary(dark = false))
        assertEquals(Color(0x8AFFFFFF), calendarPickerLabelSecondary(dark = true))
    }

    @Test
    fun gregorianMonthLengthsAndLeapYears() {
        assertTrue(isCalendarLeapYear(2000))
        assertTrue(!isCalendarLeapYear(1900))
        assertTrue(isCalendarLeapYear(2024))
        assertEquals(31, calendarDaysInMonth(2026, 1))
        assertEquals(28, calendarDaysInMonth(2026, 2))
        assertEquals(29, calendarDaysInMonth(2024, 2))
        assertEquals(30, calendarDaysInMonth(2026, 4))
        assertEquals(31, calendarDaysInMonth(2026, 12))
    }

    @Test
    fun dayOfWeekAndFirstDayOffsetFollowTheGregorianCalendar() {
        // ISO: 1 = Monday ... 7 = Sunday.
        assertEquals(4, calendarDayOfWeek(1970, 1, 1))
        assertEquals(2, calendarDayOfWeek(2026, 11, 17))
        assertEquals(6, calendarDayOfWeek(2000, 1, 1))
        assertEquals(4, calendarDayOfWeek(2024, 2, 29))
        // 2026-11-01 is a Sunday: Monday-first offset is 6.
        assertEquals(6, calendarFirstDayOffset(2026, 11, firstDayOfWeek = 1))
        // Sunday-first offset is 0.
        assertEquals(0, calendarFirstDayOffset(2026, 11, firstDayOfWeek = 7))
    }

    @Test
    fun monthIndexAndCivilDateRoundTrip() {
        assertEquals(2026 * 12 + 10, calendarMonthIndexOf(2026, 11))
        assertEquals(CalendarPickerDate(2026, 11, 1), calendarDateAtMonthIndex(calendarMonthIndexOf(2026, 11)))
        assertEquals(CalendarPickerDate(2025, 12, 1), calendarDateAtMonthIndex(calendarMonthIndexOf(2025, 12)))
        val days = calendarDaysFromCivil(2026, 11, 17)
        assertEquals(CalendarPickerDate(2026, 11, 17), calendarDateFromDays(days))
        // 1969-12-31 is epoch day -1.
        assertEquals(-1L, calendarDaysFromCivil(1969, 12, 31))
        assertEquals(CalendarPickerDate(1969, 12, 31), calendarDateFromDays(-1L))
    }

    @Test
    fun todayIsInsideTheDefaultRange() {
        val today = calendarToday()
        assertTrue(today >= CalendarPickerDefaults.MinDate)
        assertTrue(today <= CalendarPickerDefaults.MaxDate)
    }
}
