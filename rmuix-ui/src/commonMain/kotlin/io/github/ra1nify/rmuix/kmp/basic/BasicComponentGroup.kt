// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.squircle.SquircleShape
import io.github.ra1nify.rmuix.kmp.squircle.isSquircleEnabled

/** Per-row divider start reported by each [BasicComponent] in the group, in composition order. */
internal class BasicComponentGroupDividerStarts {
    internal class Slot {
        /** Inline-start offset of the row's title, in pixels; `NaN` until the row is measured. */
        var startPx: Float = Float.NaN
    }

    internal val slots = mutableListOf<Slot>()

    internal fun newSlot(): Slot = Slot().also { slots += it }

    internal fun removeSlot(slot: Slot) {
        slots.remove(slot)
    }
}

/**
 * Provided by [BasicComponentGroup] so its rows can report where their title starts; `null` everywhere
 * else, which keeps [BasicComponent] free of extra work outside a group.
 */
internal val LocalBasicComponentGroupDividerStarts = compositionLocalOf<BasicComponentGroupDividerStarts?> { null }

/**
 * Defaults for [BasicComponentGroup], taken from `COUICardListSelectedItemLayout` and
 * `COUICardListHelper` in the ColorOS settings dump.
 */
object BasicComponentGroupDefaults {
    /** `?attr/couiRoundCornerXL` = `coui_round_corner_xl`: the card group radius. */
    val CornerRadius = 20.dp

    /** `coui_round_corner_xl_weight`: the smooth-corner weight that pairs with [CornerRadius]. */
    val CornerExtension = 1.1f

    /**
     * `coui_list_card_head_or_tail_padding`: top padding above the first row and bottom padding below
     * the last one, so two stacked groups end up 4dp apart.
     */
    val GroupPadding = 2.dp

    /**
     * `coui_preference_card_margin_horizontal`: the card rect is inset 16dp from the item on both sides
     * (`COUICardListSelectedItemLayout.updatePath()`: `mCardRect.set(mHorizontalMargin, 0, width -
     * mHorizontalMargin, height)`); `_tiny` is 12dp for compact lists.
     */
    val HorizontalMargin = 16.dp

    /** `coui_list_divider_height`: the hairline drawn between rows. */
    val DividerThickness = 0.33.dp

    /**
     * Divider start used when a row cannot report its own title x (e.g. a plain composable instead of a
     * [BasicComponent]). [BasicComponent] rows align to their title automatically: the stock
     * `getDividerStartAlignView()` returns the row's title view and the decoration measures its x, which is
     * `support_preference_title_padding_start` = **32dp** without a leading icon and **84dp** for a 36dp
     * icon row.
     */
    val DividerStart = 32.dp

    /**
     * Divider inset from the inline end: `coui_preference_divider_default_horizontal_padding`, the value
     * `COUIPreference.getDividerEndInset()` returns when the row has no end-align view.
     */
    val DividerEnd = 32.dp

    /**
     * The group surface is `?attr/couiColorCardBackground` = `@color/coui_color_card_background`
     * (#FFFFFF light / #1AFFFFFF dark), the fill `COUICardListSelectedItemLayout.init()` reads;
     * the hairline is `?attr/couiColorDivider`.
     */
    @Composable
    fun colors(): BasicComponentGroupColors =
        BasicComponentGroupColors(
            containerColor = preferenceCardContainer(isBasicComponentInDarkTheme()),
            dividerColor = DividerDefaults.DividerColor,
        )

    /**
     * `coui_preference_secondary_text_color`, the footer's selector: `couiColorSecondNeutral`
     * (`#8A000000` / `#8AFFFFFF`) normally and `couiColorLabelTertiary` when disabled - the same pair
     * [BasicComponentDefaults.summaryColor] carries.
     */
    @Composable
    fun footerColor(): BasicComponentColors = BasicComponentDefaults.summaryColor()

    /** `couiTextAppearanceArticleBody`: the footer's 13sp body size. */
    val FooterTextSize = 13.sp

