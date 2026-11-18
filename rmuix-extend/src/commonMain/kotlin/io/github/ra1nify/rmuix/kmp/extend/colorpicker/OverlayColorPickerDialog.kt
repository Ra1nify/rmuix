// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.extend.colorpicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Button
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TextButton
import io.github.ra1nify.rmuix.kmp.overlay.OverlayDialog

/**
 * A colour picker hosted in an [OverlayDialog].
 *
 * The dialog keeps a draft colour: [onColorChange] fires live while the user drags, while [onConfirm]
 * only fires when the confirm button is pressed - the behaviour the realOS `RouiColorPickerPreference`
 * relies on (a cancelled dialog must not commit).
 *
 * @param show Whether the dialog is shown.
 * @param color The colour the dialog opens with.
 * @param onColorChange Called with the live draft colour.
 * @param onConfirm Called with the draft colour when the confirm button is pressed.
 * @param onDismissRequest Called when the dialog is dismissed (scrim tap, back, or cancel).
 * @param modifier The modifier to be applied to the dialog.
 * @param title The dialog title.
 * @param showAlphaSlider Whether to show the alpha track.
 * @param confirmLabel Label of the confirm button.
 * @param cancelLabel Label of the cancel button.
 * @param colors The picker's colours.
 */
@Composable
fun OverlayColorPickerDialog(
    show: Boolean,
    color: Color,
    onColorChange: (Color) -> Unit,
    onConfirm: (Color) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    showAlphaSlider: Boolean = true,
    confirmLabel: String = "OK",
    cancelLabel: String = "Cancel",
    colors: ColorPickerColors = ColorPickerDefaults.colorPickerColors(),
) {
    var draft by remember { mutableStateOf(color) }
    LaunchedEffect(show) {
        if (show) draft = color
    }

    OverlayDialog(
        show = show,
        modifier = modifier,
        title = title,
        onDismissRequest = onDismissRequest,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ColorPicker(
                color = draft,
                onColorChange = {
                    draft = it
                    onColorChange(it)
                },
                showAlphaSlider = showAlphaSlider,
                colors = colors,
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = androidx.compose.ui.Alignment.End),
            ) {
                TextButton(text = cancelLabel, onClick = onDismissRequest)
                Button(onClick = { onConfirm(draft) }) {
                    Text(text = confirmLabel)
                }
            }
        }
    }
}
