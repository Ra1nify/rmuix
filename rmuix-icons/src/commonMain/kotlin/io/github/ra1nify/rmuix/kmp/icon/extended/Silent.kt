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

val RmuixIcons.Outline.Silent: ImageVector
    get() = RmuixIcons.Regular.Silent

val RmuixIcons.Default.Silent: ImageVector
    get() = RmuixIcons.Regular.Silent

val RmuixIcons.Filled.Silent: ImageVector
    get() = RmuixIcons.Regular.Silent

val RmuixIcons.Silent: ImageVector
    get() = RmuixIcons.Regular.Silent

val RmuixIcons.Light.Silent: ImageVector
    get() {
        if (_silentLight != null) return _silentLight!!
        _silentLight = ImageVector.Builder(
            name = "Silent.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 22.0f),
                        PathNode.CurveTo(13.1f, 22.0f, 14.0f, 21.1f, 14.0f, 20.0f),
                        PathNode.HorizontalTo(10.0f),
                        PathNode.CurveTo(10.0f, 21.1f, 10.9f, 22.0f, 12.0f, 22.0f),
                        PathNode.Close,
                        PathNode.MoveTo(18.0f, 16.0f),
                        PathNode.VerticalTo(11.0f),
                        PathNode.CurveTo(18.0f, 7.93f, 15.36f, 5.36f, 12.3f, 5.06f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.CurveTo(12.3f, 3.83f, 12.17f, 3.7f, 12.0f, 3.7f),
                        PathNode.CurveTo(11.83f, 3.7f, 11.7f, 3.83f, 11.7f, 4.0f),
                        PathNode.VerticalTo(5.06f),
                        PathNode.CurveTo(8.64f, 5.36f, 6.0f, 7.93f, 6.0f, 11.0f),
                        PathNode.VerticalTo(16.0f),
                        PathNode.LineTo(4.0f, 18.0f),
                        PathNode.VerticalTo(19.0f),
                        PathNode.HorizontalTo(20.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.LineTo(18.0f, 16.0f),
                        PathNode.Close,
                        PathNode.MoveTo(3.0f, 3.0f),
                        PathNode.LineTo(21.0f, 21.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _silentLight!!
    }
private var _silentLight: ImageVector? = null

val RmuixIcons.Normal.Silent: ImageVector
    get() {
        if (_silentNormal != null) return _silentNormal!!
        _silentNormal = ImageVector.Builder(
            name = "Silent.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 22.0f),
                        PathNode.CurveTo(13.1f, 22.0f, 14.0f, 21.1f, 14.0f, 20.0f),
                        PathNode.HorizontalTo(10.0f),
                        PathNode.CurveTo(10.0f, 21.1f, 10.9f, 22.0f, 12.0f, 22.0f),
                        PathNode.Close,
                        PathNode.MoveTo(18.0f, 16.0f),
                        PathNode.VerticalTo(11.0f),
                        PathNode.CurveTo(18.0f, 7.93f, 15.36f, 5.36f, 12.3f, 5.06f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.CurveTo(12.3f, 3.83f, 12.17f, 3.7f, 12.0f, 3.7f),
                        PathNode.CurveTo(11.83f, 3.7f, 11.7f, 3.83f, 11.7f, 4.0f),
                        PathNode.VerticalTo(5.06f),
                        PathNode.CurveTo(8.64f, 5.36f, 6.0f, 7.93f, 6.0f, 11.0f),
                        PathNode.VerticalTo(16.0f),
                        PathNode.LineTo(4.0f, 18.0f),
                        PathNode.VerticalTo(19.0f),
                        PathNode.HorizontalTo(20.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.LineTo(18.0f, 16.0f),
                        PathNode.Close,
                        PathNode.MoveTo(3.0f, 3.0f),
                        PathNode.LineTo(21.0f, 21.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _silentNormal!!
    }
private var _silentNormal: ImageVector? = null

val RmuixIcons.Regular.Silent: ImageVector
    get() {
        if (_silentRegular != null) return _silentRegular!!
        _silentRegular = ImageVector.Builder(
            name = "Silent.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 22.0f),
                        PathNode.CurveTo(13.1f, 22.0f, 14.0f, 21.1f, 14.0f, 20.0f),
                        PathNode.HorizontalTo(10.0f),
                        PathNode.CurveTo(10.0f, 21.1f, 10.9f, 22.0f, 12.0f, 22.0f),
                        PathNode.Close,
                        PathNode.MoveTo(18.0f, 16.0f),
                        PathNode.VerticalTo(11.0f),
                        PathNode.CurveTo(18.0f, 7.93f, 15.36f, 5.36f, 12.3f, 5.06f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.CurveTo(12.3f, 3.83f, 12.17f, 3.7f, 12.0f, 3.7f),
                        PathNode.CurveTo(11.83f, 3.7f, 11.7f, 3.83f, 11.7f, 4.0f),
                        PathNode.VerticalTo(5.06f),
                        PathNode.CurveTo(8.64f, 5.36f, 6.0f, 7.93f, 6.0f, 11.0f),
                        PathNode.VerticalTo(16.0f),
                        PathNode.LineTo(4.0f, 18.0f),
                        PathNode.VerticalTo(19.0f),
                        PathNode.HorizontalTo(20.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.LineTo(18.0f, 16.0f),
                        PathNode.Close,
                        PathNode.MoveTo(3.0f, 3.0f),
                        PathNode.LineTo(21.0f, 21.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _silentRegular!!
    }
private var _silentRegular: ImageVector? = null

val RmuixIcons.Medium.Silent: ImageVector
    get() {
        if (_silentMedium != null) return _silentMedium!!
        _silentMedium = ImageVector.Builder(
            name = "Silent.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 22.0f),
                        PathNode.CurveTo(13.1f, 22.0f, 14.0f, 21.1f, 14.0f, 20.0f),
                        PathNode.HorizontalTo(10.0f),
                        PathNode.CurveTo(10.0f, 21.1f, 10.9f, 22.0f, 12.0f, 22.0f),
                        PathNode.Close,
                        PathNode.MoveTo(18.0f, 16.0f),
                        PathNode.VerticalTo(11.0f),
                        PathNode.CurveTo(18.0f, 7.93f, 15.36f, 5.36f, 12.3f, 5.06f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.CurveTo(12.3f, 3.83f, 12.17f, 3.7f, 12.0f, 3.7f),
                        PathNode.CurveTo(11.83f, 3.7f, 11.7f, 3.83f, 11.7f, 4.0f),
                        PathNode.VerticalTo(5.06f),
                        PathNode.CurveTo(8.64f, 5.36f, 6.0f, 7.93f, 6.0f, 11.0f),
                        PathNode.VerticalTo(16.0f),
                        PathNode.LineTo(4.0f, 18.0f),
                        PathNode.VerticalTo(19.0f),
                        PathNode.HorizontalTo(20.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.LineTo(18.0f, 16.0f),
                        PathNode.Close,
                        PathNode.MoveTo(3.0f, 3.0f),
                        PathNode.LineTo(21.0f, 21.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _silentMedium!!
    }
private var _silentMedium: ImageVector? = null

val RmuixIcons.Demibold.Silent: ImageVector
    get() {
        if (_silentDemibold != null) return _silentDemibold!!
        _silentDemibold = ImageVector.Builder(
            name = "Silent.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 22.0f),
                        PathNode.CurveTo(13.1f, 22.0f, 14.0f, 21.1f, 14.0f, 20.0f),
                        PathNode.HorizontalTo(10.0f),
                        PathNode.CurveTo(10.0f, 21.1f, 10.9f, 22.0f, 12.0f, 22.0f),
                        PathNode.Close,
                        PathNode.MoveTo(18.0f, 16.0f),
                        PathNode.VerticalTo(11.0f),
                        PathNode.CurveTo(18.0f, 7.93f, 15.36f, 5.36f, 12.3f, 5.06f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.CurveTo(12.3f, 3.83f, 12.17f, 3.7f, 12.0f, 3.7f),
                        PathNode.CurveTo(11.83f, 3.7f, 11.7f, 3.83f, 11.7f, 4.0f),
                        PathNode.VerticalTo(5.06f),
                        PathNode.CurveTo(8.64f, 5.36f, 6.0f, 7.93f, 6.0f, 11.0f),
                        PathNode.VerticalTo(16.0f),
                        PathNode.LineTo(4.0f, 18.0f),
                        PathNode.VerticalTo(19.0f),
                        PathNode.HorizontalTo(20.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.LineTo(18.0f, 16.0f),
                        PathNode.Close,
                        PathNode.MoveTo(3.0f, 3.0f),
                        PathNode.LineTo(21.0f, 21.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _silentDemibold!!
    }
private var _silentDemibold: ImageVector? = null
