// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.BasicComponent
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.SmallTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

fun LazyListScope.basicComponentSection() {
    item(key = "basicComponent") {
        SmallTitle(text = "Basic Component")
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            BasicComponent(
                title = "Title",
                summary = "Summary",
                startAction = {
                    Text(
                        text = "Start",
                    )
                },
                endActions = {
                    Text(
                        text = "End1",
                        fontSize = RmuixTheme.textStyles.body2.fontSize,
                        color = RmuixTheme.colorScheme.onSurfaceVariantActions,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "End2",
                        fontSize = RmuixTheme.textStyles.body2.fontSize,
                        color = RmuixTheme.colorScheme.onSurfaceVariantActions,
                    )
                },
                enabled = true,
            )
            BasicComponent(
                title = "Title",
                summary = "Summary",
                startAction = {
                    Text(
                        text = "Start",
                        color = RmuixTheme.colorScheme.disabledOnSecondaryVariant,
                    )
                },
                endActions = {
                    Text(
                        text = "End1",
                        fontSize = RmuixTheme.textStyles.body2.fontSize,
                        color = RmuixTheme.colorScheme.disabledOnSecondaryVariant,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "End2",
                        fontSize = RmuixTheme.textStyles.body2.fontSize,
                        color = RmuixTheme.colorScheme.disabledOnSecondaryVariant,
                    )
                },
                enabled = false,
            )
        }
    }
}
