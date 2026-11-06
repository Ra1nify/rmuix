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

val RmuixIcons.Outline.DoNotDisturb: ImageVector
    get() = RmuixIcons.Regular.DoNotDisturb

val RmuixIcons.Default.DoNotDisturb: ImageVector
    get() = RmuixIcons.Regular.DoNotDisturb

val RmuixIcons.Filled.DoNotDisturb: ImageVector
    get() = RmuixIcons.Regular.DoNotDisturb

val RmuixIcons.DoNotDisturb: ImageVector
    get() = RmuixIcons.Regular.DoNotDisturb

val RmuixIcons.Light.DoNotDisturb: ImageVector
    get() {
        if (_doNotDisturbLight != null) return _doNotDisturbLight!!
        _doNotDisturbLight = ImageVector.Builder(
            name = "DoNotDisturb.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.CurveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f),
                        PathNode.CurveTo(2.0f, 17.52f, 6.48f, 22.0f, 12.0f, 22.0f),
                        PathNode.CurveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f),
                        PathNode.CurveTo(22.0f, 6.48f, 17.52f, 2.0f, 12.0f, 2.0f),
                        PathNode.Close,
                        PathNode.MoveTo(7.0f, 11.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.VerticalTo(13.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _doNotDisturbLight!!
    }
private var _doNotDisturbLight: ImageVector? = null

val RmuixIcons.Normal.DoNotDisturb: ImageVector
    get() {
        if (_doNotDisturbNormal != null) return _doNotDisturbNormal!!
        _doNotDisturbNormal = ImageVector.Builder(
            name = "DoNotDisturb.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.CurveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f),
                        PathNode.CurveTo(2.0f, 17.52f, 6.48f, 22.0f, 12.0f, 22.0f),
                        PathNode.CurveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f),
                        PathNode.CurveTo(22.0f, 6.48f, 17.52f, 2.0f, 12.0f, 2.0f),
                        PathNode.Close,
                        PathNode.MoveTo(7.0f, 11.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.VerticalTo(13.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _doNotDisturbNormal!!
    }
private var _doNotDisturbNormal: ImageVector? = null

val RmuixIcons.Regular.DoNotDisturb: ImageVector
    get() {
        if (_doNotDisturbRegular != null) return _doNotDisturbRegular!!
        _doNotDisturbRegular = ImageVector.Builder(
            name = "DoNotDisturb.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.CurveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f),
                        PathNode.CurveTo(2.0f, 17.52f, 6.48f, 22.0f, 12.0f, 22.0f),
                        PathNode.CurveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f),
                        PathNode.CurveTo(22.0f, 6.48f, 17.52f, 2.0f, 12.0f, 2.0f),
                        PathNode.Close,
                        PathNode.MoveTo(7.0f, 11.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.VerticalTo(13.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _doNotDisturbRegular!!
    }
private var _doNotDisturbRegular: ImageVector? = null

val RmuixIcons.Medium.DoNotDisturb: ImageVector
    get() {
        if (_doNotDisturbMedium != null) return _doNotDisturbMedium!!
        _doNotDisturbMedium = ImageVector.Builder(
            name = "DoNotDisturb.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.CurveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f),
                        PathNode.CurveTo(2.0f, 17.52f, 6.48f, 22.0f, 12.0f, 22.0f),
                        PathNode.CurveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f),
                        PathNode.CurveTo(22.0f, 6.48f, 17.52f, 2.0f, 12.0f, 2.0f),
                        PathNode.Close,
                        PathNode.MoveTo(7.0f, 11.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.VerticalTo(13.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _doNotDisturbMedium!!
    }
private var _doNotDisturbMedium: ImageVector? = null

val RmuixIcons.Demibold.DoNotDisturb: ImageVector
    get() {
        if (_doNotDisturbDemibold != null) return _doNotDisturbDemibold!!
        _doNotDisturbDemibold = ImageVector.Builder(
            name = "DoNotDisturb.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 2.0f),
                        PathNode.CurveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f),
                        PathNode.CurveTo(2.0f, 17.52f, 6.48f, 22.0f, 12.0f, 22.0f),
                        PathNode.CurveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f),
                        PathNode.CurveTo(22.0f, 6.48f, 17.52f, 2.0f, 12.0f, 2.0f),
                        PathNode.Close,
                        PathNode.MoveTo(7.0f, 11.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.VerticalTo(13.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _doNotDisturbDemibold!!
    }
private var _doNotDisturbDemibold: ImageVector? = null
