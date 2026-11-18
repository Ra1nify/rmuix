// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.hairlinePx
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.abs

/**
 * Whether [NumberPicker]s draw their own selection hairlines.
 *
 * [NumberPickerGroup] provides `false` and draws one pair across the whole row instead, matching the
 * stock date/time pickers (`coui:couiIsDrawBackground="false"` on each column).
 */
internal val LocalNumberPickerShowSelectionLines = compositionLocalOf { true }

/**
 * A [NumberPicker] component with Rmuix style.
 *
 * A vertical scroll picker that displays a range of numbers with the selected value centered.
 * Items fade out and scale down as they move away from the center.
 *
 * @param value The current selected value. If outside [range], it will be coerced into the range.
 * @param onValueChange The callback invoked when the selected value changes.
 * @param modifier The modifier to be applied to the [NumberPicker].
 * @param enabled Whether the [NumberPicker] is enabled for user interaction.
 * @param range The range of selectable values.
 * @param label A function that converts a value to its display string.
 * @param visibleItemCount The number of visible items. Must be odd and at least 3.
 * @param wrapAround Whether the picker wraps around from the last item to the first (infinite scrolling).
 * @param colors The [NumberPickerColors] for this [NumberPicker].
 * @param textStyle The [TextStyle] for the picker items.
 * @param itemHeight The height of each item in the picker.
 */
