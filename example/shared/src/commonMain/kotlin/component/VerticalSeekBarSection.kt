// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.VerticalSeekBar
import io.github.ra1nify.rmuix.kmp.basic.VerticalSeekBarStyle

fun LazyListScope.verticalSeekBarSection() {
    item(key = "verticalSeekBar") {
        CategoryTitle(text = "VerticalSeekBar \u00b7 COUIVerticalSeekBar")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Stock defaults: 4dp track, 16dp square thumb, 10dp vertical progress padding.
                var stockValue by remember { mutableFloatStateOf(0.42f) }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    VerticalSeekBar(
                        value = stockValue,
                        onValueChange = { stockValue = it },
                        modifier = Modifier.size(36.dp, 180.dp),
                    )
                    Text(
                        text = "Default\n${(stockValue * 100).toInt()}%",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                // systemui vertical volume row: 46dp pill, radius 23dp, padding 23dp, no thumb,
                // progressFull; `volume_vertical_seek_bar_enlarge` = 0.16 and `..._radius_enlarge` = 0.27.
                var volumeValue by remember { mutableFloatStateOf(0.6f) }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    Text(
                        text = "Volume row\n${(volumeValue * 100).toInt()}%",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
