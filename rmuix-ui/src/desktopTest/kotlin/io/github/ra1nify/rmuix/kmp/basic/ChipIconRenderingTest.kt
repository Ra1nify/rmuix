// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ChipIconRenderingTest {
    @Test
    fun suggestionIconSitsAtTheStockInsets() =
        runComposeUiTest {
            setContent {
                ChipWithIcon(style = ChipStyle.Suggestion, tag = "chip")
            }
            waitForIdle()
            val chip = bounds("chip")
            val icon = bounds("icon")
            val label = onNodeWithText("Label", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
            assertClose(24f, icon.left - chip.left, "icon starts at chipStartPadding 12 + chipIconStartPadding 12")
            assertClose(14f, icon.width, "suggestion chipIconSize is 14dp")
            assertClose(42f, label.left - chip.left, "label starts after 24 + 14 + 4")
            assertClose(0f, icon.center.y - chip.center.y, "the icon is vertically centred")
        }

    @Test
    fun selectionIconSitsAtTheStockInsets() =
        runComposeUiTest {
            setContent {
                ChipWithIcon(style = ChipStyle.Selection, tag = "chip")
            }
            waitForIdle()
            val chip = bounds("chip")
            val icon = bounds("icon")
            val label = onNodeWithText("Label", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
            assertClose(12f, icon.left - chip.left, "selection chipIconStartPadding is 0dp")
            assertClose(16f, icon.width, "selection chipIconSize is 16dp")
            assertClose(32f, label.left - chip.left, "label starts after 12 + 16 + 4")
        }

    @Test
    fun iconsKeepTheSizeWhenTheyAreSmallerThanTheSlot() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Column {
                        Chip(
                            onClick = {},
                            label = "Label",
                            modifier = Modifier.testTag("chip"),
                            style = ChipStyle.Suggestion,
                            icon = {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(Color.Red)
                                        .testTag("icon"),
                                )
                            },
                        )
                    }
                }
            }
            waitForIdle()
            assertClose(14f, bounds("icon").width, "the slot sizes the icon to chipIconSize")
        }

    @androidx.compose.runtime.Composable
    private fun ChipWithIcon(
        style: ChipStyle,
        tag: String,
    ) {
        val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
        RmuixTheme(controller = controller) {
            Column {
                Chip(
                    onClick = {},
                    label = "Label",
                    modifier = Modifier.testTag(tag),
                    style = style,
                    icon = {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Red)
                                .testTag("icon"),
                        )
                    },
                )
            }
        }
    }

    private fun androidx.compose.ui.test.ComposeUiTest.bounds(tag: String): Rect =
        onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode().boundsInRoot

    private fun assertClose(
        expected: Float,
        actual: Float,
        message: String,
    ) {
        assertTrue(abs(expected - actual) < 0.5f, "$message (expected $expected, was $actual)")
    }
}
