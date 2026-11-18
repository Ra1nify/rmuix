// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import io.github.ra1nify.rmuix.kmp.anim.couiSpring
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

@Immutable
internal data class ButtonDeformScale(
    val horizontal: Float,
    val vertical: Float,
)

internal fun buttonDeformMaximumScale(
    width: Float,
    height: Float,
): Float {
    val area = (width * height).toInt()
    val shorter = min(width, height)
    if (area <= 38416 || shorter == 0f) return 1.2f
    val aspect = max(width, height) / shorter
    val decay = min(0.35 * ln(area.toDouble() / 38416.0) + 0.015 * (aspect - 1f).toDouble(), 1.0)
    return max((1.2f - 1.03f) * (1.0 - decay).toFloat() + 1.03f, 1.03f)
}

internal fun buttonDeformScale(
    offset: Offset,
    widthPixels: Float,
    heightPixels: Float,
    density: Float,
): ButtonDeformScale {
    val horizontal = abs(offset.x)
    val vertical = abs(offset.y)
    val distance = horizontal + vertical
    val limit = 150f * density
    if (distance <= 0f || limit <= 0f) return ButtonDeformScale(1f, 1f)
    val maximum = buttonDeformMaximumScale(widthPixels, heightPixels)

    fun stretch(value: Float): Float = 1f + buttonRubberCurve(value, 0.55f, limit) / limit * (maximum - 1f)
    val horizontalStretch = stretch(horizontal)
    val verticalStretch = stretch(vertical)
    if (vertical <= 0f) return ButtonDeformScale(horizontalStretch, 1f / horizontalStretch)
    if (horizontal <= 0f) return ButtonDeformScale(1f / verticalStretch, verticalStretch)
    val horizontalWeight = horizontal / distance
    val verticalWeight = vertical / distance
    return ButtonDeformScale(
        exp(
            (
                ln(horizontalStretch.toDouble()).toFloat() * horizontalWeight +
                    ln((1f / verticalStretch).toDouble()).toFloat() * verticalWeight
            ).toDouble(),
        ).toFloat(),
        exp(
            (
                ln((1f / horizontalStretch).toDouble()).toFloat() * horizontalWeight +
                    ln(verticalStretch.toDouble()).toFloat() * verticalWeight
            ).toDouble(),
        ).toFloat(),
    )
}

internal fun buttonDeformEffectiveOffset(
    scale: ButtonDeformScale,
    width: Float,
    height: Float,
    density: Float,
): Offset {
    val horizontal = scale.horizontal >= scale.vertical
    val dominant = if (horizontal) scale.horizontal else scale.vertical
    val maximumDelta = buttonDeformMaximumScale(width, height) - 1f
    val limit = 150f * density
    val effective =
        when {
            dominant == 1f || limit <= 0f -> 0f
            maximumDelta <= 0f -> 1f
            else -> buttonRubberCurveInverse(min((dominant - 1f) / maximumDelta, 1f) * limit, 0.55f, limit)
        }
    return if (horizontal) Offset(effective, 0f) else Offset(0f, effective)
}

internal fun buttonDeformAtRest(scale: Float): Boolean = abs(scale - 1f) < 0.002f

internal fun buttonDeformHandUp(
    width: Float,
    height: Float,
    density: Float,
    velocity: Float,
    scale: Float,
): ButtonRubberHandUp = buttonRubberHandUp(width, height, density, velocity, scale - 1f)

internal fun buttonDeformSpring(
    response: Float,
    bounce: Float,
) = couiSpring(
    response = response,
    // `COUIPressAnimatorUnit` / `COUIITouchMotionUnit` feed the raw bounce into
    // `COUISpringForce.setBounce`, whose damping is `1 - bounce`.
    bounce = bounce,
    visibilityThreshold = 0.0005f * 0.75f,
)

internal fun buttonCombinedScale(
    uniform: Float,
    deformed: Float,
): Float = uniform + deformed - 1f

internal fun buttonResolveAutoAnchor(
    offset: Offset,
    previous: ButtonDeformAnchor?,
): ButtonDeformAnchor? {
    val horizontal = abs(offset.x)
    val vertical = abs(offset.y)
    if (horizontal == 0f && vertical == 0f) return null
    if (horizontal == vertical && previous != null) return previous
    return if (horizontal > vertical) {
        if (offset.x < 0f) ButtonDeformAnchor.RightCenter else ButtonDeformAnchor.LeftCenter
    } else {
        if (offset.y < 0f) ButtonDeformAnchor.BottomCenter else ButtonDeformAnchor.TopCenter
    }
}

internal fun buttonAnchorTranslation(
    anchor: ButtonDeformAnchor,
    width: Float,
    height: Float,
    horizontalScale: Float,
    verticalScale: Float,
): Offset {
    val pivotX =
        when (anchor) {
            ButtonDeformAnchor.LeftCenter -> 0f
            ButtonDeformAnchor.RightCenter -> width
            else -> width * 0.5f
        }
    val pivotY =
        when (anchor) {
            ButtonDeformAnchor.TopCenter -> 0f
            ButtonDeformAnchor.BottomCenter -> height
            else -> height * 0.5f
        }
    return Offset(
        if (width > 0f && width.isFinite()) (width * 0.5f - pivotX) * (horizontalScale - 1f) else 0f,
        if (height > 0f && height.isFinite()) (height * 0.5f - pivotY) * (verticalScale - 1f) else 0f,
    )
}
