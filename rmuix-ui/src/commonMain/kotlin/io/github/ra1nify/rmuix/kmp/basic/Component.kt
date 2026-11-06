// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.interfaces.HoldDownObserver
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * A basic component with ColorOS 17 style. Widely used in other extension components.
 *
 * Geometry follows the ColorOS `coui_preference.xml` layout and
 * `COUICustomListSelectedLinearLayout`/`COUICustomLinearLayoutForPreference` measurement code:
 * row min height 48dp, container padding 32dp horizontal, text block inset 10dp vertical, and a 16dp
 * gap between a leading icon and the text column.
 *
 * @param modifier The modifier to be applied to the [BasicComponent].
 * @param title The title of the [BasicComponent].
 * @param titleColor The color of the title.
 * @param summary The summary of the [BasicComponent].
 * @param summaryColor The color of the summary.
 * @param assignment Optional third line, drawn under the summary in the primary text colour. ColorOS uses
 *   it for a trailing note such as a hint or a status detail. `null` (default) keeps the previous
 *   two-line structure.
 * @param assignmentColor The color of the assignment line.
 * @param startAction The [Composable] content on the start side of the [BasicComponent].
 * @param endActions The [Composable] content on the end side of the [BasicComponent].
 * @param bottomAction The [Composable] content at the bottom of the [BasicComponent].
 * @param insideMargin The margin inside the [BasicComponent].
 * @param onClick The callback when the [BasicComponent] is clicked.
 * @param onClickLabel Optional label describing the click action for accessibility services.
 * @param role The semantic [Role] of the [BasicComponent] for accessibility services.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [BasicComponent] is enabled.
 * @param iconBadge Draws a hint dot on the top-end corner of [startAction], as ColorOS does for its
 *   `img_red_dot`. `false` (default) draws nothing and reserves no space.
 * @param iconBadgeCount Number shown inside [iconBadge]; `0` renders a plain dot.
 * @param endBadge Draws a small hint dot before [endActions], matching ColorOS `jump_icon_red_dot`.
 *   `false` (default) draws nothing and reserves no space.
 * @param iconMarginTiered When true, the icon's vertical margin is chosen from the ColorOS tier table
 *   based on the measured icon height. When false, [BasicComponentDefaults.IconMarginDefault] is used
 *   for both edges, which is what the pre-migration component did.
 * @param interactionSource The [MutableInteractionSource] for the [BasicComponent].
 *   The value should remain null or non-null for the lifetime of this component;
 *   switching across recompositions will allocate a new internal source and lose pending interactions.
 */
