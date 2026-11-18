// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectColor
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectSpring
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckboxFeedbackTest {
    @Test
    fun stateLayersHaveTheOfficialTwentyDpRadius() {
        // checkbox_ripple_bg_radius, read by COUIMaskRippleDrawable.getMaskRippleRadiusByType(ctx, 1)
        assertEquals(20.dp, CheckboxDefaults.StateLayerRadius)
        assertEquals(Offset(37f, 38f), checkboxStateLayerCenter(Size(75f, 77f)))
    }

    @Test
    fun uncheckedBoxGeometryComesFromTheOffVector() {
        // coui_btn_check_off_normal.xml: 3.8 -> 20.2 = 16.4dp box, r=3.2; the checked vector draws
        // 3 -> 21 = 18dp with r=4, so the two states are deliberately different shapes.
        assertEquals(16.4.dp, CheckboxDefaults.UncheckedBoxSize)
        assertEquals(3.2.dp, CheckboxDefaults.UncheckedCornerRadius)
        assertEquals(3.8.dp, CheckboxDefaults.UncheckedBoxTopLeft)
        assertEquals(18.dp, CheckboxDefaults.BoxSize)
        assertEquals(4.dp, CheckboxDefaults.CornerRadius)
        // coui_btn_part_check_on_normal.xml starts at y=3.666, i.e. the dash state is nudged down.
        assertEquals(3.666.dp, CheckboxDefaults.IndeterminateTopInset)
    }

    @Test
    fun stockLightAndDarkStateColorsFollowTheirThemeRoles() {
        val light = checkboxFeedbackColors(Color(0xFF0080FF), isDark = false)
        val dark = checkboxFeedbackColors(Color(0xFF1A8CFF), isDark = true)
        assertEquals(Color(0x14000000), light.hover)
        assertEquals(Color(0x26FFFFFF), dark.hover)
        assertEquals(Color(0x1F0080FF), light.focus)
        assertEquals(Color(0x331A8CFF), dark.focus)
        assertEquals(Color(0x1F000000), light.press)
        assertEquals(Color(0x33FFFFFF), dark.press)
        assertEquals(Color(0x14000000), light.android34Press)
        assertEquals(Color(0x26FFFFFF), dark.android34Press)
        assertEquals(Color(0x99B4B4B4), light.legacyPress)
        assertEquals(Color(0x80FFFFFF), dark.legacyPress)
    }

    @Test
    fun stateEffectUsesTheOfficialSpringAndAndroidGamma() {
        val spring = stockStateEffectSpring()
        assertEquals(1f, spring.dampingRatio)
        assertEquals(438.6491f, spring.stiffness, 0.001f)
        assertEquals(0.000075f, spring.visibilityThreshold)
        assertEquals(Color(0x1A808080), stockStateEffectColor(Color(0x33FFFFFF), 0.5f))
        assertEquals(Color.Transparent, stockStateEffectColor(Color(0x33FFFFFF), 0f))
    }

    @Test
    fun softwareFallbackExpandsFromAClampedTouchOriginToTheCenter() {
        val start = checkboxSoftwareRippleFrame(0f, null, Offset.Zero, Size(24f, 24f), 20f)
        val entered = checkboxSoftwareRippleFrame(225f, null, Offset.Zero, Size(24f, 24f), 20f)
        assertEquals(7.2f, start.radius, 0.00001f)
        assertEquals(12.8f, (start.center - Offset(12f, 12f)).getDistance(), 0.00001f)
        assertEquals(0f, start.opacity)
        assertEquals(20f, entered.radius)
        assertEquals(Offset(12f, 12f), entered.center)
        assertEquals(1f, entered.opacity)
    }

    @Test
    fun softwareFallbackKeepsQuickTapsVisibleBeforeFading() {
        fun opacity(
            elapsed: Float,
            releasedAt: Float,
        ) = checkboxSoftwareRippleFrame(elapsed, releasedAt, Offset(12f, 12f), Size(24f, 24f), 20f).opacity
        assertEquals(1f, opacity(75f, 20f))
        assertEquals(1f, opacity(225f, 20f))
        assertEquals(0.5f, opacity(300f, 20f), 0.00001f)
        assertEquals(0f, opacity(375f, 20f))
        assertEquals(0.5f, opacity(875f, 800f), 0.00001f)
        assertEquals(0f, opacity(950f, 800f))
        assertEquals(Color(0x10000000), checkboxSoftwareRippleColor(Color(0x1F000000), 0.5f))
        assertEquals(Color(0x1AFFFFFF), checkboxSoftwareRippleColor(Color(0x33FFFFFF), 0.5f))
    }

    @Test
    fun softwareFallbackCentersUnspecifiedKeyboardPresses() {
        val frame = checkboxSoftwareRippleFrame(0f, null, Offset.Unspecified, Size(24f, 24f), 20f)
        assertEquals(Offset(12f, 12f), frame.center)
    }
}
