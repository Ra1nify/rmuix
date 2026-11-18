// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.TextButton
import io.github.ra1nify.rmuix.kmp.glass.GlassWindowBottomSheet
import io.github.ra1nify.rmuix.kmp.glass.GlassWindowDialog

@Composable
fun GlassDialogDemo() {
    Column {
        var showDialog by remember { mutableStateOf(false) }
        var showSheet by remember { mutableStateOf(false) }
        TextButton(
            text = "Show a GlassWindowDialog",
            onClick = { showDialog = true },
        )
        TextButton(
            text = "Show a GlassWindowBottomSheet",
            onClick = { showSheet = true },
        )
        GlassWindowBottomSheet(
            show = showSheet,
            title = "Glass WindowBottomSheet",
            onDismissRequest = { showSheet = false },
        ) {
            Text(text = "Uses the platform window blur where one exists.")
        }
        GlassWindowDialog(
            show = showDialog,
            title = "Glass WindowDialog",
            summary = "On Android 12+ the window behind the dialog is blurred; elsewhere the stock tint is kept.",
            confirmButtonText = "OK",
            onConfirm = { showDialog = false },
            dismissButtonText = "Cancel",
            onDismiss = { showDialog = false },
        ) {}
    }
}
