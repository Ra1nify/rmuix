// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/** `COUIRoundImageView` tokens: radius / border / shadow ring and the shape mapping. */
class RoundImageTest {
    @Test
    fun defaultsMatchTheStockResources() {
        // coui_roundimageview_default_radius
        // couiBorderRadius defaults to 1dp; coui_roundimageview_default_radius (42dp) is the CIRCLE fallback size.
        assertEquals(1.dp, RoundImageDefaults.Radius)
        assertEquals(42.dp, RoundImageDefaults.CircleFallbackSize)
        // COUIRoundImageView.mBorderWidth (2px)
        assertEquals(1.dp, RoundImageDefaults.BorderWidth)
        // BORDER_CIRCLE_WIDTH (5px) and coui_roundimageView_src_width
        assertEquals(2.dp, RoundImageDefaults.ShadowRingWidth)
        assertEquals(60.dp, RoundImageDefaults.ShadowSourceSize)
    }

    @Test
    fun coloursMatchTheStockTokens() {
        // coui_border (the dark counterpart is #26FFFFFF)
        assertEquals(Color(0x26000000), RoundImageDefaults.border(dark = false))
        assertEquals(Color(0x26FFFFFF), RoundImageDefaults.border(dark = true))
        // coui_roundimageview_outcircle_color / _dark
        assertEquals(Color(0x1A000000), RoundImageDefaults.outCircle(dark = false))
        assertEquals(Color(0x33FFFFFF), RoundImageDefaults.outCircle(dark = true))
    }

    @Test
    fun showBorderUsesTheOutCirclePaintLikeOnDraw() {
        // COUIRoundImageView.onDraw(): `mOutCircle` strokes the border when couiHasBorder is set;
        // `mBorderPaint` (coui_border) is initialised but never drawn.
        val colors = RoundImageColors(borderColor = Color(0x26000000), outCircleColor = Color(0x1A000000))
        assertEquals(null, roundImageBorderStrokeColor(showBorder = false, colors))
        assertEquals(Color(0x1A000000), roundImageBorderStrokeColor(showBorder = true, colors))
    }

    @Test
    fun shapeFollowsTheStockType() {
        // COUIRoundImageView.CIRCLE / SHADOW clip to a circle; ROUND uses the 42dp radius.
        assertEquals(CircleShape, roundImageShape(RoundImageType.Circle, 42.dp))
        assertEquals(CircleShape, roundImageShape(RoundImageType.Shadow, 42.dp))
        assertEquals(RoundedCornerShape(12.dp), roundImageShape(RoundImageType.Round, 12.dp))
    }
}
