// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.preference

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.BasicComponent
import io.github.ra1nify.rmuix.kmp.basic.BasicComponentColors
import io.github.ra1nify.rmuix.kmp.basic.BasicComponentDefaults
import io.github.ra1nify.rmuix.kmp.icon.basic.ArrowRight
import io.github.ra1nify.rmuix.kmp.preference.internal.PreferenceChevron
import io.github.ra1nify.rmuix.kmp.preference.internal.PreferenceChevronHeight
import io.github.ra1nify.rmuix.kmp.preference.internal.PreferenceChevronWidth
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * ColorOS 17 Jump Card Preference item (`COUIJumpPreference`).
 *
 * An interactive preference row with a trailing chevron (`coui_preference_widget_jump.xml`), supporting
 * title, summary, third-line assignment and unread badges.
 *
 * This is the implementation: `ArrowPreference` used to be the same widget under a second name and was
 * removed on 2026-11-14 (no `COUIArrowPreference` exists in the dump - the arrow is part of
 * `COUIPreference`'s jump mark).
 *
 * @param title The title of the [JumpPreference].
 * @param modifier The modifier to be applied to the [JumpPreference].
 * @param titleColor The color of the title.
 * @param summary The summary of the [JumpPreference].
 * @param summaryColor The color of the summary.
 * @param assignment Optional third line, drawn under the summary in the primary text colour.
 * @param assignmentColor The color of the assignment line.
 * @param startAction The [Composable] content on the start side of the [JumpPreference].
 * @param endActions The [Composable] content on the end side before the chevron arrow.
 * @param bottomAction The [Composable] content at the bottom of the [JumpPreference].
 * @param insideMargin The margin inside the [JumpPreference].
 * @param onClick The callback when the [JumpPreference] is clicked.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [JumpPreference] is clickable.
 * @param iconBadge Draws a hint dot on the top-end corner of [startAction].
 * @param iconBadgeCount Number shown inside [iconBadge]; `0` renders a plain dot.
 * @param endBadge Draws a small hint dot before [endActions].
 * @param statusText Optional status line.
 * @param statusTextColor The color of [statusText].
 */
@Composable
@NonRestartableComposable
fun JumpPreference(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable RowScope.() -> Unit = {},
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    onClick: (() -> Unit)? = null,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
    assignment: String? = null,
    assignmentColor: BasicComponentColors = BasicComponentDefaults.assignmentColor(),
    iconBadge: Boolean = false,
    iconBadgeCount: Int = 0,
    endBadge: Boolean = false,
    statusText: String? = null,
    statusTextColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
) {
    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        assignment = assignment,
        assignmentColor = assignmentColor,
        startAction = startAction,
        endActions = {
            Row(
                modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f, fill = false),
            ) {
                endActions()
            }
            JumpPreferenceEndAction(
                enabled = enabled,
            )
        },
        bottomAction = bottomAction,
        onClick = onClick,
        holdDownState = holdDownState,
        iconBadge = iconBadge,
        iconBadgeCount = iconBadgeCount,
        endBadge = endBadge,
        statusText = statusText,
        statusTextColor = statusTextColor,
        enabled = enabled,
    )
}

@Composable
private fun RowScope.JumpPreferenceEndAction(enabled: Boolean) {
    val actionColors = JumpPreferenceDefaults.endActionColors()
    val layoutDirection = LocalLayoutDirection.current
    PreferenceChevron(
        color = actionColors.color(enabled = enabled),
        layoutDirection = layoutDirection,
        modifier =
            Modifier
                .padding(start = JumpPreferenceDefaults.ArrowMarginStart)
                .size(width = PreferenceChevronWidth, height = PreferenceChevronHeight)
                .align(Alignment.CenterVertically),
    )
}

object JumpPreferenceDefaults {
    /**
     * Start margin of the chevron: `coui_preference_widget_jump.xml` sets
     * `android:layout_marginStart="@dimen/support_preference_image_margin_start"` = 4dp (note this
     * is smaller than the 16dp `support_preference_image_padding_start` used by the select row).
     */
    val ArrowMarginStart = 4.dp

    /**
     * The default color of the arrow.
     */
    @Composable
    fun endActionColors(): EndActionColors {
        // ColorOS colours the chevron with couiColorLabelTertiary (#42000000 / #4DFFFFFF, 26%) and steps
        // down to couiColorLabelQuaternary (#14000000 / #26FFFFFF, 8%) when disabled. The miuix token
        // onSurfaceVariantActions sits at 40%, which reads noticeably darker than the system arrow.
        val color = if (isChevronInDarkTheme()) Color(0x4DFFFFFF) else Color(0x42000000)
        val disabledColor = if (isChevronInDarkTheme()) Color(0x26FFFFFF) else Color(0x14000000)
        return remember(color, disabledColor) {
            EndActionColors(
                color = color,
                disabledColor = disabledColor,
            )
        }
    }
}

@Immutable
data class EndActionColors(
    private val color: Color,
    private val disabledColor: Color,
) {
    @Stable
    internal fun color(enabled: Boolean): Color = if (enabled) color else disabledColor
}

/** Resolves the neutral palette from the library theme instead of the OS theme. */
@Composable
private fun isChevronInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
