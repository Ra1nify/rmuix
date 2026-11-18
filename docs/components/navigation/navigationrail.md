# NavigationRail

`NavigationRail` is a side navigation component for wide screens. It supports fixed collapsed,
fixed expanded, and state-controlled layouts.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=navigationRail" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass reads `com.coui.appcompat.material.navigationrail.NavigationRailView` / `NavigationRailItemView` / `NavigationRailMenuView`, the COUI rail style, the `mtrl_navigation_rail_*` / `m3_navigation_rail_*` dimens and the `m3_navigation_rail_item_with_indicator_*_tint` selectors of the Settings APK.

- The COUI rail is the Material rail with a COUI item layout: `NavigationRailItemView` only overrides `getItemLayoutResId()` / `getItemDefaultMarginResId()`, so the dimensions come from the Material tokens.
- Collapsed width is `mtrl_navigation_rail_default_width` = **72dp** (was 80dp); the icon is `mtrl_navigation_rail_icon_size` = **24dp** (was 28dp).
- The expanded width follows `m3_navigation_rail_min_expanded_width` = **220dp** (was 240dp); the Material range is 220dp–360dp.
- Labels use `mtrl_navigation_rail_text_size` = 12dp collapsed and `mtrl_navigation_rail_active_text_size` = 14dp expanded (was 16sp when expanded).
- Item colours follow the Material selectors: `m3_navigation_rail_item_with_indicator_icon_tint` paints the selected icon with `colorOnSecondaryContainer` and idle icons with `colorOnSurfaceVariant`; `m3_navigation_rail_item_with_indicator_label_tint` uses `colorOnSurface` / `colorOnSurfaceVariant`; the active indicator fill is `colorSecondaryContainer`. The library previously used a single `onSurfaceContainer` tint and a `surfaceContainerHigh` pill, so selection was not distinguished at all.
- The colour roles are covered by the new `NavigationRailDefaults.navigationRailItemColors()` and the `colors` parameter on `NavigationRailItem`.

**BREAKING:** expanded width 240dp → 220dp (`m3_navigation_rail_min_expanded_width`), icon 28dp → 24dp, expanded label
16sp → 14sp, and item/indicator colours now switch with selection instead of staying `onSurfaceContainer` /
`surfaceContainerHigh`.

Collapsed width stays at the Material 3 value of **80dp**: it was briefly changed to 72dp during the migration, then
reverted on 2026-11-14 once the dump showed the settings app renders its rail with Compose M3 (see the stock-evidence
section below) - 72dp matched neither M3 nor any `coui_*` dimen.

**Still under review / corrections:** the COUI-only dimen `coui_navigation_rail_item_text_size` (10dp) is
declared but not referenced by any decompiled class - it is probably applied by an OPPO app-level theme, so
it is not adopted. `NavigationRailView.getMaxItemCount()` returns **7**, but that is the
`NavigationBarMenu` **hard cap**: adding an 8th menu item throws `IllegalArgumentException` in the stock
(`NavigationBarMenu.addInternal`), not a visual limit; Settings rails hold far fewer items. A Compose
`content` lambda cannot count its children, so no equivalent throw is enforced. The Material expanded tokens
(`m3_navigation_rail_expanded_item_spacing` 0dp, `m3_navigation_rail_expanded_leading_trailing_space` 20dp,
`m3_navigation_rail_max_expanded_width` 360dp, `m3_navigation_rail_min_expanded_width` 220dp) belong to the
Material widget shipped in the APK, not to a ColorOS-specific rail, and are not adopted.

The settings app renders its rail with **Compose Material 3** (`WideNavigationRail_androidKt`,
`DefaultModalWideNavigationRailOverride`, `MultiChoiceSegmentedButtonRowScope`-style dex output), so the
authoritative dimens are the `m3_navigation_rail_*` ones, not `coui_*`:

