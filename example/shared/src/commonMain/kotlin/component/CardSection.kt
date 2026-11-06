// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.basic.ButtonDefaults
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CardDefaults
import io.github.ra1nify.rmuix.kmp.basic.SmallTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TextButton
import io.github.ra1nify.rmuix.kmp.overlay.OverlayDialog
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.PressFeedbackType

fun LazyListScope.cardSection() {
    item(key = "card") {
        SmallTitle(text = "Card")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
            colors = CardDefaults.defaultColors(
                color = RmuixTheme.colorScheme.primaryVariant,
            ),
            insideMargin = PaddingValues(16.dp),
            pressFeedbackType = PressFeedbackType.None,
            showIndication = true,
        ) {
            Text(
                color = RmuixTheme.colorScheme.onPrimaryVariant,
                text = "Card",
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                color = RmuixTheme.colorScheme.onPrimaryVariant,
                text = "ShowIndication: true",
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(
                modifier = Modifier.weight(1f),
                insideMargin = PaddingValues(16.dp),
                pressFeedbackType = PressFeedbackType.Sink,
                onClick = { println("Card click") },
                content = {
                    Text(
                        color = RmuixTheme.colorScheme.onSurface,
                        text = "Card",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
                        text = "PressFeedback\nType: Sink",
                        style = RmuixTheme.textStyles.paragraph,
                    )
                },
            )
            Card(
                modifier = Modifier.weight(1f),
                insideMargin = PaddingValues(16.dp),
                pressFeedbackType = PressFeedbackType.Tilt,
                onLongPress = { println("Card long press") },
                content = {
                    Text(
                        color = RmuixTheme.colorScheme.onSurface,
                        text = "Card",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
                        text = "PressFeedback\nType: Tilt",
                        style = RmuixTheme.textStyles.paragraph,
                    )
                },
            )
        }
        LongPressHoldDownCardDemo()
    }
}

@Composable
private fun LongPressHoldDownCardDemo() {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var holdDown by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp),
        insideMargin = PaddingValues(16.dp),
        pressFeedbackType = PressFeedbackType.Sink,
        showIndication = true,
        holdDownState = holdDown,
        onLongPress = {
            showDialog = true
            holdDown = true
        },
        content = {
            Text(
                color = RmuixTheme.colorScheme.onSurface,
                text = "Card",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
                text = "Long press to show dialog",
                style = RmuixTheme.textStyles.paragraph,
            )
        },
    )

    OverlayDialog(
        show = showDialog,
        title = "Long Press Action",
        summary = "Triggered by long pressing the card.",
        onDismissRequest = { showDialog = false },
        onDismissFinished = { holdDown = false },
        content = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(
                    text = "Cancel",
                    onClick = { showDialog = false },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(20.dp))
                TextButton(
                    text = "Confirm",
                    onClick = { showDialog = false },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                )
            }
        },
    )
}
