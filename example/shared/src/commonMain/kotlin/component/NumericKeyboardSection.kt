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
import io.github.ra1nify.rmuix.kmp.basic.NumericKeyboard
import io.github.ra1nify.rmuix.kmp.basic.SimpleLock
import io.github.ra1nify.rmuix.kmp.basic.Text

fun LazyListScope.numericKeyboardSection() {
    item(key = "numericKeyboard") {
        CategoryTitle(text = "NumericKeyboard \u00b7 COUINumericKeyboard")
        Card(modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)) {
            NumericKeyboardRow()
        }
    }
}

@Composable
private fun NumericKeyboardRow() {
    var pin by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // COUISimpleLock above the keypad, both driven by the same PIN string.
        SimpleLock(filledCount = pin.length)
        NumericKeyboard(
            onNumber = { if (pin.length < 4) pin += it },
            onDelete = { pin = pin.dropLast(1) },
            onFinish = { pin = "" },
            finishText = "OK",
            showDelete = pin.isNotEmpty(),
        )
    }
}
