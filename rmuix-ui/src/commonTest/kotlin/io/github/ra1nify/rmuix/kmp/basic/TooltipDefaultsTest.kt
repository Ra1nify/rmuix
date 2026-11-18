// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class TooltipDefaultsTest {
    @Test
    fun dismissControlMatchesTheToolTipsLayout() {
        // coui_tool_tips_layout.xml sizes the dismiss ImageView with coui_tool_tips_delete_background_size
        // and gives it a 12dp end margin; coui_tool_tips_delete_icon_size/_padding are never referenced.
        assertEquals(18.dp, TooltipDefaults.DismissSize)
        assertEquals(12.dp, TooltipDefaults.DismissMarginEnd)
    }

    @Test
    fun imageVariantMatchesTheToolTipsImageLayout() {
        // tool_tips_image_style_max_width / coui_tool_tips_image_no_edges_style_layout.xml /
        // coui_tooltips_imageview_radius.
        assertEquals(260.dp, TooltipDefaults.ImageTooltipMaxWidth)
        assertEquals(132.dp, TooltipDefaults.ImageTooltipImageHeight)
        assertEquals(8.dp, TooltipDefaults.ImageTooltipImageRadius)
    }

    @Test
    fun iconVariantMatchesTheToolTipsIconLayout() {
        // coui_tool_tips_icon_style_layout.xml + coui_tooltips_icon_scroll_margin_top.
        assertEquals(40.dp, TooltipDefaults.IconTooltipIconSize)
        assertEquals(2.dp, TooltipDefaults.IconTooltipIconMarginTop)
        assertEquals(12.dp, TooltipDefaults.IconTooltipIconMarginStart)
        assertEquals(12.dp, TooltipDefaults.IconTooltipScrollMarginTop)
        // ?attr/couiColorBackgroundTop and ?attr/couiColorLabelSecondary.
        assertEquals(Color(0xFFFFFFFF), tooltipIconBackground(dark = false))
        assertEquals(Color(0xFF333333), tooltipIconBackground(dark = true))
        assertEquals(Color(0x8A000000), tooltipIconContent(dark = false))
        assertEquals(Color(0x8AFFFFFF), tooltipIconContent(dark = true))
    }

    @Test
    fun caretMatchesTheStockArrowVector() {
        // coui_white_sharp_middle_arrow_up.xml: 20dp x 10.1dp, a filled arrow with arc-rounded shoulders.
        assertEquals(20.dp, TooltipDefaults.caretSize.width)
        assertEquals(10.1.dp, TooltipDefaults.caretSize.height)
    }

    @Test
    fun plainTooltipUsesTheStockTipsBubble() {
        assertEquals(328.dp, TooltipDefaults.PlainTooltipMaxWidth)
        assertEquals(17.dp, TooltipDefaults.PlainTooltipCornerRadius)
        assertEquals(PaddingValues(all = 13.dp), TooltipDefaults.PlainTooltipInsideMargin)
        assertEquals(13.sp, TooltipDefaults.PlainTooltipTextSize)
    }

    @Test
    fun tipColorsFollowTheTipsRole() {
        assertEquals(Color(0xFF0080FF), tooltipBackground(false))
        assertEquals(Color(0xFF1A8CFF), tooltipBackground(true))
        assertEquals(Color.White, TooltipStockContentColor)
        assertEquals(13.dp, TooltipStockInsideMargin)
        assertEquals(17.dp, TooltipStockCornerRadius)
    }
}
