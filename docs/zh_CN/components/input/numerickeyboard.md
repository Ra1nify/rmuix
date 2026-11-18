# NumericKeyboard

ColorOS 的 PIN 数字键盘：官方 `COUINumericKeyboard`（`com.coui.appcompat.lockview`）是 3 × 4 的 68dp 键位网格——
1-9、最后一排中间的 `0`、左侧完成键与右侧删除键。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.NumericKeyboard
```

## 基本用法

```kotlin
var pin by remember { mutableStateOf("") }
SimpleLock(filledCount = pin.length)
NumericKeyboard(
    onNumber = { if (pin.length < 4) pin += it },
    onDelete = { pin = pin.dropLast(1) },
    showDelete = pin.isNotEmpty(),
    modifier = Modifier.size(NumericKeyboardDefaults.Width, NumericKeyboardDefaults.Height),
)
```

传入 `onFinish` 时左侧出现完成键：

```kotlin
NumericKeyboard(
    onNumber = { pin += it },
    onDelete = { pin = pin.dropLast(1) },
    onFinish = { submit(pin) },
    finishText = "完成",
)
```

## 参数

| 参数名 | 类型 | 说明 | 默认值 |
| :--- | :--- | :--- | :--- |
| onNumber | (Int) -> Unit | 点击数字键回调（`0..9`） | - |
| modifier | Modifier | 应用到键盘；官方 wrap-content 尺寸为 248dp × 326dp | Modifier |
| enabled | Boolean | 是否响应输入并使用可用态颜色 | true |
| onDelete | (() -> Unit)? | 右侧删除键：删除图标 + 回调 | null（隐藏） |
| showDelete | Boolean | 删除键是否显示（官方调用方用 null 右侧样式切换） | true |
| onFinish | (() -> Unit)? | 左侧完成键：文字 + 回调（`mFinishStyle`） | null（隐藏） |
| finishText | String | 完成键文字 | "OK" |
| colors | NumericKeyboardColors | 数字/按压圆/删除/完成颜色 | NumericKeyboardDefaults.colors() |

## NumericKeyboardDefaults

| 名称 | 类型 | 来源 | 值 |
| :--- | :--- | :--- | :--- |
| Width | Dp | `coui_numeric_keyboard_view_width` | 248dp |
| Height | Dp | `coui_numeric_keyboard_view_height` | 326dp |
| CellSize | Dp | `coui_numeric_keyboard_view_size` | 68dp |
| NumberTextSize | Dp | `number_keyboard_number_size` / `couiNumberTextSize` | 32dp |
| NumberOffsetY | Dp | `coui_numeric_keyboard_number_offset_y` | 1dp |
| FinishTextSize | Dp | `coui_number_keyboard_finish_text_size` | 16dp |
| DeleteIconSize | Dp | `coui_number_keyboard_delete.xml` | 24dp |
| CircleMaxAlpha | Float | `Widget.COUI.COUINumericKeyboard` 的 `couiCircleMaxAlpha` | 0.12 |
| PressDurationMillis | Int | `ALPHA_DELAY` 166 / `ALPHA_DURATION` 167 / `FADE_ANIMATOR_TIME` 160 | 160 |
| ColumnCount / RowCount | Int | `CELL_COLUMN_COUNT` / `CELL_ROW_COUNT` | 3 / 4 |

## 颜色

| 槽位 | 来源 | 浅色 | 深色 |
| :--- | :--- | :--- | :--- |
| numberColor | `?attr/couiColorLabelPrimary` | `#E6000000` | `#E6FFFFFF` |
| pressCircleColor | `couiNumPressColor` 叠加 `couiCircleMaxAlpha` | 12% 标签色 | 12% 标签色 |
| deleteColor | `coui_color_primary_neutral` | `#E6000000` | `#E6FFFFFF` |
| finishColor | `couiWordTextNormalColor` | 标签主色 | 标签主色 |

## 已知差异

- 官方有 12 项字母数组（`coui_number_keyboard_letters`），但 settings dump 中从未绘制（`cellLettersStr` 只赋值不读取），
  因此库内不绘制字母。
- `couiPressEffect=1` 的光效路径（径向渐变、内阴影、边框高光）未建模；库内只画官方默认的波纹圆（effect style 0），
  160ms 淡入淡出。
- 波纹的缩放/模糊弹簧、可变字体轴、触感/振动、`ExploreByTouchHelper` 虚拟无障碍节点未复现。数字字重已用静态 `FontWeight(750)` 近似——`COUINumericKeyboard` 默认请求 `'wght' 550 + font_variation_default_plus 200`；用户的 `font_variation_settings` 偏移与可变 TTF 不可移植。侧键文字在官方用 `setFakeBoldText(true)`，本库保持 `FontWeight.Medium`。
