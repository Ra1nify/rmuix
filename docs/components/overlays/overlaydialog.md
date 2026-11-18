---
title: OverlayDialog
requiresScaffoldHost: true
prerequisites:
  - Must be used within `Scaffold` to provide `RmuixPopupHost`
  - Using outside `Scaffold` will cause popup content not to render
  - Multiple nested or side-by-side `Scaffold`s are supported without extra configuration
hostComponent: Scaffold
popupHost: RmuixPopupHost
---

# OverlayDialog

`OverlayDialog` is a dialog component in Rmuix used to display important information, collect user input, or confirm user actions. The dialog appears above the current interface and supports custom styles and content layouts.

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=overlayDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger Prerequisite
This component depends on `Scaffold` providing `RmuixPopupHost` to render popup content. It must be used within `Scaffold`, otherwise popup content will not render correctly.
:::

## Stock ColorOS static review (2026-11-12)

This pass reads the Settings APK `coui_center_alert_dialog_layout.xml` family, the `COUIDialogTextAppearance.*` styles and the corner-radius attributes consumed by `COUIAlertDialogBuilder`. Only the centred alert dialog and the bottom-sheet radius are covered; the material layer is still a deviation.

- Maximum width `coui_dialog_max_width` = **392dp** (was 328dp); outside margin `coui_dialog_layout_margin_horizontal` / `_vertical` = **16dp / 32dp** (was 16dp / 24dp).
- Corner radius is **platform-branched** inside `COUIAlertDialogMaxLinearLayout.setOutLineProvider()`: without smooth-corner support it reads `couiRoundCornerXXXXL` (32dp); `useOs15RoundCorner()` reads `couiRoundCornerXXXXLRadius` (25dp); with smooth style ≠ 1 or OS code ≤ 37 it falls back to 32dp; and on a smooth-corner 16.1+ device it reads `coui_round_corner_xxxxl_radius_large_16_1` (**45dp**). `useOs15RoundCorner()` is `styleType == 0 ? true : (blur window && OS code == 37)`. The library ships **25dp** — the no-smooth and ColorOS 15 branches — and does not reach the 45dp branch.
- Title follows `COUIAlertDialogTitleStyle` -> `couiColorLabelPrimary` (`#E6000000` / `#E6FFFFFF`); the summary follows `COUIAlertDialogMessageStyle` -> `couiColorLabelSecondary` (`#8A000000` / `#8AFFFFFF`). Sizes were already right (title 18sp medium, message 13sp).
- The bottom sheet (`COUIBottomSheetDialog` → `COUIPanelPercentFrameLayout.initRadiusAndWeight()`) has the **same four-branch table**; its non-smooth default is the plain `coui_round_corner_xxxxl` = 32dp, and on smooth 16.1+ devices it also resolves 45dp. The two components read the same corner scale and land on different branches — they are not two unrelated tokens.

**BREAKING:** `DialogDefaults.MaxWidth` 328 -> 392dp, `CornerRadius` 25 -> 32dp (smooth corners are off by default), `outsideMargin` 16/24 -> 16/32, and the default title/summary colours change; `BottomSheetDefaults.cornerRadius` 22 -> 32dp.

**Scroll caps:** a title taller than `DialogDefaults.TitleScrollMaxHeight` (80dp,
`coui_alert_dialog_builder_title_scroll_max_height`) or a message taller than
`DialogDefaults.MessageScrollMaxHeight` (80dp, `coui_alert_dialog_builder_content_max_height`) scrolls
inside its slot, matching the stock `COUIMaxHeightScrollView` / `COUIMaxHeightNestedScrollView` wrappers.
The stock fading edge (`requiresFadingEdge`) is not drawn yet.

