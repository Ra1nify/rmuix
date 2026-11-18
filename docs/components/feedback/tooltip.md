#! /usr/bin/env markdown

# Tooltip

`Tooltip` is a component in Rmuix that briefly describes an anchor element. Following Material 3, a single `TooltipBox` anchors a tooltip to its content; you fill its `tooltip` slot with a `PlainTooltip` (a short inverse-surface label) or a `RichTooltip` (a surface-container card with an optional title and action). Tooltips are shown on hover (mouse) or long press (touch), or programmatically via `TooltipState`.

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=tooltip" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass reads `com.coui.appcompat.tooltip.COUIToolTips`, the `COUIToolTips` style and the `coui_tooltips_*` / `tool_tips_*` resources of the Settings APK.

- The stock tip bubble is filled with `couiToolTipsBackgroundColor` → `?attr/couiColorTips` (`#0080FF` / `#1A8CFF`) and writes `couiToolTipsContentTextColor` = `coui_color_white`, i.e. white text on the accent, not the library's inverse-surface pair (`onSecondaryVariant` / `secondaryVariant`).
- The bubble pads 13dp on every side (`coui_tooltips_default_background_padding_left/top/right/bottom`); the library used 12dp × 8dp.
- The corner radius follows `coui_tooltips_os_16_1_radius_17_dp` = **17dp** on the smooth-corner branch (the plain `couiRoundCornerM` is used on the old branch); the library used 12dp.
- The bubble never grows past `tool_tips_max_width` = **328dp** (the library used 200dp) and writes its text at `tool_tips_content_text_size` = **13sp**, so the `TooltipBox(text = …)` convenience now sets that size explicitly.
- Rich tooltip keeps its own `richTooltipColors()` roles; the stock tip widget has no title/action variant, so nothing is ported there.

**BREAKING:** the plain tooltip becomes an accent-filled bubble with white 13sp text, 13dp padding, a 17dp radius and a 328dp maximum width.

`PlainTooltip`'s `caretShape` now **defaults to `TooltipDefaults.caretShape()`**, because the stock plain tip always draws the `coui_white_sharp_middle_arrow_up.xml` arrow (20dp x 10.1dp, `caretSize`); pass `caretShape = null` for a caret-less bubble.

**BREAKING:** the plain tooltip draws its caret by default.

The image variant (`ImageTooltip`, `tool_tips_image_style_max_width` 260dp + `coui_tooltips_imageview_radius` 8dp) and the
delete button (`TooltipDismissButton`, `coui_tool_tips_delete_*`) **are** implemented - see the variant sections below.

Still unmodelled: `couiToolTipsArrowOverflowOffset` (5.7dp; the popup is clamped to the window margins instead, which is what
the stock uses the offset for).

