// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * One item of an [EditToolbar].
 *
 * Stock model: `COUIBottomFloatingEditToolbarItem` (`title`, `icon`, `description`, `redDotType`,
 * `redDotNumber`); the item view is `COUIBottomFloatingToolbarItemView` with item style 3 (vertical) or
 * 4 (horizontal) on medium/large screens.
 */
@Immutable
data class EditToolbarItem(
    val icon: ImageVector,
    val title: String,
    val enabled: Boolean = true,
    val redDot: Boolean = false,
    /** `0` draws the stock 6dp point dot; a positive number draws the numbered badge. */
    val redDotNumber: Int = 0,
    val contentDescription: String? = null,
)

/**
 * Colours of an [EditToolbar].
 *
 * Sources: `?attr/couiColorLabelPrimaryVariant` (`coui_color_label_primary_variant` `#B8000000` /
 * `#B8FFFFFF`), `?attr/couiColorLabelTertiary` for disabled, `coui_color_additional_red` (`#EB3B2F` /
 * `#EB493D`) for the red dot and `coui_hint_red_dot_text_color` (`#FFFFFF`) for its number.
 */
@Immutable
data class EditToolbarColors(
    val contentColor: Color,
    val disabledContentColor: Color,
    val redDotColor: Color,
    val redDotTextColor: Color,
)

/** Defaults for [EditToolbar], taken from `COUIBottomFloatingEditToolbar` and its item view. */
object EditToolbarDefaults {
    /** `coui_bottom_floating_edit_toolbar_min_height_without_padding_vertical`. */
    val MinHeightWithoutVerticalPadding = 88.dp

    /** `coui_bottom_floating_edit_toolbar_padding_horizontal_small_screen` / `_medium_` / `_large_`. */
    val PaddingHorizontalSmallScreen = 16.dp

    /** Medium-screen horizontal padding. */
    val PaddingHorizontalMediumScreen = 24.dp

    /** Large-screen horizontal padding. */
    val PaddingHorizontalLargeScreen = 40.dp

    /** `coui_bottom_floating_edit_toolbar_inner_padding_horizontal`. */
    val InnerPaddingHorizontal = 10.dp

    /** `coui_bottom_floating_edit_toolbar_inner_padding_vertical`. */
    val InnerPaddingVertical = 12.dp

    /** Small screens: `coui_bottom_floating_navigation_bar_height`, the menu view height. */
    val MenuHeightSmall = 56.dp

    /** Medium/large screens: `coui_bottom_floating_edit_toolbar_menu_height_medium_large`. */
    val MenuHeightMediumLarge = 44.dp

    /** Item style 3 (`applyEditToolbarVerticalStyle`): `coui_bottom_floating_navigation_bar_item_height`. */
    val ItemHeightVerticalStyle = 48.dp

    /** Item style 4 (`applyEditToolbarHorizontalStyle`): `coui_bottom_floating_edit_toolbar_item_height_horizontal`. */
    val ItemHeightHorizontalStyle = 40.dp

    /** `coui_bottom_floating_edit_toolbar_item_max_width_vertical`. */
    val ItemMaxWidthVertical = 70.dp

    /** `coui_bottom_floating_edit_toolbar_item_max_width_horizontal`. */
    val ItemMaxWidthHorizontal = 98.dp

    /** `coui_bottom_floating_edit_toolbar_single_item_width_vertical` (when the row has one item). */
    val SingleItemWidthVertical = 148.dp

    /** `coui_bottom_floating_edit_toolbar_single_item_width_horizontal`. */
    val SingleItemWidthHorizontal = 152.dp

    /** `coui_floating_toolbar_item_icon_width` / `_icon_height`. */
    val ItemIconSize = 24.dp

    /** `coui_floating_toolbar_item_width`: the item's minimum width. */
    val ItemMinWidth = 44.dp

    /** `coui_bottom_floating_navigation_bar_item_icon_title_gap_vertical`. */
    val IconTitleGapVertical = 2.dp

    /** `coui_bottom_floating_navigation_bar_item_icon_title_gap_horizontal`. */
    val IconTitleGapHorizontal = 4.dp

    /** `coui_bottom_floating_navigation_bar_item_icon_top_margin`, vertical style only. */
    val IconTopMargin = 4.dp

    /** `coui_bottom_floating_navigation_bar_item_space`. */
    val ItemSpace = 8.dp

    /** `applyEditToolbarVerticalStyle()`: the 10dp title of item style 3. */
    val TitleTextSizeVertical = 10.sp

    /** `applyEditToolbarHorizontalStyle()`: the 14sp title of item style 4 (`couiTextButtonM`). */
    val TitleTextSizeHorizontal = 14.sp

    /** `coui_dot_diameter`: the point red dot. */
    val RedDotDiameter = 6.dp

    /** `coui_height`: the numbered badge height. */
    val RedDotHeight = 16.dp

    /** `coui_medium_width` / `coui_large_width`: one-digit / multi-digit badge widths. */
    val RedDotMediumWidth = 20.dp

