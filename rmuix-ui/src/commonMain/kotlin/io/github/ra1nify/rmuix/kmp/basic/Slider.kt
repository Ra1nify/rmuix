// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.annotation.IntRange
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.abs

/**
 * A [Slider] component with ColorOS 17 style, supporting light and motion effects.
 *
 * @param value The current value of the [Slider]. If outside of [valueRange] provided, value will be coerced to this range.
 * @param onValueChange The callback to be called when the value changes.
 * @param modifier The modifier to be applied to the [Slider].
 * @param enabled Whether the [Slider] is enabled.
 * @param valueRange Range of values that this slider can take. The passed [value] will be coerced to this range.
 * @param steps If positive, specifies the amount of discrete allowable values between the endpoints of [valueRange].
 * @param onValueChangeFinished Called when value change has ended.
 * @param reverseDirection Controls the direction of this slider. When false (default), slider increases from left to right.
 * @param height The height of the [Slider] touch response area.
 * @param trackHeight The height of the slider track. Defaults to [SliderDefaults.CapsuleTrackHeight].
 * @param colors The [SliderColors] of the [Slider].
 * @param hapticEffect The haptic effect of the [Slider].
 * @param showGlitterEffect Whether to show the ColorOS glitter light particle effect on the active track.
 * @param showSpotLight Whether to show the ColorOS 3D spotlight halo ring on the thumb.
 * @param showKeyPoints Whether to show the key points (step indicators) on the slider.
 * @param keyPoints Custom key point values to display on the slider.
 * @param magnetThreshold The magnetic snap threshold as a fraction (0.0 to 1.0).
 */
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    reverseDirection: Boolean = false,
    height: Dp = SliderDefaults.MinHeight,
    trackHeight: Dp = SliderDefaults.CapsuleTrackHeight,
    colors: SliderColors = SliderDefaults.sliderColors(),
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.DefaultHapticEffect,
    showGlitterEffect: Boolean = true,
    showSpotLight: Boolean = true,
    showKeyPoints: Boolean = false,
    keyPoints: List<Float>? = null,
    magnetThreshold: Float = 0.02f,
) {
    require(steps >= 0) { "steps should be >= 0" }
    require(valueRange.start < valueRange.endInclusive) { "valueRange start should be less than end" }

    val hapticFeedback = LocalHapticFeedback.current
    val layoutDirection = LocalLayoutDirection.current
    val effectiveReverseDirection = if (layoutDirection == LayoutDirection.Rtl) !reverseDirection else reverseDirection
    val onValueChangeState by rememberUpdatedState(onValueChange)
    val onValueChangeFinishedState by rememberUpdatedState(onValueChangeFinished)
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isHoveringThumb by remember { mutableStateOf(false) }
    var layoutWidth by remember { mutableIntStateOf(0) }
    var layoutHeight by remember { mutableIntStateOf(0) }
    val hapticState = remember { SliderHapticState() }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val coercedValue = value.coerceIn(valueRange.start, valueRange.endInclusive)

    val progressAnimationSpec = remember(isDragging) {
        if (isDragging) {
            spring(dampingRatio = 0.9f, stiffness = 1755f)
        } else {
            spring<Float>(dampingRatio = 0.96f, stiffness = 322f)
        }
    }

    val animatedValueState = animateFloatAsState(coercedValue, progressAnimationSpec, label = "SliderAnimatedValue")
    val thumbScaleState = animateFloatAsState(
        targetValue = if (isPressed || isDragging || isHoveringThumb) 1.333f else 1f,
        animationSpec = ThumbScaleAnimationSpec,
        label = "SliderThumbScale",
    )

    val stepFractions = remember(steps) { stepsToTickFractions(steps) }

    val keyPointFractions = remember(keyPoints, stepFractions, valueRange, showKeyPoints) {
        computeKeyPointFractions(keyPoints, stepFractions, valueRange, showKeyPoints)
    }

    val allKeyPointFractions = remember(keyPoints, stepFractions, valueRange) {
        computeAllKeyPointFractions(keyPoints, stepFractions, valueRange)
    }

    val fractionToValue = remember(valueRange, steps, stepFractions, allKeyPointFractions, magnetThreshold) {
        { fraction: Float ->
            resolveValueFromFraction(
                fraction = fraction,
                valueRange = valueRange,
                steps = steps,
                allKeyPointFractions = allKeyPointFractions,
                magnetThreshold = magnetThreshold,
            )
        }
    }

    val currentLayoutWidth = layoutWidth
    val currentLayoutHeight = layoutHeight

    Box(
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier
                        .onSizeChanged {
                            layoutWidth = it.width
                            layoutHeight = it.height
                        }
                        .pointerInput(effectiveReverseDirection, valueRange) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.last()

                                    if (event.type == PointerEventType.Exit ||
                                        event.type == PointerEventType.Release ||
                                        change.type != PointerType.Mouse
                                    ) {
                                        isHoveringThumb = false
                                        continue
                                    }

                                    val thumbRadius = currentLayoutHeight / 2f
                                    val availableWidth = (currentLayoutWidth - 2f * thumbRadius).coerceAtLeast(0f)
                                    val knobRadius = thumbRadius * 0.72f
                                    val hitRadius = knobRadius + (thumbRadius * 0.5f)

                                    val position = change.position
                                    val fraction = (animatedValueState.value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                                    val effectiveFraction = if (effectiveReverseDirection) 1f - fraction else fraction
                                    val thumbX = thumbRadius + effectiveFraction * availableWidth

                                    val isOver = abs(position.x - thumbX) <= hitRadius
                                    if (isHoveringThumb != isOver) {
                                        isHoveringThumb = isOver
                                    }
                                }
                            }
                        }
                        .hoverable(
                            interactionSource = interactionSource,
                            enabled = enabled,
                        )
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { dragAmount ->
                                dragOffset += dragAmount
                                val visualFraction = horizontalVisualFraction(dragOffset, layoutWidth, layoutHeight)
                                val fractionForValue = if (effectiveReverseDirection) 1f - visualFraction else visualFraction
                                val calculatedValue = fractionToValue(fractionForValue)
                                onValueChangeState(calculatedValue)
                                hapticState.handleHapticFeedback(
                                    calculatedValue,
                                    valueRange,
                                    hapticEffect,
                                    hapticFeedback,
                                    allKeyPointFractions,
                                    hasCustomKeyPoints = keyPoints != null,
                                )
                            },
                            onDragStarted = { offset ->
                                isDragging = true
                                dragOffset = offset.x
                                val visualFraction = horizontalVisualFraction(offset.x, layoutWidth, layoutHeight)
                                val fractionForValue = if (effectiveReverseDirection) 1f - visualFraction else visualFraction
                                val calculatedValue = fractionToValue(fractionForValue)
                                onValueChangeState(calculatedValue)
                                hapticState.reset(calculatedValue, valueRange, coercedValue)
                            },
                            onDragStopped = {
                                isDragging = false
                                onValueChangeFinishedState?.invoke()
                            },
                        )
                        .indication(interactionSource, null)
                } else {
                    Modifier
                },
            )
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(
                    coercedValue,
                    valueRange.start..valueRange.endInclusive,
                    if (steps > 0) steps else 0,
                )
                setProgress { target ->
                    val clamped = target.coerceIn(valueRange.start, valueRange.endInclusive)
                    onValueChangeState(clamped)
                    true
                }
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        SliderTrack(
            backgroundColor = colors.backgroundColor(enabled),
            foregroundColor = colors.foregroundColor(enabled),
            thumbColor = colors.thumbColor(enabled),
            thumbShadowColor = colors.thumbShadowColor(enabled),
            glitterMinColor = colors.glitterMinColor(),
            glitterMaxColor = colors.glitterMaxColor(),
            keyPointColor = colors.keyPointColor(),
            keyPointForegroundColor = colors.keyPointForegroundColor(),
            valueProvider = { animatedValueState.value },
            valueRange = valueRange,
            isDragging = isDragging,
            isVertical = false,
            showGlitterEffect = showGlitterEffect,
            showSpotLight = showSpotLight,
            showKeyPoints = showKeyPoints,
            stepFractions = keyPointFractions,
            thumbScaleProvider = { thumbScaleState.value },
            reverseDirection = effectiveReverseDirection,
            trackHeight = trackHeight,
            modifier = Modifier.fillMaxWidth().height(height),
        )
    }
}

