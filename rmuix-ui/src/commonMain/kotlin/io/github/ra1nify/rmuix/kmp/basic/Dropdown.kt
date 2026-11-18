// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.ArrowRight
import io.github.ra1nify.rmuix.kmp.icon.basic.ArrowUpDown
import io.github.ra1nify.rmuix.kmp.icon.basic.Check
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

@Composable
@NonRestartableComposable
fun RowScope.DropdownArrowEndAction(
    actionColor: Color,
    modifier: Modifier = Modifier,
) {
    val colorFilter = remember(actionColor) { ColorFilter.tint(actionColor) }
    Image(
        modifier =
            modifier
                .size(width = DropdownDefaults.ArrowSize.width, height = DropdownDefaults.ArrowSize.height)
                .align(Alignment.CenterVertically),
        imageVector = RmuixIcons.Basic.ArrowUpDown,
        colorFilter = colorFilter,
        contentDescription = null,
    )
}

/**
 * The implementation of the dropdown.
 *
 * @param item The item of the current option.
 * @param optionSize The size of the options.
 * @param isSelected Whether the option is selected.
 * @param index The index of the current option in the options.
 * @param dropdownColors The [DropdownColors] used to style the option row.
 * @param enabled Whether the option is clickable. Disabled rows ignore clicks and use the disabled text color.
 * @param dialogMode Whether the item is shown in dialog mode.
 * @param hasSubmenu When true, this row acts as a submenu trigger: a trailing chevron is shown
 *   instead of the selection check, and the row's accessibility role becomes [Role.Button].
 * @param isFirst Whether this row is the first row of the entire popup. Controls the larger top
 *   padding in popup mode. Defaults to `index == 0`; multi-entry callers should pass the
 *   popup-global flag so only the actual first row gets extra padding.
 * @param isLast Whether this row is the last row of the entire popup. Controls the larger bottom
 *   padding in popup mode. Defaults to `index == optionSize - 1`; multi-entry callers should pass
 *   the popup-global flag.
 * @param onSelectedIndexChange The callback invoked with [index] when the option is selected.
 */
