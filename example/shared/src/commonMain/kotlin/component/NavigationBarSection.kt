// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.NavigationBar
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarItem
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.ArrowRight
import io.github.ra1nify.rmuix.kmp.icon.basic.Check
import io.github.ra1nify.rmuix.kmp.icon.basic.Search
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

fun LazyListScope.navigationBarSection() {
    item(key = "navigationBar") {
        CategoryTitle(text = "NavigationBar")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            var selected by remember { mutableIntStateOf(0) }
            val items =
                listOf(
                    "Home" to RmuixIcons.Basic.Search,
                    "Profile" to RmuixIcons.Basic.ArrowRight,
                    "Done" to RmuixIcons.Basic.Check,
                )
            NavigationBar(showDivider = false) {
                items.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = selected == index,
                        onClick = { selected = index },
                        icon = icon,
                        label = label,
                    )
                }
            }
        }
        CategoryTitle(text = "NavigationBar (enlarged)")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            var enlargedIndex by remember { mutableIntStateOf(0) }
            val items =
                listOf(
                    "Home" to RmuixIcons.Basic.Search,
                    "Profile" to RmuixIcons.Basic.ArrowRight,
                    "Done" to RmuixIcons.Basic.Check,
                )
            Text(
                modifier = Modifier.padding(12.dp),
                text = "enlarged = true: 70dp items, the selected item grows its icon to 64dp and hides its label",
                style = RmuixTheme.textStyles.footnote1,
                color = RmuixTheme.colorScheme.onSurfaceVariantActions,
            )
            NavigationBar(enlarged = true, showDivider = false) {
                items.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = enlargedIndex == index,
                        onClick = { enlargedIndex = index },
                        icon = icon,
                        label = label,
                    )
                }
            }
        }
    }
}
