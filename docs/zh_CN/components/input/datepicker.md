# DatePicker

ColorOS 的滚轮日期选择器：`COUIDatePicker`（`com.coui.appcompat.picker`），布局 `coui_date_picker.xml`。
ColorOS 设置里以 `SettingsDatePicker` 子类化使用。

一行三列竖向滚轮——年、月、日。日列会按所选月份的长度（含闰年）收缩，所以 31 号不会在切到 30 天的月份后
继续存在。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.DatePicker
```

## 基本用法

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

`month` 是 **1 起算**（`1..12`）。官方的 `COUIDatePicker.getMonth()` 返回的是 0 起算的
`Calendar.MONTH`；本组件在 Kotlin 侧统一用 `1..12`。

## 参数

| 参数名    | 类型                                | 说明                       | 默认值 |
| :-------- | :---------------------------------- | :------------------------- | :----- |
| year      | Int                                 | 选中年                     | - |
| month     | Int                                 | 选中月，`1..12`            | - |
| day       | Int                                 | 选中日，1 起算             | - |
| onDateChange | (Int, Int, Int) -> Unit          | 任一列停稳后回调年月日     | - |
| modifier  | Modifier                            | 应用到整行                 | Modifier |
| yearRange | IntRange                            | 可选年份范围               | 1900..2100 |
| labels    | DatePickerLabels                    | 三列的文案                 | DatePickerDefaults.labels() |
| colors    | NumberPickerColors                  | 滚轮颜色                   | NumberPickerDefaults.colors() |
| enabled   | Boolean                             | 是否响应输入               | true |
| minDate   | DatePickerDate?                     | 下界（`setMinDate`），按列收缩 | null |
| maxDate   | DatePickerDate?                     | 上界（`setMaxDate`），按列收缩 | null |

## DatePickerDefaults

| 名称            | 类型   | 来源                                                          | 值 |
| :-------------- | :----- | :------------------------------------------------------------ | :---- |
| YearWidth       | Dp     | `coui_datepicker_width_year` → `coui_numberpicker_width_big`   | 76dp |
| MonthWidth      | Dp     | `coui_datepicker_width_month` → `coui_number_picker_width_big` | 96dp |
| DayWidth        | Dp     | `coui_number_picker_width_small`                               | 62dp |
| VerticalPadding | Dp     | `coui_number_picker_view_padding_vertical`                     | 12dp |
| YearSuffix      | String | `R.string.coui_year`（`values-zh-rCN`）                        | 年 |
| MonthSuffix     | String | `R.string.coui_month`                                          | 月 |
| DaySuffix       | String | `R.string.coui_day`                                            | 日 |
| YearRange       | IntRange | 官方未设 `minDate`/`maxDate` 时的回退范围                     | 1900..2100 |

每列高 `coui_datepicker_height`（135dp），正好是三行 45dp，所以内层 [NumberPicker] 不叠加
`NumberPickerDefaults.VerticalPadding`，12dp 由整行承担。

## 已知差异

- 官方的列顺序来自 `DateFormat.getBestDateTimePattern(locale, "yyyyMMMMdd")`；中文是年-月-日，
  这里固定该顺序（RTL 下反转）。其它语言的顺序未建模。
- `setMinDate` / `setMaxDate` 已通过可选的 [DatePickerDate] `minDate` / `maxDate` 参数建模：年列收缩到
  边界年份；当选中年的月与边界同年月一致时，月列/日列也按边界收缩（`updateSpinners()`）；传入日期按同样
  规则夹紧（`datePickerClampDate()`）。两个边界都沿用 [DatePickerDefaults.YearRange] 回退范围。官方的
  `Calendar` 时刻语义未建模——边界就是普通日历日期。