@Composable
fun DropdownImpl(
    item: DropdownItem,
    optionSize: Int,
    isSelected: Boolean,
    index: Int,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    enabled: Boolean = item.enabled,
    dialogMode: Boolean = false,
    hasSubmenu: Boolean = false,
    isFirst: Boolean = index == 0,
    isLast: Boolean = index == optionSize - 1,
    onSelectedIndexChange: (Int) -> Unit,
) {
    val additionalTopPadding =
        if (!dialogMode && isFirst) {
            DropdownDefaults.FirstLastVerticalPadding
        } else {
            DropdownDefaults.MiddleVerticalPadding
        }
    val additionalBottomPadding =
        if (!dialogMode && isLast) {
            DropdownDefaults.FirstLastVerticalPadding
        } else {
            DropdownDefaults.MiddleVerticalPadding
        }

    val backgroundColor =
        if (isSelected) {
            dropdownColors.selectedContainerColor
        } else {
            dropdownColors.containerColor
        }
    val backgroundColorState = rememberUpdatedState(backgroundColor)

    val checkColor =
        when {
            !isSelected -> Color.Transparent
            !enabled -> RmuixTheme.colorScheme.disabledOnSecondaryVariant
            else -> dropdownColors.selectedIndicatorColor
        }

    val titleColor =
        when {
            !enabled -> RmuixTheme.colorScheme.disabledOnSecondaryVariant
            isSelected -> dropdownColors.selectedContentColor
            else -> dropdownColors.contentColor
        }

    val summaryColor =
        when {
            !enabled -> RmuixTheme.colorScheme.disabledOnSecondaryVariant
            isSelected -> dropdownColors.selectedSummaryColor
            else -> dropdownColors.summaryColor
        }

    val containerModifier =
        remember(dialogMode, additionalTopPadding, additionalBottomPadding) {
            val sized =
                if (dialogMode) {
                    Modifier
                        .heightIn(min = DropdownDefaults.MinHeight)
                        .widthIn(min = DropdownDefaults.MinWidth)
                        .fillMaxWidth()
                        .padding(horizontal = DropdownDefaults.DialogHorizontalPadding)
                } else {
                    Modifier.padding(horizontal = DropdownDefaults.InsideHorizontalPadding)
                }
            sized.padding(top = additionalTopPadding, bottom = additionalBottomPadding)
        }
    val innerRowModifier =
        remember(dialogMode) {
            if (dialogMode) Modifier else Modifier.widthIn(max = DropdownDefaults.MaxItemTextWidth)
        }

    val currentOnSelectedIndexChange by rememberUpdatedState(onSelectedIndexChange)
    val role = if (hasSubmenu) Role.Button else Role.RadioButton
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier =
            Modifier
                .drawBehind { drawRect(backgroundColorState.value) }
                .selectable(
                    selected = isSelected,
                    enabled = enabled,
                    role = role,
                    onClick = { currentOnSelectedIndexChange(index) },
                ).then(containerModifier),
    ) {
        Row(
            modifier = innerRowModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            item.icon?.let { it(IconCellModifier) }
            Column {
                Text(
                    text = item.text,
                    fontSize = RmuixTheme.textStyles.body1.fontSize,
                    fontWeight = FontWeight.Medium,
                    color = titleColor,
                )
                item.summary?.let {
                    Text(
                        text = it,
                        fontSize = RmuixTheme.textStyles.body2.fontSize,
                        color = summaryColor,
                    )
                }
            }
        }

        if (hasSubmenu) {
            // Chevron uses the softer summaryColor so the trigger row doesn't visually
            // compete with selected/leaf items. The chevron is intentionally static —
            // only the cloned header in the secondary popup rotates during expansion.
            val chevronColor =
                when {
                    !enabled -> RmuixTheme.colorScheme.disabledOnSecondaryVariant
                    isSelected -> dropdownColors.selectedContentColor
                    else -> dropdownColors.summaryColor
                }
            DropdownExpandIndicator(color = chevronColor, modifier = ChevronIconBaseModifier)
        } else {
            // The stock row ends with a CheckBox widget whose *button drawable* is replaced by
            // `coui_menu_ic_checkbox`, whose selected state is the bare stroked check
            // (`coui_menu_ic_checkbox_selected_new`), not a filled box - so only the tick is drawn, and
            // nothing at all when the row is unselected.
            if (isSelected) {
                DropdownMenuCheck(
                    color = if (enabled) dropdownColors.selectedContentColor else RmuixTheme.colorScheme.disabledOnSecondaryVariant,
                    modifier =
                        Modifier
                            .padding(start = DropdownDefaults.CheckIconStartPadding)
                            .size(DropdownDefaults.CheckIconSize),
                )
            } else {
                Spacer(
                    modifier =
                        Modifier
                            .padding(start = DropdownDefaults.CheckIconStartPadding)
                            .size(DropdownDefaults.CheckIconSize),
                )
            }
        }
    }
}

@Composable
@NonRestartableComposable
fun DropdownImpl(
    text: String,
    optionSize: Int,
    isSelected: Boolean,
    index: Int,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    enabled: Boolean = true,
    dialogMode: Boolean = false,
    onSelectedIndexChange: (Int) -> Unit,
) {
    val item = remember(text, enabled) { DropdownItem(text = text, enabled = enabled) }
    DropdownImpl(
        item = item,
        optionSize = optionSize,
        isSelected = isSelected,
        index = index,
        dropdownColors = dropdownColors,
        enabled = enabled,
        dialogMode = dialogMode,
        onSelectedIndexChange = onSelectedIndexChange,
    )
}

/**
 * The implementation of the spinner.
 *
 * @param entry the [DropdownItem] to be shown in the spinner.
 * @param entryCount the count of the entries in the spinner.
 * @param isSelected whether the entry is selected.
 * @param index the index of the entry.
 * @param spinnerColors the [DropdownColors] used to style the entry row.
 * @param dialogMode whether the spinner is in dialog mode.
 * @param onSelectedIndexChange the callback to be invoked when the selected index of the spinner is changed.
 */
