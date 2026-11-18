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

val RmuixIcons.Outline.Hotspot: ImageVector
    get() = RmuixIcons.Regular.Hotspot

val RmuixIcons.Default.Hotspot: ImageVector
    get() = RmuixIcons.Regular.Hotspot

val RmuixIcons.Filled.Hotspot: ImageVector
    get() = RmuixIcons.Regular.Hotspot

val RmuixIcons.Hotspot: ImageVector
    get() = RmuixIcons.Regular.Hotspot

val RmuixIcons.Light.Hotspot: ImageVector
    get() {
        if (_hotspotLight != null) return _hotspotLight!!
        _hotspotLight = ImageVector.Builder(
            name = "Hotspot.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 10.0f),
                        PathNode.CurveTo(13.1f, 10.0f, 14.0f, 10.9f, 14.0f, 12.0f),
                        PathNode.CurveTo(14.0f, 13.1f, 13.1f, 14.0f, 12.0f, 14.0f),
                        PathNode.CurveTo(10.9f, 14.0f, 10.0f, 13.1f, 10.0f, 12.0f),
                        PathNode.CurveTo(10.0f, 10.9f, 10.9f, 10.0f, 12.0f, 10.0f),
                        PathNode.Close,
                        PathNode.MoveTo(7.0f, 12.0f),
                        PathNode.CurveTo(7.0f, 9.2f, 9.2f, 7.0f, 12.0f, 7.0f),
                        PathNode.CurveTo(14.8f, 7.0f, 17.0f, 9.2f, 17.0f, 12.0f),
                        PathNode.CurveTo(17.0f, 14.8f, 14.8f, 17.0f, 12.0f, 17.0f),
                        PathNode.CurveTo(9.2f, 17.0f, 7.0f, 14.8f, 7.0f, 12.0f),
                        PathNode.MoveTo(3.0f, 12.0f),
                        PathNode.CurveTo(3.0f, 7.0f, 7.0f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(17.0f, 3.0f, 21.0f, 7.0f, 21.0f, 12.0f),
                        PathNode.CurveTo(21.0f, 17.0f, 17.0f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(7.0f, 21.0f, 3.0f, 17.0f, 3.0f, 12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _hotspotLight!!
    }
private var _hotspotLight: ImageVector? = null

val RmuixIcons.Normal.Hotspot: ImageVector
    get() {
        if (_hotspotNormal != null) return _hotspotNormal!!
        _hotspotNormal = ImageVector.Builder(
            name = "Hotspot.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 10.0f),
                        PathNode.CurveTo(13.1f, 10.0f, 14.0f, 10.9f, 14.0f, 12.0f),
                        PathNode.CurveTo(14.0f, 13.1f, 13.1f, 14.0f, 12.0f, 14.0f),
                        PathNode.CurveTo(10.9f, 14.0f, 10.0f, 13.1f, 10.0f, 12.0f),
                        PathNode.CurveTo(10.0f, 10.9f, 10.9f, 10.0f, 12.0f, 10.0f),
                        PathNode.Close,
                        PathNode.MoveTo(7.0f, 12.0f),
                        PathNode.CurveTo(7.0f, 9.2f, 9.2f, 7.0f, 12.0f, 7.0f),
                        PathNode.CurveTo(14.8f, 7.0f, 17.0f, 9.2f, 17.0f, 12.0f),
                        PathNode.CurveTo(17.0f, 14.8f, 14.8f, 17.0f, 12.0f, 17.0f),
                        PathNode.CurveTo(9.2f, 17.0f, 7.0f, 14.8f, 7.0f, 12.0f),
                        PathNode.MoveTo(3.0f, 12.0f),
                        PathNode.CurveTo(3.0f, 7.0f, 7.0f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(17.0f, 3.0f, 21.0f, 7.0f, 21.0f, 12.0f),
                        PathNode.CurveTo(21.0f, 17.0f, 17.0f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(7.0f, 21.0f, 3.0f, 17.0f, 3.0f, 12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _hotspotNormal!!
    }
private var _hotspotNormal: ImageVector? = null

val RmuixIcons.Regular.Hotspot: ImageVector
    get() {
        if (_hotspotRegular != null) return _hotspotRegular!!
        _hotspotRegular = ImageVector.Builder(
            name = "Hotspot.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 10.0f),
                        PathNode.CurveTo(13.1f, 10.0f, 14.0f, 10.9f, 14.0f, 12.0f),
                        PathNode.CurveTo(14.0f, 13.1f, 13.1f, 14.0f, 12.0f, 14.0f),
                        PathNode.CurveTo(10.9f, 14.0f, 10.0f, 13.1f, 10.0f, 12.0f),
                        PathNode.CurveTo(10.0f, 10.9f, 10.9f, 10.0f, 12.0f, 10.0f),
                        PathNode.Close,
                        PathNode.MoveTo(7.0f, 12.0f),
                        PathNode.CurveTo(7.0f, 9.2f, 9.2f, 7.0f, 12.0f, 7.0f),
                        PathNode.CurveTo(14.8f, 7.0f, 17.0f, 9.2f, 17.0f, 12.0f),
                        PathNode.CurveTo(17.0f, 14.8f, 14.8f, 17.0f, 12.0f, 17.0f),
                        PathNode.CurveTo(9.2f, 17.0f, 7.0f, 14.8f, 7.0f, 12.0f),
                        PathNode.MoveTo(3.0f, 12.0f),
                        PathNode.CurveTo(3.0f, 7.0f, 7.0f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(17.0f, 3.0f, 21.0f, 7.0f, 21.0f, 12.0f),
                        PathNode.CurveTo(21.0f, 17.0f, 17.0f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(7.0f, 21.0f, 3.0f, 17.0f, 3.0f, 12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _hotspotRegular!!
    }
private var _hotspotRegular: ImageVector? = null

val RmuixIcons.Medium.Hotspot: ImageVector
    get() {
        if (_hotspotMedium != null) return _hotspotMedium!!
        _hotspotMedium = ImageVector.Builder(
            name = "Hotspot.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 10.0f),
                        PathNode.CurveTo(13.1f, 10.0f, 14.0f, 10.9f, 14.0f, 12.0f),
                        PathNode.CurveTo(14.0f, 13.1f, 13.1f, 14.0f, 12.0f, 14.0f),
                        PathNode.CurveTo(10.9f, 14.0f, 10.0f, 13.1f, 10.0f, 12.0f),
                        PathNode.CurveTo(10.0f, 10.9f, 10.9f, 10.0f, 12.0f, 10.0f),
                        PathNode.Close,
                        PathNode.MoveTo(7.0f, 12.0f),
                        PathNode.CurveTo(7.0f, 9.2f, 9.2f, 7.0f, 12.0f, 7.0f),
                        PathNode.CurveTo(14.8f, 7.0f, 17.0f, 9.2f, 17.0f, 12.0f),
                        PathNode.CurveTo(17.0f, 14.8f, 14.8f, 17.0f, 12.0f, 17.0f),
                        PathNode.CurveTo(9.2f, 17.0f, 7.0f, 14.8f, 7.0f, 12.0f),
                        PathNode.MoveTo(3.0f, 12.0f),
                        PathNode.CurveTo(3.0f, 7.0f, 7.0f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(17.0f, 3.0f, 21.0f, 7.0f, 21.0f, 12.0f),
                        PathNode.CurveTo(21.0f, 17.0f, 17.0f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(7.0f, 21.0f, 3.0f, 17.0f, 3.0f, 12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _hotspotMedium!!
    }
private var _hotspotMedium: ImageVector? = null

val RmuixIcons.Demibold.Hotspot: ImageVector
    get() {
        if (_hotspotDemibold != null) return _hotspotDemibold!!
        _hotspotDemibold = ImageVector.Builder(
            name = "Hotspot.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 10.0f),
                        PathNode.CurveTo(13.1f, 10.0f, 14.0f, 10.9f, 14.0f, 12.0f),
                        PathNode.CurveTo(14.0f, 13.1f, 13.1f, 14.0f, 12.0f, 14.0f),
                        PathNode.CurveTo(10.9f, 14.0f, 10.0f, 13.1f, 10.0f, 12.0f),
                        PathNode.CurveTo(10.0f, 10.9f, 10.9f, 10.0f, 12.0f, 10.0f),
                        PathNode.Close,
                        PathNode.MoveTo(7.0f, 12.0f),
                        PathNode.CurveTo(7.0f, 9.2f, 9.2f, 7.0f, 12.0f, 7.0f),
                        PathNode.CurveTo(14.8f, 7.0f, 17.0f, 9.2f, 17.0f, 12.0f),
                        PathNode.CurveTo(17.0f, 14.8f, 14.8f, 17.0f, 12.0f, 17.0f),
                        PathNode.CurveTo(9.2f, 17.0f, 7.0f, 14.8f, 7.0f, 12.0f),
                        PathNode.MoveTo(3.0f, 12.0f),
                        PathNode.CurveTo(3.0f, 7.0f, 7.0f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(17.0f, 3.0f, 21.0f, 7.0f, 21.0f, 12.0f),
                        PathNode.CurveTo(21.0f, 17.0f, 17.0f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(7.0f, 21.0f, 3.0f, 17.0f, 3.0f, 12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _hotspotDemibold!!
    }
private var _hotspotDemibold: ImageVector? = null
