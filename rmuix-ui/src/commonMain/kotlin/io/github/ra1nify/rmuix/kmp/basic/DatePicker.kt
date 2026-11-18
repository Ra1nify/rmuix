// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * The per-column labels of a [DatePicker].
 *
 * `COUIDatePicker` formats a column as `value + getString(suffix)` whenever the locale is Chinese and
 * as a bare (day zero-padded) number otherwise; the suffixes are `R.string.coui_year` / `coui_month` /
 * `coui_day`, which only exist in `values-zh-rCN`. [DatePickerDefaults.labels] therefore defaults to
 * the Chinese suffixes - the branch the dump fully specifies - and the lambdas can be replaced for
 * any other locale.
 */
@Stable
data class DatePickerLabels(
    val year: (Int) -> String,
    val month: (Int) -> String,
    val day: (Int) -> String,
)

/**
 * A calendar date for [DatePicker]'s `minDate` / `maxDate` bounds (`COUIDatePicker.setMinDate()` /
 * `setMaxDate()`).
 *
 * The stock class compares `Calendar` instants; this value type keeps the same year / 1-based month /
 * 1-based day and compares in that order.
 */
@Immutable
data class DatePickerDate(
    val year: Int,
    val month: Int,
    val day: Int,
) : Comparable<DatePickerDate> {
    override fun compareTo(other: DatePickerDate): Int {
        val yearResult = year.compareTo(other.year)
        if (yearResult != 0) return yearResult
        val monthResult = month.compareTo(other.month)
        if (monthResult != 0) return monthResult
        return day.compareTo(other.day)
    }
}

/**
 * Defaults for [DatePicker], from `coui_date_picker.xml` and `COUIDatePicker`.
 */
object DatePickerDefaults {
    /** `coui_datepicker_width_year` -> `coui_numberpicker_width_big`. */
    val YearWidth = 76.dp

    /** `coui_datepicker_width_month` -> `coui_number_picker_width_big`. */
    val MonthWidth = 96.dp

    /** `coui_number_picker_width_small`, shared with the time picker's hour/minute columns. */
    val DayWidth = 62.dp

    /**
     * `coui_number_picker_view_padding_vertical`, the padding of the row in `coui_date_picker.xml`.
     *
     * The three columns themselves are `coui_datepicker_height` (135dp) tall, which is exactly the
     * three 45dp rows, so the columns are laid out without [NumberPickerDefaults.VerticalPadding] and
     * the row carries the 12dp instead.
     */
    val VerticalPadding = 12.dp

    /** `R.string.coui_year` (`values-zh-rCN`). */
    val YearSuffix = "\u5e74"

    /** `R.string.coui_month` (`values-zh-rCN`). */
    val MonthSuffix = "\u6708"

    /** `R.string.coui_day` (`values-zh-rCN`). */
    val DaySuffix = "\u65e5"

    /** The range `COUIDatePicker` falls back to when neither `minDate` nor `maxDate` is set. */
    val YearRange = 1900..2100

    /**
     * Builds the stock column labels.
     *
     * @param yearSuffix The suffix appended to the year column.
     * @param monthSuffix The suffix appended to the month column.
     * @param daySuffix The suffix appended to the day column.
     */
    fun labels(
        yearSuffix: String = YearSuffix,
        monthSuffix: String = MonthSuffix,
        daySuffix: String = DaySuffix,
    ): DatePickerLabels {
        val yearLabel: (Int) -> String = { "$it$yearSuffix" }
        val monthLabel: (Int) -> String = { "$it$monthSuffix" }
        val dayLabel: (Int) -> String = { "$it$daySuffix" }
        return DatePickerLabels(year = yearLabel, month = monthLabel, day = dayLabel)
    }
}

