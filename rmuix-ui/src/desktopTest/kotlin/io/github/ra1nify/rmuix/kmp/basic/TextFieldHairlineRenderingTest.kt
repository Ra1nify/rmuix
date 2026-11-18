// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TextFieldHairlineRenderingTest {
    @Test
    fun lineModeBottomHairlineSurvivesOneExDensity() =
        runComposeUiTest {
            setContent {
                RmuixTheme {
                    Box(Modifier.size(240.dp, 120.dp).background(Color.White)) {
                        TextField(
                            value = "",
                            onValueChange = {},
                            lineMode = true,
                            modifier = Modifier.width(240.dp).testTag("field").align(Alignment.TopStart),
                        )
                    }
                }
            }
            waitForIdle()
            val field = onNodeWithTag("field").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            val x = field.center.x.roundToInt()
            val bottom = field.bottom.roundToInt()
            // The resting line is the last pixel row of the field.
            val line = (0..1).minOf { dy -> pixels[x, (bottom - 1 - dy).coerceAtLeast(0)].red }
            assertTrue(line < 0.92f, "the 0.33dp bottom line must keep one full pixel, darkest red=$line")
        }

    @Test
    fun boxedModeBorderSurvivesOneExDensity() =
        runComposeUiTest {
            setContent {
                RmuixTheme {
                    Box(Modifier.size(240.dp, 120.dp).background(Color.White)) {
                        TextField(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier.width(240.dp).testTag("field").align(Alignment.TopStart),
                        )
                    }
                }
            }
            waitForIdle()
            val field = onNodeWithTag("field").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            val x = field.center.x.roundToInt()
            val top = field.top.roundToInt()
            val border = (0..1).minOf { dy -> pixels[x, top + dy].red }
            assertTrue(border < 0.92f, "the 0.33dp border must keep one full pixel, darkest red=$border")
        }

    @Test
    fun boxedModeFillIsTransparentLikeTheStockNinePatch() =
        runComposeUiTest {
            val page = Color(0xFF2050A0)
            setContent {
                RmuixTheme {
                    Box(Modifier.size(240.dp, 120.dp).background(page)) {
                        TextField(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier.width(240.dp).testTag("field").align(Alignment.TopStart),
                        )
                    }
                }
            }
            waitForIdle()
            val field = onNodeWithTag("field").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            // Sample well inside the box (away from the border and the caret).
            val x = field.left.roundToInt() + 20
            val y = field.center.y.roundToInt()
            val inside = pixels[x, y]
            assertTrue(
                inside.red < 0.02f && inside.green < 0.02f && inside.blue < 0.02f || inside == page,
                "the boxed mode must not fill the box, pixel=$inside page=$page",
            )
        }
}
