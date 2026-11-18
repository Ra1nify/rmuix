// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * A radio control using the stock COUI 24 dp vectors and transition timing.
 *
 * The unchecked state is a hollow 1.3 dp ring of radius 9.2 dp. The selected state contains
 * a 10 dp accent circle and a 6 dp white center. Vector transitions pulse to 1.08x and back;
 * there is no additional press-compression animation.
 *
 * @param selected Whether the [RadioButton] is currently selected.
 * @param onClick The callback to be called when the [RadioButton] is clicked. The caller is
 *   responsible for updating the state. If `null`, the [RadioButton] is not interactive.
 * @param modifier The modifier to be applied to the [RadioButton].
 * @param colors The [RadioButtonColors] of the [RadioButton].
 * @param enabled Whether the [RadioButton] is enabled.
 * @param feedbackColors The framework control-background colors, separate from the artwork colors.
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    colors: RadioButtonColors = RadioButtonDefaults.radioButtonColors(),
    enabled: Boolean = true,
    feedbackColors: RadioButtonFeedbackColors = RadioButtonDefaults.feedbackColors(),
) {
    val currentOnClickState = rememberUpdatedState(onClick)
    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)
    val interactionSource = remember { MutableInteractionSource() }
    val feedback =
        if (enabled && onClick != null) {
            rememberRadioButtonFeedback(interactionSource, feedbackColors, selected, RadioButtonDefaults.StateLayerRadius)
        } else {
            Modifier
        }

    val animation = rememberStockToggleAnimation(ToggleableState(selected), enabled)

    val finalModifier =
        if (onClick != null) {
            Modifier.selectable(
                selected = selected,
                onClick = {
                    currentOnClickState.value?.invoke()
                    if (!selected) {
                        currentHapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                    }
                },
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
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
        modifier =
            modifier
                .wrapContentSize(Alignment.Center)
                .requiredSize(RadioButtonDefaults.Size)
                .then(feedback)
                .drawWithCache {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val densityPx = size.width / RadioButtonDefaults.Size.value

                    onDrawBehind {
                        val vector = animation.transition
                        val renderedSelected = vector?.to == ToggleableState.On || (vector == null && selected)
                        val progress = if (vector == null) 1f else animation.progress.value
                        val frame = radioButtonFrame(renderedSelected, if (renderedSelected) progress else 1f - progress)
                        drawCircle(
                            color = colors.uncheckedStrokeColor(enabled),
                            radius = RadioButtonDefaults.UncheckedRingRadius.value * densityPx * frame.outerScale,
                            center = center,
                            alpha = frame.strokeAlpha,
                            style = Stroke(width = RadioButtonDefaults.StrokeWidth.toPx() * frame.outerScale),
                        )
                        drawCircle(
                            color = colors.selectedColor(enabled),
                            radius = RadioButtonDefaults.SelectedOuterRadius.value * densityPx * frame.outerScale,
                            center = center,
                            alpha = frame.fillAlpha,
                        )
                        drawCircle(
                            color =
                                if (vector != null && colors.selectedInnerColor(enabled) == Color.White) {
                                    Color(0xFFFAFAFA)
                                } else {
                                    colors.selectedInnerColor(enabled)
                                },
                            radius = RadioButtonDefaults.SelectedInnerRadius.value * densityPx * frame.innerScale,
                            center = center,
                            alpha = frame.innerAlpha,
                        )
                    }
                }.then(finalModifier),
    ) {}
}

object RadioButtonDefaults {
    /** Total control size: ColorOS `coui_btn_radio_*` drawables are 24dp. */
    val Size: Dp = 24.dp

    /**
     * Press ripple radius. **Borrowed assumption**: the APK has no COUI radio-button class — the stock
     * screens use a plain `RadioButton` with `android:button="@drawable/coui_btn_radio"`, so this value
     * reuses `checkbox_ripple_bg_radius` (20dp) rather than a radio-specific token.
     */
    val StateLayerRadius: Dp = 20.dp

    /** The press-feedback layer colours for a bare [RadioButton] (used when the caller draws its own ring). */
    @Composable
    fun feedbackColors(activatedColor: Color = platformRadioButtonActivatedColor()): RadioButtonFeedbackColors {
        val dark = isRadioButtonInDarkTheme()
        return remember(activatedColor, dark) { radioButtonFeedbackColors(activatedColor, dark) }
    }

    /** Outer circle radius when selected: `coui_btn_radio_on.xml` draws r=10 in a 24dp viewport. */
    val SelectedOuterRadius: Dp = 10.dp

    /** Inner dot radius: the second path of `coui_btn_radio_on.xml` is r=6. */
    val SelectedInnerRadius: Dp = 6.dp

    /** Unselected ring radius: `coui_btn_radio_off.xml` draws r=9.2 with `strokeWidth="1.3"`. */
    val UncheckedRingRadius: Dp = 9.2.dp

    /** Ring stroke width: `strokeWidth="1.3"` in `coui_btn_radio_off.xml` (same as the checkbox). */
    val StrokeWidth: Dp = 1.3.dp

    /** Start margin inside a preference row: `coui_preference_widget_mark_margin_left` = 16dp. */
    val MarkMarginStart: Dp = 16.dp

    /**
     * Default [RadioButtonColors] for all radio buttons.
     */
    @Composable
    fun radioButtonColors(
        selectedColor: Color = RmuixTheme.colorScheme.primary,
        disabledSelectedColor: Color = RmuixTheme.colorScheme.primary.let { it.copy(alpha = it.alpha * 0.3f) },
        uncheckedStrokeColor: Color = if (isRadioButtonInDarkTheme()) Color(0x4DFFFFFF) else Color(0x42000000),
        disabledUncheckedStrokeColor: Color = if (isRadioButtonInDarkTheme()) Color(0x26FFFFFF) else Color(0x14000000),
        selectedInnerColor: Color = Color.White,
        disabledSelectedInnerColor: Color = if (isRadioButtonInDarkTheme()) Color(0x29FFFFFF) else Color(0x8AFFFFFF),
    ): RadioButtonColors =
        remember(
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

@Composable
private fun isRadioButtonInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

@Immutable
internal data class RadioButtonFrame(
    val outerScale: Float,
    val strokeAlpha: Float,
    val fillAlpha: Float,
    val innerScale: Float,
    val innerAlpha: Float,
)

private val RadioSelectEasing = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)
private val RadioDeselectEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

internal fun radioButtonFrame(
    selected: Boolean,
    progress: Float,
): RadioButtonFrame {
    val elapsed = (if (selected) progress else 1f - progress).coerceIn(0f, 1f) * 300f
    val easing = if (selected) RadioSelectEasing else RadioDeselectEasing
    val pulse =
        if (elapsed < 150f) {
            easing.transform(elapsed / 150f)
        } else {
            1f - easing.transform((elapsed - 150f) / 150f)
        }

    fun fraction(duration: Float): Float = easing.transform((elapsed / duration).coerceIn(0f, 1f))
    return RadioButtonFrame(
        outerScale = 1f + 0.08f * pulse,
        strokeAlpha = if (selected) 1f - fraction(300f) else fraction(267f),
        fillAlpha = if (selected) fraction(300f) else 1f - fraction(200f),
        innerScale = if (selected) fraction(300f) else 1f - fraction(267f),
        innerAlpha = if (selected) fraction(300f) else 1f - fraction(200f),
    )
}
