# NumberPicker

`NumberPicker` 是 Rmuix 中的基础交互组件，用于通过垂直滚动从一组数字中选择值。选中项居中并高亮显示，周围的项目逐渐淡出并缩小。通过 `wrapAround` 参数支持无限循环滚动。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=numberPicker" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `com.coui.appcompat.picker.COUINumberPicker`、`couiPickerBackgroundColor` 属性与 `coui_number_picker_*` / `coui_selected_*` 尺寸。

- 官方选择器用两条细线框住选中行（`onDraw` 在选区上下各画一个 `mBackgroundDividerHeight` 的矩形），库内完全没有这个框。
- 细线高度是 `coui_number_picker_background_divider_height` = **0.33dp**，控件里用 `Math.max(dimen, 1)` 保证至少 1 像素。
- 细线颜色 `couiPickerBackgroundColor` → `?attr/couiColorDivider`（`#1F000000` / `#33FFFFFF`）。
- 细线左右各缩进 `coui_selected_background_horizontal_padding` = **16dp**，并且距选择器中心 `coui_selected_background_radius` = **24dp**（两条线相距 48dp，而不是 45dp 的行边界）。
- `NumberPickerDefaults` 新增 `DividerHeight`、`DividerInset` 与 `DividerOffset`；线画在文字之下，滚轮滚动时保持不动。

### 行度量与文字（续核）

- `couiPickerRowNumber` = **3** 行；组件原来默认 5 行。默认值现为 `NumberPickerStockRowCount`（3）。
- `coui_datepicker_height` / `coui_timepicker_height` / `coui_time_picker_height` = **135dp** = 3 行 × **45dp**；`coui_number_picker_view_padding_vertical`（12dp）由父布局加在选择器**外侧**，因此 `ItemHeight` 为 45dp。
- `focusTextSize` = `coui_numberpicker_textSize_big` = **20sp** 只作用于居中行；`startTextSize` = `coui_numberpicker_textSize_small` = **16sp** 是其余行的字号（`COUINumberPicker` 先按 `mNormalTextSize` 画全部行，再裁剪 ±`mBackgroundRadius` 的条带用 `mFocusTextSize` 重画，条带只覆盖居中行）。组件此前用主题 `title1`（32sp）画所有行并按距离插值；现在非居中行固定 16sp。
- 样式里还声明了 `endTextSize`（14dp），但 `COUINumberPicker` 从未读取该属性，因此未建模。
- `couiFocusTextColor` → `couiColorPrimaryNeutral`（**#E6000000** / **#E6FFFFFF**）、`couiNormalTextColor` → `couiColorHintNeutral`（**#42000000** / **#4DFFFFFF**），取代原来的主题 `onSurface` / `onSurfaceSecondary`（80% 中性色）。

**BREAKING：** 默认选择器由「5 行 × 45dp、32sp、主题中性色」改为「3 行 × 45dp（选择器 135dp）、20sp、#E6/#42 中性色」。

多列布局由 `NumberPickerGroup` 提供（见下文）；列宽由调用方按原厂 token 指定（`coui_number_picker_width_small` 62dp、`coui_numberpicker_width_middle` 66dp、`coui_numberpicker_width_big` 76dp），不是由容器自动套用。**更正：** 单位标签 token 已应用——`unitLabel` 以 14sp 绘制（`coui_numberpicker_unit_textSize`），最小宽度 `coui_number_picker_unit_min_width` 21dp，距数值 `coui_number_picker_text_margin_start` 4dp，底部偏移 `coui_numberpicker_unit_margin_bottom` 2dp（见 `NumberPickerDefaults.UnitTextSize` / `UnitMinWidth`）。**更正：** `couiPickerTouchEffectInterval`（style 设为 50，属性默认 100）在 `COUINumberPicker` 里**从未被读取**——只有 getter/setter 访问 `mTouchEffectInterval`，因此不存在需要对齐的触感节奏。该控件在每次值真正变化时调用 `performFeedback()`：`performHapticFeedback(308)`（厂商 effect，失败回退 302），本库映射为每次行切换一次 `HapticFeedbackType.TextHandleMove`；这些厂商 effect id 无法通过 Compose 触感 API 复现。同一路径还会通过 `COUISoundLoadUtil` 播放 `R.raw.coui_numberpicker_click`，该厂商音频资源未移植（资源授权 + 缺少跨平台声音 API），作为已知差距记录。`numberTextSize` 与 `endTextSize` 同样声明但从未被读取（`mFocusTextSize` 来自 `focusTextSize`，`mNormalTextSize` 来自 `startTextSize`）。
**字号 token 以 `dp` 声明、本库按 `sp` 渲染。** dump 里这些 `*_text_size` 都是以 `dp` 声明的（COUI 用 `getDimensionPixelSize` 读取并以 `COMPLEX_UNIT_PX` 应用，所以实际字高随密度变化）。本库在同一数值上
映射为与密度无关的 `sp`，即 1x 下的等效值。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.NumberPicker
```

## 基本用法

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..10
)
```

## 组件状态

