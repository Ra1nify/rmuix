// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.RedDot
import io.github.ra1nify.rmuix.kmp.basic.RedDotAnchorShape
import io.github.ra1nify.rmuix.kmp.basic.RedDotBox
import io.github.ra1nify.rmuix.kmp.basic.RedDotMode
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Search
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

fun LazyListScope.redDotSection() {
    item(key = "redDotBox") {
        CategoryTitle(text = "RedDotBox")
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
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
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
