// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ScrollBarRenderingTest {
    @Test
    fun thumbUsesTheStockWidthAndTrackInsets() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    val scrollState = rememberScrollState()
                    Box(
                        Modifier
                            .size(width = 200.dp, height = 300.dp)
                            .background(Color.White)
                            .testTag("host"),
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState),
                        ) {
                            repeat(20) { index ->
                                Box(
                                    Modifier
                                        .height(60.dp)
                                        .width(200.dp)
                                        .background(if (index % 2 == 0) Color(0xFFF5F5F5) else Color(0xFFEDEDED)),
                                )
                            }
                        }
                        VerticalScrollBar(
                            adapter = rememberScrollBarAdapter(scrollState),
                            modifier =
                                Modifier
                                    .align(Alignment.CenterEnd)
                                    .fillMaxHeight()
                                    .testTag("scrollbar"),
                            colors = ScrollBarDefaults.scrollBarColors(thumbColor = Color.Black),
                        )
                    }
                }
            }
            // Hover only: a release starts the 1s fade-out job, which `waitForIdle` would run to 0.
            onNodeWithTag("scrollbar").performMouseInput {
                moveTo(center)
            }
            waitForIdle()
            val host = onNodeWithTag("host").fetchSemanticsNode().boundsInRoot
            val image = onRoot().captureToImage()
            run {
                var root = java.io.File(System.getProperty("user.dir")).absoluteFile
                while (root.parentFile != null && !java.io.File(root, "settings.gradle.kts").exists()) root = root.parentFile
                java.io.File(root, "docs/dev/shots").mkdirs()
                javax.imageio.ImageIO.write(
                    image.toAwtImage(),
                    "png",
                    java.io.File(root, "docs/dev/shots/scrollbar-debug.png"),
                )
            }
            val pixels = image.toPixelMap()
            run {
                var root = java.io.File(System.getProperty("user.dir")).absoluteFile
                while (root.parentFile != null && !java.io.File(root, "settings.gradle.kts").exists()) root = root.parentFile
                javax.imageio.ImageIO.write(image.toAwtImage(), "png", java.io.File(root, "docs/dev/shots/scrollbar-light.png"))
            }
            val scrollbar = onNodeWithTag("scrollbar").fetchSemanticsNode().boundsInRoot
            val columns = findThumbColumns(pixels, host)
            assertTrue(columns.isNotEmpty(), "the hovered scrollbar must draw a thumb")
            val thumbWidth = columns.last() - columns.first() + 1
            // Anti-aliased edges add a pixel on each side, so allow +/- 1.5px around the dp value.
            assertTrue(
                abs(6f - thumbWidth.toFloat()) <= 1.5f,
                "hovered thumbs grow to `coui_scrollbar_drag_thumb_width` (was $thumbWidth)",
            )
            val rows = findThumbRows(pixels, columns)
            assertTrue(rows.isNotEmpty(), "the thumb must span some rows")
            val endInset = host.right - (columns.last() + 1)
            // Hovered, so the pressed inset (`coui_scrollbar_drawable_pressed_inset` 4dp) is in effect.
            assertTrue(
                abs(4f - endInset.toFloat()) <= 2.5f,
                "`coui_scrollbar_drawable_pressed_inset` keeps the hovered thumb 4dp from the edge (was $endInset)",
            )
            // 3dp thumb + 2 x 5.5dp insets = `coui_scrollbar_wight` 14dp.
            assertTrue(
                abs(14f - scrollbar.width) <= 1.5f,
                "the bar reserves the stock 14dp (was ${scrollbar.width})",
            )
            val topInset = rows.first() - host.top
            assertTrue(topInset >= 6f, "the track starts 8dp below the top (was $topInset)")
        }

    private fun findThumbColumns(
        pixels: androidx.compose.ui.graphics.PixelMap,
        host: Rect,
    ): List<Int> {
        val start = (host.right - 20f).toInt().coerceAtLeast(host.left.toInt())
        return (start until host.right.toInt()).filter { x ->
            (host.top.toInt() until host.bottom.toInt()).count { y -> pixels[x, y].luminance() < 0.7f } > 5
        }
    }

    private fun findThumbRows(
        pixels: androidx.compose.ui.graphics.PixelMap,
        columns: List<Int>,
    ): List<Int> {
        if (columns.isEmpty()) return emptyList()
        val x = columns.last()
        return (0 until pixels.height).filter { y -> pixels[x, y].luminance() < 0.7f }
    }

    private fun assertClose(
        expected: Float,
        actual: Float,
        message: String,
    ) {
        assertTrue(abs(expected - actual) < 1f, "$message (expected $expected, was $actual)")
    }
}
