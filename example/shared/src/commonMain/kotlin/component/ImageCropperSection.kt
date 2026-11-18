// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.extend.image.ImageCropper
import io.github.ra1nify.rmuix.kmp.extend.image.rememberImageCropperState

fun LazyListScope.imageCropperSection() {
    item(key = "imageCropper") {
        CategoryTitle(text = "ImageCropper (rmuix-extend)")
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            ImageCropperRows()
        }
    }
}

@Composable
private fun ImageCropperRows() {
    val image = remember { gradientBitmap() }
    val state = rememberImageCropperState(image)
    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        // No ColorOS crop component exists; the page documents rmuix-extend geometry instead of stock
        // metrics. One finger pans, two fingers pinch-zoom.
        ImageCropper(state = state, cropRatio = 1.6f)
    }
}

private fun gradientBitmap(): ImageBitmap {
    val width = 900
    val height = 600
    val bitmap = ImageBitmap(width, height)
    CanvasDrawScope().draw(
        density = Density(1f),
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(bitmap),
        size = Size(width.toFloat(), height.toFloat()),
    ) {
        drawRect(brush = Brush.linearGradient(listOf(Color(0xFF0080FF), Color(0xFFFBC02D))))
    }
    return bitmap
}
