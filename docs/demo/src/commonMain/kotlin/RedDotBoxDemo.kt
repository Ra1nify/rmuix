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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.RedDot
import io.github.ra1nify.rmuix.kmp.basic.RedDotAnchorKind
import io.github.ra1nify.rmuix.kmp.basic.RedDotAnchorShape
import io.github.ra1nify.rmuix.kmp.basic.RedDotBox
import io.github.ra1nify.rmuix.kmp.basic.RedDotMode
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Search

@Composable
fun RedDotBoxDemo() {
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card {
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    RedDotBox(
                        redDot = { RedDot(mode = RedDotMode.PointOnly) },
                        anchorSize = 36.dp,
                        anchorShape = RedDotAnchorShape.Rectangle,
                    ) {
                        Icon(
                            imageVector = RmuixIcons.Basic.Search,
                            contentDescription = "Rectangle anchor",
                            modifier = Modifier.size(36.dp),
                        )
                    }
                    RedDotBox(
                        redDot = { RedDot(mode = RedDotMode.PointOnly) },
                        anchorSize = 36.dp,
                        anchorShape = RedDotAnchorShape.Circle,
                    ) {
                        Box(
                            Modifier
                                .size(36.dp)
                                .background(RmuixTheme.colorScheme.secondaryContainer),
                        )
                    }
                    RedDotBox(
                        redDot = { RedDot(mode = RedDotMode.PointWithNum(9)) },
                        anchorSize = 50.dp,
                        anchorShape = RedDotAnchorShape.Circle,
                        redDotKind = RedDotAnchorKind.Number,
                    ) {
                        Box(
                            Modifier
                                .size(50.dp)
                                .background(RmuixTheme.colorScheme.secondaryContainer),
                        )
                    }
                }
            }
            Text(
                text = "RedDotBox pins a dot into the top-end corner with the stock margins",
                style = RmuixTheme.textStyles.footnote1,
                color = RmuixTheme.colorScheme.onSurfaceVariantActions,
            )
        }
    }
}
