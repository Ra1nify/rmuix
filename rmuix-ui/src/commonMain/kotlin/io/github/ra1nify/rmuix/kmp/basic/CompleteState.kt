// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Defaults for [CompleteState], from `coui_component_complete_state.xml` and `COUICompleteStateView`.
 */
object CompleteStateDefaults {
    /** The `100dp` `EffectiveAnimationView` of the layout. */
    val IllustrationSize = 100.dp

    /** `complete_page_text_padding_hor`, the illustration's horizontal margin and the text's padding. */
    val HorizontalPadding = 8.dp

    /** `?attr/couiTextHeadlineM`. */
    val TitleFontSize = 20.sp

    /** `complete_page_text_title_margin_top`. */
    val TitleTopMargin = 8.dp

    /** `?attr/couiTextBodyXS`. */
    val SubtitleFontSize = 12.sp

    /** `complete_page_text_subtitle_margin_top`. */
    val SubtitleTopMargin = 8.dp
}

/**
 * The ColorOS completion page: `COUICompleteStateView` (`coui_component_complete_state.xml`).
 *
 * The sibling of [EmptyState]: same centred column, but a 100dp illustration, a 20sp title and an
 * 8dp gap before the subtitle. Stock drives the illustration with an `EffectiveAnimationView`, whose
 * asset ships in the library rather than the APK, so the slot is left to the caller.
 *
 * @param title The title, `?attr/couiTextHeadlineM` (20sp medium).
 * @param modifier The modifier to be applied to the page.
 * @param subtitle The optional subtitle, `?attr/couiTextBodyXS` (12sp).
 * @param illustration The illustration shown above the text; a 100dp slot when `null`.
 * @param colors The colours of the page.
 */
@Composable
fun CompleteState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    illustration: (@Composable () -> Unit)? = null,
    colors: EmptyStateColors = EmptyStateDefaults.emptyStateColors(),
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (illustration != null) {
            illustration()
        } else {
            // The layout reserves the 100dp animation slot even when no asset is supplied.
            Column(modifier = Modifier.size(CompleteStateDefaults.IllustrationSize)) {}
        }
        Text(
            modifier =
                Modifier
                    .padding(
                        start = CompleteStateDefaults.HorizontalPadding,
                        top = CompleteStateDefaults.TitleTopMargin,
                        end = CompleteStateDefaults.HorizontalPadding,
                    ),
            text = title,
            fontSize = CompleteStateDefaults.TitleFontSize,
            fontWeight = FontWeight.Medium,
            color = colors.titleColor,
            textAlign = TextAlign.Center,
        )
        if (subtitle != null) {
            Text(
                modifier =
                    Modifier
                        .padding(
                            start = CompleteStateDefaults.HorizontalPadding,
                            top = CompleteStateDefaults.SubtitleTopMargin,
                            end = CompleteStateDefaults.HorizontalPadding,
                        ),
                text = subtitle,
                fontSize = CompleteStateDefaults.SubtitleFontSize,
                color = colors.subtitleColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}
