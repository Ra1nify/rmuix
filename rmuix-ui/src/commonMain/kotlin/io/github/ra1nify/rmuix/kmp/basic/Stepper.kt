// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.anim.couiSpring
import io.github.ra1nify.rmuix.kmp.utils.SinkFeedback
import io.github.ra1nify.rmuix.kmp.utils.stockPressTargetScale
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectColor
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectSpring
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * A numeric stepper modelled on ColorOS 17's `COUIStepperView` (Settings APK).
 *
 * Characteristics:
 * - Minus & Plus buttons: 32dp circular targets (`stepper_button_size`) filled with
 *   `couiColorContainer8` (`#14000000` / `#1AFFFFFF`) and **no resting border** — the source's
 *   `COUIStrokeDrawable` only paints the focus ring.
 * - Signs are the official vectors, not font glyphs: minus is a 13.6dp round-cap line and plus is
 *   two 15.2dp square-cap bars, both 1.6dp thick (`ic_minus_sign` / `ic_plus_sign`).
 * - Enabled signs use `couiColorPrimaryNeutral` (`#E6000000` / `#E6FFFFFF`); disabled ones switch to
 *   `couiColorDivider` (`#1F000000` / `#33FFFFFF`) instead of fading.
 * - Indicator: centred readout with `minWidth` 44dp and 12dp side margins
 *   (`couiTextAppearanceHeadline5`: 18sp, sans-serif-medium).
 * - Haptics: Performs micro-tick on increment/decrement.
 * - Boundaries: Automatically disables endpoints when reaching [minValue] or [maxValue].
 *
 * **Long-press repeat.** `COUIStepperView` wraps each button in a `LongPressProxy`: a single tap steps
 * once on release, while a long press steps immediately and then repeats every **100ms**
 * (`sendEmptyMessageDelayed(2, 100)`), each repetition preceded by `performHapticFeedback(308, 0)`.
 * `Modifier.stepperPressable` reproduces that split — see its KDoc for the implementation notes.
 *
 * The stock class also reads `couiDefStep` (initial step, default 0), `couiUnit` (increment size,
 * default 1) and `couiMinimum` / `couiMaximum` (default `-999` / `ObservableStep.MAX_VALUE`)
 * from `COUIStepperView` styled attributes; this port exposes equivalent `value` / `step` /
 * `minValue` / `maxValue` parameters instead.
 */
