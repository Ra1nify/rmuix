// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class CodeInputDefaultsTest {
    @Test
    fun cellsMatchCouiCodeInputView() {
        // couiCodeInputCount / couiEnableSecurityInput attribute defaults
        assertEquals(6, CodeInputDefaults.Count)
        // coui_code_input_cell_width / coui_code_input_cell_height
        assertEquals(42.dp, CodeInputDefaults.CellWidth)
        assertEquals(46.dp, CodeInputDefaults.CellHeight)
        // ?attr/couiRoundCornerS -> coui_round_corner_s
        assertEquals(8.dp, CodeInputDefaults.CellCornerRadius)
        // coui_code_input_cell_stroke_width
        assertEquals(1.6.dp, CodeInputDefaults.CellStrokeWidth)
        // coui_code_input_cell_text_size (declared 30dp, used as a text size)
        assertEquals(30.sp, CodeInputDefaults.CellTextSize)
        // coui_code_input_cell_security_circle_radius
        assertEquals(5.dp, CodeInputDefaults.SecurityDotRadius)
    }

    @Test
    fun gapsMatchTheAdaptiveMarginDimens() {
        // coui_code_input_cell_margin_horizontal / _min_ / _max_
        assertEquals(4.dp, CodeInputDefaults.CellGap)
        assertEquals(2.dp, CodeInputDefaults.MinCellGap)
        assertEquals(8.dp, CodeInputDefaults.MaxCellGap)
        // coui_code_input_layout_margin_start
        assertEquals(16.dp, CodeInputDefaults.HorizontalMargin)
    }

    @Test
    fun animationConstantsMatchCouiCodeInputHelper() {
        // COUICodeInputHelper.INBOX_APPEAR_ANIMATOR_DURATION / INBOX_DELAY_ANIMATOR_DURATION
        assertEquals(100, CodeInputDefaults.FOCUS_APPEAR_MILLIS)
        assertEquals(33, CodeInputDefaults.FOCUS_DISAPPEAR_MILLIS)
        // NUMBER_APPEAR_ANIMATOR_DURATION / startNumberScale
        assertEquals(100, CodeInputDefaults.DIGIT_APPEAR_MILLIS)
        assertEquals(0.6f, CodeInputDefaults.DIGIT_APPEAR_SCALE)
        // INBOX_DELAY_ANIMATOR_DURATION / NUMBER_DELAY_ANIMATOR_DURATION
        assertEquals(33, CodeInputDefaults.FOCUS_APPEAR_DELAY_MILLIS)
        assertEquals(33, CodeInputDefaults.DIGIT_DISAPPEAR_DELAY_MILLIS)
    }

    @Test
    fun digitTransitionsMatchSetNumber() {
        // COUICodeInputView.CodeItemView.setNumber(): only empty<->digit transitions animate.
        assertEquals(CodeInputDigitTransition.NONE, codeInputDigitTransition(null, null))
        assertEquals(CodeInputDigitTransition.NONE, codeInputDigitTransition("1", "1"))
        assertEquals(CodeInputDigitTransition.APPEAR, codeInputDigitTransition(null, "1"))
        assertEquals(CodeInputDigitTransition.DISAPPEAR, codeInputDigitTransition("1", null))
        // Replacing one digit with another redraws without an animator.
        assertEquals(CodeInputDigitTransition.REPLACE, codeInputDigitTransition("1", "2"))
    }
}
