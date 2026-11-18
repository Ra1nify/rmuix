// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ChipGroupRenderingTest {
    @Test
    fun chipGroupRendersOnOneRowInsideAHorizontalScroll() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Box(Modifier.width(300.dp)) {
                        Row(Modifier.horizontalScroll(rememberScrollState())) {
                            ChipGroup(modifier = Modifier.testTag("group")) {
                                repeat(6) { index ->
                                    Chip(onClick = {}, label = "Chip $index", modifier = Modifier.testTag("chip$index"))
                                }
                            }
                        }
                    }
                }
            }
            waitForIdle()
            // The scroll viewport clips the reported bounds, so the row count is what proves the single-row
            // layout: six chips at 300dp would otherwise wrap into two rows.
            val group = onNodeWithTag("group").fetchSemanticsNode().boundsInRoot
            assertEquals(28f, group.height, 0.6f, "an unbounded parent must keep the chips on one row")
            onNodeWithTag("chip5").assertExists()
        }

    @Test
    fun chipGroupStartsFromTheTrailingEdgeInRtl() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Box(Modifier.width(300.dp).background(Color.White)) {
                            ChipGroup(modifier = Modifier.testTag("group")) {
                                repeat(6) { index ->
                                    Chip(onClick = {}, label = "Chip $index", modifier = Modifier.testTag("chip$index"))
                                }
                            }
                        }
                    }
                }
            }
            waitForIdle()
            val group = onNodeWithTag("group").fetchSemanticsNode().boundsInRoot
            val first = onNodeWithTag("chip0").fetchSemanticsNode().boundsInRoot
            val second = onNodeWithTag("chip1").fetchSemanticsNode().boundsInRoot
            assertTrue(
                kotlin.math.abs(first.right - group.right) <= 1f,
                "RTL must start at the right edge: group=${group.right} first=${first.right}",
            )
            assertTrue(second.right < first.left, "the second chip must sit to the left of the first")
        }

    @Test
    fun chipGroupCollapsesToThreeRowsAndExpands() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Box(Modifier.width(300.dp).background(Color.White)) {
                        ChipGroup(modifier = Modifier.testTag("group")) {
                            repeat(20) { index ->
                                Chip(onClick = {}, label = "Chip $index")
                            }
                        }
                    }
                }
            }
            waitForIdle()
            val toggle = onNodeWithContentDescription("Expand").fetchSemanticsNode().boundsInRoot
            assertClose(28f, toggle.width, "the collapse chip is `coui_chip_group_collapsable_button_size`")
            assertClose(28f, toggle.height, "the collapse chip is `coui_chip_group_collapsable_button_size`")
            val collapsed = onNodeWithTag("group").fetchSemanticsNode().boundsInRoot.height
            val collapsedImage = onRoot().captureToImage()
            onNodeWithContentDescription("Expand").performClick()
            waitForIdle()
            val expanded = onNodeWithTag("group").fetchSemanticsNode().boundsInRoot.height
            assertTrue(
                expanded > collapsed + 20f,
                "expanding must add rows: collapsed=$collapsed expanded=$expanded",
            )
            val image = onRoot().captureToImage()
            var root = File(System.getProperty("user.dir")).absoluteFile
            while (root.parentFile != null && !File(root, "settings.gradle.kts").exists()) root = root.parentFile
            val dir = File(root, "docs/dev/shots").apply { mkdirs() }
            ImageIO.write(collapsedImage.toAwtImage(), "png", File(dir, "chipgroup-collapsed.png"))
            ImageIO.write(image.toAwtImage(), "png", File(dir, "chipgroup-expanded.png"))
        }
}

private fun assertClose(
    expected: Float,
    actual: Float,
    message: String,
) {
    assertTrue(kotlin.math.abs(expected - actual) < 0.5f, "$message (expected $expected, was $actual)")
}
