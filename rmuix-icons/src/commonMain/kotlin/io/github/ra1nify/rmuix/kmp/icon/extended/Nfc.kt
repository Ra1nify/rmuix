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

val RmuixIcons.Outline.Nfc: ImageVector
    get() = RmuixIcons.Regular.Nfc

val RmuixIcons.Default.Nfc: ImageVector
    get() = RmuixIcons.Regular.Nfc

val RmuixIcons.Filled.Nfc: ImageVector
    get() = RmuixIcons.Regular.Nfc

val RmuixIcons.Nfc: ImageVector
    get() = RmuixIcons.Regular.Nfc

val RmuixIcons.Light.Nfc: ImageVector
    get() {
        if (_nfcLight != null) return _nfcLight!!
        _nfcLight = ImageVector.Builder(
            name = "Nfc.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(20.0f, 2.0f),
                        PathNode.HorizontalTo(4.0f),
                        PathNode.CurveTo(2.9f, 2.0f, 2.0f, 2.9f, 2.0f, 4.0f),
                        PathNode.VerticalTo(20.0f),
                        PathNode.CurveTo(2.0f, 21.1f, 2.9f, 22.0f, 4.0f, 22.0f),
                        PathNode.HorizontalTo(20.0f),
                        PathNode.CurveTo(21.1f, 22.0f, 22.0f, 21.1f, 22.0f, 20.0f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.CurveTo(22.0f, 2.9f, 21.1f, 2.0f, 20.0f, 2.0f),
                        PathNode.Close,
                        PathNode.MoveTo(6.0f, 18.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(8.5f),
                        PathNode.LineTo(15.5f, 14.5f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.HorizontalTo(15.5f),
                        PathNode.LineTo(8.5f, 9.5f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _nfcLight!!
    }
private var _nfcLight: ImageVector? = null

val RmuixIcons.Normal.Nfc: ImageVector
    get() {
        if (_nfcNormal != null) return _nfcNormal!!
        _nfcNormal = ImageVector.Builder(
            name = "Nfc.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(20.0f, 2.0f),
                        PathNode.HorizontalTo(4.0f),
                        PathNode.CurveTo(2.9f, 2.0f, 2.0f, 2.9f, 2.0f, 4.0f),
                        PathNode.VerticalTo(20.0f),
                        PathNode.CurveTo(2.0f, 21.1f, 2.9f, 22.0f, 4.0f, 22.0f),
                        PathNode.HorizontalTo(20.0f),
                        PathNode.CurveTo(21.1f, 22.0f, 22.0f, 21.1f, 22.0f, 20.0f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.CurveTo(22.0f, 2.9f, 21.1f, 2.0f, 20.0f, 2.0f),
                        PathNode.Close,
                        PathNode.MoveTo(6.0f, 18.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(8.5f),
                        PathNode.LineTo(15.5f, 14.5f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.HorizontalTo(15.5f),
                        PathNode.LineTo(8.5f, 9.5f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _nfcNormal!!
    }
private var _nfcNormal: ImageVector? = null

val RmuixIcons.Regular.Nfc: ImageVector
    get() {
        if (_nfcRegular != null) return _nfcRegular!!
        _nfcRegular = ImageVector.Builder(
            name = "Nfc.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(20.0f, 2.0f),
                        PathNode.HorizontalTo(4.0f),
                        PathNode.CurveTo(2.9f, 2.0f, 2.0f, 2.9f, 2.0f, 4.0f),
                        PathNode.VerticalTo(20.0f),
                        PathNode.CurveTo(2.0f, 21.1f, 2.9f, 22.0f, 4.0f, 22.0f),
                        PathNode.HorizontalTo(20.0f),
                        PathNode.CurveTo(21.1f, 22.0f, 22.0f, 21.1f, 22.0f, 20.0f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.CurveTo(22.0f, 2.9f, 21.1f, 2.0f, 20.0f, 2.0f),
                        PathNode.Close,
                        PathNode.MoveTo(6.0f, 18.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(8.5f),
                        PathNode.LineTo(15.5f, 14.5f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.HorizontalTo(15.5f),
                        PathNode.LineTo(8.5f, 9.5f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _nfcRegular!!
    }
private var _nfcRegular: ImageVector? = null

val RmuixIcons.Medium.Nfc: ImageVector
    get() {
        if (_nfcMedium != null) return _nfcMedium!!
        _nfcMedium = ImageVector.Builder(
            name = "Nfc.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(20.0f, 2.0f),
                        PathNode.HorizontalTo(4.0f),
                        PathNode.CurveTo(2.9f, 2.0f, 2.0f, 2.9f, 2.0f, 4.0f),
                        PathNode.VerticalTo(20.0f),
                        PathNode.CurveTo(2.0f, 21.1f, 2.9f, 22.0f, 4.0f, 22.0f),
                        PathNode.HorizontalTo(20.0f),
                        PathNode.CurveTo(21.1f, 22.0f, 22.0f, 21.1f, 22.0f, 20.0f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.CurveTo(22.0f, 2.9f, 21.1f, 2.0f, 20.0f, 2.0f),
                        PathNode.Close,
                        PathNode.MoveTo(6.0f, 18.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(8.5f),
                        PathNode.LineTo(15.5f, 14.5f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.HorizontalTo(15.5f),
                        PathNode.LineTo(8.5f, 9.5f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _nfcMedium!!
    }
private var _nfcMedium: ImageVector? = null

val RmuixIcons.Demibold.Nfc: ImageVector
    get() {
        if (_nfcDemibold != null) return _nfcDemibold!!
        _nfcDemibold = ImageVector.Builder(
            name = "Nfc.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(20.0f, 2.0f),
                        PathNode.HorizontalTo(4.0f),
                        PathNode.CurveTo(2.9f, 2.0f, 2.0f, 2.9f, 2.0f, 4.0f),
                        PathNode.VerticalTo(20.0f),
                        PathNode.CurveTo(2.0f, 21.1f, 2.9f, 22.0f, 4.0f, 22.0f),
                        PathNode.HorizontalTo(20.0f),
                        PathNode.CurveTo(21.1f, 22.0f, 22.0f, 21.1f, 22.0f, 20.0f),
                        PathNode.VerticalTo(4.0f),
                        PathNode.CurveTo(22.0f, 2.9f, 21.1f, 2.0f, 20.0f, 2.0f),
                        PathNode.Close,
                        PathNode.MoveTo(6.0f, 18.0f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(8.5f),
                        PathNode.LineTo(15.5f, 14.5f),
                        PathNode.VerticalTo(6.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.HorizontalTo(15.5f),
                        PathNode.LineTo(8.5f, 9.5f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _nfcDemibold!!
    }
private var _nfcDemibold: ImageVector? = null
