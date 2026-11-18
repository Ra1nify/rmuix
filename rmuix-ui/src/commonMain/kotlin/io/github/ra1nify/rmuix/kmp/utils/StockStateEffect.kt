// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.utils

import androidx.compose.animation.core.spring
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.roundToInt

internal fun stockStateEffectSpring() =
    spring<Float>(
        dampingRatio = 1f,
        stiffness = (2 * PI / 0.3).pow(2).toFloat(),
        visibilityThreshold = 0.75f / 10000f,
    )

internal fun stockStateEffectColor(
    color: Color,
    progress: Float,
): Color {
    val fraction = progress.coerceIn(0f, 1f)
    if (fraction == 0f) return Color.Transparent
    val argb = color.toArgb()
    if (fraction == 1f) return Color(argb)

    // `StateEffectAnimator.setProgress()` calls Android's `ArgbEvaluator`: every encoded channel is
    // interpolated linearly from transparent to `color` (no gamma correction) and rounded.
    fun channel(shift: Int): Int = ((argb ushr shift and 0xFF) * fraction + 0.5f).toInt().coerceIn(0, 255)
    return Color(
        (channel(24) shl 24) or (channel(16) shl 16) or (channel(8) shl 8) or channel(0),
    )
}
