// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * `COUITouchSearchView` metrics (`coui_touchsearch_*`) and its key hit-strip math.
 */
class AlphabetIndexBarTest {
    @Test
    fun defaultsMatchTheStockResources() {
        // R.array.special_touchsearch_keys: A-Z plus the trailing "#".
        assertEquals(27, AlphabetIndexBarDefaults.Letters.size)
        assertEquals("A", AlphabetIndexBarDefaults.Letters.first())
        assertEquals("#", AlphabetIndexBarDefaults.Letters.last())
        // coui_touchsearch_background_width
        assertEquals(39.dp, AlphabetIndexBarDefaults.BackgroundWidth)
        // coui_touchsearch_each_item_height / _item_spacing
        assertEquals(14.dp, AlphabetIndexBarDefaults.KeyHeight)
        assertEquals(2.dp, AlphabetIndexBarDefaults.KeySpacing)
        // coui_touchsearch_key_textsize / _key_dot_textsize / coui_touchsearch_dot
        assertEquals(12.sp, AlphabetIndexBarDefaults.KeyTextSize)
        assertEquals(20.sp, AlphabetIndexBarDefaults.DotTextSize)
        assertEquals("\u00b7", AlphabetIndexBarDefaults.DotText)
        // coui_touchsearch_padding_top / _padding_bottom
        assertEquals(16.dp, AlphabetIndexBarDefaults.PaddingVertical)
        // coui_touchsearch_default_height / _min_height / _limit1_height (26 * 16dp)
        assertEquals(432.dp, AlphabetIndexBarDefaults.DefaultHeight)
        assertEquals(296.dp, AlphabetIndexBarDefaults.MinHeight)
        assertEquals(416.dp, AlphabetIndexBarDefaults.LimitAllLettersHeight)
        // coui_touchsearch_popup_first_default_width/_height, popupwin_first_textsize, offset, margin
        assertEquals(48.dp, AlphabetIndexBarDefaults.PopupSize)
        assertEquals(20.sp, AlphabetIndexBarDefaults.PopupTextSize)
        assertEquals(23.dp, AlphabetIndexBarDefaults.PopupOffset)
        assertEquals(4.dp, AlphabetIndexBarDefaults.PopupMargin)
        // POPUP_WINDOW_APPEAR_DURATION / _DISAPPEAR_DURATION
        assertEquals(350, AlphabetIndexBarDefaults.PopupAppearMillis)
        assertEquals(300, AlphabetIndexBarDefaults.PopupDisappearMillis)
    }

    @Test
    fun coloursMatchTheStockTokenS() {
        // coui_touchsearchview_key_text_color_unpressed
        assertEquals(Color(0x8C000000), alphabetIndexLetter(dark = false))
        assertEquals(Color(0x8CFFFFFF), alphabetIndexLetter(dark = true))
        // coui_touchsearchview_dot_color: ?attr/couiColorControls
        assertEquals(Color(0x29000000), alphabetIndexDot(dark = false))
        assertEquals(Color(0x40FFFFFF), alphabetIndexDot(dark = true))
        // coui_touch_search_popup_bg: ?attr/couiColorBackgroundTop
        assertEquals(Color(0xFFFFFFFF), alphabetIndexPopupBackground(dark = false))
        assertEquals(Color(0xFF333333), alphabetIndexPopupBackground(dark = true))
        // setPopupWindowTextColor default: ?attr/couiColorPrimaryNeutral
        assertEquals(Color(0xE6000000), alphabetIndexPopupText(dark = false))
        assertEquals(Color(0xE6FFFFFF), alphabetIndexPopupText(dark = true))
    }

    @Test
    fun keyPitchIsSixteenDp() {
        assertEquals(16f, alphabetIndexBarPitchPx(1f))
        assertEquals(32f, alphabetIndexBarPitchPx(2f))
    }

    @Test
    fun limitLevelsMatchTheStockHeightRangeSpec() {
        // letterCount = 27 ("A".."Z" + "#"), suffixAdjust = 1 (the trailing "#" is not counted),
        // pitch = 16dp at density 1; the computed LetterLimitLevelInfo.limitHeight values.
        val levels = alphabetIndexBarLimitLevels(letterCount = 27, pitchPx = 16f)
        assertEquals(listOf(0, 1, 2, 3, 4), levels.map { it.dotLevel })
        assertEquals(listOf(432f, 416f, 288f, 224f, 192f), levels.map { it.limitHeightPx })
        assertEquals(listOf(0, 12, 8, 6, 5), levels.map { it.dotSize })
        assertEquals(listOf(27, 26, 18, 14, 12), levels.map { it.showLetterSize })
        assertEquals(listOf(-1, 1, 1, 1, 0), levels.map { it.replenishDotValueTheIndex })
    }

