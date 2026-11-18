// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Close
import io.github.ra1nify.rmuix.kmp.theme.LocalTextStyles
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * Colours of a [TopTips].
 *
 * @param containerColor The card fill (`?attr/couiColorFillThin`).
 * @param titleColor The title (`?attr/couiColorLabelPrimary`).
 * @param contentColor The message (`?attr/couiColorLabelSecondary`).
 * @param actionColor The action text (`?attr/couiColorPrimaryText`).
 */
@Immutable
data class TopTipsColors(
    val containerColor: Color,
    val titleColor: Color,
    val contentColor: Color,
    val actionColor: Color,
)

/**
 * Defaults for [TopTips], taken from `coui_default_toptips.xml` and `COUIDefaultTopTipsView` /
 * `COUIDefaultTopTips` in the ColorOS settings dump.
 */
object TopTipsDefaults {
    /**
     * `?attr/couiRoundCornerFULL` = `coui_round_corner_full`, the radius stock uses when the tips fit on
     * one line (`getSingleLineRadius()` with smooth corners enabled).
     */
    val SingleLineRadius = 72.dp

    /** `COUIDefaultTopTips.createCornerAnimator(300, COUIEaseInterpolator)`: corner animation 300ms. */
    val RadiusAnimationMillis = 300

    /**
     * `coui_toptips_view_radius_os16`, the multi-line radius for OS16 (`getMultiLineRadius()`); newer OS
     * builds read `coui_toptips_view_radius` = 28dp instead.
     */
    val MultiLineRadius = 20.dp

    /**
     * `coui_toptips_view_padding`, the single-line content padding. The dimen is declared but never read by
     * code or layout in this dump - the stock container applies its own `getPadding*` - so the value is
     * cited as the tips library's intended inset rather than as a live resource.
     */
    val SingleLinePadding = 12.dp

    /** `coui_toptips_view_padding_multi_line`, the content padding once a title is present; dead like its single-line sibling. */
    val MultiLinePadding = 16.dp

    /** `coui_toptips_view_icon_btn_size`: both the leading image and the close button. */
    val IconSize = 24.dp

    /** `coui_toptips_view_icon_start_margin`. */
    val IconStartMargin = 16.dp

    /** `coui_toptips_view_title_icon_size`: the leading image when a title is present (36dp). */
    val TitleIconSize = 36.dp

    /** `coui_toptips_view_title_icon_text_gap`: 12dp between a title-case image and the title. */
    val TitleIconTextGap = 12.dp

    /** `coui_toptips_view_icon_btn_size`: the leading image when there is no title (24dp). */
    val ImageSize = 24.dp

    /** `coui_toptips_view_default_title_size` (`?style/couiTextHeadlineXS` is 16sp medium). */
    val TitleFontSize = 16.sp

    /** `coui_toptips_view_title_min_height`. */
    val TitleMinHeight = 22.dp

    /** `android:maxLines` of the title. */
    val TitleMaxLines = 2

    /** `coui_toptips_view_title_content_gap` - the layout keeps title and content flush. */
    val TitleContentGap = 0.dp

    /** `?style/couiTextBodyS` (13sp) - the message size. */
    val ContentFontSize = 13.sp

    /** `coui_toptips_view_content_min_height`. */
    val ContentMinHeight = 20.dp

    /** `coui_toptips_view_content_start_margin`: the gap between the leading image and the text. */
    val ContentStartMargin = 8.dp

    /** `coui_toptips_view_content_top_margin`, applied above and below the text block. */
    val ContentTopMargin = 12.dp

    /** `coui_toptips_view_default_text_size` - the inline action text size. */
    val ActionFontSize = 13.sp

    /** `coui_toptips_view_action_close_gap`: the gap between the action text and the close button. */
    val ActionCloseGap = 12.dp

    /** `coui_toptips_view_content_end_margin` / `coui_toptips_view_btn_margin`: the trailing inset. */
    val ContentEndMargin = 16.dp

    /**
     * `coui_top_tips_scroll_speed` = 25dp, read as `px / refresh rate` in `COUIMarqueeTextView.initSpeed()`
     * and therefore a velocity of 25dp per second.
     */
    val MarqueeVelocity = 25.dp

    /** `coui_top_tips_scroll_text_interval` = 80dp, the gap `generateTextDistance()` inserts between copies. */
    val MarqueeSpacing = 80.dp

