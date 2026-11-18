// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class RedDotStrokeRenderingTest {
    @Test
    fun strokeModeGrowsTheDotByTwoDp() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Column(Modifier.padding(8.dp)) {
                        RedDot(mode = RedDotMode.PointOnly, modifier = Modifier.testTag("plain"))
                        RedDot(
                            mode = RedDotMode.PointOnly,
                            modifier = Modifier.testTag("struck"),
                            withStroke = true,
                        )
                        RedDot(
                            mode = RedDotMode.PointWithNum(9),
                            modifier = Modifier.testTag("num"),
                        )
                        RedDot(
                            mode = RedDotMode.PointWithNum(9),
                            modifier = Modifier.testTag("numStruck"),
                            withStroke = true,
                        )
                    }
                }
            }
            waitForIdle()
            val plain = onNodeWithTag("plain").fetchSemanticsNode().boundsInRoot
            val struck = onNodeWithTag("struck").fetchSemanticsNode().boundsInRoot
            assertEquals(6f, plain.height, 0.5f, "the plain dot is coui_hint_red_dot_point_size")
            assertEquals(
                8f,
                struck.height,
                0.5f,
                "coui_hint_red_dot_mode_stroke_extra_diameter adds 2dp to the diameter",
            )
            val num = onNodeWithTag("num").fetchSemanticsNode().boundsInRoot
            val numStruck = onNodeWithTag("numStruck").fetchSemanticsNode().boundsInRoot
            assertEquals(16f, num.height, 0.5f, "the number badge is 16dp tall")
            assertEquals(num.width + 2f, numStruck.width, 0.5f, "the struck badge grows by 2dp per axis")
            assertEquals(18f, numStruck.height, 0.5f)
        }
}
