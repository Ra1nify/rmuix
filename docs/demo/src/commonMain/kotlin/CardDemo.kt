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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CardDefaults
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.PressFeedbackType

@Composable
fun CardDemo() {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                colors = CardDefaults.defaultColors(
                    color = RmuixTheme.colorScheme.primaryVariant,
                ),
                insideMargin = PaddingValues(16.dp),
            ) {
                Text(
                    color = RmuixTheme.colorScheme.onPrimary,
                    text = "Card",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    color = RmuixTheme.colorScheme.onPrimaryVariant,
                    text = "This is a Card",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    insideMargin = PaddingValues(16.dp),
                    pressFeedbackType = PressFeedbackType.Sink,
                    showIndication = true,
                    onClick = { },
                    content = {
                        Text(
                            color = RmuixTheme.colorScheme.onSurface,
                            text = "Card",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
                            text = "PressFeedback: Sink\nShowIndication: true",
                            style = RmuixTheme.textStyles.paragraph,
                        )
                    },
                )
                Card(
                    modifier = Modifier.weight(1f),
                    insideMargin = PaddingValues(16.dp),
                    pressFeedbackType = PressFeedbackType.Tilt,
                    content = {
                        Text(
                            color = RmuixTheme.colorScheme.onSurface,
                            text = "Card",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
                            text = "PressFeedback: Tilt\nShowIndication: false",
                            style = RmuixTheme.textStyles.paragraph,
                        )
                    },
                )
            }
        }
    }
}
