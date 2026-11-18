// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

// Values from the Settings APK tab layout
// (`com.coui.appcompat.tablayout.COUITabLayout`, `COUITabLayoutBaseStyle`, `coui_tab_default_color`
// and the `tablayout_*` dimens).

/** `couiTabTextColor` selected / pressed → `couiColorPrimaryNeutral`: `#E6000000` / `#E6FFFFFF`. */
internal fun tabRowSelectedContent(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/** `couiTabTextColor` idle → `couiColorSecondNeutral`: `#8A000000` / `#8AFFFFFF`. */
internal fun tabRowIdleContent(dark: Boolean): Color = if (dark) Color(0x8AFFFFFF) else Color(0x8A000000)

/**
 * Text colour while the whole row is disabled.
 *
 * `COUITabLayout` builds its text colour state list as
 * `createColorStateList(defaultColor, mTabTextDisabledColor, selectedColor)` with
 * `mTabTextDisabledColor = ?attr/couiColorDisabledNeutral`. That attr resolves to:
 *
 * - `@color/coui_color_disabled_neutral` = `#42000000`
 * - `@color/coui_color_disabled_neutral_dark` = `#4DFFFFFF`
 *
 * `setEnabled(false)` disables every tab view, so the state list resolves to this colour automatically.
 */
internal fun tabRowDisabledContent(dark: Boolean): Color = if (dark) Color(0x4DFFFFFF) else Color(0x42000000)

/** `couiTabIndicatorColor` = `couiColorPrimaryNeutral`: `#E6000000` / `#E6FFFFFF`. */
internal fun tabRowIndicator(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/** `couiTabIndicatorBackgroundColor` = `tablayout_indicator_background`: `#0F000000` / `#19FFFFFF`. */
internal fun tabRowIndicatorTrack(dark: Boolean): Color = if (dark) Color(0x19FFFFFF) else Color(0x0F000000)

/**
 * `couiTabIndicatorDisableColor` = `#FFD9D9D9` (no dark variant).
 *
 * `COUITabLayout.setEnabled(false)` swaps the indicator to this colour and calls `enableTab(i, false)`
 * on every tab, so disabling is a **row-level** operation rather than a per-tab flag.
 */
internal fun tabRowIndicatorDisabled(): Color = Color(0xFFD9D9D9)

/** `coui_tab_layout_small_tab_spacing`: 20dp below the medium window width class. */
internal val TabRowStockSmallTabSpacing = 20.dp

/** `coui_tab_layout_medium_tab_spacing`: 24dp for the medium/large window width classes. */
internal val TabRowStockMediumTabSpacing = 24.dp

/**
 * `COUITabLayout.DEFAULT_MIN_INDICATOR` / `COUISlidingTabStrip.setIndicatorPosition()`:
 * `Math.max(width, dpToPx(32))`, so the underline never gets narrower than 32dp.
 */
internal val TabRowStockMinIndicatorWidth = 32.dp

/** `WindowWidthSizeClass`: medium starts at 600dp (`COUIResponsiveUtils.isMediumScreen`). */
internal val TabRowStockMediumWindowWidth = 600.dp

/** `coui_tablayout_bg` → `couiColorBackgroundWithCard`: `#F0F1F2` / `#000000`. */
internal fun tabRowBackground(dark: Boolean): Color = if (dark) Color(0xFF000000) else Color(0xFFF0F1F2)

/**
 * `?attr/couiColorPress` painted by the tab's `COUIMaskEffectDrawable`
 * (`COUITabView.setMaskRect(mTabRect, dpToPx(8), dpToPx(8))`): `#1F000000` / `#33FFFFFF`.
 */
internal fun tabRowPress(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

/** `?attr/couiColorHover` for the same mask: `#14000000` / `#26FFFFFF`. */
internal fun tabRowHover(dark: Boolean): Color = if (dark) Color(0x26FFFFFF) else Color(0x14000000)

/** `?attr/couiColorFocus`: the primary at 31/255 (light) or 51/255 (dark) alpha, like the nav item. */
internal fun tabRowFocus(
    primary: Color,
    dark: Boolean,
): Color = primary.copy(alpha = primary.alpha * (if (dark) 51f else 31f) / 255f)

/** `COUITabView` masks the tab rect with an 8dp corner radius. */
internal val tabRowPressMaskCornerRadius = 8.dp

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isTabRowInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
