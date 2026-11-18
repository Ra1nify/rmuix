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

val RmuixIcons.Outline.Brightness: ImageVector
    get() = RmuixIcons.Regular.Brightness

val RmuixIcons.Default.Brightness: ImageVector
    get() = RmuixIcons.Regular.Brightness

val RmuixIcons.Filled.Brightness: ImageVector
    get() = RmuixIcons.Regular.Brightness

val RmuixIcons.Brightness: ImageVector
    get() = RmuixIcons.Regular.Brightness

val RmuixIcons.Light.Brightness: ImageVector
    get() {
        if (_brightnessLight != null) return _brightnessLight!!
        _brightnessLight = ImageVector.Builder(
            name = "Brightness.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 7.0f),
                        PathNode.CurveTo(9.24f, 7.0f, 7.0f, 9.24f, 7.0f, 12.0f),
                        PathNode.CurveTo(7.0f, 14.76f, 9.24f, 17.0f, 12.0f, 17.0f),
                        PathNode.CurveTo(14.76f, 17.0f, 17.0f, 14.76f, 17.0f, 12.0f),
                        PathNode.CurveTo(17.0f, 9.24f, 14.76f, 7.0f, 12.0f, 7.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(12.0f, 4.0f),
                        PathNode.MoveTo(12.0f, 20.0f),
                        PathNode.LineTo(12.0f, 22.0f),
                        PathNode.MoveTo(2.0f, 12.0f),
                        PathNode.LineTo(4.0f, 12.0f),
                        PathNode.MoveTo(20.0f, 12.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                        PathNode.MoveTo(4.93f, 4.93f),
                        PathNode.LineTo(6.34f, 6.34f),
                        PathNode.MoveTo(17.66f, 17.66f),
                        PathNode.LineTo(19.07f, 19.07f),
                        PathNode.MoveTo(4.93f, 19.07f),
                        PathNode.LineTo(6.34f, 17.66f),
                        PathNode.MoveTo(17.66f, 6.34f),
                        PathNode.LineTo(19.07f, 4.93f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _brightnessLight!!
    }
private var _brightnessLight: ImageVector? = null

val RmuixIcons.Normal.Brightness: ImageVector
    get() {
        if (_brightnessNormal != null) return _brightnessNormal!!
        _brightnessNormal = ImageVector.Builder(
            name = "Brightness.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 7.0f),
                        PathNode.CurveTo(9.24f, 7.0f, 7.0f, 9.24f, 7.0f, 12.0f),
                        PathNode.CurveTo(7.0f, 14.76f, 9.24f, 17.0f, 12.0f, 17.0f),
                        PathNode.CurveTo(14.76f, 17.0f, 17.0f, 14.76f, 17.0f, 12.0f),
                        PathNode.CurveTo(17.0f, 9.24f, 14.76f, 7.0f, 12.0f, 7.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(12.0f, 4.0f),
                        PathNode.MoveTo(12.0f, 20.0f),
                        PathNode.LineTo(12.0f, 22.0f),
                        PathNode.MoveTo(2.0f, 12.0f),
                        PathNode.LineTo(4.0f, 12.0f),
                        PathNode.MoveTo(20.0f, 12.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                        PathNode.MoveTo(4.93f, 4.93f),
                        PathNode.LineTo(6.34f, 6.34f),
                        PathNode.MoveTo(17.66f, 17.66f),
                        PathNode.LineTo(19.07f, 19.07f),
                        PathNode.MoveTo(4.93f, 19.07f),
                        PathNode.LineTo(6.34f, 17.66f),
                        PathNode.MoveTo(17.66f, 6.34f),
                        PathNode.LineTo(19.07f, 4.93f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _brightnessNormal!!
    }
private var _brightnessNormal: ImageVector? = null

val RmuixIcons.Regular.Brightness: ImageVector
    get() {
        if (_brightnessRegular != null) return _brightnessRegular!!
        _brightnessRegular = ImageVector.Builder(
            name = "Brightness.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 7.0f),
                        PathNode.CurveTo(9.24f, 7.0f, 7.0f, 9.24f, 7.0f, 12.0f),
                        PathNode.CurveTo(7.0f, 14.76f, 9.24f, 17.0f, 12.0f, 17.0f),
                        PathNode.CurveTo(14.76f, 17.0f, 17.0f, 14.76f, 17.0f, 12.0f),
                        PathNode.CurveTo(17.0f, 9.24f, 14.76f, 7.0f, 12.0f, 7.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(12.0f, 4.0f),
                        PathNode.MoveTo(12.0f, 20.0f),
                        PathNode.LineTo(12.0f, 22.0f),
                        PathNode.MoveTo(2.0f, 12.0f),
                        PathNode.LineTo(4.0f, 12.0f),
                        PathNode.MoveTo(20.0f, 12.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                        PathNode.MoveTo(4.93f, 4.93f),
                        PathNode.LineTo(6.34f, 6.34f),
                        PathNode.MoveTo(17.66f, 17.66f),
                        PathNode.LineTo(19.07f, 19.07f),
                        PathNode.MoveTo(4.93f, 19.07f),
                        PathNode.LineTo(6.34f, 17.66f),
                        PathNode.MoveTo(17.66f, 6.34f),
                        PathNode.LineTo(19.07f, 4.93f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _brightnessRegular!!
    }
private var _brightnessRegular: ImageVector? = null

val RmuixIcons.Medium.Brightness: ImageVector
    get() {
        if (_brightnessMedium != null) return _brightnessMedium!!
        _brightnessMedium = ImageVector.Builder(
            name = "Brightness.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 7.0f),
                        PathNode.CurveTo(9.24f, 7.0f, 7.0f, 9.24f, 7.0f, 12.0f),
                        PathNode.CurveTo(7.0f, 14.76f, 9.24f, 17.0f, 12.0f, 17.0f),
                        PathNode.CurveTo(14.76f, 17.0f, 17.0f, 14.76f, 17.0f, 12.0f),
                        PathNode.CurveTo(17.0f, 9.24f, 14.76f, 7.0f, 12.0f, 7.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(12.0f, 4.0f),
                        PathNode.MoveTo(12.0f, 20.0f),
                        PathNode.LineTo(12.0f, 22.0f),
                        PathNode.MoveTo(2.0f, 12.0f),
                        PathNode.LineTo(4.0f, 12.0f),
                        PathNode.MoveTo(20.0f, 12.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                        PathNode.MoveTo(4.93f, 4.93f),
                        PathNode.LineTo(6.34f, 6.34f),
                        PathNode.MoveTo(17.66f, 17.66f),
                        PathNode.LineTo(19.07f, 19.07f),
                        PathNode.MoveTo(4.93f, 19.07f),
                        PathNode.LineTo(6.34f, 17.66f),
                        PathNode.MoveTo(17.66f, 6.34f),
                        PathNode.LineTo(19.07f, 4.93f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _brightnessMedium!!
    }
private var _brightnessMedium: ImageVector? = null

val RmuixIcons.Demibold.Brightness: ImageVector
    get() {
        if (_brightnessDemibold != null) return _brightnessDemibold!!
        _brightnessDemibold = ImageVector.Builder(
            name = "Brightness.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 7.0f),
                        PathNode.CurveTo(9.24f, 7.0f, 7.0f, 9.24f, 7.0f, 12.0f),
                        PathNode.CurveTo(7.0f, 14.76f, 9.24f, 17.0f, 12.0f, 17.0f),
                        PathNode.CurveTo(14.76f, 17.0f, 17.0f, 14.76f, 17.0f, 12.0f),
                        PathNode.CurveTo(17.0f, 9.24f, 14.76f, 7.0f, 12.0f, 7.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(12.0f, 4.0f),
                        PathNode.MoveTo(12.0f, 20.0f),
                        PathNode.LineTo(12.0f, 22.0f),
                        PathNode.MoveTo(2.0f, 12.0f),
                        PathNode.LineTo(4.0f, 12.0f),
                        PathNode.MoveTo(20.0f, 12.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                        PathNode.MoveTo(4.93f, 4.93f),
                        PathNode.LineTo(6.34f, 6.34f),
                        PathNode.MoveTo(17.66f, 17.66f),
                        PathNode.LineTo(19.07f, 19.07f),
                        PathNode.MoveTo(4.93f, 19.07f),
                        PathNode.LineTo(6.34f, 17.66f),
                        PathNode.MoveTo(17.66f, 6.34f),
                        PathNode.LineTo(19.07f, 4.93f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _brightnessDemibold!!
    }
private var _brightnessDemibold: ImageVector? = null
