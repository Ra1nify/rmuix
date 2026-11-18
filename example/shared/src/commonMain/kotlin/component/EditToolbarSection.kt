// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
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

fun LazyListScope.editToolbarSection() {
    item(key = "editToolbar") {
        CategoryTitle(text = "EditToolbar \u00b7 COUIBottomFloatingEditToolbar")
        Card(modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)) {
            EditToolbarRows()
        }
    }
}

@Composable
private fun EditToolbarRows() {
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Small screens: item style 3 (icon over 10sp title, 56dp row).
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
        Text(text = "Small screen (item style 3)")
        // Medium/large screens: item style 4 (24dp icon beside 14sp title, 44dp row).
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
        Text(text = "Medium/large screen (item style 4)")
    }
}
