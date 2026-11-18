// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.extend.image

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageCropperGeometryTest {
    @Test
    fun coverScaleFillsBothAxes() {
        // 1000x500 source in a 400x400 viewport: height is the binding axis
        assertEquals(0.8f, coverScale(1000f, 500f, 400f, 400f))
        // 500x1000 source in a 400x400 viewport: width is the binding axis
        assertEquals(0.8f, coverScale(500f, 1000f, 400f, 400f))
        // exact match
        assertEquals(1f, coverScale(400f, 400f, 400f, 400f))
    }

    @Test
    fun zoomIsClampedToTheCoverScaleRange() {
        val cover = 0.8f
        assertEquals(cover, clampScale(0.1f, cover, ImageCropperDefaults.MAX_SCALE_FACTOR))
        assertEquals(cover, clampScale(cover, cover, ImageCropperDefaults.MAX_SCALE_FACTOR))
        assertEquals(
            cover * ImageCropperDefaults.MAX_SCALE_FACTOR,
            clampScale(cover * 100f, cover, ImageCropperDefaults.MAX_SCALE_FACTOR),
        )
    }

    @Test
    fun panKeepsTheImageCoveringTheViewport() {
        val clamped =
            clampOffset(
                offset = Offset(50f, -20f),
                drawnWidth = 800f,
                drawnHeight = 400f,
                viewportWidth = 400f,
                viewportHeight = 400f,
            )
        // The drawn height equals the viewport height, so vertical pan is pinned at 0.
        assertEquals(0f, clamped.x)
        assertEquals(0f, clamped.y)
        val far =
            clampOffset(
                offset = Offset(-1000f, -1000f),
                drawnWidth = 800f,
                drawnHeight = 400f,
                viewportWidth = 400f,
                viewportHeight = 400f,
            )
        assertEquals(-400f, far.x)
        assertEquals(0f, far.y)
    }

    @Test
    fun viewportMapsBackToSourcePixels() {
        // 1000x500 source shown at cover scale 0.8 -> 800x400 drawn; a 400x400 viewport at pan 0 shows
        // the left 500 source px in x and the full 500 in y.
        val rect =
            sourceRectOfViewport(
                sourceWidth = 1000,
                sourceHeight = 500,
                viewport = Size(400f, 400f),
                scale = 0.8f,
                offset = Offset.Zero,
            )
        assertEquals(0f, rect.left, 0.01f)
        assertEquals(500f, rect.width, 0.01f)
        assertEquals(0f, rect.top, 0.01f)
        assertEquals(500f, rect.height, 0.01f)
    }

    @Test
    fun centredPanMapsToTheMiddleOfTheSource() {
        // Drawn 800x400 in a 400x400 viewport: the full pan is -400 in x, so -200 centres it.
        val rect =
            sourceRectOfViewport(
                sourceWidth = 1000,
                sourceHeight = 500,
                viewport = Size(400f, 400f),
                scale = 0.8f,
                offset = Offset(-200f, 0f),
            )
        assertEquals(250f, rect.left, 0.01f)
        assertEquals(0f, rect.top, 0.01f)
        assertEquals(750f, rect.right, 0.01f)
        assertEquals(500f, rect.bottom, 0.01f)
    }
}
