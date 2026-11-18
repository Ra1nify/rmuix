// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * A three-state checkbox based on the stock COUI drawable resources.
 *
 * The 24 dp viewport contains an 18 dp checked box with a 4 dp corner radius.
 * Unchecked outlines, checked glyphs and indeterminate offsets follow their individual vectors.
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
    enableSpotlight: Boolean = false,
    spotlightColor: Color = Color.White,
) {
    val currentOnClickState = rememberUpdatedState(onClick)
    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val interactionSource = remember { MutableInteractionSource() }
    val feedback =
        if (enabled && onClick != null) {
            checkboxFeedback(interactionSource, RmuixTheme.colorScheme.primary, isCheckboxInDarkTheme(), CheckboxDefaults.StateLayerRadius)
        } else {
            Modifier
        }

    val animation = rememberStockToggleAnimation(state, enabled, animateDisabledPartTransitions = true)

    val disabledOutlineColor = if (isCheckboxInDarkTheme()) Color(0x4DFFFFFF) else Color(0x42000000)

    val checkPath = remember { Path() }
    val stockCheckPath = remember { checkboxCheckPath() }
    val dashPath = remember { Path() }
    val dashClipPath = remember { Path() }
    val animatedBoxPath = remember { checkboxAnimatedBoxPath() }

    val finalModifier =
        if (onClick != null) {
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
                interactionSource = interactionSource,
                indication = null,
            )
        } else {
            Modifier.semantics {
                role = Role.Checkbox
                toggleableState = state
                if (!enabled) disabled()
            }
        }

    Box(
        modifier =
            modifier
                .wrapContentSize(Alignment.Center)
                .requiredSize(CheckboxDefaults.Size)
                .then(feedback)
                .then(
                    if (enableSpotlight && enabled && onClick != null) {
                        Modifier.rmuixSpotlight(
                            glowColor = spotlightColor,
                            darkTheme = isCheckboxInDarkTheme(),
                        )
                    } else {
                        Modifier
                    },
                ).drawWithCache {
                    val viewportSize = 24f
                    val strokeWidth = size.width * (1.3f / 24f)
                    val centerX = size.width / 2
                    val centerY = size.height / 2

                    val startPoint =
                        Offset(
                            centerX + (-14.12f / 3f / viewportSize * size.width),
                            centerY + ((-1f + 0.764f) / 3f / viewportSize * size.height),
                        )
                    val middlePoint =
                        Offset(
                            centerX + (-3.44f / 3f / viewportSize * size.width),
                            centerY + ((9.69f + 0.764f) / 3f / viewportSize * size.height),
                        )
                    val endPoint =
                        Offset(
                            centerX + (16.97f / 3f / viewportSize * size.width),
                            centerY + ((-10.91f + 0.764f) / 3f / viewportSize * size.height),
                        )

                    val cache =
                        CheckmarkCache(
                            startPoint,
                            middlePoint,
                            endPoint,
                        )

                    val stroke =
                        Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Butt,
                            join = StrokeJoin.Round,
                            miter = 10.0f,
                        )

                    onDrawBehind {
                        val vector = animation.transition
                        if (vector == null) {
                            withTransform({ scale(size.width / 24f, size.height / 24f, pivot = Offset.Zero) }) {
                                drawCheckboxResting(state, colors, enabled, stockCheckPath, isRtl)
                            }
                        } else {
                            val initialState = vector.from
                            val targetState = vector.to
                            val elapsed = animation.progress.value * vector.durationMillis
                            val frame = checkboxTransitionFrame(initialState, targetState, elapsed)
                            val unit = size.width / 24f
                            // These are *viewport* units of the 24-unit source vector, not dp:
                            // the caller already scaled the canvas by `size.width / 24f`, so `toPx()`
                            // would multiply by the density a second time (16.4dp became 51dp and the
                            // unchecked ring wrapped far outside the 24dp box).
                            val uncheckedBoxSize = CheckboxDefaults.UncheckedBoxSize.value
                            val uncheckedCornerRadius = CheckboxDefaults.UncheckedCornerRadius.value
                            val uncheckedBoxTopLeft = CheckboxDefaults.UncheckedBoxTopLeft.value
                            if (frame.outlineWidth > 0f) {
                                withTransform({ scale(frame.outlineScale, frame.outlineScale) }) {
                                    drawRoundRect(
                                        color =
                                            if (initialState == ToggleableState.Off && targetState == ToggleableState.Indeterminate) {
                                                disabledOutlineColor
                                            } else {
                                                colors.uncheckedStrokeColor(enabled)
                                            },
                                        topLeft = Offset(uncheckedBoxTopLeft * unit, uncheckedBoxTopLeft * unit),
                                        size = Size(uncheckedBoxSize * unit, uncheckedBoxSize * unit),
                                        cornerRadius =
                                            CornerRadius(uncheckedCornerRadius * unit),
                                        style = Stroke(width = frame.outlineWidth * unit),
                                    )
                                }
                            }
                            withTransform({
                                scale(frame.boxScale, frame.boxScale)
                                scale(unit / 3f, unit / 3f, pivot = Offset.Zero)
                                translate(36f, 36f)
                            }) {
                                drawPath(animatedBoxPath, colors.checkedBackgroundColor(enabled), alpha = frame.fillAlpha)
                            }
                            if (frame.trimEnd > 0f) {
                                withTransform({ scale(frame.markScale, frame.markScale) }) {
                                    drawTrimmedCheckmark(
                                        color = colors.checkedForegroundColor(enabled),
                                        trimEnd = frame.trimEnd,
                                        path = checkPath,
                                        cache = cache,
                                        stroke = stroke,
                                    )
                                }
                            }
                            if (initialState == ToggleableState.Indeterminate || targetState == ToggleableState.Indeterminate) {
                                withTransform({
                                    scale(frame.dashScaleX, frame.dashScaleY)
                                    scale(unit / 3f, unit / 3f, pivot = Offset.Zero)
                                    translate(36f, 36f)
                                }) {
                                    checkboxDashPath(dashPath, frame.dashWidth)
                                    checkboxDashClipPath(dashClipPath, frame.dashClip)
                                    clipPath(dashClipPath) {
                                        drawPath(dashPath, colors.checkedForegroundColor(enabled))
                                    }
                                }
                            }
                        }
                    }
                }.then(finalModifier),
    ) {}
}

