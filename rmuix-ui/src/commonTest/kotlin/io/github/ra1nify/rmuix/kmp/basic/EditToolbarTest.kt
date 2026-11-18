// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * `COUIBottomFloatingEditToolbar` item metrics: `setHorizontalStyle(!isSmallScreen)` selects item style 3
 * (vertical, 48dp item, 10sp title) or 4 (horizontal, 40dp item, 14sp title), and a single item uses the
 * dedicated 148dp / 152dp widths.
 */
class EditToolbarTest {
    @Test
    fun defaultsMatchTheStockResources() {
        // coui_bottom_floating_edit_toolbar_* dimens
        assertEquals(88.dp, EditToolbarDefaults.MinHeightWithoutVerticalPadding)
        assertEquals(16.dp, EditToolbarDefaults.PaddingHorizontalSmallScreen)
        assertEquals(24.dp, EditToolbarDefaults.PaddingHorizontalMediumScreen)
        assertEquals(40.dp, EditToolbarDefaults.PaddingHorizontalLargeScreen)
        assertEquals(10.dp, EditToolbarDefaults.InnerPaddingHorizontal)
        assertEquals(12.dp, EditToolbarDefaults.InnerPaddingVertical)
        // coui_bottom_floating_navigation_bar_height / coui_bottom_floating_edit_toolbar_menu_height_medium_large
        assertEquals(56.dp, EditToolbarDefaults.MenuHeightSmall)
        assertEquals(44.dp, EditToolbarDefaults.MenuHeightMediumLarge)
        // item heights and widths
        assertEquals(48.dp, EditToolbarDefaults.ItemHeightVerticalStyle)
        assertEquals(40.dp, EditToolbarDefaults.ItemHeightHorizontalStyle)
        assertEquals(70.dp, EditToolbarDefaults.ItemMaxWidthVertical)
        assertEquals(98.dp, EditToolbarDefaults.ItemMaxWidthHorizontal)
        assertEquals(148.dp, EditToolbarDefaults.SingleItemWidthVertical)
        assertEquals(152.dp, EditToolbarDefaults.SingleItemWidthHorizontal)
        // coui_floating_toolbar_item_icon_width / _height / _width
        assertEquals(24.dp, EditToolbarDefaults.ItemIconSize)
        assertEquals(44.dp, EditToolbarDefaults.ItemMinWidth)
        // icon-title gaps and icon top margin
        assertEquals(2.dp, EditToolbarDefaults.IconTitleGapVertical)
        assertEquals(4.dp, EditToolbarDefaults.IconTitleGapHorizontal)
        assertEquals(4.dp, EditToolbarDefaults.IconTopMargin)
        // coui_bottom_floating_navigation_bar_item_space
        assertEquals(8.dp, EditToolbarDefaults.ItemSpace)
        // applyEditToolbarVerticalStyle() 10sp / applyEditToolbarHorizontalStyle() 14sp
        assertEquals(10.sp, EditToolbarDefaults.TitleTextSizeVertical)
        assertEquals(14.sp, EditToolbarDefaults.TitleTextSizeHorizontal)
        // red dot tokens
        assertEquals(6.dp, EditToolbarDefaults.RedDotDiameter)
        assertEquals(16.dp, EditToolbarDefaults.RedDotHeight)
        assertEquals(20.dp, EditToolbarDefaults.RedDotMediumWidth)
        assertEquals(26.dp, EditToolbarDefaults.RedDotLargeWidth)
        assertEquals(10.sp, EditToolbarDefaults.RedDotTextSize)
        assertEquals(4.dp, EditToolbarDefaults.RedDotTopOverflow)
        assertEquals(6.dp, EditToolbarDefaults.RedDotRightOverflow1Digit)
        assertEquals(9.dp, EditToolbarDefaults.RedDotRightOverflow2Digit)
        assertEquals(10.dp, EditToolbarDefaults.RedDotRightOverflow3Digit)
    }

    @Test
    fun coloursMatchTheStockSelectors() {
        // ?attr/couiColorLabelPrimaryVariant
        assertEquals(Color(0xB8000000), editToolbarContent(dark = false))
        assertEquals(Color(0xB8FFFFFF), editToolbarContent(dark = true))
        // ?attr/couiColorLabelTertiary
        assertEquals(Color(0x42000000), editToolbarDisabled(dark = false))
        assertEquals(Color(0x4DFFFFFF), editToolbarDisabled(dark = true))
        // coui_color_additional_red
        assertEquals(Color(0xFFEB3B2F), editToolbarRedDot(dark = false))
        assertEquals(Color(0xFFEB493D), editToolbarRedDot(dark = true))
    }

    @Test
    fun smallScreensUseTheVerticalItemStyle() {
        val metrics = editToolbarMetrics(horizontalItems = false, itemCount = 3)
        assertEquals(56.dp, metrics.menuHeight)
        assertEquals(48.dp, metrics.itemHeight)
        assertEquals(70.dp, metrics.itemMaxWidth)
        assertEquals(2.dp, metrics.iconTitleGap)
        assertEquals(10.sp, metrics.titleTextSize)
        assertEquals(3, metrics.itemStyle)
    }

    @Test
    fun mediumAndLargeScreensUseTheHorizontalItemStyle() {
        val metrics = editToolbarMetrics(horizontalItems = true, itemCount = 4)
        assertEquals(44.dp, metrics.menuHeight)
        assertEquals(40.dp, metrics.itemHeight)
        assertEquals(98.dp, metrics.itemMaxWidth)
        assertEquals(4.dp, metrics.iconTitleGap)
        assertEquals(14.sp, metrics.titleTextSize)
        assertEquals(4, metrics.itemStyle)
    }

    @Test
    fun aSingleItemUsesTheSingleItemWidths() {
        assertEquals(148.dp, editToolbarMetrics(horizontalItems = false, itemCount = 1).itemMaxWidth)
        assertEquals(152.dp, editToolbarMetrics(horizontalItems = true, itemCount = 1).itemMaxWidth)
    }
}
