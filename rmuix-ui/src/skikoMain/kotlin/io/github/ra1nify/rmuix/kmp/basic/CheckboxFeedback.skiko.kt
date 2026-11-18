// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
internal actual fun rememberCheckboxPressRipple(
    interactionSource: InteractionSource,
    colors: CheckboxFeedbackColors,
    radius: Dp,
): Modifier {
    val radiusPixels = with(LocalDensity.current) { radius.toPx().toInt() }
    return rememberSoftwareControlPressRipple(interactionSource, colors.press, radiusPixels)
}
