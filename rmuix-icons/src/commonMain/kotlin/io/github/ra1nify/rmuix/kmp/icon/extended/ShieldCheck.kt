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

val RmuixIcons.Outline.ShieldCheck: ImageVector
    get() = RmuixIcons.Regular.ShieldCheck

val RmuixIcons.Default.ShieldCheck: ImageVector
    get() = RmuixIcons.Regular.ShieldCheck

val RmuixIcons.Filled.ShieldCheck: ImageVector
    get() = RmuixIcons.Regular.ShieldCheck

val RmuixIcons.ShieldCheck: ImageVector
    get() = RmuixIcons.Regular.ShieldCheck

val RmuixIcons.Light.ShieldCheck: ImageVector
    get() {
        if (_shieldCheckLight != null) return _shieldCheckLight!!
        _shieldCheckLight = ImageVector.Builder(
            name = "ShieldCheck.Light",
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
                        PathNode.MoveTo(9.0f, 11.5f),
                        PathNode.LineTo(11.0f, 13.5f),
                        PathNode.LineTo(15.5f, 9.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _shieldCheckLight!!
    }
private var _shieldCheckLight: ImageVector? = null

val RmuixIcons.Normal.ShieldCheck: ImageVector
    get() {
        if (_shieldCheckNormal != null) return _shieldCheckNormal!!
        _shieldCheckNormal = ImageVector.Builder(
            name = "ShieldCheck.Normal",
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
                        PathNode.MoveTo(9.0f, 11.5f),
                        PathNode.LineTo(11.0f, 13.5f),
                        PathNode.LineTo(15.5f, 9.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _shieldCheckNormal!!
    }
private var _shieldCheckNormal: ImageVector? = null

val RmuixIcons.Regular.ShieldCheck: ImageVector
    get() {
        if (_shieldCheckRegular != null) return _shieldCheckRegular!!
        _shieldCheckRegular = ImageVector.Builder(
            name = "ShieldCheck.Regular",
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
                        PathNode.MoveTo(9.0f, 11.5f),
                        PathNode.LineTo(11.0f, 13.5f),
                        PathNode.LineTo(15.5f, 9.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _shieldCheckRegular!!
    }
private var _shieldCheckRegular: ImageVector? = null

val RmuixIcons.Medium.ShieldCheck: ImageVector
    get() {
        if (_shieldCheckMedium != null) return _shieldCheckMedium!!
        _shieldCheckMedium = ImageVector.Builder(
            name = "ShieldCheck.Medium",
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
                        PathNode.MoveTo(9.0f, 11.5f),
                        PathNode.LineTo(11.0f, 13.5f),
                        PathNode.LineTo(15.5f, 9.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _shieldCheckMedium!!
    }
private var _shieldCheckMedium: ImageVector? = null

val RmuixIcons.Demibold.ShieldCheck: ImageVector
    get() {
        if (_shieldCheckDemibold != null) return _shieldCheckDemibold!!
        _shieldCheckDemibold = ImageVector.Builder(
            name = "ShieldCheck.Demibold",
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
                        PathNode.MoveTo(9.0f, 11.5f),
                        PathNode.LineTo(11.0f, 13.5f),
                        PathNode.LineTo(15.5f, 9.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _shieldCheckDemibold!!
    }
private var _shieldCheckDemibold: ImageVector? = null
