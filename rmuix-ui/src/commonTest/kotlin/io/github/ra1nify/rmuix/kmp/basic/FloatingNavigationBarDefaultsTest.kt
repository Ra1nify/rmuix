// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks [FloatingNavigationBarDefaults] to the `coui_bottom_floating_navigation_bar_*` dimens.
 *
 * The row of the coverage matrix used to read "已复核" without a test file, so nothing prevented these
 * values from drifting. Values marked *no stock token* are library choices and are asserted only to make a
 * future change deliberate.
 */
class FloatingNavigationBarDefaultsTest {
    @Test
    fun barMetricsMatchTheFloatingNavigationDimens() {
        // coui_bottom_floating_navigation_bar_height / _item_height / _item_max_width
        assertEquals(56.dp, FloatingNavigationBarDefaults.BarHeight)
        assertEquals(48.dp, FloatingNavigationBarDefaults.ItemHeight)
        assertEquals(70.dp, FloatingNavigationBarDefaults.ItemMaxWidth)
        // coui_bottom_floating_navigation_bar_item_padding / _item_icon_top_margin / _item_space
        assertEquals(12.dp, FloatingNavigationBarDefaults.ItemPadding)
        assertEquals(4.dp, FloatingNavigationBarDefaults.ItemIconTopMargin)
        assertEquals(8.dp, FloatingNavigationBarDefaults.ItemSpacing)
        // coui_bottom_floating_navigation_bar_padding_horizontal / _large_icon_size
        assertEquals(16.dp, FloatingNavigationBarDefaults.HorizontalPadding)
        assertEquals(40.dp, FloatingNavigationBarDefaults.LargeIconSize)
    }

    @Test
    fun libraryOnlyValuesStayFrozen() {
        // Half of the 56dp bar: the accessory is a pill. No stock token.
        assertEquals(28.dp, FloatingNavigationBarDefaults.CornerRadius)
        // No stock token; closest official value is `..._padding_horizontal` 16dp.
        assertEquals(36.dp, FloatingNavigationBarDefaults.HorizontalOutSidePadding)
        // No stock token: the stock bar has no elevation shadow.
        assertEquals(1.dp, FloatingNavigationBarDefaults.ShadowElevation)
        // No floating-bar token: 24dp is the generic COUI icon size.
        assertEquals(24.dp, FloatingNavigationBarDefaults.IconSize)
        // COUIBottomFloatingAccessoryNavigationBar.mSideViewSpacing reads
        // coui_bottom_floating_navigation_bar_Minimum_spacing_with_slide_view (12dp).
        assertEquals(12.dp, FloatingNavigationBarDefaults.SideSpacing)
        // coui_bottom_floating_navigation_bar_item_bg_horizontal_padding
        assertEquals(8.dp, FloatingNavigationBarDefaults.ItemBackgroundHorizontalPadding)
        // coui_bottom_floating_navigation_bar_min_height_without_padding_vertical (56 + 16 + 16)
        assertEquals(88.dp, FloatingNavigationBarDefaults.MinHeightWithoutVerticalPadding)
        // coui_bottom_floating_navigation_bar_large_icon_overflow
        assertEquals(14.dp, FloatingNavigationBarDefaults.LargeIconOverflow)
        // coui_bottom_floating_navigation_bar_item_icon_title_gap_horizontal
        assertEquals(4.dp, FloatingNavigationBarDefaults.IconTitleGapHorizontal)
    }

    @Test
    fun touchMotionParamsMatchTheStockTierDefaults() {
        // TouchMotionTierParams defaults; COUIRubberAnimatorUnit uses the stretch pair for translation
        // and COUIDeformAnimatorUnit uses the deform pair for the scale.
        assertEquals(28.dp, FloatingNavigationBarDefaults.StretchMaxDistance)
        assertEquals(0.05f, FloatingNavigationBarDefaults.StretchCurveRatio)
        assertEquals(150.dp, FloatingNavigationBarDefaults.DeformMaxRubberDistance)
        assertEquals(0.55f, FloatingNavigationBarDefaults.DeformRubberCurveRatio)
        assertEquals(1.2f, FloatingNavigationBarDefaults.DeformMaxScale)
        assertEquals(0.85f, FloatingNavigationBarDefaults.DragSpringDamping)
        assertEquals(0.15f, FloatingNavigationBarDefaults.DragSpringResponse)
        assertEquals(0.35f, FloatingNavigationBarDefaults.HandUpSpringDamping)
        assertEquals(0.42f, FloatingNavigationBarDefaults.HandUpSpringResponse)
        assertEquals(0.45f, FloatingNavigationBarDefaults.HandUpSpringLargeAreaResponse)
        assertEquals(1000f, FloatingNavigationBarDefaults.HandUpMaxInitialVelocity)
    }

    @Test
    fun materialColorsMatchTheStockResources() {
        // colors.xml: mask centre #00FFFFFF, mask edge #33FFFFFF, menu shadow #8A000000.
        assertEquals(Color(0x00FFFFFF), FloatingNavigationBarDefaults.MenuMaskCenterColor)
        assertEquals(Color(0x33FFFFFF), FloatingNavigationBarDefaults.MenuMaskEdgeColor)
        assertEquals(Color(0x8A000000), FloatingNavigationBarDefaults.MenuShadowColor)
    }
}
