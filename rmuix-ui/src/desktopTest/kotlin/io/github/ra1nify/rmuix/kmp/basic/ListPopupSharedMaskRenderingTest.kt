// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ListPopupSharedMaskRenderingTest {
    @Test
    fun pressingARowDrawsTheSharedMaskInsetFromTheEdges() =
        runComposeUiTest {
            setContent {
                RmuixTheme {
                    Box(Modifier.size(200.dp, 160.dp).background(Color.White)) {
                        Box(Modifier.testTag("column")) {
                            ListPopupColumn {
                                repeat(3) {
                                    Box(Modifier.fillMaxWidth().height(40.dp).testTag("row$it"))
                                }
                            }
                        }
                    }
                }
            }
            waitForIdle()
            val row = onNodeWithTag("row1").fetchSemanticsNode().boundsInRoot
            val centerY = row.center.y.roundToInt()
            onNodeWithTag("column").performTouchInput { down(center) }
            waitForIdle()
            val pixels = onRoot().captureToImage().toPixelMap()
            // #1F000000 over white is ~0.878 red; the mask is inset 8dp so the outer pixels stay white.
            assertTrue(
                pixels[row.left.roundToInt() + 16, centerY].red < 0.95f,
                "the pressed row must be tinted by the shared mask",
            )
            assertTrue(
                pixels[row.left.roundToInt() + 2, centerY].red > 0.97f,
                "the mask must stay inset from the popup edge",
            )
        }

    @Test
    fun maskSlidesToTheRowUnderTheFinger() =
        runComposeUiTest {
            setContent {
                RmuixTheme {
                    Box(Modifier.size(200.dp, 160.dp).background(Color.White)) {
                        Box(Modifier.testTag("column")) {
                            ListPopupColumn {
                                repeat(3) {
                                    Box(Modifier.fillMaxWidth().height(40.dp).testTag("row$it"))
                                }
                            }
                        }
                    }
                }
            }
            waitForIdle()
            val column = onNodeWithTag("column").fetchSemanticsNode().boundsInRoot
            val first = onNodeWithTag("row0").fetchSemanticsNode().boundsInRoot
            val last = onNodeWithTag("row2").fetchSemanticsNode().boundsInRoot
            val x = column.center.x - column.left
            onNodeWithTag("column").performTouchInput {
                down(Offset(x, last.center.y - column.top))
                moveTo(Offset(x, first.center.y - column.top))
            }
            waitForIdle()
            val pixels = onRoot().captureToImage().toPixelMap()
            val y0 = first.center.y.roundToInt()
            val y2 = last.center.y.roundToInt()
            assertTrue(
                pixels[first.left.roundToInt() + 16, y0].red < 0.95f,
                "the mask must follow the finger to the first row",
            )
            assertTrue(
                pixels[last.left.roundToInt() + 16, y2].red > 0.97f,
                "the row the mask left must be clean again",
            )
        }
}
