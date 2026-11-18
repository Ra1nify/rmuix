// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

// Values from the Settings APK `coui_center_alert_dialog_layout.xml` family, the
// `COUIDialogTextAppearance.*` styles and the theme corner-radius attributes, as applied by
// `COUIAlertDialogBuilder` / `COUIAlertDialogMaxLinearLayout`.

/** `coui_dialog_max_width`. */
internal val DialogStockMaxWidth = 392.dp

/**
 * The dialog corner: `COUIAlertDialogMaxLinearLayout.setOutLineProvider()` always uses the XXXXL
 * tier and picks one of four radii, so the value is device-dependent:
 *
 * | branch | radius | KDoc constant |
 * | --- | ---: | --- |
 * | `!mIsSupportSmoothRoundCorner` | `?attr/couiRoundCornerXXXXL` = 32dp | [DialogStockCornerRadiusLegacy] |
 * | `useOs15RoundCorner()` | `?attr/couiRoundCornerXXXXLRadius` = 25dp | this constant |
 * | `getSmoothStyleType() != 1 \\|\\| OS <= 37` | `?attr/couiRoundCornerXXXXL` = 32dp | [DialogStockCornerRadiusLegacy] |
 * | otherwise | `radius_large_16_1` = 45dp | [DialogStockCornerRadiusSmooth] |
 *
 * The predicates resolve to plain comparisons (`COUIVersionUtil`):
 *
 * - `checkOPlusViewSubSDK(a, b)` = `osVersion > a || (osVersion == a && sdkSubVersion >= b)`;
 * - `checkOPlusViewBackgroundRenderEffectSupport()` = `checkOPlusViewSubSDK(33, 34)`;
 * - `getOSVersionCode()` reads `OplusBuild.VERSION.SDK_VERSION`, falling back to 0.
 *
 * Compose cannot read those system flags, so the caller picks the branch.
 *
 * `useOs15RoundCorner()` itself is:
 *
 * ```java
 * if (RoundCornerUtil.getSmoothStyleType() != 0) {
 *     return mBlurBackgroundWindow && COUIVersionUtil.getOSVersionCode() == 37;
 * }
 * return true;
 * ```
 *
 * `RoundCornerUtil.getSmoothStyleType()` returns **1** on ColorOS 17 (`checkOPlusViewSubSDK(37, 1) &&
 * isSmoothRoundRectOn()`), **0** on the older OS-16 smooth generation and **-1** when unsupported.
 * On a ColorOS 17 device `useOs15RoundCorner()` therefore collapses to `mBlurBackgroundWindow &&
 * getOSVersionCode() == 37`:
 *
 * - a normal (non-blur) dialog falls through to `getSmoothStyleType() != 1 || getOSVersionCode() <= 37`,
 *   which is true on OS code 37, and takes **32dp** (`DialogStockCornerRadiusLegacy`) - the ColorOS 17
 *   default, and what `DialogDefaults.CornerRadius` uses;
 * - a blur-backed window on the same OS takes the 25dp branch;
 * - 45dp needs `getSmoothStyleType() == 1`, no blur **and** `getOSVersionCode() > 37`;
 * - the `getSmoothStyleType() == 0` (ColorOS 15/16 smooth) generation takes 25dp, which this port does
 *   not use as its default.
 */
internal val DialogStockCornerRadius = 25.dp

/** `?attr/couiRoundCornerXXXXL` = `coui_round_corner_xxxxl` = 32dp (the non-smooth branch). */
internal val DialogStockCornerRadiusLegacy = 32.dp

/** `coui_round_corner_xxxxl_radius_large_16_1` = 45dp (the 16.1 smooth branch). */
internal val DialogStockCornerRadiusSmooth = 45.dp

/** `coui_dialog_layout_margin_horizontal` / `coui_dialog_layout_margin_vertical`. */
internal val DialogStockOutsideMargin = DpSize(16.dp, 32.dp)

/** `COUIAlertDialogTitleStyle` → `couiColorLabelPrimary`: `#E6000000` / `#E6FFFFFF`. */
internal fun dialogTitle(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/**
 * `?attr/couiColorSurface` (`coui_color_surface`): the solid fill of
 * `coui_alert_dialog_builder_background` when the blurred backdrop is not in use - `#FFFFFF` light /
 * `#1E1E1E` dark, which is the dialog's own surface role rather than the theme background.
 */
internal fun dialogSurface(dark: Boolean): Color = if (dark) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)

/** `COUIAlertDialogMessageStyle` → `couiColorLabelSecondary`: `#8A000000` / `#8AFFFFFF`. */
internal fun dialogMessage(dark: Boolean): Color = if (dark) Color(0x8AFFFFFF) else Color(0x8A000000)

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isDialogInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
