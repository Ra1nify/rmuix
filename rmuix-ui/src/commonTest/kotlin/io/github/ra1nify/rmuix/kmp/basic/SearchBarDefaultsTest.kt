// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchBarDefaultsTest {
    @Test
    fun expandAnimationMatchesTheSearchViewAnimateTimings() {
        // COUISearchViewAnimate: DEFAULT_*_DURATION constants.
        assertEquals(450, SearchBarDefaults.SlideMillis)
        assertEquals(150, SearchBarDefaults.FadeMillis)
        assertEquals(350, SearchBarDefaults.ButtonAlphaMillis)
        assertEquals(100, SearchBarDefaults.ButtonAlphaInstantMillis)
        // COUIMoveEaseInterpolator = cubic-bezier(0.3, 0.0, 0.1, 1.0);
        // COUIEaseInterpolator = cubic-bezier(0.33, 0.0, 0.67, 1.0).
        assertEquals(CubicBezierEasing(0.3f, 0f, 0.1f, 1f), SearchBarDefaults.MoveEase)
        assertEquals(CubicBezierEasing(0.33f, 0f, 0.67f, 1f), SearchBarDefaults.Ease)
    }

    @Test
    fun fieldMetricsMatchTheSearchViewTokens() {
        // coui_search_bar_normal_background_height
        assertEquals(40.dp, SearchBarDefaults.InputFieldMinHeight)
        // coui_search_view_corner
        assertEquals(18.dp, SearchBarDefaults.InputFieldCornerRadius)
        // coui_search_view_input_text_size / coui_search_view_text_hint_size
        assertEquals(15.sp, SearchBarDefaults.InputFieldFontSize)
        assertEquals(12.sp, SearchBarDefaults.InputFieldHintFontSize)
        // coui_search_view_icon_normal (20dp) inside the 36dp inner-icon box
        assertEquals(20.dp, SearchBarDefaults.IconSize)
        assertEquals(24.dp, SearchBarStockIconMaxSize)
        assertEquals(36.dp, SearchBarStockIconBoxSize)
        assertEquals(12.dp, SearchBarDefaults.IconStartMargin)
        // coui_search_view_margin_left/right
        assertEquals(16.dp, SearchBarDefaults.SideMargin)
    }

    @Test
    fun fieldFillUsesTheSelectorColors() {
        // coui_search_view_selector_color_normal / _pressed
        assertEquals(Color(0x14000000), searchBarFieldColor(false))
        assertEquals(Color(0x1AFFFFFF), searchBarFieldColor(true))
        assertEquals(Color(0x1E000000), searchBarFieldPressedColor(false))
        assertEquals(Color(0x33FFFFFF), searchBarFieldPressedColor(true))
    }

    @Test
    fun clearButtonUsesTheDeleteVectorGeometry() {
        // ic_edit_text_delete_search_view: 18dp circle in a 36dp box. The white cross is now drawn from
        // the stock path data (8.204dp across, 1.6dp arms, 45-degree ends) instead of two lines, so the
        // glyph length/thickness constants are gone; only the viewport-derived sizes are asserted here.
        assertEquals(18.dp, SearchBarStockClearIconSize)
        assertEquals(36.dp, SearchBarStockClearIconBoxSize)
        // couiColorHintNeutral
        assertEquals(Color(0x42000000), searchBarClearIconBackground(false))
        assertEquals(Color(0x4DFFFFFF), searchBarClearIconBackground(true))
        // coui_search_view_close_button_selector
        assertEquals(180, SearchBarStockClearEnterFadeMillis)
        assertEquals(360, SearchBarStockClearExitFadeMillis)
        // couiColorSecondNeutral
        assertEquals(Color(0x8A000000), searchBarIconColor(false))
        assertEquals(Color(0x8AFFFFFF), searchBarIconColor(true))
    }
}
