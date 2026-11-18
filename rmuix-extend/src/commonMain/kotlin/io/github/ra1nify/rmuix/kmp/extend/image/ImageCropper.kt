// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.extend.image

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * An image cropper: a fixed crop viewport over a pan/zoom image, with the area outside the viewport
 * scrimmed.
 *
 * ColorOS has no Compose image cropper - `com.coui.appcompat` has no such class, the stock crop flow is an
 * activity intent - so this component has **no stock metrics** and lives in `rmuix-extend`. It takes an
 * [androidx.compose.ui.graphics.ImageBitmap] rather than a platform Uri so it stays multiplatform: decoding
 * the user's pick is the caller's job.
 *
 * Gestures: one finger pans, two fingers pinch-zoom; rotation is not supported.
 *
 * @param state The cropper state, normally from [rememberImageCropperState].
 * @param modifier The modifier to be applied to the cropper.
 * @param cropRatio Width / height of the viewport. `1f` for an avatar, `1.6f` for a card cover.
 * @param shape The viewport's silhouette.
 * @param maskColor The scrim painted outside the viewport.
 * @param enabled Whether gestures are accepted.
 */
@Composable
fun ImageCropper(
    state: ImageCropperState,
    modifier: Modifier = Modifier,
    cropRatio: Float = 1f,
    shape: Shape = RoundedCornerShape(16.dp),
    maskColor: Color = Color(ImageCropperDefaults.MASK_COLOR_ARGB),
    enabled: Boolean = true,
) {
    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(cropRatio.coerceAtLeast(0.1f)),
    ) {
        val viewport =
            Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat()).takeIf {
                it.width > 0f && it.height > 0f
            } ?: Size.Zero

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        state.viewport = viewport
                        // Re-clamp after a size change so the image keeps covering the viewport.
                        state.applyTransform()
                    }.pointerInput(enabled, viewport) {
                        if (!enabled) return@pointerInput
                        detectTransformGestures { centroid, pan, zoom, _ ->
                            state.applyTransform(zoom = zoom, pan = pan, focal = centroid)
                        }
                    },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cover =
                    coverScale(
                        sourceWidth = state.image.width.toFloat(),
                        sourceHeight = state.image.height.toFloat(),
                        viewportWidth = size.width,
                        viewportHeight = size.height,
                    )
                val drawnWidth = state.image.width * cover * state.scale
                val drawnHeight = state.image.height * cover * state.scale
                drawImage(
                    image = state.image,
                    srcOffset = IntOffset.Zero,
                    srcSize = IntSize(state.image.width, state.image.height),
                    dstOffset = IntOffset(state.offset.x.toInt(), state.offset.y.toInt()),
                    dstSize =
                        IntSize(
                            drawnWidth.toInt().coerceAtLeast(1),
                            drawnHeight.toInt().coerceAtLeast(1),
                        ),
                    filterQuality = FilterQuality.High,
                )
                // Scrim = full viewport minus the viewport silhouette.
                val outline = shape.createOutline(size, layoutDirection, this)
                val scrim =
                    Path.combine(
                        PathOperation.Difference,
                        Path().apply { addRect(Rect(Offset.Zero, size)) },
                        Path().apply { addOutline(outline) },
                    )
                drawPath(path = scrim, color = maskColor)
                drawPath(
                    path = Path().apply { addOutline(outline) },
                    color = Color.White.copy(alpha = 0.2f),
                    style = Stroke(width = 1.dp.toPx()),
                )
            }
        }
    }
}
