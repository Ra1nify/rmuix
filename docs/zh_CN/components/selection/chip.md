#! /usr/bin/env markdown

# Chip

`Chip` 是 Rmuix 里的胶囊标签，对应 ColorOS 的 `COUIChip`：单行、紧凑，可**选中**以表示当前筛选或选择。
它是普通芯片；标签页形态的芯片族见 [`ChipTabRow`](../navigation/chiptabrow)。

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=chip" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 原厂 ColorOS 静态复核（2026-11-14）

本组件移植 Settings APK `styles.xml` 里的 `Widget.COUI.Chip`，以及 `COUIChip` / `COUIChipDrawable`。
已核实的数字（见 `docs/dev/coloros-chip.md`）：

- `coui_chip_default_min_height` = 28dp
- `coui_chip_selection_style_height` = 32dp
- `coui_chip_default_min_width` = 52dp
- `coui_chip_default_max_width` = 300dp
- `coui_chip_default_max_text_width` = 200dp
- `coui_chip_selection_style_chip_horizontal_padding` = 12dp
- `coui_chip_group_default_spacing_horizontal` = 8dp
- `coui_chip_group_default_spacing_vertical` = 8dp

基样式是胶囊（`chipCornerRadius` = -1dp），文本 12sp（`?attr/couiTextBodyXS`）；选择样式高度 32dp、文本 14sp
（`?attr/couiTextBodyM`）。未选中底色 `?attr/couiColorContainer8`（`#14000000` / `#1AFFFFFF`），
选中底色 `?attr/couiColorPrimary`，选中文字 `?attr/couiColorLabelOnColor`（`#FFFFFF`）。

`.Suggestion` 还设置了 `closeIcon`（`@drawable/ic_chip_close`）：**12dp** 图形
（`coui_chip_suggest_style_close_icon_size`）、左右各 **8dp**（`coui_chip_suggest_style_close_icon_start_padding` /
`_end_padding`）、触摸范围 **24dp**（`coui_chip_suggest_style_close_icon_touch_bounds_size`），颜色
`?attr/couiColorSecondNeutral`（`#8A000000` / `#8AFFFFFF`）。官方默认显示；这里改成 `onClose` 可选，
避免出现没有回调的按钮。

### 前置图标（2026-11-14）

前置图标通过 `icon` 建模，几何直接来自 `COUIChipDrawable`：`calculateChipIconBounds()` 把图标放在
`chipStartPadding + chipIconStartPadding` 处，宽度 `chipIconSize`，与文字之间隔 `chipIconEndPadding`。

| 样式 | `chipIconStartPadding` | `chipIconSize` | `chipIconEndPadding` | 距芯片左边缘 |
| :--- | ---: | ---: | ---: | ---: |
| `Suggestion` | 12dp | 14dp | 4dp | 24dp |
| `Selection` | 0dp | 16dp | 4dp | 12dp |

这也解释了早先「24dp 内缩 vs 28dp 芯片」的疑问：折叠芯片用的不是 `.Suggestion`，而是
`Widget.COUI.Chip.Suggestion.Collapsable`（`chipStartPadding` 0dp、`chipIconStartPadding` 6dp、
`chipIconSize` 16dp、`chipIconEndPadding` 6dp，正好 28dp）。

**更正：** `Widget.COUI.Chip.Choice` / `Widget.COUI.Chip.Record` 是 `.Selection` / `.Suggestion` 的**空别名**
（`styles.xml` 中没有任何额外 item），已由 `Chip(style = ...)` 覆盖；`icon` 插槽已建模
`chipIconSize` / `chipIconStartPadding` / `chipIconEndPadding`，并通过 `LocalContentColor` 给图标着色
（`chipIconApplyTint`）。`Widget.COUI.Chip.ChipTab` + `layout_chip_tab.xml` / `COUIChipTabGroup` 虽在 APK 中，
但 **Settings 没有任何代码 inflate 或实例化它们**（仅 `R.java` 引用），`COUIChipGroupEditText`（输入 chip）
也没有 Settings 调用点——两者均未建模。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Chip
import io.github.ra1nify.rmuix.kmp.basic.ChipDefaults
import io.github.ra1nify.rmuix.kmp.basic.ChipStyle
```

## 基本用法

```kotlin
var picked by remember { mutableStateOf(false) }

