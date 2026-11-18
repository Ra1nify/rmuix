// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithText
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
class TopAppBarGeometryTest {
    @Test
    fun subtitleStartsAtTheCollapsedTitleRow() =
        runComposeUiTest {
            setContent {
                Harness {
                    TopAppBar(
                        title = "Small",
                        largeTitle = "",
                        subtitle = "Subtitle",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            waitForIdle()
            val title = onNodeWithText("Small", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
            val subtitle = onNodeWithText("Subtitle", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
            assertClose(title.left, subtitle.left, "the subtitle shares the collapsed title's start")
            assertTrue(
                subtitle.top >= title.bottom - 0.5f,
                "the subtitle sits below the title (title bottom ${title.bottom}, subtitle top ${subtitle.top})",
            )
        }

    @Test
    fun defaultBarIsTheStaticStockToolbar() =
        runComposeUiTest {
            setContent {
                Harness {
                    TopAppBar(
                        title = "Settings",
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .testTag("bar"),
                    )
                }
            }
            waitForIdle()
            val bar = onNodeWithTag("bar").fetchSemanticsNode().boundsInRoot
            assertClose(52f, bar.height, "the default bar is the single 52dp `COUIToolbar` row")
            assertTrue(
                onAllNodesWithText("Settings", useUnmergedTree = true).fetchSemanticsNodes().size == 1,
                "no duplicate expanded title is drawn when `largeTitle` is left at its empty default",
            )
        }

    @Test
    fun largeTitleDoesNotMoveOrFadeWhileScrolling() =
        runComposeUiTest {
            val state = TopAppBarState(0f, 0f, 0f)
            setContent {
                Harness {
                    TopAppBar(
                        title = "Display",
                        largeTitle = "Display",
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .testTag("bar"),
                        scrollBehavior = RmuixScrollBehavior(state),
                    )
                }
            }
            waitForIdle()
            val bar = onNodeWithTag("bar").fetchSemanticsNode().boundsInRoot

            fun largeTitleTop(): Float {
                val bounds =
                    onAllNodesWithText("Display", useUnmergedTree = true)
                        .fetchSemanticsNodes()
                        .map { it.boundsInRoot }
                return bounds.maxByOrNull { it.top }!!.top
            }
            val expandedTop = largeTitleTop()
            // ColorOS keeps the expanded title at a fixed 54dp and full alpha; it does not slide or fade
            // with the scroll offset (`fixCollapsingTitleAlpha()` disables the collapsing fade).
            runOnIdle {
                state.heightOffset = state.heightOffsetLimit * 0.5f
                state.contentOffset = -state.expandedHeightPx * 0.5f
            }
            waitForIdle()
            assertClose(expandedTop, largeTitleTop(), "the large title must not move with the scroll")
            assertClose(54f, largeTitleTop() - bar.top, "`coui_appbar_title_expanded_margin_top` stays fixed")
        }

    @Test
    fun expandedLargeTitleUsesTheStockMarginsAndHeight() =
        runComposeUiTest {
            setContent {
                Harness {
                    TopAppBar(
                        title = "Display",
                        largeTitle = "Display",
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .testTag("bar"),
                    )
                }
            }
            waitForIdle()
            val bar = onNodeWithTag("bar").fetchSemanticsNode().boundsInRoot
            val largeBounds =
                onAllNodesWithText("Display", useUnmergedTree = true)
                    .fetchSemanticsNodes()
                    .map { it.boundsInRoot }
            val large = largeBounds.maxByOrNull { it.top } ?: error("the large title must be rendered")
            assertClose(110f, bar.height, "`coui_appbar_title_expanded_height`")
            assertClose(24f, large.left - bar.left, "`coui_appbar_title_expanded_margin_start`")
            assertClose(54f, large.top - bar.top, "`coui_appbar_title_expanded_margin_top`")
        }

    @androidx.compose.runtime.Composable
    private fun Harness(content: @androidx.compose.runtime.Composable () -> Unit) {
        val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
        RmuixTheme(controller = controller) {
            Column(Modifier.width(420.dp)) {
                content()
            }
        }
    }

    private fun assertClose(
        expected: Float,
        actual: Float,
        message: String,
    ) {
        assertTrue(abs(expected - actual) < 1.5f, "$message (expected $expected, was $actual)")
    }
}
