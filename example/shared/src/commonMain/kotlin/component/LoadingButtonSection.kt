// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.LoadingButton
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text

fun LazyListScope.loadingButtonSection() {
    item(key = "loadingButton") {
        CategoryTitle(text = "LoadingButton")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            var loading by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LoadingButton(
                    onClick = { loading = !loading },
                    loading = loading,
                ) {
                    Text(if (loading) "Working" else "Tap to load")
                }
                LoadingButton(
                    onClick = {},
                    loading = true,
                    enabled = false,
                ) {
                    Text("Disabled")
                }
            }
            Column(Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                Text("The three dots use the stock COUILoadingButton keyframes.")
            }
        }
    }
}
