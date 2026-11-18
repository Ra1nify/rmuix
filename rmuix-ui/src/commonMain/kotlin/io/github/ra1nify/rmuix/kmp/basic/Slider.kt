// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.annotation.IntRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.shader.SpotlightStyle
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.roundToInt

/**
 * Slider based on the ColorOS COUISeekBar resources and implementation.
 *
 * The background grows from 20dp to 28dp while progress stays 20dp high.
 * Thumb travel uses a fixed 14dp inset, independent of the animated background.
 * Steps and key points remain extensions pending a separate COUISectionSeekBar review.
 * Material and gesture parity remain under review.
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
    showGlitterEffect: Boolean = false,
    showSpotLight: Boolean = true,
    showKeyPoints: Boolean = false,
    keyPoints: List<Float>? = null,
    magnetThreshold: Float = 0.02f,
    progressRange: kotlin.ranges.IntRange? = null,
    thumbTextureEnabled: Boolean = false,
    intentSize: SliderDefaults.IntentSize? = null,
    showValueLabel: Boolean = false,
) {
    require(steps >= 0) { "steps should be >= 0" }
    require(valueRange.start < valueRange.endInclusive) { "valueRange start should be less than end" }

    require(progressRange == null || (progressRange.first >= 0 && progressRange.last > progressRange.first)) {
        "progressRange must have a non-negative start and a greater end"
    }
    val progressMapping = remember(valueRange, progressRange) { SliderProgressMapping(valueRange, progressRange) }

    val hapticFeedback = LocalHapticFeedback.current
    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl
    val effectiveReverse = if (isRtl) !reverseDirection else reverseDirection
    // `GlitterEffectRenderer` is velocity driven: every drag re-seeds a spring whose start value is the
    // current driver, whose target is 0, and whose initial velocity is |dragVelocity|. `response` 0.6 and
    // `bounce` 0 are the stock spring constants.
    val glitterDriver = remember { Animatable(0f) }
    val glitterScope = rememberCoroutineScope()
    val glitterSpring =
        remember {
            val stiffness = ((2 * PI / SliderDefaults.GlitterEffectResponse) * (2 * PI / SliderDefaults.GlitterEffectResponse)).toFloat()
            spring<Float>(dampingRatio = 1f, stiffness = stiffness)
        }

    val hapticState = remember { SliderHapticState() }
    val interactionSource = remember { MutableInteractionSource() }

    val coercedValue = progressMapping.valueAtProgress(value, round = true)
    val motion =
        rememberSliderMotion(
            coercedValue,
            valueRange,
            enabled,
            effectiveReverse,
            trackHeight,
            progressRange,
            steps == 0 && keyPoints.isNullOrEmpty(),
            onValueChange,
            onValueChangeFinished,
        )
    val isInteracting = enabled && (motion.isDragging || motion.isPressed)
    // `COUISeekBar` draws its value label with a `TextDrawable` while the thumb is held
    // (`setValueForLabel()`); the label box is `ValueLabelWidthPx` wide in **raw pixels** and
    // `ValueLabelHeight` tall, sitting `ValueLabelMarginBottom` above the track.
    val valueLabelMeasurer = rememberTextMeasurer()
    val valueLabelStyle =
        remember {
            TextStyle(fontSize = SliderDefaults.ValueLabelTextSize, textAlign = TextAlign.End)
        }
    // `coui_seekbar_progress_{,pressed_}padding_horizontal`: 14dp at rest, 4dp while dragging.
    val animatedPadding by
        animateDpAsState(
            targetValue = if (isInteracting) SLIDER_PRESSED_PADDING_HORIZONTAL else SLIDER_PADDING_HORIZONTAL,
            animationSpec = spring(stiffness = SliderHeightStiffness),
            label = "SliderTrackPadding",
        )

    val restingThumbRadius = intentSize?.thumbRadius ?: SliderDefaults.ThumbRestingRadius
    val expandedThumbRadius = intentSize?.thumbMaxRadius ?: SliderDefaults.ThumbExpandedRadius
    val animatedThumbRadius by animateDpAsState(
        targetValue = if (isInteracting) expandedThumbRadius else restingThumbRadius,
        // `COUISeekBarCore.initEnlargeAnim()` builds its enlarge spring with bounce 0, i.e. a damping
        // ratio of 1 in both directions; the old 0.7 while dragging made the thumb overshoot.
        animationSpec = spring(dampingRatio = 1f, stiffness = SliderHeightStiffness),
        label = "SliderThumbRadius",
    )

    val resolvedTrackHeight = intentSize?.trackHeight ?: trackHeight
    val resolvedMaxTrackHeight = intentSize?.maxBackgroundHeight
    val animatedTrackHeight = rememberSliderBackgroundHeight(resolvedTrackHeight, isInteracting, resolvedMaxTrackHeight)
    val thumbRenderer = rememberSliderThumbRenderer()
    val thumbAnimation = rememberSliderThumbAnimation(thumbTextureEnabled, isInteracting, enabled)
    val spotlightShape =
        if (showSpotLight && thumbTextureEnabled) {
            remember(trackHeight, animatedTrackHeight) { SliderTrackSpotlightShape(trackHeight, animatedTrackHeight) }
        } else {
            null
        }

    val stepFractions = remember(steps) { stepsToTickFractions(steps) }
    val keyPointFractions =
        remember(keyPoints, stepFractions, valueRange, showKeyPoints) {
            computeKeyPointFractions(keyPoints, stepFractions, valueRange, showKeyPoints)
        }
    val allKeyPointFractions =
        remember(keyPoints, stepFractions, valueRange) {
            computeAllKeyPointFractions(keyPoints, stepFractions, valueRange)
        }

    val fractionToValue =
        remember(valueRange, steps, stepFractions, allKeyPointFractions, magnetThreshold) {
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

    val currentFractionToValue by rememberUpdatedState(fractionToValue)
    val currentKeyPointFractions by rememberUpdatedState(allKeyPointFractions)
    val currentHapticEffect by rememberUpdatedState(hapticEffect)
    val currentCoercedValue by rememberUpdatedState(coercedValue)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                // The horizontal slider is capped at 480dp (the same-named dimen is never referenced).
                .widthIn(max = SliderDefaults.MaxWidth)
                .height(height)
                .hoverable(interactionSource = interactionSource, enabled = enabled)
                .rmuixSpotlight(
                    shape = spotlightShape,
                    enabled = enabled && showSpotLight && thumbTextureEnabled,
                    style = SpotlightStyle.OpaqueMedium,
                    darkTheme = isSliderInDarkTheme(),
                ).pointerInput(enabled, valueRange, effectiveReverse, trackHeight, motion) {
                    if (!enabled) return@pointerInput
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        if (down.isConsumed) return@awaitEachGesture
                        val velocityTracker = SliderVelocityTracker()
                        try {
                            velocityTracker.sample(currentEvent)
                            motion.press()
                            while (true) {
                                val event = awaitPointerEvent()
                                if (!motion.isPressed) break
                                val change = velocityTracker.sample(event) ?: break
                                if (showGlitterEffect) {
                                    val dragVelocity = velocityTracker.calculateVelocity()
                                    if (dragVelocity != 0f) {
                                        glitterScope.launch {
                                            glitterDriver.animateTo(
                                                targetValue = 0f,
                                                animationSpec = glitterSpring,
                                                initialVelocity = abs(dragVelocity),
                                            )
                                        }
                                    }
                                }
                                if (sliderIgnoresPointerChange(event.type, event.changes.any { it.pressed })) continue
                                val geometry =
                                    sliderTrackGeometry(
                                        width = size.width.toFloat(),
                                        trackHeight = trackHeight.toPx(),
                                        backgroundHeight = trackHeight.toPx(),
                                        // Hit-testing uses the *target* padding for the current gesture state - `SliderMotion`'s
                                        // `isPressed` is a `mutableStateOf`, so this read is always current - while drawing uses the
                                        // animated value so the ends glide between 14dp and 4dp.
                                        // Capsule: `COUISeekBarCore` derives a *constant* inset from the
                                        // base track height, so pass null; intent bars pass the absolute
                                        // 14dp that `COUIIntentSeekBar` reads.
                                        padding = intentSize?.let { it.trackHeight.toPx() },
                                    )
                                val target = currentFractionToValue(geometry.fraction(change.position.x, effectiveReverse))
                                if (change.isConsumed) {
                                    if (motion.isDragging) {
                                        motion.endDrag(target, geometry.length, velocityTracker.calculateVelocity())
                                    } else {
                                        motion.click(target, geometry.length)
                                    }
                                    break
                                }
                                if (!change.pressed) {
                                    if (motion.isDragging) {
                                        motion.endDrag(target, geometry.length, velocityTracker.calculateVelocity())
                                    } else if (
                                        change.position.x in 0f..size.width.toFloat() &&
                                        change.position.y in 0f..size.height.toFloat()
                                    ) {
                                        motion.click(target, geometry.length)
                                    }
                                    change.consume()
                                    break
                                }
                                if (!motion.isDragging &&
                                    sliderStartsHorizontalDrag(change.position.x - down.position.x, viewConfiguration.touchSlop)
                                ) {
                                    motion.beginDrag()
                                    hapticState.reset(currentCoercedValue, valueRange)
                                }
                                if (motion.isDragging) {
                                    motion.dragTo(
                                        target,
                                        geometry.length,
                                        geometry.rawFraction(change.position.x, effectiveReverse),
                                    )
                                    hapticState.handleHapticFeedback(
                                        motion.reportedValue,
                                        valueRange,
                                        currentHapticEffect,
                                        hapticFeedback,
                                        currentKeyPointFractions,
                                    )
                                    change.consume()
                                } else {
                                    val finalEvent = awaitPointerEvent(PointerEventPass.Final)
                                    val finalChange = finalEvent.changes.firstOrNull { it.id == change.id }
                                    if (finalChange?.isConsumed == true) {
                                        motion.click(target, geometry.length)
                                        break
                                    }
                                }
                            }
                        } finally {
                            velocityTracker.recycle()
                            motion.cancelGesture()
                        }
                    }
                }.semantics {
                    progressBarRangeInfo =
                        ProgressBarRangeInfo(
                            motion.accessibilityValue.coerceIn(valueRange),
                            valueRange.start..valueRange.endInclusive,
                            steps,
                        )
                    if (!enabled) {
                        disabled()
                    } else {
                        setProgress { target ->
                            if (target.isNaN()) return@setProgress false
                            val fraction = (target - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                            val resolved = currentFractionToValue(fraction.coerceIn(0f, 1f))
                            if (resolved == motion.value && resolved == currentCoercedValue) return@setProgress false
                            motion.setProgress(resolved)
                            true
                        }
                    }
                },
        contentAlignment = Alignment.CenterStart,
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
            val trackH = trackHeight.toPx()
            val trackRadius = trackH / 2f
            val centerY = (size.height.toInt() / 2).toFloat()
            val trackY = centerY - trackRadius
            val backgroundH =
                sliderPaintedBackgroundHeight(
                    animatedTrackHeight.toPx(),
                    trackH,
                    (trackHeight * SLIDER_BACKGROUND_ENLARGE_SCALE).toPx(),
                )
            val geometry =
                sliderTrackGeometry(
                    width = size.width,
                    trackHeight = trackH,
                    backgroundHeight = backgroundH,
                    padding = if (intentSize != null) animatedPadding.toPx() else null,
                )
            val thumbR = animatedThumbRadius.toPx()

            val minThumbX = geometry.start
            val travelWidth = geometry.length

            val rangeSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)
            val valueFrac = ((motion.value - valueRange.start) / rangeSpan).coerceIn(0f, 1f)
            val thumbX = geometry.position(valueFrac, effectiveReverse)

            drawRoundRect(
                color = colors.backgroundColor(enabled),
                topLeft = Offset(geometry.backgroundStart, centerY - backgroundH / 2f),
                size = Size((geometry.backgroundEnd - geometry.backgroundStart).coerceAtLeast(0f), backgroundH),
                cornerRadius = CornerRadius(backgroundH / 2f),
            )

            val fgLeft = (if (effectiveReverse) thumbX else geometry.start) - trackRadius
            val fgRight = (if (effectiveReverse) geometry.start + geometry.length else thumbX) + trackRadius
            val fgWidth = (fgRight - fgLeft).coerceAtLeast(0f)

            if (fgWidth > 0f) {
                drawRoundRect(
                    color = colors.foregroundColor(enabled),
                    topLeft = Offset(fgLeft, trackY),
                    size = Size(fgWidth, trackH),
                    cornerRadius = CornerRadius(trackRadius, trackRadius),
                )
            }

            if (showGlitterEffect && fgWidth > 0f) {
                // Official: the paint alpha is the curve value while the shader carries its own alpha
                // (`#00FFFFFF` -> `#A0FFFFFF`), so both multiply. Direction follows RTL, like
                // `GlitterEffectRenderer.updateGradient()`.
                val glitterAlpha = SliderDefaults.glitterEffectAlpha(glitterDriver.value)
                if (glitterAlpha > 0.001f) {
                    val glitterBrush =
                        Brush.horizontalGradient(
                            colors =
                                if (effectiveReverse) {
                                    listOf(colors.glitterMaxColor(), colors.glitterMinColor())
                                } else {
                                    listOf(colors.glitterMinColor(), colors.glitterMaxColor())
                                },
                            startX = fgLeft,
                            endX = fgRight,
                        )
                    drawRoundRect(
                        brush = glitterBrush,
                        topLeft = Offset(fgLeft, trackY),
                        size = Size(fgWidth, trackH),
                        cornerRadius = CornerRadius(trackRadius, trackRadius),
                        alpha = glitterAlpha,
                    )
                }
            }

            if (showKeyPoints && keyPointFractions.isNotEmpty()) {
                val tickRadius = SliderDefaults.TickMarkRadius.toPx()
                for (stepFrac in keyPointFractions) {
                    val effStep = if (effectiveReverse) 1f - stepFrac else stepFrac
                    val tickX = minThumbX + effStep * travelWidth
                    // Stock hides the marks the thumb covers; drawing them would show through the shadow.
                    if (!sliderSectionMarkIsVisible(tickX, thumbX, thumbR, tickRadius)) continue
                    val isSelected = if (effectiveReverse) tickX >= thumbX else tickX <= thumbX
                    val tickColor = if (isSelected) colors.keyPointForegroundColor(enabled) else colors.keyPointColor(enabled)
                    drawCircle(color = tickColor, radius = tickRadius, center = Offset(tickX, centerY))
                }
            }

            thumbRenderer.draw(
                this,
                SliderThumbFrame(
                    center = Offset(thumbX, centerY),
                    radius = thumbR,
                    enabled = enabled,
                    color = colors.thumbColor(enabled),
                    shadowColor = colors.thumbShadowColor(enabled),
                    shadowRadius = sliderThumbShadowRadius(density),
                    shadowOffset = SliderThumbValues.SHADOW_OFFSET_DP * density,
                    textureEnabled = thumbAnimation.enabled,
                    textureAlpha = thumbAnimation.texture.value.coerceIn(0f, 1f),
                    coverAlpha = thumbAnimation.cover.value.coerceIn(0f, 1f),
                ),
            )

            if (showValueLabel && isInteracting) {
                val labelText =
                    valueLabelMeasurer.measure(
                        text = sliderValueLabelText(motion.value),
                        style = valueLabelStyle,
                    )
                val labelWidthPx = SliderDefaults.ValueLabelWidthPx.toFloat()
                val labelLeft = (thumbX - labelWidthPx / 2f).coerceIn(0f, (size.width - labelWidthPx).coerceAtLeast(0f))
                val labelBottom = trackY - SliderDefaults.ValueLabelMarginBottom.toPx()
                drawText(
                    textLayoutResult = labelText,
                    color = colors.thumbColor(enabled),
                    topLeft = Offset(labelLeft, labelBottom - SliderDefaults.ValueLabelHeight.toPx()),
                )
            }
        }
    }
}

/**
 * Vertical slider extension; parity with COUIVerticalSeekBar remains under review.
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
    // Declared since the first `VerticalSlider` port and **never read** - passing it has no effect.
    // Kept (not removed) so existing call sites keep compiling; Kotlin cannot deprecate a value parameter.
    effect: Boolean = false,
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.DefaultHapticEffect,
    showSpotLight: Boolean = true,
    showKeyPoints: Boolean = false,
    keyPoints: List<Float>? = null,
    magnetThreshold: Float = 0.02f,
    intentSize: SliderDefaults.IntentSize? = null,
) {
    require(steps >= 0) { "steps should be >= 0" }
    require(valueRange.start < valueRange.endInclusive) { "valueRange start should be less than end" }

    val hapticFeedback = LocalHapticFeedback.current
    val onValueChangeState by rememberUpdatedState(onValueChange)
    val onValueChangeFinishedState by rememberUpdatedState(onValueChangeFinished)

    var isDragging by remember { mutableStateOf(false) }
    val hapticState = remember { SliderHapticState() }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val coercedValue = value.coerceIn(valueRange.start, valueRange.endInclusive)
    val isInteracting = isDragging || isPressed

    val restingThumbRadius = intentSize?.thumbRadius ?: SliderDefaults.ThumbRestingRadius
    val expandedThumbRadius = intentSize?.thumbMaxRadius ?: SliderDefaults.ThumbExpandedRadius
    val animatedThumbRadius by animateDpAsState(
        targetValue = if (isInteracting) expandedThumbRadius else restingThumbRadius,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "VerticalSliderThumbRadius",
    )

    val resolvedTrackWidth = intentSize?.trackHeight ?: trackWidth
    val resolvedMaxTrackWidth = intentSize?.maxBackgroundHeight
    val animatedTrackWidth by animateDpAsState(
        targetValue =
            if (isInteracting) {
                resolvedMaxTrackWidth ?: (resolvedTrackWidth * 1.2f)
            } else {
                resolvedTrackWidth
            },
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow),
        label = "VerticalSliderTrackWidth",
    )

    val animatedValueState =
        animateFloatAsState(
            targetValue = coercedValue,
            animationSpec =
                if (isDragging) {
                    spring(
                        dampingRatio = 0.9f,
                        stiffness = 1755f,
                    )
                } else {
                    spring(dampingRatio = 0.96f, stiffness = 322f)
                },
            label = "VerticalSliderAnimatedValue",
        )

    val stepFractions = remember(steps) { stepsToTickFractions(steps) }
    val keyPointFractions =
        remember(keyPoints, stepFractions, valueRange, showKeyPoints) {
            computeKeyPointFractions(keyPoints, stepFractions, valueRange, showKeyPoints)
        }
    val allKeyPointFractions =
        remember(keyPoints, stepFractions, valueRange) {
            computeAllKeyPointFractions(keyPoints, stepFractions, valueRange)
        }

    val fractionToValue =
        remember(valueRange, steps, stepFractions, allKeyPointFractions, magnetThreshold) {
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

    Box(
        modifier =
            modifier
                .width(width)
                // The vertical slider is capped at 480dp (the same-named dimen is never referenced).
                .heightIn(max = SliderDefaults.MaxHeight)
                .fillMaxHeight()
                .hoverable(interactionSource = interactionSource, enabled = enabled)
                .pointerInput(enabled, valueRange, reverseDirection, trackWidth) {
                    if (!enabled) return@pointerInput
                    detectTapGestures { offset ->
                        val trackRadius = (trackWidth / 2f).toPx()
                        // `coui_vertical_seekbar_progress_padding_vertical`: 10dp at rest, 4dp while dragged -
                        // an absolute value, not the track radius.
                        val verticalPadding =
                            if (isDragging || isPressed) {
                                SLIDER_PRESSED_PADDING_VERTICAL.toPx()
                            } else {
                                SLIDER_PADDING_VERTICAL.toPx()
                            }
                        val minY = verticalPadding
                        val maxY = (size.height - verticalPadding).coerceAtLeast(minY + 1f)
                        val travel = (maxY - minY).coerceAtLeast(1f)
                        val rawFrac = ((offset.y - minY) / travel).coerceIn(0f, 1f)
                        val visualFrac = if (reverseDirection) rawFrac else 1f - rawFrac
                        val targetVal = fractionToValue(visualFrac)
                        onValueChangeState(targetVal)
                        hapticState.handleHapticFeedback(targetVal, valueRange, hapticEffect, hapticFeedback, allKeyPointFractions)
                        onValueChangeFinishedState?.invoke()
                    }
                }.pointerInput(enabled, valueRange, reverseDirection, trackWidth) {
                    if (!enabled) return@pointerInput
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            val trackRadius = (trackWidth / 2f).toPx()
                            // `coui_vertical_seekbar_progress_padding_vertical`: 10dp at rest, 4dp while dragged -
                            // an absolute value, not the track radius.
                            val verticalPadding =
                                if (isDragging || isPressed) {
                                    SLIDER_PRESSED_PADDING_VERTICAL.toPx()
                                } else {
                                    SLIDER_PADDING_VERTICAL.toPx()
                                }
                            val minY = verticalPadding
                            val maxY = (size.height - verticalPadding).coerceAtLeast(minY + 1f)
                            val travel = (maxY - minY).coerceAtLeast(1f)
                            val rawFrac = ((offset.y - minY) / travel).coerceIn(0f, 1f)
                            val visualFrac = if (reverseDirection) rawFrac else 1f - rawFrac
                            val targetVal = fractionToValue(visualFrac)
                            onValueChangeState(targetVal)
                            hapticState.reset(targetVal, valueRange, coercedValue)
                        },
                        onDragEnd = {
                            isDragging = false
                            onValueChangeFinishedState?.invoke()
                        },
                        onDragCancel = {
                            isDragging = false
                            onValueChangeFinishedState?.invoke()
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val trackRadius = (trackWidth / 2f).toPx()
                            // `coui_vertical_seekbar_progress_padding_vertical`: 10dp at rest, 4dp while dragged -
                            // an absolute value, not the track radius.
                            val verticalPadding =
                                if (isDragging || isPressed) {
                                    SLIDER_PRESSED_PADDING_VERTICAL.toPx()
                                } else {
                                    SLIDER_PADDING_VERTICAL.toPx()
                                }
                            val minY = verticalPadding
                            val maxY = (size.height - verticalPadding).coerceAtLeast(minY + 1f)
                            val travel = (maxY - minY).coerceAtLeast(1f)
                            val rawFrac = ((change.position.y - minY) / travel).coerceIn(0f, 1f)
                            val visualFrac = if (reverseDirection) rawFrac else 1f - rawFrac
                            val targetVal = fractionToValue(visualFrac)
                            onValueChangeState(targetVal)
                            hapticState.handleHapticFeedback(targetVal, valueRange, hapticEffect, hapticFeedback, allKeyPointFractions)
                        },
                    )
                }.semantics {
                    progressBarRangeInfo = ProgressBarRangeInfo(coercedValue, valueRange.start..valueRange.endInclusive, steps)
                    setProgress { target ->
                        onValueChangeState(target.coerceIn(valueRange.start, valueRange.endInclusive))
                        true
                    }
                },
        contentAlignment = Alignment.BottomCenter,
    ) {
        Canvas(modifier = Modifier.width(width).fillMaxHeight().clip(CircleShape)) {
            val trackW = animatedTrackWidth.toPx()
            val trackRadius = trackW / 2f
            val trackX = (size.width - trackW) / 2f
            val thumbR = animatedThumbRadius.toPx()

            val minThumbY = trackRadius
            val maxThumbY = size.height - trackRadius
            val travelHeight = (maxThumbY - minThumbY).coerceAtLeast(1f)

            val rangeSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)
            val valueFrac = ((animatedValueState.value - valueRange.start) / rangeSpan).coerceIn(0f, 1f)
            val effFrac = if (reverseDirection) valueFrac else 1f - valueFrac

            val thumbY = minThumbY + effFrac * travelHeight

            // 1. Inactive Background Track
            drawRoundRect(
                color = colors.backgroundColor(enabled),
                topLeft = Offset(trackX, 0f),
                size = Size(trackW, size.height),
                cornerRadius = CornerRadius(trackRadius, trackRadius),
            )

            // 2. Active Foreground Track
            val fgTop = if (reverseDirection) 0f else (thumbY - trackRadius).coerceAtLeast(0f)
            val fgBottom = if (reverseDirection) (thumbY + trackRadius).coerceAtMost(size.height) else size.height
            val fgHeight = (fgBottom - fgTop).coerceAtLeast(0f)

            if (fgHeight > 0f) {
                drawRoundRect(
                    color = colors.foregroundColor(enabled),
                    topLeft = Offset(trackX, fgTop),
                    size = Size(trackW, fgHeight),
                    cornerRadius = CornerRadius(trackRadius, trackRadius),
                )
            }

            // 3. Key Points / Tick Marks

            if (showKeyPoints && keyPointFractions.isNotEmpty()) {
                val tickRadius = SliderDefaults.TickMarkRadius.toPx()
                for (stepFrac in keyPointFractions) {
                    val effStep = if (reverseDirection) stepFrac else 1f - stepFrac
                    val tickY = minThumbY + effStep * travelHeight
                    if (!sliderSectionMarkIsVisible(tickY, thumbY, thumbR, tickRadius)) continue
                    val isSelected = if (reverseDirection) tickY <= thumbY else tickY >= thumbY
                    val tickColor = if (isSelected) colors.keyPointForegroundColor(enabled) else colors.keyPointColor(enabled)
                    drawCircle(color = tickColor, radius = tickRadius, center = Offset(size.width / 2f, tickY))
                }
            }

            // 4. Solid White Thumb
            val center = Offset(size.width / 2f, thumbY)
            if (showSpotLight) {
                drawCircle(
                    color = colors.thumbShadowColor(enabled),
                    radius = thumbR + 1.5.dp.toPx(),
                    center =
                        center.copy(
                            y =
                                center.y + 1.5.dp.toPx(),
                        ),
                )
            }
            drawCircle(color = colors.thumbColor(enabled), radius = thumbR, center = center)
        }
    }
}

internal enum class RangeDragMode { None, Start, End, Track }

/** How close (in px) a touch has to be to a thumb to grab that thumb instead of the track. */
private val RangeSliderTouchRadius = 24.dp

