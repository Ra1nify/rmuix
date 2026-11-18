// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Switch
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.preference.SwitchPreference
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

fun LazyListScope.switchSection() {
    item(key = "switch") {
        val switch = remember { mutableStateOf(false) }
        val switchTrue = remember { mutableStateOf(true) }
        val changes = remember { mutableIntStateOf(0) }
        val superSwitch = remember { mutableStateOf("false") }
        val superSwitchState = remember { mutableStateOf(false) }
        val superSwitchAnimState = remember { mutableStateOf(false) }

        CategoryTitle(text = "Switch")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Switch(
                    checked = switch.value,
                    onCheckedChange = {
                        switch.value = it
                        changes.intValue++
                    },
                )
                Switch(
                    checked = switchTrue.value,
                    onCheckedChange = { switchTrue.value = it },
                    modifier = Modifier.padding(start = 6.dp),
                )
                Switch(
                    checked = false,
                    onCheckedChange = { },
                    modifier = Modifier.padding(start = 6.dp),
                    enabled = false,
                )
                Switch(
                    checked = true,
                    onCheckedChange = { },
                    modifier = Modifier.padding(start = 6.dp),
                    enabled = false,
                )
            }
            Text(
                text = "Committed: ${switch.value} · ${changes.intValue} changes",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            SwitchPreference(
                title = "Switch",
                summary = "Click to expand a Switch",
                checked = superSwitchAnimState.value,
                onCheckedChange = {
                    superSwitchAnimState.value = it
                },
            )

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Read-only")
                Switch(checked = false, onCheckedChange = null)
                Switch(checked = true, onCheckedChange = null)
            }

            AnimatedVisibility(
                visible = superSwitchAnimState.value,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                SwitchPreference(
                    title = "Switch",
                    checked = superSwitchState.value,
                    endActions = {
                        Text(
                            text = superSwitch.value,
                            color = RmuixTheme.colorScheme.onSurfaceVariantActions,
                        )
                    },
                    onCheckedChange = {
                        superSwitchState.value = it
                        superSwitch.value = "$it"
                    },
                )
            }
            SwitchPreference(
                title = "Disabled Switch",
                checked = true,
                enabled = false,
                onCheckedChange = {},
            )
        }
    }
}
