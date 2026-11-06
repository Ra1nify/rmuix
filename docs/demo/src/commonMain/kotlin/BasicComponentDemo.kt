// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.BasicComponent
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Contacts
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@Composable
fun BasicComponentDemo() {
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
            Card {
                BasicComponent(
                    title = "BasicComponent",
                    summary = "Without onClick",
                )
                BasicComponent(
                    title = "Wi-Fi",
                    summary = "Connected to MIUI-WiFi",
                    onClick = { /* Handle click event */ },
                )
                BasicComponent(
                    title = "Nickname",
                    summary = "A brief introduction",
                    startAction = {
                        Icon(
                            modifier = Modifier.padding(end = 16.dp),
                            imageVector = RmuixIcons.Contacts,
                            contentDescription = "Avatar Icon",
                            tint = RmuixTheme.colorScheme.onBackground,
                        )
                    },
                    onClick = { /* Handle click event */ },
                )
                BasicComponent(
                    title = "Mobile Network",
                    summary = "SIM card not inserted",
                    enabled = false,
                )
            }
        }
    }
}