@Composable
fun BasicComponent(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    assignment: String? = null,
    assignmentColor: BasicComponentColors = BasicComponentDefaults.assignmentColor(),
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable (RowScope.() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    role: Role? = null,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
    iconBadge: Boolean = false,
    iconBadgeCount: Int = 0,
    endBadge: Boolean = false,
    iconMarginTiered: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    BasicComponent(
        startAction = startAction,
        endActions = endActions,
        bottomAction = bottomAction,
        modifier = modifier,
        insideMargin = insideMargin,
        onClick = onClick,
        onClickLabel = onClickLabel,
        role = role,
        holdDownState = holdDownState,
        enabled = enabled,
        iconBadge = iconBadge,
        iconBadgeCount = iconBadgeCount,
        endBadge = endBadge,
        iconMarginTiered = iconMarginTiered,
        interactionSource = interactionSource,
    ) {
        if (title != null) {
            Text(
                text = title,
                fontSize = RmuixTheme.textStyles.headline2.fontSize,
                fontWeight = FontWeight.Medium,
                color = titleColor.color(enabled),
            )
        }
        if (summary != null) {
            Text(
                text = summary,
                fontSize = RmuixTheme.textStyles.footnote1.fontSize,
                color = summaryColor.color(enabled),
            )
        }
        if (assignment != null) {
            Text(
                text = assignment,
                fontSize = RmuixTheme.textStyles.footnote1.fontSize,
                color = assignmentColor.color(enabled),
            )
        }
    }
}

/**
 * A basic component with ColorOS 17 style. Widely used in other extension components.
 *
 * @param modifier The modifier to be applied to the [BasicComponent].
 * @param startAction The [Composable] content on the start side of the [BasicComponent].
 * @param endActions The [Composable] content on the end side of the [BasicComponent].
 * @param bottomAction The [Composable] content at the bottom of the [BasicComponent].
 * @param insideMargin The margin inside the [BasicComponent].
 * @param onClick The callback when the [BasicComponent] is clicked.
 * @param onClickLabel Optional label describing the click action for accessibility services.
 * @param role The semantic [Role] of the [BasicComponent] for accessibility services.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param enabled Whether the [BasicComponent] is enabled.
 * @param iconBadge Draws a hint dot on the top-end corner of [startAction].
 * @param iconBadgeCount Number shown inside [iconBadge]; `0` renders a plain dot.
 * @param endBadge Draws a small hint dot before [endActions].
 * @param iconMarginTiered Chooses the icon's vertical margin from the ColorOS tier table.
 * @param interactionSource The [MutableInteractionSource] for the [BasicComponent].
 *   The value should remain null or non-null for the lifetime of this component;
 *   switching across recompositions will allocate a new internal source and lose pending interactions.
 * @param content The content of the [BasicComponent].
 */
@Composable
fun BasicComponent(
    modifier: Modifier = Modifier,
    startAction: @Composable (() -> Unit)? = null,
    endActions: @Composable (RowScope.() -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    role: Role? = null,
    holdDownState: Boolean = false,
    enabled: Boolean = true,
    iconBadge: Boolean = false,
    iconBadgeCount: Int = 0,
    endBadge: Boolean = false,
    iconMarginTiered: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val currentOnClick by rememberUpdatedState(onClick)

    HoldDownObserver(holdDownState, interactionSource)

    val clickableModifier = remember(enabled, onClick != null, interactionSource, role, onClickLabel) {
        if (enabled && onClick != null) {
            Modifier.clickable(
                interactionSource = interactionSource,
                onClickLabel = onClickLabel,
                role = role,
                onClick = { currentOnClick?.invoke() },
            )
        } else {
            Modifier
        }
    }

    Column(
        modifier = modifier
            .heightIn(min = BasicComponentDefaults.MinHeight)
            .fillMaxWidth()
            .then(clickableModifier)
            .padding(insideMargin),
        verticalArrangement = Arrangement.Center,
    ) {
        val density = LocalDensity.current

        if (startAction == null && endActions == null && !endBadge) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                content = content,
            )
        } else {
            Layout(
                content = {
                    startAction?.let {
                        Box(modifier = Modifier.layoutId("start")) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.Start,
                            ) { it() }
                            if (iconBadge) {
                                RmuixHintBadge(
                                    count = iconBadgeCount,
                                    modifier = Modifier.align(Alignment.TopEnd),
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.layoutId("center"),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        content = content,
                    )
                    if (endActions != null || endBadge) {
                        Row(
                            modifier = Modifier.layoutId("end"),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (endBadge) {
                                RmuixHintBadge(count = iconBadgeCount)
                                Spacer(modifier = Modifier.size(BasicComponentDefaults.BadgeGap))
                            }
                            endActions?.let { it() }
                        }
                    }
                },
            ) { measurables, constraints ->
                val spacerPx = BasicComponentDefaults.IconTextGap.roundToPx()

                val startMeasurable = measurables.firstOrNull { it.layoutId == "start" }
                val centerMeasurable = measurables.first { it.layoutId == "center" }
                val endMeasurable = measurables.firstOrNull { it.layoutId == "end" }

                val maxWidth = constraints.maxWidth
                val maxHeight = constraints.maxHeight

                val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

                val startPlaceable = startMeasurable?.measure(looseConstraints)
                val startWidth = startPlaceable?.width ?: 0
                val startSpacerWidth = if (startWidth > 0) spacerPx else 0
                val widthAfterStart = (maxWidth - startWidth - startSpacerWidth).coerceAtLeast(0)

                val endIntrinsicWidth = endMeasurable?.maxIntrinsicWidth(maxHeight) ?: 0
                val endHardCap = (widthAfterStart - spacerPx).coerceAtLeast(0) * 6 / 10
                val endTargetWidth = endIntrinsicWidth.coerceAtMost(endHardCap)
                val endPlaceable = endMeasurable?.measure(
                    looseConstraints.copy(maxWidth = endTargetWidth),
                )
                val endActualWidth = endPlaceable?.width ?: 0
                val endSpacerWidth = if (endActualWidth > 0) spacerPx else 0

                val widthForCenter = (widthAfterStart - endActualWidth - endSpacerWidth).coerceAtLeast(0)
                val centerPlaceable = centerMeasurable.measure(
                    looseConstraints.copy(maxWidth = widthForCenter),
                )

                val startHeight = startPlaceable?.height ?: 0
                val endHeight = endPlaceable?.height ?: 0
                val rowHeight = maxOf(startHeight, centerPlaceable.height, endHeight)
                val layoutHeight = rowHeight
                    .coerceIn(constraints.minHeight, maxHeight.takeIf { it != Constraints.Infinity } ?: rowHeight)

                // ColorOS aligns the leading icon against the text block rather than centring it in the
                // row: the margin comes from a tier table keyed on the icon's own height, and a text block
                // taller than two lines switches the icon to top alignment.
                // ColorOS counts the text block's rendered lines. A Placeable does not expose its line
                // count, so the block height is compared against a single line height instead.
                val singleLinePx = with(density) { 22.dp.toPx() }
                val isMultiline = centerPlaceable.height > singleLinePx * 2.2f
                val iconMargin = if (iconMarginTiered && startPlaceable != null) {
                    BasicComponentDefaults.iconVerticalMargin(
                        iconHeight = with(density) { startPlaceable.height.toDp() },
                        lineCount = (centerPlaceable.height / singleLinePx).toInt().coerceAtLeast(1),
                    )
                } else {
                    BasicComponentDefaults.IconMarginDefault
                }
                val iconMarginPx = iconMargin.roundToPx()
                val centerTop = if (startPlaceable != null && isMultiline) {
                    iconMarginPx
                } else {
                    (rowHeight - centerPlaceable.height) / 2
                }
                val startTop = when {
                    startPlaceable == null -> 0
                    isMultiline -> iconMarginPx
                    else -> centerTop + (centerPlaceable.height - startHeight) / 2
                }
                val endTop = (rowHeight - endHeight).coerceAtLeast(0) / 2

                layout(width = maxWidth, height = layoutHeight) {
                    startPlaceable?.placeRelative(0, startTop)

                    val centerX = startWidth + startSpacerWidth
                    centerPlaceable.placeRelative(centerX, centerTop)

                    endPlaceable?.let {
                        val endX = maxWidth - it.width
                        it.placeRelative(endX, endTop)
                    }
                }
            }
        }

        if (bottomAction != null) {
            Spacer(modifier = Modifier.height(8.dp))
            bottomAction()
        }
    }
}

/**
 * ColorOS hint badge, used for `img_red_dot` (on the leading icon) and `jump_icon_red_dot` (before the
 * trailing content).
 *
 * Sizes come from `COUIHintRedDot`: a plain dot is 6dp, a count badge grows to fit the digits with a
 * 2.67dp corner radius.
 */
@Composable
internal fun RmuixHintBadge(
    modifier: Modifier = Modifier,
    count: Int = 0,
) {
    val dotSize = BasicComponentDefaults.BadgeSize
    if (count <= 0) {
        Box(
            modifier = modifier
                .size(dotSize)
                .clip(CircleShape)
                .background(BasicComponentDefaults.badgeColor()),
        )
    } else {
        Box(
            modifier = modifier
                .heightIn(min = dotSize)
                .widthIn(min = dotSize)
                .clip(RoundedCornerShape(BasicComponentDefaults.BadgeCornerRadius))
                .background(BasicComponentDefaults.badgeColor())
                .padding(horizontal = BasicComponentDefaults.BadgeTextPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                fontSize = RmuixTheme.textStyles.footnote2.fontSize,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Visible,
            )
        }
    }
}

object BasicComponentDefaults {

    /**
     * Row min height: ColorOS `support_preference_min_height` = 48dp.
     */
    val MinHeight: Dp = 48.dp

    /**
     * Margin inside the row: ColorOS draws its content with a 32dp horizontal inset
     * (`support_preference_title_padding_start`/`end`) and 10dp above/below the text block
     * (`support_preference_text_content_padding_top`/`bottom`).
     */
    val InsideMargin = PaddingValues(horizontal = 32.dp, vertical = 10.dp)

    /**
     * Gap between the leading action and the text column: ColorOS
     * `coui_preference_icon_margin_right` = 16dp.
     */
    val IconTextGap: Dp = 16.dp

    /**
     * Icon vertical margin used when [BasicComponent]'s `iconMarginTiered` is off, or when the leading
     * action's height matches no tier: ColorOS `coui_preference_icon_margin_top` = 14dp.
     */
    val IconMarginDefault: Dp = 14.dp

    /** Tier margins for a 24dp leading icon. */
    val IconMargin24: Dp = 12.dp
    val IconMargin24Multiline: Dp = 20.dp

    /** Tier margins for a 32dp leading icon. */
    val IconMargin32: Dp = 10.dp
    val IconMargin32Multiline: Dp = 16.dp

    /** Tier margins for a 36dp leading icon. */
    val IconMargin36: Dp = 12.dp
    val IconMargin36Multiline: Dp = 14.dp

    /** Tier margin for a 50dp leading icon; ColorOS does not vary it for multiline text. */
    val IconMargin50: Dp = 8.dp

    /** Plain hint dot diameter: ColorOS `coui_hint_red_dot_small_reddot_size` = 6dp. */
    val BadgeSize: Dp = 6.dp

    /** Count badge corner radius: ColorOS `coui_hint_red_dot_rect_radius` = 2.67dp. */
    val BadgeCornerRadius: Dp = 2.67.dp

    /** Horizontal padding inside a count badge. */
    val BadgeTextPadding: Dp = 2.dp

    /** Gap between the end badge and the trailing content. */
    val BadgeGap: Dp = 4.dp

    /**
     * Picks the leading icon's vertical margin the way
     * `COUICustomListSelectedLinearLayout.onMeasure` does: a tier per icon size, with a wider value once
     * the text block wraps past one line, and top alignment beyond two lines.
     *
     * @param iconHeight measured height of the leading action, in px
     * @param lineCount total text lines in the content column
     */
    fun iconVerticalMargin(iconHeight: Dp, lineCount: Int): Dp = when (iconHeight) {
        24.dp -> if (lineCount <= 1) IconMargin24 else IconMargin24Multiline
        32.dp -> if (lineCount <= 1) IconMargin32 else IconMargin32Multiline
        36.dp -> if (lineCount <= 1) IconMargin36 else IconMargin36Multiline
        50.dp -> IconMargin50
        else -> IconMarginDefault
    }

    /**
     * The default color of the title.
     */
    @Composable
    fun titleColor(
        color: Color = RmuixTheme.colorScheme.onBackground,
        disabledColor: Color = RmuixTheme.colorScheme.disabledOnSecondaryVariant,
    ): BasicComponentColors = remember(color, disabledColor) {
        BasicComponentColors(
            color = color,
            disabledColor = disabledColor,
        )
    }

    /**
     * The default color of the summary.
     */
    @Composable
    fun summaryColor(
        color: Color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
        disabledColor: Color = RmuixTheme.colorScheme.disabledOnSecondaryVariant,
    ): BasicComponentColors = remember(color, disabledColor) {
        BasicComponentColors(
            color = color,
            disabledColor = disabledColor,
        )
    }

    /**
     * The default color of the assignment line.
     *
     * ColorOS resolves `coui_preference_assignment_text_color` to `couiColorPrimaryText` when enabled —
     * the primary text colour, same as the title, not the muted summary tone. Only its smaller size makes
     * it read quieter.
     */
    @Composable
    fun assignmentColor(
        color: Color = RmuixTheme.colorScheme.onBackground,
        disabledColor: Color = RmuixTheme.colorScheme.disabledOnSecondaryVariant,
    ): BasicComponentColors = remember(color, disabledColor) {
        BasicComponentColors(
            color = color,
            disabledColor = disabledColor,
        )
    }

    /** Hint dot colour: ColorOS draws these in the destruction/danger accent. */
    @Composable
    internal fun badgeColor(): Color = RmuixTheme.colorScheme.error
}

@Immutable
data class BasicComponentColors(
    val color: Color,
    val disabledColor: Color,
) {
    @Stable
    internal fun color(enabled: Boolean): Color = if (enabled) color else disabledColor
}
