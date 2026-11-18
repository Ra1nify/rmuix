// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.spring
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

private val ButtonAreaEasing = CubicBezierEasing(0f, 0f, 0f, 1f)

internal fun buttonResolvedRadius(
    height: Float,
    radius: Float,
): Float = if (radius < 0f) height / 2f else radius

internal fun buttonContainer8(dark: Boolean): Color = if (dark) Color(0x1AFFFFFF) else Color(0x14000000)

internal fun buttonLabelPrimary(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

internal fun buttonDisabledSecondaryLabel(dark: Boolean): Color = if (dark) Color(0x28FFFFFF) else Color(0x42000000)

internal fun buttonDisabledPrimaryLabel(dark: Boolean): Color = if (dark) Color(0x28FFFFFF) else Color(0x8AFFFFFF)

internal fun buttonDisabledTextLabel(dark: Boolean): Color = if (dark) Color(0x4DFFFFFF) else Color(0x42000000)

internal fun buttonHoverColor(dark: Boolean): Color = if (dark) Color(0x26FFFFFF) else Color(0x14000000)

internal fun buttonPressColor(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

internal fun buttonFocusOutlineColor(dark: Boolean): Color = if (dark) Color(0x665C9DFF) else Color(0x660080FF)

internal fun buttonCompositeColor(
    foreground: Color,
    background: Color,
): Color {
    val foregroundArgb = foreground.toArgb()
    val backgroundArgb = background.toArgb()
    val foregroundAlpha = foregroundArgb ushr 24
    val backgroundAlpha = backgroundArgb ushr 24
    val alpha = 255 - (255 - backgroundAlpha) * (255 - foregroundAlpha) / 255
    if (alpha == 0) return Color.Transparent

    fun component(shift: Int): Int {
        val foregroundComponent = foregroundArgb ushr shift and 0xFF
        val backgroundComponent = backgroundArgb ushr shift and 0xFF
        return (foregroundComponent * 255 * foregroundAlpha + backgroundComponent * backgroundAlpha * (255 - foregroundAlpha)) /
            (alpha * 255)
    }
    return Color((alpha shl 24) or (component(16) shl 16) or (component(8) shl 8) or component(0))
}

internal fun buttonSecondaryContainer(dark: Boolean): Color =
    buttonCompositeColor(
        buttonContainer8(dark),
        if (dark) Color(0xFF1E1E1E) else Color.White,
    )

internal fun buttonPressScale(
    width: Float,
    height: Float,
): Float {
    val area = width * height
    if (area <= 0f) return 1f
    val normalizedArea = ((area.coerceIn(400f, 22500f) - 400f) / 22100f).coerceIn(0f, 1f)
    val areaScale = 0.85f + ButtonAreaEasing.transform(normalizedArea) * 0.12f
    val aspect = max(width / height, height / width)
    if (aspect <= 2f) return areaScale
    val aspectScale = 0.85f + ButtonAreaEasing.transform(((aspect - 2f) / 13f).coerceIn(0f, 1f)) * 0.15f
    return ((areaScale + aspectScale) / 2f).coerceIn(0.4f, 1f)
}

internal fun buttonScaleSpring(
    pressed: Boolean,
    width: Float,
    height: Float,
) = spring<Float>(
    dampingRatio =
        if (pressed) {
            1f
        } else if (min(width, height) > 0f && max(width, height) / min(width, height) > 2f) {
            0.5f
        } else {
            0.4f
        },
    stiffness =
        (
            2 * PI /
                if (!pressed) {
                    0.5
                } else if (min(width, height) > 0f &&
                    max(width, height) / min(width, height) > 2f
                ) {
                    0.3
                } else {
                    0.25
                }
        ).pow(2).toFloat(),
    visibilityThreshold = if (pressed) 0.00075f else 0.000075f,
)