    /** Multi-digit badge width (`coui_large_width`). */
    val RedDotLargeWidth = 26.dp

    /** `coui_hint_text_size`: the badge text size. */
    val RedDotTextSize = 10.sp

    /** `coui_bottom_floating_navigation_bar_red_dot_top_overflow`. */
    val RedDotTopOverflow = 4.dp

    /** `_red_dot_right_overflow_1_digit` / `_2_digit` / `_3_digit`. */
    val RedDotRightOverflow1Digit = 6.dp

    /** Two-digit right overflow. */
    val RedDotRightOverflow2Digit = 9.dp

    /** Three-digit right overflow. */
    val RedDotRightOverflow3Digit = 10.dp

    /** The stock colours, keyed on the current theme. */
    @Composable
    fun colors(
        contentColor: Color = editToolbarContent(isEditToolbarDarkTheme()),
        disabledContentColor: Color = editToolbarDisabled(isEditToolbarDarkTheme()),
        redDotColor: Color = editToolbarRedDot(isEditToolbarDarkTheme()),
        redDotTextColor: Color = Color(0xFFFFFFFF),
    ): EditToolbarColors =
        remember(contentColor, disabledContentColor, redDotColor, redDotTextColor) {
            EditToolbarColors(
                contentColor = contentColor,
                disabledContentColor = disabledContentColor,
                redDotColor = redDotColor,
                redDotTextColor = redDotTextColor,
            )
        }
}

@Composable
private fun isEditToolbarDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/** `coui_color_label_primary_variant` / `_dark`. */
internal fun editToolbarContent(dark: Boolean): Color = if (dark) Color(0xB8FFFFFF) else Color(0xB8000000)

/** `coui_color_label_tertiary` / `_dark`. */
internal fun editToolbarDisabled(dark: Boolean): Color = if (dark) Color(0x4DFFFFFF) else Color(0x42000000)

/** `coui_color_additional_red` / `_dark`. */
internal fun editToolbarRedDot(dark: Boolean): Color = if (dark) Color(0xFFEB493D) else Color(0xFFEB3B2F)

/** Metrics of one [EditToolbar] row, derived from `COUIBottomFloatingEditToolbarMenuView.setHorizontalStyle()`. */
internal data class EditToolbarMetrics(
    val menuHeight: Dp,
    val itemHeight: Dp,
    val itemMaxWidth: Dp,
    val iconTitleGap: Dp,
    val titleTextSize: androidx.compose.ui.unit.TextUnit,
    val itemStyle: Int,
)

/**
 * `COUIBottomFloatingEditToolbarMenuView.setHorizontalStyle(!isSmallScreen)`:
 *
 * - small screens stay vertical (item style **3**, 48dp item, 10sp title, 70dp max width);
 * - medium/large screens switch to horizontal (item style **4**, 40dp item, 14sp title, 98dp max width);
 * - a single item uses the stock 148dp / 152dp single-item widths.
 */
internal fun editToolbarMetrics(
    horizontalItems: Boolean,
    itemCount: Int,
): EditToolbarMetrics {
    val single = itemCount <= 1
    return EditToolbarMetrics(
        menuHeight = if (horizontalItems) EditToolbarDefaults.MenuHeightMediumLarge else EditToolbarDefaults.MenuHeightSmall,
        itemHeight = if (horizontalItems) EditToolbarDefaults.ItemHeightHorizontalStyle else EditToolbarDefaults.ItemHeightVerticalStyle,
        itemMaxWidth =
            when {
                horizontalItems && single -> EditToolbarDefaults.SingleItemWidthHorizontal
                horizontalItems -> EditToolbarDefaults.ItemMaxWidthHorizontal
                single -> EditToolbarDefaults.SingleItemWidthVertical
                else -> EditToolbarDefaults.ItemMaxWidthVertical
            },
        iconTitleGap =
            if (horizontalItems) EditToolbarDefaults.IconTitleGapHorizontal else EditToolbarDefaults.IconTitleGapVertical,
        titleTextSize =
            if (horizontalItems) EditToolbarDefaults.TitleTextSizeHorizontal else EditToolbarDefaults.TitleTextSizeVertical,
        itemStyle = if (horizontalItems) 4 else 3,
    )
}

/**
 * The ColorOS bottom floating edit toolbar: `COUIBottomFloatingEditToolbar` (`com.coui.appcompat.bottomfloatingedittoolbar`).
 *
 * The stock widget is the item row inside the floating toolbar pill (`COUIBottomFloatingToolbar`), with one
 * `COUIBottomFloatingToolbarItemView` per item: icon 24dp, an optional red dot / numbered badge, and a title
 * that is stacked under the icon on small screens (item style 3, 10sp, 56dp row) and inline on medium/large
 * screens (item style 4, 14sp, 44dp row). Gallery's `BottomFloatingMenuBar` is this component with an extra
 * popup and a vertical show/hide spring. The pill itself is re-used from [FloatingToolbar].
 *
 * @param items The toolbar items.
 * @param onItemClick Called with the item index when an enabled item is clicked.
 * @param modifier Applied to the pill.
 * @param horizontalItems Whether to use item style 4; `null` follows the stock window-width class
 *   (>= 600dp = medium/large = horizontal, otherwise vertical).
 * @param colors The item and badge colours.
 */
