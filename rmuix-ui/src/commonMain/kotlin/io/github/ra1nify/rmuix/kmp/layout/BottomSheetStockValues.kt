// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

// Drag-view values from the Settings APK bottom sheet (`COUIBottomSheetDialog`, the panel
// package resources it inflates): `coui_panel_drag_view.xml`, the `coui_panel_drag_view_*`
// dimens and the `coui_pannel_press_shadow_bg` press background.

/** `coui_panel_drag_view.xml` width. */
internal val BottomSheetHandleWidth = 36.dp

/** `coui_panel_drag_view.xml` height. */
internal val BottomSheetHandleHeight = 4.dp

/** `coui_panel_drag_view.xml` corner radius (a fully rounded 4dp bar). */
internal val BottomSheetHandleRadius = 2.dp

/** `coui_panel_drag_view_height` — the strip that hosts the bar. */
internal val BottomSheetHandleStripHeight = 12.dp

/** `coui_panel_drag_view_shadow_margin_top`. */
internal val BottomSheetHandleStripTopMargin = 4.dp

/** `coui_panel_drag_view_shadow_width`. */
internal val BottomSheetHandlePressWidth = 70.dp

/** `coui_panel_drag_view_shadow_height`. */
internal val BottomSheetHandlePressHeight = 20.dp

/** `coui_pannel_press_shadow_bg` corner radius. */
internal val BottomSheetHandlePressRadius = 16.dp

/** Handle bar tint defaults to `?attr/couiColorControls`: `#29000000` / `#40FFFFFF`. */
internal fun bottomSheetHandle(dark: Boolean): Color = if (dark) Color(0x40FFFFFF) else Color(0x29000000)

/** Press background colour `?attr/couiColorPress`: `#1F000000` / `#33FFFFFF`. */
internal fun bottomSheetHandlePress(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isBottomSheetInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
