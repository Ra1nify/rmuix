// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.utils.hairlineDp

/**
 * A divider is a thin line that groups content in lists and layouts.
 *
 * @param modifier the [Modifier] to be applied to this divider line.
 * @param thickness thickness of this divider line. Using [Dp.Hairline] will produce a single pixel
 *   divider regardless of screen density.
 * @param color color of this divider line.
 * @param startIndent inset from the inline start, used by ColorOS card lists to line the hairline up
 *   with the row title instead of the card edge.
 * @param endIndent inset from the inline end.
 * @param length Optional fixed line length. `null` fills the parent width; ColorOS uses a 24dp line for
 *   the short instruction rule (`coui_component_card_instruction_divider_width`,
 *   `coui_component_card_instruction_divider.xml`), which a full-width divider cannot express.
 */
@Composable
@NonRestartableComposable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.DividerColor,
    startIndent: Dp = DividerDefaults.StartIndent,
    endIndent: Dp = DividerDefaults.EndIndent,
    length: Dp? = null,
) {
    val line = with(LocalDensity.current) { hairlineDp(thickness) }
    Canvas(
        modifier
            .then(if (length != null) Modifier.width(length) else Modifier.fillMaxWidth())
            .height(line),
    ) {
        val stroke = size.height
        val start = startIndent.toPx()
        val end = size.width - endIndent.toPx()
        if (end <= start) return@Canvas
        drawLine(
            color = color,
            strokeWidth = stroke,
            start = Offset(start, stroke / 2f),
            end = Offset(end, stroke / 2f),
        )
    }
}

/**
 * A divider is a thin line that groups content in lists and layouts.
 *
 * @param modifier the [Modifier] to be applied to this divider line.
 * @param thickness thickness of this divider line. Using [Dp.Hairline] will produce a single pixel
 *   divider regardless of screen density.
 * @param color color of this divider line.
 * @param startIndent inset from the top.
 * @param endIndent inset from the bottom.
 * @param length Optional fixed line length. `null` fills the parent height.
 */
@Composable
@NonRestartableComposable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.DividerColor,
    startIndent: Dp = DividerDefaults.StartIndent,
    endIndent: Dp = DividerDefaults.EndIndent,
    length: Dp? = null,
) {
    val line = with(LocalDensity.current) { hairlineDp(thickness) }
    Canvas(
        modifier
            .then(if (length != null) Modifier.height(length) else Modifier.fillMaxHeight())
            .width(line),
    ) {
        val stroke = size.width
        val start = startIndent.toPx()
        val end = size.height - endIndent.toPx()
        if (end <= start) return@Canvas
        drawLine(
            color = color,
            strokeWidth = stroke,
            start = Offset(stroke / 2f, start),
            end = Offset(stroke / 2f, end),
        )
    }
}

object DividerDefaults {
    /**
     * Default thickness of the divider line: ColorOS `content_divider_width` = 0.33dp, a hairline
     * rather than a full logical pixel.
     */
    val Thickness = DividerStockThickness

    /**
     * Default color of the divider line: `couiColorDivider` (`#1F000000` / `#33FFFFFF`). This is
     * stronger than the theme's `dividerLine` role (`#0D000000` / `#1AFFFFFF`).
     */
    val DividerColor @Composable get() = dividerStockColor(isDividerInDarkTheme())

    /**
     * No indent: a standalone divider spans the full width.
     *
     * The ColorOS card list indents its row hairlines instead of changing the divider itself - pass
     * [BasicComponentGroupDefaults.DividerStart] / [BasicComponentGroupDefaults.DividerEnd] (both 32dp)
     * to line a divider up with a row's title.
     */
    val StartIndent = 0.dp

    /** No indent, see [StartIndent]. */
    val EndIndent = 0.dp
}
