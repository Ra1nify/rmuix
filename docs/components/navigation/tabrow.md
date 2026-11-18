# TabRow

`TabRow` is a navigation component in Rmuix used to create horizontally scrollable tabs. It is the port of the stock `COUITabLayout`, suitable for content categorization and navigation.

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=tabRow" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass reads `COUITabLayout` / `COUITabLayoutBaseStyle`, the `coui_tab_default_color` selector and the `tablayout_*` dimens of the Settings APK.

- Tab text follows `couiTabTextColor` → `coui_tab_default_color`: selected and pressed both resolve to `couiColorPrimaryNeutral` (`#E6000000` / `#E6FFFFFF`), idle to `couiColorSecondNeutral` (`#8A000000` / `#8AFFFFFF`). The library used `onBackground` / `onSurfaceVariantSummary` before.
- A pressed tab paints `couiColorPrimaryNeutral` whether or not it is selected (`coui_tab_default_color`), so pressing an idle tab darkens it to the selected colour.
- The selected indicator is `couiTabIndicatorColor` = `couiColorPrimaryNeutral` (`#E6000000` / `#E6FFFFFF`), 2dp tall (`tablayout_selected_indicator_height`). `COUISlidingTabStrip.updateIndicatorPosition()` spans the selected label (its `TextView`, indicator padding 0) and `setIndicatorPosition()` widens it to at least `DEFAULT_MIN_INDICATOR` = **32dp**, centred over the label.
- A second, static 2dp track runs under the whole row (`couiTabIndicatorBackgroundColor` = `tablayout_indicator_background` = `#0F000000` / `#19FFFFFF`, same 24dp inset and 2dp height). It was missing; it is now drawn behind the moving indicator.
- The row background is `coui_tablayout_bg` → `couiColorBackgroundWithCard` (`#F0F1F2` / `#000000`), not the theme's `surface` role.
- Metrics: 40dp height (`tablayout_large_layout_height`), responsive tab spacing (**20dp** `coui_tab_layout_small_tab_spacing`, **24dp** `_medium_tab_spacing` from the 600dp medium window class up), 72dp min / 128dp max width, 58dp fade edge, 16sp text (`tablayout_large_text_size`), and the vibrator flag (`couiTabEnableVibrator`) is honoured through `HapticFeedbackType.SegmentTick`. **Corrected:** the selected label uses `sans-serif-medium` (`COUITabView.setSelected()` swaps `mNormalTypeface` for `mSelectedTypeface`), so selection does change the weight; the earlier "no weight change / 16dp" note was wrong.

**BREAKING:** default tab text and indicator colours change, the row background becomes `couiColorBackgroundWithCard`, and the static indicator track is drawn. `TabRowColors` gains `indicatorTrackColor` (`Color.Unspecified` falls back to the stock hairline).

**Press feedback:** `COUITabView` wraps each tab in an 8dp-radius `COUIMaskEffectDrawable`
(`setMaskRect(mTabRect, dpToPx(8), dpToPx(8))`, `setTouched(...)`), so pressing a tab paints a mask in
`?attr/couiColorPress` - the label colour does not change. The component now draws that mask for press, hover
(`?attr/couiColorHover`) and focus (`?attr/couiColorFocus`), each faded in over the 0.3s state-effect spring.
A shared row-level `interactionSource` still lights every tab at once; that is a caller choice.

**Indicator geometry:** `couiTabIndicatorBackgroundPaddingLeft` / `Right` are both `tablayout_background_padding` = **24dp**, and they apply only to the static `couiTabIndicatorBackgroundColor` track (`TabRowDefaults.IndicatorInset`).
The moving underline itself has indicator padding 0 and spans the selected label, min **32dp** (`TabRowDefaults.MinIndicatorWidth`) - not the tab width minus 24dp.
`TabRowDefaults.IndicatorInset` - the static `couiTabIndicatorBackgroundColor` track and the moving indicator
are both inset 24dp on each side of the tab.

**Ported elsewhere:** the `COUIChipTab*` family is [`ChipTabRow`](./chiptabrow) (spec in
`docs/dev/coloros-chip-tab.md`).

**The small / middle tab style:** `COUISmallTabLayoutStyle` keeps the 40dp height and the 16sp label
(`tablayout_large_text_size`; `COUIMiddleTabLayoutStyle` only swaps in `COUIMiddleTabViewStyle`, which is the
same 16sp - the earlier note claiming 14sp and a 1dp indicator was wrong). What actually differs:

- `couiTabIndicatorBackgroundHeight` = 0dp (no static track)
- `couiTabIndicatorHeight` = 2dp (unchanged)
- `tablayout_small_min_divider` / `tablayout_large_min_divider` only pin the spacing to 20dp / 16dp when a style sets them; the base style leaves `couiTabMinDivider` unset, so `parseMinDivider()` uses the responsive 20dp / 24dp pair above.

That variant is expressible without a new parameter:

```kotlin
TabRow(
    tabs = listOf("One", "Two", "Three"),
    selectedTabIndex = selected,
    onTabSelected = { selected = it },
    itemSpacing = 20.dp,
    colors = TabRowDefaults.tabRowColors(indicatorTrackColor = Color.Transparent),
)
```

