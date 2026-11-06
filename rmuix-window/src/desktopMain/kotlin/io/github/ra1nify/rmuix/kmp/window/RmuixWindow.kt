// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.awt.SwingWindow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.LocalWindowExceptionHandlerFactory
import androidx.compose.ui.window.WindowDecoration
import androidx.compose.ui.window.WindowExceptionHandler
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.window.internal.DesktopOS
import io.github.ra1nify.rmuix.kmp.window.internal.createSafeWindowExceptionHandlerFactory
import io.github.ra1nify.rmuix.kmp.window.internal.findSkiaLayer
import io.github.ra1nify.rmuix.kmp.window.internal.hackContentPane
import io.github.ra1nify.rmuix.kmp.window.internal.isIgnorableComposeDisposalException
import io.github.ra1nify.rmuix.kmp.window.internal.rememberDesktopSystemDarkTheme
import io.github.ra1nify.rmuix.kmp.window.internal.resolveForPlatform
import io.github.ra1nify.rmuix.kmp.window.platform.linux.LinuxClientShadow
import io.github.ra1nify.rmuix.kmp.window.platform.linux.LinuxRmuixWindowFrame
import io.github.ra1nify.rmuix.kmp.window.platform.macos.MacOSRmuixWindowFrame
import io.github.ra1nify.rmuix.kmp.window.platform.windows.WindowsRmuixWindowFrame
import java.awt.Dimension
import java.awt.Window
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RmuixWindow(
    onCloseRequest: () -> Unit,
    state: WindowState = rememberWindowState(),
    visible: Boolean = true,
    title: String = "Untitled",
    icon: Painter? = null,
    decoration: WindowDecoration = WindowDecoration.SystemDefault,
    transparent: Boolean = false,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    alwaysOnTop: Boolean = false,
    properties: RmuixWindowProperties<ComposeWindow> = RmuixWindowProperties.default(),
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    init: (ComposeWindow) -> Unit = {},
    content: @Composable FrameWindowScope.() -> Unit,
) {
    require(properties.captionButtonHeight <= properties.captionBarHeight) {
        "Caption button height must be less than or equal to caption bar height"
    }

    val currentProperties by rememberUpdatedState(properties)
    val resolvedDecoration = decoration.resolveForPlatform()
    val resolvedTransparent = transparent ||
        (DesktopOS.isLinux() && LinuxClientShadow.shouldUseClientShadow(decoration))

    val currentExceptionHandlerFactory = LocalWindowExceptionHandlerFactory.current
    val safeExceptionHandlerFactory = remember(currentExceptionHandlerFactory) {
        createSafeWindowExceptionHandlerFactory(currentExceptionHandlerFactory)
    }

    CompositionLocalProvider(
        LocalWindowExceptionHandlerFactory provides safeExceptionHandlerFactory,
    ) {
        SwingWindow(
            onCloseRequest = onCloseRequest,
            state = state,
            visible = visible,
            title = title,
            icon = icon,
            decoration = resolvedDecoration,
            transparent = resolvedTransparent,
            resizable = resizable,
            enabled = enabled,
            focusable = focusable,
            alwaysOnTop = alwaysOnTop,
            onPreviewKeyEvent = onPreviewKeyEvent,
            onKeyEvent = onKeyEvent,
            init = { window ->
                window.background = java.awt.Color.BLACK
                window.findSkiaLayer()?.transparency = true
                window.hackContentPane()
                val existingHandler = window.exceptionHandler
                if (existingHandler != null) {
                    window.exceptionHandler = WindowExceptionHandler { throwable ->
                        if (isIgnorableComposeDisposalException(throwable)) {
                            return@WindowExceptionHandler
                        }
                        existingHandler.onException(throwable)
                    }
                }
                init(window)
            },
        ) {
            val density = LocalDensity.current
            val isSystemDark = rememberDesktopSystemDarkTheme()
            val resolvedCaptionButtonIsDarkTheme = properties.captionButtonIsDarkTheme ?: isSystemDark
            val resolvedBackgroundIsDarkTheme = properties.backgroundIsDarkTheme ?: isSystemDark
            val resolvedProperties = remember(properties, resolvedCaptionButtonIsDarkTheme, resolvedBackgroundIsDarkTheme) {
                properties.copy(
                    captionButtonIsDarkTheme = resolvedCaptionButtonIsDarkTheme,
                    backgroundIsDarkTheme = resolvedBackgroundIsDarkTheme,
                )
            }

            @Suppress("UNCHECKED_CAST")
            CompositionLocalProvider(
                LocalWindowState provides state,
                LocalWindowDraggableAreaRegistration provides remember(window) {
                    WindowDraggableAreaRegistration()
                },
                LocalRmuixWindowProperties provides resolvedProperties as RmuixWindowProperties<Window>,
                LocalDensity provides Density(
                    density = density.density * resolvedProperties.extraDisplayScale,
                    fontScale = density.fontScale * resolvedProperties.extraFontScale,
                ),
            ) {
                val minSize = properties.minSize
                LaunchedEffect(minSize) {
                    if (minSize.width.isSpecified && minSize.height.isSpecified) {
                        window.minimumSize = Dimension(minSize.width.value.toInt(), minSize.height.value.toInt())
                    }
                }

                DisposableEffect(window) {
                    val adapter = object : WindowAdapter(), ComponentListener {
                        override fun windowActivated(e: WindowEvent?) {}
                        override fun windowDeactivated(e: WindowEvent?) {}
                        override fun windowIconified(e: WindowEvent?) {}
                        override fun windowDeiconified(e: WindowEvent?) {}
                        override fun windowStateChanged(e: WindowEvent) {}
                        override fun componentResized(e: ComponentEvent?) {}
                        override fun componentMoved(e: ComponentEvent?) {}
                        override fun componentShown(e: ComponentEvent?) {
                            currentProperties.onVisibleChange(window, true)
                        }
                        override fun componentHidden(e: ComponentEvent?) {
                            currentProperties.onVisibleChange(window, false)
                        }
                    }

                    window.addWindowListener(adapter)
                    window.addWindowStateListener(adapter)
                    window.addComponentListener(adapter)

                    onDispose {
                        window.removeWindowListener(adapter)
                        window.removeWindowStateListener(adapter)
                        window.removeComponentListener(adapter)
                    }
                }

                WindowBackgroundBox(
                    transparent = resolvedTransparent,
                    backgroundType = resolvedProperties.backgroundType,
                ) {
                    when (DesktopOS.current) {
                        is DesktopOS.Windows ->
                            WindowsRmuixWindowFrame(
                                resizable = resizable,
                                properties = resolvedProperties,
                                content = content,
                            )

                        is DesktopOS.MacOS ->
                            MacOSRmuixWindowFrame(
                                properties = resolvedProperties,
                                content = content,
                            )

                        is DesktopOS.Linux ->
                            LinuxRmuixWindowFrame(
                                resizable = resizable,
                                decoration = decoration,
                                properties = resolvedProperties,
                                content = content,
                            )

                        else -> content()
                    }
                }
            }
        }
    }
}

val LocalWindowState = staticCompositionLocalOf<WindowState> {
    error("LocalWindowState is not provided")
}

@Composable
internal fun WindowBackgroundBox(
    transparent: Boolean,
    backgroundType: RmuixWindowBackgroundType,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val shouldDrawBackground = when (DesktopOS.current) {
        is DesktopOS.Windows ->
            !transparent && (
                backgroundType == RmuixWindowBackgroundType.None ||
                    backgroundType == RmuixWindowBackgroundType.Vibrancy
                )

        is DesktopOS.MacOS ->
            !transparent && backgroundType != RmuixWindowBackgroundType.Vibrancy

        else -> !transparent
    }

    val backgroundColor = RmuixTheme.colorScheme.surface

    Box(
        modifier = modifier.thenIf(shouldDrawBackground) {
            background(backgroundColor)
        },
        content = content,
    )
}

private inline fun Modifier.thenIf(condition: Boolean, crossinline other: Modifier.() -> Modifier): Modifier = if (condition) other() else this
