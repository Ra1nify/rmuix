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
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class CircularProgressRenderingTest {
    @Test
    fun determinateArcCoversTheRequestedSweep() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Box(Modifier.size(200.dp, 200.dp).background(Color.White)) {
                        CircularProgressIndicator(
                            progress = 0.5f,
                            size = 120.dp,
                            modifier = Modifier.testTag("ring").align(Alignment.TopStart),
                        )
                    }
                }
            }
            waitForIdle()
            val ring = onNodeWithTag("ring").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            val cx = ring.center.x.roundToInt()
            val cy = ring.center.y.roundToInt()

            // The stroke is centred on (size - strokeWidth) / 2; scan a small band to stay inside it.
            fun darkestAt(points: List<Pair<Int, Int>>): Float = points.minOf { (x, y) -> pixels[x, y].red }
            val radius = (ring.width / 2f - 2f).roundToInt()
            // The arc starts at -90deg (top) and sweeps clockwise for half a turn, so the top and the
            // right edge are progress, the left edge is still the track.
            val top = darkestAt((0..2).map { d -> cx to (cy - radius - d + 1) })
            val right = darkestAt((0..2).map { d -> (cx + radius + d - 1) to cy })
            val left = darkestAt((0..2).map { d -> (cx - radius - d + 1) to cy })
            assertTrue(top < left - 0.05f, "the arc must be darker than the track: top=$top left=$left")
            assertTrue(right < left - 0.05f, "the arc must reach the right edge: right=$right left=$left")
        }
}
