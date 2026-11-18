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
import io.github.ra1nify.rmuix.kmp.basic.DatePicker
import io.github.ra1nify.rmuix.kmp.basic.Text

@Composable
fun DatePickerDemo() {
    Box(
        modifier = Modifier.fillMaxSize().background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        var year by remember { mutableIntStateOf(2026) }
        var month by remember { mutableIntStateOf(12) }
        var day by remember { mutableIntStateOf(31) }

        Column(
            Modifier.padding(16.dp).widthIn(max = 600.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card {
                DatePicker(
                    year = year,
                    month = month,
                    day = day,
                    onDateChange = { newYear, newMonth, newDay ->
                        year = newYear
                        month = newMonth
                        day = newDay
                    },
                )
            }
            Text(text = "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}")
        }
    }
}
