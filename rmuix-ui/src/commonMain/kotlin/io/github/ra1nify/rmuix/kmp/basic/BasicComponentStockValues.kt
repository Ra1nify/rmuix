// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

// Neutral text roles for the ColorOS preference row (`coui_preference.xml`), resolved from the
// Settings APK selectors in `res/color/`.

/** `coui_preference_title_color` → `couiColorLabelPrimary`: `#E6000000` / `#E6FFFFFF`. */
internal fun preferenceTitle(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/** `coui_preference_secondary_text_color` → `couiColorSecondNeutral`: `#8A000000` / `#8AFFFFFF`. */
internal fun preferenceSummary(dark: Boolean): Color = if (dark) Color(0x8AFFFFFF) else Color(0x8A000000)

/** Disabled state of every preference text selector → `couiColorLabelTertiary`: `#42000000` / `#4DFFFFFF`. */
internal fun preferenceTertiary(dark: Boolean): Color = if (dark) Color(0x4DFFFFFF) else Color(0x42000000)

/**
 * `coui_preference_assignment_text_color` → `?attr/couiColorPrimaryText`; under `Theme.COUI.Main`
 * (parent `Theme.COUI.Blue`) that resolves to `@color/coui_color_primary_text_blue` = `#0080FF` light /
 * `@color/coui_color_primary_text_blue_dark` = `#5C9DFF` dark - an app accent, not the library theme's
 * `colorScheme.primary`.
 */
internal fun preferenceAssignment(dark: Boolean): Color = if (dark) Color(0xFF5C9DFF) else Color(0xFF0080FF)

/**
 * `coui_preference_bg_selector` pressed colour → `couiColorPressBackground`:
 *
 * - `@color/coui_color_press_background` = `#14000000`
 * - `@color/coui_color_press_background_dark` = `#26FFFFFF`
 *
 * The selector also carries a disabled branch that paints nothing; it is not a colour this function returns.
 */
internal fun preferencePress(dark: Boolean): Color = if (dark) Color(0x26FFFFFF) else Color(0x14000000)

/** `android:enterFadeDuration` of `coui_preference_bg_selector`. */
internal const val PREFERENCE_PRESS_FADE_IN_MILLIS = 100

/** `android:exitFadeDuration` of `coui_preference_bg_selector`. */
internal const val PREFERENCE_PRESS_FADE_OUT_MILLIS = 360

/** `?attr/couiColorCardBackground` -> `@color/coui_color_card_background`: #FFFFFF / #1AFFFFFF. */
internal fun preferenceCardContainer(dark: Boolean): Color = if (dark) Color(0x1AFFFFFF) else Color(0xFFFFFFFF)

/**
 * `?attr/couiColorBackgroundWithCard`: the ColorOS page / toolbar surface.
 *
 * `Theme.COUI` maps the attr to `@color/coui_color_background_with_card` = **#F0F1F2** in the light
 * theme and to `@color/coui_color_background_with_card_dark` = **#000000** in the night theme; the
 * stock window background (`coui_window_background_with_card_selector`) and `coui_toolbar_bg` both
 * paint this value, which is why the top bar and the page share it.
 */
internal fun backgroundWithCard(dark: Boolean): Color = if (dark) Color(0xFF000000) else Color(0xFFF0F1F2)

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isBasicComponentInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
