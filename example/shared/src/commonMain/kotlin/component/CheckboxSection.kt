// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Button
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Checkbox
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.preference.CheckboxLocation
import io.github.ra1nify.rmuix.kmp.preference.CheckboxPreference
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

fun LazyListScope.checkboxSection() {
    item(key = "checkbox") {
        CategoryTitle(text = "Checkbox")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            CheckboxRow()
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Initial/static")
                Checkbox(state = ToggleableState.Off, onClick = null)
                Checkbox(state = ToggleableState.On, onClick = null)
                Checkbox(state = ToggleableState.Indeterminate, onClick = null)
            }
            CheckboxFocusDemo()
            CheckboxPreferenceStartDemo()
            CheckboxPreferenceDemo()
            CheckboxPreference(
                title = "Disabled Checkbox",
                checked = true,
                enabled = false,
                onCheckedChange = {},
            )
        }
    }
}

@Composable
private fun CheckboxFocusDemo() {
    var checked by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
            modifier = Modifier.focusRequester(focusRequester).focusProperties { canFocus = true },
        )
        Button(onClick = { focusRequester.requestFocus() }) { Text("Focus checkbox") }
    }
}

@Composable
private fun CheckboxRow() {
    var checkbox by remember { mutableStateOf(false) }
    var checkboxTrue by remember { mutableStateOf(true) }
    var checkboxIndeterminate by remember { mutableStateOf(ToggleableState.Indeterminate) }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Checkbox(
            state = ToggleableState(checkbox),
            onClick = { checkbox = !checkbox },
        )
        Checkbox(
            state = ToggleableState(checkboxTrue),
            onClick = { checkboxTrue = !checkboxTrue },
            modifier = Modifier.padding(start = 8.dp),
        )
        Checkbox(
            state = checkboxIndeterminate,
            onClick = {
                checkboxIndeterminate =
                    when (checkboxIndeterminate) {
                        ToggleableState.Off -> ToggleableState.Indeterminate
                        ToggleableState.Indeterminate -> ToggleableState.On
                        ToggleableState.On -> ToggleableState.Off
                    }
            },
            modifier = Modifier.padding(start = 8.dp),
        )
        Checkbox(
            state = ToggleableState.Off,
            onClick = null,
            modifier = Modifier.padding(start = 8.dp),
            enabled = false,
        )
        Checkbox(
            state = ToggleableState.On,
            onClick = null,
            modifier = Modifier.padding(start = 8.dp),
            enabled = false,
        )
        Checkbox(
            state = ToggleableState.Indeterminate,
            onClick = null,
            modifier = Modifier.padding(start = 8.dp),
            enabled = false,
        )
    }
}

@Composable
private fun CheckboxPreferenceStartDemo() {
    var checked by remember { mutableStateOf(false) }

    CheckboxPreference(
        checkboxLocation = CheckboxLocation.Start,
        title = "Checkbox",
        checked = checked,
        endActions = {
            Text(
                text = "$checked",
                fontSize = RmuixTheme.textStyles.body2.fontSize,
                color = RmuixTheme.colorScheme.onSurfaceVariantActions,
            )
        },
        onCheckedChange = { checked = it },
    )
}

@Composable
private fun CheckboxPreferenceDemo() {
    var checked by remember { mutableStateOf(false) }

    CheckboxPreference(
        title = "Checkbox",
        summary = "State: $checked",
        checked = checked,
        onCheckedChange = { checked = it },
    )
}
