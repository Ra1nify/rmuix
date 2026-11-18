// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * `TabRow` positions the indicator with `Modifier.offset`, which mirrors the x offset in RTL
 * (`placeRelativeWithLayer`), so no manual flip is needed - this test pins that behaviour.
 */
@OptIn(ExperimentalTestApi::class)
class TabRowRtlRenderingTest {
    @Test
    fun indicatorFollowsTheInvertedItemOrder() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Column {
                        Box(Modifier.width(400.dp)) {
                            TabRow(
                                tabs = listOf("One", "Two"),
                                selectedTabIndex = 0,
                                onTabSelected = {},
                                modifier = Modifier.testTag("ltr"),
                            )
                        }
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Box(Modifier.width(400.dp)) {
                                TabRow(
                                    tabs = listOf("One", "Two"),
                                    selectedTabIndex = 0,
                                    onTabSelected = {},
                                    modifier = Modifier.testTag("rtl"),
                                )
                            }
                        }
                    }
                }
            }
            waitForIdle()
            val pixels = onRoot().captureToImage().toPixelMap()
            val ltr = indicatorCentre(pixels, onNodeWithTag("ltr").fetchSemanticsNode().boundsInRoot)
            val rtl = indicatorCentre(pixels, onNodeWithTag("rtl").fetchSemanticsNode().boundsInRoot)
            assertTrue(ltr < 200f, "LTR keeps tab 0 on the left, indicator was at $ltr")
            assertTrue(rtl > 200f, "RTL mirrors tab 0 to the right edge, indicator was at $rtl")
        }

    private fun indicatorCentre(
        pixels: PixelMap,
        row: Rect,
    ): Float {
        val y = (row.bottom - 1f).roundToInt().coerceAtMost(pixels.height - 1)
        val xs =
            (row.left.roundToInt() until row.right.roundToInt()).filter { x ->
                pixels[x, y].luminance() < 0.5f
            }
        assertTrue(xs.isNotEmpty(), "the 2dp indicator must be drawn at the bottom of the row")
        return (xs.first() + xs.last()) / 2f
    }
}
