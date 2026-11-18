// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.os.Build
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
internal actual fun rememberCheckboxPressRipple(
    interactionSource: InteractionSource,
    colors: CheckboxFeedbackColors,
    radius: Dp,
): Modifier {
    val drawable = remember { RippleDrawable(ColorStateList.valueOf(0), null, null) }
    val color =
        when {
            Build.VERSION.SDK_INT >= 35 -> colors.press
            Build.VERSION.SDK_INT == 34 -> colors.android34Press
            else -> colors.legacyPress
        }
    val nativeColors = remember(color) { ColorStateList.valueOf(color.toArgb()) }
    val radiusPixels = with(LocalDensity.current) { radius.toPx().toInt() }
    return rememberNativeControlRipple(interactionSource, drawable, nativeColors, radiusPixels)
}
