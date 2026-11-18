# TimePicker

ColorOS 的滚轮时间选择器：`COUITimePicker`（`com.coui.appcompat.picker`），布局 `coui_time_picker.xml`。
ColorOS 设置里以 `LocalSettingsTimePicker` 子类化使用。

一个时列加一个分列；12 小时制下中间会插入一个上午/下午列，时列变成 `1..12`。官方内部始终用 `0..23`
存时间、只在显示时映射，所以 `hour` 参数不论 `is24Hour` 都是 24 小时制数值。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TimePicker
```

## 基本用法

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

// 12 小时制会多出上午/下午列
TimePicker(hour = hour, minute = minute, is24Hour = false, onTimeChange = { h, m -> })
```

## 参数

| 参数名     | 类型                          | 说明                             | 默认值 |
| :--------- | :---------------------------- | :------------------------------- | :----- |
| hour       | Int                           | 选中的小时，`0..23`              | - |
| minute     | Int                           | 选中的分钟，`0..59`              | - |
| onTimeChange | (Int, Int) -> Unit          | 回调新的 24 小时制时与分         | - |
| modifier   | Modifier                      | 应用到整行                       | Modifier |
| is24Hour   | Boolean                       | `0..23` 无上午/下午列，或 `1..12` 带上午/下午列 | true |
| minuteStep | Int                           | 每一格代表的分钟数（官方支持 1 / 5） | 1 |
| labels     | TimePickerLabels              | 各列文案                         | TimePickerDefaults.labels(is24Hour) |
| colors     | NumberPickerColors            | 滚轮颜色                         | NumberPickerDefaults.colors() |
| enabled    | Boolean                       | 是否响应输入                     | true |

## TimePickerDefaults

| 名称            | 类型   | 来源                                       | 值 |
| :-------------- | :----- | :----------------------------------------- | :---- |
| HourWidth       | Dp     | `coui_number_picker_width_small`           | 62dp |
| MinuteWidth     | Dp     | `coui_number_picker_width_small`           | 62dp |
| AmPmWidth       | Dp     | `coui_number_picker_width_small`           | 62dp |
| VerticalPadding | Dp     | `coui_number_picker_view_padding_vertical` | 12dp |
| AmLabel         | String | `R.string.morning`                         | 上午 |
| PmLabel         | String | `R.string.afternoon`                       | 下午 |
| MinuteStep      | Int    | `COUITimePicker.MINUTES_STEP`              | 1 |

各列高 `coui_time_picker_height`（135dp = 三行 45dp），12dp 由整行承担。

## 值得注意的行为

- **只有 24 小时制的时列补零。** `COUITimePicker` 只在 24 小时制下给时列 `setTwoDigitFormatter()`；
- **只有 24 小时制的时列补零，但分列始终补零。** `COUITimePicker` 在 24 小时制下给时列调用 `setTwoDigitFormatter()`，
  并且无条件给分列调用，因此分钟显示 `05`；12 小时制的时列显示 `1..12`，不带前导零。
  12 会跨过上午/下午的原因。
- **上午/下午列不循环**（`setWrapSelectorWheel(false)`），时列循环。

## 已知差异

- 官方可选的前置日期列（`coui_time_picker_date`，150dp，`MMMdd日 E` 文案）未建模；那一列只出现在同时选
  日期的选择器里。
- 五分钟模式以 `minuteStep` 参数暴露，而不是由组件内部开关。
