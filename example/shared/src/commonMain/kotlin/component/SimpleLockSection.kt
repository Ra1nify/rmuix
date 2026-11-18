// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.SimpleLock
import io.github.ra1nify.rmuix.kmp.basic.SimpleLockDefaults
import io.github.ra1nify.rmuix.kmp.basic.Text

fun LazyListScope.simpleLockSection() {
    item(key = "simpleLock") {
        var failureDemo by remember { mutableStateOf(false) }
        LaunchedEffect(failureDemo) {
            if (failureDemo) {
                delay(900)
                failureDemo = false
            }
        }
        CategoryTitle(text = "SimpleLock \u00b7 COUISimpleLock")
        Card(modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // FOURCIRCLE type: empty, 2 of 4, 4 of 4.
                SimpleLock(filledCount = 0)
                SimpleLock(filledCount = 2)
                SimpleLock(filledCount = 4)
                Text(text = "4-digit PIN (FOURCIRCLE)")
                // SIXCIRCLETYPE: 4 of 6.
                SimpleLock(
                    filledCount = 4,
                    length = SimpleLockDefaults.SixCircles,
                )
                Text(text = "6-digit PIN (SIXCIRCLETYPE)")
                // `failed` runs the stock 800ms shake/fall; tapping replays it.
                Text(
                    text = "Tap to play the failed animation",
                    modifier = Modifier.clickable { failureDemo = true },
                )
                SimpleLock(
                    filledCount = 4,
                    failed = failureDemo,
                    reserveFailureSpace = true,
                )
            }
        }
    }
}
