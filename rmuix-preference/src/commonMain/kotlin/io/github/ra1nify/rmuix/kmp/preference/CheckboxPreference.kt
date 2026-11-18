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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.BasicComponent
import io.github.ra1nify.rmuix.kmp.basic.BasicComponentColors
import io.github.ra1nify.rmuix.kmp.basic.BasicComponentDefaults
import io.github.ra1nify.rmuix.kmp.basic.Checkbox
import io.github.ra1nify.rmuix.kmp.basic.CheckboxColors
import io.github.ra1nify.rmuix.kmp.basic.CheckboxDefaults
import io.github.ra1nify.rmuix.kmp.preference.internal.PreferenceRowDefaults
import io.github.ra1nify.rmuix.kmp.preference.internal.StartActionSlot

/**
 * A checkbox with a title and a summary.
 *
 * @param title The title of the [CheckboxPreference].
 * @param checked The checked state of the [CheckboxPreference].
 * @param onCheckedChange The callback when the checked state of the [CheckboxPreference] is changed.
 * @param modifier The modifier to be applied to the [CheckboxPreference].
 * @param titleColor The color of the title.
 * @param summary The summary of the [CheckboxPreference].
 * @param summaryColor The color of the summary.
 * @param checkboxColors The [CheckboxColors] of the [CheckboxPreference].
 * @param startAction The [Composable] content on the start side of the [CheckboxPreference].
 * @param endActions The [Composable] content on the end side of the [CheckboxPreference].
 * @param checkboxLocation The location of checkbox, [CheckboxLocation.Start] or [CheckboxLocation.End].
 * @param bottomAction The [Composable] content at the bottom of the [CheckboxPreference].
 * @param insideMargin The margin inside the [CheckboxPreference].
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [CheckboxPreference] is clickable.
 */
@Composable
@NonRestartableComposable
fun CheckboxPreference(
    title: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    checkboxColors: CheckboxColors = CheckboxDefaults.checkboxColors(),
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable (RowScope.() -> Unit)? = null,
    checkboxLocation: CheckboxLocation = CheckboxLocation.End,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
) {
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)

    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction =
            if (checkboxLocation == CheckboxLocation.Start || startAction != null) {
                {
                    Row {
                        if (checkboxLocation == CheckboxLocation.Start) {
                            CheckboxPreferenceStartAction(
                                checked = checked,
                                onCheckedChange = currentOnCheckedChange,
                                enabled = enabled,
                                checkboxColors = checkboxColors,
                            )
                        }

                        startAction?.let {
                            StartActionSlot(
                                modifier =
                                    Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(1f, fill = false),
                                endSpacing = 5.dp,
                                content = it,
                            )
                        }
                    }
                }
            } else {
                null
            },
        endActions = {
            endActions?.let {
                Row(
                    modifier =
                        Modifier
                            .padding(end = PreferenceRowDefaults.EndActionsEndPadding)
                            .align(Alignment.CenterVertically)
                            .weight(1f, fill = false),
                ) {
                    it()
                }
            }

            if (checkboxLocation == CheckboxLocation.End) {
                CheckboxPreferenceEndAction(
                    checked = checked,
                    onCheckedChange = currentOnCheckedChange,
                    enabled = enabled,
                    checkboxColors = checkboxColors,
                )
            }
        },
        bottomAction = bottomAction,
        onClick = {
            currentOnCheckedChange.takeIf { enabled }?.invoke(!checked)
        },
        role = Role.Checkbox,
        holdDownState = holdDownState,
        enabled = enabled,
    )
}

@Composable
private fun CheckboxPreferenceStartAction(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean,
    checkboxColors: CheckboxColors,
) {
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val currentChecked by rememberUpdatedState(checked)
    val onClick =
        remember(onCheckedChange != null) {
            if (onCheckedChange != null) {
                { currentOnCheckedChange?.invoke(!currentChecked) ?: Unit }
            } else {
                null
            }
        }
    Checkbox(
        modifier =
            Modifier
                .padding(end = 5.dp),
        state = ToggleableState(checked),
        onClick = onClick,
        enabled = enabled,
        colors = checkboxColors,
    )
}

@Composable
private fun CheckboxPreferenceEndAction(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean,
    checkboxColors: CheckboxColors,
) {
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val currentChecked by rememberUpdatedState(checked)
    val onClick =
        remember(onCheckedChange != null) {
            if (onCheckedChange != null) {
                { currentOnCheckedChange?.invoke(!currentChecked) ?: Unit }
            } else {
                null
            }
        }
    Checkbox(
        // `coui_preference_widget_checkbox.xml` adds `android:paddingRight="1dp"` to the widget and
        // `android:layout_marginRight` = `coui_preference_checkbox_margin_right` = -10dp; the helper
        // reports a 10dp narrower box so the checkbox overhangs the row's 32dp end padding by 9dp.
        modifier =
            Modifier
                .checkboxWidgetMarginEnd(CheckboxPreferenceDefaults.WidgetMarginEnd)
                .padding(end = CheckboxPreferenceDefaults.EndPadding),
        state = ToggleableState(checked),
        onClick = onClick,
        enabled = enabled,
        colors = checkboxColors,
    )
}

/** Contains default values used by [CheckboxPreference]. */
object CheckboxPreferenceDefaults {
    /**
     * End margin of the trailing checkbox: `coui_preference_checkbox_margin_right` = **-10dp**, a
     * negative margin that lets the widget overhang the row padding toward the screen edge.
     * [checkboxWidgetMarginEnd] reproduces it by shrinking the reported width and shifting the placement,
     * so the drawing and the touch target both move.
     */
    val WidgetMarginEnd = (-10).dp

    /**
     * End padding of the trailing checkbox: ColorOS `coui_preference_widget_checkbox.xml` adds
     * `android:paddingRight` = 1dp.
     */
    val EndPadding = 1.dp
}

/**
 * Applies `android:layout_marginRight` = [marginEnd] (negative) to the trailing checkbox the way a
 * `LinearLayout` does: the widget's left edge is unchanged, while its reported width shrinks by the
 * margin so the drawing (and hit area) overhangs the parent's end content edge.
 */
private fun Modifier.checkboxWidgetMarginEnd(marginEnd: Dp): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val marginPx = marginEnd.roundToPx()
        layout(width = (placeable.width + marginPx).coerceAtLeast(0), height = placeable.height) {
            placeable.place(x = 0, y = 0)
        }
    }

enum class CheckboxLocation {
    Start,
    End,
}
