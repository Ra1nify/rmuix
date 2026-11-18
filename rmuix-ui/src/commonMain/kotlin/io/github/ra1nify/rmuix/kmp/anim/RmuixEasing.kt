// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.anim

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import kotlin.math.PI
import kotlin.math.pow

/**
 * Creates a [SpringSpec] from damping and response parameters.
 *
 * @param damping The damping ratio. 1.0 = critically damped (no overshoot),
 *   < 1.0 = underdamped (oscillates), > 1.0 = overdamped.
 * @param response The response time in seconds. Smaller values = faster animation.
 * @param visibilityThreshold The magnitude below which the animation is considered settled and the
 *   invisible settle tail is cut; null falls back to the framework default displacement threshold.
 */
fun <T> folmeSpring(
    damping: Float,
    response: Float,
    visibilityThreshold: T? = null,
): SpringSpec<T> {
    val stiffness = ((2.0 * PI / response) * (2.0 * PI / response)).toFloat()
    return spring(dampingRatio = damping, stiffness = stiffness, visibilityThreshold = visibilityThreshold)
}

/**
 * The stock (`COUISpringForce.setResponse`) stiffness for a response time: the natural frequency
 * squared, `(2*pi / response)^2` (the same number as `39.47 / response^2`). The response clamp to
 * `[0.1, 1]` and the stiffness clamp to `[100, 10000]` are library guards; the stock setter only
 * rejects a non-positive value, and every call site sits inside both guards.
 */
fun couiSpringStiffness(response: Float): Float {
    val clamped = response.coerceIn(0.1f, 1f)
    return (39.47f / clamped.pow(2)).coerceIn(100f, 10_000f)
}

/**
 * The stock damping ratio for a bounce value, exactly as `COUISpringForce.setBounce` applies it:
 * `dampingRatio = 1 - bounce` (bounce clamped to `[-1, 1]`, ratio clamped to `[0.1, 2]` so the value is
 * always a valid Compose spring).
 *
 * `com.coui.iola.SpringData.getDampingRatio()` halves the bounce, but no Settings class calls it: every
 * animator (`COUISwitch`, `COUIITouchMotionUnit`, `COUIDeformAnimatorUnit`, `COUIPressAnimatorUnit`, ...)
 * copies the raw `SpringData` bounce into `COUISpringForce.setBounce`.
 */
fun couiSpringDamping(bounce: Float): Float = (1f - bounce.coerceIn(-1f, 1f)).coerceIn(0.1f, 2f)

/**
 * A [SpringSpec] built from the stock `COUISpringForce` pair (`response`, `bounce`).
 *
 * @param response The response time in seconds, clamped to `[0.1, 1]` as in the framework.
 * @param bounce The bounce value, clamped to `[-1, 1]`; `0` is critically damped.
 * @param visibilityThreshold The magnitude below which the animation is considered settled.
 */
fun <T> couiSpring(
    response: Float,
    bounce: Float,
    visibilityThreshold: T? = null,
): SpringSpec<T> =
    spring(
        dampingRatio = couiSpringDamping(bounce),
        stiffness = couiSpringStiffness(response),
        visibilityThreshold = visibilityThreshold,
    )
