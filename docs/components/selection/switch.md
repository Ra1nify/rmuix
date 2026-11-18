# Switch

`Switch` is a basic toggle component in Rmuix used to switch between two states. It provides an interactive switch control with animation effects, suitable for enabling and disabling settings.

<div style="position: relative; height: 160px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=switch" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass reads `com.coui.appcompat.couiswitch.COUISwitch`, the `COUISwitchStyle` style block, `coui_switch_padding`, and the `switch_custom_track_*` drawables of the Settings APK. No value needed changing; the entries below record why the current geometry is already correct.

- Geometry comes from `COUISwitchStyle`: `bar_width` 44dp, `bar_height` 24dp, `outer_circle_width` 18dp, `circle_padding` 3dp. `coui_switch_padding` is 0dp, and `onMeasure` adds twice that padding, so the widget measures exactly 44 x 24dp — the same numbers `SwitchDefaults` uses.
- Track colours are set in code, not by the drawables: `mBarCheckedColor` = `?attr/couiColorPrimary`, `mBarUnCheckedColor` = `?attr/couiColorControls` (**#29000000** / **#40FFFFFF**). The drawable's own `couiColorDivider` solid is overridden by `setTint`, so the library's unchecked track (`#29000000` / `#40FFFFFF`) is the one that matches the device.
- Disabled tracks: `mBarCheckedDisabledColor` = `couiColorPrimary & 0x4DFFFFFF` (30% alpha) and `mBarUncheckedDisabledColor` = `?attr/couiColorPressBackground` (**#14000000** / **#26FFFFFF**), matching the library's disabled colours.
- Themed variant: `setSwitchMinWidth(theme_style_bar_width)` (38dp) is only applied when `coui_switch_theme_enable` is true; the flag is **false** in this APK, so the 44dp geometry is the shipped one.
- Dead attributes: `inner_circle_width` (7.9dp) and `outer_circle_stroke_width` are read into fields but never drawn (`mInnerCirclePaint` is created and never used), so the single filled 18dp thumb is correct.
- Thumb shadow matches: `setShadowLayer(8, 0, 4, argb(25, 0, 0, 0))`.

**Loading state:** `loading = true` mirrors `COUISwitch.setLoading()` - the thumb collapses over 433ms, the official `coui_icon_loading` glyph (a 270-degree open circle, 1.4 stroke) fades in and grows over 550ms into the 18dp thumb circle (`LoadingSize`; `drawLoading()` sets the drawable bounds to `mOuterCircleRectF`, so the 40x40px xxhdpi `switch_loading.png` is stretched from its 13.33dp intrinsic size) and then rotates every 800ms, and the switch stops accepting input while it runs (`enabled && !loading`). **Still under review:** the press/hover mask compositing timings are not verified against `COUIStateEffectDrawable`.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Switch
```

## Basic Usage

The Switch component can be used to toggle between two states:

```kotlin
var checked by remember { mutableStateOf(false) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it }
)
```

## Component States

### Stock ColorOS Behavior

The 44 × 24 dp track contains an 18 dp thumb that stretches into a capsule during a toggle.
Track color follows thumb movement using the stock ARGB interpolation, not a separate color animation.
Dragging works across the whole track and mirrors in RTL. Passing `onCheckedChange = null` makes the switch read-only, including dragging and haptics.
Default neutral colors follow the active `RmuixTheme`, including when its appearance differs from the system theme.

BREAKING: `switchColors().uncheckedTrackColor` changes from `#1F000000 / #33FFFFFF` to the official `#29000000 / #40FFFFFF` light/dark pair; `checkedThumbColor` defaults to white instead of `onPrimary`; `disabledCheckedTrackColor` uses `primary & 0x4DFFFFFF` instead of `disabledPrimary`.

Dragging previews the state without committing it: the release spring uses response 0.4 / bounce 0.2 and calls `onCheckedChange` only after attachment finishes and only when the state changes. Its initial velocity is limited to ±1000 raw px/s. Toggle and attachment animations cannot be interrupted by a new drag; attachment does not stretch the thumb. Touch slop does not subtract from the pointer displacement.
The stock press/hover masks are reproduced with their own response 0.3 / bounce 0 springs. Material/loading variants, native focus feedback and device frame-by-frame validation remain outstanding; this is not a complete 1:1 certification.

The click spring decodes the official SpringData(bounce 0.3, response 0.4) and copies the raw bounce into `COUISpringForce.setBounce`, whose damping ratio is `1 - bounce` = 0.7 (drag 1.0, attach 0.8). Note the attachment spring is 0.8, not 0.9: `SpringData.getDampingRatio()` halves the bounce, but `COUISwitch` never reads it.

### Disabled State

```kotlin
var checked by remember { mutableStateOf(false) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it },
    enabled = false
)
```

## Properties

### Switch Properties

| Property Name   | Type                 | Description                        | Default Value                 | Required |
| --------------- | -------------------- | ---------------------------------- | ----------------------------- | -------- |
| checked         | Boolean              | Whether the switch is checked      | -                             | Yes      |
| onCheckedChange | ((Boolean) -> Unit)? | Callback when switch state changes | -                             | No       |
| modifier        | Modifier             | Modifier applied to the switch     | Modifier                      | No       |
| colors          | SwitchColors         | Color configuration for the switch | SwitchDefaults.switchColors() | No       |
| enabled         | Boolean              | Whether the switch is interactive  | true                          | No       |
| loading         | Boolean              | Show the stock loading spinner in place of the thumb (blocks toggling) | false | No       |

`loading` mirrors `COUISwitch.setLoading()`: `circleScale` collapses the thumb over 433ms while the spinner fades in and grows (0.5 to 1) over 550ms, then rotates 360 degrees every 800ms. The glyph is the official `coui_icon_loading` vector (a 270-degree open circle, stroke 1.4 in a 24-unit viewport) drawn at `LoadingSize` = 18dp (the `drawLoading()` `mOuterCircleRectF` bounds), and taps are ignored while it runs.

### SwitchDefaults Object

The SwitchDefaults object provides the geometry tokens and default color configurations for the Switch.

#### Properties

| Property Name         | Type | Description                                    | Value  |
| --------------------- | ---- | ---------------------------------------------- | ------ |
| Width                 | Dp   | Track width                                    | 44.dp  |
| Height                | Dp   | Track height                                   | 24.dp  |
| ThumbSize             | Dp   | Thumb diameter                                 | 18.dp  |
| ThumbPadding          | Dp   | Inset between thumb and track edge             | 3.dp   |
| Travel                | Dp   | Thumb travel, `Width - ThumbPadding * 2 - ThumbSize` | 20.dp |
| ThumbOffsetUnchecked  | Dp   | Resting thumb x when off                       | 3.dp   |
| ThumbOffsetChecked    | Dp   | Resting thumb x when on                        | 23.dp  |
| thumbShadow           | Shadow | Thumb drop shadow, applied only while enabled | -      |

#### Methods

| Method Name    | Type         | Description                                 |
| -------------- | ------------ | ------------------------------------------- |
| switchColors() | SwitchColors | Creates default color config for the switch |

### SwitchColors Class

| Property Name               | Type  | Description                             | Default Value | Required |
| --------------------------- | ----- | --------------------------------------- | ------------- | -------- |
| checkedThumbColor           | Color | Thumb color when checked                | -             | Yes      |
| uncheckedThumbColor         | Color | Thumb color when unchecked              | -             | Yes      |
| disabledCheckedThumbColor   | Color | Thumb color when disabled and checked   | -             | Yes      |
| disabledUncheckedThumbColor | Color | Thumb color when disabled and unchecked | -             | Yes      |
| checkedTrackColor           | Color | Track color when checked                | -             | Yes      |
| uncheckedTrackColor         | Color | Track color when unchecked              | -             | Yes      |
| disabledCheckedTrackColor   | Color | Track color when disabled and checked   | -             | Yes      |
| disabledUncheckedTrackColor | Color | Track color when disabled and unchecked | -             | Yes      |

## Advanced Usage

### Custom Colors

```kotlin
var checked by remember { mutableStateOf(false) }

Switch(
    checked = checked,
    onCheckedChange = { checked = it },
    colors = SwitchDefaults.switchColors(
        checkedTrackColor = Color.Red,
        checkedThumbColor = Color.White
    )
)
```

### Using with Text

```kotlin
var checked by remember { mutableStateOf(false) }

Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp)
) {
    Switch(
        checked = checked,
        onCheckedChange = { checked = it }
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = if (checked) "Enabled" else "Disabled")
}
```

### Using in Lists

```kotlin
val options = listOf("Airplane Mode", "Bluetooth", "Location Services")
val checkedStates = remember { mutableStateListOf(false, true, false) }

LazyColumn {
    items(options.size) { index ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = options[index])
            Switch(
                checked = checkedStates[index],
                onCheckedChange = { checkedStates[index] = it }
            )
        }
    }
}
```

### Clickable List Row

```kotlin
data class Option(val text: String, var isSelected: Boolean)
val options = remember {
    mutableStateListOf(
        Option("Airplane Mode", false),
        Option("Bluetooth", true),
        Option("Location Services", false)
    )
}

LazyColumn {
    items(options.size) { index ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    options[index] = options[index].copy(
                        isSelected = !options[index].isSelected
                    )
                }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = options[index].text)
            Switch(
                checked = options[index].isSelected,
                onCheckedChange = { 
                    options[index] = options[index].copy(
                        isSelected = !options[index].isSelected
                    )
                }
            )
        }
    }
}
````
