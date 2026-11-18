# FloatingToolbar

`FloatingToolbar` is a floating toolbar component in Rmuix that renders its content within a container with a rounded background, arranged either horizontally or vertically. The actual placement on the screen is handled by the parent component, typically `Scaffold`.

This component is usually used in conjunction with `Scaffold`, placed in a specific position on the page to provide quick actions or display information.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=floatingToolbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.FloatingToolbar
import io.github.ra1nify.rmuix.kmp.basic.FloatingToolbarDefaults
import io.github.ra1nify.rmuix.kmp.basic.ToolbarPosition // Used for Scaffold
```

## Basic Usage

```kotlin
Scaffold(
    floatingToolbar = {
        FloatingToolbar {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) { // or Column
                IconButton(onClick = { /* Action 1 */ }) {
                    Icon(RmuixIcons.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { /* Action 2 */ }) {
                    Icon(RmuixIcons.Delete, contentDescription = "Delete")
                }
            }
        }
    },
    floatingToolbarPosition = ToolbarPosition.BottomCenter // Set the position
)
```

## Properties

### FloatingToolbar Properties

| Property Name              | Type                   | Description                            | Default Value                          | Required |
| -------------------------- | ---------------------- | -------------------------------------- | -------------------------------------- | -------- |
| modifier                   | Modifier               | Modifier applied to the toolbar        | Modifier                               | No       |
| color                      | Color                  | Background color of the toolbar        | FloatingToolbarDefaults.defaultColor() | No       |
| cornerRadius               | Dp                     | Corner radius of the toolbar           | FloatingToolbarDefaults.CornerRadius   | No       |
| outSidePadding             | PaddingValues          | Padding outside the toolbar            | FloatingToolbarDefaults.OutSidePadding | No       |
| shadowElevation            | Dp                     | The shadow elevation of the toolbar    | FloatingToolbarDefaults.ShadowElevation | No       |
| showDivider                | Boolean                | Show divider line around the toolbar   | false                                  | No       |
| groupMargin                | Dp?                    | Horizontal inner margin of the content | null                                   | No       |
| content                    | @Composable () -> Unit | Composable content area of the toolbar | -                                      | Yes      |

### FloatingToolbarDefaults Object

| Property Name  | Type                    | Description              | Value                                   |
| -------------- | ----------------------- | ------------------------ | --------------------------------------- |
| CornerRadius   | Dp                      | Default corner radius    | 50.dp                                   |
| shadowElevation            | Dp                     | The shadow elevation of the toolbar    | FloatingToolbarDefaults.ShadowElevation | No       |
| defaultColor() | @Composable () -> Color | Default background color | RmuixTheme.colorScheme.surfaceContainer |
| OutSidePadding | PaddingValues           | Default outside padding  | PaddingValues(12.dp, 8.dp)              |

### ToolbarPosition (for Scaffold)

Please refer to the `ToolbarPosition` options in the [Scaffold](../containers/scaffold#toolbarposition-options) documentation.

## Advanced Usage

### Custom Styles

```kotlin
FloatingToolbar(
    color = RmuixTheme.colorScheme.primaryContainer,
    cornerRadius = 16.dp,
    outSidePadding = PaddingValues(24.dp),
    showDivider = false
) {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) { // or Column
        IconButton(onClick = { /* Action 1 */ }) {
            Icon(RmuixIcons.Edit, contentDescription = "Edit", tint = RmuixTheme.colorScheme.onPrimaryContainer)
        }
        IconButton(onClick = { /* Action 2 */ }) {
            Icon(RmuixIcons.Delete, contentDescription = "Delete", tint = RmuixTheme.colorScheme.onPrimaryContainer)
        }
    }
}
```

### Vertically Arranged Content

```kotlin
FloatingToolbar {
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = { /* Action 1 */ }) {
            Icon(RmuixIcons.Edit, contentDescription = "Edit")
        }
        IconButton(onClick = { /* Action 2 */ }) {
            Icon(RmuixIcons.Delete, contentDescription = "Delete")
        }
    }
}
```

### Changing Position with Scaffold

```kotlin
Scaffold(
    floatingToolbar = {
        FloatingToolbar {
            // toolbar content
        }
    },
    floatingToolbarPosition = ToolbarPosition.CenterEnd // Place centered on the right edge
)
```

## Window-width aware margin

COUIBottomFloatingToolbar.changePadding() sets the toolbar's own left/right padding from
coui_floating_toolbar_group_margin_{small,medium,large}_screen, chosen by
COUIResponsiveUtils.{isSmall,isMedium,isLarge}Screen — material's WindowWidthSizeClass
(Compact < 600dp, Medium 600..840dp, Expanded >= 840dp). Passing groupMargin = null reproduces it:
16dp / 24dp / 40dp. FloatingToolbarDefaults.groupMarginFor(widthDp) is the pure resolver, so callers
that already know their width can reuse it.

## EditToolbar (`COUIBottomFloatingEditToolbar`)

`EditToolbar` is the ColorOS bottom floating edit bar: one `COUIBottomFloatingToolbarItemView` per action inside
the same pill (`COUIBottomFloatingToolbar`). Gallery's `BottomFloatingMenuBar` (album, timeline, immersive
fragments) is this widget plus a popup and a show/hide spring.

```kotlin
EditToolbar(
    items =
        listOf(
            EditToolbarItem(icon = RmuixIcons.Share, title = "Share"),
            EditToolbarItem(icon = RmuixIcons.Edit, title = "Edit", redDot = true),
            EditToolbarItem(icon = RmuixIcons.Delete, title = "Delete", enabled = false),
        ),
    onItemClick = { index -> },
)
```

| Stock attribute / source | Parameter or default | Source |
| :--- | :--- | :--- |
| `coui_bottom_floating_edit_toolbar_min_height_without_padding_vertical` | `EditToolbarDefaults.MinHeightWithoutVerticalPadding` = 88dp | resource |
| horizontal padding | small / medium / large = 16dp / 24dp / 40dp | `coui_bottom_floating_edit_toolbar_padding_horizontal_*` |
| menu view height | 56dp small, 44dp medium/large | `coui_bottom_floating_navigation_bar_height` / `coui_bottom_floating_edit_toolbar_menu_height_medium_large` |
| item style | `horizontalItems = null` follows the stock `setHorizontalStyle(!isSmallScreen)` (< 600dp = vertical style 3, else horizontal style 4) | `COUIBottomFloatingEditToolbar` |
| item height | 48dp (style 3) / 40dp (style 4) | `coui_bottom_floating_navigation_bar_item_height` / `coui_bottom_floating_edit_toolbar_item_height_horizontal` |
| item max width | 70dp (vertical) / 98dp (horizontal); single item 148dp / 152dp | `_item_max_width_*` / `_single_item_width_*` |
| icon | 24dp, min item width 44dp | `coui_floating_toolbar_item_icon_width/_height`, `_item_width` |
| icon-title gap | 2dp vertical / 4dp horizontal; icon top margin 4dp (vertical) | `_item_icon_title_gap_*`, `_item_icon_top_margin` |
| title | 10sp vertical / 14sp horizontal (`couiTextButtonM`) | item view `applyEditToolbar*Style()` |
| item space | 8dp | `coui_bottom_floating_navigation_bar_item_space` |
| red dot | 6dp dot or 16dp numbered badge (20dp / 26dp wide, 10sp white), overflow 4dp top / 6-10dp right | `COUIHintRedDot` tokens |

Colours: content `?attr/couiColorLabelPrimaryVariant` (`#B8000000` / `#B8FFFFFF`), disabled
`?attr/couiColorLabelTertiary` (`#42000000` / `#4DFFFFFF`), red dot `coui_color_additional_red`
(`#EB3B2F` / `#EB493D`), badge text `#FFFFFF`.

Known gaps: the stock vertical show/hide spring (`TOOLBAR_TRANSLATION_Y_SPRING_RESPONSE` 0.38 / bounce 0.25) and
the gallery popup window are not part of this component; the pill re-uses `FloatingToolbar`, so the caller
animates visibility. The menu-resource driven item list (`couiEditToolbarMenu`) is passed as `items` instead.
