// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.blur

import androidx.compose.ui.graphics.Color

/**
 * ColorOS 17 Liquid Glass Material preset types corresponding to COUIMaterialBlurEffect.BlurEffectType.
 */
enum class ColorOSGlassType {
    TopBar,
    BottomBar,
    SecondaryButton,
    ChipTab,
    ChipTabSelected,
}

/**
 * Provides factory functions and preset parameters for ColorOS 17 Liquid Glass material effects.
 */
object ColorOSLiquidGlassDefaults {

    /**
     * Creates a [BlurColors] instance configured for ColorOS 17 Liquid Glass material.
     *
     * @param type The [ColorOSGlassType] preset.
     * @param isDark Whether the dark theme preset should be used.
     */
    fun glassColors(
        type: ColorOSGlassType,
        isDark: Boolean,
    ): BlurColors {
        val blendEntries = when (type) {
            ColorOSGlassType.TopBar -> if (!isDark) {
                listOf(
                    BlendColorEntry(Color(0x66333333), BlurBlendMode.ColorDodge),
                    BlendColorEntry(Color(0x99F5F5F5), BlurBlendMode.Luminosity),
                )
            } else {
                listOf(
                    BlendColorEntry(Color(0xCC262626), BlurBlendMode.SrcOver),
                    BlendColorEntry(Color(0xB37F7F7F), BlurBlendMode.Overlay),
                )
            }

            ColorOSGlassType.BottomBar -> if (!isDark) {
                listOf(
                    BlendColorEntry(Color(0xB3666666), BlurBlendMode.ColorDodge),
                    BlendColorEntry(Color(0x99F5F5F5), BlurBlendMode.Luminosity),
                )
            } else {
                listOf(
                    BlendColorEntry(Color(0x80666666), BlurBlendMode.Overlay),
                    BlendColorEntry(Color(0xB3262626), BlurBlendMode.Luminosity),
                )
            }

            ColorOSGlassType.SecondaryButton -> if (!isDark) {
                listOf(
                    BlendColorEntry(Color(0xCCFFFFFF), BlurBlendMode.SrcOver),
                    BlendColorEntry(Color(0x00FFFFFF), BlurBlendMode.SrcOver),
                )
            } else {
                listOf(
                    BlendColorEntry(Color(0x33FFFFFF), BlurBlendMode.SrcOver),
                    BlendColorEntry(Color(0x00FFFFFF), BlurBlendMode.SrcOver),
                )
            }

            ColorOSGlassType.ChipTab -> if (!isDark) {
                listOf(
                    BlendColorEntry(Color(0x144E4E4E), BlurBlendMode.ColorDodge),
                    BlendColorEntry(Color(0x14606060), BlurBlendMode.Luminosity),
                )
            } else {
                listOf(
                    BlendColorEntry(Color(0x33B2B2B2), BlurBlendMode.Luminosity),
                    BlendColorEntry(Color(0x33303030), BlurBlendMode.Overlay),
                )
            }

            ColorOSGlassType.ChipTabSelected -> if (!isDark) {
                listOf(
                    BlendColorEntry(Color(0x66A3A1A1), BlurBlendMode.ColorDodge),
                    BlendColorEntry(Color(0x99F5F5F5), BlurBlendMode.Luminosity),
                )
            } else {
                listOf(
                    BlendColorEntry(Color(0xA6505050), BlurBlendMode.Luminosity),
                    BlendColorEntry(Color(0x4CA7A7A7), BlurBlendMode.Overlay),
                )
            }
        }
        return BlurColors(blendColors = blendEntries)
    }

    /**
     * Gets the standard blur radius in dp for the specified [ColorOSGlassType].
     */
    fun glassBlurRadius(type: ColorOSGlassType): Float = when (type) {
        ColorOSGlassType.TopBar -> 20f
        ColorOSGlassType.BottomBar -> 25f
        ColorOSGlassType.SecondaryButton -> 25f
        ColorOSGlassType.ChipTab -> 40f
        ColorOSGlassType.ChipTabSelected -> 40f
    }
}

/**
 * Chains a ColorOS 17 Liquid Glass blur effect into the [BackdropEffectScope].
 *
 * @param type The [ColorOSGlassType] preset.
 * @param isDark Whether to apply the dark theme preset.
 */
fun BackdropEffectScope.colorOSLiquidGlass(
    type: ColorOSGlassType,
    isDark: Boolean,
) {
    val radius = ColorOSLiquidGlassDefaults.glassBlurRadius(type)
    val colors = ColorOSLiquidGlassDefaults.glassColors(type, isDark)

    blur(radiusX = radius, radiusY = radius)
    blendColors(colors)
}
