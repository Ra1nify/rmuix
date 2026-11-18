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
import io.github.ra1nify.rmuix.kmp.basic.ChipTabItem
import io.github.ra1nify.rmuix.kmp.basic.ChipTabRow
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Check
import io.github.ra1nify.rmuix.kmp.icon.basic.Search
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@Composable
fun ChipTabRowDemo() {
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
            var selected by remember { mutableIntStateOf(0) }
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategoryTitle(text = "Chip tabs")
                    ChipTabRow(
                        items =
                            listOf(
                                ChipTabItem(
                                    label = "All",
                                    icon = { Icon(RmuixIcons.Basic.Search, contentDescription = null) },
                                ),
                                ChipTabItem(label = "Unread", count = "12"),
                                ChipTabItem(
                                    label = "Done",
                                    icon = { Icon(RmuixIcons.Basic.Check, contentDescription = null) },
                                ),
                            ),
                        selectedIndex = selected,
                        onSelect = { selected = it },
                    )
                    Text(
                        text = "32dp tall, 14sp label, 16dp icon with a 4dp gap, optional count",
                        style = RmuixTheme.textStyles.footnote1,
                        color = RmuixTheme.colorScheme.onSurfaceVariantActions,
                    )
                }
            }
        }
    }
}
