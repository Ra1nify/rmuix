---
title: WindowDialog
requiresScaffoldHost: false
prerequisites:
  - Can be used anywhere, does not require `Scaffold` or `RmuixPopupHost`
  - Renders at window level
hostComponent: None
popupHost: None
---

# WindowDialog

`WindowDialog` is a window-level dialog component. It renders using platform `Dialog` and does not require `Scaffold` or `RmuixPopupHost`. It supports large-screen optimized animations, system back gesture dismissal, and a composition local to request dismiss from inside content.

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=windowDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: tip
This component is independent of `Scaffold` and can be used in any composable scope.
:::

## Stock ColorOS static review (2026-11-12)

This pass reads the Settings APK `coui_center_alert_dialog_layout.xml` family, the `COUIDialogTextAppearance.*` styles and the corner-radius attributes consumed by `COUIAlertDialogBuilder`. Only the centred alert dialog and the bottom-sheet radius are covered; the material layer is still a deviation.

- Maximum width `coui_dialog_max_width` = **392dp** (was 328dp); outside margin `coui_dialog_layout_margin_horizontal` / `_vertical` = **16dp / 32dp** (was 16dp / 24dp).
- Corner radius comes from `?attr/couiRoundCornerXXXXLRadius`, i.e. `coui_round_corner_xxxxl_radius` = **25dp** with G2 squircle weight 1.1, not the un-suffixed `coui_round_corner_xxxxl` (32dp) used previously.
- Title follows `COUIAlertDialogTitleStyle` -> `couiColorLabelPrimary` (`#E6000000` / `#E6FFFFFF`); the summary follows `COUIAlertDialogMessageStyle` -> `couiColorLabelSecondary` (`#8A000000` / `#8AFFFFFF`). Sizes were already right (title 18sp medium, message 13sp).
- The bottom sheet is a different component (`COUIBottomSheetDialog`) and reads the plain `coui_round_corner_xxxxl` = **32dp**; the two are no longer described as sharing one token.

**BREAKING:** `DialogDefaults.MaxWidth` 328 -> 392dp, `CornerRadius` 32 -> 25dp, `outsideMargin` 16/24 -> 16/32, and the default title/summary colours change; `BottomSheetDefaults.cornerRadius` 22 -> 32dp.

**Scroll caps:** a title taller than `DialogDefaults.TitleScrollMaxHeight` (80dp,
`coui_alert_dialog_builder_title_scroll_max_height`) or a message taller than
`DialogDefaults.MessageScrollMaxHeight` (80dp, `coui_alert_dialog_builder_content_max_height`) scrolls
inside its slot, matching the stock `COUIMaxHeightScrollView` / `COUIMaxHeightNestedScrollView` wrappers.
The stock fading edge (`requiresFadingEdge`) is not drawn yet.

**Center-dialog layout:** the window dialog shares `DialogContentLayout`, so the stock full-bleed text
button bar (`confirmButtonText` / `dismissButtonText` / `confirmButtonDestructive`) and the
`?attr/couiColorSurface` background apply here too (`coui_center_alert_dialog_layout.xml`).
**Still under review:** the stock dialog background is a blurred, tinted backdrop (`COUIBackgroundBlurBuilder`, 200px blur, `coui_dialog_list_mix_blur_light` `#DC909090` / dark `#8C777777`, blend `#509F9F9F` / `#B2292929`); the library still paints a solid theme surface. The OS-version branch that switches the radius to 45dp (`coui_round_corner_xxxxl_radius_large_16_1`) is not selected at runtime.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.window.WindowDialog
import io.github.ra1nify.rmuix.kmp.theme.LocalDismissState
```

## Basic Usage

```kotlin
var showDialog by remember { mutableStateOf(false) }

TextButton(
    text = "Open",
    onClick = { showDialog = true }
)