| token | value | used for |
| :--- | ---: | :--- |
| `m3_navigation_rail_min_expanded_width` | **220dp** | `NavigationRailDefaults.ExpandedWidth` (already matches) |
| `m3_navigation_rail_max_expanded_width` | 360dp | upper bound of the expanded rail |
| `m3_navigation_rail_item_min_height` | 60dp | minimum item height |
| `m3_navigation_rail_item_padding_top` / `_bottom` | 4dp / 12dp | item vertical padding |
| `m3_navigation_rail_item_active_indicator_margin_horizontal` | 4dp | indicator inset |
| `m3_navigation_rail_expanded_active_indicator_height` | 56dp | expanded indicator height |
| `m3_navigation_rail_expanded_leading_trailing_space` | 20dp | expanded rail inset |
| `m3_navigation_rail_icon_label_padding` / `_horizontal_padding` | 4dp / 8dp | icon-to-label spacing |
| `m3_navigation_rail_label_padding_horizontal` | 2dp | label inset |

The only `coui_*` token is `coui_navigation_rail_item_text_size` (10dp), and no decompiled class reads it.

**Collapsed width:** M3's `NavigationRailWidth` is **80dp**. `NavigationRailDefaults.MinWidth` moved from
72dp to **80dp** on 2026-11-14 - 72dp matched neither M3 nor any dump token.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.NavigationRail
import io.github.ra1nify.rmuix.kmp.basic.NavigationRailItem
import io.github.ra1nify.rmuix.kmp.basic.rememberNavigationRailState
```

## Basic Usage

By default, `NavigationRail` uses the collapsed layout:

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Home", "Profile", "Settings")
val icons = listOf(RmuixIcons.VerticalSplit, RmuixIcons.Contacts, RmuixIcons.Settings)

Row {
    NavigationRail {
        items.forEachIndexed { index, label ->
            NavigationRailItem(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                icon = icons[index],
                label = label
            )
        }
    }
    // Content area
}
```

## Layout Modes

Use the fixed-layout overload and set `expanded` to choose its layout:

```kotlin
NavigationRail(expanded = true) {
    // NavigationRailItem content
}
```

Use the stateful overload to show a toggle and animate between layouts:

```kotlin
val railState = rememberNavigationRailState()

NavigationRail(state = railState) {
    // NavigationRailItem(...)
}
```

Use `railState.expand()`, `railState.collapse()` or `railState.toggle()` to control the layout
programmatically. Omit both `state` and `expanded` for a fixed collapsed rail.

## Properties

### NavigationRail Overloads

`NavigationRail` provides two overloads. Use one of them:

#### Fixed Layout

| Parameter | Type | Description | Default | Required |
| --------- | ---- | ----------- | ------- | -------- |
| expanded  | Boolean | Use the expanded layout | false | No |

#### Expandable Layout

| Parameter | Type | Description | Default | Required |
| --------- | ---- | ----------- | ------- | -------- |
| state | NavigationRailState | Controls the layout and displays the toggle | - | Yes |
| expandContentDescription | String | Toggle description when collapsed | NavigationRailDefaults.ExpandContentDescription | No |
| collapseContentDescription | String | Toggle description when expanded | NavigationRailDefaults.CollapseContentDescription | No |

### Common NavigationRail Parameters

| Property Name              | Type                                   | Description                                   | Default Value                     | Required |
| -------------------------- | -------------------------------------- | --------------------------------------------- | --------------------------------- | -------- |
| modifier                   | Modifier                               | Modifier applied to the rail                  | Modifier                          | No       |
| header                     | @Composable (ColumnScope.() -> Unit)?  | Header content (e.g. FAB or Logo)             | null                              | No       |
| color                      | Color                                  | Background color of the rail                  | RmuixTheme.colorScheme.surface    | No       |
| showDivider                | Boolean                                | Show divider line between rail and content    | true                              | No       |
| defaultWindowInsetsPadding | Boolean                                | Apply default window insets padding           | true                              | No       |
| minWidth                   | Dp                                     | Minimum (collapsed) width of the rail         | NavigationRailDefaults.MinWidth   | No       |
| expandedWidth              | Dp                                     | Width of the rail when expanded               | NavigationRailDefaults.ExpandedWidth | No    |
| scrollState                | ScrollState                            | Scroll state of the rail's content column    | rememberScrollState()             | No       |
| content                    | @Composable ColumnScope.()             | The content of the rail                       | -                                 | Yes      |

### NavigationRailItem Properties

