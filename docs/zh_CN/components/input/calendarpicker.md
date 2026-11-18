# CalendarPicker

ColorOS 月历选择器：官方 `COUICalendarPicker`（`com.coui.appcompat.calendar`），相册拍摄时间调整面板
（`business_lib_adjust_capture_time_sheet.xml`）在使用。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.CalendarPicker
import io.github.ra1nify.rmuix.kmp.basic.CalendarPickerDate
```

## 基本用法

```kotlin
var date by remember { mutableStateOf(CalendarPickerDate(2026, 11, 17)) }
CalendarPicker(
    selectedDate = date,
    onDateSelected = { date = it },
    minDate = CalendarPickerDate(2020, 1, 1),
    maxDate = CalendarPickerDate(2030, 12, 31),
)
```

## 参数

| 参数名 | 类型 | 说明 | 默认值 |
| :--- | :--- | :--- | :--- |
| selectedDate | CalendarPickerDate | 当前选中日期（状态提升） | - |
| onDateSelected | (CalendarPickerDate) -> Unit | 点击日期回调 | - |
| modifier | Modifier | 应用到选择器 | Modifier |
| minDate | CalendarPickerDate | 最早可选日期 | 1900-01-01 |
| maxDate | CalendarPickerDate | 最晚可选日期 | 2100-12-31 |
| firstDayOfWeek | Int | `1` = 周一 … `7` = 周日（`android:firstDayOfWeek`） | 1 |
| labels | CalendarPickerLabels | 星期与年月标题文案 | 英文文案 |
| colors | CalendarPickerColors | 选择器颜色 | CalendarPickerDefaults.colors() |

## CalendarPickerDefaults

| 名称 | 类型 | 来源 | 值 |
| :--- | :--- | :--- | :--- |
| MaxWidth / MinWidth | Dp | `calendar_picker_max_width` / `_min_width` | 724dp / 300dp |
| VerticalMargin | Dp | `coui_calendar_picker_material.xml` marginVertical | 16dp |
| HeaderHeight | Dp | 头部行高 | 32dp |
| HeaderPaddingHorizontal | Dp | 头部 paddingStart/End | 24dp |
| HeaderIconSize | Dp | `coui_ic_chevron_start/end` | 24dp |
| HeaderIconPadding | Dp | 头部图标 padding | 4dp |
| HeaderIconGap | Dp | 下一月按钮 marginStart / 标题箭头间距 | 8dp |
| TitleIconSize | Dp | `calendar_picker_title_icon_size` | 16dp |
| TitleTextSize | TextUnit | `calendar_picker_month_text_size` | 16sp |
| MonthPaddingHorizontal | Dp | `calendar_picker_padding_horizontal` | 16dp |
| WeekdayHeight / WeekdayTextSize | Dp / TextUnit | `calendar_picker_day_of_week_height` / `_text_size` | 40dp / 14sp |
| DayHeight / DayTextSize | Dp / TextUnit | `calendar_picker_day_height` / `TextAppearance.Material.Widget.Calendar.Day` | 40dp / 16sp |
| RowExtraSpacing | Dp | `calendar_picker_day_min_col_padding`（5 行月份） | 8dp |
| SelectedCircleSize | Dp | `calendar_picker_current_day_radius` | 32dp |
| TodayStrokeWidth | Dp | `calendar_picker_current_day_stroke_radius` | 1.8dp |
| SelectionAnimationMillis / DeselectionAnimationMillis | Int | `COUIDateMonthView.configAnimator()` | 280 / 150 |
| FirstDayOfWeek | Int | `android:firstDayOfWeek` 默认值 | 1 |
| MinDate / MaxDate | CalendarPickerDate | Material 默认范围 | 1900-01-01 / 2100-12-31 |

## 颜色

| 槽位 | 来源 | 浅色 | 深色 |
| :--- | :--- | :--- | :--- |
| titleColor / iconColor | `?attr/couiColorPrimaryNeutral` | `#E6000000` | `#E6FFFFFF` |
| weekdayColor | `?attr/couiColorHintNeutral` | `#42000000` | `#4DFFFFFF` |
| dayTextColor | `?attr/couiColorPrimaryNeutral` | `#E6000000` | `#E6FFFFFF` |
| hintDayColor / disabledDayColor | `?attr/couiColorDisabledNeutral` | `#42000000` | `#4DFFFFFF` |
| selectedDayColor / todayColor | `?attr/couiColorPrimary` | 主题 primary | 主题 primary |
| selectedDayTextColor | `?attr/couiColorOnPrimary` | `#FFFFFF` | `#FFFFFF` |
| expandArrowColor / expandArrowExpandedColor | `?attr/couiColorLabelSecondary` / `?attr/couiColorPrimary` | `#8A000000` / primary | `#8AFFFFFF` / primary |

## 选中动画

`COUIDateMonthView.configAnimator()` 在 `setSelectedDay` 时同时启动两个动画：

- **圆圈进入** —— 280ms，`COUIMoveEaseInterpolator`（`cubic-bezier(0.3, 0, 0.1, 1)`）：选中圆从日圆半径的
  80% 放大到 100%（`mDaySelectRadius = radius*0.2*f + radius*0.8`），同时画笔 alpha 0 → 255；
- **圆圈退出** —— 150ms，`COUIEaseInterpolator`（`cubic-bezier(0.33, 0, 0.67, 1)`）：原选中日保留整圆，
  alpha 由 255 淡出到 0。

组件用 `CalendarPickerDefaults.SelectionAnimationMillis` / `DeselectionAnimationMillis` 驱动两者；
`calendarPickerSelectedCircleScale()` 暴露官方 0.8..1 的半径映射。

## 已知差异

- 展开箭头会把月历换成现有 `DatePicker` 滚轮（官方 `COUICalendarYearView` 内就是 `COUIDatePicker`），但 120ms
  淡入动画与箭头旋转动画未复现。
- 按压高亮圈（`mHighlightedDay` + `SELECTED_HIGHLIGHT_ALPHA` = 176 的叠加层）未建模；选中圆进入/退出动画已实现。
- `today` 通过 `kotlin.time.Clock` 以 UTC 计算；需要本地时区精确值的调用方可自行比较。
- RTL 镜像、键盘焦点遍历与 `ExploreByTouchHelper` 月份无障碍节点未复现。
