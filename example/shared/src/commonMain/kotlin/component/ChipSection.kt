// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Chip
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.ChipDefaults
import io.github.ra1nify.rmuix.kmp.basic.ChipGroup
import io.github.ra1nify.rmuix.kmp.basic.ChipStyle
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Search

fun LazyListScope.chipSection() {
    item(key = "chip") {
        CategoryTitle(text = "Chip")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                var all by remember { mutableStateOf(true) }
                var photos by remember { mutableStateOf(false) }
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing),
                    verticalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing),
                ) {
                    Chip(onClick = { all = !all }, label = "All", selected = all)
                    Chip(onClick = { photos = !photos }, label = "Photos", selected = photos)
                    Chip(onClick = {}, label = "Disabled", enabled = false)
                    Chip(onClick = {}, label = "Selected", selected = true, style = ChipStyle.Selection)
                        Chip(
                            onClick = {},
                            label = "Icon",
                            icon = { Icon(imageVector = RmuixIcons.Basic.Search, contentDescription = null) },
                        )
                    Chip(onClick = {}, label = "Selection", style = ChipStyle.Selection)
                }
                ChipGroup {
                    repeat(14) { index ->
                        Chip(onClick = {}, label = "Tag $index")
                    }
                }
            }
        }
    }
}