    /**
     * The footer row's padding: `support_preference_title_padding_start` / `_end` = 32dp on the sides
     * and `category_bottom_tip_preference_padding_top` / `_bottom` = 8dp above and below.
     */
    val FooterPadding = PaddingValues(start = 32.dp, top = 8.dp, end = 32.dp, bottom = 8.dp)
}

/** Colors of a [BasicComponentGroup]. */
@Immutable
data class BasicComponentGroupColors(
    val containerColor: Color,
    val dividerColor: Color,
)

/**
 * A ColorOS card group: the rows in [content] share one rounded card, and a hairline is drawn
 * **between rows only** - never above the first row or below the last one.
 *
 * That is the `COUICardListHelper` model: each row gets a `positionInGroup` of HEAD / MIDDLE / TAIL /
 * FULL, and `COUIPreference.drawDivider()` returns true only for HEAD and MIDDLE. The divider belongs
 * to the row *above* it, so the seam between two groups has none; a `PreferenceCategory` is not a card
 * member, which is what makes a category header separate groups.
 *
 * The group also owns the head/tail padding and the smooth `couiRoundCornerXL` radius, so callers no
 * longer wrap every row in its own [Card].
 *
 * A stock preference section is the category title plus its card plus an optional footer; pass [title]
 * and [footer] to get that without stacking three composables by hand. Both default to `null`, which
 * draws exactly the card-only group.
 *
 * @param modifier The modifier to be applied to the group.
 * @param cornerRadius The card radius, [BasicComponentGroupDefaults.CornerRadius] by default.
 * @param colors Container and divider colours.
 * @param showDividers Whether to draw the hairlines between rows.
 * @param dividerStart Inline-start inset of the hairline used for rows that do not report their title x;
 *   [BasicComponent] rows align the hairline to their own title (32dp without an icon, 84dp for a 36dp
 *   icon) automatically.
 * @param dividerEnd Inline-end inset of the hairline (32dp in the settings card list).
 * @param horizontalMargin Inline inset of the card from the group's edges: `coui_preference_card_margin_horizontal`
 *   = 16dp (`_tiny` 12dp).
 * @param title The optional section title, drawn as a [CategoryTitle] above the card
 *   (`COUIPreferenceCategory`'s title view).
 * @param footer The optional footer below the card, 13sp in `couiColorSecondNeutral`
 *   (`SettingsCategoryBottomTipPreference`).
 * @param headerContent An optional slot between the title and the card.
 * @param footerContent An optional slot between the card and the footer text.
 * @param content The rows, normally [BasicComponent]s.
 */
@Composable
fun BasicComponentGroup(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = BasicComponentGroupDefaults.CornerRadius,
    colors: BasicComponentGroupColors = BasicComponentGroupDefaults.colors(),
    showDividers: Boolean = true,
    dividerStart: Dp = BasicComponentGroupDefaults.DividerStart,
    dividerEnd: Dp = BasicComponentGroupDefaults.DividerEnd,
    title: String? = null,
    footer: String? = null,
    headerContent: (@Composable () -> Unit)? = null,
    footerContent: (@Composable () -> Unit)? = null,
    // Appended after the existing parameters so every current call site keeps compiling.
    horizontalMargin: Dp = BasicComponentGroupDefaults.HorizontalMargin,
    content: @Composable () -> Unit,
) {
    // The modifier goes on the outer column, so the spacing of a titled section applies to the whole
    // section; with no title/footer the column has a single child and renders exactly as before.
    Column(modifier = modifier) {
        if (title != null) {
            CategoryTitle(text = title)
        }
        headerContent?.invoke()
        GroupSurface(
            cornerRadius = cornerRadius,
            colors = colors,
            showDividers = showDividers,
            dividerStart = dividerStart,
            dividerEnd = dividerEnd,
            horizontalMargin = horizontalMargin,
            content = content,
        )
        footerContent?.invoke()
        if (footer != null) {
            Text(
                modifier = Modifier.padding(BasicComponentGroupDefaults.FooterPadding),
                text = footer,
                fontSize = BasicComponentGroupDefaults.FooterTextSize,
                color = BasicComponentGroupDefaults.footerColor().color(enabled = true),
            )
        }
    }
}

