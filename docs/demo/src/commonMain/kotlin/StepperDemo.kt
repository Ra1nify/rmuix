// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Stepper
import io.github.ra1nify.rmuix.kmp.basic.Text

@Composable
fun StepperDemo() {
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
            var basic by remember { mutableIntStateOf(1) }
            Card {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Stepper(value = basic, onValueChange = { basic = it })
                }
            }

            var bounded by remember { mutableIntStateOf(0) }
            Card {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Stepper(
                        value = bounded,
                        onValueChange = { bounded = it },
                        minValue = 0,
                        maxValue = 5,
                    )
                }
            }

            var percent by remember { mutableIntStateOf(50) }
            Card {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Stepper(
                        value = percent,
                        onValueChange = { percent = it },
                        maxValue = 100,
                        step = 10,
                        valueFormatter = { "$it%" },
                    )
                }
            }

            Card {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Stepper(value = 3, onValueChange = {}, enabled = false)
                }
            }

            Text("Stepper")
        }
    }
}
