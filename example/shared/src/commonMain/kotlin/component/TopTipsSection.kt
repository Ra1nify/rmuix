// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.TopTips
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Info

fun LazyListScope.topTipsSection() {
    item(key = "topTips") {
        CategoryTitle(text = "TopTips")
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            TopTipsRows()
        }
    }
}

@Composable
private fun TopTipsRows() {
    Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // COUIDefaultTopTipsView: title + message + inline action + close, 20dp card.
        TopTips(
            modifier = Modifier.fillMaxWidth(),
            title = "Battery saver is on",
            content = "Background activity is restricted.",
            actionText = "Settings",
            onActionClick = {},
            onClose = {},
            icon = { Icon(imageVector = RmuixIcons.Info, contentDescription = null) },
        )
        // Single-line pill: couiRoundCornerFULL (72dp) with 12dp padding.
        TopTips(
            modifier = Modifier.fillMaxWidth(),
            content = "Copied to clipboard",
            onClose = {},
        )
        // COUIMarqueeTopTips: the message collapses to one line and rolls after the 1000ms delay.
        TopTips(
            modifier = Modifier.fillMaxWidth(),
            content =
                "Keep the phone close to the sensor. Fingerprint data stays on this device and is never uploaded.",
            marquee = true,
            onClose = {},
        )
    }
}