/**
 * Which thumb - if any - a press picks up, mirroring roui's `RouiRangeSlider`:
 * within [touchRadius] of a thumb picks that thumb, a press **between** the thumbs drags the whole range,
 * and a press **outside** the range grabs the nearest thumb.
 */
internal fun rangeDragModeFor(
    offsetX: Float,
    startThumbX: Float,
    endThumbX: Float,
    touchRadius: Float,
): RangeDragMode {
    val distanceToStart = abs(offsetX - startThumbX)
    val distanceToEnd = abs(offsetX - endThumbX)
    val windowStart = minOf(startThumbX, endThumbX)
    val windowEnd = maxOf(startThumbX, endThumbX)
    return when {
        distanceToStart <= touchRadius && distanceToStart <= distanceToEnd -> RangeDragMode.Start
        distanceToEnd <= touchRadius && distanceToEnd < distanceToStart -> RangeDragMode.End
        offsetX in windowStart..windowEnd -> RangeDragMode.Track
        distanceToStart < distanceToEnd -> RangeDragMode.Start
        else -> RangeDragMode.End
    }
}

/** Clamps a dragged start value into the range *and* against the end thumb (`minGap` apart). */
internal fun clampRangeStart(
    candidate: Float,
    end: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    minGap: Float,
): Float = candidate.coerceIn(valueRange.start, (end - minGap).coerceAtLeast(valueRange.start))

