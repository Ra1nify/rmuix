// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.SinkFeedback
import io.github.ra1nify.rmuix.kmp.utils.pressable

/**
 * A [RadioButton] component with ColorOS 17 style.
 *
 * Selected draws two concentric filled circles — the theme-primary outer ring with a white inner dot —
 * while unselected draws a 1.3dp stroked ring. Geometry comes from the ColorOS `coui_btn_radio_*`
 * drawables (24dp on a 24 viewport): outer radius 10, inner dot 6, unselected ring 9.2, matching the
 * stroke width the checkbox uses.
 *
 * The checkmark figure the previous version drew is gone: ColorOS marks a chosen radio row with the
 * concentric circles, and the trim animation cannot survive that shape change, so the transition is
 * radius/colour tweens instead.
 *
 * @param selected Whether the [RadioButton] is currently selected.
 * @param onClick The callback to be called when the [RadioButton] is clicked. The caller is
 *   responsible for updating the state. If `null`, the [RadioButton] is not interactive.
 * @param modifier The modifier to be applied to the [RadioButton].
 * @param colors The [RadioButtonColors] of the [RadioButton].
 * @param enabled Whether the [RadioButton] is enabled.
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    colors: RadioButtonColors = RadioButtonDefaults.radioButtonColors(),
    enabled: Boolean = true,
) {
    val currentOnClickState = rememberUpdatedState(onClick)
    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)

    val transition = updateTransition(selected, label = "RadioButtonTransition")

    val outerColorState = transition.animateColor(
        transitionSpec = { tween(durationMillis = 300, easing = FastOutSlowInEasing) },
        label = "OuterColor",
    ) {
        if (it) colors.selectedColor(enabled) else colors.uncheckedStrokeColor(enabled)
    }

    // The unselected ring morphs into the filled outer circle; its radius is eased so the shape grows
    // rather than jumping between the two figures.
    val outerRadiusState = transition.animateFloat(
        transitionSpec = { tween(durationMillis = 250, easing = FastOutSlowInEasing) },
        label = "OuterRadius",
    ) { if (it) RadioButtonDefaults.SelectedOuterRadius.value else RadioButtonDefaults.UncheckedRingRadius.value }

    val strokeAlphaState = transition.animateFloat(
        transitionSpec = { tween(durationMillis = 250, easing = FastOutSlowInEasing) },
        label = "StrokeAlpha",
    ) { if (it) 0f else 1f }

    val innerScaleState = transition.animateFloat(
        transitionSpec = {
            spring(dampingRatio = 0.7f, stiffness = 600f)
        },
        label = "InnerScale",
    ) { if (it) 1f else 0f }

    val sinkFeedback = remember { SinkFeedback(sinkAmount = 0.85f, animationSpec = spring(0.99f, 986.96f)) }

    val finalModifier = if (onClick != null) {
        Modifier.selectable(
            selected = selected,
            onClick = {
                currentOnClickState.value?.invoke()
                currentHapticFeedback.performHapticFeedback(
                    if (selected) HapticFeedbackType.ToggleOff else HapticFeedbackType.ToggleOn,
                )
            },
            enabled = enabled,
            role = Role.RadioButton,
            interactionSource = null,
            indication = null,
        )
    } else {
        Modifier.semantics {
            role = Role.RadioButton
            this.selected = selected
            if (!enabled) disabled()
        }
    }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .requiredSize(RadioButtonDefaults.Size)
            .pressable(
                interactionSource = remember { MutableInteractionSource() },
                indication = sinkFeedback,
                enabled = enabled,
                delay = null,
            )
            .drawWithCache {
                val center = Offset(size.width / 2f, size.height / 2f)
                val densityPx = size.width / RadioButtonDefaults.Size.value

                onDrawBehind {
                    // Filled outer circle: primary when selected. While unselected the same circle keeps
                    // the ring stroke, so the two figures share one draw call and morph cleanly.
                    val radiusPx = outerRadiusState.value * densityPx
                    drawCircle(
                        color = outerColorState.value,
                        radius = radiusPx,
                        center = center,
                        alpha = if (selected) 1f else strokeAlphaState.value,
                    )
                    if (!selected) {
                        // Unselected keeps the ring: a stroke centred on the path, inflated by half its
                        // width so the visual outer edge matches the 10dp selected circle.
                        drawCircle(
                            color = outerColorState.value,
                            radius = radiusPx + RadioButtonDefaults.StrokeWidth.toPx() / 2f,
                            center = center,
                            style = Stroke(width = RadioButtonDefaults.StrokeWidth.toPx()),
                        )
                    } else {
                        drawCircle(
                            color = colors.selectedInnerColor(enabled),
                            radius = RadioButtonDefaults.SelectedInnerRadius.value *
                                densityPx * innerScaleState.value,
                            center = center,
                        )
                    }
                }
            }
            .then(finalModifier),
    ) {}
}

object RadioButtonDefaults {

    /** Total control size: ColorOS `coui_btn_radio_*` drawables are 24dp on a 24 viewport. */
    val Size: Dp = 24.dp

    /** Outer circle radius when selected: ColorOS draws r=10 on the 24 viewport. */
    val SelectedOuterRadius: Dp = 10.dp

    /** Inner white dot radius when selected: ColorOS draws r=6. */
    val SelectedInnerRadius: Dp = 6.dp

    /** Unselected ring radius: ColorOS draws r=9.2. */
    val UncheckedRingRadius: Dp = 9.2.dp

    /** Ring stroke width, shared with the checkbox: 1.3dp. */
    val StrokeWidth: Dp = 1.3.dp

    /** Start margin inside a preference row: `coui_preference_widget_mark_margin_left` = 16dp. */
    val MarkMarginStart: Dp = 16.dp

    /**
     * The default colors for the [RadioButton].
     *
     * Unchecked stroke and disabled tones follow the measured ColorOS pairs, the same neutrals the
     * checkbox uses.
     */
    @Composable
    fun radioButtonColors(
        selectedColor: Color = RmuixTheme.colorScheme.primary,
        disabledSelectedColor: Color = RmuixTheme.colorScheme.disabledPrimary,
        uncheckedStrokeColor: Color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
        disabledUncheckedStrokeColor: Color = RmuixTheme.colorScheme.disabledSecondary,
        selectedInnerColor: Color = Color.White,
        disabledSelectedInnerColor: Color = if (isSystemInDarkTheme()) {
            Color(0x29FFFFFF)
        } else {
            Color(0x8AFFFFFF)
        },
    ): RadioButtonColors = remember(
        selectedColor,
        disabledSelectedColor,
        uncheckedStrokeColor,
        disabledUncheckedStrokeColor,
        selectedInnerColor,
        disabledSelectedInnerColor,
    ) {
        RadioButtonColors(
            selectedColor = selectedColor,
            disabledSelectedColor = disabledSelectedColor,
            uncheckedStrokeColor = uncheckedStrokeColor,
            disabledUncheckedStrokeColor = disabledUncheckedStrokeColor,
            selectedInnerColor = selectedInnerColor,
            disabledSelectedInnerColor = disabledSelectedInnerColor,
        )
    }
}

@Immutable
data class RadioButtonColors(
    private val selectedColor: Color,
    private val disabledSelectedColor: Color,
    private val uncheckedStrokeColor: Color,
    private val disabledUncheckedStrokeColor: Color,
    private val selectedInnerColor: Color,
    private val disabledSelectedInnerColor: Color,
) {
    @Stable
    internal fun selectedColor(enabled: Boolean): Color = if (enabled) selectedColor else disabledSelectedColor

    @Stable
    internal fun uncheckedStrokeColor(enabled: Boolean): Color = if (enabled) uncheckedStrokeColor else disabledUncheckedStrokeColor

    @Stable
    internal fun selectedInnerColor(enabled: Boolean): Color = if (enabled) selectedInnerColor else disabledSelectedInnerColor
}
