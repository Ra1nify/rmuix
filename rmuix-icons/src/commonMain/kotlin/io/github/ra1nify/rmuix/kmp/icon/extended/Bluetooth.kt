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

val RmuixIcons.Outline.Bluetooth: ImageVector
    get() = RmuixIcons.Regular.Bluetooth

val RmuixIcons.Default.Bluetooth: ImageVector
    get() = RmuixIcons.Regular.Bluetooth

val RmuixIcons.Filled.Bluetooth: ImageVector
    get() = RmuixIcons.Regular.Bluetooth

val RmuixIcons.Bluetooth: ImageVector
    get() = RmuixIcons.Regular.Bluetooth

val RmuixIcons.Light.Bluetooth: ImageVector
    get() {
        if (_bluetoothLight != null) return _bluetoothLight!!
        _bluetoothLight = ImageVector.Builder(
            name = "Bluetooth.Light",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(6.5f, 17.5f),
                        PathNode.LineTo(17.5f, 6.5f),
                        PathNode.LineTo(12.0f, 1.0f),
                        PathNode.LineTo(12.0f, 23.0f),
                        PathNode.LineTo(17.5f, 17.5f),
                        PathNode.LineTo(6.5f, 6.5f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.2f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _bluetoothLight!!
    }
private var _bluetoothLight: ImageVector? = null

val RmuixIcons.Normal.Bluetooth: ImageVector
    get() {
        if (_bluetoothNormal != null) return _bluetoothNormal!!
        _bluetoothNormal = ImageVector.Builder(
            name = "Bluetooth.Normal",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(6.5f, 17.5f),
                        PathNode.LineTo(17.5f, 6.5f),
                        PathNode.LineTo(12.0f, 1.0f),
                        PathNode.LineTo(12.0f, 23.0f),
                        PathNode.LineTo(17.5f, 17.5f),
                        PathNode.LineTo(6.5f, 6.5f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 1.6f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _bluetoothNormal!!
    }
private var _bluetoothNormal: ImageVector? = null

val RmuixIcons.Regular.Bluetooth: ImageVector
    get() {
        if (_bluetoothRegular != null) return _bluetoothRegular!!
        _bluetoothRegular = ImageVector.Builder(
            name = "Bluetooth.Regular",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(6.5f, 17.5f),
                        PathNode.LineTo(17.5f, 6.5f),
                        PathNode.LineTo(12.0f, 1.0f),
                        PathNode.LineTo(12.0f, 23.0f),
                        PathNode.LineTo(17.5f, 17.5f),
                        PathNode.LineTo(6.5f, 6.5f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.0f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _bluetoothRegular!!
    }
private var _bluetoothRegular: ImageVector? = null

val RmuixIcons.Medium.Bluetooth: ImageVector
    get() {
        if (_bluetoothMedium != null) return _bluetoothMedium!!
        _bluetoothMedium = ImageVector.Builder(
            name = "Bluetooth.Medium",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(6.5f, 17.5f),
                        PathNode.LineTo(17.5f, 6.5f),
                        PathNode.LineTo(12.0f, 1.0f),
                        PathNode.LineTo(12.0f, 23.0f),
                        PathNode.LineTo(17.5f, 17.5f),
                        PathNode.LineTo(6.5f, 6.5f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _bluetoothMedium!!
    }
private var _bluetoothMedium: ImageVector? = null

val RmuixIcons.Demibold.Bluetooth: ImageVector
    get() {
        if (_bluetoothDemibold != null) return _bluetoothDemibold!!
        _bluetoothDemibold = ImageVector.Builder(
            name = "Bluetooth.Demibold",
            defaultWidth = 24.0f.dp,
            defaultHeight = 24.0f.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f,
        ).apply {
            addPath(
                pathData = listOf(
                    PathNode.MoveTo(6.5f, 17.5f),
                        PathNode.LineTo(17.5f, 6.5f),
                        PathNode.LineTo(12.0f, 1.0f),
                        PathNode.LineTo(12.0f, 23.0f),
                        PathNode.LineTo(17.5f, 17.5f),
                        PathNode.LineTo(6.5f, 6.5f),
                ),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.8f,
                pathFillType = PathFillType.NonZero,
            )
        }.build()
        return _bluetoothDemibold!!
    }
private var _bluetoothDemibold: ImageVector? = null
