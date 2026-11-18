// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class RedDotDefaultsTest {
    @Test
    fun redDotUsesTheAdditionalRedAndPillRadius() {
        // couiHintRedDotColor -> @color/coui_color_additional_red
        assertEquals(Color(0xFFEB3B2F), redDotBackground(false))
        assertEquals(Color(0xFFEB493D), redDotBackground(true))
        assertEquals(Color.White, RedDotStockTextColor)
        // couiCornerRadius = couiRoundCornerFULL -> pill, half of the 16dp badge
        assertEquals(8.dp, RedDotDefaults.BadgeRadius)
    }

    @Test
    fun redDotSizesFollowTheHintRedDotTokens() {
        assertEquals(16.dp, RedDotDefaults.BadgeHeight)
        assertEquals(6.dp, RedDotDefaults.PointSize)
        assertEquals(6.dp, RedDotStockPointSize)
        assertEquals(10.sp, RedDotStockTextSize)
        assertEquals(9.sp, RedDotStockSmallTextSize)
        // coui_dot_stroke_width
        assertEquals(1.dp, RedDotDefaults.StrokeWidth)
    }

    @Test
    fun redDotWidthsFollowTheThreeStockTiers() {
        // COUIHintRedDotHelper.getBgWidth: 16 / 20 / 26dp, and four-digit counts fall back to 20dp.
        assertEquals(16.dp, RedDotDefaults.SmallWidth)
        assertEquals(20.dp, RedDotDefaults.MediumWidth)
        assertEquals(26.dp, RedDotDefaults.LargeWidth)
        assertEquals(RedDotDefaults.SmallWidth, RedDotDefaults.SingleDigitMinWidth)
        assertEquals(RedDotDefaults.MediumWidth, RedDotDefaults.MultiDigitMinWidth)
        assertEquals(RedDotDefaults.MediumWidth, RedDotDefaults.TextBadgeMinWidth)
        // couiEllipsisDiameter / coui_hint_red_dot_ellipsis_spacing
        assertEquals(2.dp, RedDotStockEllipsisDotSize)
        assertEquals(2.dp, RedDotStockEllipsisSpacing)
        // The stock badge prints the plain number below 1000 (100-999 included, no "99+" clamp) and
        // switches to three dots from 1000 on; both rules live in COUIHintRedDotHelper.drawNumber.
        assertEquals(26.dp, RedDotDefaults.LargeWidth)
    }
}
