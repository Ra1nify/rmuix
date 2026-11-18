---
title: OverlayListPopup
requiresScaffoldHost: true
prerequisites:
  - Must be used within `Scaffold` to provide `RmuixPopupHost`
  - Using outside `Scaffold` will cause popup content not to render
  - Multiple nested or side-by-side `Scaffold`s are supported without extra configuration
hostComponent: Scaffold
popupHost: RmuixPopupHost
---

# OverlayListPopup

`OverlayListPopup` is a popup list component in Rmuix used to display a popup menu with multiple options. It provides a lightweight, floating temporary list suitable for various dropdown menus, context menus, and similar scenarios.

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=overlayListPopup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger Prerequisite
This component depends on `Scaffold` providing `RmuixPopupHost` to render popup content. It must be used within `Scaffold`, otherwise popup content will not render correctly.
:::

## Stock ColorOS static review (2026-11-12)

This pass reads the `coui_popup_list_window_*` dimens of the Settings APK.

- The popup window width range is `coui_popup_list_window_min_width` = **168dp** to `coui_popup_list_window_max_width` = **256dp**; `ListPopupColumn` clamped to hard-coded 200dp / 288dp, and `ListPopupDefaults.MinWidth` was 200dp even though the docs pointed at it.
- `COUIPopupListWindow.getMainMenuMinWidth()` returns `coui_popup_list_window_width_with_icon` = **196dp** when the menu **has a submenu** (the chevron is the "icon") and `coui_popup_list_window_min_width` = 168dp otherwise; it is exposed as `ListPopupStockWidthWithIcon` for callers that want the preset, but the list still measures its items rather than forcing it.
- The window background is `coui_popup_window_background.xml`: **12dp** corners (`coui_round_corner_m`) filled with `couiColorBackgroundTop` (`#FFFFFF` / `#333333`). The surface used 9dp corners and the theme `surfaceContainer`.
- `ListPopupDefaults` gains `MaxWidth` so the surface default and the column clamp resolve from the same constants.

**BREAKING:** the popup window is narrower by default (168dp … 256dp instead of 200dp … 288dp), its corners go from 9dp to 12dp and its background becomes the stock `#FFFFFF` / `#333333` instead of the theme `surfaceContainer`.

**Still under review:** the item-level tokens (`coui_popup_list_window_item_min_height` 40dp, `…_item_title_text_size` 16sp, `…_item_icon_margin_left` 20dp, `…_item_title_margin_left` 12dp / right 24dp) belong to the OPPO menu rows, not to this container; the row content comes from the caller. The vertical mask is now **implemented**: `ListPopupColumn` replaces row ripples with one shared mask (`COUITouchRecyclerView`): a press/hover highlight with the 28dp 16.1 radius, an 8dp side inset and `?attr/couiColorPress` / `?attr/couiColorHover`, sliding between rows as the pointer moves (the mask follows `coui_popup_list_window_shared_mask_smooth_round_corner_weight` by drawing the squircle path when the squircle is available, and falls back to a plain rounded rect otherwise). `coui_popup_list_padding_vertical` **is** applied: the column now pads 8dp above and below the list (`ListPopupDefaults.VerticalContentPadding`), matching `COUIPopupListWindow.applyMenuContainerPaddingForRecyclerView()`. The divider tokens are now exposed as `ListPopupDivider` (0.5dp hairline in `?attr/couiColorDivider`, 20dp insets, 52dp start inset with `withIcon = true`) and `ListPopupGroupDivider` (4dp strip in `#14000000`, no night variant); since the popup takes caller content, the caller places them. The mixed list's group divider is now provided too: `ListPopupMixedGroupDivider` mirrors
`MixedPopupListAdapter` - a `coui_popup_list_group_divider_total_height` **12dp** row whose
`coui_popup_list_divider_height` **0.5dp** line in `?attr/couiColorDivider` is inset by
`(12 - 0.5) / 2 = 5.75dp` vertically and `coui_popup_list_default_divider_margin_horizontal` (20dp)
horizontally. Both dividers draw the stroke as a hairline (`>= 1` physical pixel): a 0.5dp box rounds
to nothing at 1x density, so the earlier `height(0.5.dp)` box was invisible on desktop.

Nothing is missing from the divider set; the cascading overlap is covered by
`rememberCascadingSubmenuPositionProvider`, which shifts the submenu by
`coui_popup_list_window_horizontal_overlap_between_main_and_sub_menu` = **14dp** so the two panels
share an edge instead of butting against each other.
## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.overlay.OverlayListPopup
import io.github.ra1nify.rmuix.kmp.basic.ListPopupColumn
```

## Basic Usage

The OverlayListPopup component can be used to create simple dropdown menus:

```kotlin
var showPopup by remember { mutableStateOf(false) }
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Option 1", "Option 2", "Option 3")

