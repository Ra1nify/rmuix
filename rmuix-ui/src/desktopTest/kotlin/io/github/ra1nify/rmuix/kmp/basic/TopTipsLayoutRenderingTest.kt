// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TopTipsLayoutRenderingTest {
    @Test
    fun wrappedMessageSwitchesTheCardToTheMultiLineLayout() = runComposeUiTest {
        setContent {
            Box(Modifier.size(200.dp)) {
                Column {
                    TopTips(content = "Short", modifier = Modifier.testTag("short"))
                    TopTips(
                        content =
                        "This message is long enough to wrap onto several lines inside a narrow tips card, " +
                            "so the stock view uses the multi-line radius and padding.",
                        modifier = Modifier.testTag("long"),
                    )
                }
            }
        }
        waitForIdle()
        val short = onNodeWithTag("short").fetchSemanticsNode().boundsInRoot.height
        val long = onNodeWithTag("long").fetchSemanticsNode().boundsInRoot.height
        // Single-line card: 12dp * 2 padding + 20dp content minimum; a wrapped message is taller.
        assertTrue(short < 50f, "single-line card height was $short")
        assertTrue(long > short + 10f, "wrapped card height $long should exceed the single-line $short")
    }
}
