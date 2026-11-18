// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

enum class ButtonDeformAnchor {
    TopCenter,
    LeftCenter,
    BottomCenter,
    Center,
    RightCenter,
}

@Immutable
data class ButtonTouchMotionConfig(
    val deformAnchor: ButtonDeformAnchor = ButtonDeformAnchor.Center,
    val autoDeformAnchor: Boolean = false,
)

val LocalButtonTouchMotionConfig = staticCompositionLocalOf { ButtonTouchMotionConfig() }
