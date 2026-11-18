// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * `COUISectionSeekBar` parity checks: mark occlusion follows
 * `SectionProgressRenderer.drawMarkIfVisible()`, the snap timings follow
 * `SectionAnimationHandler` (100ms drag / 150ms release, PathInterpolator(0,0,0.25,1)), and the mark
 * colours are the stock `couiColorLabelOnColor` / `couiColorLabelTertiary` pairs.
 */
class SliderSectionMarkTest {
    @Test
    fun markIsHiddenWhileTheThumbCoversIt() {
        // Stock: draw only when `mark + r <= thumbLeft || mark - r >= thumbRight`, i.e. the circles do
        // not overlap. Thumb radius 8, mark radius 3 -> the mark is hidden within 11px of the centre.
        assertFalse(sliderSectionMarkIsVisible(markCenter = 100f, thumbCenter = 100f, thumbRadius = 8f, markRadius = 3f))
        assertFalse(sliderSectionMarkIsVisible(markCenter = 110.9f, thumbCenter = 100f, thumbRadius = 8f, markRadius = 3f))
        assertTrue(sliderSectionMarkIsVisible(markCenter = 111f, thumbCenter = 100f, thumbRadius = 8f, markRadius = 3f))
        assertTrue(sliderSectionMarkIsVisible(markCenter = 89f, thumbCenter = 100f, thumbRadius = 8f, markRadius = 3f))
        assertFalse(sliderSectionMarkIsVisible(markCenter = 89.1f, thumbCenter = 100f, thumbRadius = 8f, markRadius = 3f))
    }

    @Test
    fun occlusionScalesWithTheAnimatedThumbRadius() {
        // `curThumbRadius` grows while pressed (resting 4dp -> expanded 5dp in the capsule style), so the
        // hidden window must grow with it.
        val markGap = 8.5f
        assertTrue(sliderSectionMarkIsVisible(markGap, 0f, thumbRadius = 4f, markRadius = 3f))
        assertFalse(sliderSectionMarkIsVisible(markGap, 0f, thumbRadius = 6f, markRadius = 3f))
    }

    @Test
    fun sectionSnapUsesTheStockDurations() {
        val drag = sliderSectionSnapSpec(dragging = true)
        val release = sliderSectionSnapSpec(dragging = false)
        // SectionAnimationHandler.DRAG_SNAP_DURATION / RELEASE_SNAP_DURATION
        assertEquals(100, drag.durationMillis)
        assertEquals(150, release.durationMillis)
        assertEquals(SLIDER_SECTION_DRAG_SNAP_DURATION_MS, drag.durationMillis)
        assertEquals(SLIDER_SECTION_RELEASE_SNAP_DURATION_MS, release.durationMillis)
        assertEquals(CubicBezierEasing(0f, 0f, 0.25f, 1f), SliderSectionSnapEasing)
        // The mark radius token is `coui_section_seekbar_tick_mark_radius`.
        assertEquals(3.dp, SliderDefaults.TickMarkRadius)
    }

    @Test
    fun markColoursMatchTheStockSelectors() {
        // `coui_seekbar_active_mark_selector` -> couiColorLabelOnColor (#FFFFFF both themes).
        assertEquals(Color(0xFFFFFFFF), sliderKeyPointForeground(dark = false))
        assertEquals(Color(0xFFFFFFFF), sliderKeyPointForeground(dark = true))
        // `coui_seekbar_inactive_mark_selector` -> couiColorLabelTertiary (#42000000 / #4Dffffff).
        assertEquals(Color(0x42000000), sliderKeyPoint(dark = false))
        assertEquals(Color(0x4DFFFFFF), sliderKeyPoint(dark = true))
        // Disabled pairs: active #8AFFFFFF / #29FFFFFF, inactive #42000000 / #29FFFFFF.
        assertEquals(Color(0x8AFFFFFF), sliderKeyPointForegroundDisabled(dark = false))
        assertEquals(Color(0x29FFFFFF), sliderKeyPointForegroundDisabled(dark = true))
        assertEquals(Color(0x42000000), sliderKeyPointDisabled(dark = false))
        assertEquals(Color(0x29FFFFFF), sliderKeyPointDisabled(dark = true))
    }
}