Chip(
    onClick = { picked = !picked },
    label = "Suggested",
    selected = picked,
)
```

多个芯片按官方组间距排列：

```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing)) {
    Chip(onClick = {}, label = "全部", selected = true)
    Chip(onClick = {}, label = "照片")
    Chip(onClick = {}, label = "视频")
}
```

## 可关闭芯片

传入 `onClose` 即在尾部显示原厂 `ic_chip_close` 图形：

```kotlin
Chip(
    onClick = {},
    label = "WLAN",
    onClose = { /* 移除该筛选 */ },
)
```

## 选择样式

```kotlin
Chip(
    onClick = {},
    label = "Selection",
    selected = true,
    style = ChipStyle.Selection,
)
```

## 语义

`Widget.COUI.Chip` 设 `android:checkable="false"`，`.Selection` 才改成 `true`，所以无障碍角色跟着样式走：
`ChipStyle.Suggestion` 是普通按钮（`Role.Button`），`ChipStyle.Selection` 可选中
（`Role.Checkbox`，`selected` 作为选中态暴露）。

## Chip

```kotlin
@Composable
fun Chip(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    style: ChipStyle = ChipStyle.Suggestion,
    colors: ChipColors = ChipDefaults.chipColors(),
)
```

| 参数名 | 类型      | 说明                       | 默认值                   | 必填 |
| ------ | --------- | -------------------------- | ------------------------ | ---- |
| onClick | () -> Unit | 点击芯片时回调            | -                        | 是   |
| label  | String    | 芯片文本                   | -                        | 是   |
| modifier | Modifier | 应用于芯片的修饰符         | Modifier                 | 否   |
| selected | Boolean  | 是否选中                   | false                    | 否   |
| enabled | Boolean   | 是否可交互                 | true                     | 否   |
| style  | ChipStyle | 高度与文本尺寸             | ChipStyle.Suggestion     | 否   |
| colors | ChipColors | 底色与文字颜色            | ChipDefaults.chipColors() | 否  |
| onClose | (() -> Unit)? | 非空时显示原厂关闭按钮  | null                      | 否  |
| icon | (@Composable () -> Unit)? | 可选前置图标，使用芯片内容色着色 | null | 否 |
| borderWidth     | Dp      | 可选轮廓的线宽（仅当指定了边框色时绘制）                                                             | ChipDefaults.BorderWidth（1dp） | No |

## ChipDefaults

| 属性名              | 类型   | 说明                       | 默认值 |
| ------------------- | ------ | -------------------------- | ------ |
| MinHeight           | Dp     | 建议样式高度               | 28.dp  |
| BorderWidth         | Dp     | 可选轮廓线宽（非 COUI token，官方 chip 无边框） | 1.dp |
| SelectionHeight     | Dp     | 选择样式高度               | 32.dp  |
| MinWidth            | Dp     | 最小宽度                   | 52.dp  |
| MaxWidth            | Dp     | 最大宽度                   | 300.dp |
| MaxTextWidth        | Dp     | 文本宽度上限               | 200.dp |
| HorizontalPadding   | Dp     | 文本左右内边距             | 12.dp  |
| GroupSpacing        | Dp     | 芯片之间的间距             | 8.dp   |
| SuggestionTextSize  | TextUnit | 建议样式文本尺寸         | 12.sp  |
| CloseIconSize       | Dp     | 关闭图形尺寸               | 12.dp  |
| CloseIconStartPadding | Dp   | 文本与关闭图形之间的间距   | 8.dp   |
| CloseIconEndPadding | Dp     | 关闭图形之后的间距         | 8.dp   |
| CloseIconTouchBounds | Dp    | 关闭图形触摸范围           | 24.dp  |
| SuggestionIconSize  | Dp       | `coui_chip_suggest_style_chip_icon_size`            | 14.dp         |
| SelectionIconSize   | Dp       | `coui_chip_selection_style_chip_icon_size`           | 16.dp         |
| IconEndPadding      | Dp       | `coui_chip_*_style_chip_icon_end_padding`            | 4.dp          |
| SuggestionIconStartPadding | Dp | `coui_chip_suggest_style_chip_icon_start_padding`   | 12.dp         |
| SelectionIconStartPadding  | Dp | `coui_chip_selection_style_chip_icon_start_padding` | 0.dp          |
| SelectionTextSize   | TextUnit | 选择样式文本尺寸         | 14.sp  |
