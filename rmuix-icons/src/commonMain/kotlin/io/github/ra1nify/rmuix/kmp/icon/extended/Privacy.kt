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

val RmuixIcons.Outline.Privacy: ImageVector
    get() = RmuixIcons.Regular.Privacy

val RmuixIcons.Default.Privacy: ImageVector
    get() = RmuixIcons.Regular.Privacy

val RmuixIcons.Filled.Privacy: ImageVector
    get() = RmuixIcons.Regular.Privacy

val RmuixIcons.Privacy: ImageVector
    get() = RmuixIcons.Regular.Privacy

val RmuixIcons.Light.Privacy: ImageVector
    get() {
        if (_privacyLight != null) return _privacyLight!!
        _privacyLight = ImageVector.Builder(
            name = "Privacy.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(4.0f, 5.0f),
                        PathNode.VerticalTo(11.0f),
                        PathNode.CurveTo(4.0f, 16.55f, 7.42f, 20.74f, 12.0f, 22.0f),
                        PathNode.CurveTo(16.58f, 20.74f, 20.0f, 16.55f, 20.0f, 11.0f),
                        PathNode.VerticalTo(5.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(10.9f, 8.0f, 10.0f, 8.9f, 10.0f, 10.0f),
                        PathNode.CurveTo(10.0f, 10.8f, 10.5f, 11.5f, 11.2f, 11.8f),
                        PathNode.LineTo(10.5f, 15.0f),
                        PathNode.HorizontalTo(13.5f),
                        PathNode.LineTo(12.8f, 11.8f),
                        PathNode.CurveTo(13.5f, 11.5f, 14.0f, 10.8f, 14.0f, 10.0f),
                        PathNode.CurveTo(14.0f, 8.9f, 13.1f, 8.0f, 12.0f, 8.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _privacyLight!!
    }
private var _privacyLight: ImageVector? = null

val RmuixIcons.Normal.Privacy: ImageVector
    get() {
        if (_privacyNormal != null) return _privacyNormal!!
        _privacyNormal = ImageVector.Builder(
            name = "Privacy.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(4.0f, 5.0f),
                        PathNode.VerticalTo(11.0f),
                        PathNode.CurveTo(4.0f, 16.55f, 7.42f, 20.74f, 12.0f, 22.0f),
                        PathNode.CurveTo(16.58f, 20.74f, 20.0f, 16.55f, 20.0f, 11.0f),
                        PathNode.VerticalTo(5.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(10.9f, 8.0f, 10.0f, 8.9f, 10.0f, 10.0f),
                        PathNode.CurveTo(10.0f, 10.8f, 10.5f, 11.5f, 11.2f, 11.8f),
                        PathNode.LineTo(10.5f, 15.0f),
                        PathNode.HorizontalTo(13.5f),
                        PathNode.LineTo(12.8f, 11.8f),
                        PathNode.CurveTo(13.5f, 11.5f, 14.0f, 10.8f, 14.0f, 10.0f),
                        PathNode.CurveTo(14.0f, 8.9f, 13.1f, 8.0f, 12.0f, 8.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _privacyNormal!!
    }
private var _privacyNormal: ImageVector? = null

val RmuixIcons.Regular.Privacy: ImageVector
    get() {
        if (_privacyRegular != null) return _privacyRegular!!
        _privacyRegular = ImageVector.Builder(
            name = "Privacy.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(4.0f, 5.0f),
                        PathNode.VerticalTo(11.0f),
                        PathNode.CurveTo(4.0f, 16.55f, 7.42f, 20.74f, 12.0f, 22.0f),
                        PathNode.CurveTo(16.58f, 20.74f, 20.0f, 16.55f, 20.0f, 11.0f),
                        PathNode.VerticalTo(5.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(10.9f, 8.0f, 10.0f, 8.9f, 10.0f, 10.0f),
                        PathNode.CurveTo(10.0f, 10.8f, 10.5f, 11.5f, 11.2f, 11.8f),
                        PathNode.LineTo(10.5f, 15.0f),
                        PathNode.HorizontalTo(13.5f),
                        PathNode.LineTo(12.8f, 11.8f),
                        PathNode.CurveTo(13.5f, 11.5f, 14.0f, 10.8f, 14.0f, 10.0f),
                        PathNode.CurveTo(14.0f, 8.9f, 13.1f, 8.0f, 12.0f, 8.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _privacyRegular!!
    }
private var _privacyRegular: ImageVector? = null

val RmuixIcons.Medium.Privacy: ImageVector
    get() {
        if (_privacyMedium != null) return _privacyMedium!!
        _privacyMedium = ImageVector.Builder(
            name = "Privacy.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(4.0f, 5.0f),
                        PathNode.VerticalTo(11.0f),
                        PathNode.CurveTo(4.0f, 16.55f, 7.42f, 20.74f, 12.0f, 22.0f),
                        PathNode.CurveTo(16.58f, 20.74f, 20.0f, 16.55f, 20.0f, 11.0f),
                        PathNode.VerticalTo(5.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(10.9f, 8.0f, 10.0f, 8.9f, 10.0f, 10.0f),
                        PathNode.CurveTo(10.0f, 10.8f, 10.5f, 11.5f, 11.2f, 11.8f),
                        PathNode.LineTo(10.5f, 15.0f),
                        PathNode.HorizontalTo(13.5f),
                        PathNode.LineTo(12.8f, 11.8f),
                        PathNode.CurveTo(13.5f, 11.5f, 14.0f, 10.8f, 14.0f, 10.0f),
                        PathNode.CurveTo(14.0f, 8.9f, 13.1f, 8.0f, 12.0f, 8.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _privacyMedium!!
    }
private var _privacyMedium: ImageVector? = null

val RmuixIcons.Demibold.Privacy: ImageVector
    get() {
        if (_privacyDemibold != null) return _privacyDemibold!!
        _privacyDemibold = ImageVector.Builder(
            name = "Privacy.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.LineTo(4.0f, 5.0f),
                        PathNode.VerticalTo(11.0f),
                        PathNode.CurveTo(4.0f, 16.55f, 7.42f, 20.74f, 12.0f, 22.0f),
                        PathNode.CurveTo(16.58f, 20.74f, 20.0f, 16.55f, 20.0f, 11.0f),
                        PathNode.VerticalTo(5.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(10.9f, 8.0f, 10.0f, 8.9f, 10.0f, 10.0f),
                        PathNode.CurveTo(10.0f, 10.8f, 10.5f, 11.5f, 11.2f, 11.8f),
                        PathNode.LineTo(10.5f, 15.0f),
                        PathNode.HorizontalTo(13.5f),
                        PathNode.LineTo(12.8f, 11.8f),
                        PathNode.CurveTo(13.5f, 11.5f, 14.0f, 10.8f, 14.0f, 10.0f),
                        PathNode.CurveTo(14.0f, 8.9f, 13.1f, 8.0f, 12.0f, 8.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _privacyDemibold!!
    }
private var _privacyDemibold: ImageVector? = null
