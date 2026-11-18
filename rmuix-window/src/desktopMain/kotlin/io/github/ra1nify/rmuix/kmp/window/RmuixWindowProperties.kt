// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.window.internal.DesktopOS
import io.github.ra1nify.rmuix.kmp.window.platform.macos.MacOSWindowMetrics
import java.awt.Window

@Composable
fun defaultCaptionBarHeight(): Dp = when {
    DesktopOS.isMacOS() -> MacOSWindowMetrics.defaultCaptionBarHeight
    else -> 32.dp
}

/**
 * Properties for configuring [RmuixWindow] and [RmuixDialogWindow].
 */
@Stable
data class RmuixWindowProperties<T : Window>(
    val minSize: DpSize = DpSize.Zero,
    val onVisibleChange: (T, Boolean) -> Unit = { _, _ -> },
    val onResizeEdgeChange: (T, WindowResizeEdge) -> Unit = { _, _ -> },
    val captionBarHeight: Dp = 32.dp,
    val captionButtonsVisible: Boolean = true,
    val captionButtonHeight: Dp = captionBarHeight,
    val captionButtonIsDarkTheme: Boolean? = null,
    val backgroundType: RmuixWindowBackgroundType = RmuixWindowBackgroundType.None,
    val backgroundIsDarkTheme: Boolean? = null,
    val extraDisplayScale: Float = 1.0f,
    val extraFontScale: Float = 1.0f,
    val minimizeButtonEnabled: Boolean = true,
    val maximizeOrRestoreButtonEnabled: Boolean = true,
    val moveable: Boolean = true,
) {
    companion object {
        @Composable
        fun <T : Window> default(
            minSize: DpSize = DpSize.Zero,
            onVisibleChange: (T, Boolean) -> Unit = { _, _ -> },
            onResizeEdgeChange: (T, WindowResizeEdge) -> Unit = { _, _ -> },
            captionBarHeight: Dp = defaultCaptionBarHeight(),
            captionButtonsVisible: Boolean = true,
            captionButtonHeight: Dp = captionBarHeight,
            captionButtonIsDarkTheme: Boolean? = null,
            backgroundType: RmuixWindowBackgroundType = RmuixWindowBackgroundType.None,
            backgroundIsDarkTheme: Boolean? = null,
            extraDisplayScale: Float = 1.0f,
            extraFontScale: Float = 1.0f,
            minimizeButtonEnabled: Boolean = true,
            maximizeOrRestoreButtonEnabled: Boolean = true,
            moveable: Boolean = true,
        ): RmuixWindowProperties<T> = RmuixWindowProperties(
            minSize = minSize,
            onVisibleChange = onVisibleChange,
            onResizeEdgeChange = onResizeEdgeChange,
            captionBarHeight = captionBarHeight,
            captionButtonsVisible = captionButtonsVisible,
            captionButtonHeight = captionButtonHeight,
            captionButtonIsDarkTheme = captionButtonIsDarkTheme,
            backgroundType = backgroundType,
            backgroundIsDarkTheme = backgroundIsDarkTheme,
            extraDisplayScale = extraDisplayScale,
            extraFontScale = extraFontScale,
            minimizeButtonEnabled = minimizeButtonEnabled,
            maximizeOrRestoreButtonEnabled = maximizeOrRestoreButtonEnabled,
            moveable = moveable,
        )
    }
}

val LocalRmuixWindowProperties = staticCompositionLocalOf {
    RmuixWindowProperties<Window>()
}
