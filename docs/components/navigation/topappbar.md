# TopAppBar

`TopAppBar` is a top application bar component in Rmuix, used to provide navigation, title, and action buttons at the top of the interface. It supports both large title and regular modes, as well as dynamic effects during scrolling.

This component is typically used in conjunction with the `Scaffold` component to maintain consistent layout and behavior across different pages in the application.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=topAppBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS app bar forms

ColorOS 17 ships two app bar layouts, and this component maps one form per call:

| Stock form | Layout / style | This library |
| :--- | :--- | :--- |
| **Static toolbar** (Settings default) | `COUIAppBarLayout` / `COUIDividerAppBarLayout` + `COUIToolbar` (`common_appbar_layout.xml`, `common_divider_appbar_layout.xml`): `toolbar_min_height` 52dp, title `textAppearanceSecondTitle` = `TextAppearance.COUI.Toolbar.SecondTitle` (18dp, `sans-serif-medium`, normal style), **no expanded title** | `TopAppBar(title = "...")` without `largeTitle`, and `SmallTopAppBar` |
| **Collapsable bar** | `COUICollapsableAppBarLayout` + `COUIAppbarTitleStyle`: collapsed `coui_appbar_title_collapsed_text_size` 24dp **bold**, expanded `coui_appbar_title_expanded_text_size` 32dp `sans-serif-semibold`, expanded height 110dp, top margin 54dp, side margins 24dp | `TopAppBar(title, largeTitle = title)` and `BlurTopAppBar` |

**BREAKING:** `largeTitle` used to default to `title`, so every bar always drew the collapsable expanded
title - a duplicated title and a 110dp-tall bar. It now defaults to `""`, the stock static `COUIToolbar`
form. Pass `largeTitle = title` (or any other string) to opt into the collapsable form. `SmallTopAppBar`'
s title also moved from 24sp to the stock static 18sp.

**Title metrics** (`topAppBarTitleMetrics`, tested by `TopAppBarDefaultsTest`):

| Form | Title |
| :--- | :--- |
| Static (no `largeTitle`) | 18sp Medium (`ToolbarTitleTextSize`) |
| Collapsable (`largeTitle` set) | 24sp Bold (`TitleTextSize`) |
| Either form with `subtitle` | 18sp Medium (`TitleTextSizeWithSubtitle`) |
| Expanded large title | 32sp SemiBold (`LargeTitleTextSize`) |

