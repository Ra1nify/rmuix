// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.LocalContentColor
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectColor
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectSpring

/**
 * The two stock chip styles that the library models.
 *
 * - [Suggestion]: `Widget.COUI.Chip.Suggestion` - 28dp tall, `couiTextBodyXS` (12sp).
 * - [Selection]: `Widget.COUI.Chip.Selection` - 32dp tall, `couiTextBodyM` (14sp).
 */
enum class ChipStyle {
    Suggestion,
    Selection,
}

/**
 * A ColorOS chip (`COUIChip`): an optional-state capsule label with the stock
 * `?attr/couiColorContainer8` / `?attr/couiColorPrimary` containers.
 *
 * The unchecked container is `couiColorContainer8` (`#14000000` / `#1AFFFFFF`), the checked one is
 * `?attr/couiColorPrimary` with `?attr/couiColorLabelOnColor` text. The stock base style
 * (`Widget.COUI.Chip`) is a capsule (`chipCornerRadius = -1dp`), 28dp high, 52dp min width, 300dp max
 * width and a 200dp label cap. The press/hover feedback is a `COUIStateEffectDrawable` mask; this
 * component paints it over the container like the other migrated components.
 *
 * @param onClick Invoked when the chip is clicked.
 * @param label The chip label; it is truncated with an ellipsis past [ChipDefaults.MaxTextWidth].
 * @param modifier The modifier to be applied to the chip.
 * @param selected Whether the chip is in the checked state.
 * @param enabled Whether the chip accepts input.
 * @param style The [ChipStyle] (height and label size).
 * @param colors The [ChipColors] of the chip.
 * @param onClose When non-null the chip shows the stock close glyph (`ic_chip_close`, 12dp with 8dp
 *   paddings and a 24dp touch bounds); the stock `.Suggestion` style sets it by default, this component
 *   keeps it opt-in so existing call sites never show a button without an action.
 * @param icon An optional leading icon. The stock styles reserve 14dp (.Suggestion) or 16dp (.Selection)
 *   for it, preceded by `chipIconStartPadding` (12dp / 0dp) and followed by 4dp
 *   (`chipIconEndPadding`); the slot is tinted with the chip's content colour.
 * @param borderWidth Width of the opt-in outline. The stock chip has no border, so this only draws when
 *   [ChipColors.borderColor] / [ChipColors.selectedBorderColor] is specified - e.g. a roui-style badge.
 */
@Composable
fun Chip(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    style: ChipStyle = ChipStyle.Suggestion,
    colors: ChipColors = ChipDefaults.chipColors(),
    borderWidth: Dp = ChipDefaults.BorderWidth,
    onClose: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
) {
    val dark = isChipInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val hovered by interactionSource.collectIsHoveredAsState()
    val pressProgress by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = stockStateEffectSpring(),
        label = "chipPress",
    )
    val hoverProgress by animateFloatAsState(
        targetValue = if (hovered && enabled) 1f else 0f,
        animationSpec = stockStateEffectSpring(),
        label = "chipHover",
    )
    val baseContainer =
        when {
            !enabled && selected -> colors.selectedDisabledContainerColor
            !enabled -> colors.disabledContainerColor
            selected -> colors.selectedContainerColor
            else -> colors.containerColor
        }
    val pressColor = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)
    val hoverColor = if (dark) Color(0x26FFFFFF) else Color(0x14000000)
    val container =
        stockStateEffectColor(pressColor, pressProgress)
            .compositeOver(stockStateEffectColor(hoverColor, hoverProgress))
            .compositeOver(baseContainer)
    val contentColor =
        when {
            !enabled && selected -> colors.selectedDisabledContentColor
            !enabled -> colors.disabledContentColor
            selected -> colors.selectedContentColor
            else -> colors.contentColor
        }
    // The stock chip draws no outline; a specified border colour opts it in (roui-style status badges).
    val borderColor = if (selected) colors.selectedBorderColor else colors.borderColor
    val height = if (style == ChipStyle.Selection) ChipDefaults.SelectionHeight else ChipDefaults.MinHeight
    val textSize =
        if (style == ChipStyle.Selection) ChipDefaults.SelectionTextSize else ChipDefaults.SuggestionTextSize
    Box(
        modifier =
            modifier
                .height(height)
                .widthIn(min = ChipDefaults.MinWidth, max = ChipDefaults.MaxWidth)
                .clip(CircleShape)
                .background(container)
                .then(
                    if (borderColor.isSpecified && borderWidth > 0.dp) {
                        Modifier.border(width = borderWidth, color = borderColor, shape = CircleShape)
                    } else {
                        Modifier
                    },
                ).hoverable(interactionSource, enabled = enabled)
                // `Widget.COUI.Chip` is `android:checkable="false"` and `.Selection` turns it on, so the
                // semantics follow the style: a button by default, a checkable row for the selection style.
                .then(
                    if (style == ChipStyle.Selection) {
                        Modifier.selectable(
                            selected = selected,
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = enabled,
                            role = Role.Checkbox,
                            onClick = onClick,
                        )
                    } else {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = enabled,
                            role = Role.Button,
                            onClick = onClick,
                        )
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier =
                Modifier.padding(
                    start = ChipDefaults.HorizontalPadding,
                    end = ChipDefaults.CloseIconEndPadding,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                // `calculateChipIconBounds`: the icon sits at `chipStartPadding + chipIconStartPadding`
                // from the start edge, is `chipIconSize` wide, then `chipIconEndPadding` separates it
                // from the label. The Row already applies `chipStartPadding` on the start side.
                val iconSize =
                    if (style == ChipStyle.Selection) {
                        ChipDefaults.SelectionIconSize
                    } else {
                        ChipDefaults.SuggestionIconSize
                    }
                val iconStartPadding =
                    if (style == ChipStyle.Selection) {
                        ChipDefaults.SelectionIconStartPadding
                    } else {
                        ChipDefaults.SuggestionIconStartPadding
                    }
                Spacer(Modifier.width(iconStartPadding))
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    Box(Modifier.size(iconSize), contentAlignment = Alignment.Center) { icon() }
                }
                Spacer(Modifier.width(ChipDefaults.IconEndPadding))
            }
            Text(
                text = label,
                modifier = Modifier.widthIn(max = ChipDefaults.MaxTextWidth),
                fontSize = textSize,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (onClose != null) {
                Spacer(Modifier.width(ChipDefaults.CloseIconStartPadding))
                ChipCloseIcon(onClick = onClose, tint = chipCloseIconColor(dark))
            }
        }
    }
}

