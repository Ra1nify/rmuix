// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.RoundImage
import io.github.ra1nify.rmuix.kmp.basic.RoundImageType

@Composable
fun RoundImageDemo() {
    Box(
        modifier = Modifier.fillMaxSize().background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(16.dp).widthIn(max = 600.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card {
                Column(
                    Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CategoryTitle(text = "RoundImage \u00b7 COUIRoundImageView")
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RoundImage(
                            painter = ColorPainter(Color(0xFF3A7DFF)),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            type = RoundImageType.Round,
                        )
                        RoundImage(
                            painter = ColorPainter(Color(0xFF34C759)),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            type = RoundImageType.Circle,
                            showBorder = true,
                        )
                        RoundImage(
                            painter = ColorPainter(Color(0xFFFF9F0A)),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            type = RoundImageType.Shadow,
                        )
                    }
                }
            }
        }
    }
}