/** Clamps a dragged end value into the range *and* against the start thumb (`minGap` apart). */
internal fun clampRangeEnd(
    candidate: Float,
    start: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    minGap: Float,
): Float = candidate.coerceIn((start + minGap).coerceAtMost(valueRange.endInclusive), valueRange.endInclusive)

/** Shifts a window of [span] from [start] into [valueRange], keeping the span intact. */
internal fun shiftRangeIntoBounds(
    start: Float,
    span: Float,
    valueRange: ClosedFloatingPointRange<Float>,
): ClosedFloatingPointRange<Float> {
    var shiftedStart = start
    var shiftedEnd = start + span
    if (shiftedStart < valueRange.start) {
        shiftedStart = valueRange.start
        shiftedEnd = shiftedStart + span
    } else if (shiftedEnd > valueRange.endInclusive) {
        shiftedEnd = valueRange.endInclusive
        shiftedStart = shiftedEnd - span
    }
    return shiftedStart.coerceAtLeast(valueRange.start)..shiftedEnd.coerceAtMost(valueRange.endInclusive)
}

/**
 * Dual-thumb range extension sharing the Slider palette.
 * A matching COUIRangeSeekBar implementation has not been established in the official dump.
 * Features:
 * - Start Thumb Dragging
 * - End Thumb Dragging
 * - Active Track Span Dragging (Touch and drag the active region to slide the whole window)
 * - Anti-Crossing Protection (`minGap`)
 * - Step & KeyPoint Snapping
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
    minGap: Float = 0f,
    height: Dp = SliderDefaults.MinHeight,
    trackHeight: Dp = SliderDefaults.CapsuleTrackHeight,
    colors: SliderColors = SliderDefaults.sliderColors(),
    hapticEffect: SliderDefaults.SliderHapticEffect = SliderDefaults.DefaultHapticEffect,
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

    var dragMode by remember { mutableStateOf(RangeDragMode.None) }
    var trackDragTouchX by remember { mutableFloatStateOf(0f) }
    var trackDragInitialRange by remember { mutableStateOf(0f..0f) }

    val hapticState = remember { RangeSliderHapticState() }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Like roui's RouiRangeSlider, the drag handlers always read the latest external value; a controlled
    // range slider that keeps its own mirror drifts from the parent's (clamped / rounded) value.
    val currentValues by rememberUpdatedState(value)

    val coercedStart = currentValues.start.coerceIn(valueRange.start, valueRange.endInclusive)
    val coercedEnd = currentValues.endInclusive.coerceIn(valueRange.start, valueRange.endInclusive)
    val isInteracting = dragMode != RangeDragMode.None || isPressed

    val restingThumbRadius = SliderDefaults.ThumbRestingRadius
    val expandedThumbRadius = SliderDefaults.ThumbExpandedRadius

    val animatedStartRadius by animateDpAsState(
        targetValue = if (isInteracting) expandedThumbRadius else restingThumbRadius,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "RangeSliderStartThumbRadius",
    )

    val animatedEndRadius by animateDpAsState(
        targetValue = if (isInteracting) expandedThumbRadius else restingThumbRadius,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "RangeSliderEndThumbRadius",
    )

    val animatedTrackHeight by animateDpAsState(
        targetValue = if (isInteracting) (trackHeight * 1.2f) else trackHeight,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow),
        label = "RangeSliderTrackHeight",
    )

    val progressSpec =
        if (dragMode !=
            RangeDragMode.None
        ) {
            spring(dampingRatio = 0.9f, stiffness = 1755f)
        } else {
            spring<Float>(dampingRatio = 0.96f, stiffness = 322f)
        }
    val animatedStartVal = animateFloatAsState(coercedStart, progressSpec, label = "RangeSliderStartVal")
    val animatedEndVal = animateFloatAsState(coercedEnd, progressSpec, label = "RangeSliderEndVal")

    val stepFractions = remember(steps) { stepsToTickFractions(steps) }
    val keyPointFractions =
        remember(keyPoints, stepFractions, valueRange, showKeyPoints) {
            computeKeyPointFractions(keyPoints, stepFractions, valueRange, showKeyPoints)
        }
    val allKeyPointFractions =
        remember(keyPoints, stepFractions, valueRange) {
            computeAllKeyPointFractions(keyPoints, stepFractions, valueRange)
        }

    val fractionToValueRange =
        remember(valueRange, steps, stepFractions, allKeyPointFractions, magnetThreshold) {
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

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(height)
                .hoverable(interactionSource = interactionSource, enabled = enabled)
                .pointerInput(enabled, valueRange, isRtl, trackHeight) {
                    if (!enabled) return@pointerInput
                    detectTapGestures { offset ->
                        val trackRadius = (trackHeight / 2f).toPx()
                        val minX = trackRadius
                        val maxX = (size.width - trackRadius).coerceAtLeast(minX + 1f)
                        val travel = (maxX - minX).coerceAtLeast(1f)
                        val rawFrac = ((offset.x - minX) / travel).coerceIn(0f, 1f)
                        val visualFrac = if (isRtl) 1f - rawFrac else rawFrac
                        val tapValue = fractionToValueRange(visualFrac)

                        val distToStart = abs(tapValue - currentValues.start)
                        val distToEnd = abs(tapValue - currentValues.endInclusive)

                        if (distToStart <= distToEnd) {
                            val newStart = clampRangeStart(tapValue, currentValues.endInclusive, valueRange, minGap)
                            onValueChangeState(newStart..currentValues.endInclusive)
                        } else {
                            val newEnd = clampRangeEnd(tapValue, currentValues.start, valueRange, minGap)
                            onValueChangeState(currentValues.start..newEnd)
                        }
                        onValueChangeFinishedState?.invoke()
                    }
                }.pointerInput(enabled, valueRange, isRtl, trackHeight) {
                    if (!enabled) return@pointerInput
                    detectDragGestures(
                        onDragStart = { offset ->
                            val trackRadius = (trackHeight / 2f).toPx()
                            val minX = trackRadius
                            val maxX = (size.width - trackRadius).coerceAtLeast(minX + 1f)
                            val travel = (maxX - minX).coerceAtLeast(1f)
                            val totalSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)

                            val startFrac = (coercedStart - valueRange.start) / totalSpan
                            val endFrac = (coercedEnd - valueRange.start) / totalSpan
                            val startEffFrac = if (isRtl) 1f - startFrac else startFrac
                            val endEffFrac = if (isRtl) 1f - endFrac else endFrac

                            val startX = minX + startEffFrac * travel
                            val endX = minX + endEffFrac * travel

                            dragMode = rangeDragModeFor(offset.x, startX, endX, RangeSliderTouchRadius.toPx())
                            when (dragMode) {
                                RangeDragMode.Start -> {
                                    hapticState.resetStart(coercedStart, valueRange)
                                }

                                RangeDragMode.End -> {
                                    hapticState.resetEnd(coercedEnd, valueRange)
                                }

                                RangeDragMode.Track -> {
                                    trackDragTouchX = offset.x
                                    trackDragInitialRange = coercedStart..coercedEnd
                                }

                                RangeDragMode.None -> {
                                    // Nothing to latch.
                                }
                            }
                        },
                        onDragEnd = {
                            dragMode = RangeDragMode.None
                            onValueChangeFinishedState?.invoke()
                        },
                        onDragCancel = {
                            dragMode = RangeDragMode.None
                            onValueChangeFinishedState?.invoke()
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val trackRadius = (trackHeight / 2f).toPx()
                            val minX = trackRadius
                            val maxX = (size.width - trackRadius).coerceAtLeast(minX + 1f)
                            val travel = (maxX - minX).coerceAtLeast(1f)
                            val totalSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)

                            when (dragMode) {
                                RangeDragMode.Start -> {
                                    val rawFrac = ((change.position.x - minX) / travel).coerceIn(0f, 1f)
                                    val visualFrac = if (isRtl) 1f - rawFrac else rawFrac
                                    val newStart =
                                        clampRangeStart(fractionToValueRange(visualFrac), currentValues.endInclusive, valueRange, minGap)
                                    if (newStart != currentValues.start) {
                                        onValueChangeState(newStart..currentValues.endInclusive)
                                    }
                                    hapticState.handleStartHapticFeedback(
                                        newStart,
                                        valueRange,
                                        hapticEffect,
                                        hapticFeedback,
                                        allKeyPointFractions,
                                    )
                                }

                                RangeDragMode.End -> {
                                    val rawFrac = ((change.position.x - minX) / travel).coerceIn(0f, 1f)
                                    val visualFrac = if (isRtl) 1f - rawFrac else rawFrac
                                    val newEnd =
                                        clampRangeEnd(fractionToValueRange(visualFrac), currentValues.start, valueRange, minGap)
                                    if (newEnd != currentValues.endInclusive) {
                                        onValueChangeState(currentValues.start..newEnd)
                                    }
                                    hapticState.handleEndHapticFeedback(
                                        newEnd,
                                        valueRange,
                                        hapticEffect,
                                        hapticFeedback,
                                        allKeyPointFractions,
                                    )
                                }

                                RangeDragMode.Track -> {
                                    val rangeSpan = trackDragInitialRange.endInclusive - trackDragInitialRange.start
                                    val deltaPx = change.position.x - trackDragTouchX
                                    val deltaValue = (deltaPx / travel) * (if (isRtl) -1f else 1f) * totalSpan
                                    val shifted =
                                        shiftRangeIntoBounds(trackDragInitialRange.start + deltaValue, rangeSpan, valueRange)
                                    if (shifted.start != currentValues.start || shifted.endInclusive != currentValues.endInclusive) {
                                        onValueChangeState(shifted)
                                    }
                                    hapticState.handleTrackHapticFeedback(
                                        shifted.start,
                                        shifted.endInclusive,
                                        valueRange,
                                        hapticEffect,
                                        hapticFeedback,
                                    )
                                }

                                RangeDragMode.None -> {}
                            }
                        },
                    )
                }.semantics {
                    stateDescription = "$coercedStart-$coercedEnd"
                },
        contentAlignment = Alignment.CenterStart,
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(height).clip(CircleShape)) {
            val trackH = animatedTrackHeight.toPx()
            val trackRadius = trackH / 2f
            val trackY = (size.height - trackH) / 2f

            val startThumbRadiusPx = animatedStartRadius.toPx()
            val endThumbRadiusPx = animatedEndRadius.toPx()

            val minThumbX = trackRadius
            val maxThumbX = size.width - trackRadius
            val travelWidth = (maxThumbX - minThumbX).coerceAtLeast(1f)
            val totalSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)

            val startFrac = (animatedStartVal.value - valueRange.start) / totalSpan
            val endFrac = (animatedEndVal.value - valueRange.start) / totalSpan
            val startEffFrac = if (isRtl) 1f - startFrac else startFrac
            val endEffFrac = if (isRtl) 1f - endFrac else endFrac

            val startX = minThumbX + startEffFrac * travelWidth
            val endX = minThumbX + endEffFrac * travelWidth

            // 1. Inactive Background Capsule Track
            drawRoundRect(
                color = colors.backgroundColor(enabled),
                topLeft = Offset(0f, trackY),
                size = Size(size.width, trackH),
                cornerRadius = CornerRadius(trackRadius, trackRadius),
            )

            // 2. Active Foreground Capsule Track
            val fgLeft = if (startX <= endX) (startX - trackRadius).coerceAtLeast(0f) else (endX - trackRadius).coerceAtLeast(0f)
            val fgRight =
                if (startX <=
                    endX
                ) {
                    (endX + trackRadius).coerceAtMost(size.width)
                } else {
                    (startX + trackRadius).coerceAtMost(size.width)
                }
            val fgWidth = (fgRight - fgLeft).coerceAtLeast(0f)

            if (fgWidth > 0f) {
                drawRoundRect(
                    color = colors.foregroundColor(enabled),
                    topLeft = Offset(fgLeft, trackY),
                    size = Size(fgWidth, trackH),
                    cornerRadius = CornerRadius(trackRadius, trackRadius),
                )
            }

            // 3. Key Points / Tick Marks

            if (showKeyPoints && keyPointFractions.isNotEmpty()) {
                val tickRadius = SliderDefaults.TickMarkRadius.toPx()
                for (stepFrac in keyPointFractions) {
                    val effStep = if (isRtl) 1f - stepFrac else stepFrac
                    val tickX = minThumbX + effStep * travelWidth
                    // Same stock occlusion rule, applied to both handles.
                    if (!sliderSectionMarkIsVisible(tickX, startX, startThumbRadiusPx, tickRadius) ||
                        !sliderSectionMarkIsVisible(tickX, endX, endThumbRadiusPx, tickRadius)
                    ) {
                        continue
                    }
                    val isSelected = if (isRtl) tickX in endX..startX else tickX in startX..endX
                    val tickColor = if (isSelected) colors.keyPointForegroundColor(enabled) else colors.keyPointColor(enabled)
                    drawCircle(color = tickColor, radius = tickRadius, center = Offset(tickX, size.height / 2f))
                }
            }

            // 4. Solid White Dual Thumbs
            val startCenter = Offset(startX, size.height / 2f)
            val endCenter = Offset(endX, size.height / 2f)

            if (showSpotLight) {
                drawCircle(
                    color = colors.thumbShadowColor(enabled),
                    radius = startThumbRadiusPx + 1.5.dp.toPx(),
                    center =
                        startCenter.copy(
                            y =
                                startCenter.y + 1.5.dp.toPx(),
                        ),
                )
                drawCircle(
                    color = colors.thumbShadowColor(enabled),
                    radius = endThumbRadiusPx + 1.5.dp.toPx(),
                    center =
                        endCenter.copy(
                            y =
                                endCenter.y + 1.5.dp.toPx(),
                        ),
                )
            }

            drawCircle(color = colors.thumbColor(enabled), radius = startThumbRadiusPx, center = startCenter)
            drawCircle(color = colors.thumbColor(enabled), radius = endThumbRadiusPx, center = endCenter)
        }
    }
}

/**
 * Whether a section/tick mark is drawn: the stock `SectionProgressRenderer.drawMarkIfVisible()` skips a
 * mark whose circle overlaps the thumb's bounds (`thumbLeft > mark - r || thumbRight < mark + r`), so the
 * thumb silhouette is never broken by a mark peeking out from under it.
 */
