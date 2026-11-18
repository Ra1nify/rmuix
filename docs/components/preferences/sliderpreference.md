# SliderPreference

`SliderPreference` is a preference component in Rmuix that combines a title/summary with a slider control. The slider is placed in the bottom action area of the `BasicComponent`, making it ideal for settings screens where users need to adjust values such as volume, brightness, or font size.

## Stock ColorOS static review (2026-11-12)

This pass reads `res/layout/coui_preference_widget_select.xml` and `res/layout/coui_preference_widget_jump.xml` from the Settings APK (the slider row reuses both widgets: a status line plus a chevron when the row is clickable).

- `valueText` is the stock status line: it used `body2` (14sp, numerically equal) with `onSurfaceVariantActions` (40% neutral) and no width cap. It now resolves `BasicComponentDefaults.StatusTextSize` (14sp), the `summaryColor` (secondary text colour), a 162dp width cap and a 24dp start margin.
- The chevron keeps the stock `support_preference_image_margin_start` (4dp) instead of the row-level `padding(end = 8.dp)` that had no source.
- Both the single and the range variants were changed.

**BREAKING:** the value text is secondary-coloured and capped at 162dp; the gap before the chevron drops from 8dp to 4dp.

## Import
```kotlin
import io.github.ra1nify.rmuix.kmp.preference.SliderPreference
```

## Basic Usage

```kotlin
var sliderValue by remember { mutableFloatStateOf(0.5f) }

SliderPreference(
    value = sliderValue,
    onValueChange = { sliderValue = it },
    title = "Volume"
)
```

## With Summary

```kotlin
var brightness by remember { mutableFloatStateOf(0.8f) }

SliderPreference(
    value = brightness,
    onValueChange = { brightness = it },
    title = "Brightness",
    summary = "Adjust screen brightness level"
)
```

## Component States

### Disabled State

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

SliderPreference(
    value = value,
    onValueChange = { value = it },
    title = "Disabled Slider",
    summary = "This slider is currently unavailable",
    enabled = false
)
```

## Properties

### SliderPreference Properties

| Property Name         | Type                              | Description                                                                                                                                                                    | Default Value                      | Required |
| --------------------- | --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------- | -------- |
| value                 | Float                             | Current value of the slider. If outside of valueRange provided, value will be coerced to this range                                                                            | -                                  | Yes      |
| onValueChange         | (Float) -> Unit                   | Callback when value changes                                                                                                                                                    | -                                  | Yes      |
| title                 | String?                           | Title of the preference                                                                                                                                                        | null                               | No       |
| modifier              | Modifier                          | Modifier applied to the component                                                                                                                                              | Modifier                           | No       |
| titleColor            | BasicComponentColors              | Title text color configuration                                                                                                                                                 | BasicComponentDefaults.titleColor() | No       |
| summary               | String?                           | Summary description                                                                                                                                                            | null                               | No       |
| summaryColor          | BasicComponentColors              | Summary text color configuration                                                                                                                                               | BasicComponentDefaults.summaryColor() | No       |
| startAction           | @Composable (() -> Unit)?         | Custom start side content                                                                                                                                                      | null                               | No       |
| valueText             | String?                           | Current slider value text displayed in the end area with summary-style formatting. Rendered inside the Row layout with center-vertical alignment and weight                     | null                               | No       |
| endActions            | @Composable (RowScope.() -> Unit)?| Custom end side content, rendered after valueText within the same Row                                                                                                          | null                               | No       |
| bottomAction          | @Composable (() -> Unit)?         | Custom content at the top of the bottom area, above the slider                                                                                                                 | null                               | No       |
| onClick               | (() -> Unit)?                     | Callback triggered on click. When non-null, an arrow icon is displayed in the end area                                                                                        | null                               | No       |
| holdDownState         | Boolean                           | Whether the component is in the pressed state                                                                                                                                  | false                              | No       |
| enabled               | Boolean                           | Whether the preference is enabled                                                                                                                                              | true                               | No       |
| valueRange            | ClosedFloatingPointRange\<Float\> | Range of values that this slider can take. The passed value will be coerced to this range                                                                                      | 0f..1f                             | No       |
| steps                 | Int                               | Amount of discrete allowable values. If 0, the slider will behave continuously. Must not be negative                                                                           | 0                                  | No       |
| onValueChangeFinished | (() -> Unit)?                     | Called when value change has ended                                                                                                                                              | null                               | No       |
| reverseDirection      | Boolean                           | Controls the direction of slider. When false (default), follows LayoutDirection (L-R in LTR, R-L in RTL). When true, reverses the direction.                                   | false                              | No       |
| sliderHeight          | Dp                                | Height of the slider                                                                                                                                                           | SliderDefaults.MinHeight           | No       |
| sliderColors          | SliderColors                      | Color configuration of the slider                                                                                                                                              | SliderDefaults.sliderColors()      | No       |
| hapticEffect          | SliderDefaults.SliderHapticEffect | Type of haptic feedback                                                                                                                                                        | SliderDefaults.DefaultHapticEffect | No       |
| showKeyPoints         | Boolean                           | Whether to show key point indicators on the slider. Only works when keyPoints is not null                                                                                      | false                              | No       |
| keyPoints             | List\<Float\>?                    | Custom key point values to display on the slider. If null, uses step positions from steps parameter. Values should be within valueRange                                        | null                               | No       |
| magnetThreshold       | Float                             | Magnetic snap threshold as a fraction (0.0 to 1.0). When slider value is within this distance from a key point, it will snap to that point. Only applies when keyPoints is set | 0.02f                              | No       |
| startLabel            | String?                           | Label drawn to the start of the slider, vertical-centre aligned (seek_bar_preference.xml's left_tip) | null | No |
| endLabel              | String?                           | Label drawn to the end of the slider (right_tip) | null | No |
| labelColor            | BasicComponentColors              | Colour of startLabel / endLabel (section_seekbar_text_color) | SliderPreferenceDefaults.labelColor() | No |
| insideMargin          | PaddingValues                     | Internal content padding                                                                                                                                                       | BasicComponentDefaults.InsideMargin | No       |

## Advanced Usage

### Custom Value Range with Steps

```kotlin
var temperature by remember { mutableFloatStateOf(25f) }

