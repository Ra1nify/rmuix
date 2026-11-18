# SegmentButton

The ColorOS segmented control (`COUISegmentButtonLayout`): a capsule track holding content-sized
segments, with a sliding indicator behind the selected one.

Geometry comes from `getDrawableRadius()`, which returns `(bottom - top) / 2` for both the track and
the indicator — so both are capsules: the track's radius is half of its height and the indicator's is
half of its own (`height - 2 * padding`). Segment widths follow
`COUISegmentButtonLayout.onMeasure()` / `calculateChildrenWidths()`: natural text width plus the 12dp
padding (min `coui_segment_min_width` 52dp), then the leftover space is shared equally by the labels that
exceed the running average - so the segments are equal only when every label is shorter than the average.
The indicator slides on the stock spring
(`response` 0.4 / `bounce` 0.1).

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.SegmentButton
```

## Basic Usage

```kotlin
var index by remember { mutableIntStateOf(0) }
SegmentButton(
    options = listOf("Day", "Week", "Month"),
    selectedIndex = index,
    onSelect = { index = it },
)
```

## Properties

| Name          | Type                | Description                                  | Default |
| :------------ | :------------------ | :------------------------------------------- | :------ |
| options       | List<String>        | Segment labels                               | - |
| selectedIndex | Int                 | Selected segment (clamped)                   | - |
| onSelect      | (Int) -> Unit       | Called with the tapped segment index         | - |
| modifier      | Modifier            | Modifier applied to the control              | Modifier |
| enabled       | Boolean             | Whether segments can be selected             | true |
| tiny          | Boolean             | Use the 32dp `SegmentButton.Tiny` metrics    | false |
| height        | Dp                  | Track height                                 | 40dp / 32dp (tiny) |
| colors        | SegmentButtonColors | Track, indicator and text colours            | SegmentButtonDefaults.colors() |

## SegmentButtonDefaults

| Name                    | Type | Description                                    | Value |
| :---------------------- | :--- | :--------------------------------------------- | :---- |
| Height                  | Dp   | `SegmentButton` style `layout_height`          | 40dp |
| TinyHeight              | Dp   | `SegmentButton.Tiny` style `layout_height`     | 32dp |
| Padding                 | Dp   | `coui_segment_padding`                         | 4dp |
| TinyPadding             | Dp   | `coui_segment_padding_tiny`                    | 2dp |
| MinSegmentWidth         | Dp   | `coui_segment_min_width`                       | 52dp |
| SegmentHorizontalPadding| Dp   | `coui_segment_btn_padding_horizontal`          | 12dp |
| TextSize                | TextUnit | `couiTextButtonM` text size                 | 14sp |
| WidthSpringResponse     | Float | `COUISegmentButtonLayout.WIDTH_SPRING_RESPONSE` | 0.6 |
| WidthSpringBounce       | Float | `COUISegmentButtonLayout.WIDTH_SPRING_BOUNCE`   | 0.5 |
| IndicatorShadowOffset   | Dp   | `coui_segment_shadow_offset`                   | 2dp |
| IndicatorShadowEndColor | Color | `coui_segment_button_shadow_end_color`        | `#03000000` |

### Colours

| Role | Light | Dark | Source |
| :--- | :--- | :--- | :--- |
| Track | `#0F000000` | `#1FFFFFFF` | `coui_color_segment_button_background` |
| Indicator | `#B2FFFFFF` | `#33FFFFFF` | `coui_color_segment_button_indicator` |
| Indicator shadow | `#0D000000` | `#14000000` | `coui_segment_button_shadow_color` |
| Indicator shadow end | `#03000000` | (dark uses the solid start colour) | `coui_segment_button_shadow_end_color` |
| Selected / unselected text | `?attr/couiColorLabelPrimary` (both) | same | `loadAttr()` |

The indicator is a near-white block in the light theme over a 6% black track, and the reverse in the
dark theme. That is the resource value, not an inversion mistake — the selected segment is
distinguished by the block, not by its text colour.

## Indicator shadow

`COUISegmentButtonLayout.drawIndicator(canvas, TRUE, ...)` always paints the shadow before the block: a
rect that starts `coui_segment_shadow_offset` = **2dp** below the indicator top, keeps the indicator's
left/right edges and reaches the container bottom, with the indicator rect cut out of it. Light theme
uses a radial gradient `#0D000000 -> #03000000` centred on the indicator (radius = indicator width / 2);
dark theme paints `#14000000` solid. The port draws the same layer (its bottom edge follows the padded
content, 4dp above the container's very bottom).

## Not reproduced

- the caustic shadow edge (`mDrawMaterialEffect` / `OPlusMaterialEffect`) and its 1dp `coui_segment_shadow_radius`;
- the short-to-long extra-width animation is modelled (`animateIndicatorToPosition()`): segment widths
  follow the stock children distribution; a selection change animates the indicator width with the dynamic
  width spring (`WIDTH_SPRING_VELOCITY` 1000px/s; short-to-long fixed 0.5 bounce, long-to-short dynamic
  0.5..0.1, response dynamic 0.6..0.4 via `calculateDynamicWidthParam()`), the position spring
  (0.4/0.1, tiny short-to-long dynamic bounce 0.1..0.03) and the height dip (`HEIGHT_SPRING_*` 0.6/0.5,
  -500px/s, clamped at `0.9 * height`). Compose springs cannot cap overshoot, so the drawn width is clamped
  at `target + coui_segment_regular_extra_width` 7dp (tiny: `coui_segment_small_extra_width` 5dp) - the
  stock's `getEffectiveMaxExtraWidthForShortToLong()`; `coui_segment_switch_distance` 5dp is likewise
  covered by that extra-width budget. The responsive width cap is implemented:
  `getResponsiveWidth()` maps the window's total size class to a centred 4 / 6 / 8-column span
  (`LayoutGridSystem`: compact 4 columns / 16dp margin / 8dp gutter; medium and expanded-portrait
  8 columns / 24dp / 8dp; plain expanded 12 columns / 40dp / 12dp) and the control is limited to
  `min(available, that span)`;
- the drag-to-select touch handling is modelled: dragging across segments selects the segment under the
  finger and calls `performHapticFeedback(302)` when the segment changes (mapped to Compose
  `HapticFeedbackType.SegmentFrequentTick`); lifting the finger selects the touched segment without the
  haptic, matching `dispatchTouchEvent()`. Taps also select through the same gesture detector, while the
  per-segment semantics keep `Role.RadioButton` / selected state / `onClick` for accessibility.
