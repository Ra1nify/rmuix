// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import io.github.ra1nify.rmuix.kmp.basic.SingleInputCard
import io.github.ra1nify.rmuix.kmp.basic.Text

@Composable
fun SingleInputCardDemo() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CategoryTitle(text = "SingleInputCard \u00b7 COUICardSingleInputView")
                    SingleInputCard(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.width(328.dp),
                        title = "Email",
                        hint = "Enter email address",
                        showCount = true,
                        maxCount = 50,
                        errorText = if (email.isNotEmpty() && !email.contains("@")) "Enter a valid email address" else null,
                    )
                    Text(text = "email: ${email.length}/50")
                    SingleInputCard(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.width(328.dp),
                        hint = "Password",
                        password = true,
                        maxCount = 20,
                    )
                    Text(text = "password length: ${password.length}")
                }
            }
        }
    }
}
