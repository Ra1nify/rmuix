// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.SinkFeedback
import io.github.ra1nify.rmuix.kmp.utils.pressable

/**
 * A [Checkbox] component with Rmuix style, supporting three states: On, Off, and Indeterminate.
 *
 * @param state The current [ToggleableState] of the [Checkbox].
 * @param onClick The callback to be called when the [Checkbox] is clicked. The caller is
 *   responsible for updating the state. If `null`, the [Checkbox] is not interactive.
 * @param modifier The modifier to be applied to the [Checkbox].
 * @param colors The [CheckboxColors] of the [Checkbox].
 * @param enabled Whether the [Checkbox] is enabled.
 * @param enableSpotlight Whether to enable touch spotlight glow.
 * @param spotlightColor The color of the touch spotlight glow.
 */
@Composable
fun Checkbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    colors: CheckboxColors = CheckboxDefaults.checkboxColors(),
    enabled: Boolean = true,
    enableSpotlight: Boolean = true,
    spotlightColor: Color = if (isSystemInDarkTheme()) Color(0x33FFFFFF) else Color(0x1F000000),
) {
    val currentOnClickState = rememberUpdatedState(onClick)
    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)

    val transition = updateTransition(state, label = "CheckboxTransition")

    val backgroundColorState = transition.animateColor(
        transitionSpec = { tween(durationMillis = 300, easing = FastOutSlowInEasing) },
        label = "BackgroundColor",
    ) {
        if (it != ToggleableState.Off) colors.checkedBackgroundColor(enabled) else colors.uncheckedBackgroundColor(enabled)
    }

    val foregroundColorState = transition.animateColor(
        transitionSpec = { tween(durationMillis = 300, easing = FastOutSlowInEasing) },
        label = "ForegroundColor",
    ) {
        if (it != ToggleableState.Off) colors.checkedForegroundColor(enabled) else colors.uncheckedForegroundColor(enabled)
    }

    val strokeColorState = transition.animateColor(
        transitionSpec = { tween(durationMillis = 300, easing = FastOutSlowInEasing) },
        label = "StrokeColor",
    ) { colors.uncheckedStrokeColor(enabled) }

    val checkAlphaState = transition.animateFloat(
        transitionSpec = {
            if (targetState != ToggleableState.Off) {
                tween(durationMillis = 10, easing = FastOutSlowInEasing)
            } else {
                tween(durationMillis = 150, easing = FastOutSlowInEasing)
            }
        },
        label = "CheckAlpha",
    ) { if (it != ToggleableState.Off) 1f else 0f }

    val checkStartTrimState = transition.animateFloat(
        transitionSpec = {
            if (targetState != ToggleableState.Off) {
                tween(durationMillis = 200, easing = FastOutSlowInEasing)
            } else {
                keyframes {
                    durationMillis = 300
                    0.1f at 300
                }
            }
        },
        label = "CheckStartTrim",
    ) { if (it != ToggleableState.Off) 0.186f else 0.1f }

    val checkEndTrimState = transition.animateFloat(
        transitionSpec = {
            if (targetState != ToggleableState.Off) {
                keyframes {
                    durationMillis = 300
                    0.85f at 200 using FastOutSlowInEasing
                    0.803f at 300 using FastOutSlowInEasing
                }
            } else {
                keyframes {
                    durationMillis = 300
                    0.1f at 300
                }
            }
        },
        label = "CheckEndTrim",
    ) { if (it != ToggleableState.Off) 0.803f else 0.1f }

    val crossCenterGravitationState = transition.animateFloat(
        transitionSpec = {
            if (targetState == ToggleableState.Indeterminate) {
                tween(durationMillis = 200, easing = FastOutSlowInEasing)
            } else {
                tween(durationMillis = 150, easing = FastOutSlowInEasing)
            }
        },
        label = "CrossCenterGravitation",
    ) { if (it == ToggleableState.Indeterminate) 1f else 0f }

    val capsuleShape = CircleShape
    val checkPath = remember { Path() }
    val sinkFeedback = remember { SinkFeedback(sinkAmount = 0.85f, animationSpec = spring(0.99f, 986.96f)) }

    val finalModifier = if (onClick != null) {
        Modifier.triStateToggleable(
            state = state,
            onClick = {
                currentOnClickState.value?.invoke()
                currentHapticFeedback.performHapticFeedback(
                    when (state) {
                        ToggleableState.Off -> HapticFeedbackType.ToggleOn
                        ToggleableState.On -> HapticFeedbackType.ToggleOff
                        ToggleableState.Indeterminate -> HapticFeedbackType.SegmentTick
                    },
                )
            },
            enabled = enabled,
            role = Role.Checkbox,
            interactionSource = null,
        )
    } else {
        Modifier.semantics {
            role = Role.Checkbox
            toggleableState = state
            if (!enabled) disabled()
        }
    }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .requiredSize(CheckboxDefaults.Size)
            .then(
                if (enableSpotlight && enabled) {
                    Modifier.rmuixSpotlight(
                        glowColor = spotlightColor,
                        lightRadius = 40.dp,
                    )
                } else {
                    Modifier
                },
            )
            .pressable(
                interactionSource = remember { MutableInteractionSource() },
                indication = sinkFeedback,
                enabled = enabled,
                delay = null,
            )
            .clip(capsuleShape)
            .drawWithCache {
                val viewportSize = 24f
                // ColorOS artwork stroke: ~2.1 on a 24 viewport
                val strokeWidth = size.width * (2.1f / 24f)
                val centerX = size.width / 2
                val centerY = size.height / 2
                val viewportCenterX = viewportSize / 2
                val viewportCenterY = viewportSize / 2

                // Check mark vertices from coui_btn_check_on_normal.xml (24 viewport):
                // start (7.97, 11.40) -> middle (10.83, 14.32) -> end (17.03, 8.0)
                val startPoint = Offset(
                    centerX + ((7.97f - viewportCenterX) / viewportSize * size.width),
                    centerY + ((11.40f - viewportCenterY) / viewportSize * size.height),
                )
                val middlePoint = Offset(
                    centerX + ((10.83f - viewportCenterX) / viewportSize * size.width),
                    centerY + ((14.32f - viewportCenterY) / viewportSize * size.height),
                )
                val endPoint = Offset(
                    centerX + ((17.03f - viewportCenterX) / viewportSize * size.width),
                    centerY + ((8.0f - viewportCenterY) / viewportSize * size.height),
                )

                val cache = CheckmarkCache(
                    startPoint,
                    middlePoint,
                    endPoint,
                    centerX,
                    centerY,
                    strokeWidth,
                )

                val stroke = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    miter = 10.0f,
                )

                onDrawBehind {
                    // ColorOS draws a rounded square, not a circle. Unchecked is an outline; checked and
                    // part-check are filled, with the artwork knocked out of the fill.
                    val boxSize = CheckboxDefaults.BoxSize.toPx()
                    val boxTopLeft = Offset(
                        (size.width - boxSize) / 2f,
                        (size.height - boxSize) / 2f,
                    )
                    val corner = CheckboxDefaults.CornerRadius.toPx()
                    if (state == ToggleableState.Off) {
                        drawRoundRect(
                            color = strokeColorState.value,
                            topLeft = boxTopLeft,
                            size = Size(boxSize, boxSize),
                            cornerRadius = CornerRadius(corner, corner),
                            style = Stroke(width = CheckboxDefaults.StrokeWidth.toPx()),
                        )
                    } else {
                        drawRoundRect(
                            color = backgroundColorState.value,
                            topLeft = boxTopLeft,
                            size = Size(boxSize, boxSize),
                            cornerRadius = CornerRadius(corner, corner),
                        )
                    }
                    drawTrimmedCheckmark(
                        color = foregroundColorState.value,
                        alpha = checkAlphaState.value,
                        trimStart = checkStartTrimState.value,
                        trimEnd = checkEndTrimState.value,
                        crossCenterGravitation = crossCenterGravitationState.value,
                        path = checkPath,
                        cache = cache,
                        stroke = stroke,
                    )
                }
            }
            .then(finalModifier),
    ) {}
}

