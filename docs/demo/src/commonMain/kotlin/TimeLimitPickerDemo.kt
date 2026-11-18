// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TimeLimitPicker

@Composable
fun TimeLimitPickerDemo() {
    Box(
        modifier = Modifier.fillMaxSize().background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        var hour by remember { mutableIntStateOf(22) }
        var minute by remember { mutableIntStateOf(30) }

        Column(
            Modifier.padding(16.dp).widthIn(max = 600.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // The labelled form: the default of `coui_time_limit_picker.xml`.
            Card {
                TimeLimitPicker(
                    hour = hour,
                    minute = minute,
                    onTimeChange = { newHour, newMinute ->
                        hour = newHour
                        minute = newMinute
                    },
                )
            }
            Text(text = "$hour \u5c0f\u65f6 $minute \u5206\u949f")

            // `SettingsTimePicker` hides the units via setTextVisibility(false).
            Card {
                TimeLimitPicker(
                    hour = hour,
                    minute = minute,
                    showUnits = false,
                    onTimeChange = { newHour, newMinute ->
                        hour = newHour
                        minute = newMinute
                    },
                )
            }
            Text(text = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}")
        }
    }
}
