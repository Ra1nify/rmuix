// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class TopAppBarDefaultsTest {
    @Test
    fun appBarMetricsMatchTheAppBarDimens() {
        // coui_appbar_title_toolbar_height / core_toolbar_min_height
        assertEquals(52.dp, TopAppBarDefaults.CollapsedHeight)
        // TextAppearance.COUI.Toolbar.SecondTitle (textAppearanceSecondTitle): 18dp, the static app bar
        assertEquals(18.sp, TopAppBarDefaults.ToolbarTitleTextSize)
        // coui_appbar_title_collapsed_text_size: the collapsable form's collapsed title
        assertEquals(24.sp, TopAppBarDefaults.TitleTextSize)
        // coui_appbar_title_collapsed_with_subtitle_text_size
        assertEquals(18.sp, TopAppBarDefaults.TitleTextSizeWithSubtitle)
        // coui_appbar_subtitle_text_size
        assertEquals(14.sp, TopAppBarDefaults.SubtitleTextSize)
        // coui_appbar_title_expanded_text_size
        assertEquals(32.sp, TopAppBarDefaults.LargeTitleTextSize)
        // coui_appbar_title_expanded_height / _margin_top / _margin_start_end
        assertEquals(110.dp, TopAppBarDefaults.LargeTitleExpandedHeight)
        assertEquals(54.dp, TopAppBarDefaults.LargeTitleMarginTop)
        assertEquals(24.dp, TopAppBarDefaults.LargeTitleMarginHorizontal)
        // coui_appbar_start_padding_bottom / coui_appbar_end_padding_bottom
        assertEquals(12.dp, TopAppBarDefaults.StartPaddingBottom)
        assertEquals(0.dp, TopAppBarDefaults.EndPaddingBottom)
    }

    @Test
    fun staticToolbarMetricsComeFromCouiToolbar() {
        // coui_toolbar_gap_between_navigation_and_title
        assertEquals(4.dp, TopAppBarDefaults.TitlePadding)
        // toolbar_normal_menu_padding_left / _right
        assertEquals(8.dp, TopAppBarDefaults.NavigationIconPadding)
        assertEquals(11.dp, TopAppBarDefaults.ActionIconPadding)
        // coui_toolbar_subtitle_text_size through TextAppearance.COUI.AppCompatSupport.Toolbar.SubTitle
        assertEquals(13.sp, TopAppBarDefaults.ToolbarSubtitleTextSize)
        // coui_appbar_subtitle_text_size stays the collapsable form's size
        assertEquals(14.sp, TopAppBarDefaults.SubtitleTextSize)
        assertEquals(14.sp, topAppBarSubtitleTextSize(hasLargeTitle = true))
        assertEquals(13.sp, topAppBarSubtitleTextSize(hasLargeTitle = false))
    }

    @Test
    fun titleMetricsFollowTheStockAppBarForm() {
        // Static COUIToolbar (no large title): 18sp medium.
        assertEquals(18.sp, topAppBarTitleMetrics(hasLargeTitle = false, hasSubtitle = false).textSize)
        assertEquals(FontWeight.Medium, topAppBarTitleMetrics(hasLargeTitle = false, hasSubtitle = false).fontWeight)
        // Collapsable COUICollapsableAppBarLayout: collapsed title 24sp bold.
        assertEquals(24.sp, topAppBarTitleMetrics(hasLargeTitle = true, hasSubtitle = false).textSize)
        assertEquals(FontWeight.Bold, topAppBarTitleMetrics(hasLargeTitle = true, hasSubtitle = false).fontWeight)
        // A subtitle drops either form to 18sp.
        assertEquals(18.sp, topAppBarTitleMetrics(hasLargeTitle = true, hasSubtitle = true).textSize)
        assertEquals(18.sp, topAppBarTitleMetrics(hasLargeTitle = false, hasSubtitle = true).textSize)
    }
}