/**
 * A convenience overload of [Checkbox] that takes a [Boolean] value instead of a [ToggleableState].
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: CheckboxColors = CheckboxDefaults.checkboxColors(),
    enabled: Boolean = true,
    enableSpotlight: Boolean = false,
    spotlightColor: Color = Color.White,
) {
    Checkbox(
        state = ToggleableState(checked),
        onClick =
            if (onCheckedChange != null) {
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
) {
    val firstLength = (middlePoint - startPoint).getDistance()
    val lastLength = (endPoint - middlePoint).getDistance()
}

private fun DrawScope.drawTrimmedCheckmark(
    color: Color,
    trimEnd: Float,
    path: Path,
    cache: CheckmarkCache,
    stroke: Stroke,
) {
    path.rewind()
    val endDistance = trimEnd.coerceIn(0f, 1f) * (cache.firstLength + cache.lastLength)
    if (endDistance <= 0f) return
    path.moveTo(cache.startPoint.x, cache.startPoint.y)
    if (endDistance <= cache.firstLength) {
        val fraction = endDistance / cache.firstLength
        path.lineTo(
            lerp(cache.startPoint.x, cache.middlePoint.x, fraction),
            lerp(cache.startPoint.y, cache.middlePoint.y, fraction),
        )
    } else {
        path.lineTo(cache.middlePoint.x, cache.middlePoint.y)
        val fraction = (endDistance - cache.firstLength) / cache.lastLength
        path.lineTo(
            lerp(cache.middlePoint.x, cache.endPoint.x, fraction),
            lerp(cache.middlePoint.y, cache.endPoint.y, fraction),
        )
    }
    drawPath(path = path, color = color, style = stroke)
}

object CheckboxDefaults {
    /**
     * Touch target size of a [Checkbox]. The stock drawables (`coui_btn_check_off/on_normal`) are
     * declared as 24dp × 24dp vectors.
     */
    val Size: Dp = 24.dp

    /**
     * Box size of the drawn checkbox box: the visible square inside the 24dp vector is 18dp.
     */
    val BoxSize: Dp = 18.dp

    /**
     * Corner radius from the stock **checked** vector (`coui_btn_check_on_normal`: 18dp box, r=4).
     */
    val CornerRadius: Dp = 4.dp

    /**
     * Size of the **unchecked** box: `coui_btn_check_off_normal.xml` draws from 3.8 to 20.2, i.e.
     * 16.4dp — smaller than the checked 18dp. The two states are not scaled copies.
     */
    val UncheckedBoxSize: Dp = 16.4.dp

    /** Corner radius of the unchecked box: `3.2` in the same vector. */
    val UncheckedCornerRadius: Dp = 3.2.dp

    /** Top-left inset of the unchecked box inside the 24dp vector. */
    val UncheckedBoxTopLeft: Dp = 3.8.dp

    /**
     * Top inset of the **indeterminate** box: `coui_btn_part_check_on_normal.xml` starts at y=3.666
     * instead of 3, i.e. the dash state is nudged down by 0.666dp while keeping the 18dp size.
     */
    val IndeterminateTopInset: Dp = 3.666.dp

    /**
     * Border stroke width of the unchecked checkbox outline: `strokeWidth="1.3"` in
     * `coui_btn_check_off_normal.xml`. The box itself is a rounded square of 16.4dp with a 3.2dp
     * corner radius, smaller than the 18dp checked box — the two states are not scaled copies.
     */
    val StrokeWidth: Dp = 1.3.dp

    /**
     * Press ripple radius: `checkbox_ripple_bg_radius` = 20dp, resolved by
     * `COUIMaskRippleDrawable.getMaskRippleRadiusByType(context, 1)`.
     */
    val StateLayerRadius: Dp = 20.dp

    /**
     * Default [CheckboxColors] for all checkboxes.
     */
    @Composable
    fun checkboxColors(
        checkedBackgroundColor: Color = RmuixTheme.colorScheme.primary,
        uncheckedBackgroundColor: Color = Color.Transparent,
        checkedForegroundColor: Color = Color.White,
        uncheckedForegroundColor: Color = Color.Transparent,
        uncheckedStrokeColor: Color = if (isCheckboxInDarkTheme()) Color(0x4DFFFFFF) else Color(0x42000000),
        disabledCheckedBackgroundColor: Color = RmuixTheme.colorScheme.primary.let { it.copy(alpha = it.alpha * 0.3f) },
        disabledUncheckedBackgroundColor: Color = Color.Transparent,
        disabledCheckedForegroundColor: Color = if (isCheckboxInDarkTheme()) Color(0x29FFFFFF) else Color(0x8AFFFFFF),
        disabledUncheckedForegroundColor: Color = Color.Transparent,
        disabledUncheckedStrokeColor: Color = if (isCheckboxInDarkTheme()) Color(0x26FFFFFF) else Color(0x14000000),
    ): CheckboxColors =
        remember(
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

@Composable
private fun isCheckboxInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

internal fun checkboxCheckPath(): Path =
    Path().apply {
        moveTo(10.831f, 14.318f)
        lineTo(17.033f, 8f)
        lineTo(18f, 8.985f)
        lineTo(11.315f, 15.796f)
        cubicTo(11.047f, 16.068f, 10.614f, 16.068f, 10.347f, 15.796f)
        lineTo(7f, 12.386f)
        lineTo(7.967f, 11.401f)
        close()
    }

private fun DrawScope.drawCheckboxResting(
    state: ToggleableState,
    colors: CheckboxColors,
    enabled: Boolean,
    checkPath: Path,
    isRtl: Boolean,
) {
    if (state == ToggleableState.Off) {
        // Viewport units again - drawCheckboxResting runs inside the 24-unit scale above.
        val boxSize = CheckboxDefaults.UncheckedBoxSize.value
        val boxCorner = CheckboxDefaults.UncheckedCornerRadius.value
        val boxTopLeft = CheckboxDefaults.UncheckedBoxTopLeft.value
        drawRoundRect(
            color = colors.uncheckedBackgroundColor(enabled),
            topLeft = Offset(boxTopLeft, boxTopLeft),
            size = Size(boxSize, boxSize),
            cornerRadius = CornerRadius(boxCorner),
        )
        drawRoundRect(
            color = colors.uncheckedStrokeColor(enabled),
            topLeft = Offset(boxTopLeft, boxTopLeft),
            size = Size(boxSize, boxSize),
            cornerRadius = CornerRadius(boxCorner),
            style = Stroke(width = CheckboxDefaults.StrokeWidth.value),
        )
    } else {
        drawRoundRect(
            color = colors.checkedBackgroundColor(enabled),
            topLeft =
                Offset(
                    3f,
                    if (state == ToggleableState.Indeterminate) {
                        // Viewport unit too - see the note above `drawCheckboxResting`'s Off branch.
                        CheckboxDefaults.IndeterminateTopInset.value
                    } else {
                        3f
                    },
                ),
            size = Size(18f, 18f),
            cornerRadius = CornerRadius(4f),
        )
        if (state == ToggleableState.On) {
            withTransform({ if (!enabled && isRtl) scale(-1f, 1f, pivot = Offset(12f, 12f)) }) {
                drawPath(checkPath, colors.checkedForegroundColor(enabled))
            }
        } else {
            drawRect(
                color = colors.checkedForegroundColor(enabled),
                topLeft = Offset(7f, if (enabled) 11.866f else 11.5f),
                size = Size(10f, if (enabled) 1.6f else 1.5f),
            )
        }
    }
}

@Immutable
internal data class CheckboxTransitionFrame(
    val boxScale: Float,
    val outlineScale: Float,
    val markScale: Float,
    val dashScaleX: Float,
    val dashScaleY: Float,
    val fillAlpha: Float,
    val trimEnd: Float,
    val dashClip: Float,
    val dashWidth: Float,
    val outlineWidth: Float,
)

private val CheckboxSelectEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)
private val CheckboxMarkReturnEasing = CubicBezierEasing(0.3f, 0f, 0.633f, 1f)
private val CheckboxDashEnterEasing = CubicBezierEasing(0.167f, 0f, 0.1f, 1f)

