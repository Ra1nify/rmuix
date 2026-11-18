// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.blur

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LegacyBackdropFallbackTest {
    @Test
    fun legacyTintsKeepOnlySrcOverLayers() {
        val colors =
            BlurColors(
                blendColors =
                    listOf(
                        BlendColorEntry(Color(0xDC909090), BlurBlendMode.SrcOver),
                        BlendColorEntry(Color(0x509F9F9F), BlurBlendMode.Overlay),
                        BlendColorEntry(Color(0xB2292929), BlurBlendMode.SrcOver),
                    ),
            )
        val legacy = legacyTintEntries(colors)
        assertEquals(2, legacy.size)
        assertTrue(legacy.all { it.mode == BlurBlendMode.SrcOver })
        assertEquals(Color(0xDC909090), legacy[0].color)
        assertEquals(Color(0xB2292929), legacy[1].color)
    }

    @Test
    fun legacyTintsAreEmptyForShaderOnlyMaterials() {
        val colors = BlurColors(blendColors = listOf(BlendColorEntry(Color.White, BlurBlendMode.Luminosity)))
        assertTrue(legacyTintEntries(colors).isEmpty())
    }
}
