# SearchBar

`SearchBar` is a component in Rmuix used for user search input. It provides an intuitive and
easy-to-use search interface with support for expanded/collapsed state switching and search
suggestions display.

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=searchBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass reads the `COUISearchViewAnimate` layout / style, the `coui_searchview_corner_rect_bg` drawable and the `coui_search*` dimens of the Settings APK.

- The field is `coui_search_bar_normal_background_height` = **40dp** tall (the library used 45dp).
- The field background is a rounded rectangle, not a capsule: `coui_searchview_corner_rect_bg` uses `coui_search_view_corner` = **18dp**. `InputField` now uses `RoundedCornerShape(18.dp)` instead of `CircleShape`.
- Input text is `coui_search_view_input_text_size` = **15sp** (the library used 17sp) and the placeholder is `coui_search_view_text_hint_size` = **12sp**; the component now applies the hint size while the placeholder is visible.
- The drawn magnifier is the `coui_search_view_icon_normal` vector at **20dp**, not the `coui_search_view_icon_size` 24dp (that token is only `mIconMaxWidth` / `mIconMaxHeight`, the cap for custom icons). It is centred in the `coui_search_bar_inner_search_icon_size` **36dp** box: `coui_search_bar_inner_search_icon_start_gap` 4dp + `(36 - 20) / 2` = 8dp puts its visible edge 12dp from the field. `SearchBarDefaults.IconStartMargin` keeps that 12dp, and the bar keeps `coui_search_view_margin_left/right` = **16dp** on both sides.

**BREAKING:** the field defaults change from 45dp / capsule / 17sp to 40dp / 18dp corner / 15sp, and the placeholder renders at 12sp.

The field fill is **not** an opaque surface role: `COUISearchBar` reads `mNormalBackgroundColor` from `coui_search_view_selector_color_normal` = `#14000000` (light) / `#1AFFFFFF` (dark), with a pressed `coui_search_view_selector_color_pressed` = `#1E000000` / `#33FFFFFF`. The component default was the theme `surfaceContainerHigh`; it now resolves `searchBarFieldColor(dark)` (and `searchBarFieldPressedColor(dark)` is available for a pressed layer). The `couiSearchviewHintBgColorNormal` / `…Disabled` attributes that the drawable mentions stay unused by this APK.

### Functional button

The stock bar's trailing text button (`SearchBarFunctionButton`) is provided as a composable: a **40dp**
capsule (`coui_search_bar_normal_background_height`) in `?attr/couiColorBar` (`#FAFAFA` / `#1E1E1E`),
**16dp** side padding (`coui_search_bar_functional_button_text_padding_horizontal`), a **152dp** width cap
(`coui_search_bar_functional_button_max_width`), the **14sp** sans-serif-medium `couiTextAppearanceButton`
label and `coui_search_view_cancel` ("Cancel") as the stock default string. It scales on press through
`COUIViewTouchAnimator`, i.e. the `PressCalc.calculateUniformScaleBreakdown()` target for the measured button
size (e.g. a 40dp-tall button sits at ~0.89) with the `PressParamsExtKt` jelly springs (press response 0.25 /
bounce 0, release response 0.5 / bounce 0.6)
(`COUIViewTouchAnimator`). Pass it through `SearchBar`'s `outsideEndAction` slot:

```kotlin
SearchBar(
    inputField = { InputField(query, onQueryChange, onSearch, expanded, onExpandedChange) },
    onExpandedChange = { expanded = it },
    expanded = expanded,
    outsideEndAction = {
        SearchBarFunctionButton(text = "Cancel", onClick = { expanded = false })
    },
) { /* results */ }
```

