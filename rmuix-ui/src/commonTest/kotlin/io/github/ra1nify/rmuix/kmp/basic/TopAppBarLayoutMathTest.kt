// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import kotlin.test.Test
import kotlin.test.assertEquals

class TopAppBarLayoutMathTest {
    private val collapsed = 52
    private val expanded = 110

    @Test
    fun limitIsTheCollapsibleDistance() {
        assertEquals(-58f, topAppBarHeightOffsetLimit(expanded, collapsed))
        assertEquals(0f, topAppBarHeightOffsetLimit(collapsed, collapsed))
        // A bar shorter than the collapsed height must not produce a positive limit.
        assertEquals(0f, topAppBarHeightOffsetLimit(40, collapsed))
    }

    @Test
    fun layoutHeightMovesBetweenCollapsedAndExpanded() {
        assertEquals(expanded, topAppBarLayoutHeight(expanded, collapsed, 0f))
        // Positive offsets are clamped away: the bar never grows past its expanded height.
        assertEquals(expanded, topAppBarLayoutHeight(expanded, collapsed, 20f))
        assertEquals(81, topAppBarLayoutHeight(expanded, collapsed, -29f))
        assertEquals(collapsed, topAppBarLayoutHeight(expanded, collapsed, -58f))
        // Offsets past the limit are clamped: the bar never shrinks below the collapsed height.
        assertEquals(collapsed, topAppBarLayoutHeight(expanded, collapsed, -200f))
    }

    @Test
    fun subPixelOffsetsRoundToWholePixels() {
        assertEquals(109, topAppBarLayoutHeight(expanded, collapsed, -0.6f))
        assertEquals(52, topAppBarLayoutHeight(expanded, collapsed, -57.6f))
    }

    @Test
    fun dividerMarginInterpolatesFromExpandedToCollapsed() {
        // `coui_appbar_divider_expanded_margin_horizontal` 24dp -> `..._collapsed_...` 0dp.
        assertEquals(24f, topAppBarDividerMarginPx(24f, 0f, 0f))
        assertEquals(12f, topAppBarDividerMarginPx(24f, 0f, 0.5f))
        assertEquals(0f, topAppBarDividerMarginPx(24f, 0f, 1f))
        // The fraction is clamped, so overscroll cannot push the inset past either end.
        assertEquals(24f, topAppBarDividerMarginPx(24f, 0f, -1f))
        assertEquals(0f, topAppBarDividerMarginPx(24f, 0f, 2f))
    }

    @Test
    fun dividerFractionIsScrollDistanceOverTheBarHeight() {
        // `COUIDividerAppBarLayout.getDividerScrollRange()` returns `getMeasuredHeight()`.
        assertEquals(0f, topAppBarDividerFraction(0f, 220f))
        assertEquals(0.5f, topAppBarDividerFraction(110f, 220f))
        assertEquals(1f, topAppBarDividerFraction(220f, 220f))
        // Clamped: scrolling further, or a negative distance, stays inside [0, 1].
        assertEquals(1f, topAppBarDividerFraction(400f, 220f))
        assertEquals(0f, topAppBarDividerFraction(-40f, 220f))
        assertEquals(0f, topAppBarDividerFraction(10f, 0f))
    }

    @Test
    fun collapseBottomPaddingInterpolatesTheStockTokens() {
        // `COUICollapsableAppBarLayout.adjustPaddingBottom()`: start 12dp while expanded,
        // end 0dp once the bar is pinned.
        assertEquals(12, topAppBarCollapseBottomPaddingPx(startPx = 12, endPx = 0, fraction = 0f))
        assertEquals(6, topAppBarCollapseBottomPaddingPx(startPx = 12, endPx = 0, fraction = 0.5f))
        assertEquals(0, topAppBarCollapseBottomPaddingPx(startPx = 12, endPx = 0, fraction = 1f))
        // Clamped outside [0, 1] and symmetric when the end padding is larger.
        assertEquals(12, topAppBarCollapseBottomPaddingPx(startPx = 12, endPx = 0, fraction = -1f))
        assertEquals(0, topAppBarCollapseBottomPaddingPx(startPx = 12, endPx = 0, fraction = 2f))
        assertEquals(8, topAppBarCollapseBottomPaddingPx(startPx = 0, endPx = 16, fraction = 0.5f))
    }

    @Test
    fun dividerAlphaAppearsOnlyWhenCollapsed() {
        // `dividerStartAlpha` 0 while expanded, `dividerEndAlpha` 1 once collapsed.
        assertEquals(0f, topAppBarDividerAlpha(0f))
        assertEquals(0.25f, topAppBarDividerAlpha(0.25f))
        assertEquals(1f, topAppBarDividerAlpha(1f))
        assertEquals(0f, topAppBarDividerAlpha(-1f))
        assertEquals(1f, topAppBarDividerAlpha(2f))
    }
}
