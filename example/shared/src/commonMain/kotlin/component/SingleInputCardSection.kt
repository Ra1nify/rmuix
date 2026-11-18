// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import io.github.ra1nify.rmuix.kmp.basic.SingleInputCard
import io.github.ra1nify.rmuix.kmp.basic.Text

fun LazyListScope.singleInputCardSection() {
    item(key = "singleInputCard") {
        CategoryTitle(text = "SingleInputCard \u00b7 COUICardSingleInputView")
        Card(modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)) {
            SingleInputCardRow()
        }
    }
}

@Composable
private fun SingleInputCardRow() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Settings email binding: 328dp wide, 50 character limit with the stock count.
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
        // Password mode with the eye toggle and quick delete.
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
