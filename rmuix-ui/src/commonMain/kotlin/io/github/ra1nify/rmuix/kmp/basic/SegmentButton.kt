// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.anim.couiSpring
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/** Colours of a [SegmentButton]. */
@Immutable
data class SegmentButtonColors(
    val containerColor: Color,
    val indicatorColor: Color,
    val selectedContentColor: Color,
    val unselectedContentColor: Color,
    val indicatorShadowColor: Color,
    /** `coui_segment_button_shadow_end_color`: the outer stop of the light radial gradient. */
    val indicatorShadowEndColor: Color = Color(0x03000000),
)

/**
 * Defaults for [SegmentButton], read from `COUISegmentButtonLayout`, its `SegmentButton` style and
 * the `coui_segment_*` / `coui_*_segment_button_*` resources in the ColorOS settings dump.
 */
object SegmentButtonDefaults {
    /** `SegmentButton` style `android:layout_height`. */
    val Height = 40.dp

    /** `SegmentButton.Tiny` style `android:layout_height`. */
    val TinyHeight = 32.dp

    /** `coui_segment_padding`: the track's inset around the indicator. */
    val Padding = 4.dp

    /** `coui_segment_padding_tiny`. */
    val TinyPadding = 2.dp

    /** `coui_segment_min_width`: the narrowest a single segment gets. */
    val MinSegmentWidth = 52.dp

    /** `coui_segment_btn_padding_horizontal`: text inset inside a segment. */
    val SegmentHorizontalPadding = 12.dp

    /** `COUISegmentButtonLayout.WIDTH_SPRING_RESPONSE`: 0.6s response for the indicator width. */
    val WidthSpringResponse = 0.6f

    /** `COUISegmentButtonLayout.WIDTH_SPRING_BOUNCE`: 0.5 base bounce for the indicator width. */
    val WidthSpringBounce = 0.5f

    /** `COUISegmentButtonLayout.WIDTH_SPRING_VELOCITY`: 1000px/s initial width velocity. */
    val WidthSpringVelocity = 1000f

    /** `coui_segment_regular_extra_width`: the extra width of a regular short-to-long change. */
    val RegularExtraWidth = 7.dp

    /** `coui_segment_small_extra_width`: the extra width with the tiny (2dp) padding. */
    val SmallExtraWidth = 5.dp

    /** `coui_segment_switch_distance`: the travel used by the extra-width animator. */
    val SwitchDistance = 5.dp

    /** `POSITION_SPRING_RESPONSE`: 0.4s response of the indicator position spring. */
    val PositionSpringResponse = 0.4f

    /** `POSITION_SPRING_BOUNCE`: 0.1 base bounce of the indicator position spring. */
    val PositionSpringBounce = 0.1f

    /** `HEIGHT_SPRING_RESPONSE`: 0.6s response of the indicator height spring. */
    val HeightSpringResponse = 0.6f

    /** `HEIGHT_SPRING_BOUNCE`: 0.5 bounce of the indicator height spring. */
    val HeightSpringBounce = 0.5f

    /** `HEIGHT_SPRING_VELOCITY`: -500px/s initial height velocity (the stock dip). */
    val HeightSpringVelocity = -500f

    /** `HEIGHT_SPRING_MIN_SCALE`: the stock clamps the height spring at `0.9 * height`. */
    val IndicatorMinHeightFraction = 0.9f

    /** `couiTextButtonM` text size. */
    val TextSize = 14.sp

    /**
     * `coui_segment_shadow_offset`: the gradient shadow rect starts 2dp below the indicator top.
     *
     * `COUISegmentButtonLayout.drawShadowWithGradient()` sets `shadowRect.top = indicator.top + offset`,
     * keeps the indicator left/right edges and extends to the container bottom.
     */
    val IndicatorShadowOffset = 2.dp

    /** `coui_segment_button_shadow_end_color` (#03000000): the radial gradient's outer stop. */
    val IndicatorShadowEndColor = Color(0x03000000)

