# RedDot

`RedDot` is the ColorOS hint red dot / badge indicator (`COUIHintRedDot`). It renders a 6dp dot, a numeric counter or a short text label, in ColorOS's additional-red palette.

## Stock ColorOS static review (2026-11-12)

This pass reads `com.coui.appcompat.reddot.COUIHintRedDot` / `COUIHintRedDotHelper` and the `Widget.COUI.COUIHintRedDot` style of the Settings APK.

- Background: `?attr/couiHintRedDotColor` resolves to `@color/coui_color_additional_red` (`#EB3B2F` light / `#EB493D` dark). The component previously documented `coui_color_primary_text_red` (`#DB382C`), which no style references, and used the theme `error` role (`#E94634` / `#F12522`).
- Text: `couiHintRedDotTextColor` = `coui_color_white`, `couiHintTextSize` = 10dp. Both the count and text modes use `sans-serif-medium` (`COUIHintRedDotHelper` creates every `TextPaint` with it), and `PointWithText` draws the caller's string verbatim - stock does not upper-case it. The number badge used 10.5sp and the text badge 10sp; both now resolve `10sp`.
- Geometry was already right: `couiHeight` 16dp for the number/text badge, `couiDotDiameter` 6dp for the point, and `couiCornerRadius` = `?attr/couiRoundCornerFULL` (a pill, i.e. half the height).
- `coui_hint_red_dot_rect_radius` (2.67dp) is **declared but never read** (`mDotCornerRadius` is assigned and unused; the point dot is a circle of radius `height / 2`), so it is not the source of any radius here.

- The optional ring (`withStroke`) is `coui_dot_stroke_width` = **1dp** in `coui_color_white`; it was 1.5dp.

**BREAKING:** the default colour becomes `coui_color_additional_red` (theme-aware) instead of the theme's `error` role, and the number badge text is 10sp instead of 10.5sp.

`withStroke` also applies `coui_hint_red_dot_mode_stroke_extra_diameter` (**2dp**): the stock stroke modes (`POINT_ONLY_MODE_STROKE` / `POINT_NUM_MODE_STROKE`) grow the dot by 2dp on each axis so the white `red_dot_stroke_circle` ring fits around it, so a struck point dot is 8dp instead of 6dp and a struck number badge is 18dp instead of 16dp.

**Dot sizes:** the small/medium/large dot sizes (6/8/12dp) and the small text size (9dp) are exposed as constants; `RedDot` itself renders the default sizes, while the anchor buckets that pick a size live in `RedDotBox`.

`COUIRedDotFrameLayout` is ported as **`RedDotBox`** (see below) since 2026-11-14.

