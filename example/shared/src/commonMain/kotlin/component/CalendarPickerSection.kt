// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.CalendarPicker
import io.github.ra1nify.rmuix.kmp.basic.CalendarPickerDate
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text

fun LazyListScope.calendarPickerSection() {
    item(key = "calendarPicker") {
        CategoryTitle(text = "CalendarPicker \u00b7 COUICalendarPicker")
        Card(modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)) {
            CalendarPickerRow()
        }
    }
}

@Composable
private fun CalendarPickerRow() {
    var date by remember { mutableStateOf(CalendarPickerDate(2026, 11, 17)) }
    Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // Gallery capture-time sheet: the stock month grid with a bounded date range.
        CalendarPicker(
            selectedDate = date,
            onDateSelected = { date = it },
            minDate = CalendarPickerDate(2020, 1, 1),
            maxDate = CalendarPickerDate(2030, 12, 31),
        )
        Text(text = "selected: ${date.year}-${date.month}-${date.day}")
    }
}