internal fun sliderSectionMarkIsVisible(
    markCenter: Float,
    thumbCenter: Float,
    thumbRadius: Float,
    markRadius: Float,
): Boolean = abs(markCenter - thumbCenter) >= thumbRadius + markRadius

/**
 * Helper calculation functions for steps and key points.
 */
private fun stepsToTickFractions(steps: Int): FloatArray {
    if (steps == 0) return floatArrayOf()
    return FloatArray(steps + 2) { i -> i.toFloat() / (steps + 1) }
}

private fun pointsToFractions(
    points: List<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
): FloatArray =
    points
        .map { point ->
            ((point - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
        }.toFloatArray()

private fun computeKeyPointFractions(
    keyPoints: List<Float>?,
    stepFractions: FloatArray,
    valueRange: ClosedFloatingPointRange<Float>,
    showKeyPoints: Boolean,
): FloatArray =
    when {
        keyPoints != null -> pointsToFractions(keyPoints, valueRange)
        showKeyPoints -> stepFractions
        else -> floatArrayOf()
    }

private fun computeAllKeyPointFractions(
    keyPoints: List<Float>?,
    stepFractions: FloatArray,
    valueRange: ClosedFloatingPointRange<Float>,
): FloatArray =
    when {
        keyPoints != null -> pointsToFractions(keyPoints, valueRange)
        stepFractions.isNotEmpty() -> stepFractions
        else -> floatArrayOf()
    }

private fun resolveValueFromFraction(
    fraction: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    allKeyPointFractions: FloatArray,
    magnetThreshold: Float,
): Float {
    val rawValue = valueRange.start + fraction * (valueRange.endInclusive - valueRange.start)

    if (allKeyPointFractions.isNotEmpty()) {
        val totalRange = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)
        for (kpFrac in allKeyPointFractions) {
            if (abs(fraction - kpFrac) <= magnetThreshold) {
                return valueRange.start + kpFrac * totalRange
            }
        }
    }

    if (steps > 0) {
        val stepSize = (valueRange.endInclusive - valueRange.start) / (steps + 1)
        val stepIndex = ((rawValue - valueRange.start) / stepSize).roundToInt()
        return (valueRange.start + stepIndex * stepSize).coerceIn(valueRange.start, valueRange.endInclusive)
    }

    return rawValue.coerceIn(valueRange.start, valueRange.endInclusive)
}

/**
 * Haptic feedback for a single slider, modelled on ColorOS' `VibrationFeedbackHandler`:
 *
 * - reaching either end of `valueRange` fires the heavier [HapticFeedbackType.GestureThresholdActivate]
 *   once per arrival (stock: `HapticFeedbackConstants` 306, or waveform 154 on a linear motor);
 * - [SliderDefaults.SliderHapticEffect.Continuous] fires [HapticFeedbackType.SegmentFrequentTick] for
 *   every value that actually changes while dragging (stock: 305, or waveform 152);
 * - [SliderDefaults.SliderHapticEffect.Step] fires [HapticFeedbackType.SegmentTick] once per key point
 *   crossing (stock's section style: 308 / 302 / waveform 0);
 * - values that did not change are ignored, like `mLastRealProgress` in the stock handler.
 */
@Stable
internal class SliderHapticState {
    private var lastValue: Float? = null
    private var edgeFeedbackTriggered: Boolean = false

    fun reset(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        previousValue: Float = currentValue,
    ) {
        val isAtEdge = currentValue <= valueRange.start || currentValue >= valueRange.endInclusive
        edgeFeedbackTriggered = isAtEdge && previousValue == currentValue
        lastValue = null
    }

    fun handleHapticFeedback(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticEffect: SliderDefaults.SliderHapticEffect,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray = floatArrayOf(),
    ) {
        if (hapticEffect == SliderDefaults.SliderHapticEffect.None) {
            lastValue = currentValue
            return
        }

        val changed = lastValue == null || lastValue != currentValue
        val atEdge = currentValue <= valueRange.start || currentValue >= valueRange.endInclusive

        if (atEdge) {
            if (!edgeFeedbackTriggered) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                edgeFeedbackTriggered = true
            }
        } else {
            edgeFeedbackTriggered = false
        }

        if (changed && !atEdge) {
            when (hapticEffect) {
                SliderDefaults.SliderHapticEffect.Continuous -> {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                }

                SliderDefaults.SliderHapticEffect.Step -> {
                    if (crossedKeyPoint(lastValue, currentValue, valueRange, keyPointFractions)) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    }
                }

                SliderDefaults.SliderHapticEffect.Edge, SliderDefaults.SliderHapticEffect.None -> {
                    // The edge feedback above already covers this branch.
                }
            }
        }

        lastValue = currentValue
    }

    private fun crossedKeyPoint(
        previousValue: Float?,
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        keyPointFractions: FloatArray,
    ): Boolean {
        if (previousValue == null) return true
        val span = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)
        val previousFraction = (previousValue - valueRange.start) / span
        val currentFraction = (currentValue - valueRange.start) / span
        val low = minOf(previousFraction, currentFraction)
        val high = maxOf(previousFraction, currentFraction)
        if (high - low < 0.0001f) return false
        if (keyPointFractions.isEmpty()) return true
        return keyPointFractions.any { it > low && it <= high }
    }
}

