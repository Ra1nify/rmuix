// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class TabRowDefaultsTest {
    @Test
    fun tabMetricsMatchTheTablayoutDimens() {
        assertEquals(40.dp, TabRowDefaults.TabRowHeight)
        assertEquals(2.dp, TabRowDefaults.IndicatorHeight)
        assertEquals(24.dp, TabRowDefaults.IndicatorInset)
        // DEFAULT_MIN_INDICATOR: the moving underline is max(label width, 32dp).
        assertEquals(32.dp, TabRowDefaults.MinIndicatorWidth)
        // coui_tab_layout_small_tab_spacing / _medium_tab_spacing (>= 600dp window width).
        assertEquals(20.dp, TabRowDefaults.ItemSpacing)
        assertEquals(24.dp, TabRowDefaults.MediumItemSpacing)
        assertEquals(58.dp, TabRowDefaults.FadeEdgeLength)
        assertEquals(72.dp, TabRowDefaults.TabRowMinWidth)
        assertEquals(128.dp, TabRowDefaults.TabRowMaxWidth)
    }

    @Test
    fun disabledContentMatchesTheDisabledNeutralToken() {
        // couiColorDisabledNeutral: #42000000 light / #4DFFFFFF dark.
        assertEquals(Color(0x42000000), tabRowDisabledContent(false))
        assertEquals(Color(0x4DFFFFFF), tabRowDisabledContent(true))
    }

    @Test
    fun disabledIndicatorMatchesTheStockDisableColour() {
        // couiTabIndicatorDisableColor = #FFD9D9D9, used when COUITabLayout.setEnabled(false) runs.
        assertEquals(Color(0xFFD9D9D9), tabRowIndicatorDisabled())
    }

    @Test
    fun tabColorsMatchTheTabSelectors() {
        assertEquals(Color(0xE6000000), tabRowSelectedContent(false))
        assertEquals(Color(0xE6FFFFFF), tabRowSelectedContent(true))
        assertEquals(Color(0x8A000000), tabRowIdleContent(false))
        assertEquals(Color(0x8AFFFFFF), tabRowIdleContent(true))
        assertEquals(Color(0xE6000000), tabRowIndicator(false))
        assertEquals(Color(0xE6FFFFFF), tabRowIndicator(true))
        assertEquals(Color(0x0F000000), tabRowIndicatorTrack(false))
        assertEquals(Color(0x19FFFFFF), tabRowIndicatorTrack(true))
        assertEquals(Color(0xFFF0F1F2), tabRowBackground(false))
        assertEquals(Color(0xFF000000), tabRowBackground(true))
    }

    @Test
    fun contentColoursFollowTheSelection() {
        val colors =
            TabRowColors(
                backgroundColor = tabRowBackground(false),
                contentColor = tabRowIdleContent(false),
                selectedBackgroundColor = Color.Transparent,
                selectedContentColor = tabRowSelectedContent(false),
                indicatorColor = tabRowIndicator(false),
            )
        assertEquals(tabRowIdleContent(false), colors.contentColor(selected = false))
        assertEquals(tabRowSelectedContent(false), colors.contentColor(selected = true))
        // `indicatorTrackColor(caller)`: the colors object wins when it specifies a track colour,
        // otherwise the caller's fallback (the stock token) is used.
        assertEquals(tabRowIndicatorTrack(false), colors.indicatorTrackColor(tabRowIndicatorTrack(false)))
        assertEquals(Color.Red, colors.indicatorTrackColor(Color.Red))
        val withTrack =
            TabRowColors(
                backgroundColor = tabRowBackground(false),
                contentColor = tabRowIdleContent(false),
                selectedBackgroundColor = Color.Transparent,
                selectedContentColor = tabRowSelectedContent(false),
                indicatorColor = tabRowIndicator(false),
                indicatorTrackColor = tabRowIndicatorTrack(false),
            )
        assertEquals(tabRowIndicatorTrack(false), withTrack.indicatorTrackColor(Color.Red))
    }
}
