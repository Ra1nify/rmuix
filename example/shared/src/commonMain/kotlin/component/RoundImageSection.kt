// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
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

fun LazyListScope.roundImageSection() {
    item(key = "roundImage") {
        CategoryTitle(text = "RoundImage \u00b7 COUIRoundImageView")
        Card(modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp)) {
            RoundImageRow()
        }
    }
}

@Composable
private fun RoundImageRow() {
    Row(
        modifier = Modifier.padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Settings preference icons: 36dp, couiType="round".
        RoundImage(
            painter = ColorPainter(Color(0xFF3A7DFF)),
            contentDescription = "round",
            modifier = Modifier.size(36.dp),
            type = RoundImageType.Round,
        )
        RoundImage(
            painter = ColorPainter(Color(0xFF34C759)),
            contentDescription = "circle",
            modifier = Modifier.size(60.dp),
            type = RoundImageType.Circle,
            showBorder = true,
        )
        RoundImage(
            painter = ColorPainter(Color(0xFFFF9F0A)),
            contentDescription = "shadow",
            modifier = Modifier.size(48.dp),
            type = RoundImageType.Shadow,
        )
    }
}