    private fun layoutAt(
        heightDp: Float,
        letters: List<String> = AlphabetIndexBarDefaults.Letters,
    ): AlphabetIndexBarLayout = alphabetIndexBarLayout(
        heightPx = heightDp,
        paddingVerticalPx = 0f,
        letters = letters,
        pitchPx = 16f,
        cellHeightPx = 14f,
        itemSpacingPx = 2f,
    )

    @Test
    fun tallStripDrawsEveryLetterAtTheStockPitch() {
        val layout = layoutAt(heightDp = 432f)
        assertFalse(layout.heightNotEnough)
        assertEquals(0, layout.dotLevel)
        assertEquals(27, layout.keys.size)
        assertTrue(layout.keys.none { it.isDot })
        assertEquals(AlphabetIndexBarDefaults.Letters, layout.keys.map { it.text })
    }

    @Test
    fun shortStripReplacesLetterGroupsWithDots() {
        // available = 416 -> level 1: 26 rows. The stock layout alternates a normal key with a dot
        // (the dot branch resets `i7`), so "B" is hidden behind the first dot, and the last dot hides the
        // two remaining letters "X" and "Y" (indices 23, 24).
        val level1 = layoutAt(heightDp = 416f)
        assertTrue(level1.heightNotEnough)
        assertEquals(1, level1.dotLevel)
        assertEquals(26, level1.keys.size)
        assertEquals(12, level1.keys.count { it.isDot })
        assertEquals(0, level1.keys.first().letterIndex)
        assertTrue(level1.keys.first().hiddenLetterIndices.isEmpty())
        assertEquals(listOf(1), level1.keys[1].hiddenLetterIndices)
        assertEquals(listOf(23, 24), level1.keys[23].hiddenLetterIndices)
        // Every letter appears exactly once, in order, across normal keys and hidden lists.
        val reported =
            level1.keys.flatMap { key ->
                if (key.isDot) key.hiddenLetterIndices else listOf(key.letterIndex!!)
            }
        assertEquals((0 until 27).toList(), reported)

        // available = 288 -> level 2: 18 rows, hidden letter groups.
        val level2 = layoutAt(heightDp = 288f)
        assertEquals(2, level2.dotLevel)
        assertEquals(18, level2.keys.size)
        val level2Reported =
            level2.keys.flatMap { key ->
                if (key.isDot) key.hiddenLetterIndices else listOf(key.letterIndex!!)
            }
        assertEquals((0 until 27).toList(), level2Reported)
    }

    @Test
    fun stripTooShortForAnyLevelDrawsNothing() {
        val layout = layoutAt(heightDp = 100f)
        assertTrue(layout.heightNotEnough)
        assertEquals(6, layout.dotLevel)
        assertTrue(layout.keys.isEmpty())
    }

    @Test
    fun hitTestingResolvesDotsToTheirHiddenLetters() {
        val enough = layoutAt(heightDp = 432f)
        assertEquals(0, alphabetIndexBarHitAt(enough, 1f)!!.letterIndex)
        assertEquals(5, alphabetIndexBarHitAt(enough, 16f * 5 + 8f)!!.letterIndex)
        assertEquals(26, alphabetIndexBarHitAt(enough, 500f)!!.letterIndex)

        // Level 1: the first dot (B) has a single 16px cell; the last dot hides "X" (23) and "Y" (24)
        // in two 8px cells.
        val level1 = layoutAt(heightDp = 416f)
        val firstDot = level1.keys[1]
        assertEquals(1, alphabetIndexBarHitAt(level1, firstDot.touchTopPx + 1f)!!.letterIndex)
        val lastDot = level1.keys[23]
        assertEquals(23, alphabetIndexBarHitAt(level1, lastDot.touchTopPx + 1f)!!.letterIndex)
        assertEquals(24, alphabetIndexBarHitAt(level1, lastDot.touchBottomPx - 1f)!!.letterIndex)
    }
}
