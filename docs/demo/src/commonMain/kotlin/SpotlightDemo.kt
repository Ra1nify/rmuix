// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.shader.SpotlightStyle
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@Composable
fun SpotlightDemo() {
    Box(
        modifier =
            Modifier
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
        ) {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategoryTitle(text = "Spotlight styles")
                    Row(
                        Modifier.fillMaxWidth(),
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
                                            darkTheme = false,
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
                    Text(
                        text = "Move the pointer across a tile: the light follows it, per the COUI lighting kernel",
                        style = RmuixTheme.textStyles.footnote1,
                        color = RmuixTheme.colorScheme.onSurfaceVariantActions,
                    )
                }
            }
        }
    }
}
