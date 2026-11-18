# RoundImage

The ColorOS rounded image: `COUIRoundImageView` (`com.coui.appcompat.imageview`), the 36dp preference icons
(`couiType="round"`) used across Settings (180 layout references in the dumps).

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.RoundImage
import io.github.ra1nify.rmuix.kmp.basic.RoundImageType
```

## Basic Usage

```kotlin
RoundImage(
    painter = painterResource(Res.drawable.account_avatar),
    contentDescription = null,
    modifier = Modifier.size(36.dp),
    type = RoundImageType.Round,
    showBorder = true,
)
```

## Properties

| Name | Type | Description | Default |
| :--- | :--- | :--- | :--- |
| painter | Painter | The image to draw | - |
| contentDescription | String? | Accessibility description | - |
| modifier | Modifier | Applied to the image; the stock size comes from the layout | Modifier |
| type | RoundImageType | `Circle` / `Round` / `Shadow` (`COUIRoundImageView.mType`) | Round |
| radius | Dp | The `Round` corner radius (`couiBorderRadius`, 1dp by default) | 1dp |
| showBorder | Boolean | Draws the stock `mOutCircle` outline (the only stroke `onDraw()` paints) | false |
| contentScale | ContentScale | How the painter fills the bounds | ContentScale.Crop |
| colors | RoundImageColors | Border colour | RoundImageDefaults.colors() |

## RoundImageDefaults

| Name | Type | Source | Value |
| :--- | :--- | :--- | :--- |
| Radius | Dp | `couiBorderRadius` default corner radius | 1dp |
| CircleFallbackSize | Dp | `coui_roundimageview_default_radius` fallback circle width | 42dp |
| BorderWidth | Dp | `COUIRoundImageView.mBorderWidth` (2px, also `mOutCircle`'s stroke width) | 1dp |
| ShadowRingWidth | Dp | `BORDER_CIRCLE_WIDTH` (5px) | 2dp |
| ShadowSourceSize | Dp | `coui_roundimageView_src_width` | 60dp |

## Colours

| Slot | Source | Light | Dark |
| :--- | :--- | :--- | :--- |
| borderColor | `coui_border` (`mBorderPaint`): **dead token** - initialised by `initBorderPaint()` but never drawn by `onDraw()` | `#26000000` | `#26FFFFFF` |
| outCircleColor | `coui_roundimageview_outcircle_color` / `_dark`: the `mOutCircle` border stroke and the `Shadow` ring | `#1A000000` | `#33FFFFFF` |

**BREAKING:** `showBorder` now strokes with `outCircleColor` - the stock `mOutCircle` paint
(`setOutCircleColor` target, `mBorderWidth` stroke). It previously used `colors.borderColor`, which the
stock's `onDraw()` never draws.

## Known gaps

- The stock `BitmapShader` matrix / `ContentScale` cropping is delegated to Compose's `Image`, which is
  equivalent for the `Crop`/`Fit` cases the dumps use.
- The `Shadow` type approximates the stock `coui_round_image_view_shadow` ring with a drop shadow.
- The out-circle stroke uses the fixed `BorderWidth` (`mOutCircle.setStrokeWidth(mBorderWidth)`);
  `COUIRoundImageView` exposes `setOutCircleColor` but **no** `setOutCircleWidth` setter, so there is no
  width token to expose.
