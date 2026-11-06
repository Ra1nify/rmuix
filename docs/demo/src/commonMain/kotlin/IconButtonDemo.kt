// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.IconButton
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Favorites
import io.github.ra1nify.rmuix.kmp.icon.extended.More
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@Composable
fun IconButtonDemo() {
    Box(
        modifier = Modifier
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                insideMargin = PaddingValues(16.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                ) {
                    IconButton(
                        modifier = Modifier,
                        onClick = {},
                    ) {
                        Icon(
                            imageVector = RmuixIcons.More,
                            tint = RmuixTheme.colorScheme.onBackground,
                            contentDescription = "More",
                        )
                    }
                    IconButton(
                        modifier = Modifier,
                        onClick = {},
                    ) {
                        Icon(
                            imageVector = RmuixIcons.Favorites,
                            contentDescription = "Favorites",
                        )
                    }
                    IconButton(
                        modifier = Modifier,
                        onClick = {},
                        enabled = false,
                    ) {
                        Icon(
                            imageVector = RmuixIcons.More,
                            tint = RmuixTheme.colorScheme.disabledOnSecondaryVariant,
                            contentDescription = "More",
                        )
                    }
                    IconButton(
                        modifier = Modifier,
                        onClick = {},
                        enabled = false,
                    ) {
                        Icon(
                            imageVector = RmuixIcons.Favorites,
                            tint = RmuixTheme.colorScheme.disabledOnSecondaryVariant,
                            contentDescription = "Favorites",
                        )
                    }
                }
            }
        }
    }
}
