// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class PageSurfaceRenderingTest {
    @Test
    fun scaffoldAndTopBarUseTheStockBackgroundWithCardColor() = runComposeUiTest {
        setContent {
            val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
            RmuixTheme(controller = controller) {
                Scaffold(
                    topBar = { TopAppBar(title = "Title") },
                ) { padding ->
                    Box(Modifier.fillMaxSize().padding(padding))
                }
            }
        }
        waitForIdle()
        val pixels = onRoot().captureToImage().toPixelMap()
        val topBar = pixels[pixels.width / 2, 2]
        val page = pixels[pixels.width / 2, pixels.height - 2]
        assertEquals(Color(0xFFF0F1F2), topBar, "the top bar must use couiColorBackgroundWithCard")
        assertEquals(Color(0xFFF0F1F2), page, "the page must use couiColorBackgroundWithCard")
    }

    @Test
    fun darkScaffoldUsesTheStockBlackPageBackground() = runComposeUiTest {
        setContent {
            val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Dark) }
            RmuixTheme(controller = controller) {
                Scaffold { padding ->
                    Box(Modifier.fillMaxSize().padding(padding))
                }
            }
        }
        waitForIdle()
        val pixels = onRoot().captureToImage().toPixelMap()
        val page = pixels[pixels.width / 2, pixels.height / 2]
        assertEquals(Color(0xFF000000), page, "the dark page must use coui_color_background_with_card_dark")
    }
}
