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

val RmuixIcons.Outline.Cellular: ImageVector
    get() = RmuixIcons.Regular.Cellular

val RmuixIcons.Default.Cellular: ImageVector
    get() = RmuixIcons.Regular.Cellular

val RmuixIcons.Filled.Cellular: ImageVector
    get() = RmuixIcons.Regular.Cellular

val RmuixIcons.Cellular: ImageVector
    get() = RmuixIcons.Regular.Cellular

val RmuixIcons.Light.Cellular: ImageVector
    get() {
        if (_cellularLight != null) return _cellularLight!!
        _cellularLight = ImageVector.Builder(
            name = "Cellular.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(3.0f, 20.0f),
                        PathNode.LineTo(3.0f, 16.0f),
                        PathNode.LineTo(5.5f, 16.0f),
                        PathNode.LineTo(5.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(8.0f, 20.0f),
                        PathNode.LineTo(8.0f, 12.0f),
                        PathNode.LineTo(10.5f, 12.0f),
                        PathNode.LineTo(10.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(13.0f, 20.0f),
                        PathNode.LineTo(13.0f, 8.0f),
                        PathNode.LineTo(15.5f, 8.0f),
                        PathNode.LineTo(15.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(18.0f, 20.0f),
                        PathNode.LineTo(18.0f, 4.0f),
                        PathNode.LineTo(20.5f, 4.0f),
                        PathNode.LineTo(20.5f, 20.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _cellularLight!!
    }
private var _cellularLight: ImageVector? = null

val RmuixIcons.Normal.Cellular: ImageVector
    get() {
        if (_cellularNormal != null) return _cellularNormal!!
        _cellularNormal = ImageVector.Builder(
            name = "Cellular.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(3.0f, 20.0f),
                        PathNode.LineTo(3.0f, 16.0f),
                        PathNode.LineTo(5.5f, 16.0f),
                        PathNode.LineTo(5.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(8.0f, 20.0f),
                        PathNode.LineTo(8.0f, 12.0f),
                        PathNode.LineTo(10.5f, 12.0f),
                        PathNode.LineTo(10.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(13.0f, 20.0f),
                        PathNode.LineTo(13.0f, 8.0f),
                        PathNode.LineTo(15.5f, 8.0f),
                        PathNode.LineTo(15.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(18.0f, 20.0f),
                        PathNode.LineTo(18.0f, 4.0f),
                        PathNode.LineTo(20.5f, 4.0f),
                        PathNode.LineTo(20.5f, 20.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _cellularNormal!!
    }
private var _cellularNormal: ImageVector? = null

val RmuixIcons.Regular.Cellular: ImageVector
    get() {
        if (_cellularRegular != null) return _cellularRegular!!
        _cellularRegular = ImageVector.Builder(
            name = "Cellular.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(3.0f, 20.0f),
                        PathNode.LineTo(3.0f, 16.0f),
                        PathNode.LineTo(5.5f, 16.0f),
                        PathNode.LineTo(5.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(8.0f, 20.0f),
                        PathNode.LineTo(8.0f, 12.0f),
                        PathNode.LineTo(10.5f, 12.0f),
                        PathNode.LineTo(10.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(13.0f, 20.0f),
                        PathNode.LineTo(13.0f, 8.0f),
                        PathNode.LineTo(15.5f, 8.0f),
                        PathNode.LineTo(15.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(18.0f, 20.0f),
                        PathNode.LineTo(18.0f, 4.0f),
                        PathNode.LineTo(20.5f, 4.0f),
                        PathNode.LineTo(20.5f, 20.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _cellularRegular!!
    }
private var _cellularRegular: ImageVector? = null

val RmuixIcons.Medium.Cellular: ImageVector
    get() {
        if (_cellularMedium != null) return _cellularMedium!!
        _cellularMedium = ImageVector.Builder(
            name = "Cellular.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(3.0f, 20.0f),
                        PathNode.LineTo(3.0f, 16.0f),
                        PathNode.LineTo(5.5f, 16.0f),
                        PathNode.LineTo(5.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(8.0f, 20.0f),
                        PathNode.LineTo(8.0f, 12.0f),
                        PathNode.LineTo(10.5f, 12.0f),
                        PathNode.LineTo(10.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(13.0f, 20.0f),
                        PathNode.LineTo(13.0f, 8.0f),
                        PathNode.LineTo(15.5f, 8.0f),
                        PathNode.LineTo(15.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(18.0f, 20.0f),
                        PathNode.LineTo(18.0f, 4.0f),
                        PathNode.LineTo(20.5f, 4.0f),
                        PathNode.LineTo(20.5f, 20.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _cellularMedium!!
    }
private var _cellularMedium: ImageVector? = null

val RmuixIcons.Demibold.Cellular: ImageVector
    get() {
        if (_cellularDemibold != null) return _cellularDemibold!!
        _cellularDemibold = ImageVector.Builder(
            name = "Cellular.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(3.0f, 20.0f),
                        PathNode.LineTo(3.0f, 16.0f),
                        PathNode.LineTo(5.5f, 16.0f),
                        PathNode.LineTo(5.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(8.0f, 20.0f),
                        PathNode.LineTo(8.0f, 12.0f),
                        PathNode.LineTo(10.5f, 12.0f),
                        PathNode.LineTo(10.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(13.0f, 20.0f),
                        PathNode.LineTo(13.0f, 8.0f),
                        PathNode.LineTo(15.5f, 8.0f),
                        PathNode.LineTo(15.5f, 20.0f),
                        PathNode.Close,
                        PathNode.MoveTo(18.0f, 20.0f),
                        PathNode.LineTo(18.0f, 4.0f),
                        PathNode.LineTo(20.5f, 4.0f),
                        PathNode.LineTo(20.5f, 20.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _cellularDemibold!!
    }
private var _cellularDemibold: ImageVector? = null
