// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** A Gregorian calendar date (`month` is `1..12`). */
@Immutable
data class CalendarPickerDate(
    val year: Int,
    val month: Int,
    val day: Int,
) : Comparable<CalendarPickerDate> {
    init {
        require(month in 1..12) { "month must be in 1..12, was $month" }
        require(day in 1..calendarDaysInMonth(year, month)) { "invalid day $day for $year-$month" }
    }

    override fun compareTo(other: CalendarPickerDate): Int = compareValuesBy(this, other, { it.year }, { it.month }, { it.day })
}

/** The labels of a [CalendarPicker], replacing the stock locale-formatted weekday and month-year texts. */
@Immutable
data class CalendarPickerLabels(
    /** The weekday label for an ISO day of week (`1` = Monday ... `7` = Sunday). */
    val weekday: (isoDayOfWeek: Int) -> String,
    /** The header title (`DateFormat.getBestDateTimePattern(locale, "MMMMy")` in the stock code). */
    val monthYear: (year: Int, month: Int) -> String,
)

/** Colours of a [CalendarPicker]. */
@Immutable
data class CalendarPickerColors(
    val titleColor: Color,
    val iconColor: Color,
    val weekdayColor: Color,
    val dayTextColor: Color,
    val hintDayColor: Color,
    val disabledDayColor: Color,
    val selectedDayColor: Color,
    val selectedDayTextColor: Color,
    val todayColor: Color,
    val expandArrowColor: Color,
    val expandArrowExpandedColor: Color,
)

/**
 * `COUIDateMonthView.configAnimator()`: the selected circle grows from 80% to 100% of the day circle
 * radius (`mDaySelectRadius = radius*0.2*f + radius*0.8`) while its paint alpha goes 0 -> 255. The stock
 * select animator runs for `DURATION_OF_SELECT_ANIMATOR` = 280ms on `COUIMoveEaseInterpolator`
 * (`cubic-bezier(0.3, 0, 0.1, 1)`).
 */
internal fun calendarPickerSelectedCircleScale(progress: Float): Float = 0.8f + 0.2f * progress.coerceIn(0f, 1f)

/** `COUIDateMonthView` select/deselect interpolators. */
private val CalendarSelectionEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)
private val CalendarDeselectionEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** Defaults for [CalendarPicker], taken from `COUICalendarPicker` and its resources. */
object CalendarPickerDefaults {
    /** `calendar_picker_max_width` (`COUICalendarPicker.onMeasure()` clamps the width to it). */
    val MaxWidth = 724.dp

    /** `calendar_picker_min_width`. */
    val MinWidth = 300.dp

    /** `coui_calendar_picker_material.xml` `android:layout_marginVertical`. */
    val VerticalMargin = 16.dp

    /** The 32dp header row of `coui_calendar_picker_header_material.xml`. */
    val HeaderHeight = 32.dp

    /** Header `paddingStart` / `paddingEnd` (`coui_calendar_picker_header_material.xml`). */
    val HeaderPaddingHorizontal = 24.dp

    /** The 24dp `coui_ic_chevron_start/end` header icons (with 4dp padding). */
    val HeaderIconSize = 24.dp

    /** Header icon padding. */
    val HeaderIconPadding = 4.dp

    /** Header `android:layout_marginStart` of the next button. */
    val HeaderIconGap = 8.dp

    /** `calendar_picker_title_icon_size`: the expand arrow next to the title. */
    val TitleIconSize = 16.dp

    /** `calendar_picker_month_text_size`: the header title size. */
    val TitleTextSize = 16.sp

    /** `coui_calendar_picker_month_item_material.xml` horizontal padding (`calendar_picker_padding_horizontal`). */
    val MonthPaddingHorizontal = 16.dp

    /** `calendar_picker_day_of_week_height`. */
    val WeekdayHeight = 40.dp

