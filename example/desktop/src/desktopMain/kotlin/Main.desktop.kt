// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.nucleusframework.darkmodedetector.isSystemInDarkMode
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.shared.generated.resources.Res
import io.github.ra1nify.rmuix.kmp.shared.generated.resources.icon
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.window.LocalRmuixWindowInfo
import io.github.ra1nify.rmuix.kmp.window.LocalWindowState
import io.github.ra1nify.rmuix.kmp.window.RmuixCaptionBar
import io.github.ra1nify.rmuix.kmp.window.RmuixWindow
import io.github.ra1nify.rmuix.kmp.window.RmuixWindowBackgroundType
import io.github.ra1nify.rmuix.kmp.window.RmuixWindowProperties
import io.github.ra1nify.rmuix.kmp.window.WindowDraggableArea
import org.jetbrains.compose.resources.painterResource
import ui.AppTheme
import ui.keyColorFor

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val state = rememberWindowState(
        size = DpSize(420.dp, 840.dp),
        position = WindowPosition.Aligned(Alignment.Center),
    )
    val isHotReloadMode = System.getProperty("app.mode") == "hot"
    var appState by remember { mutableStateOf(AppState()) }
    val updateAppState: ((AppState) -> AppState) -> Unit = remember {
        { transform -> appState = transform(appState) }
    }
    val systemIsDark = isSystemInDarkMode()

    AppTheme(
        colorMode = appState.colorMode,
        keyColor = keyColorFor(appState.seedIndex),
        paletteStyle = appState.paletteStyle,
        colorSpec = appState.colorSpec,
        isDark = systemIsDark,
    ) {
        val isDark = RmuixTheme.colorScheme.surface.luminance() < 0.5f
        RmuixWindow(
            state = state,
            onCloseRequest = ::exitApplication,
            alwaysOnTop = isHotReloadMode,
            title = "Rmuix",
            icon = painterResource(Res.drawable.icon),
            properties = RmuixWindowProperties.default(
                minSize = DpSize(380.dp, 600.dp),
                captionButtonIsDarkTheme = isDark,
                backgroundIsDarkTheme = isDark,
                backgroundType = when {
                    RmuixWindowBackgroundType.Vibrancy.isSupported() -> RmuixWindowBackgroundType.Vibrancy
                    RmuixWindowBackgroundType.Mica.isSupported() -> RmuixWindowBackgroundType.Mica
                    else -> RmuixWindowBackgroundType.None
                },
            ),
        ) {
            val isFullscreen = LocalWindowState.current.placement == WindowPlacement.Fullscreen
            val captionBarHeight = LocalRmuixWindowInfo.current.captionBarHeight
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RmuixTheme.colorScheme.surface),
            ) {
                if (!isFullscreen && captionBarHeight > 0.dp) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(captionBarHeight)
                            .background(RmuixTheme.colorScheme.surface),
                    ) {
                        WindowDraggableArea()
                        RmuixCaptionBar {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Spacer(Modifier.width(8.dp))
                                Image(
                                    painter = painterResource(Res.drawable.icon),
                                    contentDescription = "Rmuix",
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Rmuix",
                                    style = RmuixTheme.textStyles.body2,
                                )
                            }
                        }
                    }
                }
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    App(
                        appState = appState,
                        onUpdateAppState = updateAppState,
                    )
                }
            }
        }
    }
}
