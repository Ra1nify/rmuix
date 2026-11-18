// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.extend.colorpicker.ColorPicker
import io.github.ra1nify.rmuix.kmp.extend.preference.ColorPickerPreference

fun LazyListScope.colorPickerSection() {
    item(key = "colorPicker") {
        CategoryTitle(text = "ColorPicker (rmuix-extend)")
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            ColorPickerRows()
        }
    }
}

@Composable
private fun ColorPickerRows() {
    var accent by remember { mutableStateOf(Color(0xFF0080FF)) }
    Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // No ColorOS colour picker exists, so the page documents rmuix-extend tokens instead of stock
        // metrics.
        ColorPicker(
            modifier = Modifier.fillMaxWidth(),
            color = accent,
            onColorChange = { accent = it },
        )
        ColorPickerPreference(
            color = accent,
            onColorChange = { accent = it },
            title = "Accent colour",
            summary = "Drag the picker, then confirm",
        )
    }
}
