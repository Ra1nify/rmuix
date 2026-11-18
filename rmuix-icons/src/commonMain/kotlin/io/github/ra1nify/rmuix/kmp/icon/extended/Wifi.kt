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

val RmuixIcons.Outline.Wifi: ImageVector
    get() = RmuixIcons.Regular.Wifi

val RmuixIcons.Default.Wifi: ImageVector
    get() = RmuixIcons.Regular.Wifi

val RmuixIcons.Filled.Wifi: ImageVector
    get() = RmuixIcons.Regular.Wifi

val RmuixIcons.Wifi: ImageVector
    get() = RmuixIcons.Regular.Wifi

val RmuixIcons.Light.Wifi: ImageVector
    get() {
        if (_wifiLight != null) return _wifiLight!!
        _wifiLight = ImageVector.Builder(
            name = "Wifi.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(2.88f, 8.00f),
                        PathNode.CurveTo(8.0f, 3.0f, 16.0f, 3.0f, 21.12f, 8.00f),
                        PathNode.MoveTo(5.88f, 12.00f),
                        PathNode.CurveTo(9.5f, 8.5f, 14.5f, 8.5f, 18.12f, 12.00f),
                        PathNode.MoveTo(8.88f, 16.00f),
                        PathNode.CurveTo(10.8f, 14.2f, 13.2f, 14.2f, 15.12f, 16.00f),
                        PathNode.MoveTo(11.64f, 19.5f),
                        PathNode.CurveTo(11.64f, 18.68f, 12.92f, 18.0f, 12.0f, 18.0f),
                        PathNode.CurveTo(11.08f, 18.0f, 11.64f, 18.68f, 11.64f, 19.5f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _wifiLight!!
    }
private var _wifiLight: ImageVector? = null

val RmuixIcons.Normal.Wifi: ImageVector
    get() {
        if (_wifiNormal != null) return _wifiNormal!!
        _wifiNormal = ImageVector.Builder(
            name = "Wifi.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(2.84f, 8.00f),
                        PathNode.CurveTo(8.0f, 3.0f, 16.0f, 3.0f, 21.16f, 8.00f),
                        PathNode.MoveTo(5.84f, 12.00f),
                        PathNode.CurveTo(9.5f, 8.5f, 14.5f, 8.5f, 18.16f, 12.00f),
                        PathNode.MoveTo(8.84f, 16.00f),
                        PathNode.CurveTo(10.8f, 14.2f, 13.2f, 14.2f, 15.16f, 16.00f),
                        PathNode.MoveTo(11.52f, 19.5f),
                        PathNode.CurveTo(11.52f, 18.64f, 12.96f, 18.0f, 12.0f, 18.0f),
                        PathNode.CurveTo(11.04f, 18.0f, 11.52f, 18.64f, 11.52f, 19.5f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _wifiNormal!!
    }
private var _wifiNormal: ImageVector? = null

val RmuixIcons.Regular.Wifi: ImageVector
    get() {
        if (_wifiRegular != null) return _wifiRegular!!
        _wifiRegular = ImageVector.Builder(
            name = "Wifi.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(2.80f, 8.00f),
                        PathNode.CurveTo(8.0f, 3.0f, 16.0f, 3.0f, 21.20f, 8.00f),
                        PathNode.MoveTo(5.80f, 12.00f),
                        PathNode.CurveTo(9.5f, 8.5f, 14.5f, 8.5f, 18.20f, 12.00f),
                        PathNode.MoveTo(8.80f, 16.00f),
                        PathNode.CurveTo(10.8f, 14.2f, 13.2f, 14.2f, 15.20f, 16.00f),
                        PathNode.MoveTo(11.40f, 19.5f),
                        PathNode.CurveTo(11.40f, 18.60f, 13.00f, 18.0f, 12.0f, 18.0f),
                        PathNode.CurveTo(11.00f, 18.0f, 11.40f, 18.60f, 11.40f, 19.5f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _wifiRegular!!
    }
private var _wifiRegular: ImageVector? = null

val RmuixIcons.Medium.Wifi: ImageVector
    get() {
        if (_wifiMedium != null) return _wifiMedium!!
        _wifiMedium = ImageVector.Builder(
            name = "Wifi.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(2.76f, 8.00f),
                        PathNode.CurveTo(8.0f, 3.0f, 16.0f, 3.0f, 21.24f, 8.00f),
                        PathNode.MoveTo(5.76f, 12.00f),
                        PathNode.CurveTo(9.5f, 8.5f, 14.5f, 8.5f, 18.24f, 12.00f),
                        PathNode.MoveTo(8.76f, 16.00f),
                        PathNode.CurveTo(10.8f, 14.2f, 13.2f, 14.2f, 15.24f, 16.00f),
                        PathNode.MoveTo(11.28f, 19.5f),
                        PathNode.CurveTo(11.28f, 18.56f, 13.04f, 18.0f, 12.0f, 18.0f),
                        PathNode.CurveTo(10.96f, 18.0f, 11.28f, 18.56f, 11.28f, 19.5f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _wifiMedium!!
    }
private var _wifiMedium: ImageVector? = null

val RmuixIcons.Demibold.Wifi: ImageVector
    get() {
        if (_wifiDemibold != null) return _wifiDemibold!!
        _wifiDemibold = ImageVector.Builder(
            name = "Wifi.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(2.72f, 8.00f),
                        PathNode.CurveTo(8.0f, 3.0f, 16.0f, 3.0f, 21.28f, 8.00f),
                        PathNode.MoveTo(5.72f, 12.00f),
                        PathNode.CurveTo(9.5f, 8.5f, 14.5f, 8.5f, 18.28f, 12.00f),
                        PathNode.MoveTo(8.72f, 16.00f),
                        PathNode.CurveTo(10.8f, 14.2f, 13.2f, 14.2f, 15.28f, 16.00f),
                        PathNode.MoveTo(11.16f, 19.5f),
                        PathNode.CurveTo(11.16f, 18.52f, 13.08f, 18.0f, 12.0f, 18.0f),
                        PathNode.CurveTo(10.92f, 18.0f, 11.16f, 18.52f, 11.16f, 19.5f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _wifiDemibold!!
    }
private var _wifiDemibold: ImageVector? = null
