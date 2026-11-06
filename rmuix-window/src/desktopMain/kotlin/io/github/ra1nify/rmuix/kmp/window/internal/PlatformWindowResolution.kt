// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window.internal

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowDecoration
import io.github.ra1nify.rmuix.kmp.window.platform.linux.LinuxWindowBackend

@OptIn(ExperimentalComposeUiApi::class)
internal fun WindowDecoration.resolveForPlatform(): WindowDecoration {
    val requiresUndecorated = DesktopOS.isLinux() &&
        this == WindowDecoration.SystemDefault &&
        LinuxWindowBackend.current != LinuxWindowBackend.FallbackSystem
    return if (requiresUndecorated) WindowDecoration.Undecorated(0.dp) else this
}
