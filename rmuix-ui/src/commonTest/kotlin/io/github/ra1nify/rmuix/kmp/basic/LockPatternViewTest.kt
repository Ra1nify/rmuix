// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * `COUILockPatternView` layout: `mSquareWidth/Height = (size - paddings) / 3`, the `mHitFactor` 0.6 hit boxes
 * and the `detectAndAddHit()` middle-cell rule.
 */
class LockPatternViewTest {
    @Test
    fun defaultsMatchTheStockResources() {
        // coui_lock_pattern_view_width / _height / _horizontal_padding / _vertical_padding
        assertEquals(312.dp, LockPatternDefaults.Width)
        assertEquals(280.dp, LockPatternDefaults.Height)
        assertEquals(21.dp, LockPatternDefaults.HorizontalPadding)
        assertEquals(5.dp, LockPatternDefaults.VerticalPadding)
        // lock_pattern_dot_size / lock_pattern_dot_line_width
        assertEquals(10.dp, LockPatternDefaults.DotSize)
        assertEquals(2.dp, LockPatternDefaults.PathWidth)
        // COUILockPatternView.mHitFactor
        assertEquals(0.6f, LockPatternDefaults.HitFactor)
        // coui_lock_pattern_inner_circle.png / outer_circle.png (90px at xxhdpi)
        assertEquals(30.dp, LockPatternDefaults.InnerGlowDiameter)
        assertEquals(9.dp, LockPatternDefaults.OuterDotDiameter)
        // startOuterAnimation(): 1f -> 7f, 460ms; startInnerAnimation(): 230ms; startWrongAnimator(): 1000ms
        assertEquals(7f, LockPatternDefaults.OuterCircleMaxScale)
        assertEquals(230, LockPatternDefaults.InnerAnimationMillis)
        assertEquals(460, LockPatternDefaults.OuterAnimationMillis)
        assertEquals(1000, LockPatternDefaults.WrongAnimationMillis)
        // color_lock_pattern_view_max_translate_y (dead for rendering, see KDoc)
        assertEquals(258.dp, LockPatternDefaults.MaxTranslateY)
        // MILLIS_PER_CIRCLE_ANIMATING
        assertEquals(700, LockPatternDefaults.AnimateCellMillis)
    }

    @Test
    fun animateReplayFollowsTheStockTiming() {
        // 3-cell pattern: (size + 1) * 700 = 2800ms cycle.
        val atStart = lockPatternAnimateFrame(patternSize = 3, elapsedMillis = 0L)
        assertEquals(0, atStart.drawnCellCount)
        assertEquals(0f, atStart.fractionToNextCell, 0.001f)
        // Halfway through the first cell: nothing lit yet, the line is half-way towards cell 1.
        val halfway = lockPatternAnimateFrame(patternSize = 3, elapsedMillis = 350L)
        assertEquals(0, halfway.drawnCellCount)
        assertEquals(0.5f, halfway.fractionToNextCell, 0.001f)
        // One full cell: cell 0 is lit, the line starts towards cell 1.
        val one = lockPatternAnimateFrame(patternSize = 3, elapsedMillis = 700L)
        assertEquals(1, one.drawnCellCount)
        assertEquals(0f, one.fractionToNextCell, 0.001f)
        // 300ms into the second cell: the line is 300/700 towards cell 1.
        val second = lockPatternAnimateFrame(patternSize = 3, elapsedMillis = 1000L)
        assertEquals(1, second.drawnCellCount)
        assertEquals(300f / 700f, second.fractionToNextCell, 0.001f)
        // The full pattern holds for one extra tick (2100..2799ms).
        assertEquals(3, lockPatternAnimateFrame(patternSize = 3, elapsedMillis = 2100L).drawnCellCount)
        assertEquals(3, lockPatternAnimateFrame(patternSize = 3, elapsedMillis = 2799L).drawnCellCount)
        // ...then the loop restarts.
        val looped = lockPatternAnimateFrame(patternSize = 3, elapsedMillis = 2800L)
        assertEquals(0, looped.drawnCellCount)
        assertEquals(0f, looped.fractionToNextCell, 0.001f)
        // 200ms into the looped cycle.
        val looped200 = lockPatternAnimateFrame(patternSize = 3, elapsedMillis = 3000L)
        assertEquals(0, looped200.drawnCellCount)
        assertEquals(200f / 700f, looped200.fractionToNextCell, 0.001f)
    }