    /** `calendar_picker_day_of_week_text_size`. */
    val WeekdayTextSize = 14.sp

    /** `calendar_picker_day_height`. */
    val DayHeight = 40.dp

    /** `TextAppearance.Material.Widget.Calendar.Day` (`couiTextAppearanceHeadline6`): 16sp medium. */
    val DayTextSize = 16.sp

    /** `calendar_picker_day_min_col_padding`: the extra row spacing used when the month needs 5 rows. */
    val RowExtraSpacing = 8.dp

    /** `calendar_picker_current_day_radius` / 2: the selected day circle. */
    val SelectedCircleSize = 32.dp

    /** `calendar_picker_current_day_stroke_radius`: the today ring stroke. */
    val TodayStrokeWidth = 1.8.dp

    /** `COUIDateMonthView.configAnimator()`: the selected circle in animation. */
    val SelectionAnimationMillis = 280

    /** `COUIDateMonthView.configAnimator()`: the deselection animation. */
    val DeselectionAnimationMillis = 150

    /** `COUICalendarPicker` default `android:firstDayOfWeek` (Monday). */
    val FirstDayOfWeek = 1

    /** Material's default minimum date of the calendar view. */
    val MinDate = CalendarPickerDate(1900, 1, 1)

    /** Material's default maximum date of the calendar view. */
    val MaxDate = CalendarPickerDate(2100, 12, 31)

    /** The default English weekday / month-year labels. */
    fun labels(): CalendarPickerLabels =
        CalendarPickerLabels(
            weekday = { isoDayOfWeek -> weekdayShortNames[isoDayOfWeek - 1] },
            monthYear = { year, month -> "${monthNames[month - 1]} $year" },
        )

    /** The stock colours, keyed on the current theme. */
    @Composable
    fun colors(
        titleColor: Color = calendarPickerPrimaryNeutral(isCalendarPickerDarkTheme()),
        iconColor: Color = calendarPickerPrimaryNeutral(isCalendarPickerDarkTheme()),
        weekdayColor: Color = calendarPickerHintNeutral(isCalendarPickerDarkTheme()),
        dayTextColor: Color = calendarPickerPrimaryNeutral(isCalendarPickerDarkTheme()),
        hintDayColor: Color = calendarPickerDisabledNeutral(isCalendarPickerDarkTheme()),
        disabledDayColor: Color = calendarPickerDisabledNeutral(isCalendarPickerDarkTheme()),
        selectedDayColor: Color = RmuixTheme.colorScheme.primary,
        selectedDayTextColor: Color = RmuixTheme.colorScheme.onPrimary,
        todayColor: Color = RmuixTheme.colorScheme.primary,
        expandArrowColor: Color = calendarPickerLabelSecondary(isCalendarPickerDarkTheme()),
        expandArrowExpandedColor: Color = RmuixTheme.colorScheme.primary,
    ): CalendarPickerColors =
        remember(
            titleColor,
            iconColor,
            weekdayColor,
            dayTextColor,
            hintDayColor,
            disabledDayColor,
            selectedDayColor,
            selectedDayTextColor,
            todayColor,
            expandArrowColor,
            expandArrowExpandedColor,
        ) {
            CalendarPickerColors(
                titleColor = titleColor,
                iconColor = iconColor,
                weekdayColor = weekdayColor,
                dayTextColor = dayTextColor,
                hintDayColor = hintDayColor,
                disabledDayColor = disabledDayColor,
                selectedDayColor = selectedDayColor,
                selectedDayTextColor = selectedDayTextColor,
                todayColor = todayColor,
                expandArrowColor = expandArrowColor,
                expandArrowExpandedColor = expandArrowExpandedColor,
            )
        }
}

private val weekdayShortNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

private val monthNames =
    listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December",
    )

