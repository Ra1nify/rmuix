// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.ra1nify.rmuix.kmp.basic.DropdownColors
import io.github.ra1nify.rmuix.kmp.basic.DropdownDefaults
import io.github.ra1nify.rmuix.kmp.basic.DropdownEntry
import io.github.ra1nify.rmuix.kmp.basic.DropdownItem
import io.github.ra1nify.rmuix.kmp.basic.ListPopupDefaults
import io.github.ra1nify.rmuix.kmp.basic.PopupPositionProvider
import io.github.ra1nify.rmuix.kmp.layout.CascadingListPopupLayout
import io.github.ra1nify.rmuix.kmp.utils.RemovePlatformDialogDefaultEffects
import io.github.ra1nify.rmuix.kmp.utils.WindowNavigationEventScope
import io.github.ra1nify.rmuix.kmp.utils.platformDialogProperties

/**
 * A cascading list popup rendered at window level (as a [Dialog]) instead of inside a
 * `Scaffold`. Otherwise behaves identically to [io.github.ra1nify.rmuix.kmp.overlay.OverlayCascadingListPopup].
 *
 * @param show Whether the popup is shown.
 * @param entries Grouped dropdown entries; top-level [DropdownItem]s with non-empty
 *   [DropdownItem.children] become submenu triggers. Keep the entry and item order stable while the
 *   popup is shown; item state such as [DropdownItem.selected] may change.
 * @param onDismissRequest Invoked when the popup wants to be dismissed.
 * @param onDismissFinished Invoked after the exit animation finishes.
 * @param popupModifier Modifier applied to the popup body.
 * @param popupPositionProvider Position strategy for the primary popup relative to its anchor.
 * @param alignment Alignment of the primary popup.
 * @param enableWindowDim Whether to dim the rest of the window while the popup is shown.
 * @param maxHeight Maximum height of either side. Null bounds it by the safe area.
 * @param minWidth Minimum width of the popup.
 * @param dropdownColors Colors used by every row.
 * @param collapseOnSelection When true, selecting any leaf dismisses the popup.
 */
@Composable
fun WindowCascadingListPopup(
    show: Boolean,
    entries: List<DropdownEntry>,
    onDismissRequest: () -> Unit,
    popupModifier: Modifier = Modifier,
    onDismissFinished: (() -> Unit)? = null,
    popupPositionProvider: PopupPositionProvider = ListPopupDefaults.DropdownPositionProvider,
    alignment: PopupPositionProvider.Align = PopupPositionProvider.Align.End,
    enableWindowDim: Boolean = true,
    maxHeight: Dp? = null,
    minWidth: Dp = 200.dp,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    collapseOnSelection: Boolean = true,
) {
    val currentOnDismissRequest = rememberUpdatedState(onDismissRequest)

    CascadingListPopupLayout(
        show = show,
        popupHost = { visible, hostContent ->
            if (visible) {
                Dialog(
                    onDismissRequest = { currentOnDismissRequest.value() },
                    properties = platformDialogProperties(),
                ) {
                    RemovePlatformDialogDefaultEffects()
                    WindowNavigationEventScope {
                        hostContent()
                    }
                }
            }
        },
        entries = entries,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        popupModifier = popupModifier,
        popupPositionProvider = popupPositionProvider,
        alignment = alignment,
        enableWindowDim = enableWindowDim,
        maxHeight = maxHeight,
        minWidth = minWidth,
        dropdownColors = dropdownColors,
        collapseOnSelection = collapseOnSelection,
    )
}
