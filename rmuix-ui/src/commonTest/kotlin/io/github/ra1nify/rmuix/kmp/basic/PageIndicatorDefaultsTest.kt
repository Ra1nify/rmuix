// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class PageIndicatorDefaultsTest {
    @Test
    fun dotsComeFromTheStockResources() {
        assertEquals(6.dp, PageIndicatorDefaults.DotSize)
        assertEquals(4.5.dp, PageIndicatorDefaults.DotSizeMedium)
        assertEquals(2.5.dp, PageIndicatorDefaults.DotSizeSmall)
        assertEquals(3.dp, PageIndicatorDefaults.Spacing)
        assertEquals(1.dp, PageIndicatorDefaults.StrokeWidth)
    }

    @Test
    fun dotColorsMatchThePageIndicatorStyle() {
        // Widget.COUI.COUIPageIndicator: traceDotColor = @color/coui_color_label_primary,
        // dotColor = ?attr/couiColorHintNeutral.
        assertEquals(Color(0xE6000000), pageIndicatorSelected(dark = false))
        assertEquals(Color(0xE6FFFFFF), pageIndicatorSelected(dark = true))
        assertEquals(Color(0x42000000), pageIndicatorUnselected(dark = false))
        assertEquals(Color(0x4DFFFFFF), pageIndicatorUnselected(dark = true))
    }
}
