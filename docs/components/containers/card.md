# Card

## Shared Stock Spotlight

Spotlight calls now reuse the [shared Spotlight](../media/spotlight): the official lighting shader, presets and springs replace the old power glow/radial fallback, and blending occurs after content. This shared-engine replacement does not certify each component's system material gate or preset selection. Button/Checkbox spotlight options still default off.

BREAKING: default spotlight tint is white; arbitrary internal 40/140/380dp radii are removed. Explicit tint and radius overrides remain available through the public modifier.

**BREAKING (stock surface, 2026-11-17):** every stock card drawable (`card_preference_normal_background.xml`,
the `about_device_*_card_*` family, `coui_component_card_*`) fills with `?attr/couiColorCardBackground` =
`@color/coui_color_card_background` (**#FFFFFF** light / **#1AFFFFFF** dark), and card preferences take
`?attr/couiColorLabelPrimary` for their text. `CardDefaults.defaultColors()` now uses that pair instead of
the theme's `surfaceContainer` / `onSurfaceContainer`.


`Card` is a basic container component in Rmuix, used to hold related content and actions. It provides a card container with Rmuix style, suitable for scenarios such as information display and content grouping. Supports both static display and interactive modes.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=card" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.utils.PressFeedbackType // If using interactive card
```

## Basic Usage

The Card component can be used to wrap and organize content (static card):

```kotlin
Card {
    Text("This is card content")
}
```

## Properties

### Card Properties

| Property Name     | Type                               | Description                               | Default Value                | Required | Applies To  |
| ----------------- | ---------------------------------- | ----------------------------------------- | ---------------------------- | -------- | ----------- |
| modifier          | Modifier                           | Modifier applied to the card              | Modifier                     | No       | All         |
| cornerRadius      | Dp                                 | Card corner radius                        | CardDefaults.CornerRadius    | No       | All         |
| cornerTier        | CardCornerTier?           | One of the eight stock tiers; overrides `cornerRadius` when set (table below) | null |
| cornerScale       | CardCornerScale           | Which radius family a tier resolves to: `LEGACY` / `NORMAL_16_1` / `LARGE_16_1` | `LARGE_16_1` |
| cornerExtension   | Float                     | Smooth-corner weight forwarded to `squircleSurface` | `CardDefaults.CornerExtension` (1.1) |
| insideMargin      | PaddingValues                      | Card inner padding                        | CardDefaults.InsideMargin    | No       | All         |
| colors            | CardColors                         | Card color configuration                  | CardDefaults.defaultColors() | No       | All         |
| pressFeedbackType | PressFeedbackType                  | Feedback type when pressed                | PressFeedbackType.None       | No       | Interactive |
| showIndication    | Boolean                            | Show indication on interaction            | false                        | No       | Interactive |
| holdDownState     | Boolean                            | Whether the card is in the pressed state  | false                        | No       | Interactive |
| onClick           | (() -> Unit)?                      | Callback when clicked                     | null                         | No       | Interactive |
| onLongPress       | (() -> Unit)?                      | Callback when long pressed                | null                         | No       | Interactive |
| content           | @Composable ColumnScope.() -> Unit | Composable function for card content area | -                            | Yes      | All         |

::: warning
Some properties are only available when creating an interactive card!
:::

### CardDefaults Object

The CardDefaults object provides default values and color configurations for the card component.

#### Constants

| Constant Name | Type          | Description        | Default Value       |
| ------------- | ------------- | ------------------ | ------------------- |
| CornerRadius  | Dp            | Card corner radius (`?attr/couiRoundCornerXL` = `coui_round_corner_xl`) | 20.dp |
| InsideMargin  | PaddingValues | Card inner padding | PaddingValues(0.dp) |

#### Methods

| Method Name     | Type       | Description                |
| --------------- | ---------- | -------------------------- |
| defaultColors() | CardColors | The default color for card |

### CardColors Class

| Property Name | Type  | Description                      |
| ------------- | ----- | -------------------------------- |
| color         | Color | Default background color of card |
| contentColor  | Color | Default content color of card    |


## Advanced Usage

### Custom Style Card

```kotlin
Card(
    cornerRadius = 8.dp,
    insideMargin = PaddingValues(16.dp),
    colors = CardDefaults.defaultColors(
        color = RmuixTheme.colorScheme.primaryVariant
    ),
) {
    Text("Custom Style Card")
}
```

### Content-Rich Card

```kotlin
Card(
    modifier = Modifier.padding(16.dp),
    insideMargin = PaddingValues(16.dp)
) {
    Text(
        text = "Card Title",
        style = RmuixTheme.textStyles.title2
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "This is a detailed description of the card, which can contain multiple lines of text."
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(
            text = "Cancel",
            onClick = { /* Handle cancel event */ }
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(
            text = "Confirm",
            colors = ButtonDefaults.textButtonColorsPrimary(), // Use theme colors
            onClick = { /* Handle confirm event */ }
        )
    }
}
```

### Cards in a List

```kotlin
LazyColumn {
    items(5) { index ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            insideMargin = PaddingValues(16.dp)
        ) {
            Text("List Item ${index + 1}")
        }
    }
}
```

### Interactive Card

```kotlin
Card(
    modifier = Modifier.padding(16.dp),
    pressFeedbackType = PressFeedbackType.Sink, // Set press feedback to sink effect
    showIndication = true, // Show indication on click
    onClick = {/* Handle click event */ },
    onLongPress = {/* Handle long press event */ }
) {
    Text("Interactive Card")
}
```


`PressFeedbackType.Sink` reproduces `COUIPressFeedbackJumpPreference`'s `COUIPressFeedbackHelper(view, 0)`: the
measured card is mapped through `PressCalc.calculateUniformScaleBreakdown()` to **0.85..0.97** (0.97 once the
card area exceeds the 150dp mapping limit, which most list cards do) and animated with the `PressParamsExtKt`
jelly springs (press response 0.25 / bounce 0, release response 0.5 / bounce 0.6). `Tilt` remains the library's
own tilt effect.

### The eight stock corner tiers (`CardCornerTier`)

| Tier | Display | legacy draw | 16.1 normal | 16.1 large | Weight |
| :--- | ---: | ---: | ---: | ---: | ---: |
| XS | 4dp | 4dp | 5dp | 5dp | 1.5 |
| S | 8dp | 8dp | 10dp | 11dp | 1.5 |
| M | 12dp | 9dp | 15dp | 17dp | 0.99 |
| L | 16dp | 13dp | 19dp | 23dp | 0.99 |
| XL | 20dp | 16dp | 24dp | 28dp | 1.1 |
| XXL | 24dp | 19dp | 29dp | 34dp | 1.1 |
| XXXL | 28dp | 22dp | 34dp | 39dp | 1.1 |
| XXXXL | 32dp | 25dp | 38dp | 45dp | 1.1 |

> `RoundCornerUtil.getRoundCornerForOS17()` only reads `*_radius_large_16_1` (keyed by attributes such as
> `couiRoundCornerXXL`), so the `*_radius_normal_16_1` family is only used when the device disables the smooth styling.
>
> **Smoothness weight.** Two different "weights" live in the dump: the per-tier `coui_round_corner_<tier>_weight`
> (1.5 / 0.99 / 1.1, exposed here as `CardCornerTier.weight`) and a *global* smooth weight in `RoundCornerUtil`
> (`DEFAULT_WIGHT = 170` / `DEFAULT_WIGHT_DIVISOR = 100` = **1.7** by default, `WEIGHT_16_1_NORMAL` = **2.5**,
> `WEIGHT_16_1_LARGE` = **3.0**, `NON_WEIGHT` = 2.0). How either number maps onto `SquircleShape.extension` is still
> unproven, so `cornerExtension` keeps its measured default instead of being derived from them.
## Squircle extension and a fixed tier bug

`Card` forwards its corner radius to `squircleSurface`, whose corner-tile size is an `extension` factor
(1.0 = circular arc, 1.1 = continuous corner). `CardDefaults.CornerExtension` exposes it. The official
tiers carry a "smooth round corner weight" of **3** for the 16_1 families, but that weight belongs to the
stock shader's parameterisation and has **no known conversion** to `extension`, so the default stays
`SquircleDefaults.Extension` (1.1) rather than a guessed mapping.

> Fixed in the same pass: `BasicCard` previously passed the raw `cornerRadius` parameter to
> `squircleSurface` instead of `resolvedCornerRadius`, so setting `cornerTier` changed the spotlight shape
> but **not** the drawn card corners.

## One silhouette for both layers

`Card` used to pass a `RoundedCornerShape` to `Modifier.rmuixSpotlight(shape = …)` while painting a squircle
background underneath, so the spotlight's outline was a circular arc on top of a continuous-curvature card.
`SquircleShape` (in `rmuix-squircle`) closes that: it builds the same `Path.addSquircleRect` silhouette as
`squircleSurface` / `squircleClip`, so `Card` now feeds one shape to both layers.

```kotlin
val shape = SquircleShape(resolvedCornerRadius, cornerExtension)
Box(Modifier.rmuixSpotlight(shape = shape).squircleSurface(colors.color, resolvedCornerRadius, cornerExtension))
```

`SquircleShape` returns an `Outline.Generic` path (resolution independent, but not an analytical
`Outline.Rounded`), and honours `squircleEnabled = false` by emitting a plain rounded rectangle.

## Degradation contract

`squircleSurface` falls back to `.clip(RoundedCornerShape(…))` when the shader brush is unavailable, so the
spotlight outline has to degrade **with** it. `Card` therefore passes `isSquircleEnabled()` into
`SquircleShape(cornerRadius, extension, squircleEnabled)`; flipping the composition-local off makes both
layers fall back to the same rounded rectangle.

`Modifier.rmuixSpotlight` handles the `Outline.Generic` that `SquircleShape` returns through
`clipOutline`, which maps it to `clipPath(outline.path)` — so the glow really is clipped to the squircle
silhouette rather than just given the right radius.