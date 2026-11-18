// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

// Values from `com.coui.appcompat.stepper.COUIStepperView`, `coui_stepper_view.xml`, the
// `ic_minus_sign` / `ic_plus_sign` vectors and `stepper_button_size`.

/** `stepper_button_size`: both +/- targets are 32dp squares. */
internal val StepperStockButtonSize = 32.dp

/** `coui_stepper_view.xml` `android:minWidth="44dp"` on the indicator. */
internal val StepperStockValueMinWidth = 44.dp

/** `coui_stepper_view.xml`: `layout_marginStart` = `layout_marginEnd` = 12dp on the indicator. */
internal val StepperStockValueMargin = 12.dp

/** `?attr/couiTextAppearanceHeadline5`: 18sp, sans-serif-medium. */
internal val StepperStockValueTextSize = 18.sp

/**
 * `ic_minus_sign`: a 1.6dp round-cap line from x = 9.199 to x = 22.799 in a 32dp viewport, i.e.
 * 13.6dp long — shorter than the plus arms.
 */
internal val StepperStockMinusLength = 13.6.dp

/**
 * `ic_plus_sign`: the horizontal arm runs from x = 8.398 to x = 23.598 (15.2dp) and the vertical arm
 * from y = 8.4 to y = 23.6 (15.2dp), both 1.6dp thick with square ends.
 */
internal val StepperStockPlusLength = 15.2.dp

/** `strokeWidth` of both sign vectors. */
internal val StepperStockGlyphStroke = 1.6.dp

/**
 * `COUIStrokeDrawable` sets `paint.strokeWidth = default_focus_stroke_radius * 2` (3dp × 2) and the
 * ring is clipped to the outside, so the visible focus ring is 3dp wide.
 */
internal val StepperStockFocusRingWidth = 3.dp

/** The centred stroke the visible 3dp ring is cut from (`default_focus_stroke_radius * 2`). */
internal val StepperStockFocusRingStroke = 6.dp

/**
 * `?attr/couiColorFocusOutline` → `couiBlueColorFocusOutlineNormal` → `coui_color_focus_outline_blue`:
 * `#660080FF` light / `#665C9DFF` dark, the same pair `Button` uses.
 */
internal fun stepperFocusOutlineColor(dark: Boolean): Color = if (dark) Color(0x665C9DFF) else Color(0x660080FF)

/**
 * `?attr/couiColorPress`: the `COUIMaskEffectDrawable` press mask over the circular button,
 * `#1F000000` / `#33FFFFFF` (`COUIStepperView.configEffectDrawableForButton`).
 */
internal fun stepperPressColor(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

/** `?attr/couiColorContainer8`: the circular button fill, `#14000000` / `#1AFFFFFF`. */
internal fun stepperButtonBackground(dark: Boolean): Color = if (dark) Color(0x1AFFFFFF) else Color(0x14000000)

/** `?attr/couiColorPrimaryNeutral` (`coui_color_primary_neutral`): the enabled sign/indicator colour. */
internal fun stepperIconColor(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/**
 * `?attr/couiColorDivider`: the disabled drawables (`ic_minus_sign_disable` / `ic_plus_sign_disable`)
 * and the disabled indicator colour (`coui_stepper_text_color_select`), `#1F000000` / `#33FFFFFF`.
 */
internal fun stepperDisabledColor(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isStepperInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
