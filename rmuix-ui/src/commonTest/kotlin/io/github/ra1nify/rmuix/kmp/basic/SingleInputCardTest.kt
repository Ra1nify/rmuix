// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * `COUICardSingleInputView` tokens + the stock count logic from `COUIInputView.handleWithCount()`.
 */
class SingleInputCardTest {
    @Test
    fun defaultsMatchTheStockResources() {
        // coui_round_corner_xl / coui_preference_input_margin_horizontal(_tiny)
        assertEquals(20.dp, SingleInputCardDefaults.CardCornerRadius)
        assertEquals(16.dp, SingleInputCardDefaults.CardHorizontalMargin)
        assertEquals(12.dp, SingleInputCardDefaults.CardHorizontalMarginTiny)
        // coui_single_input_card_view.xml paddings
        assertEquals(32.dp, SingleInputCardDefaults.ContentPaddingHorizontal)
        // COUIInputTitleStyle (couiTextAppearanceHeadline6) / coui_inputView_title_minheight
        assertEquals(16.sp, SingleInputCardDefaults.TitleTextSize)
        assertEquals(22.dp, SingleInputCardDefaults.TitleMinHeight)
        // coui_input_preference_single_title_padding_top / coui_input_preference_title_padding_bottom
        assertEquals(12.dp, SingleInputCardDefaults.TitlePaddingTop)
        assertEquals(4.dp, SingleInputCardDefaults.TitlePaddingBottom)
        // Widget.COUI.Input -> couiTextBodyL
        assertEquals(15.sp, SingleInputCardDefaults.TextSize)
        // coui_input_edit_text_no_title_padding_top / _bottom / _single_title_padding_bottom
        assertEquals(15.dp, SingleInputCardDefaults.EditTextPaddingVertical)
        assertEquals(12.dp, SingleInputCardDefaults.EditTextWithTitlePaddingBottom)
        // coui_input_button_layout_margin_start / ic_edit_text_delete / coui_inputview_password_size
        assertEquals(8.dp, SingleInputCardDefaults.ButtonMarginStart)
        assertEquals(22.dp, SingleInputCardDefaults.DeleteIconSize)
        assertEquals(28.dp, SingleInputCardDefaults.PasswordIconSize)
        assertEquals(2.dp, SingleInputCardDefaults.PasswordMarginStart)
        // couiTextCaption / coui_card_single_input_padding_top
        assertEquals(10.sp, SingleInputCardDefaults.CaptionTextSize)
        assertEquals(4.dp, SingleInputCardDefaults.ErrorPaddingTop)
        assertEquals(32.dp, SingleInputCardDefaults.ErrorPaddingHorizontal)
        // COUIErrorEditTextHelper.DURATION_HINT_ANIMATOR / ShakeInterpolator.TOTAL_DURATION / amplitude
        assertEquals(217, SingleInputCardDefaults.ErrorHintColorMillis)
        assertEquals(450, SingleInputCardDefaults.ErrorShakeMillis)
        assertEquals(5.33.dp, SingleInputCardDefaults.ErrorShakeAmplitude)
        // COUIInputView.showErrorMsgAnim() 217ms / hideErrorMsgAnim() 283ms
        assertEquals(217, SingleInputCardDefaults.ErrorTextAppearMillis)
        assertEquals(283, SingleInputCardDefaults.ErrorTextDisappearMillis)
    }

    @Test
    fun errorShakeFollowsShakeInterpolatorKeyframes() {
        // COUIEaseInterpolator cubic-bezier(0.33, 0, 0.67, 1): symmetric, so 0.5 maps to 0.5.
        assertEquals(0f, singleInputEaseInterpolation(0f), 0.001f)
        assertEquals(0.5f, singleInputEaseInterpolation(0.5f), 0.001f)
        assertEquals(1f, singleInputEaseInterpolation(1f), 0.001f)
        // ShakeInterpolator: offsets 0, -1, 0.5, -0.5, 0 over 83 / 133 / 117 / 117ms.
        assertEquals(0f, singleInputErrorShakeFactor(0f), 0.001f)
        assertEquals(-0.5f, singleInputErrorShakeFactor(41.5f), 0.001f)
        assertEquals(-1f, singleInputErrorShakeFactor(83f), 0.001f)
        assertEquals(0.5f, singleInputErrorShakeFactor(216f), 0.001f)
        assertEquals(-0.5f, singleInputErrorShakeFactor(333f), 0.001f)
        assertEquals(0f, singleInputErrorShakeFactor(450f), 0.001f)
        assertEquals(0f, singleInputErrorShakeFactor(500f), 0.001f)
        assertEquals(450, SINGLE_INPUT_ERROR_SHAKE_MILLIS)
    }

    @Test
    fun coloursMatchTheStockTokens() {
        // ?attr/couiColorPrimaryNeutral
        assertEquals(Color(0xE6000000), singleInputPrimaryNeutral(dark = false))
        assertEquals(Color(0xE6FFFFFF), singleInputPrimaryNeutral(dark = true))
        // ?attr/couiColorHintNeutral
        assertEquals(Color(0x42000000), singleInputHintNeutral(dark = false))
        assertEquals(Color(0x4DFFFFFF), singleInputHintNeutral(dark = true))
        // coui_color_error / _dark
        assertEquals(Color(0xFFDB382C), singleInputError(dark = false))
        assertEquals(Color(0xFFFF6C61), singleInputError(dark = true))
        // ?attr/couiColorLabelSecondary
        assertEquals(Color(0x8A000000), singleInputLabelSecondary(dark = false))
        assertEquals(Color(0x8AFFFFFF), singleInputLabelSecondary(dark = true))
        // ?attr/couiColorDisabledNeutral
        assertEquals(Color(0x42000000), singleInputDisabledNeutral(dark = false))
        assertEquals(Color(0x4DFFFFFF), singleInputDisabledNeutral(dark = true))
    }

    @Test
    fun countTextFollowsLengthAndClampsToTheLimit() {
        // COUIInputView.handleWithCount(): length < mMaxCount -> "length/max", else "max/max".
        assertEquals("0/10", singleInputCountText(0, 10))
        assertEquals("9/10", singleInputCountText(9, 10))
        assertEquals("10/10", singleInputCountText(10, 10))
        assertEquals("10/10", singleInputCountText(12, 10))
        assertFalse(singleInputCountExceeded(9, 10))
        assertTrue(singleInputCountExceeded(10, 10))
        assertTrue(singleInputCountExceeded(12, 10))
        // maxCount == 0 disables the counter, so it never turns into the error colour.
        assertFalse(singleInputCountExceeded(5, 0))
    }
}