/**
 * A vertical [Slider] component with ColorOS 17 style.
 */
@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    reverseDirection: Boolean = false,
    width: Dp = SliderDefaults.MinHeight,
    trackWidth: Dp = SliderDefaults.CapsuleTrackHeight,
    colors: SliderColors = SliderDefaults.sliderColors(),
    effect: Boolean = false,
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.DefaultHapticEffect,
    showGlitterEffect: Boolean = true,
    showSpotLight: Boolean = true,
    showKeyPoints: Boolean = false,
    keyPoints: List<Float>? = null,
    magnetThreshold: Float = 0.02f,
) {
    require(steps >= 0) { "steps should be >= 0" }
    require(valueRange.start < valueRange.endInclusive) { "valueRange start should be less than end" }

    val hapticFeedback = LocalHapticFeedback.current
    val onValueChangeState by rememberUpdatedState(onValueChange)
    val onValueChangeFinishedState by rememberUpdatedState(onValueChangeFinished)
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isHoveringThumb by remember { mutableStateOf(false) }
    val hapticState = remember { SliderHapticState() }
    val interactionSource = remember { MutableInteractionSource() }
    var layoutWidth by remember { mutableIntStateOf(0) }
    var layoutHeight by remember { mutableIntStateOf(0) }
    val isPressed by interactionSource.collectIsPressedAsState()

    val coercedValue = value.coerceIn(valueRange.start, valueRange.endInclusive)

    val progressAnimationSpec = remember(isDragging) {
        if (isDragging) {
            spring(dampingRatio = 0.9f, stiffness = 1755f)
        } else {
            spring<Float>(dampingRatio = 0.96f, stiffness = 322f)
        }
    }

    val animatedValueState = animateFloatAsState(coercedValue, progressAnimationSpec, label = "VerticalSliderAnimatedValue")
    val thumbScaleState = animateFloatAsState(
        targetValue = if (isPressed || isDragging || isHoveringThumb) 1.333f else 1f,
        animationSpec = ThumbScaleAnimationSpec,
        label = "VerticalSliderThumbScale",
    )

    val stepFractions = remember(steps) { stepsToTickFractions(steps) }

    val keyPointFractions = remember(keyPoints, stepFractions, valueRange, showKeyPoints) {
        computeKeyPointFractions(keyPoints, stepFractions, valueRange, showKeyPoints)
    }

    val allKeyPointFractions = remember(keyPoints, stepFractions, valueRange) {
        computeAllKeyPointFractions(keyPoints, stepFractions, valueRange)
    }

    val fractionToValueVertical = remember(valueRange, steps, stepFractions, allKeyPointFractions, magnetThreshold) {
        { fraction: Float ->
            resolveValueFromFraction(
                fraction = fraction,
                valueRange = valueRange,
                steps = steps,
                allKeyPointFractions = allKeyPointFractions,
                magnetThreshold = magnetThreshold,
            )
        }
    }

    val currentLayoutWidth = layoutWidth
    val currentLayoutHeight = layoutHeight

    Box(
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier
                        .onSizeChanged {
                            layoutWidth = it.width
                            layoutHeight = it.height
                        }
                        .pointerInput(reverseDirection, valueRange) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.last()

                                    if (event.type == PointerEventType.Exit ||
                                        event.type == PointerEventType.Release ||
                                        change.type != PointerType.Mouse
                                    ) {
                                        isHoveringThumb = false
                                        continue
                                    }

                                    val thumbRadius = currentLayoutWidth / 2f
                                    val availableHeight = (currentLayoutHeight - 2f * thumbRadius).coerceAtLeast(0f)
                                    val knobRadius = thumbRadius * 0.72f
                                    val hitRadius = knobRadius + (thumbRadius * 0.5f)

                                    val position = change.position
                                    val fraction =
                                        (animatedValueState.value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                                    val effectiveFraction = if (reverseDirection) fraction else 1f - fraction
                                    val thumbY = thumbRadius + effectiveFraction * availableHeight

                                    val isOver = abs(position.y - thumbY) <= hitRadius
                                    if (isHoveringThumb != isOver) {
                                        isHoveringThumb = isOver
                                    }
                                }
                            }
                        }
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { dragAmount ->
                                dragOffset += dragAmount
                                val visualFraction = verticalVisualFraction(dragOffset, layoutHeight, layoutWidth)
                                val fractionForValue = if (reverseDirection) visualFraction else 1f - visualFraction
                                val calculatedValue = fractionToValueVertical(fractionForValue)
                                onValueChangeState(calculatedValue)
                                hapticState.handleHapticFeedback(
                                    calculatedValue,
                                    valueRange,
                                    hapticEffect,
                                    hapticFeedback,
                                    allKeyPointFractions,
                                    hasCustomKeyPoints = keyPoints != null,
                                )
                            },
                            onDragStarted = { offset ->
                                isDragging = true
                                dragOffset = offset.y
                                val visualFraction = verticalVisualFraction(offset.y, layoutHeight, layoutWidth)
                                val fractionForValue = if (reverseDirection) visualFraction else 1f - visualFraction
                                val calculatedValue = fractionToValueVertical(fractionForValue)
                                onValueChangeState(calculatedValue)
                                hapticState.reset(calculatedValue, valueRange, coercedValue)
                            },
                            onDragStopped = {
                                isDragging = false
                                onValueChangeFinishedState?.invoke()
                            },
                        )
                        .indication(interactionSource, null)
                } else {
                    Modifier
                },
            )
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(
                    coercedValue,
                    valueRange.start..valueRange.endInclusive,
                    if (steps > 0) steps else 0,
                )
                setProgress { target ->
                    val clamped = target.coerceIn(valueRange.start, valueRange.endInclusive)
                    onValueChangeState(clamped)
                    true
                }
            },
        contentAlignment = Alignment.BottomCenter,
    ) {
        SliderTrack(
            backgroundColor = colors.backgroundColor(enabled),
            foregroundColor = colors.foregroundColor(enabled),
            thumbColor = colors.thumbColor(enabled),
            thumbShadowColor = colors.thumbShadowColor(enabled),
            glitterMinColor = colors.glitterMinColor(),
            glitterMaxColor = colors.glitterMaxColor(),
            keyPointColor = colors.keyPointColor(),
            keyPointForegroundColor = colors.keyPointForegroundColor(),
            valueProvider = { animatedValueState.value },
            valueRange = valueRange,
            isDragging = isDragging,
            isVertical = true,
            showGlitterEffect = showGlitterEffect || effect,
            showSpotLight = showSpotLight,
            showKeyPoints = showKeyPoints,
            stepFractions = keyPointFractions,
            thumbScaleProvider = { thumbScaleState.value },
            reverseDirection = reverseDirection,
            trackHeight = trackWidth,
            modifier = Modifier.width(width).fillMaxHeight(),
        )
    }
}

