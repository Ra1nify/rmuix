// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.glass

import androidx.compose.runtime.Composable

/**
 * Skiko implementation: no platform window blur exists, so [GlassWindowDialog] keeps the stock
 * translucent tint without blurring whatever is behind the window.
 */
@Composable
internal actual fun ApplyWindowBackgroundBlur(radiusPx: Int) = Unit
