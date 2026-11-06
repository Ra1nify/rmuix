// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Button
import io.github.ra1nify.rmuix.kmp.basic.ButtonDefaults
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TextButton
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Contacts
import io.github.ra1nify.rmuix.kmp.icon.extended.Favorites
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@Composable
fun ButtonDemo() {
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
            var buttonText1 by remember { mutableStateOf("Button") }
            var buttonText2 by remember { mutableStateOf("TextButton") }
            var clickCount1 by remember { mutableIntStateOf(0) }
            var clickCount2 by remember { mutableIntStateOf(0) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    onClick = {
                        clickCount1++
                        buttonText1 = "Button: $clickCount1"
                    },
                ) {
                    Icon(
                        imageVector = RmuixIcons.Favorites,
                        contentDescription = "Favorites",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = buttonText1,
                        style = RmuixTheme.textStyles.button,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
                TextButton(
                    text = buttonText2,
                    onClick = {
                        clickCount2++
                        buttonText2 = "TextButton: $clickCount2"
                    },
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Button(
                    enabled = false,
                    onClick = {},
                    colors = ButtonDefaults.buttonColorsPrimary(),
                ) {
                    Icon(
                        imageVector = RmuixIcons.Contacts,
                        contentDescription = null,
                        tint = RmuixTheme.colorScheme.disabledOnSecondaryVariant,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = "Disabled Button",
                        style = RmuixTheme.textStyles.button,
                        color = RmuixTheme.colorScheme.disabledOnSecondaryVariant,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
                TextButton(
                    text = "Disabled TextButton",
                    enabled = false,
                    onClick = {},
                )
            }
        }
    }
}