/**
 * A [RangeSlider] component with ColorOS 17 style.
 */
@Composable
fun RangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    height: Dp = SliderDefaults.MinHeight,
    trackHeight: Dp = SliderDefaults.CapsuleTrackHeight,
    colors: SliderColors = SliderDefaults.sliderColors(),
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.DefaultHapticEffect,
    showGlitterEffect: Boolean = true,
    showSpotLight: Boolean = true,
    showKeyPoints: Boolean = false,
    keyPoints: List<Float>? = null,
    magnetThreshold: Float = 0.02f,
) {
    require(steps >= 0) { "steps should be >= 0" }
    require(valueRange.start < valueRange.endInclusive) { "valueRange start should be less than end" }

    val hapticFeedback = LocalHapticFeedback.current
    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl
    val onValueChangeState by rememberUpdatedState(onValueChange)
    val onValueChangeFinishedState by rememberUpdatedState(onValueChangeFinished)
    var startDragOffset by remember { mutableFloatStateOf(0f) }
    var endDragOffset by remember { mutableFloatStateOf(0f) }
    var isDraggingStart by remember { mutableStateOf(false) }
    var isDraggingEnd by remember { mutableStateOf(false) }
    var isHoveringStartThumb by remember { mutableStateOf(false) }
    var isHoveringEndThumb by remember { mutableStateOf(false) }
    val isDragging by remember { derivedStateOf { isDraggingStart || isDraggingEnd } }
    val hapticState = remember { RangeSliderHapticState() }
    val interactionSource = remember { MutableInteractionSource() }
    var lastDraggedIsStart by remember { mutableStateOf(true) }
    var layoutWidth by remember { mutableIntStateOf(0) }
    var layoutHeight by remember { mutableIntStateOf(0) }
    val isPressed by interactionSource.collectIsPressedAsState()

    var currentStartValue by remember { mutableFloatStateOf(value.start) }
    var currentEndValue by remember { mutableFloatStateOf(value.endInclusive) }

    if (!isDragging) {
        currentStartValue = value.start
        currentEndValue = value.endInclusive
    }

    val coercedStart = currentStartValue.coerceIn(valueRange.start, valueRange.endInclusive)
    val coercedEnd = currentEndValue.coerceIn(valueRange.start, valueRange.endInclusive)

    val progressAnimationSpec = remember(isDragging) {
        if (isDragging) {
            spring(dampingRatio = 0.9f, stiffness = 1755f)
        } else {
            spring<Float>(dampingRatio = 0.96f, stiffness = 322f)
        }
    }

    val animatedStartValueState = animateFloatAsState(coercedStart, progressAnimationSpec, label = "RangeSliderStartValue")
    val animatedEndValueState = animateFloatAsState(coercedEnd, progressAnimationSpec, label = "RangeSliderEndValue")
    val startThumbScaleState = animateFloatAsState(
        targetValue = if (isDraggingStart || isPressed || isHoveringStartThumb) 1.333f else 1f,
        animationSpec = ThumbScaleAnimationSpec,
        label = "RangeSliderStartThumbScale",
    )
    val endThumbScaleState = animateFloatAsState(
        targetValue = if (isDraggingEnd || isPressed || isHoveringEndThumb) 1.333f else 1f,
        animationSpec = ThumbScaleAnimationSpec,
        label = "RangeSliderEndThumbScale",
    )

    val stepFractions = remember(steps) { stepsToTickFractions(steps) }

    val keyPointFractions = remember(keyPoints, stepFractions, valueRange, showKeyPoints) {
        computeKeyPointFractions(keyPoints, stepFractions, valueRange, showKeyPoints)
    }

    val allKeyPointFractions = remember(keyPoints, stepFractions, valueRange) {
        computeAllKeyPointFractions(keyPoints, stepFractions, valueRange)
    }

    val fractionToValueRange = remember(valueRange, steps, stepFractions, allKeyPointFractions, magnetThreshold) {
        { fraction: Float ->
            resolveValueFromFraction(
                fraction = fraction,
                valueRange = valueRange,
                steps = steps,
                allKeyPointFractions = allKeyPointFractions,
                magnetThreshold = magnetThreshold,
            )
        }
    }

    val currentLayoutWidth = layoutWidth
    val currentLayoutHeight = layoutHeight

    Box(
        modifier = modifier
            .then(
                if (enabled) {
                    Modifier
                        .onSizeChanged {
                            layoutWidth = it.width
                            layoutHeight = it.height
                        }
                        .pointerInput(isRtl, valueRange) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.last()

                                    if (event.type == PointerEventType.Exit ||
                                        event.type == PointerEventType.Release ||
                                        change.type != PointerType.Mouse
                                    ) {
                                        isHoveringStartThumb = false
                                        isHoveringEndThumb = false
                                        continue
                                    }

                                    val thumbRadius = currentLayoutHeight / 2f
                                    val availableWidth = (currentLayoutWidth - 2f * thumbRadius).coerceAtLeast(0f)
                                    val knobRadius = thumbRadius * 0.72f
                                    val hitRadius = knobRadius + (thumbRadius * 0.5f)

                                    val position = change.position
                                    val startFraction = (animatedStartValueState.value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                                    val endFraction = (animatedEndValueState.value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                                    val effectiveStartFraction = if (isRtl) 1f - startFraction else startFraction
                                    val effectiveEndFraction = if (isRtl) 1f - endFraction else endFraction
                                    val startThumbX = thumbRadius + effectiveStartFraction * availableWidth
                                    val endThumbX = thumbRadius + effectiveEndFraction * availableWidth

                                    val isOverStart = abs(position.x - startThumbX) <= hitRadius
                                    val isOverEnd = abs(position.x - endThumbX) <= hitRadius

                                    if (isHoveringStartThumb != isOverStart) {
                                        isHoveringStartThumb = isOverStart
                                    }
                                    if (isHoveringEndThumb != isOverEnd) {
                                        isHoveringEndThumb = isOverEnd
                                    }
                                }
                            }
                        }
                        .hoverable(interactionSource = interactionSource, enabled = enabled)
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { dragAmount ->
                                if (isDraggingStart) {
                                    lastDraggedIsStart = true
                                    val tentativeStartOffset = startDragOffset + dragAmount
                                    val visualFractionStart = horizontalVisualFraction(tentativeStartOffset, layoutWidth, layoutHeight)
                                    val fractionForValue = if (isRtl) 1f - visualFractionStart else visualFractionStart
                                    val newStart = fractionToValueRange(fractionForValue).coerceAtMost(currentEndValue)
                                    val crossCondition = if (isRtl) dragAmount < 0f else dragAmount > 0f

                                    if (newStart >= currentEndValue && crossCondition && currentStartValue == currentEndValue) {
                                        isDraggingStart = false
                                        isDraggingEnd = true

                                        endDragOffset = tentativeStartOffset
                                        hapticState.resetEnd(currentEndValue, valueRange)
                                        hapticState.inheritEndKeyPoint()

                                        val visualFractionEnd = horizontalVisualFraction(endDragOffset, layoutWidth, layoutHeight)
                                        val fractionForValueEnd = if (isRtl) 1f - visualFractionEnd else visualFractionEnd
                                        val newEnd = fractionToValueRange(fractionForValueEnd).coerceAtLeast(currentStartValue)
                                        currentEndValue = newEnd
                                        onValueChangeState(currentStartValue..newEnd)
                                        hapticState.handleEndHapticFeedback(
                                            newEnd,
                                            valueRange,
                                            hapticEffect,
                                            hapticFeedback,
                                            allKeyPointFractions,
                                            hasCustomKeyPoints = keyPoints != null,
                                        )
                                    } else {
                                        startDragOffset = tentativeStartOffset
                                        currentStartValue = newStart
                                        onValueChangeState(newStart..currentEndValue)
                                        hapticState.handleStartHapticFeedback(
                                            newStart,
                                            valueRange,
                                            hapticEffect,
                                            hapticFeedback,
                                            allKeyPointFractions,
                                            hasCustomKeyPoints = keyPoints != null,
                                        )
                                    }
                                } else if (isDraggingEnd) {
                                    lastDraggedIsStart = false
                                    val tentativeEndOffset = endDragOffset + dragAmount
                                    val visualFractionEnd = horizontalVisualFraction(tentativeEndOffset, layoutWidth, layoutHeight)
                                    val fractionForValue = if (isRtl) 1f - visualFractionEnd else visualFractionEnd
                                    val newEnd = fractionToValueRange(fractionForValue).coerceAtLeast(currentStartValue)
                                    val crossCondition = if (isRtl) dragAmount > 0f else dragAmount < 0f

                                    if (newEnd <= currentStartValue && crossCondition && currentStartValue == currentEndValue) {
                                        isDraggingEnd = false
                                        isDraggingStart = true
                                        startDragOffset = tentativeEndOffset
                                        hapticState.resetStart(currentStartValue, valueRange)
                                        hapticState.inheritStartKeyPoint()

                                        val visualFractionStart = horizontalVisualFraction(startDragOffset, layoutWidth, layoutHeight)
                                        val fractionForValueStart = if (isRtl) 1f - visualFractionStart else visualFractionStart
                                        val newStart = fractionToValueRange(fractionForValueStart).coerceAtMost(currentEndValue)
                                        currentStartValue = newStart
                                        onValueChangeState(newStart..currentEndValue)
                                        hapticState.handleStartHapticFeedback(
                                            newStart,
                                            valueRange,
                                            hapticEffect,
                                            hapticFeedback,
                                            allKeyPointFractions,
                                            hasCustomKeyPoints = keyPoints != null,
                                        )
                                    } else {
                                        endDragOffset = tentativeEndOffset
                                        currentEndValue = newEnd
                                        onValueChangeState(currentStartValue..newEnd)
                                        hapticState.handleEndHapticFeedback(
                                            newEnd,
                                            valueRange,
                                            hapticEffect,
                                            hapticFeedback,
                                            allKeyPointFractions,
                                            hasCustomKeyPoints = keyPoints != null,
                                        )
                                    }
                                }
                            },
                            onDragStarted = { offset ->
                                val thumbRadius = layoutHeight / 2f
                                val availableWidth = (layoutWidth - 2f * thumbRadius).coerceAtLeast(0f)
                                val startFraction =
                                    (currentStartValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                                val endFraction =
                                    (currentEndValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                                val effectiveStartFraction = if (isRtl) 1f - startFraction else startFraction
                                val effectiveEndFraction = if (isRtl) 1f - endFraction else endFraction
                                val startPos = thumbRadius + effectiveStartFraction * availableWidth
                                val endPos = thumbRadius + effectiveEndFraction * availableWidth

                                val knobRadius = thumbRadius * 0.72f
                                val hitRadius = knobRadius + (thumbRadius * 0.5f)
                                val isOnStartThumb = abs(offset.x - startPos) <= hitRadius
                                val isOnEndThumb = abs(offset.x - endPos) <= hitRadius

                                when {
                                    isOnStartThumb && !isOnEndThumb -> {
                                        isDraggingStart = true
                                        startDragOffset = offset.x
                                        hapticState.resetStart(coercedStart, valueRange)
                                    }

                                    !isOnStartThumb && isOnEndThumb -> {
                                        isDraggingEnd = true
                                        endDragOffset = offset.x
                                        hapticState.resetEnd(coercedEnd, valueRange)
                                    }

                                    isOnStartThumb && isOnEndThumb -> {
                                        if (lastDraggedIsStart) {
                                            isDraggingStart = true
                                            startDragOffset = offset.x
                                            hapticState.resetStart(coercedStart, valueRange)
                                        } else {
                                            isDraggingEnd = true
                                            endDragOffset = offset.x
                                            hapticState.resetEnd(coercedEnd, valueRange)
                                        }
                                    }

                                    else -> {
                                        val diffStart = abs(offset.x - startPos)
                                        val diffEnd = abs(offset.x - endPos)
                                        if (diffStart <= diffEnd) {
                                            isDraggingStart = true
                                            startDragOffset = offset.x
                                            hapticState.resetStart(coercedStart, valueRange)
                                        } else {
                                            isDraggingEnd = true
                                            endDragOffset = offset.x
                                            hapticState.resetEnd(coercedEnd, valueRange)
                                        }
                                    }
                                }
                            },
                            onDragStopped = {
                                isDraggingStart = false
                                isDraggingEnd = false
                                onValueChangeFinishedState?.invoke()
                            },
                        )
                        .indication(interactionSource, null)
                } else {
                    Modifier
                },
            )
            .semantics {
                stateDescription = "$coercedStart-$coercedEnd"
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        RangeSliderTrack(
            backgroundColor = colors.backgroundColor(enabled),
            foregroundColor = colors.foregroundColor(enabled),
            thumbColor = colors.thumbColor(enabled),
            thumbShadowColor = colors.thumbShadowColor(enabled),
            glitterMinColor = colors.glitterMinColor(),
            glitterMaxColor = colors.glitterMaxColor(),
            keyPointColor = colors.keyPointColor(),
            keyPointForegroundColor = colors.keyPointForegroundColor(),
            valueStartProvider = { animatedStartValueState.value },
            valueEndProvider = { animatedEndValueState.value },
            startThumbScaleProvider = { startThumbScaleState.value },
            endThumbScaleProvider = { endThumbScaleState.value },
            valueRange = valueRange,
            isDragging = isDragging,
            showGlitterEffect = showGlitterEffect,
            showSpotLight = showSpotLight,
            showKeyPoints = showKeyPoints,
            stepFractions = keyPointFractions,
            isRtl = isRtl,
            trackHeight = trackHeight,
            modifier = Modifier.fillMaxWidth().height(height),
        )
    }
}

/**
 * Internal slider track renderer supporting ColorOS 17 light and motion effects
 */
@Composable
private fun SliderTrack(
    backgroundColor: Color,
    foregroundColor: Color,
    thumbColor: Color,
    thumbShadowColor: Color,
    glitterMinColor: Color,
    glitterMaxColor: Color,
    keyPointColor: Color,
    keyPointForegroundColor: Color,
    valueProvider: () -> Float,
    valueRange: ClosedFloatingPointRange<Float>,
    isDragging: Boolean,
    isVertical: Boolean,
    showGlitterEffect: Boolean,
    showSpotLight: Boolean,
    showKeyPoints: Boolean,
    stepFractions: FloatArray,
    thumbScaleProvider: () -> Float,
    reverseDirection: Boolean,
    trackHeight: Dp,
    modifier: Modifier = Modifier,
) {
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isDragging) 0.044f else 0f,
        animationSpec = tween(150),
        label = "SliderTrackAlpha",
    )

    Canvas(
        modifier = modifier
            .clip(CircleShape)
            .drawBehind {
                drawRect(backgroundColor)
                drawRect(Color.Black, alpha = backgroundAlpha)
            },
    ) {
        val barHeight = size.height
        val barWidth = size.width
        val trackPx = trackHeight.toPx().coerceAtMost(if (isVertical) barWidth else barHeight)
        val value = valueProvider()
        val thumbScale = thumbScaleProvider()
        val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)

        if (isVertical) {
            val thumbRadius = barWidth / 2f
            val availableHeight = (barHeight - 2f * thumbRadius).coerceAtLeast(0f)
            val effectiveFraction = if (reverseDirection) fraction else (1f - fraction)
            val centerY = thumbRadius + effectiveFraction * availableHeight

            // Background Track
            val trackX = (barWidth - trackPx) / 2f
            drawRoundRect(
                color = backgroundColor,
                topLeft = Offset(trackX, 0f),
                size = Size(trackPx, barHeight),
                cornerRadius = CornerRadius(trackPx / 2f, trackPx / 2f),
            )

            // Foreground Active Track
            val fgTop = if (reverseDirection) 0f else centerY
            val fgBottom = if (reverseDirection) centerY else barHeight
            val fgHeight = (fgBottom - fgTop).coerceAtLeast(0f)

            if (fgHeight > 0f) {
                if (showGlitterEffect) {
                    val glitterBrush = Brush.verticalGradient(
                        colors = listOf(foregroundColor, foregroundColor.copy(alpha = 0.85f), glitterMaxColor, foregroundColor),
                        startY = fgTop,
                        endY = fgBottom,
                    )
                    drawRoundRect(
                        brush = glitterBrush,
                        topLeft = Offset(trackX, fgTop),
                        size = Size(trackPx, fgHeight),
                        cornerRadius = CornerRadius(trackPx / 2f, trackPx / 2f),
                    )
                } else {
                    drawRoundRect(
                        color = foregroundColor,
                        topLeft = Offset(trackX, fgTop),
                        size = Size(trackPx, fgHeight),
                        cornerRadius = CornerRadius(trackPx / 2f, trackPx / 2f),
                    )
                }
            }

            if (showKeyPoints && stepFractions.isNotEmpty()) {
                val keyPointRadius = trackPx / 5f
                for (i in stepFractions.indices) {
                    val stepFraction = stepFractions[i]
                    val effectiveStep = if (reverseDirection) stepFraction else (1f - stepFraction)
                    val y = thumbRadius + effectiveStep * availableHeight
                    val kpColor = if (y >= centerY) keyPointForegroundColor else keyPointColor
                    drawCircle(
                        color = kpColor,
                        radius = keyPointRadius,
                        center = Offset(barWidth / 2f, y),
                    )
                }
            }

            val r = (thumbRadius * 0.72f * thumbScale).coerceAtLeast(1f)
            val center = Offset(barWidth / 2f, centerY)

            if (showSpotLight) {
                drawCircle(
                    color = thumbShadowColor,
                    radius = r + 2.dp.toPx(),
                    center = center.copy(y = center.y + 2.dp.toPx()),
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.35f),
                    radius = r + 1.2.dp.toPx(),
                    center = center,
                )
            }

            drawCircle(
                color = thumbColor,
                radius = r,
                center = center,
            )
        } else {
            val thumbRadius = barHeight / 2f
            val availableWidth = (barWidth - 2f * thumbRadius).coerceAtLeast(0f)
            val effectiveFraction = if (reverseDirection) 1f - fraction else fraction
            val centerX = thumbRadius + effectiveFraction * availableWidth
            val trackY = (barHeight - trackPx) / 2f

            // Background Track
            drawRoundRect(
                color = backgroundColor,
                topLeft = Offset(0f, trackY),
                size = Size(barWidth, trackPx),
                cornerRadius = CornerRadius(trackPx / 2f, trackPx / 2f),
            )

            // Foreground Active Track
            val fgLeft = if (reverseDirection) centerX else 0f
            val fgRight = if (reverseDirection) barWidth else centerX
            val fgWidth = (fgRight - fgLeft).coerceAtLeast(0f)

            if (fgWidth > 0f) {
                if (showGlitterEffect) {
                    val glitterBrush = Brush.horizontalGradient(
                        colors = listOf(foregroundColor, foregroundColor.copy(alpha = 0.85f), glitterMaxColor, foregroundColor),
                        startX = fgLeft,
                        endX = fgRight,
                    )
                    drawRoundRect(
                        brush = glitterBrush,
                        topLeft = Offset(fgLeft, trackY),
                        size = Size(fgWidth, trackPx),
                        cornerRadius = CornerRadius(trackPx / 2f, trackPx / 2f),
                    )
                } else {
                    drawRoundRect(
                        color = foregroundColor,
                        topLeft = Offset(fgLeft, trackY),
                        size = Size(fgWidth, trackPx),
                        cornerRadius = CornerRadius(trackPx / 2f, trackPx / 2f),
                    )
                }
            }

            if (showKeyPoints && stepFractions.isNotEmpty()) {
                val keyPointRadius = trackPx / 5f
                for (i in stepFractions.indices) {
                    val stepFraction = stepFractions[i]
                    val effectiveStep = if (reverseDirection) 1f - stepFraction else stepFraction
                    val x = thumbRadius + effectiveStep * availableWidth
                    val isSelected = if (reverseDirection) x >= centerX else x <= centerX
                    val kpColor = if (isSelected) keyPointForegroundColor else keyPointColor
                    drawCircle(
                        color = kpColor,
                        radius = keyPointRadius,
                        center = Offset(x, barHeight / 2f),
                    )
                }
            }

            val r = (thumbRadius * 0.72f * thumbScale).coerceAtLeast(1f)
            val center = Offset(centerX, barHeight / 2f)

            if (showSpotLight) {
                drawCircle(
                    color = thumbShadowColor,
                    radius = r + 2.dp.toPx(),
                    center = center.copy(y = center.y + 2.dp.toPx()),
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.35f),
                    radius = r + 1.2.dp.toPx(),
                    center = center,
                )
            }

            drawCircle(
                color = thumbColor,
                radius = r,
                center = center,
            )
        }
    }
}

