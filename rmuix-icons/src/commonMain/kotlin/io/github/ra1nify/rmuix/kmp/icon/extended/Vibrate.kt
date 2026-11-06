// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.icon.extended

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons

val RmuixIcons.Outline.Vibrate: ImageVector
    get() = RmuixIcons.Regular.Vibrate

val RmuixIcons.Default.Vibrate: ImageVector
    get() = RmuixIcons.Regular.Vibrate

val RmuixIcons.Filled.Vibrate: ImageVector
    get() = RmuixIcons.Regular.Vibrate

val RmuixIcons.Vibrate: ImageVector
    get() = RmuixIcons.Regular.Vibrate

val RmuixIcons.Light.Vibrate: ImageVector
    get() {
        if (_vibrateLight != null) return _vibrateLight!!
        _vibrateLight = ImageVector.Builder(
            name = "Vibrate.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(16.0f, 3.0f),
                        PathNode.HorizontalTo(8.0f),
                        PathNode.CurveTo(6.9f, 3.0f, 6.0f, 3.9f, 6.0f, 5.0f),
                        PathNode.VerticalTo(19.0f),
                        PathNode.CurveTo(6.0f, 20.1f, 6.9f, 21.0f, 8.0f, 21.0f),
                        PathNode.HorizontalTo(16.0f),
                        PathNode.CurveTo(17.1f, 21.0f, 18.0f, 20.1f, 18.0f, 19.0f),
                        PathNode.VerticalTo(5.0f),
                        PathNode.CurveTo(18.0f, 3.9f, 17.1f, 3.0f, 16.0f, 3.0f),
                        PathNode.Close,
                        PathNode.MoveTo(3.0f, 8.0f),
                        PathNode.LineTo(2.0f, 12.0f),
                        PathNode.LineTo(3.0f, 16.0f),
                        PathNode.MoveTo(21.0f, 8.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                        PathNode.LineTo(21.0f, 16.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _vibrateLight!!
    }
private var _vibrateLight: ImageVector? = null

val RmuixIcons.Normal.Vibrate: ImageVector
    get() {
        if (_vibrateNormal != null) return _vibrateNormal!!
        _vibrateNormal = ImageVector.Builder(
            name = "Vibrate.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(16.0f, 3.0f),
                        PathNode.HorizontalTo(8.0f),
                        PathNode.CurveTo(6.9f, 3.0f, 6.0f, 3.9f, 6.0f, 5.0f),
                        PathNode.VerticalTo(19.0f),
                        PathNode.CurveTo(6.0f, 20.1f, 6.9f, 21.0f, 8.0f, 21.0f),
                        PathNode.HorizontalTo(16.0f),
                        PathNode.CurveTo(17.1f, 21.0f, 18.0f, 20.1f, 18.0f, 19.0f),
                        PathNode.VerticalTo(5.0f),
                        PathNode.CurveTo(18.0f, 3.9f, 17.1f, 3.0f, 16.0f, 3.0f),
                        PathNode.Close,
                        PathNode.MoveTo(3.0f, 8.0f),
                        PathNode.LineTo(2.0f, 12.0f),
                        PathNode.LineTo(3.0f, 16.0f),
                        PathNode.MoveTo(21.0f, 8.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                        PathNode.LineTo(21.0f, 16.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _vibrateNormal!!
    }
private var _vibrateNormal: ImageVector? = null

val RmuixIcons.Regular.Vibrate: ImageVector
    get() {
        if (_vibrateRegular != null) return _vibrateRegular!!
        _vibrateRegular = ImageVector.Builder(
            name = "Vibrate.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(16.0f, 3.0f),
                        PathNode.HorizontalTo(8.0f),
                        PathNode.CurveTo(6.9f, 3.0f, 6.0f, 3.9f, 6.0f, 5.0f),
                        PathNode.VerticalTo(19.0f),
                        PathNode.CurveTo(6.0f, 20.1f, 6.9f, 21.0f, 8.0f, 21.0f),
                        PathNode.HorizontalTo(16.0f),
                        PathNode.CurveTo(17.1f, 21.0f, 18.0f, 20.1f, 18.0f, 19.0f),
                        PathNode.VerticalTo(5.0f),
                        PathNode.CurveTo(18.0f, 3.9f, 17.1f, 3.0f, 16.0f, 3.0f),
                        PathNode.Close,
                        PathNode.MoveTo(3.0f, 8.0f),
                        PathNode.LineTo(2.0f, 12.0f),
                        PathNode.LineTo(3.0f, 16.0f),
                        PathNode.MoveTo(21.0f, 8.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                        PathNode.LineTo(21.0f, 16.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _vibrateRegular!!
    }
private var _vibrateRegular: ImageVector? = null

val RmuixIcons.Medium.Vibrate: ImageVector
    get() {
        if (_vibrateMedium != null) return _vibrateMedium!!
        _vibrateMedium = ImageVector.Builder(
            name = "Vibrate.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(16.0f, 3.0f),
                        PathNode.HorizontalTo(8.0f),
                        PathNode.CurveTo(6.9f, 3.0f, 6.0f, 3.9f, 6.0f, 5.0f),
                        PathNode.VerticalTo(19.0f),
                        PathNode.CurveTo(6.0f, 20.1f, 6.9f, 21.0f, 8.0f, 21.0f),
                        PathNode.HorizontalTo(16.0f),
                        PathNode.CurveTo(17.1f, 21.0f, 18.0f, 20.1f, 18.0f, 19.0f),
                        PathNode.VerticalTo(5.0f),
                        PathNode.CurveTo(18.0f, 3.9f, 17.1f, 3.0f, 16.0f, 3.0f),
                        PathNode.Close,
                        PathNode.MoveTo(3.0f, 8.0f),
                        PathNode.LineTo(2.0f, 12.0f),
                        PathNode.LineTo(3.0f, 16.0f),
                        PathNode.MoveTo(21.0f, 8.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                        PathNode.LineTo(21.0f, 16.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _vibrateMedium!!
    }
private var _vibrateMedium: ImageVector? = null

val RmuixIcons.Demibold.Vibrate: ImageVector
    get() {
        if (_vibrateDemibold != null) return _vibrateDemibold!!
        _vibrateDemibold = ImageVector.Builder(
            name = "Vibrate.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(16.0f, 3.0f),
                        PathNode.HorizontalTo(8.0f),
                        PathNode.CurveTo(6.9f, 3.0f, 6.0f, 3.9f, 6.0f, 5.0f),
                        PathNode.VerticalTo(19.0f),
                        PathNode.CurveTo(6.0f, 20.1f, 6.9f, 21.0f, 8.0f, 21.0f),
                        PathNode.HorizontalTo(16.0f),
                        PathNode.CurveTo(17.1f, 21.0f, 18.0f, 20.1f, 18.0f, 19.0f),
                        PathNode.VerticalTo(5.0f),
                        PathNode.CurveTo(18.0f, 3.9f, 17.1f, 3.0f, 16.0f, 3.0f),
                        PathNode.Close,
                        PathNode.MoveTo(3.0f, 8.0f),
                        PathNode.LineTo(2.0f, 12.0f),
                        PathNode.LineTo(3.0f, 16.0f),
                        PathNode.MoveTo(21.0f, 8.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                        PathNode.LineTo(21.0f, 16.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _vibrateDemibold!!
    }
private var _vibrateDemibold: ImageVector? = null
