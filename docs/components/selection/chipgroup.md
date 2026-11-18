#! /usr/bin/env markdown

# ChipGroup

`ChipGroup` is the ColorOS chip group (`COUIChipGroup`): chips wrap into rows with the stock 8dp
spacing, and when the content would exceed a few rows the group collapses to a fixed number of rows and
appends a small chevron chip that expands it again.

<div style="position: relative; height: 280px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=chipGroup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-14)

`COUIChipGroup` is Material's `ChipGroup` plus a collapse behaviour. Verified facts
(`docs/dev/coloros-chip-group.md`):

- `coui_chip_group_default_spacing_horizontal` = 8dp
- `coui_chip_group_default_spacing_vertical` = 8dp
- `collapsedMaxRows` attribute default = 3 (`DEFAULT_MINIMUM_COLLAPSED_MAX_ROWS`)
- `coui_chip_group_collapsable_button_size` = 28dp, and the `.Collapsable` style puts the 16dp chevron at
  `chipIconStartPadding` 6dp with `chipIconEndPadding` 6dp (6 + 16 + 6 = 28dp, i.e. centred) - the same
  geometry the centred `Canvas` produces, pinned by `ChipGroupRenderingTest`
- the collapse / expand chip uses `Widget.COUI.Chip.Suggestion.Collapsable` (28dp, `chipIconSize` = 16dp,
  `closeIconSize` = 0dp) and the `ic_chip_collapse` (chevron up) / `ic_chip_expand` (chevron down) vectors,
  both 16dp viewports with a 0.9333 stroke tinted `?attr/couiColorLabelPrimary`

Inside a horizontally scrollable parent the width is unbounded and the group deliberately stays on a
single row (as a scroll container expects) instead of wrapping or computing an infinite size.

The group lays its rows out from the inline start, so in a right-to-left layout the first chip sits on
the right edge and each row fills towards the left (`COUIChipGroup` does the same through `isRTL()`).

**Differences this first pass keeps:** the stock view animates the reflow through view transitions
(`mAnimatingWidth` / `mAnimatingHeight`). This group animates the same size change on the equivalent
spring (`ChipGroupStockSizeChangeSpring`, response 0.35s / bounce 0). The per-chip choreography -
alpha spring response 0.2s, scale 0.8 -> 1.0 and X/Y motion at response 0.3s, all bounce 0 - is
**not** implemented: it needs composition-time knowledge of which rows get folded away, which this
custom layout only learns while measuring. The `singleSelection` / `selectionRequired` / `checkedChip`
attributes are not modelled either (pass `selected` to each [Chip] instead).

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Chip
import io.github.ra1nify.rmuix.kmp.basic.ChipDefaults
import io.github.ra1nify.rmuix.kmp.basic.ChipGroup
```

## Basic Usage

```kotlin
ChipGroup {
    repeat(20) { index ->
        Chip(onClick = {}, label = "Chip $index")
    }
}
```

When the chips overflow [ChipDefaults.CollapsedMaxRows] rows the group shows that many rows plus the
chevron chip; tapping it expands the group, tapping the up chevron collapses it again.

## ChipGroup

```kotlin
@Composable
fun ChipGroup(
    modifier: Modifier = Modifier,
    collapsedMaxRows: Int = ChipDefaults.CollapsedMaxRows,
    horizontalSpacing: Dp = ChipDefaults.GroupSpacing,
    verticalSpacing: Dp = ChipDefaults.GroupSpacing,
    content: @Composable () -> Unit,
)
```

| Parameter Name   | Type       | Description                                   | Default Value               | Required |
| ---------------- | ---------- | --------------------------------------------- | --------------------------- | -------- |
| modifier         | Modifier   | Modifier applied to the group                 | Modifier                    | No       |
| collapsedMaxRows | Int        | Rows kept while collapsed                     | ChipDefaults.CollapsedMaxRows | No     |
| horizontalSpacing | Dp        | Space between chips                           | ChipDefaults.GroupSpacing   | No       |
| verticalSpacing  | Dp         | Space between rows                            | ChipDefaults.GroupSpacing   | No       |
| content          | @Composable () -> Unit | The chips                         | -                           | Yes      |

## ChipDefaults additions

| Property Name          | Type | Description                          | Default Value |
| ---------------------- | ---- | ------------------------------------ | ------------- |
| CollapsedMaxRows       | Int  | Rows kept while collapsed            | 3             |
| GroupCollapseButtonSize | Dp  | Chevron chip size                    | 28.dp         |
| GroupCollapseGlyphSize | Dp   | Chevron glyph size                   | 16.dp         |

## List overload (selection + per-chip animation)

`ChipGroup(items = listOf(ChipGroupItem(...)))` mirrors the stock `COUIChipGroup` attributes that a
content lambda cannot express, because only the list form can index its chips:

| Parameter | `COUIChipGroup` attribute | Behaviour |
| :--- | :--- | :--- |
| `singleSelection` | `singleSelection` | at most one chip is `selected` at a time |
| `selectionRequired` | `selectionRequired` | the checked chip cannot be cleared; ignored when `singleSelection` is off |
| `checkedChip` | `checkedChip` | `null` keeps the state inside the group; a value makes it controlled |
| `onCheckedChange` | `setOnCheckedStateChangeListener` | called with the new index (`null` when cleared) |

Because the group now knows how many chips are visible it also reproduces the per-chip show/hide
motion: the stock builds `mAlphaAnimation` (response 0.2s, bounce 0) and `mScaleAnimation`
(scale 0.8 -> 1.0, response 0.3s, bounce 0) for every chip that folds in or out. Folded chips stay
laid out at their expanded positions and the group clips them, so they fade instead of vanishing.
The stock's `mHorizontalAnimation` / `mVerticalAnimation` (sliding a chip to its new position) is
**not** reproduced yet.