@Composable
fun Stepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int = 100,
    step: Int = 1,
    enabled: Boolean = true,
    valueFormatter: (Int) -> String = { it.toString() },
    colors: StepperColors = StepperDefaults.stepperColors(),
) {
    val haptic = LocalHapticFeedback.current
    val canMinus = enabled && value > minValue
    val canPlus = enabled && value < maxValue
    val minusInteractionSource = remember { MutableInteractionSource() }
    val plusInteractionSource = remember { MutableInteractionSource() }
    val minusFocused by minusInteractionSource.collectIsFocusedAsState()
    val plusFocused by plusInteractionSource.collectIsFocusedAsState()
    // `COUIStepperView.configEffectDrawableForButton` layers a `COUIMaskEffectDrawable` (press mask in
    // `?attr/couiColorPress`) under the glyph, on top of the `enableScaleEffect(view, 2)` scale.
    val stepperDark = isStepperInDarkTheme()
    val minusPressed by minusInteractionSource.collectIsPressedAsState()
    val plusPressed by plusInteractionSource.collectIsPressedAsState()
    val minusPressProgress by animateFloatAsState(
        targetValue = if (minusPressed) 1f else 0f,
        animationSpec = stockStateEffectSpring(),
        label = "stepperMinusPress",
    )
    val plusPressProgress by animateFloatAsState(
        targetValue = if (plusPressed) 1f else 0f,
        animationSpec = stockStateEffectSpring(),
        label = "stepperPlusPress",
    )
    val pressColor = stepperPressColor(stepperDark)
    val minusPressColor = stockStateEffectColor(pressColor, minusPressProgress)
    val plusPressColor = stockStateEffectColor(pressColor, plusPressProgress)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        // Minus Button
        Box(
            modifier =
                Modifier
                    .size(StepperDefaults.ButtonSize)
                    // `couiColorContainer8` fill only: the source's `COUIStrokeDrawable` paints the
                    // focus ring on focus, so there is no resting border. The ring is clipped to the
                    // outside half of a 6dp stroke, matching `Button`.
                    .background(colors.buttonBgColor, CircleShape)
                    .drawBehind {
                        if (minusPressColor != Color.Transparent) drawCircle(minusPressColor)
                        if (minusFocused) {
                            drawStockFocusRing(colors.focusOutlineColor)
                        }
                    }.stepperPressable(
                        interactionSource = minusInteractionSource,
                        enabled = canMinus,
                        onStep = {
                            val nextVal = (value - step).coerceAtLeast(minValue)
                            if (nextVal != value) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onValueChange(nextVal)
                            }
                        },
                    ),
            contentAlignment = Alignment.Center,
        ) {
            // `ic_minus_sign`: a 13.6dp round-cap line, 1.6dp thick, in the 32dp viewport.
            Canvas(modifier = Modifier.size(StepperDefaults.ButtonSize)) {
                val half = StepperStockMinusLength.toPx() / 2f
                val thickness = StepperStockGlyphStroke.toPx()
                val y = size.height / 2f
                drawLine(
                    color = if (canMinus) colors.iconTint else colors.disabledIconTint,
                    start = Offset(size.width / 2f - half, y),
                    end = Offset(size.width / 2f + half, y),
                    strokeWidth = thickness,
                    cap = StrokeCap.Round,
                )
            }
        }

        // Value Readout: `coui_stepper_view.xml` styles the indicator with
        // `?attr/couiTextAppearanceHeadline5` (18sp, sans-serif-medium).
        Text(
            text = valueFormatter(value),
            fontSize = StepperDefaults.ValueTextSize,
            fontWeight = StepperDefaults.ValueFontWeight,
            color = if (enabled) colors.textColor else colors.disabledTextColor,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    // `android:minWidth="44dp"` plus `layout_marginStart/End="12dp"`: the readout is
                    // 44dp wide at minimum *before* the 12dp margins, so the minimum total is 68dp.
                    .padding(horizontal = StepperDefaults.ValueHorizontalPadding)
                    .defaultMinSize(minWidth = StepperDefaults.ValueMinWidth),
        )

        // Plus Button
        Box(
            modifier =
                Modifier
                    .size(StepperDefaults.ButtonSize)
                    .background(colors.buttonBgColor, CircleShape)
                    .drawBehind {
                        if (plusPressColor != Color.Transparent) drawCircle(plusPressColor)
                        if (plusFocused) {
                            drawStockFocusRing(colors.focusOutlineColor)
                        }
                    }.stepperPressable(
                        interactionSource = plusInteractionSource,
                        enabled = canPlus,
                        onStep = {
                            val nextVal = (value + step).coerceAtMost(maxValue)
                            if (nextVal != value) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onValueChange(nextVal)
                            }
                        },
                    ),
            contentAlignment = Alignment.Center,
        ) {
            // `ic_plus_sign`: two 15.2dp arms with rounded ends (the vector is a filled path whose
            // 1.6dp stroke leaves 13.6dp of centre line, and the 0.8dp end radius is part of the length).
            Canvas(modifier = Modifier.size(StepperDefaults.ButtonSize)) {
                val half = (StepperStockPlusLength - StepperStockGlyphStroke).toPx() / 2f
                val thickness = StepperStockGlyphStroke.toPx()
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val tint = if (canPlus) colors.iconTint else colors.disabledIconTint
                drawLine(
                    color = tint,
                    start = Offset(centerX - half, centerY),
                    end = Offset(centerX + half, centerY),
                    strokeWidth = thickness,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = tint,
                    start = Offset(centerX, centerY - half),
                    end = Offset(centerX, centerY + half),
                    strokeWidth = thickness,
                    cap = StrokeCap.Square,
                )
            }
        }
    }
}

/**
 * Draws the stock focus ring the way `Button` does: a [StepperStockFocusRingStroke] stroke centred on
 * the circle edge, clipped to the outside so only [StepperStockFocusRingWidth] stays visible.
 */
private fun DrawScope.drawStockFocusRing(color: Color) {
    val radius = size.minDimension / 2f
    val circle = Path().apply { addOval(Rect(center = Offset(size.width / 2f, size.height / 2f), radius = radius)) }
    clipPath(circle, clipOp = ClipOp.Difference) {
        drawCircle(
            color = color,
            radius = radius,
            style = Stroke(width = StepperStockFocusRingStroke.toPx()),
        )
    }
}

object StepperDefaults {
    /** `stepper_button_size`: the +/- hit areas are 32dp squares. */
    val ButtonSize: Dp = 32.dp

    /**
     * `COUIPressAnimatorUnit.initTargetScale()` for the [ButtonSize] square button: the PressCalc area
     * mapping (`PressParamsExtKt` defaults) yields **0.8766** instead of the earlier hand-tuned 0.92.
     */
    val PressTargetScale: Float = stockPressTargetScale(ButtonSize.value, ButtonSize.value)

    /** Indicator text size: `couiTextAppearanceHeadline5` = 18sp. */
    val ValueTextSize = 18.sp

