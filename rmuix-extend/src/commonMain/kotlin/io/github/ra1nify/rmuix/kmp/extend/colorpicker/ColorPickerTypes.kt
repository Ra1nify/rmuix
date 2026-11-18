// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.extend.colorpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * Colours of a [ColorPicker].
 *
 * @param thumbColor The ring drawn around the drag handles.
 * @param trackColor The hue / alpha track outline.
 * @param hexTextColor The hex readout colour.
 */
@Immutable
data class ColorPickerColors(
    val thumbColor: Color,
    val trackColor: Color,
    val hexTextColor: Color,
)

/**
 * Defaults for [ColorPicker].
 *
 * ColorOS has no `com.coui.appcompat` colour picker - none of the three decompiled trees contains a
 * colour-picker class - so these values are **not** stock metrics. They are built from the ColorOS
 * design tokens the rest of the library uses and are free to change.
 */
object ColorPickerDefaults {
    /** The saturation/value area's width-to-height ratio. */
    const val AREA_ASPECT_RATIO = 1.6f

    /** Corner radius of the saturation/value area (`?attr/couiRoundCornerM` is 12dp). */
    val AreaCornerRadius = 12.dp

    /** Height of the hue and alpha tracks. */
    val TrackHeight = 12.dp

    /** Diameter of the drag handles. */
    val ThumbSize = 20.dp

    /** Diameter of a preset swatch. */
    val PresetSize = 28.dp

    /** Vertical gap between the picker's rows. */
    val RowSpacing = 12.dp

    /** Hex readout size: `couiTextAppearanceArticleBody`. */
    val HexTextSize = 14.sp

    /** A small neutral-plus-colour shortcut row. */
    @Composable
    fun presetColors(): List<Color> =
        remember {
            listOf(
                Color(0xFFD32F2F),
                Color(0xFFF57C00),
                Color(0xFFFBC02D),
                Color(0xFF388E3C),
                Color(0xFF0080FF),
                Color(0xFF7B1FA2),
                Color(0xFF000000),
                Color(0xFFFFFFFF),
            )
        }

    @Composable
    fun colorPickerColors(
        thumbColor: Color = Color.White,
        trackColor: Color = RmuixTheme.colorScheme.dividerLine,
        hexTextColor: Color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
    ): ColorPickerColors =
        remember(thumbColor, trackColor, hexTextColor) {
            ColorPickerColors(thumbColor = thumbColor, trackColor = trackColor, hexTextColor = hexTextColor)
        }
}
