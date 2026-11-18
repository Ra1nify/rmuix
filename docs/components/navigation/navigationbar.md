# NavigationBar

## Shared Stock Spotlight

Spotlight calls now reuse the [shared Spotlight](../media/spotlight): the official lighting shader, presets and springs replace the old power glow/radial fallback, and blending occurs after content. This shared-engine replacement does not certify each component's system material gate or preset selection. Button/Checkbox spotlight options still default off.

BREAKING: default spotlight tint is white; arbitrary internal 40/140/380dp radii are removed. Explicit tint and radius overrides remain available through the public modifier.


`NavigationBar` is a bottom navigation bar component in Rmuix styled according to ColorOS 17 specifications. It supports 2 to 5 navigation items, offering different display modes (icon only, icon and text, icon with selected label).

`FloatingNavigationBar` is a floating-style bottom navigation bar: a 56dp capsule for 2 to 5 items that also
supports the four ColorOS 17 three-island modes (Single / Left / Right / Both) through its
`startAccessory` / `endAccessory` slots, and an optional SkSL melt-ball bridge (`meltEffectMode = 1`).

These components are typically used in conjunction with the `Scaffold` component to maintain consistent layout and behavior across different pages in the application.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=navigationBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass reads `COUINavigationMenuView` / `COUINavigationItemView`, `COUIMaskEffectDrawable` and the `coui_tool_navigation_*` / `coui_navigation_*` resources of the Settings APK. It covers the standard bottom bar only; the floating capsule has its own review.

- The bar background is `?attr/couiColorBar` (`coui_color_bar` = `#FAFAFA` / `#1E1E1E`). An earlier pass used `couiColorSurface`; that was wrong.
- The items are inset by `coui_tool_navigation_edge_item_padding` = **12dp**; `onMeasure` splits `width - 2 * padding` across the visible items.
- The hairline above the bar is `coui_navigation_shadow_height` = **0.33dp**.
- Each item paints a full-size state mask (`COUIMaskEffectDrawable`) with `coui_navigation_item_background_radius` = **8dp** for hover, focus and press. Hover uses `couiColorHover` (`#14000000` / `#26FFFFFF`), press `couiColorPress` (`#1F000000` / `#33FFFFFF`) and focus the primary accent at 31/51 of 255. The library draws the same three layers with the shared state-effect spring and Android `ArgbEvaluator` colour interpolation, in the source's hover → focus → press order.
- Because the mask is the press feedback, the standard item no longer dims its content while pressed; the floating capsule keeps its press-alpha path until its own review.
- Bar and item are **both 56dp**: `coui_navigation_normal_item_height` (read by `COUINavigationItemView`) and `coui_tool_navigation_item_height` (the height of `coui_navigation_item_layout.xml`) agree. The 54dp `coui_navigation_item_height` dimen exists but is **never referenced**, so it is not the item height — an earlier revision of this page attributed 54dp to it and added 1dp of breathing room that the stock bar does not have. `ItemHeight` is therefore 56dp and the vertical padding is zero.
  - Unchanged item metrics: 24dp icon, 9dp icon top padding, 2dp icon/label gap, 10sp label.
- `coui_navigation_item_layout` gives each item a 2dp start/end margin (`coui_navigation_item_half_gap`), so neighbours sit 4dp apart (`coui_navigation_item_gap`); the row's 12dp inset still applies at the outer edges.
- Both label views declare `sans-serif-medium`, so selection is not a weight change; the item only becomes bold in the old library convention.
- The decompiled item does **not** restyle the selected item outside `navigationType="enlarge"`: `coui_bottom_tool_navigation_item_selector` has no selected entry (idle and selected both resolve to `couiColorLabelPrimary`), and the layout's `navigation_bar_item_active_indicator_view` stays 0x0 unless the Material theme supplies an indicator style. Settings itself uses `navigationType="tool"` (a bottom action bar), not a tab bar.

**BREAKING (reverted):** `NavigationBarDefaults.ItemHeight` stays at 56dp — the earlier "drop to 54dp" was based on a dead dimen and has been undone; the default `color` becomes `couiColorBar`, the row gains 12dp side insets, the divider becomes 0.33dp, items gain a 4dp gap, labels become medium in both states, and the standard item gains the 8dp state mask instead of a pressed-content dim. `NavigationBarDefaults` gains `EdgePadding`, `DividerHeight`, `ItemCornerRadius` and `ItemGap`.