### 禁用状态

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..10,
    enabled = false
)
```

### 无限滚动

启用 `wrapAround` 后，选择器会从最后一项循环回到第一项，实现连续滚动。

```kotlin
var value by remember { mutableIntStateOf(0) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..23,
    wrapAround = true
)
```

## 属性

### NumberPicker 属性

| 属性名           | 类型               | 说明                                                 | 默认值                           | 是否必须 |
| ---------------- | ------------------ | ---------------------------------------------------- | -------------------------------- | -------- |
| value            | Int                | 当前选中的值，超出 range 时会自动修正                | -                                | 是       |
| onValueChange    | (Int) -> Unit      | 选中值变化时的回调函数                               | -                                | 是       |
| modifier         | Modifier           | 应用于选择器的修饰符                                 | Modifier                         | 否       |
| enabled          | Boolean            | 是否启用用户交互                                     | true                             | 否       |
| range            | IntRange           | 可选择的值范围                                       | 0..10                            | 否       |
| label            | (Int) -> String    | 将值转换为显示字符串的函数                           | { it.toString() }                | 否       |
| visibleItemCount | Int                | 可见项数。必须为奇数且不小于 3（`couiPickerRowNumber` = 3） | NumberPickerStockRowCount | 否 |
| wrapAround       | Boolean            | 是否启用循环滚动（无限滚动）                         | false                            | 否       |
| colors           | NumberPickerColors | 选择器的颜色配置                                     | NumberPickerDefaults.colors()    | 否       |
| textStyle        | TextStyle          | 选择器项目的文本样式                                 | RmuixTheme.textStyles.title1     | 否       |
| itemHeight       | Dp                 | 每个项目的高度                                       | NumberPickerDefaults.ItemHeight   | 否       |
| unitLabel        | String             | 选中值旁显示的单位 | "" | 否 |
| showSelectionLines | Boolean          | 是否由本选择器绘制两条选择线；`NumberPickerGroup` 置 false，由整行统一绘制 | true | 否 |

### NumberPickerDefaults 对象

NumberPickerDefaults 对象提供了 NumberPicker 组件的默认配置。

#### 属性

| 属性名     | 类型 | 说明                 | 默认值  |
| ---------- | ---- | -------------------- | ------- |
| ItemHeight | Dp   | 每个项目的默认高度（135dp 选择器 ÷ 3 行；12dp `coui_number_picker_view_padding_vertical` 在选择器外侧） | 45.dp |
| VerticalPadding | Dp | 行上下内边距（coui_number_picker_view_padding_vertical） | 12.dp |
| unitLabel         | String                    | 选中值右侧的单位标签：`coui_numberpicker_unit_textSize` 14sp、左侧 4dp、底部 2dp | "" |
| FocusTextSize | TextUnit | 居中行字号（`focusTextSize` → `coui_numberpicker_textSize_big`） | 20.sp |
| NormalTextSize | TextUnit | 其余行字号（`startTextSize` → `coui_numberpicker_textSize_small`） | 16.sp |
| DividerHeight | Dp | 选中框细线高度（coui_number_picker_background_divider_height） | 0.33.dp |
| DividerInset | Dp | 细线左右缩进（coui_selected_background_horizontal_padding） | 16.dp |
| DividerOffset | Dp | 细线距选择器中心的距离（`coui_selected_background_radius`） | 24.dp |

#### 方法

| 方法名   | 返回类型           | 说明                       |
| -------- | ------------------ | -------------------------- |
| colors() | NumberPickerColors | 创建默认的颜色配置         |

### NumberPickerColors 类

| 属性名                      | 类型  | 说明                       |
| --------------------------- | ----- | -------------------------- |
| selectedTextColor           | Color | 选中项（居中）的文字颜色   |
| unselectedTextColor         | Color | 未选中项的文字颜色         |
| disabledSelectedTextColor   | Color | 禁用时选中项的文字颜色     |
| disabledUnselectedTextColor | Color | 禁用时未选中项的文字颜色   |

## 进阶用法

### 自定义标签格式

```kotlin
var hour by remember { mutableIntStateOf(9) }

NumberPicker(
    value = hour,
    onValueChange = { hour = it },
    range = 0..23,
    label = { it.toString().padStart(2, '0') }  // "00", "01", ... "23"
)
```

### 时间选择器

组合两个 NumberPicker 创建时间选择器。

```kotlin
var hour by remember { mutableIntStateOf(16) }
var minute by remember { mutableIntStateOf(30) }

Row(
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
) {
    NumberPicker(
        value = hour,
        onValueChange = { hour = it },
        range = 0..23,
        label = { it.toString().padStart(2, '0') },
        wrapAround = true,
        modifier = Modifier.weight(1f),
    )
    Text(
        text = ":",
        fontWeight = FontWeight.Bold,
    )
    NumberPicker(
        value = minute,
        onValueChange = { minute = it },
        range = 0..59,
        label = { it.toString().padStart(2, '0') },
        wrapAround = true,
        modifier = Modifier.weight(1f),
    )
}
```

### 自定义可见项数

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 1..100,
    visibleItemCount = 3
)
```

### 自定义颜色

```kotlin
var value by remember { mutableIntStateOf(5) }

NumberPicker(
    value = value,
    onValueChange = { value = it },
    range = 0..10,
    colors = NumberPickerDefaults.colors(
        selectedTextColor = Color.Red,
        unselectedTextColor = Color.Gray
    )
)
```

## 多列选择器

`NumberPickerGroup` 把多个选择器排成一行，对应官方的日期/时间选择器（`coui_date_picker.xml`）：
各列居中、行顺序**强制 LTR**（RTL 语言下也不镜像），并且各列不再自行绘制选择线 —— 由整行统一画一对，
让线穿过列与列之间的空隙。

```kotlin
NumberPickerGroup {
    NumberPicker(value = year, onValueChange = { year = it }, range = 1970..2100, modifier = Modifier.width(96.dp))
    NumberPicker(value = month, onValueChange = { month = it }, range = 1..12, modifier = Modifier.width(96.dp))
    NumberPicker(value = day, onValueChange = { day = it }, range = 1..31, modifier = Modifier.width(62.dp))
}
```

列宽由调用方给出：`coui_datepicker_width_year` / `_month` 为 96dp，`coui_number_picker_width_small`
（日、时、分）为 62dp。
