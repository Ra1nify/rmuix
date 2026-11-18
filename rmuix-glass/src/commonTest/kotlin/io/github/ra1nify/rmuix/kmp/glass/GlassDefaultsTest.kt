// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.glass

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class GlassDefaultsTest {
    @Test
    fun dialogMaterialTokensMatchTheStockResources() {
        // integers.xml: coui_dialog_blur_radius = 200
        assertEquals(200f, GlassDefaults.DIALOG_BLUR_RADIUS_PX)
        // colors.xml: coui_dialog_list_mix_blur_light / _dark, coui_dialog_list_blend_blur_light / _dark
        assertEquals(Color(0xDC909090), GlassDefaults.DialogMixLight)
        assertEquals(Color(0x8C777777), GlassDefaults.DialogMixDark)
        assertEquals(Color(0x509F9F9F), GlassDefaults.DialogBlendLight)
        assertEquals(Color(0xB2292929), GlassDefaults.DialogBlendDark)
        // integers.xml: coui_bottomdialog_blur_radius = 400; colors.xml: coui_bottomdialog_list_*_blur_*
        assertEquals(400f, GlassDefaults.BOTTOM_SHEET_BLUR_RADIUS_PX)
        assertEquals(Color(0xDCC8C8C8), GlassDefaults.BottomSheetMixLight)
        assertEquals(Color(0xA4515151), GlassDefaults.BottomSheetMixDark)
        assertEquals(Color(0x376E6E6E), GlassDefaults.BottomSheetBlendLight)
        assertEquals(Color(0xB30F0F0F), GlassDefaults.BottomSheetBlendDark)
        // coui_alert_dialog_background -> ?attr/couiColorSurface
        assertEquals(Color(0xFFFFFFFF), GlassDefaults.DialogFallbackLight)
        assertEquals(Color(0xFF1E1E1E), GlassDefaults.DialogFallbackDark)
    }
}
