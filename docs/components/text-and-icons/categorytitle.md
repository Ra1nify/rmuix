# CategoryTitle

A ColorOS section header: the title view of `COUIPreferenceCategory`.

It carries the stock metrics only - 12sp **medium** (`couiTextAppearanceSmallButton` sets
`sans-serif-medium`), at least 16dp tall, inset 32dp from the inline start and 8dp from the inline end,
20dp above (12dp category top margin + 8dp title margin) and 8dp below, in `couiColorSecondNeutral`.
Letter spacing is left to the theme's default text style.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
```

## Basic Usage

```kotlin
CategoryTitle(text = "Network & internet")
BasicComponentGroup {
    /* rows */
}
```

## Properties

| Name         | Type           | Description                     | Default |
| :----------- | :------------- | :------------------------------ | :------ |
| text         | String         | Section title                   | - |
| modifier     | Modifier       | Modifier applied to the title    | Modifier |
| textColor    | Color          | Title colour                    | CategoryTitleDefaults.textColor() |
| insideMargin | PaddingValues  | Padding around the title text    | CategoryTitleDefaults.InsideMargin |

## CategoryTitleDefaults

| Name          | Type          | Description                                             | Value |
| :------------ | :------------ | :------------------------------------------------------ | :---- |
| TextSize      | TextUnit      | `Widget.COUI.List.Category.Title` -> `couiTextAppearanceSmallButton` (and `setTextSize(2, 12.0f)`) | 12sp |
| MinHeight     | Dp            | `coui_preference_category_text_height`                  | 16dp |
| MarginStart   | Dp            | `support_preference_category_layout_title_margin_start_large` (class default margin type; no Settings XML overrides it) | 32dp |
| MarginTop     | Dp            | `coui_preference_category_margintop_small` (category item top margin; every Settings XML that sets `top_margin_type` uses `small`) | 12dp |
| TextWeight    | FontWeight    | `couiTextAppearanceSmallButton` -> `sans-serif-medium` | Medium |
| MarginEnd     | Dp            | `coui_category_title_pading_end_with_reddot_default`     | 8dp |
| VerticalMargin| Dp            | `coui_common_category_text_padding_top` / `_bottom` (title view margins) | 8dp |
| InsideMargin  | PaddingValues | The margins above, as padding (title view padding is 0dp) | start 32dp / end 8dp / top 20dp / bottom 8dp |

Colour is `?attr/couiColorSecondNeutral` = `coui_color_secondary_neutral`: `#8A000000` in the light theme
and `#8AFFFFFF` in the dark one. `COUIPreferenceCategory.initStartRect()` widens it to
`?attr/couiColorPrimaryNeutral` (`#E6000000` / `#E6FFFFFF`) only when the category uses a **medium/large
`title_type`** or a **small title with an `icon_with_title` glyph**. **Correction:** this is not a
"title-only style" - no Settings XML sets `title_type` / `icon_with_title`, and no decompiled Settings class
calls `setTitleType()` / `setIconWithTitle()`, so every Settings category uses `SecondNeutral`. A caller that
enables those styles passes the other colour through `textColor`.

## Replaces SmallTitle

`SmallTitle` was a miuix leftover with no ColorOS counterpart (28dp start, 8dp vertical, `subtitle` text
style). It was removed on 2026-11-14 and every call site now uses this component.