/**
 * Haptic feedback for [RangeSlider]: one [SliderHapticState] per thumb plus one for the whole-range drag,
 * so each thumb keeps its own "already at the edge" latch.
 */
@Stable
internal class RangeSliderHapticState {
    private val startState = SliderHapticState()
    private val endState = SliderHapticState()
    private var lastTrackRange: Pair<Float, Float>? = null

    fun resetStart(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
    ) {
        startState.reset(currentValue, valueRange)
    }

    fun resetEnd(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
    ) {
        endState.reset(currentValue, valueRange)
    }

    fun handleStartHapticFeedback(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticEffect: SliderDefaults.SliderHapticEffect,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray,
    ) {
        startState.handleHapticFeedback(currentValue, valueRange, hapticEffect, hapticFeedback, keyPointFractions)
    }

    fun handleEndHapticFeedback(
        currentValue: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticEffect: SliderDefaults.SliderHapticEffect,
        hapticFeedback: HapticFeedback,
        keyPointFractions: FloatArray,
    ) {
        endState.handleHapticFeedback(currentValue, valueRange, hapticEffect, hapticFeedback, keyPointFractions)
    }

    /** The whole-range drag has no key points; it only reports that the range moved. */
    fun handleTrackHapticFeedback(
        currentStart: Float,
        currentEnd: Float,
        valueRange: ClosedFloatingPointRange<Float>,
        hapticEffect: SliderDefaults.SliderHapticEffect,
        hapticFeedback: HapticFeedback,
    ) {
        if (hapticEffect == SliderDefaults.SliderHapticEffect.None) {
            lastTrackRange = currentStart to currentEnd
            return
        }
        // Both ends of the window are "edges" for the track drag, so only the generic tick applies.
        val previous = lastTrackRange
        if (previous == null || previous.first != currentStart || previous.second != currentEnd) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        }
        lastTrackRange = currentStart to currentEnd
    }
}

