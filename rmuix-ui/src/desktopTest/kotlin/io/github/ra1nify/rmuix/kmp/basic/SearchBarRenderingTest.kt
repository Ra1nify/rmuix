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
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SearchBarRenderingTest {
    @Test
    fun defaultLeadingIconCentresInTheStock36dpBox() =
        runComposeUiTest {
            setContent {
                RmuixTheme {
                    Box(Modifier.size(360.dp, 80.dp).background(Color.White)) {
                        InputField(
                            query = "",
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
            val field = onNodeWithTag("field").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            // Stop before the placeholder (it starts after the 4 + 36 = 40dp icon box).
            val left = field.left.roundToInt()
            val centerY = field.center.y.roundToInt()
            val xs =
                buildList {
                    for (y in (centerY - 12)..(centerY + 12)) {
                        for (x in left until left + 36) {
                            if (pixels[x, y].red < 0.85f) add(x)
                        }
                    }
                }
            assertTrue(xs.size >= 10, "the magnifier must be visible: ${xs.size} dark pixels")
            val centre = (xs.min() + xs.max()) / 2f
            // 4dp box gap + half of the 36dp icon box = 22dp from the field edge.
            assertTrue(
                kotlin.math.abs(centre - left - 22f) <= 2f,
                "the magnifier centre should sit at 22dp, was ${centre - left}dp",
            )
        }
}
