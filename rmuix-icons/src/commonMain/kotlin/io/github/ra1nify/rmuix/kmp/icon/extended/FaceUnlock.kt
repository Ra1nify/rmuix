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

val RmuixIcons.Outline.FaceUnlock: ImageVector
    get() = RmuixIcons.Regular.FaceUnlock

val RmuixIcons.Default.FaceUnlock: ImageVector
    get() = RmuixIcons.Regular.FaceUnlock

val RmuixIcons.Filled.FaceUnlock: ImageVector
    get() = RmuixIcons.Regular.FaceUnlock

val RmuixIcons.FaceUnlock: ImageVector
    get() = RmuixIcons.Regular.FaceUnlock

val RmuixIcons.Light.FaceUnlock: ImageVector
    get() {
        if (_faceUnlockLight != null) return _faceUnlockLight!!
        _faceUnlockLight = ImageVector.Builder(
            name = "FaceUnlock.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(2.0f, 8.0f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.HorizontalTo(6.0f),
                        PathNode.MoveTo(18.0f, 4.0f),
                        PathNode.HorizontalTo(22.0f),
                        PathNode.VerticalTo(8.0f),
                        PathNode.MoveTo(22.0f, 16.0f),
                        PathNode.VerticalTo(20.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.MoveTo(6.0f, 20.0f),
                        PathNode.HorizontalTo(2.0f),
                        PathNode.VerticalTo(16.0f),
                        PathNode.MoveTo(9.0f, 9.0f),
                        PathNode.LineTo(9.01f, 9.0f),
                        PathNode.MoveTo(15.0f, 9.0f),
                        PathNode.LineTo(15.01f, 9.0f),
                        PathNode.MoveTo(8.5f, 14.0f),
                        PathNode.CurveTo(9.5f, 16.0f, 14.5f, 16.0f, 15.5f, 14.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _faceUnlockLight!!
    }
private var _faceUnlockLight: ImageVector? = null

val RmuixIcons.Normal.FaceUnlock: ImageVector
    get() {
        if (_faceUnlockNormal != null) return _faceUnlockNormal!!
        _faceUnlockNormal = ImageVector.Builder(
            name = "FaceUnlock.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(2.0f, 8.0f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.HorizontalTo(6.0f),
                        PathNode.MoveTo(18.0f, 4.0f),
                        PathNode.HorizontalTo(22.0f),
                        PathNode.VerticalTo(8.0f),
                        PathNode.MoveTo(22.0f, 16.0f),
                        PathNode.VerticalTo(20.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.MoveTo(6.0f, 20.0f),
                        PathNode.HorizontalTo(2.0f),
                        PathNode.VerticalTo(16.0f),
                        PathNode.MoveTo(9.0f, 9.0f),
                        PathNode.LineTo(9.01f, 9.0f),
                        PathNode.MoveTo(15.0f, 9.0f),
                        PathNode.LineTo(15.01f, 9.0f),
                        PathNode.MoveTo(8.5f, 14.0f),
                        PathNode.CurveTo(9.5f, 16.0f, 14.5f, 16.0f, 15.5f, 14.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _faceUnlockNormal!!
    }
private var _faceUnlockNormal: ImageVector? = null

val RmuixIcons.Regular.FaceUnlock: ImageVector
    get() {
        if (_faceUnlockRegular != null) return _faceUnlockRegular!!
        _faceUnlockRegular = ImageVector.Builder(
            name = "FaceUnlock.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(2.0f, 8.0f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.HorizontalTo(6.0f),
                        PathNode.MoveTo(18.0f, 4.0f),
                        PathNode.HorizontalTo(22.0f),
                        PathNode.VerticalTo(8.0f),
                        PathNode.MoveTo(22.0f, 16.0f),
                        PathNode.VerticalTo(20.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.MoveTo(6.0f, 20.0f),
                        PathNode.HorizontalTo(2.0f),
                        PathNode.VerticalTo(16.0f),
                        PathNode.MoveTo(9.0f, 9.0f),
                        PathNode.LineTo(9.01f, 9.0f),
                        PathNode.MoveTo(15.0f, 9.0f),
                        PathNode.LineTo(15.01f, 9.0f),
                        PathNode.MoveTo(8.5f, 14.0f),
                        PathNode.CurveTo(9.5f, 16.0f, 14.5f, 16.0f, 15.5f, 14.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _faceUnlockRegular!!
    }
private var _faceUnlockRegular: ImageVector? = null

val RmuixIcons.Medium.FaceUnlock: ImageVector
    get() {
        if (_faceUnlockMedium != null) return _faceUnlockMedium!!
        _faceUnlockMedium = ImageVector.Builder(
            name = "FaceUnlock.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(2.0f, 8.0f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.HorizontalTo(6.0f),
                        PathNode.MoveTo(18.0f, 4.0f),
                        PathNode.HorizontalTo(22.0f),
                        PathNode.VerticalTo(8.0f),
                        PathNode.MoveTo(22.0f, 16.0f),
                        PathNode.VerticalTo(20.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.MoveTo(6.0f, 20.0f),
                        PathNode.HorizontalTo(2.0f),
                        PathNode.VerticalTo(16.0f),
                        PathNode.MoveTo(9.0f, 9.0f),
                        PathNode.LineTo(9.01f, 9.0f),
                        PathNode.MoveTo(15.0f, 9.0f),
                        PathNode.LineTo(15.01f, 9.0f),
                        PathNode.MoveTo(8.5f, 14.0f),
                        PathNode.CurveTo(9.5f, 16.0f, 14.5f, 16.0f, 15.5f, 14.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _faceUnlockMedium!!
    }
private var _faceUnlockMedium: ImageVector? = null

val RmuixIcons.Demibold.FaceUnlock: ImageVector
    get() {
        if (_faceUnlockDemibold != null) return _faceUnlockDemibold!!
        _faceUnlockDemibold = ImageVector.Builder(
            name = "FaceUnlock.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(2.0f, 8.0f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.HorizontalTo(6.0f),
                        PathNode.MoveTo(18.0f, 4.0f),
                        PathNode.HorizontalTo(22.0f),
                        PathNode.VerticalTo(8.0f),
                        PathNode.MoveTo(22.0f, 16.0f),
                        PathNode.VerticalTo(20.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.MoveTo(6.0f, 20.0f),
                        PathNode.HorizontalTo(2.0f),
                        PathNode.VerticalTo(16.0f),
                        PathNode.MoveTo(9.0f, 9.0f),
                        PathNode.LineTo(9.01f, 9.0f),
                        PathNode.MoveTo(15.0f, 9.0f),
                        PathNode.LineTo(15.01f, 9.0f),
                        PathNode.MoveTo(8.5f, 14.0f),
                        PathNode.CurveTo(9.5f, 16.0f, 14.5f, 16.0f, 15.5f, 14.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _faceUnlockDemibold!!
    }
private var _faceUnlockDemibold: ImageVector? = null