/**
 * Internal range slider track renderer supporting ColorOS 17 light and motion effects
 */
@Composable
private fun RangeSliderTrack(
    backgroundColor: Color,
    foregroundColor: Color,
    thumbColor: Color,
    thumbShadowColor: Color,
    glitterMinColor: Color,
    glitterMaxColor: Color,
    keyPointColor: Color,
    keyPointForegroundColor: Color,
    valueStartProvider: () -> Float,
    valueEndProvider: () -> Float,
    startThumbScaleProvider: () -> Float,
    endThumbScaleProvider: () -> Float,
    valueRange: ClosedFloatingPointRange<Float>,
    isDragging: Boolean,
    showGlitterEffect: Boolean,
    showSpotLight: Boolean,
    showKeyPoints: Boolean,
    stepFractions: FloatArray,
    isRtl: Boolean,
    trackHeight: Dp,
    modifier: Modifier = Modifier,
) {
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isDragging) 0.044f else 0f,
        animationSpec = tween(150),
        label = "RangeSliderTrackAlpha",
    )

    Canvas(
        modifier = modifier
            .clip(CircleShape)
            .drawBehind {
                drawRect(backgroundColor)
                drawRect(Color.Black, alpha = backgroundAlpha)
            },
    ) {
        val barHeight = size.height
        val barWidth = size.width
        val trackPx = trackHeight.toPx().coerceAtMost(barHeight)
        val valueStart = valueStartProvider()
        val valueEnd = valueEndProvider()
        val startThumbScale = startThumbScaleProvider()
        val endThumbScale = endThumbScaleProvider()
        val startFraction = (valueStart - valueRange.start) / (valueRange.endInclusive - valueRange.start)
        val endFraction = (valueEnd - valueRange.start) / (valueRange.endInclusive - valueRange.start)
        val thumbRadius = barHeight / 2f
        val availableWidth = (barWidth - 2f * thumbRadius).coerceAtLeast(0f)
        val effectiveStartFraction = if (isRtl) 1f - startFraction else startFraction
        val effectiveEndFraction = if (isRtl) 1f - endFraction else endFraction
        val startX = thumbRadius + effectiveStartFraction * availableWidth
        val endX = thumbRadius + effectiveEndFraction * availableWidth

        val centerY = barHeight / 2f
        val trackY = (barHeight - trackPx) / 2f

        // Background Track
        drawRoundRect(
            color = backgroundColor,
            topLeft = Offset(0f, trackY),
            size = Size(barWidth, trackPx),
            cornerRadius = CornerRadius(trackPx / 2f, trackPx / 2f),
        )

        val fgLeft = if (startX <= endX) startX else endX
        val fgRight = if (startX <= endX) endX else startX
        val fgWidth = (fgRight - fgLeft).coerceAtLeast(0f)

        if (fgWidth > 0f) {
            if (showGlitterEffect) {
                val glitterBrush = Brush.horizontalGradient(
                    colors = listOf(foregroundColor, foregroundColor.copy(alpha = 0.85f), glitterMaxColor, foregroundColor),
                    startX = fgLeft,
                    endX = fgRight,
                )
                drawRoundRect(
                    brush = glitterBrush,
                    topLeft = Offset(fgLeft, trackY),
                    size = Size(fgWidth, trackPx),
                    cornerRadius = CornerRadius(trackPx / 2f, trackPx / 2f),
                )
            } else {
                drawRoundRect(
                    color = foregroundColor,
                    topLeft = Offset(fgLeft, trackY),
                    size = Size(fgWidth, trackPx),
                    cornerRadius = CornerRadius(trackPx / 2f, trackPx / 2f),
                )
            }
        }

        if (showKeyPoints && stepFractions.isNotEmpty()) {
            val keyPointRadius = SliderDefaults.KeyPointRadius.toPx()
            for (i in stepFractions.indices) {
                val stepFraction = stepFractions[i]
                val effectiveStep = if (isRtl) 1f - stepFraction else stepFraction
                val x = thumbRadius + effectiveStep * availableWidth
                val isSelected = if (isRtl) x in endX..startX else x in startX..endX
                val kpColor = if (isSelected) keyPointForegroundColor else keyPointColor
                drawCircle(
                    color = kpColor,
                    radius = keyPointRadius,
                    center = Offset(x, barHeight / 2f),
                )
            }
        }

        val startR = (thumbRadius * 0.72f * startThumbScale).coerceAtLeast(1f)
        val endR = (thumbRadius * 0.72f * endThumbScale).coerceAtLeast(1f)
        val startCenter = Offset(startX, centerY)
        val endCenter = Offset(endX, centerY)

        if (showSpotLight) {
            drawCircle(color = thumbShadowColor, radius = startR + 2.dp.toPx(), center = startCenter.copy(y = centerY + 2.dp.toPx()))
            drawCircle(color = Color.White.copy(alpha = 0.35f), radius = startR + 1.2.dp.toPx(), center = startCenter)

            drawCircle(color = thumbShadowColor, radius = endR + 2.dp.toPx(), center = endCenter.copy(y = centerY + 2.dp.toPx()))
            drawCircle(color = Color.White.copy(alpha = 0.35f), radius = endR + 1.2.dp.toPx(), center = endCenter)
        }

        drawCircle(color = thumbColor, radius = startR, center = startCenter)
        drawCircle(color = thumbColor, radius = endR, center = endCenter)
    }
}

