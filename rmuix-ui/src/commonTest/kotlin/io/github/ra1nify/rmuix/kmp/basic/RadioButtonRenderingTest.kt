// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class RadioButtonRenderingTest {
    @Test
    fun restingStatesMatchStockVectorLayers() {
        // coui_btn_radio_on.xml: fill ?attr/couiColorPrimary at r=10, white dot at r=6.
        // coui_btn_radio_off.xml: stroke ?attr/couiColorLabelTertiary at r=9.2, stroke 1.3.
        assertEquals(24.dp, RadioButtonDefaults.Size)
        assertEquals(10.dp, RadioButtonDefaults.SelectedOuterRadius)
        assertEquals(6.dp, RadioButtonDefaults.SelectedInnerRadius)
        assertEquals(9.2.dp, RadioButtonDefaults.UncheckedRingRadius)
        assertEquals(1.3.dp, RadioButtonDefaults.StrokeWidth)
        assertEquals(RadioButtonFrame(1f, 1f, 0f, 0f, 0f), radioButtonFrame(selected = false, progress = 0f))
        assertEquals(RadioButtonFrame(1f, 0f, 1f, 1f, 1f), radioButtonFrame(selected = true, progress = 1f))
    }

    @Test
    fun stockVectorPulsePeaksAt150Milliseconds() {
        assertEquals(1.08f, radioButtonFrame(selected = true, progress = 0.5f).outerScale, 0.000001f)
        assertEquals(1.08f, radioButtonFrame(selected = false, progress = 0.5f).outerScale, 0.000001f)
    }

    @Test
    fun deselectionFillDisappearsBeforeTheRingFinishesAppearing() {
        val frameAt200 = radioButtonFrame(selected = false, progress = 1f - 200f / 300f)
        val frameAt267 = radioButtonFrame(selected = false, progress = 1f - 267f / 300f)

        assertEquals(0f, frameAt200.fillAlpha, 0.000001f)
        assertEquals(0f, frameAt200.innerAlpha, 0.000001f)
        assertEquals(1f, frameAt267.strokeAlpha, 0.000001f)
        assertEquals(0f, frameAt267.innerScale, 0.000001f)
    }

    @Test
    fun colorsRetainTheProvidedDisabledOpacity() {
        val colors =
            RadioButtonColors(
                selectedColor = Color.Blue,
                disabledSelectedColor = Color.Blue.copy(alpha = 0.3f),
                uncheckedStrokeColor = Color(0x42000000),
                disabledUncheckedStrokeColor = Color(0x14000000),
                selectedInnerColor = Color.White,
                disabledSelectedInnerColor = Color(0x29FFFFFF),
            )

        assertEquals(Color.Blue.copy(alpha = 0.3f), colors.selectedColor(enabled = false))
        assertEquals(Color(0x29FFFFFF), colors.selectedInnerColor(enabled = false))
    }
}
