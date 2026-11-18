# TextField

`TextField` is a basic input component in Rmuix for receiving text input from users. The component provides rich customization options, supporting label animations, leading and trailing icons, and other features.

<div style="position: relative; height: 340px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=textField" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass covers only the rectangle background mode of the Settings APK `Widget.COUI.EditText` family (`COUIEditText` / `COUICutoutDrawable`) and its dimension, colour and style resources. It is not a complete 1:1 acceptance result.

- Corner radius 16dp → 10dp (`coui_textinput_corner_radius`); minimum height 62dp (`coui_edit_text_height_min`).
- Resting border: `coui_textinput_stroke_width` hairline (0.33dp) in `couiColorDivider` (`#1F000000` / `#33FFFFFF`); disabled uses `coui_textinput_stroke_color_disabled` (`#19000000` / `#19FFFFFF`). The hairline is clamped to at least one physical pixel (`hairlinePx`), so it stays visible at 1x densities such as desktop and web.
- Focused border: `Widget.COUI.EditText` also binds `couiFocusStrokeWidth` → `coui_textinput_focus_stroke_width` = **1dp**, and `COUIEditText` reads it (`mStrokeWidthFocused`), so the focused box both recolours to `couiColorPrimary` **and** thickens to 1dp. An earlier pass wrongly stated the rectangle keeps 0.33dp on focus; the old 0→2dp grow animation is still gone, the stock jump is 0.33 → 1dp.
- Default text is 16sp (`couiEditTextTextAppearance` = headline6 with a regular family); the floating label is `coui_textinput_hint_text_size` (10sp) and the inline label 16sp, both in sp so system font scaling applies.
- Label colour defaults to `couiColorLabelSecondary`, equal to `couiColorSecondNeutral` (`#8A000000` / `#8AFFFFFF`).
- While the label floats on the top border it now erases the border 4dp to each side (`coui_textinput_label_cutout_padding`, `TextFieldDefaults.LabelCutoutPadding`), matching the stock cutout drawable instead of letting the stroke cross the label.
- `TextFieldColors` gained optional `restingBorderColor` and `disabledBorderColor`; `Color.Unspecified` falls back to the stock neutrals, so existing three-argument call sites keep compiling.

**BREAKING:** `CornerRadius` 16→10dp, the resting border is a 0.33dp hairline and the focused border now follows the stock 1dp `couiFocusStrokeWidth` instead of a 0→2dp grow animation, the default label colour and text size change, and the field is at least 62dp tall.

Line mode (`lineMode`, `couiBackgroundMode=line`: a 0.33dp bottom hairline that wipes in 250ms while focused) and the error state (`errorState`: `coui_edit_text_shake_amplitude` 5.33dp shake plus a 217ms colour ramp) are implemented. The boxed mode now matches the stock `coui_edittext_default_background` nine-patch: **transparent fill** (the selector paints only the 0.33dp divider hairline, 1dp `couiColorPrimary` while focused) with the border cut out around the floating label. `backgroundColor` therefore defaults to `Color.Transparent`; pass a colour only when a screen wants a filled box. The quick-delete icon is available as an opt-in `showClearButton` (default `false`, so nothing changes for existing call sites); `COUIEditText` gates its delete icon behind attributes, hence the opt-in. The drawable `ic_edit_text_delete_search_view.xml` fills the disc with `?attr/couiColorHintNeutral` and hardcodes the cross to `#ffffff`, and has no `drawable-night` variant, so the dark-theme rendering keeps the white cross.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TextField
```

## Basic Usage

The TextField component can be used to get user input:

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Username"
)
```

::: info
This TextField component now also supports the latest state-based version. Please refer to the [State-based](https://developer.android.com/develop/ui/compose/text/user-input?textfield=state-based) documentation for details.
:::

## Input Types

### TextField with Label

The label automatically moves to the top when the input field gains focus or has content:

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Email Address"
)
```

### Using Label as Placeholder

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Please enter content",
    useLabelAsPlaceholder = true
)
```

## Component States

### Disabled State

```kotlin
var text by remember { mutableStateOf("") }
TextField(
    value = text,
    onValueChange = { text = it },
    label = "Disabled Input Field",
    enabled = false
)
```

### Read-Only State

```kotlin
var text by remember { mutableStateOf("This is read-only content") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Read-Only Input Field",
    readOnly = true
)
```

## Properties

### TextField Properties

