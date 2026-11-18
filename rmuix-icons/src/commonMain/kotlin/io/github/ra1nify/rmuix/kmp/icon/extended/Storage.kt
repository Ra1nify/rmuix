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

val RmuixIcons.Outline.Storage: ImageVector
    get() = RmuixIcons.Regular.Storage

val RmuixIcons.Default.Storage: ImageVector
    get() = RmuixIcons.Regular.Storage

val RmuixIcons.Filled.Storage: ImageVector
    get() = RmuixIcons.Regular.Storage

val RmuixIcons.Storage: ImageVector
    get() = RmuixIcons.Regular.Storage

val RmuixIcons.Light.Storage: ImageVector
    get() {
        if (_storageLight != null) return _storageLight!!
        _storageLight = ImageVector.Builder(
            name = "Storage.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(20.0f, 6.0f),
                        PathNode.CurveTo(20.0f, 7.66f, 16.42f, 9.0f, 12.0f, 9.0f),
                        PathNode.CurveTo(7.58f, 9.0f, 4.0f, 7.66f, 4.0f, 6.0f),
                        PathNode.CurveTo(4.0f, 4.34f, 7.58f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(16.42f, 3.0f, 20.0f, 4.34f, 20.0f, 6.0f),
                        PathNode.Close,
                        PathNode.MoveTo(4.0f, 6.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(4.0f, 13.66f, 7.58f, 15.0f, 12.0f, 15.0f),
                        PathNode.CurveTo(16.42f, 15.0f, 20.0f, 13.66f, 20.0f, 12.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.MoveTo(4.0f, 12.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(4.0f, 19.66f, 7.58f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(16.42f, 21.0f, 20.0f, 19.66f, 20.0f, 18.0f),
                        PathNode.VerticalTo(12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _storageLight!!
    }
private var _storageLight: ImageVector? = null

val RmuixIcons.Normal.Storage: ImageVector
    get() {
        if (_storageNormal != null) return _storageNormal!!
        _storageNormal = ImageVector.Builder(
            name = "Storage.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(20.0f, 6.0f),
                        PathNode.CurveTo(20.0f, 7.66f, 16.42f, 9.0f, 12.0f, 9.0f),
                        PathNode.CurveTo(7.58f, 9.0f, 4.0f, 7.66f, 4.0f, 6.0f),
                        PathNode.CurveTo(4.0f, 4.34f, 7.58f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(16.42f, 3.0f, 20.0f, 4.34f, 20.0f, 6.0f),
                        PathNode.Close,
                        PathNode.MoveTo(4.0f, 6.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(4.0f, 13.66f, 7.58f, 15.0f, 12.0f, 15.0f),
                        PathNode.CurveTo(16.42f, 15.0f, 20.0f, 13.66f, 20.0f, 12.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.MoveTo(4.0f, 12.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(4.0f, 19.66f, 7.58f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(16.42f, 21.0f, 20.0f, 19.66f, 20.0f, 18.0f),
                        PathNode.VerticalTo(12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _storageNormal!!
    }
private var _storageNormal: ImageVector? = null

val RmuixIcons.Regular.Storage: ImageVector
    get() {
        if (_storageRegular != null) return _storageRegular!!
        _storageRegular = ImageVector.Builder(
            name = "Storage.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(20.0f, 6.0f),
                        PathNode.CurveTo(20.0f, 7.66f, 16.42f, 9.0f, 12.0f, 9.0f),
                        PathNode.CurveTo(7.58f, 9.0f, 4.0f, 7.66f, 4.0f, 6.0f),
                        PathNode.CurveTo(4.0f, 4.34f, 7.58f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(16.42f, 3.0f, 20.0f, 4.34f, 20.0f, 6.0f),
                        PathNode.Close,
                        PathNode.MoveTo(4.0f, 6.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(4.0f, 13.66f, 7.58f, 15.0f, 12.0f, 15.0f),
                        PathNode.CurveTo(16.42f, 15.0f, 20.0f, 13.66f, 20.0f, 12.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.MoveTo(4.0f, 12.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(4.0f, 19.66f, 7.58f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(16.42f, 21.0f, 20.0f, 19.66f, 20.0f, 18.0f),
                        PathNode.VerticalTo(12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _storageRegular!!
    }
private var _storageRegular: ImageVector? = null

val RmuixIcons.Medium.Storage: ImageVector
    get() {
        if (_storageMedium != null) return _storageMedium!!
        _storageMedium = ImageVector.Builder(
            name = "Storage.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(20.0f, 6.0f),
                        PathNode.CurveTo(20.0f, 7.66f, 16.42f, 9.0f, 12.0f, 9.0f),
                        PathNode.CurveTo(7.58f, 9.0f, 4.0f, 7.66f, 4.0f, 6.0f),
                        PathNode.CurveTo(4.0f, 4.34f, 7.58f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(16.42f, 3.0f, 20.0f, 4.34f, 20.0f, 6.0f),
                        PathNode.Close,
                        PathNode.MoveTo(4.0f, 6.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(4.0f, 13.66f, 7.58f, 15.0f, 12.0f, 15.0f),
                        PathNode.CurveTo(16.42f, 15.0f, 20.0f, 13.66f, 20.0f, 12.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.MoveTo(4.0f, 12.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(4.0f, 19.66f, 7.58f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(16.42f, 21.0f, 20.0f, 19.66f, 20.0f, 18.0f),
                        PathNode.VerticalTo(12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _storageMedium!!
    }
private var _storageMedium: ImageVector? = null

val RmuixIcons.Demibold.Storage: ImageVector
    get() {
        if (_storageDemibold != null) return _storageDemibold!!
        _storageDemibold = ImageVector.Builder(
            name = "Storage.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(20.0f, 6.0f),
                        PathNode.CurveTo(20.0f, 7.66f, 16.42f, 9.0f, 12.0f, 9.0f),
                        PathNode.CurveTo(7.58f, 9.0f, 4.0f, 7.66f, 4.0f, 6.0f),
                        PathNode.CurveTo(4.0f, 4.34f, 7.58f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(16.42f, 3.0f, 20.0f, 4.34f, 20.0f, 6.0f),
                        PathNode.Close,
                        PathNode.MoveTo(4.0f, 6.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(4.0f, 13.66f, 7.58f, 15.0f, 12.0f, 15.0f),
                        PathNode.CurveTo(16.42f, 15.0f, 20.0f, 13.66f, 20.0f, 12.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.MoveTo(4.0f, 12.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(4.0f, 19.66f, 7.58f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(16.42f, 21.0f, 20.0f, 19.66f, 20.0f, 18.0f),
                        PathNode.VerticalTo(12.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _storageDemibold!!
    }
private var _storageDemibold: ImageVector? = null
