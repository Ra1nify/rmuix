// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

@Immutable
data class RadioButtonFeedbackColors(
    val checked: Color,
    val unchecked: Color,
)

@Immutable
internal data class RadioButtonBackgroundState(
    val checked: Boolean,
    val focused: Boolean,
    val hovered: Boolean,
    val pressed: Boolean,
    val windowFocused: Boolean,
)

internal fun radioButtonBackgroundTarget(state: RadioButtonBackgroundState): Float? =
    when {
        !state.focused && !state.hovered -> 0f
        state.pressed -> null
        else -> radioButtonBackgroundOpacity(state.focused, state.hovered, state.windowFocused)
    }

internal fun radioButtonFeedbackColors(
    activatedColor: Color,
    isDark: Boolean,
): RadioButtonFeedbackColors {
    val argb = activatedColor.toArgb()
    val alpha = ((argb ushr 24) * 0.26f).roundToInt()
    return RadioButtonFeedbackColors(
        checked = Color((alpha shl 24) or (argb and 0x00FFFFFF)),
        unchecked = if (isDark) Color(0x7F707070) else Color(0x7FD0D0D0),
    )
}

internal fun radioButtonBackgroundOpacity(
    focused: Boolean,
    hovered: Boolean,
    windowFocused: Boolean,
): Float =
    when {
        focused -> if (windowFocused) 0.6f else 0.2f
        hovered -> 0.2f
        else -> 0f
    }

internal fun radioButtonFallbackRadiusPixels(radius: Float): Int = radius.roundToInt()

@Composable
internal expect fun platformRadioButtonActivatedColor(): Color

@Composable
internal expect fun rememberRadioButtonFeedback(
    interactionSource: InteractionSource,
    colors: RadioButtonFeedbackColors,
    selected: Boolean,
    fallbackRadius: Dp,
): Modifier