| Property Name         | Type                                         | Description                         | Default Value                                    | Required |
| --------------------- | -------------------------------------------- | ----------------------------------- | ------------------------------------------------ | -------- |
| value                 | String or TextFieldValue                     | Text value of the input field       | -                                                | Yes      |
| onValueChange         | (String) -> Unit or (TextFieldValue) -> Unit | Callback when text changes          | -                                                | Yes      |
| modifier              | Modifier                                     | Modifier applied to the input field | Modifier                                         | No       |
| insideMargin          | DpSize                                       | Internal padding of input field     | TextFieldDefaults.InsideMargin                    | No       |
| colors                | TextFieldColors                              | Colors used by the field            | TextFieldDefaults.textFieldColors()              | No       |
| cornerRadius          | Dp                                           | Corner radius                       | TextFieldDefaults.CornerRadius                    | No       |
| label                 | String                                       | Label text                          | ""                                               | No       |
| useLabelAsPlaceholder | Boolean                                      | Use label as placeholder            | false                                            | No       |
| enabled               | Boolean                                      | Whether input field is enabled      | true                                             | No       |
| errorState        | Boolean                   | Error state: the stroke ramps to `couiColorErrorTextBg` (`#4DE82A18`/`#EA3939`) over 217ms and the content shakes `coui_edit_text_shake_amplitude` (5.33dp) over 450ms | false |
| lineMode          | Boolean                   | Bottom-only stroke (`couiBackgroundMode = 1`): a 0.33dp hairline at rest, and a 1dp line that grows left to right over 250ms on focus | false |

> The three line-mode paddings: `coui_textinput_line_padding_top` (10dp) and `coui_textinput_line_padding_middle` (5dp) are exposed as `TextFieldDefaults.LinePaddingTop` / `LinePaddingMiddle` (the stock `getModePaddingTop()` is top + hint height + middle); the third one, `coui_textinput_line_padding` (11dp), is **assigned to `mLinePadding` and never read** - a dead resource, documented but not used.
| readOnly              | Boolean                                      | Whether input field is read-only    | false                                            | No       |
| textStyle             | TextStyle                                    | Text style                          | TextFieldDefaults.textStyle()                    | No       |
| keyboardOptions       | KeyboardOptions                              | Keyboard options                    | KeyboardOptions.Default                          | No       |
| keyboardActions       | KeyboardActions                              | Keyboard actions                    | KeyboardActions.Default                          | No       |
| leadingIcon           | @Composable (() -> Unit)?                    | Leading icon                        | null                                             | No       |
| trailingIcon          | @Composable (() -> Unit)?                    | Trailing icon                       | null                                             | No       |
| singleLine            | Boolean                                      | Single line input                   | false                                            | No       |
| maxLines              | Int                                          | Maximum lines                       | If singleLine is true then 1, else Int.MAX_VALUE | No       |
| minLines              | Int                                          | Minimum lines                       | 1                                                | No       |
| visualTransformation  | VisualTransformation                         | Visual transformation               | VisualTransformation.None                        | No       |
| onTextLayout          | (TextLayoutResult) -> Unit                   | Text layout callback                | {}                                               | No       |
| interactionSource     | MutableInteractionSource?                    | Interaction source                  | null                                             | No       |
| cursorBrush           | Brush                                        | Cursor brush                        | SolidColor(colors.borderColor)                   | No       |
| showClearButton       | Boolean                                      | Draw the stock `ic_edit_text_delete_search_view` clear button once the field has text; tapping it empties the field | false |

### TextField (state-based) Properties

| Property Name         | Type                                           | Description                                        | Default Value                              | Required |
| --------------------- | ---------------------------------------------- | -------------------------------------------------- | ------------------------------------------ | -------- |
| state                 | TextFieldState                                 | State object holding text and selection            | -                                          | Yes      |
| modifier              | Modifier                                       | Modifier applied to the input field                | Modifier                                   | No       |
| insideMargin          | DpSize                                         | Internal padding of input field                    | TextFieldDefaults.InsideMargin              | No       |
| colors                | TextFieldColors                                | Colors used by the field                           | TextFieldDefaults.textFieldColors()        | No       |
| cornerRadius          | Dp                                             | Corner radius                                      | TextFieldDefaults.CornerRadius              | No       |
| label                 | String                                         | Label text                                         | ""                                         | No       |
| useLabelAsPlaceholder | Boolean                                        | Use label as placeholder                           | false                                      | No       |
| enabled               | Boolean                                        | Whether input field is enabled                     | true                                       | No       |
| readOnly              | Boolean                                        | Whether input field is read-only                   | false                                      | No       |
| inputTransformation   | InputTransformation?                           | Input transformation                               | null                                       | No       |
| textStyle             | TextStyle                                      | Text style                                         | TextFieldDefaults.textStyle()              | No       |
| keyboardOptions       | KeyboardOptions                                | Keyboard options                                   | KeyboardOptions.Default                    | No       |
| onKeyboardAction      | KeyboardActionHandler?                         | Keyboard action handler                            | null                                       | No       |
| lineLimits            | TextFieldLineLimits                            | Line limits                                        | TextFieldLineLimits.Default                | No       |
| leadingIcon           | @Composable (() -> Unit)?                      | Leading icon                                       | null                                       | No       |
| trailingIcon          | @Composable (() -> Unit)?                      | Trailing icon                                      | null                                       | No       |
| onTextLayout          | Density.(getResult: () -> TextLayoutResult?) -> Unit | Text layout callback with density receiver    | null                                       | No       |
| interactionSource     | MutableInteractionSource?                      | Interaction source                                 | null                                       | No       |
| cursorBrush           | Brush                                          | Cursor brush                                       | SolidColor(colors.borderColor)             | No       |
| showClearButton       | Boolean                                      | Draw the stock `ic_edit_text_delete_search_view` clear button once the field has text; tapping it empties the field | false |
| outputTransformation  | OutputTransformation?                          | Output transformation                              | null                                       | No       |
| scrollState           | ScrollState                                    | Scroll state                                       | rememberScrollState()                       | No       |