**`COUIToolTips.DetailFloating`** is now exposed as a preset pair instead of being modelled by a separate component:
`TooltipDefaults.detailFloatingContainerColor()` = `couiColorBackgroundTop` **#FFFFFF** / **#333333**,
`detailFloatingContentColor()` = `couiColorSecondNeutral` **#8A000000** / **#8AFFFFFF**,
`DetailFloatingInsideMargin` = 16dp horizontal with the 13dp default vertical,
`DetailFloatingContainerMargins` = `detail_floating_text_margin` **12dp on all four sides** (pass it to
`rememberTooltipPositionProvider(containerMargins = …)`), and `DetailFloatingMinWidth` = **80dp** (recorded; `PlainTooltip`
has no min-width slot). `TooltipDetailFloatingTest` pins all of it.
## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TooltipBox
import io.github.ra1nify.rmuix.kmp.basic.RichTooltipBox
import io.github.ra1nify.rmuix.kmp.basic.PlainTooltip
import io.github.ra1nify.rmuix.kmp.basic.RichTooltip
import io.github.ra1nify.rmuix.kmp.basic.TooltipState
import io.github.ra1nify.rmuix.kmp.basic.rememberTooltipState
import io.github.ra1nify.rmuix.kmp.basic.TooltipDefaults
import io.github.ra1nify.rmuix.kmp.basic.TooltipAnchorPosition
```

## Basic Usage

The quickest way to label an icon-only control is the `text` convenience overload of `TooltipBox`:

```kotlin
TooltipBox(text = "Search") {
    IconButton(onClick = { /* ... */ }) {
        Icon(
            imageVector = RmuixIcons.Basic.Search,
            contentDescription = "Search",
        )
    }
}
```

## TooltipBox

`TooltipBox` anchors a tooltip to its `content`. Its `tooltip` slot is a `TooltipScope` lambda, filled with `PlainTooltip` or `RichTooltip`.

```kotlin
@Composable
fun TooltipBox(
    positionProvider: PopupPositionProvider,
    tooltip: @Composable TooltipScope.() -> Unit,
    state: TooltipState,
    modifier: Modifier = Modifier,
    focusable: Boolean = false,
    enableUserInput: Boolean = true,
    content: @Composable () -> Unit,
)
```

| Parameter Name  | Type                                | Description                                                        | Default Value | Required |
| --------------- | ----------------------------------- | ----------------------------------------------------------------- | ------------- | -------- |
| positionProvider | PopupPositionProvider               | Positions the tooltip; see `TooltipDefaults.rememberTooltipPositionProvider` | -    | Yes      |
| tooltip         | @Composable TooltipScope.() -> Unit | The tooltip content (`PlainTooltip` / `RichTooltip`)              | -             | Yes      |
| state           | TooltipState                        | State controlling visibility                                      | -             | Yes      |
| modifier        | Modifier                            | Modifier applied to the anchor wrapper                            | Modifier      | No       |
| focusable       | Boolean                             | Whether the tooltip is focusable (true for interactive rich tooltips) | false    | No       |
| enableUserInput | Boolean                             | Whether hover / long press on the anchor shows the tooltip        | true          | No       |
| content         | @Composable () -> Unit              | The anchor content                                                | -             | Yes      |

```kotlin
TooltipBox(
    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
    tooltip = { PlainTooltip { Text("Add to favorites") } },
    state = rememberTooltipState(),
) {
    IconButton(onClick = { /* ... */ }) {
        Icon(imageVector = RmuixIcons.Basic.Check, contentDescription = "Add to favorites")
    }
}
```

## PlainTooltip

`PlainTooltip` is a short, non-interactive label rendered on the Rmuix inverse surface. It is a `TooltipScope` extension, so it is used inside the `TooltipBox` `tooltip` slot.

```kotlin
@Composable
fun TooltipScope.PlainTooltip(
    modifier: Modifier = Modifier,
    caretShape: Shape? = TooltipDefaults.caretShape(),
    maxWidth: Dp = TooltipDefaults.PlainTooltipMaxWidth,
    cornerRadius: Dp = TooltipDefaults.PlainTooltipCornerRadius,
    containerColor: Color = TooltipDefaults.plainTooltipContainerColor,
    contentColor: Color = TooltipDefaults.plainTooltipContentColor,
    insideMargin: PaddingValues = TooltipDefaults.PlainTooltipInsideMargin,
    content: @Composable () -> Unit,
)
```

By default the plain tooltip uses an inverse surface tone (a dark bubble in light theme, a light bubble in dark theme). By default `caretShape` is `TooltipDefaults.caretShape()`, so the bubble draws the stock caret pointing at the anchor; pass `caretShape = null` for a caret-less bubble.

## RichTooltip

`RichTooltip` is a persistent, interactive card with an optional title and action, rendered on a normal `surfaceContainer`. It is a `TooltipScope` extension.

```kotlin
@Composable
fun TooltipScope.RichTooltip(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    caretShape: Shape? = null,
    maxWidth: Dp = TooltipDefaults.RichTooltipMaxWidth,
    cornerRadius: Dp = TooltipDefaults.RichTooltipCornerRadius,
    colors: RichTooltipColors = TooltipDefaults.richTooltipColors(),
    insideMargin: PaddingValues = TooltipDefaults.RichTooltipInsideMargin,
    text: @Composable () -> Unit,
)
```

A rich tooltip is interactive, so set `focusable = true` on the `TooltipBox` (the convenience `RichTooltipBox` does this for you) so an outside tap dismisses it and its action is reachable.

```kotlin
val tooltipState = rememberTooltipState(isPersistent = true)
val scope = rememberCoroutineScope()

