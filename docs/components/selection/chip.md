#! /usr/bin/env markdown

# Chip

`Chip` is a capsule label in Rmuix that mirrors ColorOS `COUIChip`: a compact, single-line tag that can be
**checked** to show an active filter or selection. It is the plain chip; the tab-shaped family lives in
[`ChipTabRow`](../navigation/chiptabrow).

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=chip" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-14)

This component ports `Widget.COUI.Chip` from the Settings APK's `styles.xml` plus the
`COUIChip` / `COUIChipDrawable` classes. Verified numbers (`docs/dev/coloros-chip.md`):

- `coui_chip_default_min_height` = 28dp
- `coui_chip_selection_style_height` = 32dp
- `coui_chip_default_min_width` = 52dp
- `coui_chip_default_max_width` = 300dp
- `coui_chip_default_max_text_width` = 200dp
- `coui_chip_selection_style_chip_horizontal_padding` = 12dp
- `coui_chip_group_default_spacing_horizontal` = 8dp
- `coui_chip_group_default_spacing_vertical` = 8dp

The base style is a capsule (`chipCornerRadius = -1dp`) with a 12sp `?attr/couiTextBodyXS` label; the
selection style raises the height to 32dp and the label to 14sp (`?attr/couiTextBodyM`). The unchecked
container is `?attr/couiColorContainer8` (`#14000000` / `#1AFFFFFF`), the checked one is
`?attr/couiColorPrimary`, and the checked label is `?attr/couiColorLabelOnColor` (`#FFFFFF`).

The `.Suggestion` style also sets `closeIcon` (`@drawable/ic_chip_close`): a **12dp** glyph
(`coui_chip_suggest_style_close_icon_size`) with **8dp** start/end paddings
(`coui_chip_suggest_style_close_icon_start_padding` / `_end_padding`) and a **24dp** touch bounds
(`coui_chip_suggest_style_close_icon_touch_bounds_size`), tinted `?attr/couiColorSecondNeutral`
(`#8A000000` / `#8AFFFFFF`). The stock style shows it by default; here it is opt-in through `onClose`, so a
chip never shows a button without an action.

### Leading icon (2026-11-14)

The leading icon slot is modelled through `icon`. The geometry comes straight from `COUIChipDrawable`:
`calculateChipIconBounds()` puts the icon at `chipStartPadding + chipIconStartPadding` from the start edge,
sizes it `chipIconSize` and separates it from the label by `chipIconEndPadding`.

| style | `chipIconStartPadding` | `chipIconSize` | `chipIconEndPadding` | icon inset from the chip edge |
| :--- | ---: | ---: | ---: | ---: |
| `Suggestion` | 12dp | 14dp | 4dp | 24dp |
| `Selection` | 0dp | 16dp | 4dp | 12dp |

That also settles the older "24dp inset vs 28dp chip" question: the collapsing chip is not the `.Suggestion`
style but `Widget.COUI.Chip.Suggestion.Collapsable` (`chipStartPadding` 0dp, `chipIconStartPadding` 6dp,
`chipIconSize` 16dp, `chipIconEndPadding` 6dp = exactly 28dp).

**Corrections:** `Widget.COUI.Chip.Choice` and `Widget.COUI.Chip.Record` are **empty aliases** of
`.Selection` / `.Suggestion` (no extra items in `styles.xml`), so `Chip(style = ...)` already covers them;
the `icon` slot models `chipIconSize` / `chipIconStartPadding` / `chipIconEndPadding` and tints the slot via
`LocalContentColor` (`chipIconApplyTint`). `Widget.COUI.Chip.ChipTab` + `layout_chip_tab.xml` /
`COUIChipTabGroup` exist in the APK, but **no Settings code inflates or instantiates them** (only `R.java`
references), and `COUIChipGroupEditText` (input chips) has no Settings call site either - neither is modelled.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Chip
import io.github.ra1nify.rmuix.kmp.basic.ChipDefaults
import io.github.ra1nify.rmuix.kmp.basic.ChipStyle
```

## Basic Usage

```kotlin
var picked by remember { mutableStateOf(false) }