@Composable
private fun isCalendarPickerDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/** `?attr/couiColorPrimaryNeutral`. */
internal fun calendarPickerPrimaryNeutral(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/** `?attr/couiColorHintNeutral`. */
internal fun calendarPickerHintNeutral(dark: Boolean): Color = if (dark) Color(0x4DFFFFFF) else Color(0x42000000)

/** `?attr/couiColorDisabledNeutral`. */
internal fun calendarPickerDisabledNeutral(dark: Boolean): Color = if (dark) Color(0x4DFFFFFF) else Color(0x42000000)

/** `?attr/couiColorLabelSecondary` (the collapsed expand arrow). */
internal fun calendarPickerLabelSecondary(dark: Boolean): Color = if (dark) Color(0x8AFFFFFF) else Color(0x8A000000)

// ---------------------------------------------------------------------------------------------
// Gregorian calendar math (`COUIPickerMathUtils` / `COUIDateMonthView.getDaysInMonth`).
// ---------------------------------------------------------------------------------------------

/** `Calendar.getActualMaximum(DAY_OF_MONTH)` for the Gregorian calendar. */
internal fun calendarDaysInMonth(
    year: Int,
    month: Int,
): Int =
    when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isCalendarLeapYear(year)) 29 else 28
        else -> error("invalid month $month")
    }

/** Gregorian leap year rule. */
internal fun isCalendarLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

/** Days since 1970-01-01 (Howard Hinnant's `days_from_civil`). */
internal fun calendarDaysFromCivil(
    year: Int,
    month: Int,
    day: Int,
): Long {
    val y = if (month <= 2) year - 1 else year
    val era = (if (y >= 0) y else y - 399) / 400
    val yoe = y - era * 400
    val mp = (month + 9) % 12
    val doy = (153 * mp + 2) / 5 + day - 1
    val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
    return era.toLong() * 146097L + doe - 719468L
}

/** ISO day of week (`1` = Monday ... `7` = Sunday). */
internal fun calendarDayOfWeek(
    year: Int,
    month: Int,
    day: Int,
): Int = calendarFloorMod(calendarDaysFromCivil(year, month, day) + 3L, 7L).toInt() + 1

/** The `0..6` column of the first day of [month], honouring [firstDayOfWeek] (`1` = Monday ... `7` = Sunday). */
internal fun calendarFirstDayOffset(
    year: Int,
    month: Int,
    firstDayOfWeek: Int,
): Int = calendarFloorMod(calendarDayOfWeek(year, month, 1) - firstDayOfWeek, 7)

/** `Math.floorDiv` equivalent (JS/Wasm have no `java.lang.Math`). */
internal fun calendarFloorDiv(
    value: Int,
    divisor: Int,
): Int {
    val quotient = value / divisor
    return if (value % divisor != 0 && (value xor divisor) < 0) quotient - 1 else quotient
}

/** `Math.floorDiv` equivalent for [Long]. */
internal fun calendarFloorDiv(
    value: Long,
    divisor: Long,
): Long {
    val quotient = value / divisor
    return if (value % divisor != 0L && (value xor divisor) < 0L) quotient - 1L else quotient
}

/** `Math.floorMod` equivalent. */
internal fun calendarFloorMod(
    value: Int,
    divisor: Int,
): Int {
    val remainder = value % divisor
    return if (remainder != 0 && (remainder xor divisor) < 0) remainder + divisor else remainder
}

/** `Math.floorMod` equivalent for [Long]. */
internal fun calendarFloorMod(
    value: Long,
    divisor: Long,
): Long {
    val remainder = value % divisor
    return if (remainder != 0L && (remainder xor divisor) < 0L) remainder + divisor else remainder
}

/** The month index used by the pager: `year * 12 + (month - 1)`. */
internal fun calendarMonthIndexOf(
    year: Int,
    month: Int,
): Int = year * 12 + (month - 1)

