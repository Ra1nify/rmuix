// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.More
import io.github.ra1nify.rmuix.kmp.preference.AppInfoPreference
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@Composable
fun AppInfoPreferenceDemo() {
    Box(
        modifier = Modifier.fillMaxSize().background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(16.dp).widthIn(max = 600.dp).fillMaxWidth(),
        ) {
            Card {
                AppInfoPreference(
                    appName = "Rmuix",
                    appVersion = "Version 1.0.0",
                    copyText = "Copy",
                    icon = {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color(0xFF3A7AFE)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = RmuixIcons.More,
                                contentDescription = null,
                                tint = RmuixTheme.colorScheme.onPrimary,
                            )
                        }
                    },
                )
            }
        }
    }
}
