// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CardDefaults
import io.github.ra1nify.rmuix.kmp.basic.Icon
import io.github.ra1nify.rmuix.kmp.basic.IconButton
import io.github.ra1nify.rmuix.kmp.basic.SmallTitle
import io.github.ra1nify.rmuix.kmp.basic.TextField
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.extended.Close
import io.github.ra1nify.rmuix.kmp.icon.extended.Ok
import io.github.ra1nify.rmuix.kmp.overlay.OverlayBottomSheet
import io.github.ra1nify.rmuix.kmp.preference.ArrowPreference
import io.github.ra1nify.rmuix.kmp.preference.OverlayDropdownPreference
import io.github.ra1nify.rmuix.kmp.preference.SwitchPreference
import io.github.ra1nify.rmuix.kmp.preference.WindowDropdownPreference
import io.github.ra1nify.rmuix.kmp.theme.LocalDismissState
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.overScrollVertical
import io.github.ra1nify.rmuix.kmp.utils.scrollEndHaptic
import io.github.ra1nify.rmuix.kmp.window.WindowBottomSheet

private val BottomSheetDropdownOptions = listOf("Option 1", "Option 2")

fun LazyListScope.bottomSheetSection() {
    item(key = "bottomSheet") {
        var showSuperBottomSheet by remember { mutableStateOf(false) }
        var showWindowBottomSheet by remember { mutableStateOf(false) }
        var superBottomSheetHoldDown by remember { mutableStateOf(false) }
        var windowBottomSheetHoldDown by remember { mutableStateOf(false) }
        var bottomSheetDropdownSelectedOption by remember { mutableIntStateOf(0) }
        var bottomSheetSuperSwitchState by remember { mutableStateOf(true) }

        SmallTitle(text = "BottomSheet")
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
        ) {
            ArrowPreference(
                title = "BottomSheet (O)",
                summary = "Click to show an OverlayBottomSheet",
                onClick = {
                    showSuperBottomSheet = true
                    superBottomSheetHoldDown = true
                },
                holdDownState = superBottomSheetHoldDown,
            )
            ArrowPreference(
                title = "BottomSheet (W)",
                summary = "Click to show a WindowBottomSheet",
                onClick = {
                    showWindowBottomSheet = true
                    windowBottomSheetHoldDown = true
                },
                holdDownState = windowBottomSheetHoldDown,
            )
        }

        SuperBottomSheetDemo(
            show = showSuperBottomSheet,
            onDismissRequest = { showSuperBottomSheet = false },
            dropdownSelectedIndex = bottomSheetDropdownSelectedOption,
            onDropdownSelectedIndexChange = { bottomSheetDropdownSelectedOption = it },
            switchChecked = bottomSheetSuperSwitchState,
            onSwitchCheckedChange = { bottomSheetSuperSwitchState = it },
            onDismissFinished = { superBottomSheetHoldDown = false },
        )
        WindowBottomSheetDemo(
            show = showWindowBottomSheet,
            onDismissRequest = { showWindowBottomSheet = false },
            dropdownSelectedIndex = bottomSheetDropdownSelectedOption,
            onDropdownSelectedIndexChange = { bottomSheetDropdownSelectedOption = it },
            switchChecked = bottomSheetSuperSwitchState,
            onSwitchCheckedChange = { bottomSheetSuperSwitchState = it },
            onDismissFinished = { windowBottomSheetHoldDown = false },
        )
    }
}

@Composable
private fun SuperBottomSheetDemo(
    show: Boolean,
    onDismissRequest: () -> Unit,
    dropdownSelectedIndex: Int,
    onDropdownSelectedIndexChange: (Int) -> Unit,
    switchChecked: Boolean,
    onSwitchCheckedChange: (Boolean) -> Unit,
    onDismissFinished: () -> Unit,
) {
    var allowDismiss by remember { mutableStateOf(true) }
    var enableNestedScroll by remember { mutableStateOf(true) }

    OverlayBottomSheet(
        title = "BottomSheet (O)",
        show = show,
        allowDismiss = allowDismiss,
        enableNestedScroll = enableNestedScroll,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        startAction = {
            BottomSheetActionButton(RmuixIcons.Close, "Cancel", onClick = onDismissRequest)
        },
        endAction = {
            BottomSheetActionButton(RmuixIcons.Ok, "Confirm", onClick = onDismissRequest)
        },
    ) {
        BottomSheetContent(
            allowDismiss = allowDismiss,
            onAllowDismissChange = { allowDismiss = it },
            enableNestedScroll = enableNestedScroll,
            onEnableNestedScrollChange = { enableNestedScroll = it },
            switchChecked = switchChecked,
            onSwitchCheckedChange = onSwitchCheckedChange,
        ) {
            OverlayDropdownPreference(
                title = "DropdownPref (O)",
                items = BottomSheetDropdownOptions,
                selectedIndex = dropdownSelectedIndex,
                onSelectedIndexChange = onDropdownSelectedIndexChange,
            )
        }
    }
}

