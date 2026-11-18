// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class NavigationBarSelectedIconRenderingTest {
    private fun emptyIcon(): ImageVector = ImageVector
        .Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).build()

    private fun redSquareIcon(): ImageVector = ImageVector
        .Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).addPath(
            pathData = PathParser().parsePathString("M0,0 h24 v24 h-24 z").toNodes(),
            fill = SolidColor(Color.Red),
        ).build()

    private fun filledSquareIcon(): ImageVector = ImageVector
        .Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).addPath(
            pathData = PathParser().parsePathString("M0,0 h24 v24 h-24 z").toNodes(),
            fill = SolidColor(Color.Black),
        ).build()

    @Test
    fun selectedItemDrawsTheSelectedIcon() = runComposeUiTest {
        setContent {
            RmuixTheme {
                Box(Modifier.size(240.dp, 120.dp).background(Color.White)) {
                    NavigationBar(modifier = Modifier.testTag("bar")) {
                        NavigationBarItem(
                            selected = true,
                            onClick = {},
                            icon = emptyIcon(),
                            selectedIcon = filledSquareIcon(),
                            label = "A",
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = {},
                            icon = emptyIcon(),
                            selectedIcon = filledSquareIcon(),
                            label = "B",
                        )
                    }
                }
            }
        }
        waitForIdle()
        val bar = onNodeWithTag("bar").fetchSemanticsNode().boundsInRoot
        val pixels = onRoot().captureToImage().toPixelMap()
        val half = bar.width / 2f
        val top = bar.top.roundToInt()

        // The icon band starts at IconTopPadding from the item's top; scanning the first 30dp of
        // each half is enough to find the 24dp glyph.
        fun darkestInHalf(index: Int): Float {
            val left = (bar.left + index * half).roundToInt()
            val right = (bar.left + (index + 1) * half).roundToInt()
            var darkest = 1f
            // Skip the bar's 1px top divider.
            for (y in top + 2 until top + 30) {
                for (x in left until right) {
                    val red = pixels[x, y].red
                    if (red < darkest) darkest = red
                }
            }
            return darkest
        }
        val selected = darkestInHalf(0)
        val unselected = darkestInHalf(1)
        assertTrue(selected < 0.7f, "the selected item should paint the filled selectedIcon, darkest=$selected")
        assertTrue(unselected > 0.95f, "the unselected item keeps the empty icon, darkest=$unselected")
    }

    @Test
    fun iconArtworkKeepsItsOwnColours() = runComposeUiTest {
        setContent {
            RmuixTheme {
                Box(Modifier.size(240.dp, 120.dp).background(Color.White)) {
                    NavigationBar(modifier = Modifier.testTag("bar")) {
                        NavigationBarItem(
                            selected = true,
                            onClick = {},
                            icon = redSquareIcon(),
                            label = "A",
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = {},
                            icon = redSquareIcon(),
                            label = "B",
                        )
                    }
                }
            }
        }
        waitForIdle()
        val bar = onNodeWithTag("bar").fetchSemanticsNode().boundsInRoot
        val pixels = onRoot().captureToImage().toPixelMap()
        val half = bar.width / 2f
        val top = bar.top.roundToInt()

        fun hasRedInHalf(index: Int): Boolean {
            val left = (bar.left + index * half).roundToInt()
            val right = (bar.left + (index + 1) * half).roundToInt()
            for (y in top + 2 until top + 40) {
                for (x in left until right) {
                    val color = pixels[x, y]
                    if (color.red > 0.6f && color.green < 0.4f && color.blue < 0.4f) return true
                }
            }
            return false
        }
        assertTrue(hasRedInHalf(0), "the selected icon must keep the artwork's red fill")
        assertTrue(hasRedInHalf(1), "the unselected icon must keep the artwork's red fill")
    }
}