@Composable
fun EditToolbar(
    items: List<EditToolbarItem>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    horizontalItems: Boolean? = null,
    colors: EditToolbarColors = EditToolbarDefaults.colors(),
) {
    val density = LocalDensity.current
    val windowWidthDp =
        with(density) {
            LocalWindowInfo.current.containerSize.width
                .toDp()
        }
    val horizontal = horizontalItems ?: (windowWidthDp >= FloatingToolbarDefaults.MediumScreenMinWidth)
    val metrics = remember(horizontal, items.size) { editToolbarMetrics(horizontal, items.size) }
    FloatingToolbar(
        modifier = modifier,
        groupMargin = FloatingToolbarDefaults.groupMarginFor(windowWidthDp),
    ) {
        Row(
            modifier =
                Modifier
                    .height(metrics.menuHeight)
                    .padding(horizontal = EditToolbarDefaults.InnerPaddingHorizontal),
            horizontalArrangement = Arrangement.spacedBy(EditToolbarDefaults.ItemSpace),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { index, item ->
                val contentColor = if (item.enabled) colors.contentColor else colors.disabledContentColor
                EditToolbarItemContent(
                    item = item,
                    metrics = metrics,
                    contentColor = contentColor,
                    colors = colors,
                    modifier =
                        Modifier
                            .defaultMinSize(minWidth = EditToolbarDefaults.ItemMinWidth)
                            .widthIn(max = metrics.itemMaxWidth)
                            .height(metrics.itemHeight)
                            .clickable(enabled = item.enabled) { onItemClick(index) },
                )
            }
        }
    }
}

@Composable
private fun EditToolbarItemContent(
    item: EditToolbarItem,
    metrics: EditToolbarMetrics,
    contentColor: Color,
    colors: EditToolbarColors,
    modifier: Modifier = Modifier,
) {
    val horizontal = metrics.itemStyle == 4
    if (horizontal) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EditToolbarIcon(item = item, contentColor = contentColor, colors = colors)
            Spacer(Modifier.size(metrics.iconTitleGap))
            Text(
                modifier = Modifier.padding(end = 4.dp),
                text = item.title,
                color = contentColor,
                fontSize = metrics.titleTextSize,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.height(EditToolbarDefaults.IconTopMargin))
            EditToolbarIcon(item = item, contentColor = contentColor, colors = colors)
            Spacer(Modifier.size(metrics.iconTitleGap))
            Text(
                text = item.title,
                color = contentColor,
                fontSize = metrics.titleTextSize,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun EditToolbarIcon(
    item: EditToolbarItem,
    contentColor: Color,
    colors: EditToolbarColors,
) {
    Box(modifier = Modifier.size(EditToolbarDefaults.ItemIconSize)) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.contentDescription ?: item.title,
            tint = contentColor,
            modifier = Modifier.size(EditToolbarDefaults.ItemIconSize),
        )
        if (item.redDot && item.enabled) {
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                EditToolbarBadge(item = item, colors = colors)
            }
        }
    }
}

@Composable
private fun EditToolbarBadge(
    item: EditToolbarItem,
    colors: EditToolbarColors,
) {
    val numbered = item.redDotNumber > 0
    val digits = item.redDotNumber.toString().length
    val width =
        when {
            !numbered -> EditToolbarDefaults.RedDotDiameter
            digits <= 1 -> EditToolbarDefaults.RedDotMediumWidth
            else -> EditToolbarDefaults.RedDotLargeWidth
        }
    val rightOverflow =
        when {
            !numbered -> EditToolbarDefaults.RedDotRightOverflow1Digit
            digits <= 1 -> EditToolbarDefaults.RedDotRightOverflow1Digit
            digits == 2 -> EditToolbarDefaults.RedDotRightOverflow2Digit
            else -> EditToolbarDefaults.RedDotRightOverflow3Digit
        }
    Box(
        modifier =
            Modifier
                .offset(x = rightOverflow, y = -EditToolbarDefaults.RedDotTopOverflow)
                .height(if (numbered) EditToolbarDefaults.RedDotHeight else EditToolbarDefaults.RedDotDiameter)
                .widthIn(min = width)
                .clip(CircleShape)
                .background(colors.redDotColor),
        contentAlignment = Alignment.Center,
    ) {
        if (numbered) {
            Text(
                text = if (item.redDotNumber > 99) "99+" else item.redDotNumber.toString(),
                color = colors.redDotTextColor,
                fontSize = EditToolbarDefaults.RedDotTextSize,
                maxLines = 1,
            )
        }
    }
}
