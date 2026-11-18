// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons

/**
 * The `Camera` icon, ported from the roui icon set (24dp, 24 viewport, 2dp round stroke).
 */
val RmuixIcons.Camera: ImageVector
    get() {
        if (_camera != null) return _camera!!
        _camera =
            ImageVector.Builder(
                name = "Camera",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2.0f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(3f, 8f)
                    curveTo(3f, 6.9f, 3.9f, 6f, 5f, 6f)
                    lineTo(7.5f, 6f)
                    lineTo(9f, 4f)
                    lineTo(15f, 4f)
                    lineTo(16.5f, 6f)
                    lineTo(19f, 6f)
                    curveTo(20.1f, 6f, 21f, 6.9f, 21f, 8f)
                    lineTo(21f, 19f)
                    curveTo(21f, 20.1f, 20.1f, 21f, 19f, 21f)
                    lineTo(5f, 21f)
                    curveTo(3.9f, 21f, 3f, 20.1f, 3f, 19f)
                    close()
                    moveTo(12f, 9.5f)
                    curveTo(10.07f, 9.5f, 8.5f, 11.07f, 8.5f, 13f)
                    curveTo(8.5f, 14.93f, 10.07f, 16.5f, 12f, 16.5f)
                    curveTo(13.93f, 16.5f, 15.5f, 14.93f, 15.5f, 13f)
                    curveTo(15.5f, 11.07f, 13.93f, 9.5f, 12f, 9.5f)
                    close()
                }
            }.build()
        return _camera!!
    }

private var _camera: ImageVector? = null
