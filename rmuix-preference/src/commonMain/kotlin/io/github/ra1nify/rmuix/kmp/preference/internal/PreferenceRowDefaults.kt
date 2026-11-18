// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.preference.internal

import androidx.compose.ui.unit.dp

/**
 * Layout values shared by the preference rows.
 *
 * These are **library defaults, not ported values**: the Settings APK spaces its widget slots from
 * `coui_preference_widget_*.xml` layouts whose margins differ per widget
 * (`android_preference_switch_margin_left` 16dp, `support_preference_image_margin_start` 4dp,
 * `support_preference_widget_focus_jump_margin_start` 24dp), and it has no dimen for "gap between the
 * trailing content and the row edge". Keeping the number here instead of inlining it in eight call
 * sites means the rows stay consistent and the value is visible in one place.
 */
internal object PreferenceRowDefaults {
    /**
     * End padding applied to the `endActions` slot of a preference row.
     *
     * No official dimen exists for this gap; 8dp is the library's own choice.
     */
    val EndActionsEndPadding = 8.dp
}
