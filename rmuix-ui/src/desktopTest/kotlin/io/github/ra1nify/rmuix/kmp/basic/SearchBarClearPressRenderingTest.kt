// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SearchBarClearPressRenderingTest {
    @Test
    fun pressingTheClearButtonDrawsTheCircularMask() =
        runComposeUiTest {
            setContent {
                RmuixTheme {
                    Box(Modifier.size(360.dp, 80.dp).background(Color.White)) {
                        InputField(
                            query = "abc",
                            onQueryChange = {},
                            onSearch = {},
                            expanded = true,
                            onExpandedChange = {},
                            modifier = Modifier.testTag("field"),
                        )
                    }
                }
            }
            waitForIdle()
            val clear = onNodeWithContentDescription("Search Cleanup").fetchSemanticsNode().boundsInRoot
            // Inside the 36dp circular mask but outside the 18dp disc and its white cross.
            val sampleX = (clear.center.x + 14f).roundToInt()
            val sampleY = clear.center.y.roundToInt()
            val before = onRoot().captureToImage().toPixelMap()[sampleX, sampleY].red

            onNodeWithContentDescription("Search Cleanup").performTouchInput { down(center) }
            waitForIdle()
            val pressed = onRoot().captureToImage().toPixelMap()[sampleX, sampleY].red
            assertTrue(
                pressed < before - 0.05f,
                "pressing the clear button must add the couiColorPress mask: before=$before pressed=$pressed",
            )
        }
}
