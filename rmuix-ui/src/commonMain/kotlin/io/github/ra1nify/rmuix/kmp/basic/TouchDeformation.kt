// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sign

/**
 * The rubber-band curve ColorOS uses for every touch-motion effect.
 *
 * Port of `TouchMotionRubberBand.apply(offset, curveRatio, maxDistancePx)`:
 *
 * ```java
 * return Math.signum(offset)
 *      * (1f - 1f / ((Math.abs(offset) * curveRatio) / maxDistancePx + 1f))
 *      * maxDistancePx;
 * ```
 *
 * @param offset raw drag distance (px, signed)
 * @param curveRatio how quickly the curve saturates (`iolRubberCurveRatio` 0.05 for stretching,
 *   `iolDeformRubberCurveRatio` 0.55 for deformation)
 * @param maxDistancePx the distance the curve approaches asymptotically
 */
internal fun rubberBand(
    offset: Float,
    curveRatio: Float,
    maxDistancePx: Float,
): Float {
    if (maxDistancePx <= 0f) return 0f
    if (curveRatio <= 0f) return 0f
    return sign(offset) * (1f - 1f / (abs(offset) * curveRatio / maxDistancePx + 1f)) * maxDistancePx
}

/** Inverse of [rubberBand]; port of `TouchMotionRubberBand.inverse(...)`. */
internal fun rubberBandInverse(
    result: Float,
    curveRatio: Float,
    maxDistancePx: Float,
): Float {
    if (maxDistancePx <= 0f) return result
    val magnitude = abs(result)
    if (magnitude >= maxDistancePx) return sign(result) * maxDistancePx
    val remaining = maxDistancePx - magnitude
    if (curveRatio <= 0f || remaining <= 0f) return result
    return sign(result) * (maxDistancePx * magnitude / (curveRatio * remaining))
}

/** Area-conserving deformation scale for one axis pair. */
internal data class TouchDeformScale(
    val scaleX: Float,
    val scaleY: Float,
) {
    companion object {
        /** Neutral scale, used when there is nothing to deform. */
        val None = TouchDeformScale(1f, 1f)
    }
}

private fun lerpClamped(
    start: Float,
    stop: Float,
    fraction: Float,
): Float = start + (stop - start) * fraction.coerceIn(0f, 1f)

/**
 * Port of `COUIDeformAnimatorUnit.calculateDirectionScale(distance, curveRatio, maxPx, mainAxis)`.
 *
 * The stretched axis grows towards `maxScale` along the rubber curve while the other axis shrinks by
 * the reciprocal, so the area of the blob stays roughly constant.
 */
internal fun deformDirectionScale(
    distancePx: Float,
    curveRatio: Float,
    maxDistancePx: Float,
    maxScale: Float,
    mainAxisIsX: Boolean,
): TouchDeformScale {
    if (distancePx <= 0f || maxDistancePx <= 0f) return TouchDeformScale.None
    val grown = lerpClamped(maxScale, 1f, rubberBand(distancePx, curveRatio, maxDistancePx) / maxDistancePx)
    val shrunk = if (grown > 0f) 1f / grown else 1f
    return if (mainAxisIsX) TouchDeformScale(grown, shrunk) else TouchDeformScale(shrunk, grown)
}

/**
 * Port of `COUIDeformAnimatorUnit.calculateScaleFactors(dx, dy)`.
 *
 * Both axes are deformed independently and then blended geometrically with the normalized distance
 * as the weight (`blendAreaConservingScaleFactors`), which keeps the area constant in the diagonal
 * case as well.
 */
internal fun touchDeformScale(
    dxPx: Float,
    dyPx: Float,
    curveRatio: Float,
    maxDistancePx: Float,
    maxScale: Float,
): TouchDeformScale {
    val absX = abs(dxPx)
    val absY = abs(dyPx)
    if (absX <= 0f && absY <= 0f) return TouchDeformScale.None
    val xScale = deformDirectionScale(absX, curveRatio, maxDistancePx, maxScale, mainAxisIsX = true)
    val yScale = deformDirectionScale(absY, curveRatio, maxDistancePx, maxScale, mainAxisIsX = false)
    if (absY <= 0f) return xScale
    if (absX <= 0f) return yScale
    val total = absX + absY
    val weightX = absX / total
    val weightY = absY / total
    return TouchDeformScale(
        scaleX = exp(ln(xScale.scaleX) * weightX + ln(yScale.scaleX) * weightY),
        scaleY = exp(ln(xScale.scaleY) * weightX + ln(yScale.scaleY) * weightY),
    )
}
