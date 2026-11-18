// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins the geometry that `COUISeekBarCore` reads from `coui_seekbar_*`:
 * `coui_seekbar_view_min_height` (36dp), `coui_seekbar_background_height` (20dp),
 * `coui_seekbar_intent_height_default` (4dp), `coui_seekbar_thumb_radius` (6dp),
 * `coui_seekbar_thumb_max_radius` (8dp), `coui_seekbar_thumb_in_scale_radius` (3.67dp) and
 * `coui_seekbar_thumb_shadow_size` (2dp).
 */
class SliderDefaultsTest {
    @Test
    fun valueLabelMatchesTheSeekbarTextDrawable() {
        // coui_seekbar_popup_text_* plus the hard-coded `WIDTH = 144` in seekbar.TextDrawable.
        // 144 is a raw pixel constant (`getIntrinsicWidth()`), not a dp value.\n        assertEquals(144, SliderDefaults.ValueLabelWidthPx)
        assertEquals(12.sp, SliderDefaults.ValueLabelTextSize)
        assertEquals(16.dp, SliderDefaults.ValueLabelHeight)
        assertEquals(6.dp, SliderDefaults.ValueLabelMarginBottom)
        assertEquals(4.dp, SliderDefaults.ValueLabelPaddingEnd)
    }

    @Test
    fun trackPaddingAndViewCapsMatchTheSeekbarDimens() {
        // coui_seekbar_progress_padding_horizontal / _pressed_ / coui_seekbar_view_max_width
        assertEquals(14.dp, SliderDefaults.TrackPadding)
        assertEquals(4.dp, SliderDefaults.PressedTrackPadding)
        assertEquals(480.dp, SliderDefaults.MaxWidth)
        // coui_vertical_seekbar_progress_padding_vertical / _pressed_ / _view_max_height
        assertEquals(10.dp, SliderDefaults.VerticalTrackPadding)
        assertEquals(4.dp, SliderDefaults.PressedVerticalTrackPadding)
        assertEquals(480.dp, SliderDefaults.MaxHeight)
    }

    @Test
    fun intentSizesMatchTheSeekbarIntentDimens() {
        // coui_seekbar_intent_{height,max_background_height,thumb_radius,thumb_max_radius}_{size}
        assertEquals(2.dp, SliderDefaults.IntentSize.Small.trackHeight)
        assertEquals(10.dp, SliderDefaults.IntentSize.Small.maxBackgroundHeight)
        assertEquals(3.dp, SliderDefaults.IntentSize.Small.thumbRadius)
        assertEquals(8.dp, SliderDefaults.IntentSize.Small.thumbMaxRadius)
        assertEquals(4.dp, SliderDefaults.IntentSize.Default.trackHeight)
        assertEquals(6.dp, SliderDefaults.IntentSize.Default.maxBackgroundHeight)
        assertEquals(4.dp, SliderDefaults.IntentSize.Default.thumbRadius)
        assertEquals(5.dp, SliderDefaults.IntentSize.Default.thumbMaxRadius)
        assertEquals(6.dp, SliderDefaults.IntentSize.Large.trackHeight)
        assertEquals(10.dp, SliderDefaults.IntentSize.Large.maxBackgroundHeight)
        assertEquals(5.dp, SliderDefaults.IntentSize.Large.thumbRadius)
        assertEquals(8.dp, SliderDefaults.IntentSize.Large.thumbMaxRadius)
        // coui_seekbar_intent_thumb_out_shade_radius
        assertEquals(2.dp, SliderDefaults.IntentThumbShadeRadius)
    }

    @Test
    fun geometryMatchesTheSeekBarTokens() {
        assertEquals(36.dp, SliderDefaults.MinHeight)
        assertEquals(20.dp, SliderDefaults.CapsuleTrackHeight)
        assertEquals(4.dp, SliderDefaults.IntentTrackHeight)
        assertEquals(6.dp, SliderDefaults.ThumbRestingRadius)
        assertEquals(8.dp, SliderDefaults.ThumbExpandedRadius)
        assertEquals(3.67.dp, SliderDefaults.ThumbInScaleRadius)
        assertEquals(2.dp, SliderDefaults.ThumbShadowSize)
    }

    /**
     * `GlitterEffectRenderer` reads two colours and drives a spring: `GLITTER_EFFECT_SPRING_RESPONSE`
     * (0.6), `GLITTER_LN_BASE` (85) and `FULL_ANGLE` (360). The alpha curve is
     * `round(255 * (1 - exp(-(ln 85 / 360) * x)))`, normalised here to 0..1.
     */
    @Test
    fun glitterEffectMatchesTheLenBaseCurve() {
        assertEquals(0f, SliderDefaults.glitterEffectAlpha(0f))
        assertEquals(0.0f, SliderDefaults.glitterEffectAlpha(0f), 1e-6f)
        // 1 - 85^-0.5
        assertEquals(0.8911f, SliderDefaults.glitterEffectAlpha(180f), 1e-3f)
        // 1 - 85^-1
        assertEquals(0.9882f, SliderDefaults.glitterEffectAlpha(360f), 1e-3f)
        // beyond the full angle the curve clamps nowhere; it keeps approaching 1
        assertEquals(true, SliderDefaults.glitterEffectAlpha(720f) > SliderDefaults.glitterEffectAlpha(360f))
    }
}
