// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.theme

import androidx.compose.runtime.staticCompositionLocalOf
import io.github.ra1nify.rmuix.kmp.overlay.OverlayBottomSheet
import io.github.ra1nify.rmuix.kmp.overlay.OverlayDialog
import io.github.ra1nify.rmuix.kmp.overlay.OverlayListPopup
import io.github.ra1nify.rmuix.kmp.window.WindowBottomSheet
import io.github.ra1nify.rmuix.kmp.window.WindowDialog
import io.github.ra1nify.rmuix.kmp.window.WindowListPopup

/**
 * CompositionLocal that provides a dismiss request function for overlay components.
 *
 * This is automatically provided by all overlay components ([OverlayDialog], [WindowDialog],
 * [OverlayBottomSheet], [WindowBottomSheet], [OverlayListPopup], [WindowListPopup]).
 *
 * Call the provided function to request dismissal from inside overlay content:
 * ```kotlin
 * val dismiss = LocalDismissState.current
 * Button(onClick = { dismiss?.invoke() }) { Text("Close") }
 * ```
 */
val LocalDismissState = staticCompositionLocalOf<(() -> Unit)?> { null }
