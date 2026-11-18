# Stepper

`Stepper` is a numeric stepper in Rmuix: a centred value readout flanked by circular minus and plus buttons. It clamps to `minValue` / `maxValue`, disables the endpoint buttons automatically, and fires a micro haptic tick on every step.

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=stepper" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass reads `com.coui.appcompat.stepper.COUIStepperView`, `coui_stepper_view.xml`, the `ic_minus_sign` / `ic_plus_sign` vectors and the `stepper_button_size` dimen of the Settings APK.

- Both buttons are **32dp** targets (`stepper_button_size`) filled with `?attr/couiColorContainer8` (`#14000000` / `#1AFFFFFF`) and carry **no resting border**: the source combines a `ShapeDrawable`, a `COUIMaskEffectDrawable` and a `COUIStrokeDrawable`, and the stroke drawable is driven by `FocusChangedMetricHelper.HAS_FOCUS` only.
- The signs are vectors, not font glyphs: `ic_minus_sign` is a **13.6dp round-cap line** (x = 9.199 → 22.799) and `ic_plus_sign` is two **15.2dp arms with rounded ends** (8.398 → 23.598 and 8.4 → 23.6), both **1.6dp** thick in a 32dp viewport. The component drew bold `Text("-")` / `Text("+")` glyphs.
- Enabled signs and the readout use `?attr/couiColorPrimaryNeutral` (`#E6000000` / `#E6FFFFFF`); the disabled drawables (`ic_minus_sign_disable` / `ic_plus_sign_disable`) and the disabled readout (`coui_stepper_text_color_select`) switch to `?attr/couiColorDivider` (`#1F000000` / `#33FFFFFF`) instead of fading.
- The readout keeps `android:minWidth="44dp"` with 12dp start/end margins and the `?attr/couiTextAppearanceHeadline5` style (**18sp**, `sans-serif-medium`).
- Keyboard focus draws the same ring as `Button`: a 6dp stroke (`default_focus_stroke_radius` × 2) clipped to the outside, so 3dp is visible, coloured `?attr/couiColorFocusOutline` → `coui_color_focus_outline_blue` (`#660080FF` / `#665C9DFF`).

**BREAKING:** the default buttons lose their resting border, the signs become vectors, the disabled state swaps colour instead of fading, and `StepperColors` gains `focusOutlineColor` and `disabledIconTint` while dropping `borderColor`.

**Press feedback:** `COUIStepperView.configEffectDrawableForButton` layers three drawables on each circular
button - a `couiColorContainer8` oval, a `COUIMaskEffectDrawable` press mask in `?attr/couiColorPress`, and the
focus stroke - and calls `COUIStateEffectDrawable.enableScaleEffect(view, 2)`. The component now draws the
press mask over the fill (`#1F000000` / `#33FFFFFF`, 0.3s spring) in addition to the scale. The scale is now
the stock `enableScaleEffect` result: `COUIPressAnimatorUnit.initTargetScale()` -> `PressCalc.calculateUniformScaleBreakdown()`
with the `PressParamsExtKt` defaults (area mapping 0.85..0.97 over 20dp..150dp, BezierData(0,0,0,1), aspect channel
2..15) gives **0.8766** for the 32dp button (`StepperDefaults.PressTargetScale`); the press spring is
`jellyPressSpring` (response 0.25 / bounce 0) and the release spring `jellyHandupSpring` (response 0.5 / bounce 0.6).
The long-press repeat is now modelled: a tap steps once on release, and holding a button steps immediately after the platform long-press timeout and then every **100ms**, each repetition preceded by a haptic tick (see `LongPressProxy.MyHandler` -> `Modifier.stepperPressable`). The `couiUnit` / `couiDefStep` / `couiMinimum` / `couiMaximum` attributes are replaced by the `step` / `value` / `minValue` / `maxValue` parameters.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Stepper
import io.github.ra1nify.rmuix.kmp.basic.StepperDefaults
```

## Basic Usage

```kotlin
var value by remember { mutableIntStateOf(1) }

