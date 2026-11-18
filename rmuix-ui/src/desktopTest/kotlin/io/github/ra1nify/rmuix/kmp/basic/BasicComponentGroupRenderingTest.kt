// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class BasicComponentGroupRenderingTest {
    @Test
    fun dividersAppearBetweenRowsOnly() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(2f)) {
                RmuixTheme {
                    Box(Modifier.size(400.dp, 400.dp).background(Color.Black)) {
                        BasicComponentGroup(
                            modifier = Modifier.testTag("group").size(320.dp),
                            colors =
                            BasicComponentGroupColors(
                                containerColor = Color.White,
                                dividerColor = Color.Black,
                            ),
                            dividerStart = 16.dp,
                            dividerEnd = 16.dp,
                        ) {
                            repeat(3) { index ->
                                Box(Modifier.size(320.dp, 40.dp)) {
                                    // Plain rows: the group owns the surface and the hairlines.
                                }
                                if (index < 2) Unit
                            }
                        }
                    }
                }
            }
        }
        waitForIdle()
        val node = onNodeWithTag("group").fetchSemanticsNode()
        val pixels = onRoot().captureToImage().toPixelMap()
        val density = 2f
        val pad = (2 * density).roundToInt()
        val rowHeight = (40 * density).roundToInt()
        val left = node.boundsInRoot.left.roundToInt()
        // coui_preference_card_margin_horizontal: the card is inset 16dp, so the page shows at the sides.
        val sampleY = node.boundsInRoot.top.roundToInt() + pad + 10
        assertTrue(pixels[left + (8 * density).roundToInt(), sampleY].red < 0.2f, "page visible in the 16dp inset")
        assertTrue(pixels[left + (40 * density).roundToInt(), sampleY].red > 0.8f, "card starts after 16dp")
        fun rowSeam(index: Int): Int = node.boundsInRoot.top.roundToInt() + pad + rowHeight * index

        // Seam 1 (between row 0 and row 1) and seam 2 (between row 1 and row 2) are dark. The 0.33dp
        // hairline lands on a fractional pixel at density 2, so sample the three rows around it and
        // take the darkest.
        fun darkestAround(y: Int): Float {
            var darkest = 1f
            for (offset in -1..1) {
                darkest = minOf(darkest, pixels[left + (160 * density).roundToInt(), y + offset].red)
            }
            return darkest
        }
        for (seam in 1..2) {
            val darkest = darkestAround(rowSeam(seam))
            assertTrue(darkest < 0.7f, "seam $seam must be drawn, darkest=$darkest")
        }
        // Just past the last row there is no third hairline: the group ends there.
        val beyond = rowSeam(3)
        if (beyond < node.boundsInRoot.bottom.roundToInt() - 1) {
            val lightest = darkestAround(beyond)
            assertTrue(lightest > 0.7f, "the group must not draw a divider after the last row")
        }
    }

    @Test
    fun dividersFollowEachRowsTitleStart() = runComposeUiTest {
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(2f)) {
                RmuixTheme {
                    Box(Modifier.size(400.dp, 400.dp).background(Color.Black)) {
                        BasicComponentGroup(
                            modifier = Modifier.testTag("iconGroup").size(320.dp),
                            colors =
                            BasicComponentGroupColors(
                                containerColor = Color.White,
                                dividerColor = Color.Black,
                            ),
                        ) {
                            repeat(2) { index ->
                                BasicComponent(
                                    title = "Row $index",
                                    startAction = {
                                        // 36dp leading icon -> title x = 32 + 36 + 16 = 84dp.
                                        Box(Modifier.size(36.dp))
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
        waitForIdle()
        val node = onNodeWithTag("iconGroup").fetchSemanticsNode()
        val pixels = onRoot().captureToImage().toPixelMap()
        val density = 2f
        val left = node.boundsInRoot.left.roundToInt()
        // Row height: max(48dp, title line + 20dp, 36dp icon + 2 * 12dp tier margin) = 60dp.
        val seamY = node.boundsInRoot.top.roundToInt() + (2 * density).roundToInt() + (60 * density).roundToInt()
        fun sample(xDp: Int): Float = pixels[left + (xDp * density).roundToInt(), seamY].red
        // Card starts at the 16dp inset + 32dp content padding + 36dp icon + 16dp gap = 100dp.
        assertTrue(sample(90) > 0.8f, "the hairline must not reach under the 36dp icon")
        assertTrue(sample(110) < 0.7f, "the hairline must start at the title x")
    }

    @Test
    fun defaultsMatchTheStockCardList() {
        assertEquals(20.dp, BasicComponentGroupDefaults.CornerRadius)
        assertEquals(2.dp, BasicComponentGroupDefaults.GroupPadding)
        assertEquals(0.33.dp, BasicComponentGroupDefaults.DividerThickness)
        // coui_preference_card_margin_horizontal
        assertEquals(16.dp, BasicComponentGroupDefaults.HorizontalMargin)
        assertEquals(32.dp, BasicComponentGroupDefaults.DividerStart)
        assertEquals(32.dp, BasicComponentGroupDefaults.DividerEnd)
    }
}