    @Test
    fun lineEndAnimationCatchesUpToTheCellCentre() {
        // startLineEndAnimation(): 100ms, from the finger position to the cell centre.
        assertEquals(100, LockPatternDefaults.LineEndAnimationMillis)
        val start = Offset(10f, 20f)
        val end = Offset(110f, 220f)
        assertEquals(start, lockPatternLineEndPosition(start, end, 0f))
        assertEquals(Offset(60f, 120f), lockPatternLineEndPosition(start, end, 0.5f))
        assertEquals(end, lockPatternLineEndPosition(start, end, 1f))
        // The animator never overshoots, even if a frame reports progress outside 0..1.
        assertEquals(start, lockPatternLineEndPosition(start, end, -1f))
        assertEquals(end, lockPatternLineEndPosition(start, end, 2f))
    }

    @Test
    fun coloursMatchTheStockTokens() {
        // coui_lock_pattern_dot_color / _dark_color
        assertEquals(Color(0x4D000000), lockPatternRegular(dark = false))
        assertEquals(Color(0x66FFFFFF), lockPatternRegular(dark = true))
        // coui_lock_pattern_path_color / _dark_color
        assertEquals(Color(0xFF000000), lockPatternPath(dark = false))
        assertEquals(Color(0xFFFFFFFF), lockPatternPath(dark = true))
        // coui_lock_pattern_success_color / _dark_color
        assertEquals(Color(0xFF000000), lockPatternSuccess(dark = false))
        assertEquals(Color(0xFFFFFFFF), lockPatternSuccess(dark = true))
    }

    @Test
    fun layoutMatchesTheStockMeasureMath() {
        // Default 312dp x 280dp at density 2 -> 624 x 560, paddings 42 / 10.
        val metrics = lockPatternMetrics(width = 624f, height = 560f, density = 2f)
        // (624 - 84) / 3 = 180; (560 - 20) / 3 = 180
        assertEquals(180f, metrics.squareWidth)
        assertEquals(180f, metrics.squareHeight)
        assertEquals(listOf(132f, 312f, 492f), metrics.columnCenters)
        assertEquals(listOf(100f, 280f, 460f), metrics.rowCenters)
        assertEquals(Offset(132f, 100f), metrics.centerOf(0))
        assertEquals(Offset(492f, 460f), metrics.centerOf(8))
    }

    @Test
    fun hitTestingUsesSixtyPercentOfTheSquare() {
        val metrics = lockPatternMetrics(width = 624f, height = 560f, density = 2f)
        // hit box = 180 * 0.6 = 108, inset = (180 - 108) / 2 = 36
        assertEquals(0, metrics.cellAt(Offset(132f, 100f)))
        assertEquals(0, metrics.cellAt(Offset(80f, 50f))) // 42+36 = 78 .. 186
        assertEquals(8, metrics.cellAt(Offset(492f, 460f)))
        assertEquals(4, metrics.cellAt(Offset(312f, 280f)))
        assertNull(metrics.cellAt(Offset(60f, 48f))) // left of the first hit box
        assertNull(metrics.cellAt(Offset(222f, 100f))) // between the first two columns
    }

    @Test
    fun skippedCellsFollowDetectAndAddHit() {
        val metrics = lockPatternMetrics(width = 624f, height = 560f, density = 2f)
        // row jump by two adds the middle column
        assertEquals(1, metrics.skippedCellBetween(0, 2))
        assertEquals(1, metrics.skippedCellBetween(2, 0))
        // column jump by two adds the middle row
        assertEquals(3, metrics.skippedCellBetween(0, 6))
        // diagonal jump by two adds the centre
        assertEquals(4, metrics.skippedCellBetween(0, 8))
        assertEquals(4, metrics.skippedCellBetween(1, 7))
        // a jump that also moves by one in the other axis does not add a cell
        assertNull(metrics.skippedCellBetween(0, 5))
    }
}
