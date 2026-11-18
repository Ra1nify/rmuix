// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SliderThumbTest {
    @Test
    fun theStyleShadowIsNotTheBaseResourceOrTheOldFakeCircle() {
        assertEquals(4f, SliderThumbValues.SHADOW_RADIUS_DP)
        assertEquals(2f, SliderThumbValues.SHADOW_OFFSET_DP)
        assertEquals(12f, SliderThumbValues.TEXTURE_PADDING_PX)
        assertEquals(5f, SliderThumbValues.INNER_BLUR_PX)
        assertEquals(0.4f, SliderThumbValues.INNER_OFFSET_PX)
        assertEquals(10f, SliderThumbValues.INNER_STROKE_PX)
        assertEquals(11f, sliderThumbShadowRadius(2.625f))
        assertEquals(1f, sliderThumbShadowRadius(0.1f))
    }

    @Test
    fun nativeAlphaTruncatesRatherThanRounding() {
        assertEquals(178, sliderThumbAlpha(0.7f))
        assertEquals(254, sliderThumbAlpha(0.999f))
        assertEquals(255, sliderThumbAlpha(1f))
        assertEquals(255, sliderThumbAlpha(2f))
        assertEquals(0, sliderThumbAlpha(-1f))
        assertEquals(0, sliderThumbAlpha(Float.NaN))
    }

    @Test
    fun textureBitmapUsesRawPixelPaddingAndCeiling() {
        assertEquals(40, sliderThumbBitmapSize(16f))
        assertEquals(62, sliderThumbBitmapSize(37.5f))
        assertEquals(65, sliderThumbBitmapSize(40.01f))
        assertEquals(0xE6FFFFFF.toInt(), SliderThumbValues.GRADIENT_OPAQUE)
        assertEquals(0x8CFFFFFF.toInt(), SliderThumbValues.GRADIENT_TRANSPARENT)
        assertEquals(0x77FFFFFF, SliderThumbValues.INNER_FILL)
    }

    @Test
    fun theWhiteCoverHasADifferentPressResponseAndBothUseTheNativeScaleThreshold() {
        val texture = sliderThumbMaterialSpring(0.3f)
        val coverPress = sliderThumbMaterialSpring(0.5f)
        assertEquals(438.6491f, texture.stiffness, 0.001f)
        assertEquals(157.91367f, coverPress.stiffness, 0.001f)
        assertEquals(1f, texture.dampingRatio)
        assertEquals(1f, coverPress.dampingRatio)
        assertEquals(0.0075f, texture.visibilityThreshold)
        assertEquals(0.0075f, coverPress.visibilityThreshold)
    }

    @Test
    fun changingTheMaterialFlagWhileHeldDoesNotInventANewPress() {
        assertFalse(sliderThumbPressChanged(true, true, materialEnabled = true))
        assertFalse(sliderThumbPressChanged(true, false, materialEnabled = true, controlEnabled = false))
        assertFalse(sliderThumbPressChanged(false, false, materialEnabled = true))
        assertFalse(sliderThumbPressChanged(false, true, materialEnabled = false))
        assertTrue(sliderThumbPressChanged(false, true, materialEnabled = true))
        assertTrue(sliderThumbPressChanged(true, false, materialEnabled = true))
    }

    @Test
    fun zeroTextureOpacityUsesTheOrdinarySelectorRatherThanAnInvisibleThumb() {
        val frame =
            SliderThumbFrame(
                center = Offset(14f, 18f),
                radius = 6f,
                enabled = true,
                color = Color.White,
                shadowColor = Color(0x1A000000),
                shadowRadius = 4f,
                shadowOffset = 2f,
                textureEnabled = true,
                textureAlpha = 0f,
                coverAlpha = 1f,
            )
        assertFalse(frame.textured)
        assertTrue(frame.copy(textureAlpha = 0.01f).textured)
        assertFalse(frame.copy(textureEnabled = false, textureAlpha = 1f).textured)
    }
}
