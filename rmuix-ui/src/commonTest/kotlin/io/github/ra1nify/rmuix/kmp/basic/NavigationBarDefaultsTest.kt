// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigationBarDefaultsTest {
    @Test
    fun barMetricsMatchTheNavigationDimens() {
        // coui_navigation_normal_item_height (live, 56dp) — the 54dp coui_navigation_item_height is dead
        assertEquals(56.dp, NavigationBarDefaults.BarHeight)
        assertEquals(56.dp, NavigationBarDefaults.ItemHeight)
        assertEquals(12.dp, NavigationBarDefaults.EdgePadding)
        assertEquals(0.33.dp, NavigationBarDefaults.DividerHeight)
        assertEquals(24.dp, NavigationBarDefaults.IconSize)
        assertEquals(10.sp, NavigationBarDefaults.LabelFontSize)
        assertEquals(9.dp, NavigationBarDefaults.IconTopPadding)
        assertEquals(8.dp, NavigationBarDefaults.ItemCornerRadius)
        assertEquals(2.dp, NavigationBarDefaults.ItemGap)
        // Library values: no coui_navigation_* dimen carries these.
        assertEquals(20.dp, FloatingNavigationBarDefaults.IndicatorCornerRadius)
        assertEquals(10.dp, FloatingNavigationBarDefaults.ShadowRadius)
        assertEquals(20.dp, NavigationBarDefaults.IosBottomPadding)
        assertEquals(2.dp, NavigationBarDefaults.IconTextGap)
        // coui_bottom_floating_navigation_bar_item_padding / _item_icon_top_margin
        assertEquals(12.dp, FloatingNavigationBarDefaults.ItemPadding)
        assertEquals(4.dp, FloatingNavigationBarDefaults.ItemIconTopMargin)
    }

    @Test
    fun barBackgroundFollowsTheStockBarRole() {
        assertEquals(Color(0xFFFAFAFA), navigationBarBar(false))
        assertEquals(Color(0xFF1E1E1E), navigationBarBar(true))
    }

    @Test
    fun itemMaskUsesTheStateEffectNeutrals() {
        assertEquals(Color(0x14000000), navigationBarItemHover(false))
        assertEquals(Color(0x26FFFFFF), navigationBarItemHover(true))
        assertEquals(Color(0x1F000000), navigationBarItemPress(false))
        assertEquals(Color(0x33FFFFFF), navigationBarItemPress(true))
        assertEquals(Color(0xFF3482FF).copy(alpha = 31f / 255f), navigationBarItemFocus(Color(0xFF3482FF), false))
        assertEquals(Color(0xFF3482FF).copy(alpha = 51f / 255f), navigationBarItemFocus(Color(0xFF3482FF), true))
    }

    @Test
    fun badgeAndMeltBallTokensMatchTheStockNavBar() {
        // coui_navigation_red_dot_offset / ..._with_number_horizontal/vertical_offset
        assertEquals(DpSize(1.dp, 1.dp), NavigationBarDefaults.BadgeDotOffset)
        assertEquals(DpSize(8.dp, 5.dp), NavigationBarDefaults.BadgeNumberOffset)
        // coui_bottom_floating_navigation_bar_touch_effect_radius
        assertEquals(40.dp, FloatingNavigationBarDefaults.TouchEffectRadius)
        // coui_bottom_floating_navigation_bar_melt_ball_param_extension + MELT_BALL_EXTENSION_FACTOR_{X,Y}
        assertEquals(4.dp, FloatingNavigationBarDefaults.MeltBallExtension)
        assertEquals(1.5f, FloatingNavigationBarDefaults.MeltBallFactorX)
        assertEquals(2.0f, FloatingNavigationBarDefaults.MeltBallFactorY)
    }

    @Test
    fun accessoryExpandCollapseMatchesTheStockController() {
        // AccessorySideExpansionController: doAlphaScaleAnim() animates scale to 10000 (1.0) / 5000 (0.5)
        assertEquals(0.5f, FloatingNavigationBarDefaults.AccessoryMinScale)
        // initAlphaAnim(): bounce 0, response 0.35 (melt ball) / 0.2 (no melt ball)
        assertEquals(0.35f, FloatingNavigationBarDefaults.AccessoryAlphaResponse)
        assertEquals(0.2f, FloatingNavigationBarDefaults.AccessoryAlphaResponseWithoutMelt)
        // initScaleAnim() / initTranslationAnim(): bounce 0.35 + response 0.55 -> damping 1 - 0.35
        assertEquals(0.65f, FloatingNavigationBarDefaults.AccessoryScaleDamping)
        assertEquals(0.55f, FloatingNavigationBarDefaults.AccessoryScaleResponse)
        assertEquals(0.65f, FloatingNavigationBarDefaults.AccessoryTranslationDamping)
        assertEquals(0.55f, FloatingNavigationBarDefaults.AccessoryTranslationResponse)
        assertEquals(0.001f, FloatingNavigationBarDefaults.AccessoryTranslationMinVisibleChange)
    }
}