**Two icon states:** the stock item view clears its icon tint (`setIconTintList(null)`) and lets the artwork switch between states, so `NavigationBarItem` gained an optional `selectedIcon` (default `null` draws `icon` in both states):

```kotlin
NavigationBarItem(
    selected = selected,
    onClick = { },
    icon = outlineHome,        // the caller's idle artwork
    selectedIcon = filledHome, // the caller's selected artwork
    label = "Home",
)
```

**Icon box vs artwork:** `COUINavigationItemView` sets the icon *container* to `coui_navigation_normal_icon_size` (**40dp**), while the artwork inside is `coui_navigation_icon_size` (**24dp**). This component draws the 24dp artwork, so a 40dp value would be wrong here.

**Enlarge variant (`navigationType="enlarge"`):** whose items are 70dp tall (`coui_navigation_enlarge_item_height`) and swap on selection - the selected item draws the 64dp icon with no top margin and hides its label, the others keep the 24dp icon with a 9dp top margin and their label.

**Icon artwork is drawn untinted:** `COUINavigationItemView` calls `setIconTintList(null)` (line 285), the
floating item view sets `mIcon.setImageDrawable(drawable.mutate())` on a `COUIFadeMaskImageView`, and the
accessory view draws its drawable directly - none of them apply `coui_navigation_item_icon_color.xml`
(disabled `#F5F6F7`, pressed/selected `?attr/couiColorPressBackground`, normal `?attr/couiColorPrimary`). The
library therefore no longer tints icons with `colors.contentColor(selected)`: pass a `selectedIcon` (or
themed artwork) for state changes. Labels keep their text-selector colour. `NavigationBar(enlarged = true)`
models `NAVIGATION_TYPE_ENLARGE` since 2026-11-14: items are **70dp** tall
(`coui_navigation_enlarge_item_height`), the selected item draws a **64dp** icon with no top padding and
hides its label, while unselected items keep the 24dp icon, the 9dp top padding
(`coui_navigation_enlarge_icon_margin_top`) and their label; **Correction:** the 4dp neighbour gap (2dp per item, `coui_navigation_item_half_gap` / `coui_navigation_item_gap`, `NavigationBarDefaults.ItemGap`) and the badge offsets (`coui_navigation_red_dot_offset` 1dp, numbered 8dp/5dp -> `BadgeDotOffset` / `BadgeNumberOffset`) **are** modelled. Not ported: the item-count caps (`COUINavigationView.getMaxItemCount()` = 10, `getMaxItemCount(width)` = 7 / 6 / 5 by window class; `COUINavigationMenuView.MAX_ITEM_COUNT*`) and the `COUINavigationPopupMenu` overflow menu that goes with them.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.NavigationBar
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarDefaults
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarItem
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBar
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBarItem
import io.github.ra1nify.rmuix.kmp.basic.FloatingAccessoryButton
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarDisplayMode
```

## Basic Usage

### NavigationBar

The NavigationBar component can be used to create bottom navigation menus fixed to the bottom:

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Home", "Profile", "Settings")
val icons = listOf(RmuixIcons.VerticalSplit, RmuixIcons.Contacts, RmuixIcons.Settings)

Scaffold(
    bottomBar = {
        NavigationBar {
            items.forEachIndexed { index, label ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    icon = icons[index],
                    label = label
                )
            }
        }
    }
)
```

### FloatingNavigationBar

The FloatingNavigationBar component can be used to create floating navigation menus at the bottom:

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Home", "Profile", "Settings")
val icons = listOf(RmuixIcons.VerticalSplit, RmuixIcons.Contacts, RmuixIcons.Settings)

Scaffold(
    bottomBar = {
        FloatingNavigationBar(
            mode = NavigationBarDisplayMode.IconAndText
        ) {
            items.forEachIndexed { index, label ->
                FloatingNavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    icon = icons[index],
                    label = label
                )
            }
        }
    }
)
```


The capsule's background can be replaced through the optional `surface` slot, which receives the resolved
corner radius: `surface = { radius -> Modifier.squircleSurface(Color.Transparent, radius) }` keeps the stock
geometry while letting a material layer (see `rmuix-glass`'s `GlassFloatingNavigationBar`) paint the pill.
`surface == null` (the default) keeps the solid `color` background.

### Custom Colors

Both item components accept `colors` from `NavigationBarDefaults.navigationBarItemColors()`. For example, use the theme's primary color for the selected item:

```kotlin
val itemColors = NavigationBarDefaults.navigationBarItemColors(
    unselectedContentColor = RmuixTheme.colorScheme.onSurfaceContainer,
    selectedContentColor = RmuixTheme.colorScheme.primary,
)

