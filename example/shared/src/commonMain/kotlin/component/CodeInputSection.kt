// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.CodeInput
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

fun LazyListScope.codeInputSection() {
    item(key = "codeInput") {
        CategoryTitle(text = "CodeInput")
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            CodeInputRows()
        }
    }
}

@Composable
private fun CodeInputRows() {
    var code by remember { mutableStateOf("") }
    var secureCode by remember { mutableStateOf("") }

    Column {

        CodeInput(
            modifier = Modifier.padding(top = 8.dp),
            value = code,
            onValueChange = { code = it },
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = if (code.isEmpty()) "Enter the 6-digit code" else code,
            color = RmuixTheme.colorScheme.onBackgroundVariant,
        )
        CodeInput(
            modifier = Modifier.padding(top = 16.dp),
            value = secureCode,
            onValueChange = { secureCode = it },
            count = 4,
            secure = true,
        )
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            text = "${secureCode.length} of 4 digits (secure)",
            color = RmuixTheme.colorScheme.onBackgroundVariant,
        )
    }
}
