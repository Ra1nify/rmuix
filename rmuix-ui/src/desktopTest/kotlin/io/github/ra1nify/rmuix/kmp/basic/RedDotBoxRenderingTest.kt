// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class RedDotBoxRenderingTest {
    @Test
    fun rectangleAnchorGrowsByTheMargin() =
        runComposeUiTest {
            setContent {
                Harness(direction = LayoutDirection.Ltr) {
                    RedDotBox(
                        redDot = { Dot() },
                        modifier = Modifier.testTag("box"),
                        anchorSize = 36.dp,
                        anchorShape = RedDotAnchorShape.Rectangle,
                        redDotKind = RedDotAnchorKind.Dot,
                    ) {
                        Anchor()
                    }
                }
            }
            waitForIdle()
            val box = bounds("box")
            val anchor = bounds("anchor")
            val dot = bounds("dot")
            assertClose(38f, box.width, "rectangle dot container width 36 + 2")
            assertClose(38f, box.height, "rectangle dot container height 36 + 2")
            assertClose(box.top + 2f, anchor.top, "the anchor is offset by the margin")
            assertClose(box.left, anchor.left, "LTR keeps the anchor on the left")
            assertClose(box.right, dot.right, "the dot stays flush with the container in LTR")
            assertClose(box.top, dot.top, "the dot sits at the very top")
        }

    @Test
    fun numberAnchorUsesTheWiderMargin() =
        runComposeUiTest {
            setContent {
                Harness(direction = LayoutDirection.Ltr) {
                    RedDotBox(
                        redDot = { Dot() },
                        modifier = Modifier.testTag("box"),
                        anchorSize = 36.dp,
                        anchorShape = RedDotAnchorShape.Rectangle,
                        redDotKind = RedDotAnchorKind.Number,
                    ) {
                        Anchor()
                    }
                }
            }
            waitForIdle()
            val box = bounds("box")
            assertClose(39f, box.width, "medium number margin is 3dp")
            assertClose(39f, box.height, "medium number margin is 3dp")
        }

    @Test
    fun circleAnchorKeepsTheAnchorSizeAndInsetsTheDot() =
        runComposeUiTest {
            setContent {
                Harness(direction = LayoutDirection.Ltr) {
                    RedDotBox(
                        redDot = { Dot() },
                        modifier = Modifier.testTag("box"),
                        anchorSize = 36.dp,
                        anchorShape = RedDotAnchorShape.Circle,
                        redDotKind = RedDotAnchorKind.Dot,
                    ) {
                        Anchor()
                    }
                }
            }
            waitForIdle()
            val box = bounds("box")
            val anchor = bounds("anchor")
            val dot = bounds("dot")
            assertClose(36f, box.width, "circle anchors keep the anchor size")
            assertClose(box.left, anchor.left, "circle anchors are not offset")
            assertClose(box.top, anchor.top, "circle anchors are not offset")
            assertClose(box.right - 2f, dot.right, "circle dot is inset by 2dp")
            assertClose(box.top + 2f, dot.top, "circle dot is inset by 2dp")
        }

    @Test
    fun rtlPlacesTheDotAtTheMirroredCorner() =
        runComposeUiTest {
            setContent {
                Harness(direction = LayoutDirection.Rtl) {
                    RedDotBox(
                        redDot = { Dot() },
                        modifier = Modifier.testTag("box"),
                        anchorSize = 50.dp,
                        anchorShape = RedDotAnchorShape.Rectangle,
                        redDotKind = RedDotAnchorKind.Number,
                    ) {
                        Anchor(size = 50.dp)
                    }
                }
            }
            waitForIdle()
            val box = bounds("box")
            val anchor = bounds("anchor")
            val dot = bounds("dot")
            assertClose(56f, box.width, "large number margin is 6dp")
            assertClose(box.left + 6f, anchor.left, "RTL offsets the anchor by the margin")
            assertClose(box.left, dot.left, "RTL mirrors the dot to the start edge")
            assertClose(box.top, dot.top, "RTL keeps the dot at the top")
        }

    @Test
    fun smallBucketUsesOneDpMargins() =
        runComposeUiTest {
            setContent {
                Harness(direction = LayoutDirection.Ltr) {
                    RedDotBox(
                        redDot = { Dot() },
                        modifier = Modifier.testTag("box"),
                        anchorSize = 24.dp,
                        anchorShape = RedDotAnchorShape.Circle,
                        redDotKind = RedDotAnchorKind.Dot,
                    ) {
                        Anchor(size = 24.dp)
                    }
                }
            }
            waitForIdle()
            val box = bounds("box")
            val dot = bounds("dot")
            assertClose(24f, box.width, "small circle anchors keep the anchor size")
            assertClose(box.right - 1f, dot.right, "small circle margin is 1dp")
        }

    private fun androidx.compose.ui.test.ComposeUiTest.bounds(tag: String): Rect = onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot

    private fun assertClose(
        expected: Float,
        actual: Float,
        message: String,
    ) {
        assertTrue(abs(expected - actual) < 0.5f, "$message (expected $expected, was $actual)")
    }

    @androidx.compose.runtime.Composable
    private fun Harness(
        direction: LayoutDirection,
        content: @androidx.compose.runtime.Composable () -> Unit,
    ) {
        val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
        RmuixTheme(controller = controller) {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                Column {
                    content()
                }
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun Anchor(size: androidx.compose.ui.unit.Dp = 36.dp) {
        Box(
            Modifier
                .size(size)
                .background(Color(0xFF3482FF))
                .testTag("anchor"),
        )
    }

    @androidx.compose.runtime.Composable
    private fun Dot() {
        Box(
            Modifier
                .size(6.dp)
                .background(Color.Red)
                .testTag("dot"),
        )
    }
}