@Composable
private fun GroupSurface(
    cornerRadius: Dp,
    colors: BasicComponentGroupColors,
    showDividers: Boolean,
    dividerStart: Dp,
    dividerEnd: Dp,
    horizontalMargin: Dp,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val strokePx = with(density) { BasicComponentGroupDefaults.DividerThickness.toPx() }.coerceAtLeast(1f)
    val startPx = with(density) { dividerStart.roundToPx() }.toFloat()
    val endPx = with(density) { dividerEnd.roundToPx() }.toFloat()
    val groupPaddingPx = with(density) { BasicComponentGroupDefaults.GroupPadding.roundToPx() }
    val squircleEnabled = isSquircleEnabled()
    val dividerColor = colors.dividerColor

    // Row seams are computed during layout and drawn from a snapshot list: the list is only written when
    // the measurements actually change, so the write costs one recomposition at most per layout change.
    val seams = remember { mutableStateListOf<DividerSeam>() }

    Box(
        modifier =
            Modifier
                // `COUICardListSelectedItemLayout.updatePath()` draws the card inside
                // `[mHorizontalMargin, width - mHorizontalMargin]`, so the surface is inset 16dp.
                .padding(horizontal = horizontalMargin)
                .clip(
                    SquircleShape(
                        cornerRadius = cornerRadius,
                        extension = BasicComponentGroupDefaults.CornerExtension,
                        squircleEnabled = squircleEnabled,
                    ),
                ).background(colors.containerColor),
    ) {
        val dividerStarts = remember { BasicComponentGroupDividerStarts() }
        Layout(
            content = {
                CompositionLocalProvider(LocalBasicComponentGroupDividerStarts provides dividerStarts) {
                    content()
                }
            },
            modifier =
                Modifier.drawBehind {
                    val left = if (layoutDirection == LayoutDirection.Rtl) endPx else startPx
                    val right = size.width - if (layoutDirection == LayoutDirection.Rtl) startPx else endPx
                    val width = (right - left).coerceAtLeast(0f)
                    if (!showDividers) return@drawBehind
                    seams.forEach { seam ->
                        val start = if (seam.startPx.isNaN()) startPx else seam.startPx
                        val seamLeft = if (layoutDirection == LayoutDirection.Rtl) endPx else start
                        val seamRight = size.width - if (layoutDirection == LayoutDirection.Rtl) start else endPx
                        val seamWidth = (seamRight - seamLeft).coerceAtLeast(0f)
                        drawRect(
                            color = dividerColor,
                            topLeft = Offset(seamLeft, seam.y),
                            size = Size(seamWidth, strokePx),
                        )
                    }
                },
        ) { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }
            val width = placeables.maxOfOrNull { it.width } ?: 0
            val contentHeight = placeables.sumOf { it.height }
            // `COUIBaseListItemViewHolder.getDividerStartAlignView()` returns the title view, so the
            // hairline starts where the row's title does (32dp without an icon, 84dp for a 36dp icon).
            // BasicComponent reports that x through the composition local; rows that do not report it
            // (plain composables) fall back to the group-level `dividerStart`.
            val reportedStarts =
                dividerStarts.slots
                    .takeIf { it.size == placeables.size }
                    ?.map { it.startPx }
            val newSeams = ArrayList<DividerSeam>(placeables.size)
            var seamY = groupPaddingPx
            placeables.forEachIndexed { index, placeable ->
                // The hairline sits on the boundary between this row and the previous one.
                if (index > 0) {
                    newSeams.add(
                        DividerSeam(
                            y = seamY.toFloat() - strokePx / 2f,
                            startPx = reportedStarts?.get(index - 1) ?: Float.NaN,
                        ),
                    )
                }
                seamY += placeable.height
            }
            if (newSeams != seams.toList()) {
                seams.clear()
                seams.addAll(newSeams)
            }
            layout(width = width, height = contentHeight + groupPaddingPx * 2) {
                var placeY = groupPaddingPx
                placeables.forEach { placeable ->
                    placeable.place(0, placeY)
                    placeY += placeable.height
                }
            }
        }
    }
}

/** One hairline boundary: `startPx` is the inline-start offset reported by the row above it. */
private data class DividerSeam(
    val y: Float,
    val startPx: Float,
)
