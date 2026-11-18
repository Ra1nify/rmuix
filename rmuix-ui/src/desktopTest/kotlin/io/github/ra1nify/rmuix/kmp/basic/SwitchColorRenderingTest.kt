// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SwitchColorRenderingTest {
    @Test
    fun uncheckedTrackUsesTheStockNeutralFill() =
        runComposeUiTest {
            var primaryRed = 0f
            setContent {
                RmuixTheme {
                    primaryRed = RmuixTheme.colorScheme.primary.red
                    Box(Modifier.size(120.dp, 60.dp).background(Color.White)) {
                        Switch(
                            checked = false,
                            onCheckedChange = {},
                            modifier = Modifier.testTag("switch").align(Alignment.TopStart),
                        )
                    }
                }
            }
            waitForIdle()
            val node = onNodeWithTag("switch").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            val y = node.center.y.roundToInt()
            // The thumb sits at 3dp..21dp, so 38dp is bare track.
            val track = pixels[(node.left + 38).roundToInt(), y]
            // ?attr/couiColorControls: #29000000 over white = (255 - 41)/255 = 214/255.
            assertTrue(abs(track.red - 214f / 255f) < 0.02f, "unchecked track should be #29000000 over white, was ${track.red}")
            val thumb = pixels[(node.left + 12).roundToInt(), y]
            assertTrue(thumb.red > 0.98f, "the unchecked thumb should be white, was ${thumb.red}")
            assertTrue(primaryRed > 0f)
        }

    @Test
    fun checkedTrackUsesTheThemePrimary() =
        runComposeUiTest {
            var primary = Color.White
            setContent {
                RmuixTheme {
                    primary = RmuixTheme.colorScheme.primary
                    Box(Modifier.size(120.dp, 60.dp).background(Color.White)) {
                        Switch(
                            checked = true,
                            onCheckedChange = {},
                            modifier = Modifier.testTag("switch").align(Alignment.TopStart),
                        )
                    }
                }
            }
            waitForIdle()
            val node = onNodeWithTag("switch").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            val y = node.center.y.roundToInt()
            // The checked thumb sits at 23dp..41dp, so 6dp is bare track.
            val track = pixels[(node.left + 6).roundToInt(), y]
            assertTrue(
                abs(track.red - primary.red) < 0.02f && abs(track.blue - primary.blue) < 0.02f,
                "checked track should be the theme primary $primary, was $track",
            )
            val thumb = pixels[(node.left + 32).roundToInt(), y]
            assertTrue(thumb.red > 0.98f && thumb.green > 0.98f && thumb.blue > 0.98f, "the checked thumb should be white")
        }
}
