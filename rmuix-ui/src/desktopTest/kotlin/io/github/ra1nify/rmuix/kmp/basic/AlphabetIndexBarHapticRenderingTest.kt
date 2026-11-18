// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class AlphabetIndexBarHapticRenderingTest {
    @Test
    fun keyChangeFiresTheStockSegmentTickHaptic() = runComposeUiTest {
        val fired = mutableListOf<HapticFeedbackType>()
        val haptics =
            object : HapticFeedback {
                override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
                    fired += hapticFeedbackType
                }
            }
        setContent {
            CompositionLocalProvider(LocalHapticFeedback provides haptics) {
                AlphabetIndexBar(
                    onLetterSelected = {},
                    modifier = Modifier.size(39.dp, 432.dp).testTag("bar"),
                )
            }
        }
        waitForIdle()
        onNodeWithTag("bar").performTouchInput {
            down(Offset(center.x, 60f))
            moveTo(Offset(center.x, 200f))
            moveTo(Offset(center.x, 320f))
            up()
        }
        waitForIdle()
        // performFeedback(): the 308 path maps to SegmentTick, once per changed key.
        assertTrue(fired.isNotEmpty(), "dragging over keys must fire haptics")
        assertTrue(fired.all { it == HapticFeedbackType.SegmentTick }, "fired=$fired")
        assertTrue(fired.size >= 2, "dragging across keys should fire a haptic per transition, fired=$fired")
    }
}
