// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class BasicComponentGroupDefaultsTest {
    @Test
    fun cardMetricsMatchCouiCardList() {
        // ?attr/couiRoundCornerXL = coui_round_corner_xl
        assertEquals(20.dp, BasicComponentGroupDefaults.CornerRadius)
        // coui_round_corner_xl_weight
        assertEquals(1.1f, BasicComponentGroupDefaults.CornerExtension)
        // coui_list_card_head_or_tail_padding
        assertEquals(2.dp, BasicComponentGroupDefaults.GroupPadding)
        // coui_list_divider_height
        assertEquals(0.33.dp, BasicComponentGroupDefaults.DividerThickness)
        // support_preference_title_padding_start/end and coui_preference_divider_default_horizontal_padding
        assertEquals(32.dp, BasicComponentGroupDefaults.DividerStart)
        assertEquals(32.dp, BasicComponentGroupDefaults.DividerEnd)
    }

    @Test
    fun footerMatchesSettingsCategoryBottomTip() {
        // couiTextAppearanceArticleBody
        assertEquals(13.sp, BasicComponentGroupDefaults.FooterTextSize)
        // support_preference_title_padding_start/end = 32dp, category_bottom_tip_preference_padding_top/bottom = 8dp
        assertEquals(32.dp, BasicComponentGroupDefaults.FooterPadding.calculateStartPadding(LayoutDirection.Ltr))
        assertEquals(32.dp, BasicComponentGroupDefaults.FooterPadding.calculateEndPadding(LayoutDirection.Ltr))
        assertEquals(8.dp, BasicComponentGroupDefaults.FooterPadding.calculateTopPadding())
        assertEquals(8.dp, BasicComponentGroupDefaults.FooterPadding.calculateBottomPadding())
    }
}
