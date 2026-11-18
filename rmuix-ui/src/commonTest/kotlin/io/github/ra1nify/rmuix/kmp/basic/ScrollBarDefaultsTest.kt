// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class ScrollBarDefaultsTest {
    @Test
    fun thumbMetricsFollowTheInsetArithmetic() {
        // coui_scrollbar_wight 14dp - 2 * 5.5dp default inset / - 2 * 4dp pressed inset
        assertEquals(3.dp, ScrollBarDefaults.ThumbWidth)
        assertEquals(6.dp, ScrollBarDefaults.DragThumbWidth)
        // coui_scrollbar_min_height (48dp) minus margin_top 8dp + margin_bottom 16dp = 24dp visible.
        assertEquals(24.dp, ScrollBarDefaults.ThumbMinLength)
        // coui_scrollbar_margin_top / coui_scrollbar_margin_bottom (InsetDrawable)
        assertEquals(8.dp, ScrollBarDefaults.VerticalTrackPadding.calculateTopPadding())
        assertEquals(16.dp, ScrollBarDefaults.VerticalTrackPadding.calculateBottomPadding())
    }

    @Test
    fun thumbAlphaFollowsTheStockNeutral() {
        assertEquals(41f / 255f, scrollBarThumbAlpha(false))
        assertEquals(64f / 255f, scrollBarThumbAlpha(true))
        assertEquals(ScrollBarDefaults.ThumbAlpha, scrollBarThumbAlpha(false))
        assertEquals(ScrollBarDefaults.ThumbAlpha, ScrollBarDefaults.DragThumbAlpha)
    }
}
