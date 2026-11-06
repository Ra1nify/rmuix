// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window.platform.windows

internal enum class DwmWindowAttribute(
    val value: Int,
) {
    DWMWA_USE_IMMERSIVE_DARK_MODE(20),
    DWMWA_CAPTION_COLOR(35),
    DWMWA_SYSTEMBACKDROP_TYPE(38),
    DWMWA_MICA_EFFECT(1029),
}
