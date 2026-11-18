# ImageCropper

A pan/zoom image cropper: a fixed crop viewport over the image, with the area outside it scrimmed.

::: warning No ColorOS counterpart
ColorOS has no Compose image cropper - `com.coui.appcompat` has no such class and the stock crop flow is an
activity intent - so this component has **no stock metrics**. It lives in `rmuix-extend` and its sizes come
from the library's tokens.
:::

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.extend.image.ImageCropper
import io.github.ra1nify.rmuix.kmp.extend.image.rememberImageCropperState
```

## Basic Usage

```kotlin
val image: ImageBitmap = rememberDecodedBitmap()   // decode the user's pick yourself
val state = rememberImageCropperState(image)

ImageCropper(
    state = state,
    modifier = Modifier.padding(16.dp),
    cropRatio = 1.6f,          // card cover
)

Button(onClick = { cropped = state.crop() }) { Text("Crop") }
```

`state.crop()` returns an `ImageBitmap` of the current viewport at 1:1 source resolution, or `null` before
the first layout.

## Properties

| Name       | Type               | Description                                  | Default |
| :--------- | :----------------- | :------------------------------------------- | :------ |
| state      | ImageCropperState  | Image, zoom, pan and viewport                 | - |
| modifier   | Modifier           | Modifier applied to the cropper               | Modifier |
| cropRatio  | Float              | Viewport width / height                       | 1f |
| shape      | Shape              | Viewport silhouette                           | RoundedCornerShape(16.dp) |
| maskColor  | Color              | Scrim painted outside the viewport            | #66000000 |
| enabled    | Boolean            | Whether gestures are accepted                 | true |

## ImageCropperState

| Name           | Type      | Description                                             |
| :------------- | :-------- | :------------------------------------------------------ |
| image          | ImageBitmap | The image being cropped                               |
| scale          | Float     | User zoom, `1f` = cover scale                           |
| offset         | Offset    | Pan in pixels, relative to the viewport's top-left      |
| viewport       | Size      | Crop viewport in pixels, set during layout              |
| canCrop        | Boolean   | Whether the viewport has been laid out                  |
| effectiveScale | Float     | Cover scale times `scale`                               |
| setImage()     | -         | Replaces the image and resets the transform             |
| reset()        | -         | Resets zoom and pan                                     |
| applyTransform() | -       | Applies a zoom about a focal point plus a pan           |
| crop()         | ImageBitmap? | Renders the viewport into a new bitmap               |

## Geometry notes

- `scale` is relative to the **cover scale** (`max(viewportW / sourceW, viewportH / sourceH)`), so `1f`
  always means "the image exactly fills the viewport" and the pan can never expose an empty edge.
- Zoom is clamped to `[1f, ImageCropperDefaults.MAX_SCALE_FACTOR]` (4x).
- Gestures: one finger pans, two fingers pinch-zoom; rotation is not supported.
- The component takes an `ImageBitmap`, not a platform Uri, so it stays multiplatform - decoding the user's
  pick (and writing the result back to a file) is the caller's job.

## ImageCropperDefaults

| Name            | Type  | Value |
| :-------------- | :---- | :---- |
| MASK_COLOR_ARGB | Long  | 0x66000000 |
| MAX_SCALE_FACTOR| Float | 4 |
| SAFE_INSET_DP   | Float | 16 |
