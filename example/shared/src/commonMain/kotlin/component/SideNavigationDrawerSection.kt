// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.SideNavigationDrawer
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TextButton
import io.github.ra1nify.rmuix.kmp.basic.rememberSideNavigationDrawerState
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.launch

fun LazyListScope.sideNavigationDrawerSection() {
    item(key = "sideNavigationDrawer") {
        CategoryTitle(text = "SideNavigationDrawer")
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            SideNavigationDrawerRow()
        }
    }
}

@Composable
private fun SideNavigationDrawerRow() {
    val state = rememberSideNavigationDrawerState()
    val scope = rememberCoroutineScope()

    Column {
        Box(modifier = Modifier.height(320.dp)) {
            SideNavigationDrawer(
                state = state,
                drawerContent = {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Albums")
                        Text(text = "Photos")
                        Text(text = "Trash")
                    }
                },
                content = {
                    Box(
                        modifier = Modifier.fillMaxSize().background(RmuixTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "Content (drag from the left edge)")
                    }
                },
            )
        }
        TextButton(
            modifier = Modifier.padding(top = 8.dp),
            text = "Open drawer",
            onClick = { scope.launch { state.open() } },
        )
    }
}
