// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.popup

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks the dialog-style popup spacing.
 *
 * `coui_dialog_button_height` (50dp) exists in the Settings APK but is **never read** — no `R.dimen` use,
 * no layout reference — so the 50dp assertion documents the library's value rather than a ported one. The
 * 24dp / 12dp paddings have no dialog dimen at all.
 */
class PopupDialogDefaultsTest {
    @Test
    fun dialogPopupSpacingIsTheLibraryValue() {
        assertEquals(24.dp, PopupDialogDefaults.InsideMarginVertical)
        assertEquals(24.dp, PopupDialogDefaults.ActionPaddingHorizontal)
        assertEquals(12.dp, PopupDialogDefaults.ActionPaddingTop)
        assertEquals(50.dp, PopupDialogDefaults.ActionButtonMinHeight)
    }
}
