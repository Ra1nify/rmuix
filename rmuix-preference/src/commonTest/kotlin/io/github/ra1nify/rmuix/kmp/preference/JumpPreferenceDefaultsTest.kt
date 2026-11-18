// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.preference

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class JumpPreferenceDefaultsTest {
    @Test
    fun arrowMarginMatchesTheJumpWidgetLayout() {
        // coui_preference_widget_jump.xml: android:layout_marginStart =
        // @dimen/support_preference_image_margin_start (4dp)
        assertEquals(4.dp, JumpPreferenceDefaults.ArrowMarginStart)
    }
}
