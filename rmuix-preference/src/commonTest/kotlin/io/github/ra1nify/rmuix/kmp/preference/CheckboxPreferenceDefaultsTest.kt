// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.preference

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckboxPreferenceDefaultsTest {
    @Test
    fun endPaddingMatchesTheWidgetLayout() {
        // coui_preference_widget_checkbox.xml: android:paddingRight="1dp"
        assertEquals(1.dp, CheckboxPreferenceDefaults.EndPadding)
    }

    @Test
    fun widgetMarginsMatchTheCheckboxLayout() {
        // coui_preference_widget_checkbox.xml: layout_marginLeft = coui_preference_checkbox_margin_left
        // (0dp) and layout_marginRight = coui_preference_checkbox_margin_right (-10dp).
        assertEquals((-10).dp, CheckboxPreferenceDefaults.WidgetMarginEnd)
    }
}
