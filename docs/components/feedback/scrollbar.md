# ScrollBar

`ScrollBar` is a draggable scroll indicator in Rmuix. It renders a thin thumb that fades in while the content is scrolling, widens while hovered or dragged, and fades out after a short delay. `VerticalScrollBar` and `HorizontalScrollBar` share the same implementation and are driven by a `ScrollBarAdapter`.

## Stock ColorOS static review (2026-11-12)

This pass reads `com.coui.appcompat.scrollbar.COUIScrollBar` and the `coui_scrollbar_*` resources of the Settings APK.

- Thumb width is arithmetic, not a single token: `coui_scrollbar_wight` (14dp) minus the two `coui_scrollbar_drawable_default_inset` (5.5dp) sides, so the resting thumb is **3dp**. While pressed the inset shrinks to `coui_scrollbar_drawable_pressed_inset` (4dp) and the thumb becomes **6dp**.
- The shortest thumb rect is `coui_scrollbar_min_height` = **48dp**, wrapped in an `InsetDrawable` with
  `coui_scrollbar_margin_top` 8dp and `_margin_bottom` 16dp; the shortest **visible** thumb is therefore
  **24dp**. The component applies those insets as the default `VerticalTrackPadding`, so
  `ScrollBarDefaults.ThumbMinLength` is the visible 24dp.
- `coui_scrollbar_color` is `#29000000` (light) / `#40FFFFFF` (dark). The normal and pressed `StateListDrawable` entries read the same resource, so pressing only widens the thumb and never changes its alpha; the library previously modulated `onSurface` between 0.1 and 0.3.
- The thumb now resolves `41/255` in light and `64/255` in dark through `scrollBarThumbAlpha(dark)`, and the fade still multiplies the animated `opacity`.
- The stock bar wraps its drawable in an `InsetDrawable` with `coui_scrollbar_margin_top` = **8dp** and `coui_scrollbar_margin_bottom` = **16dp**, so the track never touches either edge. `ScrollBarDefaults.VerticalTrackPadding` now carries that asymmetric pair and is the default `trackPadding` of `VerticalScrollBar`; pass `PaddingValues(0.dp)` to restore the old edge-to-edge track. `HorizontalScrollBar` keeps `PaddingValues(0.dp)` because the horizontal stock bar has no such margin.

**BREAKING:** the default thumb is 3dp wide (was 3.64dp), its minimum visible length is 24dp (the 48dp stock rect minus the 8/16dp vertical insets; was 36dp), its alpha no longer changes between resting and pressed, `VerticalScrollBar` insets its track by 8dp/16dp by default,
and the end inset becomes 5.5dp (was 3.46dp, which had no token behind it) animating to 4dp while highlighted - so the
strip measures the stock 14dp instead of ~9.9dp.

**End padding (2026-11-14):** `EndPadding` is now `coui_scrollbar_drawable_default_inset` = **5.5dp**, and the
highlighted/dragging state animates it to `coui_scrollbar_drawable_pressed_inset` = **4dp** (`PressedEndPadding`).
Together with the 3dp thumb the bar measures **14dp**, which is exactly `coui_scrollbar_wight`. The old 3.46dp
value came from a nine-patch measurement and had no token behind it.

> **Sizing:** the bar only reserves `thumbWidth + 2 * endPadding` (~10dp) and draws inside that strip - it does not fill
> the space you hand it. Overlay it yourself, e.g. `Box { content; VerticalScrollBar(adapter, Modifier.align(Alignment.CenterEnd).fillMaxHeight()) }`.
> `ScrollBarRenderingTest` pins the hovered 6dp thumb, the 4dp pressed inset and the stock 14dp bar width.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.HorizontalScrollBar
import io.github.ra1nify.rmuix.kmp.basic.ScrollBarAdapter
import io.github.ra1nify.rmuix.kmp.basic.ScrollBarDefaults
import io.github.ra1nify.rmuix.kmp.basic.VerticalScrollBar
import io.github.ra1nify.rmuix.kmp.basic.rememberScrollBarAdapter
```

`ScrollBarAdapter`, `ScrollBarColors`, `ScrollBarDefaults`, `VerticalScrollBar` and `HorizontalScrollBar` are annotated with `@ExperimentalScrollBarApi`.

## Basic Usage

```kotlin
val listState = rememberLazyListState()
val adapter = rememberScrollBarAdapter(listState)

Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().padding(end = 12.dp),
    ) {
        items(100) { index -> Text("Item $index") }
    }
    VerticalScrollBar(
        adapter = adapter,
        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
    )
}
```

`rememberScrollBarAdapter` accepts a `ScrollState`, a `LazyListState` or a `LazyGridState`.

### Horizontal ScrollBar

```kotlin
val rowState = rememberScrollState()
val adapter = rememberScrollBarAdapter(rowState)