    /**
     * `coui_top_tips_scroll_text_start_location` = 10dp: `COUIMarqueeTextView.initTextViewAttributes()`
     * initialises `mCurrentScrollLocation` with it, so the first copy starts 10dp in from the start edge.
     */
    val MarqueeStartLocation = 10.dp

    /** `coui_top_tips_fading_edge_size` = 26dp, the native fading-edge length on both sides. */
    val MarqueeFadeEdgeLength = 26.dp

    /** `COUIMarqueeTextView` starts rolling `1000ms` after construction (`postDelayed(..., 1000L)`). */
    val MarqueeInitialDelayMillis = 1000

    /**
     * The stock colours.
     *
     * `?attr/couiColorFillThin` = `coui_color_fill_thin` (`#0A000000` / `#14FFFFFF`),
     * `?attr/couiColorLabelPrimary` (`#E6000000` / `#E6FFFFFF`),
     * `?attr/couiColorLabelSecondary` (`#8A000000` / `#8AFFFFFF`) and `?attr/couiColorPrimaryText`, which
     * `Theme.COUI` maps to `coui_color_primary_text_blue` (`#0080FF` / `#5C9DFF`).
     */
    @Composable
    fun topTipsColors(
        containerColor: Color = topTipsContainerColor(isTopTipsDarkTheme()),
        titleColor: Color = topTipsTitleColor(isTopTipsDarkTheme()),
        contentColor: Color = topTipsContentColor(isTopTipsDarkTheme()),
        actionColor: Color = topTipsActionColor(isTopTipsDarkTheme()),
    ): TopTipsColors =
        remember(containerColor, titleColor, contentColor, actionColor) {
            TopTipsColors(
                containerColor = containerColor,
                titleColor = titleColor,
                contentColor = contentColor,
                actionColor = actionColor,
            )
        }
}

@Composable
private fun isTopTipsDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() <
        RmuixTheme.colorScheme.onBackground.luminance()

/** `?attr/couiColorFillThin` = `coui_color_fill_thin` / `coui_color_fill_thin_dark`. */
internal fun topTipsContainerColor(dark: Boolean): Color = if (dark) Color(0x14FFFFFF) else Color(0x0A000000)

