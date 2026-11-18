# Checkbox

## Shared Stock Spotlight

Spotlight calls now reuse the [shared Spotlight](../media/spotlight): the official lighting shader, presets and springs replace the old power glow/radial fallback, and blending occurs after content. This shared-engine replacement does not certify each component's system material gate or preset selection. Button/Checkbox spotlight options still default off.

BREAKING: default spotlight tint is white; arbitrary internal 40/140/380dp radii are removed. Explicit tint and radius overrides remain available through the public modifier.


`Checkbox` is a basic selection component in Rmuix, supporting three states: checked, unchecked, and indeterminate. It provides an interactive selection control with animation effects, suitable for multiple selection scenarios and enabling/disabling configuration items.

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=checkbox" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Checkbox
import androidx.compose.ui.state.ToggleableState
```

## Basic Usage

The Checkbox component can be used to create selectable options:

```kotlin
var state by remember { mutableStateOf(ToggleableState.Off) }

Checkbox(
    state = state,
    onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On }
)
```

## Component States

### Stock ColorOS Artwork

The 24 dp viewport contains an 18 dp checked square with 4 dp circular corners. The unchecked outline uses its own 16.4 dp centerline geometry and 1.3 dp stroke; it is not an enlarged copy of the checked box.
The initial/static item uses the official filled checkmark. Its indeterminate box is offset down by 0.666 dp and has a separate horizontal mark, not a flattened checkmark. The animated selector retains its vector end frame after a transition; it does not jump back to the initial/static artwork.
Transitions preserve the resource's separate fill, outline, check-trim and dash-clipping channels. Most last 300 ms; indeterminate-to-checked lasts 351 ms because one nested X-scale returns later. Disabled transitions between checked and indeterminate use the corresponding muted colors.

BREAKING: `BoxSize` changes from 20 dp to 18 dp and `CornerRadius` from 7 dp to 4 dp. `enableSpotlight` now defaults to `false`: the custom shader spotlight and 0.92x press compression are not present in the official checkbox. Explicit `enableSpotlight = true` remains available as a non-stock extension. `checkedForegroundColor` defaults to white; disabled fill uses primary at 30% alpha and the disabled mark uses `#8AFFFFFF / #29FFFFFF`.

Interrupted transitions restart at time zero from the previous target state, following the non-reversible stock selector. Changing enabled state always selects a static item. While remaining disabled, only the explicitly defined checked/indeterminate pair can animate.
### Interaction Feedback

Interactive checkboxes use the stock 20 dp circular state-layer radius, extending beyond the 24 dp artwork without scaling it. Hover and focus are independent layers, each using response 0.3 / bounce 0 springs and Android `ArgbEvaluator` linear ARGB interpolation. Focus follows the library primary color with `#1F` / `#33` light/dark alpha.
Android presses delegate to the system `RippleDrawable`, including the touch hotspot and its movement; they are not approximated by a generic Compose indication or custom spotlight. Disabled and read-only controls have no state feedback. `enableSpotlight` controls only the extra non-stock shader extension, not native feedback. The demos include a keyboard-focus comparison.
Non-Android renderers currently use an explicitly separate software ripple fallback based on the local Android SDK solid-ripple implementation. This is not certified as the ColorOS material/noise shader. Older Android resource overlays, non-Android material rendering and device frame-by-frame validation remain outstanding. The Android native path also depends on the host framework; cross-vendor material differences still require comparison against a stock ColorOS device.

### Disabled State

```kotlin
Checkbox(
    state = ToggleableState.Off,
    onClick = null,
    enabled = false
)
```

### Indeterminate State

The indeterminate state is typically used to represent a parent checkbox where only some child checkboxes are selected. When indeterminate, a horizontal dash is shown instead of a checkmark.

```kotlin
var parentState by remember { mutableStateOf(ToggleableState.Indeterminate) }
val childStates = remember { mutableStateListOf(true, false, true) }

// Cycle: Off → Indeterminate → On → Off
Checkbox(
    state = parentState,
    onClick = {
        parentState = when (parentState) {
            ToggleableState.Off -> ToggleableState.Indeterminate
            ToggleableState.Indeterminate -> ToggleableState.On
            ToggleableState.On -> ToggleableState.Off
        }
    }
)
```

## Properties

### Checkbox Properties

