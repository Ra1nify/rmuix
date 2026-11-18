// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.BasicComponent
import io.github.ra1nify.rmuix.kmp.basic.BasicComponentGroup
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle

@Composable
private fun GroupRow(
    title: String,
    summary: String? = null,
) {
    // The group owns the stock 16dp `coui_preference_card_margin_horizontal` inset.
    BasicComponent(
        title = title,
        summary = summary,
        onClick = {},
    )
}

fun LazyListScope.basicComponentGroupSection() {
    item(key = "basicComponentGroup") {
        CategoryTitle(text = "BasicComponentGroup")
        // Two groups back to back: rows inside a group are separated by the 0.33dp hairline, the seam
        // between the two cards has none (COUIPreference.drawDivider() only fires for HEAD / MIDDLE).
        BasicComponentGroup {
            GroupRow("Wi-Fi", "Connected")
            GroupRow("Bluetooth", "On")
            GroupRow("Mobile data")
        }
        // A full stock section: category title (COUIPreferenceCategory) + card + footer
        // (SettingsCategoryBottomTipPreference).
        BasicComponentGroup(
            modifier = Modifier.padding(top = 12.dp),
            title = "Display",
            footer = "Screen brightness adapts to the ambient light.",
        ) {
            GroupRow("Display", "Dark mode off")
            GroupRow("Sound & vibration", "Ring")
        }
    }
}