/** `?attr/couiColorLabelPrimary`. */
internal fun topTipsTitleColor(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/** `?attr/couiColorLabelSecondary`. */
internal fun topTipsContentColor(dark: Boolean): Color = if (dark) Color(0x8AFFFFFF) else Color(0x8A000000)

/** `?attr/couiColorPrimaryText` -> `coui_color_primary_text_blue`. */
internal fun topTipsActionColor(dark: Boolean): Color = if (dark) Color(0xFF5C9DFF) else Color(0xFF0080FF)

/** `COUIDefaultTopTips.createCornerAnimator(300, COUIEaseInterpolator)`: 300ms COUIEase. */
private val TopTipsCornerEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/**
 * `COUIDefaultTopTips.updateRadius(hasTitle, isSingleLineText)`: a title forces the card shape,
 * otherwise the measured message line count decides between the single-line pill and the card. The
 * same state selects `coui_toptips_view_padding` / `coui_toptips_view_padding_multi_line`.
 */
internal data class TopTipsLayoutSpec(
    val multiLine: Boolean,
    val radius: Dp,
    val padding: Dp,
)

internal fun topTipsLayoutSpec(
    withTitle: Boolean,
    lineCount: Int,
): TopTipsLayoutSpec {
    val multiLine = withTitle || lineCount > 1
    return TopTipsLayoutSpec(
        multiLine = multiLine,
        radius = if (multiLine) TopTipsDefaults.MultiLineRadius else TopTipsDefaults.SingleLineRadius,
        padding = if (multiLine) TopTipsDefaults.MultiLinePadding else TopTipsDefaults.SingleLinePadding,
    )
}

/**
 * The ColorOS top tips card: `COUIDefaultTopTipsView` (`coui_default_toptips.xml`).
 *
 * A floating hint bar above the content: an optional leading image, an optional two-line title, the
 * message, an optional inline action and an optional close button. With a title the card is a 20dp
 * rounded card with 16dp padding; without one it is a single-line pill (`couiRoundCornerFULL` = 72dp)
 * with 12dp padding - the two shapes stock animates between in `updateRadius(hasTitle(),
 * isSingleLineText())`.
 *
 * @param content The message (`?style/couiTextBodyS`, 13sp, `?attr/couiColorLabelSecondary`).
 * @param modifier The modifier to be applied to the card.
 * @param title The optional title (`?attr/couiTextHeadlineXS`, 16sp medium, at most two lines).
 * @param icon The optional leading image: 24dp without a title (`coui_toptips_view_icon_btn_size`),
 *   36dp and 12dp before the title with one (`coui_toptips_view_title_icon_size` / `_title_icon_text_gap`).
 * @param actionText The optional inline action (`?attr/couiColorPrimaryText`, 13sp).
 * @param onActionClick Called when [actionText] is tapped.
 * @param onClose When non-null, a close button is drawn; called when it is tapped.
 * @param closeContentDescription The close button's content description.
 * @param colors The card's colours.
 * @param marquee Whether the message rolls like `COUIMarqueeTopTips` when it overflows a single line:
 *   the content collapses to one line and scrolls at 25dp/s after a 1000ms delay, with 26dp fading
 *   edges. Off by default, which keeps the stock multi-line / ellipsised message of
 *   `COUIDefaultTopTipsView`. Off the stock (`COUIMarqueeTopTips`) path, `startRoll()` is not exposed -
 *   passing `true` starts the roll immediately.
 */
@Composable
fun TopTips(
    content: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    closeContentDescription: String = "Close",
    colors: TopTipsColors = TopTipsDefaults.topTipsColors(),
    marquee: Boolean = false,
) {
    val withTitle = title != null
    var lineCount by remember(content) { mutableIntStateOf(1) }
    val layout = topTipsLayoutSpec(withTitle = withTitle, lineCount = lineCount)
    // The stock `createCornerAnimator()` animates the corner over 300ms with COUIEase when the
    // single-line <-> multi-line layout changes; the padding is swapped with the layout.
    val radius by
        animateDpAsState(
            targetValue = layout.radius,
            animationSpec = tween(durationMillis = TopTipsDefaults.RadiusAnimationMillis, easing = TopTipsCornerEasing),
            label = "TopTipsRadius",
        )
    val verticalPadding = layout.padding

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radius))
                .background(colors.containerColor)
                .padding(
                    start = TopTipsDefaults.IconStartMargin,
                    top = verticalPadding,
                    end = TopTipsDefaults.ContentEndMargin,
                    bottom = verticalPadding,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            // `COUIDefaultTopTipsView.createConstraintSet()`: without a title the image is
            // `coui_toptips_view_icon_btn_size` (24dp) and the text starts 8dp after it; with a title it
            // becomes `coui_toptips_view_title_icon_size` (36dp) with a 12dp title gap.
            val iconSize = if (withTitle) TopTipsDefaults.TitleIconSize else TopTipsDefaults.ImageSize
            val iconTextGap = if (withTitle) TopTipsDefaults.TitleIconTextGap else TopTipsDefaults.ContentStartMargin
            Box(
                modifier = Modifier.padding(end = iconTextGap).size(iconSize),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
        }
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .heightIn(min = TopTipsDefaults.ContentMinHeight),
        ) {
            if (title != null) {
                Text(
                    modifier =
                        Modifier
                            .defaultMinSize(minHeight = TopTipsDefaults.TitleMinHeight)
                            .padding(bottom = TopTipsDefaults.TitleContentGap),
                    text = title,
                    fontSize = TopTipsDefaults.TitleFontSize,
                    fontWeight = FontWeight.Medium,
                    color = colors.titleColor,
                    maxLines = TopTipsDefaults.TitleMaxLines,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (marquee) {
                TopTipsMarqueeContent(content = content, color = colors.contentColor)
            } else {
                Text(
                    text = content,
                    fontSize = TopTipsDefaults.ContentFontSize,
                    color = colors.contentColor,
                    // `coui_default_toptips.xml` gives the content TextView no `maxLines`: it wraps
                    // freely, and `isSingleLineText()` counts the measured lines to pick the shape.
                    maxLines = Int.MAX_VALUE,
                    overflow = TextOverflow.Clip,
                    onTextLayout = { lineCount = it.lineCount },
                )
            }
        }
        if (actionText != null) {
            Text(
                modifier =
                    Modifier
                        .padding(start = TopTipsDefaults.ActionCloseGap)
                        .clickable(enabled = onActionClick != null, onClick = { onActionClick?.invoke() }),
                text = actionText,
                fontSize = TopTipsDefaults.ActionFontSize,
                color = colors.actionColor,
                maxLines = 1,
            )
        }
        if (onClose != null) {
            Icon(
                imageVector = RmuixIcons.Basic.Close,
                contentDescription = closeContentDescription,
                tint = colors.contentColor,
                modifier =
                    Modifier
                        .padding(start = TopTipsDefaults.ActionCloseGap)
                        .size(TopTipsDefaults.IconSize)
                        .clickable(onClick = onClose),
            )
        }
    }
}

