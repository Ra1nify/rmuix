# SliderPreference

`SliderPreference` 是 Rmuix 中的滑块偏好设置组件，将标题/摘要与滑块控件结合在一起。滑块放置在 `BasicComponent` 的底部操作区域中，适用于设置界面中需要调整数值的场景，如音量、亮度或字体大小控制。

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `res/layout/coui_preference_widget_select.xml` 与 `res/layout/coui_preference_widget_jump.xml`（滑块行在可点击时同时复用这两个控件：状态文字 + 箭头）。

- `valueText` 即官方状态文字：原实现用 `body2`（14sp，数值恰好一致）、`onSurfaceVariantActions`（40% 中性色）且没有宽度上限。现改为 `BasicComponentDefaults.StatusTextSize`（14sp）、`summaryColor`（次级文字色），并加 162dp 宽度上限与 24dp 起始留白。
- 箭头改用官方 `support_preference_image_margin_start`（4dp）起始边距，取代原本没有出处的行级 `padding(end = 8.dp)`。
- 单选与区间两个变体都已修改。

**BREAKING：** 值文本改为次级文字色并限制 162dp；箭头前间距由 8dp 改为 4dp。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.preference.SliderPreference
```

## 基本用法

```kotlin
var sliderValue by remember { mutableFloatStateOf(0.5f) }

SliderPreference(
    value = sliderValue,
    onValueChange = { sliderValue = it },
    title = "音量"
)
```

## 带摘要

```kotlin
var brightness by remember { mutableFloatStateOf(0.8f) }

SliderPreference(
    value = brightness,
    onValueChange = { brightness = it },
    title = "亮度",
    summary = "调整屏幕亮度级别"
)
```

## 组件状态

### 禁用状态

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

SliderPreference(
    value = value,
    onValueChange = { value = it },
    title = "禁用的滑块",
    summary = "此滑块当前不可用",
    enabled = false
)
```

## 属性

### SliderPreference 属性

| 属性名                | 类型                              | 说明                                                                                                             | 默认值                             | 是否必须 |
| --------------------- | --------------------------------- | ---------------------------------------------------------------------------------------------------------------- | ---------------------------------- | -------- |
| value                 | Float                             | 当前滑块的值。如果超出 valueRange，值将被强制限制在该范围内                                                      | -                                  | 是       |
| onValueChange         | (Float) -> Unit                   | 值变化时的回调函数                                                                                               | -                                  | 是       |
| title                 | String?                           | 偏好设置的标题                                                                                                   | null                               | 否       |
| modifier              | Modifier                          | 应用于组件的修饰符                                                                                               | Modifier                           | 否       |
| titleColor            | BasicComponentColors              | 标题文本的颜色配置                                                                                               | BasicComponentDefaults.titleColor() | 否       |
| summary               | String?                           | 摘要说明                                                                                                         | null                               | 否       |
| summaryColor          | BasicComponentColors              | 摘要文本的颜色配置                                                                                               | BasicComponentDefaults.summaryColor() | 否       |
| startAction           | @Composable (() -> Unit)?         | 左侧自定义内容                                                                                                   | null                               | 否       |
| valueText             | String?                           | 当前滑块值文本，以摘要样式显示在末尾区域。在 Row 布局中居中垂直对齐并使用权重                                     | null                               | 否       |
| endActions            | @Composable (RowScope.() -> Unit)?| 右侧自定义内容，紧跟 valueText 在同一 Row 中渲染                                                                 | null                               | 否       |
| bottomAction          | @Composable (() -> Unit)?         | 底部区域顶部的自定义内容，位于滑块上方                                                                           | null                               | 否       |
| onClick               | (() -> Unit)?                     | 点击时触发的回调。当非 null 时，末尾区域显示箭头图标                                                              | null                               | 否       |
| holdDownState         | Boolean                           | 组件是否处于按下状态                                                                                             | false                              | 否       |
| enabled               | Boolean                           | 是否启用偏好设置                                                                                                 | true                               | 否       |
| valueRange            | ClosedFloatingPointRange\<Float\> | 滑块可以采用的值范围。传递的值将被强制限制在此范围内                                                             | 0f..1f                             | 否       |
| steps                 | Int                               | 离散值的数量。如果为 0，滑块将连续运行。必须非负                                                                 | 0                                  | 否       |
| onValueChangeFinished | (() -> Unit)?                     | 值变化结束时调用                                                                                                 | null                               | 否       |
| reverseDirection      | Boolean                           | 控制滑块的方向。默认 false，方向跟随 LayoutDirection (LTR 时从左到右，RTL 时从右到左)。为 true 时反转方向。       | false                              | 否       |
| sliderHeight          | Dp                                | 滑块的高度                                                                                                       | SliderDefaults.MinHeight           | 否       |
| sliderColors          | SliderColors                      | 滑块的颜色配置                                                                                                   | SliderDefaults.sliderColors()      | 否       |
| hapticEffect          | SliderDefaults.SliderHapticEffect | 滑块的触感反馈类型                                                                                               | SliderDefaults.DefaultHapticEffect | 否       |
| showKeyPoints         | Boolean                           | 是否显示关键点指示器。仅当 keyPoints 不为 null 时有效                                                            | false                              | 否       |
| keyPoints             | List\<Float\>?                    | 要在滑块上显示的自定义关键点值。如果为 null，则使用 steps 参数的步长位置。值应在 valueRange 范围内               | null                               | 否       |
| magnetThreshold       | Float                             | 磁吸对齐阈值，以分数表示 (0.0 到 1.0)。当滑块值与关键点的距离在此阈值内时，将对齐到该点。仅在设置 keyPoints 时生效 | 0.02f                              | 否       |
| startLabel            | String?                           | 滑杆左侧的标尺文字，垂直居中（seek_bar_preference.xml 的 left_tip） | null | No |
| endLabel              | String?                           | 滑杆右侧的标尺文字（right_tip） | null | No |
| labelColor            | BasicComponentColors              | 标尺文字颜色（section_seekbar_text_color） | SliderPreferenceDefaults.labelColor() | No |
| insideMargin          | PaddingValues                     | 组件内部内容的边距                                                                                               | BasicComponentDefaults.InsideMargin | 否       |