/**
 * The stock `ic_chip_close` glyph (14-unit viewport) drawn at `coui_chip_suggest_style_close_icon_size`
 * (12dp) inside its `coui_chip_suggest_style_close_icon_touch_bounds_size` (24dp) hit area.
 */
@Composable
private fun ChipCloseIcon(
    onClick: () -> Unit,
    tint: Color,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier =
            Modifier
                .size(ChipDefaults.CloseIconTouchBounds)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                ).semantics { contentDescription = "Remove" },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(ChipDefaults.CloseIconSize)) {
            val scale = size.minDimension / CHIP_CLOSE_VIEWPORT
            withTransform({ scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero) }) {
                drawPath(chipClosePath(), color = tint)
            }
        }
    }
}

/** Parsed once: the close glyph comes from the stock path data rather than four lines. */
private val chipClosePathCache: Path by lazy {
    PathParser().parsePathString(CHIP_CLOSE_PATH_DATA).toPath()
}

private fun chipClosePath(): Path = chipClosePathCache

@Immutable
data class ChipColors(
    val containerColor: Color,
    val contentColor: Color,
    val selectedContainerColor: Color,
    val selectedContentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
    val selectedDisabledContainerColor: Color,
    val selectedDisabledContentColor: Color,
    /** Draws an outline when specified; [Color.Unspecified] (the default) keeps the stock no-border chip. */
    val borderColor: Color = Color.Unspecified,
    /** Border colour while [Chip] is selected; no outline when unspecified. */
    val selectedBorderColor: Color = Color.Unspecified,
)

object ChipDefaults {
    /**
     * Width of the opt-in outline. Not a COUI token - the stock chip has no border; this exists for the
     * roui-style status badge realOS uses (a 1dp capsule outline).
     */
    val BorderWidth = 1.dp

    /** `coui_chip_default_min_height`: the [ChipStyle.Suggestion] height, 28dp. */
    val MinHeight = ChipStockMinHeight

    /** `coui_chip_selection_style_height`: the [ChipStyle.Selection] height, 32dp. */
    val SelectionHeight = ChipStockSelectionHeight

    /** `coui_chip_default_min_width`: 52dp. */
    val MinWidth = ChipStockMinWidth

    /** `coui_chip_default_max_width`: 300dp. */
    val MaxWidth = ChipStockMaxWidth

    /** `coui_chip_default_max_text_width`: the label truncates past 200dp. */
    val MaxTextWidth = ChipStockMaxTextWidth

