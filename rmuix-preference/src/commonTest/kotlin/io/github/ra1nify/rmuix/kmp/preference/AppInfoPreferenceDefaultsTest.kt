// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class AppInfoPreferenceDefaultsTest {
    @Test
    fun headerMatchesCouiAppInfoPreference() {
        // coui_component_preference_application_info.xml / setting_about_preference_appinfo.xml
        assertEquals(60.dp, AppInfoPreferenceDefaults.IconSize)
        assertEquals(16.dp, AppInfoPreferenceDefaults.IconCornerRadius)
        assertEquals(42.dp, AppInfoPreferenceDefaults.IconTopMargin)
        assertEquals(8.dp, AppInfoPreferenceDefaults.NameTopMargin)
        assertEquals(4.dp, AppInfoPreferenceDefaults.VersionTopMargin)
        assertEquals(8.dp, AppInfoPreferenceDefaults.BottomMargin)
        // ?attr/couiTextAppearanceHeadline4 / ?attr/couiTextAppearanceDescription
        assertEquals(20.sp, AppInfoPreferenceDefaults.NameFontSize)
        assertEquals(12.sp, AppInfoPreferenceDefaults.VersionFontSize)
        assertEquals(
            PaddingValues(top = 42.dp, bottom = 8.dp),
            AppInfoPreferenceDefaults.InsideMargin,
        )
    }

    @Test
    fun copyBubbleMatchesCouiPopupWindow() {
        // coui_component_popup_window_layout.xml + coui_component_copy_window_* dimens
        assertEquals(76.dp, AppInfoPreferenceDefaults.CopyLabelMinWidth)
        assertEquals(48.dp, AppInfoPreferenceDefaults.CopyLabelMinHeight)
        assertEquals(24.dp, AppInfoPreferenceDefaults.CopyLabelHorizontalPadding)
        assertEquals(16.dp, AppInfoPreferenceDefaults.CopyBubbleSpacing)
        // coui_round_corner_m, shared by the bubble background and the ripple mask
        assertEquals(12.dp, AppInfoPreferenceDefaults.CopyLabelCornerRadius)
        // ?style/couiTextAppearanceBody
        assertEquals(14.sp, AppInfoPreferenceDefaults.CopyLabelFontSize)
    }
}
