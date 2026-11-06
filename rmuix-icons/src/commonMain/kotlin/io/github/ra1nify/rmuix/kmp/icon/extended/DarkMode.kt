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

val RmuixIcons.Outline.DarkMode: ImageVector
    get() = RmuixIcons.Regular.DarkMode

val RmuixIcons.Default.DarkMode: ImageVector
    get() = RmuixIcons.Regular.DarkMode

val RmuixIcons.Filled.DarkMode: ImageVector
    get() = RmuixIcons.Regular.DarkMode

val RmuixIcons.DarkMode: ImageVector
    get() = RmuixIcons.Regular.DarkMode

val RmuixIcons.Light.DarkMode: ImageVector
    get() {
        if (_darkModeLight != null) return _darkModeLight!!
        _darkModeLight = ImageVector.Builder(
            name = "DarkMode.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 3.0f),
                        PathNode.CurveTo(7.03f, 3.0f, 3.0f, 7.03f, 3.0f, 12.0f),
                        PathNode.CurveTo(3.0f, 16.97f, 7.03f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(15.83f, 21.0f, 19.08f, 18.61f, 20.35f, 15.21f),
                        PathNode.CurveTo(19.68f, 15.35f, 18.98f, 15.43f, 18.27f, 15.43f),
                        PathNode.CurveTo(12.61f, 15.43f, 8.02f, 10.84f, 8.02f, 5.18f),
                        PathNode.CurveTo(8.02f, 4.41f, 8.11f, 3.66f, 8.28f, 2.94f),
                        PathNode.CurveTo(9.44f, 2.33f, 10.74f, 2.0f, 12.0f, 2.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _darkModeLight!!
    }
private var _darkModeLight: ImageVector? = null

val RmuixIcons.Normal.DarkMode: ImageVector
    get() {
        if (_darkModeNormal != null) return _darkModeNormal!!
        _darkModeNormal = ImageVector.Builder(
            name = "DarkMode.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 3.0f),
                        PathNode.CurveTo(7.03f, 3.0f, 3.0f, 7.03f, 3.0f, 12.0f),
                        PathNode.CurveTo(3.0f, 16.97f, 7.03f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(15.83f, 21.0f, 19.08f, 18.61f, 20.35f, 15.21f),
                        PathNode.CurveTo(19.68f, 15.35f, 18.98f, 15.43f, 18.27f, 15.43f),
                        PathNode.CurveTo(12.61f, 15.43f, 8.02f, 10.84f, 8.02f, 5.18f),
                        PathNode.CurveTo(8.02f, 4.41f, 8.11f, 3.66f, 8.28f, 2.94f),
                        PathNode.CurveTo(9.44f, 2.33f, 10.74f, 2.0f, 12.0f, 2.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _darkModeNormal!!
    }
private var _darkModeNormal: ImageVector? = null

val RmuixIcons.Regular.DarkMode: ImageVector
    get() {
        if (_darkModeRegular != null) return _darkModeRegular!!
        _darkModeRegular = ImageVector.Builder(
            name = "DarkMode.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 3.0f),
                        PathNode.CurveTo(7.03f, 3.0f, 3.0f, 7.03f, 3.0f, 12.0f),
                        PathNode.CurveTo(3.0f, 16.97f, 7.03f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(15.83f, 21.0f, 19.08f, 18.61f, 20.35f, 15.21f),
                        PathNode.CurveTo(19.68f, 15.35f, 18.98f, 15.43f, 18.27f, 15.43f),
                        PathNode.CurveTo(12.61f, 15.43f, 8.02f, 10.84f, 8.02f, 5.18f),
                        PathNode.CurveTo(8.02f, 4.41f, 8.11f, 3.66f, 8.28f, 2.94f),
                        PathNode.CurveTo(9.44f, 2.33f, 10.74f, 2.0f, 12.0f, 2.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _darkModeRegular!!
    }
private var _darkModeRegular: ImageVector? = null

val RmuixIcons.Medium.DarkMode: ImageVector
    get() {
        if (_darkModeMedium != null) return _darkModeMedium!!
        _darkModeMedium = ImageVector.Builder(
            name = "DarkMode.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 3.0f),
                        PathNode.CurveTo(7.03f, 3.0f, 3.0f, 7.03f, 3.0f, 12.0f),
                        PathNode.CurveTo(3.0f, 16.97f, 7.03f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(15.83f, 21.0f, 19.08f, 18.61f, 20.35f, 15.21f),
                        PathNode.CurveTo(19.68f, 15.35f, 18.98f, 15.43f, 18.27f, 15.43f),
                        PathNode.CurveTo(12.61f, 15.43f, 8.02f, 10.84f, 8.02f, 5.18f),
                        PathNode.CurveTo(8.02f, 4.41f, 8.11f, 3.66f, 8.28f, 2.94f),
                        PathNode.CurveTo(9.44f, 2.33f, 10.74f, 2.0f, 12.0f, 2.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _darkModeMedium!!
    }
private var _darkModeMedium: ImageVector? = null

val RmuixIcons.Demibold.DarkMode: ImageVector
    get() {
        if (_darkModeDemibold != null) return _darkModeDemibold!!
        _darkModeDemibold = ImageVector.Builder(
            name = "DarkMode.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 3.0f),
                        PathNode.CurveTo(7.03f, 3.0f, 3.0f, 7.03f, 3.0f, 12.0f),
                        PathNode.CurveTo(3.0f, 16.97f, 7.03f, 21.0f, 12.0f, 21.0f),
                        PathNode.CurveTo(15.83f, 21.0f, 19.08f, 18.61f, 20.35f, 15.21f),
                        PathNode.CurveTo(19.68f, 15.35f, 18.98f, 15.43f, 18.27f, 15.43f),
                        PathNode.CurveTo(12.61f, 15.43f, 8.02f, 10.84f, 8.02f, 5.18f),
                        PathNode.CurveTo(8.02f, 4.41f, 8.11f, 3.66f, 8.28f, 2.94f),
                        PathNode.CurveTo(9.44f, 2.33f, 10.74f, 2.0f, 12.0f, 2.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _darkModeDemibold!!
    }
private var _darkModeDemibold: ImageVector? = null
