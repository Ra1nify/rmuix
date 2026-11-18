# ColorPicker

A saturation/value area with hue and alpha tracks, an optional hex readout and a preset swatch row, plus
a preference row and an overlay dialog that wraps it.

::: warning No ColorOS counterpart
None of the three decompiled ColorOS trees (`settings`, `gallery3d`, `systemui`) contains a colour-picker
class, so this component has **no stock metrics**. Its sizes are built from the library's ColorOS tokens
(`ColorPickerDefaults`) and may change. It lives in `rmuix-extend` for that reason.
:::

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.extend.colorpicker.ColorPicker
import io.github.ra1nify.rmuix.kmp.extend.colorpicker.OverlayColorPickerDialog
import io.github.ra1nify.rmuix.kmp.extend.preference.ColorPickerPreference
```

## Basic Usage

```kotlin
// A row that shows a swatch and opens the picker in a dialog; only commits on confirm.
var accent by remember { mutableStateOf(Color(0xFF0080FF)) }

ColorPickerPreference(
    color = accent,
    onColorChange = { accent = it },
    title = "Accent colour",
    summary = "Used for highlights",
)
```

Inline, without a dialog:

```kotlin
ColorPicker(
    color = accent,
    onColorChange = { accent = it },
    showAlphaSlider = false,
)
```

## Properties

### ColorPicker

| Name            | Type                    | Description                                     | Default |
| :-------------- | :---------------------- | :---------------------------------------------- | :------ |
| color           | Color                   | The current colour                              | - |
| onColorChange   | (Color) -> Unit         | Called with the new colour while dragging        | - |
| modifier        | Modifier                | Modifier applied to the picker                   | Modifier |
| showAlphaSlider | Boolean                 | Whether the alpha track is shown                 | true |
| showHexText     | Boolean                 | Whether the `#RRGGBB` / `#AARRGGBB` readout is shown | true |
| presetColors    | List<Color>             | Shortcut swatches; empty hides the row            | ColorPickerDefaults.presetColors() |
| colors          | ColorPickerColors       | Thumb, track and hex colours                     | ColorPickerDefaults.colorPickerColors() |

### OverlayColorPickerDialog

| Name            | Type            | Description                                        | Default |
| :-------------- | :-------------- | :------------------------------------------------- | :------ |
| show            | Boolean         | Whether the dialog is shown                         | - |
| color           | Color           | The colour the dialog opens with                    | - |
| onColorChange   | (Color) -> Unit | The live draft colour                               | - |
| onConfirm       | (Color) -> Unit | The draft colour, only when confirm is pressed      | - |
| onDismissRequest| () -> Unit      | Scrim tap, back or cancel                           | - |
| modifier        | Modifier        | Modifier applied to the dialog                      | Modifier |
| title           | String?         | Dialog title                                        | null |
| showAlphaSlider | Boolean         | Whether the alpha track is shown                    | true |
| confirmLabel    | String          | Confirm button label                                | "OK" |
| cancelLabel     | String          | Cancel button label                                 | "Cancel" |
| colors          | ColorPickerColors | Picker colours                                    | ColorPickerDefaults.colorPickerColors() |

### ColorPickerPreference

| Name            | Type                    | Description                                    | Default |
| :-------------- | :---------------------- | :--------------------------------------------- | :------ |
| color           | Color                   | The current colour                             | - |
| onColorChange   | (Color) -> Unit         | Called with the chosen colour after confirm     | - |
| title           | String                  | Row title                                      | - |
| modifier        | Modifier                | Modifier applied to the row                    | Modifier |
| summary         | String?                 | Row summary                                    | null |
| enabled         | Boolean                 | Whether the row is interactive                 | true |
| showAlphaSlider | Boolean                 | Whether the dialog shows the alpha track       | true |
| dialogTitle     | String                  | Dialog title                                   | title |
| previewShape    | Shape                   | Swatch shape                                   | CircleShape |
| previewSize     | Dp                      | Swatch size                                    | 28dp |
| confirmLabel    | String                  | Confirm button label                           | "OK" |
| cancelLabel     | String                  | Cancel button label                            | "Cancel" |

## ColorPickerDefaults

| Name             | Type     | Value |
| :--------------- | :------- | :---- |
| AREA_ASPECT_RATIO| Float    | 1.6 |
| AreaCornerRadius | Dp       | 12dp (`?attr/couiRoundCornerM`) |
| TrackHeight      | Dp       | 12dp |
| ThumbSize        | Dp       | 20dp |
| PresetSize       | Dp       | 28dp |
| RowSpacing       | Dp       | 12dp |
| HexTextSize      | TextUnit | 14sp (`couiTextAppearanceArticleBody`, 13sp, rounded up to 14sp for the readout) |

## Behaviour notes

- While the dialog is open the row keeps a **draft** colour: `onColorChange` on the dialog fires live, but
  the preference's `onColorChange` only fires on confirm, so dismissing leaves the stored colour untouched.
- Hue is kept as component state: dragging saturation to 0 does not reset the hue to red.
