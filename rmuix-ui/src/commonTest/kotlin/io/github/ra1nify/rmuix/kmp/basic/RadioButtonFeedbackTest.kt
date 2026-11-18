// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RadioButtonFeedbackTest {
    @Test
    fun standaloneBackgroundUsesItsOwnActivatedRoleInsteadOfCheckboxColors() {
        val light = radioButtonFeedbackColors(Color(0xFF0080FF), isDark = false)
        val dark = radioButtonFeedbackColors(Color(0xFF1A8CFF), isDark = true)
        assertEquals(Color(0x420080FF), light.checked)
        assertEquals(Color(0x421A8CFF), dark.checked)
        assertEquals(Color(0x7FD0D0D0), light.unchecked)
        assertEquals(Color(0x7F707070), dark.unchecked)
        assertEquals(Color(0x42FFFFFF), radioButtonFeedbackColors(Color.White, isDark = false).checked)
        assertEquals(20.dp, RadioButtonDefaults.StateLayerRadius)
        assertEquals(63, radioButtonFallbackRadiusPixels(20f * 3.125f))
    }

    @Test
    fun checkedHighlightMultipliesExistingAccentAlpha() {
        val colors = radioButtonFeedbackColors(Color(0x800080FF), isDark = false)
        assertEquals(Color(0x210080FF), colors.checked)
    }

    @Test
    fun focusWinsOverHoverRatherThanDrawingTwoLayers() {
        assertEquals(0.6f, radioButtonBackgroundOpacity(focused = true, hovered = true, windowFocused = true))
        assertEquals(0.2f, radioButtonBackgroundOpacity(focused = true, hovered = true, windowFocused = false))
        assertEquals(0.2f, radioButtonBackgroundOpacity(focused = false, hovered = true, windowFocused = true))
        assertEquals(0f, radioButtonBackgroundOpacity(focused = false, hovered = false, windowFocused = true))
    }

    @Test
    fun pressingDoesNotRestartAnExistingFocusedBackgroundAnimation() {
        val state = RadioButtonBackgroundState(checked = false, focused = true, hovered = false, pressed = true, windowFocused = true)
        assertNull(radioButtonBackgroundTarget(state))
        assertEquals(0.6f, radioButtonBackgroundTarget(state.copy(pressed = false)))
        assertEquals(0f, radioButtonBackgroundTarget(state.copy(focused = false)))
    }
}
