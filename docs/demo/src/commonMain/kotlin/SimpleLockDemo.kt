// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.SimpleLock
import io.github.ra1nify.rmuix.kmp.basic.SimpleLockDefaults
import io.github.ra1nify.rmuix.kmp.basic.Text
import kotlinx.coroutines.delay

@Composable
fun SimpleLockDemo() {
    var failureDemo by remember { mutableStateOf(false) }
    LaunchedEffect(failureDemo) {
        if (failureDemo) {
            delay(900)
            failureDemo = false
        }
    }
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
                    CategoryTitle(text = "SimpleLock \u00b7 COUISimpleLock")
                    SimpleLock(filledCount = 0)
                    SimpleLock(filledCount = 2)
                    SimpleLock(filledCount = 4)
                    Text(text = "4-digit PIN (FOURCIRCLE)")
                    SimpleLock(filledCount = 4, length = SimpleLockDefaults.SixCircles)
                    Text(text = "6-digit PIN (SIXCIRCLETYPE)")
                    Text(
                        text = "Tap to play the failed animation",
                        modifier = Modifier.clickable { failureDemo = true },
                    )
                    SimpleLock(filledCount = 4, failed = failureDemo, reserveFailureSpace = true)
                }
            }
        }
    }
}
