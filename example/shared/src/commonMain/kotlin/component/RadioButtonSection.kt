// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
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
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.preference.RadioButtonPreference
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

fun LazyListScope.radioButtonSection() {
    item(key = "radioButton") {
        CategoryTitle(text = "RadioButton")
        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text("Initial/static")
            RadioButton(selected = false, onClick = null)
            RadioButton(selected = true, onClick = null)
            RadioButton(selected = false, onClick = null, enabled = false)
            RadioButton(selected = true, onClick = null, enabled = false)
        }
        RadioButtonFocusDemo()
        RadioButtonCardsDemo()
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            RadioButtonPreference(
                title = "Disabled RadioButton",
                summary = "This option is unavailable",
                selected = true,
                enabled = false,
                onClick = {},
            )
        }
    }
}

@Composable
private fun RadioButtonFocusDemo() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
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
    }
}

@Composable
private fun RadioButtonCardsDemo() {
    var selectedIndex by remember { mutableIntStateOf(0) }

    listOf("Option A", "Option B", "Option C").forEachIndexed { index, title ->
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            RadioButtonPreference(
                title = title,
                summary = "Selected: ${selectedIndex == index}",
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
            )
        }
    }
}
