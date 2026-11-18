// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.RangeSlider
import io.github.ra1nify.rmuix.kmp.basic.Slider
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TextButton

@Composable
fun SliderDemo() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(demoBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var slider1 by remember { mutableFloatStateOf(0.5f) }
            var slider2 by remember { mutableFloatStateOf(0.7f) }
            var slider3 by remember { mutableStateOf(0.2f..0.8f) }
            var nativeProgress by remember { mutableFloatStateOf(0.5f) }
            var thumbPreview by remember { mutableFloatStateOf(0.5f) }
            var textureEnabled by remember { mutableStateOf(true) }
            Text("Plain thumb / shadow remains when spotlight is off")
            Slider(value = thumbPreview, onValueChange = { thumbPreview = it }, showSpotLight = false)
            Text("Explicit thumb material preview / track spotlight pending")
            Slider(
                value = thumbPreview,
                onValueChange = { thumbPreview = it },
                thumbTextureEnabled = textureEnabled,
            )
            TextButton(
                text = if (textureEnabled) "Thumb texture on" else "Thumb texture off",
                onClick = { textureEnabled = !textureEnabled },
            )
            var nativeChanges by remember { mutableIntStateOf(0) }
            var nativeFinishes by remember { mutableIntStateOf(0) }
            Text("Native 0..100 / progress: " + (nativeProgress * 100).toInt() + " / changes: " + nativeChanges)
            Text("Flick to coast / tracking finished: " + nativeFinishes)
            Text("Two fingers: lifting one keeps tracking; last release may coast")
            Slider(
                value = nativeProgress,
                onValueChange = {
                    nativeProgress = it
                    nativeChanges++
                },
                progressRange = 0..100,
                onValueChangeFinished = { nativeFinishes++ },
            )
            var minimum by remember { mutableFloatStateOf(0f) }
            Text("Reversed native progress / drag past an edge, then return")
            Slider(
                value = nativeProgress,
                onValueChange = {
                    nativeProgress = it
                    nativeChanges++
                },
                reverseDirection = true,
                progressRange = 0..100,
                onValueChangeFinished = { nativeFinishes++ },
            )
            var maximum by remember { mutableFloatStateOf(1f) }
            var changes by remember { mutableIntStateOf(0) }
            var finishes by remember { mutableIntStateOf(0) }
            var motionEnabled by remember { mutableStateOf(true) }
            Slider(value = minimum, onValueChange = { minimum = it })
            Slider(value = maximum, onValueChange = { maximum = it })
            Slider(value = slider1, onValueChange = { slider1 = it }, reverseDirection = true)
            Text("Owner: " + (slider1 * 100).toInt() + "% / changes: " + changes + " / finished: " + finishes)
            Slider(
                value = slider1,
                onValueChange = {
                    slider1 = it
                    changes++
                },
                onValueChangeFinished = { finishes++ },
                enabled = motionEnabled,
            )
            Row {
                TextButton(text = "Set 0%", onClick = { slider1 = 0f })
                TextButton(text = "Set 100%", onClick = { slider1 = 1f })
                TextButton(text = if (motionEnabled) "Disable" else "Enable", onClick = { motionEnabled = !motionEnabled })
            }
            Slider(
                value = slider2,
                onValueChange = { slider2 = it },
                enabled = false,
            )
            RangeSlider(
                value = slider3,
                onValueChange = { slider3 = it },
            )
        }
    }
}
