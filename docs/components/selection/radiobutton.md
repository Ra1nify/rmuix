# RadioButton

`RadioButton` supports selected and unselected states. The selected state displays an accent-colored circle with a white center; the unselected state displays a hollow ring. Use it for mutually exclusive choices.

<div style="position: relative; height: 384px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=radioButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.RadioButton
import io.github.ra1nify.rmuix.kmp.basic.RadioButtonDefaults
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
```

## Basic Usage

The RadioButton component is typically used within a mutually exclusive group:

```kotlin
var selectedIndex by remember { mutableIntStateOf(0) }

Column {
    RadioButtonPreference(
        title = "Option A",
        selected = selectedIndex == 0,
        onClick = { selectedIndex = 0 }
    )
    RadioButtonPreference(
        title = "Option B",
        selected = selectedIndex == 1,
        onClick = { selectedIndex = 1 }
    )
}
```

## Component States

### Stock ColorOS Rendering

The unselected state is a transparent-centered ring, not a filled disc. The selected state has a 10 dp outer radius and a 6 dp white center inside a 24 dp viewport.
Transitions follow the official vector keyframes: a 1.08x pulse peaks at 150 ms and ends at 300 ms. Deselection fades the fill over 200 ms and restores the ring over 267 ms. There is no extra press-compression animation, and a read-only control has no press handler.
The non-reversible selector retains the animated end frame. The initial/static white center is `#FFFFFF`; after an enabled selection animation the default center remains `#FAFAFA` until a static item is selected again. Custom non-white colors are preserved. Interruptions restart from the previous target, not from a reversed partial progress. Enabled-state changes select static items without an animation.

BREAKING: `SelectedInnerRadius` changes from 4.5 dp to 6 dp; `disabledSelectedColor` uses the primary color at 30% alpha instead of `disabledPrimary`; the dark disabled dot changes from `#26FFFFFF` to `#29FFFFFF`. Neutral colors follow the active library theme.

### Standalone Controls And Decorative Indicators

Interactive standalone radios use the framework control background, not the Checkbox COUI hover/focus spring masks. Android reads the framework RadioButton background and forwards checked, pressed, hovered, focused, window-focused and hotspot state. It preserves the platform radius; 20 dp is used only if a native RippleDrawable cannot be loaded.
The background has its own stateful colors: enabled/checked uses framework `colorControlActivated` at 26% alpha, not necessarily the artwork primary. Unchecked uses the official `#7FD0D0D0 / #7F707070` pair. Android resolves the host activated role; non-Android uses the explicit bare-APK `coui_theme_primary_color=#FFFFFFFF` fallback, which does not prove the effective ColorOS runtime-overlay color. Disabled or `onClick = null` indicators leave feedback to their containing row.
Non-Android renderers retain a software press fallback. Their background follows the local SDK baseline: focus takes priority over hover, with target opacity 0.6 for an active-window focus and 0.2 for hover/inactive-window focus, entering linearly over 80 ms. It does not add two independent circles or use Checkbox ARGB state-effect springs. A press does not restart an existing focus/hover background animation.
The demos include standalone interaction and keyboard-focus comparisons. Software rendering, host-framework materials and cross-vendor resource overlays still need comparison against a stock ColorOS device; this is not a full 1:1 certification.

### Background Color Override

`RadioButtonDefaults.feedbackColors(activatedColor)` configures the independent activated role and returns `checked` and `unchecked` colors. An application may explicitly bind this role to its library primary; this is an application mapping, not an inferred blue value for the APK white placeholder. The new optional trailing `feedbackColors` parameter preserves existing calls.

```kotlin
RadioButton(
    selected = selected,
    onClick = { selected = true },
    feedbackColors = RadioButtonDefaults.feedbackColors(
        activatedColor = RmuixTheme.colorScheme.primary
    )
)
```

### Disabled State

```kotlin
RadioButton(
    selected = true,
    onClick = null,
    enabled = false
)
```

## Properties

### RadioButton Properties

| Property Name | Type                | Description                                                        | Default Value                             | Required |
| ------------- | ------------------- | ------------------------------------------------------------------ | ----------------------------------------- | -------- |
| selected      | Boolean             | Whether the RadioButton is currently selected                      | -                                         | Yes      |
| onClick       | (() -> Unit)?       | Callback when the RadioButton is clicked; `null` = non-interactive | -                                         | Yes      |
| modifier      | Modifier            | Modifier applied to the RadioButton                                | Modifier                                  | No       |
| colors        | RadioButtonColors   | Color configuration for the RadioButton                            | RadioButtonDefaults.radioButtonColors()   | No       |
| enabled       | Boolean             | Whether the RadioButton is interactive                             | true                                      | No       |
| feedbackColors | RadioButtonFeedbackColors | Independent framework background colors | RadioButtonDefaults.feedbackColors() | No |

### RadioButtonDefaults Object

The RadioButtonDefaults object provides geometry tokens and default color configurations for the
RadioButton component.

#### Properties

| Property Name       | Type | Description                                  | Value  |
| ------------------- | ---- | -------------------------------------------- | ------ |
| Size                | Dp   | Total control size                           | 24.dp  |
| SelectedOuterRadius | Dp   | Outer circle radius when selected            | 10.dp  |
| SelectedInnerRadius | Dp   | Inner white dot radius when selected         | 6.dp   |
| UncheckedRingRadius | Dp   | Ring radius when unselected                  | 9.2.dp |
| StrokeWidth         | Dp   | Ring stroke width, shared with the checkbox  | 1.3.dp |
| StateLayerRadius | Dp | Feedback radius for software/native-background fallback | 20.dp |
| MarkMarginStart     | Dp   | Start margin inside a preference row         | 16.dp  |

#### Methods

| Method Name         | Type              | Description                                   |
| ------------------- | ----------------- | --------------------------------------------- |
| radioButtonColors() | RadioButtonColors | Creates default color config for radio button |

### RadioButtonColors Class

| Property Name         | Type  | Description                                     |
| --------------------- | ----- | ----------------------------------------------- |
| selectedColor         | Color | Outer circle color when selected                |
| disabledSelectedColor | Color | Outer circle color when disabled and selected   |
| uncheckedStrokeColor         | Color | Ring color when unselected                        |
| disabledUncheckedStrokeColor | Color | Ring color when unselected and disabled           |
| selectedInnerColor           | Color | Inner dot color when selected                     |
| disabledSelectedInnerColor   | Color | Inner dot color when selected and disabled        |

## Advanced Usage

### Custom Colors

```kotlin
var selected by remember { mutableStateOf(false) }

RadioButton(
    selected = selected,
    onClick = { selected = !selected },
    colors = RadioButtonDefaults.radioButtonColors(
        selectedColor = Color.Red
    )
)
```

### Using in Lists

```kotlin
val options = listOf("Option A", "Option B", "Option C")
var selectedIndex by remember { mutableIntStateOf(0) }

options.forEachIndexed { index, option ->
    Card(
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        RadioButtonPreference(
            title = option,
            selected = selectedIndex == index,
            onClick = { selectedIndex = index }
        )
    }
}
```