    /** Indicator weight: `couiTextAppearanceHeadline5` uses `sans-serif-medium`. */
    val ValueFontWeight = FontWeight.Medium

    /** Indicator minimum width: `coui_stepper_view.xml` declares `android:minWidth="44dp"`. */
    val ValueMinWidth: Dp = 44.dp
    val ValueHorizontalPadding: Dp = 12.dp

    /** Default [StepperColors] for the stock stepper, keyed on the current theme. */
    @Composable
    fun stepperColors(
        buttonBgColor: Color = stepperButtonBackground(isStepperInDarkTheme()),
        iconTint: Color = stepperIconColor(isStepperInDarkTheme()),
        textColor: Color = stepperIconColor(isStepperInDarkTheme()),
        disabledTextColor: Color = stepperDisabledColor(isStepperInDarkTheme()),
        disabledIconTint: Color = stepperDisabledColor(isStepperInDarkTheme()),
        focusOutlineColor: Color = stepperFocusOutlineColor(isStepperInDarkTheme()),
    ): StepperColors =
        remember(
            buttonBgColor,
            iconTint,
            textColor,
            disabledTextColor,
            disabledIconTint,
            focusOutlineColor,
        ) {
            StepperColors(
                buttonBgColor = buttonBgColor,
                iconTint = iconTint,
                textColor = textColor,
                disabledTextColor = disabledTextColor,
                disabledIconTint = disabledIconTint,
                focusOutlineColor = focusOutlineColor,
            )
        }
}

@Immutable
data class StepperColors(
    val buttonBgColor: Color,
    val iconTint: Color,
    val textColor: Color,
    val disabledTextColor: Color,
    val disabledIconTint: Color,
    val focusOutlineColor: Color,
)

/** Repeat interval of the stepper buttons while held: `sendEmptyMessageDelayed(2, 100L)`. */
private const val STEPPER_REPEAT_INTERVAL_MILLIS = 100L

/**
 * Press handling that mirrors `COUIStepperView`'s `LongPressProxy`.
 *
 * The stock proxy wraps each button in a `GestureDetectorCompat`: `onSingleTapUp` runs the step once and
 * `onLongPress` runs it immediately and then re-posts itself with `sendEmptyMessageDelayed(2, 100L)`, i.e.
 * every **100ms** while the finger stays down and the view is still enabled. Releasing cancels the pending
 * message. A tap that ends before the long-press timeout therefore steps exactly once, on release.
 *
 * This reproduces that split: [onStep] runs once from `onTap` (which `detectTapGestures` only sends when no
 * long press happened) and, while held, from a loop that starts after the platform long-press timeout and
 * then ticks every [STEPPER_REPEAT_INTERVAL_MILLIS]. Releasing cancels the loop and ends the press
 * indication.
 */
@Composable
private fun Modifier.stepperPressable(
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
    onStep: () -> Unit,
): Modifier {
    val currentOnStep by rememberUpdatedState(onStep)
    val longPressTimeoutMillis = LocalViewConfiguration.current.longPressTimeoutMillis
    // `PressGestureScope` is not a `CoroutineScope`; the repeat loop needs one, so take the composition
    // scope instead of spawning an unstructured coroutine.
    val scope = rememberCoroutineScope()
    val indication =
        remember {
            // `COUIStepperView.configEffectDrawableForButton` uses `enableScaleEffect(view, 2)`, whose
            // `COUIPressAnimatorUnit` target scale is the PressCalc area mapping for the button size and whose
            // springs are the `PressParamsExtKt` default `jellyPressSpring` / `jellyHandupSpring`.
            SinkFeedback(
                sinkAmount = StepperDefaults.PressTargetScale,
                animationSpec = couiSpring(response = 0.25f, bounce = 0f),
                releaseAnimationSpec = couiSpring(response = 0.5f, bounce = 0.6f),
            )
        }
    return this
        .indication(interactionSource = interactionSource, indication = indication)
        .pointerInput(enabled, longPressTimeoutMillis) {
            if (!enabled) return@pointerInput
            var stepRepeated = false
            detectTapGestures(
                onPress = { offset ->
                    stepRepeated = false
                    val press = PressInteraction.Press(offset)
                    interactionSource.emit(press)
                    val repeatJob =
                        scope.launch {
                            delay(longPressTimeoutMillis)
                            stepRepeated = true
                            while (isActive) {
                                currentOnStep()
                                delay(STEPPER_REPEAT_INTERVAL_MILLIS)
                            }
                        }
                    tryAwaitRelease()
                    repeatJob.cancel()
                    interactionSource.emit(PressInteraction.Release(press))
                },
                onTap = { if (!stepRepeated) currentOnStep() },
            )
        }
}
