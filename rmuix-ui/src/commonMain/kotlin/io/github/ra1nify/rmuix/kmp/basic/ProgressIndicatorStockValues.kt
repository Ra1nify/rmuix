// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

// Values from the Settings APK progress bars
// (`com.coui.appcompat.progressbar.COUIHorizontalProgressBar`, `COUICircularProgressBar`,
// `coui_progressbar_*_selector` and the `coui_circular_progress_*` dimens).

/** `couiColorDivider`: the track behind a progress bar, `#1F000000` / `#33FFFFFF`. */
internal fun progressIndicatorTrack(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

/**
 * `couiCircularProgressBarColor` = `couiColorHintNeutral`: the circular ring is a neutral hint,
 * `#42000000` / `#4DFFFFFF`, not the accent.
 */
internal fun progressIndicatorRing(dark: Boolean): Color = if (dark) Color(0x4DFFFFFF) else Color(0x42000000)

/** `coui_circular_progress_medium_stroke_width`: the 30dp medium preset uses a 3dp stroke. */
internal val ProgressIndicatorStockCircularStrokeWidth = 3.dp

/** `coui_circular_progress_medium_length`: the medium preset is 30dp across. */
internal val ProgressIndicatorStockCircularMediumSize = 30.dp

/** `coui_circular_progress_large_length`: the large preset is 40dp across. */
internal val ProgressIndicatorStockCircularLargeSize = 40.dp

/** `coui_circular_progress_large_stroke_width`: the 40dp large preset. */
internal val ProgressIndicatorStockCircularLargeStrokeWidth = 5.dp

// --- Pause / error overlay icons (`COUICircularProgressDrawable`) ------------------------------

/** `coui_circular_progress_pause_icon_rect_width`: each pause bar is 4dp wide. */
internal val ProgressIndicatorStockPauseRectWidth = 4.dp

/** `coui_circular_progress_pause_icon_rect_height`: 16dp tall. */
internal val ProgressIndicatorStockPauseRectHeight = 16.dp

/** `coui_circular_progress_pause_icon_rect_radius`: 1dp rounded ends. */
internal val ProgressIndicatorStockPauseRectRadius = 1.dp

/** `coui_circular_progress_pause_icon_rect_gap`: 4dp between the two bars. */
internal val ProgressIndicatorStockPauseGap = 4.dp

/** `coui_circular_progress_error_diameter`: the error ring is 19dp across. */
internal val ProgressIndicatorStockErrorDiameter = 19.dp

/** `coui_circular_progress_error_stroke_width`: 1.5dp ring. */
internal val ProgressIndicatorStockErrorStrokeWidth = 1.5.dp

/** `coui_circular_progress_error_icon_rect_width` / `_rect_height`: the exclamation stem. */
internal val ProgressIndicatorStockErrorStemWidth = 1.6.dp

/** `coui_circular_progress_error_icon_rect_height`: 5.25dp. */
internal val ProgressIndicatorStockErrorStemHeight = 5.25.dp

/** `coui_circular_progress_error_icon_rect_bias`: the stem starts 4.25dp above the centre. */
internal val ProgressIndicatorStockErrorStemBias = 4.25.dp

/** `coui_circular_progress_error_icon_circle_radius`: the dot is r=1.1dp. */
internal val ProgressIndicatorStockErrorDotRadius = 1.1.dp

/** `coui_circular_progress_error_icon_circle_bias`: the dot sits 3.1dp below the centre. */
internal val ProgressIndicatorStockErrorDotBias = 3.1.dp

/** `coui_circular_progress_shadow_radius` / `_x_bias` / `_y_bias`: 4dp / 0dp / 2dp. */
internal val ProgressIndicatorStockShadowRadius = 4.dp

/** `coui_circular_progress_shadow_color`: `#19000000` (10% black) - the stock `Paint.setShadowLayer` colour. */
internal val ProgressIndicatorStockShadowColor = 0x19000000

/** `coui_circular_progress_shadow_y_bias`: 2dp. */
internal val ProgressIndicatorStockShadowYBias = 2.dp

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isProgressIndicatorInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
