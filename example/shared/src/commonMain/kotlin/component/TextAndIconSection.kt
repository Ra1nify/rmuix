// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Home
import io.github.ra1nify.rmuix.kmp.icon.extended.More
import io.github.ra1nify.rmuix.kmp.icon.extended.Settings
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

fun LazyListScope.textAndIconSection() {
    item(key = "textAndIcons") {
        CategoryTitle(text = "Text & Icons")
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Title",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RmuixTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Body text renders through RmuixTheme.textStyles and follows the platform font.",
                    fontSize = 15.sp,
                    color = RmuixTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Caption uses the secondary label colour.",
                    fontSize = 12.sp,
                    color = RmuixTheme.colorScheme.onBackgroundVariant,
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Icon(imageVector = RmuixIcons.Settings, contentDescription = "Settings")
                    Icon(imageVector = RmuixIcons.Home, contentDescription = "Home")
                    Icon(imageVector = RmuixIcons.More, contentDescription = "More")
                }
            }
        }
    }
}
