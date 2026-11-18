// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.TopTips
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Info

@Composable
fun TopTipsDemo() {
    Box(
        modifier = Modifier.fillMaxSize().background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        var showTips by remember { mutableStateOf(true) }
        Column(
            Modifier.padding(16.dp).widthIn(max = 600.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategoryTitle(text = "TopTips")
                    if (showTips) {
                        TopTips(
                            modifier = Modifier.fillMaxWidth(),
                            title = "Battery saver is on",
                            content = "Background activity is restricted.",
                            actionText = "Settings",
                            onActionClick = {},
                            onClose = { showTips = false },
                            icon = { Icon(imageVector = RmuixIcons.Info, contentDescription = null) },
                        )
                    }
                    // single-line pill, no title
                    TopTips(
                        modifier = Modifier.fillMaxWidth(),
                        content = "Copied to clipboard",
                        onClose = {},
                    )
                    // marquee variant: long message rolls on one line after the 1000ms delay
                    TopTips(
                        modifier = Modifier.fillMaxWidth(),
                        content =
                            "Keep the phone close to the sensor. Fingerprint data stays on this device and is never uploaded.",
                        marquee = true,
                        onClose = {},
                    )
                }
            }
        }
    }
}
