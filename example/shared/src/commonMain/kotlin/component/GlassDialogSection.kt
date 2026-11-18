// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CategoryTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TextButton
import io.github.ra1nify.rmuix.kmp.glass.GlassOverlayBottomSheet
import io.github.ra1nify.rmuix.kmp.glass.GlassOverlayDialog
import io.github.ra1nify.rmuix.kmp.glass.GlassWindowBottomSheet
import io.github.ra1nify.rmuix.kmp.glass.GlassWindowDialog

fun LazyListScope.glassDialogSection() {
    item(key = "glassDialog") {
        CategoryTitle(text = "Glass Dialogs")
        Card(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
        ) {
            var showOverlayDialog by remember { mutableStateOf(false) }
            var showWindowDialog by remember { mutableStateOf(false) }
            var showOverlaySheet by remember { mutableStateOf(false) }
            var showWindowSheet by remember { mutableStateOf(false) }
            TextButton(
                text = "Glass OverlayDialog",
                onClick = { showOverlayDialog = true },
            )
            TextButton(
                text = "Glass WindowDialog",
                onClick = { showWindowDialog = true },
            )
            TextButton(
                text = "Glass OverlayBottomSheet",
                onClick = { showOverlaySheet = true },
            )
            TextButton(
                text = "Glass WindowBottomSheet",
                onClick = { showWindowSheet = true },
            )
            GlassOverlayDialog(
                show = showOverlayDialog,
                title = "Glass OverlayDialog",
                summary = "Samples the backdrop installed by the app root through LocalGlassBackdrop.",
                confirmButtonText = "OK",
                onConfirm = { showOverlayDialog = false },
                dismissButtonText = "Cancel",
                onDismiss = { showOverlayDialog = false },
            ) {}
            GlassWindowDialog(
                show = showWindowDialog,
                title = "Glass WindowDialog",
                summary = "Uses the platform window blur where one exists, otherwise the stock tint.",
                confirmButtonText = "OK",
                onConfirm = { showWindowDialog = false },
            ) {}
            GlassOverlayBottomSheet(
                show = showOverlaySheet,
                title = "Glass OverlayBottomSheet",
                onDismissRequest = { showOverlaySheet = false },
            ) {
                Text(text = "Samples the backdrop installed through LocalGlassBackdrop.")
            }
            GlassWindowBottomSheet(
                show = showWindowSheet,
                title = "Glass WindowBottomSheet",
                onDismissRequest = { showWindowSheet = false },
            ) {
                Text(text = "Uses the platform window blur where one exists.")
            }
        }
    }
}
