# PageIndicator

The ColorOS page dots (`COUIPageIndicator`): one circle per page, spaced by
`coui_page_indicator_dot_spacing` (3dp).

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.PageIndicator
```

## Basic Usage

```kotlin
PageIndicator(count = 5, selectedIndex = 2)
```

## Properties

| Name          | Type                 | Description                        | Default |
| :------------ | :------------------- | :--------------------------------- | :------ |
| count         | Int                  | Number of pages                    | - |
| selectedIndex | Int                  | Highlighted page (clamped)         | - |
| modifier      | Modifier             | Modifier applied to the indicator  | Modifier |
| dotSize       | Dp                   | Dot diameter                       | PageIndicatorDefaults.DotSize (6dp) |
| spacing       | Dp                   | Space between dots                 | PageIndicatorDefaults.Spacing (3dp) |
| colors        | PageIndicatorColors  | Selected / unselected dot colours  | PageIndicatorDefaults.colors() |

## PageIndicatorDefaults

| Name          | Type | Description                    | Value |
| :------------ | :--- | :----------------------------- | :---- |
| DotSize       | Dp   | `coui_page_indicator_dot_size` | 6dp |
| DotSizeMedium | Dp   | `coui_page_indicator_dot_size_medium` | 4.5dp |
| DotSizeSmall  | Dp   | `coui_page_indicator_dot_size_small`  | 2.5dp |
| Spacing       | Dp   | `coui_page_indicator_dot_spacing`     | 3dp |
| StrokeWidth   | Dp   | `coui_page_indicator_dot_stroke_width`| 1dp |

Colours: `Widget.COUI.COUIPageIndicator` sets `traceDotColor` = `@color/coui_color_label_primary`
(selected, `#E6000000` / `#E6FFFFFF`) and `dotColor` = `?attr/couiColorHintNeutral` (unselected,
`#42000000` / `#4DFFFFFF`); the `.Dark` style swaps in the `_dark` variants of the same two colours.

## Not ported

The stock widget shrinks the dots when more than six pages exist (`MAX_VISIBLE_DOT_NUMBER` 6,
`DEFAULT_MAXIMUM_LARGE_DOT_NUMBER` 4, `DEFAULT_MAXIMUM_MEDIUM_DOT_NUMBER` 2, level sizes `9 / 3 / 5 / 5 / 7`).
Only the flat variant is implemented.