@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    range: IntRange = 0..10,
    label: (Int) -> String = { it.toString() },
    visibleItemCount: Int = NumberPickerStockRowCount,
    wrapAround: Boolean = false,
    colors: NumberPickerColors = NumberPickerDefaults.colors(),
    textStyle: TextStyle = RmuixTheme.textStyles.title1.copy(fontSize = NumberPickerDefaults.FocusTextSize),
    itemHeight: Dp = NumberPickerDefaults.ItemHeight,
    verticalPadding: Dp = NumberPickerDefaults.VerticalPadding,
    unitLabel: String = "",
    showSelectionLines: Boolean = LocalNumberPickerShowSelectionLines.current,
) {
    require(visibleItemCount % 2 == 1 && visibleItemCount >= 3) {
        "visibleItemCount must be odd and at least 3, but was $visibleItemCount"
    }
    require(range.first <= range.last) {
        "range must not be empty"
    }

    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val itemCount = range.last - range.first + 1
    val coercedValue = value.coerceIn(range)
    val currentIndex = coercedValue - range.first
    val halfVisibleCount = visibleItemCount / 2
    val hapticFeedback = LocalHapticFeedback.current

    // Drag offset (updated synchronously, no coroutine needed)
    var dragOffset by remember { mutableFloatStateOf(0f) }
    // Fling/snap animated offset
    val flingAnimatable = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isUserScrolling by remember { mutableStateOf(false) }
    var itemHeightPx by remember { mutableIntStateOf(0) }

    // Total offset combines drag + fling
    val totalOffset by remember {
        derivedStateOf { dragOffset + flingAnimatable.value }
    }

    // Sync offset when value changes externally
    LaunchedEffect(coercedValue) {
        if (!isDragging && dragOffset == 0f) {
            flingAnimatable.snapTo(0f)
        }
    }

    // Haptic feedback when crossing item boundaries during scroll
    val effectiveIndex by remember(currentIndex, itemCount, wrapAround) {
        derivedStateOf {
            val rawIndex = currentIndex + totalOffset.fastRoundToInt()
            if (wrapAround) {
                ((rawIndex % itemCount) + itemCount) % itemCount
            } else {
                rawIndex.coerceIn(0, itemCount - 1)
            }
        }
    }

    var lastHapticIndex by remember { mutableIntStateOf(currentIndex) }
    LaunchedEffect(Unit) {
        snapshotFlow { effectiveIndex }
            .distinctUntilChanged()
            .collect { index ->
                if (index != lastHapticIndex) {
                    if (isUserScrolling) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    lastHapticIndex = index
                }
            }
    }

    val totalHeight = itemHeight * visibleItemCount

    val draggableState =
        rememberDraggableState { delta ->
            if (itemHeightPx > 0) {
                val newOffset = dragOffset - delta / itemHeightPx
                dragOffset =
                    if (wrapAround) {
                        newOffset
                    } else {
                        newOffset.coerceIn(
                            -(currentIndex.toFloat()),
                            (itemCount - 1 - currentIndex).toFloat(),
                        )
                    }
            }
        }

    val displayValue = label(coercedValue)
    val dividerColor = numberPickerDividerColor(isNumberPickerInDarkTheme())
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                // `coui_number_picker_view_padding_vertical` sits outside the rows, so the total
                // height becomes 3 x 45dp = 135dp (`coui_datepicker_height` / `coui_timepicker_height`), and the
                .padding(vertical = verticalPadding)
                .height(totalHeight)
                .clipToBounds()
                // `COUINumberPicker` frames the selected row with two `couiPickerBackgroundColor`
                // hairlines (`coui_number_picker_background_divider_height`), inset by
                // `coui_selected_background_horizontal_padding` on both sides.
                .drawBehind {
                    if (itemHeightPx <= 0) return@drawBehind
                    if (!showSelectionLines) return@drawBehind
                    // Stock clamps the hairline with `Math.max(dimen, 1)`; a 1x density would otherwise
                    // round `coui_number_picker_background_divider_height` (0.33dp) down to a sub-pixel.
                    val strokeWidth = hairlinePx(NumberPickerDefaults.DividerHeight)
                    val inset = NumberPickerDefaults.DividerInset.toPx()
                    val centerY = size.height / 2f
                    val halfItem = NumberPickerDefaults.DividerOffset.toPx()
                    drawLine(
                        color = dividerColor,
                        start = Offset(inset, centerY - halfItem),
                        end = Offset(size.width - inset, centerY - halfItem),
                        strokeWidth = strokeWidth,
                    )
                    drawLine(
                        color = dividerColor,
                        start = Offset(inset, centerY + halfItem),
                        end = Offset(size.width - inset, centerY + halfItem),
                        strokeWidth = strokeWidth,
                    )
                }.semantics {
                    contentDescription = "$displayValue, ${range.first} - ${range.last}"
                }.onSizeChanged { size ->
                    itemHeightPx = size.height / visibleItemCount
                }.then(
                    if (enabled) {
                        Modifier.draggable(
                            orientation = Orientation.Vertical,
                            state = draggableState,
                            onDragStarted = {
                                // Stop any ongoing fling animation
                                flingAnimatable.stop()
                                // Absorb fling remainder into drag
                                dragOffset += flingAnimatable.value
                                flingAnimatable.snapTo(0f)
                                isDragging = true
                                isUserScrolling = true
                            },
                            onDragStopped = { velocity ->
                                isDragging = false
                                if (itemHeightPx > 0) {
                                    // Transfer drag offset to fling animatable
                                    val currentDragOffset = dragOffset
                                    dragOffset = 0f
                                    flingAnimatable.snapTo(currentDragOffset)

                                    val velocityInItems = -velocity / itemHeightPx
                                    val decay = exponentialDecay<Float>(frictionMultiplier = 2f)
                                    // Set bounds for non-wrap mode
                                    if (!wrapAround) {
                                        val min = -(currentIndex.toFloat())
                                        val max = (itemCount - 1 - currentIndex).toFloat()
                                        flingAnimatable.updateBounds(min, max)
                                    }
                                    // Fling with real decay for natural inertia
                                    flingAnimatable.animateDecay(velocityInItems, decay)
                                    // Reset bounds
                                    flingAnimatable.updateBounds(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY)
                                    // Snap to nearest item
                                    val snappedTarget = flingAnimatable.value.fastRoundToInt().toFloat()
                                    flingAnimatable.animateTo(
                                        targetValue = snappedTarget,
                                        animationSpec = spring(dampingRatio = 1f, stiffness = 400f),
                                    )
                                    val offsetInt = flingAnimatable.value.fastRoundToInt()
                                    val newIndex =
                                        if (wrapAround) {
                                            ((currentIndex + offsetInt) % itemCount + itemCount) % itemCount
                                        } else {
                                            (currentIndex + offsetInt).coerceIn(0, itemCount - 1)
                                        }
                                    val newValue = range.first + newIndex
                                    if (newValue != coercedValue) {
                                        currentOnValueChange(newValue)
                                    }
                                    isUserScrolling = false
                                    flingAnimatable.snapTo(0f)
                                }
                            },
                        )
                    } else {
                        Modifier
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        if (itemHeightPx > 0) {
            val currentTotalOffset = totalOffset
            val centerItemOffset = currentTotalOffset - currentTotalOffset.fastRoundToInt()
            val roundedOffset = currentTotalOffset.fastRoundToInt()
            val selectedColor = colors.selectedTextColor(enabled)
            val unselectedColor = colors.unselectedTextColor(enabled)
            val resolvedTextStyle = if (textStyle.fontWeight == null) textStyle.copy(fontWeight = FontWeight.SemiBold) else textStyle

            for (i in -halfVisibleCount - 1..halfVisibleCount + 1) {
                val rawItemIndex = currentIndex + i + roundedOffset
                val itemIndex =
                    if (wrapAround) {
                        ((rawItemIndex % itemCount) + itemCount) % itemCount
                    } else {
                        if (rawItemIndex !in 0..<itemCount) continue
                        rawItemIndex
                    }

                val distanceFromCenter = i.toFloat() - centerItemOffset
                val normalizedDistance = (abs(distanceFromCenter) / (halfVisibleCount + 0.5f)).coerceIn(0f, 1f)

                val alpha = (1f - normalizedDistance) * (1f - normalizedDistance * 0.5f)
                val scale = 1f - 0.2f * normalizedDistance
                val yOffset = distanceFromCenter * itemHeightPx

                // `COUINumberPicker.onDraw` paints every row at `mNormalTextSize` (16sp) first, then
                // clips a band of `mBackgroundRadius` around the centre and repaints only that band at
                // `mFocusTextSize` (20sp); the band covers the centered row alone, so neighbouring rows
                // stay at 16sp instead of interpolating.
                val rowFontSize =
                    if (abs(distanceFromCenter) < 0.5f) {
                        NumberPickerDefaults.FocusTextSize
                    } else {
                        NumberPickerDefaults.NormalTextSize
                    }

                val textColor =
                    Color(
                        red = lerp(selectedColor.red, unselectedColor.red, normalizedDistance),
                        green = lerp(selectedColor.green, unselectedColor.green, normalizedDistance),
                        blue = lerp(selectedColor.blue, unselectedColor.blue, normalizedDistance),
                        alpha = lerp(selectedColor.alpha, unselectedColor.alpha, normalizedDistance),
                    )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = label(range.first + itemIndex),
                        modifier =
                            Modifier
                                .graphicsLayer {
                                    this.alpha = alpha
                                    scaleX = scale
                                    scaleY = scale
                                    translationY = yOffset
                                },
                        style = resolvedTextStyle,
                        fontSize = rowFontSize,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    // `coui_numberpicker_unit_textSize` (14dp) + `coui_number_picker_text_margin_start`
                    // (4dp) + `coui_numberpicker_unit_margin_bottom` (2dp); only the selected row shows it.
                    if (unitLabel.isNotEmpty() && itemIndex == currentIndex) {
                        Text(
                            text = unitLabel,
                            modifier =
                                Modifier
                                    // `coui_number_picker_unit_min_width` (21dp): the stock label never shrinks below
                                    // that, so single-character units keep a stable offset after the value column.
                                    .widthIn(min = NumberPickerDefaults.UnitMinWidth)
                                    .padding(start = NumberPickerDefaults.UnitTextMarginStart)
                                    .padding(bottom = NumberPickerDefaults.UnitTextMarginBottom),
                            style = resolvedTextStyle,
                            fontSize = NumberPickerDefaults.UnitTextSize,
                            color = textColor,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Contains default values used by [NumberPicker].
 */
object NumberPickerDefaults {
    /** One row is 45dp: the 135dp picker height divided by the three visible rows. */
    val ItemHeight = NumberPickerStockItemHeight

    /** `coui_number_picker_view_padding_vertical`: 12dp above and below the rows. */
    val VerticalPadding = NumberPickerStockVerticalPadding

    /** `coui_number_picker_unit_min_width`: 21dp floor for the unit label. */
    val UnitMinWidth = NumberPickerStockUnitMinWidth

    /** `focusTextSize` → `coui_numberpicker_textSize_big`: the centered row renders at 20sp. */
    val FocusTextSize = NumberPickerStockFocusTextSize

    /** `startTextSize` → `coui_numberpicker_textSize_small`: every other row renders at 16sp. */
    val NormalTextSize = NumberPickerStockNormalTextSize

    /** Height of the two selection hairlines (`coui_number_picker_background_divider_height`). */
    val DividerHeight = NumberPickerStockDividerHeight

    /** Horizontal inset of those hairlines (`coui_selected_background_horizontal_padding`). */
    val DividerInset = NumberPickerStockDividerInset

    /**
     * Distance of each hairline from the picker centre: `coui_selected_background_radius` = 24dp.
     * `COUINumberPicker.onDraw` draws them at `getHeight()/2 -/+ mBackgroundRadius`, i.e. 48dp apart,
     * not at the 45dp row boundaries.
     */
    val DividerOffset = NumberPickerStockDividerOffset

    /** `coui_numberpicker_unit_textSize` = 14dp, drawn next to the selected value. */
    val UnitTextSize = NumberPickerStockUnitTextSize

    /** `coui_number_picker_text_margin_start` = 4dp between the value and the unit. */
    val UnitTextMarginStart = NumberPickerStockUnitMarginStart

    /** `coui_numberpicker_unit_margin_bottom` = 2dp: the unit sits slightly above the baseline. */
    val UnitTextMarginBottom = NumberPickerStockUnitMarginBottom

    /**
     * Creates the default [NumberPickerColors] for a [NumberPicker].
     *
     * @param selectedTextColor The color for the selected (center) item text.
     * @param unselectedTextColor The color for unselected item text.
     * @param disabledSelectedTextColor The color for the selected item text when disabled.
     * @param disabledUnselectedTextColor The color for unselected item text when disabled.
     */
    @Composable
    fun colors(
        // `couiFocusTextColor` → `couiColorPrimaryNeutral`, `couiNormalTextColor` → `couiColorHintNeutral`.
        selectedTextColor: Color = numberPickerSelectedTextColor(isNumberPickerInDarkTheme()),
        unselectedTextColor: Color = numberPickerUnselectedTextColor(isNumberPickerInDarkTheme()),
        disabledSelectedTextColor: Color = RmuixTheme.colorScheme.disabledOnSecondary,
        disabledUnselectedTextColor: Color = RmuixTheme.colorScheme.disabledOnSecondary,
    ): NumberPickerColors =
        remember(
            selectedTextColor,
            unselectedTextColor,
            disabledSelectedTextColor,
            disabledUnselectedTextColor,
        ) {
            NumberPickerColors(
                selectedTextColor = selectedTextColor,
                unselectedTextColor = unselectedTextColor,
                disabledSelectedTextColor = disabledSelectedTextColor,
                disabledUnselectedTextColor = disabledUnselectedTextColor,
            )
        }
}

/**
 * The colors for a [NumberPicker].
 *
 * @see NumberPickerDefaults.colors
 */
@Immutable
data class NumberPickerColors(
    private val selectedTextColor: Color,
    private val unselectedTextColor: Color,
    private val disabledSelectedTextColor: Color,
    private val disabledUnselectedTextColor: Color,
) {
    @Stable
    internal fun selectedTextColor(enabled: Boolean): Color = if (enabled) selectedTextColor else disabledSelectedTextColor

    @Stable
    internal fun unselectedTextColor(enabled: Boolean): Color = if (enabled) unselectedTextColor else disabledUnselectedTextColor
}
