// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

// Values from the Settings APK: `content_divider_width` and `couiColorDivider`
// (`coui_color_divider` / `coui_color_divider_dark`).

/** `content_divider_width`: the settings hairline is 0.33dp, not a full logical pixel. */
internal val DividerStockThickness = 0.33.dp

/** `couiColorDivider` = `#1F000000` (light) / `#33FFFFFF` (dark). */
internal fun dividerStockColor(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isDividerInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