    /** `coui_chip_selection_style_chip_horizontal_padding`: 12dp (see the research note on the base style). */
    val HorizontalPadding = ChipStockHorizontalPadding

    /** `coui_chip_group_default_spacing_horizontal` / `_vertical`: 8dp; spacing between chips in a row. */
    val GroupSpacing = ChipStockGroupSpacing

    /** The stock `collapsedMaxRows` attribute default: a chip group keeps 3 rows while collapsed. */
    val CollapsedMaxRows: Int = CHIP_GROUP_STOCK_COLLAPSED_MAX_ROWS

    /** `coui_chip_suggest_style_chip_icon_size`: the [ChipStyle.Suggestion] icon slot is 14dp. */
    val SuggestionIconSize = ChipStockSuggestionIconSize

    /** `coui_chip_selection_style_chip_icon_size`: the [ChipStyle.Selection] icon slot is 16dp. */
    val SelectionIconSize = ChipStockSelectionIconSize

    /** `coui_chip_*_style_chip_icon_end_padding`: 4dp between the icon and the label. */
    val IconEndPadding = ChipStockIconEndPadding

    /** `coui_chip_suggest_style_chip_icon_start_padding`: 12dp extra before the suggestion icon. */
    val SuggestionIconStartPadding = ChipStockSuggestionIconStartPadding

    /** `coui_chip_selection_style_chip_icon_start_padding`: 0dp extra before the selection icon. */
    val SelectionIconStartPadding = ChipStockSelectionIconStartPadding

    /** `coui_chip_group_collapsable_button_size`: the collapse / expand chip is 28dp. */
    val GroupCollapseButtonSize = ChipGroupStockButtonSize

    /** `Widget.COUI.Chip.Suggestion.Collapsable`: the chevron is 16dp. */
    val GroupCollapseGlyphSize = ChipGroupStockGlyphSize

    /** `coui_chip_suggest_style_close_icon_size`: the close glyph is 12dp. */
    val CloseIconSize = ChipStockCloseIconSize

    /** `coui_chip_suggest_style_close_icon_start_padding`: 8dp between the label and the glyph. */
    val CloseIconStartPadding = ChipStockCloseIconStartPadding

    /** `coui_chip_suggest_style_close_icon_end_padding`: 8dp after the glyph (replaces [HorizontalPadding]). */
    val CloseIconEndPadding = ChipStockCloseIconEndPadding

    /** `coui_chip_suggest_style_close_icon_touch_bounds_size`: 24dp hit area. */
    val CloseIconTouchBounds = ChipStockCloseIconTouchBounds

    /** `?attr/couiTextBodyXS`: 12sp, the [ChipStyle.Suggestion] label size. */
    val SuggestionTextSize = ChipStockSuggestionTextSize

    /** `?attr/couiTextBodyM`: 14sp, the [ChipStyle.Selection] label size. */
    val SelectionTextSize = ChipStockSelectionTextSize

    @Composable
    fun chipColors(
        containerColor: Color = chipUncheckedContainerColor(isChipInDarkTheme()),
        contentColor: Color = chipUncheckedContentColor(isChipInDarkTheme()),
        selectedContainerColor: Color = RmuixTheme.colorScheme.primary,
        selectedContentColor: Color = chipCheckedContentColor(),
        disabledContainerColor: Color = chipUncheckedContainerColor(isChipInDarkTheme()),
        disabledContentColor: Color = chipUncheckedDisabledContentColor(isChipInDarkTheme()),
        selectedDisabledContainerColor: Color = RmuixTheme.colorScheme.primary.copy(alpha = CHIP_STOCK_DISABLED_ALPHA),
        selectedDisabledContentColor: Color = chipCheckedDisabledContentColor(isChipInDarkTheme()),
        borderColor: Color = Color.Unspecified,
        selectedBorderColor: Color = Color.Unspecified,
    ): ChipColors =
        remember(
            containerColor,
            contentColor,
            selectedContainerColor,
            selectedContentColor,
            disabledContainerColor,
            disabledContentColor,
            selectedDisabledContainerColor,
            selectedDisabledContentColor,
            borderColor,
            selectedBorderColor,
        ) {
            ChipColors(
                containerColor = containerColor,
                contentColor = contentColor,
                selectedContainerColor = selectedContainerColor,
                selectedContentColor = selectedContentColor,
                disabledContainerColor = disabledContainerColor,
                disabledContentColor = disabledContentColor,
                selectedDisabledContainerColor = selectedDisabledContainerColor,
                selectedDisabledContentColor = selectedDisabledContentColor,
                borderColor = borderColor,
                selectedBorderColor = selectedBorderColor,
            )
        }
}
