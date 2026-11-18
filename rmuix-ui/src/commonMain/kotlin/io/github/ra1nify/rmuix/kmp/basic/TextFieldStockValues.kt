// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

// Values extracted from the Settings APK `Widget.COUI.EditText` family
// (`com.coui.appcompat.edittext.COUIEditText`, rectangle background mode).
// `Widget.COUI.EditText` sets `couiStrokeWidth` (0.33dp) plus `couiFocusStrokeWidth` (1dp) and
// `couiStrokeColor` (?attr/couiColorPrimary); `COUIEditText` reads all three, so the rectangle box
// both recolours and thickens on focus. The line mode (bottom-only stroke) is not ported.

/** `coui_textinput_corner_radius`. */
internal val TextFieldStockCornerRadius = 10.dp

/** `android:minHeight` = `coui_edit_text_height_min`. */
internal val TextFieldStockMinHeight = 62.dp

/**
 * Error-state colours.
 *
 * `COUIEditText` resolves `couiEditTextErrorColor` with `?attr/couiColorErrorTextBg` as the fallback; that
 * attr maps to:
 *
 * - `@color/coui_color_error_text_bg` = `#4DE82A18`
 * - `@color/coui_color_error_text_bg_dark` = `#EA3939`
 *
 * `COUIErrorEditTextHelper` ramps the label / hint / stroke from their normal colours to this one over
 * **217ms** (`COUIEaseInterpolator`), and shakes the text by `coui_edit_text_shake_amplitude` (**5.33dp**)
 * over **450ms** through a 5-keyframe interpolator whose segment durations are 83/133/117/117ms.
 */
internal fun textFieldErrorColor(dark: Boolean): Color = if (dark) Color(0xFFEA3939) else Color(0x4DE82A18)

/** `coui_edit_text_shake_amplitude`: 5.33dp horizontal shake while the error state is active. */
internal val TextFieldStockShakeAmplitude = 5.33.dp

/** `COUIErrorEditTextHelper.initAnimator()`: the colour ramp to the error colour takes 217ms. */
internal val TextFieldStockErrorRampMillis = 217

/** `ShakeInterpolator.TOTAL_DURATION`: the horizontal shake lasts 450ms. */
internal val TextFieldStockShakeMillis = 450

/** `coui_textinput_stroke_width`: the resting border hairline. */
internal val TextFieldStockStrokeWidth = 0.33.dp

/** `couiFocusStrokeWidth` (`Widget.COUI.EditText`) → `coui_textinput_focus_stroke_width`: 1dp. */
internal val TextFieldStockFocusStrokeWidth = 1.dp

/** `couiEditTextTextAppearance` = `couiTextAppearanceHeadline6` with a regular family. */
internal val TextFieldStockTextSize = 16.sp

/** `coui_textinput_label_cutout_padding`: the floating label erases the border 4dp to each side. */
internal val TextFieldStockLabelCutoutPadding = 4.dp

/** `coui_textinput_hint_text_size` (collapsed floating label). */
internal const val TEXT_FIELD_LABEL_FLOATING_SIZE = 10f

/** Expanded hint keeps the edit text size; the rectangle style only overrides the collapsed size. */
internal const val TEXT_FIELD_LABEL_SIZE = 16f

/** `couiColorDivider` (`values` / `values-night`). */
internal fun textFieldStroke(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

/** `coui_textinput_stroke_color_disabled` / `_dark`. */
internal fun textFieldDisabledStroke(dark: Boolean): Color = if (dark) Color(0x19FFFFFF) else Color(0x19000000)

/** `couiColorLabelSecondary`, equal to `couiColorSecondNeutral` in both themes. */
internal fun textFieldLabel(dark: Boolean): Color = if (dark) Color(0x8AFFFFFF) else Color(0x8A000000)

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isTextFieldInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/** `COUIEditText.animateToShowBackground()`: the focused bottom line grows over 250ms. */
internal val TextFieldStockLineGrowMillis = 250

/**
 * `coui_textinput_line_padding_top` = 10dp.
 *
 * Mode 1 (`couiBackgroundMode = 1`) uses this twice: `getBoundsTop()` returns it as the box background
 * top, and `getModePaddingTop()` starts from it.
 */
internal val TextFieldStockLinePaddingTop = 10.dp

/**
 * `coui_textinput_line_padding_middle` = 5dp.
 *
 * `getModePaddingTop()` for mode 1 is
 * `line_padding_top + hintHeight + line_padding_middle`, i.e. the field text starts 15dp plus the hint
 * height below the top edge.
 */
internal val TextFieldStockLinePaddingMiddle = 5.dp

/**
 * `coui_textinput_line_padding` = 11dp - **declared but never read**.
 *
 * `COUIEditText` assigns it to `mLinePadding` at line 540 and never uses that field again; no layout or
 * style references the dimen either. Kept here so the three line tokens are documented together, but
 * nothing should be derived from it.
 */
internal val TextFieldStockLinePaddingDead = 11.dp
