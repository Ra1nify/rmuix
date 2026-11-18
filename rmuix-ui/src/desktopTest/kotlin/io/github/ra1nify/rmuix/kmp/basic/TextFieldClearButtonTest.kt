// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class TextFieldClearButtonTest {
    @Test
    fun showClearButtonClearsTheTextAndHidesWhenEmpty() =
        runComposeUiTest {
            var text by mutableStateOf("Hello")
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Box(Modifier.size(320.dp, 120.dp)) {
                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            showClearButton = true,
                            modifier = Modifier.width(320.dp).testTag("field").align(Alignment.TopStart),
                        )
                    }
                }
            }
            waitForIdle()
            onNodeWithContentDescription("Clear text").performClick()
            waitForIdle()
            assertEquals("", text, "the clear button empties the field")
            onNodeWithContentDescription("Clear text").assertDoesNotExist()
        }
}
