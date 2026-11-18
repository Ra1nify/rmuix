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
 * The `Keyboard` icon, ported from the roui icon set (24dp, 24 viewport, 2dp round stroke).
 */
val RmuixIcons.Keyboard: ImageVector
    get() {
        if (_keyboard != null) return _keyboard!!
        _keyboard =
            ImageVector.Builder(
                name = "Keyboard",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 1.8f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                ) {
                    moveTo(2f, 6.9f)
                    curveTo(2f, 5.575f, 3.075f, 4.5f, 4.4f, 4.5f)
                    horizontalLineTo(19.6f)
                    curveTo(20.926f, 4.5f, 22f, 5.575f, 22f, 6.9f)
                    verticalLineTo(18.1f)
                    curveTo(22f, 19.426f, 20.926f, 20.5f, 19.6f, 20.5f)
                    horizontalLineTo(4.4f)
                    curveTo(3.075f, 20.5f, 2f, 19.426f, 2f, 18.1f)
                    verticalLineTo(6.9f)
                    close()
                }
                path(fill = SolidColor(Color.Black)) {
                    moveTo(6f, 7.5f); horizontalLineTo(8f); arcTo(0.5f, 0.5f, 0f, false, true, 8.5f, 8f); verticalLineTo(9f); arcTo(0.5f, 0.5f, 0f, false, true, 8f, 9.5f); horizontalLineTo(6f); arcTo(0.5f, 0.5f, 0f, false, true, 5.5f, 9f); verticalLineTo(8f); arcTo(0.5f, 0.5f, 0f, false, true, 6f, 7.5f); close()
                    moveTo(6f, 11.5f); horizontalLineTo(8f); arcTo(0.5f, 0.5f, 0f, false, true, 8.5f, 12f); verticalLineTo(13f); arcTo(0.5f, 0.5f, 0f, false, true, 8f, 13.5f); horizontalLineTo(6f); arcTo(0.5f, 0.5f, 0f, false, true, 5.5f, 13f); verticalLineTo(12f); arcTo(0.5f, 0.5f, 0f, false, true, 6f, 11.5f); close()
                    moveTo(11f, 7.5f); horizontalLineTo(13f); arcTo(0.5f, 0.5f, 0f, false, true, 13.5f, 8f); verticalLineTo(9f); arcTo(0.5f, 0.5f, 0f, false, true, 13f, 9.5f); horizontalLineTo(11f); arcTo(0.5f, 0.5f, 0f, false, true, 10.5f, 9f); verticalLineTo(8f); arcTo(0.5f, 0.5f, 0f, false, true, 11f, 7.5f); close()
                    moveTo(16f, 7.5f); horizontalLineTo(18f); arcTo(0.5f, 0.5f, 0f, false, true, 18.5f, 8f); verticalLineTo(9f); arcTo(0.5f, 0.5f, 0f, false, true, 18f, 9.5f); horizontalLineTo(16f); arcTo(0.5f, 0.5f, 0f, false, true, 15.5f, 9f); verticalLineTo(8f); arcTo(0.5f, 0.5f, 0f, false, true, 16f, 7.5f); close()
                    moveTo(11f, 11.5f); horizontalLineTo(13f); arcTo(0.5f, 0.5f, 0f, false, true, 13.5f, 12f); verticalLineTo(13f); arcTo(0.5f, 0.5f, 0f, false, true, 13f, 13.5f); horizontalLineTo(11f); arcTo(0.5f, 0.5f, 0f, false, true, 10.5f, 13f); verticalLineTo(12f); arcTo(0.5f, 0.5f, 0f, false, true, 11f, 11.5f); close()
                    moveTo(16f, 11.5f); horizontalLineTo(18f); arcTo(0.5f, 0.5f, 0f, false, true, 18.5f, 12f); verticalLineTo(13f); arcTo(0.5f, 0.5f, 0f, false, true, 18f, 13.5f); horizontalLineTo(16f); arcTo(0.5f, 0.5f, 0f, false, true, 15.5f, 13f); verticalLineTo(12f); arcTo(0.5f, 0.5f, 0f, false, true, 16f, 11.5f); close()
                    moveTo(7f, 16f); curveTo(7f, 15.724f, 7.224f, 15.5f, 7.5f, 15.5f); horizontalLineTo(16.5f); curveTo(16.776f, 15.5f, 17f, 15.724f, 17f, 16f); verticalLineTo(17f); curveTo(17f, 17.276f, 16.776f, 17.5f, 16.5f, 17.5f); horizontalLineTo(7.5f); curveTo(7.224f, 17.5f, 7f, 17.276f, 7f, 17f); close()
                }
            }.build()
        return _keyboard!!
    }

private var _keyboard: ImageVector? = null
