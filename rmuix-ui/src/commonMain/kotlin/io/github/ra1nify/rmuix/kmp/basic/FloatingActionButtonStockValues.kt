// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp

// Values from the Settings APK floating button
// (`coui_floating_button_fab_*` and `coui_shadow_elevation_caustic_fab`).

/** `coui_floating_button_fab_normal_size`: the regular button is 56dp. */
internal val FloatingActionButtonStockSize = 56.dp

/**
 * `coui_floating_button_fab_mini_size` declares 54dp, but nothing in this APK reads that dimen (the class
 * falls back to `DEFAULT_COUI_FLOATING_BUTTON_SIZE = 56` and the live `coui_floating_button_size`), so the
 * mini size is a visual default here rather than a ported value.
 */
internal val FloatingActionButtonStockMiniSize = 54.dp

/** `coui_floating_button_fab_side_margin`: the button keeps 12dp from the screen edge. */
internal val FloatingActionButtonStockSideMargin = 12.dp

/** `coui_shadow_elevation_caustic_fab`: the stock shadow elevation is 8dp. */
internal val FloatingActionButtonStockShadowElevation = 8.dp