**Center-dialog layout (stock `coui_center_alert_dialog_layout.xml`):** the title and message carry
`coui_alert_dialog_message_padding_left` (24dp) side margins themselves, the title keeps
`coui_alert_dialog_title_margin_bottom` (6dp) below it, the message keeps
`coui_alert_dialog_scroll_padding_bottom_message_tallDialog` (16dp), and the button panel is full-bleed:
equal-width text-only buttons (`COUIAlertDialogBottomButtonSecondary`: `coui_alert_dialog_button_height`
44dp, `coui_alert_dialog_button_horizontal_padding` 12dp, 4dp vertical padding, two-line cap) in the
label-primary colour, with `confirmButtonDestructive = true` switching the confirm label to the error
colour (`COUIAlertDialogBottomButtonSecondary.Last`). `confirmButtonText` / `dismissButtonText` therefore
render a real ColorOS button bar instead of a filled miuix button.

**BREAKING:** the default dialog background now resolves `?attr/couiColorSurface`
(`coui_alert_dialog_background`, `#FFFFFF` / `#1E1E1E`) instead of the theme background role, and the
optional confirm/dismiss footer renders as the stock full-width text button bar.

**Still under review:** the stock background is a blurred, tinted backdrop (`COUIBackgroundBlurBuilder`,
200px blur, `coui_dialog_list_mix_blur_light` `#DC909090` / dark `#8C777777`, blend `#509F9F9F` /
`#B2292929`) built on the private `com.oplus.graphics` APIs, which cannot be reproduced offline. The
library paints the solid `couiColorSurface` fallback; a blurred surface can be supplied today through the
`surface` slot when the caller owns a `LayerBackdrop` (the overlay variants draw in the same scene, the
window variants do not). The OS-version branch that switches the radius to 45dp is not selected at runtime.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.overlay.OverlayDialog
```

## Basic Usage

OverlayDialog component provides basic dialog functionality:

```kotlin
var showDialog by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "Show Dialog",
        onClick = { showDialog = true }
    )

    OverlayDialog(
        title = "Dialog Title",
        summary = "This is a basic dialog example that can contain various content.",
        show = showDialog,
        onDismissRequest = { showDialog = false } // Close dialog
    ) {
        TextButton(
            text = "Confirm",
            onClick = { showDialog = false }, // Close dialog
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

## Properties

### OverlayDialog Properties

| Property Name              | Type                   | Description                                                   | Default Value                         | Required |
| -------------------------- | ---------------------- | ------------------------------------------------------------- | ------------------------------------- | -------- |
| show                       | Boolean                | Whether to show the dialog                                    | -                                     | Yes      |
| modifier                   | Modifier               | Modifier applied to the dialog                                | Modifier                              | No       |
| title                      | String?                | Dialog title                                                  | null                                  | No       |
| titleColor                 | Color                  | Title text color                                              | DialogDefaults.titleColor()      | No       |
| summary                    | String?                | Dialog summary text                                           | null                                  | No       |
| summaryColor               | Color                  | Summary text color                                            | DialogDefaults.summaryColor()    | No       |
| backgroundColor            | Color                  | Dialog background color                                       | DialogDefaults.backgroundColor() | No       |
| enableWindowDim            | Boolean                | Whether to enable dimming layer                               | true                                  | No       |
| onDismissRequest           | (() -> Unit)?          | Called when the user requests dismissal (outside tap or back) | null                                  | No       |
| onDismissFinished          | (() -> Unit)?          | Invoked after the hide animation completes; not invoked if the hide is cancelled mid-flight (e.g., `show` toggled back to true) | null              | No       |
| outsideMargin              | DpSize                 | Dialog external margin                                        | DialogDefaults.outsideMargin     | No       |
| insideMargin               | DpSize                 | Dialog internal content margin                                | DialogDefaults.insideMargin      | No       |
| defaultWindowInsetsPadding | Boolean                | Whether to apply default window insets padding                | true                                  | No       |
| renderInRootScaffold       | Boolean                | Whether to render the dialog in the root (outermost) Scaffold. When true, the dialog covers the full screen. When false, it renders within the current Scaffold's bounds | true | No |
| maxWidth                   | Dp                     | Maximum width of the dialog                                   | DialogDefaults.MaxWidth          | No       |
| largeScreen                | Boolean?               | Overrides the large-screen presentation (centered scale/fade instead of bottom slide-in). When null, detected from the window size | null | No |
| cornerRadius               | Dp?                    | Overrides the dialog corner radius. When null, DialogDefaults.CornerRadius for the centered presentation, or DialogDefaults.CornerRadius for the bottom presentation as well | null | No |
| cornerRadius (via `DialogDefaults.cornerRadius(mode)`) | CornerMode | The four stock branches: `Legacy` 32dp / `Os15Smooth` 25dp / `Os16NonSmooth` 32dp / `LargeOS161` 45dp, all on the XXXXL tier. Compose cannot read the device flags, so the caller picks the mode and passes the resolved radius to `cornerRadius` | `Os16NonSmooth` |
| dimColor                   | Color                  | Dimming color before opening/closing and predictive-back progress are applied | RmuixTheme.colorScheme.windowDimming | No |
| icon | (@Composable () -> Unit)? | Top-aligned slot before the title, mirroring the stock `android:id/icon` | null | No |
| surface                    | (@Composable DialogSurfaceScope.() -> Modifier)? | Replaces the default background and clip, before content padding | null | No |
| confirmButtonText          | String?                | Text of the confirm button; non-null draws a `COUIAlertDialog`-style right-aligned footer with the two text buttons | null | No |
| onConfirm                  | (() -> Unit)?          | Invoked by the confirm button; falls back to `onDismissRequest` | null | No |
| confirmButtonDestructive | Boolean | Renders the confirm label in the error colour (`COUIAlertDialogBottomButtonSecondary.Last`) | false | No |
| dismissButtonText          | String?                | Text of the dismiss button | null | No |
| onDismiss                  | (() -> Unit)?          | Invoked by the dismiss button; falls back to `onDismissRequest` | null | No |
| content                    | @Composable () -> Unit | Dialog content                                                | -                                     | Yes      |

### DialogDefaults Object

The DialogDefaults object provides default settings for the OverlayDialog component.

#### Properties

| Property Name | Type   | Description                    | Value |
| ------------- | ------ | ------------------------------ | ----- |
| MaxWidth      | Dp     | Default maximum dialog width   | 392.dp |
| CornerRadius  | Dp     | Default corner radius (`coui_round_corner_xxxxl`, the smooth-off branch) | 32.dp |
| outsideMargin | DpSize | Default dialog external margin | DpSize(16.dp, 32.dp) |
| insideMargin  | DpSize | Default dialog internal margin | DpSize(24.dp, 24.dp) |

#### Functions

| Function Name     | Return Type | Description                         |
| ----------------- | ----------- | ----------------------------------- |
| titleColor()      | Color       | Get default title color             |
| summaryColor()    | Color       | Get default summary color           |
| backgroundColor() | Color       | Get default dialog background color |

## Advanced Usage

### Custom Surface

The optional `surface` slot replaces the dialog background and clipping while retaining the host's layout, animation, insets and dismissal behavior. `backgroundColor` applies only when `surface` is null. The slot receives a `DialogSurfaceScope`:

| Property | Type | Description |
| --- | --- | --- |
| cornerRadius | Dp | Resolved radius, including explicit overrides and screen-dependent defaults |
| scrimColor | State&lt;Color&gt; | Current animated dimming color; transparent when window dimming is disabled |
| scale | State&lt;Float&gt; | Combined opening/closing and predictive-back scale, for screen-aligned backdrop sampling |

Read animated state in the drawing phase so custom drawing follows the host's current animation.

### Custom Styled Dialog

```kotlin
var showDialog by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "Show Custom Styled Dialog",
        onClick = { showDialog = true }
    )

    OverlayDialog(
        title = "Custom Style",
        summary = "This dialog uses custom colors and margins",
        show = showDialog,
        onDismissRequest = { showDialog = false }, // Close dialog
        titleColor = Color.Blue,
        summaryColor = Color.Gray,
        backgroundColor = Color(0xFFF5F5F5),
        outsideMargin = DpSize(20.dp, 20.dp),
        insideMargin = DpSize(30.dp, 30.dp)
    ) {
        Text(
            text = "Custom Content Area",
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        TextButton(
            text = "Close",
            onClick = { showDialog = false }, // Close dialog
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

### Creating a Confirmation Dialog

```kotlin
var showConfirmDialog by remember { mutableStateOf(false) }
var result by remember { mutableStateOf("") }

Scaffold {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(
            text = "Show Confirmation Dialog",
            onClick = { showConfirmDialog = true }
        )
        
        Text("Result: $result")
    }
    
    OverlayDialog(
        title = "Confirm Action",
        summary = "This action is irreversible, do you want to proceed?",
        show = showConfirmDialog,
        onDismissRequest = { showConfirmDialog = false } // Close dialog
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                text = "Cancel",
                onClick = { 
                    result = "User cancelled the action"
                    showConfirmDialog = false // Close dialog
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                text = "Confirm",
                onClick = { 
                    result = "User confirmed the action"
                    showConfirmDialog = false // Close dialog 
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        }
    }
}
```

### Dialog with Input Field

```kotlin
var showDialog by remember { mutableStateOf(false) }
var textFieldValue by remember { mutableStateOf("") }

Scaffold {
    TextButton(
        text = "Show Input Dialog",
        onClick = { showDialog = true }
    )

    OverlayDialog(
        title = "Please Enter Content",
        show = showDialog,
        onDismissRequest = { showDialog = false } // Close dialog
    ) {
        TextField(
            modifier = Modifier.padding(bottom = 16.dp),
            value = textFieldValue,
            maxLines = 1,
            onValueChange = { textFieldValue = it }
        )
        
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                text = "Cancel",
                onClick = { showDialog = false }, // Close dialog
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                text = "Confirm",
                onClick = { showDialog = false }, // Close dialog
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary() // Use theme color
            )
        }
    }
}
```

### Dialog with Form

```kotlin
var showDialog by remember { mutableStateOf(false) }
var dropdownSelectedOption by remember { mutableStateOf(0) }
var switchState by remember { mutableStateOf(false) }
val dropdownOptions = listOf("Option 1", "Option 2")