NavigationBar {
    items.forEachIndexed { index, label ->
        NavigationBarItem(
            selected = selectedIndex == index,
            onClick = { selectedIndex = index },
            icon = icons[index],
            label = label,
            colors = itemColors,
        )
    }
}
```

Colors are base colors: their alpha is multiplied by `0.4` when unselected, `0.6` when unselected and pressed, and `0.5` when selected and pressed. Selected content defaults to `RmuixTheme.colorScheme.primary`.

## Properties

### Touch deformation

Pressing and dragging on a `FloatingAccessoryButton` stretches it along the finger movement and thins
it on the cross axis, keeping the area roughly constant — the same math as ColorOS
`COUIDeformAnimatorUnit` / `TouchMotionRubberBand`:

```text
rubber band:  sign(o) * (1 - 1 / (1 + |o| * curve / max)) * max     curve 0.55, max 150dp
main axis:    1.2 max scale, grown along the rubber curve
cross axis:   reciprocal of the main axis (area conserving)
both axes:    geometric blend weighted by |dx| / (|dx| + |dy|)
release:      hand-up spring, bounce 0.65 / response 0.42
```

Defaults: `DeformMaxScale = 1.2f`, `DeformRubberCurveRatio = 0.55f`, `DeformMaxRubberDistance = 150.dp`,
`HandUpSpringDamping = 0.35f`, `HandUpSpringResponse = 0.42f`.
### NavigationBar Properties

| Property Name              | Type                     | Description                         | Default Value                         | Required |
| -------------------------- | ------------------------ | ----------------------------------- | ------------------------------------- | -------- |
| modifier                   | Modifier                 | Modifier applied to the nav bar     | Modifier                              | No       |
| color                      | Color                    | Background color of the nav bar     | `couiColorBar` (see review) | No       |
| showDivider                | Boolean                  | Show top divider line or not        | true                                  | No       |
| defaultWindowInsetsPadding | Boolean                  | Apply default window insets padding | true                                  | No       |
| mode                       | NavigationBarDisplayMode | Display mode for items              | NavigationBarDisplayMode.IconAndText  | No       |
| enlarged                   | Boolean                  | `navigationType=enlarge`: 70dp items, selected icon 64dp, no label | `false` | No       |
| content                    | @Composable RowScope.()  | The content of the nav bar          | -                                     | Yes      |

### NavigationBarDefaults Object

| Constant | Type | Description | Value |
| --- | --- | --- | --- |
| BarHeight | Dp | Bar height (`coui_navigation_normal_item_height`) | 56.dp |
| EnlargedItemHeight | Dp | Item/bar height in the `enlarge` variant (`coui_navigation_enlarge_item_height`) | 70.dp |
| EnlargedIconSize | Dp | Selected icon in the `enlarge` variant (`coui_navigation_enlarge_icon_size`) | 64.dp |
| EnlargedIconTopPadding | Dp | Top inset of an unselected icon (`coui_navigation_enlarge_icon_margin_top`) | 9.dp |
| ItemHeight | Dp | Item height (same 56dp as the bar: `coui_navigation_normal_item_height` / `coui_tool_navigation_item_height`) | 56.dp |
| EdgePadding | Dp | Side inset before the items (`coui_tool_navigation_edge_item_padding`) | 12.dp |
| DividerHeight | Dp | Hairline above the bar (`coui_navigation_shadow_height`) | 0.33.dp |
| ItemCornerRadius | Dp | Item state-mask radius (`coui_navigation_item_background_radius`) | 8.dp |
| ItemGap | Dp | Margin per item side (`coui_navigation_item_half_gap`; 4dp between items) | 2.dp |
| IconSize | Dp | Icon size (`coui_navigation_icon_size`) | 24.dp |
| LabelFontSize | TextUnit | Label size (`coui_navigation_item_text_size`) | 10.sp |
| IconTopPadding | Dp | Icon top padding (`coui_navigation_icon_margin_top`) | 9.dp |
| IconTextGap | Dp | Icon/label gap (`coui_navigation_text_margin_top`) | 2.dp |
| BadgeDotOffset | DpSize | Dot-badge outward offset (`coui_navigation_red_dot_offset`) | 1x1dp |
| BadgeNumberOffset | DpSize | Numbered-badge outward offset (`..._with_number_horizontal/vertical_offset`) | 8x5dp |
| TouchEffectRadius | Dp | «...touch_effect_radius»: radius of the radial finger glow, drawn by the port | 40.dp |
| MeltBallExtension | Dp | Stock meta-ball extension (`..._melt_ball_param_extension`), not yet applied | 4.dp |
| MeltBallFactorX / Y | Float | Stock `MELT_BALL_EXTENSION_FACTOR_{X,Y}`, not yet applied | 1.5 / 2.0 |
| AccessoryMinScale | Float | Minimum scale of a collapsed satellite button (`doAlphaScaleAnim` to 5000/10000) | 0.5f |
| AccessoryAlphaResponse / Scale+Translation Damping,Response | Float | Expand/collapse springs: alpha response 0.35, scale/translation bounce 0.35 -> damping 0.65, response 0.55 | see defaults |
| `showSpotLight` | Boolean | 40dp radial press glow of the accessory button (`touch_effect_radius` + `0x26` centre alpha, spring response 0.2) | true | optional |
| BottomPadding | Dp | Label bottom padding | 4.dp |

> **Indicator colour:** `indicatorColor` defaults to white in light theme, so on the default white capsule the
> sliding indicator is effectively invisible and selection reads from the label weight and icon tint. Pass an
> explicit colour (the stock floating bar draws translucent mask layers instead of a solid pill, so there is no
> token to default to).

### FloatingNavigationBar Properties

| Property Name              | Type                     | Description                             | Default Value                           | Required |
| -------------------------- | ------------------------ | --------------------------------------- | --------------------------------------- | -------- |
| modifier                   | Modifier                 | Modifier applied to the nav bar         | Modifier                                | No       |
| mode                       | NavigationBarDisplayMode | Item layout (`IconOnly`, `IconAndText`, `IconWithSelectedLabel`) | NavigationBarDisplayMode.IconAndText | No |
| meltEffectMode             | Int                      | `1` draws the SkSL melt-ball bridge between capsule and satellite | 0                         | No       |
| startAccessory             | (@Composable () -> Unit)? | Leading satellite button (see `FloatingAccessoryButton`)          | null                      | No       |
| endAccessory               | (@Composable () -> Unit)? | Trailing satellite button                                        | null                      | No       |
| startAccessoryVisible      | Boolean                  | Expands / collapses the leading satellite                        | true                      | No       |
| endAccessoryVisible        | Boolean                  | Expands / collapses the trailing satellite                       | true                      | No       |
| indicatorColor             | Color                    | Sliding selection indicator                                      | white / `#2C2C2E` in dark | No       |
| color                      | Color                    | Background color of the nav bar         | RmuixTheme.colorScheme.surfaceContainer | No       |
| cornerRadius               | Dp                       | Corner radius of the nav bar            | FloatingNavigationBarDefaults.CornerRadius | No |
| horizontalAlignment        | Alignment.Horizontal     | Horizontal alignment within its parent  | CenterHorizontally                      | No       |
| horizontalOutSidePadding   | Dp                       | Horizontal padding outside the nav bar  | FloatingNavigationBarDefaults.HorizontalOutSidePadding | No       |
| shadowElevation            | Dp                       | The shadow elevation of the nav bar     | FloatingNavigationBarDefaults.ShadowElevation          | No       |
| showDivider                | Boolean                  | Show divider line around the nav bar    | false                                   | No       |
| defaultWindowInsetsPadding | Boolean                  | Apply default window insets padding     | true                                    | No       |
| content                    | @Composable () -> Unit   | The content of the nav bar              | -                                       | Yes      |

