// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastRoundToInt
import io.github.ra1nify.rmuix.kmp.interfaces.HoldDownObserver
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight
import io.github.ra1nify.rmuix.kmp.theme.LocalTextStyles
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.roundToInt

/** Measured line metrics of one [BasicComponent] text view, recorded from its `TextLayoutResult`. */
@Immutable
internal data class PreferenceTextMetrics(
    val lineCount: Int,
    val heightPx: Float,
    val firstLineBottomPx: Float,
    val secondLineBottomPx: Float,
)

/**
 * Records the measured title / summary / assignment metrics so `operateMultilineIconPosition()` can use
 * the real `TextView.getLineCount()` instead of the old height-based estimate.
 */
@Stable
internal class PreferenceTextMetricsRecorder {
    var title by mutableStateOf<PreferenceTextMetrics?>(null)
    var summary by mutableStateOf<PreferenceTextMetrics?>(null)
    var assignment by mutableStateOf<PreferenceTextMetrics?>(null)

    /** True once at least one of the three text views has reported its layout. */
    val hasMetrics: Boolean
        get() = title != null || summary != null || assignment != null

    /** `TextView.getLineCount()` of title + summary + assignment, as `operateMultilineIconPosition()` sums them. */
    val totalLineCount: Int
        get() = (title?.lineCount ?: 0) + (summary?.lineCount ?: 0) + (assignment?.lineCount ?: 0)
}

/** The wrapper's recorder, read by the custom layout to compute the stock icon alignment. */
internal val LocalPreferenceTextMetrics = compositionLocalOf<PreferenceTextMetricsRecorder?> { null }

private const val PREFERENCE_TEXT_TITLE = 0
private const val PREFERENCE_TEXT_SUMMARY = 1
private const val PREFERENCE_TEXT_ASSIGNMENT = 2

private fun recordPreferenceTextMetrics(
    recorder: PreferenceTextMetricsRecorder,
    role: Int,
    result: TextLayoutResult,
) {
    val metrics =
        PreferenceTextMetrics(
            lineCount = result.lineCount,
            heightPx = result.size.height.toFloat(),
            firstLineBottomPx = if (result.lineCount > 0) result.getLineBottom(0).toFloat() else 0f,
            secondLineBottomPx = if (result.lineCount > 1) result.getLineBottom(1).toFloat() else 0f,
        )
    when (role) {
        PREFERENCE_TEXT_TITLE -> recorder.title = metrics
        PREFERENCE_TEXT_SUMMARY -> recorder.summary = metrics
        PREFERENCE_TEXT_ASSIGNMENT -> recorder.assignment = metrics
    }
}

/**
 * `android:lineSpacingMultiplier` of the stock title / summary appearances (see
 * [BasicComponentDefaults.TitleLineSpacingMultiplier] and
 * [BasicComponentDefaults.SummaryLineSpacingMultiplier]).
 *
 * Android scales the font's natural line height by the multiplier; Compose has no such factor, so this
 * helper measures the real natural line height of the style on the current platform (two identical
 * lines, `getLineBottom(1) - getLineBottom(0)`) and returns `natural * multiplier` as a [TextUnit].
 */