SliderPreference(
    value = temperature,
    onValueChange = { temperature = it },
    title = "Temperature",
    summary = "Current: ${temperature.roundToInt()}°C",
    valueRange = 16f..32f,
    steps = 15,
    hapticEffect = SliderDefaults.SliderHapticEffect.Step
)
```

### With Start Icon

```kotlin
var volume by remember { mutableFloatStateOf(0.7f) }

SliderPreference(
    value = volume,
    onValueChange = { volume = it },
    title = "Volume",
    summary = "${(volume * 100).roundToInt()}%",
    startAction = {
        Icon(
            imageVector = RmuixIcons.Basic.Audio,
            contentDescription = "Volume Icon",
            tint = RmuixTheme.colorScheme.onBackground,
            modifier = Modifier.padding(end = 16.dp)
        )
    },
    valueRange = 0f..1f
)
```

### With Custom Key Points

```kotlin
var fontSize by remember { mutableFloatStateOf(14f) }

SliderPreference(
    value = fontSize,
    onValueChange = { fontSize = it },
    title = "Font Size",
    summary = "Current: ${fontSize.roundToInt()}sp",
    valueRange = 10f..24f,
    showKeyPoints = true,
    keyPoints = listOf(10f, 12f, 14f, 16f, 18f, 20f, 24f),
    magnetThreshold = 0.05f,
    hapticEffect = SliderDefaults.SliderHapticEffect.Step
)
```


## Stacked rows in one card

`insideMargin` defaults to `BasicComponentDefaults.InsideMargin` = `PaddingValues(horizontal = 32.dp, vertical = 10.dp)`,
the stock `support_preference_title_padding_start` / `_end` (32dp) the `seek_bar_preference.xml` row uses. Keep
every `SliderPreference` in the same card on the **same** `insideMargin`: mixing values makes the shorter row's
track look inset at both ends (a 16dp row is 32dp wider than a 32dp row).

## SliderPreferenceDefaults

The end labels come from `seek_bar_preference.xml`.

| Name           | Type     | Description | Value |
| :------------- | :------- | :---------- | :---- |
| LabelTextSize  | TextUnit | `text_descripe_seekbar_value` | 14sp |
| LabelPadding   | Dp       | `text_descripe_seekbar_padding`: the start label's `paddingEnd` / the end label's `paddingStart` | 10dp |
| labelColor()   | BasicComponentColors | `section_seekbar_text_color` → `coui_color_secondary_neutral` | `#8A000000` / `#8AFFFFFF` |
