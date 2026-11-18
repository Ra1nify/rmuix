// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class TextFieldRenderingTest {
    @Test
    fun lineModePaddingsMatchTheStockDimens() {
        // coui_textinput_line_padding_top / _middle; the third one (coui_textinput_line_padding = 11dp)
        // is assigned to mLinePadding and never read, so it is documented but not exposed.
        assertEquals(10.dp, TextFieldDefaults.LinePaddingTop)
        assertEquals(5.dp, TextFieldDefaults.LinePaddingMiddle)
    }

    @Test
    fun lineModeUsesTheStockGrowDuration() {
        // COUIEditText.animateToShowBackground(): setDuration(250L) on the focus-line animator.
        assertEquals(250, TextFieldDefaults.LineGrowMillis)
    }

    @Test
    fun errorDefaultsMatchTheErrorHelperTimings() {
        // coui_edit_text_shake_amplitude = 5.33dp; the helper ramps over 217ms and shakes for 450ms.
        assertEquals(5.33.dp, TextFieldDefaults.ErrorShakeAmplitude)
        assertEquals(217, TextFieldDefaults.ErrorRampMillis)
        assertEquals(450, TextFieldDefaults.ErrorShakeMillis)
    }

    @Test
    fun rectangleModeMetricsComeFromTheEditTextResources() {
        assertEquals(10.dp, TextFieldDefaults.CornerRadius)
        assertEquals(62.dp, TextFieldDefaults.MinHeight)
        assertEquals(0.33.dp, TextFieldDefaults.StrokeWidth)
        // coui_textinput_focus_stroke_width
        assertEquals(1.dp, TextFieldDefaults.FocusStrokeWidth)
        // coui_textinput_label_cutout_padding
        assertEquals(4.dp, TextFieldDefaults.LabelCutoutPadding)
        assertEquals(16.sp, TextFieldStockTextSize)
        assertEquals(10f, TextFieldDefaults.LabelFontSizeFloating)
        assertEquals(16f, TextFieldDefaults.LabelFontSizeNormal)
    }

    @Test
    fun strokeAndLabelColorsMatchTheNeutralResources() {
        assertEquals(Color(0x1F000000), textFieldStroke(false))
        assertEquals(Color(0x33FFFFFF), textFieldStroke(true))
        assertEquals(Color(0x19000000), textFieldDisabledStroke(false))
        assertEquals(Color(0x19FFFFFF), textFieldDisabledStroke(true))
        assertEquals(Color(0x8A000000), textFieldLabel(false))
        assertEquals(Color(0x8AFFFFFF), textFieldLabel(true))
    }

    @Test
    fun unspecifiedBordersFallBackToTheStockHairline() {
        val colors = TextFieldColors(Color.White, Color.Black, Color.Red)
        assertEquals(Color.Unspecified, colors.restingBorderColor)
        assertEquals(Color.Unspecified, colors.disabledBorderColor)
        assertEquals(Color(0x1F000000), colors.restingBorderColor.takeOrElse { textFieldStroke(false) })
        assertEquals(Color(0x19FFFFFF), colors.disabledBorderColor.takeOrElse { textFieldDisabledStroke(true) })
    }

    @Test
    fun explicitBorderColorsArePreservedByTheFallback() {
        val colors = TextFieldColors(Color.White, Color.Black, Color.Red, Color.Green, Color.Blue)
        assertEquals(Color.Green, colors.restingBorderColor.takeOrElse { textFieldStroke(false) })
        assertEquals(Color.Blue, colors.disabledBorderColor.takeOrElse { textFieldDisabledStroke(false) })
    }
}
