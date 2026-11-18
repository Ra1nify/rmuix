# ProgressIndicator

`ProgressIndicator` is a progress indication component in Rmuix used to display the progress status of operations. It provides three styles: linear progress bar, circular progress indicator, and infinite spinning indicator, suitable for different loading and progress display scenarios.

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=progressIndicator" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass reads `COUIHorizontalProgressBar`, `COUICircularProgressBar`, the `coui_progressbar_*_selector` colour lists, `Widget.COUI.COUICircularProgressBar` and the `coui_circular_progress_*` dimens of the Settings APK.

- Linear track: `couiHorizontalProgressBarBackgroundColor` → `coui_progressbar_background_selector` → `?attr/couiColorDivider` (`#1F000000` / `#33FFFFFF`). The library used `secondaryContainer` (`#F0F0F0` / `#434343`).
- Linear fill: `coui_progressbar_progress_selector` → `?attr/couiColorPrimary` (`#0080FF` / `#1A8CFF`). The library keeps the accent role ([RmuixTheme.colorScheme.primary]) as documented elsewhere; the numeric difference is a palette question, not a component bug.
- Linear fill geometry: `COUIHorizontalProgressBar.onDraw` paints one full-width rounded rect whose trailing edge lands on the progress point, intersected with the rounded track (nothing is drawn at 0, the whole track is filled at 1). The library now paints the same and mirrors it in RTL; it previously drew a minimum capsule even at 0.
- Circular: `Widget.COUI.COUICircularProgressBar` sets `couiCircularProgressBarColor` = `couiColorHintNeutral` (`#42000000` / `#4DFFFFFF`) and `couiCircularProgressBarTrackColor` = `couiColorDivider`, i.e. a neutral ring over the divider track, not the accent. `CircularProgressIndicator` now defaults to `circularProgressIndicatorColors()` for that reason.
- Circular presets: medium is 30dp / 3dp stroke, large is 40dp / 5dp (`coui_circular_progress_medium_length` etc.). `Widget.COUI.COUICircularProgressBar` declares `couiCircularProgressBarSize = large_size`, so the stock default is the **large** preset; the component default moves from the medium preset to 40dp / 5dp.
- The official widgets are plain `ProgressBar` subclasses whose `state_enabled` drives the selectors, but the library's `enabled` argument does not exist yet, so the disabled colours stay unused.

**BREAKING:** the linear track becomes `couiColorDivider`, `CircularProgressIndicator` switches its default colours to the neutral ring + divider track, and the default circular preset becomes the stock **large** one (40dp / 5dp) instead of the medium 30dp / 3dp. `ProgressIndicatorDefaults` gains `circularProgressIndicatorColors()`.

- The circular preset pair is now explicit: the component default is the **large** preset (40dp / 5dp, the stock `large_size`), exposed as `ProgressIndicatorDefaults.LargeCircularSize` / `LargeCircularStrokeWidth`; the medium pair (30dp / 3dp) stays available as the internal stock values for callers that need it.

**Still under review:** the disabled selectors (`couiProgressbarBackgroundColorDisabled` / `couiProgressbarProgressColorDisabled`) are declared but never assigned in this APK's themes, so the disabled palette cannot be pinned down; an `enabled` parameter does not exist; the large 40dp / 5dp preset is ported, and so are the pause/error overlays (`CircularProgressIndicatorState.Paused` / `.Error`, drawn from the stock primitives); the drop shadow is opt-in in the source (`COUICircularProgressBar.setIsDrawShadow(mProgressBarType == 1)`, and the default type is 0), so the default ring draws none; its tokens are `coui_circular_progress_shadow_radius` 4dp, `_y_bias` 2dp and `#19000000`, and they are not ported. `COUICompProgressIndicator` (a lottie loader with a 10sp caption) is **deliberately not ported**: the component plays a proprietary OPPO animation asset shipped inside the vendor APK (395 lottie JSON files live in `assets/`), and copying that artwork into this Apache-2.0 library is a licensing problem. Callers that own such an asset can drive `animateFloatAsState` + `InfiniteProgressIndicator` themselves.
## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.LinearProgressIndicator // Linear progress bar
import io.github.ra1nify.rmuix.kmp.basic.CircularProgressIndicator // Circular progress indicator
import io.github.ra1nify.rmuix.kmp.basic.InfiniteProgressIndicator // Infinite spinning indicator
```

## Basic Usage

### Linear Progress Bar

Linear progress bar can be used to show operation progress:

```kotlin
// Linear progress bar with determinate progress
var progress by remember { mutableStateOf(0.3f) }