## 进阶用法

### 自定义数值范围与步长

```kotlin
var temperature by remember { mutableFloatStateOf(25f) }

SliderPreference(
    value = temperature,
    onValueChange = { temperature = it },
    title = "温度",
    summary = "当前: ${temperature.roundToInt()}°C",
    valueRange = 16f..32f,
    steps = 15,
    hapticEffect = SliderDefaults.SliderHapticEffect.Step
)
```

### 带左侧图标

```kotlin
var volume by remember { mutableFloatStateOf(0.7f) }

SliderPreference(
    value = volume,
    onValueChange = { volume = it },
    title = "音量",
    summary = "${(volume * 100).roundToInt()}%",
    startAction = {
        Icon(
            imageVector = RmuixIcons.Basic.Audio,
            contentDescription = "音量图标",
            tint = RmuixTheme.colorScheme.onBackground,
            modifier = Modifier.padding(end = 16.dp)
        )
    },
    valueRange = 0f..1f
)
```

### 自定义关键点

```kotlin
var fontSize by remember { mutableFloatStateOf(14f) }

SliderPreference(
    value = fontSize,
    onValueChange = { fontSize = it },
    title = "字体大小",
    summary = "当前: ${fontSize.roundToInt()}sp",
    valueRange = 10f..24f,
    showKeyPoints = true,
    keyPoints = listOf(10f, 12f, 14f, 16f, 18f, 20f, 24f),
    magnetThreshold = 0.05f,
    hapticEffect = SliderDefaults.SliderHapticEffect.Step
)
```


## 同一张卡里的多行

`insideMargin` 默认是 `BasicComponentDefaults.InsideMargin` = `PaddingValues(horizontal = 32.dp, vertical = 10.dp)`，
即 `seek_bar_preference.xml` 行使用的官方 `support_preference_title_padding_start` / `_end`（32dp）。
同一张卡里的所有 `SliderPreference` 请使用**同一个** `insideMargin`：混用会让较窄那行的轨道两端看起来被缩进
（16dp 的行比 32dp 的行宽 32dp）。

## SliderPreferenceDefaults

标尺数据来自 `seek_bar_preference.xml`。

| Name           | Type     | 说明 | Value |
| :------------- | :------- | :---------- | :---- |
| LabelTextSize  | TextUnit | `text_descripe_seekbar_value` | 14sp |
| LabelPadding   | Dp       | `text_descripe_seekbar_padding`：左侧文字的 `paddingEnd` / 右侧文字的 `paddingStart` | 10dp |
| labelColor()   | BasicComponentColors | `section_seekbar_text_color` → `coui_color_secondary_neutral` | `#8A000000` / `#8AFFFFFF` |