@Composable
internal fun rememberPreferenceLineHeight(
    fontSize: TextUnit,
    fontWeight: FontWeight,
    multiplier: Float,
): TextUnit {
    val density = LocalDensity.current
    val baseStyle = LocalTextStyles.current.main
    val textMeasurer = rememberTextMeasurer()
    return remember(fontSize, fontWeight, multiplier, density, baseStyle, textMeasurer) {
        val style = baseStyle.copy(fontSize = fontSize, fontWeight = fontWeight)
        val sample =
            textMeasurer.measure(
                text = "Mg\nMg",
                style = style,
                maxLines = 2,
                softWrap = false,
            )
        val naturalLineHeightPx =
            if (sample.lineCount >= 2) {
                (sample.getLineBottom(1) - sample.getLineBottom(0)).toFloat()
            } else {
                0f
            }
        if (naturalLineHeightPx <= 0f) TextUnit.Unspecified else with(density) { (naturalLineHeightPx * multiplier).toSp() }
    }
}

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
 * @param assignmentInRight Draws [assignment] right-aligned on the title row instead of as a third line
 *   under the summary, like ColorOS `coui_preference_assignment_in_right.xml`: the text column keeps only
 *   the title and summary, while the assignment sits before [endActions], right-aligned, capped at two
 *   lines with an end ellipsis in the secondary colour (`TextAppearance.COUI.List.Assignment.End`), with
 *   a 16dp gap (`support_preference_title_margin_end_in_right`). `false` (default) keeps the stacked
 *   three-line layout.
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
 * @param enableSpotlight Draws the shared light effect on touch. Off by default: the stock
 *   `coui_preference_bg_selector` only paints `couiColorPressBackground` and has no light.
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
    assignmentInRight: Boolean = false,
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
    statusText: String? = null,
    statusTextColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    iconMarginTiered: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    enableSpotlight: Boolean = false,
) {
    val textMetricsRecorder = remember { PreferenceTextMetricsRecorder() }
    // `android:lineSpacingMultiplier` 1.05 / 1.1: Android multiplies the font's natural line height;
    // Compose has no multiplier, so measure the platform's natural line height and scale it.
    val titleLineHeight =
        rememberPreferenceLineHeight(
            fontSize = BasicComponentDefaults.TitleTextSize,
            fontWeight = FontWeight.Medium,
            multiplier = BasicComponentDefaults.TitleLineSpacingMultiplier,
        )
    val summaryLineHeight =
        rememberPreferenceLineHeight(
            fontSize = BasicComponentDefaults.SummaryTextSize,
            fontWeight = FontWeight.Normal,
            multiplier = BasicComponentDefaults.SummaryLineSpacingMultiplier,
        )
    CompositionLocalProvider(LocalPreferenceTextMetrics provides textMetricsRecorder) {
        BasicComponent(
            startAction = startAction,
            endActions = {
                if (assignmentInRight && assignment != null) {
                    // `coui_preference_assignment_in_right.xml`: the assignment is the trailing view of the
                    // preference row; its style `TextAppearance.COUI.List.Assignment.End` inherits the
                    // summary appearance (12sp) but switches to the secondary colour and an end ellipsis.
                    Text(
                        text = assignment,
                        fontSize = BasicComponentDefaults.SummaryTextSize,
                        color = summaryColor.color(enabled),
                        textAlign = TextAlign.End,
                        lineHeight = summaryLineHeight,
                        maxLines = BasicComponentDefaults.ASSIGNMENT_IN_RIGHT_MAX_LINES,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = BasicComponentDefaults.AssignmentInRightGap),
                    )
                }
                endActions?.invoke(this)
            },
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
            statusText = statusText,
            statusTextColor = statusTextColor,
            iconMarginTiered = iconMarginTiered,
            interactionSource = interactionSource,
            enableSpotlight = enableSpotlight,
        ) {
            if (title != null) {
                Text(
                    text = title,
                    fontSize = BasicComponentDefaults.TitleTextSize,
                    fontWeight = FontWeight.Medium,
                    color = titleColor.color(enabled),
                    lineHeight = titleLineHeight,
                    onTextLayout = { recordPreferenceTextMetrics(textMetricsRecorder, PREFERENCE_TEXT_TITLE, it) },
                )
            }
            if (summary != null) {
                Text(
                    text = summary,
                    fontSize = BasicComponentDefaults.SummaryTextSize,
                    color = summaryColor.color(enabled),
                    lineHeight = summaryLineHeight,
                    onTextLayout = { recordPreferenceTextMetrics(textMetricsRecorder, PREFERENCE_TEXT_SUMMARY, it) },
                )
            }
            if (assignment != null && !assignmentInRight) {
                Text(
                    text = assignment,
                    fontSize = BasicComponentDefaults.SummaryTextSize,
                    color = assignmentColor.color(enabled),
                    lineHeight = summaryLineHeight,
                    onTextLayout = { recordPreferenceTextMetrics(textMetricsRecorder, PREFERENCE_TEXT_ASSIGNMENT, it) },
                )
            }
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
 * @param enableSpotlight Draws the shared light effect on touch; off by default.
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
    statusText: String? = null,
    statusTextColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    iconMarginTiered: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    enableSpotlight: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val currentOnClick by rememberUpdatedState(onClick)

    HoldDownObserver(holdDownState, interactionSource)

    // Inside a BasicComponentGroup this row reports where its title starts, so the group can align the
    // hairline to the stock `getDividerStartAlignView()` title view instead of a fixed inset.
    val groupDividerStarts = LocalBasicComponentGroupDividerStarts.current
    val dividerSlot = remember(groupDividerStarts) { groupDividerStarts?.newSlot() }
    DisposableEffect(groupDividerStarts, dividerSlot) {
        onDispose { if (dividerSlot != null) groupDividerStarts?.removeSlot(dividerSlot) }
    }

    val dark = isBasicComponentInDarkTheme()
    // The wrapper records the real title/summary/assignment line metrics here; custom `content` leaves it
    // empty and the layout keeps the height-based estimate.
    val textMetricsRecorder = LocalPreferenceTextMetrics.current
    val isPressed by interactionSource.collectIsPressedAsState()
    // `coui_preference_bg_selector`: press paints `couiColorPressBackground` (100ms in / 360ms
    // out); the disabled branch is fully transparent, so nothing is painted there.
    val pressTarget =
        if (enabled && onClick != null && isPressed) {
            preferencePress(dark)
        } else {
            Color.Transparent
        }
    val rowBackground by animateColorAsState(
        targetValue = pressTarget,
        animationSpec =
            tween(
                durationMillis =
                    if (pressTarget != Color.Transparent) {
                        PREFERENCE_PRESS_FADE_IN_MILLIS
                    } else {
                        PREFERENCE_PRESS_FADE_OUT_MILLIS
                    },
            ),
        label = "PreferencePressBackground",
    )

    val clickableModifier =
        remember(enabled, onClick != null, interactionSource, role, onClickLabel) {
            if (enabled && onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClickLabel = onClickLabel,
                    role = role,
                    onClick = { currentOnClick?.invoke() },
                )
            } else {
                Modifier
            }
        }

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val insideStartPx = with(density) { insideMargin.calculateStartPadding(layoutDirection).roundToPx() }

    Column(
        modifier =
            modifier
                .background(rowBackground)
                .then(
                    if (enableSpotlight && enabled && onClick != null) {
                        Modifier.rmuixSpotlight(
                            darkTheme = dark,
                            enabled = true,
                        )
                    } else {
                        Modifier
                    },
                ).heightIn(min = BasicComponentDefaults.MinHeight)
                .fillMaxWidth()
                .then(clickableModifier)
                .padding(insideMargin),
        verticalArrangement = Arrangement.Center,
    ) {
        if (startAction == null && endActions == null && !endBadge && statusText == null) {
            // No leading content: the title starts at the content inset.
            SideEffect { dividerSlot?.startPx = insideStartPx.toFloat() }
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
                                // `coui_preference.xml` `img_red_dot` carries
                                // `support_preference_icon_red_dot_margin_top` = 2dp top and end.
                                RmuixHintBadge(
                                    count = iconBadgeCount,
                                    modifier =
                                        Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(top = 2.dp, end = 2.dp),
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
                    if (endActions != null || endBadge || statusText != null) {
                        Row(
                            modifier = Modifier.layoutId("end"),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (endBadge) {
                                RmuixHintBadge(count = iconBadgeCount)
                                Spacer(modifier = Modifier.size(BasicComponentDefaults.BadgeGap))
                            }
                            // `coui_preference_widget_select.xml`: a trailing status line sits before
                            // the widget, right aligned, capped at 162dp with a 24dp start margin.
                            statusText?.let {
                                Text(
                                    text = it,
                                    color = statusTextColor.color(enabled),
                                    fontSize = BasicComponentDefaults.StatusTextSize,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.End,
                                    modifier =
                                        Modifier
                                            .padding(start = BasicComponentDefaults.StatusTextStartMargin)
                                            .widthIn(max = BasicComponentDefaults.StatusTextMaxWidth),
                                )
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
                // `basiccomponent_layout` title x = content start + leading action + its 16dp gap.
                dividerSlot?.startPx = (insideStartPx + startWidth + startSpacerWidth).toFloat()
                val widthAfterStart = (maxWidth - startWidth - startSpacerWidth).coerceAtLeast(0)

                val endIntrinsicWidth = endMeasurable?.maxIntrinsicWidth(maxHeight) ?: 0
                val endHardCap = (widthAfterStart - spacerPx).coerceAtLeast(0) * 6 / 10
                val endTargetWidth = endIntrinsicWidth.coerceAtMost(endHardCap)
                val endPlaceable =
                    endMeasurable?.measure(
                        looseConstraints.copy(maxWidth = endTargetWidth),
                    )
                val endActualWidth = endPlaceable?.width ?: 0
                val endSpacerWidth = if (endActualWidth > 0) spacerPx else 0

                val widthForCenter = (widthAfterStart - endActualWidth - endSpacerWidth).coerceAtLeast(0)
                val centerPlaceable =
                    centerMeasurable.measure(
                        looseConstraints.copy(maxWidth = widthForCenter),
                    )

                val startHeight = startPlaceable?.height ?: 0
                val endHeight = endPlaceable?.height ?: 0

                // `COUICustomListSelectedLinearLayout.operateMultilineIconPosition()`:
                // - up to two text lines: the icon frame keeps centre gravity, and its symmetric tier
                //   margins only grow the row (so the icon top is (rowHeight - iconHeight) / 2);
                // - more than two lines: the icon frame switches to top gravity and gets
                //   max(centreOfFirstTwoLines - iconHeight / 2, coui_preference_50icon_margin_vertical_default)
                //   as topMargin, while the style's 14dp bottomMargin still contributes to the row height.
                // The wrapper's title/summary/assignment texts report their real `TextLayoutResult` line
                // metrics, so the stock `getLineCount()` / `getLineBottom()` values are exact; only a
                // custom `content` falls back to the height-based line estimate below.
                val textPaddingPx = with(density) { BasicComponentDefaults.TextBlockVerticalPadding.toPx() }
                val lineUnitPx = with(density) { BasicComponentDefaults.EstimatedLineHeight.toPx() }
                // Prefer the real `TextView.getLineCount()` that the wrapper supplies through its
                // `TextLayoutResult`; the height-based estimate remains the fallback for custom content.
                val recordedMetrics = textMetricsRecorder?.takeIf { it.hasMetrics }
                val totalLines =
                    recordedMetrics?.totalLineCount
                        ?: estimatedPreferenceLineCount(
                            textBlockHeightPx = centerPlaceable.height.toFloat(),
                            lineUnitPx = lineUnitPx,
                        )
                val iconMargin =
                    if (iconMarginTiered && startPlaceable != null) {
                        BasicComponentDefaults.iconVerticalMargin(
                            iconHeight = with(density) { startPlaceable.height.toDp() },
                            lineCount = totalLines,
                        )
                    } else {
                        BasicComponentDefaults.IconMarginDefault
                    }
                val iconMarginPx = iconMargin.roundToPx()
                val minIconMarginPx = with(density) { BasicComponentDefaults.IconMargin50.toPx() }
                val textPaddingTopPx = with(density) { BasicComponentDefaults.TextBlockVerticalPadding.toPx() }
                val multilineIconTopPx =
                    when {
                        startPlaceable == null -> {
                            0
                        }

                        recordedMetrics != null -> {
                            preferenceMultilineIconTopMarginExactPx(
                                recorder = recordedMetrics,
                                textContentPaddingTopPx = textPaddingTopPx,
                                marginBetweenLinePx = with(density) { BasicComponentDefaults.LineGap.toPx() },
                                iconHeightPx = startPlaceable.height.toFloat(),
                                minMarginPx = minIconMarginPx,
                            )
                        }

                        else -> {
                            preferenceMultilineIconTopMarginPx(
                                centerHeightPx = centerPlaceable.height.toFloat(),
                                iconHeightPx = startPlaceable.height.toFloat(),
                                textPaddingTopPx = textPaddingTopPx,
                                lineUnitPx = lineUnitPx,
                                minMarginPx = minIconMarginPx,
                            )
                        }
                    }
                val iconBlockHeightPx =
                    when {
                        startPlaceable == null -> {
                            0
                        }

                        totalLines <= 2 -> {
                            startPlaceable.height + 2 * iconMarginPx
                        }

                        else -> {
                            multilineIconTopPx +
                                startPlaceable.height +
                                BasicComponentDefaults.IconMarginDefault.roundToPx()
                        }
                    }
                val minRowTotalPx = with(density) { BasicComponentDefaults.MinHeight.toPx() }
                val stockRowHeightPx =
                    maxOf(
                        minRowTotalPx.toInt(),
                        centerPlaceable.height + 2 * textPaddingPx.toInt(),
                        iconBlockHeightPx,
                        endHeight,
                    )
                val topInsetPx = insideMargin.calculateTopPadding().roundToPx()
                val bottomInsetPx = insideMargin.calculateBottomPadding().roundToPx()
                val measuredLayoutHeight =
                    (stockRowHeightPx - topInsetPx - bottomInsetPx)
                        .coerceAtLeast(0)
                        .coerceIn(
                            constraints.minHeight,
                            maxHeight.takeIf { it != Constraints.Infinity } ?: Int.MAX_VALUE,
                        )
                val centerTop = ((stockRowHeightPx - centerPlaceable.height) / 2f - topInsetPx).fastRoundToInt()
                val startTop =
                    when {
                        startPlaceable == null -> 0
                        totalLines <= 2 -> ((stockRowHeightPx - startPlaceable.height) / 2f - topInsetPx).fastRoundToInt()
                        else -> multilineIconTopPx - topInsetPx
                    }
                val endTop = ((stockRowHeightPx - endHeight) / 2f - topInsetPx).fastRoundToInt()

                layout(width = maxWidth, height = measuredLayoutHeight) {
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
    val color = redDotBackground(isBasicComponentInDarkTheme())
    if (count <= 0) {
        Box(
            modifier =
                modifier
                    .size(BasicComponentDefaults.BadgeSize)
                    .clip(CircleShape)
                    .background(color),
        )
    } else {
        // `COUIHintRedDotHelper.getBgWidth`: 16 / 20 / 26dp for 1 / 2 / 3 digits, and the ellipsis
        // form uses the medium width; the height is `coui_height` (16dp) in every count mode.
        val width =
            when {
                count < 10 -> BasicComponentDefaults.BadgeSmallWidth
                count < 100 -> BasicComponentDefaults.BadgeMediumWidth
                count < 1000 -> BasicComponentDefaults.BadgeLargeWidth
                else -> BasicComponentDefaults.BadgeMediumWidth
            }
        Box(
            modifier =
                modifier
                    .height(BasicComponentDefaults.BadgeHeight)
                    .widthIn(min = width)
                    .clip(RoundedCornerShape(BasicComponentDefaults.BadgeCornerRadius))
                    .background(color),
            contentAlignment = Alignment.Center,
        ) {
            if (count >= 1000) {
                // `drawNumber` >= 1000: three ellipsis circles of 2dp with 2dp spacing.
                Row(horizontalArrangement = Arrangement.spacedBy(BasicComponentDefaults.BadgeEllipsisSpacing)) {
                    repeat(3) {
                        Box(
                            modifier =
                                Modifier
                                    .size(BasicComponentDefaults.BadgeEllipsisDotSize)
                                    .clip(CircleShape)
                                    .background(Color.White),
                        )
                    }
                }
            } else {
                Text(
                    text = count.toString(),
                    fontSize = BasicComponentDefaults.BadgeTextSize,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 1,
                )
            }
        }
    }
}

object BasicComponentDefaults {
    /**
     * Row min height: ColorOS `support_preference_min_height` = 48dp.
     */
    val MinHeight: Dp = 48.dp

    /**
     * `support_preference_text_content_padding_top`: the stock text block (`PreferenceContent`) pads itself
     * by 10dp top and bottom; `COUICustomListSelectedLinearLayout.setIconTopInParent()` starts its
     * first-two-line centre from this value.
     */
    internal val TextBlockVerticalPadding: Dp = 10.dp

    /**
     * `support_preference_margin_between_line` = 2dp: `operateMultilineIconPosition()` offsets the
     * summary / assignment start by this gap when it folds the text views into the first-two-line centre.
     */
    internal val LineGap: Dp = 2.dp

    /**
     * One rendered text line, used only as the fallback `TextView.getLineCount()` estimate for a custom
     * `content` (the title/summary wrapper reports real `TextLayoutResult` metrics instead; 16sp title
     * line ≈ 19dp, 13sp summary ≈ 17dp).
     */
    internal val EstimatedLineHeight: Dp = 20.dp

    /**
     * Title size: ColorOS `support_preference_title_size` = 16sp, i.e. the theme's headline2 size
     * that the title already used.
     */
    val TitleTextSize = 16.sp

    /**
     * `COUIPreferenceTitleStyle` -> `TextAppearance.COUI.List.Title` -> `couiTextAppearanceHeadline6`,
     * whose `android:lineSpacingMultiplier` is `coui_spacing_multiplier_headline_xs` = **1.05**.
     */
    val TitleLineSpacingMultiplier = 1.05f

    /**
     * `COUIPreferenceSummaryStyle` -> `TextAppearance.COUI.Preference.Summary` ->
     * `couiTextAppearanceArticleBody`, whose `android:lineSpacingMultiplier` is
     * `coui_spacing_multiplier_body_s` = **1.1**.
     */
    val SummaryLineSpacingMultiplier = 1.1f

    /**
     * Summary and assignment size: ColorOS `couiTextAppearanceArticleBody` = 13sp.
     *
     * `coui_preference.xml` styles the summary with `COUIPreferenceSummaryStyle` ->
     * `TextAppearance.COUI.Preference.Summary` -> `couiTextAppearanceArticleBody` (13sp,
     * `lineSpacingMultiplier` 1.1), and `TextAppearance.COUI.List.Assignment` (the third line) inherits
     * the same appearance. `support_preference_summary_size` (12sp) is declared in the APK but no class
     * reads it - it only appears in the generated `R` - so it is not the size the stock row renders.
     */
    val SummaryTextSize = 13.sp

    /**
     * Trailing status line size: `PreferenceStatusText.Tiny` (`coui_preference_widget_select.xml`)
     * overrides the body appearance to 14dp.
     */
    val StatusTextSize = 14.sp

    /** `coui_preference_status_text_max_width`: the status line never exceeds 162dp. */
    val StatusTextMaxWidth = 162.dp

    /** `support_preference_widget_focus_jump_margin_start`: the status line starts 24dp in. */
    val StatusTextStartMargin = 24.dp

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

    /** Plain hint dot diameter: ColorOS `coui_dot_diameter` = 6dp (`COUIHintRedDot` point mode). */
    val BadgeSize: Dp = 6.dp

    /** Count badge height: `coui_height` = 16dp (`COUIHintRedDotHelper.getBgHeight`). */
    val BadgeHeight: Dp = 16.dp

    /**
     * Count badge corner radius: `?attr/couiRoundCornerFULL`, i.e. a pill = [BadgeHeight] / 2.
     * `coui_hint_red_dot_rect_radius` (2.67dp) is assigned to `mDotCornerRadius` and never read, so it
     * is not the badge radius.
     */
    val BadgeCornerRadius: Dp = 8.dp

    /** Count badge width for one digit: `coui_small_width` = 16dp. */
    val BadgeSmallWidth: Dp = 16.dp

    /** Count badge width for two digits (and the `1000+` ellipsis): `coui_medium_width` = 20dp. */
    val BadgeMediumWidth: Dp = 20.dp

    /** Count badge width for three digits: `coui_large_width` = 26dp. */
    val BadgeLargeWidth: Dp = 26.dp

    /** Count badge text size: `coui_hint_text_size` = 10dp, rendered as 10sp with `sans-serif-medium`. */
    val BadgeTextSize = 10.sp

    /** `coui_hint_red_dot_ellipsis`: a count of 1000 or more renders as three 2dp dots 2dp apart. */
    val BadgeEllipsisDotSize: Dp = 2.dp
    val BadgeEllipsisSpacing: Dp = 2.dp

    /** Horizontal padding inside a count badge; the stock badge uses a fixed width per digit tier. */
    val BadgeTextPadding: Dp = 0.dp

    /** Gap between the end badge and the trailing content: the stock jump dot carries no margin. */
    val BadgeGap: Dp = 0.dp

    /**
     * Gap between the text column and a right-aligned assignment:
     * `support_preference_title_margin_end_in_right` = 16dp, read by
     * `COUICustomLinearLayoutForPreference.mMessageLayoutMarginEndInRight`.
     */
    val AssignmentInRightGap: Dp = 16.dp

    /**
     * Line cap of the right-aligned assignment: `coui_preference_assignment_in_right.xml` declares
     * `android:maxLines="2"` plus an end ellipsis.
     */
    const val ASSIGNMENT_IN_RIGHT_MAX_LINES = 2

    /**
     * Picks the leading icon's vertical margin the way
     * `COUICustomListSelectedLinearLayout.onMeasure` does: a tier per icon size, with a wider value once
     * the text block wraps past one line, and top alignment beyond two lines.
     *
     * @param iconHeight measured height of the leading action, in px
     * @param lineCount total text lines in the content column
     */
    fun iconVerticalMargin(
        iconHeight: Dp,
        lineCount: Int,
    ): Dp =
        when (iconHeight) {
            24.dp -> if (lineCount <= 1) IconMargin24 else IconMargin24Multiline
            32.dp -> if (lineCount <= 1) IconMargin32 else IconMargin32Multiline
            36.dp -> if (lineCount <= 1) IconMargin36 else IconMargin36Multiline
            50.dp -> IconMargin50
            else -> IconMarginDefault
        }

    /**
     * The default color of the title: `coui_preference_title_color` → `couiColorLabelPrimary`.
     * Disabled rows use `couiColorLabelTertiary`.
     */
    @Composable
    fun titleColor(
        color: Color = preferenceTitle(isBasicComponentInDarkTheme()),
        disabledColor: Color = preferenceTertiary(isBasicComponentInDarkTheme()),
    ): BasicComponentColors =
        remember(color, disabledColor) {
            BasicComponentColors(
                color = color,
                disabledColor = disabledColor,
            )
        }

    /**
     * The default color of the summary: `coui_preference_secondary_text_color` →
     * `couiColorSecondNeutral`. Disabled rows use `couiColorLabelTertiary`.
     */
    @Composable
    fun summaryColor(
        color: Color = preferenceSummary(isBasicComponentInDarkTheme()),
        disabledColor: Color = preferenceTertiary(isBasicComponentInDarkTheme()),
    ): BasicComponentColors =
        remember(color, disabledColor) {
            BasicComponentColors(
                color = color,
                disabledColor = disabledColor,
            )
        }

    /**
     * The default color of the assignment line: `coui_preference_assignment_text_color` →
     * `?attr/couiColorPrimaryText`, which `Theme.COUI.Main` maps to `coui_color_primary_text_blue` =
     * `#0080FF` light / `#5C9DFF` dark. That is an app accent, not the library theme's
     * `colorScheme.primary`. Disabled rows use `couiColorLabelTertiary`.
     */
    @Composable
    fun assignmentColor(
        color: Color = preferenceAssignment(isBasicComponentInDarkTheme()),
        disabledColor: Color = preferenceTertiary(isBasicComponentInDarkTheme()),
    ): BasicComponentColors =
        remember(color, disabledColor) {
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
    /** The [Color] for the given enabled state.
     *
     * Public because `rmuix-preference` resolves it too; it was `internal` while only `rmuix-ui` used it.
     */
    @Stable
    fun color(enabled: Boolean): Color = if (enabled) color else disabledColor
}

/**
 * Fallback `TextView.getLineCount()` estimate for a text block height; the title/summary wrapper's
 * `TextLayoutResult` metrics take precedence when present.
 */
internal fun estimatedPreferenceLineCount(
    textBlockHeightPx: Float,
    lineUnitPx: Float,
): Int = if (lineUnitPx <= 0f) 1 else (textBlockHeightPx / lineUnitPx).roundToInt().coerceAtLeast(1)

/**
 * `COUICustomListSelectedLinearLayout.setIconTopInParent()`: the icon's top margin is the centre of the
 * first two rendered text lines minus half the icon height, clamped to
 * `coui_preference_50icon_margin_vertical_default` (8dp).
 */
internal fun preferenceMultilineIconTopMarginPx(
    centerHeightPx: Float,
    iconHeightPx: Float,
    textPaddingTopPx: Float,
    lineUnitPx: Float,
    minMarginPx: Float,
): Int {
    val firstTwoLinesPx = minOf(centerHeightPx, 2f * lineUnitPx)
    val centerOfFirstTwoLinesPx = textPaddingTopPx + firstTwoLinesPx / 2f
    return (centerOfFirstTwoLinesPx - iconHeightPx / 2f).roundToInt().coerceAtLeast(minMarginPx.roundToInt())
}

/**
 * `COUICustomListSelectedLinearLayout.setIconTopInParent()` with the real `TextView` line metrics:
 * `checkFirstTwoLineCenter()` walks title -> summary -> assignment, folds the first two rendered lines
 * (title line one + line two, or title line one + summary line one, ...) into `(lineTop(0), lineBottom(1))`,
 * and the icon's top margin becomes `max(centre - iconHeight / 2, 8dp)`.
 *
 * The caller starts the text column [textContentPaddingTopPx] below the row top (the stock
 * `PreferenceContent` padding) and folds each consecutive text view through [marginBetweenLinePx].
 */
internal fun preferenceMultilineIconTopMarginExactPx(
    recorder: PreferenceTextMetricsRecorder,
    textContentPaddingTopPx: Float,
    marginBetweenLinePx: Float,
    iconHeightPx: Float,
    minMarginPx: Float,
): Int {
    var remainingLines = 2
    var firstLineTopPx = textContentPaddingTopPx
    var secondLineBottomPx = textContentPaddingTopPx
    var viewTopPx = textContentPaddingTopPx

    fun visit(view: PreferenceTextMetrics?) {
        if (view == null || view.lineCount <= 0 || remainingLines == 0) return
        when (remainingLines) {
            2 -> {
                firstLineTopPx = viewTopPx
                if (view.lineCount < 2) {
                    secondLineBottomPx = viewTopPx + view.firstLineBottomPx
                    remainingLines = 1
                } else {
                    secondLineBottomPx = viewTopPx + view.secondLineBottomPx
                    remainingLines = 0
                }
            }

            1 -> {
                secondLineBottomPx = viewTopPx + view.firstLineBottomPx
                remainingLines = 0
            }
        }
        viewTopPx += view.heightPx + marginBetweenLinePx
    }

    visit(recorder.title)
    visit(recorder.summary)
    visit(recorder.assignment)
    val centerOfFirstTwoLinesPx = (firstLineTopPx + secondLineBottomPx) / 2f
    return (centerOfFirstTwoLinesPx - iconHeightPx / 2f).roundToInt().coerceAtLeast(minMarginPx.roundToInt())
}
