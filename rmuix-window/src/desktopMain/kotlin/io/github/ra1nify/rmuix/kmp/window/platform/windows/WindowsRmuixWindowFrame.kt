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
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import io.github.ra1nify.rmuix.kmp.window.CaptionButtonsAlign
import io.github.ra1nify.rmuix.kmp.window.LocalIsHitTestInCaptionBarState
import io.github.ra1nify.rmuix.kmp.window.LocalRmuixWindowInfo
import io.github.ra1nify.rmuix.kmp.window.LocalWindowState
import io.github.ra1nify.rmuix.kmp.window.RmuixWindowInfo
import io.github.ra1nify.rmuix.kmp.window.RmuixWindowProperties
import io.github.ra1nify.rmuix.kmp.window.basic.RmuixCaptionButtonClose
import io.github.ra1nify.rmuix.kmp.window.basic.RmuixCaptionButtonMaximize
import io.github.ra1nify.rmuix.kmp.window.basic.RmuixCaptionButtonMinimize
import io.github.ra1nify.rmuix.kmp.window.basic.RmuixCaptionButtonWidth
import io.github.ra1nify.rmuix.kmp.window.basic.rememberWindowsCaptionBarFontIconFamily
import java.awt.event.WindowEvent

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FrameWindowScope.WindowsRmuixWindowFrame(
    resizable: Boolean,
    properties: RmuixWindowProperties<ComposeWindow>,
    content: @Composable FrameWindowScope.() -> Unit,
) {
    val windowState = LocalWindowState.current

    val currentResizable by rememberUpdatedState(resizable)
    val currentProperties by rememberUpdatedState(properties)
    val currentIsFullscreen by rememberUpdatedState(windowState.placement == WindowPlacement.Fullscreen)

    val isHitTestInCaptionBar = remember { mutableStateOf(false) }
    val effectiveCaptionBarHeight = if (currentIsFullscreen) 0.dp else properties.captionBarHeight
    val effectiveButtonsWidth = if (currentIsFullscreen || !properties.captionButtonsVisible) 0.dp else RmuixCaptionButtonWidth * 3f
    val iconFontFamily by rememberWindowsCaptionBarFontIconFamily()
    CompositionLocalProvider(
        LocalRmuixWindowInfo provides RmuixWindowInfo(
            captionBarHeight = effectiveCaptionBarHeight,
            captionButtonsAlign = CaptionButtonsAlign.End,
            captionButtonsFullWidth = effectiveButtonsWidth,
        ),
        LocalIsHitTestInCaptionBarState provides isHitTestInCaptionBar,
    ) {
        val windowClientInsets = remember { MutableWindowInsets() }

        var captionBarRect by remember { mutableStateOf(Rect.Zero) }
        var minimizeButtonRect by remember { mutableStateOf(Rect.Zero) }
        var maximizeButtonRect by remember { mutableStateOf(Rect.Zero) }
        var closeButtonRect by remember { mutableStateOf(Rect.Zero) }

        val styler = remember(window) {
            WindowsRmuixWindowStyler(
                window = window,
                hitTest = { x, y ->
                    when {
                        !currentIsFullscreen &&
                            currentProperties.captionButtonsVisible &&
                            minimizeButtonRect.contains(x, y) -> HitTestResult.HTMINBUTTON

                        !currentIsFullscreen &&
                            currentProperties.captionButtonsVisible &&
                            currentResizable &&
                            maximizeButtonRect.contains(x, y) -> HitTestResult.HTMAXBUTTON

                        !currentIsFullscreen &&
                            currentProperties.captionButtonsVisible &&
                            closeButtonRect.contains(x, y) -> HitTestResult.HTCLOSE

                        !currentIsFullscreen &&
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
            )
        }

        LaunchedEffect(resizable) {
            styler.updateIsResizable(resizable)
        }

        LaunchedEffect(currentIsFullscreen) {
            styler.updateBorderAndShadow(!currentIsFullscreen)
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
            if (!currentIsFullscreen && effectiveCaptionBarHeight > 0.dp) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(effectiveCaptionBarHeight)
                        .onGloballyPositioned {
                            captionBarRect = it.boundsInWindow()
                        },
                )
            } else {
                captionBarRect = Rect.Zero
            }

            content()

            if (properties.captionButtonsVisible && !currentIsFullscreen) {
                Row(
                    modifier = Modifier.align(Alignment.TopEnd),
                ) {
                    RmuixCaptionButtonMinimize(
                        onClick = {
                            windowState.isMinimized = true
                        },
                        height = properties.captionButtonHeight,
                        iconFontFamily = iconFontFamily,
                        modifier = Modifier.onGloballyPositioned {
                            minimizeButtonRect = it.boundsInWindow()
                        },
                        enabled = properties.minimizeButtonEnabled,
                    )
                    val isMaximized = windowState.placement == WindowPlacement.Maximized
                    RmuixCaptionButtonMaximize(
                        onClick = {
                            if (isMaximized) {
                                windowState.placement = WindowPlacement.Floating
                            } else {
                                windowState.placement = WindowPlacement.Maximized
                            }
                        },
                        maximized = isMaximized,
                        height = properties.captionButtonHeight,
                        iconFontFamily = iconFontFamily,
                        modifier = Modifier.onGloballyPositioned {
                            maximizeButtonRect = it.boundsInWindow()
                        },
                        enabled = properties.maximizeOrRestoreButtonEnabled &&
                            resizable &&
                            windowState.placement != WindowPlacement.Fullscreen,
                    )
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
