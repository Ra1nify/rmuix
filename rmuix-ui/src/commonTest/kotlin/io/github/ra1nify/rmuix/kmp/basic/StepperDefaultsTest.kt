// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class StepperDefaultsTest {
    @Test
    fun pressTargetScaleComesFromThePressCalcAreaMapping() {
        // 32dp button: scaleRange 0.85..0.97 over the 20dp..150dp area mapping.
        assertEquals(0.876604f, StepperDefaults.PressTargetScale, 0.0005f)
    }

    @Test
    fun metricsMatchTheStepperLayout() {
        // stepper_button_size
        assertEquals(32.dp, StepperDefaults.ButtonSize)
        // coui_stepper_view.xml: android:minWidth="44dp" on the indicator
        assertEquals(44.dp, StepperDefaults.ValueMinWidth)
        // coui_stepper_view.xml: layout_marginStart/End="12dp" around the indicator
        assertEquals(12.dp, StepperDefaults.ValueHorizontalPadding)
        // ?attr/couiTextAppearanceHeadline5
        assertEquals(18.sp, StepperDefaults.ValueTextSize)
        assertEquals(FontWeight.Medium, StepperDefaults.ValueFontWeight)
    }

    @Test
    fun signVectorsMatchTheIconGeometry() {
        // ic_minus_sign: M9.199,16H22.799 -> 13.6dp round-cap line
        assertEquals(13.6.dp, StepperStockMinusLength)
        // ic_plus_sign: 8.398..23.598 and 8.4..23.6 -> 15.2dp arms with rounded ends
        assertEquals(15.2.dp, StepperStockPlusLength)
        // both vectors use strokeWidth 1.6
        assertEquals(1.6.dp, StepperStockGlyphStroke)
    }

    @Test
    fun buttonAndSignColoursFollowTheStateEffectTokens() {
        // couiColorContainer8
        assertEquals(Color(0x14000000), stepperButtonBackground(false))
        assertEquals(Color(0x1AFFFFFF), stepperButtonBackground(true))
        // couiColorPrimaryNeutral
        assertEquals(Color(0xE6000000), stepperIconColor(false))
        assertEquals(Color(0xE6FFFFFF), stepperIconColor(true))
        // couiColorDivider (disabled signs and disabled indicator)
        assertEquals(Color(0x1F000000), stepperDisabledColor(false))
        assertEquals(Color(0x33FFFFFF), stepperDisabledColor(true))
        // default_focus_stroke_radius (the 6dp stroke is clipped to a visible 3dp ring)
        assertEquals(3.dp, StepperStockFocusRingWidth)
        assertEquals(6.dp, StepperStockFocusRingStroke)
        // couiColorFocusOutline -> coui_color_focus_outline_blue
        assertEquals(Color(0x660080FF), stepperFocusOutlineColor(false))
        assertEquals(Color(0x665C9DFF), stepperFocusOutlineColor(true))
    }
}
