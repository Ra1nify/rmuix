// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.util.lerp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@Immutable
internal data class CheckboxSoftwareRippleFrame(
    val center: Offset,
    val radius: Float,
    val opacity: Float,
)

private val CheckboxRippleExpansionEasing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

internal fun checkboxSoftwareRippleExitStart(releasedAtMillis: Float): Float =
    if (releasedAtMillis > 0f && releasedAtMillis < 225f) 225f else releasedAtMillis

internal fun checkboxSoftwareRippleFrame(
    elapsedMillis: Float,
    releasedAtMillis: Float?,
    pressPosition: Offset,
    size: Size,
    radius: Float,
): CheckboxSoftwareRippleFrame {
    val center = Offset(size.width / 2f, size.height / 2f)
    val startRadius = max(size.width, size.height) * 0.3f
    val pressOrigin = if (pressPosition.x.isFinite() && pressPosition.y.isFinite()) pressPosition else center
    val displacement = pressOrigin - center
    val distance = displacement.getDistance()
    val maxDisplacement = radius - startRadius
    val origin = if (distance > abs(maxDisplacement)) center + displacement * (maxDisplacement / distance) else pressOrigin
    val expansion = CheckboxRippleExpansionEasing.transform((elapsedMillis / 225f).coerceIn(0f, 1f))
    val exitStart = releasedAtMillis?.let(::checkboxSoftwareRippleExitStart) ?: Float.POSITIVE_INFINITY
    val opacity =
        if (elapsedMillis < exitStart) {
            (elapsedMillis / 75f).coerceIn(0f, 1f)
        } else {
            (exitStart / 75f).coerceIn(0f, 1f) * (1f - (elapsedMillis - exitStart) / 150f).coerceIn(0f, 1f)
        }
    return CheckboxSoftwareRippleFrame(
        center = Offset(lerp(origin.x, center.x, expansion), lerp(origin.y, center.y, expansion)),
        radius = lerp(startRadius, radius, expansion),
        opacity = opacity,
    )
}

internal fun checkboxSoftwareRippleColor(
    color: Color,
    opacity: Float,
): Color {
    val argb = color.toArgb()
    val alpha = ((argb ushr 24) * opacity.coerceIn(0f, 1f)).roundToInt()
    return Color((alpha shl 24) or (argb and 0x00FFFFFF))
}
