// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.utils

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

/**
 * Pixel width of a sub-pixel hairline.
 *
 * The stock hairlines are 0.33dp (`content_divider_width`, `coui_number_picker_background_divider_height`,
 * `toolbar_divider_height`). Android densities are >= 1.5 in practice so they land on a whole pixel, but a
 * 1x desktop or web density rounds them to zero and the line disappears; [COUINumberPicker] itself clamps
 * with `Math.max(dimen, 1)`. Keep at least one pixel so the line survives every density.
 */
internal fun Density.hairlinePx(thickness: Dp): Float = thickness.toPx().coerceAtLeast(1f)

/** [hairlinePx] as a [Dp] value, for sizing the line's layout box. */
internal fun Density.hairlineDp(thickness: Dp): Dp = hairlinePx(thickness).toDp()
