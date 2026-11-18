// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.preference.internal

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * The chevron the stock preference rows draw at their end.
 *
 * `coui_preference_widget_jump.xml` sets `android:src="@drawable/coui_btn_next"`, whose normal state
 * (`coui_btn_next_normal.xml`) is a **12dp × 24dp** vector with a *stroked* path:
 *
 * ```
 * M5,6 L10.646,11.646 C… L5,18        strokeWidth 1.4, round caps
 * ```
 *
 * That is a 1.4dp round-cap line, not a filled glyph: horizontally it spans 5 → 10.646 (plus half the
 * stroke on each side), vertically 6 → 18. The library used a 10dp × 16dp filled `ArrowRight` icon
 * instead, which is both the wrong box and the wrong rendering style, so this composable redraws the
 * official geometry.
 *
 * @param color Chevron colour; the stock vector tints `?attr/couiColorLabelTertiary`.
 * @param layoutDirection Used to mirror the chevron for RTL.
 * @param modifier Modifier applied to the 12dp × 24dp canvas.
 */
@Composable
internal fun PreferenceChevron(
    color: Color,
    layoutDirection: LayoutDirection,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val scaleX = size.width / 12f
        val scaleY = size.height / 24f
        val mirrored = layoutDirection == LayoutDirection.Rtl

        fun x(value: Float) = if (mirrored) size.width - value * scaleX else value * scaleX

        fun y(value: Float) = value * scaleY

        val path =
            Path().apply {
                moveTo(x(5f), y(6f))
                lineTo(x(10.646f), y(11.646f))
                // The stock vector rounds the tip: C10.842,11.842 10.842,12.158 10.646,12.354.
                cubicTo(
                    x(10.842f),
                    y(11.842f),
                    x(10.842f),
                    y(12.158f),
                    x(10.646f),
                    y(12.354f),
                )
                lineTo(x(5f), y(18f))
            }
        drawPath(
            path = path,
            color = color,
            style =
                Stroke(
                    width = 1.4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
        )
    }
}

/** Size of the stock chevron viewport (`coui_btn_next_normal.xml`: 12dp × 24dp). */
internal val PreferenceChevronWidth = 12.dp

/** Size of the stock chevron viewport (`coui_btn_next_normal.xml`: 12dp × 24dp). */
internal val PreferenceChevronHeight = 24.dp
