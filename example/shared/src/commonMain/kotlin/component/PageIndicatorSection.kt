// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.PageIndicator
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle

fun LazyListScope.pageIndicatorSection() {
    item(key = "pageIndicator") {
        CategoryTitle(text = "PageIndicator")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PageIndicator(count = 3, selectedIndex = 0)
                PageIndicator(count = 3, selectedIndex = 1)
                PageIndicator(count = 5, selectedIndex = 4)
            }
        }
    }
}
