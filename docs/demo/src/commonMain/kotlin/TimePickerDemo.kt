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
import io.github.ra1nify.rmuix.kmp.basic.TimePicker

@Composable
fun TimePickerDemo() {
    Box(
        modifier = Modifier.fillMaxSize().background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        var hour by remember { mutableIntStateOf(9) }
        var minute by remember { mutableIntStateOf(30) }

        Column(
            Modifier.padding(16.dp).widthIn(max = 600.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card {
                TimePicker(
                    hour = hour,
                    minute = minute,
                    onTimeChange = { newHour, newMinute ->
                        hour = newHour
                        minute = newMinute
                    },
                )
            }
            Text(text = "24h: ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}")

            Card {
                TimePicker(
                    hour = hour,
                    minute = minute,
                    is24Hour = false,
                    onTimeChange = { newHour, newMinute ->
                        hour = newHour
                        minute = newMinute
                    },
                )
            }
            Text(
                text =
                    buildString {
                        append("12h: ")
                        append(if (hour < 12) "AM " else "PM ")
                        append(if (hour % 12 == 0) 12 else hour % 12)
                        append(':')
                        append(minute.toString().padStart(2, '0'))
                    },
            )
        }
    }
}