TooltipBox(
    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
    tooltip = {
        RichTooltip(
            title = { Text("What's new") },
            action = {
                TextButton(text = "Got it", onClick = { tooltipState.dismiss() })
            },
        ) {
            Text("Rich tooltips support a title, supporting text, and an action.")
        }
    },
    state = tooltipState,
    focusable = true,
) {
    IconButton(onClick = { scope.launch { tooltipState.show() } }) {
        Icon(imageVector = RmuixIcons.Basic.Check, contentDescription = "What's new")
    }
}
```

## TooltipState

`TooltipState` controls the tooltip's visibility. A global `MutatorMutex` keeps only one tooltip visible at a time.

```kotlin
@Stable
interface TooltipState {
    val transition: MutableTransitionState<Boolean>
    val isVisible: Boolean
    val isPersistent: Boolean
    suspend fun show(mutatePriority: MutatePriority = MutatePriority.Default)
    fun dismiss()
    fun onDispose()
}

@Composable
fun rememberTooltipState(
    initialIsVisible: Boolean = false,
    isPersistent: Boolean = false,
    mutatorMutex: MutatorMutex = BasicTooltipDefaults.GlobalMutatorMutex,
): TooltipState
```

| Parameter Name  | Type         | Description                                              | Default Value                      | Required |
| --------------- | ------------ | ------------------------------------------------------- | ---------------------------------- | -------- |
| initialIsVisible | Boolean     | Whether the tooltip is initially visible                | false                              | No       |
| isPersistent    | Boolean      | Whether the tooltip stays visible until dismissed       | false                              | No       |
| mutatorMutex    | MutatorMutex | Mutex ensuring only one tooltip shows at a time         | BasicTooltipDefaults.GlobalMutatorMutex | No  |

When `isPersistent` is false (plain tooltips), the tooltip is dismissed automatically after a short timeout. When true (rich tooltips), it stays until `dismiss()`, an outside tap, or a back press. `show()` is a suspend function, so call it from a coroutine (e.g. `scope.launch { state.show() }`).

## TooltipDefaults

`TooltipDefaults` provides position providers, colors, the caret, and dimension constants.

### Position provider

```kotlin
@Composable
fun TooltipDefaults.rememberTooltipPositionProvider(
    positioning: TooltipAnchorPosition = TooltipAnchorPosition.Below,
    spacingBetweenTooltipAndAnchor: Dp = TooltipDefaults.SpacingBetweenTooltipAndAnchor,
): PopupPositionProvider
```

`TooltipAnchorPosition` selects the preferred side: `Above`, `Below`, `Left`, `Right`, `Start`, `End`.
The popup is clamped on both axes with the stock container margins - `couiToolTipsContainerLayoutMarginStart` **12dp**,
`_MarginEnd` **42dp**, `_MarginTop` **12dp**, `_MarginBottom` **12dp** (`COUIToolTips` style), exposed as
`TooltipDefaults.ContainerMarginStart` / `ContainerMarginEnd` / `ContainerMarginTop` / `ContainerMarginBottom`. Start/end are inline edges, so they swap in RTL. A tooltip near a screen edge
therefore slides inward and keeps the stock breathing room instead of touching the edge.

`Start` / `End` mean the **inline** start / end of the anchor, so they resolve to `Left` in LTR and `Right` in RTL
(pinned by `TooltipPositionProviderTest`). The tooltip is centered on the cross axis and flips to the opposite side when there is not enough room.

### Colors

```kotlin
// Plain tooltip (inverse surface)
val plainContainer = TooltipDefaults.plainTooltipContainerColor   // couiColorTips: #0080FF / #1A8CFF
val plainContent = TooltipDefaults.plainTooltipContentColor       // white

