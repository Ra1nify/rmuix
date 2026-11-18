# FloatingActionButton

`FloatingActionButton` is a floating button component in Rmuix, typically used to display the most important or frequently used actions on a page. It usually floats above the interface with a prominent visual effect for quick user access.

This component is typically used in conjunction with the `Scaffold` component to maintain consistent layout and behavior across different pages in the application.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=floatingActionButton" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass reads `COUIFABPressFeedbackUtil` / `COUIFABShadowUtils` and the `coui_floating_button_fab_*` / `coui_shadow_*_caustic_fab` dimens of the Settings APK.

- The regular button is `coui_floating_button_fab_normal_size` = **56dp** (the library used 60dp), and the mini variant is `coui_floating_button_fab_mini_size` = **54dp** — not the Material 40dp.
- The button keeps `coui_floating_button_fab_side_margin` = **12dp** from the screen edge; it is exposed as `FloatingActionButtonDefaults.SideMargin`.
- The stock shadow elevation is `coui_shadow_elevation_caustic_fab` = **8dp** (the library used 4dp). The caustic shadow's other tokens (`coui_shadow_light_*_caustic_fab`) belong to the OPPO light-effect shadow pipeline, which is not ported.
- The rest of the visual (circle shape, primary container) already matched the Material contract.

**BREAKING:** default size 60dp → 56dp and default shadow elevation 4dp → 8dp; `FloatingActionButtonDefaults` gains `MiniSize` and `SideMargin`.

**Still under review / corrections:** the caustic/light shadow pipeline (`COUIFABShadowUtils`,
`coui_shadow_light_*`) is not modelled. The earlier note that "press feedback (`COUIFABPressFeedbackUtil`) is
left to the shared `Surface` indication" mixed up two views: `COUIFABPressFeedbackUtil` is only used by
`COUIFloatingButtonLabel` (the extended-FAB child buttons: scale 1.0 -> 0.9 over 200ms, back over 340ms, both
`COUIMoveEaseInterpolator` = cubic-bezier(0.3, 0, 0.1, 1)); the plain `COUIFloatingButton` keeps its press
constants (`DEFAULT_PRESS_FEEDBACK_SCALE_MIN_VALUE` 0.9, 200ms) but `getScaleValue()` / `getBrightnessValue()`
have no call sites, and its actual feedback is the shared `COUIMaskEffectDrawable` state mask
(`COUIStateEffectDrawable`, response 0.3 / bounce 0), which the library reproduces through the theme's
`RmuixIndication`. Only the extended-FAB label scale animation is not modelled.
## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.FloatingActionButton
```

## Basic Usage

The FloatingActionButton component can be used to trigger important actions:

```kotlin
FloatingActionButton(
    onClick = { /* Handle click event */ }
) {
    Icon(
        imageVector = RmuixIcons.Add,
        contentDescription = "Add"
    )
}
```

## Component States

Since this component is typically used for the most common actions, it does not have built-in state variations.

## Properties

### FloatingActionButton Properties

| Property Name              | Type                   | Description                         | Default Value                  | Required |
| -------------------------- | ---------------------- | ----------------------------------- |--------------------------------| -------- |
| onClick                    | () -> Unit             | Callback triggered when clicked     | -                              | Yes      |
| modifier                   | Modifier               | Modifier applied to the button      | Modifier                       | No       |
| shape                      | Shape                  | Shape of the button                 | CircleShape           | No       |
| containerColor             | Color                  | Background color of the button      | RmuixTheme.colorScheme.primary | No       |
| shadowElevation            | Dp                     | Shadow elevation of the button      | FloatingActionButtonDefaults.ShadowElevation | No       |
| minWidth                   | Dp                     | Minimum width of the button         | FloatingActionButtonDefaults.MinWidth  | No       |
| minHeight                  | Dp                     | Minimum height of the button        | FloatingActionButtonDefaults.MinHeight | No       |
| content                    | @Composable () -> Unit | Composable content of the button    | -                              | Yes      |

### FloatingActionButtonDefaults Object

The FloatingActionButtonDefaults object provides default values for FloatingActionButton components.

#### Constants

| Constant Name   | Type | Description                    | Default Value |
| --------------- | ---- | ------------------------------ | ------------- |
| MinWidth        | Dp   | Minimum width of the button (`coui_floating_button_fab_normal_size`) | 56.dp |
| MinHeight       | Dp   | Minimum height of the button (`coui_floating_button_fab_normal_size`) | 56.dp |
| ShadowElevation | Dp   | Shadow elevation of the button (`coui_shadow_elevation_caustic_fab`) | 8.dp |
| MiniSize        | Dp   | Mini FAB size (`coui_floating_button_fab_mini_size`) | 54.dp |
| SideMargin      | Dp   | Minimum distance from the screen edge (`coui_floating_button_fab_side_margin`) | 12.dp |

## Advanced Usage

### Custom Color

```kotlin
FloatingActionButton(
    onClick = { /* Handle click event */ },
    containerColor = Color.Red
) {
    Icon(
        imageVector = RmuixIcons.Favorites,
        contentDescription = "Favorites",
        tint = Color.White
    )
}
```

### Extended Floating Action Button

```kotlin
FloatingActionButton(
    onClick = { /* Handle click event */ },
    shape = RoundedCornerShape(16.dp),
    minWidth = 120.dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            imageVector = RmuixIcons.Add,
            contentDescription = "Add",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add", color = Color.White)
    }
}
```

### Floating Action Button with Scaffold

```kotlin
Scaffold(
    floatingActionButton = {
        FloatingActionButton(
            onClick = { /* Handle click event */ }
        ) {
            Icon(
                imageVector = RmuixIcons.Add,
                contentDescription = "Add"
            )
        }
    },
    floatingActionButtonPosition = FabPosition.End
) { paddingValues ->
    // Content area needs to consider padding
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // ...
    }
}
```

### Animated Floating Action Button

```kotlin
var expanded by remember { mutableStateOf(false) }
val animatedSize by animateDpAsState(
    targetValue = if (expanded) 65.dp else 60.dp,
    label = "FAB size animation",
)

FloatingActionButton(
    onClick = { expanded = !expanded },
    minWidth = animatedSize,
    minHeight = animatedSize
) {
    Icon(
        imageVector = if (expanded) RmuixIcons.Remove else RmuixIcons.Add,
        contentDescription = if (expanded) "Remove" else "Add",
        tint = Color.White
    )
}
```
