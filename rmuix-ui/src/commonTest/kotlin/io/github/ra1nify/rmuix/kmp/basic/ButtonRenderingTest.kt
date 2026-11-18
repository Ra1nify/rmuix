// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ButtonRenderingTest {
    @Test
    fun largeTierDimensionsDoNotMixSmallAndLargeDefaults() {
        assertEquals(132.dp, ButtonDefaults.MinWidth)
        assertEquals(44.dp, ButtonDefaults.MinHeight)
        assertEquals((-1).dp, ButtonDefaults.CornerRadius)
        assertEquals(12.dp, ButtonDefaults.InsideMargin.calculateLeftPadding(LayoutDirection.Ltr))
        assertEquals(8.dp, ButtonDefaults.InsideMargin.calculateTopPadding())
        assertEquals(22f, buttonResolvedRadius(44f, -1f))
        assertEquals(40f, buttonResolvedRadius(80f, -1f))
        assertEquals(16f, buttonResolvedRadius(80f, 16f))
    }

    @Test
    fun secondaryUsesTheOfficialIntegerCompositeOverElevated() {
        assertEquals(Color(0xFFEBEBEB), buttonSecondaryContainer(dark = false))
        assertEquals(Color(0xFF343434), buttonSecondaryContainer(dark = true))
        assertEquals(Color(0x14000000), buttonContainer8(dark = false))
        assertEquals(Color(0x1AFFFFFF), buttonContainer8(dark = true))
        assertEquals(Color.Transparent, buttonCompositeColor(Color.Transparent, Color.Transparent))
    }

    @Test
    fun disabledTextUsesItsOwnSelectorsRatherThanGenericThemeOpacity() {
        assertEquals(Color(0x8AFFFFFF), buttonDisabledPrimaryLabel(dark = false))
        assertEquals(Color(0x28FFFFFF), buttonDisabledPrimaryLabel(dark = true))
        assertEquals(Color(0x42000000), buttonDisabledSecondaryLabel(dark = false))
        assertEquals(Color(0x28FFFFFF), buttonDisabledSecondaryLabel(dark = true))
        assertEquals(Color(0x4DFFFFFF), buttonDisabledTextLabel(dark = true))
        assertEquals(Color(0x665C9DFF), buttonFocusOutlineColor(dark = true))
    }

    @Test
    fun pressureUsesAreaAndAspectRatherThanTheMaximumScaleForEverySize() {
        assertEquals(1f, buttonPressScale(0f, 44f))
        assertEquals(0.85f, buttonPressScale(20f, 20f), 0.00001f)
        assertEquals(0.97f, buttonPressScale(150f, 150f), 0.00001f)
        assertEquals(0.985f, buttonPressScale(1500f, 100f), 0.00001f)
        assertEquals(buttonPressScale(132f, 44f), buttonPressScale(44f, 132f), 0.00001f)
        assertTrue(buttonPressScale(132f, 44f) < 0.96f)
    }

    @Test
    fun candyAndJellyKeepDifferentPressAndReleasePhysics() {
        val candyPress = buttonScaleSpring(true, 132f, 44f)
        val jellyPress = buttonScaleSpring(true, 44f, 44f)
        val candyRelease = buttonScaleSpring(false, 132f, 44f)
        val jellyRelease = buttonScaleSpring(false, 44f, 44f)
        assertEquals(438.6491f, candyPress.stiffness, 0.001f)
        assertEquals(631.6547f, jellyPress.stiffness, 0.001f)
        assertEquals(157.9137f, candyRelease.stiffness, 0.001f)
        assertEquals(1f, candyPress.dampingRatio)
        assertEquals(0.5f, candyRelease.dampingRatio)
        assertEquals(0.4f, jellyRelease.dampingRatio)
        assertEquals(0.00075f, candyPress.visibilityThreshold)
        assertEquals(0.000075f, candyRelease.visibilityThreshold)
    }

    @Test
    fun existingFourColorConstructorsStillWorkWithoutExplicitFeedbackOverrides() {
        val colors = ButtonColors(Color.Blue, Color.Gray, Color.White, Color.Gray)
        val textColors = TextButtonColors(Color.Transparent, Color.Transparent, Color.Blue, Color.Gray)
        assertEquals(Color.Unspecified, colors.pressColor)
        assertEquals(Color.Unspecified, textColors.focusOutlineColor)
    }
}
