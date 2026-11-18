// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.extend.colorpicker

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.test.Test
import kotlin.test.assertEquals

class HsvConversionTest {
    @Test
    fun primaryColoursConvert() {
        val red = colorToHsv(Color(0xFFFF0000))
        assertEquals(0f, red.hue)
        assertEquals(1f, red.saturation)
        assertEquals(1f, red.value)

        assertEquals(120f, colorToHsv(Color(0xFF00FF00)).hue)
        assertEquals(240f, colorToHsv(Color(0xFF0000FF)).hue)
        assertEquals(0f, colorToHsv(Color(0xFFFFFFFF)).saturation)
        assertEquals(0f, colorToHsv(Color(0xFF000000)).value)
    }

    @Test
    fun hsvMapsBackToTheSameColour() {
        // #0080FF is the ColorOS primary blue: hue 210, full saturation and value.
        val hsv = colorToHsv(Color(0xFF0080FF))
        assertEquals(210f, hsv.hue, 1f)
        val roundTrip = hsvToColor(hsv.hue, hsv.saturation, hsv.value, 1f)
        assertEquals(0xFF0080FF.toInt(), roundTrip.toArgb())
    }

    @Test
    fun hexReadoutHidesOpaqueAlphaAndShowsTranslucentAlpha() {
        assertEquals("#0080FF", Color(0xFF0080FF).toHexString(includeAlpha = true))
        assertEquals("#800080FF", Color(0x800080FF).toHexString(includeAlpha = true))
        // without the alpha switch the colour is still six digits
        assertEquals("#0080FF", Color(0x800080FF).toHexString(includeAlpha = false))
    }
}
