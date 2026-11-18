// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.preference

import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.preference.internal.PreferenceRowDefaults
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks the shared preference-row spacing.
 *
 * `EndActionsEndPadding` has **no official dimen**: the Settings APK spaces widget slots from
 * `coui_preference_widget_*.xml` layouts whose margins differ per widget, and none of them describes the
 * gap between the trailing content and the row edge. The assertion therefore documents the library's own
 * value so a future change is a deliberate one.
 */
class PreferenceRowDefaultsTest {
    @Test
    fun endActionsPaddingIsTheLibraryValue() {
        assertEquals(8.dp, PreferenceRowDefaults.EndActionsEndPadding)
    }
}
