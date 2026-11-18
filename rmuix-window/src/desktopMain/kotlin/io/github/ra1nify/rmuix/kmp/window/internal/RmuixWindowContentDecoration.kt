// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window.internal

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class RmuixWindowContentDecoration(
    val inset: Dp = 0.dp,
    val cornerRadius: Dp = 0.dp,
)

internal val LocalRmuixWindowContentDecoration = compositionLocalOf {
    RmuixWindowContentDecoration()
}