/**
 * Manages haptic feedback state for the slider.
 */
@Stable
internal class SliderHapticState {
    private var edgeFeedbackTriggered: Boolean = false
    private var lastStep: Float = 0f
    private var isAtKeyPoint: Boolean = false

    fun reset(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        previousValue: Float = currentValue,
    ) {
        val isAtEdge = currentValue == valueRange.start || currentValue == valueRange.endInclusive
        edgeFeedbackTriggered = isAtEdge && previousValue == currentValue
        lastStep = currentValue
        isAtKeyPoint = false
    }

    fun handleHapticFeedback(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticEffect: SliderDefaults.SliderHapticEffect,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray = floatArrayOf(),
        hasCustomKeyPoints: Boolean = false,
    ) {
        if (hapticEffect == SliderDefaults.SliderHapticEffect.None) return

        handleEdgeHaptic(currentValue, valueRange, hapticFeedback)

        if (hapticEffect == SliderDefaults.SliderHapticEffect.Step) {
            handleStepHaptic(currentValue, valueRange, hapticFeedback, keyPointFractions, hasCustomKeyPoints)
        }
    }

    private fun handleEdgeHaptic(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticFeedback: HapticFeedback,
    ) {
        val isAtEdge = currentValue == valueRange.start || currentValue == valueRange.endInclusive
        if (isAtEdge && !edgeFeedbackTriggered) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
            edgeFeedbackTriggered = true
        } else if (!isAtEdge) {
            edgeFeedbackTriggered = false
        }
    }

    private fun handleStepHaptic(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray,
        hasCustomKeyPoints: Boolean,
    ) {
        val isNotAtEdge = currentValue != valueRange.start && currentValue != valueRange.endInclusive

        if (hasCustomKeyPoints && keyPointFractions.isNotEmpty()) {
            handleKeyPointHaptic(currentValue, valueRange, hapticFeedback, keyPointFractions, isNotAtEdge)
        } else if (!isNotAtEdge) {
            lastStep = currentValue
        } else if (currentValue != lastStep && isNotAtEdge) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            lastStep = currentValue
        }
    }

    private fun handleKeyPointHaptic(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray,
        isNotAtEdge: Boolean,
    ) {
        val fraction = (currentValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
        val threshold = 0.005f

        var nearestDist = Float.MAX_VALUE
        for (i in keyPointFractions.indices) {
            val dist = abs(keyPointFractions[i] - fraction)
            if (dist < nearestDist) nearestDist = dist
        }
        val currentlyAtKeyPoint = nearestDist < threshold

        if (currentlyAtKeyPoint && !isAtKeyPoint && isNotAtEdge) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }

        isAtKeyPoint = currentlyAtKeyPoint
    }
}