@Composable
private fun WindowBottomSheetDemo(
    show: Boolean,
    onDismissRequest: () -> Unit,
    dropdownSelectedIndex: Int,
    onDropdownSelectedIndexChange: (Int) -> Unit,
    switchChecked: Boolean,
    onSwitchCheckedChange: (Boolean) -> Unit,
    onDismissFinished: () -> Unit,
) {
    var allowDismiss by remember { mutableStateOf(true) }
    var enableNestedScroll by remember { mutableStateOf(true) }

    WindowBottomSheet(
        title = "BottomSheet (W)",
        show = show,
        allowDismiss = allowDismiss,
        enableNestedScroll = enableNestedScroll,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        startAction = {
            val dismissState = LocalDismissState.current
            BottomSheetActionButton(RmuixIcons.Close, "Cancel", onClick = { dismissState?.invoke() })
        },
        endAction = {
            val dismissState = LocalDismissState.current
            BottomSheetActionButton(RmuixIcons.Ok, "Confirm", onClick = { dismissState?.invoke() })
        },
    ) {
        BottomSheetContent(
            allowDismiss = allowDismiss,
            onAllowDismissChange = { allowDismiss = it },
            enableNestedScroll = enableNestedScroll,
            onEnableNestedScrollChange = { enableNestedScroll = it },
            switchChecked = switchChecked,
            onSwitchCheckedChange = onSwitchCheckedChange,
        ) {
            WindowDropdownPreference(
                title = "DropdownPref (W)",
                items = BottomSheetDropdownOptions,
                selectedIndex = dropdownSelectedIndex,
                onSelectedIndexChange = onDropdownSelectedIndexChange,
            )
        }
    }
}

/**
 * Shared content of both BottomSheet demos. The only part that differs between the Overlay and
 * Window variants is the dropdown preference, supplied through the [dropdown] slot.
 */
@Composable
private fun BottomSheetContent(
    allowDismiss: Boolean,
    onAllowDismissChange: (Boolean) -> Unit,
    enableNestedScroll: Boolean,
    onEnableNestedScrollChange: (Boolean) -> Unit,
    switchChecked: Boolean,
    onSwitchCheckedChange: (Boolean) -> Unit,
    dropdown: @Composable () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
            .scrollEndHaptic()
            .overScrollVertical(),
    ) {
        item {
            SmallTitle(
                text = "Behavior Settings",
                insideMargin = PaddingValues(16.dp, 8.dp),
            )
            Card(
                modifier = Modifier.padding(bottom = 12.dp),
                colors = CardDefaults.defaultColors(
                    color = RmuixTheme.colorScheme.secondaryContainer,
                ),
            ) {
                SwitchPreference(
                    title = "Allow Dismiss",
                    summary = "Drag or Back to dismiss",
                    checked = allowDismiss,
                    onCheckedChange = onAllowDismissChange,
                )
                SwitchPreference(
                    title = "Enable NestedScroll",
                    summary = "Scroll content vs Drag sheet",
                    checked = enableNestedScroll,
                    onCheckedChange = onEnableNestedScrollChange,
                )
            }
        }
        item {
            var textFieldValue by remember { mutableStateOf("") }
            TextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                label = "TextField",
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Card(
                modifier = Modifier.padding(bottom = 12.dp),
                colors = CardDefaults.defaultColors(
                    color = RmuixTheme.colorScheme.secondaryContainer,
                ),
            ) {
                dropdown()
                SwitchPreference(
                    title = "SwitchPref",
                    checked = switchChecked,
                    onCheckedChange = onSwitchCheckedChange,
                )
            }
            Spacer(
                Modifier.padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() +
                        WindowInsets.captionBar.asPaddingValues().calculateBottomPadding(),
                ),
            )
        }
    }
}

@Composable
private fun BottomSheetActionButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = RmuixTheme.colorScheme.onBackground,
        )
    }
}
