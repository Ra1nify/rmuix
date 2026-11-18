// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window.platform.macos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import io.github.ra1nify.rmuix.kmp.window.CaptionButtonsAlign
import io.github.ra1nify.rmuix.kmp.window.LocalIsHitTestInCaptionBarState
import io.github.ra1nify.rmuix.kmp.window.LocalRmuixWindowInfo
import io.github.ra1nify.rmuix.kmp.window.LocalWindowState
import io.github.ra1nify.rmuix.kmp.window.RmuixWindowInfo
import io.github.ra1nify.rmuix.kmp.window.RmuixWindowProperties

@Composable
internal fun FrameWindowScope.MacOSRmuixWindowFrame(
    properties: RmuixWindowProperties<ComposeWindow>,
    content: @Composable FrameWindowScope.() -> Unit,
) {
    val isHitTestInCaptionBar = remember { mutableStateOf(false) }
    val windowState = LocalWindowState.current
    val isFullscreen = windowState.placement == WindowPlacement.Fullscreen
    val windowInfo = remember(properties.captionBarHeight, isFullscreen) {
        if (isFullscreen) {
            RmuixWindowInfo(
                captionBarHeight = 0.dp,
                captionButtonsAlign = CaptionButtonsAlign.Start,
                captionButtonsFullWidth = 0.dp,
            )
        } else {
            macOSRmuixWindowInfo(properties.captionBarHeight)
        }
    }
    val nativeCaptionBarHeight = macOSCaptionBarHeightInWindowCoordinates(
        window = window,
        captionBarHeight = windowInfo.captionBarHeight,
    )
    CompositionLocalProvider(
        LocalRmuixWindowInfo provides windowInfo,
        LocalIsHitTestInCaptionBarState provides isHitTestInCaptionBar,
    ) {
        val windowState = LocalWindowState.current
        val styler = remember(window) {
            MacOSRmuixWindowStyler(window)
        }

        DisposableEffect(styler) {
            onDispose(styler::dispose)
        }

        LaunchedEffect(properties.backgroundType, properties.backgroundIsDarkTheme) {
            styler.updateBackground(
                type = properties.backgroundType,
                isDarkTheme = properties.backgroundIsDarkTheme == true,
            )
        }

        LaunchedEffect(
            styler,
            window.isUndecorated,
            nativeCaptionBarHeight,
            isFullscreen,
        ) {
            if (!window.isUndecorated && !isFullscreen) {
                styler.updateTitleBar(nativeCaptionBarHeight)
            }
        }

        if (properties.moveable) {
            MacOSCaptionBarDragHandler(
                onDrag = styler::performWindowDrag,
                onDoubleClick = {
                    windowState.placement = when (windowState.placement) {
                        WindowPlacement.Maximized -> WindowPlacement.Floating
                        else -> WindowPlacement.Maximized
                    }
                },
                window = window,
                captionBarHeightInWindowCoordinates = nativeCaptionBarHeight,
                isHitTestInCaptionBar = isHitTestInCaptionBar.value,
                canDrag = windowState.placement != WindowPlacement.Fullscreen,
            )
        }

        content()
    }
}
