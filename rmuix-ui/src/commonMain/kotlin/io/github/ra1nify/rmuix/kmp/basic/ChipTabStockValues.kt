// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp

// Values from `Widget.COUI.Chip.ChipTab` (parent `Widget.COUI.Chip`) in the Settings APK styles, plus
// the dimens and colours that style points at.

/** `coui_chip_tab_style_height`: a chip tab is 32dp tall. */
internal val ChipTabStockHeight = 32.dp

/** `coui_chip_default_min_width`: 52dp. */
internal val ChipTabStockMinWidth = 52.dp

/** `coui_chip_default_max_width`: 300dp. */
internal val ChipTabStockMaxWidth = 300.dp

/** `coui_chip_suggest_style_chip_horizontal_padding`: 12dp of start and end padding. */
internal val ChipTabStockHorizontalPadding = 12.dp

/** `coui_chip_tab_style_chip_icon_size`: 16dp. */
internal val ChipTabStockIconSize = 16.dp

/** `coui_chip_suggest_style_chip_icon_end_padding`: 4dp between the icon and the label. */
internal val ChipTabStockIconEndPadding = 4.dp

/** `coui_chip_tab_count_start_margin`: 2dp before a count badge. */
internal val ChipTabStockCountStartMargin = 2.dp

/**
 * `chipCornerRadius = -1dp` in `Widget.COUI.Chip`, which `COUIChipTabDrawable` turns into a pill:
 * `getChipCornerRadius() < 0` falls back to `min(width, height) / 2`.
 */
internal val ChipTabStockCornerRadiusPx = -1f

/** `coui_chip_tab_surface_unchecked_surface` = `#FFFFFF` light / `#000000` dark. */
internal fun chipTabUncheckedSurface(dark: Boolean): Color = if (dark) Color(0xFF000000) else Color(0xFFFFFFFF)

/**
 * Fill of a checked chip.
 *
 * `COUIChipTabDrawable.drawDualLayerBackground()` first paints the chip with
 * `mCheckedSurface` = `?attr/couiColorBackgroundElevated` (#FFFFFF / #1E1E1E), then
 * `drawLightOSCheckedBackground()` overlays `coui_color_checked_background_gradient_start` (#40FFFFFF).
 * The resulting light fill is white; dark is #1E1E1E + 25% white.
 */
internal fun chipTabCheckedContainer(dark: Boolean): Color = ChipTabStockCheckedOverlay.compositeOver(chipTabCheckedSurface(dark))

/** `uncheckedTextColor` → `?attr/couiColorLabelPrimary` = `#E6000000` / `#E6FFFFFF`. */
internal fun chipTabUncheckedContent(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/** `checkedTextColor` is the same `?attr/couiColorLabelPrimary` pair; ColorOS does not use white here. */
internal fun chipTabCheckedContent(dark: Boolean): Color = chipTabUncheckedContent(dark)

/**
 * Fill of an unchecked chip: `coui_chip_tab_surface_unchecked_surface` (#FFFFFF / #000000) with
 * `?attr/couiColorContainer4` (#0A000000 / #1AFFFFFF) drawn on top, which yields #F5F5F5 light /
 * #1A1A1A dark at rest.
 */
internal fun chipTabUncheckedContainer(dark: Boolean): Color = chipTabTransitionOverlay(dark).compositeOver(chipTabUncheckedSurface(dark))

/**
 * `coui_color_checked_background_gradient_start` = `#40FFFFFF`.
 *
 * Despite the name, `COUIChipTabDrawable.drawLightOSCheckedBackground()` uses it as a **flat fill**
 * (`setColor(...)`, no shader) layered on top of the checked chip, and only while the blur effect is off.
 */
internal val ChipTabStockCheckedOverlay = Color(0x40FFFFFF)

/**
 * `coui_color_checked_background_stroke` = `#0A000000` / `#0AFFFFFF`.
 *
 * Drawn as a stroke around the checked chip unless the stroke effect is enabled.
 */
internal fun chipTabCheckedStroke(dark: Boolean): Color = if (dark) Color(0x0AFFFFFF) else Color(0x0A000000)

/**
 * Stroke width of the checked chip: `density * 0.6f` in `drawLightOSCheckedBackground()`, i.e. 0.6dp.
 *
 * The stroke is inset by half its width and the corner radius shrinks by the same half.
 */
internal val ChipTabStockCheckedStrokeWidth = 0.6.dp

/**
 * `?attr/couiColorContainer4` = `coui_color_container4` (**#0A000000** / **#1AFFFFFF**).
 *
 * `drawDualLayerBackground()` overlays it on the unchecked layer at `mUncheckedLayerAlpha`, i.e. it only
 * shows while a chip is transitioning.
 */
internal fun chipTabTransitionOverlay(dark: Boolean): Color = if (dark) Color(0x1AFFFFFF) else Color(0x0A000000)

/**
 * `?attr/couiColorBackgroundElevated` = `coui_color_background_elevated` (**#FFFFFF** / **#1E1E1E**).
 *
 * The drawable's own "checked surface"; the style additionally sets `checkedBackgroundColor` to
 * `couiColorPrimary`. Which one wins is unresolved - see the review notes.
 */
internal fun chipTabCheckedSurface(dark: Boolean): Color = if (dark) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