@Deprecated(
    message = "Use DropdownImpl instead. SpinnerItemImpl is a thin alias kept for compatibility.",
    replaceWith =
        ReplaceWith(
            "DropdownImpl(item = entry, optionSize = entryCount, isSelected = isSelected, " +
                "index = index, dropdownColors = spinnerColors, enabled = entry.enabled, " +
                "dialogMode = dialogMode, onSelectedIndexChange = onSelectedIndexChange)",
        ),
    level = DeprecationLevel.WARNING,
)
@Composable
fun SpinnerItemImpl(
    entry: DropdownItem,
    entryCount: Int,
    isSelected: Boolean,
    index: Int,
    spinnerColors: DropdownColors,
    dialogMode: Boolean = false,
    onSelectedIndexChange: (Int) -> Unit,
) {
    DropdownImpl(
        item = entry,
        optionSize = entryCount,
        isSelected = isSelected,
        index = index,
        dropdownColors = spinnerColors,
        enabled = entry.enabled,
        dialogMode = dialogMode,
        onSelectedIndexChange = onSelectedIndexChange,
    )
}

/**
 * Colors used by dropdown option rows.
 *
 * @param contentColor The text color of an unselected option.
 * @param summaryColor The summary text color of an unselected option.
 * @param containerColor The background color of an unselected option.
 * @param selectedContentColor The text color of the selected option.
 * @param selectedSummaryColor The summary text color of the selected option.
 * @param selectedContainerColor The background color of the selected option.
 * @param selectedIndicatorColor The color of the selected indicator icon.
 */
@Immutable
data class DropdownColors(
    val contentColor: Color,
    val summaryColor: Color,
    val containerColor: Color,
    val selectedContentColor: Color,
    val selectedSummaryColor: Color,
    val selectedContainerColor: Color,
    val selectedIndicatorColor: Color,
)

/**
 * A group of dropdown items.
 *
 * A [DropdownEntry] represents one visual group in a dropdown menu. Group titles are intentionally
 * reserved for future use because the original MIUI dropdown style currently has no matching
 * group-title presentation.
 *
 * @param items Items shown in this dropdown group.
 * @param enabled Whether this group is enabled. When false, all items in this group are disabled;
 * when true, each item's [DropdownItem.enabled] value is still respected.
 */
@Stable
data class DropdownEntry(
    val items: List<DropdownItem>,
    val enabled: Boolean = true,
)

/**
 * An item shown inside a dropdown, spinner, or dropdown menu.
 *
 * @param text Text shown for the item.
 * @param enabled Whether the item can be clicked.
 * @param selected Whether the item is selected.
 * @param onClick Callback invoked when the item is clicked. Ignored when [children] is non-null
 *   and non-empty (the click is consumed by the cascading layer to expand the submenu).
 * @param icon Optional icon shown before [text].
 * @param summary Optional summary shown below [text].
 * @param children Optional submenu items. When non-null and non-empty, this item becomes a
 *   submenu trigger: cascading dropdown popups render a chevron and open a child popup on click,
 *   recursively. Stabilize this list (e.g. via `remember`) to avoid unnecessary recomposition.
 */
@Stable
data class DropdownItem(
    val text: String,
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val onClick: (() -> Unit)? = null,
    val icon: @Composable ((Modifier) -> Unit)? = null,
    val summary: String? = null,
    val children: List<DropdownItem>? = null,
) {
    /**
     * [SpinnerEntry] compatibility
     */
    constructor(
        icon: @Composable ((Modifier) -> Unit)? = null,
        title: String? = null,
        summary: String? = null,
    ) : this(
        text = title.orEmpty(),
        icon = icon,
        summary = summary,
    )
}

object DropdownDefaults {
    /** `coui_popup_list_window_item_min_height`: the stock menu row is 40dp tall. */
    val MinHeight: Dp = 40.dp

    /** `coui_popup_list_window_min_width`: the menu never measures narrower than 168dp. */
    val MinWidth: Dp = 168.dp

    /** Size of the trailing check icon shown on the selected option. */
    val CheckIconSize: Dp = 24.dp

