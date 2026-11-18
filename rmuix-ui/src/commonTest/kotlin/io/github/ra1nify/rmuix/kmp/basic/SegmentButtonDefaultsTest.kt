// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SegmentButtonDefaultsTest {
    @Test
    fun metricsComeFromTheSegmentButtonStyle() {
        // `SegmentButton` / `SegmentButton.Tiny` in styles.xml.
        assertEquals(40.dp, SegmentButtonDefaults.Height)
        assertEquals(32.dp, SegmentButtonDefaults.TinyHeight)
        assertEquals(4.dp, SegmentButtonDefaults.Padding)
        assertEquals(2.dp, SegmentButtonDefaults.TinyPadding)
        assertEquals(52.dp, SegmentButtonDefaults.MinSegmentWidth)
        assertEquals(12.dp, SegmentButtonDefaults.SegmentHorizontalPadding)
        // `couiTextButtonM`.
        assertEquals(14.sp, SegmentButtonDefaults.TextSize)
        // coui_segment_shadow_offset / coui_segment_button_shadow_end_color.
        assertEquals(2.dp, SegmentButtonDefaults.IndicatorShadowOffset)
        assertEquals(Color(0x03000000), SegmentButtonDefaults.IndicatorShadowEndColor)
        // WIDTH_SPRING_RESPONSE / WIDTH_SPRING_BOUNCE drive the animated indicator width.
        assertEquals(0.6f, SegmentButtonDefaults.WidthSpringResponse)
        assertEquals(0.5f, SegmentButtonDefaults.WidthSpringBounce)
        // WIDTH_SPRING_VELOCITY / coui_segment_*_extra_width / coui_segment_switch_distance.
        assertEquals(1000f, SegmentButtonDefaults.WidthSpringVelocity)
        assertEquals(7.dp, SegmentButtonDefaults.RegularExtraWidth)
        assertEquals(5.dp, SegmentButtonDefaults.SmallExtraWidth)
        assertEquals(5.dp, SegmentButtonDefaults.SwitchDistance)
        // POSITION_SPRING_* / HEIGHT_SPRING_*.
        assertEquals(0.4f, SegmentButtonDefaults.PositionSpringResponse)
        assertEquals(0.1f, SegmentButtonDefaults.PositionSpringBounce)
        assertEquals(0.6f, SegmentButtonDefaults.HeightSpringResponse)
        assertEquals(0.5f, SegmentButtonDefaults.HeightSpringBounce)
        assertEquals(-500f, SegmentButtonDefaults.HeightSpringVelocity)
        assertEquals(0.9f, SegmentButtonDefaults.IndicatorMinHeightFraction)
    }

    @Test
    fun dynamicSpringsFollowCalculateDynamicWidthParam() {
        // calculateDynamicWidthParam(100, 180, 0.5, 0.1, 2):
        // 0.5 - (0.5 - 0.1) * (80 / 180)^2 = 0.5 - 0.4 * 0.1975 = 0.4210.
        assertEquals(
            0.421f,
            segmentDynamicSpringParam(currentPx = 100f, targetPx = 180f, start = 0.5f, end = 0.1f, power = 2f),
            0.001f,
        )
        // Equal widths -> the start value.
        assertEquals(
            0.6f,
            segmentDynamicSpringParam(currentPx = 100f, targetPx = 100f, start = 0.6f, end = 0.4f, power = 2f),
            0.001f,
        )
        // Short-to-long: fixed 0.5 bounce, dynamic 0.6..0.4 response.
        val shortToLong = segmentWidthSpring(currentWidthPx = 100f, targetWidthPx = 180f)
        assertEquals(0.5f, shortToLong.bounce, 0.001f)
        // 0.6 - (0.6 - 0.4) * 0.1975 = 0.5605.
        assertEquals(0.5605f, shortToLong.response, 0.001f)
        // Long-to-short: dynamic 0.5..0.1 bounce.
        val longToShort = segmentWidthSpring(currentWidthPx = 180f, targetWidthPx = 100f)
        assertEquals(0.421f, longToShort.bounce, 0.001f)
        assertEquals(0.5605f, longToShort.response, 0.001f)
        // Position bounce: 0.1 regular, dynamic 0.1..0.03 only for the tiny short-to-long switch.
        assertEquals(0.1f, segmentPositionSpringBounce(100f, 180f, tiny = false), 0.001f)
        assertEquals(0.0689f, segmentPositionSpringBounce(100f, 180f, tiny = true), 0.001f)
        assertEquals(0.1f, segmentPositionSpringBounce(180f, 100f, tiny = true), 0.001f)
        // getEffectiveMaxExtraWidthForShortToLong().
        assertEquals(7.dp, segmentShortToLongExtraWidth(tiny = false))
        assertEquals(5.dp, segmentShortToLongExtraWidth(tiny = true))
    }

    @Test
    fun segmentWidthsFollowTheStockChildrenDistribution() {
        // calculateChildrenWidths(): labels below the running average keep their natural width.
        assertEquals(listOf(60f, 60f, 280f), distributeSegmentWidthsPx(listOf(60f, 60f, 300f), 400f))
        // A long first label keeps its natural width; the last one absorbs the remainder.
        assertEquals(listOf(200f, 100f), distributeSegmentWidthsPx(listOf(250f, 100f), 300f))
        // If every label is shorter than the initial average, all of them become equal.
        assertEquals(listOf(200f, 200f), distributeSegmentWidthsPx(listOf(60f, 80f), 400f))
        assertEquals(listOf(150f, 150f), distributeSegmentWidthsPx(listOf(200f, 200f), 300f))
        // getResponsiveWidth(): 4 columns on compact, 6 on medium / expanded-portrait, 8 on plain expanded.
        assertEquals(368f, segmentResponsiveWidth(windowWidthDp = 400f, windowHeightDp = 800f).value, 0.01f)
        assertEquals(487f, segmentResponsiveWidth(windowWidthDp = 700f, windowHeightDp = 800f).value, 0.01f)
        assertEquals(637f, segmentResponsiveWidth(windowWidthDp = 900f, windowHeightDp = 1000f).value, 0.01f)
        assertEquals(609.33f, segmentResponsiveWidth(windowWidthDp = 1000f, windowHeightDp = 600f).value, 0.02f)
    }

    @Test
    fun touchXResolvesToTheSegmentUnderIt() {
        // `COUISegmentButtonLayout.dispatchTouchEvent()`: the segment comes from the measured child bounds.
        val widths = listOf(100f, 60f, 140f)
        assertEquals(0, segmentIndexAt(0f, widths))
        assertEquals(0, segmentIndexAt(99.9f, widths))
        assertEquals(1, segmentIndexAt(100f, widths))
        assertEquals(1, segmentIndexAt(159.9f, widths))
        assertEquals(2, segmentIndexAt(160f, widths))
        assertEquals(2, segmentIndexAt(299.9f, widths))
        assertNull(segmentIndexAt(300f, widths))
        assertNull(segmentIndexAt(-1f, widths))
        assertNull(segmentIndexAt(10f, emptyList()))
    }
}
