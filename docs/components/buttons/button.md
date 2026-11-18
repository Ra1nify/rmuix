# Button

## Shared Stock Spotlight

Spotlight calls now reuse the [shared Spotlight](../media/spotlight): the official lighting shader, presets and springs replace the old power glow/radial fallback, and blending occurs after content. This shared-engine replacement does not certify each component's system material gate or preset selection. Button/Checkbox spotlight options still default off.

BREAKING: default spotlight tint is white; arbitrary internal 40/140/380dp radii are removed. Explicit tint and radius overrides remain available through the public modifier.


`Button` is a basic interactive component in Rmuix, used to trigger actions or events. It provides multiple style options, including primary buttons, secondary buttons, and text buttons.

<div style="position: relative; height: 592px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=button" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Button
import io.github.ra1nify.rmuix.kmp.basic.ButtonDeformAnchor
import io.github.ra1nify.rmuix.kmp.basic.ButtonTouchMotionConfig
import io.github.ra1nify.rmuix.kmp.basic.LocalButtonTouchMotionConfig
import androidx.compose.runtime.CompositionLocalProvider
```

## Basic Usage

The Button component can be used to trigger actions or events:

```kotlin
Button(
    onClick = { /* Handle click event */ }
) {
    Text("Button")
}
```

## Button Types

Rmuix provides various types of buttons suitable for different scenarios and levels of importance:

### Primary Button

```kotlin
Button(
    onClick = { /* Handle click event */ },
    colors = ButtonDefaults.buttonColorsPrimary()
) {
    Text("Primary Button")
}
```

### Secondary Button

```kotlin
Button(
    onClick = { /* Handle click event */ },
    colors = ButtonDefaults.buttonColors()
) {
    Text("Secondary Button")
}
```

### Text Button

```kotlin
TextButton(
    text = "Text Button",
    onClick = { /* Handle click event */ }
)
```

## Component States

### Disabled State

```kotlin
Button(
    onClick = { /* Handle click event */ },
    enabled = false
) {
    Text("Disabled Button")
}
```

## Stock ColorOS Baseline

The large tier has a 132×44 dp minimum and 12×8 dp content padding. The default radius uses the stock -1 sentinel and adapts to half the current height instead of staying at 21.5 dp. TextButton is single-line with end ellipsis by default. Explicit positive radii and textStyle remain available.
A secondary button is not always a translucent overlay: its enabled container uses Android ColorUtils integer compositing over elevated, yielding #FFEBEBEB / #FF343434 by default. Disabled containers retain #14000000 / #1AFFFFFF. Disabled fill/text values follow their own selectors, not a generic disabledPrimary role.
Default Sink compression follows the official area/aspect mapping, not a fixed 0.97 for every size. Press/release use the separate Candy/Jelly springs, and a short-tap mask reaches 70% progress before exiting. Hover/press draw before content; keyboard focus uses an outer stroke rather than a generic MIUI indication. Keyboard/synthetic PressInteraction does not impersonate MotionEvent compression.
ButtonColors and TextButtonColors gain optional trailing hoverColor, pressColor and focusOutlineColor fields. Color.Unspecified resolves the baseline role; Color.Transparent explicitly suppresses that color layer. All five color factories expose these overrides and TextButton forwards them.
BREAKING: MinWidth 58→132 dp, horizontal padding 16→12 dp and CornerRadius 21.5 dp→-1 (adaptive); enableSpotlight true→false and indication defaults to null. Secondary compositing, disabled defaults, size-mapped press behavior and TextButton single-line rendering change. Existing parameter names and order remain intact.
Native material/blur/shadow edges and exact Oplus contours remain outstanding. The deformation/anchor math and composition baseline below does not replace device frame-by-frame validation. The squircle contour and explicit spotlight/destructive application extensions retain their stated limitations.

### Rubber Drag Contribution

Default Sink now includes the official rubber translation contribution. Its 5px gate uses local event coordinates without density scaling or slop subtraction. X/Y map independently through a nonlinear curve, with a 28dp limit converted using native ties-to-even rounding. Moving back inside the gate does not forcibly reset the last target.
Following uses response 0.15 / bounce 0.15. Release response attenuates from 0.42 to 0.45 by area, with the original velocity units, remaining distance and velocity cap determining initial velocity/bounce. Regrabbing uses the inverse curve; a newly added pointer becomes active and lifting it selects the first remaining pointer without losing displacement.
Rubber updates drawing transforms only: it does not invoke onClick or consume pointer events. Existing clickable handling still determines activation. Parent-consumed movement cancels with zero gesture velocity. PressFeedbackType.None retains no physical scale/translation while color/focus feedback stays independent. Disabling or disposing cancels motion.
Rubber is an independent translation contribution. Nonuniform deformation and anchor composition are now provided as described next; native material/contour validation remains outstanding. Edge/cancel demos retain click-count and No motion comparisons.

### Nonuniform Deformation And Anchors

Deformation targets follow the source area/aspect attenuation: the 38416 threshold uses raw pixel area, not dp², with a 1.2 maximum and 1.03 floor. Directional stretch pairs with reciprocal compression; diagonal targets blend logarithms by displacement weights. Area conservation describes targets, not every intermediate animation frame.
The collector adds scale deviations: combined = uniform + deform - 1, not multiplication. Anchor compensation uses this combined scale and actual pixel size, adding (size/2 - pivot) × (combined - 1) to rubber translation. transformOrigin is not changed a second time.
Following uses .15/.15 springs and .0005×.75 visibility threshold. On release, axes within abs(scale-1)<.002 snap to unity; other axes use the source hand-up calculation with scale-delta rest magnitude, not pixel displacement or a substituted 150dp velocity denominator. Regrabbing inverts the dominant deformation axis without inventing its old sign.
Center and autoDeformAnchor=false remain the default. LocalButtonTouchMotionConfig supplies explicit anchor presets; automatic selection follows the local dominant axis, retains a prior choice on ties and persists across release until reset. Changing the configuration scope rebuilds that contribution; arbitrary native live-config/unset-pivot details are not claimed.

| Configuration | Type | Default |
| --- | --- | --- |
| deformAnchor | ButtonDeformAnchor | Center |
| autoDeformAnchor | Boolean | false |

Explicit opt-in anchor configuration; default calls remain unchanged:

```kotlin
CompositionLocalProvider(
    LocalButtonTouchMotionConfig provides ButtonTouchMotionConfig(
        deformAnchor = ButtonDeformAnchor.Center,
        autoDeformAnchor = true
    )
) {
    Button(onClick = {}) { Text("Auto anchor") }
}
```


### Pressed fill vs press mask (2026-11-12, resolved)

`COUIButton.changeButtonAlphaColor(pressed, buttonType)` swaps the button's own fill (`mDrawableColor`) per type — `PRIMARY` keeps `couiColorContainerTheme`, `SECONDARY` becomes white at 0xA6 (light) / 0x33 (dark), `TRANSPARENT` white at 0x33 / 0x19. **But it is only reachable from `applyBlurEffect()`**, i.e. inside the material/blur (glass) path: `COUIMaterialEffect.isMaterialVersion() && mStyle != 0 && mIsDrawMaterialEffect`.

In that same method the source also calls `mMaskDrawable.enablePressedState(!isBlurEffectEnabled())`: when the blur effect is active the **press mask is disabled** and the fill is swapped instead; for ordinary buttons the mask stays and the fill never changes.

The gate is `COUIEffectVersionUtil.isMaterialVersion()` = `checkOPlusViewSubSDK(40, 2)`, i.e. true on OS version code 40.2 and above, and each button still opts in through the `useMaterialEffect` style attribute. In the APK the enum is `FORCE_ENABLE(1)` / `FORCE_DISABLE(0)` / `FOLLOW_SYSTEM(-1)` and only the AI-payment styles set it explicitly (`followSystem`); `Widget.COUI.Button.Large.ButtonNew.Secondary.NoMargin` sets `forceDisable`, and every other button style falls back to `FOLLOW_SYSTEM`, where the effect follows `COUIMaterialStateManager` at runtime. So the standard `Button` matches `COUIBackgroundDrawable`'s mask model, and the white-fill behaviour belongs to a **material/blur button variant that this library does not ship** (there are glass dialog / bottom sheet / popup / tab row / top app bar / icon button / navigation bar components, but no glass button). No divergence for `Button`, and no code change was made for it.

## Properties

### Button Properties

| Property Name     | Type                            | Description                            | Default Value                 | Required |
| ----------------- | ------------------------------- | -------------------------------------- | ----------------------------- | -------- |
| onClick           | () -> Unit                      | Callback triggered on click            | -                             | Yes      |
| modifier          | Modifier                        | Modifier applied to the button         | Modifier                      | No       |
| enabled           | Boolean                         | Whether the button is clickable        | true                          | No       |
| enableSpotlight | Boolean | Extra non-stock shader extension | false | No |
| spotlightColor | Color | Optional shared light color, not a press-mask color | Color.White | No |
| pressFeedbackType | PressFeedbackType | Physical scaling; Sink uses size-mapped compression | PressFeedbackType.Sink | No |
| cornerRadius      | Dp                              | Corner radius of the button            | ButtonDefaults.CornerRadius   | No       |
| minWidth          | Dp                              | Minimum width of the button            | ButtonDefaults.MinWidth       | No       |
| minHeight         | Dp                              | Minimum height of the button           | ButtonDefaults.MinHeight      | No       |
| colors            | ButtonColors                    | Button color configuration             | ButtonDefaults.buttonColors() | No       |
| insideMargin      | PaddingValues                   | Internal padding of the button         | ButtonDefaults.InsideMargin   | No       |
| interactionSource | MutableInteractionSource?       | Interaction source for the button      | null                          | No       |
| indication        | Indication?                     | Indication for click interactions      | null       | No       |
| content           | @Composable RowScope.() -> Unit | Composable function for button content | -                             | Yes      |

### TextButton Properties

| Property Name     | Type                      | Description                       | Default Value                     | Required |
| ----------------- | ------------------------- | --------------------------------- | --------------------------------- | -------- |
| text              | String                    | Text displayed on the button      | -                                 | Yes      |
| onClick           | () -> Unit                | Callback triggered on click       | -                                 | Yes      |
| modifier          | Modifier                  | Modifier applied to the button    | Modifier                          | No       |
| enabled           | Boolean                   | Whether the button is clickable   | true                              | No       |
| enableSpotlight | Boolean | Extra non-stock shader extension | false | No |
| spotlightColor | Color | Optional shared light color, not a press-mask color | Color.White | No |
| pressFeedbackType | PressFeedbackType | Physical scaling; Sink uses size-mapped compression | PressFeedbackType.Sink | No |
| cornerRadius      | Dp                        | Corner radius of the button       | ButtonDefaults.CornerRadius       | No       |
| minWidth          | Dp                        | Minimum width of the button       | ButtonDefaults.MinWidth           | No       |
| minHeight         | Dp                        | Minimum height of the button      | ButtonDefaults.MinHeight          | No       |
| colors            | TextButtonColors          | Text button color configuration   | ButtonDefaults.textButtonColors() | No       |
| insideMargin      | PaddingValues             | Internal padding of the button    | ButtonDefaults.InsideMargin       | No       |
| textStyle         | TextStyle?                | Text style of the button text     | null (RmuixTheme.textStyles.button)| No      |
| interactionSource | MutableInteractionSource? | Interaction source for the button | null                              | No       |
| indication        | Indication?               | Indication for click interactions | null           | No       |

### ButtonDefaults Object

The ButtonDefaults object provides default values and color configurations for button components.

#### Constants

| Constant Name | Type          | Description                    | Default Value        |
| ------------- | ------------- | ------------------------------ | -------------------- |
| MinWidth      | Dp            | Minimum width of the button    | 132.dp                |
| MinHeight     | Dp            | Minimum height of the button   | 44.dp                |
| CornerRadius  | Dp            | Corner radius of the button    | (-1).dp (adaptive half-height)              |
| InsideMargin  | PaddingValues | Internal padding of the button | PaddingValues(horizontal = 12.dp, vertical = 8.dp) |

#### Methods

| Method Name               | Type             | Description                                            |
| ------------------------- | ---------------- | ------------------------------------------------------ |
| buttonColors() | ButtonColors | Secondary: enabled uses the native integer composite over elevated; disabled keeps container8 |
| buttonColorsPrimary()     | ButtonColors     | Creates color configuration for primary buttons        |
| textButtonColors()        | TextButtonColors | Creates color configuration for secondary text buttons |
| textButtonColorsPrimary() | TextButtonColors | Creates color configuration for primary text buttons   |

## Advanced Usage

### Button with Icon

```kotlin
Button(
    onClick = { /* Handle click event */ }
) {
    Icon(
        imageVector = RmuixIcons.Favorites,
        contentDescription = "Favorites"
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text("Button with Icon")
}
```

### Custom Style Button

```kotlin
Button(
    onClick = { /* Handle click event */ },
    colors = ButtonDefaults.buttonColors(
        color = Color.Red.copy(alpha = 0.7f)
    ),
    cornerRadius = 8.dp
) {
    Text("Custom Button")
}
```

### Loading State Button

```kotlin
var isLoading by remember { mutableStateOf(false) }
val scope = rememberCoroutineScope()

Button(
    onClick = {
        isLoading = true
        // Simulate operation
        scope.launch {
            delay(2000)
            isLoading = false
        }
    },
    enabled = !isLoading
) {
     AnimatedVisibility(
        visible = isLoading
    ) {
        CircularProgressIndicator(
            modifier = Modifier.padding(end = 8.dp),
            size = 20.dp,
            strokeWidth = 4.dp
        )
    }
    Text("Submit")
}
```
