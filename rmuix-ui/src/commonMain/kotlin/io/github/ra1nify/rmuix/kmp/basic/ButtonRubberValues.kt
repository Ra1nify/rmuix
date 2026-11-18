// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sign

private val ButtonHandUpAreaEasing = CubicBezierEasing(0.55026454f, 0f, 0.8915344f, 0.18783069f)

internal fun buttonRubberLimit(density: Float): Float = round(28f * density)

internal fun buttonRubberCurve(
    offset: Float,
    curveRatio: Float,
    limit: Float,
): Float {
    if (limit <= 0f) return 0f
    return (1f - 1f / (curveRatio * abs(offset) / limit + 1f)) * limit * sign(offset)
}

internal fun buttonRubberCurveInverse(
    result: Float,
    curveRatio: Float,
    limit: Float,
): Float {
    if (limit <= 0f || curveRatio <= 0f) return result
    val distance = abs(result)
    if (distance >= limit) return limit * sign(result)
    return limit * distance / (curveRatio * (limit - distance)) * sign(result)
}

internal fun buttonRubberOffset(
    offset: Float,
    density: Float,
): Float = buttonRubberCurve(offset, 0.05f, buttonRubberLimit(density))

internal fun buttonRubberInverse(
    result: Float,
    density: Float,
): Float = buttonRubberCurveInverse(result, 0.05f, buttonRubberLimit(density))

internal fun buttonDragPassesDeadZone(offset: Offset): Boolean = abs(offset.x) >= 5f || abs(offset.y) >= 5f

internal fun buttonRubberRebaseOrigin(
    origin: Offset,
    previous: Offset,
    next: Offset,
): Offset = next - (previous - origin)

internal fun buttonRubberDragSpring() =
    spring<Float>(
        dampingRatio = 1f - 0.15f,
        stiffness = (2 * PI / 0.15f).pow(2).toFloat(),
        visibilityThreshold = 0.5f * 0.75f,
    )

@Immutable
internal data class ButtonRubberHandUp(
    val response: Float,
    val bounce: Float,
    val velocity: Float,
)

internal fun buttonRubberHandUp(
    widthPixels: Float,
    heightPixels: Float,
    density: Float,
    velocityPixelsPerSecond: Float,
    restMagnitude: Float,
): ButtonRubberHandUp {
    val smallWidth = 56f * density
    val largeWidth = 100f * density
    val area = widthPixels * heightPixels
    val fraction =
        if (area <=
            0f
        ) {
            0f
        } else {
            ((area - smallWidth * smallWidth) / (largeWidth * largeWidth - smallWidth * smallWidth)).coerceIn(0f, 1f)
        }
    val attenuation = ButtonHandUpAreaEasing.transform(fraction)
    val response = 0.42f + (0.45f - 0.42f) * attenuation
    val totalDistance = max(28f * density, 1f)
    val trackedVelocity = velocityPixelsPerSecond / 1000f * 0.3f
    val velocity = (trackedVelocity / totalDistance * abs(restMagnitude)).coerceIn(-1000f, 1000f)
    val bounce = max(0.65f, (abs(velocity) / 1000f).coerceAtMost(1f))
    return ButtonRubberHandUp(response, bounce, velocity)
}

internal fun buttonRubberReturnSpring(parameters: ButtonRubberHandUp) =
    spring<Float>(
        dampingRatio = 1f - parameters.bounce,
        stiffness = (2 * PI / parameters.response).pow(2).toFloat(),
        visibilityThreshold = 0.5f * 0.75f,
    )