    /**
     * Both text colours default to `?attr/couiColorLabelPrimary` in the stock - the selected segment is
     * distinguished by the indicator block, not by its text colour.
     *
     * The indicator is a near-white block in the light theme (`#B2FFFFFF`) over a 6% black track
     * (`#0F000000`), and the reverse in the dark theme (`#33FFFFFF` over `#1FFFFFFF`); that is the
     * resource value, not an inversion mistake.
     */
    @Composable
    fun colors(): SegmentButtonColors {
        val dark = segmentButtonIsDarkTheme()
        return SegmentButtonColors(
            containerColor = if (dark) Color(0x1FFFFFFF) else Color(0x0F000000),
            indicatorColor = if (dark) Color(0x33FFFFFF) else Color(0xB2FFFFFF),
            selectedContentColor = RmuixTheme.colorScheme.onSurface,
            unselectedContentColor = RmuixTheme.colorScheme.onSurface,
            // `coui_segment_button_shadow_color`; the dark theme only supplies #14000000 and draws it
            // solid instead of a gradient (`drawShadowWithGradient()` takes the solid branch at night).
            indicatorShadowColor = if (dark) Color(0x14000000) else Color(0x0D000000),
            indicatorShadowEndColor = IndicatorShadowEndColor,
        )
    }
}

/** Resolves the neutral palette from the library theme, matching [SegmentButtonDefaults.colors]. */
@Composable
internal fun segmentButtonIsDarkTheme(): Boolean = RmuixTheme.colorScheme.onBackground.luminance() > 0.5f

/**
 * `COUISegmentButtonLayout.calculateDynamicWidthParam(current, target, start, end, power)`:
 * `start - (start - end) * (|current - target| / max(current, target))^power`, clamped to `0..1`.
 */
internal fun segmentDynamicSpringParam(
    currentPx: Float,
    targetPx: Float,
    start: Float,
    end: Float,
    power: Float,
): Float {
    if (currentPx <= 0f || targetPx <= 0f) return start
    val ratio = abs(currentPx - targetPx) / max(currentPx, targetPx)
    return (start - (start - end) * ratio.pow(power)).coerceIn(0f, 1f)
}

/** The indicator width spring pair for one selection change (`animateIndicatorToPosition()`). */
internal data class SegmentWidthSpring(
    val bounce: Float,
    val response: Float,
)

/** `animateIndicatorToPosition()`: short-to-long uses the fixed 0.5 bounce, long-to-short the dynamic one. */
internal fun segmentWidthSpring(
    currentWidthPx: Float,
    targetWidthPx: Float,
): SegmentWidthSpring {
    val shortToLong = targetWidthPx > currentWidthPx
    val bounce =
        if (shortToLong) {
            SegmentButtonDefaults.WidthSpringBounce
        } else {
            segmentDynamicSpringParam(currentWidthPx, targetWidthPx, start = 0.5f, end = 0.1f, power = 2f)
        }
    val response =
        segmentDynamicSpringParam(currentWidthPx, targetWidthPx, start = 0.6f, end = 0.4f, power = 2f)
    return SegmentWidthSpring(bounce = bounce, response = response)
}

/**
 * `getEffectiveTranslationBounceForShortToLong()`: the position spring bounce is the base 0.1, except a tiny
 * (2dp padding) short-to-long switch uses the dynamic `0.1..0.03` value.
 */
internal fun segmentPositionSpringBounce(
    currentWidthPx: Float,
    targetWidthPx: Float,
    tiny: Boolean,
): Float =
    if (tiny && targetWidthPx > currentWidthPx) {
        segmentDynamicSpringParam(currentWidthPx, targetWidthPx, start = 0.1f, end = 0.03f, power = 1f)
    } else {
        SegmentButtonDefaults.PositionSpringBounce
    }

/** `getEffectiveMaxExtraWidthForShortToLong()`: 7dp regular padding, 5dp tiny padding. */
internal fun segmentShortToLongExtraWidth(tiny: Boolean): Dp =
    if (tiny) SegmentButtonDefaults.SmallExtraWidth else SegmentButtonDefaults.RegularExtraWidth

