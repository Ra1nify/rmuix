# TimeLimitPicker

The ColorOS picker with unit labels: `COUITimeLimitPicker` (`coui_time_limit_picker.xml`).

Two wheels with the unit drawn **outside** them - `小时` after the hour wheel, `分钟` after the minute wheel -
plus an AM/PM wheel in front of the hour wheel in 12-hour mode. ColorOS Settings uses it for every 定时
screen: `timepower_time_settings.xml` (定时开关机), the dark-mode schedule
(`DarkModeSettingTimePreference`), the eye-care schedule (`ProtectEyesSettingTimePreference`) and the
breathing-light schedule (`BreathingLightTimeSelectPreference`). `SettingsTimePicker` subclasses it and
hides the units with `setTextVisibility(false)`.

The labelled form is the default: `COUITimeLimitPicker` clears the wheels' own `setUnitText("")` at
construction and keeps the two `TextView`s visible.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TimeLimitPicker
```

## Basic Usage

```kotlin
var hour by remember { mutableIntStateOf(22) }
var minute by remember { mutableIntStateOf(30) }

TimeLimitPicker(
    hour = hour,
    minute = minute,
    onTimeChange = { newHour, newMinute ->
        hour = newHour
        minute = newMinute
    },
)

// SettingsTimePicker: same widget without the unit labels
TimeLimitPicker(hour = hour, minute = minute, showUnits = false, onTimeChange = { h, m -> })
```

## Properties

| Name        | Type                                | Description                                            | Default |
| :---------- | :---------------------------------- | :----------------------------------------------------- | :------ |
| hour        | Int                                 | Selected hour (`0..23`, or `1..12` when `is24Hour=false`) | - |
| minute      | Int                                 | Selected minute, `0..59`                                | - |
| onTimeChange| (Int, Int) -> Unit                  | Called with the new hour and minute when a wheel settles | - |
| modifier    | Modifier                            | Modifier applied to the row                            | Modifier |
| is24Hour    | Boolean                             | `0..23` without AM/PM, or `1..12` with AM/PM           | true |
| showUnits   | Boolean                             | Draw the `小时` / `分钟` labels (`setTextVisibility`)     | true |
| labels      | TimeLimitPickerLabels               | Column and unit labels                                 | TimeLimitPickerDefaults.labels(is24Hour) |
| colors      | NumberPickerColors                  | Colours of the wheels                                  | NumberPickerDefaults.colors() |
| enabled     | Boolean                             | Whether the wheels respond to input                    | true |

## TimeLimitPickerDefaults

| Name                   | Type     | Source                                                | Value |
| :--------------------- | :------- | :---------------------------------------------------- | :---- |
| HourWidth              | Dp       | `coui_number_picker_width_middle` (`couiPickerVisualWidth`) | 76dp |
| MinuteWidth            | Dp       | `coui_number_picker_width_middle`                     | 76dp |
| AmPmWidth              | Dp       | `coui_number_picker_width_small`                      | 62dp |
| VerticalPadding        | Dp       | `coui_number_picker_view_padding_vertical`            | 12dp |
| UnitTextSize           | TextUnit | `coui_timepicker_text_size` (declared `16dp`, used as a text size, so `16sp`) | 16sp |
| HourUnitWidth          | Dp       | `android:layout_width` of the hour unit in the layout   | 50dp |
| HourUnitOffsetStart    | Dp       | `android:layout_marginLeft="-10dp"` on the hour unit    | -10dp |
| MinuteUnitPaddingStart | Dp       | `android:paddingLeft` of the minute unit in the layout  | 56dp |
| HourUnit               | String   | `R.string.coui_hour`                                   | 小时 |
| MinuteUnit             | String   | `R.string.coui_minute`                                 | 分钟 |
| AmLabel                | String   | `R.string.morning`                                     | 上午 |
| PmLabel                | String   | `R.string.afternoon`                                   | 下午 |

The wheels are `coui_timepicker_height` (135dp = three 45dp rows) tall, so the row carries the 12dp and
the inner [NumberPicker]s are drawn without `NumberPickerDefaults.VerticalPadding`.

## Zero padding matches TimePicker

`COUITimeLimitPicker` calls `setTwoDigitFormatter()` on the hour wheel in 24-hour mode **and on the
minute wheel unconditionally**, so minutes render as `05`. [TimePicker] applies the same rule (its minute
wheel is always padded), so the earlier note that TimePicker renders `5` was wrong.

## Known gaps

- `@color/coui_picker_text_color` (the unit labels' colour) is defined in the `com.support.picker`
  library and is not part of the APK's `values`, so the labels take the wheels' selected colour
  (`couiColorPrimaryNeutral`).
- The hour unit's negative start margin is applied as a layout-neutral offset, since Compose has no
  negative margin.
- **Correction:** `COUITimePicker.setRowNumber()` / `COUINumberPicker.setPickerRowNumber()` exist in the
  stock API, but **no decompiled class or layout calls them** (the only `setRowNumber` call sites belong to
  ZXing). The three visible rows are a layout consequence: the wheels are `coui_timepicker_height` = 135dp
  = 3 × 45dp, while the `couiPickerRowNumber` attribute (default 5) only sizes the internal selector ring
  (`mSelectorItemCount = rowNumber + 2`) and does not change the viewport. There is therefore no row-count
  behaviour to expose.
