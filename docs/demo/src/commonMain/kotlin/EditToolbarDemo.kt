// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.EditToolbar
import io.github.ra1nify.rmuix.kmp.basic.EditToolbarItem
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Delete
import io.github.ra1nify.rmuix.kmp.icon.extended.Edit
import io.github.ra1nify.rmuix.kmp.icon.extended.More
import io.github.ra1nify.rmuix.kmp.icon.extended.Share

@Composable
fun EditToolbarDemo() {
    Box(
        modifier = Modifier.fillMaxSize().background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(16.dp).widthIn(max = 600.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card {
                Column(
                    Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CategoryTitle(text = "EditToolbar \u00b7 COUIBottomFloatingEditToolbar")
                    EditToolbar(
                        items =
                            listOf(
                                EditToolbarItem(icon = RmuixIcons.Share, title = "Share"),
                                EditToolbarItem(icon = RmuixIcons.Edit, title = "Edit", redDot = true),
                                EditToolbarItem(icon = RmuixIcons.More, title = "More", redDot = true, redDotNumber = 3),
                                EditToolbarItem(icon = RmuixIcons.Delete, title = "Delete", enabled = false),
                            ),
                        onItemClick = {},
                        horizontalItems = false,
                    )
                    Text(text = "item style 3 (small screens)")
                    EditToolbar(
                        items =
                            listOf(
                                EditToolbarItem(icon = RmuixIcons.Share, title = "Share"),
                                EditToolbarItem(icon = RmuixIcons.Edit, title = "Edit"),
                                EditToolbarItem(icon = RmuixIcons.More, title = "More"),
                            ),
                        onItemClick = {},
                        horizontalItems = true,
                    )
                    Text(text = "item style 4 (medium/large screens)")
                }
            }
        }
    }
}
