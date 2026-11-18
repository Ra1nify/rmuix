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
class SwitchLoadingRenderingTest {
    @Test
    fun loadingSwitchCollapsesTheThumbAndShowsTheSpinner() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Box(Modifier.size(120.dp, 60.dp).background(Color.White)) {
                        Switch(
                            checked = true,
                            onCheckedChange = {},
                            loading = true,
                            modifier = Modifier.testTag("switch").align(Alignment.TopStart),
                        )
                    }
                }
            }
            // Past the 433ms collapse and the 550ms spinner fade.
            mainClock.advanceTimeBy(1200)
            val node = onNodeWithTag("switch").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            // The checked thumb sits at the trailing end (23..41dp); the collapsed thumb leaves the track.
            val thumbX = (node.left + 32f).roundToInt()
            val midY = node.center.y.roundToInt()
            val track = pixels[thumbX, midY]
            assertTrue(
                track.blue > 0.8f && track.red < 0.5f,
                "the thumb must collapse so the primary track shows through, was $track",
            )
            // The spinner replaces the collapsed thumb: a dark `coui_icon_loading` stroke over the track.
            val darkest = (-7..7).minOf { dx -> (-7..7).minOf { dy -> pixels[thumbX + dx, midY + dy].blue } }
            assertTrue(darkest < track.blue - 0.2f, "the loading glyph should be drawn over the thumb slot")
        }
}
