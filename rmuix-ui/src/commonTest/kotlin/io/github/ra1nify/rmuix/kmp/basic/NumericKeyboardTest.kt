// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * `COUINumericKeyboard` layout: `mCellWidth = mCellHeight = coui_numeric_keyboard_view_size`, the two
 * spacing formulas from `onSizeChanged()` and the `mKeyboardNumbers = {1..9, -1, 0, -1}` mapping.
 */
class NumericKeyboardTest {
    @Test
    fun defaultsMatchTheStockResources() {
        // coui_numeric_keyboard_view_width / _height / _size
        assertEquals(248.dp, NumericKeyboardDefaults.Width)
        assertEquals(326.dp, NumericKeyboardDefaults.Height)
        assertEquals(68.dp, NumericKeyboardDefaults.CellSize)
        // number_keyboard_number_size / coui_numeric_keyboard_number_offset_y
        assertEquals(32.dp, NumericKeyboardDefaults.NumberTextSize)
        assertEquals(1.dp, NumericKeyboardDefaults.NumberOffsetY)
        // coui_number_keyboard_finish_text_size
        assertEquals(16.dp, NumericKeyboardDefaults.FinishTextSize)
        // coui_number_keyboard_delete.xml is a 24dp vector
        assertEquals(24.dp, NumericKeyboardDefaults.DeleteIconSize)
        // Widget.COUI.COUINumericKeyboard: couiCircleMaxAlpha 0.12
        assertEquals(0.12f, NumericKeyboardDefaults.CircleMaxAlpha)
        // CELL_COLUMN_COUNT / CELL_ROW_COUNT / KEYCODE_0_COLUMN / KEYCODE_0_ROW
        assertEquals(3, NumericKeyboardDefaults.ColumnCount)
        assertEquals(4, NumericKeyboardDefaults.RowCount)
        assertEquals(1, NumericKeyboardDefaults.ZeroColumn)
        assertEquals(3, NumericKeyboardDefaults.ZeroRow)
        // ALPHA_DELAY 166 / ALPHA_DURATION 167 / FADE_ANIMATOR_TIME 160
        assertEquals(160, NumericKeyboardDefaults.PressDurationMillis)
        // FONT_VARIATION_DEFAULT 550 + font_variation_default_plus 200 = 'wght' 750
        assertEquals(FontWeight(750), NumericKeyboardDefaults.NumberFontWeight)
    }

    @Test
    fun numberColourFollowsCouiColorLabelPrimary() {
        assertEquals(Color(0xE6000000), numericKeyboardNumberColor(dark = false))
        assertEquals(Color(0xE6FFFFFF), numericKeyboardNumberColor(dark = true))
    }

    @Test
    fun layoutUsesTheStockSpacingFormulas() {
        // Default 248dp x 326dp at density 2 -> 496 x 652 px, cell 136 px.
        val metrics = numericKeyboardMetrics(width = 496f, height = 652f, density = 2f)
        assertEquals(136f, metrics.cellSize)
        // mHorizontalSpacing = (496 - 3 * 136) / 2 = 44
        assertEquals(44f, metrics.horizontalSpacing)
        // mVerticalSpacing = (652 - 4 * 136) / 3 = 36
        assertEquals(36f, metrics.verticalSpacing)
        assertEquals(listOf(68f, 248f, 428f), metrics.columnCenters)
        assertEquals(listOf(68f, 240f, 412f, 584f), metrics.rowCenters)
    }

    @Test
    fun narrowSizesClampTheSpacingToZero() {
        val metrics = numericKeyboardMetrics(width = 300f, height = 300f, density = 2f)
        assertEquals(0f, metrics.horizontalSpacing)
        assertEquals(0f, metrics.verticalSpacing)
        assertEquals(listOf(68f, 204f, 340f), metrics.columnCenters)
    }

    @Test
    fun digitMappingMatchesTheStockKeyboardNumbers() {
        // mKeyboardNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, -1, 0, -1}
        val metrics = numericKeyboardMetrics(width = 496f, height = 652f, density = 2f)
        assertEquals(1, metrics.digitAt(0))
        assertEquals(9, metrics.digitAt(8))
        assertNull(metrics.digitAt(9)) // left side key
        assertEquals(0, metrics.digitAt(10))
        assertNull(metrics.digitAt(11)) // right side key
    }

    @Test
    fun hitTestingMapsTouchesToRowMajorCells() {
        val metrics = numericKeyboardMetrics(width = 496f, height = 652f, density = 2f)
        assertEquals(0, metrics.cellIndexAt(Offset(68f, 68f))) // key 1
        assertEquals(10, metrics.cellIndexAt(Offset(248f, 584f))) // key 0
        assertEquals(11, metrics.cellIndexAt(Offset(428f, 584f))) // delete key
        assertEquals(8, metrics.cellIndexAt(Offset(428f, 412f))) // key 9
        assertEquals(-1, metrics.cellIndexAt(Offset(68f, 700f))) // below the last row
        assertEquals(-1, metrics.cellIndexAt(Offset(-5f, 68f))) // left of the first column
    }
}
