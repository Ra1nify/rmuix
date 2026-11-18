// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.blur

import androidx.compose.runtime.Immutable
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
 * ColorOS 17 Glass Material color configuration supporting baseline and light/dark independent tuning.
 */
@Immutable
data class GlassColors(
    val color: Color = Color.Unspecified,
    val lightColor: Color = Color(0x14FFFFFF),
    val darkColor: Color = Color(0x1A1A1A1A),
    val lightAlpha: Float = 1.0f,
    val darkAlpha: Float = 1.0f,
    val lightFallbackColor: Color = Color(0xFFF7F8F9),
    val darkFallbackColor: Color = Color(0xFF262626),
    val blendColors: List<BlendColorEntry> = emptyList(),
) {
    /**
     * Resolves surface color based on theme and blur availability.
     */
    fun resolveSurfaceColor(
        isDark: Boolean,
        isBlurActive: Boolean,
    ): Color {
        if (color != Color.Unspecified) return color
        return if (isBlurActive) {
            if (isDark) {
                darkColor.copy(alpha = (darkColor.alpha * darkAlpha).coerceIn(0f, 1f))
            } else {
                lightColor.copy(alpha = (lightColor.alpha * lightAlpha).coerceIn(0f, 1f))
            }
        } else {
            if (isDark) darkFallbackColor else lightFallbackColor
        }
    }
}

/**
 * Provides factory functions and preset parameters for ColorOS 17 Liquid Glass material effects.
 */
object ColorOSLiquidGlassDefaults {
    /**
     * 8% crystal-translucent white tint for genuine Stock ColorOS 17 liquid glass in light mode.
     */
    val LightBackgroundColor = Color(0x14FFFFFF)

    /**
     * 10% crystal-translucent deep OLED surface tint for genuine Stock ColorOS 17 liquid glass in dark mode.
     */
    val DarkBackgroundColor = Color(0x1A1A1A1A)

    /**
     * Fallback solid background for light mode when blur is disabled.
     */
    val LightFallbackBackgroundColor = Color(0xFFF7F8F9)

    /**
     * Fallback solid background for dark mode when blur is disabled.
     */
    val DarkFallbackBackgroundColor = Color(0xFF262626)

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
        // `COUIMaterialBlurEffect.java` static block (lines 547-576). Layer order matters: layer 1 is
        // composited onto the blurred backdrop first, layer 2 onto layer 1.
        val blendEntries =
            when (type) {
                ColorOSGlassType.TopBar -> {
                    if (!isDark) {
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
                }

                ColorOSGlassType.BottomBar -> {
                    if (!isDark) {
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
                }

                ColorOSGlassType.SecondaryButton -> {
                    if (!isDark) {
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
                }

                ColorOSGlassType.ChipTab -> {
                    if (!isDark) {
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
                }

                ColorOSGlassType.ChipTabSelected -> {
                    if (!isDark) {
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
            }
        return BlurColors(blendColors = blendEntries)
    }

    /**
     * Resolves [GlassColors] configuration for ColorOS 17 Glass Material with baseline and light/dark
     * independent tuning.
     *
     * Only the two [BlendColorEntry] layers come from `COUIMaterialBlurEffect`; the surface tints
     * ([GlassColors.lightColor] / [GlassColors.darkColor]) and the solid fallbacks are library choices,
     * because the stock class does not define a surface tint for these presets.
     */
    fun glassStyleColors(
        type: ColorOSGlassType,
        isDark: Boolean,
        customColor: Color = Color.Unspecified,
        lightColor: Color? = null,
        darkColor: Color? = null,
        lightAlpha: Float = 1.0f,
        darkAlpha: Float = 1.0f,
    ): GlassColors {
        val defaultBlendColors = glassColors(type, isDark).blendColors
        val defaultLightBg =
            when (type) {
                ColorOSGlassType.BottomBar -> LightBackgroundColor
                ColorOSGlassType.TopBar -> Color(0x1CFFFFFF)
                ColorOSGlassType.SecondaryButton -> Color(0x88FFFFFF)
                else -> LightBackgroundColor
            }
        val defaultDarkBg =
            when (type) {
                ColorOSGlassType.BottomBar -> DarkBackgroundColor
                ColorOSGlassType.TopBar -> Color(0x221A1A1A)
                ColorOSGlassType.SecondaryButton -> Color(0x22FFFFFF)
                else -> DarkBackgroundColor
            }
        return GlassColors(
            color = customColor,
            lightColor = lightColor ?: defaultLightBg,
            darkColor = darkColor ?: defaultDarkBg,
            lightAlpha = lightAlpha,
            darkAlpha = darkAlpha,
            lightFallbackColor = LightFallbackBackgroundColor,
            darkFallbackColor = DarkFallbackBackgroundColor,
            blendColors = defaultBlendColors,
        )
    }

    /**
     * Gets the standard blur radius in dp for the specified [ColorOSGlassType].
     */
    fun glassBlurRadius(type: ColorOSGlassType): Float =
        when (type) {
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
