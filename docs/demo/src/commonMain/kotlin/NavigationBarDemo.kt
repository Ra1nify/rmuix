// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Badge
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBar
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBarItem
import io.github.ra1nify.rmuix.kmp.basic.NavigationBar
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarItem
import io.github.ra1nify.rmuix.kmp.basic.NavigationItem
import io.github.ra1nify.rmuix.kmp.basic.Scaffold
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Contacts
import io.github.ra1nify.rmuix.kmp.icon.extended.Settings
import io.github.ra1nify.rmuix.kmp.icon.extended.VerticalSplit
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@Composable
fun NavigationBarDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val pages = listOf("Home", "Profile", "Settings")
                val items = listOf(
                    NavigationItem("Home", RmuixIcons.VerticalSplit),
                    NavigationItem("Profile", RmuixIcons.Contacts),
                    NavigationItem("Settings", RmuixIcons.Settings),
                )
                var selectedIndex1 by remember { mutableIntStateOf(0) }
                var selectedIndex2 by remember { mutableIntStateOf(0) }
                Card(
                    modifier = Modifier.weight(0.5f),
                ) {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedIndex1 == index,
                                        onClick = { selectedIndex1 = index },
                                        icon = item.icon,
                                        label = item.label,
                                        badge = when (index) {
                                            1 -> ({ Badge { Text("8") } })
                                            2 -> ({ Badge() })
                                            else -> null
                                        },
                                    )
                                }
                            }
                        },
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Current: ${pages[selectedIndex1]}",
                                style = RmuixTheme.textStyles.title1,
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier.weight(0.5f),
                ) {
                    Scaffold(
                        bottomBar = {
                            FloatingNavigationBar {
                                items.forEachIndexed { index, item ->
                                    FloatingNavigationBarItem(
                                        selected = selectedIndex2 == index,
                                        onClick = { selectedIndex2 = index },
                                        icon = item.icon,
                                        label = item.label,
                                        badge = when (index) {
                                            1 -> ({ Badge { Text("8") } })
                                            2 -> ({ Badge() })
                                            else -> null
                                        },
                                    )
                                }
                            }
                        },
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Current: ${pages[selectedIndex2]}",
                                style = RmuixTheme.textStyles.title1,
                            )
                        }
                    }
                }
            }
        }
    }
}