/**
 * The ColorOS wheel date picker (`COUIDatePicker`, `coui_date_picker.xml`).
 *
 * Three [NumberPicker] columns - year, month, day - in a row, with the day column clamped to the
 * length of the selected month. `COUIDatePicker` derives the column order from the locale's
 * `yyyyMMMMdd` pattern and reverses it for RTL; the Chinese pattern is year, month, day, which is the
 * order used here (reversed when the layout direction is RTL).
 *
 * @param year The selected year.
 * @param month The selected month, `1`-based (`1..12`). Stock's `getMonth()` returns the 0-based
 *   `Calendar.MONTH`; the component takes and reports `1..12`.
 * @param day The selected day of month, `1`-based.
 * @param onDateChange Called with the new year, month (`1..12`) and day whenever a column settles on a
 *   different value.
 * @param modifier The modifier to be applied to the row.
 * @param yearRange The selectable years.
 * @param labels The column labels.
 * @param colors The colours of the columns.
 * @param enabled Whether the columns respond to input.
 * @param minDate Optional lower bound (`setMinDate`): the year column is clipped to it and, when the
 *   selected year / month matches the bound, the month / day columns start at it.
 * @param maxDate Optional upper bound (`setMaxDate`), applied like [minDate].
 */
@Composable
fun DatePicker(
    year: Int,
    month: Int,
    day: Int,
    onDateChange: (year: Int, month: Int, day: Int) -> Unit,
    modifier: Modifier = Modifier,
    yearRange: IntRange = DatePickerDefaults.YearRange,
    labels: DatePickerLabels = DatePickerDefaults.labels(),
    colors: NumberPickerColors = NumberPickerDefaults.colors(),
    enabled: Boolean = true,
    minDate: DatePickerDate? = null,
    maxDate: DatePickerDate? = null,
) {
    // `COUIDatePicker.updateSpinners()`: `setMinDate` / `setMaxDate` shrink the year column and, when
    // the current year / month sits on a bound, the month and day columns as well.
    val selectableYears = datePickerYearRange(yearRange, minDate, maxDate)
    val clampedYear = year.coerceIn(selectableYears)
    val selectableMonths = datePickerMonthRange(clampedYear, minDate, maxDate)
    val clampedMonth = month.coerceIn(selectableMonths)
    val selectableDays = datePickerDayRange(clampedYear, clampedMonth, minDate, maxDate)
    val clampedDay = day.coerceIn(selectableDays)
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    fun emit(
        newYear: Int,
        newMonth: Int,
        newDay: Int,
    ) {
        // `updateSpinners()`: re-clamp the month and day columns against the new bounds, so a 31st cannot
        // survive a switch to a 30-day month and a same-year bound cannot be crossed.
        val clamped = datePickerClampDate(newYear, newMonth, newDay, yearRange, minDate, maxDate)
        onDateChange(clamped.year, clamped.month, clamped.day)
    }

    val yearColumn: @Composable () -> Unit = {
        NumberPicker(
            modifier = Modifier.width(DatePickerDefaults.YearWidth),
            value = clampedYear,
            onValueChange = { emit(it, clampedMonth, clampedDay) },
            enabled = enabled,
            range = selectableYears,
            label = labels.year,
            wrapAround = true,
            colors = colors,
            verticalPadding = 0.dp,
        )
    }
    val monthColumn: @Composable () -> Unit = {
        NumberPicker(
            modifier = Modifier.width(DatePickerDefaults.MonthWidth),
            value = clampedMonth,
            onValueChange = { emit(clampedYear, it, clampedDay) },
            enabled = enabled,
            range = selectableMonths,
            label = labels.month,
            wrapAround = true,
            colors = colors,
            verticalPadding = 0.dp,
        )
    }
    val dayColumn: @Composable () -> Unit = {
        NumberPicker(
            modifier = Modifier.width(DatePickerDefaults.DayWidth),
            value = clampedDay,
            onValueChange = { emit(clampedYear, clampedMonth, it) },
            enabled = enabled,
            range = selectableDays,
            label = labels.day,
            wrapAround = true,
            colors = colors,
            verticalPadding = 0.dp,
        )
    }

    NumberPickerGroup(
        modifier = modifier.padding(vertical = DatePickerDefaults.VerticalPadding),
        // `COUIDatePicker`: the columns draw no lines of their own; the row draws one pair inset by
        // `coui_selected_background_horizontal_padding` (16dp).
        dividerInset = NumberPickerDefaults.DividerInset,
    ) {
        if (rtl) {
            dayColumn()
            monthColumn()
            yearColumn()
        } else {
            yearColumn()
            monthColumn()
            dayColumn()
        }
    }
}

