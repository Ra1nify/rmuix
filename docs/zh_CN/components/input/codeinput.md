# CodeInput

ColorOS 的一次性验证码输入：`COUICodeInputView`（`coui_phone_code_layout.xml`）。

一行方格里叠着一个完全透明的输入框——输入框负责接收数字，方格负责显示，这与官方实现一致
（`code_container_edittext` 覆盖在 `code_container_layout` 上，且 `android:textColor` 为透明）。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.CodeInput
```

## 基本用法

```kotlin
var code by remember { mutableStateOf("") }

CodeInput(
    value = code,
    onValueChange = { code = it },
    onCompleted = { completed -> /* 提交 */ },
)

// 4 格，显示圆点
CodeInput(value = code, onValueChange = { code = it }, count = 4, secure = true)
```

## 参数

| 参数名        | 类型                     | 说明                                    | 默认值 |
| :------------ | :----------------------- | :-------------------------------------- | :----- |
| value         | String                   | 已输入的数字                            | - |
| onValueChange | (String) -> Unit         | 回调新的数字串（非数字会被丢弃）        | - |
| modifier      | Modifier                 | 应用到输入框                            | Modifier |
| count         | Int                      | 格子数量（`couiCodeInputCount`）        | 6 |
| enabled       | Boolean                  | 是否可编辑                              | true |
| secure        | Boolean                  | 每格画圆点（`couiEnableSecurityInput`） | false |
| colors        | CodeInputColors          | 格子颜色                                | CodeInputDefaults.codeInputColors() |
| cellWidth     | Dp                       | 窄屏缩放前的格子宽                      | 42dp |
| cellHeight    | Dp                       | 窄屏缩放前的格子高                      | 46dp |
| onCompleted   | ((String) -> Unit)?      | 填满后回调完整验证码                    | null |

## CodeInputDefaults

| 名称                 | 类型     | 来源                                                    | 值 |
| :------------------- | :------- | :------------------------------------------------------ | :---- |
| Count                | Int      | `couiCodeInputCount` 属性默认值                          | 6 |
| CellWidth            | Dp       | `coui_code_input_cell_width`                            | 42dp |
| CellHeight           | Dp       | `coui_code_input_cell_height`                           | 46dp |
| CellCornerRadius     | Dp       | `?attr/couiRoundCornerS` → `coui_round_corner_s`         | 8dp |
| CellStrokeWidth      | Dp       | `coui_code_input_cell_stroke_width`                     | 1.6dp |
| CellTextSize         | TextUnit | `coui_code_input_cell_text_size`（声明 `30dp`，作文本尺寸用） | 30sp |
| SecurityDotRadius    | Dp       | `coui_code_input_cell_security_circle_radius`           | 5dp |
| CellGap              | Dp       | `coui_code_input_cell_margin_horizontal`                | 4dp |
| MinCellGap           | Dp       | `coui_code_input_cell_min_margin_horizontal`            | 2dp |
| MaxCellGap           | Dp       | `coui_code_input_cell_max_margin_horizontal`            | 8dp |
| HorizontalMargin     | Dp       | `coui_code_input_layout_margin_start`                   | 16dp |
| FOCUS_APPEAR_MILLIS  | Int      | `COUICodeInputHelper.INBOX_APPEAR_ANIMATOR_DURATION`    | 100 |
| FOCUS_APPEAR_DELAY_MILLIS | Int  | `COUICodeInputHelper.INBOX_DELAY_ANIMATOR_DURATION`     | 33 |
| FOCUS_DISAPPEAR_MILLIS | Int    | `COUICodeInputHelper.INBOX_DELAY_ANIMATOR_DURATION`     | 33 |
| DIGIT_DISAPPEAR_DELAY_MILLIS | Int | `COUICodeInputHelper.NUMBER_DELAY_ANIMATOR_DURATION`  | 33 |
| DIGIT_APPEAR_MILLIS  | Int      | `COUICodeInputHelper.NUMBER_APPEAR_ANIMATOR_DURATION`   | 100 |
| DIGIT_APPEAR_SCALE   | Float    | `COUICodeInputHelper.startNumberScale`                  | 0.6 |

## 颜色

| 槽位             | 来源                                          | 浅色        | 深色        |
| :--------------- | :-------------------------------------------- | :---------- | :---------- |
| cellColor        | `?attr/couiColorCardBackground`                | `#FFFFFF`   | `#1AFFFFFF` |
| focusStrokeColor | `?attr/couiColorPrimary`                       | 主题 primary | 主题 primary |
| digitColor       | `?attr/couiColorPrimaryNeutral`                | `#E6000000` | `#E6FFFFFF` |
| securityDotColor | `coui_code_input_security_circle_color`        | `#D8000000` | `#D8FFFFFF` |

## 几何

- 窄屏时格子会缩小：官方把宽高都乘以 `min(screenWidthDp, 360) / 360`
  （`COUICodeInputView.setCodeItemWidth`）。
- 单元格间距按可用宽度计算：
  `round((宽度 - 格数 × 格宽 - 2 × 16dp) / (2 × 格数 - 2))`，再夹到 `2dp..8dp`（`getCellMarginHorizontal`）。官方给每个中间单元格同时设置 `marginStart` 与 `marginEnd`，所以两格之间的可见间距是它的 **2 倍**。
- 选中的格子是「下一个待填」；全部填满后停在最后一格（`updateViewsByCodesChange`）。

## 已知差异

- 数字交叉动画已按 `COUICodeInputHelper` 复刻（`CodeInputDigitTransition`）：新数字 alpha 0→1、scale 0.6→1（100ms）；
  删除的数字保持 scale 1，等待 33ms（`NUMBER_DELAY_ANIMATOR_DURATION`）后用 100ms 淡出；替换数字直接重绘。
  聚焦描边 100ms 淡入淡出，出现时等待 33ms（`INBOX_DELAY_ANIMATOR_DURATION`）。安全输入格因 `mIsEnableSecurity`
  跳过动画，圆点即时显示/隐藏。
- `coui_code_input_cell_radius` 这个 dimen **不是**官方绘制用的那个——`CodeItemView` 读的是
  `?attr/couiRoundCornerS`（恰好也是 8dp）。本组件按代码实现。
