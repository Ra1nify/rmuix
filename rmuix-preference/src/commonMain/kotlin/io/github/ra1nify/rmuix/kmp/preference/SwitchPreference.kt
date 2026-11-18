// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.BasicComponent
import io.github.ra1nify.rmuix.kmp.basic.BasicComponentColors
import io.github.ra1nify.rmuix.kmp.basic.BasicComponentDefaults
import io.github.ra1nify.rmuix.kmp.basic.Switch
import io.github.ra1nify.rmuix.kmp.basic.SwitchColors
import io.github.ra1nify.rmuix.kmp.basic.SwitchDefaults

/**
 * A switch with a title and a summary.
 *
 * @param checked The checked state of the [SwitchPreference].
 * @param onCheckedChange The callback when the checked state of the [SwitchPreference] is changed.
 * @param title The title of the [SwitchPreference].
 * @param modifier The modifier to be applied to the [SwitchPreference].
 * @param titleColor The color of the title.
 * @param summary The summary of the [SwitchPreference].
 * @param summaryColor The color of the summary.
 * @param startAction The [Composable] content on the start side of the [SwitchPreference].
 * @param endActions The [Composable] content on the end side of the [SwitchPreference].
 * @param bottomAction The [Composable] content at the bottom of the [SwitchPreference].
 * @param switchColors The [SwitchColors] of the [SwitchPreference].
 * @param insideMargin The margin inside the [SwitchPreference].
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [SwitchPreference] is clickable.
 * @param shortcutHint An optional second summary shown **instead of** [summary] while the switch is on.
 *   The stock roui shortcut-switch row swaps its description for the "open the detail page" hint once the
 *   feature is enabled; the row click is expected to be the shortcut in that state (see [onClick]).
 * @param onClick An optional row-click override. When `null` (the default) the row toggles the switch,
 *   which is the miuix behaviour; a non-null value replaces that toggle so a checked row can open a
 *   detail page while the switch keeps working on its own.
 * @param shortcutOnlyWhenChecked When `true` and [onClick] is non-null, the row click only acts as the
 *   shortcut while the switch is **on**; while it is off the row enables the feature instead (toggles
 *   to `true`). This restores the stock shortcut-switch behaviour, where turning the feature on from
 *   the row is the expected first tap and a row click on an off row must not navigate away.
 */
@Composable
@NonRestartableComposable
fun SwitchPreference(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    shortcutHint: String? = null,
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable RowScope.() -> Unit = {},
    bottomAction: (@Composable () -> Unit)? = null,
    switchColors: SwitchColors = SwitchDefaults.switchColors(),
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    shortcutOnlyWhenChecked: Boolean = false,
) {
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val currentOnClick by rememberUpdatedState(onClick)

    // 行点击语义：
    // - 未提供 onClick：整行切换开关（miuix 行为）
    // - shortcutOnlyWhenChecked：开关为开时整行跳转，为关时整行开启功能（ColorOS 快捷键行行为）
    // - 其他：整行跳转，开关本体仍独立可切换
    val rowClick: () -> Unit =
        when {
            currentOnClick == null -> {
                { if (enabled) currentOnCheckedChange(!checked) }
            }

            shortcutOnlyWhenChecked -> {
                {
                    if (checked) {
                        currentOnClick?.invoke()
                    } else if (enabled) {
                        currentOnCheckedChange(true)
                    }
                }
            }

            else -> {
                currentOnClick ?: {}
            }
        }
    val displayedSummary =
        if (checked && !shortcutHint.isNullOrBlank()) shortcutHint else summary
    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        title = title,
        titleColor = titleColor,
        summary = displayedSummary,
        summaryColor = summaryColor,
        startAction = startAction,
        endActions = {
            Row(
                modifier =
                    Modifier
                        .padding(end = SwitchPreferenceDefaults.SwitchMarginStart)
                        .align(Alignment.CenterVertically)
                        .weight(1f, fill = false),
            ) {
                endActions()
            }
            SwitchPreferenceEndActions(
                checked = checked,
                onCheckedChange = currentOnCheckedChange,
                enabled = enabled,
                switchColors = switchColors,
            )
        },
        bottomAction = bottomAction,
        onClick = rowClick,
        role = Role.Switch,
        holdDownState = holdDownState,
        enabled = enabled,
    )
}

@Composable
private fun SwitchPreferenceEndActions(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean,
    switchColors: SwitchColors,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        colors = switchColors,
    )
}

/** Contains default values used by [SwitchPreference]. */
object SwitchPreferenceDefaults {
    /**
     * Margin between the trailing content and the switch widget: ColorOS
     * `coui_preference_widget_switch.xml` sets `android:layout_marginStart` on the switch to
     * `@dimen/android_preference_switch_margin_left` = 16dp.
     */
    val SwitchMarginStart = 16.dp
}
