// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
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

/** The search bar's capsule height and the leading icon inset come straight from `coui_search_bar_*`. */
@OptIn(ExperimentalTestApi::class)
class SearchBarGeometryTest {
    @Test
    fun barKeepsTheStockFieldHeight() =
        runComposeUiTest {
            setContent { Bar() }
            waitForIdle()
            val bar = bounds("bar")
            assertClose(SearchBarDefaults.InputFieldMinHeight.value, bar.height, "`coui_search_bar_normal_background_height`")
        }

    @Test
    fun placeholderStartsAfterTheLeadingIcon() =
        runComposeUiTest {
            setContent { Bar() }
            waitForIdle()
            val bar = bounds("bar")
            val placeholder = onNodeWithText("Search settings", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
            assertTrue(
                placeholder.left > bar.left + SearchBarDefaults.InsideMargin.width.value,
                "the text starts after the 12dp inside margin (bar=${bar.left}, text=${placeholder.left})",
            )
        }

    @androidx.compose.runtime.Composable
    private fun Bar() {
        val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
        RmuixTheme(controller = controller) {
            Column {
                SearchBar(
                    inputField = {
                        InputField(
                            query = "",
                            onQueryChange = {},
                            onSearch = {},
                            expanded = false,
                            onExpandedChange = {},
                            label = "Search settings",
                        )
                    },
                    onExpandedChange = {},
                    modifier =
                        Modifier
                            .width(400.dp)
                            .testTag("bar"),
                ) { }
            }
        }
    }

    private fun ComposeUiTest.bounds(tag: String): Rect = onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot

    private fun assertClose(
        expected: Float,
        actual: Float,
        message: String,
    ) {
        assertTrue(abs(expected - actual) < 1.5f, "$message (expected $expected, was $actual)")
    }
}