/**
 * The rolling message of `COUIMarqueeTextView` (and therefore `COUIMarqueeTopTips`).
 *
 * Stock forces a single line while rolling, waits `1000ms` (`DEFAULT_SCROLL_DELAY_DURATION`), then scrolls
 * leftwards at `coui_top_tips_scroll_speed` / refresh rate = 25dp/s, repeating the text with
 * `coui_top_tips_scroll_text_interval` = 80dp between copies. The left and right fading edges are
 * `coui_top_tips_fading_edge_size` = 26dp and only appear while the text actually overflows.
 */
@Composable
private fun TopTipsMarqueeContent(
    content: String,
    color: Color,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().heightIn(min = TopTipsDefaults.ContentMinHeight),
    ) {
        val measurer = rememberTextMeasurer()
        val baseStyle = LocalTextStyles.current.main
        val textStyle = remember(baseStyle) { baseStyle.copy(fontSize = TopTipsDefaults.ContentFontSize) }
        val maxWidthPx = constraints.maxWidth
        val overflows =
            remember(content, maxWidthPx, textStyle) {
                maxWidthPx > 0 &&
                    measurer
                        .measure(
                            text = content,
                            style = textStyle,
                            maxLines = 1,
                            softWrap = false,
                            constraints = Constraints(),
                        ).size.width > maxWidthPx
            }
        Text(
            modifier =
                Modifier.then(
                    if (!overflows) {
                        Modifier
                    } else {
                        // `mCurrentScrollLocation` starts at `coui_top_tips_scroll_text_start_location`
                        // (10dp) and the stock `onDraw()` translates the layout by it; this padding
                        // reproduces the initial inset (mirrored for RTL layouts).
                        val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
                        Modifier
                            .padding(start = if (rtl) 0.dp else TopTipsDefaults.MarqueeStartLocation)
                            .padding(end = if (rtl) TopTipsDefaults.MarqueeStartLocation else 0.dp)
                            .basicMarquee(
                                iterations = Int.MAX_VALUE,
                                animationMode = MarqueeAnimationMode.Immediately,
                                repeatDelayMillis = 0,
                                initialDelayMillis = TopTipsDefaults.MarqueeInitialDelayMillis,
                                spacing = MarqueeSpacing(TopTipsDefaults.MarqueeSpacing),
                                velocity = TopTipsDefaults.MarqueeVelocity,
                            ).marqueeFadeEdges(TopTipsDefaults.MarqueeFadeEdgeLength)
                    },
                ),
            text = content,
            fontSize = TopTipsDefaults.ContentFontSize,
            color = color,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
        )
    }
}

/**
 * Fades both horizontal edges of the rolling text: `COUIMarqueeTextView.getLeftFadingEdgeStrength()` /
 * `getRightFadingEdgeStrength()` return `1.0` while rolling and stock's length is
 * `coui_top_tips_fading_edge_size` = 26dp.
 */
private fun Modifier.marqueeFadeEdges(edgeLength: Dp): Modifier =
    this
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        .drawWithContent {
            drawContent()
            val edgePx = edgeLength.toPx().coerceAtMost(size.width / 2f)
            if (edgePx <= 0f || size.width <= 0f) return@drawWithContent
            val stop = (edgePx / size.width).coerceIn(0f, 0.5f)
            drawRect(
                brush =
                    Brush.horizontalGradient(
                        0f to Color.Transparent,
                        stop to Color.Black,
                        (1f - stop) to Color.Black,
                        1f to Color.Transparent,
                    ),
                blendMode = BlendMode.DstIn,
            )
        }