    /** Size of the up-down arrow rendered by [DropdownArrowEndAction]. */
    val ArrowSize: DpSize = DpSize(width = 10.dp, height = 16.dp)

    /**
     * Size of the expand indicator shown on rows that own a submenu: the viewport of the stock
     * `coui_list_expandable_indicator` vector (24dp x 24dp, two stacked 1.4dp round-cap chevrons).
     */
    val ChevronSize: DpSize = DpSize(width = 24.dp, height = 24.dp)

    /** `coui_popup_list_window_horizontal_item_icon_size_with_text`: a 20dp icon cell. */
    val IconMinSize: Dp = 20.dp

    /** Derived: `coui_popup_list_window_item_max_width` (248dp) − `…_icon_margin_left` (20dp) − `…_icon_margin_right` (12dp) = 216dp. */
    val MaxItemTextWidth: Dp = 216.dp

    /** `coui_popup_list_window_item_icon_margin_left`: 20dp of leading space in popup rows. */
    val InsideHorizontalPadding: Dp = 20.dp

    /** Horizontal padding of each row in dialog mode. */
    val DialogHorizontalPadding: Dp = 28.dp

    /** `coui_popup_list_window_item_padding_top_and_bottom`: 8dp above and below every row. */
    val FirstLastVerticalPadding: Dp = 8.dp

    /** Same `coui_popup_list_window_item_padding_top_and_bottom` value as the first/last rows. */
    val MiddleVerticalPadding: Dp = 8.dp

    /**
     * 12dp between the icon cell and the title. The `coui_popup_list_window_item_title_margin_left` dimen
     * exists but is **never referenced**, so this is a library spacing, not a ported one.
     */
    val IconEndPadding: Dp = 12.dp

    /**
     * 18dp between the title block and the trailing check.
     *
     * `coui_popup_list_window_item_checkbox_margin_end` is 18dp but **never referenced** in the APK, so the
     * value is a library spacing rather than a ported dimens.
     */
    val CheckIconStartPadding: Dp = 18.dp

    /** Upper bound on menu width: ColorOS `coui_popup_list_window_max_width` = 256dp. */
    val MaxWidth: Dp = 256.dp

    /** Width when the menu carries icons: ColorOS `coui_popup_list_window_width_with_icon` = 196dp. */
    val WidthWithIcon: Dp = 196.dp

    /** Menu corner radius: ColorOS popup background uses `coui_round_corner_m_radius` = 9dp. */
    val ContentCornerRadius: Dp = 9.dp

    /** Vertical gap between the menu and its anchor: ColorOS `..._default_vertical_gap_to_anchor` = 8dp. */
    val VerticalGapToAnchor: Dp = 8.dp

    /**
     * Group separator: ColorOS `coui_popup_list_group_divider_height` = 4dp, centred inside
     * `coui_popup_list_group_divider_total_height` = 12dp of space.
     */
    val GroupDividerHeight: Dp = 4.dp

    /** Total vertical space a group separator occupies. */
    val GroupDividerTotalHeight: Dp = 12.dp

    /** Row divider thickness: ColorOS `coui_popup_list_divider_height` = 0.5dp. */
    val DividerThickness: Dp = 0.5.dp

    /** Divider inset when rows carry an icon: ColorOS `..._margin_start_with_icon` = 52dp. */
    val DividerStartIndentWithIcon: Dp = 52.dp

    /** Divider inset without an icon: ColorOS `..._default_divider_margin_horizontal` = 20dp. */
    val DividerStartIndent: Dp = 20.dp

    /** Default [DropdownColors] for an overlay dropdown, keyed on the current theme. */
    @Composable
    fun dropdownColors(
        contentColor: Color = RmuixTheme.colorScheme.onSurfaceContainer,
        summaryColor: Color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
        containerColor: Color = RmuixTheme.colorScheme.surfaceContainer,
        selectedContentColor: Color = RmuixTheme.colorScheme.primary,
        selectedSummaryColor: Color = RmuixTheme.colorScheme.primary,
        selectedContainerColor: Color = RmuixTheme.colorScheme.surfaceContainer,
        selectedIndicatorColor: Color = RmuixTheme.colorScheme.primary,
    ): DropdownColors =
        rememberDropdownColorsImpl(
            contentColor = contentColor,
            summaryColor = summaryColor,
            containerColor = containerColor,
            selectedContentColor = selectedContentColor,
            selectedSummaryColor = selectedSummaryColor,
            selectedContainerColor = selectedContainerColor,
            selectedIndicatorColor = selectedIndicatorColor,
        )

