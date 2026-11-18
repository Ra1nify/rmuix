// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class BasicComponentRenderingTest {
    @Test
    fun preferenceTextNeutralsMatchTheLabelResources() {
        assertEquals(Color(0xE6000000), preferenceTitle(false))
        assertEquals(Color(0xE6FFFFFF), preferenceTitle(true))
        assertEquals(Color(0x8A000000), preferenceSummary(false))
        assertEquals(Color(0x8AFFFFFF), preferenceSummary(true))
        assertEquals(Color(0x42000000), preferenceTertiary(false))
        assertEquals(Color(0x4DFFFFFF), preferenceTertiary(true))
    }

    @Test
    fun rowMetricsMatchThePreferenceDimens() {
        assertEquals(48.dp, BasicComponentDefaults.MinHeight)
        assertEquals(16.dp, BasicComponentDefaults.IconTextGap)
        assertEquals(PaddingValues(horizontal = 32.dp, vertical = 10.dp), BasicComponentDefaults.InsideMargin)
        // COUIHintRedDot: coui_dot_diameter / coui_height / coui_*_width / coui_hint_text_size.
        assertEquals(6.dp, BasicComponentDefaults.BadgeSize)
        assertEquals(16.dp, BasicComponentDefaults.BadgeHeight)
        assertEquals(8.dp, BasicComponentDefaults.BadgeCornerRadius)
        assertEquals(16.dp, BasicComponentDefaults.BadgeSmallWidth)
        assertEquals(20.dp, BasicComponentDefaults.BadgeMediumWidth)
        assertEquals(26.dp, BasicComponentDefaults.BadgeLargeWidth)
        assertEquals(10.sp, BasicComponentDefaults.BadgeTextSize)
        // The stock jump dot has no margin; the 4dp margin belongs to the assignment-in-right layout.
        assertEquals(0.dp, BasicComponentDefaults.BadgeGap)
    }

    @Test
    fun assignmentUsesThePrimaryTextTokenNotTheThemeAccent() {
        // coui_preference_assignment_text_color -> ?attr/couiColorPrimaryText, mapped by Theme.COUI.Main
        // to coui_color_primary_text_blue (#0080FF light / #5C9DFF dark).
        assertEquals(Color(0xFF0080FF), preferenceAssignment(false))
        assertEquals(Color(0xFF5C9DFF), preferenceAssignment(true))
    }

    @Test
    fun pressBackgroundUsesTheRowSelectorColorsAndFades() {
        assertEquals(Color(0x14000000), preferencePress(false))
        assertEquals(Color(0x26FFFFFF), preferencePress(true))
        assertEquals(100, PREFERENCE_PRESS_FADE_IN_MILLIS)
        assertEquals(360, PREFERENCE_PRESS_FADE_OUT_MILLIS)
    }

    @Test
    fun iconMarginsFollowTheSourceTiers() {
        assertEquals(12.dp, BasicComponentDefaults.iconVerticalMargin(24.dp, 1))
        assertEquals(20.dp, BasicComponentDefaults.iconVerticalMargin(24.dp, 2))
        assertEquals(10.dp, BasicComponentDefaults.iconVerticalMargin(32.dp, 1))
        assertEquals(16.dp, BasicComponentDefaults.iconVerticalMargin(32.dp, 3))
        assertEquals(12.dp, BasicComponentDefaults.iconVerticalMargin(36.dp, 1))
        assertEquals(14.dp, BasicComponentDefaults.iconVerticalMargin(36.dp, 4))
        assertEquals(8.dp, BasicComponentDefaults.iconVerticalMargin(50.dp, 3))
        assertEquals(14.dp, BasicComponentDefaults.iconVerticalMargin(18.dp, 1))
    }

    @Test
    fun lineCountEstimateUsesTheRenderedTextBlockHeight() {
        // 16sp title line is about 19dp, 13sp summary about 17dp; the estimate rounds to the nearest line.
        assertEquals(1, estimatedPreferenceLineCount(19f, 20f))
        assertEquals(2, estimatedPreferenceLineCount(36f, 20f))
        assertEquals(3, estimatedPreferenceLineCount(53f, 20f))
        // An empty text block still counts as one line.
        assertEquals(1, estimatedPreferenceLineCount(0f, 20f))
    }

    @Test
    fun multilineIconTopMarginMatchesSetIconTopInParent() {
        // setIconTopInParent(): top = max(centre of the first two lines - iconHeight / 2, 8dp).
        // textContentPaddingTop = 10dp, two lines = 40dp -> centre = 30dp.
        assertEquals(18, preferenceMultilineIconTopMarginPx(60f, 24f, 10f, 20f, 8f))
        assertEquals(12, preferenceMultilineIconTopMarginPx(60f, 36f, 10f, 20f, 8f))
        // 50dp icon would land on 5dp and is clamped to coui_preference_50icon_margin_vertical_default.
        assertEquals(8, preferenceMultilineIconTopMarginPx(60f, 50f, 10f, 20f, 8f))
        // Shorter blocks use their own height as the first-two-lines extent.
        assertEquals(10, preferenceMultilineIconTopMarginPx(36f, 36f, 10f, 20f, 8f))
    }

    @Test
    fun statusTextFollowsTheSelectWidget() {
        // PreferenceStatusText.Tiny / coui_preference_status_text_max_width /
        // support_preference_widget_focus_jump_margin_start
        assertEquals(14.sp, BasicComponentDefaults.StatusTextSize)
        assertEquals(162.dp, BasicComponentDefaults.StatusTextMaxWidth)
        assertEquals(24.dp, BasicComponentDefaults.StatusTextStartMargin)
    }

    @Test
    fun rightAlignedAssignmentFollowsTheInRightLayout() {
        // support_preference_title_margin_end_in_right and
        // coui_preference_assignment_in_right.xml (android:maxLines="2").
        assertEquals(16.dp, BasicComponentDefaults.AssignmentInRightGap)
        assertEquals(2, BasicComponentDefaults.ASSIGNMENT_IN_RIGHT_MAX_LINES)
    }

    @Test
    fun textSizesFollowThePreferenceStyles() {
        // COUIPreferenceTitleStyle -> couiTextAppearanceHeadline6 (16sp) and
        // COUIPreferenceSummaryStyle -> couiTextAppearanceArticleBody (13sp). The 12sp
        // support_preference_summary_size is declared in the APK but never read.
        assertEquals(16.sp, BasicComponentDefaults.TitleTextSize)
        assertEquals(13.sp, BasicComponentDefaults.SummaryTextSize)
        // The row metrics already matched the base preference layout.
        assertEquals(48.dp, BasicComponentDefaults.MinHeight)
        assertEquals(PaddingValues(horizontal = 32.dp, vertical = 10.dp), BasicComponentDefaults.InsideMargin)
    }

    @Test
    fun exactMultilineIconMarginUsesTheRealLineMetrics() {
        val recorder = PreferenceTextMetricsRecorder()
        // Two title lines: the centre is the middle between line 0 top (0) and line 1 bottom (40).
        recorder.title = PreferenceTextMetrics(lineCount = 2, heightPx = 40f, firstLineBottomPx = 20f, secondLineBottomPx = 40f)
        assertEquals(
            18,
            preferenceMultilineIconTopMarginExactPx(
                recorder = recorder,
                textContentPaddingTopPx = 10f,
                marginBetweenLinePx = 2f,
                iconHeightPx = 24f,
                minMarginPx = 8f,
            ),
        )
        // One title line + one summary line: the summary starts after title height + 2dp.
        val oneOne = PreferenceTextMetricsRecorder()
        oneOne.title = PreferenceTextMetrics(lineCount = 1, heightPx = 20f, firstLineBottomPx = 20f, secondLineBottomPx = 0f)
        oneOne.summary = PreferenceTextMetrics(lineCount = 1, heightPx = 18f, firstLineBottomPx = 18f, secondLineBottomPx = 0f)
        assertEquals(
            18,
            preferenceMultilineIconTopMarginExactPx(
                recorder = oneOne,
                textContentPaddingTopPx = 10f,
                marginBetweenLinePx = 2f,
                iconHeightPx = 24f,
                minMarginPx = 8f,
            ),
        )
        // A single title line clamps to the 8dp floor: centre (10 + 20) / 2 = 20 -> 20 - 12 = 8.
        val single = PreferenceTextMetricsRecorder()
        single.title = PreferenceTextMetrics(lineCount = 1, heightPx = 20f, firstLineBottomPx = 20f, secondLineBottomPx = 0f)
        assertEquals(
            8,
            preferenceMultilineIconTopMarginExactPx(
                recorder = single,
                textContentPaddingTopPx = 10f,
                marginBetweenLinePx = 2f,
                iconHeightPx = 24f,
                minMarginPx = 8f,
            ),
        )
        // No title: the summary contributes the first two lines.
        val summaryOnly = PreferenceTextMetricsRecorder()
        summaryOnly.summary = PreferenceTextMetrics(lineCount = 2, heightPx = 36f, firstLineBottomPx = 18f, secondLineBottomPx = 36f)
        assertEquals(
            16,
            preferenceMultilineIconTopMarginExactPx(
                recorder = summaryOnly,
                textContentPaddingTopPx = 10f,
                marginBetweenLinePx = 2f,
                iconHeightPx = 24f,
                minMarginPx = 8f,
            ),
        )
        // couiTextAppearanceHeadline6 / couiTextAppearanceArticleBody lineSpacingMultiplier.
        assertEquals(1.05f, BasicComponentDefaults.TitleLineSpacingMultiplier)
        assertEquals(1.1f, BasicComponentDefaults.SummaryLineSpacingMultiplier)
    }
}