/** The first day of the month at [monthIndex], e.g. for the pager pages. */
internal fun calendarDateAtMonthIndex(monthIndex: Int): CalendarPickerDate {
    val year = calendarFloorDiv(monthIndex, 12)
    val month = calendarFloorMod(monthIndex, 12) + 1
    return CalendarPickerDate(year, month, 1)
}

/**
 * The ColorOS calendar picker: `COUICalendarPicker` (`com.coui.appcompat.calendar`), used by Gallery's capture
 * time adjust sheet (`business_lib_adjust_capture_time_sheet.xml`).
 *
 * The header is the stock 32dp row: 24dp chevrons on both sides and a centred 16sp month-year title whose
 * expand arrow swaps the month grid for the existing [DatePicker] wheel (`COUICalendarYearView` is a
 * `COUIDatePicker` in the stock code). The month grid follows `COUIDateMonthView`: 7 columns of
 * width/7, 40dp day rows (48dp when the month needs only 5 rows, per
 * `calendar_picker_day_min_col_padding`), 40dp weekday header, 32dp selected circle in the theme primary,
 * a 1.8dp today ring and the neighbouring months' days in the disabled colour.
 *
 * @param selectedDate The selected date (hoisted).
 * @param onDateSelected Called with the tapped date.
 * @param modifier Applied to the picker; the stock clamps the width to 724dp and the Settings/Gallery use
 *   `match_parent`.
 * @param minDate The earliest selectable date.
 * @param maxDate The latest selectable date.
 * @param firstDayOfWeek `1` = Monday ... `7` = Sunday (the stock `android:firstDayOfWeek`).
 * @param labels The weekday and month-year labels.
 * @param colors The picker colours.
 */
