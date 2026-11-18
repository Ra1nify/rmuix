# SingleInputCard

The ColorOS card single input: `COUICardSingleInputView` (`com.coui.appcompat.edittext`), used by Settings for
email binding (`preference_email_single_edit_layout.xml`, `EmailInputPreferenceCategory`).

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.SingleInputCard
```

## Basic Usage

```kotlin
var email by remember { mutableStateOf("") }
var error by remember { mutableStateOf<String?>(null) }
SingleInputCard(
    value = email,
    onValueChange = {
        email = it
        error = null
    },
    title = "Email",
    hint = "Enter email address",
    showCount = true,
    maxCount = 50,
    errorText = error,
    modifier = Modifier.width(328.dp),
)
```

## Properties

| Name | Type | Description | Default |
| :--- | :--- | :--- | :--- |
| value | String | The text value | - |
| onValueChange | (String) -> Unit | Called with the new value, truncated to [maxCount] when set | - |
| modifier | Modifier | Applied to the view; the stock Settings width is 328dp | Modifier |
| title | String? | Optional 16sp title above the text field | null |
| hint | String | Hint shown while the value is empty | "" |
| enabled | Boolean | Whether the card accepts input | true |
| password | Boolean | Enables the eye toggle (`couiEnablePassword`) | false |
| passwordVisible | Boolean | Whether the password starts visible (stock `couiPasswordType` 0 shows it) | false |
| showCount | Boolean | Shows `length/maxCount` (`couiEnableInputCount`) | false |
| maxCount | Int | Character limit (`couiInputMaxCount`); 0 disables truncation | 0 |
| quickDelete | Boolean | Shows the clear button while focused and non-empty (`quickDelete`) | true |
| errorText | String? | Error message below the card (`showError`) | null |
| colors | SingleInputCardColors | Card colours | SingleInputCardDefaults.colors() |
| keyboardOptions | KeyboardOptions | Keyboard options | KeyboardOptions() |
| keyboardActions | KeyboardActions | Keyboard actions | KeyboardActions() |

## SingleInputCardDefaults

| Name | Type | Source | Value |
| :--- | :--- | :--- | :--- |
| CardCornerRadius | Dp | `coui_round_corner_xl` | 20dp |
| CardHorizontalMargin | Dp | `coui_preference_input_margin_horizontal` | 16dp |
| CardHorizontalMarginTiny | Dp | `coui_preference_input_margin_horizontal_tiny` | 12dp |
| ContentPaddingHorizontal | Dp | `coui_single_input_card_view.xml` paddingStart/End | 32dp |
| TitleTextSize | TextUnit | `COUIInputTitleStyle` (`couiTextAppearanceHeadline6`) | 16sp |
| TitleMinHeight | Dp | `coui_inputView_title_minheight` | 22dp |
| TitlePaddingTop / TitlePaddingBottom | Dp | `coui_input_preference_single_title_padding_top` / `coui_input_preference_title_padding_bottom` | 12dp / 4dp |
| TextSize | TextUnit | `Widget.COUI.Input` (`couiTextBodyL`) | 15sp |
| EditTextPaddingVertical | Dp | `coui_input_edit_text_no_title_padding_top/_bottom` | 15dp |
| EditTextWithTitlePaddingBottom | Dp | `coui_input_preference_single_title_padding_bottom` | 12dp |
| ButtonMarginStart | Dp | `coui_input_button_layout_margin_start` | 8dp |
| DeleteIconSize | Dp | `ic_edit_text_delete` | 22dp |
| PasswordIconSize | Dp | `coui_inputview_password_size` | 28dp |
| PasswordMarginStart | Dp | `coui_card_input_password_margin_start` | 2dp |
| CaptionTextSize | TextUnit | `couiTextCaption` | 10sp |
| ErrorPaddingTop | Dp | `coui_card_single_input_padding_top` | 4dp |
| ErrorPaddingHorizontal | Dp | `coui_single_input_card_view.xml` error padding | 32dp |

## Colours

| Slot | Source | Light | Dark |
| :--- | :--- | :--- | :--- |
| cardColor | `coui_color_card_background` | `#FFFFFF` | `#1AFFFFFF` |
| titleColor / textColor | `?attr/couiColorPrimaryNeutral` | `#E6000000` | `#E6FFFFFF` |
| hintColor / countColor | `?attr/couiColorHintNeutral` | `#42000000` | `#4DFFFFFF` |
| errorColor | `coui_color_error` | `#DB382C` | `#FF6C61` |
| iconColor | `?attr/couiColorLabelSecondary` | `#8A000000` | `#8AFFFFFF` |
| disabledTextColor | `?attr/couiColorDisabledNeutral` | `#42000000` | `#4DFFFFFF` |

## Known gaps

- The error-state animation is modelled from `COUIErrorEditTextHelper`: showing `errorText` turns the hint /
  title colour error-red over `DURATION_HINT_ANIMATOR` **217ms** (`COUIEaseInterpolator`, cubic-bezier
  (0.33, 0, 0.67, 1)) and shakes the focused field with `ShakeInterpolator` - offsets `0, -1, 0.5, -0.5, 0`
  scaled by `coui_edit_text_shake_amplitude` **5.33dp** across the 83/133/117/117ms segments (450ms total).
  The error label itself fades in over `showErrorMsgAnim()` **217ms** and out over `hideErrorMsgAnim()`
  **283ms** (both `COUIEaseInterpolator`), staying composed while fading out.
  **Correction on the stroke:** the card style is `Widget.COUI.SingleInput`, which sets
  `couiBackgroundMode=noLine`; `COUIEditText.onDraw()` paints a box background only in box mode
  (`mBoxBackgroundMode == 2`) and the error redraw (`drawModeBackgroundRect()` / `drawModeBackgroundLine()`)
  is only reached in box/line modes, so the card has **no** thick/thin focus stroke and **no** error-state
  background redraw. The `coui_input_preference_stroke_width` (0.33dp) and `coui_textinput_*_stroke_width`
  tokens are declared but never referenced by the decompiled classes (dead tokens). Still not modelled: the
  `133ms`/`80ms` selection-mask highlight; the error message, count colour and truncation follow the stock
  behaviour.
- The password eye uses the stock `coui_btn_eye_on` / `coui_btn_eye_off` vectors without the animated-selector
  transition.
- `COUIInputView`'s header container (leading text/icons) and custom buttons are not exposed yet.
