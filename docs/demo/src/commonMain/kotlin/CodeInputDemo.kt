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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.CodeInput
import io.github.ra1nify.rmuix.kmp.basic.Text

@Composable
fun CodeInputDemo() {
    Box(
        modifier = Modifier.fillMaxSize().background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        var code by remember { mutableStateOf("") }
        var secureCode by remember { mutableStateOf("123") }

        Column(
            Modifier.padding(16.dp).widthIn(max = 600.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CodeInput(
                value = code,
                onValueChange = { code = it },
                onCompleted = { code = it },
            )
            Text(text = "code: $code")

            CodeInput(
                value = secureCode,
                onValueChange = { secureCode = it },
                count = 4,
                secure = true,
            )
            Text(text = "secure: ${secureCode.length} digits")
        }
    }
}
