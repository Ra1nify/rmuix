// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class PageSurfaceDefaultsTest {
    @Test
    fun backgroundWithCardMatchesTheStockThemeAttr() {
        // Theme.COUI: ?attr/couiColorBackgroundWithCard -> coui_color_background_with_card (#F0F1F2)
        // / coui_color_background_with_card_dark (#000000); also used by
        // coui_window_background_with_card_selector and coui_toolbar_bg.
        assertEquals(Color(0xFFF0F1F2), backgroundWithCard(dark = false))
        assertEquals(Color(0xFF000000), backgroundWithCard(dark = true))
    }
}