    /** Default [DropdownColors] for a dropdown hosted in a window (dialog variant). */
    @Composable
    fun dialogDropdownColors(
        contentColor: Color = RmuixTheme.colorScheme.onSurfaceContainer,
        summaryColor: Color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
        containerColor: Color = Color.Transparent,
        selectedContentColor: Color = RmuixTheme.colorScheme.onTertiaryContainer,
        selectedSummaryColor: Color = RmuixTheme.colorScheme.onTertiaryContainer,
        selectedContainerColor: Color = RmuixTheme.colorScheme.tertiaryContainer,
        selectedIndicatorColor: Color = RmuixTheme.colorScheme.onTertiaryContainer,
    ): DropdownColors =
        rememberDropdownColorsImpl(
            contentColor = contentColor,
            summaryColor = summaryColor,
            containerColor = containerColor,
            selectedContentColor = selectedContentColor,
            selectedSummaryColor = selectedSummaryColor,
            selectedContainerColor = selectedContainerColor,
            selectedIndicatorColor = selectedIndicatorColor,
        )
}

@Composable
private fun rememberDropdownColorsImpl(
    contentColor: Color,
    summaryColor: Color,
    containerColor: Color,
    selectedContentColor: Color,
    selectedSummaryColor: Color,
    selectedContainerColor: Color,
    selectedIndicatorColor: Color,
): DropdownColors =
    remember(
        contentColor,
        summaryColor,
        containerColor,
        selectedContentColor,
        selectedSummaryColor,
        selectedContainerColor,
        selectedIndicatorColor,
    ) {
        DropdownColors(
            contentColor = contentColor,
            summaryColor = summaryColor,
            containerColor = containerColor,
            selectedContentColor = selectedContentColor,
            selectedSummaryColor = selectedSummaryColor,
            selectedContainerColor = selectedContainerColor,
            selectedIndicatorColor = selectedIndicatorColor,
        )
    }

private val CheckIconBaseModifier =
    Modifier
        .padding(start = DropdownDefaults.CheckIconStartPadding)
        .size(DropdownDefaults.CheckIconSize)

private val ChevronIconBaseModifier =
    Modifier
        .padding(start = DropdownDefaults.CheckIconStartPadding)
        .size(width = DropdownDefaults.ChevronSize.width, height = DropdownDefaults.ChevronSize.height)

private val IconCellModifier =
    Modifier
        .sizeIn(minWidth = DropdownDefaults.IconMinSize, minHeight = DropdownDefaults.IconMinSize)
        .padding(end = DropdownDefaults.IconEndPadding)

@Deprecated(
    message = "Use DropdownDefaults instead.",
    replaceWith = ReplaceWith("DropdownDefaults"),
)
object SpinnerDefaults {
    /** Default [DropdownColors] for an overlay spinner. */
    @Composable
    fun spinnerColors(
        contentColor: Color = RmuixTheme.colorScheme.onSurfaceContainer,
        summaryColor: Color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
        containerColor: Color = RmuixTheme.colorScheme.surfaceContainer,
        selectedContentColor: Color = RmuixTheme.colorScheme.primary,
        selectedSummaryColor: Color = RmuixTheme.colorScheme.primary,
        selectedContainerColor: Color = RmuixTheme.colorScheme.surfaceContainer,
        selectedIndicatorColor: Color = RmuixTheme.colorScheme.primary,
    ): DropdownColors =
        DropdownDefaults.dropdownColors(
            contentColor = contentColor,
            summaryColor = summaryColor,
            containerColor = containerColor,
            selectedContentColor = selectedContentColor,
            selectedSummaryColor = selectedSummaryColor,
            selectedContainerColor = selectedContainerColor,
            selectedIndicatorColor = selectedIndicatorColor,
        )

