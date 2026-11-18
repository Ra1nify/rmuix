# NumericKeyboard

The ColorOS PIN keypad: `COUINumericKeyboard` (`com.coui.appcompat.lockview`) is a 3 × 4 grid of 68dp keys —
digits 1-9, `0` on the last row's middle key, a leading finish key and a trailing delete key.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.NumericKeyboard
```

## Basic Usage

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

The leading side key becomes a finish action when `onFinish` is provided:

```kotlin
NumericKeyboard(
    onNumber = { pin += it },
    onDelete = { pin = pin.dropLast(1) },
    onFinish = { submit(pin) },
    finishText = "OK",
)
```

## Properties

| Name | Type | Description | Default |
| :--- | :--- | :--- | :--- |
| onNumber | (Int) -> Unit | Called with the digit (`0..9`) of the tapped key | - |
| modifier | Modifier | Applied to the keypad; the stock wrap-content size is 248dp × 326dp | Modifier |
| enabled | Boolean | Whether the keypad reacts and draws the enabled colours | true |
| onDelete | (() -> Unit)? | Trailing side key: delete vector + callback | null (hidden) |
| showDelete | Boolean | Whether the delete key is visible (stock callers set the right style to null) | true |
| onFinish | (() -> Unit)? | Leading side key: finish label + callback (`mFinishStyle`) | null (hidden) |
| finishText | String | Finish label text | "OK" |
| colors | NumericKeyboardColors | Digit/ripple/delete/finish colours | NumericKeyboardDefaults.colors() |

## NumericKeyboardDefaults

| Name | Type | Source | Value |
| :--- | :--- | :--- | :--- |
| Width | Dp | `coui_numeric_keyboard_view_width` | 248dp |
| Height | Dp | `coui_numeric_keyboard_view_height` | 326dp |
| CellSize | Dp | `coui_numeric_keyboard_view_size` | 68dp |
| NumberTextSize | Dp | `number_keyboard_number_size` / `couiNumberTextSize` | 32dp |
| NumberOffsetY | Dp | `coui_numeric_keyboard_number_offset_y` | 1dp |
| FinishTextSize | Dp | `coui_number_keyboard_finish_text_size` | 16dp |
| DeleteIconSize | Dp | `coui_number_keyboard_delete.xml` | 24dp |
| CircleMaxAlpha | Float | `couiCircleMaxAlpha` in `Widget.COUI.COUINumericKeyboard` | 0.12 |
| PressDurationMillis | Int | `ALPHA_DELAY` 166 / `ALPHA_DURATION` 167 / `FADE_ANIMATOR_TIME` 160 | 160 |
| ColumnCount / RowCount | Int | `CELL_COLUMN_COUNT` / `CELL_ROW_COUNT` | 3 / 4 |

## Colours

| Slot | Source | Light | Dark |
| :--- | :--- | :--- | :--- |
| numberColor | `?attr/couiColorLabelPrimary` | `#E6000000` | `#E6FFFFFF` |
| pressCircleColor | `couiCircleMaxAlpha` over `couiNumPressColor` | 12% label | 12% label |
| deleteColor | `coui_color_primary_neutral` | `#E6000000` | `#E6FFFFFF` |
| finishColor | `couiWordTextNormalColor` | label primary | label primary |

## Known gaps

- `COUINumericKeyboard` exposes a 12-item letter array (`coui_number_keyboard_letters`), but in the settings dump
  the letters are never painted (`cellLettersStr` is assigned and unused), so this library does not draw them.
- The `couiPressEffect=1` light-effect path (radial gradient, inner shadow, border highlight) is not modelled;
  the library draws the stock ripple circle (effect style 0) with a 160ms fade.
- The ripple's scale/blur springs, the variable-font axis, tactile feedback/vibration and the
  `ExploreByTouchHelper` virtual accessibility nodes are not reproduced. The digit weight is modelled as a
  static `FontWeight(750)` - `COUINumericKeyboard` requests `'wght' 550 + font_variation_default_plus 200`
  by default; the user's `font_variation_settings` offset and the variable TTF are not portable.
  Side-key words are painted with `setFakeBoldText(true)` in the stock; they stay `FontWeight.Medium` here.
