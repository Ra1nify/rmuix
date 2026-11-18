// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.AlphabetIndexBar
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text

fun LazyListScope.alphabetIndexBarSection() {
    item(key = "alphabetIndexBar") {
        CategoryTitle(text = "AlphabetIndexBar \u00b7 COUITouchSearchView")
        Card(modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)) {
            AlphabetIndexBarRow()
        }
    }
}

@Composable
private fun AlphabetIndexBarRow() {
    var selected by remember { mutableStateOf("-") }
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Settings foldable app list: 469dp strip, A-Z + "#" keys, pressed-letter bubble.
        AlphabetIndexBar(
            onLetterSelected = { selected = it },
            onLetterLongPressed = { selected = "long: $it" },
            modifier = Modifier.height(469.dp),
        )
        Text(text = "selected: $selected")
    }
}
