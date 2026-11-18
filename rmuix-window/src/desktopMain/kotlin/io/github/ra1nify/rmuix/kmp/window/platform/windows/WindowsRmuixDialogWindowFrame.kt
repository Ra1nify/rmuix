// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window.platform.windows

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeDialog
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.window.DialogWindowScope
import io.github.ra1nify.rmuix.kmp.window.CaptionButtonsAlign
import io.github.ra1nify.rmuix.kmp.window.LocalIsHitTestInCaptionBarState
import io.github.ra1nify.rmuix.kmp.window.LocalRmuixWindowInfo
import io.github.ra1nify.rmuix.kmp.window.RmuixWindowInfo
import io.github.ra1nify.rmuix.kmp.window.RmuixWindowProperties
import io.github.ra1nify.rmuix.kmp.window.basic.RmuixCaptionButtonClose
import io.github.ra1nify.rmuix.kmp.window.basic.RmuixCaptionButtonWidth
import io.github.ra1nify.rmuix.kmp.window.basic.rememberWindowsCaptionBarFontIconFamily
import java.awt.event.WindowEvent
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DialogWindowScope.WindowsRmuixDialogWindowFrame(
    resizable: Boolean,
    properties: RmuixWindowProperties<ComposeDialog>,
    content: @Composable DialogWindowScope.() -> Unit,
) {
    val currentProperties by rememberUpdatedState(properties)
    val isHitTestInCaptionBar = remember { mutableStateOf(false) }
    val iconFontFamily by rememberWindowsCaptionBarFontIconFamily()
    CompositionLocalProvider(
        LocalRmuixWindowInfo provides RmuixWindowInfo(
            captionBarHeight = properties.captionBarHeight,
            captionButtonsAlign = CaptionButtonsAlign.End,
            captionButtonsFullWidth = RmuixCaptionButtonWidth,
        ),
        LocalIsHitTestInCaptionBarState provides isHitTestInCaptionBar,
    ) {
        val windowClientInsets = remember { MutableWindowInsets() }

        var captionBarRect by remember { mutableStateOf(Rect.Zero) }
        var closeButtonRect by remember { mutableStateOf(Rect.Zero) }

        val styler = remember(window) {
            WindowsRmuixWindowStyler(
                window = window,
                hitTest = { x, y ->
                    when {
                        currentProperties.captionButtonsVisible &&
                            closeButtonRect.contains(x, y) -> HitTestResult.HTCLOSE

                        currentProperties.moveable &&
                            captionBarRect.contains(x, y) &&
                            isHitTestInCaptionBar.value -> HitTestResult.HTCAPTION

                        else -> HitTestResult.HTCLIENT
                    }
                },
                onWindowInsetUpdate = { windowInsets ->
                    windowClientInsets.insets = windowInsets
                },
                onResizeEdgeChange = { edge ->
                    currentProperties.onResizeEdgeChange(window, edge)
                },
            ).apply {
                updateBorderAndShadow(true)
                updateIsToolWindow(true)
            }
        }

        LaunchedEffect(resizable) {
            styler.updateIsResizable(resizable)
        }

        LaunchedEffect(properties.backgroundType, properties.backgroundIsDarkTheme) {
            styler.updateBackground(
                type = properties.backgroundType,
                isDarkTheme = properties.backgroundIsDarkTheme == true,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(windowClientInsets),
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(properties.captionBarHeight)
                    .onGloballyPositioned {
                        captionBarRect = it.boundsInWindow()
                    },
            )

            content()

            if (properties.captionButtonsVisible) {
                Row(
                    modifier = Modifier.align(Alignment.TopEnd),
                ) {
                    RmuixCaptionButtonClose(
                        onClick = {
                            window.dispatchEvent(WindowEvent(window, WindowEvent.WINDOW_CLOSING))
                        },
                        height = properties.captionButtonHeight,
                        iconFontFamily = iconFontFamily,
                        modifier = Modifier.onGloballyPositioned {
                            closeButtonRect = it.boundsInWindow()
                        },
                    )
                }
            }
        }
    }
}

private fun Rect.contains(x: Float, y: Float): Boolean = x in left..right && y in top..bottom
