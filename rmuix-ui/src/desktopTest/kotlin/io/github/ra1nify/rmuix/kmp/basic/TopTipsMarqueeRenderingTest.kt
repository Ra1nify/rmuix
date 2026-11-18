// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.test.Test

/**
 * `marquee = true` maps to `COUIMarqueeTopTips`: the message still participates in semantics and short
 * messages are left static (the stock `COUIMarqueeTextView` only rolls when the text overflows).
 */
@OptIn(ExperimentalTestApi::class)
class TopTipsMarqueeRenderingTest {
    @Test
    fun marqueeKeepsTheMessageInSemantics() =
        runComposeUiTest {
            val message =
                "Keep the phone close to the sensor. Fingerprint data stays on this device and is never uploaded."
            setContent {
                RmuixTheme {
                    TopTips(
                        modifier = Modifier.fillMaxWidth(),
                        content = message,
                        marquee = true,
                    )
                }
            }
            onNodeWithText(message).assertExists()
        }

    @Test
    fun shortMessagesComposeWithoutRolling() =
        runComposeUiTest {
            setContent { RmuixTheme { TopTips(content = "Copied", marquee = true) } }
            onNodeWithText("Copied").assertExists()
        }
}
