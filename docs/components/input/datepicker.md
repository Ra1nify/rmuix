# DatePicker

The ColorOS wheel date picker: `COUIDatePicker` (`com.coui.appcompat.picker`), inflated from
`coui_date_picker.xml`. ColorOS Settings subclasses it as `SettingsDatePicker`.

Three vertical wheels - year, month, day - in a row. The day wheel is clamped to the length of the
selected month (leap years included), so the 31st cannot survive a switch to a 30-day month.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.DatePicker
```

## Basic Usage

```kotlin
var year by remember { mutableIntStateOf(2026) }
var month by remember { mutableIntStateOf(12) }
var day by remember { mutableIntStateOf(31) }

DatePicker(
    year = year,
    month = month,
    day = day,
    onDateChange = { newYear, newMonth, newDay ->
        year = newYear
        month = newMonth
        day = newDay
    },
)
```

`month` is `1`-based (`1..12`). Stock's `COUIDatePicker.getMonth()` returns the 0-based
`Calendar.MONTH`; the component takes and reports `1..12` for the Kotlin side.

## Properties

| Name      | Type                                | Description                                          | Default |
| :-------- | :---------------------------------- | :--------------------------------------------------- | :------ |
| year      | Int                                 | Selected year                                        | - |
| month     | Int                                 | Selected month, `1..12`                              | - |
| day       | Int                                 | Selected day of month, `1`-based                     | - |
| onDateChange | (Int, Int, Int) -> Unit          | Called with year, month and day when a wheel settles | - |
| modifier  | Modifier                            | Modifier applied to the row                          | Modifier |
| yearRange | IntRange                            | Selectable years                                     | 1900..2100 |
| labels    | DatePickerLabels                    | Column labels                                        | DatePickerDefaults.labels() |
| colors    | NumberPickerColors                  | Colours of the wheels                                | NumberPickerDefaults.colors() |
| enabled   | Boolean                             | Whether the wheels respond to input                  | true |
| minDate   | DatePickerDate?                     | Lower bound (`setMinDate`), clipped per column       | null |
| maxDate   | DatePickerDate?                     | Upper bound (`setMaxDate`), clipped per column       | null |

## DatePickerDefaults

| Name            | Type   | Source                                                        | Value |
| :-------------- | :----- | :------------------------------------------------------------ | :---- |
| YearWidth       | Dp     | `coui_datepicker_width_year` -> `coui_numberpicker_width_big`  | 76dp |
| MonthWidth      | Dp     | `coui_datepicker_width_month` -> `coui_number_picker_width_big` | 96dp |
| DayWidth        | Dp     | `coui_number_picker_width_small`                               | 62dp |
| VerticalPadding | Dp     | `coui_number_picker_view_padding_vertical`                     | 12dp |
| YearSuffix      | String | `R.string.coui_year` (`values-zh-rCN`)                         | 年 |
| MonthSuffix     | String | `R.string.coui_month`                                          | 月 |
| DaySuffix       | String | `R.string.coui_day`                                            | 日 |
| YearRange       | IntRange | `COUIDatePicker` fallback when `minDate`/`maxDate` are unset | 1900..2100 |

Each column is `coui_datepicker_height` (135dp) tall, which is exactly three 45dp rows, so the inner
[NumberPicker]s are drawn without `NumberPickerDefaults.VerticalPadding` and the row carries the 12dp.

## Known gaps

- `COUIDatePicker` derives the column order from `DateFormat.getBestDateTimePattern(locale,
  "yyyyMMMMdd")`; the Chinese pattern is year-month-day, which is the fixed order here (reversed for
  RTL). Other locales' orders are not modelled.
- `setMinDate` / `setMaxDate` are modelled through the optional [DatePickerDate] `minDate` / `maxDate`
  parameters: the year wheel is clipped to the bound years and, when the selected year / month matches a
  bound, the month / day wheels are clipped too (`updateSpinners()`); requested dates are clamped into the
  same ranges (`datePickerClampDate()`). Both bounds share the stock fallback of [DatePickerDefaults.YearRange].
  The stock `Calendar` time-of-day semantics are not modelled - the bounds are plain calendar dates.
