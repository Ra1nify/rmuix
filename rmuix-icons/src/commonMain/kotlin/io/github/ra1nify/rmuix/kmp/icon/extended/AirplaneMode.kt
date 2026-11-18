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

val RmuixIcons.Outline.AirplaneMode: ImageVector
    get() = RmuixIcons.Regular.AirplaneMode

val RmuixIcons.Default.AirplaneMode: ImageVector
    get() = RmuixIcons.Regular.AirplaneMode

val RmuixIcons.Filled.AirplaneMode: ImageVector
    get() = RmuixIcons.Regular.AirplaneMode

val RmuixIcons.AirplaneMode: ImageVector
    get() = RmuixIcons.Regular.AirplaneMode

val RmuixIcons.Light.AirplaneMode: ImageVector
    get() {
        if (_airplaneModeLight != null) return _airplaneModeLight!!
        _airplaneModeLight = ImageVector.Builder(
            name = "AirplaneMode.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(21.0f, 16.0f),
                        PathNode.LineTo(21.0f, 14.0f),
                        PathNode.LineTo(13.0f, 9.0f),
                        PathNode.LineTo(13.0f, 3.5f),
                        PathNode.CurveTo(13.0f, 2.67f, 12.33f, 2.0f, 11.5f, 2.0f),
                        PathNode.CurveTo(10.67f, 2.0f, 10.0f, 2.67f, 10.0f, 3.5f),
                        PathNode.LineTo(10.0f, 9.0f),
                        PathNode.LineTo(2.0f, 14.0f),
                        PathNode.LineTo(2.0f, 16.0f),
                        PathNode.LineTo(10.0f, 13.5f),
                        PathNode.LineTo(10.0f, 19.0f),
                        PathNode.LineTo(7.5f, 21.0f),
                        PathNode.LineTo(7.5f, 22.5f),
                        PathNode.LineTo(11.5f, 21.5f),
                        PathNode.LineTo(15.5f, 22.5f),
                        PathNode.LineTo(15.5f, 21.0f),
                        PathNode.LineTo(13.0f, 19.0f),
                        PathNode.LineTo(13.0f, 13.5f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _airplaneModeLight!!
    }
private var _airplaneModeLight: ImageVector? = null

val RmuixIcons.Normal.AirplaneMode: ImageVector
    get() {
        if (_airplaneModeNormal != null) return _airplaneModeNormal!!
        _airplaneModeNormal = ImageVector.Builder(
            name = "AirplaneMode.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(21.0f, 16.0f),
                        PathNode.LineTo(21.0f, 14.0f),
                        PathNode.LineTo(13.0f, 9.0f),
                        PathNode.LineTo(13.0f, 3.5f),
                        PathNode.CurveTo(13.0f, 2.67f, 12.33f, 2.0f, 11.5f, 2.0f),
                        PathNode.CurveTo(10.67f, 2.0f, 10.0f, 2.67f, 10.0f, 3.5f),
                        PathNode.LineTo(10.0f, 9.0f),
                        PathNode.LineTo(2.0f, 14.0f),
                        PathNode.LineTo(2.0f, 16.0f),
                        PathNode.LineTo(10.0f, 13.5f),
                        PathNode.LineTo(10.0f, 19.0f),
                        PathNode.LineTo(7.5f, 21.0f),
                        PathNode.LineTo(7.5f, 22.5f),
                        PathNode.LineTo(11.5f, 21.5f),
                        PathNode.LineTo(15.5f, 22.5f),
                        PathNode.LineTo(15.5f, 21.0f),
                        PathNode.LineTo(13.0f, 19.0f),
                        PathNode.LineTo(13.0f, 13.5f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _airplaneModeNormal!!
    }
private var _airplaneModeNormal: ImageVector? = null

val RmuixIcons.Regular.AirplaneMode: ImageVector
    get() {
        if (_airplaneModeRegular != null) return _airplaneModeRegular!!
        _airplaneModeRegular = ImageVector.Builder(
            name = "AirplaneMode.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(21.0f, 16.0f),
                        PathNode.LineTo(21.0f, 14.0f),
                        PathNode.LineTo(13.0f, 9.0f),
                        PathNode.LineTo(13.0f, 3.5f),
                        PathNode.CurveTo(13.0f, 2.67f, 12.33f, 2.0f, 11.5f, 2.0f),
                        PathNode.CurveTo(10.67f, 2.0f, 10.0f, 2.67f, 10.0f, 3.5f),
                        PathNode.LineTo(10.0f, 9.0f),
                        PathNode.LineTo(2.0f, 14.0f),
                        PathNode.LineTo(2.0f, 16.0f),
                        PathNode.LineTo(10.0f, 13.5f),
                        PathNode.LineTo(10.0f, 19.0f),
                        PathNode.LineTo(7.5f, 21.0f),
                        PathNode.LineTo(7.5f, 22.5f),
                        PathNode.LineTo(11.5f, 21.5f),
                        PathNode.LineTo(15.5f, 22.5f),
                        PathNode.LineTo(15.5f, 21.0f),
                        PathNode.LineTo(13.0f, 19.0f),
                        PathNode.LineTo(13.0f, 13.5f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _airplaneModeRegular!!
    }
private var _airplaneModeRegular: ImageVector? = null

val RmuixIcons.Medium.AirplaneMode: ImageVector
    get() {
        if (_airplaneModeMedium != null) return _airplaneModeMedium!!
        _airplaneModeMedium = ImageVector.Builder(
            name = "AirplaneMode.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(21.0f, 16.0f),
                        PathNode.LineTo(21.0f, 14.0f),
                        PathNode.LineTo(13.0f, 9.0f),
                        PathNode.LineTo(13.0f, 3.5f),
                        PathNode.CurveTo(13.0f, 2.67f, 12.33f, 2.0f, 11.5f, 2.0f),
                        PathNode.CurveTo(10.67f, 2.0f, 10.0f, 2.67f, 10.0f, 3.5f),
                        PathNode.LineTo(10.0f, 9.0f),
                        PathNode.LineTo(2.0f, 14.0f),
                        PathNode.LineTo(2.0f, 16.0f),
                        PathNode.LineTo(10.0f, 13.5f),
                        PathNode.LineTo(10.0f, 19.0f),
                        PathNode.LineTo(7.5f, 21.0f),
                        PathNode.LineTo(7.5f, 22.5f),
                        PathNode.LineTo(11.5f, 21.5f),
                        PathNode.LineTo(15.5f, 22.5f),
                        PathNode.LineTo(15.5f, 21.0f),
                        PathNode.LineTo(13.0f, 19.0f),
                        PathNode.LineTo(13.0f, 13.5f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _airplaneModeMedium!!
    }
private var _airplaneModeMedium: ImageVector? = null

val RmuixIcons.Demibold.AirplaneMode: ImageVector
    get() {
        if (_airplaneModeDemibold != null) return _airplaneModeDemibold!!
        _airplaneModeDemibold = ImageVector.Builder(
            name = "AirplaneMode.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(21.0f, 16.0f),
                        PathNode.LineTo(21.0f, 14.0f),
                        PathNode.LineTo(13.0f, 9.0f),
                        PathNode.LineTo(13.0f, 3.5f),
                        PathNode.CurveTo(13.0f, 2.67f, 12.33f, 2.0f, 11.5f, 2.0f),
                        PathNode.CurveTo(10.67f, 2.0f, 10.0f, 2.67f, 10.0f, 3.5f),
                        PathNode.LineTo(10.0f, 9.0f),
                        PathNode.LineTo(2.0f, 14.0f),
                        PathNode.LineTo(2.0f, 16.0f),
                        PathNode.LineTo(10.0f, 13.5f),
                        PathNode.LineTo(10.0f, 19.0f),
                        PathNode.LineTo(7.5f, 21.0f),
                        PathNode.LineTo(7.5f, 22.5f),
                        PathNode.LineTo(11.5f, 21.5f),
                        PathNode.LineTo(15.5f, 22.5f),
                        PathNode.LineTo(15.5f, 21.0f),
                        PathNode.LineTo(13.0f, 19.0f),
                        PathNode.LineTo(13.0f, 13.5f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _airplaneModeDemibold!!
    }
private var _airplaneModeDemibold: ImageVector? = null