@Composable
fun CalendarPicker(
    selectedDate: CalendarPickerDate,
    onDateSelected: (CalendarPickerDate) -> Unit,
    modifier: Modifier = Modifier,
    minDate: CalendarPickerDate = CalendarPickerDefaults.MinDate,
    maxDate: CalendarPickerDate = CalendarPickerDefaults.MaxDate,
    firstDayOfWeek: Int = CalendarPickerDefaults.FirstDayOfWeek,
    labels: CalendarPickerLabels = CalendarPickerDefaults.labels(),
    colors: CalendarPickerColors = CalendarPickerDefaults.colors(),
) {
    val firstMonth = calendarMonthIndexOf(minDate.year, minDate.month)
    val lastMonth = calendarMonthIndexOf(maxDate.year, maxDate.month)
    val selectedMonth = calendarMonthIndexOf(selectedDate.year, selectedDate.month).coerceIn(firstMonth, lastMonth)
    var expanded by remember { mutableStateOf(false) }
    val pagerState =
        rememberPagerState(initialPage = selectedMonth - firstMonth) {
            lastMonth - firstMonth + 1
        }
    val scope = rememberCoroutineScope()

    // `COUIDateMonthView`: a new selection starts the 280ms circle-in animation (new circle grows
    // 80% -> 100% radius while fading in) and the 150ms circle-out animation (old circle alpha 255 -> 0).
    var previousSelectedDate by remember { mutableStateOf<CalendarPickerDate?>(null) }
    val selectProgress = remember { Animatable(1f) }
    val deselectProgress = remember { Animatable(1f) }
    var committedSelectedDate by remember { mutableStateOf(selectedDate) }
    LaunchedEffect(selectedDate) {
        if (selectedDate == committedSelectedDate) return@LaunchedEffect
        previousSelectedDate = committedSelectedDate
        committedSelectedDate = selectedDate
        launch {
            selectProgress.snapTo(0f)
            selectProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(CalendarPickerDefaults.SelectionAnimationMillis, easing = CalendarSelectionEasing),
            )
        }
        launch {
            deselectProgress.snapTo(1f)
            deselectProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(CalendarPickerDefaults.DeselectionAnimationMillis, easing = CalendarDeselectionEasing),
            )
        }
    }

    LaunchedEffect(selectedDate.year, selectedDate.month) {
        val target = calendarMonthIndexOf(selectedDate.year, selectedDate.month) - firstMonth
        if (target in 0 until pagerState.pageCount && target != pagerState.currentPage) {
            pagerState.scrollToPage(target)
        }
    }

    val shownDate = calendarDateAtMonthIndex(firstMonth + pagerState.currentPage)
    Column(
        modifier =
            modifier
                .widthIn(
                    min = CalendarPickerDefaults.MinWidth,
                    max = CalendarPickerDefaults.MaxWidth,
                ).padding(vertical = CalendarPickerDefaults.VerticalMargin),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(CalendarPickerDefaults.HeaderHeight)
                    .padding(horizontal = CalendarPickerDefaults.HeaderPaddingHorizontal),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeaderIcon(
                icon = calendarChevronStartIcon,
                color = colors.iconColor,
                enabled = pagerState.currentPage > 0,
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
            )
            Row(
                modifier =
                    Modifier
                        .weight(1f)
                        .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = labels.monthYear(shownDate.year, shownDate.month),
                    color = if (expanded) colors.expandArrowExpandedColor else colors.titleColor,
                    fontSize = CalendarPickerDefaults.TitleTextSize,
                    maxLines = 2,
                )
                Spacer(Modifier.size(CalendarPickerDefaults.HeaderIconGap))
                Icon(
                    imageVector = if (expanded) calendarExpandLessIcon else calendarExpandMoreIcon,
                    contentDescription = null,
                    tint = if (expanded) colors.expandArrowExpandedColor else colors.expandArrowColor,
                    modifier = Modifier.size(CalendarPickerDefaults.TitleIconSize),
                )
            }
            HeaderIcon(
                icon = calendarChevronEndIcon,
                color = colors.iconColor,
                enabled = pagerState.currentPage < pagerState.pageCount - 1,
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
            )
        }
        if (expanded) {
            DatePicker(
                year = selectedDate.year,
                month = selectedDate.month,
                day = selectedDate.day,
                onDateChange = { year, month, day ->
                    onDateSelected(CalendarPickerDate(year, month, day.coerceAtMost(calendarDaysInMonth(year, month))))
                    expanded = false
                },
                yearRange = minDate.year..maxDate.year,
                modifier = Modifier.padding(horizontal = CalendarPickerDefaults.MonthPaddingHorizontal),
            )
        } else {
            HorizontalPager(state = pagerState) { page ->
                val month = calendarDateAtMonthIndex(firstMonth + page)
                MonthGrid(
                    year = month.year,
                    month = month.month,
                    selectedDate = selectedDate,
                    previousSelectedDate = previousSelectedDate,
                    selectProgress = selectProgress.value,
                    deselectProgress = deselectProgress.value,
                    minDate = minDate,
                    maxDate = maxDate,
                    firstDayOfWeek = firstDayOfWeek,
                    labels = labels,
                    colors = colors,
                    onDateSelected = onDateSelected,
                )
            }
        }
    }
}

@Composable
private fun HeaderIcon(
    icon: ImageVector,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(CalendarPickerDefaults.HeaderIconSize)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(CalendarPickerDefaults.HeaderIconPadding),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color.copy(alpha = if (enabled) color.alpha else 0.3f),
            modifier = Modifier.size(CalendarPickerDefaults.HeaderIconSize - CalendarPickerDefaults.HeaderIconPadding * 2),
        )
    }
}