### FloatingNavigationBarDefaults Object

The FloatingNavigationBarDefaults object provides ColorOS 17 default values for FloatingNavigationBar and FloatingNavigationBarItem components.

#### Constants

| Constant Name            | Type  | Description                                   | Default Value |
| ------------------------ | ----- | --------------------------------------------- | ------------- |
| BarHeight                | Dp    | ColorOS 17 floating bar height                | 56.dp         |
| ItemHeight               | Dp    | ColorOS 17 item height                        | 48.dp         |
| ItemMaxWidth             | Dp    | ColorOS 17 item max width                     | 70.dp         |
| ItemPadding              | Dp    | Text-only item horizontal padding (`coui_bottom_floating_navigation_bar_item_padding`) | 12.dp |
| ItemIconTopMargin        | Dp    | Item vertical content padding (`coui_bottom_floating_navigation_bar_item_icon_top_margin`) | 4.dp |
| CornerRadius             | Dp    | ColorOS 17 corner radius                      | 28.dp         |
| HorizontalOutSidePadding | Dp    | Horizontal outside padding                    | 36.dp         |
| ShadowElevation          | Dp    | Shadow elevation                              | 1.dp          |
| HorizontalPadding        | Dp    | Horizontal padding inside the bar             | 16.dp         |
| ItemSpacing              | Dp    | Spacing between items                         | 8.dp          |
| IconSize                 | Dp    | Icon size                                     | 24.dp         |
| IconTextGap              | Dp    | Gap between icon and title                    | 2.dp          |
| NaviDistance             | Dp    | Distance above bottom navigation inset        | 8.dp          |
| GestureNaviDistance | Dp | Distance above the bottom when no navigation-bar inset exists | 36.dp |
| ItemHorizontalPadding | Dp | Padding inside a horizontal (icon + label) item | 4.dp |
| ItemBackgroundHorizontalPadding | Dp | Horizontal overhang of the selected item background (`..._item_bg_horizontal_padding`) | 8.dp |
| SideSpacing | Dp | Gap between the capsule and a satellite button (`..._Minimum_spacing_with_slide_view`) | 12.dp |
| MinHeightWithoutVerticalPadding | Dp | Bar height without vertical padding (`..._min_height_without_padding_vertical`) | 88.dp |
| LargeIconSize / LargeIconOverflow | Dp | Selected large icon and its allowed overflow (`..._large_icon_size` / `_large_icon_overflow`) | 40.dp / 14.dp |
| IconTitleGapHorizontal | Dp | Icon / label gap of horizontal items (`..._item_icon_title_gap_horizontal`) | 4.dp |
| IconPadding | Dp | Icon padding inside an item | 10.dp |
| IndicatorCornerRadius | Dp | Sliding selection indicator radius | 20.dp |
| ShadowRadius | Dp | Capsule shadow softness | 10.dp |
| StretchMaxDistance / StretchCurveRatio | Dp / Float | Drag rubber band (`maxStretchDistance` / `rubberCurveRatio`) | 28.dp / 0.05f |
| DeformMaxRubberDistance / DeformRubberCurveRatio / DeformMaxScale | Dp / Float | Deformation (`maxDeformRubberDistance` / `deformRubberCurveRatio` / `maxScale`) | 150.dp / 0.55f / 1.2f |
| DragSpringDamping / DragSpringResponse | Float | Drag spring (stock `bounce 0.15`; damping = 1 - bounce) | 0.85f / 0.15f |
| HandUpSpringDamping / HandUpSpringResponse / HandUpSpringLargeAreaResponse | Float | Hand-up spring (stock `bounce 0.65`) | 0.35f / 0.42f / 0.45f |
| HandUpMaxInitialVelocity | Float | Hand-up velocity cap | 1000f |
| AccessoryMinScale / AccessoryAlphaResponse / AccessoryAlphaResponseWithoutMelt | Float | Satellite collapse scale and alpha springs | 0.5f / 0.35f / 0.2f |
| AccessoryScaleDamping / AccessoryScaleResponse | Float | Satellite scale spring (stock `bounce 0.35`) | 0.65f / 0.55f |
| AccessoryTranslationDamping / AccessoryTranslationResponse / AccessoryTranslationMinVisibleChange | Float | Satellite translation spring | 0.65f / 0.55f / 0.001f |
| MenuMaskCenterColor / MenuMaskEdgeColor / MenuShadowColor | Color | Stock `mask_*` / `menu_shadow` colours | `#00FFFFFF` / `#33FFFFFF` / `#8A000000` |
| SelectedPressedAlpha / UnselectedPressedAlpha / UnselectedAlpha | Float | Content alpha multipliers when pressed / idle | 0.5f / 0.6f / 0.4f |
| IosBottomPadding | Dp | Bottom padding on iOS | 20.dp |
