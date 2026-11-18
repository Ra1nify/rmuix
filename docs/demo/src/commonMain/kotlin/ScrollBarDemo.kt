// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.VerticalScrollBar
import io.github.ra1nify.rmuix.kmp.basic.rememberScrollBarAdapter
import io.github.ra1nify.rmuix.kmp.interfaces.ExperimentalScrollBarApi
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@OptIn(ExperimentalScrollBarApi::class)
@Composable
fun ScrollBarDemo() {
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
                val scrollState = rememberScrollState()
                Box(Modifier.fillMaxWidth().height(220.dp)) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        repeat(30) { index ->
                            Text(
                                text = "Row ${index + 1}",
                                style = RmuixTheme.textStyles.body2,
                                color = RmuixTheme.colorScheme.onSurface,
                            )
                        }
                    }
                    VerticalScrollBar(
                        adapter = rememberScrollBarAdapter(scrollState),
                        modifier =
                            Modifier
                                .align(Alignment.CenterEnd)
                                .fillMaxHeight(),
                    )
                }
            }
            Text(
                text = "The bar only reserves the 14dp strip (3dp thumb + 2 x 5.5dp insets); hover to widen the thumb",
                style = RmuixTheme.textStyles.footnote1,
                color = RmuixTheme.colorScheme.onSurfaceVariantActions,
            )
        }
    }
}
