// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectColor
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectSpring

@Immutable
internal data class CheckboxFeedbackColors(
    val hover: Color,
    val focus: Color,
    val press: Color,
    val android34Press: Color,
    val legacyPress: Color,
)

internal fun checkboxFeedbackColors(
    primary: Color,
    isDark: Boolean,
): CheckboxFeedbackColors =
    CheckboxFeedbackColors(
        hover = if (isDark) Color(0x26FFFFFF) else Color(0x14000000),
        focus = primary.copy(alpha = primary.alpha * (if (isDark) 51f else 31f) / 255f),
        press = if (isDark) Color(0x33FFFFFF) else Color(0x1F000000),
        android34Press = if (isDark) Color(0x26FFFFFF) else Color(0x14000000),
        legacyPress = if (isDark) Color(0x80FFFFFF) else Color(0x99B4B4B4),
    )

internal fun checkboxStateLayerCenter(size: Size): Offset = Offset((size.width.toInt() / 2).toFloat(), (size.height.toInt() / 2).toFloat())

@Composable
internal fun checkboxFeedback(
    interactionSource: InteractionSource,
    primary: Color,
    isDark: Boolean,
    radius: Dp,
): Modifier {
    val colors = remember(primary, isDark) { checkboxFeedbackColors(primary, isDark) }
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val spring = remember { stockStateEffectSpring() }
    val hoverProgress = animateFloatAsState(if (hovered) 1f else 0f, spring, label = "CheckboxHover")
    val focusProgress = animateFloatAsState(if (focused) 1f else 0f, spring, label = "CheckboxFocus")
    val pressRipple = rememberCheckboxPressRipple(interactionSource, colors, radius)
    return Modifier
        .drawBehind {
            val radiusPixels = radius.toPx().toInt().toFloat()
            val center = checkboxStateLayerCenter(size)
            val hoverColor = stockStateEffectColor(colors.hover, hoverProgress.value)
            val focusColor = stockStateEffectColor(colors.focus, focusProgress.value)
            if (hoverColor != Color.Transparent) drawCircle(hoverColor, radiusPixels, center)
            if (focusColor != Color.Transparent) drawCircle(focusColor, radiusPixels, center)
        }.then(pressRipple)
}

@Composable
internal expect fun rememberCheckboxPressRipple(
    interactionSource: InteractionSource,
    colors: CheckboxFeedbackColors,
    radius: Dp,
): Modifier
