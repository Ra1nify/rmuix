// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
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
class SnackbarPillRenderingTest {
    @Test
    fun singleLineSnackbarIsACapsule() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Box(Modifier.size(360.dp, 200.dp).background(Color(0xFF9E9E9E))) {
                        val hostState = remember { SnackbarHostState() }
                        LaunchedEffect(hostState) { hostState.showSnackbar("Settings saved") }
                        SnackbarHost(state = hostState)
                    }
                }
            }
            waitForIdle()
            val pixels = onRoot().captureToImage().toPixelMap()
            // Collect the white capsule against the grey backdrop, ignoring the drop shadow.
            var left = Int.MAX_VALUE
            var top = Int.MAX_VALUE
            var right = -1
            var bottom = -1
            for (y in 0 until pixels.height) {
                for (x in 0 until pixels.width) {
                    val c = pixels[x, y]
                    if (c.red > 0.9f && c.green > 0.9f && c.blue > 0.9f) {
                        if (x < left) left = x
                        if (x > right) right = x
                        if (y < top) top = y
                        if (y > bottom) bottom = y
                    }
                }
            }
            assertTrue(right > left && bottom > top, "the snackbar capsule must be visible")
            val midY = ((top + bottom) / 2f).roundToInt()
            // A capsule (radius = height / 2) fills the left edge at mid-height but not its top corner.
            val inside = pixels[left + 1, midY].red > 0.9f
            val corner = pixels[left + 1, top + 1].red > 0.9f
            assertTrue(inside, "the capsule must reach its left edge at mid height")
            assertTrue(!corner, "a capsule must not fill its top-left corner")
        }
}
