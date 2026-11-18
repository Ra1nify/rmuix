// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.SegmentButton
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle

fun LazyListScope.segmentButtonSection() {
    item(key = "segmentButton") {
        CategoryTitle(text = "SegmentButton")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            var defaultIndex by remember { mutableIntStateOf(0) }
            var tinyIndex by remember { mutableIntStateOf(1) }
            SegmentButton(
                options = listOf("Day", "Week", "Month"),
                selectedIndex = defaultIndex,
                onSelect = { defaultIndex = it },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
            SegmentButton(
                options = listOf("On", "Off"),
                selectedIndex = tinyIndex,
                onSelect = { tinyIndex = it },
                tiny = true,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
            )
        }
    }
}