/**
 * Manages haptic feedback state for the range slider.
 */
@Stable
internal class RangeSliderHapticState {
    private var startEdgeFeedbackTriggered: Boolean = false
    private var endEdgeFeedbackTriggered: Boolean = false
    private var startLastStep: Float = 0f
    private var endLastStep: Float = 0f
    private var startIsAtKeyPoint: Boolean = false
    private var endIsAtKeyPoint: Boolean = false

    fun resetStart(currentValue: Float, valueRange: ClosedFloatingPointRange<Float>) {
        startEdgeFeedbackTriggered = currentValue == valueRange.start
        startLastStep = currentValue
        startIsAtKeyPoint = false
    }

    fun resetEnd(currentValue: Float, valueRange: ClosedFloatingPointRange<Float>) {
        endEdgeFeedbackTriggered = currentValue == valueRange.endInclusive
        endLastStep = currentValue
        endIsAtKeyPoint = false
    }

    fun inheritStartKeyPoint() {
        startIsAtKeyPoint = endIsAtKeyPoint
    }

    fun inheritEndKeyPoint() {
        endIsAtKeyPoint = startIsAtKeyPoint
    }

    fun handleStartHapticFeedback(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticEffect: SliderDefaults.SliderHapticEffect,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray = floatArrayOf(),
        hasCustomKeyPoints: Boolean = false,
    ) {
        handleHapticFeedbackInternal(
            currentValue = currentValue,
            valueRange = valueRange,
            hapticEffect = hapticEffect,
            hapticFeedback = hapticFeedback,
            keyPointFractions = keyPointFractions,
            edgeFeedbackTriggered = startEdgeFeedbackTriggered,
            lastStep = startLastStep,
            isAtKeyPoint = startIsAtKeyPoint,
            isStartEdge = true,
            hasCustomKeyPoints = hasCustomKeyPoints,
            onEdgeFeedbackUpdate = { startEdgeFeedbackTriggered = it },
            onLastStepUpdate = { startLastStep = it },
            onKeyPointUpdate = { startIsAtKeyPoint = it },
        )
    }

