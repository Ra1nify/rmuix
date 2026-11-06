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

val RmuixIcons.Outline.Headphones: ImageVector
    get() = RmuixIcons.Regular.Headphones

val RmuixIcons.Default.Headphones: ImageVector
    get() = RmuixIcons.Regular.Headphones

val RmuixIcons.Filled.Headphones: ImageVector
    get() = RmuixIcons.Regular.Headphones

val RmuixIcons.Headphones: ImageVector
    get() = RmuixIcons.Regular.Headphones

val RmuixIcons.Light.Headphones: ImageVector
    get() {
        if (_headphonesLight != null) return _headphonesLight!!
        _headphonesLight = ImageVector.Builder(
            name = "Headphones.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 3.0f),
                        PathNode.CurveTo(7.03f, 3.0f, 3.0f, 7.03f, 3.0f, 12.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(3.0f, 19.66f, 4.34f, 21.0f, 6.0f, 21.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.VerticalTo(14.0f),
                        PathNode.HorizontalTo(5.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(5.0f, 8.13f, 8.13f, 5.0f, 12.0f, 5.0f),
                        PathNode.CurveTo(15.87f, 5.0f, 19.0f, 8.13f, 19.0f, 12.0f),
                        PathNode.VerticalTo(14.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.VerticalTo(21.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.CurveTo(19.66f, 21.0f, 21.0f, 19.66f, 21.0f, 18.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(21.0f, 7.03f, 16.97f, 3.0f, 12.0f, 3.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _headphonesLight!!
    }
private var _headphonesLight: ImageVector? = null

val RmuixIcons.Normal.Headphones: ImageVector
    get() {
        if (_headphonesNormal != null) return _headphonesNormal!!
        _headphonesNormal = ImageVector.Builder(
            name = "Headphones.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 3.0f),
                        PathNode.CurveTo(7.03f, 3.0f, 3.0f, 7.03f, 3.0f, 12.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(3.0f, 19.66f, 4.34f, 21.0f, 6.0f, 21.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.VerticalTo(14.0f),
                        PathNode.HorizontalTo(5.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(5.0f, 8.13f, 8.13f, 5.0f, 12.0f, 5.0f),
                        PathNode.CurveTo(15.87f, 5.0f, 19.0f, 8.13f, 19.0f, 12.0f),
                        PathNode.VerticalTo(14.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.VerticalTo(21.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.CurveTo(19.66f, 21.0f, 21.0f, 19.66f, 21.0f, 18.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(21.0f, 7.03f, 16.97f, 3.0f, 12.0f, 3.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _headphonesNormal!!
    }
private var _headphonesNormal: ImageVector? = null

val RmuixIcons.Regular.Headphones: ImageVector
    get() {
        if (_headphonesRegular != null) return _headphonesRegular!!
        _headphonesRegular = ImageVector.Builder(
            name = "Headphones.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 3.0f),
                        PathNode.CurveTo(7.03f, 3.0f, 3.0f, 7.03f, 3.0f, 12.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(3.0f, 19.66f, 4.34f, 21.0f, 6.0f, 21.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.VerticalTo(14.0f),
                        PathNode.HorizontalTo(5.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(5.0f, 8.13f, 8.13f, 5.0f, 12.0f, 5.0f),
                        PathNode.CurveTo(15.87f, 5.0f, 19.0f, 8.13f, 19.0f, 12.0f),
                        PathNode.VerticalTo(14.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.VerticalTo(21.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.CurveTo(19.66f, 21.0f, 21.0f, 19.66f, 21.0f, 18.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(21.0f, 7.03f, 16.97f, 3.0f, 12.0f, 3.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _headphonesRegular!!
    }
private var _headphonesRegular: ImageVector? = null

val RmuixIcons.Medium.Headphones: ImageVector
    get() {
        if (_headphonesMedium != null) return _headphonesMedium!!
        _headphonesMedium = ImageVector.Builder(
            name = "Headphones.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 3.0f),
                        PathNode.CurveTo(7.03f, 3.0f, 3.0f, 7.03f, 3.0f, 12.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(3.0f, 19.66f, 4.34f, 21.0f, 6.0f, 21.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.VerticalTo(14.0f),
                        PathNode.HorizontalTo(5.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(5.0f, 8.13f, 8.13f, 5.0f, 12.0f, 5.0f),
                        PathNode.CurveTo(15.87f, 5.0f, 19.0f, 8.13f, 19.0f, 12.0f),
                        PathNode.VerticalTo(14.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.VerticalTo(21.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.CurveTo(19.66f, 21.0f, 21.0f, 19.66f, 21.0f, 18.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(21.0f, 7.03f, 16.97f, 3.0f, 12.0f, 3.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _headphonesMedium!!
    }
private var _headphonesMedium: ImageVector? = null

val RmuixIcons.Demibold.Headphones: ImageVector
    get() {
        if (_headphonesDemibold != null) return _headphonesDemibold!!
        _headphonesDemibold = ImageVector.Builder(
            name = "Headphones.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 3.0f),
                        PathNode.CurveTo(7.03f, 3.0f, 3.0f, 7.03f, 3.0f, 12.0f),
                        PathNode.VerticalTo(18.0f),
                        PathNode.CurveTo(3.0f, 19.66f, 4.34f, 21.0f, 6.0f, 21.0f),
                        PathNode.HorizontalTo(7.0f),
                        PathNode.VerticalTo(14.0f),
                        PathNode.HorizontalTo(5.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(5.0f, 8.13f, 8.13f, 5.0f, 12.0f, 5.0f),
                        PathNode.CurveTo(15.87f, 5.0f, 19.0f, 8.13f, 19.0f, 12.0f),
                        PathNode.VerticalTo(14.0f),
                        PathNode.HorizontalTo(17.0f),
                        PathNode.VerticalTo(21.0f),
                        PathNode.HorizontalTo(18.0f),
                        PathNode.CurveTo(19.66f, 21.0f, 21.0f, 19.66f, 21.0f, 18.0f),
                        PathNode.VerticalTo(12.0f),
                        PathNode.CurveTo(21.0f, 7.03f, 16.97f, 3.0f, 12.0f, 3.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _headphonesDemibold!!
    }
private var _headphonesDemibold: ImageVector? = null
