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

val RmuixIcons.Outline.AutoRotate: ImageVector
    get() = RmuixIcons.Regular.AutoRotate

val RmuixIcons.Default.AutoRotate: ImageVector
    get() = RmuixIcons.Regular.AutoRotate

val RmuixIcons.Filled.AutoRotate: ImageVector
    get() = RmuixIcons.Regular.AutoRotate

val RmuixIcons.AutoRotate: ImageVector
    get() = RmuixIcons.Regular.AutoRotate

val RmuixIcons.Light.AutoRotate: ImageVector
    get() {
        if (_autoRotateLight != null) return _autoRotateLight!!
        _autoRotateLight = ImageVector.Builder(
            name = "AutoRotate.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(7.5f, 4.0f),
                        PathNode.LineTo(16.5f, 4.0f),
                        PathNode.CurveTo(18.43f, 4.0f, 20.0f, 5.57f, 20.0f, 7.5f),
                        PathNode.LineTo(20.0f, 16.5f),
                        PathNode.CurveTo(20.0f, 18.43f, 18.43f, 20.0f, 16.5f, 20.0f),
                        PathNode.LineTo(7.5f, 20.0f),
                        PathNode.CurveTo(5.57f, 20.0f, 4.0f, 18.43f, 4.0f, 16.5f),
                        PathNode.LineTo(4.0f, 7.5f),
                        PathNode.CurveTo(4.0f, 5.57f, 5.57f, 4.0f, 7.5f, 4.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 1.0f),
                        PathNode.LineTo(15.0f, 4.0f),
                        PathNode.LineTo(12.0f, 7.0f),
                        PathNode.MoveTo(12.0f, 23.0f),
                        PathNode.LineTo(9.0f, 20.0f),
                        PathNode.LineTo(12.0f, 17.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _autoRotateLight!!
    }
private var _autoRotateLight: ImageVector? = null

val RmuixIcons.Normal.AutoRotate: ImageVector
    get() {
        if (_autoRotateNormal != null) return _autoRotateNormal!!
        _autoRotateNormal = ImageVector.Builder(
            name = "AutoRotate.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(7.5f, 4.0f),
                        PathNode.LineTo(16.5f, 4.0f),
                        PathNode.CurveTo(18.43f, 4.0f, 20.0f, 5.57f, 20.0f, 7.5f),
                        PathNode.LineTo(20.0f, 16.5f),
                        PathNode.CurveTo(20.0f, 18.43f, 18.43f, 20.0f, 16.5f, 20.0f),
                        PathNode.LineTo(7.5f, 20.0f),
                        PathNode.CurveTo(5.57f, 20.0f, 4.0f, 18.43f, 4.0f, 16.5f),
                        PathNode.LineTo(4.0f, 7.5f),
                        PathNode.CurveTo(4.0f, 5.57f, 5.57f, 4.0f, 7.5f, 4.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 1.0f),
                        PathNode.LineTo(15.0f, 4.0f),
                        PathNode.LineTo(12.0f, 7.0f),
                        PathNode.MoveTo(12.0f, 23.0f),
                        PathNode.LineTo(9.0f, 20.0f),
                        PathNode.LineTo(12.0f, 17.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _autoRotateNormal!!
    }
private var _autoRotateNormal: ImageVector? = null

val RmuixIcons.Regular.AutoRotate: ImageVector
    get() {
        if (_autoRotateRegular != null) return _autoRotateRegular!!
        _autoRotateRegular = ImageVector.Builder(
            name = "AutoRotate.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(7.5f, 4.0f),
                        PathNode.LineTo(16.5f, 4.0f),
                        PathNode.CurveTo(18.43f, 4.0f, 20.0f, 5.57f, 20.0f, 7.5f),
                        PathNode.LineTo(20.0f, 16.5f),
                        PathNode.CurveTo(20.0f, 18.43f, 18.43f, 20.0f, 16.5f, 20.0f),
                        PathNode.LineTo(7.5f, 20.0f),
                        PathNode.CurveTo(5.57f, 20.0f, 4.0f, 18.43f, 4.0f, 16.5f),
                        PathNode.LineTo(4.0f, 7.5f),
                        PathNode.CurveTo(4.0f, 5.57f, 5.57f, 4.0f, 7.5f, 4.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 1.0f),
                        PathNode.LineTo(15.0f, 4.0f),
                        PathNode.LineTo(12.0f, 7.0f),
                        PathNode.MoveTo(12.0f, 23.0f),
                        PathNode.LineTo(9.0f, 20.0f),
                        PathNode.LineTo(12.0f, 17.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _autoRotateRegular!!
    }
private var _autoRotateRegular: ImageVector? = null

val RmuixIcons.Medium.AutoRotate: ImageVector
    get() {
        if (_autoRotateMedium != null) return _autoRotateMedium!!
        _autoRotateMedium = ImageVector.Builder(
            name = "AutoRotate.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(7.5f, 4.0f),
                        PathNode.LineTo(16.5f, 4.0f),
                        PathNode.CurveTo(18.43f, 4.0f, 20.0f, 5.57f, 20.0f, 7.5f),
                        PathNode.LineTo(20.0f, 16.5f),
                        PathNode.CurveTo(20.0f, 18.43f, 18.43f, 20.0f, 16.5f, 20.0f),
                        PathNode.LineTo(7.5f, 20.0f),
                        PathNode.CurveTo(5.57f, 20.0f, 4.0f, 18.43f, 4.0f, 16.5f),
                        PathNode.LineTo(4.0f, 7.5f),
                        PathNode.CurveTo(4.0f, 5.57f, 5.57f, 4.0f, 7.5f, 4.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 1.0f),
                        PathNode.LineTo(15.0f, 4.0f),
                        PathNode.LineTo(12.0f, 7.0f),
                        PathNode.MoveTo(12.0f, 23.0f),
                        PathNode.LineTo(9.0f, 20.0f),
                        PathNode.LineTo(12.0f, 17.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _autoRotateMedium!!
    }
private var _autoRotateMedium: ImageVector? = null

val RmuixIcons.Demibold.AutoRotate: ImageVector
    get() {
        if (_autoRotateDemibold != null) return _autoRotateDemibold!!
        _autoRotateDemibold = ImageVector.Builder(
            name = "AutoRotate.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(7.5f, 4.0f),
                        PathNode.LineTo(16.5f, 4.0f),
                        PathNode.CurveTo(18.43f, 4.0f, 20.0f, 5.57f, 20.0f, 7.5f),
                        PathNode.LineTo(20.0f, 16.5f),
                        PathNode.CurveTo(20.0f, 18.43f, 18.43f, 20.0f, 16.5f, 20.0f),
                        PathNode.LineTo(7.5f, 20.0f),
                        PathNode.CurveTo(5.57f, 20.0f, 4.0f, 18.43f, 4.0f, 16.5f),
                        PathNode.LineTo(4.0f, 7.5f),
                        PathNode.CurveTo(4.0f, 5.57f, 5.57f, 4.0f, 7.5f, 4.0f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 1.0f),
                        PathNode.LineTo(15.0f, 4.0f),
                        PathNode.LineTo(12.0f, 7.0f),
                        PathNode.MoveTo(12.0f, 23.0f),
                        PathNode.LineTo(9.0f, 20.0f),
                        PathNode.LineTo(12.0f, 17.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _autoRotateDemibold!!
    }
private var _autoRotateDemibold: ImageVector? = null
