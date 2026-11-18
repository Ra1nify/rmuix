// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class DividerLengthRenderingTest {
    @Test
    fun shortHorizontalDividerUsesTheInstructionRuleLength() = runComposeUiTest {
        setContent {
            Box(Modifier.size(200.dp)) {
                Column {
                    HorizontalDivider(modifier = Modifier.testTag("short"), length = 24.dp)
                    HorizontalDivider(modifier = Modifier.testTag("full"))
                }
            }
        }
        waitForIdle()
        val short = onNodeWithTag("short").fetchSemanticsNode().boundsInRoot.width
        val full = onNodeWithTag("full").fetchSemanticsNode().boundsInRoot.width
        assertEquals(24f, short, 0.5f, "coui_component_card_instruction_divider_width is 24dp")
        assertEquals(200f, full, 0.5f, "without length the divider fills the parent")
    }

    @Test
    fun shortVerticalDividerUsesTheSameLength() = runComposeUiTest {
        setContent {
            Box(Modifier.size(200.dp)) {
                Row {
                    VerticalDivider(modifier = Modifier.testTag("short"), length = 24.dp)
                    VerticalDivider(modifier = Modifier.testTag("full"))
                }
            }
        }
        waitForIdle()
        val short = onNodeWithTag("short").fetchSemanticsNode().boundsInRoot.height
        val full = onNodeWithTag("full").fetchSemanticsNode().boundsInRoot.height
        assertEquals(24f, short, 0.5f)
        assertEquals(200f, full, 0.5f)
    }
}
