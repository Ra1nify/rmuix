# CalendarPicker

The ColorOS month-grid calendar: `COUICalendarPicker` (`com.coui.appcompat.calendar`), used by Gallery's capture
time adjust sheet (`business_lib_adjust_capture_time_sheet.xml`).

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.CalendarPicker
import io.github.ra1nify.rmuix.kmp.basic.CalendarPickerDate
```

## Basic Usage

```kotlin
var date by remember { mutableStateOf(CalendarPickerDate(2026, 11, 17)) }
CalendarPicker(
    selectedDate = date,
    onDateSelected = { date = it },
    minDate = CalendarPickerDate(2020, 1, 1),
    maxDate = CalendarPickerDate(2030, 12, 31),
)
```

## Properties

| Name | Type | Description | Default |
| :--- | :--- | :--- | :--- |
| selectedDate | CalendarPickerDate | The selected date (hoisted) | - |
| onDateSelected | (CalendarPickerDate) -> Unit | Called with the tapped date | - |
| modifier | Modifier | Applied to the picker | Modifier |
| minDate | CalendarPickerDate | Earliest selectable date | 1900-01-01 |
| maxDate | CalendarPickerDate | Latest selectable date | 2100-12-31 |
| firstDayOfWeek | Int | `1` = Monday ... `7` = Sunday (`android:firstDayOfWeek`) | 1 |
| labels | CalendarPickerLabels | Weekday and month-year labels | English labels |
| colors | CalendarPickerColors | Picker colours | CalendarPickerDefaults.colors() |

## CalendarPickerDefaults

| Name | Type | Source | Value |
| :--- | :--- | :--- | :--- |
| MaxWidth / MinWidth | Dp | `calendar_picker_max_width` / `_min_width` | 724dp / 300dp |
| VerticalMargin | Dp | `coui_calendar_picker_material.xml` marginVertical | 16dp |
| HeaderHeight | Dp | header row | 32dp |
| HeaderPaddingHorizontal | Dp | header paddingStart/End | 24dp |
| HeaderIconSize | Dp | `coui_ic_chevron_start/end` | 24dp |
| HeaderIconPadding | Dp | header icon padding | 4dp |
| HeaderIconGap | Dp | next-button marginStart / title-arrow gap | 8dp |
| TitleIconSize | Dp | `calendar_picker_title_icon_size` | 16dp |
| TitleTextSize | TextUnit | `calendar_picker_month_text_size` | 16sp |
| MonthPaddingHorizontal | Dp | `calendar_picker_padding_horizontal` | 16dp |
| WeekdayHeight / WeekdayTextSize | Dp / TextUnit | `calendar_picker_day_of_week_height` / `_text_size` | 40dp / 14sp |
| DayHeight / DayTextSize | Dp / TextUnit | `calendar_picker_day_height` / `TextAppearance.Material.Widget.Calendar.Day` | 40dp / 16sp |
| RowExtraSpacing | Dp | `calendar_picker_day_min_col_padding` (5-row months) | 8dp |
| SelectedCircleSize | Dp | `calendar_picker_current_day_radius` | 32dp |
| TodayStrokeWidth | Dp | `calendar_picker_current_day_stroke_radius` | 1.8dp |
| SelectionAnimationMillis / DeselectionAnimationMillis | Int | `COUIDateMonthView.configAnimator()` | 280 / 150 |
| FirstDayOfWeek | Int | `android:firstDayOfWeek` default | 1 |
| MinDate / MaxDate | CalendarPickerDate | Material default range | 1900-01-01 / 2100-12-31 |

## Colours

| Slot | Source | Light | Dark |
| :--- | :--- | :--- | :--- |
| titleColor / iconColor | `?attr/couiColorPrimaryNeutral` | `#E6000000` | `#E6FFFFFF` |
| weekdayColor | `?attr/couiColorHintNeutral` | `#42000000` | `#4DFFFFFF` |
| dayTextColor | `?attr/couiColorPrimaryNeutral` | `#E6000000` | `#E6FFFFFF` |
| hintDayColor / disabledDayColor | `?attr/couiColorDisabledNeutral` | `#42000000` | `#4DFFFFFF` |
| selectedDayColor / todayColor | `?attr/couiColorPrimary` | theme primary | theme primary |
| selectedDayTextColor | `?attr/couiColorOnPrimary` | `#FFFFFF` | `#FFFFFF` |
| expandArrowColor / expandArrowExpandedColor | `?attr/couiColorLabelSecondary` / `?attr/couiColorPrimary` | `#8A000000` / primary | `#8AFFFFFF` / primary |

## Selection animation

`COUIDateMonthView.configAnimator()` starts two animators on `setSelectedDay`:

- **circle in** - 280ms on `COUIMoveEaseInterpolator` (`cubic-bezier(0.3, 0, 0.1, 1)`): the selected
  circle grows from 80% to 100% of the day-circle radius (`mDaySelectRadius = radius*0.2*f + radius*0.8`)
  while its paint alpha goes 0 -> 255;
- **circle out** - 150ms on `COUIEaseInterpolator` (`cubic-bezier(0.33, 0, 0.67, 1)`): the previously
  selected day keeps a full-radius circle whose alpha fades 255 -> 0.

The component drives both from `CalendarPickerDefaults.SelectionAnimationMillis` /
`DeselectionAnimationMillis`; `calendarPickerSelectedCircleScale()` exposes the stock 0.8..1 radius mapping.

## Known gaps

- The expand arrow swaps the month grid for the existing `DatePicker` wheel (`COUICalendarYearView` is a
  `COUIDatePicker` in the stock code), but the 120ms fade animator and the rotating arrow animation are not
  reproduced.
- The touch highlight circle (`mHighlightedDay` + `SELECTED_HIGHLIGHT_ALPHA` = 176 overlay) is not modelled; the
  selected-circle in/out animation now is.
- `today` comes from `kotlin.time.Clock` in UTC; a timezone-aware local date can be passed by comparing against
  the month grid instead (documented on `calendarToday()`).
- RTL mirroring, keyboard focus traversal and the `ExploreByTouchHelper` month accessibility nodes are not
  reproduced.
