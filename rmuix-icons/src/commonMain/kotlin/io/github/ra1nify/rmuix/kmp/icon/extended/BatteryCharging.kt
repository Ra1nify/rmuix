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

val RmuixIcons.Outline.BatteryCharging: ImageVector
    get() = RmuixIcons.Regular.BatteryCharging

val RmuixIcons.Default.BatteryCharging: ImageVector
    get() = RmuixIcons.Regular.BatteryCharging

val RmuixIcons.Filled.BatteryCharging: ImageVector
    get() = RmuixIcons.Regular.BatteryCharging

val RmuixIcons.BatteryCharging: ImageVector
    get() = RmuixIcons.Regular.BatteryCharging

val RmuixIcons.Light.BatteryCharging: ImageVector
    get() {
        if (_batteryChargingLight != null) return _batteryChargingLight!!
        _batteryChargingLight = ImageVector.Builder(
            name = "BatteryCharging.Light",
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
                        PathNode.MoveTo(12.5f, 9.0f),
                        PathNode.LineTo(9.5f, 13.5f),
                        PathNode.HorizontalTo(12.0f),
                        PathNode.LineTo(11.5f, 17.0f),
                        PathNode.LineTo(14.5f, 12.5f),
                        PathNode.HorizontalTo(12.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batteryChargingLight!!
    }
private var _batteryChargingLight: ImageVector? = null

val RmuixIcons.Normal.BatteryCharging: ImageVector
    get() {
        if (_batteryChargingNormal != null) return _batteryChargingNormal!!
        _batteryChargingNormal = ImageVector.Builder(
            name = "BatteryCharging.Normal",
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
                        PathNode.MoveTo(12.5f, 9.0f),
                        PathNode.LineTo(9.5f, 13.5f),
                        PathNode.HorizontalTo(12.0f),
                        PathNode.LineTo(11.5f, 17.0f),
                        PathNode.LineTo(14.5f, 12.5f),
                        PathNode.HorizontalTo(12.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batteryChargingNormal!!
    }
private var _batteryChargingNormal: ImageVector? = null

val RmuixIcons.Regular.BatteryCharging: ImageVector
    get() {
        if (_batteryChargingRegular != null) return _batteryChargingRegular!!
        _batteryChargingRegular = ImageVector.Builder(
            name = "BatteryCharging.Regular",
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
                        PathNode.MoveTo(12.5f, 9.0f),
                        PathNode.LineTo(9.5f, 13.5f),
                        PathNode.HorizontalTo(12.0f),
                        PathNode.LineTo(11.5f, 17.0f),
                        PathNode.LineTo(14.5f, 12.5f),
                        PathNode.HorizontalTo(12.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batteryChargingRegular!!
    }
private var _batteryChargingRegular: ImageVector? = null

val RmuixIcons.Medium.BatteryCharging: ImageVector
    get() {
        if (_batteryChargingMedium != null) return _batteryChargingMedium!!
        _batteryChargingMedium = ImageVector.Builder(
            name = "BatteryCharging.Medium",
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
                        PathNode.MoveTo(12.5f, 9.0f),
                        PathNode.LineTo(9.5f, 13.5f),
                        PathNode.HorizontalTo(12.0f),
                        PathNode.LineTo(11.5f, 17.0f),
                        PathNode.LineTo(14.5f, 12.5f),
                        PathNode.HorizontalTo(12.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batteryChargingMedium!!
    }
private var _batteryChargingMedium: ImageVector? = null

val RmuixIcons.Demibold.BatteryCharging: ImageVector
    get() {
        if (_batteryChargingDemibold != null) return _batteryChargingDemibold!!
        _batteryChargingDemibold = ImageVector.Builder(
            name = "BatteryCharging.Demibold",
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
                        PathNode.MoveTo(12.5f, 9.0f),
                        PathNode.LineTo(9.5f, 13.5f),
                        PathNode.HorizontalTo(12.0f),
                        PathNode.LineTo(11.5f, 17.0f),
                        PathNode.LineTo(14.5f, 12.5f),
                        PathNode.HorizontalTo(12.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batteryChargingDemibold!!
    }
private var _batteryChargingDemibold: ImageVector? = null