    /** Default [DropdownColors] for a window-hosted spinner. */
    @Composable
    fun dialogSpinnerColors(
        contentColor: Color = RmuixTheme.colorScheme.onSurfaceContainer,
        summaryColor: Color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
        containerColor: Color = Color.Transparent,
        selectedContentColor: Color = RmuixTheme.colorScheme.onTertiaryContainer,
        selectedSummaryColor: Color = RmuixTheme.colorScheme.onTertiaryContainer,
        selectedContainerColor: Color = RmuixTheme.colorScheme.tertiaryContainer,
        selectedIndicatorColor: Color = RmuixTheme.colorScheme.onTertiaryContainer,
    ): DropdownColors =
        DropdownDefaults.dialogDropdownColors(
            contentColor = contentColor,
            summaryColor = summaryColor,
            containerColor = containerColor,
            selectedContentColor = selectedContentColor,
            selectedSummaryColor = selectedSummaryColor,
            selectedContainerColor = selectedContainerColor,
            selectedIndicatorColor = selectedIndicatorColor,
        )
}

@Deprecated(
    message = "Use DropdownColors instead.",
    replaceWith = ReplaceWith("DropdownColors"),
)
typealias SpinnerColors = DropdownColors

@Deprecated(
    message = "Use DropdownItem instead.",
    replaceWith = ReplaceWith("DropdownItem"),
)
typealias SpinnerEntry = DropdownItem

/**
 * The chevron shown on menu rows that open a submenu.
 *
 * The stock row puts `coui_list_expandable_indicator` into the end `CheckBox` (see `DefaultAdapter`):
 * a 24dp x 24dp vector holding two stacked chevrons, each a 1.4dp round-cap stroke
 * (`M14.2,10 L18.329,5.413 C... L23.2,10` and `M23.2,14 L19.072,18.587 C... L14.2,14`), autoMirrored.
 * It is an *expand* affordance, not the single jump chevron of a preference row.
 */
@Composable
private fun DropdownExpandIndicator(
    color: Color,
    modifier: Modifier = Modifier,
) {
    val layoutDirection = LocalLayoutDirection.current
    Canvas(modifier = modifier) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f
        val mirrored = layoutDirection == LayoutDirection.Rtl

        fun x(value: Float) = if (mirrored) size.width - value * scaleX else value * scaleX

        fun y(value: Float) = value * scaleY

        val stroke = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val upper =
            Path().apply {
                moveTo(x(14.2f), y(10f))
                lineTo(x(18.329f), y(5.413f))
                cubicTo(x(18.527f), y(5.192f), x(18.873f), y(5.192f), x(19.072f), y(5.413f))
                lineTo(x(23.2f), y(10f))
            }
        drawPath(path = upper, color = color, style = stroke)
        val lower =
            Path().apply {
                moveTo(x(23.2f), y(14f))
                lineTo(x(19.072f), y(18.587f))
                cubicTo(x(18.873f), y(18.808f), x(18.527f), y(18.808f), x(18.329f), y(18.587f))
                lineTo(x(14.2f), y(14f))
            }
        drawPath(path = lower, color = color, style = stroke)
    }
}

/**
 * The tick a selected menu row shows.
 *
 * `coui_menu_ic_checkbox_selected_new` is a 24dp x 24dp vector with one stroked path
 * (`M9,12.435 L13.388,17 L23,7`, `strokeWidth 1.6`, round cap and join, `strokeAlpha 0.9`) - a bare
 * checkmark, not the filled box our [Checkbox] component draws. The row is a `CheckBox` widget, but its
 * button drawable is replaced by this vector.
 */
@Composable
private fun DropdownMenuCheck(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f
        val path =
            Path().apply {
                moveTo(9f * scaleX, 12.435f * scaleY)
                lineTo(13.388f * scaleX, 17f * scaleY)
                lineTo(23f * scaleX, 7f * scaleY)
            }
        drawPath(
            path = path,
            color = color.copy(alpha = color.alpha * 0.9f),
            style =
                Stroke(
                    width = 1.6.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
        )
    }
}
