// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Chip
import io.github.ra1nify.rmuix.kmp.basic.ChipGroup
import io.github.ra1nify.rmuix.kmp.basic.ChipGroupItem
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle

@Composable
fun ChipGroupDemo() {
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
                .widthIn(max = 600.dp),
        ) {
            Card {
                Column(modifier = Modifier.padding(12.dp)) {
                    CategoryTitle(text = "20 chips, collapsed to 3 rows")
                    ChipGroup {
                        repeat(20) { index ->
                            Chip(onClick = {}, label = "Chip $index")
                        }
                    }
                    CategoryTitle(text = "List overload: single selection, one required")
                    ChipGroup(
                        items =
                            List(8) { index ->
                                ChipGroupItem(label = "Option ${index + 1}")
                            },
                        singleSelection = true,
                        selectionRequired = true,
                    )
                }
            }
        }
    }
}
