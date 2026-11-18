// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
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
class HairlineRenderingTest {
    @Test
    fun hairlineDividerKeepsOnePixelAtOneExDensity() =
        runComposeUiTest {
            setContent {
                RmuixTheme {
                    Box(Modifier.size(200.dp, 100.dp).background(Color.White)) {
                        HorizontalDivider(Modifier.fillMaxWidth().testTag("divider"))
                    }
                }
            }
            waitForIdle()
            val divider = onNodeWithTag("divider").fetchSemanticsNode().boundsInRoot
            assertTrue(divider.height >= 1f, "0.33dp must not collapse to zero height: ${divider.height}")
            val pixels = onRoot().captureToImage().toPixelMap()
            val x = divider.center.x.roundToInt()
            val darkest = minRedAround(pixels, x, divider.top.roundToInt(), rows = 2)
            assertTrue(darkest < 0.95f, "the hairline must be painted, darkest red=$darkest")
        }

    @Test
    fun numberPickerSelectionLinesSurviveOneExDensity() =
        runComposeUiTest {
            setContent {
                RmuixTheme {
                    Box(Modifier.size(160.dp, 160.dp).background(Color.White)) {
                        NumberPicker(
                            value = 3,
                            onValueChange = {},
                            range = 1..30,
                            modifier = Modifier.testTag("picker"),
                        )
                    }
                }
            }
            waitForIdle()
            val picker = onNodeWithTag("picker").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            // Sample left of the centred digits so only the hairlines (16dp inset) are in range.
            val left = picker.left.roundToInt() + 20
            val right = left + 30
            val top = picker.top.roundToInt()
            val bottom = picker.bottom.roundToInt()
            val lineRows =
                (top until bottom).filter { y ->
                    (left until right).any { x -> pixels[x, y].red < 0.95f }
                }
            val bands =
                lineRows.fold(mutableListOf<MutableList<Int>>()) { acc, y ->
                    if (acc.isNotEmpty() && y - acc.last().last() <= 1) acc.last().add(y) else acc.add(mutableListOf(y))
                    acc
                }
            assertTrue(
                bands.size == 2,
                "expected two selection hairlines, bands=${bands.map { it.joinToString("-") }}",
            )
        }

    private fun minRedAround(
        pixels: PixelMap,
        x: Int,
        startY: Int,
        rows: Int,
    ): Float {
        var min = 1f
        for (y in startY until startY + rows) {
            if (y < 0 || y >= pixels.height) continue
            val red = pixels[x, y].red
            if (red < min) min = red
        }
        return min
    }
}
