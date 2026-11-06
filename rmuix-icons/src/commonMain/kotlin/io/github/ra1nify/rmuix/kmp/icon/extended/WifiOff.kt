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

val RmuixIcons.Outline.WifiOff: ImageVector
    get() = RmuixIcons.Regular.WifiOff

val RmuixIcons.Default.WifiOff: ImageVector
    get() = RmuixIcons.Regular.WifiOff

val RmuixIcons.Filled.WifiOff: ImageVector
    get() = RmuixIcons.Regular.WifiOff

val RmuixIcons.WifiOff: ImageVector
    get() = RmuixIcons.Regular.WifiOff

val RmuixIcons.Light.WifiOff: ImageVector
    get() {
        if (_wifiOffLight != null) return _wifiOffLight!!
        _wifiOffLight = ImageVector.Builder(
            name = "WifiOff.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(3.0f, 8.0f),
                        PathNode.CurveTo(8.0f, 3.0f, 16.0f, 3.0f, 21.0f, 8.0f),
                        PathNode.MoveTo(6.0f, 12.0f),
                        PathNode.CurveTo(9.5f, 8.5f, 14.5f, 8.5f, 18.0f, 12.0f),
                        PathNode.MoveTo(9.0f, 16.0f),
                        PathNode.CurveTo(10.8f, 14.2f, 13.2f, 14.2f, 15.0f, 16.0f),
                        PathNode.MoveTo(3.0f, 3.0f),
                        PathNode.LineTo(21.0f, 21.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _wifiOffLight!!
    }
private var _wifiOffLight: ImageVector? = null

val RmuixIcons.Normal.WifiOff: ImageVector
    get() {
        if (_wifiOffNormal != null) return _wifiOffNormal!!
        _wifiOffNormal = ImageVector.Builder(
            name = "WifiOff.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(3.0f, 8.0f),
                        PathNode.CurveTo(8.0f, 3.0f, 16.0f, 3.0f, 21.0f, 8.0f),
                        PathNode.MoveTo(6.0f, 12.0f),
                        PathNode.CurveTo(9.5f, 8.5f, 14.5f, 8.5f, 18.0f, 12.0f),
                        PathNode.MoveTo(9.0f, 16.0f),
                        PathNode.CurveTo(10.8f, 14.2f, 13.2f, 14.2f, 15.0f, 16.0f),
                        PathNode.MoveTo(3.0f, 3.0f),
                        PathNode.LineTo(21.0f, 21.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _wifiOffNormal!!
    }
private var _wifiOffNormal: ImageVector? = null

val RmuixIcons.Regular.WifiOff: ImageVector
    get() {
        if (_wifiOffRegular != null) return _wifiOffRegular!!
        _wifiOffRegular = ImageVector.Builder(
            name = "WifiOff.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(3.0f, 8.0f),
                        PathNode.CurveTo(8.0f, 3.0f, 16.0f, 3.0f, 21.0f, 8.0f),
                        PathNode.MoveTo(6.0f, 12.0f),
                        PathNode.CurveTo(9.5f, 8.5f, 14.5f, 8.5f, 18.0f, 12.0f),
                        PathNode.MoveTo(9.0f, 16.0f),
                        PathNode.CurveTo(10.8f, 14.2f, 13.2f, 14.2f, 15.0f, 16.0f),
                        PathNode.MoveTo(3.0f, 3.0f),
                        PathNode.LineTo(21.0f, 21.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _wifiOffRegular!!
    }
private var _wifiOffRegular: ImageVector? = null

val RmuixIcons.Medium.WifiOff: ImageVector
    get() {
        if (_wifiOffMedium != null) return _wifiOffMedium!!
        _wifiOffMedium = ImageVector.Builder(
            name = "WifiOff.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(3.0f, 8.0f),
                        PathNode.CurveTo(8.0f, 3.0f, 16.0f, 3.0f, 21.0f, 8.0f),
                        PathNode.MoveTo(6.0f, 12.0f),
                        PathNode.CurveTo(9.5f, 8.5f, 14.5f, 8.5f, 18.0f, 12.0f),
                        PathNode.MoveTo(9.0f, 16.0f),
                        PathNode.CurveTo(10.8f, 14.2f, 13.2f, 14.2f, 15.0f, 16.0f),
                        PathNode.MoveTo(3.0f, 3.0f),
                        PathNode.LineTo(21.0f, 21.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _wifiOffMedium!!
    }
private var _wifiOffMedium: ImageVector? = null

val RmuixIcons.Demibold.WifiOff: ImageVector
    get() {
        if (_wifiOffDemibold != null) return _wifiOffDemibold!!
        _wifiOffDemibold = ImageVector.Builder(
            name = "WifiOff.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(3.0f, 8.0f),
                        PathNode.CurveTo(8.0f, 3.0f, 16.0f, 3.0f, 21.0f, 8.0f),
                        PathNode.MoveTo(6.0f, 12.0f),
                        PathNode.CurveTo(9.5f, 8.5f, 14.5f, 8.5f, 18.0f, 12.0f),
                        PathNode.MoveTo(9.0f, 16.0f),
                        PathNode.CurveTo(10.8f, 14.2f, 13.2f, 14.2f, 15.0f, 16.0f),
                        PathNode.MoveTo(3.0f, 3.0f),
                        PathNode.LineTo(21.0f, 21.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _wifiOffDemibold!!
    }
private var _wifiOffDemibold: ImageVector? = null