Scaffold {
    Box {
        TextButton(
            text = "Click to show menu",
            onClick = { showPopup = true }
        )
        OverlayListPopup(
            show = showPopup,
            alignment = PopupPositionProvider.Align.Start,
            onDismissRequest = { showPopup = false } // Close the popup menu
        ) {
            ListPopupColumn {
                items.forEachIndexed { index, string ->
                    DropdownImpl(
                        text = string,
                        optionSize = items.size,
                        isSelected = selectedIndex == index,
                        index = index,
                        onSelectedIndexChange = {
                            selectedIndex = index
                            showPopup = false // Close the popup menu
                        }
                    )
                }
            }
        }
    }
}
```

## Component States

### Different Alignments

OverlayListPopup can be set with different alignment options:

```kotlin
var showPopup by remember { mutableStateOf(false) }

OverlayListPopup(
    show = showPopup,
    onDismissRequest = { showPopup = false }, // Close the popup menu
    alignment = PopupPositionProvider.Align.Start
) {
    ListPopupColumn {
        // Custom content
    }
}
```

### Disable Window Dimming

```kotlin
var showPopup by remember { mutableStateOf(false) }

OverlayListPopup(
    show = showPopup,
    onDismissRequest = { showPopup = false } // Close the popup menu
    enableWindowDim = false // Disable dimming layer
) {
    ListPopupColumn {
        // Custom content
    }
}
```

## Properties

### OverlayListPopup

| Property Name         | Type                        | Description                                                       | Default Value                              |
| --------------------- | --------------------------- | ----------------------------------------------------------------- | ------------------------------------------ |
| show                  | Boolean                     | Whether to show the popup.                                        | -                                          |
| popupModifier         | Modifier                    | Modifier applied to the popup container.                          | Modifier                                   |
| popupPositionProvider | PopupPositionProvider       | Provides position calculation logic for the popup.                | ListPopupDefaults.DropdownPositionProvider |
| alignment             | PopupPositionProvider.Align | Specifies the alignment of the popup relative to the anchor.      | PopupPositionProvider.Align.Start          |
| enableWindowDim       | Boolean                     | Whether to dim the background when popup is shown.                | true                                       |
| onDismissRequest      | (() -> Unit)?               | Called when the user requests dismissal (e.g., clicking outside). | null                                       |
| onDismissFinished     | (() -> Unit)?               | Invoked after the hide animation completes; not invoked if the hide is cancelled mid-flight (e.g., `show` toggled back to true) | null         |
| maxHeight             | Dp?                         | Maximum height of the popup content.                              | null                                       |
| minWidth              | Dp                          | Minimum width of the popup content.                               | ListPopupDefaults.MinWidth                 |
| renderInRootScaffold  | Boolean                     | Whether to render the popup in the root (outermost) Scaffold. When true, the popup covers the full screen. When false, it renders within the current Scaffold's bounds with position compensation. | true |
| content               | @Composable () -> Unit      | The content to display inside the popup.                          | -                                          |

### ListPopupColumn

| Property Name | Type                   | Description                                    | Default Value |
| ------------- | ---------------------- | ---------------------------------------------- | ------------- |
| content       | @Composable () -> Unit | The list content to display inside the column. | -             |

The column owns the popup's press feedback: it draws the stock shared mask and suppresses the rows' own
ripples, so a pressed or hovered row is highlighted by one sliding layer instead of per-row indications.

### DropdownImpl

`DropdownImpl` can be used as a standard row inside `ListPopupColumn`. Set `enabled = false` to disable a row; disabled rows are not clickable and use the disabled text color.

```kotlin
DropdownImpl(
    text = "Disabled option",
    optionSize = items.size,
    isSelected = false,
    index = 1,
    enabled = false,
    onSelectedIndexChange = {}
)
```

| Property Name         | Type           | Description                                 | Default Value                     |
| --------------------- | -------------- | ------------------------------------------- | --------------------------------- |
| text                  | String         | Text shown for the option                   | -                                 |
| optionSize            | Int            | Total number of options                     | -                                 |
| isSelected            | Boolean        | Whether this option is selected             | -                                 |
| index                 | Int            | Index of this option                        | -                                 |
| dropdownColors        | DropdownColors | Color configuration for the option          | DropdownDefaults.dropdownColors() |
| enabled               | Boolean        | Whether this option can be clicked          | true                              |
| onSelectedIndexChange | (Int) -> Unit  | Callback when this option is clicked        | -                                 |

### PopupPositionProvider.Align

| Value       | Description                                         |
| ----------- | --------------------------------------------------- |
| Start       | Aligns the popup to the start of the anchor.        |
| End         | Aligns the popup to the end of the anchor.          |
| TopStart    | Aligns the popup to the top-start of the anchor.    |
| TopEnd      | Aligns the popup to the top-end of the anchor.      |
| BottomStart | Aligns the popup to the bottom-start of the anchor. |
| BottomEnd   | Aligns the popup to the bottom-end of the anchor.   |
