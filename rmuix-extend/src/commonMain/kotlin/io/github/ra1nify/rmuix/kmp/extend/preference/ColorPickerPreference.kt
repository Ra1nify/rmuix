// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.extend.preference

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.BasicComponent
import io.github.ra1nify.rmuix.kmp.basic.BasicComponentColors
import io.github.ra1nify.rmuix.kmp.basic.BasicComponentDefaults
import io.github.ra1nify.rmuix.kmp.extend.colorpicker.ColorPickerDefaults
import io.github.ra1nify.rmuix.kmp.extend.colorpicker.OverlayColorPickerDialog

/**
 * A preference row that shows a colour swatch and opens [OverlayColorPickerDialog] when tapped.
 *
 * The row only commits on confirm: while the dialog is open it keeps a draft colour, and
 * [onColorChange] is called with the draft once the user presses confirm. That matches the behaviour of
 * the realOS `RouiColorPickerPreference` it replaces.
 *
 * @param color The current colour.
 * @param onColorChange Called with the chosen colour after the dialog is confirmed.
 * @param title The row title.
 * @param modifier The modifier to be applied to the row.
 * @param summary The optional row summary.
 * @param enabled Whether the row is interactive.
 * @param showAlphaSlider Whether the dialog shows the alpha track.
 * @param dialogTitle The dialog title, `title` by default.
 * @param previewShape The shape of the swatch.
 * @param previewSize The size of the swatch.
 * @param confirmLabel Label of the dialog's confirm button.
 * @param cancelLabel Label of the dialog's cancel button.
 */
@Composable
fun ColorPickerPreference(
    color: Color,
    onColorChange: (Color) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
    showAlphaSlider: Boolean = true,
    dialogTitle: String = title,
    previewShape: Shape = CircleShape,
    previewSize: Dp = ColorPickerDefaults.PresetSize,
    confirmLabel: String = "OK",
    cancelLabel: String = "Cancel",
) {
    var showDialog by remember { mutableStateOf(false) }
    val summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor()

    BasicComponent(
        modifier = modifier,
        title = title,
        summary = summary,
        enabled = enabled,
        onClick =
            if (enabled) {
                { showDialog = true }
            } else {
                null
            },
        endActions = {
            Box(
                modifier =
                    Modifier
                        .padding(start = 8.dp)
                        .size(previewSize)
                        .clip(previewShape)
                        .background(color)
                        .border(width = 1.dp, color = summaryColor.color(enabled).copy(alpha = 0.2f), shape = previewShape),
                contentAlignment = Alignment.Center,
            ) {}
        },
    )

    if (enabled) {
        OverlayColorPickerDialog(
            show = showDialog,
            color = color,
            onColorChange = {},
            onConfirm = { chosen ->
                showDialog = false
                onColorChange(chosen)
            },
            onDismissRequest = { showDialog = false },
            title = dialogTitle,
            showAlphaSlider = showAlphaSlider,
            confirmLabel = confirmLabel,
            cancelLabel = cancelLabel,
        )
    }
}
