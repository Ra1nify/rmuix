// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.extend.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Defaults for [ImageCropper].
 *
 * ColorOS has no Compose image cropper; the nearest stock pieces are the crop activity intent and the
 * gallery's own editor, neither of which exposes dimensions. So these values are **not** stock metrics.
 */
object ImageCropperDefaults {
    /** Scrim painted outside the crop viewport (`0x66000000`). */
    const val MASK_COLOR_ARGB = 0x66000000L

    /** How far past the cover scale the user may zoom in. */
    const val MAX_SCALE_FACTOR = 4f

    /** Inset kept around a derived viewport, in dp. */
    const val SAFE_INSET_DP = 16f
}

/** Cover scale: the smallest zoom that fills the viewport in both axes. */
internal fun coverScale(
    sourceWidth: Float,
    sourceHeight: Float,
    viewportWidth: Float,
    viewportHeight: Float,
): Float {
    if (sourceWidth <= 0f || sourceHeight <= 0f || viewportWidth <= 0f || viewportHeight <= 0f) return 1f
    return max(viewportWidth / sourceWidth, viewportHeight / sourceHeight)
}

/** Clamps a user zoom to `[coverScale, coverScale * maxScaleFactor]`. */
internal fun clampScale(
    scale: Float,
    cover: Float,
    maxScaleFactor: Float,
): Float = scale.coerceIn(cover, cover * maxScaleFactor)

/** Clamps the pan so the image always covers the viewport. */
internal fun clampOffset(
    offset: Offset,
    drawnWidth: Float,
    drawnHeight: Float,
    viewportWidth: Float,
    viewportHeight: Float,
): Offset {
    val minX = min(0f, viewportWidth - drawnWidth)
    val minY = min(0f, viewportHeight - drawnHeight)
    return Offset(
        x = offset.x.coerceIn(minX, 0f),
        y = offset.y.coerceIn(minY, 0f),
    )
}

/** Collapses `-0f` to `0f` so geometry comparisons stay exact. */
private fun normaliseZero(value: Float): Float = if (value == 0f) 0f else value

/** The source-image rectangle that the viewport currently shows, in source pixels. */
internal fun sourceRectOfViewport(
    sourceWidth: Int,
    sourceHeight: Int,
    viewport: Size,
    scale: Float,
    offset: Offset,
): Rect {
    val drawnScale = scale
    if (drawnScale <= 0f) return Rect(0f, 0f, sourceWidth.toFloat(), sourceHeight.toFloat())
    val width = (viewport.width / drawnScale).coerceIn(1f, sourceWidth.toFloat())
    val height = (viewport.height / drawnScale).coerceIn(1f, sourceHeight.toFloat())
    val left = normaliseZero((-offset.x / drawnScale).coerceIn(0f, sourceWidth - width))
    val top = normaliseZero((-offset.y / drawnScale).coerceIn(0f, sourceHeight - height))
    return Rect(left, top, left + width, top + height)
}

/**
 * The state of an [ImageCropper]: the displayed image, the user zoom and pan, and the viewport.
 *
 * Geometry helpers are pure functions so they stay testable: [scale] is relative to the cover scale, so
 * `1f` always means "the image exactly fills the viewport".
 */
@Stable
class ImageCropperState internal constructor(
    image: ImageBitmap,
) {
    /** The image being cropped. */
    var image: ImageBitmap by mutableStateOf(image)
        internal set

    /** User zoom, `1f` = cover scale. */
    var scale: Float by mutableFloatStateOf(1f)
        internal set

    /** Pan in pixels, relative to the viewport's top-left corner. */
    var offset: Offset by mutableStateOf(Offset.Zero)
        internal set

    /** The crop viewport in pixels; set by [ImageCropper] during layout. */
    var viewport: Size by mutableStateOf(Size.Zero)
        internal set

    /** Whether the image has been laid out at least once. */
    val canCrop: Boolean
        get() = viewport.width > 0f && viewport.height > 0f

    /** The effective scale applied to the source image: cover scale times [scale]. */
    val effectiveScale: Float
        get() = coverScale(image.width.toFloat(), image.height.toFloat(), viewport.width, viewport.height) * scale

    /** Replaces the image and resets the transform. */
    fun setImage(newImage: ImageBitmap) {
        image = newImage
        reset()
    }

    /** Resets zoom and pan. */
    fun reset() {
        scale = 1f
        offset = Offset.Zero
    }

    /** Applies a zoom about [focal] (viewport coordinates) and a pan. */
    fun applyTransform(
        zoom: Float = 1f,
        pan: Offset = Offset.Zero,
        focal: Offset? = null,
    ) {
        val cover = coverScale(image.width.toFloat(), image.height.toFloat(), viewport.width, viewport.height)
        val nextScale = clampScale(scale * zoom, 1f, ImageCropperDefaults.MAX_SCALE_FACTOR)
        val pivot = focal ?: Offset(viewport.width / 2f, viewport.height / 2f)
        // Keep the point under the fingers fixed while the scale changes.
        val scaleChange = nextScale / scale
        val nextOffset =
            Offset(
                x = pivot.x - (pivot.x - offset.x) * scaleChange + pan.x,
                y = pivot.y - (pivot.y - offset.y) * scaleChange + pan.y,
            )
        scale = nextScale
        offset =
            clampOffset(
                offset = nextOffset,
                drawnWidth = image.width * cover * nextScale,
                drawnHeight = image.height * cover * nextScale,
                viewportWidth = viewport.width,
                viewportHeight = viewport.height,
            )
    }

    /**
     * Renders the current viewport into a new [ImageBitmap].
     *
     * Returns `null` while the cropper has not been laid out yet. The result is drawn at 1:1 source
     * resolution, so the caller controls the final size by the viewport's aspect ratio, not by its pixel
     * count.
     */
    fun crop(): ImageBitmap? {
        if (!canCrop) return null
        val effective = effectiveScale
        val rect = sourceRectOfViewport(image.width, image.height, viewport, effective, offset)
        val width = rect.width.roundToInt().coerceAtLeast(1)
        val height = rect.height.roundToInt().coerceAtLeast(1)
        val target = ImageBitmap(width, height)
        val canvas = Canvas(target)
        CanvasDrawScope().draw(
            density = Density(1f),
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = Size(width.toFloat(), height.toFloat()),
        ) {
            drawImage(
                image = this@ImageCropperState.image,
                srcOffset = IntOffset(rect.left.roundToInt(), rect.top.roundToInt()),
                srcSize = IntSize(width, height),
                dstOffset = IntOffset.Zero,
                dstSize = IntSize(width, height),
                filterQuality = FilterQuality.High,
            )
        }
        return target
    }
}

/** Remembers an [ImageCropperState] for [image]. */
@Composable
fun rememberImageCropperState(image: ImageBitmap): ImageCropperState = remember(image) { ImageCropperState(image) }
