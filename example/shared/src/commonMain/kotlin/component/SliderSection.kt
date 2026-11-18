// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.Slider
import io.github.ra1nify.rmuix.kmp.basic.SliderDefaults
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TextButton
import io.github.ra1nify.rmuix.kmp.basic.VerticalSlider
import io.github.ra1nify.rmuix.kmp.preference.RangeSliderPreference
import io.github.ra1nify.rmuix.kmp.preference.SliderPreference

fun LazyListScope.sliderSection() {
    item(key = "slider") {
        CategoryTitle(text = "Continuous Slider · stock track geometry")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            Column(Modifier.padding(16.dp)) {
                var minimum by remember { mutableFloatStateOf(0f) }
                var maximum by remember { mutableFloatStateOf(1f) }
                var reversed by remember { mutableFloatStateOf(0.5f) }
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
                Text("Native 0..100 / first-drag frame callbacks")
                Text("Progress: " + (nativeProgress * 100).toInt() + " / changes: " + nativeChanges)
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
                Text("Endpoints / hold to expand background only")
                Slider(value = minimum, onValueChange = { minimum = it })
                Slider(value = maximum, onValueChange = { maximum = it })
                Text("Reversed / fixed thumb travel")
                Slider(value = reversed, onValueChange = { reversed = it }, reverseDirection = true)
                var committed by remember { mutableFloatStateOf(0.5f) }
                var changes by remember { mutableIntStateOf(0) }
                var finishes by remember { mutableIntStateOf(0) }
                var motionEnabled by remember { mutableStateOf(true) }
                Text("Click commits at spring end / owner: " + (committed * 100).toInt() + "%")
                Text("Changes: " + changes + " / finished: " + finishes)
                Slider(
                    value = committed,
                    onValueChange = {
                        committed = it
                        changes++
                    },
                    onValueChangeFinished = { finishes++ },
                    enabled = motionEnabled,
                )
                Row {
                    TextButton(text = "Set 0%", onClick = { committed = 0f })
                    TextButton(text = "Set 100%", onClick = { committed = 1f })
                    TextButton(text = if (motionEnabled) "Disable" else "Enable", onClick = { motionEnabled = !motionEnabled })
                }
            }
        }
        CategoryTitle(text = "Slider")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            var sliderValue by remember { mutableFloatStateOf(0.3f) }
            SliderPreference(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                title = "Normal",
                valueText = "${(sliderValue * 100).toInt()}%",
                insideMargin = PaddingValues(32.dp, 10.dp, 32.dp, 0.dp),
            )
            // seek_bar_preference.xml tips: 14sp end labels beside the track.
            SliderPreference(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                title = "With end labels",
                startLabel = "0",
                endLabel = "100",
                insideMargin = PaddingValues(32.dp, 10.dp, 32.dp, 0.dp),
            )
            var stepsValue by remember { mutableFloatStateOf(100f) }
            SliderPreference(
                value = stepsValue,
                onValueChange = { stepsValue = it },
                title = "Steps",
                valueText = "${stepsValue.toInt()}/200",
                valueRange = 0f..200f,
                steps = 199,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                insideMargin = PaddingValues(32.dp, 10.dp, 32.dp, 0.dp),
            )
            var stepsWithKeyPointsValue by remember { mutableFloatStateOf(5f) }
            // COUISectionSeekBar parity: `steps` + `showKeyPoints` draws a mark per section (3dp), hides the
            // ones under the thumb, snaps to the nearest section and uses the section tick haptic.
            SliderPreference(
                value = stepsWithKeyPointsValue,
                onValueChange = { stepsWithKeyPointsValue = it },
                title = "Steps with Key Points",
                valueText = "${stepsWithKeyPointsValue.toInt()}/8",
                valueRange = 0f..8f,
                steps = 7,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                showKeyPoints = true,
                insideMargin = PaddingValues(32.dp, 10.dp, 32.dp, 0.dp),
            )
            var customKeyPointsValue by remember { mutableFloatStateOf(25f) }
            SliderPreference(
                value = customKeyPointsValue,
                onValueChange = { customKeyPointsValue = it },
                title = "Custom Key Points",
                valueText = "${customKeyPointsValue.toInt()}%",
                valueRange = 0f..100f,
                showKeyPoints = true,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                keyPoints = listOf(0f, 25f, 50f, 75f, 100f),
                insideMargin = PaddingValues(32.dp, 10.dp, 32.dp, 0.dp),
            )
            val disabledValue = 0.7f
            SliderPreference(
                value = disabledValue,
                onValueChange = {},
                title = "Disabled",
                valueText = "${(disabledValue * 100).toInt()}%",
                enabled = false,
                insideMargin = PaddingValues(32.dp, 10.dp, 32.dp, 0.dp),
            )
        }

        // RangeSlider
        CategoryTitle(text = "RangeSlider")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            var rangeValue by remember { mutableStateOf(0.2f..0.8f) }
            RangeSliderPreference(
                value = rangeValue,
                onValueChange = { rangeValue = it },
                title = "Range",
                valueText = "${(rangeValue.start * 100).toInt()}% - ${(rangeValue.endInclusive * 100).toInt()}%",
                insideMargin = PaddingValues(32.dp, 10.dp, 32.dp, 0.dp),
            )
            var rangeStepsValue by remember { mutableStateOf(2f..8f) }
            RangeSliderPreference(
                value = rangeStepsValue,
                onValueChange = { rangeStepsValue = it },
                title = "Range with Key Points",
                valueText = "${rangeStepsValue.start.toInt()} - ${rangeStepsValue.endInclusive.toInt()}",
                valueRange = 0f..8f,
                steps = 7,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                showKeyPoints = true,
                insideMargin = PaddingValues(32.dp, 10.dp, 32.dp, 0.dp),
            )
            var customRangeValue by remember { mutableStateOf(20f..80f) }
            RangeSliderPreference(
                value = customRangeValue,
                onValueChange = { customRangeValue = it },
                title = "Custom Range Points",
                valueText = "${customRangeValue.start.toInt()}% - ${customRangeValue.endInclusive.toInt()}%",
                valueRange = 0f..100f,
                showKeyPoints = true,
                hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                keyPoints = listOf(0f, 20f, 40f, 60f, 80f, 100f),
                insideMargin = PaddingValues(32.dp, 10.dp, 32.dp, 0.dp),
            )
            var disabledRangeValue by remember { mutableStateOf(0.3f..0.7f) }
            RangeSliderPreference(
                value = disabledRangeValue,
                onValueChange = {},
                title = "Disabled",
                valueText = "${(disabledRangeValue.start * 100).toInt()}% - ${(disabledRangeValue.endInclusive * 100).toInt()}%",
                enabled = false,
            )
        }

        // VerticalSlider
        CategoryTitle(text = "VerticalSlider")
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
                        .padding(horizontal = 12.dp)
                        .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var verticalValue1 by remember { mutableFloatStateOf(0.3f) }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    VerticalSlider(
                        value = verticalValue1,
                        onValueChange = { verticalValue1 = it },
                        modifier = Modifier.size(25.dp, 160.dp),
                    )
                    Text(
                        text = "Normal\n${(verticalValue1 * 100).toInt()}%",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                var verticalValue2 by remember { mutableFloatStateOf(5f) }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    VerticalSlider(
                        value = verticalValue2,
                        onValueChange = { verticalValue2 = it },
                        valueRange = 0f..6f,
                        steps = 5,
                        hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                        modifier = Modifier.size(25.dp, 160.dp),
                    )
                    Text(
                        text = "Steps\n${verticalValue2.toInt()}/6",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                var verticalValue3 by remember { mutableFloatStateOf(5f) }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    VerticalSlider(
                        value = verticalValue3,
                        onValueChange = { verticalValue3 = it },
                        valueRange = 0f..6f,
                        steps = 5,
                        hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                        showKeyPoints = true,
                        modifier = Modifier.size(25.dp, 160.dp),
                    )
                    Text(
                        text = "Points\n${verticalValue3.toInt()}/6",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                var verticalValue4 by remember { mutableFloatStateOf(50f) }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    VerticalSlider(
                        value = verticalValue4,
                        onValueChange = { verticalValue4 = it },
                        valueRange = 0f..100f,
                        showKeyPoints = true,
                        hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                        keyPoints = listOf(0f, 25f, 50f, 75f, 100f),
                        modifier = Modifier.size(25.dp, 160.dp),
                    )
                    Text(
                        text = "Custom\n${verticalValue4.toInt()}%",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                val disabledVerticalValue = 0.7f
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    VerticalSlider(
                        value = disabledVerticalValue,
                        onValueChange = {},
                        enabled = false,
                        modifier = Modifier.size(25.dp, 160.dp),
                    )
                    Text(
                        text = "Disabled\n${(disabledVerticalValue * 100).toInt()}%",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
