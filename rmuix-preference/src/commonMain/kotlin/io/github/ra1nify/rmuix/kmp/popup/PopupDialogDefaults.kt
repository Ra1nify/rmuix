// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.popup

import androidx.compose.ui.unit.dp

/**
 * Layout values shared by the dialog-style dropdown and spinner popups.
 *
 * The two popups (`OverlayDropdownPopup` / `WindowDropdownPopup` and their spinner siblings) render the
 * same "list + full-width action button" dialog, so their spacing lives here instead of being repeated as
 * literals in each file.
 *
 * **Provenance:** the Settings APK declares `coui_dialog_button_height` = 50dp, but **nothing reads it** —
 * there is no `R.dimen.coui_dialog_button_height` use and no `@dimen/coui_dialog_button_height` reference
 * in any layout. The 24dp / 12dp paddings have no dialog dimen at all
 * (`coui_dialog_builder_button_content_divider_margin_horizontal` is 24dp but describes the content
 * divider, not button padding). Treat every value here as a **library default**, not a ported dimens.
 */
internal object PopupDialogDefaults {
    /** Vertical inset of the dialog surface (`DpSize(0.dp, 24.dp)` on the overlay host). */
    val InsideMarginVertical = 24.dp

    /** Horizontal padding either side of the action button. */
    val ActionPaddingHorizontal = 24.dp

    /** Padding above the action button, separating it from the list. */
    val ActionPaddingTop = 12.dp

    /**
     * Minimum height of the action button.
     *
     * Numerically equal to the unreferenced `coui_dialog_button_height` (50dp); see the note above.
     */
    val ActionButtonMinHeight = 50.dp
}
