# TimeLimitPicker

带单位文案的 ColorOS 选择器：`COUITimeLimitPicker`（`coui_time_limit_picker.xml`）。

两个滚轮，单位画在滚轮**外面**——时轮后面是 `小时`，分轮后面是 `分钟`；12 小时制下时轮前面还有一个上午/下午轮。
ColorOS 设置里所有「定时」界面都用它：`timepower_time_settings.xml`（定时开关机）、深色模式定时
（`DarkModeSettingTimePreference`）、护眼模式定时（`ProtectEyesSettingTimePreference`）、呼吸灯定时
（`BreathingLightTimeSelectPreference`）。`SettingsTimePicker` 继承它并用 `setTextVisibility(false)`
把单位隐藏。

默认就是带单位的形式：`COUITimeLimitPicker` 构造时把两个滚轮自身的 `setUnitText("")` 清空，只保留那两个
`TextView`。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TimeLimitPicker
```

## 基本用法

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

// 对应 SettingsTimePicker：同一个控件，不画单位
TimeLimitPicker(hour = hour, minute = minute, showUnits = false, onTimeChange = { h, m -> })
```

## 参数

| 参数名      | 类型                                | 说明                                       | 默认值 |
| :---------- | :---------------------------------- | :----------------------------------------- | :----- |
| hour        | Int                                 | 选中的小时（`0..23`；`is24Hour=false` 时 `1..12`） | - |
| minute      | Int                                 | 选中的分钟，`0..59`                         | - |
| onTimeChange| (Int, Int) -> Unit                  | 任一滚轮停稳后回调时与分                   | - |
| modifier    | Modifier                            | 应用到整行                                 | Modifier |
| is24Hour    | Boolean                             | `0..23` 无上午/下午轮，或 `1..12` 带上午/下午轮 | true |
| showUnits   | Boolean                             | 是否绘制 `小时` / `分钟`（`setTextVisibility`） | true |
| labels      | TimeLimitPickerLabels               | 各列与单位文案                             | TimeLimitPickerDefaults.labels(is24Hour) |
| colors      | NumberPickerColors                  | 滚轮颜色                                   | NumberPickerDefaults.colors() |
| enabled     | Boolean                             | 是否响应输入                               | true |

## TimeLimitPickerDefaults

| 名称                   | 类型     | 来源                                                  | 值 |
| :--------------------- | :------- | :---------------------------------------------------- | :---- |
| HourWidth              | Dp       | `coui_number_picker_width_middle`（`couiPickerVisualWidth`） | 76dp |
| MinuteWidth            | Dp       | `coui_number_picker_width_middle`                     | 76dp |
| AmPmWidth              | Dp       | `coui_number_picker_width_small`                      | 62dp |
| VerticalPadding        | Dp       | `coui_number_picker_view_padding_vertical`            | 12dp |
| UnitTextSize           | TextUnit | `coui_timepicker_text_size`（声明为 `16dp`，却被当作文本尺寸，故 `16sp`） | 16sp |
| HourUnitWidth          | Dp       | 布局里时单位的 `layout_width`                          | 50dp |
| HourUnitOffsetStart    | Dp       | 时单位的 `android:layout_marginLeft="-10dp"`           | -10dp |
| MinuteUnitPaddingStart | Dp       | 布局里分单位的 `paddingLeft`                           | 56dp |
| HourUnit               | String   | `R.string.coui_hour`                                  | 小时 |
| MinuteUnit             | String   | `R.string.coui_minute`                                | 分钟 |
| AmLabel                | String   | `R.string.morning`                                    | 上午 |
| PmLabel                | String   | `R.string.afternoon`                                  | 下午 |

各滚轮高 `coui_timepicker_height`（135dp = 三行 45dp），12dp 由整行承担，内层 [NumberPicker] 不再叠加
`NumberPickerDefaults.VerticalPadding`。

## 补零规则与 TimePicker 不同

`COUITimeLimitPicker` 在 24 小时制下给时列、并且始终给分列调用 `setTwoDigitFormatter()`，
所以分钟显示 `05`；[TimePicker] 的分列同样始终补零，两者行为一致。之前“TimePicker 显示 5”的说法是错的。

## 已知差异

- 单位文案的颜色 `@color/coui_picker_text_color` 定义在 `com.support.picker` 库里，不在 APK 的 `values` 中，
  因此这里取滚轮的选中色（`couiColorPrimaryNeutral`）。
- 时单位的负左边距用「不参与布局的 offset」实现，因为 Compose 没有负 margin。
- **更正：** `COUITimePicker.setRowNumber()` / `COUINumberPicker.setPickerRowNumber()` 虽是官方公开 API，
  但**反编译类与布局里没有任何调用方**（仅 ZXing 里有同名 `setRowNumber`）。可见三行是布局结果：
  滚轮高 `coui_timepicker_height` = 135dp = 3 × 45dp；`couiPickerRowNumber` 属性（默认 5）只决定内部
  选择环大小（`mSelectorItemCount = rowNumber + 2`），不改变可见行数。因此没有需要暴露的行数行为。