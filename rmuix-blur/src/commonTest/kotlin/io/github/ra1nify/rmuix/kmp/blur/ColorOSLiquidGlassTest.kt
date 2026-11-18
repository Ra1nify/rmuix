// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.blur

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins every [ColorOSGlassType] preset to the `COUIMaterialBlurEffect` static block
 * (`COUIMaterialBlurEffect.java`, lines 547-576).
 *
 * Layer order follows the stock pipeline: layer 1 is composited onto the blurred backdrop first,
 * layer 2 onto layer 1.
 */
class ColorOSLiquidGlassTest {
    private fun layers(
        type: ColorOSGlassType,
        isDark: Boolean,
    ): List<BlendColorEntry> = ColorOSLiquidGlassDefaults.glassColors(type, isDark).blendColors

    @Test
    fun blurRadiiMatchStock() {
        assertEquals(20f, ColorOSLiquidGlassDefaults.glassBlurRadius(ColorOSGlassType.TopBar))
        assertEquals(25f, ColorOSLiquidGlassDefaults.glassBlurRadius(ColorOSGlassType.BottomBar))
        assertEquals(25f, ColorOSLiquidGlassDefaults.glassBlurRadius(ColorOSGlassType.SecondaryButton))
        assertEquals(40f, ColorOSLiquidGlassDefaults.glassBlurRadius(ColorOSGlassType.ChipTab))
        assertEquals(40f, ColorOSLiquidGlassDefaults.glassBlurRadius(ColorOSGlassType.ChipTabSelected))
    }

    @Test
    fun topBarLayersMatchStock() {
        val light = layers(ColorOSGlassType.TopBar, isDark = false)
        assertEquals(Color(0x66333333), light[0].color)
        assertEquals(BlurBlendMode.ColorDodge, light[0].mode)
        assertEquals(Color(0x99F5F5F5), light[1].color)
        assertEquals(BlurBlendMode.Luminosity, light[1].mode)

        val dark = layers(ColorOSGlassType.TopBar, isDark = true)
        assertEquals(Color(0xCC262626), dark[0].color)
        assertEquals(BlurBlendMode.SrcOver, dark[0].mode)
        assertEquals(Color(0xB37F7F7F), dark[1].color)
        assertEquals(BlurBlendMode.Overlay, dark[1].mode)
    }

    @Test
    fun bottomBarLayersMatchStock() {
        val light = layers(ColorOSGlassType.BottomBar, isDark = false)
        assertEquals(Color(0xB3666666), light[0].color)
        assertEquals(BlurBlendMode.ColorDodge, light[0].mode)
        assertEquals(Color(0x99F5F5F5), light[1].color)
        assertEquals(BlurBlendMode.Luminosity, light[1].mode)

        val dark = layers(ColorOSGlassType.BottomBar, isDark = true)
        assertEquals(Color(0x80666666), dark[0].color)
        assertEquals(BlurBlendMode.Overlay, dark[0].mode)
        assertEquals(Color(0xB3262626), dark[1].color)
        assertEquals(BlurBlendMode.Luminosity, dark[1].mode)
    }

    @Test
    fun secondaryButtonLayersMatchStock() {
        val light = layers(ColorOSGlassType.SecondaryButton, isDark = false)
        assertEquals(Color(0xCCFFFFFF), light[0].color)
        assertEquals(BlurBlendMode.SrcOver, light[0].mode)
        assertEquals(Color(0x00FFFFFF), light[1].color)

        val dark = layers(ColorOSGlassType.SecondaryButton, isDark = true)
        assertEquals(Color(0x33FFFFFF), dark[0].color)
        assertEquals(Color(0x00FFFFFF), dark[1].color)
    }

    @Test
    fun chipTabLayersMatchStock() {
        val light = layers(ColorOSGlassType.ChipTab, isDark = false)
        assertEquals(Color(0x144E4E4E), light[0].color)
        assertEquals(BlurBlendMode.ColorDodge, light[0].mode)
        assertEquals(Color(0x14606060), light[1].color)
        assertEquals(BlurBlendMode.Luminosity, light[1].mode)

        val dark = layers(ColorOSGlassType.ChipTab, isDark = true)
        assertEquals(Color(0x33B2B2B2), dark[0].color)
        assertEquals(BlurBlendMode.Luminosity, dark[0].mode)
        assertEquals(Color(0x33303030), dark[1].color)
        assertEquals(BlurBlendMode.Overlay, dark[1].mode)
    }

    @Test
    fun chipTabSelectedLayersMatchStock() {
        val light = layers(ColorOSGlassType.ChipTabSelected, isDark = false)
        assertEquals(Color(0x66A3A1A1), light[0].color)
        assertEquals(BlurBlendMode.ColorDodge, light[0].mode)
        assertEquals(Color(0x99F5F5F5), light[1].color)
        assertEquals(BlurBlendMode.Luminosity, light[1].mode)

        val dark = layers(ColorOSGlassType.ChipTabSelected, isDark = true)
        assertEquals(Color(0xA6505050), dark[0].color)
        assertEquals(BlurBlendMode.Luminosity, dark[0].mode)
        assertEquals(Color(0x4CA7A7A7), dark[1].color)
        assertEquals(BlurBlendMode.Overlay, dark[1].mode)
    }

    @Test
    fun everyPresetHasTwoLayers() {
        ColorOSGlassType.entries.forEach { type ->
            assertEquals(2, layers(type, isDark = false).size, "$type light")
            assertEquals(2, layers(type, isDark = true).size, "$type dark")
        }
    }
}
