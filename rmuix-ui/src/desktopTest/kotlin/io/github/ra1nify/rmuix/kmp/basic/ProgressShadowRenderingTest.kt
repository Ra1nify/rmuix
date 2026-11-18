// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * `showShadow` paints the stock `coui_circular_progress_shadow_*` layer (4dp radius, 2dp down, `#19000000`).
 * The default stays shadow-less because the stock default progress bar type is 0.
 */
@OptIn(ExperimentalTestApi::class)
class ProgressShadowRenderingTest {
    @Test
    fun shadowDarkensTheAreaBelowTheRing() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Column(
                        Modifier.background(Color.White).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        CircularProgressIndicator(
                            progress = 0.5f,
                            modifier = Modifier.testTag("shadowed"),
                            showShadow = true,
                        )
                        CircularProgressIndicator(
                            progress = 0.5f,
                            modifier = Modifier.testTag("plain"),
                        )
                    }
                }
            }
            waitForIdle()
            val shadowed = sampleBelow("shadowed")
            val plain = sampleBelow("plain")
            assertTrue(
                shadowed < plain - 0.001f,
                "the shadow darkens the pixel below the ring (shadowed=$shadowed plain=$plain)",
            )
        }

    private fun ComposeUiTest.sampleBelow(tag: String): Float {
        val bounds = onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        val pixels = onRoot().captureToImage().toPixelMap()
        // Two pixels below the ring: the 2dp y_bias keeps the shadow outside the drawn arc.
        val y = (bounds.bottom + 2f).toInt().coerceAtMost(pixels.height - 1)
        val x = bounds.center.x.toInt()
        return pixels[x, y].luminance()
    }
}
