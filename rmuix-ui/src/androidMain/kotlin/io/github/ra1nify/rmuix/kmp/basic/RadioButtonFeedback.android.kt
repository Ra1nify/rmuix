// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp

@Composable
internal actual fun platformRadioButtonActivatedColor(): Color {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    return remember(context, configuration) {
        val attributes = context.obtainStyledAttributes(intArrayOf(android.R.attr.colorControlActivated))
        try {
            Color(attributes.getColor(0, Color.White.toArgb()))
        } finally {
            attributes.recycle()
        }
    }
}

@Composable
internal actual fun rememberRadioButtonFeedback(
    interactionSource: InteractionSource,
    colors: RadioButtonFeedbackColors,
    selected: Boolean,
    fallbackRadius: Dp,
): Modifier {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val frameworkBackground =
        remember(context, configuration) {
            val attributes =
                context.obtainStyledAttributes(
                    null,
                    intArrayOf(android.R.attr.background),
                    0,
                    android.R.style.Widget_Material_CompoundButton_RadioButton,
                )
            try {
                attributes.getDrawable(0)?.mutate() as? RippleDrawable
            } finally {
                attributes.recycle()
            }
        }
    val drawable =
        remember(context, configuration, frameworkBackground) {
            frameworkBackground ?: RippleDrawable(ColorStateList.valueOf(0), null, null)
        }
    val nativeColors =
        remember(colors) {
            ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_checked, android.R.attr.state_enabled), intArrayOf()),
                intArrayOf(colors.checked.toArgb(), colors.unchecked.toArgb()),
            )
        }
    return rememberNativeControlRipple(
        interactionSource = interactionSource,
        drawable = drawable,
        colors = nativeColors,
        radiusPixels =
            if (frameworkBackground ==
                null
            ) {
                radioButtonFallbackRadiusPixels(with(LocalDensity.current) { fallbackRadius.toPx() })
            } else {
                null
            },
        checked = selected,
        includeHoverAndFocus = true,
        windowFocused = LocalWindowInfo.current.isWindowFocused,
    )
}
