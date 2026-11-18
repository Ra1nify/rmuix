// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * Defaults for [CategoryTitle], taken from `COUIPreferenceCategory` and the
 * `coui_preference_category_*` / `support_preference_category_*` resources in the ColorOS settings dump.
 */
object CategoryTitleDefaults {
    /**
     * `Widget.COUI.List.Category.Title` sets `@style/couiTextAppearanceSmallButton`:
     * `textSize` 12sp, `fontFamily` `sans-serif-medium`, `lineSpacingMultiplier` 1
     * (`COUIPreferenceCategory` also calls `setTextSize(2, 12.0f)` for its default title type).
     */
    val TextSize = 12.sp

    /** `couiTextAppearanceSmallButton` -> `android:fontFamily="sans-serif-medium"`. */
    val TextWeight = FontWeight.Medium

    /** `coui_preference_category_text_height`: the title view's minimum height. */
    val MinHeight = 16.dp

    /**
     * `support_preference_category_layout_title_margin_start_large` = 32dp.
     *
     * `COUIPreferenceCategory` initialises `mHorizontalMarginType = 1` (large), reads
     * `title_margin_start_type` from the preference XML, and no Settings preference XML overrides it, so
     * stock category titles sit 32dp in - aligned with the preference row inset.
     */
    val MarginStart = 32.dp

    /** `coui_category_title_pading_end_with_reddot_default`. */
    val MarginEnd = 8.dp

    /**
     * `coui_preference_category_margintop_small` = 12dp.
     *
     * The category item's top margin (`COUIPreferenceCategory.onBindViewHolder`): `top_margin_type`
     * defaults to large (16dp) in the class, but every Settings preference XML that sets it uses
     * `small`, so 12dp is the spacing users see. It stacks on top of the title's own 8dp top margin.
     */
    val MarginTop = 12.dp

    /**
     * `coui_common_category_text_padding_top` / `_bottom` = 8dp: the title view's top and bottom margins
     * (`Widget.COUI.List.Category.Title`). The title view itself has 0dp horizontal padding
     * (`coui_preference_category_text_padding_left`).
     */
    val VerticalMargin = 8.dp

    /**
     * `?attr/couiColorSecondNeutral` = `coui_color_secondary_neutral`: `#8A000000` / `#8AFFFFFF`.
     *
     * `COUIPreferenceCategory.initStartRect()` widens it to `?attr/couiColorPrimaryNeutral`
     * (`#E6000000` / `#E6FFFFFF`) in two cases: a `title_type` of medium/large, or a small title with an
     * `icon_with_title` glyph. No Settings XML sets `title_type` / `icon_with_title` and no decompiled Settings
     * class calls `setTitleType()` / `setIconWithTitle()`, so every Settings category uses `SecondNeutral`; pass
     * the other colour explicitly through [CategoryTitle]'s `textColor` if a caller enables those styles.
     */
    @Composable
    fun textColor(): Color {
        val dark = RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
        return remember(dark) { if (dark) Color(0x8AFFFFFF) else Color(0x8A000000) }
    }

    /** Inline start 32dp, inline end 8dp, 12dp + 8dp above (item + title margin), 8dp below. */
    val InsideMargin =
        PaddingValues(
            start = MarginStart,
            top = MarginTop + VerticalMargin,
            end = MarginEnd,
            bottom = VerticalMargin,
        )
}

/**
 * A ColorOS section header (`COUIPreferenceCategory`'s title view).
 *
 * It carries the stock metrics only: 12sp medium (`couiTextAppearanceSmallButton`), at least 16dp tall,
 * inset 32dp from the inline start and 8dp from the inline end, 20dp above (12dp category margin + 8dp
 * title margin) and 8dp below, in `couiColorSecondNeutral`. Letter spacing is left to the theme's
 * default text style.
 *
 * @param text The section title.
 * @param modifier The modifier to be applied to the title.
 * @param textColor The title colour.
 * @param insideMargin The padding around the title text.
 */
@Composable
@NonRestartableComposable
fun CategoryTitle(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = CategoryTitleDefaults.textColor(),
    insideMargin: PaddingValues = CategoryTitleDefaults.InsideMargin,
) {
    Text(
        modifier =
            modifier
                .padding(insideMargin)
                .heightIn(min = CategoryTitleDefaults.MinHeight),
        text = text,
        fontSize = CategoryTitleDefaults.TextSize,
        fontWeight = CategoryTitleDefaults.TextWeight,
        color = textColor,
        minLines = 1,
    )
}
