// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.shader.SpotlightStyle
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

fun LazyListScope.spotlightSection() {
    item(key = "spotlight") {
        CategoryTitle(text = "Spotlight")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                listOf(
                    "Capsule" to SpotlightStyle.Capsule,
                    "Circle" to SpotlightStyle.Circle,
                    "Rounded" to SpotlightStyle.RoundedRectangle,
                ).forEach { (label, style) ->
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(72.dp)
                                .rmuixSpotlight(
                                    shape = RoundedCornerShape(16.dp),
                                    style = style,
                                    darkTheme = null,
                                ).background(
                                    color = RmuixTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(16.dp),
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = label, style = RmuixTheme.textStyles.body2)
                    }
                }
            }
        }
    }
}