object SliderDefaults {
    /** `coui_seekbar_glitter_effect_min_color`: `#00FFFFFF`. */
    val GlitterEffectMinColor = Color(0x00FFFFFF)

    /** `coui_seekbar_glitter_effect_max_color`: `#A0FFFFFF`. */
    val GlitterEffectMaxColor = Color(0xA0FFFFFF)

    /** `GlitterEffectRenderer.GLITTER_EFFECT_SPRING_RESPONSE` = 0.6. */
    val GlitterEffectResponse = 0.6f

    /** `GlitterEffectRenderer.GLITTER_LN_BASE` = 85. */
    val GlitterEffectLnBase = 85.0

    /** `GlitterEffectRenderer.FULL_ANGLE` = 360. */
    val GlitterEffectFullAngle = 360.0

    /**
     * `GlitterEffectRenderer.computeGlitterEffectAlpha(x) = round(255 * (1 - exp(-(ln 85 / 360) * x)))`,
     * normalised to 0..1 so it can be used as a draw alpha directly.
     */
    fun glitterEffectAlpha(driver: Float): Float =
        (1.0 - exp(-(ln(GlitterEffectLnBase) / GlitterEffectFullAngle) * driver.toDouble())).toFloat()

    val MinHeight = 36.dp

    /** The horizontal cap: 480dp (the same-named dimen is never referenced). */
    val MaxWidth = SLIDER_VIEW_MAX_WIDTH