Scaffold {
    TextButton(
        text = "Show Form Dialog",
        onClick = { showDialog = true }
    )

    OverlayDialog(
        title = "Form Dialog",
        show = showDialog,
        onDismissRequest = { showDialog = false } // Close dialog
    ) {
        Card(
            colors = CardDefaults.defaultColors(
                color = RmuixTheme.colorScheme.secondaryContainer,
            ),
        ) {
            OverlayDropdownPreference(
                title = "Dropdown Selection",
                items = dropdownOptions,
                selectedIndex = dropdownSelectedOption,
                onSelectedIndexChange = { dropdownSelectedOption = it }
            )
            
            SwitchPreference(
                title = "Switch Option",
                checked = switchState,
                onCheckedChange = { switchState = it }
            )
        }
        
        Spacer(Modifier.height(12.dp))
        
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                text = "Cancel",
                onClick = { showDialog = false }, // Close dialog
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                text = "Confirm",
                onClick = { showDialog = false }, // Close dialog
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary() // Use theme color
            )
        }
    }
}
```

### Dialog with Color Picker

```kotlin
var showColorDialog by remember { mutableStateOf(false) }
Scaffold {
    TextButton(
        text = "Select Color",
        onClick = { showColorDialog = true }
    )
    
    OverlayDialog(
        title = "Select Color",
        show = showColorDialog,
        onDismissRequest = { showColorDialog = false } // Close dialog
    ) {
        Column {
            Text(text = "Dialog content goes here")
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    text = "Cancel",
                    onClick = { showColorDialog = false } // Close dialog
                )
                TextButton(
                    modifier = Modifier.weight(1f),
                    text = "Confirm",
                    colors = ButtonDefaults.textButtonColorsPrimary(), // Use theme color
                    onClick = {
                        showColorDialog = false // Close dialog
                        // Handle confirm logic
                    }
                )
            }
        }
    }
}
```