**Still under review:** the surrounding `searchBackground` (`coui_search_view_search_background`) is fully
transparent and the animated expand/collapse choreography (`COUISearchViewAnimate`, 100KB) is not ported.
The `coui_search_bar_functional_button_divider_width` / `_height` 1dp x 16dp divider is intentionally
caller-composed (the stock bar draws it inside `SearchBarBackgroundView`); use the component's `length`
parameter: `VerticalDivider(thickness = 1.dp, length = 16.dp)`, as in [Functional button geometry](#functional-button-geometry).

**Clear-button press feedback:** `SearchBarIconButton` paints a full-circle `COUIMaskEffectDrawable`
in `?attr/couiColorPress` (`#1F000000` / `#33FFFFFF`) instead of a ripple; the clear button now does the same.

### Field height vs wrapper height (2026-11-12)

`homepage_toolbar.xml` sizes the whole animated search view with `coui_search_view_height` = **52dp** (`layout_height` and `minHeight`), while the field itself is `coui_search_bar_normal_background_height` = `coui_search_view_wrapper_height` = **40dp**; the 12dp difference is the vertical breathing room around the field. The library only models the field, so 40dp is the correct number to expose — the 52dp belongs to the container that `SearchBar` does not replace.

Other height-ish tokens in the same family, none of which the library uses: `coui_search_view_animate_height` 50dp (the expanded active state), `coui_search_view_min_height` 33dp, `coui_search_view_collapsed_min_height` 1px.

### Functional button geometry

`SearchBarFunctionButton` implements the button itself (`coui_search_bar_normal_background_height` 40dp,
`coui_search_bar_functional_button_max_width` 152dp, `coui_search_bar_functional_button_text_padding_horizontal`
16dp, `couiTextAppearanceButton`). The spacing **around** it stays with the caller, because `SearchBar`'s
`outsideEndAction` slot is generic:

| Token | Value | Notes |
| --- | --- | --- |
| `coui_search_bar_functional_button_start_gap` | 12dp | gap between the field and the button |
| `coui_search_bar_functional_button_end_gap_compat` / `_medium` / `_expanded` | 4dp / 12dp / 28dp | responsive end gap |
| `coui_search_bar_functional_button_offset_distance` | 60dp | slide-in offset |
| `coui_search_bar_functional_button_divider_width` / `_height` | 1dp / 16dp | optional divider |
| `coui_search_bar_edittext_end_gap` | 4dp | gap between the text and the field edge |
| `coui_search_bar_animation_translate_extra` | 5dp | extra translate during the expand animation |

A caller that wants the stock composition writes `Row { Spacer(12.dp); VerticalDivider(thickness = 1.dp, length = 16.dp);
SearchBarFunctionButton(...) }` inside `outsideEndAction`.

### Clear button geometry (2026-11-12)

The trailing clear button is not a tinted glyph. `COUISearchBar` inflates `ic_edit_text_delete_search_view` (36dp viewport) through `configImageViewDrawable`, so the drawn artwork is the vector itself:

- an **18dp** circle (`M18,18m-9,0a9,9 …`) filled with `?attr/couiColorHintNeutral` (`#42000000` / `#4DFFFFFF`);
- a **solid white filled** cross (`#ffffff`) spanning **8.204dp** (13.898..22.102 in the 36dp viewport) with **1.6dp** thick arms whose ends are cut at 45 degrees - the library used to draw two square-capped lines 8.2dp long and 1.13dp thick, which is thinner and has square ends;
- the whole **36dp** box is the touch target.

`InputField` used to draw `RmuixIcons.Basic.SearchCleanup` tinted `onSurfaceContainerHighest`; it noww draws the circle and the cross with `Canvas` from `SearchBarStockClearIconSize` / `SearchBarStockClearGlyphLength` / `SearchBarStockClearGlyphThickness` and `searchBarClearIconBackground(dark)`.

**BREAKING:** the default trailing icon becomes the stock 18dp circle with a white cross inside a 36dp touch box, its fade uses the selector's `enterFadeDuration` 180ms / `exitFadeDuration` 360ms, and the magnifier now tints `couiColorSecondNeutral` (`#8A000000` / `#8AFFFFFF`) instead of `onSurfaceContainerHigh`.

The leading magnifier tint comes from `coui_search_view_icon_normal`'s `?attr/couiColorSecondNeutral` = `coui_color_secondary_neutral` (`#8A000000` / `#8AFFFFFF`), so it resolves through `searchBarIconColor(dark)` rather than a surface content role.
## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.SearchBar
import io.github.ra1nify.rmuix.kmp.basic.InputField
```

## Basic Usage

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }

SearchBar(
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { /* Handle search action */ },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it }
) {
    // Search results content
    Column {
        // Add search suggestions or results here
    }
}
```

## Properties

### SearchBar Properties

| Property Name    | Type                               | Description                                     | Default Value       | Required |
|------------------|------------------------------------|-------------------------------------------------|---------------------|----------|
| inputField       | @Composable () -> Unit             | Search input field component                    | -                   | Yes      |
| onExpandedChange | (Boolean) -> Unit                  | Callback when expanded state changes            | -                   | Yes      |
| modifier         | Modifier                           | Modifier applied to the search bar              | Modifier            | No       |
| insideMargin     | DpSize                             | Internal padding                                | SearchBarDefaults.InsideMargin | No       |
| expanded         | Boolean                            | Whether to show search results                  | false               | No       |
| outsideEndAction | @Composable (() -> Unit)?          | Action component shown on the end when expanded | null                | No       |
| content          | @Composable ColumnScope.() -> Unit | Content shown when expanded                     | -                   | Yes      |

### InputField Properties

| Property Name     | Type                      | Description                          | Default Value        | Required |
|-------------------|---------------------------|--------------------------------------|----------------------|----------|
| query             | String                    | Text content in search field         | -                    | Yes      |
| onQueryChange     | (String) -> Unit          | Callback when text content changes   | -                    | Yes      |
| onSearch          | (String) -> Unit          | Callback when search is executed     | -                    | Yes      |
| expanded          | Boolean                   | Whether in expanded state            | -                    | Yes      |
| onExpandedChange  | (Boolean) -> Unit         | Callback when expanded state changes | -                    | Yes      |
| modifier          | Modifier                  | Modifier applied to the input field  | Modifier             | No       |
| label             | String                    | Placeholder text when empty          | ""                   | No       |
| enabled           | Boolean                   | Whether search field is enabled      | true                 | No       |
| textStyle         | TextStyle?                | Style of text in search box          | null                 | No       |
| color             | Color                     | Background color of the capsule; pass Color.Transparent when drawing your own background (e.g. a backdrop blur) | `searchBarFieldColor(dark)`: `#14000000` light / `#1AFFFFFF` dark | No       |
| leadingIcon       | @Composable (() -> Unit)? | Leading icon                         | default search icon  | No       |
| trailingIcon      | @Composable (() -> Unit)? | Trailing icon                        | default clear button | No       |
| interactionSource | MutableInteractionSource? | Interaction source                   | null                 | No       |

### SearchBarDefaults Object

The SearchBarDefaults object provides default values for SearchBar and InputField components.

#### Constants

| Constant Name         | Type     | Description                              | Default Value      |
| --------------------- | -------- | ---------------------------------------- | ------------------ |
| InsideMargin          | DpSize   | Internal padding of the SearchBar        | DpSize(12.dp, 0.dp)|
| InputFieldMinHeight   | Dp       | Minimum height of the InputField (`coui_search_bar_normal_background_height`) | 40.dp |
| InputFieldFontSize    | TextUnit | Font size for the InputField label (`coui_search_view_input_text_size`) | 15.sp |
| InputFieldHintFontSize | TextUnit | Placeholder font size (`coui_search_view_text_hint_size`) | 12.sp |
| InputFieldCornerRadius | Dp      | InputField corner radius (`coui_search_view_corner`) | 18.dp |
| IconSize              | Dp       | Search icon size (the 20dp `coui_search_view_icon_normal` vector; `coui_search_view_icon_size` 24dp only caps custom icons) | 20.dp |
| IconStartMargin       | Dp       | Start margin of the search icon (`coui_search_view_search_icon_margin`) | 12.dp |
| SideMargin            | Dp       | Side margin of the bar (`coui_search_view_margin_left/right`) | 16.dp |
| LeadingIconStartPadding | Dp     | Visible start margin of the default leading icon; the layout derives it from the stock 4dp box gap + (36 - 20) / 2 | 12.dp |
| LeadingIconEndPadding | Dp       | Retained for source compatibility; the stock 36dp icon box replaces it, so it no longer affects the layout | 8.dp |
| TrailingIconStartPadding | Dp    | Start padding for default trailing icon  | 8.dp               |
| TrailingIconEndPadding | Dp      | End padding for default trailing icon    | 16.dp              |

## Advanced Usage

### SearchBar with Icons

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }

SearchBar(
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { /* Handle search action */ },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            leadingIcon = {
                Icon(
                    modifier = Modifier.padding(start = 12.dp, end = 8.dp),
                    imageVector = RmuixIcons.Search,
                    contentDescription = "Search"
                )
            }
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it }
) {
    // Search results content
}
```

### SearchBar with Suggestions

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }
val suggestions = listOf("Suggestion 1", "Suggestion 2", "Suggestion 3")

SearchBar(
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { /* Handle search action */ },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it }
) {
    Column {
        suggestions.forEach { suggestion ->
            BasicComponent(
                title = suggestion,
                onClick = {
                    searchText = suggestion
                    expanded = false
                }
            )
        }
    }
}
```

### SearchBar with Cancel Button

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }

SearchBar(
    modifier = Modifier.padding(horizontal = 12.dp),
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { expanded = false },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            label = "Search"
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it },
    outsideEndAction = {
        Text(
            modifier = Modifier
                .padding(start = 12.dp)
                .clickable(
                    interactionSource = null,
                    indication = null
                ) {
                    expanded = false
                    searchText = ""
                },
            text = "Cancel",
            color = RmuixTheme.colorScheme.primary
        )
    }
) {
    // Search results content
}
```

## Expand / collapse timing

`COUISearchViewAnimate` drives the transition with four `PathInterpolator`s and a set of `DEFAULT_*_DURATION`
constants. `SearchBar` now uses them instead of the framework defaults:

| constant | value | used by |
| :--- | ---: | :--- |
| `DEFAULT_SEARCH_VIEW_OFFSET_CHANGE_DURATION` | 450ms | trailing action slide |
| `DEFAULT_SEARCH_VIEW_SCALE_CHANGE_DURATION` | 450ms | Already covered: despite the name, this animator never touches `scaleX`/`scaleY` - it writes `marginEnd` from `shrinkWidth` to `originWidth` on `COUIMoveEaseInterpolator`, i.e. a width animation at the same 450ms `slideSpec()` uses |
| `DEFAULT_BUTTON_MOVE_DURATION` | 450ms | button move |
| `DEFAULT_BUTTON_ALPHA_CHANGE_DURATION` | 350ms | button alpha |
| `NON_INSTANT_SEARCH_BUTTON_ALPHA_CHANGE_DURATION` | 100ms | non-instant button alpha |
| `DEFAULT_FADE_DURATION` | 150ms | results container fade |
| `DEFAULT_PRESS_FEEDBACK_DURATION` | 150ms | press feedback |

Two curves, both plain `PathInterpolator`s:

| class | control points | Compose equivalent |
| :--- | :--- | :--- |
| `COUIMoveEaseInterpolator` | `cubic-bezier(0.3, 0.0, 0.1, 1.0)` | `SearchBarDefaults.MoveEase` |
| `COUIEaseInterpolator` | `cubic-bezier(0.33, 0.0, 0.67, 1.0)` | `SearchBarDefaults.Ease` |

`SearchBarDefaults.slideSpec()` (450ms, MoveEase) drives the trailing action's grow / shrink / slide, and
`SearchBarDefaults.fadeSpec()` (150ms) drives the results container.