// Rich tooltip (surface container)
val richColors = TooltipDefaults.richTooltipColors(
    containerColor = RmuixTheme.colorScheme.surfaceContainer,
    contentColor = RmuixTheme.colorScheme.onSurfaceContainerVariant,
    titleContentColor = RmuixTheme.colorScheme.onSurfaceContainer,
    actionContentColor = RmuixTheme.colorScheme.primary,
)
```

`RichTooltipColors` is a data class of `containerColor`, `contentColor`, `titleContentColor`, and `actionContentColor`.

### Caret

```kotlin
fun TooltipDefaults.caretShape(): Shape
val TooltipDefaults.caretSize: DpSize // 20 x 10.1 dp (`coui_white_sharp_middle_arrow_up.xml`)
```

`PlainTooltip` defaults to `TooltipDefaults.caretShape()` (the stock caret), while `RichTooltip` stays caret-less unless you pass one.

### Constants

| Constant Name              | Type          | Description                              | Default Value                                      |
| -------------------------- | ------------- | ---------------------------------------- | -------------------------------------------------- |
| SpacingBetweenTooltipAndAnchor | Dp        | Spacing between a tooltip and its anchor | 8.dp                                               |
| PlainTooltipMaxWidth       | Dp            | Maximum width of a plain tooltip (`tool_tips_max_width`) | 328.dp |
| PlainTooltipCornerRadius   | Dp            | Corner radius of a plain tooltip (`coui_tooltips_os_16_1_radius_17_dp`) | 17.dp |
| PlainTooltipInsideMargin   | PaddingValues | Inner padding of a plain tooltip (`coui_tooltips_default_background_padding_*`) | PaddingValues(all = 13.dp) |
| PlainTooltipTextSize       | TextUnit      | Text size of a plain tooltip (`tool_tips_content_text_size`) | 13.sp |
| RichTooltipMaxWidth        | Dp            | Maximum width of a rich tooltip          | 320.dp                                             |
| RichTooltipCornerRadius    | Dp            | Corner radius of a rich tooltip          | 16.dp                                              |
| RichTooltipInsideMargin    | PaddingValues | Inner padding of a rich tooltip          | PaddingValues(all = 16.dp)                         |
| RichTooltipActionCornerRadius | Dp         | Corner radius of a rich tooltip action button | 8.dp                                          |
| RichTooltipActionInsideMargin | PaddingValues | Inner padding of a rich tooltip action button | PaddingValues(horizontal = 12.dp, vertical = 6.dp) |
| caretSize                  | DpSize        | Size of the caret | DpSize(20.dp, 10.1.dp) |

## Convenience overloads

For the common cases you can skip the position provider and content composables.

### Plain tooltip

```kotlin
@Composable
fun TooltipBox(
    text: String,
    modifier: Modifier = Modifier,
    state: TooltipState = rememberTooltipState(isPersistent = false),
    enabled: Boolean = true,
    positioning: TooltipAnchorPosition = TooltipAnchorPosition.Below,
    containerColor: Color = TooltipDefaults.plainTooltipContainerColor,
    contentColor: Color = TooltipDefaults.plainTooltipContentColor,
    containerMargins: PaddingValues = TooltipDefaults.ContainerMargins,
    content: @Composable () -> Unit,
)
```

`containerMargins` defaults to `TooltipDefaults.ContainerMargins` (12 / 42 / 12 / 12dp); pass
`TooltipDefaults.DetailFloatingContainerMargins` together with `detailFloatingContainerColor()` /
`detailFloatingContentColor()` to get the `COUIToolTips.DetailFloating` look.

### Rich tooltip

```kotlin
@Composable
fun RichTooltipBox(
    text: String,
    modifier: Modifier = Modifier,
    state: TooltipState = rememberTooltipState(isPersistent = true),
    title: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    positioning: TooltipAnchorPosition = TooltipAnchorPosition.Below,
    colors: RichTooltipColors = TooltipDefaults.richTooltipColors(),
    containerMargins: PaddingValues = TooltipDefaults.ContainerMargins,
    content: @Composable () -> Unit,
)
```

`RichTooltipBox` is persistent and focusable. Trigger it via long press / hover, or hoist `state` and call `state.show()` from the anchor's own click. The action invokes `onActionClick` then dismisses the tooltip.

```kotlin
val richState = rememberTooltipState(isPersistent = true)
val scope = rememberCoroutineScope()

