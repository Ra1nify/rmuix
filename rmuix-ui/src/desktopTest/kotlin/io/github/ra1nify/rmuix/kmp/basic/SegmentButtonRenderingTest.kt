// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SegmentButtonRenderingTest {
    @Test
    fun indicatorSitsUnderTheSelectedSegment() = runComposeUiTest {
        setContent {
            RmuixTheme {
                Box(Modifier.size(400.dp, 200.dp).background(Color.White)) {
                    SegmentButton(
                        options = listOf("A", "B", "C"),
                        selectedIndex = 2,
                        onSelect = {},
                        modifier = Modifier.testTag("segments").size(300.dp, 40.dp),
                        colors =
                        SegmentButtonColors(
                            containerColor = Color.White,
                            indicatorColor = Color.Black,
                            selectedContentColor = Color.White,
                            unselectedContentColor = Color.White,
                            indicatorShadowColor = Color.Transparent,
                        ),
                    )
                }
            }
        }
        waitForIdle()
        val node = onNodeWithTag("segments").fetchSemanticsNode().boundsInRoot
        val pixels = onRoot().captureToImage().toPixelMap()
        // Sample near the top of the indicator band: the segment labels are vertically centred, so
        // the middle row would hit their glyphs instead of the indicator.
        val y = node.top.roundToInt() + (node.height * 0.15f).roundToInt()

        // Track is 4dp padding + 3 equal segments; the indicator fills the third one.
        fun darkAt(xFraction: Float): Boolean {
            val x = node.left + node.width * xFraction
            return pixels[x.roundToInt(), y].red < 0.2f
        }
        assertTrue(darkAt(0.83f), "the third segment must carry the indicator")
        assertTrue(!darkAt(0.17f), "the first segment must not")
        assertTrue(!darkAt(0.5f), "the second segment must not")
    }

    @Test
    fun tappingASegmentReportsItsIndex() = runComposeUiTest {
        var selected = -1
        setContent {
            RmuixTheme {
                SegmentButton(
                    options = listOf("Day", "Week", "Month"),
                    selectedIndex = 0,
                    onSelect = { selected = it },
                    modifier = Modifier.size(300.dp, 40.dp),
                )
            }
        }
        onNodeWithText("Week").performClick()
        assertEquals(1, selected)
    }

    @Test
    fun controlIsCappedAtTheResponsiveGridWidth() = runComposeUiTest {
        val phoneWindow =
            object : WindowInfo {
                override val isWindowFocused = true
                override val containerDpSize = DpSize(900.dp, 1000.dp)
            }
        setContent {
            RmuixTheme {
                CompositionLocalProvider(LocalWindowInfo provides phoneWindow) {
                    Box(Modifier.size(1200.dp, 200.dp).background(Color.White)) {
                        SegmentButton(
                            options = listOf("Day", "Week", "Month"),
                            selectedIndex = 0,
                            onSelect = {},
                            modifier = Modifier.fillMaxWidth().testTag("segments"),
                        )
                    }
                }
            }
        }
        waitForIdle()
        val bounds = onNodeWithTag("segments").fetchSemanticsNode().boundsInRoot
        // 900x1000dp is an expanded-portrait window: 8-column grid, 24dp margin, 8dp gutter,
        // 6-column span -> 6 * 99.5 + 5 * 8 = 637dp.
        assertEquals(637f, bounds.width, 0.6f, "the control must be capped at the stock grid width")
    }
}
