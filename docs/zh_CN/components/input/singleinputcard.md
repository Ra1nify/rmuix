# SingleInputCard

ColorOS 卡片式单行输入框：官方 `COUICardSingleInputView`（`com.coui.appcompat.edittext`），设置用于绑定邮箱
（`preference_email_single_edit_layout.xml`、`EmailInputPreferenceCategory`）。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.SingleInputCard
```

## 基本用法

```kotlin
var email by remember { mutableStateOf("") }
var error by remember { mutableStateOf<String?>(null) }
SingleInputCard(
    value = email,
    onValueChange = {
        email = it
        error = null
    },
    title = "邮箱",
    hint = "请输入邮箱地址",
    showCount = true,
    maxCount = 50,
    errorText = error,
    modifier = Modifier.width(328.dp),
)
```

## 参数

| 参数名 | 类型 | 说明 | 默认值 |
| :--- | :--- | :--- | :--- |
| value | String | 文本值 | - |
| onValueChange | (String) -> Unit | 文本变化回调；设置 [maxCount] 时按官方规则截断 | - |
| modifier | Modifier | 应用到视图；设置实际使用宽度 328dp | Modifier |
| title | String? | 输入框上方的可选 16sp 标题 | null |
| hint | String | 值为空时的提示 | "" |
| enabled | Boolean | 是否接受输入 | true |
| password | Boolean | 是否显示密码眼睛按钮（`couiEnablePassword`） | false |
| passwordVisible | Boolean | 密码初始是否可见（官方 `couiPasswordType` 0 为可见） | false |
| showCount | Boolean | 是否显示 `length/maxCount`（`couiEnableInputCount`） | false |
| maxCount | Int | 字符上限（`couiInputMaxCount`）；0 表示不限制 | 0 |
| quickDelete | Boolean | 聚焦且有内容时显示清除按钮（`quickDelete`） | true |
| errorText | String? | 卡片下方错误文案（`showError`） | null |
| colors | SingleInputCardColors | 卡片颜色 | SingleInputCardDefaults.colors() |
| keyboardOptions | KeyboardOptions | 键盘选项 | KeyboardOptions() |
| keyboardActions | KeyboardActions | 键盘动作 | KeyboardActions() |

## SingleInputCardDefaults

| 名称 | 类型 | 来源 | 值 |
| :--- | :--- | :--- | :--- |
| CardCornerRadius | Dp | `coui_round_corner_xl` | 20dp |
| CardHorizontalMargin | Dp | `coui_preference_input_margin_horizontal` | 16dp |
| CardHorizontalMarginTiny | Dp | `coui_preference_input_margin_horizontal_tiny` | 12dp |
| ContentPaddingHorizontal | Dp | `coui_single_input_card_view.xml` paddingStart/End | 32dp |
| TitleTextSize | TextUnit | `COUIInputTitleStyle`（`couiTextAppearanceHeadline6`） | 16sp |
| TitleMinHeight | Dp | `coui_inputView_title_minheight` | 22dp |
| TitlePaddingTop / TitlePaddingBottom | Dp | `coui_input_preference_single_title_padding_top` / `coui_input_preference_title_padding_bottom` | 12dp / 4dp |
| TextSize | TextUnit | `Widget.COUI.Input`（`couiTextBodyL`） | 15sp |
| EditTextPaddingVertical | Dp | `coui_input_edit_text_no_title_padding_top/_bottom` | 15dp |
| EditTextWithTitlePaddingBottom | Dp | `coui_input_preference_single_title_padding_bottom` | 12dp |
| ButtonMarginStart | Dp | `coui_input_button_layout_margin_start` | 8dp |
| DeleteIconSize | Dp | `ic_edit_text_delete` | 22dp |
| PasswordIconSize | Dp | `coui_inputview_password_size` | 28dp |
| PasswordMarginStart | Dp | `coui_card_input_password_margin_start` | 2dp |
| CaptionTextSize | TextUnit | `couiTextCaption` | 10sp |
| ErrorPaddingTop | Dp | `coui_card_single_input_padding_top` | 4dp |
| ErrorPaddingHorizontal | Dp | `coui_single_input_card_view.xml` 错误文案 padding | 32dp |

## 颜色

| 槽位 | 来源 | 浅色 | 深色 |
| :--- | :--- | :--- | :--- |
| cardColor | `coui_color_card_background` | `#FFFFFF` | `#1AFFFFFF` |
| titleColor / textColor | `?attr/couiColorPrimaryNeutral` | `#E6000000` | `#E6FFFFFF` |
| hintColor / countColor | `?attr/couiColorHintNeutral` | `#42000000` | `#4DFFFFFF` |
| errorColor | `coui_color_error` | `#DB382C` | `#FF6C61` |
| iconColor | `?attr/couiColorLabelSecondary` | `#8A000000` | `#8AFFFFFF` |
| disabledTextColor | `?attr/couiColorDisabledNeutral` | `#42000000` | `#4DFFFFFF` |

## 已知差异

- 错误态动画已按 `COUIErrorEditTextHelper` 建模：出现 `errorText` 时提示/标题色在 `DURATION_HINT_ANIMATOR`
  **217ms** 内渐变为错误红（`COUIEaseInterpolator`，cubic-bezier(0.33, 0, 0.67, 1)），聚焦输入框用
  `ShakeInterpolator` 抖动——偏移 `0, -1, 0.5, -0.5, 0` 乘以 `coui_edit_text_shake_amplitude` **5.33dp**，
  按 83/133/117/117ms 分段（总 450ms）。错误文案本身用 `showErrorMsgAnim()` **217ms** 淡入、
  `hideErrorMsgAnim()` **283ms** 淡出（均 `COUIEaseInterpolator`），淡出期间保留文案。仍未建模：
  **描边更正：** 卡片样式 `Widget.COUI.SingleInput` 设置 `couiBackgroundMode=noLine`；`COUIEditText.onDraw()`
  只在 box 模式（`mBoxBackgroundMode == 2`）绘制盒背景，错误态重绘（`drawModeBackgroundRect()` /
  `drawModeBackgroundLine()`）也只在 box/line 模式执行——因此卡片**没有**粗/细聚焦描边，也**没有**错误态背景
  重绘。`coui_input_preference_stroke_width`（0.33dp）与 `coui_textinput_*_stroke_width` 只声明、反编译类
  从未引用（死 token）。仍未建模：133ms/80ms 选区遮罩；错误文案、计数颜色、截断规则与官方一致。
- 密码眼睛使用官方 `coui_btn_eye_on` / `coui_btn_eye_off` 矢量，但未做 animated-selector 过渡。
- `COUIInputView` 的 header 容器（前置文字/图标）与自定义按钮暂未开放。
