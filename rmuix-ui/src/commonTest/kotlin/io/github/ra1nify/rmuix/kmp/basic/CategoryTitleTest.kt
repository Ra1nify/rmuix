// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class CategoryTitleTest {
    @Test
    fun metricsMatchTheCategoryTitleStyle() {
        // Widget.COUI.List.Category.Title -> couiTextAppearanceSmallButton (12sp, sans-serif-medium).
        assertEquals(12.sp, CategoryTitleDefaults.TextSize)
        assertEquals(FontWeight.Medium, CategoryTitleDefaults.TextWeight)
        // coui_preference_category_text_height.
        assertEquals(16.dp, CategoryTitleDefaults.MinHeight)
        // support_preference_category_layout_title_margin_start_large (COUIPreferenceCategory defaults
        // its margin type to large and no Settings XML overrides it) /
        // coui_category_title_pading_end_with_reddot_default / coui_common_category_text_padding_* /
        // coui_preference_category_margintop_small.
        assertEquals(32.dp, CategoryTitleDefaults.MarginStart)
        assertEquals(8.dp, CategoryTitleDefaults.MarginEnd)
        assertEquals(8.dp, CategoryTitleDefaults.VerticalMargin)
        assertEquals(12.dp, CategoryTitleDefaults.MarginTop)
        assertEquals(
            PaddingValues(start = 32.dp, top = 20.dp, end = 8.dp, bottom = 8.dp),
            CategoryTitleDefaults.InsideMargin,
        )
    }
}
