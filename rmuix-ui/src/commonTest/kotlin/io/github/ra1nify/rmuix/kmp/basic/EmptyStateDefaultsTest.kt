// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class EmptyStateDefaultsTest {
    @Test
    fun emptyStateMatchesCouiComponentEmptyState() {
        // coui_component_empty_group_width / coui_component_empty_text_scroll_max_height
        assertEquals(280.dp, EmptyStateDefaults.GroupWidth)
        assertEquals(92.dp, EmptyStateDefaults.TextGroupMaxHeight)
        // ?attr/couiTextHeadlineXS / ?attr/couiTextBodyXS / ?attr/couiTextButtonM
        assertEquals(16.sp, EmptyStateDefaults.TitleFontSize)
        assertEquals(12.sp, EmptyStateDefaults.SubtitleFontSize)
        assertEquals(14.sp, EmptyStateDefaults.ActionFontSize)
        // android:maxLines of the two labels, and the two layout margins
        assertEquals(2, EmptyStateDefaults.TitleMaxLines)
        assertEquals(3, EmptyStateDefaults.SubtitleMaxLines)
        assertEquals(2.dp, EmptyStateDefaults.SubtitleTopMargin)
        assertEquals(8.dp, EmptyStateDefaults.ActionTopMargin)
    }

    @Test
    fun completeStateMatchesCouiComponentCompleteState() {
        // The 100dp EffectiveAnimationView of the layout
        assertEquals(100.dp, CompleteStateDefaults.IllustrationSize)
        // complete_page_text_padding_hor / _title_margin_top / _subtitle_margin_top
        assertEquals(8.dp, CompleteStateDefaults.HorizontalPadding)
        assertEquals(8.dp, CompleteStateDefaults.TitleTopMargin)
        assertEquals(8.dp, CompleteStateDefaults.SubtitleTopMargin)
        // ?attr/couiTextHeadlineM / ?attr/couiTextBodyXS
        assertEquals(20.sp, CompleteStateDefaults.TitleFontSize)
        assertEquals(12.sp, CompleteStateDefaults.SubtitleFontSize)
    }
}
