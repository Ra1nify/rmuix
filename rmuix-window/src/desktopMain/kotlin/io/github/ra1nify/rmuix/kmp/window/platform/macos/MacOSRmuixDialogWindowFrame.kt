// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window.platform.macos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.awt.ComposeDialog
import androidx.compose.ui.window.DialogWindowScope
import io.github.ra1nify.rmuix.kmp.window.LocalIsHitTestInCaptionBarState
import io.github.ra1nify.rmuix.kmp.window.LocalRmuixWindowInfo
import io.github.ra1nify.rmuix.kmp.window.RmuixWindowProperties

@Composable
internal fun DialogWindowScope.MacOSRmuixDialogWindowFrame(
    properties: RmuixWindowProperties<ComposeDialog>,
    content: @Composable DialogWindowScope.() -> Unit,
) {
    val isHitTestInCaptionBar = remember { mutableStateOf(false) }
    val windowInfo = remember(properties.captionBarHeight) {
        macOSRmuixWindowInfo(properties.captionBarHeight)
    }
    val nativeCaptionBarHeight = macOSCaptionBarHeightInWindowCoordinates(
        window = window,
        captionBarHeight = windowInfo.captionBarHeight,
    )
    CompositionLocalProvider(
        LocalRmuixWindowInfo provides windowInfo,
        LocalIsHitTestInCaptionBarState provides isHitTestInCaptionBar,
    ) {
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

        LaunchedEffect(styler, window.isUndecorated, nativeCaptionBarHeight) {
            if (!window.isUndecorated) {
                styler.updateTitleBar(nativeCaptionBarHeight)
            }
        }

        if (properties.moveable) {
            MacOSCaptionBarDragHandler(
                onDrag = styler::performWindowDrag,
                onDoubleClick = null,
                window = window,
                captionBarHeightInWindowCoordinates = nativeCaptionBarHeight,
                isHitTestInCaptionBar = isHitTestInCaptionBar.value,
            )
        }

        content()
    }
}
