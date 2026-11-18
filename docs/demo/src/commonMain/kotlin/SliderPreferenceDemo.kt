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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.preference.SliderPreference

@Composable
fun SliderPreferenceDemo() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            var volume by remember { mutableFloatStateOf(0.4f) }
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryTitle(text = "SliderPreference")
                    SliderPreference(
                        title = "Media volume",
                        summary = "Value: ${(volume * 100).toInt()}%",
                        value = volume,
                        onValueChange = { volume = it },
                    )
                    // The seek_bar_preference.xml tips: 14sp labels beside the track.
                    SliderPreference(
                        title = "Brightness",
                        value = volume,
                        onValueChange = { volume = it },
                        startLabel = "Dim",
                        endLabel = "Bright",
                    )
                }
            }
        }
    }
}
