// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.LockPatternDisplayMode
import io.github.ra1nify.rmuix.kmp.basic.LockPatternView
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TextButton

fun LazyListScope.lockPatternViewSection() {
    item(key = "lockPatternView") {
        CategoryTitle(text = "LockPatternView \u00b7 COUILockPatternView")
        Card(modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)) {
            LockPatternViewRow()
        }
    }
}

@Composable
private fun LockPatternViewRow() {
    var pattern by remember { mutableStateOf<List<Int>>(emptyList()) }
    var wrong by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // COUILockPatternView: 312dp x 280dp, 3 x 3 cells, Correct / Wrong display modes.
        LockPatternView(
            pattern = pattern,
            displayMode = if (wrong) LockPatternDisplayMode.Wrong else LockPatternDisplayMode.Correct,
            onPatternDetected = { detected ->
                pattern = detected
                wrong = detected.size < 4
            },
            onPatternCleared = { pattern = emptyList() },
        )
        Text(
            text =
                when {
                    pattern.isEmpty() -> "Draw a pattern"
                    wrong -> "Wrong: ${pattern.joinToString()}"
                    else -> "Pattern: ${pattern.joinToString()}"
                },
        )
        TextButton(
            text = "Clear",
            onClick = {
                pattern = emptyList()
                wrong = false
            },
        )
    }
}
