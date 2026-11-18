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

val RmuixIcons.Outline.Battery: ImageVector
    get() = RmuixIcons.Regular.Battery

val RmuixIcons.Default.Battery: ImageVector
    get() = RmuixIcons.Regular.Battery

val RmuixIcons.Filled.Battery: ImageVector
    get() = RmuixIcons.Regular.Battery

val RmuixIcons.Battery: ImageVector
    get() = RmuixIcons.Regular.Battery

val RmuixIcons.Light.Battery: ImageVector
    get() {
        if (_batteryLight != null) return _batteryLight!!
        _batteryLight = ImageVector.Builder(
            name = "Battery.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(17.0f, 6.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.CurveTo(5.9f, 6.0f, 5.0f, 6.9f, 5.0f, 8.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(5.0f, 19.1f, 5.9f, 20.0f, 7.0f, 20.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.CurveTo(18.1f, 20.0f, 19.0f, 19.1f, 19.0f, 18.0f),
                        PathNode.VerticalTo(8.0f),
                        PathNode.CurveTo(19.0f, 6.9f, 18.1f, 6.0f, 17.0f, 6.0f),
                        PathNode.Close,
                        PathNode.MoveTo(10.0f, 4.0f),
                        PathNode.HorizontalTo(14.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(10.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batteryLight!!
    }
private var _batteryLight: ImageVector? = null

val RmuixIcons.Normal.Battery: ImageVector
    get() {
        if (_batteryNormal != null) return _batteryNormal!!
        _batteryNormal = ImageVector.Builder(
            name = "Battery.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(17.0f, 6.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.CurveTo(5.9f, 6.0f, 5.0f, 6.9f, 5.0f, 8.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(5.0f, 19.1f, 5.9f, 20.0f, 7.0f, 20.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.CurveTo(18.1f, 20.0f, 19.0f, 19.1f, 19.0f, 18.0f),
                        PathNode.VerticalTo(8.0f),
                        PathNode.CurveTo(19.0f, 6.9f, 18.1f, 6.0f, 17.0f, 6.0f),
                        PathNode.Close,
                        PathNode.MoveTo(10.0f, 4.0f),
                        PathNode.HorizontalTo(14.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(10.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batteryNormal!!
    }
private var _batteryNormal: ImageVector? = null

val RmuixIcons.Regular.Battery: ImageVector
    get() {
        if (_batteryRegular != null) return _batteryRegular!!
        _batteryRegular = ImageVector.Builder(
            name = "Battery.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(17.0f, 6.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.CurveTo(5.9f, 6.0f, 5.0f, 6.9f, 5.0f, 8.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(5.0f, 19.1f, 5.9f, 20.0f, 7.0f, 20.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.CurveTo(18.1f, 20.0f, 19.0f, 19.1f, 19.0f, 18.0f),
                        PathNode.VerticalTo(8.0f),
                        PathNode.CurveTo(19.0f, 6.9f, 18.1f, 6.0f, 17.0f, 6.0f),
                        PathNode.Close,
                        PathNode.MoveTo(10.0f, 4.0f),
                        PathNode.HorizontalTo(14.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(10.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batteryRegular!!
    }
private var _batteryRegular: ImageVector? = null

val RmuixIcons.Medium.Battery: ImageVector
    get() {
        if (_batteryMedium != null) return _batteryMedium!!
        _batteryMedium = ImageVector.Builder(
            name = "Battery.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(17.0f, 6.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.CurveTo(5.9f, 6.0f, 5.0f, 6.9f, 5.0f, 8.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(5.0f, 19.1f, 5.9f, 20.0f, 7.0f, 20.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.CurveTo(18.1f, 20.0f, 19.0f, 19.1f, 19.0f, 18.0f),
                        PathNode.VerticalTo(8.0f),
                        PathNode.CurveTo(19.0f, 6.9f, 18.1f, 6.0f, 17.0f, 6.0f),
                        PathNode.Close,
                        PathNode.MoveTo(10.0f, 4.0f),
                        PathNode.HorizontalTo(14.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(10.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batteryMedium!!
    }
private var _batteryMedium: ImageVector? = null

val RmuixIcons.Demibold.Battery: ImageVector
    get() {
        if (_batteryDemibold != null) return _batteryDemibold!!
        _batteryDemibold = ImageVector.Builder(
            name = "Battery.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(17.0f, 6.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.CurveTo(5.9f, 6.0f, 5.0f, 6.9f, 5.0f, 8.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(5.0f, 19.1f, 5.9f, 20.0f, 7.0f, 20.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.CurveTo(18.1f, 20.0f, 19.0f, 19.1f, 19.0f, 18.0f),
                        PathNode.VerticalTo(8.0f),
                        PathNode.CurveTo(19.0f, 6.9f, 18.1f, 6.0f, 17.0f, 6.0f),
                        PathNode.Close,
                        PathNode.MoveTo(10.0f, 4.0f),
                        PathNode.HorizontalTo(14.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(10.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batteryDemibold!!
    }
private var _batteryDemibold: ImageVector? = null