WindowDialog(
    title = "WindowDialog",
    summary = "A basic window-level dialog",
    show = showDialog,
    onDismissRequest = { showDialog = false }
) {
    val dismiss = LocalDismissState.current
    TextButton(
        text = "Confirm",
        onClick = { dismiss?.invoke() },
        modifier = Modifier.fillMaxWidth()
    )
}
```

## Properties

### WindowDialog Properties

| Property Name              | Type                   | Description                                                   | Default Value                          | Required |
| -------------------------- | ---------------------- | ------------------------------------------------------------- | -------------------------------------- | -------- |
| show                       | Boolean                | Whether to show the dialog                                    | -                                      | Yes      |
| modifier                   | Modifier               | Root content modifier                                         | Modifier                               | No       |
| title                      | String?                | Dialog title                                                  | null                                   | No       |
| titleColor                 | Color                  | Title color                                                   | DialogDefaults.titleColor()      | No       |
| summary                    | String?                | Dialog summary                                                | null                                   | No       |
| summaryColor               | Color                  | Summary color                                                 | DialogDefaults.summaryColor()    | No       |
| backgroundColor            | Color                  | Dialog background color                                       | DialogDefaults.backgroundColor() | No       |
| enableWindowDim            | Boolean                | Whether to enable dimming layer                               | true                                   | No       |
| onDismissRequest           | (() -> Unit)?          | Called when the user requests dismissal (outside tap or back) | null                                   | No       |
| onDismissFinished          | (() -> Unit)?          | Invoked after the hide animation completes; not invoked if the hide is cancelled mid-flight (e.g., `show` toggled back to true) | null              | No       |
| outsideMargin              | DpSize                 | Outer margin (window edges)                                   | DialogDefaults.outsideMargin     | No       |
| insideMargin               | DpSize                 | Inner padding for dialog content                              | DialogDefaults.insideMargin      | No       |
| defaultWindowInsetsPadding | Boolean                | Apply default insets padding (IME, nav, caption)              | true                                   | No       |
| maxWidth                   | Dp                     | Maximum width of the dialog                                   | DialogDefaults.MaxWidth          | No       |
| largeScreen                | Boolean?               | Overrides the large-screen presentation (centered scale/fade instead of bottom slide-in). When null, detected from the window size | null | No |
| cornerRadius               | Dp?                    | Overrides the dialog corner radius. When null, DialogDefaults.CornerRadius for the centered presentation, or DialogDefaults.CornerRadius for the bottom presentation as well | null | No |
| dimColor                   | Color                  | Colour of the dimming layer behind the dialog                 | RmuixTheme.colorScheme.windowDimming   | No       |
| icon | (@Composable () -> Unit)? | Top-aligned slot before the title, mirroring the stock `android:id/icon` | null | No |
| surface                    | `(@Composable DialogSurfaceScope.() -> Modifier)?` | Custom panel surface (e.g. a glass material). `null` keeps the stock squircle background | null | No |
| confirmButtonText          | String?                | Text of the confirm button; non-null draws a `COUIAlertDialog`-style right-aligned footer with the two text buttons | null | No |
| onConfirm                  | (() -> Unit)?          | Invoked by the confirm button; falls back to `onDismissRequest` | null | No |
| confirmButtonDestructive | Boolean | Renders the confirm label in the error colour (`COUIAlertDialogBottomButtonSecondary.Last`) | false | No |
| dismissButtonText          | String?                | Text of the dismiss button | null | No |
| onDismiss                  | (() -> Unit)?          | Invoked by the dismiss button; falls back to `onDismissRequest` | null | No |
| content                    | @Composable () -> Unit | Dialog content                                                | -                                      | Yes      |

### DialogDefaults

#### Properties

| Name          | Type   | Description                      | Value |
| ------------- | ------ | -------------------------------- | ----- |
| MaxWidth      | Dp     | Default maximum dialog width     | 392.dp |
| CornerRadius  | Dp     | Default corner radius (`coui_round_corner_xxxxl_radius`) | 25.dp |
| outsideMargin | DpSize | Default outer margin for dialog  | DpSize(16.dp, 32.dp) |
| insideMargin  | DpSize | Default inner padding for dialog | DpSize(24.dp, 24.dp) |

#### Functions

| Name              | Return Type | Description                         |
| ----------------- | ----------- | ----------------------------------- |
| titleColor()      | Color       | Get default title color             |
| summaryColor()    | Color       | Get default summary color           |
| backgroundColor() | Color       | Get default dialog background color |

### LocalDismissState

Provides a `(() -> Unit)?` function to close the current popup from within the content. This is a unified dismiss state provided by all overlay components.

```kotlin
val dismiss = LocalDismissState.current
TextButton(
    text = "Close",
    onClick = { dismiss?.invoke() }
)
```