internal fun checkboxTransitionFrame(
    initialState: ToggleableState,
    targetState: ToggleableState,
    elapsed: Float,
): CheckboxTransitionFrame {
    val entering = initialState == ToggleableState.Off
    val leaving = targetState == ToggleableState.Off

    fun eased(
        duration: Float,
        delay: Float = 0f,
    ): Float = CheckboxSelectEasing.transform(((elapsed - delay) / duration).coerceIn(0f, 1f))

    fun pulse(returnEasing: CubicBezierEasing = CheckboxSelectEasing): Float =
        1f + 0.08f *
            if (elapsed < 150f) {
                eased(150f)
            } else {
                1f - returnEasing.transform(((elapsed - 150f) / 150f).coerceIn(0f, 1f))
            }
    val partToSelected = initialState == ToggleableState.Indeterminate && targetState == ToggleableState.On
    val dashScaleX: Float
    val dashScaleY: Float
    if (partToSelected) {
        val entry = CheckboxDashEnterEasing.transform((elapsed / 150f).coerceIn(0f, 1f))
        val innerScale = if (elapsed < 150f) 1f + 0.08f * entry else 1.08f - 0.08f * eased(150f, 150f)
        val outerScaleX = if (elapsed < 150f) 1f + 0.08f * entry else 1.08f - 0.08f * eased(150f, 201f)
        dashScaleX = innerScale * outerScaleX
        dashScaleY = innerScale * innerScale
    } else {
        dashScaleX = pulse()
        dashScaleY = dashScaleX
    }
    val dashClip =
        when {
            initialState == ToggleableState.Indeterminate -> 1f - eased(if (leaving) 300f else 200f)
            targetState == ToggleableState.Indeterminate -> eased(if (entering) 300f else 200f, if (entering) 0f else 100f)
            else -> 0f
        }
    val dashWidth =
        if (targetState == ToggleableState.Indeterminate) {
            ((elapsed - if (entering) 0f else 100f) / if (entering) 300f else 200f).coerceIn(0f, 1f)
        } else {
            dashClip
        }
    return CheckboxTransitionFrame(
        boxScale = pulse(),
        outlineScale = if (entering && targetState == ToggleableState.Indeterminate) pulse(CheckboxMarkReturnEasing) else pulse(),
        markScale =
            if (entering) {
                pulse(CheckboxMarkReturnEasing)
            } else if (leaving) {
                pulse()
            } else {
                1f
            },
        dashScaleX = dashScaleX,
        dashScaleY = dashScaleY,
        fillAlpha =
            if (entering) {
                eased(150f)
            } else if (leaving) {
                1f - eased(150f, 150f)
            } else {
                1f
            },
        trimEnd =
            if (targetState == ToggleableState.On) {
                eased(300f)
            } else if (initialState == ToggleableState.On) {
                1f - eased(300f)
            } else {
                0f
            },
        dashClip = dashClip,
        dashWidth = dashWidth,
        outlineWidth =
            if (entering) {
                1.6f
            } else if (leaving) {
                1.3f
            } else {
                0f
            },
    )
}

