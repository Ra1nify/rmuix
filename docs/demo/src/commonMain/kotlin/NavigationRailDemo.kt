// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import io.github.ra1nify.rmuix.kmp.basic.NavigationItem
import io.github.ra1nify.rmuix.kmp.basic.NavigationRail
import io.github.ra1nify.rmuix.kmp.basic.NavigationRailItem
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.rememberNavigationRailState
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Contacts
import io.github.ra1nify.rmuix.kmp.icon.extended.Settings
import io.github.ra1nify.rmuix.kmp.icon.extended.VerticalSplit
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@Composable
fun NavigationRailDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .widthIn(max = 800.dp) // Wider for Rail
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val pages = listOf("Home", "Profile", "Settings")
            val items = listOf(
                NavigationItem("Home", RmuixIcons.VerticalSplit),
                NavigationItem("Profile", RmuixIcons.Contacts),
                NavigationItem("Settings", RmuixIcons.Settings),
            )
            var selectedIndex by remember { mutableIntStateOf(0) }
            val railState = rememberNavigationRailState()

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(400.dp),
            ) {
                Card(
                    modifier = Modifier.weight(0.5f).fillMaxHeight(),
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        NavigationRail(
                            state = railState,
                        ) {
                            items.forEachIndexed { index, item ->
                                NavigationRailItem(
                                    selected = selectedIndex == index,
                                    onClick = { selectedIndex = index },
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

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Current: ${pages[selectedIndex]}",
                                style = RmuixTheme.textStyles.title1,
                            )
                        }
                    }
                }
            }
        }
    }
}
