// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.RedDot
import io.github.ra1nify.rmuix.kmp.basic.RedDotAnchorShape
import io.github.ra1nify.rmuix.kmp.basic.RedDotBox
import io.github.ra1nify.rmuix.kmp.basic.RedDotMode
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@Composable
fun RedDotDemo() {
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
                Column(
                    Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CategoryTitle(text = "Modes")
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        RedDot(mode = RedDotMode.PointOnly)
                        RedDot(mode = RedDotMode.PointWithNum(9))
                        RedDot(mode = RedDotMode.PointWithNum(120))
                        RedDot(mode = RedDotMode.PointWithText("NEW"))
                    }
                    Text(
                        text = "PointOnly / PointWithNum / PointWithText; counts above 999 render as three dots",
                        style = RmuixTheme.textStyles.footnote1,
                        color = RmuixTheme.colorScheme.onSurfaceVariantActions,
                    )
                }
            }
            Card {
                Column(
                    Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CategoryTitle(text = "Pinned to a corner")
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        RedDotBox(redDot = { RedDot(mode = RedDotMode.PointOnly) }, anchorSize = 36.dp) {
                            Box(
                                Modifier
                                    .size(36.dp)
                                    .background(RmuixTheme.colorScheme.secondaryContainer),
                            )
                        }
                        RedDotBox(
                            redDot = { RedDot(mode = RedDotMode.PointWithNum(5)) },
                            anchorSize = 50.dp,
                            anchorShape = RedDotAnchorShape.Circle,
                        ) {
                            Box(
                                Modifier
                                    .size(50.dp)
                                    .background(RmuixTheme.colorScheme.secondaryContainer),
                            )
                        }
                    }
                }
            }
        }
    }
}
