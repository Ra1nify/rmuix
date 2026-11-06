// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.preference

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.BasicComponent
import io.github.ra1nify.rmuix.kmp.basic.BasicComponentColors
import io.github.ra1nify.rmuix.kmp.basic.BasicComponentDefaults
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.ArrowRight
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * An arrow with a title and a summary.
 *
 * @param title The title of the [ArrowPreference].
 * @param titleColor The color of the title.
 * @param summary The summary of the [ArrowPreference].
 * @param summaryColor The color of the summary.
 * @param startAction The [Composable] content on the start side of the [ArrowPreference].
 * @param endActions The [Composable] content on the end side of the [ArrowPreference].
 * @param bottomAction The [Composable] content at the bottom of the [ArrowPreference].
 * @param modifier The modifier to be applied to the [ArrowPreference].
 * @param insideMargin The margin inside the [ArrowPreference].
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [ArrowPreference] is clickable.
 */
@Composable
@NonRestartableComposable
fun ArrowPreference(
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
) {
    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        startAction = startAction,
        endActions = {
            Row(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.CenterVertically)
                    .weight(1f, fill = false),
            ) {
                endActions()
            }
            ArrowPreferenceEndAction(
                enabled = enabled,
            )
        },
        bottomAction = bottomAction,
        onClick = onClick,
        holdDownState = holdDownState,
        enabled = enabled,
    )
}

@Composable
private fun RowScope.ArrowPreferenceEndAction(
    enabled: Boolean,
) {
    val actionColors = ArrowPreferenceDefaults.endActionColors()
    val tintFilter = remember(enabled, actionColors) {
        ColorFilter.tint(actionColors.color(enabled = enabled))
    }
    val layoutDirection = LocalLayoutDirection.current
    Image(
        modifier = Modifier
            .size(width = 10.dp, height = 16.dp)
            .graphicsLayer {
                scaleX = if (layoutDirection == LayoutDirection.Rtl) -1f else 1f
            }
            .align(Alignment.CenterVertically),
        imageVector = RmuixIcons.Basic.ArrowRight,
        contentDescription = null,
        colorFilter = tintFilter,
    )
}

object ArrowPreferenceDefaults {
    /**
     * The default color of the arrow.
     */
    @Composable
    fun endActionColors(): EndActionColors {
        // ColorOS colours the chevron with couiColorLabelTertiary (#42000000 / #4DFFFFFF, 26%) and steps
        // down to couiColorLabelQuaternary (#14000000 / #26FFFFFF, 8%) when disabled. The miuix token
        // onSurfaceVariantActions sits at 40%, which reads noticeably darker than the system arrow.
        val color = if (isSystemInDarkTheme()) Color(0x4DFFFFFF) else Color(0x42000000)
        val disabledColor = if (isSystemInDarkTheme()) Color(0x26FFFFFF) else Color(0x14000000)
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
