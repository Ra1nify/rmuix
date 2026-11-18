// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.anim.folmeSpring
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * One entry of a [ChipTabRow].
 *
 * @param label the chip text.
 * @param icon optional leading icon, drawn at [ChipTabDefaults.IconSize].
 * @param count optional trailing count badge, separated by [ChipTabDefaults.CountStartMargin].
 */
data class ChipTabItem(
    val label: String,
    val icon: (@Composable () -> Unit)? = null,
    val count: String? = null,
)

/**
 * A row of ColorOS chip tabs (`COUIChipTabLayout` / `Widget.COUI.Chip.ChipTab`).
 *
 * The stock chip is a **pill** (`chipCornerRadius = -1dp` falls back to `min(width, height) / 2`), 32dp
 * tall, at least 52dp and at most 300dp wide, with 12dp of horizontal padding, a 16dp icon and a 4dp
 * gap after it. Checked chips paint `?attr/couiColorPrimary` behind `couiColorLabelOnColor` text;
 * unchecked ones paint `?attr/couiColorContainer8` behind `couiColorLabelPrimary` text.
 *
 * @param items the chips to render, left to right.
 * @param selectedIndex the index of the checked chip.
 * @param onSelect invoked with the index of the chip the user picked.
 * @param modifier the modifier applied to the row.
 * @param enabled whether the chips accept input.
 * @param height the chip height; [ChipTabDefaults.Height] by default.
 * @param colors the container / content colours.
 * @param showCheckedStroke whether a checked chip draws the stock 0.6dp stroke (inset by half its width,
 *   `#0A000000` / `#0AFFFFFF`). Stock draws it by default; pass `false` to omit it.
 */
