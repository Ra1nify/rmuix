// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Alignment for window caption buttons (minimize, maximize, close).
 */
enum class CaptionButtonsAlign {
    Start,
    End,
}

/**
 * Geometric and layout information for the custom window caption bar.
 */
data class RmuixWindowInfo(
    val captionBarHeight: Dp = 32.dp,
    val captionButtonsAlign: CaptionButtonsAlign = CaptionButtonsAlign.End,
    val captionButtonsFullWidth: Dp = 0.dp,
)

val LocalRmuixWindowInfo = staticCompositionLocalOf {
    RmuixWindowInfo()
}

val LocalIsHitTestInCaptionBarState = compositionLocalOf<MutableState<Boolean>> {
    mutableStateOf(false)
}