Stepper(
    value = value,
    onValueChange = { value = it },
)
```

## Component States

### Bounds

The minus button disables at `minValue`, the plus button at `maxValue`; both switch their sign colour to `couiColorDivider`.

```kotlin
var value by remember { mutableIntStateOf(0) }

Stepper(
    value = value,
    onValueChange = { value = it },
    minValue = 0,
    maxValue = 5,
)
```

### Custom Step And Format

```kotlin
var percent by remember { mutableIntStateOf(50) }

Stepper(
    value = percent,
    onValueChange = { percent = it },
    minValue = 0,
    maxValue = 100,
    step = 10,
    valueFormatter = { "$it%" },
)
```

### Disabled

```kotlin
Stepper(
    value = 3,
    onValueChange = {},
    enabled = false,
)
```

## Properties

### Stepper Properties

| Property Name  | Type                  | Description                                                        | Default Value                 | Required |
| -------------- | --------------------- | ------------------------------------------------------------------ | ----------------------------- | -------- |
| value          | Int                   | Current value                                                      | -                             | Yes      |
| onValueChange  | (Int) -> Unit         | Invoked with the clamped value after a step                        | -                             | Yes      |
| modifier       | Modifier              | Modifier applied to the row                                        | Modifier                      | No       |
| minValue       | Int                   | Lower bound; disables the minus button at the bound                | 0                             | No       |
| maxValue       | Int                   | Upper bound; disables the plus button at the bound                 | 100                           | No       |
| step           | Int                   | Amount added or subtracted per press                               | 1                             | No       |
| enabled        | Boolean               | Whether the whole stepper accepts input                            | true                          | No       |
| valueFormatter | (Int) -> String       | Converts the value to its readout string                           | `{ it.toString() }`           | No       |
| colors         | StepperColors         | Button, sign, readout and focus colours                            | `StepperDefaults.stepperColors()` | No   |

### StepperDefaults Object

| Constant Name               | Type | Description                                                    | Default Value |
| --------------------------- | ---- | -------------------------------------------------------------- | ------------- |
| ButtonSize                  | Dp   | Both button targets (`stepper_button_size`)                    | 32.dp         |
| PressTargetScale            | Float | `PressCalc.calculateUniformScaleBreakdown()` for `stepper_button_size` | 0.8766       |
| ValueTextSize               | TextUnit | Readout size (`?attr/couiTextAppearanceHeadline5`)         | 18.sp         |
| ValueFontWeight             | FontWeight | Readout weight (`sans-serif-medium`)                     | FontWeight.Medium |
| ValueMinWidth               | Dp   | Readout minimum width (`android:minWidth="44dp"`)              | 44.dp         |
| ValueHorizontalPadding      | Dp   | Readout start/end margin                                       | 12.dp         |

`StepperDefaults.stepperColors()` resolves `buttonBgColor` (`couiColorContainer8`), `iconTint` and `textColor` (`couiColorPrimaryNeutral`), `disabledTextColor` / `disabledIconTint` (`couiColorDivider`) and `focusOutlineColor` (`couiColorFocusOutline`) from the current theme.

## Advanced Usage

### Custom Colours

```kotlin
Stepper(
    value = value,
    onValueChange = { value = it },
    colors = StepperDefaults.stepperColors(
        iconTint = Color(0xFF3482FF),
        textColor = Color(0xFF3482FF),
    ),
)
```

### Inside A Preference Row

`Stepper` has no intrinsic row padding, so it can sit on the trailing side of a preference row by wrapping it in a `Row` with the desired alignment.

```kotlin
Row(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
) {
    Text("Brightness level")
    Stepper(value = level, onValueChange = { level = it }, maxValue = 10)
}
```

## Best Practices

- Always pass `minValue` / `maxValue`: the endpoint disabling is driven by those bounds, and the buttons do not wrap around.
- Use `step` together with a `valueFormatter` when the readout carries a unit, so the unit stays in sync with the step size.
- Do not add a border to the buttons: the stock widget has none at rest, and the focus ring is what marks keyboard focus.
- Keep the row height unchanged when the value grows: `ValueMinWidth` (44dp) reserves space for two-digit values, larger widths shift the signs only when the text exceeds it.
