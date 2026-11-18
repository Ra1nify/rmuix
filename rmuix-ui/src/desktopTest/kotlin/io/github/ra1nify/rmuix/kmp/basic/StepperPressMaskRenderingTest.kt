// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class StepperPressMaskRenderingTest {
    @Test
    fun pressingThePlusButtonDrawsTheStockPressMask() =
        runComposeUiTest {
            setContent {
                RmuixTheme {
                    Box(Modifier.size(240.dp, 100.dp).background(Color.White)) {
                        Stepper(
                            value = 5,
                            onValueChange = {},
                            modifier = Modifier.testTag("stepper").align(Alignment.TopStart),
                            minValue = 0,
                            maxValue = 10,
                        )
                    }
                }
            }
            waitForIdle()
            // The row wraps its content, so the plus button is the last 32dp on the right.
            val stepper = onNodeWithTag("stepper").fetchSemanticsNode().boundsInRoot
            val plusCenter = Offset(stepper.right - 16f, stepper.center.y)
            // 11dp right of the centre stays inside the 32dp circle but outside the 15.2dp glyph.
            val sampleX = (plusCenter.x + 11f).roundToInt()
            val sampleY = plusCenter.y.roundToInt()
            val before = onRoot().captureToImage().toPixelMap()[sampleX, sampleY].red
            assertEquals(236f / 255f, before, 0.02f, "resting button is couiColorContainer8 over white")

            onNodeWithTag("stepper").performTouchInput {
                down(Offset(stepper.width - 16f, stepper.height / 2f))
            }
            waitForIdle()
            val pressed = onRoot().captureToImage().toPixelMap()[sampleX, sampleY].red
            assertTrue(
                pressed < before - 0.05f,
                "pressing must add the couiColorPress mask: before=$before pressed=$pressed",
            )
        }
}
