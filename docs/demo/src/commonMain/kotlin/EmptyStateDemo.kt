// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CompleteState
import io.github.ra1nify.rmuix.kmp.basic.EmptyState
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@Composable
fun EmptyStateDemo() {
    Box(
        modifier = Modifier.fillMaxSize().background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(16.dp).widthIn(max = 600.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card {
                EmptyState(
                    modifier = Modifier.padding(vertical = 24.dp),
                    title = "No photos yet",
                    subtitle = "Photos you take will show up here.",
                    actionText = "Take a photo",
                    onActionClick = {},
                    illustration = { Illustration() },
                )
            }
            Card {
                CompleteState(
                    modifier = Modifier.padding(vertical = 24.dp),
                    title = "All done",
                    subtitle = "Everything has been backed up.",
                    illustration = { Illustration() },
                )
            }
        }
    }
}

@Composable
private fun Illustration() {
    Box(
        modifier =
            Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(RmuixTheme.colorScheme.primary.copy(alpha = 0.12f)),
    )
}
