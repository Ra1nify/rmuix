// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import io.github.ra1nify.rmuix.kmp.basic.LockPatternDisplayMode
import io.github.ra1nify.rmuix.kmp.basic.LockPatternView
import io.github.ra1nify.rmuix.kmp.basic.Text

@Composable
fun LockPatternViewDemo() {
    var pattern by remember { mutableStateOf<List<Int>>(emptyList()) }
    var wrong by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize().background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(16.dp).widthIn(max = 600.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card {
                Column(
                    Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CategoryTitle(text = "LockPatternView \u00b7 COUILockPatternView")
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
                                wrong -> "Too short \u2014 try again"
                                else -> "Pattern: ${pattern.joinToString()}"
                            },
                    )
                }
            }
        }
    }
}
