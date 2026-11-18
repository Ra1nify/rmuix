// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ListPopupMixedGroupDividerRenderingTest {
    @Test
    fun mixedGroupDividerIsATwelveDpRowWithAThinInsetLine() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Box(Modifier.size(300.dp, 40.dp).background(Color.White)) {
                        ListPopupMixedGroupDivider(modifier = Modifier.testTag("divider").align(Alignment.TopStart))
                    }
                }
            }
            waitForIdle()
            val row = onNodeWithTag("divider").fetchSemanticsNode().boundsInRoot
            assertEquals(12f, row.height, 0.5f, "coui_popup_list_group_divider_total_height is 12dp")
            val pixels = onRoot().captureToImage().toPixelMap()
            // A 0.5dp line at 1x density is anti-aliased into a faint row, so only its presence and
            // position are checked, not a fully saturated colour.
            val lineRows = (0 until 40).filter { y -> (0 until 300).minOf { x -> pixels[x, y].red } < 0.99f }
            assertTrue(lineRows.isNotEmpty(), "the divider line must be drawn")
            assertTrue(lineRows.all { it in 4..9 }, "the 0.5dp line sits around 5.75dp: $lineRows")
            val lineY = lineRows.minByOrNull { y -> (0 until 300).minOf { x -> pixels[x, y].red } }!!
            val lineX = (0 until 300).filter { x -> pixels[x, lineY].red < 0.99f }
            assertTrue(abs(lineX.first() - 20f) <= 1f, "coui_popup_list_default_divider_margin_horizontal is 20dp")
            assertTrue(abs(lineX.last() - 280f) <= 1f, "the line ends 20dp from the right edge")
        }
}
