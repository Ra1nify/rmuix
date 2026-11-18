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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Scaffold
import io.github.ra1nify.rmuix.kmp.extend.colorpicker.ColorPicker
import io.github.ra1nify.rmuix.kmp.extend.preference.ColorPickerPreference

@Composable
fun ColorPickerDemo() {
    Scaffold {
        Box(
            modifier = Modifier.fillMaxSize().background(demoBackground()),
            contentAlignment = Alignment.Center,
        ) {
            var accent by remember { mutableStateOf(Color(0xFF0080FF)) }
            Column(
                Modifier
                    .padding(16.dp)
                    .widthIn(max = 600.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Card {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CategoryTitle(text = "ColorPicker")
                        ColorPicker(
                            modifier = Modifier.fillMaxWidth(),
                            color = accent,
                            onColorChange = { accent = it },
                        )
                    }
                }
                Card {
                    ColorPickerPreference(
                        color = accent,
                        onColorChange = { accent = it },
                        title = "Accent colour",
                        summary = "Used for highlights",
                    )
                }
            }
        }
    }
}