/**
 * The ColorOS segmented control (`COUISegmentButtonLayout`): a capsule track holding content-sized
 * segments, with a sliding indicator behind the selected one.
 *
 * Geometry comes from `getDrawableRadius()`, which returns `(bottom - top) / 2` for both the track and
 * the indicator - so both are capsules, the track's radius is half of [height] and the indicator's is
 * half of its own (`height - 2 * padding`). Segment widths follow `COUISegmentButtonLayout.onMeasure()`
 * and `calculateChildrenWidths()`: natural text width plus the 12dp padding (min
 * `coui_segment_min_width` 52dp), then the remaining space shared equally by the labels that exceed the
 * running average. The indicator slides on the stock position spring (`response` 0.4 / `bounce` 0.1) and
 * its width follows `WIDTH_SPRING_RESPONSE` 0.6 / `WIDTH_SPRING_BOUNCE` 0.5.
 *
 * `drawIndicator(canvas, TRUE, ...)` always paints the stock gradient shadow before the block: a rect
 * that starts 2dp below the indicator top, keeps its left/right edges and reaches the container bottom,
 * with the indicator itself cut out; light uses a radial gradient `#0D000000 -> #03000000` centred on the
 * indicator (radius = indicator width / 2), dark uses the solid `#14000000`. This port draws the same
 * shadow (the bottom edge follows the padded content, 4dp above the container's very bottom).
 *
 * The control is capped at `getResponsiveWidth()`: 4 layout-grid columns on a compact window, 6 on a
 * medium / expanded-portrait window and 8 on an expanded landscape window (the stock 4/6/8 columns of
 * `LayoutGridSystem`, gutter 8/12dp and `layout_grid_margin_*_small` 16/24/40dp). A zero / unknown
 * `LocalWindowInfo.containerSize` leaves the control at `min(available, window)`.
 *
 * Not reproduced: the caustic shadow edge (`mDrawMaterialEffect` / `OplusMaterialEffect`) and the
 * vibration on switch.
 *
 * @param options The segment labels, in order.
 * @param selectedIndex The selected segment; clamped into range.
 * @param onSelect Called with the index of the segment that was tapped.
 * @param modifier The modifier to be applied to the control.
 * @param enabled Whether segments can be selected.
 * @param tiny Whether to use the 32dp `SegmentButton.Tiny` metrics instead of the 40dp ones.
 * @param height Overrides the track height (the dump's call sites set their own).
 * @param colors Track, indicator and text colours.
 */