**Tab search bar:** the earlier note claimed `COUITabLayout` can host a search view that was "not ported". Checked on
2026-11-14: `COUITabLayout.java` contains no search-related code at all, and no layout in this APK pairs a `TabLayout` with a
search view - there is nothing to port. Removed from the review list.
## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TabRow
```

## Basic Usage

### Standard Style

```kotlin
val tabs = listOf("Recommended", "Following", "Popular", "Featured")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRow(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it }
)
```

### Preserve Scroll Position

```kotlin
val tabs = listOf("Tab 1", "Tab 2", "Tab 3", "Tab 4", "Tab 5")
var selectedTabIndex by remember { mutableStateOf(3) }
val tabListState = rememberLazyListState()

TabRow(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    listState = tabListState,
)
```

## Properties

### TabRow Properties

| Property Name     | Type                      | Description                      | Default Value                     | Required |
| ----------------- | ------------------------- | -------------------------------- | --------------------------------- | -------- |
| tabs              | List\<String>             | List of tab texts                | -                                 | Yes      |
| selectedTabIndex  | Int                       | Current selected tab index       | -                                 | Yes      |
| onTabSelected     | (Int) -> Unit             | Callback when tab is selected    | -                                 | Yes      |
| modifier          | Modifier                  | Modifier for the tab row         | Modifier                          | No       |
| colors            | TabRowColors              | Color configuration              | TabRowDefaults.tabRowColors()     | No       |
| minWidth          | Dp                        | Minimum width of each tab        | TabRowDefaults.TabRowMinWidth     | No       |
| maxWidth          | Dp                        | Maximum width of each tab        | TabRowDefaults.TabRowMaxWidth     | No       |
| height            | Dp                        | Height of the tab row            | TabRowDefaults.TabRowHeight       | No       |
| cornerRadius      | Dp                        | Corner radius of tabs            | TabRowDefaults.TabRowCornerRadius | No       |
| itemSpacing       | Dp                        | Spacing between tabs             | TabRowDefaults.ItemSpacing        | No       |
| contentAlignment  | Alignment                 | Alignment of tab content         | Alignment.Center                  | No       |
| listState         | LazyListState?            | External scroll state for tabs   | null                              | No       |
| interactionSource | MutableInteractionSource? | Interaction source for tab items | null                              | No       |
| indication        | Indication?               | Indication for tab items         | null                              | No       |
| enabled           | Boolean                   | Disables the whole row (`COUITabLayout.setEnabled(false)`: indicator turns `#FFD9D9D9`, text falls to `couiColorDisabledNeutral` `#42000000`/`#4DFFFFFF`, no tab responds) | true                              | No       |

### TabRowDefaults Object

The TabRowDefaults object provides default configurations for the TabRow component.

#### Constants

| Constant Name                 | Type | Value | Description                                  |
| ----------------------------- | ---- | ----- | -------------------------------------------- |
| TabRowHeight                  | Dp   | 40.dp  | Default height of tab row for standard style |
| TabRowCornerRadius            | Dp   | 0.dp   | Corner radius for standard style (ColorOS tabs are rectangles) |
| TabRowMinWidth                | Dp   | 72.dp  | Min width of tabs for standard style         |
| TabRowMaxWidth                | Dp   | 128.dp | Max width of tabs for standard style         |
| IndicatorHeight               | Dp   | 2.dp   | Underline thickness                          |
| IndicatorInset                | Dp   | 24.dp  | Inset of the static indicator track from each row edge |
| MinIndicatorWidth             | Dp   | 32.dp  | Minimum width of the moving underline (`DEFAULT_MIN_INDICATOR`) |
| ItemSpacing                   | Dp   | 20.dp  | Gap between tabs below the medium window class |
| MediumItemSpacing             | Dp   | 24.dp  | Gap between tabs from 600dp up (`coui_tab_layout_medium_tab_spacing`) |
| FadeEdgeLength                | Dp   | 58.dp  | Width of the horizontal fading edge          |

#### Methods

| Method Name    | Type         | Description                        |
| -------------- | ------------ | ---------------------------------- |
| tabRowColors() | TabRowColors | Create the default colour configuration (idle/selected text, indicator, indicator track) |

### TabRowColors Class

| Property Name           | Type  | Description                      |
| ----------------------- | ----- | -------------------------------- |
| backgroundColor         | Color | Default background color of tabs |
| contentColor            | Color | Default content color of tabs    |
| selectedBackgroundColor | Color | Background color of selected tab |
| selectedContentColor    | Color | Content color of selected tab    |
| indicatorColor          | Color | Selected indicator color          |
| indicatorTrackColor     | Color | Static 2dp track under the row (`Color.Unspecified` falls back to the stock hairline) |

## Advanced Usage

### Custom Colors

```kotlin
val tabs = listOf("Latest", "Popular", "Following")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRow(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    colors = TabRowDefaults.tabRowColors(
        backgroundColor = Color.LightGray.copy(alpha = 0.5f),
        contentColor = Color.Gray,
        selectedBackgroundColor = RmuixTheme.colorScheme.primary,
        selectedContentColor = Color.White
    )
)
```

### Using with Pager

```kotlin
val tabs = listOf("Page 1", "Page 2", "Page 3")
val pagerState = rememberPagerState { tabs.size }
val scope = rememberCoroutineScope()

Surface {
    Column {
        TabRow(
            tabs = tabs,
            selectedTabIndex = pagerState.currentPage,
            onTabSelected = { index ->
                scope.launch { pagerState.animateScrollToPage(index) }
            }
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Page Content ${page + 1}")
            }
        }
    }
}
```
