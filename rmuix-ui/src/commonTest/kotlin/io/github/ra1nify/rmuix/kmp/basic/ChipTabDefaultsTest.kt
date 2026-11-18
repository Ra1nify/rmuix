// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks [ChipTabDefaults] to `Widget.COUI.Chip.ChipTab` and the dimens / colours that style points at.
 */
class ChipTabDefaultsTest {
    @Test
    fun geometryMatchesTheChipTabStyle() {
        assertEquals(32.dp, ChipTabDefaults.Height)
        assertEquals(52.dp, ChipTabDefaults.MinWidth)
        assertEquals(300.dp, ChipTabDefaults.MaxWidth)
        assertEquals(12.dp, ChipTabDefaults.HorizontalPadding)
        assertEquals(16.dp, ChipTabDefaults.IconSize)
        assertEquals(4.dp, ChipTabDefaults.IconEndPadding)
        assertEquals(2.dp, ChipTabDefaults.CountStartMargin)
        assertEquals(14.sp, ChipTabDefaults.TextSize)
        assertEquals(13.sp, ChipTabDefaults.CountTextSize)
        // couiTextButtonM sets sans-serif-medium.
        assertEquals(FontWeight.Medium, ChipTabDefaults.TextWeight)
    }

    @Test
    fun colorsMatchTheChipTabDrawable() {
        // drawDualLayerBackground(): checked = couiColorBackgroundElevated + #40FFFFFF overlay.
        assertEquals(Color(0xFFFFFFFF), chipTabCheckedContainer(dark = false))
        assertEquals(
            Color(0x40FFFFFF).compositeOver(Color(0xFF1E1E1E)),
            chipTabCheckedContainer(dark = true),
        )
        // checkedTextColor / uncheckedTextColor are both ?attr/couiColorLabelPrimary (no white label).
        assertEquals(Color(0xE6000000), chipTabCheckedContent(dark = false))
        assertEquals(Color(0xE6FFFFFF), chipTabCheckedContent(dark = true))
        assertEquals(Color(0xE6000000), chipTabUncheckedContent(dark = false))
        assertEquals(Color(0xE6FFFFFF), chipTabUncheckedContent(dark = true))
        // unchecked = coui_chip_tab_surface_unchecked_surface + ?attr/couiColorContainer4 overlay.
        assertEquals(
            Color(0x0A000000).compositeOver(Color(0xFFFFFFFF)),
            chipTabUncheckedContainer(dark = false),
        )
        assertEquals(
            Color(0x1AFFFFFF).compositeOver(Color(0xFF000000)),
            chipTabUncheckedContainer(dark = true),
        )
    }

    @Test
    fun checkedOverlayMatchesTheChipTabDrawable() {
        // coui_color_checked_background_gradient_start (used as a flat fill) and _stroke.
        assertEquals(Color(0x40FFFFFF), ChipTabStockCheckedOverlay)
        assertEquals(Color(0x0A000000), chipTabCheckedStroke(dark = false))
        assertEquals(Color(0x0AFFFFFF), chipTabCheckedStroke(dark = true))
        // drawLightOSCheckedBackground(): strokeWidth = density * 0.6f.
        assertEquals(0.6.dp, ChipTabDefaults.CheckedStrokeWidth)
    }

    @Test
    fun transitionLayerMatchesTheChipTabDrawable() {
        // ?attr/couiColorContainer4 = coui_color_container4, overlaid while a chip cross-fades.
        assertEquals(Color(0x0A000000), chipTabTransitionOverlay(dark = false))
        assertEquals(Color(0x1AFFFFFF), chipTabTransitionOverlay(dark = true))
        // ?attr/couiColorBackgroundElevated, the drawable's own checked surface.
        assertEquals(Color(0xFFFFFFFF), chipTabCheckedSurface(dark = false))
        assertEquals(Color(0xFF1E1E1E), chipTabCheckedSurface(dark = true))
        // CheckedLayerAlphaAnimation: COUISpringForce().setResponse(0.2f), BOUNCE = 0f.
        assertEquals(0.2f, ChipTabDefaults.ColorTransitionResponse)
    }

    @Test
    fun cornerRadiusIsTheStockPillSentinel() {
        // chipCornerRadius = -1dp; COUIChipTabDrawable falls back to min(width, height) / 2.
        assertEquals(-1f, ChipTabStockCornerRadiusPx)
    }
}
