// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window.platform.macos

import io.github.ra1nify.rmuix.kmp.window.RmuixWindowBackgroundType
import io.github.ra1nify.rmuix.kmp.window.internal.WindowStyler
import io.github.ra1nify.rmuix.kmp.window.internal.findSkiaLayer
import java.awt.Window

internal class MacOSRmuixWindowStyler(
    window: Window,
) : WindowStyler {
    private val skiaLayer = window.findSkiaLayer()
    private val vibrancy = MacOSWindowVibrancy(window)
    private val titleBar = MacOSWindowTitleBar(window)

    fun updateTitleBar(captionBarHeight: Float) {
        titleBar.update(captionBarHeight)
    }

    override fun updateIsResizable(value: Boolean) {}

    override fun updateBackground(type: RmuixWindowBackgroundType, isDarkTheme: Boolean) {
        val isVibrancyActive = vibrancy.update(
            enabled = type == RmuixWindowBackgroundType.Vibrancy,
            isDarkTheme = isDarkTheme,
        )
        if (isVibrancyActive) {
            skiaLayer?.transparency = true
        }
    }

    fun performWindowDrag(): Boolean = titleBar.performWindowDrag()

    fun dispose() {
        titleBar.dispose()
        vibrancy.dispose()
    }
}
