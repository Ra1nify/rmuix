// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.preference

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class SwitchPreferenceDefaultsTest {
    @Test
    fun switchMarginMatchesTheWidgetLayout() {
        // android_preference_switch_margin_left, applied by coui_preference_widget_switch.xml
        assertEquals(16.dp, SwitchPreferenceDefaults.SwitchMarginStart)
    }
}
