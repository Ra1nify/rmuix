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
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@Composable
fun TextDemo() {
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Card(
                    modifier = Modifier.weight(0.5f),
                    insideMargin = PaddingValues(16.dp),
                ) {
                    Text(
                        text = "Title Text",
                        style = RmuixTheme.textStyles.headline1,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    Text(
                        text = "Subtitle Text",
                        style = RmuixTheme.textStyles.subtitle,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    Text(
                        text = "Summary Text",
                        style = RmuixTheme.textStyles.body2,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    Text(
                        text = "Main Text",
                        style = RmuixTheme.textStyles.main,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    Text(
                        text = "Primary Color Text",
                        color = RmuixTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    Text(
                        text = "Secondary Text",
                        color = RmuixTheme.colorScheme.onSurfaceContainerVariant,
                    )
                }
                Card(
                    modifier = Modifier.weight(0.5f),
                    insideMargin = PaddingValues(16.dp),
                ) {
                    Text(
                        text = "Early in the day it was whispered that we should sail in a boat, only thou and I, and never a soul in the world would know of this our pilgrimage to no country and to no end.",
                    )
                }
            }
        }
    }
}
