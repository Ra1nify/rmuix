# TimePicker

The ColorOS wheel time picker: `COUITimePicker` (`com.coui.appcompat.picker`), inflated from
`coui_time_picker.xml`. ColorOS Settings subclasses it as `LocalSettingsTimePicker`.

An hour wheel and a minute wheel; in 12-hour mode an AM/PM wheel sits between them and the hour wheel
runs `1..12`. Stock keeps the hour in `0..23` internally and only maps it for display, so `hour` is
always the 24-hour value regardless of `is24Hour`.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TimePicker
```

## Basic Usage

```kotlin
var hour by remember { mutableIntStateOf(9) }
var minute by remember { mutableIntStateOf(30) }

TimePicker(
    hour = hour,
    minute = minute,
    onTimeChange = { newHour, newMinute ->
        hour = newHour
        minute = newMinute
    },
)

// 12-hour mode adds the AM/PM wheel
TimePicker(hour = hour, minute = minute, is24Hour = false, onTimeChange = { h, m -> })
```

## Properties

| Name       | Type                          | Description                                    | Default |
| :--------- | :---------------------------- | :--------------------------------------------- | :------ |
| hour       | Int                           | Selected hour, `0..23`                          | - |
| minute     | Int                           | Selected minute, `0..59`                        | - |
| onTimeChange | (Int, Int) -> Unit          | Called with the new 24-hour hour and minute     | - |
| modifier   | Modifier                      | Modifier applied to the row                     | Modifier |
| is24Hour   | Boolean                       | `0..23` without AM/PM, or `1..12` plus AM/PM    | true |
| minuteStep | Int                           | Minutes per wheel step (`1` or `5` in stock)    | 1 |
| labels     | TimePickerLabels              | Column labels                                   | TimePickerDefaults.labels(is24Hour) |
| colors     | NumberPickerColors            | Colours of the wheels                           | NumberPickerDefaults.colors() |
| enabled    | Boolean                       | Whether the wheels respond to input             | true |

## TimePickerDefaults

| Name            | Type   | Source                                      | Value |
| :-------------- | :----- | :------------------------------------------ | :---- |
| HourWidth       | Dp     | `coui_number_picker_width_small`            | 62dp |
| MinuteWidth     | Dp     | `coui_number_picker_width_small`            | 62dp |
| AmPmWidth       | Dp     | `coui_number_picker_width_small`            | 62dp |
| VerticalPadding | Dp     | `coui_number_picker_view_padding_vertical`  | 12dp |
| AmLabel         | String | `R.string.morning`                          | 上午 |
| PmLabel         | String | `R.string.afternoon`                        | 下午 |
| MinuteStep      | Int    | `COUITimePicker.MINUTES_STEP`               | 1 |

The columns are `coui_time_picker_height` (135dp = three 45dp rows) tall, so the row carries the 12dp.

## Behaviour worth knowing

- **Only the 24-hour hour wheel is zero-padded, but minutes always are.** `COUITimePicker` calls
  `setTwoDigitFormatter()` on the hour wheel in 24-hour mode and on the minute wheel unconditionally, so
  minutes render as `05` while the 12-hour hour wheel renders `1..12` without a leading zero.
- **Settling the 12-hour wheel on `12` flips the meridian.** Stock toggles `mAmPm` when the hour value
  becomes `12`, which is what makes a single step from 11 to 12 cross AM/PM.
- **The AM/PM wheel does not wrap** (`setWrapSelectorWheel(false)`), the hour wheel does.

## Known gaps

- Stock's optional leading date column (`coui_time_picker_date`, 150dp, `MMMdd日 E` names) is not
  modelled; that column only appears in pickers that also choose a day.
- The five-minute mode is exposed as `minuteStep` instead of being toggled internally by the component.
