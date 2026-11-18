// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins the geometry that `COUISwitch` reads from `COUISwitchStyle` (`barWidth`, `barHeight`,
 * `outerCircleWidth`, `circlePadding`) and from `coui_switch_padding` (0dp, so the measured size is
 * exactly 44 x 24dp).
 */
class SwitchDefaultsTest {
    @Test
    fun geometryMatchesTheSwitchStyle() {
        assertEquals(44.dp, SwitchDefaults.Width)
        assertEquals(24.dp, SwitchDefaults.Height)
        assertEquals(18.dp, SwitchDefaults.ThumbSize)
        assertEquals(3.dp, SwitchDefaults.ThumbPadding)
        assertEquals(20.dp, SwitchDefaults.Travel)
        assertEquals(3.dp, SwitchDefaults.ThumbOffsetUnchecked)
        assertEquals(23.dp, SwitchDefaults.ThumbOffsetChecked)
    }

    /**
     * `COUISwitch.initStartLoadingAnimator()` / `initStopLoadingAnimator()`: the thumb collapses while a
     * spinner fades and grows in, then spins. The glyph is `coui_icon_loading.xml` (a 270-degree open
     * circle stroked at 1.4 in a 24-unit viewport), and the stock bitmap it replaces is 40x40px at
     * xxhdpi, i.e. 13.33dp.
     */
    @Test
    fun loadingAnimationMatchesTheStockAnimators() {
        assertEquals(433, SwitchDefaults.LoadingShrinkMillis)
        assertEquals(550, SwitchDefaults.LoadingFadeMillis)
        assertEquals(800, SwitchDefaults.LoadingSpinMillis)
        assertEquals(0.5f, SwitchDefaults.LoadingScaleStart)
        // drawLoading() stretches switch_loading.png to mOuterCircleRectF (outerCircleWidth = 18dp).
        assertEquals(18.dp, SwitchDefaults.LoadingSize)
        assertEquals(
            "M21.5,12C21.5,6.753 17.247,2.5 12,2.5C6.753,2.5 2.5,6.753 2.5,12C2.5,17.247 6.753,21.5 12,21.5",
            SwitchDefaults.LOADING_PATH_DATA,
        )
    }
}
