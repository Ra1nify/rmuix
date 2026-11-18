// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.DatePicker
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TimeLimitPicker
import io.github.ra1nify.rmuix.kmp.basic.TimePicker
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

fun LazyListScope.datePickerSection() {
    item(key = "datePicker") {
        CategoryTitle(text = "DatePicker")
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            DatePickerRow()
        }
    }
}

fun LazyListScope.timePickerSection() {
    item(key = "timePicker") {
        CategoryTitle(text = "TimePicker")
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            TimePickerRow()
        }
    }
}

@Composable
private fun DatePickerRow() {
    var year by remember { mutableIntStateOf(2026) }
    var month by remember { mutableIntStateOf(12) }
    var day by remember { mutableIntStateOf(31) }

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
    Text(
        modifier = Modifier.padding(bottom = 8.dp),
        text = "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}",
        color = RmuixTheme.colorScheme.onBackgroundVariant,
    )
}

@Composable
private fun TimePickerRow() {
    var hour by remember { mutableIntStateOf(9) }
    var minute by remember { mutableIntStateOf(30) }

    TimePicker(
        hour = hour,
        minute = minute,
        onTimeChange = { newHour, newMinute ->
            hour = newHour
            minute = newMinute
        },
    )
    Text(
        modifier = Modifier.padding(bottom = 8.dp),
        text = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} (24h)",
        color = RmuixTheme.colorScheme.onBackgroundVariant,
    )
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

fun LazyListScope.timeLimitPickerSection() {
    item(key = "timeLimitPicker") {
        CategoryTitle(text = "TimeLimitPicker")
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            TimeLimitPickerRow()
        }
    }
}

@Composable
private fun TimeLimitPickerRow() {
    var hour by remember { mutableIntStateOf(22) }
    var minute by remember { mutableIntStateOf(30) }

    TimeLimitPicker(
        hour = hour,
        minute = minute,
        onTimeChange = { newHour, newMinute ->
            hour = newHour
            minute = newMinute
        },
    )
    Text(
        modifier = Modifier.padding(bottom = 8.dp),
        text = "$hour \u5c0f\u65f6 $minute \u5206\u949f",
        color = RmuixTheme.colorScheme.onBackgroundVariant,
    )
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