@Composable
private fun MonthGrid(
    year: Int,
    month: Int,
    selectedDate: CalendarPickerDate,
    previousSelectedDate: CalendarPickerDate?,
    selectProgress: Float,
    deselectProgress: Float,
    minDate: CalendarPickerDate,
    maxDate: CalendarPickerDate,
    firstDayOfWeek: Int,
    labels: CalendarPickerLabels,
    colors: CalendarPickerColors,
    onDateSelected: (CalendarPickerDate) -> Unit,
) {
    val daysInMonth = calendarDaysInMonth(year, month)
    val firstOffset = calendarFirstDayOffset(year, month, firstDayOfWeek)
    val rowCount = if (firstOffset + daysInMonth > 35) 6 else 5
    val rowHeight =
        if (rowCount == 6) {
            CalendarPickerDefaults.DayHeight
        } else {
            CalendarPickerDefaults.DayHeight + CalendarPickerDefaults.RowExtraSpacing
        }
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = CalendarPickerDefaults.MonthPaddingHorizontal),
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(CalendarPickerDefaults.WeekdayHeight)) {
            for (column in 0 until 7) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = labels.weekday(calendarFloorMod(firstDayOfWeek - 1 + column, 7) + 1),
                        color = colors.weekdayColor,
                        fontSize = CalendarPickerDefaults.WeekdayTextSize,
                    )
                }
            }
        }
        for (row in 0 until rowCount) {
            Row(modifier = Modifier.fillMaxWidth().height(rowHeight)) {
                for (column in 0 until 7) {
                    val day = row * 7 + column - firstOffset + 1
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        if (day in 1..daysInMonth) {
                            DayCell(
                                date = CalendarPickerDate(year, month, day),
                                selectedDate = selectedDate,
                                previousSelectedDate = previousSelectedDate,
                                selectProgress = selectProgress,
                                deselectProgress = deselectProgress,
                                minDate = minDate,
                                maxDate = maxDate,
                                colors = colors,
                                onClick = onDateSelected,
                            )
                        } else {
                            Text(
                                text = hintDayText(day, year, month, daysInMonth)?.toString().orEmpty(),
                                color = colors.hintDayColor,
                                fontSize = CalendarPickerDefaults.DayTextSize,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: CalendarPickerDate,
    selectedDate: CalendarPickerDate,
    previousSelectedDate: CalendarPickerDate?,
    selectProgress: Float,
    deselectProgress: Float,
    minDate: CalendarPickerDate,
    maxDate: CalendarPickerDate,
    colors: CalendarPickerColors,
    onClick: (CalendarPickerDate) -> Unit,
) {
    val selected = date == selectedDate
    val oldSelected = !selected && date == previousSelectedDate
    val enabled = date >= minDate && date <= maxDate
    val today = date == calendarToday()
    val contentColor =
        when {
            selected -> colors.selectedDayTextColor
            today -> colors.todayColor
            !enabled -> colors.disabledDayColor
            else -> colors.dayTextColor
        }
    val cellModifier =
        Modifier
            .size(CalendarPickerDefaults.SelectedCircleSize)
            .clip(CircleShape)
            .drawBehind {
                if (selected) {
                    drawCircle(
                        color = colors.selectedDayColor.copy(alpha = colors.selectedDayColor.alpha * selectProgress),
                        radius = size.minDimension / 2f * calendarPickerSelectedCircleScale(selectProgress),
                    )
                } else if (oldSelected) {
                    drawCircle(
                        color = colors.selectedDayColor.copy(alpha = colors.selectedDayColor.alpha * deselectProgress),
                        radius = size.minDimension / 2f,
                    )
                }
            }.then(
                if (today && !selected) {
                    Modifier.border(CalendarPickerDefaults.TodayStrokeWidth, colors.todayColor, CircleShape)
                } else {
                    Modifier
                },
            ).clickable(enabled = enabled) { onClick(date) }
    Box(modifier = cellModifier, contentAlignment = Alignment.Center) {
        Text(
            text = date.day.toString(),
            color = contentColor,
            fontSize = CalendarPickerDefaults.DayTextSize,
        )
    }
}

/** The neighbouring-month day number drawn in the hint colour (`COUIDateMonthView.drawDays`). */
private fun hintDayText(
    day: Int,
    year: Int,
    month: Int,
    daysInMonth: Int,
): Int? =
    when {
        day < 1 -> {
            val previousMonth = if (month == 1) 12 else month - 1
            val previousYear = if (month == 1) year - 1 else year
            calendarDaysInMonth(previousYear, previousMonth) + day
        }

        day > daysInMonth -> {
            day - daysInMonth
        }

        else -> {
            null
        }
    }

/**
 * The system "today" (UTC epoch days converted to a civil date with `CalendarPickerDefaults`'s Gregorian
 * math). The stock code uses `Calendar.getInstance()`, i.e. the local timezone; call sites that need local
 * midnight precision can compare against `CalendarPickerDefaults.MinDate`/`MaxDate` instead.
 */
@OptIn(ExperimentalTime::class)
internal fun calendarToday(): CalendarPickerDate =
    calendarDateFromDays(calendarFloorDiv(Clock.System.now().toEpochMilliseconds(), 86_400_000L))

/** Inverse of [calendarDaysFromCivil] (Howard Hinnant's `civil_from_days`). */
internal fun calendarDateFromDays(epochDays: Long): CalendarPickerDate {
    val z = epochDays + 719468L
    val era = (if (z >= 0L) z else z - 146096L) / 146097L
    val doe = z - era * 146097L
    val yoe = (doe - doe / 1460L + doe / 36524L - doe / 146096L) / 365L
    val y = yoe + era * 400L
    val doy = doe - (365L * yoe + yoe / 4L - yoe / 100L)
    val mp = (5L * doy + 2L) / 153L
    val day = (doy - (153L * mp + 2L) / 5L + 1L).toInt()
    val month = (if (mp < 10L) mp + 3L else mp - 9L).toInt()
    val year = (if (month <= 2) y + 1L else y).toInt()
    return CalendarPickerDate(year, month, day)
}

// coui_ic_chevron_start.xml (24dp, auto-mirrored).
private val calendarChevronStartIcon: ImageVector by lazy {
    ImageVector
        .Builder("CalendarChevronStart", 24.dp, 24.dp, 24f, 24f)
        .apply {
            path(
                fill = SolidColor(Color.Transparent),
                pathFillType = PathFillType.NonZero,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
            ) {
                moveTo(13.84f, 5.636f)
                lineTo(7.829f, 11.646f)
                curveTo(7.634f, 11.842f, 7.634f, 12.158f, 7.829f, 12.354f)
                lineTo(13.84f, 18.364f)
            }
        }.build()
}

// coui_ic_chevron_end.xml (24dp, auto-mirrored).
private val calendarChevronEndIcon: ImageVector by lazy {
    ImageVector
        .Builder("CalendarChevronEnd", 24.dp, 24.dp, 24f, 24f)
        .apply {
            path(
                fill = SolidColor(Color.Transparent),
                pathFillType = PathFillType.NonZero,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.4f,
            ) {
                moveTo(10.16f, 5.636f)
                lineTo(16.171f, 11.646f)
                curveTo(16.366f, 11.842f, 16.366f, 12.158f, 16.171f, 12.354f)
                lineTo(10.16f, 18.364f)
            }
        }.build()
}

// coui_calendar_arrow_expanded / _collapsed.xml (16dp).
private val calendarExpandLessIcon: ImageVector by lazy {
    ImageVector
        .Builder("CalendarExpandLess", 16.dp, 16.dp, 16f, 16f)
        .apply {
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
            ) {
                moveTo(4f, 10f)
                lineTo(8f, 6f)
                lineTo(12f, 10f)
            }
        }.build()
}

private val calendarExpandMoreIcon: ImageVector by lazy {
    ImageVector
        .Builder("CalendarExpandMore", 16.dp, 16.dp, 16f, 16f)
        .apply {
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
            ) {
                moveTo(4f, 6f)
                lineTo(8f, 10f)
                lineTo(12f, 6f)
            }
        }.build()
}
