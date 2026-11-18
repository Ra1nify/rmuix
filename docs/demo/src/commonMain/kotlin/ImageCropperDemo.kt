// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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

@Composable
fun ImageCropperDemo() {
    Box(
        modifier = Modifier.fillMaxSize().background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        val image = remember { demoGradientBitmap() }
        val state = rememberImageCropperState(image)
        Column(
            Modifier.padding(16.dp).widthIn(max = 600.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategoryTitle(text = "ImageCropper")
                    ImageCropper(state = state, cropRatio = 1.6f)
                }
            }
        }
    }
}

/** A 900x600 gradient so the demo needs no bundled asset. */
private fun demoGradientBitmap(): ImageBitmap {
    val width = 900
    val height = 600
    val bitmap = ImageBitmap(width, height)
    CanvasDrawScope().draw(
        density = Density(1f),
        layoutDirection = LayoutDirection.Ltr,
        canvas = Canvas(bitmap),
        size = Size(width.toFloat(), height.toFloat()),
    ) {
        drawRect(
            brush =
                Brush.linearGradient(
                    listOf(Color(0xFF0080FF), Color(0xFF7B1FA2), Color(0xFFFBC02D)),
                ),
        )
    }
    return bitmap
}