| Property Name | Type                | Description                                                    | Default Value                     | Required |
| ------------- | ------------------- | -------------------------------------------------------------- | --------------------------------- | -------- |
| state         | ToggleableState     | The current state of the Checkbox (On, Off, or Indeterminate)  | -                                 | Yes      |
| onClick       | (() -> Unit)?       | Callback when the Checkbox is clicked; `null` = non-interactive | -                                | Yes      |
| modifier      | Modifier            | Modifier applied to the Checkbox                               | Modifier                          | No       |
| colors        | CheckboxColors      | Color configuration for the Checkbox                           | CheckboxDefaults.checkboxColors() | No       |
| enabled       | Boolean             | Whether the Checkbox is interactive                            | true                              | No       |
| enableSpotlight | Boolean | Non-stock shader spotlight extension | false | No |
| spotlightColor | Color | Optional shared light color, not a press-mask color | Color.White | No |

### CheckboxDefaults Object

The CheckboxDefaults object provides the geometry tokens and default color configurations for the
Checkbox component.

#### Properties

| Property Name | Type | Description                                              | Value  |
| ------------- | ---- | -------------------------------------------------------- | ------ |
| Size          | Dp   | Total box size including its transparent margin          | 24.dp  |
| BoxSize       | Dp   | Painted artwork size, excluding the margin               | 18.dp  |
| CornerRadius  | Dp   | Corner radius of the painted square                      | 4.dp   |
| StrokeWidth   | Dp   | Outline width of the unchecked state                     | 1.3.dp |
| StateLayerRadius | Dp | Circular hover/focus/press feedback radius | 20.dp |

#### Methods

| Method Name      | Type           | Description                               |
| ---------------- | -------------- | ----------------------------------------- |
| checkboxColors() | CheckboxColors | Creates default color config for checkbox |

### CheckboxColors Class

| Property Name                    | Type  | Description                                  |
| -------------------------------- | ----- | -------------------------------------------- |
| uncheckedStrokeColor             | Color | Outline color when unchecked                 |
| disabledUncheckedStrokeColor     | Color | Outline color when unchecked and disabled    |
| checkedForegroundColor           | Color | Foreground color when checked (checkmark)    |
| uncheckedForegroundColor         | Color | Foreground color when unchecked              |
| disabledCheckedForegroundColor   | Color | Foreground color when disabled and checked   |
| disabledUncheckedForegroundColor | Color | Foreground color when disabled and unchecked |
| checkedBackgroundColor           | Color | Background color when checked                |
| uncheckedBackgroundColor         | Color | Background color when unchecked              |
| disabledCheckedBackgroundColor   | Color | Background color when disabled and checked   |
| disabledUncheckedBackgroundColor | Color | Background color when disabled and unchecked |

> The indeterminate state shares the same colors as the checked state.

## Advanced Usage

### Custom Colors

```kotlin
var state by remember { mutableStateOf(ToggleableState.Off) }

Checkbox(
    state = state,
    onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On },
    colors = CheckboxDefaults.checkboxColors(
        checkedBackgroundColor = Color.Red,
        checkedForegroundColor = Color.White
    )
)
```

### Parent-Child Checkboxes (Indeterminate Pattern)

```kotlin
val childStates = remember { mutableStateListOf(false, true, false) }
val parentState by remember {
    derivedStateOf {
        when {
            childStates.all { it } -> ToggleableState.On
            childStates.none { it } -> ToggleableState.Off
            else -> ToggleableState.Indeterminate
        }
    }
}

Column {
    Checkbox(
        state = parentState,
        onClick = {
            val newValue = parentState != ToggleableState.On
            childStates.indices.forEach { childStates[it] = newValue }
        }
    )
    childStates.forEachIndexed { index, checked ->
        Checkbox(
            state = ToggleableState(checked),
            onClick = { childStates[index] = !childStates[index] }
        )
    }
}
```

### Using with Text

```kotlin
var state by remember { mutableStateOf(ToggleableState.Off) }

Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(16.dp)
) {
    Checkbox(
        state = state,
        onClick = { state = if (state == ToggleableState.On) ToggleableState.Off else ToggleableState.On }
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = "Accept Terms and Privacy Policy")
}
```

### Using in Lists

```kotlin
val options = listOf("Option A", "Option B", "Option C")
val checkedStates = remember { mutableStateListOf(false, true, false) }

LazyColumn {
    items(options.size) { index ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                state = ToggleableState(checkedStates[index]),
                onClick = { checkedStates[index] = !checkedStates[index] }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = options[index])
        }
    }
}
```
