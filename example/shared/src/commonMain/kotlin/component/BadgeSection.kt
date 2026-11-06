// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Badge
import io.github.ra1nify.rmuix.kmp.basic.BadgedBox
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.SmallTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Email
import io.github.ra1nify.rmuix.kmp.icon.extended.Favorites
import io.github.ra1nify.rmuix.kmp.icon.extended.Messages
import io.github.ra1nify.rmuix.kmp.icon.extended.Settings

fun LazyListScope.badgeSection() {
    item(key = "badge") {
        SmallTitle(text = "Badge")
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    BadgedBox(badge = { Badge() }) {
                        Icon(
                            imageVector = RmuixIcons.Messages,
                            contentDescription = "Messages",
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    BadgedBox(badge = { Badge { Text("8") } }) {
                        Icon(
                            imageVector = RmuixIcons.Email,
                            contentDescription = "Email",
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    BadgedBox(badge = { Badge { Text("99+") } }) {
                        Icon(
                            imageVector = RmuixIcons.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    BadgedBox(badge = { Badge { Text("5") } }) {
                        Icon(
                            imageVector = RmuixIcons.Favorites,
                            contentDescription = "Favorites",
                            modifier = Modifier.size(28.dp),
                        )
                    }
                }
            }
        }
    }
}