/**
 * A convenience overload of [Checkbox] that takes a [Boolean] value instead of a [ToggleableState].
 *
 * @param checked Whether the [Checkbox] is checked.
 * @param onCheckedChange The callback to be called when the [Checkbox] is clicked. The caller is
 *   responsible for updating the state. If `null`, the [Checkbox] is not interactive.
 * @param modifier The modifier to be applied to the [Checkbox].
 * @param colors The [CheckboxColors] of the [Checkbox].
 * @param enabled Whether the [Checkbox] is enabled.
 * @param enableSpotlight Whether to enable touch spotlight glow.
 * @param spotlightColor The color of the touch spotlight glow.
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: CheckboxColors = CheckboxDefaults.checkboxColors(),
    enabled: Boolean = true,
    enableSpotlight: Boolean = true,
    spotlightColor: Color = if (isSystemInDarkTheme()) Color(0x33FFFFFF) else Color(0x1F000000),
) {
    Checkbox(
        state = ToggleableState(checked),
        onClick = if (onCheckedChange != null) {
            { onCheckedChange(!checked) }
        } else {
            null
        },
        modifier = modifier,
        colors = colors,
        enabled = enabled,
        enableSpotlight = enableSpotlight,
        spotlightColor = spotlightColor,
    )
}

private data class CheckmarkCache(
    val startPoint: Offset,
    val middlePoint: Offset,
    val endPoint: Offset,
    val centerX: Float,
    val centerY: Float,
    val strokeWidth: Float,
)

private fun DrawScope.drawTrimmedCheckmark(
    color: Color,
    alpha: Float = 1f,
    trimStart: Float,
    trimEnd: Float,
    crossCenterGravitation: Float,
    path: Path,
    cache: CheckmarkCache,
    stroke: Stroke,
) {
    path.rewind()

    val gravitatedStart = Offset(
        cache.startPoint.x,
        lerp(cache.startPoint.y, cache.centerY, crossCenterGravitation),
    )
    val gravitatedMiddle = Offset(
        lerp(cache.middlePoint.x, cache.centerX, crossCenterGravitation),
        lerp(cache.middlePoint.y, cache.centerY, crossCenterGravitation),
    )
    val gravitatedEnd = Offset(
        cache.endPoint.x,
        lerp(cache.endPoint.y, cache.centerY, crossCenterGravitation),
    )

    val currentLength = lerp(0f, 1f, alpha)

    if (currentLength > 0f) {
        val totalLength = lerp(0f, 1f, trimEnd) - lerp(0f, 1f, trimStart)
        if (totalLength > 0f) {
            val actualStart = lerp(0f, 1f, trimStart)
            val actualEnd = lerp(0f, 1f, trimEnd)

            val p1 = gravitatedStart
            val p2 = gravitatedMiddle
            val p3 = gravitatedEnd

            val d12 = (p2 - p1).getDistance()
            val d23 = (p3 - p2).getDistance()
            val totalDist = d12 + d23

            if (totalDist > 0f) {
                val startDist = actualStart * totalDist
                val endDist = actualEnd * totalDist

                val startPointOnPath = getPointOnTwoSegmentPath(p1, p2, p3, d12, startDist)
                val endPointOnPath = getPointOnTwoSegmentPath(p1, p2, p3, d12, endDist)

                path.moveTo(startPointOnPath.x, startPointOnPath.y)

                if (startDist <= d12 && endDist >= d12) {
                    path.lineTo(p2.x, p2.y)
                }
                path.lineTo(endPointOnPath.x, endPointOnPath.y)

                drawPath(
                    path = path,
                    color = color.copy(alpha = color.alpha * alpha),
                    style = stroke,
                )
            }
        }
    }
}

private fun getPointOnTwoSegmentPath(
    p1: Offset,
    p2: Offset,
    p3: Offset,
    d12: Float,
    targetDistance: Float,
): Offset = if (targetDistance <= d12) {
    val fraction = if (d12 > 0f) targetDistance / d12 else 0f
    Offset(
        lerp(p1.x, p2.x, fraction),
        lerp(p1.y, p2.y, fraction),
    )
} else {
    val remainingDist = targetDistance - d12
    val d23 = (p3 - p2).getDistance()
    val fraction = if (d23 > 0f) remainingDist / d23 else 0f
    Offset(
        lerp(p2.x, p3.x, fraction),
        lerp(p2.y, p3.y, fraction),
    )
}

object CheckboxDefaults {

    /**
     * The overall default layout size of a [Checkbox] (touch area).
     *
     * ColorOS uses a 24dp touch target.
     */
    val Size: Dp = 24.dp

    /**
     * The size of the drawn checkbox box itself inside the [Size] layout.
     *
     * ColorOS `coui_btn_check_on_normal.xml` viewport width/height is 24x24.
     */
    val BoxSize: Dp = 20.dp

    /**
     * The corner radius of the drawn checkbox box.
     *
     * ColorOS uses a 6dp corner for a 20dp box (`coui_checkbox_radius` = 6dp).
     */
    val CornerRadius: Dp = 6.dp

    /**
     * The stroke width of the unchecked checkbox outline.
     */
    val StrokeWidth: Dp = 1.5.dp

    /**
     * The default [CheckboxColors] for all checkboxes.
     */
    @Composable
    fun checkboxColors(
        checkedBackgroundColor: Color = RmuixTheme.colorScheme.primary,
        uncheckedBackgroundColor: Color = Color.Transparent,
        checkedForegroundColor: Color = RmuixTheme.colorScheme.onPrimary,
        uncheckedForegroundColor: Color = Color.Transparent,
        uncheckedStrokeColor: Color = if (isSystemInDarkTheme()) Color(0x33FFFFFF) else Color(0x1F000000),
        disabledCheckedBackgroundColor: Color = RmuixTheme.colorScheme.disabledPrimaryButton,
        disabledUncheckedBackgroundColor: Color = Color.Transparent,
        disabledCheckedForegroundColor: Color = RmuixTheme.colorScheme.disabledOnPrimaryButton,
        disabledUncheckedForegroundColor: Color = Color.Transparent,
        disabledUncheckedStrokeColor: Color = RmuixTheme.colorScheme.disabledSecondaryVariant,
    ): CheckboxColors = remember(
        checkedBackgroundColor,
        uncheckedBackgroundColor,
        checkedForegroundColor,
        uncheckedForegroundColor,
        uncheckedStrokeColor,
        disabledCheckedBackgroundColor,
        disabledUncheckedBackgroundColor,
        disabledCheckedForegroundColor,
        disabledUncheckedForegroundColor,
        disabledUncheckedStrokeColor,
    ) {
        CheckboxColors(
            checkedBackgroundColor = checkedBackgroundColor,
            uncheckedBackgroundColor = uncheckedBackgroundColor,
            checkedForegroundColor = checkedForegroundColor,
            uncheckedForegroundColor = uncheckedForegroundColor,
            uncheckedStrokeColor = uncheckedStrokeColor,
            disabledCheckedBackgroundColor = disabledCheckedBackgroundColor,
            disabledUncheckedBackgroundColor = disabledUncheckedBackgroundColor,
            disabledCheckedForegroundColor = disabledCheckedForegroundColor,
            disabledUncheckedForegroundColor = disabledUncheckedForegroundColor,
            disabledUncheckedStrokeColor = disabledUncheckedStrokeColor,
        )
    }
}

