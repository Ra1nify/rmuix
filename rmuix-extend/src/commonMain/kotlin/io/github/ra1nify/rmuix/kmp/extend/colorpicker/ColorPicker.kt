// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.extend.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Text

/**
 * A colour picker with a saturation/value area, optional hue and alpha tracks and an optional hex
 * readout.
 *
 * ColorOS ships no colour picker - `com.coui.appcompat` has no such class in any of the three
 * decompiled trees - so this component has **no stock metrics**; it is built from the library's ColorOS
 * tokens and lives in `rmuix-extend`.
 *
 * @param color The current colour.
 * @param onColorChange Called with the new colour while the user drags or taps a preset.
 * @param modifier The modifier to be applied to the picker.
 * @param showAlphaSlider Whether to show the alpha track.
 * @param showHexText Whether to show the `#RRGGBB` / `#AARRGGBB` readout.
 * @param presetColors Shortcut swatches; pass an empty list to hide the row.
 * @param colors The picker's colours.
 */
@Composable
fun ColorPicker(
    color: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    showAlphaSlider: Boolean = true,
    showHexText: Boolean = true,
    presetColors: List<Color> = ColorPickerDefaults.presetColors(),
    colors: ColorPickerColors = ColorPickerDefaults.colorPickerColors(),
) {
    val initial = remember { colorToHsv(color) }
    var hue by remember { mutableFloatStateOf(initial.hue) }
    var saturation by remember { mutableFloatStateOf(initial.saturation) }
    var value by remember { mutableFloatStateOf(initial.value) }

    // Follow an external colour change, but keep the hue when the colour is fully desaturated (hue is
    // meaningless at saturation 0 and would otherwise snap back to red).
    LaunchedEffect(color) {
        val hsv = colorToHsv(color)
        if (hsv.saturation > 0f) hue = hsv.hue
        saturation = hsv.saturation
        value = hsv.value
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ColorPickerDefaults.RowSpacing),
    ) {
        SaturationValueArea(
            hue = hue,
            saturation = saturation,
            value = value,
            thumbColor = colors.thumbColor,
            onPick = { newSaturation, newValue ->
                saturation = newSaturation
                value = newValue
                onColorChange(hsvToColor(hue, newSaturation, newValue, color.alpha))
            },
        )
        HueTrack(
            hue = hue,
            thumbColor = colors.thumbColor,
            onHue = { newHue ->
                hue = newHue
                onColorChange(hsvToColor(newHue, saturation, value, color.alpha))
            },
        )
        if (showAlphaSlider) {
            AlphaTrack(
                color = hsvToColor(hue, saturation, value, 1f),
                alpha = color.alpha,
                thumbColor = colors.thumbColor,
                trackColor = colors.trackColor,
                onAlpha = { newAlpha -> onColorChange(hsvToColor(hue, saturation, value, newAlpha)) },
            )
        }
        if (showHexText) {
            Text(
                text = color.toHexString(includeAlpha = showAlphaSlider && color.alpha < 1f),
                fontSize = ColorPickerDefaults.HexTextSize,
                color = colors.hexTextColor,
            )
        }
        if (presetColors.isNotEmpty()) {
            PresetRow(colors = presetColors, onPick = onColorChange)
        }
    }
}

/**
 * The saturation (x) / value (y) square for the given [hue], with a ring marking the current
 * [saturation] and [value].
 */
@Composable
private fun SaturationValueArea(
    hue: Float,
    saturation: Float,
    value: Float,
    thumbColor: Color,
    onPick: (saturation: Float, value: Float) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .aspectRatio(ColorPickerDefaults.AREA_ASPECT_RATIO)
                .clip(RoundedCornerShape(ColorPickerDefaults.AreaCornerRadius)),
    ) {
        Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) { trackPointer(onPick) }) {
            drawRect(color = hsvToColor(hue, 1f, 1f, 1f))
            drawRect(brush = Brush.horizontalGradient(listOf(Color.White, Color.White.copy(alpha = 0f))))
            drawRect(brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
            drawCircle(
                color = thumbColor,
                radius = ColorPickerDefaults.ThumbSize.toPx() / 2f,
                center = Offset(saturation * size.width, (1f - value) * size.height),
                style = Stroke(width = 2.dp.toPx()),
            )
        }
    }
}

/** A hue rail: the 7-stop gradient from red through the spectrum and back. */
@Composable
private fun HueTrack(
    hue: Float,
    thumbColor: Color,
    onHue: (Float) -> Unit,
) {
    Canvas(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(ColorPickerDefaults.TrackHeight)
                .clip(CircleShape)
                .pointerInput(Unit) { trackPointer { _, x -> onHue((x * 360f).coerceIn(0f, 360f)) } },
    ) {
        drawRect(
            brush =
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFF0000),
                        Color(0xFFFFFF00),
                        Color(0xFF00FF00),
                        Color(0xFF00FFFF),
                        Color(0xFF0000FF),
                        Color(0xFFFF00FF),
                        Color(0xFFFF0000),
                    ),
                ),
        )
        val x = (hue / 360f) * size.width
        drawCircle(
            color = thumbColor,
            radius = ColorPickerDefaults.ThumbSize.toPx() / 2f,
            center = Offset(x, size.height / 2f),
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}

/** A transparency rail: transparent to opaque of [color], with a hairline [trackColor] outline. */
@Composable
private fun AlphaTrack(
    color: Color,
    alpha: Float,
    thumbColor: Color,
    trackColor: Color,
    onAlpha: (Float) -> Unit,
) {
    Canvas(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(ColorPickerDefaults.TrackHeight)
                .clip(CircleShape)
                .background(trackColor)
                .pointerInput(Unit) { trackPointer { _, x -> onAlpha(x.coerceIn(0f, 1f)) } },
    ) {
        drawRect(brush = Brush.horizontalGradient(listOf(color.copy(alpha = 0f), color)))
        val x = alpha * size.width
        drawCircle(
            color = thumbColor,
            radius = ColorPickerDefaults.ThumbSize.toPx() / 2f,
            center = Offset(x, size.height / 2f),
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}

/** A row of shortcut swatches. */
@Composable
private fun PresetRow(
    colors: List<Color>,
    onPick: (Color) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ColorPickerDefaults.RowSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        colors.forEach { preset ->
            Box(
                modifier =
                    Modifier
                        .size(ColorPickerDefaults.PresetSize)
                        .clip(CircleShape)
                        .background(preset)
                        .border(width = 1.dp, color = Color.Black.copy(alpha = 0.12f), shape = CircleShape)
                        .pointerInput(preset) { detectTapGestures { onPick(preset) } },
            )
        }
    }
}

/**
 * Follows a press-drag anywhere in the current node, reporting the position as `(fractionX,
 * fractionY)`, and keeps following while the pointer moves.
 */
private suspend fun androidx.compose.ui.input.pointer.PointerInputScope.trackPointer(
    onPosition: (fractionX: Float, fractionY: Float) -> Unit,
) {
    val report: (Offset) -> Unit = { offset ->
        val width = size.width.toFloat().coerceAtLeast(1f)
        val height = size.height.toFloat().coerceAtLeast(1f)
        onPosition((offset.x / width).coerceIn(0f, 1f), (offset.y / height).coerceIn(0f, 1f))
    }
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        report(down.position)
        down.consume()
        drag(down.id) { change ->
            report(change.position)
            change.consume()
        }
    }
}