private fun checkboxDashPath(
    path: Path,
    fraction: Float,
) {
    val right = lerp(-15f, 15f, fraction)
    path.rewind()
    path.moveTo(right, -3f)
    path.cubicTo(right, -3f, right, 3f, right, 3f)
    path.cubicTo(right, 3f, -15f, 3f, -15f, 3f)
    path.cubicTo(-15f, 3f, -15f, -3f, -15f, -3f)
    path.cubicTo(-15f, -3f, right, -3f, right, -3f)
    path.close()
}

private fun checkboxAnimatedBoxPath(): Path =
    Path().apply {
        moveTo(27f, -15f)
        cubicTo(27f, -15f, 27f, 15f, 27f, 15f)
        cubicTo(27f, 21.62f, 21.62f, 27f, 15f, 27f)
        cubicTo(15f, 27f, -15f, 27f, -15f, 27f)
        cubicTo(-21.62f, 27f, -27f, 21.62f, -27f, 15f)
        cubicTo(-27f, 15f, -27f, -15f, -27f, -15f)
        cubicTo(-27f, -21.62f, -21.62f, -27f, -15f, -27f)
        cubicTo(-15f, -27f, 15f, -27f, 15f, -27f)
        cubicTo(21.62f, -27f, 27f, -21.62f, 27f, -15f)
        close()
    }