    fun handleEndHapticFeedback(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticEffect: SliderDefaults.SliderHapticEffect,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray = floatArrayOf(),
        hasCustomKeyPoints: Boolean = false,
    ) {
        handleHapticFeedbackInternal(
            currentValue = currentValue,
            valueRange = valueRange,
            hapticEffect = hapticEffect,
            hapticFeedback = hapticFeedback,
            keyPointFractions = keyPointFractions,
            edgeFeedbackTriggered = endEdgeFeedbackTriggered,
            lastStep = endLastStep,
            isAtKeyPoint = endIsAtKeyPoint,
            isStartEdge = false,
            hasCustomKeyPoints = hasCustomKeyPoints,
            onEdgeFeedbackUpdate = { endEdgeFeedbackTriggered = it },
            onLastStepUpdate = { endLastStep = it },
            onKeyPointUpdate = { endIsAtKeyPoint = it },
        )
    }

    private fun handleHapticFeedbackInternal(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticEffect: SliderDefaults.SliderHapticEffect,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray,
        edgeFeedbackTriggered: Boolean,
        lastStep: Float,
        isAtKeyPoint: Boolean,
        isStartEdge: Boolean,
        hasCustomKeyPoints: Boolean,
        onEdgeFeedbackUpdate: (Boolean) -> Unit,
        onLastStepUpdate: (Float) -> Unit,
        onKeyPointUpdate: (Boolean) -> Unit,
    ) {
        if (hapticEffect == SliderDefaults.SliderHapticEffect.None) return

        val targetEdge = if (isStartEdge) valueRange.start else valueRange.endInclusive
        val isAtEdge = currentValue == targetEdge

        if (isAtEdge && !edgeFeedbackTriggered) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
            onEdgeFeedbackUpdate(true)
        } else if (!isAtEdge) {
            onEdgeFeedbackUpdate(false)
        }

        if (hapticEffect == SliderDefaults.SliderHapticEffect.Step) {
            val isNotAtEdge = currentValue != targetEdge

            if (hasCustomKeyPoints && keyPointFractions.isNotEmpty()) {
                val fraction = (currentValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                val threshold = 0.005f

                var nearestDist = Float.MAX_VALUE
                for (i in keyPointFractions.indices) {
                    val dist = abs(keyPointFractions[i] - fraction)
                    if (dist < nearestDist) nearestDist = dist
                }
                val currentlyAtKeyPoint = nearestDist < threshold

                if (currentlyAtKeyPoint && !isAtKeyPoint && isNotAtEdge) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }

                onKeyPointUpdate(currentlyAtKeyPoint)
            } else if (!isNotAtEdge) {
                onLastStepUpdate(currentValue)
            } else if (currentValue != lastStep && isNotAtEdge) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onLastStepUpdate(currentValue)
            }
        }
    }
}

