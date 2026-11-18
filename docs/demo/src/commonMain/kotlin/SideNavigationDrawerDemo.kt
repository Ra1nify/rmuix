// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.SideNavigationDrawer
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TextButton
import io.github.ra1nify.rmuix.kmp.basic.rememberSideNavigationDrawerState
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.launch

@Composable
fun SideNavigationDrawerDemo() {
    Box(
        modifier = Modifier.fillMaxSize().background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        val state = rememberSideNavigationDrawerState()
        val scope = rememberCoroutineScope()

        Column(
            Modifier.padding(16.dp).widthIn(max = 600.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card {
                Box(modifier = Modifier.height(360.dp)) {
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
                                Text(text = "Content")
                            }
                        },
                    )
                }
            }
            TextButton(text = "Open drawer", onClick = { scope.launch { state.open() } })
            TextButton(text = "Close drawer", onClick = { scope.launch { state.close() } })
        }
    }
}
