// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Button
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.RadioButton
import io.github.ra1nify.rmuix.kmp.basic.RadioButtonDefaults
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.preference.RadioButtonPreference
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@Composable
fun RadioButtonDemo() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var selectedIndex by remember { mutableIntStateOf(0) }
            val focusRequester = remember { FocusRequester() }

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Text("Initial/static")
                RadioButton(selected = false, onClick = null)
                RadioButton(selected = true, onClick = null)
                RadioButton(selected = false, onClick = null, enabled = false)
                RadioButton(selected = true, onClick = null, enabled = false)
            }

            Text("Left: platform role; right: app primary")
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 },
                    modifier = Modifier.focusRequester(focusRequester).focusProperties { canFocus = true },
                )
                RadioButton(
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 },
                    feedbackColors = RadioButtonDefaults.feedbackColors(activatedColor = RmuixTheme.colorScheme.primary),
                )
                Button(onClick = { focusRequester.requestFocus() }) { Text("Focus radio") }
            }

            Card {
                RadioButtonPreference(
                    title = "Option A",
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 },
                )
            }
            Card {
                RadioButtonPreference(
                    title = "Option B",
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 },
                )
            }
            Card {
                RadioButtonPreference(
                    title = "Option C",
                    selected = selectedIndex == 2,
                    onClick = { selectedIndex = 2 },
                )
            }
        }
    }
}