private fun checkboxDashClipPath(
    path: Path,
    fraction: Float,
) {
    val right = lerp(-15f, 15f, fraction)
    val lowerRight = lerp(-15.01f, 14.99f, fraction)
    path.rewind()
    path.moveTo(right, -3f)
    path.cubicTo(right, -3f, -15f, -3f, -15f, -3f)
    path.cubicTo(-15f, -3f, -14.98f, 3f, -14.98f, 3f)
    path.cubicTo(-14.98f, 3f, lowerRight, 3f, lowerRight, 3f)
    path.cubicTo(lowerRight, 3f, right, -3f, right, -3f)
    path.close()
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
    @Stable
    internal fun checkedBackgroundColor(enabled: Boolean): Color = if (enabled) checkedBackgroundColor else disabledCheckedBackgroundColor

    @Stable
    internal fun uncheckedBackgroundColor(enabled: Boolean): Color =
        if (enabled) uncheckedBackgroundColor else disabledUncheckedBackgroundColor

    @Stable
    internal fun checkedForegroundColor(enabled: Boolean): Color = if (enabled) checkedForegroundColor else disabledCheckedForegroundColor

    @Stable
    internal fun uncheckedForegroundColor(enabled: Boolean): Color =
        if (enabled) uncheckedForegroundColor else disabledUncheckedForegroundColor

    @Stable
    internal fun uncheckedStrokeColor(enabled: Boolean): Color = if (enabled) uncheckedStrokeColor else disabledUncheckedStrokeColor
}
