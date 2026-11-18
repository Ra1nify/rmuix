// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.utils

import androidx.compose.runtime.Composable

@Composable
actual fun WindowNavigationEventScope(content: @Composable () -> Unit) {
    // Dialogs are layered inside the host window; there is no window boundary to rebind across.
    content()
}
