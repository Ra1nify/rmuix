// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.CompleteState
import io.github.ra1nify.rmuix.kmp.basic.EmptyState
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

fun LazyListScope.emptyStateSection() {
    item(key = "emptyState") {
        CategoryTitle(text = "EmptyState / CompleteState")
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            EmptyStateRows()
        }
    }
}

@Composable
private fun EmptyStateRows() {
    Column {
        EmptyState(
            modifier = Modifier.padding(vertical = 16.dp),
            title = "No photos yet",
            subtitle = "Photos you take will show up here.",
            actionText = "Take a photo",
            onActionClick = {},
            illustration = { IllustrationBox() },
        )
        CompleteState(
            modifier = Modifier.padding(vertical = 16.dp),
            title = "All done",
            subtitle = "Everything has been backed up.",
            illustration = { IllustrationBox() },
        )
    }
}

@Composable
private fun IllustrationBox() {
    Box(
        modifier =
            Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(RmuixTheme.colorScheme.primary.copy(alpha = 0.12f)),
    )
}