private fun stepsToTickFractions(steps: Int): FloatArray = if (steps == 0) floatArrayOf() else FloatArray(steps + 2) { it.toFloat() / (steps + 1) }

private fun resolveValueFromFraction(
    fraction: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    allKeyPointFractions: FloatArray,
    magnetThreshold: Float,
): Float {
    val f = fraction.coerceIn(0f, 1f)
    val base = lerp(valueRange.start, valueRange.endInclusive, f)
    return when {
        steps > 0 -> {
            val stepCount = steps + 1
            val start = valueRange.start.toDouble()
            val end = valueRange.endInclusive.toDouble()
            val stepIndex = (f * stepCount).fastRoundToInt().coerceIn(0, stepCount)
            (start + (end - start) * stepIndex / stepCount).toFloat()
        }

        allKeyPointFractions.isNotEmpty() -> {
            var closest = allKeyPointFractions[0]
            var bestDist = abs(closest - f)
            for (i in 1 until allKeyPointFractions.size) {
                val cand = allKeyPointFractions[i]
                val dist = abs(cand - f)
                if (dist < bestDist) {
                    bestDist = dist
                    closest = cand
                }
            }
            if (bestDist < magnetThreshold) {
                lerp(valueRange.start, valueRange.endInclusive, closest)
            } else {
                base
            }
        }

        else -> base
    }
}

private val ThumbScaleAnimationSpec = spring<Float>(dampingRatio = 0.75f, stiffness = 450f)

private fun horizontalVisualFraction(offsetX: Float, sizeWidth: Int, sizeHeight: Int): Float {
    val thumbRadius = sizeHeight / 2f
    val availableWidth = (sizeWidth.toFloat() - 2f * thumbRadius).coerceAtLeast(0f)
    return if (availableWidth == 0f) 0f else ((offsetX - thumbRadius) / availableWidth).coerceIn(0f, 1f)
}

private fun verticalVisualFraction(offsetY: Float, sizeHeight: Int, sizeWidth: Int): Float {
    val thumbRadius = sizeWidth / 2f
    val availableHeight = (sizeHeight.toFloat() - 2f * thumbRadius).coerceAtLeast(0f)
    return if (availableHeight == 0f) 0f else ((offsetY - thumbRadius) / availableHeight).coerceIn(0f, 1f)
}

private fun pointsToFractions(
    points: List<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
): FloatArray = points.map { point ->
    ((point - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)
}.toFloatArray()

private fun computeKeyPointFractions(
    keyPoints: List<Float>?,
    stepFractions: FloatArray,
    valueRange: ClosedFloatingPointRange<Float>,
    showKeyPoints: Boolean,
): FloatArray = when {
    keyPoints != null -> pointsToFractions(keyPoints, valueRange)
    showKeyPoints -> stepFractions
    else -> floatArrayOf()
}

private fun computeAllKeyPointFractions(
    keyPoints: List<Float>?,
    stepFractions: FloatArray,
    valueRange: ClosedFloatingPointRange<Float>,
): FloatArray = when {
    keyPoints != null -> pointsToFractions(keyPoints, valueRange)
    stepFractions.isNotEmpty() -> stepFractions
    else -> floatArrayOf()
}

object SliderDefaults {
    val MinHeight = 36.dp
    val CapsuleTrackHeight = 20.dp
    val IntentTrackHeight = 4.dp
    val ThumbRadius = 6.dp
    val ThumbMaxRadius = 8.dp
    val ThumbInScaleRadius = 3.67.dp
    val ThumbShadowSize = 2.dp
    val KeyPointRadius = 3.855.dp

    enum class SliderHapticEffect {
        None,
        Edge,
        Step,
    }

    val DefaultHapticEffect = SliderHapticEffect.Edge

    @Composable
    fun sliderColors(
        foregroundColor: Color = RmuixTheme.colorScheme.primary,
        disabledForegroundColor: Color = RmuixTheme.colorScheme.primary.copy(alpha = 0.24f),
        backgroundColor: Color = if (isSystemInDarkTheme()) Color(0x33FFFFFF) else Color(0x1F000000),
        disabledBackgroundColor: Color = if (isSystemInDarkTheme()) Color(0x26FFFFFF) else Color(0x14000000),
        thumbColor: Color = Color.White,
        disabledThumbColor: Color = Color.White.copy(alpha = 0.6f),
        thumbShadowColor: Color = Color(0x1A000000),
        glitterMinColor: Color = Color(0x1AFFFFFF),
        glitterMaxColor: Color = Color(0x4DFFFFFF),
        keyPointColor: Color = RmuixTheme.colorScheme.sliderKeyPoint,
        keyPointForegroundColor: Color = RmuixTheme.colorScheme.sliderKeyPointForeground,
    ): SliderColors = remember(
        foregroundColor,
        disabledForegroundColor,
        backgroundColor,
        disabledBackgroundColor,
        thumbColor,
        disabledThumbColor,
        thumbShadowColor,
        glitterMinColor,
        glitterMaxColor,
        keyPointColor,
        keyPointForegroundColor,
    ) {
        SliderColors(
            foregroundColor = foregroundColor,
            disabledForegroundColor = disabledForegroundColor,
            backgroundColor = backgroundColor,
            disabledBackgroundColor = disabledBackgroundColor,
            thumbColor = thumbColor,
            disabledThumbColor = disabledThumbColor,
            thumbShadowColor = thumbShadowColor,
            glitterMinColor = glitterMinColor,
            glitterMaxColor = glitterMaxColor,
            keyPointColor = keyPointColor,
            keyPointForegroundColor = keyPointForegroundColor,
        )
    }
}

@Immutable
data class SliderColors(
    private val foregroundColor: Color,
    private val disabledForegroundColor: Color,
    private val backgroundColor: Color,
    private val disabledBackgroundColor: Color,
    private val thumbColor: Color,
    private val disabledThumbColor: Color,
    private val thumbShadowColor: Color,
    private val glitterMinColor: Color,
    private val glitterMaxColor: Color,
    private val keyPointColor: Color,
    private val keyPointForegroundColor: Color,
) {
    @Stable
    internal fun foregroundColor(enabled: Boolean): Color = if (enabled) foregroundColor else disabledForegroundColor

    @Stable
    internal fun backgroundColor(enabled: Boolean): Color = if (enabled) backgroundColor else disabledBackgroundColor

    @Stable
    internal fun thumbColor(enabled: Boolean): Color = if (enabled) thumbColor else disabledThumbColor

    @Stable
    internal fun thumbShadowColor(enabled: Boolean): Color = if (enabled) thumbShadowColor else Color.Transparent

    @Stable
    internal fun glitterMinColor(): Color = glitterMinColor

    @Stable
    internal fun glitterMaxColor(): Color = glitterMaxColor

    @Stable
    internal fun keyPointColor(): Color = keyPointColor

    @Stable
    internal fun keyPointForegroundColor(): Color = keyPointForegroundColor
}
