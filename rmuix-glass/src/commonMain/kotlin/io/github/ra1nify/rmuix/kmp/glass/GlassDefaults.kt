// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.glass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import io.github.ra1nify.rmuix.kmp.blur.BlendColorEntry
import io.github.ra1nify.rmuix.kmp.blur.BlurBlendMode
import io.github.ra1nify.rmuix.kmp.blur.BlurColors
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * ColorOS 17 glass-material tokens, as used by `COUIBackgroundBlurBuilder` for dialogs.
 *
 * The stock dialog backdrop blur is built on the private `com.oplus.graphics` API
 * (`OplusBlurParam.setMaterialParams(mode, blendColor, mixColor)`), which this library cannot call, so
 * the two colour layers are replayed as plain [BlurColors] entries over the blurred backdrop. Treat the
 * result as an approximation of the material, not a pixel-exact port.
 */
object GlassDefaults {
    /**
     * `coui_dialog_blur_radius` = 200. The stock builder passes the raw integer to
     * `OplusBlurParam.setBlurRadius`, i.e. **pixels**, so this value is used with the pixel-based
     * `BackdropEffectScope.blur` instead of the density-converted dp helpers.
     */
    const val DIALOG_BLUR_RADIUS_PX = 200f

    /** `coui_dialog_list_mix_blur_light` = `#dc909090`: the light-mode material mix colour. */
    val DialogMixLight = Color(0xDC909090)

    /** `coui_dialog_list_mix_blur_dark` = `#8c777777`. */
    val DialogMixDark = Color(0x8C777777)

    /** `coui_dialog_list_blend_blur_light` = `#509f9f9f`: the light-mode material blend colour. */
    val DialogBlendLight = Color(0x509F9F9F)

    /** `coui_dialog_list_blend_blur_dark` = `#b2292929`. */
    val DialogBlendDark = Color(0xB2292929)

    /**
     * `coui_bottomdialog_blur_radius` = 400. The stock bottom sheet builder passes the same pixel unit,
     * and its radius is twice the dialog's because the sheet is blurred through a taller surface.
     */
    const val BOTTOM_SHEET_BLUR_RADIUS_PX = 400f

    /** `coui_bottomdialog_list_mix_blur_light` = `#dcc8c8c8`. */
    val BottomSheetMixLight = Color(0xDCC8C8C8)

    /** `coui_bottomdialog_list_mix_blur_dark` = `#a4515151`. */
    val BottomSheetMixDark = Color(0xA4515151)

    /** `coui_bottomdialog_list_blend_blur_light` = `#376e6e6e`. */
    val BottomSheetBlendLight = Color(0x376E6E6E)

    /** `coui_bottomdialog_list_blend_blur_dark` = `#b30f0f0f`. */
    val BottomSheetBlendDark = Color(0xB30F0F0F)

    /** Solid fallback when no backdrop is available (window dialogs on platforms without window blur). */
    val DialogFallbackLight = Color(0xFFFFFFFF)

    /** Solid fallback for the dark theme: `?attr/couiColorSurface` = `#1E1E1E`. */
    val DialogFallbackDark = Color(0xFF1E1E1E)

    /**
     * The dialog material replayed as two blend layers: the mix colour establishes the frosted tint, the
     * blend colour adds the material sheen.
     *
     * @param isDark Whether the dark-mode tokens should be used.
     */
    @Composable
    fun dialogBlurColors(isDark: Boolean = isGlassInDarkTheme()): BlurColors =
        remember(isDark) {
            if (isDark) {
                BlurColors(
                    blendColors =
                        listOf(
                            BlendColorEntry(DialogMixDark, BlurBlendMode.SrcOver),
                            BlendColorEntry(DialogBlendDark, BlurBlendMode.SrcOver),
                        ),
                )
            } else {
                BlurColors(
                    blendColors =
                        listOf(
                            BlendColorEntry(DialogMixLight, BlurBlendMode.SrcOver),
                            BlendColorEntry(DialogBlendLight, BlurBlendMode.SrcOver),
                        ),
                )
            }
        }

    /**
     * The bottom-sheet material replayed as two blend layers: mix establishes the frosted tint, blend adds
     * the sheet sheen.
     *
     * @param isDark Whether the dark-mode tokens should be used.
     */
    @Composable
    fun bottomSheetBlurColors(isDark: Boolean = isGlassInDarkTheme()): BlurColors =
        remember(isDark) {
            if (isDark) {
                BlurColors(
                    blendColors =
                        listOf(
                            BlendColorEntry(BottomSheetMixDark, BlurBlendMode.SrcOver),
                            BlendColorEntry(BottomSheetBlendDark, BlurBlendMode.SrcOver),
                        ),
                )
            } else {
                BlurColors(
                    blendColors =
                        listOf(
                            BlendColorEntry(BottomSheetMixLight, BlurBlendMode.SrcOver),
                            BlendColorEntry(BottomSheetBlendLight, BlurBlendMode.SrcOver),
                        ),
                )
            }
        }
}

/** Resolves the dark theme from the library palette, matching the other stock-value helpers. */
@Composable
internal fun isGlassInDarkTheme(): Boolean = RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
