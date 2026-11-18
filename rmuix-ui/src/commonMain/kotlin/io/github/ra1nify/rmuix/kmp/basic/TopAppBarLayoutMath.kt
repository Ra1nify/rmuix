// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import kotlin.math.roundToInt

// Pure layout math behind the large TopAppBar's exit-until-collapsed behaviour. Keeping it out of
// the measure lambda makes the arithmetic testable without a composition.

/**
 * The most negative `heightOffset` the bar may reach: the distance between the expanded and the
 * collapsed height. `heightOffset` is clamped to `[limit, 0]`, so scrolling to the limit collapses
 * the bar completely.
 */
internal fun topAppBarHeightOffsetLimit(
    expandedHeightPx: Int,
    collapsedHeightPx: Int,
): Float = if (expandedHeightPx > collapsedHeightPx) -(expandedHeightPx - collapsedHeightPx).toFloat() else 0f

/**
 * The bar height for a given scroll offset: expanded height plus the (negative) offset, clamped
 * between the collapsed and the expanded height so rounding can never overshoot either end.
 */
internal fun topAppBarLayoutHeight(
    expandedHeightPx: Int,
    collapsedHeightPx: Int,
    heightOffsetPx: Float,
): Int {
    val clamped = heightOffsetPx.coerceIn(topAppBarHeightOffsetLimit(expandedHeightPx, collapsedHeightPx), 0f)
    return (expandedHeightPx + clamped).roundToInt().coerceIn(collapsedHeightPx, expandedHeightPx)
}

/**
 * Horizontal inset of the app-bar divider: `coui_appbar_divider_expanded_margin_horizontal` (24dp) at
 * fraction 0, `coui_appbar_divider_collapsed_margin_horizontal` (0dp) at fraction 1
 * (`COUIDividerAppBarLayout.updateDivider`).
 */
internal fun topAppBarDividerMarginPx(
    expandedMarginPx: Float,
    collapsedMarginPx: Float,
    fraction: Float,
): Float {
    val clamped = fraction.coerceIn(0f, 1f)
    return expandedMarginPx + (collapsedMarginPx - expandedMarginPx) * clamped
}

/**
 * The divider's progress (`COUIDividerAppBarLayout.onDividerChanged()`).
 *
 * The stock layout accumulates the nested-scroll distance and divides it by `getMeasuredHeight()`
 * (`getDividerScrollRange()`), clamping at 1 - it is **not** the collapse fraction. With the default
 * `dividerStartAlpha` 0 and `dividerEndAlpha` 1 the alpha equals this fraction.
 */
internal fun topAppBarDividerFraction(
    scrollDistancePx: Float,
    appBarHeightPx: Float,
): Float {
    if (appBarHeightPx <= 0f) return 0f
    return (scrollDistancePx / appBarHeightPx).coerceIn(0f, 1f)
}

/**
 * The divider's alpha: `dividerStartAlpha` (0) + (`dividerEndAlpha` (1) - start) * [fraction], i.e.
 * equal to the fraction for the stock defaults.
 */
internal fun topAppBarDividerAlpha(fraction: Float): Float = fraction.coerceIn(0f, 1f)

/**
 * Vertical bottom padding of the collapsable app bar (`COUICollapsableAppBarLayout.adjustPaddingBottom()`).
 *
 * The stock app bar interpolates `coui_appbar_start_padding_bottom` (12dp) down to
 * `_end_padding_bottom` (0dp) as the bar collapses, so the bar is 12dp taller at rest and loses that
 * padding once pinned. The divider and subtitle are translated by the same amount in the stock
 * implementation, which is equivalent to keeping them in place while the bar's padding animates.
 *
 * @param startPx `coui_appbar_start_padding_bottom` in pixels
 * @param endPx `coui_appbar_end_padding_bottom` in pixels
 * @param fraction collapse fraction: 0 fully expanded, 1 fully collapsed
 */
internal fun topAppBarCollapseBottomPaddingPx(
    startPx: Int,
    endPx: Int,
    fraction: Float,
): Int {
    val clamped = fraction.coerceIn(0f, 1f)
    return (startPx + (endPx - startPx) * clamped).roundToInt()
}

/**
 * Title metrics for the two stock app bar forms:
 *
 * - **Static toolbar** (`COUIAppBarLayout` + `COUIToolbar`, `common_divider_appbar_layout.xml`): 18sp,
 *   medium family (`textAppearanceSecondTitle` -> `TextAppearance.COUI.Toolbar.SecondTitle`, 18dp,
 *   `textStyle` normal, `sans-serif-medium`). This is the Settings default and also what
 *   [topAppBarTitleMetrics] returns while [TopAppBar] has no large title.
 * - **Collapsable bar** (`COUICollapsableAppBarLayout`, `COUIAppbarTitleStyle`): the collapsed title is
 *   24dp bold (`coui_appbar_title_collapsed_text_size` on top of the base style's `textStyle` bold).
 * - A subtitle row lowers the size to 18sp in both forms (`coui_appbar_title_collapsed_with_subtitle_text_size`
 *   is a dead token, so this is the library's choice).
 *
 * @param hasLargeTitle Whether the bar renders the expanded large title (collapsable form).
 * @param hasSubtitle Whether a subtitle row is present.
 */
internal fun topAppBarTitleMetrics(
    hasLargeTitle: Boolean,
    hasSubtitle: Boolean,
): TopAppBarTitleMetrics =
    when {
        hasSubtitle -> TopAppBarTitleMetrics(TopAppBarDefaults.TitleTextSizeWithSubtitle, FontWeight.Medium)
        hasLargeTitle -> TopAppBarTitleMetrics(TopAppBarDefaults.TitleTextSize, FontWeight.Bold)
        else -> TopAppBarTitleMetrics(TopAppBarDefaults.ToolbarTitleTextSize, FontWeight.Medium)
    }

/** Resolved title text size + weight for [topAppBarTitleMetrics]. */
internal data class TopAppBarTitleMetrics(
    val textSize: TextUnit,
    val fontWeight: FontWeight,
)

/**
 * Subtitle size per stock form: the static `COUIToolbar` uses `coui_toolbar_subtitle_text_size` (13sp,
 * `TextAppearance.COUI.AppCompatSupport.Toolbar.SubTitle`), the collapsable app bar uses
 * `coui_appbar_subtitle_text_size` (14sp).
 */
internal fun topAppBarSubtitleTextSize(hasLargeTitle: Boolean): TextUnit =
    if (hasLargeTitle) TopAppBarDefaults.SubtitleTextSize else TopAppBarDefaults.ToolbarSubtitleTextSize