    /** The vertical cap: 480dp (the same-named dimen is never referenced). */
    val MaxHeight = SLIDER_VIEW_MAX_HEIGHT

    /** `coui_seekbar_progress_padding_horizontal`: 14dp at rest. */
    val TrackPadding = SLIDER_PADDING_HORIZONTAL

    /** 4dp while dragging (the same-named dimen is never referenced). */
    val PressedTrackPadding = SLIDER_PRESSED_PADDING_HORIZONTAL

    /** `coui_vertical_seekbar_progress_padding_vertical`: 10dp at rest. */
    val VerticalTrackPadding = SLIDER_PADDING_VERTICAL

    /** `coui_vertical_seekbar_progress_pressed_padding_vertical`: 4dp while dragging. */
    val PressedVerticalTrackPadding = SLIDER_PRESSED_PADDING_VERTICAL

    /**
     * Intrinsic width of the value label box: `144` **pixels** in `TextDrawable.getIntrinsicWidth()`.
     *
     * Not a dp value - the constant is mixed with pixel offsets in `draw()`. Use
     * `with(LocalDensity.current) { ValueLabelWidthPx.toDp() }` when a Dp is needed.
     */
    val ValueLabelWidthPx = SLIDER_VALUE_LABEL_WIDTH_PX

    /** `coui_seekbar_popup_text_size_small`: 12sp. */
    val ValueLabelTextSize = SLIDER_VALUE_LABEL_TEXT_SIZE