### TextFieldDefaults Object

The TextFieldDefaults object provides default values for TextField components.

#### Constants

| Constant Name | Type   | Description                  | Default Value          |
| ------------- | ------ | ---------------------------- | ---------------------- |
| CornerRadius  | Dp     | Corner radius of the field   | 10.dp                  |
| InsideMargin  | DpSize | Internal padding of the field| DpSize(16.dp, 16.dp)  |
| MinHeight     | Dp     | Minimum field height         | 62.dp                  |
| StrokeWidth   | Dp     | Resting border hairline (`coui_textinput_stroke_width`) | 0.33.dp |

#### `textFieldColors()` factory

Builds a [TextFieldColors] instance. Override any subset; unspecified params fall back to the stock ColorOS neutrals.

| Parameter        | Type  | Default                                          |
| ---------------- | ----- | ------------------------------------------------ |
| backgroundColor  | Color | Color.Transparent (stock border-only box)        |
| labelColor       | Color | Stock `couiColorLabelSecondary` neutral          |
| borderColor      | Color | RmuixTheme.colorScheme.primary                   |
| restingBorderColor | Color | Stock `couiColorDivider` hairline              |
| disabledBorderColor | Color | Stock disabled stroke                        |

## Advanced Usage

### TextField with Icons

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Search",
    leadingIcon = {
        Icon(
            imageVector = RmuixIcons.Search,
            contentDescription = "Search Icon",
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
)
```

### Password Input Field

```kotlin
var password by remember { mutableStateOf("") }
var passwordVisible by remember { mutableStateOf(false) }

TextField(
    value = password,
    onValueChange = { password = it },
    label = "Password",
    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    trailingIcon = {
        IconButton(
            onClick = { passwordVisible = !passwordVisible },
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Icon(
                imageVector = RmuixIcons.Rename,
                tint = if (passwordVisible) RmuixTheme.colorScheme.primary else RmuixTheme.colorScheme.onSecondaryContainer,
                contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
            )
        }
    }
)
```

### Input Field with Validation

```kotlin
var email by remember { mutableStateOf("") }
var isError by remember { mutableStateOf(false) }
val errorColor = Color.Red.copy(0.3f)
val emailPattern = remember { Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") }

Column {
    TextField(
        value = email,
        onValueChange = {
            email = it
            isError = email.isNotEmpty() && !emailPattern.matches(email)
        },
        label = "Email",
        colors = TextFieldDefaults.textFieldColors(
            labelColor = if (isError) errorColor else RmuixTheme.colorScheme.onSurfaceVariantSummary,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
    if (isError) {
        Text(
            text = "Please enter a valid email address",
            color = errorColor,
            style = RmuixTheme.textStyles.body2,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}
```

### Custom Styles

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = "Custom Input Field",
    cornerRadius = 8.dp,
    colors = TextFieldDefaults.textFieldColors(
        backgroundColor = RmuixTheme.colorScheme.primary.copy(alpha = 0.1f),
    ),
    textStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = RmuixTheme.colorScheme.primary
    )
)
```

### Using TextFieldValue

When you need more fine-grained control over text selection and cursor position:

```kotlin
var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

TextField(
    value = textFieldValue,
    onValueChange = { textFieldValue = it },
    label = "Advanced Input Control",
    // TextFieldValue provides control over text, selection range, and cursor position
)
```