LinearProgressIndicator(progress = progress)
```

```kotlin
// Linear progress bar with indeterminate progress
LinearProgressIndicator()
```

### Circular Progress Indicator

Circular progress indicator is suitable for space-saving scenarios:

```kotlin
// Circular progress indicator with determinate progress
var progress by remember { mutableStateOf(0.7f) }

CircularProgressIndicator(progress = progress)
```

```kotlin
// Circular progress indicator with indeterminate progress
CircularProgressIndicator()
```

### Infinite Progress Indicator

Infinite progress indicator is suitable for scenarios where operation duration is uncertain:

```kotlin
InfiniteProgressIndicator()
```

## Component States

All progress indicator components support both determinate and indeterminate progress states:

### Determinate Progress State

When a specific progress value (float between 0.0-1.0) is provided, the progress indicator shows exact progress:

```kotlin
var progress by remember { mutableStateOf(0.6f) }

LinearProgressIndicator(progress = progress)
CircularProgressIndicator(progress = progress)
```

### Indeterminate Progress State

When the progress value is null, the progress indicator shows an animation indicating an ongoing operation with unknown progress:

```kotlin
LinearProgressIndicator(progress = null)
CircularProgressIndicator(progress = null)
```

### Circular shadow (2026-11-14)

`CircularProgressIndicator(showShadow = true)` paints the stock shadow layer:
`coui_circular_progress_shadow_radius` 4dp, `_x_bias` 0dp, `_y_bias` 2dp, `_color` `#19000000`.
It is **off by default** because the stock drawable only installs the layer for
`couiCircularProgressBarType == 1` (`setIsDrawShadow(1 == mProgressBarType)`), and that type defaults to 0 -
the stock default has no shadow either. `ProgressIndicatorDefaults.ShadowRadius` / `ShadowYBias` / `ShadowColor`
expose the three values; `ProgressShadowRenderingTest` pins that the drawn layer darkens the area below the ring.

## Properties

### LinearProgressIndicator Properties

| Property Name | Type                    | Description                                    | Default Value                                                  | Required |
| ------------- | ----------------------- | ---------------------------------------------- | -------------------------------------------------------------- | -------- |
| modifier      | Modifier                | Modifier applied to the progress bar           | Modifier                                                       | No       |
| progress      | Float?                  | Current progress value, null for indeterminate | null                                                           | No       |
| colors        | ProgressIndicatorColors | Color configuration for the progress bar       | ProgressIndicatorDefaults.progressIndicatorColors()            | No       |
| height        | Dp                      | Height of the progress bar                     | ProgressIndicatorDefaults.DefaultLinearProgressIndicatorHeight | No       |

### CircularProgressIndicator Properties

| Property Name | Type                    | Description                                    | Default Value                                                         | Required |
| ------------- | ----------------------- | ---------------------------------------------- | --------------------------------------------------------------------- | -------- |
| modifier      | Modifier                | Modifier applied to the progress indicator     | Modifier                                                              | No       |
| progress      | Float?                  | Current progress value, null for indeterminate | null                                                                  | No       |
| colors        | ProgressIndicatorColors | Color configuration for the progress indicator | ProgressIndicatorDefaults.circularProgressIndicatorColors()           | No       |
| strokeWidth   | Dp                      | Stroke width of the circular track             | ProgressIndicatorDefaults.DefaultCircularProgressIndicatorStrokeWidth | No       |
| size          | Dp                      | Size of the circular indicator                 | ProgressIndicatorDefaults.DefaultCircularProgressIndicatorSize        | No       |

