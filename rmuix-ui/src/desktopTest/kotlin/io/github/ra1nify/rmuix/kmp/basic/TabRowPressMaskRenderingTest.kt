// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TabRowPressMaskRenderingTest {
    @Test
    fun pressingATabDrawsThe8dpMaskWithoutRecolouringTheLabel() =
        runComposeUiTest {
            setContent {
                // Force the light palette: the CI desktop environment may report a dark system theme.
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Box(Modifier.size(400.dp, 80.dp).background(Color.White)) {
                        TabRow(
                            tabs = listOf("Alpha", "Beta", "Gamma"),
                            selectedTabIndex = 0,
                            onTabSelected = {},
                            modifier = Modifier.testTag("row"),
                        )
                    }
                }
            }
            waitForIdle()
            // The row scrolls when the tabs are wider than it, so press the first (always laid out) tab.
            val label = onNodeWithText("Alpha").fetchSemanticsNode().boundsInRoot
            // `selectable` merges the label, so these are the tab's bounds. Sample 8dp inside the
            // top-left corner (the 8dp mask corner radius passes through that point) and away from the
            // centred glyph.
            val sampleX = (label.left + 8f).roundToInt()
            val sampleY = (label.top + 8f).roundToInt()
            val before = onRoot().captureToImage().toPixelMap()[sampleX, sampleY].red
            // Regression guard: the DstIn edge fade used to erase the whole row once it scrolled, so the
            // tab background (the row's `couiColorBar` fill) has to be visible at rest.
            assertTrue(before > 0.85f, "the scrollable tab row must not be erased by the edge fade, red=$before")

            onNodeWithText("Alpha").performTouchInput { down(center) }
            waitForIdle()
            val pixels = onRoot().captureToImage().toPixelMap()
            val pressed = pixels[sampleX, sampleY].red
            assertTrue(
                pressed < before - 0.05f,
                "pressing a tab must paint the couiColorPress mask: before=$before pressed=$pressed bounds=$label",
            )
        }
}
