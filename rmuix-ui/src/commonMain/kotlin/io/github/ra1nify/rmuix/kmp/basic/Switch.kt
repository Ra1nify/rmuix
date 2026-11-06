// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.LocalColors
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme.isDynamicColor
import kotlin.math.absoluteValue

/**
 * A [Switch] component with ColorOS 17 styled visuals.
 *
 * The public API is unchanged from the Rmuix shape: identical parameters, identical defaults, identical
 * semantics. Only the internal rendering was re-tuned, so existing call sites need no edit.
 *
 * Geometry and colours come from the ColorOS `COUISwitch` implementation:
 *
 * | token | value |
 * | :--- | ---: |
 * | track | 44 x 24 dp |
 * | thumb | 18 dp |
 * | track inset | 3 dp |
 * | travel | 20 dp |
 *
 * The thumb carries a soft paint-style shadow (`setShadowLayer(8px, 0, 4px, 9.8% black)` in the original,
 * translated to a Compose [dropShadow]) while enabled.
 *
 * @param checked The checked state of the [Switch].
 * @param onCheckedChange The callback to be called when the state of the [Switch] changes.
 * @param modifier The modifier to be applied to the [Switch].
 * @param colors The [SwitchColors] of the [Switch].
 * @param enabled Whether the [Switch] is enabled.
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: SwitchColors = SwitchDefaults.switchColors(),
    enabled: Boolean = true,
) {
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDragged by interactionSource.collectIsDraggedAsState()

    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)
    var hasVibrated by remember { mutableStateOf(false) }
    var hasVibratedOnce by remember { mutableStateOf(false) }
    var rawDragOffset by remember { mutableFloatStateOf(0f) }
    var currentDragInteraction by remember { mutableStateOf<DragInteraction.Start?>(null) }

    val capsuleShape = CircleShape
    val thumbOffsetSpringSpec = remember { spring<Dp>(dampingRatio = 0.7f, stiffness = 987f) }

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val thumbOffsetState = animateDpAsState(
        targetValue = (if (checked) SwitchDefaults.ThumbOffsetChecked else SwitchDefaults.ThumbOffsetUnchecked) +
            dragOffset.dp,
        animationSpec = thumbOffsetSpringSpec,
    )

    val thumbColorState = animateColorAsState(
        if (checked) colors.checkedThumbColor(enabled) else colors.uncheckedThumbColor(enabled),
    )

    val backgroundColorState = animateColorAsState(
        if (checked) colors.checkedTrackColor(enabled) else colors.uncheckedTrackColor(enabled),
        animationSpec = spring(dampingRatio = 0.99f, stiffness = 438.6f),
    )

    val hasCallback = onCheckedChange != null
    val toggleableModifier = if (hasCallback) {
        remember(checked, enabled, interactionSource) {
            Modifier.toggleable(
                value = checked,
                onValueChange = { v ->
                    currentOnCheckedChange?.invoke(v)
                    currentHapticFeedback.performHapticFeedback(
                        if (v) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff,
                    )
                },
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
            )
        }
    } else {
        Modifier.semantics {
            role = Role.Switch
            toggleableState = ToggleableState(checked)
            if (!enabled) disabled()
        }
    }

    val travel = SwitchDefaults.Travel
    val progress = ((thumbOffsetState.value - SwitchDefaults.ThumbOffsetUnchecked) / travel).coerceIn(0f, 1f)
    val scaleX = if (progress < 0.34725848f) {
        1.0f + (progress / 0.34725848f) * 0.3f
    } else {
        1.3f - ((progress - 0.34725848f) / 0.65274155f) * 0.3f
    }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .size(SwitchDefaults.Width, SwitchDefaults.Height)
            .clip(capsuleShape)
            .drawBehind {
                drawRect(backgroundColorState.value)
            }
            .then(toggleableModifier),
    ) {
        val thumbShadow = if (enabled) SwitchDefaults.thumbShadow else null
        Box(
            modifier = Modifier
                .size(SwitchDefaults.ThumbSize)
                .align(Alignment.CenterStart)
                .offset {
                    IntOffset(thumbOffsetState.value.roundToPx(), 0)
                }
                .graphicsLayer {
                    this.scaleX = scaleX
                }
                .then(
                    if (thumbShadow != null) {
                        Modifier.dropShadow(shape = CircleShape, shadow = thumbShadow)
                    } else {
                        Modifier
                    },
                )
                .drawBehind {
                    drawCircle(color = thumbColorState.value)
                }
                .then(
                    if (enabled) {
                        Modifier.draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { dragAmount ->
                                rawDragOffset += dragAmount / 2f
                                dragOffset = if (checked) {
                                    rawDragOffset.coerceIn(-travel.value, 0f)
                                } else {
                                    rawDragOffset.coerceIn(0f, travel.value)
                                }

                                if (dragOffset in -11f..-10f || dragOffset in 10f..11f) {
                                    hasVibratedOnce = false
                                } else if (dragOffset in -20f..-1f || dragOffset in 1f..20f) {
                                    hasVibrated = false
                                } else if (!hasVibrated) {
                                    if ((checked && dragOffset == -travel.value) ||
                                        (!checked && dragOffset == 0f)
                                    ) {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                                        hasVibrated = true
                                        hasVibratedOnce = true
                                    } else if ((checked && dragOffset == 0f) ||
                                        (!checked && dragOffset == travel.value)
                                    ) {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                                        hasVibrated = true
                                        hasVibratedOnce = true
                                    }
                                }
                            },
                            onDragStarted = { _ ->
                                currentDragInteraction = DragInteraction.Start().also {
                                    interactionSource.tryEmit(it)
                                }
                                hasVibrated = true
                                hasVibratedOnce = false
                                rawDragOffset = 0f
                            },
                            onDragStopped = {
                                if (dragOffset.absoluteValue > travel.value / 2f) {
                                    currentOnCheckedChange?.invoke(!checked)
                                }
                                if (!hasVibratedOnce && dragOffset.absoluteValue >= 1f) {
                                    if ((checked && dragOffset <= -11f) || (!checked && dragOffset <= 10f)) {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                                    } else if ((checked && dragOffset >= -10f) || (!checked && dragOffset >= 11f)) {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                                    }
                                }
                                currentDragInteraction?.let { interactionSource.tryEmit(DragInteraction.Stop(it)) }
                                dragOffset = 0f
                                rawDragOffset = 0f
                            },
                        )
                    } else {
                        Modifier
                    },
                ),
        )
    }
}

object SwitchDefaults {

    /**
     * Track width: ColorOS `bar_width` = 44dp.
     */
    val Width: Dp = 44.dp

    /**
     * Track height: ColorOS `bar_height` = 24dp.
     */
    val Height: Dp = 24.dp

    /**
     * Thumb diameter: ColorOS `outer_circle_width` = 18dp.
     */
    val ThumbSize: Dp = 18.dp

    /**
     * Inset between the thumb and the track edge: ColorOS `circle_padding` = 3dp.
     *
     * Horisontal and vertical insets coincide: `(24 - 18) / 2 = 3`.
     */
    val ThumbPadding: Dp = 3.dp

    /**
     * Thumb travel distance, derived exactly as ColorOS does:
     * `max(barWidth - circlePadding * 2 - outerCircleWidth, 0)` = `44 - 6 - 18` = 20dp.
     */
    val Travel: Dp = Width - ThumbPadding * 2 - ThumbSize

    /** Resting x offset of the thumb when off. */
    val ThumbOffsetUnchecked: Dp = ThumbPadding

    /** Resting x offset of the thumb when on. */
    val ThumbOffsetChecked: Dp = ThumbPadding + Travel

    /**
     * Thumb shadow, matching ColorOS `setShadowLayer(8f, 0f, 4f, argb(25, 0, 0, 0))`.
     *
     * The original works in raw pixels (8px radius, 4px downward offset at density 3.125), i.e. roughly
     * 2.56dp / 1.28dp. Compose's [Shadow] takes dp and accepts an offset, so the dy is preserved at 1.3dp.
     */
    val thumbShadow: Shadow = Shadow(
        radius = 2.5.dp,
        color = Color.Black,
        alpha = 0.098f,
        offset = DpOffset(0.dp, 1.3.dp),
    )

    /**
     * The default colors for the [Switch].
     *
     * The checked track follows the Rmuix primary colour, which is the same relationship ColorOS has
     * between its switch track and `couiColorPrimary` (the app accent). Neutral colours reproduce the
     * measured ColorOS pairs: `#1F000000` / `#33FFFFFF` for the unchecked track and `#8AFFFFFF` /
     * `#29FFFFFF` for a disabled thumb in light / dark.
     */
    @Composable
    fun switchColors(
        checkedThumbColor: Color = if (isDynamicColor) LocalColors.current.onPrimary else RmuixTheme.colorScheme.onPrimary,
        uncheckedThumbColor: Color = Color.White,
        disabledCheckedThumbColor: Color = if (isSystemInDarkTheme()) {
            Color(0x29FFFFFF)
        } else {
            Color(0x8AFFFFFF)
        },
        disabledUncheckedThumbColor: Color = if (isSystemInDarkTheme()) {
            Color(0x29FFFFFF)
        } else {
            Color(0x8AFFFFFF)
        },
        checkedTrackColor: Color = RmuixTheme.colorScheme.primary,
        uncheckedTrackColor: Color = if (isSystemInDarkTheme()) {
            Color(0x33FFFFFF)
        } else {
            Color(0x1F000000)
        },
        disabledCheckedTrackColor: Color = RmuixTheme.colorScheme.disabledPrimary,
        disabledUncheckedTrackColor: Color = if (isSystemInDarkTheme()) {
            Color(0x26FFFFFF)
        } else {
            Color(0x14000000)
        },
    ): SwitchColors = remember(
        checkedThumbColor,
        uncheckedThumbColor,
        disabledCheckedThumbColor,
        disabledUncheckedThumbColor,
        checkedTrackColor,
        uncheckedTrackColor,
        disabledCheckedTrackColor,
        disabledUncheckedTrackColor,
    ) {
        SwitchColors(
            checkedThumbColor = checkedThumbColor,
            uncheckedThumbColor = uncheckedThumbColor,
            disabledCheckedThumbColor = disabledCheckedThumbColor,
            disabledUncheckedThumbColor = disabledUncheckedThumbColor,
            checkedTrackColor = checkedTrackColor,
            uncheckedTrackColor = uncheckedTrackColor,
            disabledCheckedTrackColor = disabledCheckedTrackColor,
            disabledUncheckedTrackColor = disabledUncheckedTrackColor,
        )
    }
}

@Immutable
data class SwitchColors(
    private val checkedThumbColor: Color,
    private val uncheckedThumbColor: Color,
    private val disabledCheckedThumbColor: Color,
    private val disabledUncheckedThumbColor: Color,
    private val checkedTrackColor: Color,
    private val uncheckedTrackColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledUncheckedTrackColor: Color,
) {
    @Stable
    internal fun checkedThumbColor(enabled: Boolean): Color = if (enabled) checkedThumbColor else disabledCheckedThumbColor

    @Stable
    internal fun uncheckedThumbColor(enabled: Boolean): Color = if (enabled) uncheckedThumbColor else disabledUncheckedThumbColor

    @Stable
    internal fun checkedTrackColor(enabled: Boolean): Color = if (enabled) checkedTrackColor else disabledCheckedTrackColor

    @Stable
    internal fun uncheckedTrackColor(enabled: Boolean): Color = if (enabled) uncheckedTrackColor else disabledUncheckedTrackColor
}