RichTooltipBox(
    title = "Rich tooltip",
    text = "Rich tooltips support a title, supporting text, and an action.",
    actionText = "Got it",
    onActionClick = { /* ... */ },
    state = richState,
) {
    TextButton(
        text = "Show rich tooltip",
        onClick = { scope.launch { richState.show() } },
    )
}
```

> The **icon variant** (`COUIToolTips.Icon`) is recorded as `TooltipDefaults.IconTooltipIconSize` (40dp), `IconTooltipIconMarginTop` (2dp), `IconTooltipIconMarginStart` (12dp), `IconTooltipScrollMarginTop` (12dp), `iconTooltipContainerColor` (`?attr/couiColorBackgroundTop` = `#FFFFFF` / `#333333`) and `iconTooltipContentColor` (`?attr/couiColorLabelSecondary` = `#8A000000` / `#8AFFFFFF`). The variant layout itself is not composed yet.

## Image variant

`COUIToolTips.Image` has an empty style that parents `COUIToolTips.Icon`; the difference is the layout and the width cap. `ImageTooltip(image = { ... }) { Text(...) }` implements it:

| token | value | notes |
| :--- | ---: | :--- |
| `tool_tips_image_style_max_width` | 260dp | the default bubble caps at 328dp |
| `coui_tool_tips_image_no_edges_style_layout.xml` | 132dp | image band height |
| `android:scaleType` | `centerCrop` | the image is **cropped** to fill, not letterboxed |
| `coui_tooltips_imageview_radius` | 8dp | corner radius on the image itself |

> The research note for this variant said the layout "does not set `scaleType`" - it does, and the value is `centerCrop`. Recorded as a correction.

## Dismiss control

`coui_tool_tips_layout.xml` places an 18dp `ImageView` (`coui_tool_tips_delete_icon`) at `end|center_vertical` with a 12dp end margin. `TooltipDismissButton(onDismiss = { ... })` reproduces it, drawing the stock vector from its path data (a 30% white disc plus a solid white cross).

| token | value | notes |
| :--- | ---: | :--- |
| `coui_tool_tips_delete_background_size` | 18dp | the glyph, and therefore the hit area |
| `coui_tool_tips_delete_icon_size` | 44dp | **declared but never referenced** |
| `coui_tool_tips_delete_icon_padding` | 12dp | **declared but never referenced** |
| `layout_marginEnd` | 12dp | |

> The 44dp "touch target" in the earlier research note does not exist in the stock layout - the 18dp glyph is the whole hit area. Recorded as a correction.

## `caretShape` is a flag, not a shape

`PlainTooltip` / `RichTooltip` / `ImageTooltip` all take `caretShape: Shape?`. **Only null-ness is read.**
`TooltipSurface` checks `caretShape != null` and then draws the stock
`coui_white_sharp_middle_arrow_up.xml` vector itself (20 x 10.1dp, arc-rounded shoulders, 2dp rounded tip) -
a bespoke shape passed in is accepted and silently ignored.

This is a known API wart, recorded rather than papered over: the parameter should eventually become
`showCaret: Boolean`, but changing it now would break every existing call site.