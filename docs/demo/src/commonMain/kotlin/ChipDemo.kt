// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Search
import io.github.ra1nify.rmuix.kmp.icon.basic.Check
import io.github.ra1nify.rmuix.kmp.icon.basic.Close
import io.github.ra1nify.rmuix.kmp.basic.Chip
import io.github.ra1nify.rmuix.kmp.basic.ChipDefaults
import io.github.ra1nify.rmuix.kmp.basic.ChipStyle
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle

@Composable
fun ChipDemo() {
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
            var all by remember { mutableStateOf(true) }
            var photos by remember { mutableStateOf(false) }
            var videos by remember { mutableStateOf(false) }

            Card {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CategoryTitle(text = "Icon chips")
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing),
                        verticalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing),
                    ) {
                        Chip(
                            onClick = {},
                            label = "Search",
                            icon = { Icon(imageVector = RmuixIcons.Basic.Search, contentDescription = null) },
                        )
                        Chip(
                            onClick = { photos = !photos },
                            label = "Photos",
                            selected = photos,
                            style = ChipStyle.Selection,
                            icon = { Icon(imageVector = RmuixIcons.Basic.Check, contentDescription = null) },
                        )
                        Chip(
                            onClick = {},
                            label = "Clear",
                            onClose = {},
                            icon = { Icon(imageVector = RmuixIcons.Basic.Close, contentDescription = null) },
                        )
                    }
                    CategoryTitle(text = "Filter chips")
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing),
                        verticalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing),
                    ) {
                        Chip(onClick = { all = !all }, label = "All", selected = all)
                        Chip(onClick = { photos = !photos }, label = "Photos", selected = photos)
                        Chip(onClick = { videos = !videos }, label = "Videos", selected = videos)
                        Chip(onClick = {}, label = "Disabled", enabled = false)
                    }
                }
            }
            Card {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CategoryTitle(text = "Selection style")
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing),
                        verticalArrangement = Arrangement.spacedBy(ChipDefaults.GroupSpacing),
                    ) {
                        Chip(
                            onClick = {},
                            label = "Selected",
                            selected = true,
                            style = ChipStyle.Selection,
                        )
                        Chip(onClick = {}, label = "Unselected", style = ChipStyle.Selection)
                        Chip(
                            onClick = {},
                            label = "Disabled",
                            enabled = false,
                            style = ChipStyle.Selection,
                        )
                    }
                }
            }
        }
    }
}