@Composable
fun SegmentButton(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tiny: Boolean = false,
    height: Dp = if (tiny) SegmentButtonDefaults.TinyHeight else SegmentButtonDefaults.Height,
    colors: SegmentButtonColors = SegmentButtonDefaults.colors(),
) {
    if (options.isEmpty()) return
    val selected = selectedIndex.coerceIn(0, options.lastIndex)
    val trackPadding = if (tiny) SegmentButtonDefaults.TinyPadding else SegmentButtonDefaults.Padding
    // `coui_segment_switch_distance` (5dp) is the travel of a short-to-long width change, which this
    // equal-width port does not have; the whole indicator moves instead.
    // `COUISegmentButtonLayout.onMeasure()` caps the control at `getResponsiveWidth()`: the width of
    // 4 / 6 / 8 centred layout-grid columns for the current window size class. `widthIn` applies the
    // same min(available, grid width); a zero / unknown window size leaves the control unconstrained.
    val density = LocalDensity.current
    val windowDpSize = LocalWindowInfo.current.containerDpSize
    val responsiveMaxWidth =
        remember(windowDpSize) {
            if (windowDpSize == DpSize.Unspecified || windowDpSize.width.value <= 0f || windowDpSize.height.value <= 0f) {
                Dp.Unspecified
            } else {
                segmentResponsiveWidth(
                    windowWidthDp = windowDpSize.width.value,
                    windowHeightDp = windowDpSize.height.value,
                )
            }
        }
    BoxWithConstraints(
        modifier =
            modifier
                .then(
                    if (responsiveMaxWidth == Dp.Unspecified) {
                        Modifier
                    } else {
                        // The stock layout reports `min(responsiveWidth, requested)` even when the
                        // parent passes an exact width (`setMeasuredDimension(size, ...)`), so the cap
                        // overrides the incoming minimum instead of clamping it with `widthIn`.
                        val capPx = with(density) { responsiveMaxWidth.roundToPx() }
                        Modifier.layout { measurable, constraints ->
                            val cappedWidth = minOf(constraints.maxWidth, capPx)
                            val placeable =
                                measurable.measure(
                                    constraints.copy(minWidth = 0, maxWidth = cappedWidth),
                                )
                            layout(placeable.width, placeable.height) { placeable.place(0, 0) }
                        }
                    },
                ).height(height)
                .clip(CircleShape)
                .background(colors.containerColor)
                .padding(trackPadding),
    ) {
        // `COUISegmentButtonLayout.onMeasure()` gives each segment its natural width
        // (text + 2 * coui_segment_btn_padding_horizontal, at least coui_segment_min_width) and then
        // `calculateChildrenWidths()` distributes the remaining space: every label shorter than the
        // running average keeps its natural width, the rest share the remainder equally.
        val textMeasurer = rememberTextMeasurer()
        val labelStyle =
            remember {
                TextStyle(fontSize = SegmentButtonDefaults.TextSize, fontWeight = FontWeight.Medium)
            }
        val segmentWidths =
            remember(options, maxWidth, labelStyle, textMeasurer, density) {
                val paddingPx = with(density) { SegmentButtonDefaults.SegmentHorizontalPadding.toPx() }
                val minWidthPx = with(density) { SegmentButtonDefaults.MinSegmentWidth.toPx() }
                val naturalWidths =
                    options.map { label ->
                        val textWidth =
                            textMeasurer
                                .measure(
                                    text = label,
                                    style = labelStyle,
                                    maxLines = 1,
                                    softWrap = false,
                                    constraints = Constraints(),
                                ).size.width
                                .toFloat()
                        max(textWidth + 2 * paddingPx, minWidthPx)
                    }
                distributeSegmentWidthsPx(naturalWidths, with(density) { maxWidth.toPx() })
                    .map { with(density) { it.toDp() } }
            }
        val segmentOffsets =
            remember(segmentWidths) {
                segmentWidths.runningFold(0.dp) { acc, width -> acc + width }.dropLast(1)
            }
        val segmentWidthsPx = remember(segmentWidths, density) { segmentWidths.map { with(density) { it.toPx() } } }
        val segmentOffsetsPx = remember(segmentOffsets, density) { segmentOffsets.map { with(density) { it.toPx() } } }
        val indicatorHeightPx = with(density) { (height - trackPadding * 2).toPx() }
        val widthAnimation = remember { Animatable(segmentWidthsPx[selected]) }
        val offsetAnimation = remember { Animatable(segmentOffsetsPx[selected]) }
        val heightAnimation = remember { Animatable(indicatorHeightPx) }
        // The stock spring has a max value of `target + coui_segment_(regular|small)_extra_width` for a
        // short-to-long change; Compose springs cannot cap their overshoot, so the drawn width is clamped.
        var widthCapPx by remember { mutableFloatStateOf(Float.MAX_VALUE) }
        var animationInitialised by remember { mutableStateOf(false) }
        val latestWidthsPx by rememberUpdatedState(segmentWidthsPx)
        val latestOffsetsPx by rememberUpdatedState(segmentOffsetsPx)
        LaunchedEffect(selected, segmentWidths) {
            val targetWidthPx = latestWidthsPx[selected]
            val targetOffsetPx = latestOffsetsPx[selected]
            if (!animationInitialised) {
                animationInitialised = true
                widthAnimation.snapTo(targetWidthPx)
                offsetAnimation.snapTo(targetOffsetPx)
                heightAnimation.snapTo(indicatorHeightPx)
                return@LaunchedEffect
            }
            val spring = segmentWidthSpring(widthAnimation.value, targetWidthPx)
            widthCapPx =
                if (targetWidthPx > widthAnimation.value) {
                    targetWidthPx + with(density) { segmentShortToLongExtraWidth(tiny).toPx() }
                } else {
                    Float.MAX_VALUE
                }
            coroutineScope {
                launch {
                    widthAnimation.animateTo(
                        targetValue = targetWidthPx,
                        animationSpec = couiSpring(response = spring.response, bounce = spring.bounce),
                        initialVelocity = SegmentButtonDefaults.WidthSpringVelocity,
                    )
                }
                launch {
                    offsetAnimation.animateTo(
                        targetValue = targetOffsetPx,
                        animationSpec =
                            couiSpring(
                                response = SegmentButtonDefaults.PositionSpringResponse,
                                bounce =
                                    segmentPositionSpringBounce(
                                        currentWidthPx = widthAnimation.value,
                                        targetWidthPx = targetWidthPx,
                                        tiny = tiny,
                                    ),
                            ),
                    )
                }
                launch {
                    heightAnimation.animateTo(
                        targetValue = indicatorHeightPx,
                        animationSpec =
                            couiSpring(
                                response = SegmentButtonDefaults.HeightSpringResponse,
                                bounce = SegmentButtonDefaults.HeightSpringBounce,
                            ),
                        initialVelocity = SegmentButtonDefaults.HeightSpringVelocity,
                    )
                }
            }
        }
        val indicatorWidth = with(density) { min(widthAnimation.value, widthCapPx).toDp() }
        val indicatorOffset = with(density) { offsetAnimation.value.toDp() }
        // `mSpringAnimationHeight` has a 0.9 * height minimum; the simulation may dip further, so the
        // drawn height is clamped like the stock `setMinValue(0.9f * height)`.
        val indicatorHeight =
            with(density) {
                max(
                    heightAnimation.value,
                    indicatorHeightPx * SegmentButtonDefaults.IndicatorMinHeightFraction,
                ).toDp()
            }
        val haptics = LocalHapticFeedback.current
        val dark = segmentButtonIsDarkTheme()
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .drawBehind {
                        drawSegmentIndicatorShadow(
                            leftPx = indicatorOffset.toPx(),
                            widthPx = indicatorWidth.toPx(),
                            dark = dark,
                            shadowColor = colors.indicatorShadowColor,
                            shadowEndColor = colors.indicatorShadowEndColor,
                        )
                    },
        )
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = indicatorOffset)
                    .width(indicatorWidth)
                    .height(indicatorHeight)
                    .clip(CircleShape)
                    .background(colors.indicatorColor),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .selectableGroup()
                    // `COUISegmentButtonLayout.dispatchTouchEvent()`: dragging across segments selects them as
                    // the finger enters them (with `performHapticFeedback(302)`), and lifting without a drag
                    // selects the touched segment. Taps are handled here too so the whole row shares one
                    // gesture detector; the per-segment semantics keep it accessible.
                    .pointerInput(enabled, segmentWidthsPx) {
                        if (!enabled) return@pointerInput
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            var lastNotified: Int? = null
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                if (!change.pressed) {
                                    change.consume()
                                    val index = segmentIndexAt(change.position.x, segmentWidthsPx)
                                    if (index != null && index != selected && index != lastNotified) {
                                        onSelect(index)
                                    }
                                    break
                                }
                                change.consume()
                                val index = segmentIndexAt(change.position.x, segmentWidthsPx)
                                if (index != null && index != selected && index != lastNotified) {
                                    onSelect(index)
                                    lastNotified = index
                                    haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                                }
                            }
                        }
                    },
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            options.forEachIndexed { index, label ->
                val isSelected = index == selected
                Box(
                    modifier =
                        Modifier
                            .width(segmentWidths[index])
                            .fillMaxHeight()
                            .semantics {
                                this.role = Role.RadioButton
                                this.selected = isSelected
                                if (enabled) {
                                    onClick {
                                        onSelect(index)
                                        true
                                    }
                                } else {
                                    disabled()
                                }
                            },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        // The inset lives on the text, so the whole segment stays clickable.
                        modifier = Modifier.padding(horizontal = SegmentButtonDefaults.SegmentHorizontalPadding),
                        color = if (isSelected) colors.selectedContentColor else colors.unselectedContentColor,
                        fontSize = SegmentButtonDefaults.TextSize,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

/**
 * The segment under [xPx] in the row's content coordinates, or `null` outside the segments.
 *
 * `COUISegmentButtonLayout.dispatchTouchEvent()` resolves the touch position through the child views
 * (`mTouchPosition`); the port resolves it from the measured segment widths.
 */
internal fun segmentIndexAt(
    xPx: Float,
    segmentWidthsPx: List<Float>,
): Int? {
    var start = 0f
    for (index in segmentWidthsPx.indices) {
        val end = start + segmentWidthsPx[index]
        if (xPx >= start && xPx < end) return index
        start = end
    }
    return null
}

/**
 * `COUISegmentButtonLayout.getResponsiveWidth()`: the width spanned by the centred 4 / 6 / 8 columns of
 * the `LayoutGridSystem` on the window's total size class.
 *
 * - compact width (< 600dp): 4 columns, `layout_grid_margin_compat_small` 16dp, `layout_grid_gutter` 8dp;
 * - medium width (600..840dp) or an expanded window that is not plain `Expanded` (portrait / land
 *   portrait): 8-column grid, `layout_grid_margin_medium_small` 24dp, gutter 8dp, span 6 columns;
 * - plain `Expanded` (width >= 840dp and height 480..900dp): 12-column grid,
 *   `layout_grid_margin_expanded_small` 40dp, `layout_grid_gutter_expanded` 12dp, span 8 columns.
 *
 * Column width = `(window - 2 * margin - (columns - 1) * gutter) / columns`; the span adds the gutters.
 */
internal fun segmentResponsiveWidth(
    windowWidthDp: Float,
    windowHeightDp: Float,
): Dp {
    val widthClass =
        when {
            windowWidthDp < 600f -> 0

            // Compact
            windowWidthDp < 840f -> 1

            // Medium
            else -> 2 // Expanded width
        }
    val plainExpanded = widthClass == 2 && windowHeightDp in 480f..900f
    val columns: Int
    val marginDp: Float
    val gutterDp: Float
    val spanColumns: Int
    when {
        widthClass == 0 -> {
            columns = 4
            marginDp = 16f
            gutterDp = 8f
            spanColumns = 4
        }

        plainExpanded -> {
            columns = 12
            marginDp = 40f
            gutterDp = 12f
            spanColumns = 8
        }

        else -> {
            columns = 8
            marginDp = 24f
            gutterDp = 8f
            spanColumns = 6
        }
    }
    val columnWidthDp = (windowWidthDp - 2 * marginDp - (columns - 1) * gutterDp) / columns
    return (spanColumns * columnWidthDp + (spanColumns - 1) * gutterDp).coerceAtLeast(0f).dp
}

/**
 * `COUISegmentButtonLayout.calculateChildrenWidths()`: every label shorter than the running average
 * keeps its natural width, the remaining space is then split equally; when every label fits below the
 * initial average they all become equal.
 */
internal fun distributeSegmentWidthsPx(
    naturalWidthsPx: List<Float>,
    totalPx: Float,
): List<Float> {
    val size = naturalWidthsPx.size
    if (size == 0) return emptyList()
    if (totalPx <= 0f) return naturalWidthsPx
    val average = totalPx / size
    val sorted = naturalWidthsPx.indices.sortedBy { naturalWidthsPx[it] }
    val widths = FloatArray(size)
    var remaining = totalPx
    var current = average
    var lastSplitIndex = -1
    var i = 0
    while (i < size) {
        val natural = naturalWidthsPx[sorted[i]]
        if (natural < current) {
            widths[sorted[i]] = natural
            remaining -= natural
            i++
        } else {
            if (lastSplitIndex == i) {
                while (i < size) {
                    widths[sorted[i]] = current
                    i++
                }
                remaining = 0f
                break
            }
            current = remaining / (size - i)
            lastSplitIndex = i
        }
    }
    if (lastSplitIndex == -1) {
        return List(size) { average }
    }
    if (remaining > 0f && size > 1) {
        for (k in 0 until size - 1) {
            widths[sorted[k]] += remaining / (size - 1)
        }
    }
    return widths.map { it.roundToInt().toFloat() }
}

/**
 * `COUISegmentButtonLayout.drawShadowWithGradient()`: the indicator rect is cut out of a shadow rect that
 * starts [SegmentButtonDefaults.IndicatorShadowOffset] below the indicator top and runs to the container
 * bottom. Light theme paints a radial gradient from `shadowColor` to `shadowEndColor` centred on the
 * indicator, with radius = indicator width / 2; dark theme paints [shadowColor] solid.
 */
private fun DrawScope.drawSegmentIndicatorShadow(
    leftPx: Float,
    widthPx: Float,
    dark: Boolean,
    shadowColor: Color,
    shadowEndColor: Color,
) {
    if (widthPx <= 0f || size.height <= 0f) return
    val indicatorRect = Rect(leftPx, 0f, leftPx + widthPx, size.height)
    val radius = CornerRadius(size.height / 2f)
    val shadowTop = SegmentButtonDefaults.IndicatorShadowOffset.toPx()
    if (shadowTop >= size.height) return
    val shadowRect = Rect(leftPx, shadowTop, leftPx + widthPx, size.height)
    val indicatorPath = Path().apply { addRoundRect(RoundRect(indicatorRect, radius)) }
    val shadowPath =
        Path().apply {
            addRoundRect(RoundRect(shadowRect, radius))
            op(this, indicatorPath, PathOperation.Difference)
        }
    if (dark) {
        drawPath(shadowPath, shadowColor)
    } else {
        drawPath(
            shadowPath,
            Brush.radialGradient(
                colorStops = arrayOf(0f to shadowColor, 1f to shadowEndColor),
                center = Offset(indicatorRect.center.x, indicatorRect.center.y),
                radius = shadowRect.width / 2f,
            ),
        )
    }
}