@Composable
fun ChipTabRow(
    items: List<ChipTabItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = ChipTabDefaults.Height,
    colors: ChipTabColors = ChipTabDefaults.colors(),
    showCheckedStroke: Boolean = true,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ChipTabDefaults.ItemSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            // `COUIChipTabDrawable.drawDualLayerBackground()` cross-fades the checked and unchecked
            // surfaces through `EVALUATOR.evaluate(mUncheckedLayerAlpha, ...)`, so the chip animates
            // between the two containers instead of snapping.
            val animatedContainer by
                animateColorAsState(
                    targetValue = colors.containerColor(selected),
                    // `LayerAlphaAnimation` uses `COUISpringForce().setResponse(0.2f).setBounce(0f)` - a
                    // critically damped spring with a 0.2s response, not a fixed-duration tween.
                    animationSpec = ChipTabDefaults.colorTransitionSpec(),
                    label = "ChipTabContainer",
                )
            Box(
                modifier =
                    Modifier
                        .heightIn(min = height)
                        .widthIn(min = ChipTabDefaults.MinWidth, max = ChipTabDefaults.MaxWidth)
                        .background(
                            color = animatedContainer,
                            shape = RoundedCornerShape(height / 2),
                        ).then(
                            if (showCheckedStroke && selected) {
                                Modifier.border(
                                    width = ChipTabDefaults.CheckedStrokeWidth,
                                    color = colors.checkedStrokeColor,
                                    shape = RoundedCornerShape(height / 2),
                                )
                            } else {
                                Modifier
                            },
                        ).selectable(
                            selected = selected,
                            enabled = enabled,
                            role = Role.Tab,
                            onClick = { onSelect(index) },
                        ).padding(horizontal = ChipTabDefaults.HorizontalPadding),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (item.icon != null) {
                        Box(modifier = Modifier.size(ChipTabDefaults.IconSize)) { item.icon() }
                    }
                    Text(
                        text = item.label,
                        modifier =
                            Modifier.padding(
                                start = if (item.icon != null) ChipTabDefaults.IconEndPadding else 0.dp,
                            ),
                        color = colors.contentColor(selected),
                        fontSize = ChipTabDefaults.TextSize,
                        fontWeight = ChipTabDefaults.TextWeight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (item.count != null) {
                        Text(
                            text = item.count,
                            modifier = Modifier.padding(start = ChipTabDefaults.CountStartMargin),
                            color = colors.contentColor(selected),
                            fontSize = ChipTabDefaults.CountTextSize,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

/** Container / content colours of a [ChipTabRow]. */
@Immutable
data class ChipTabColors(
    val checkedContainerColor: Color,
    val checkedContentColor: Color,
    val uncheckedContainerColor: Color,
    val uncheckedContentColor: Color,
    /** Stroke painted around a checked chip when `showCheckedStroke` is on. */
    val checkedStrokeColor: Color = Color.Transparent,
) {
    /** The container of a chip in [selected] state. */
    fun containerColor(selected: Boolean): Color = if (selected) checkedContainerColor else uncheckedContainerColor

    /** The content of a chip in [selected] state. */
    fun contentColor(selected: Boolean): Color = if (selected) checkedContentColor else uncheckedContentColor
}

/** Defaults for [ChipTabRow], all taken from `Widget.COUI.Chip.ChipTab`. */
object ChipTabDefaults {
    /** `coui_chip_tab_style_height`: 32dp. */
    val Height = ChipTabStockHeight

    /** `coui_chip_default_min_width`: 52dp. */
    val MinWidth = ChipTabStockMinWidth

    /** `coui_chip_default_max_width`: 300dp. */
    val MaxWidth = ChipTabStockMaxWidth

    /** `coui_chip_suggest_style_chip_horizontal_padding`: 12dp. */
    val HorizontalPadding = ChipTabStockHorizontalPadding

    /** `coui_chip_tab_style_chip_icon_size`: 16dp. */
    val IconSize = ChipTabStockIconSize

    /** `coui_chip_suggest_style_chip_icon_end_padding`: 4dp. */
    val IconEndPadding = ChipTabStockIconEndPadding

    /** `coui_chip_tab_count_start_margin`: 2dp. */
    val CountStartMargin = ChipTabStockCountStartMargin

    /** `coui_color_checked_background_stroke` width: 0.6dp. */
    val CheckedStrokeWidth = ChipTabStockCheckedStrokeWidth

    /** `?attr/couiTextButtonM` -> `couiTextButtonM` style: **14sp**, `sans-serif-medium`. */
    val TextSize = 14.sp

    /** `couiTextButtonM` sets `sans-serif-medium`; the chip label uses the matching weight. */
    val TextWeight = FontWeight.Medium

    /** `?attr/couiTextBodyS` -> `couiTextBodyS` style: **13sp**. */
    val CountTextSize = 13.sp

    /** Gap between two chips; the stock layout lets the host decide, 8dp matches the settings rows. */
    val ItemSpacing = 8.dp

    /**
     * Spring response of the checked/unchecked cross-fade: `CheckedLayerAlphaAnimation` calls
     * `COUISpringForce().setResponse(0.2f)` with `BOUNCE = 0f`, i.e. critically damped.
     */
    val ColorTransitionResponse = 0.2f

    /** The spring the chip uses for [ColorTransitionResponse]. */
    fun <T> colorTransitionSpec(): SpringSpec<T> = folmeSpring(damping = 1f, response = ColorTransitionResponse)

    /** The chip colours, keyed on the current theme. */
    @Composable
    fun colors(dark: Boolean = isChipTabInDarkTheme()): ChipTabColors =
        remember(dark) {
            ChipTabColors(
                checkedContainerColor = chipTabCheckedContainer(dark),
                checkedContentColor = chipTabCheckedContent(dark),
                uncheckedContainerColor = chipTabUncheckedContainer(dark),
                uncheckedContentColor = chipTabUncheckedContent(dark),
                checkedStrokeColor = chipTabCheckedStroke(dark),
            )
        }
}

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isChipTabInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
