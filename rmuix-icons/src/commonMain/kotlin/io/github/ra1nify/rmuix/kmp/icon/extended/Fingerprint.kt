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

val RmuixIcons.Outline.Fingerprint: ImageVector
    get() = RmuixIcons.Regular.Fingerprint

val RmuixIcons.Default.Fingerprint: ImageVector
    get() = RmuixIcons.Regular.Fingerprint

val RmuixIcons.Filled.Fingerprint: ImageVector
    get() = RmuixIcons.Regular.Fingerprint

val RmuixIcons.Fingerprint: ImageVector
    get() = RmuixIcons.Regular.Fingerprint

val RmuixIcons.Light.Fingerprint: ImageVector
    get() {
        if (_fingerprintLight != null) return _fingerprintLight!!
        _fingerprintLight = ImageVector.Builder(
            name = "Fingerprint.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(17.81f, 4.42f),
                        PathNode.CurveTo(16.22f, 2.92f, 14.13f, 2.0f, 11.83f, 2.0f),
                        PathNode.CurveTo(7.51f, 2.0f, 4.0f, 5.51f, 4.0f, 9.83f),
                        PathNode.VerticalTo(10.0f),
                        PathNode.MoveTo(20.0f, 11.0f),
                        PathNode.CurveTo(20.0f, 6.58f, 16.42f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(7.58f, 3.0f, 4.0f, 6.58f, 4.0f, 11.0f),
                        PathNode.VerticalTo(15.0f),
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(9.79f, 8.0f, 8.0f, 9.79f, 8.0f, 12.0f),
                        PathNode.VerticalTo(22.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _fingerprintLight!!
    }
private var _fingerprintLight: ImageVector? = null

val RmuixIcons.Normal.Fingerprint: ImageVector
    get() {
        if (_fingerprintNormal != null) return _fingerprintNormal!!
        _fingerprintNormal = ImageVector.Builder(
            name = "Fingerprint.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(17.81f, 4.42f),
                        PathNode.CurveTo(16.22f, 2.92f, 14.13f, 2.0f, 11.83f, 2.0f),
                        PathNode.CurveTo(7.51f, 2.0f, 4.0f, 5.51f, 4.0f, 9.83f),
                        PathNode.VerticalTo(10.0f),
                        PathNode.MoveTo(20.0f, 11.0f),
                        PathNode.CurveTo(20.0f, 6.58f, 16.42f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(7.58f, 3.0f, 4.0f, 6.58f, 4.0f, 11.0f),
                        PathNode.VerticalTo(15.0f),
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(9.79f, 8.0f, 8.0f, 9.79f, 8.0f, 12.0f),
                        PathNode.VerticalTo(22.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _fingerprintNormal!!
    }
private var _fingerprintNormal: ImageVector? = null

val RmuixIcons.Regular.Fingerprint: ImageVector
    get() {
        if (_fingerprintRegular != null) return _fingerprintRegular!!
        _fingerprintRegular = ImageVector.Builder(
            name = "Fingerprint.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(17.81f, 4.42f),
                        PathNode.CurveTo(16.22f, 2.92f, 14.13f, 2.0f, 11.83f, 2.0f),
                        PathNode.CurveTo(7.51f, 2.0f, 4.0f, 5.51f, 4.0f, 9.83f),
                        PathNode.VerticalTo(10.0f),
                        PathNode.MoveTo(20.0f, 11.0f),
                        PathNode.CurveTo(20.0f, 6.58f, 16.42f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(7.58f, 3.0f, 4.0f, 6.58f, 4.0f, 11.0f),
                        PathNode.VerticalTo(15.0f),
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(9.79f, 8.0f, 8.0f, 9.79f, 8.0f, 12.0f),
                        PathNode.VerticalTo(22.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _fingerprintRegular!!
    }
private var _fingerprintRegular: ImageVector? = null

val RmuixIcons.Medium.Fingerprint: ImageVector
    get() {
        if (_fingerprintMedium != null) return _fingerprintMedium!!
        _fingerprintMedium = ImageVector.Builder(
            name = "Fingerprint.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(17.81f, 4.42f),
                        PathNode.CurveTo(16.22f, 2.92f, 14.13f, 2.0f, 11.83f, 2.0f),
                        PathNode.CurveTo(7.51f, 2.0f, 4.0f, 5.51f, 4.0f, 9.83f),
                        PathNode.VerticalTo(10.0f),
                        PathNode.MoveTo(20.0f, 11.0f),
                        PathNode.CurveTo(20.0f, 6.58f, 16.42f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(7.58f, 3.0f, 4.0f, 6.58f, 4.0f, 11.0f),
                        PathNode.VerticalTo(15.0f),
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(9.79f, 8.0f, 8.0f, 9.79f, 8.0f, 12.0f),
                        PathNode.VerticalTo(22.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _fingerprintMedium!!
    }
private var _fingerprintMedium: ImageVector? = null

val RmuixIcons.Demibold.Fingerprint: ImageVector
    get() {
        if (_fingerprintDemibold != null) return _fingerprintDemibold!!
        _fingerprintDemibold = ImageVector.Builder(
            name = "Fingerprint.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(17.81f, 4.42f),
                        PathNode.CurveTo(16.22f, 2.92f, 14.13f, 2.0f, 11.83f, 2.0f),
                        PathNode.CurveTo(7.51f, 2.0f, 4.0f, 5.51f, 4.0f, 9.83f),
                        PathNode.VerticalTo(10.0f),
                        PathNode.MoveTo(20.0f, 11.0f),
                        PathNode.CurveTo(20.0f, 6.58f, 16.42f, 3.0f, 12.0f, 3.0f),
                        PathNode.CurveTo(7.58f, 3.0f, 4.0f, 6.58f, 4.0f, 11.0f),
                        PathNode.VerticalTo(15.0f),
                        PathNode.MoveTo(12.0f, 8.0f),
                        PathNode.CurveTo(9.79f, 8.0f, 8.0f, 9.79f, 8.0f, 12.0f),
                        PathNode.VerticalTo(22.0f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _fingerprintDemibold!!
    }
private var _fingerprintDemibold: ImageVector? = null
