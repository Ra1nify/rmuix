// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * `COUISectionSeekBar` is the stock name for a seekbar with tick marks; this library expresses it with
 * [Slider]'s `showKeyPoints` / `keyPoints` / `magnetThreshold`, so these tests pin the tick geometry and
 * colours to that class instead of to a separate component.
 */
class SectionSeekBarValuesTest {
    @Test
    fun tickRadiusMatchesCouiSectionSeekBar() {
        // `coui_section_seekbar_tick_mark_radius`
        assertEquals(3.dp, SliderDefaults.TickMarkRadius)
    }

    @Test
    fun tickColoursMatchTheMarkSelectors() {
        // coui_seekbar_inactive_mark_selector -> ?attr/couiColorLabelTertiary
        assertEquals(Color(0x42000000), sliderKeyPoint(dark = false))
        assertEquals(Color(0x4DFFFFFF), sliderKeyPoint(dark = true))
        // coui_seekbar_active_mark_selector -> ?attr/couiColorLabelOnColor (`coui_color_label_on_color`)
        assertEquals(Color(0xFFFFFFFF), sliderKeyPointForeground(dark = false))
        assertEquals(Color(0xFFFFFFFF), sliderKeyPointForeground(dark = true))
    }

    @Test
    fun disabledTickColoursMatchTheDisableColours() {
        // coui_seekbar_inactive_mark_disable_color
        assertEquals(Color(0x42000000), sliderKeyPointDisabled(dark = false))
        assertEquals(Color(0x29FFFFFF), sliderKeyPointDisabled(dark = true))
        // coui_seekbar_active_mark_disable_color
        assertEquals(Color(0x8AFFFFFF), sliderKeyPointForegroundDisabled(dark = false))
        assertEquals(Color(0x29FFFFFF), sliderKeyPointForegroundDisabled(dark = true))
    }
}