### InfiniteProgressIndicator Properties

| Property Name   | Type     | Description                        | Default Value                                                             | Required |
| --------------- | -------- | ---------------------------------- | ------------------------------------------------------------------------- | -------- |
| modifier        | Modifier | Modifier applied to the indicator  | Modifier                                                                  | No       |
| color           | Color    | Color of the progress indicator    | Color.Gray                                                                | No       |
| size            | Dp       | Size of the indicator              | ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorSize            | No       |
| strokeWidth     | Dp       | Stroke width of the circular track | ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorStrokeWidth     | No       |
| orbitingDotSize | Dp       | Size of the orbiting dot           | ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorOrbitingDotSize | No       |

### ProgressIndicatorDefaults Object

The ProgressIndicatorDefaults object provides default values and color configurations for progress indicator components.

#### Constants

| Constant Name                                   | Type | Default Value | Description                                     |
| ----------------------------------------------- | ---- | ------------- | ----------------------------------------------- |
| DefaultLinearProgressIndicatorHeight            | Dp   | 6.dp          | Default height of linear progress bar           |
| DefaultCircularProgressIndicatorStrokeWidth     | Dp   | 5.dp          | Default stroke width of circular indicator (large preset) |
| DefaultCircularProgressIndicatorSize            | Dp   | 40.dp         | Default size of circular indicator (large preset) |
| LargeCircularSize                               | Dp   | 40.dp         | Large circular preset (`coui_circular_progress_large_length`) |
| LargeCircularStrokeWidth                        | Dp   | 5.dp          | Large preset ring (`coui_circular_progress_large_stroke_width`) |
| DefaultInfiniteProgressIndicatorStrokeWidth     | Dp   | 2.dp          | Default stroke width of infinite indicator      |
| DefaultInfiniteProgressIndicatorOrbitingDotSize | Dp   | 2.dp          | Default orbiting dot size of infinite indicator |
| DefaultInfiniteProgressIndicatorSize            | Dp   | 20.dp         | Default size of infinite indicator              |

#### Methods

| Method Name               | Type                    | Description                                        |
| ------------------------- | ----------------------- | -------------------------------------------------- |
| progressIndicatorColors() | ProgressIndicatorColors | Creates the linear colors (accent fill over the divider track) |
| circularProgressIndicatorColors() | ProgressIndicatorColors | Creates the circular colors (neutral hint ring over the divider track) |

### ProgressIndicatorColors Class

| Property Name           | Type  | Description                                 |
| ----------------------- | ----- | ------------------------------------------- |
| foregroundColor         | Color | Foreground color of the progress indicator  |
| disabledForegroundColor | Color | Foreground color when indicator is disabled |
| backgroundColor         | Color | Background color of the progress indicator  |

## Advanced Usage

### Custom Colored Linear Progress Bar

```kotlin
var progress by remember { mutableStateOf(0.4f) }

LinearProgressIndicator(
    progress = progress,
    colors = ProgressIndicatorDefaults.progressIndicatorColors(
        foregroundColor = Color.Red,
        backgroundColor = Color.LightGray
    )
)
```

### Resized Circular Progress Indicator

```kotlin
var progress by remember { mutableStateOf(0.75f) }

CircularProgressIndicator(
    progress = progress,
    size = 50.dp,
    strokeWidth = 6.dp
)
```

### Loading State with Button

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
            modifier = Modifier
                .padding(end = 8.dp),
            size = 20.dp,
            strokeWidth = 4.dp
        )
    }
    Text("Submit")
}
```

### Custom Infinite Progress Indicator

```kotlin
InfiniteProgressIndicator(
    color = RmuixTheme.colorScheme.primary,
    size = 40.dp,
    strokeWidth = 3.dp,
    orbitingDotSize = 4.dp
)
```

### Loading State with Card

```kotlin
var isLoading by remember { mutableStateOf(true) }

Card(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(16.dp)
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading...")
            }
        } else {
            Text("Content Loaded")
        }
    }
}
// Control loading state
LaunchedEffect(Unit) {
    delay(3000)
    isLoading = false
}
```