| Property Name | Type        | Description                      | Default Value | Required |
| ------------- | ----------- | -------------------------------- | ------------- | -------- |
| selected      | Boolean     | Whether the item is selected     | -             | Yes      |
| onClick       | () -> Unit  | Callback when the item is clicked| -             | Yes      |
| icon          | ImageVector | Icon of the item                 | -             | Yes      |
| label         | String      | Label of the item                | -             | Yes      |
| modifier      | Modifier    | Modifier applied to the item     | Modifier      | No       |
| enabled       | Boolean     | Whether the item is enabled      | true          | No       |
| colors        | NavigationRailItemColors | Colors of the icon, label and selection indicator | NavigationRailDefaults.navigationRailItemColors() | No |
| badge         | (@Composable () -> Unit)? | Optional badge shown on the item's icon, e.g. a `RedDot` | null | No |

### NavigationRailDefaults Object

The NavigationRailDefaults object provides default values for NavigationRail and NavigationRailItem components.

#### Constants

| Constant Name                      | Type     | Description                                | Default Value |
| ---------------------------------- | -------- | ------------------------------------------ | ------------- |
| MinWidth                           | Dp       | Collapsed rail width (`coui_navigation_rail_min_width`) | 72.dp         |
| MenuWidth                          | Dp       | Width of one item (`coui_navigation_rail_menu_width`) | 64.dp         |
| ExpandedWidth                      | Dp       | Width of the rail when expanded (M3 only; COUI has no expanded rail) | 220.dp        |
| VerticalPadding                    | Dp       | Vertical padding of the content            | 24.dp         |
| HeaderSpacing                      | Dp       | Spacing after the header                   | 24.dp         |
| IconSize                           | Dp       | Icon size (`mtrl_navigation_rail_icon_size`) | 24.dp |
| IconTextSpacing                    | Dp       | Margin above the label (`coui_navigation_rail_text_margin_top`) | 2.dp          |
| ItemVerticalPadding                | Dp       | Vertical padding for each item             | 12.dp         |
| LabelFontSize                      | TextUnit | Label font size (`coui_side_navigation_rail_item_text_size`) | 10.sp         |
| ExpandedLabelFontSize              | TextUnit | Label font size when expanded (`mtrl_navigation_rail_active_text_size`) | 14.sp |
| ExpandedItemHorizontalMargin       | Dp       | Margin between an expanded item and edges  | 12.dp         |
| ExpandedItemCornerRadius           | Dp       | Corner radius of the selected pill         | 16.dp         |
| CollapsedIndicatorVerticalPadding  | Dp       | Padding of a collapsed item's indicator around its icon | 4.dp |
| ExpandedItemContentHorizontalPadding | Dp     | Horizontal padding inside an expanded item | 14.dp         |
| ExpandedItemContentVerticalPadding | Dp       | Vertical padding inside an expanded item   | 14.dp         |
| ExpandedItemIconTextSpacing        | Dp       | Spacing between icon and label (expanded)  | 16.dp         |
| ExpandContentDescription           | String   | Toggle description while collapsed         | "Expand navigation rail" |
| CollapseContentDescription         | String   | Toggle description while expanded          | "Collapse navigation rail" |

#### Methods

| Method Name | Type | Description |
| --- | --- | --- |
| navigationRailItemColors() | NavigationRailItemColors | Creates the stock item colours: `colorOnSecondaryContainer` / `colorOnSurfaceVariant` icons, `colorOnSurface` / `colorOnSurfaceVariant` labels and a `colorSecondaryContainer` indicator |

Pass the result to `NavigationRailItem(colors = …)` to override them per item.
### NavigationRailState

Create it with `rememberNavigationRailState(initialValue)` and pass it to `NavigationRail` to make the rail expandable.

| Member       | Type                | Description                                    |
| ------------ | ------------------- | ---------------------------------------------- |
| currentValue | NavigationRailValue | The current expansion value                    |
| isExpanded   | Boolean             | Whether the rail is currently expanded         |
| expand()     | fun                 | Expands the rail                               |
| collapse()   | fun                 | Collapses the rail                             |
| toggle()     | fun                 | Toggles between collapsed and expanded         |

### NavigationRailValue

| Value     | Description                                     |
| --------- | ----------------------------------------------- |
| Collapsed | The rail is collapsed to `MinWidth`.            |
| Expanded  | The rail is expanded to `ExpandedWidth`.        |