Chip(
    onClick = { picked = !picked },
    label = "Suggested",
    selected = picked,
)
```

Place several chips in a row using the stock group spacing:

```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing)) {
    Chip(onClick = {}, label = "All", selected = true)
    Chip(onClick = {}, label = "Photos")
    Chip(onClick = {}, label = "Videos")
}
```

## Removable chip

Pass `onClose` to show the stock `ic_chip_close` glyph at the end:

```kotlin
Chip(
    onClick = {},
    label = "Wi-Fi",
    onClose = { /* remove this filter */ },
)
```

## Selection style

```kotlin
Chip(
    onClick = {},
    label = "Selection",
    selected = true,
    style = ChipStyle.Selection,
)
```

## Semantics

`Widget.COUI.Chip` sets `android:checkable="false"` and `.Selection` flips it to `true`, so the accessibility
role follows the style: `ChipStyle.Suggestion` is a plain button (`Role.Button`), while
`ChipStyle.Selection` is selectable (`Role.Checkbox`, `selected` exposed as the checked state).

## Chip

```kotlin
@Composable
fun Chip(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    style: ChipStyle = ChipStyle.Suggestion,
    colors: ChipColors = ChipDefaults.chipColors(),
)
```

| Parameter Name | Type      | Description                                | Default Value           | Required |
| -------------- | --------- | ------------------------------------------ | ----------------------- | -------- |
| onClick        | () -> Unit | Invoked when the chip is clicked          | -                       | Yes      |
| label          | String    | The chip label                             | -                       | Yes      |
| modifier       | Modifier  | Modifier applied to the chip               | Modifier                | No       |
| selected       | Boolean   | Whether the chip is checked                | false                   | No       |
| enabled        | Boolean   | Whether the chip accepts input             | true                    | No       |
| style          | ChipStyle | Height and label size                      | ChipStyle.Suggestion    | No       |
| colors         | ChipColors | Container and label colours               | ChipDefaults.chipColors() | No     |
| onClose        | (() -> Unit)? | Shows the stock close glyph when non-null | null                     | No     |
| icon | (@Composable () -> Unit)? | Optional leading icon, tinted with the chip content colour | null | No |
| borderWidth     | Dp      | Width of the opt-in outline (only drawn when a border colour is specified)       | ChipDefaults.BorderWidth (1dp) | No |

## ChipDefaults

| Property Name       | Type   | Description                                   | Default Value |
| ------------------- | ------ | --------------------------------------------- | ------------- |
| MinHeight           | Dp     | Suggestion chip height                        | 28.dp         |
| BorderWidth         | Dp     | Opt-in outline width (not a COUI token; the stock chip has no border) | 1.dp |
| SelectionHeight     | Dp     | Selection chip height                         | 32.dp         |
| MinWidth            | Dp     | Minimum chip width                            | 52.dp         |
| MaxWidth            | Dp     | Maximum chip width                            | 300.dp        |
| MaxTextWidth        | Dp     | Label width cap before ellipsis               | 200.dp        |
| HorizontalPadding   | Dp     | Label side padding                            | 12.dp         |
| GroupSpacing        | Dp     | Spacing between chips in a row                | 8.dp          |
| SuggestionTextSize  | TextUnit | Suggestion label size                       | 12.sp         |
| CloseIconSize       | Dp       | Close glyph size                            | 12.dp         |
| CloseIconStartPadding | Dp     | Gap between label and close glyph           | 8.dp          |
| CloseIconEndPadding | Dp       | Gap after the close glyph                   | 8.dp          |
| CloseIconTouchBounds | Dp      | Close glyph hit area                        | 24.dp         |
| SuggestionIconSize  | Dp       | `coui_chip_suggest_style_chip_icon_size`            | 14.dp         |
| SelectionIconSize   | Dp       | `coui_chip_selection_style_chip_icon_size`           | 16.dp         |
| IconEndPadding      | Dp       | `coui_chip_*_style_chip_icon_end_padding`            | 4.dp          |
| SuggestionIconStartPadding | Dp | `coui_chip_suggest_style_chip_icon_start_padding`   | 12.dp         |
| SelectionIconStartPadding  | Dp | `coui_chip_selection_style_chip_icon_start_padding` | 0.dp          |
| SelectionTextSize   | TextUnit | Selection label size                        | 14.sp         |
