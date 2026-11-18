// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class TopTipsDefaultsTest {
    @Test
    fun singleLinePillRequiresAOneLineMessageWithoutTitle() {
        // `COUIDefaultTopTips.updateRadius(hasTitle, isSingleLineText)`.
        val single = topTipsLayoutSpec(withTitle = false, lineCount = 1)
        assertEquals(false, single.multiLine)
        assertEquals(72.dp, single.radius)
        assertEquals(12.dp, single.padding)
        assertEquals(true, topTipsLayoutSpec(withTitle = true, lineCount = 1).multiLine)
        assertEquals(20.dp, topTipsLayoutSpec(withTitle = true, lineCount = 1).radius)
        assertEquals(16.dp, topTipsLayoutSpec(withTitle = true, lineCount = 1).padding)
        assertEquals(true, topTipsLayoutSpec(withTitle = false, lineCount = 2).multiLine)
        assertEquals(20.dp, topTipsLayoutSpec(withTitle = false, lineCount = 2).radius)
        assertEquals(16.dp, topTipsLayoutSpec(withTitle = false, lineCount = 3).padding)
    }

    @Test
    fun cardShapeMatchesCouiDefaultTopTips() {
        // getSingleLineRadius() -> ?attr/couiRoundCornerFULL = coui_round_corner_full
        assertEquals(72.dp, TopTipsDefaults.SingleLineRadius)
        // getMultiLineRadius() -> coui_toptips_view_radius_os16
        assertEquals(20.dp, TopTipsDefaults.MultiLineRadius)
        // coui_toptips_view_padding / coui_toptips_view_padding_multi_line
        assertEquals(12.dp, TopTipsDefaults.SingleLinePadding)
        assertEquals(16.dp, TopTipsDefaults.MultiLinePadding)
    }

    @Test
    fun layoutMatchesCouiDefaultTopTips() {
        // coui_toptips_view_icon_btn_size
        assertEquals(24.dp, TopTipsDefaults.IconSize)
        // coui_toptips_view_icon_start_margin
        assertEquals(16.dp, TopTipsDefaults.IconStartMargin)
        // coui_toptips_view_icon_btn_size (no title) / coui_toptips_view_title_icon_size (with title)
        assertEquals(24.dp, TopTipsDefaults.ImageSize)
        assertEquals(36.dp, TopTipsDefaults.TitleIconSize)
        // coui_toptips_view_title_icon_text_gap
        assertEquals(12.dp, TopTipsDefaults.TitleIconTextGap)
        // coui_toptips_view_title_min_height
        assertEquals(22.dp, TopTipsDefaults.TitleMinHeight)
        // coui_toptips_view_content_min_height / content_start_margin / content_top_margin
        assertEquals(20.dp, TopTipsDefaults.ContentMinHeight)
        assertEquals(8.dp, TopTipsDefaults.ContentStartMargin)
        assertEquals(12.dp, TopTipsDefaults.ContentTopMargin)
        // coui_toptips_view_action_close_gap / content_end_margin
        assertEquals(12.dp, TopTipsDefaults.ActionCloseGap)
        assertEquals(16.dp, TopTipsDefaults.ContentEndMargin)
        // coui_toptips_view_title_content_gap
        assertEquals(0.dp, TopTipsDefaults.TitleContentGap)
    }

    @Test
    fun textSizesMatchTheStockStyles() {
        // coui_toptips_view_default_title_size / ?style/couiTextHeadlineXS
        assertEquals(16.sp, TopTipsDefaults.TitleFontSize)
        // ?style/couiTextBodyS
        assertEquals(13.sp, TopTipsDefaults.ContentFontSize)
        // coui_toptips_view_default_text_size
        assertEquals(13.sp, TopTipsDefaults.ActionFontSize)
        assertEquals(2, TopTipsDefaults.TitleMaxLines)
    }

    @Test
    fun marqueeMatchesCouiMarqueeTextView() {
        // coui_top_tips_scroll_speed: read as px / refresh rate in COUIMarqueeTextView.initSpeed()
        assertEquals(25.dp, TopTipsDefaults.MarqueeVelocity)
        // coui_top_tips_scroll_text_interval: the gap generateTextDistance() appends between copies
        assertEquals(80.dp, TopTipsDefaults.MarqueeSpacing)
        // coui_top_tips_fading_edge_size: setFadingEdgeLength() / getLeft/RightFadingEdgeStrength()
        assertEquals(26.dp, TopTipsDefaults.MarqueeFadeEdgeLength)
        // DEFAULT_SCROLL_DELAY_DURATION: postDelayed(mStartScrollRunnable, 1000L)
        assertEquals(1000, TopTipsDefaults.MarqueeInitialDelayMillis)
        // coui_top_tips_scroll_text_start_location = 10dp: the initial mCurrentScrollLocation inset.
        assertEquals(10.dp, TopTipsDefaults.MarqueeStartLocation)
    }

    @Test
    fun coloursMatchTheStockTokens() {
        // coui_color_fill_thin / _dark
        assertEquals(Color(0x0A000000), topTipsContainerColor(false))
        assertEquals(Color(0x14FFFFFF), topTipsContainerColor(true))
        // ?attr/couiColorLabelPrimary
        assertEquals(Color(0xE6000000), topTipsTitleColor(false))
        assertEquals(Color(0xE6FFFFFF), topTipsTitleColor(true))
        // ?attr/couiColorLabelSecondary
        assertEquals(Color(0x8A000000), topTipsContentColor(false))
        assertEquals(Color(0x8AFFFFFF), topTipsContentColor(true))
        // coui_color_primary_text_blue
        assertEquals(Color(0xFF0080FF), topTipsActionColor(false))
        assertEquals(Color(0xFF5C9DFF), topTipsActionColor(true))
    }
}
