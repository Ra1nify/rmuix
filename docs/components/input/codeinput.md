# CodeInput

The ColorOS one-time-code input: `COUICodeInputView` (`coui_phone_code_layout.xml`).

A row of boxed cells with a fully transparent text field layered over them - the field captures the
digits while the cells draw them, which is how stock works (`code_container_edittext` sits over
`code_container_layout` with `android:textColor="@android:color/transparent"`).

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.CodeInput
```

## Basic Usage

```kotlin
var code by remember { mutableStateOf("") }

CodeInput(
    value = code,
    onValueChange = { code = it },
    onCompleted = { completed -> /* submit */ },
)

// 4 cells, dots instead of digits
CodeInput(value = code, onValueChange = { code = it }, count = 4, secure = true)
```

## Properties

| Name         | Type                     | Description                                          | Default |
| :----------- | :----------------------- | :--------------------------------------------------- | :------ |
| value        | String                   | The entered digits                                    | - |
| onValueChange| (String) -> Unit         | Called with the new digits (non-digits are dropped)   | - |
| modifier     | Modifier                 | Modifier applied to the input                         | Modifier |
| count        | Int                      | Number of cells (`couiCodeInputCount`)                | 6 |
| enabled      | Boolean                  | Whether the input accepts edits                       | true |
| secure       | Boolean                  | Draw a dot per digit (`couiEnableSecurityInput`)       | false |
| colors       | CodeInputColors          | Cell colours                                          | CodeInputDefaults.codeInputColors() |
| cellWidth    | Dp                       | Cell width before narrow-screen scaling               | 42dp |
| cellHeight   | Dp                       | Cell height before narrow-screen scaling              | 46dp |
| onCompleted  | ((String) -> Unit)?      | Called with the code once every cell is filled        | null |

## CodeInputDefaults

| Name                 | Type     | Source                                                     | Value |
| :------------------- | :------- | :--------------------------------------------------------- | :---- |
| Count                | Int      | `couiCodeInputCount` attribute default                      | 6 |
| CellWidth            | Dp       | `coui_code_input_cell_width`                                | 42dp |
| CellHeight           | Dp       | `coui_code_input_cell_height`                               | 46dp |
| CellCornerRadius     | Dp       | `?attr/couiRoundCornerS` -> `coui_round_corner_s`            | 8dp |
| CellStrokeWidth      | Dp       | `coui_code_input_cell_stroke_width`                         | 1.6dp |
| CellTextSize         | TextUnit | `coui_code_input_cell_text_size` (declared `30dp`, used as a text size) | 30sp |
| SecurityDotRadius    | Dp       | `coui_code_input_cell_security_circle_radius`               | 5dp |
| CellGap              | Dp       | `coui_code_input_cell_margin_horizontal`                    | 4dp |
| MinCellGap           | Dp       | `coui_code_input_cell_min_margin_horizontal`                | 2dp |
| MaxCellGap           | Dp       | `coui_code_input_cell_max_margin_horizontal`                | 8dp |
| HorizontalMargin     | Dp       | `coui_code_input_layout_margin_start`                       | 16dp |
| FOCUS_APPEAR_MILLIS  | Int      | `COUICodeInputHelper.INBOX_APPEAR_ANIMATOR_DURATION`        | 100 |
| FOCUS_APPEAR_DELAY_MILLIS | Int  | `COUICodeInputHelper.INBOX_DELAY_ANIMATOR_DURATION`         | 33 |
| FOCUS_DISAPPEAR_MILLIS | Int    | `COUICodeInputHelper.INBOX_DELAY_ANIMATOR_DURATION`         | 33 |
| DIGIT_DISAPPEAR_DELAY_MILLIS | Int | `COUICodeInputHelper.NUMBER_DELAY_ANIMATOR_DURATION`      | 33 |
| DIGIT_APPEAR_MILLIS  | Int      | `COUICodeInputHelper.NUMBER_APPEAR_ANIMATOR_DURATION`       | 100 |
| DIGIT_APPEAR_SCALE   | Float    | `COUICodeInputHelper.startNumberScale`                      | 0.6 |

## Colours

| Slot             | Source                                        | Light       | Dark        |
| :--------------- | :-------------------------------------------- | :---------- | :---------- |
| cellColor        | `?attr/couiColorCardBackground`                | `#FFFFFF`   | `#1AFFFFFF` |
| focusStrokeColor | `?attr/couiColorPrimary`                       | theme primary | theme primary |
| digitColor       | `?attr/couiColorPrimaryNeutral`                | `#E6000000` | `#E6FFFFFF` |
| securityDotColor | `coui_code_input_security_circle_color`        | `#D8000000` | `#D8FFFFFF` |

## Geometry

- The cell size shrinks on narrow screens: stock multiplies both dimensions by
  `min(screenWidthDp, 360) / 360` (`COUICodeInputView.setCodeItemWidth`).
- The gap is derived from the available width:
  `round((width - count * cellWidth - 2 * 16dp) / (2 * count - 2))`, clamped to `2dp..8dp`
  (`getCellMarginHorizontal`). Stock puts that value on both `marginStart` and `marginEnd` of every
  interior cell, so the visible gap between two cells is **twice** it.
- The selected cell is the next one to fill; once every cell is filled, the last cell stays selected
  (`updateViewsByCodesChange`).

## Known gaps

- The digit cross-fade matches `COUICodeInputHelper` (`CodeInputDigitTransition`): a digit appears with
  alpha 0..1 + scale 0.6..1 over 100ms; a deleted digit keeps scale 1 and fades out over 100ms after the
  33ms `NUMBER_DELAY_ANIMATOR_DURATION`; replacing a digit redraws instantly. The focus stroke fades over
  100ms and waits 33ms when appearing (`INBOX_DELAY_ANIMATOR_DURATION`). Security cells bypass the
  animators (`setNumber` is guarded by `mIsEnableSecurity`), so their dot shows/hides instantly.
- The `coui_code_input_cell_radius` dimen is not the one stock draws with - `CodeItemView` reads
  `?attr/couiRoundCornerS` instead, which happens to be 8dp as well. This component follows the code.
