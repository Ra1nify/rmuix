// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class StepperGeometryTest {
    @Test
    fun stepperIsOneButtonTall() =
        runComposeUiTest {
            setContent { Harness() }
            waitForIdle()
            val stepper = bounds("stepper")
            assertClose(32f, stepper.height, "`stepper_button_size` defines the row height")
        }

    @Test
    fun valueIsCentredBetweenTheButtons() =
        runComposeUiTest {
            setContent { Harness() }
            waitForIdle()
            val stepper = bounds("stepper")
            val value = onNodeWithText("5", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
            assertClose(stepper.center.x, value.center.x, "the symmetric 32dp buttons keep the value centred")
        }

    @androidx.compose.runtime.Composable
    private fun Harness() {
        val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
        RmuixTheme(controller = controller) {
            Column {
                Stepper(
                    value = 5,
                    onValueChange = {},
                    modifier = Modifier.testTag("stepper"),
                )
            }
        }
    }

    private fun ComposeUiTest.bounds(tag: String): Rect = onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot

    private fun assertClose(
        expected: Float,
        actual: Float,
        message: String,
    ) {
        assertTrue(abs(expected - actual) < 1f, "$message (expected $expected, was $actual)")
    }
}
