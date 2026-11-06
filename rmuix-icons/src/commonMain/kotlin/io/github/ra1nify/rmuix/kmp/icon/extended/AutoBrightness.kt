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

val RmuixIcons.Outline.AutoBrightness: ImageVector
    get() = RmuixIcons.Regular.AutoBrightness

val RmuixIcons.Default.AutoBrightness: ImageVector
    get() = RmuixIcons.Regular.AutoBrightness

val RmuixIcons.Filled.AutoBrightness: ImageVector
    get() = RmuixIcons.Regular.AutoBrightness

val RmuixIcons.AutoBrightness: ImageVector
    get() = RmuixIcons.Regular.AutoBrightness

val RmuixIcons.Light.AutoBrightness: ImageVector
    get() {
        if (_autoBrightnessLight != null) return _autoBrightnessLight!!
        _autoBrightnessLight = ImageVector.Builder(
            name = "AutoBrightness.Light",
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
                        PathNode.MoveTo(12.0f, 9.0f),
                        PathNode.LineTo(9.5f, 15.0f),
                        PathNode.HorizontalTo(11.0f),
                        PathNode.LineTo(11.5f, 13.5f),
                        PathNode.HorizontalTo(12.5f),
                        PathNode.LineTo(13.0f, 15.0f),
                        PathNode.HorizontalTo(14.5f),
                        PathNode.LineTo(12.0f, 9.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(12.0f, 4.0f),
                        PathNode.MoveTo(12.0f, 20.0f),
                        PathNode.LineTo(12.0f, 22.0f),
                        PathNode.MoveTo(2.0f, 12.0f),
                        PathNode.LineTo(4.0f, 12.0f),
                        PathNode.MoveTo(20.0f, 12.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _autoBrightnessLight!!
    }
private var _autoBrightnessLight: ImageVector? = null

val RmuixIcons.Normal.AutoBrightness: ImageVector
    get() {
        if (_autoBrightnessNormal != null) return _autoBrightnessNormal!!
        _autoBrightnessNormal = ImageVector.Builder(
            name = "AutoBrightness.Normal",
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
                        PathNode.MoveTo(12.0f, 9.0f),
                        PathNode.LineTo(9.5f, 15.0f),
                        PathNode.HorizontalTo(11.0f),
                        PathNode.LineTo(11.5f, 13.5f),
                        PathNode.HorizontalTo(12.5f),
                        PathNode.LineTo(13.0f, 15.0f),
                        PathNode.HorizontalTo(14.5f),
                        PathNode.LineTo(12.0f, 9.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(12.0f, 4.0f),
                        PathNode.MoveTo(12.0f, 20.0f),
                        PathNode.LineTo(12.0f, 22.0f),
                        PathNode.MoveTo(2.0f, 12.0f),
                        PathNode.LineTo(4.0f, 12.0f),
                        PathNode.MoveTo(20.0f, 12.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _autoBrightnessNormal!!
    }
private var _autoBrightnessNormal: ImageVector? = null

val RmuixIcons.Regular.AutoBrightness: ImageVector
    get() {
        if (_autoBrightnessRegular != null) return _autoBrightnessRegular!!
        _autoBrightnessRegular = ImageVector.Builder(
            name = "AutoBrightness.Regular",
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
                        PathNode.MoveTo(12.0f, 9.0f),
                        PathNode.LineTo(9.5f, 15.0f),
                        PathNode.HorizontalTo(11.0f),
                        PathNode.LineTo(11.5f, 13.5f),
                        PathNode.HorizontalTo(12.5f),
                        PathNode.LineTo(13.0f, 15.0f),
                        PathNode.HorizontalTo(14.5f),
                        PathNode.LineTo(12.0f, 9.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(12.0f, 4.0f),
                        PathNode.MoveTo(12.0f, 20.0f),
                        PathNode.LineTo(12.0f, 22.0f),
                        PathNode.MoveTo(2.0f, 12.0f),
                        PathNode.LineTo(4.0f, 12.0f),
                        PathNode.MoveTo(20.0f, 12.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _autoBrightnessRegular!!
    }
private var _autoBrightnessRegular: ImageVector? = null

val RmuixIcons.Medium.AutoBrightness: ImageVector
    get() {
        if (_autoBrightnessMedium != null) return _autoBrightnessMedium!!
        _autoBrightnessMedium = ImageVector.Builder(
            name = "AutoBrightness.Medium",
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
                        PathNode.MoveTo(12.0f, 9.0f),
                        PathNode.LineTo(9.5f, 15.0f),
                        PathNode.HorizontalTo(11.0f),
                        PathNode.LineTo(11.5f, 13.5f),
                        PathNode.HorizontalTo(12.5f),
                        PathNode.LineTo(13.0f, 15.0f),
                        PathNode.HorizontalTo(14.5f),
                        PathNode.LineTo(12.0f, 9.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(12.0f, 4.0f),
                        PathNode.MoveTo(12.0f, 20.0f),
                        PathNode.LineTo(12.0f, 22.0f),
                        PathNode.MoveTo(2.0f, 12.0f),
                        PathNode.LineTo(4.0f, 12.0f),
                        PathNode.MoveTo(20.0f, 12.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _autoBrightnessMedium!!
    }
private var _autoBrightnessMedium: ImageVector? = null

val RmuixIcons.Demibold.AutoBrightness: ImageVector
    get() {
        if (_autoBrightnessDemibold != null) return _autoBrightnessDemibold!!
        _autoBrightnessDemibold = ImageVector.Builder(
            name = "AutoBrightness.Demibold",
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
                        PathNode.MoveTo(12.0f, 9.0f),
                        PathNode.LineTo(9.5f, 15.0f),
                        PathNode.HorizontalTo(11.0f),
                        PathNode.LineTo(11.5f, 13.5f),
                        PathNode.HorizontalTo(12.5f),
                        PathNode.LineTo(13.0f, 15.0f),
                        PathNode.HorizontalTo(14.5f),
                        PathNode.LineTo(12.0f, 9.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(12.0f, 4.0f),
                        PathNode.MoveTo(12.0f, 20.0f),
                        PathNode.LineTo(12.0f, 22.0f),
                        PathNode.MoveTo(2.0f, 12.0f),
                        PathNode.LineTo(4.0f, 12.0f),
                        PathNode.MoveTo(20.0f, 12.0f),
                        PathNode.LineTo(22.0f, 12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _autoBrightnessDemibold!!
    }
private var _autoBrightnessDemibold: ImageVector? = null