**Text sizes are declared in `dp`, rendered as `sp`.** The dump declares these `*_text_size` dimens in `dp`
(COUI reads them with `getDimensionPixelSize` and applies them as `COMPLEX_UNIT_PX`, so the real glyph
height scales with density). This library maps them to density-independent `sp` at the same number, which
is the equivalent value at 1x.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.RedDot
import io.github.ra1nify.rmuix.kmp.basic.RedDotDefaults
import io.github.ra1nify.rmuix.kmp.basic.RedDotMode
```

## Basic Usage

### Point Only

```kotlin
RedDot(mode = RedDotMode.PointOnly)
```

### Number

```kotlin
RedDot(mode = RedDotMode.PointWithNum(count = 5))   // prints the number; 1000+ renders three dots
```

### Text

```kotlin
RedDot(mode = RedDotMode.PointWithText(text = "NEW"))
```

### Outline

```kotlin
RedDot(
    mode = RedDotMode.PointWithNum(count = 12),
    withStroke = true,
    strokeColor = RmuixTheme.colorScheme.background,
)
```

## Anchoring a dot in a corner (`RedDotBox`)

`RedDotBox` is the port of `COUIRedDotFrameLayout`: it pins a dot or badge into the top-end
corner of its content, with the margins from the `coui_hint_red_dot_*_icon_topend_margin_*` /
`coui_hint_red_dot_*_number_topend_margin_rectangle` dimens.

```kotlin
RedDotBox(
    redDot = { RedDot(mode = RedDotMode.PointOnly) },
    anchorSize = 36.dp,
    anchorShape = RedDotAnchorShape.Rectangle,
    redDotKind = RedDotAnchorKind.Dot,
) {
    Icon(imageVector = RmuixIcons.Basic.Search, contentDescription = "Search")
}
```

The margin bucket follows the anchor size, exactly like the stock `init()`:

| `anchorSize` | bucket | circle margin | rectangle dot margin | rectangle number margin |
| :--- | :--- | ---: | ---: | ---: |
| `< 36dp` | small | 1dp | 1dp | 6dp |
| `36dp` – `49dp` | medium | 2dp | 2dp | 3dp |
| `>= 50dp` | large | 2dp | 4dp | 6dp |

The container is measured as the anchor plus that margin on both axes; for rectangle anchors the
anchor itself is offset by the margin (stock `mMarginTopAndLeftOfAnchorView`), while circle anchors
keep the anchor in place and inset only the dot (`mMarginTopAndLeftOfReddot`). RTL places both views
at the same margin, mirroring `COUIRedDotFrameLayout.onLayout`.

### RedDotBox Properties

| Property Name | Type | Description | Default Value | Required |
| ------------- | ---- | ----------- | ------------- | -------- |
| redDot | @Composable () -> Unit | The dot or badge to pin in the corner | - | Yes |
| modifier | Modifier | Modifier applied to the container | Modifier | No |
| anchorSize | Dp | Anchor size, selects the margin bucket (`anchorViewDpSize`) | `RedDotBoxDefaults.AnchorSize` (36dp) | No |
| anchorShape | RedDotAnchorShape | `Rectangle` or `Circle` (`anchorViewShapeType`) | `Rectangle` | No |
| redDotKind | RedDotAnchorKind | `Dot` or `Number` (`couiHintRedPointMode`) | `Dot` | No |
| content | @Composable () -> Unit | The anchored content | - | Yes |

## Properties

| Property Name | Type        | Description                                              | Default Value             | Required |
| ------------- | ----------- | -------------------------------------------------------- | ------------------------- | -------- |
| mode          | RedDotMode  | `PointOnly`, `PointWithNum(count)` or `PointWithText(text)` | -                      | Yes      |
| modifier      | Modifier    | Modifier applied to the red dot                           | Modifier                  | No       |
| color         | Color       | Background colour (`coui_color_additional_red` by default) | redDotDefaultColor()      | No       |
| withStroke    | Boolean     | Draw an outline around the dot/badge                      | false                     | No       |
| strokeColor   | Color       | Outline colour                                            | Color.White               | No       |
| strokeWidth   | Dp          | Outline width                                             | RedDotDefaults.StrokeWidth | No      |

## RedDotDefaults Object

| Constant Name | Type | Value | Description |
| ------------- | ---- | ----- | ----------- |
| BadgeHeight   | Dp   | 16.dp | Height of the number/text badge (`coui_height`) |
| BadgeRadius   | Dp   | 8.dp  | Pill radius, half the height (`couiRoundCornerFULL`) |
| PointSize     | Dp   | 6.dp  | Point-only dot diameter (`coui_dot_diameter`) |
| StrokeWidth   | Dp   | 1.dp   | Outline width (`coui_dot_stroke_width`, `coui_color_white`) |
| SmallWidth           | Dp | 16.dp | Minimum width for one digit (`coui_small_width`) |
| MediumWidth          | Dp | 20.dp | Minimum width for two digits, and the fallback at 1000+ (`coui_medium_width`) |
| LargeWidth           | Dp | 26.dp | Minimum width for three digits (`coui_large_width`) |
| SingleDigitMinWidth  | Dp | 16.dp | **Deprecated** — use `SmallWidth` |
| MultiDigitMinWidth   | Dp | 20.dp | **Deprecated** — use `MediumWidth` / `LargeWidth` |
| TextBadgeMinWidth    | Dp | 20.dp | Minimum width for the text badge |
| BadgeHorizontalPadding | Dp | 4.5.dp | Horizontal padding of the number badge |
| TextBadgeHorizontalPadding | Dp | 6.dp | Horizontal padding of the text badge |

## Advanced Usage

### Custom Colour

```kotlin
RedDot(
    mode = RedDotMode.PointOnly,
    color = RmuixTheme.colorScheme.primary,
)
```
