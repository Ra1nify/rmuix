// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.VerticalSeekBar
import io.github.ra1nify.rmuix.kmp.basic.VerticalSeekBarStyle

@Composable
fun VerticalSeekBarDemo() {
    Box(
        modifier = Modifier.fillMaxSize().background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(16.dp).widthIn(max = 600.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategoryTitle(text = "VerticalSeekBar \u00b7 COUIVerticalSeekBar")
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        var stockValue by remember { mutableFloatStateOf(0.42f) }
                        VerticalSeekBar(
                            value = stockValue,
                            onValueChange = { stockValue = it },
                            modifier = Modifier.size(36.dp, 180.dp),
                        )
                        var volumeValue by remember { mutableFloatStateOf(0.6f) }
                        VerticalSeekBar(
                            value = volumeValue,
                            onValueChange = { volumeValue = it },
                            showThumb = false,
                            progressFull = true,
                            style =
                                VerticalSeekBarStyle(
                                    minWidth = 46.dp,
                                    backgroundWidth = 46.dp,
                                    backgroundRadius = 23.dp,
                                    paddingVertical = 23.dp,
                                    backgroundEnlargeScale = 0.16f,
                                    progressEnlargeScale = 0.16f,
                                    backgroundRadiusEnlargeScale = 0.27f,
                                    progressRadiusEnlargeScale = 0.27f,
                                ),
                            modifier = Modifier.size(46.dp, 180.dp),
                        )
                        Text(text = "Drag the bars")
                    }
                }
            }
        }
    }
}
