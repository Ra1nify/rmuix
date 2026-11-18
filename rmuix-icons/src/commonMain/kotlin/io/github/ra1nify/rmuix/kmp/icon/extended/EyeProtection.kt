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

val RmuixIcons.Outline.EyeProtection: ImageVector
    get() = RmuixIcons.Regular.EyeProtection

val RmuixIcons.Default.EyeProtection: ImageVector
    get() = RmuixIcons.Regular.EyeProtection

val RmuixIcons.Filled.EyeProtection: ImageVector
    get() = RmuixIcons.Regular.EyeProtection

val RmuixIcons.EyeProtection: ImageVector
    get() = RmuixIcons.Regular.EyeProtection

val RmuixIcons.Light.EyeProtection: ImageVector
    get() {
        if (_eyeProtectionLight != null) return _eyeProtectionLight!!
        _eyeProtectionLight = ImageVector.Builder(
            name = "EyeProtection.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 4.5f),
                        PathNode.CurveTo(7.0f, 4.5f, 2.73f, 7.61f, 1.0f, 12.0f),
                        PathNode.CurveTo(2.73f, 16.39f, 7.0f, 19.5f, 12.0f, 19.5f),
                        PathNode.CurveTo(17.0f, 19.5f, 21.27f, 16.39f, 23.0f, 12.0f),
                        PathNode.CurveTo(21.27f, 7.61f, 17.0f, 4.5f, 12.0f, 4.5f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(9.79f, 8.0f, 8.0f, 9.79f, 8.0f, 12.0f),
                        PathNode.CurveTo(8.0f, 14.21f, 9.79f, 16.0f, 12.0f, 16.0f),
                        PathNode.CurveTo(14.21f, 16.0f, 16.0f, 14.21f, 16.0f, 12.0f),
                        PathNode.CurveTo(16.0f, 9.79f, 14.21f, 8.0f, 12.0f, 8.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _eyeProtectionLight!!
    }
private var _eyeProtectionLight: ImageVector? = null

val RmuixIcons.Normal.EyeProtection: ImageVector
    get() {
        if (_eyeProtectionNormal != null) return _eyeProtectionNormal!!
        _eyeProtectionNormal = ImageVector.Builder(
            name = "EyeProtection.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 4.5f),
                        PathNode.CurveTo(7.0f, 4.5f, 2.73f, 7.61f, 1.0f, 12.0f),
                        PathNode.CurveTo(2.73f, 16.39f, 7.0f, 19.5f, 12.0f, 19.5f),
                        PathNode.CurveTo(17.0f, 19.5f, 21.27f, 16.39f, 23.0f, 12.0f),
                        PathNode.CurveTo(21.27f, 7.61f, 17.0f, 4.5f, 12.0f, 4.5f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(9.79f, 8.0f, 8.0f, 9.79f, 8.0f, 12.0f),
                        PathNode.CurveTo(8.0f, 14.21f, 9.79f, 16.0f, 12.0f, 16.0f),
                        PathNode.CurveTo(14.21f, 16.0f, 16.0f, 14.21f, 16.0f, 12.0f),
                        PathNode.CurveTo(16.0f, 9.79f, 14.21f, 8.0f, 12.0f, 8.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _eyeProtectionNormal!!
    }
private var _eyeProtectionNormal: ImageVector? = null

val RmuixIcons.Regular.EyeProtection: ImageVector
    get() {
        if (_eyeProtectionRegular != null) return _eyeProtectionRegular!!
        _eyeProtectionRegular = ImageVector.Builder(
            name = "EyeProtection.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 4.5f),
                        PathNode.CurveTo(7.0f, 4.5f, 2.73f, 7.61f, 1.0f, 12.0f),
                        PathNode.CurveTo(2.73f, 16.39f, 7.0f, 19.5f, 12.0f, 19.5f),
                        PathNode.CurveTo(17.0f, 19.5f, 21.27f, 16.39f, 23.0f, 12.0f),
                        PathNode.CurveTo(21.27f, 7.61f, 17.0f, 4.5f, 12.0f, 4.5f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(9.79f, 8.0f, 8.0f, 9.79f, 8.0f, 12.0f),
                        PathNode.CurveTo(8.0f, 14.21f, 9.79f, 16.0f, 12.0f, 16.0f),
                        PathNode.CurveTo(14.21f, 16.0f, 16.0f, 14.21f, 16.0f, 12.0f),
                        PathNode.CurveTo(16.0f, 9.79f, 14.21f, 8.0f, 12.0f, 8.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _eyeProtectionRegular!!
    }
private var _eyeProtectionRegular: ImageVector? = null

val RmuixIcons.Medium.EyeProtection: ImageVector
    get() {
        if (_eyeProtectionMedium != null) return _eyeProtectionMedium!!
        _eyeProtectionMedium = ImageVector.Builder(
            name = "EyeProtection.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 4.5f),
                        PathNode.CurveTo(7.0f, 4.5f, 2.73f, 7.61f, 1.0f, 12.0f),
                        PathNode.CurveTo(2.73f, 16.39f, 7.0f, 19.5f, 12.0f, 19.5f),
                        PathNode.CurveTo(17.0f, 19.5f, 21.27f, 16.39f, 23.0f, 12.0f),
                        PathNode.CurveTo(21.27f, 7.61f, 17.0f, 4.5f, 12.0f, 4.5f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(9.79f, 8.0f, 8.0f, 9.79f, 8.0f, 12.0f),
                        PathNode.CurveTo(8.0f, 14.21f, 9.79f, 16.0f, 12.0f, 16.0f),
                        PathNode.CurveTo(14.21f, 16.0f, 16.0f, 14.21f, 16.0f, 12.0f),
                        PathNode.CurveTo(16.0f, 9.79f, 14.21f, 8.0f, 12.0f, 8.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _eyeProtectionMedium!!
    }
private var _eyeProtectionMedium: ImageVector? = null

val RmuixIcons.Demibold.EyeProtection: ImageVector
    get() {
        if (_eyeProtectionDemibold != null) return _eyeProtectionDemibold!!
        _eyeProtectionDemibold = ImageVector.Builder(
            name = "EyeProtection.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(12.0f, 4.5f),
                        PathNode.CurveTo(7.0f, 4.5f, 2.73f, 7.61f, 1.0f, 12.0f),
                        PathNode.CurveTo(2.73f, 16.39f, 7.0f, 19.5f, 12.0f, 19.5f),
                        PathNode.CurveTo(17.0f, 19.5f, 21.27f, 16.39f, 23.0f, 12.0f),
                        PathNode.CurveTo(21.27f, 7.61f, 17.0f, 4.5f, 12.0f, 4.5f),
                        PathNode.Close,
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(9.79f, 8.0f, 8.0f, 9.79f, 8.0f, 12.0f),
                        PathNode.CurveTo(8.0f, 14.21f, 9.79f, 16.0f, 12.0f, 16.0f),
                        PathNode.CurveTo(14.21f, 16.0f, 16.0f, 14.21f, 16.0f, 12.0f),
                        PathNode.CurveTo(16.0f, 9.79f, 14.21f, 8.0f, 12.0f, 8.0f),
                        PathNode.Close,
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _eyeProtectionDemibold!!
    }
private var _eyeProtectionDemibold: ImageVector? = null