Box(modifier = Modifier.fillMaxWidth()) {
    Row(modifier = Modifier.horizontalScroll(rowState)) {
        repeat(20) { Text("Cell $it") }
    }
    HorizontalScrollBar(
        adapter = adapter,
        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
    )
}
```

## Properties

### VerticalScrollBar / HorizontalScrollBar

| Property Name | Type              | Description                                              | Default Value                       | Required |
| ------------- | ----------------- | -------------------------------------------------------- | ----------------------------------- | -------- |
| adapter       | ScrollBarAdapter  | Communicates with the scrollable component               | -                                   | Yes      |
| modifier      | Modifier          | Modifier applied to the scroll bar                       | Modifier                            | No       |
| reverseLayout | Boolean           | Reverse the direction of scrolling and layout            | false                               | No       |
| trackPadding  | PaddingValues     | Padding applied to the track to skip content padding     | `VerticalScrollBar`: `ScrollBarDefaults.VerticalTrackPadding` (8dp top / 16dp bottom); `HorizontalScrollBar`: `PaddingValues(0.dp)` | No |
| colors        | ScrollBarColors   | Colors of the scroll bar                                 | ScrollBarDefaults.scrollBarColors() | No       |
| thumbWidth    | Dp                | Width of the thumb                                       | ScrollBarDefaults.ThumbWidth        | No       |
| cornerRadius  | Dp                | Corner radius, `Dp.Unspecified` uses half of `thumbWidth` | ScrollBarDefaults.CornerRadius      | No       |
| thumbMinLength| Dp                | Minimum length of the thumb                              | ScrollBarDefaults.ThumbMinLength    | No       |
| endPadding    | Dp                | Padding from the end edge                                | ScrollBarDefaults.EndPadding        | No       |

### ScrollBarDefaults Object

| Constant Name              | Type | Value  | Description |
| -------------------------- | ---- | ------ | ----------- |
| ThumbWidth                 | Dp   | 3.dp   | Resting thumb width (`coui_scrollbar_wight` 14dp minus two 5.5dp insets) |
| DragThumbWidth             | Dp   | 6.dp   | Thumb width while hovered or dragged (two 4dp insets) |
| ThumbMinLength             | Dp   | 24.dp  | Minimum visible thumb length (`coui_scrollbar_min_height` 48dp minus the 8/16dp insets) |
| EndPadding                 | Dp   | 5.5.dp | Padding from the end edge (`coui_scrollbar_drawable_default_inset`) |
| PressedEndPadding          | Dp   | 4.dp   | Same, while highlighted or dragging (`coui_scrollbar_drawable_pressed_inset`) |
| CornerRadius               | Dp   | Dp.Unspecified | Corner radius, half of the thumb width when unspecified |
| TouchTargetWidth           | Dp   | 48.dp  | Width of the touch target |
| ThumbAlpha                 | Float| 41f / 255f | Light-mode thumb alpha (`coui_scrollbar_color` `#29000000`) |
| DragThumbAlpha             | Float| 41f / 255f | Pressed thumb alpha, identical to `ThumbAlpha` |
| FadeDelayMillis            | Int  | 1000   | Delay before the thumb starts fading out |
| FadeDurationMillis         | Int  | 500    | Fade-out duration |
| DragAnimationDurationMillis| Int  | 150    | Thumb width animation duration |

### ScrollBarColors Class

| Property Name | Type  | Description                                                       |
| ------------- | ----- | ----------------------------------------------------------------- |
| thumbColor    | Color | Thumb color, `Color.Unspecified` uses the theme's `onSurface` with the stock alpha |
| trackColor    | Color | Track color, `Color.Unspecified` hides the track                  |

## Advanced Usage

### Custom Colors

```kotlin
VerticalScrollBar(
    adapter = adapter,
    colors = ScrollBarDefaults.scrollBarColors(
        thumbColor = RmuixTheme.colorScheme.primary,
        trackColor = RmuixTheme.colorScheme.surfaceContainer,
    ),
)
```

Passing an explicit `thumbColor` uses its own alpha and disables the stock per-theme modulation.

### Skipping Content Padding

```kotlin
VerticalScrollBar(
    adapter = adapter,
    trackPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
)
```