@Immutable
data class CheckboxColors(
    private val checkedBackgroundColor: Color,
    private val uncheckedBackgroundColor: Color,
    private val checkedForegroundColor: Color,
    private val uncheckedForegroundColor: Color,
    private val uncheckedStrokeColor: Color,
    private val disabledCheckedBackgroundColor: Color,
    private val disabledUncheckedBackgroundColor: Color,
    private val disabledCheckedForegroundColor: Color,
    private val disabledUncheckedForegroundColor: Color,
    private val disabledUncheckedStrokeColor: Color,
) {
    internal fun checkedBackgroundColor(enabled: Boolean): Color = if (enabled) checkedBackgroundColor else disabledCheckedBackgroundColor
    internal fun uncheckedBackgroundColor(enabled: Boolean): Color = if (enabled) uncheckedBackgroundColor else disabledUncheckedBackgroundColor
    internal fun checkedForegroundColor(enabled: Boolean): Color = if (enabled) checkedForegroundColor else disabledCheckedForegroundColor
    internal fun uncheckedForegroundColor(enabled: Boolean): Color = if (enabled) uncheckedForegroundColor else disabledUncheckedForegroundColor
    internal fun uncheckedStrokeColor(enabled: Boolean): Color = if (enabled) uncheckedStrokeColor else disabledUncheckedStrokeColor
}
