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

val RmuixIcons.Outline.BatterySaver: ImageVector
    get() = RmuixIcons.Regular.BatterySaver

val RmuixIcons.Default.BatterySaver: ImageVector
    get() = RmuixIcons.Regular.BatterySaver

val RmuixIcons.Filled.BatterySaver: ImageVector
    get() = RmuixIcons.Regular.BatterySaver

val RmuixIcons.BatterySaver: ImageVector
    get() = RmuixIcons.Regular.BatterySaver

val RmuixIcons.Light.BatterySaver: ImageVector
    get() {
        if (_batterySaverLight != null) return _batterySaverLight!!
        _batterySaverLight = ImageVector.Builder(
            name = "BatterySaver.Light",
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
                        PathNode.MoveTo(12.0f, 10.0f),
                        PathNode.LineTo(12.0f, 16.0f),
                        PathNode.MoveTo(9.0f, 13.0f),
                        PathNode.LineTo(15.0f, 13.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batterySaverLight!!
    }
private var _batterySaverLight: ImageVector? = null

val RmuixIcons.Normal.BatterySaver: ImageVector
    get() {
        if (_batterySaverNormal != null) return _batterySaverNormal!!
        _batterySaverNormal = ImageVector.Builder(
            name = "BatterySaver.Normal",
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
                        PathNode.MoveTo(12.0f, 10.0f),
                        PathNode.LineTo(12.0f, 16.0f),
                        PathNode.MoveTo(9.0f, 13.0f),
                        PathNode.LineTo(15.0f, 13.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batterySaverNormal!!
    }
private var _batterySaverNormal: ImageVector? = null

val RmuixIcons.Regular.BatterySaver: ImageVector
    get() {
        if (_batterySaverRegular != null) return _batterySaverRegular!!
        _batterySaverRegular = ImageVector.Builder(
            name = "BatterySaver.Regular",
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
                        PathNode.MoveTo(12.0f, 10.0f),
                        PathNode.LineTo(12.0f, 16.0f),
                        PathNode.MoveTo(9.0f, 13.0f),
                        PathNode.LineTo(15.0f, 13.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batterySaverRegular!!
    }
private var _batterySaverRegular: ImageVector? = null

val RmuixIcons.Medium.BatterySaver: ImageVector
    get() {
        if (_batterySaverMedium != null) return _batterySaverMedium!!
        _batterySaverMedium = ImageVector.Builder(
            name = "BatterySaver.Medium",
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
                        PathNode.MoveTo(12.0f, 10.0f),
                        PathNode.LineTo(12.0f, 16.0f),
                        PathNode.MoveTo(9.0f, 13.0f),
                        PathNode.LineTo(15.0f, 13.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batterySaverMedium!!
    }
private var _batterySaverMedium: ImageVector? = null

val RmuixIcons.Demibold.BatterySaver: ImageVector
    get() {
        if (_batterySaverDemibold != null) return _batterySaverDemibold!!
        _batterySaverDemibold = ImageVector.Builder(
            name = "BatterySaver.Demibold",
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
                        PathNode.MoveTo(12.0f, 10.0f),
                        PathNode.LineTo(12.0f, 16.0f),
                        PathNode.MoveTo(9.0f, 13.0f),
                        PathNode.LineTo(15.0f, 13.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _batterySaverDemibold!!
    }
private var _batterySaverDemibold: ImageVector? = null
