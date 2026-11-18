// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ChipRenderingTest {
    @Test
    fun chipUsesTheStockGeometryAndContainers() =
        runComposeUiTest {
            var clicked = false
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Box(Modifier.size(320.dp, 140.dp).background(Color.White)) {
                        Column(Modifier.padding(start = 20.dp, top = 20.dp)) {
                            Chip(
                                onClick = { clicked = true },
                                label = "Hello",
                                modifier = Modifier.testTag("suggestion"),
                            )
                            Chip(
                                onClick = {},
                                label = "Picked",
                                selected = true,
                                modifier = Modifier.testTag("selected"),
                            )
                            Chip(
                                onClick = {},
                                label = "Big",
                                style = ChipStyle.Selection,
                                modifier = Modifier.testTag("selection"),
                            )
                        }
                    }
                }
            }
            waitForIdle()
            val suggestion = onNodeWithTag("suggestion").fetchSemanticsNode().boundsInRoot
            val selection = onNodeWithTag("selection").fetchSemanticsNode().boundsInRoot
            assertEquals(28f, suggestion.height, 0.5f, "coui_chip_default_min_height is 28dp")
            assertEquals(32f, selection.height, 0.5f, "coui_chip_selection_style_height is 32dp")
            assertTrue(
                suggestion.width >= 52f - 0.5f && suggestion.width <= 300f,
                "coui_chip_default_min_width / _max_width bound the chip: ${suggestion.width}",
            )
            val pixels = onRoot().captureToImage().toPixelMap()
            assertCapsule(pixels, suggestion, "suggestion")
            val body = pixels[(suggestion.left + 4f).roundToInt(), suggestion.center.y.roundToInt()]
            assertTrue(
                body.red > 0.85f && body.red < 0.97f,
                "couiColorContainer8 #14000000 over white should be ~0.92, was $body",
            )
            val selectedRect = onNodeWithTag("selected").fetchSemanticsNode().boundsInRoot
            val selectedBody = pixels[(selectedRect.left + 4f).roundToInt(), selectedRect.center.y.roundToInt()]
            assertTrue(
                selectedBody.blue > 0.8f && selectedBody.red < 0.5f,
                "the checked container must be the primary colour, was $selectedBody",
            )
            onNodeWithTag("suggestion").performClick()
            assertTrue(clicked, "the chip forwards clicks")
        }

    @Test
    fun closeGlyphOnlyAppearsWithACallback() =
        runComposeUiTest {
            var closed = false
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Column(Modifier.padding(16.dp)) {
                        Chip(
                            onClick = {},
                            label = "Removable",
                            onClose = { closed = true },
                            modifier = Modifier.testTag("closable"),
                        )
                        Chip(onClick = {}, label = "Plain")
                    }
                }
            }
            waitForIdle()
            onNodeWithContentDescription("Remove").performClick()
            assertTrue(closed, "the close glyph must run onClose")
        }

    @Test
    fun selectionStyleIsCheckableWhileSuggestionStaysAButton() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Column(Modifier.padding(16.dp)) {
                        Chip(
                            onClick = {},
                            label = "Sel",
                            selected = true,
                            style = ChipStyle.Selection,
                            modifier = Modifier.testTag("selection"),
                        )
                        Chip(onClick = {}, label = "Sug", modifier = Modifier.testTag("suggestion"))
                    }
                }
            }
            waitForIdle()
            onNodeWithTag("selection").assertIsSelected()
            val suggestion = onNodeWithTag("suggestion").fetchSemanticsNode()
            assertTrue(
                suggestion.config.getOrNull(SemanticsProperties.Selected) == null,
                "the base style is android:checkable=false, so it must stay a plain button",
            )
        }

    private fun assertCapsule(
        pixels: androidx.compose.ui.graphics.PixelMap,
        rect: Rect,
        label: String,
    ) {
        val left = rect.left.roundToInt()
        val top = rect.top.roundToInt()
        val midY = rect.center.y.roundToInt()
        val inside = pixels[left + 1, midY].red < 0.99f
        val corner = pixels[left + 1, top + 1].red < 0.99f
        assertTrue(inside, "$label: the capsule must reach its left edge at mid height")
        assertTrue(!corner, "$label: a capsule must not fill its top-left corner")
        assertTrue(rect.width > rect.height, "$label: capsule bounds")
    }
}