/**
 * `updateSpinners()`: clamp a requested date into the year / month / day bounds of
 * `setMinDate()` / `setMaxDate()`.
 */
internal fun datePickerClampDate(
    year: Int,
    month: Int,
    day: Int,
    yearRange: IntRange,
    minDate: DatePickerDate?,
    maxDate: DatePickerDate?,
): DatePickerDate {
    val targetYear = year.coerceIn(datePickerYearRange(yearRange, minDate, maxDate))
    val targetMonth = month.coerceIn(datePickerMonthRange(targetYear, minDate, maxDate))
    val targetDay = day.coerceIn(datePickerDayRange(targetYear, targetMonth, minDate, maxDate))
    return DatePickerDate(targetYear, targetMonth, targetDay)
}

/**
 * `COUIDatePicker.setMinDate()` / `setMaxDate()`: intersect the caller's [yearRange] with the bound years.
 *
 * The stock fallback is `1900..2100` when neither bound is set; an invalid (reversed) pair is squeezed to
 * the first year so the columns always have a value.
 */
internal fun datePickerYearRange(
    yearRange: IntRange,
    minDate: DatePickerDate?,
    maxDate: DatePickerDate?,
): IntRange {
    val first = maxOf(yearRange.first, minDate?.year ?: yearRange.first)
    val last = minOf(yearRange.last, maxDate?.year ?: yearRange.last)
    return if (first <= last) first..last else first..first
}

/**
 * `updateSpinners()` month branch: a same-year lower/upper bound clips the `1..12` month column; both bounds
 * in the same year clip it from both sides.
 */
internal fun datePickerMonthRange(
    year: Int,
    minDate: DatePickerDate?,
    maxDate: DatePickerDate?,
): IntRange {
    val lower = if (minDate != null && year == minDate.year) minDate.month else 1
    val upper = if (maxDate != null && year == maxDate.year) maxDate.month else 12
    val first = lower.coerceIn(1, 12)
    val last = upper.coerceIn(1, 12)
    return if (first <= last) first..last else last..first
}

/**
 * `updateSpinners()` day branch: a day bound applies only when the year **and** month match the bound; the
 * max stays `Calendar.getActualMaximum(DAY_OF_MONTH)` otherwise.
 */
internal fun datePickerDayRange(
    year: Int,
    month: Int,
    minDate: DatePickerDate?,
    maxDate: DatePickerDate?,
): IntRange {
    val days = daysInMonth(year, month)
    if (days <= 0) return 1..31
    val lower =
        if (minDate != null && year == minDate.year && month == minDate.month) minDate.day else 1
    val upper =
        if (maxDate != null && year == maxDate.year && month == maxDate.month) maxDate.day else days
    val first = lower.coerceIn(1, days)
    val last = upper.coerceIn(1, days)
    return if (first <= last) first..last else last..first
}

/**
 * The number of days in [month] of [year] (`1`-based month).
 *
 * Mirrors `Calendar.getActualMaximum(Calendar.DAY_OF_MONTH)` for the Gregorian calendar.
 */
internal fun daysInMonth(
    year: Int,
    month: Int,
): Int =
    when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 0
    }

/** Gregorian leap-year rule. */
internal fun isLeapYear(year: Int): Boolean = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
