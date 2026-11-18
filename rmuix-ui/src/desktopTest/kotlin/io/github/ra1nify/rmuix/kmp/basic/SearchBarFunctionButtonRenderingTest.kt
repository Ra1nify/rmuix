// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SearchBarFunctionButtonRenderingTest {
    @Test
    fun functionButtonIsA40dpCapsuleThatClicks() = runComposeUiTest {
        var clicked = false
        setContent {
            val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
            RmuixTheme(controller = controller) {
                Box(Modifier.size(300.dp, 100.dp).background(Color(0xFF9E9E9E))) {
                    SearchBarFunctionButton(
                        text = "Cancel",
                        onClick = { clicked = true },
                        modifier = Modifier.testTag("button").align(Alignment.TopStart),
                    )
                }
            }
        }
        waitForIdle()
        val button = onNodeWithTag("button").fetchSemanticsNode().boundsInRoot
        assertTrue(abs(button.height - 40f) <= 0.5f, "the button height is coui_search_bar_normal_background_height: ${button.height}")
        val pixels = onRoot().captureToImage().toPixelMap()
        val midY = button.center.y.roundToInt()
        val left = button.left.roundToInt()
        val top = button.top.roundToInt()
        // Capsule: the mid-left edge is filled, the top-left corner is not.
        assertTrue(pixels[left + 1, midY].red > 0.9f, "the capsule must reach its left edge at mid height")
        assertTrue(pixels[left + 1, top + 1].red < 0.9f, "a capsule must not fill its top-left corner")
        onNodeWithTag("button").performClick()
        assertTrue(clicked, "the button must forward clicks")
    }
}
