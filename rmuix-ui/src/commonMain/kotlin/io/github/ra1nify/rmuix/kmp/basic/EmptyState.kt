// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * Colours of an [EmptyState] and a [CompleteState].
 *
 * @param titleColor The title (`?attr/couiColorLabelPrimary`).
 * @param subtitleColor The subtitle (`?attr/couiColorLabelSecondary`).
 * @param actionColor The action text (`?attr/couiColorPrimaryText`); only [EmptyState] has one.
 */
@Immutable
data class EmptyStateColors(
    val titleColor: Color,
    val subtitleColor: Color,
    val actionColor: Color,
)

/**
 * Defaults for [EmptyState], from `coui_component_empty_state.xml` and `COUIEmptyStateView`.
 */
object EmptyStateDefaults {
    /** `coui_component_empty_group_width`: the width of the title/subtitle block. */
    val GroupWidth = 280.dp

    /**
     * `coui_component_empty_text_scroll_max_height`: the title/subtitle block scrolls past this
     * height, so a long message never pushes the action off screen.
     */
    val TextGroupMaxHeight = 92.dp

    /** `?attr/couiTextHeadlineXS`. */
    val TitleFontSize = 16.sp

    /** `android:maxLines` of the title. */
    val TitleMaxLines = 2

    /** `?attr/couiTextBodyXS`. */
    val SubtitleFontSize = 12.sp

    /** `android:layout_marginTop` of the subtitle. */
    val SubtitleTopMargin = 2.dp

    /** `android:maxLines` of the subtitle. */
    val SubtitleMaxLines = 3

    /** `?attr/couiTextButtonM`. */
    val ActionFontSize = 14.sp

    /** `android:layout_marginTop` of the action. */
    val ActionTopMargin = 8.dp

    /**
     * The stock colours.
     *
     * `?attr/couiColorLabelPrimary` is `#E6000000` / `#E6FFFFFF`, `?attr/couiColorLabelSecondary` is
     * `#8A000000` / `#8AFFFFFF`, and `?attr/couiColorPrimaryText` resolves through `Theme.COUI` to
     * `coui_color_primary_text_blue` (`#0080FF` / `#5C9DFF`).
     */
    @Composable
    fun emptyStateColors(
        titleColor: Color = if (isEmptyStateDarkTheme()) Color(0xE6FFFFFF) else Color(0xE6000000),
        subtitleColor: Color = if (isEmptyStateDarkTheme()) Color(0x8AFFFFFF) else Color(0x8A000000),
        actionColor: Color = if (isEmptyStateDarkTheme()) Color(0xFF5C9DFF) else Color(0xFF0080FF),
    ): EmptyStateColors =
        remember(titleColor, subtitleColor, actionColor) {
            EmptyStateColors(
                titleColor = titleColor,
                subtitleColor = subtitleColor,
                actionColor = actionColor,
            )
        }
}

@Composable
internal fun isEmptyStateDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() <
        RmuixTheme.colorScheme.onBackground.luminance()

/**
 * The ColorOS empty-state page: `COUIEmptyStateView` (`coui_component_empty_state.xml`).
 *
 * A centred illustration followed by a 280dp-wide title/subtitle block and an optional action. The
 * block is height-capped at `92dp` (stock wraps it in a `COUIMaxHeightScrollView`), the title is capped
 * at two lines and the subtitle at three.
 *
 * The illustration slot replaces the `EmptyStateAnimView`, whose animation asset ships in the
 * `coui-support-emptyview` library and is not part of the APK's resources; stock hides the view
 * entirely when no size is set (`setAnimSize` with a zero dimension).
 *
 * @param title The title, `?attr/couiTextHeadlineXS` (16sp medium).
 * @param modifier The modifier to be applied to the page.
 * @param subtitle The optional subtitle, `?attr/couiTextBodyXS` (12sp).
 * @param actionText The optional action label, `?attr/couiTextButtonM` (14sp medium).
 * @param onActionClick Called when [actionText] is tapped.
 * @param illustration The illustration shown above the text; nothing is drawn when `null`.
 * @param colors The colours of the page.
 */
@Composable
fun EmptyState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    illustration: (@Composable () -> Unit)? = null,
    colors: EmptyStateColors = EmptyStateDefaults.emptyStateColors(),
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (illustration != null) {
            illustration()
        }
        Column(
            modifier =
                Modifier
                    .width(EmptyStateDefaults.GroupWidth)
                    .heightIn(max = EmptyStateDefaults.TextGroupMaxHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                fontSize = EmptyStateDefaults.TitleFontSize,
                fontWeight = FontWeight.Medium,
                color = colors.titleColor,
                textAlign = TextAlign.Center,
                maxLines = EmptyStateDefaults.TitleMaxLines,
            )
            if (subtitle != null) {
                Text(
                    modifier = Modifier.padding(top = EmptyStateDefaults.SubtitleTopMargin),
                    text = subtitle,
                    fontSize = EmptyStateDefaults.SubtitleFontSize,
                    color = colors.subtitleColor,
                    textAlign = TextAlign.Center,
                    maxLines = EmptyStateDefaults.SubtitleMaxLines,
                )
            }
        }
        if (actionText != null) {
            Text(
                modifier =
                    Modifier
                        .padding(top = EmptyStateDefaults.ActionTopMargin)
                        .then(if (onActionClick != null) Modifier.clickable(onClick = onActionClick) else Modifier),
                text = actionText,
                fontSize = EmptyStateDefaults.ActionFontSize,
                fontWeight = FontWeight.Medium,
                color = colors.actionColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}