- `subtitle` keeps `coui_appbar_subtitle_text_size` = 14dp (= `body2`, via `TopAppBarDefaults.SubtitleTextSize`).
- `coui_appbar_title_collapsed_with_subtitle_text_size` (18dp) is declared but never read, so the with-subtitle 18sp is the library's choice.
**Static toolbar geometry (`COUIToolbar`):** the bar's own padding is `toolbar_normal_menu_padding_left` = 8dp
(each side 12dp of it comes from the navigation button's `coui_action_bar_navigation_padding_*_material`) and
`toolbar_normal_menu_padding_right` = 11dp; the title starts `coui_toolbar_gap_between_navigation_and_title` =
4dp after the navigation button (`supportTitleMarginStart` resolves to `coui_toolbar_support_margin_start` =
0dp). Its subtitle is 13sp with `couiColorSecondNeutral`. `coui_toolbar_subtitle_offset_top` (-2.5dp) is a
**dead token**: declared in `R.dimen` but `COUIToolbar` never reads it, so no offset is applied.

- The expanded large title does **not** move or fade: ColorOS sets the collapsing fade start to 1
  (`fixCollapsingTitleAlpha()`) and forces the text paint opaque, so the title position and opacity are
  fixed while the content scrolls. The library used to translate/fade it; that was removed in 2026-11-14
  and is pinned by `TopAppBarGeometryTest`.

**App-bar divider - off by default:** the settings app bar inflates `common_divider_appbar_layout.xml`, which
sets **`settings:hasDivider="false"`**, so ColorOS draws **no** hairline under the top bar.
`COUIDividerAppBarLayout.isDividerShown()` is `mHasDivider && !mBlurDividerSuppressed`, i.e. the divider is
suppressed whenever the bar's blur is active. What separates the bar is a **continuous gradient blur**
(`AppBarBlurHelper.setGradientBlurEnabled(true)`; `GRADIENT_DARK_ALPHA` 0.4, `NORMAL_BLUR_DARK_ALPHA` 0.6, and
a `GradientBlurConfig(startBlurRadius, endBlurRadius, gradientBlurSpeed, ...)`) over the bar's own
`coui_toolbar_bg` solid `?attr/couiColorBackgroundWithCard` (`#F0F1F2` light / `#000000` dark - `Theme.COUI` maps the attr to `coui_color_background_with_card`, the same value as the page background). `TopAppBar` / `BlurTopAppBar` therefore default `color` to `backgroundWithCard(...)`; `TopAppBar(showDivider = true)` still draws the
0.33dp `toolbar_divider_height` line (24dp -> 0dp inset, alpha ramping with the scroll distance over the bar
height) when a caller wants the legacy look.

**Collapse-time bottom padding:** a collapsable bar interpolates `coui_appbar_start_padding_bottom` (12dp,
`TopAppBarDefaults.StartPaddingBottom`) down to `coui_appbar_end_padding_bottom` (0dp,
`EndPaddingBottom`) as it collapses (`topAppBarCollapseBottomPaddingPx`, driven by the scroll state's
`collapsedFraction`). The stock class also translates the divider and the subtitle by
`-(start + (end - start) * fraction)` / `(end - start) * (1 - fraction)`; both are equivalent to keeping
those views in place while the bar's padding animates, which is how this component renders it - the divider
is placed above the padding instead of at the very bottom edge. Static (non-collapsing) bars keep the
library's fixed `SubtitleBottomPadding` (8dp) because the padding tokens only exist on the collapsable
layout.

`coui_appbar_title_icon_size` (16dp) is a **dead token**: it is declared in `R.java` but no decompiled class
reads it, so there is nothing to model. The 158dp "with search" variant is a style only -
`COUICollapsingToolbarLayoutStyle.WithSearch` (`coui_appbar_title_expanded_with_search_height` 158dp,
`expandedTitleMarginTop` 54dp) is declared in `styles.xml` but **no layout or code in this APK applies it**, so
porting it would be speculative.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TopAppBar
import io.github.ra1nify.rmuix.kmp.basic.BlurTopAppBar
import io.github.ra1nify.rmuix.kmp.basic.SmallTopAppBar
import io.github.ra1nify.rmuix.kmp.basic.RmuixScrollBehavior
import io.github.ra1nify.rmuix.kmp.basic.rememberTopAppBarState
```

## Basic Usage

### Static TopAppBar (stock default)

Leaving `largeTitle` empty renders the stock static `COUIToolbar` app bar: one 52dp row, 18sp title, no
expanded title and no collapse growth. This is what the Settings app uses.

```kotlin
TopAppBar(
    title = "Settings",
    navigationIcon = { IconButton(onClick = { /* back */ }) { Icon(RmuixIcons.Back, null) } },
    scrollBehavior = scrollBehavior, // optional: without a large title the bar stays put
)
```

### Small TopAppBar

```kotlin
Scaffold(
    topBar = {
        SmallTopAppBar(
            title = "Title",
            navigationIcon = {
                IconButton(onClick = { /* Handle click event */ }) {
                    Icon(RmuixIcons.Back, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { /* Handle click event */ }) {
                    Icon(RmuixIcons.More, contentDescription = "More")
                }
            }
        )
    }
)
```

### Large TopAppBar

```kotlin
Scaffold(
    topBar = {
        TopAppBar(
            title = "Title",
            largeTitle = "Large Title", // Opt into the collapsable form; omit it for the stock static bar
            navigationIcon = {
                IconButton(onClick = { /* Handle click event */ }) {
                    Icon(RmuixIcons.Back, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { /* Handle click event */ }) {
                    Icon(RmuixIcons.More, contentDescription = "More")
                }
            }
        )
    }
)
```

### Blurred Large Title (BlurTopAppBar)

`BlurTopAppBar` is `TopAppBar` with a large title that blurs as it collapses. The blur follows the
collapse fraction, so the title is sharp when the bar is expanded and reaches `largeTitleBlurRadius`
exactly when it has faded out.

```kotlin
Scaffold(
    topBar = {
        BlurTopAppBar(
            title = "Title",
            largeTitle = "Large Title",
            largeTitleBlurRadius = 12.dp,
            scrollBehavior = scrollBehavior,
        )
    }
)
```

`largeTitleBlurRadius` is also available on `TopAppBar` itself (default `0.dp`, i.e. no blur), so the
same effect can be opted into without switching components.

## Large TopAppBar Scroll Behavior (Using Scaffold)

TopAppBar supports changing its display state when content scrolls:

```kotlin
val scrollBehavior = RmuixScrollBehavior()

Scaffold(
    topBar = {
        TopAppBar(
            title = "Title",
            largeTitle = "Large Title", // Opt into the collapsable form; omit it for the stock static bar
            scrollBehavior = scrollBehavior
        )
    }
) { paddingValues ->
    // Content area needs to consider padding
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // If you want to add the overscroll effect, please add it before the scroll behavior
            .overScrollVertical()
            // Bind TopAppBar scroll behavior
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(top = paddingValues.calculateTopPadding())
    ) {
        // List content
    }
}
```

## Custom Styles

### Custom Colors

```kotlin
TopAppBar(
    title = "Title",
    color = RmuixTheme.colorScheme.primary,
    titleColor = RmuixTheme.colorScheme.onPrimary,
    largeTitleColor = RmuixTheme.colorScheme.onPrimary
)
```

### Custom Content Padding

```kotlin
TopAppBar(
    title = "Title",
    titlePadding = 32.dp
)
```

### Custom Icon Padding

```kotlin
TopAppBar(
    title = "Title",
    navigationIconPadding = 12.dp,
    actionIconPadding = 12.dp
)
```

## Properties

### TopAppBar Properties

| Property Name              | Type                            | Description                                    | Default Value                   | Required |
| -------------------------- | ------------------------------- | ---------------------------------------------- | ------------------------------- | -------- |
| title                      | String                          | Top bar title                                  | -                               | Yes      |
| modifier                   | Modifier                        | Modifier applied to the top bar                | Modifier                        | No       |
| color                      | Color                           | Top bar background color                       | `backgroundWithCard(...)`: `#F0F1F2` / `#000000` | No       |
| titleColor                 | Color                           | Color of the collapsed small title text        | RmuixTheme.colorScheme.onSurface | No       |
| largeTitle                 | String                          | Expanded large title; empty keeps the stock static toolbar form (collapsable form when set) | "" | No |
| largeTitleColor            | Color                           | Color of the expanded large title text         | RmuixTheme.colorScheme.onSurface | No       |
| subtitle                   | String                          | Subtitle text displayed below the title bar    | ""                              | No       |
| subtitleColor              | Color                           | Color of the subtitle text                     | RmuixTheme.colorScheme.onSurfaceVariantSummary | No       |
| navigationIcon             | @Composable () -> Unit          | Composable function for navigation icon area   | {}                              | No       |
| actions                    | @Composable RowScope.() -> Unit | Composable function for action buttons area    | {}                              | No       |
| scrollBehavior             | ScrollBehavior?                 | Controls top bar scroll behavior               | null                            | No       |
| defaultWindowInsetsPadding | Boolean                         | Whether to apply default window insets padding | true                            | No       |
| titlePadding          | Dp                              | Horizontal content padding                     | TopAppBarDefaults.TitlePadding | No       |
| navigationIconPadding      | Dp                              | Start padding of the navigation icon           | TopAppBarDefaults.NavigationIconPadding | No       |
| actionIconPadding          | Dp                              | End padding of the action icons                | TopAppBarDefaults.ActionIconPadding | No       |
| bottomContent              | @Composable () -> Unit          | Composable content displayed below the title bar area | {} | No |
| showDivider                | Boolean                         | Draw the stock 0.33dp hairline on the bar's bottom edge | **false** | No |
| dividerColor               | Color                           | Divider colour (`couiColorDivider`)            | TopAppBarDefaults.dividerColor() | No |
| largeTitleBlurRadius       | Dp                              | Blur of the large title as the bar collapses; `0.dp` keeps it sharp | 0.dp | No |

### SmallTopAppBar Properties

| Property Name              | Type                            | Description                                    | Default Value                   | Required |
| -------------------------- | ------------------------------- | ---------------------------------------------- | ------------------------------- | -------- |
| title                      | String                          | Top bar title                                  | -                               | Yes      |
| modifier                   | Modifier                        | Modifier applied to the top bar                | Modifier                        | No       |
| color                      | Color                           | Top bar background color                       | `backgroundWithCard(...)`: `#F0F1F2` / `#000000` | No       |
| titleColor                 | Color                           | Color of the title text                        | RmuixTheme.colorScheme.onSurface | No       |
| subtitle                   | String                          | Subtitle text displayed below the title bar    | ""                              | No       |
| subtitleColor              | Color                           | Color of the subtitle text                     | RmuixTheme.colorScheme.onSurfaceVariantSummary | No       |
| navigationIcon             | @Composable () -> Unit          | Composable function for navigation icon area   | {}                              | No       |
| actions                    | @Composable RowScope.() -> Unit | Composable function for action buttons area    | {}                              | No       |
| scrollBehavior             | ScrollBehavior?                 | Controls top bar scroll behavior               | null                            | No       |
| defaultWindowInsetsPadding | Boolean                         | Whether to apply default window insets padding | true                            | No       |
| titlePadding          | Dp                              | Horizontal content padding                     | TopAppBarDefaults.TitlePadding | No       |
| navigationIconPadding      | Dp                              | Start padding of the navigation icon           | TopAppBarDefaults.NavigationIconPadding | No       |
| actionIconPadding          | Dp                              | End padding of the action icons                | TopAppBarDefaults.ActionIconPadding | No       |
| bottomContent              | @Composable () -> Unit          | Composable content displayed below the title bar area | {} | No |

### TopAppBarDefaults Object

The TopAppBarDefaults object provides default values for TopAppBar and SmallTopAppBar components.

#### Constants

| Constant Name             | Type | Description                                        | Default Value |
| ------------------------- | ---- | -------------------------------------------------- | ------------- |
| TitlePadding    | Dp   | Gap between the navigation icon and the title (`coui_toolbar_gap_between_navigation_and_title`) | 4.dp |
| NavigationIconPadding      | Dp   | Toolbar start padding before the navigation icon (`toolbar_normal_menu_padding_left`) | 8.dp |
| ActionIconPadding          | Dp   | Toolbar end padding after the action icons (`toolbar_normal_menu_padding_right`) | 11.dp |
| CollapsedHeight            | Dp   | Collapsed height of the TopAppBar                 | 52.dp         |
| SmallTopAppBarCenterHeight | Dp   | Vertical center height for SmallTopAppBar layout  | 50.dp         |
| LargeTitleBottomPadding    | Dp   | Bottom padding below the large title when no subtitle is present | 4.dp |
| SubtitleBottomPadding      | Dp   | Bottom padding below the subtitle on **static** bars (collapsable bars use `StartPaddingBottom` / `EndPaddingBottom`) | 8.dp  |
| StartPaddingBottom         | Dp   | `coui_appbar_start_padding_bottom`: bottom padding of a collapsable bar while expanded | 12.dp |
| EndPaddingBottom           | Dp   | `coui_appbar_end_padding_bottom`: bottom padding once the bar is pinned | 0.dp |
| ToolbarTitleTextSize       | TextUnit | Static app bar title size (`TextAppearance.COUI.Toolbar.SecondTitle`, `textAppearanceSecondTitle`) | 18.sp |
| TitleTextSize              | TextUnit | Collapsed title size of the collapsable form (`coui_appbar_title_collapsed_text_size`) | 24.sp |
| TitleTextSizeWithSubtitle  | TextUnit | Collapsed title size once a subtitle is present (`coui_appbar_title_collapsed_with_subtitle_text_size`) | 18.sp |
| SubtitleTextSize           | TextUnit | Subtitle size of the collapsable form (`coui_appbar_subtitle_text_size`) | 14.sp |
| ToolbarSubtitleTextSize    | TextUnit | Subtitle size of the static toolbar form (`coui_toolbar_subtitle_text_size` via `TextAppearance.COUI.AppCompatSupport.Toolbar.SubTitle`) | 13.sp |
| LargeTitleTextSize         | TextUnit | Expanded large title size (`coui_appbar_title_expanded_text_size`) | 32.sp |
| DividerHeight              | Dp   | Divider hairline height (`toolbar_divider_height`) | 0.33.dp |
| DividerExpandedMargin      | Dp   | Divider side inset while expanded (`coui_appbar_divider_expanded_margin_horizontal`) | 24.dp |
| DividerCollapsedMargin     | Dp   | Divider side inset when collapsed (`coui_appbar_divider_collapsed_margin_horizontal`) | 0.dp |

### ScrollBehavior

RmuixScrollBehavior is a configuration object used to control the scroll behavior of the top bar.

#### rememberTopAppBarState

Used to create and remember TopAppBarState:

```kotlin
val scrollBehavior = RmuixScrollBehavior(
    state = rememberTopAppBarState(),
    snapAnimationSpec = spring(stiffness = 2500f),
    canScroll = { true }
)
```

| Parameter Name     | Type                        | Default Value              | Description                                      |
| ------------------ | --------------------------- | -------------------------- | ------------------------------------------------ |
| state              | TopAppBarState              | rememberTopAppBarState()   | State object controlling scroll state            |
| canScroll          | () -> Boolean               | { true }                   | Callback to control whether scrolling is allowed |
| snapAnimationSpec  | AnimationSpec\<Float>?      | spring(stiffness = 2500f)  | Defines snap animation after scrolling           |
| flingAnimationSpec | DecayAnimationSpec\<Float>? | rememberSplineBasedDecay() | Defines decay animation for fling                |

## Advanced Usage

### Handling Window Insets

```kotlin
TopAppBar(
    title = "Title",
    largeTitle = "Large Title",
    defaultWindowInsetsPadding = false // Handle window insets manually
)
```

### Custom Scroll Behavior Animation

```kotlin
var isScrollingEnabled by remember { mutableStateOf(true) }
val scrollBehavior = RmuixScrollBehavior(
    snapAnimationSpec = tween(durationMillis = 100),
    flingAnimationSpec = rememberSplineBasedDecay(),
    canScroll = { isScrollingEnabled } // Can dynamically control whether scrolling is allowed
)

TopAppBar(
    title = "Title",
    largeTitle = "Large Title",
    scrollBehavior = scrollBehavior
)
```

### Combining Large and Small Titles

```kotlin
var useSmallTopBar by remember { mutableStateOf(false) }

Box(modifier = Modifier.fillMaxSize()) {
    if (useSmallTopBar) {
        SmallTopAppBar(
            title = "Compact Mode",
            navigationIcon = {
                IconButton(onClick = { useSmallTopBar = false }) {
                    Icon(
                        imageVector = RmuixIcons.Back,
                        contentDescription = "Switch to Large Title",
                        tint = RmuixTheme.colorScheme.onBackground
                    )
                }
            }
        )
    } else {
        TopAppBar(
            title = "Title",
            largeTitle = "Expanded Mode",
            navigationIcon = {
                IconButton(onClick = { useSmallTopBar = true }) {
                    Icon(
                        imageVector = RmuixIcons.Back,
                        contentDescription = "Switch to Small Title",
                        tint = RmuixTheme.colorScheme.onBackground
                    )
                }
            }
        )
    }
}
```