    /** `coui_seekbar_popup_text_height`: 16dp. */
    val ValueLabelHeight = SLIDER_VALUE_LABEL_HEIGHT

    /** `coui_seekbar_popup_text_margin_bottom`: 6dp. */
    val ValueLabelMarginBottom = SLIDER_VALUE_LABEL_MARGIN_BOTTOM

    /** `coui_seekbar_popup_text_padding_end`: 4dp. */
    val ValueLabelPaddingEnd = SLIDER_VALUE_LABEL_PADDING_END
    val CapsuleTrackHeight = 20.dp

    /** `coui_seekbar_intent_height_default`: the thin "intent" track is 4dp. */
    val IntentTrackHeight = 4.dp

    /**
     * The three `COUIIntentSeekBar` sizes.
     *
     * Each entry pairs the numbers the stock widget reads - `coui_seekbar_intent_height_{size}`
     * (track), `..._max_background_height_{size}` (the track while it stretches) and
     * `..._thumb_radius_{size}` / `..._thumb_max_radius_{size}` (the knob at rest and stretched):
     *
     * | size | track | max background | thumb | max thumb |
     * | --- | --- | --- | --- | --- |
     * | Small | 2dp | 10dp | 3dp | 8dp |
     * | Default | 4dp | 6dp | 4dp | 5dp |
     * | Large | 6dp | 10dp | 5dp | 8dp |
     */
    enum class IntentSize(
        val trackHeight: Dp,
        val maxBackgroundHeight: Dp,
        val thumbRadius: Dp,
        val thumbMaxRadius: Dp,
    ) {
        Small(trackHeight = 2.dp, maxBackgroundHeight = 10.dp, thumbRadius = 3.dp, thumbMaxRadius = 8.dp),
        Default(trackHeight = 4.dp, maxBackgroundHeight = 6.dp, thumbRadius = 4.dp, thumbMaxRadius = 5.dp),
        Large(trackHeight = 6.dp, maxBackgroundHeight = 10.dp, thumbRadius = 5.dp, thumbMaxRadius = 8.dp),
    }

    /** `coui_seekbar_intent_thumb_out_shade_radius`: 2dp halo around the intent thumb. */
    val IntentThumbShadeRadius = 2.dp
    val ThumbRestingRadius = 6.dp
    val ThumbExpandedRadius = 8.dp
    val ThumbInScaleRadius = 3.67.dp
    val ThumbShadowSize = 2.dp
    val TickMarkRadius = 3.dp

    enum class SliderHapticEffect {
        /** No haptic feedback. */
        None,

        /** Feedback only when a thumb reaches either end of `valueRange`. */
        Edge,

        /** One tick per key point / step crossing, plus the end feedback. */
        Step,

        /**
         * One tick for every value that changes while dragging, plus a heavier feedback when a thumb
         * reaches either end - the ColorOS seek bar model (`VibrationFeedbackHandler`: 305 while
         * dragging, 306 at the ends, 152 / 154 on a linear motor).
         */
        Continuous,
    }

    val DefaultHapticEffect = SliderHapticEffect.Continuous

    /** Default [SliderColors] for the stock slider, keyed on the current theme. */
    @Composable
    fun sliderColors(
        foregroundColor: Color = RmuixTheme.colorScheme.primary,
        disabledForegroundColor: Color = RmuixTheme.colorScheme.primary.copy(alpha = 0x4D / 255f),
        backgroundColor: Color = sliderContainer12(isSliderInDarkTheme()),
        disabledBackgroundColor: Color = sliderContainer12(isSliderInDarkTheme()),
        thumbColor: Color = Color.White,
        disabledThumbColor: Color = sliderDisabledThumb(isSliderInDarkTheme()),
        thumbShadowColor: Color = sliderThumbShadow(isSliderInDarkTheme()),
        // `coui_seekbar_glitter_effect_min_color` / `..._max_color`: #00FFFFFF / #A0FFFFFF.
        glitterMinColor: Color = Color(0x00FFFFFF),
        glitterMaxColor: Color = Color(0xA0FFFFFF),
        keyPointColor: Color = RmuixTheme.colorScheme.sliderKeyPoint,
        keyPointForegroundColor: Color = RmuixTheme.colorScheme.sliderKeyPointForeground,
        // `coui_seekbar_inactive_mark_disable_color` / `coui_seekbar_active_mark_disable_color`
        keyPointDisabledColor: Color = sliderKeyPointDisabled(isSliderInDarkTheme()),
        keyPointForegroundDisabledColor: Color = sliderKeyPointForegroundDisabled(isSliderInDarkTheme()),
    ): SliderColors =
        remember(
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
            keyPointDisabledColor,
            keyPointForegroundDisabledColor,
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
                keyPointDisabledColor = keyPointDisabledColor,
                keyPointForegroundDisabledColor = keyPointForegroundDisabledColor,
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
    private val keyPointDisabledColor: Color,
    private val keyPointForegroundDisabledColor: Color,
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
    internal fun keyPointColor(enabled: Boolean): Color = if (enabled) keyPointColor else keyPointDisabledColor

    @Stable
    internal fun keyPointForegroundColor(enabled: Boolean): Color =
        if (enabled) keyPointForegroundColor else keyPointForegroundDisabledColor
}

@Composable
private fun isSliderInDarkTheme(): Boolean = RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
