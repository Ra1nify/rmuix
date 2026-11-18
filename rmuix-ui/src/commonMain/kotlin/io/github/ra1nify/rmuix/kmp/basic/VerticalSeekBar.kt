// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Colours of a [VerticalSeekBar].
 *
 * The stock `COUIVerticalSeekBar` resolves `?attr/couiColorContainer12` for the track
 * (`coui_seekbar_background_selector`), `?attr/couiColorContainerTheme` for the progress
 * (`coui_seekbar_progress_selector`) and `coui_seekbar_thumb_color` (#FFFFFF) for the thumb.
 */
@Immutable
data class VerticalSeekBarColors(
    val backgroundColor: Color,
    val progressColor: Color,
    val thumbColor: Color,
    val disabledBackgroundColor: Color,
    val disabledProgressColor: Color,
    val disabledThumbColor: Color,
) {
    internal fun backgroundColor(enabled: Boolean): Color = if (enabled) backgroundColor else disabledBackgroundColor

    internal fun progressColor(enabled: Boolean): Color = if (enabled) progressColor else disabledProgressColor

    internal fun thumbColor(enabled: Boolean): Color = if (enabled) thumbColor else disabledThumbColor
}

/**
 * Metrics of a [VerticalSeekBar], mapped one-to-one onto the `couiVerticalSeekBar*` attributes of
 * `COUIVerticalSeekBar` in `com.coui.appcompat.seekbar`.
 *
 * @param minWidth `couiVerticalSeekBarMinWidth` (default `coui_vertical_seekbar_view_min_width`, 36dp).
 * @param maxHeight `couiVerticalSeekBarMaxHeight`; [Dp.Unspecified] keeps the stock `0` = uncapped.
 * @param backgroundWidth `couiVerticalSeekBarBackgroundWidth`; defaults to `backgroundRadius * 2`.
 * @param backgroundRadius `couiVerticalSeekBarBackgroundRadius` (`coui_seekbar_background_radius`, 2dp).
 * @param progressWidth `couiVerticalSeekBarProgressWidth`; defaults to `progressRadius * 2`.
 * @param progressRadius `couiVerticalSeekBarProgressRadius` (`coui_seekbar_progress_radius`, 2dp).
 * @param paddingVertical `couiVerticalSeekBarProgressPaddingVertical`
 *   (`coui_vertical_seekbar_progress_padding_vertical`, 10dp).
 * @param pressedPaddingVertical the first term of the pressed padding target
 *   (`coui_vertical_seekbar_progress_pressed_padding_vertical`, 4dp): stock animates the padding to
 *   `pressedPaddingVertical + backgroundRadius * backgroundRadiusEnlargeScale`.
 * @param backgroundEnlargeScale `couiVerticalSeekBarBackGroundEnlargeScale` (6.0): the pressed streak widens
 *   the track to `backgroundWidth * scale`.
 * @param progressEnlargeScale `couiVerticalSeekBarProgressEnlargeScale` (4.0): the pressed streak widens the
 *   progress bar to `progressWidth * scale`; the thumb keeps `progressWidth * scale` as its width.
 * @param backgroundRadiusEnlargeScale `couiVerticalSeekBarBackGroundRadiusEnlargeScale` (defaults to
 *   [backgroundEnlargeScale]).
 * @param progressRadiusEnlargeScale `couiVerticalSeekBarProgressRadiusEnlargeScale` (defaults to
 *   [progressEnlargeScale]).
 * @param touchEnlargeDurationMillis `TOUCH_ANIMATION_ENLARGE_DURATION` / `RELEASE_ANIM_DURATION`, both 183ms.
 */
@Immutable
data class VerticalSeekBarStyle(
    val minWidth: Dp = 36.dp,
    val maxHeight: Dp = Dp.Unspecified,
    val backgroundWidth: Dp = 4.dp,
    val backgroundRadius: Dp = 2.dp,
    val progressWidth: Dp = 4.dp,
    val progressRadius: Dp = 2.dp,
    val paddingVertical: Dp = 10.dp,
    val pressedPaddingVertical: Dp = 4.dp,
    val backgroundEnlargeScale: Float = 6f,
    val progressEnlargeScale: Float = 4f,
    val backgroundRadiusEnlargeScale: Float = 6f,
    val progressRadiusEnlargeScale: Float = 4f,
    val touchEnlargeDurationMillis: Int = 183,
)

/** Defaults for [VerticalSeekBar] (`COUIVerticalSeekBar` in `com.coui.appcompat.seekbar`). */
object VerticalSeekBarDefaults {
    /** `coui_vertical_seekbar_view_min_width`. */
    val MinWidth = 36.dp

    /** `coui_seekbar_background_radius`, also `backgroundWidth = radius * 2` when unset. */
    val BackgroundRadius = 2.dp

    /** `coui_seekbar_progress_radius`, also `progressWidth = radius * 2` when unset. */
    val ProgressRadius = 2.dp

    /** `coui_vertical_seekbar_progress_padding_vertical`. */
    val ProgressPaddingVertical = 10.dp

    /** `coui_vertical_seekbar_progress_pressed_padding_vertical`. */
    val PressedPaddingVertical = 4.dp

    /** `couiVerticalSeekBarBackGroundEnlargeScale` default in `COUIVerticalSeekBar`. */
    val BackgroundEnlargeScale = 6f

    /** `couiVerticalSeekBarProgressEnlargeScale` default in `COUIVerticalSeekBar`. */
    val ProgressEnlargeScale = 4f

    /** `TOUCH_ANIMATION_ENLARGE_DURATION` and `RELEASE_ANIM_DURATION`. */
    val TouchEnlargeDurationMillis = 183

    /** The stock defaults as a [VerticalSeekBarStyle]. */
    fun style(): VerticalSeekBarStyle = VerticalSeekBarStyle()

    /** The stock colours, keyed on the current theme. */
    @Composable
    fun colors(
        backgroundColor: Color = sliderContainer12(isVerticalSeekBarDarkTheme()),
        progressColor: Color = RmuixTheme.colorScheme.primary,
        thumbColor: Color = Color.White,
        disabledBackgroundColor: Color = sliderContainer12(isVerticalSeekBarDarkTheme()),
        disabledProgressColor: Color = RmuixTheme.colorScheme.primary.copy(alpha = 0x4D / 255f),
        disabledThumbColor: Color = sliderDisabledThumb(isVerticalSeekBarDarkTheme()),
    ): VerticalSeekBarColors =
        remember(
            backgroundColor,
            progressColor,
            thumbColor,
            disabledBackgroundColor,
            disabledProgressColor,
            disabledThumbColor,
        ) {
            VerticalSeekBarColors(
                backgroundColor = backgroundColor,
                progressColor = progressColor,
                thumbColor = thumbColor,
                disabledBackgroundColor = disabledBackgroundColor,
                disabledProgressColor = disabledProgressColor,
                disabledThumbColor = disabledThumbColor,
            )
        }
}

@Composable
private fun isVerticalSeekBarDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/**
 * The ColorOS vertical seek bar: `COUIVerticalSeekBar` (`com.coui.appcompat.seekbar`).
 *
 * A track that grows out of the bottom padding (`couiVerticalSeekBarProgressPaddingVertical`), a progress bar
 * that fills from the bottom, an optional square thumb of `progressWidth * progressEnlargeScale`, and the
 * stock press streak: on drag the track, progress and padding animate to their `*EnlargeScale` targets over
 * `183ms` and animate back on release. `setProgress` is the caller's state, matching the rest of the library.
 *
 * @param value The current progress.
 * @param onValueChange Called with the new progress while dragging and when a tap picks a position.
 * @param modifier Applied to the seek bar; width defaults to [VerticalSeekBarStyle.minWidth], height is
 *   caller-defined.
 * @param onValueChangeFinished Called when the gesture ends.
 * @param enabled Whether the bar reacts to input and draws the enabled colours.
 * @param valueRange The progress range, `0f..1f` by default.
 * @param steps Number of discrete steps between the ends; `0` keeps continuous progress.
 * @param showThumb `couiVerticalSeekBarShowThumb`: the square thumb at the progress position.
 * @param showProgress `couiVerticalSeekBarShowProgress`.
 * @param progressFull `couiVerticalSeekBarProgressFull`: progress inherits the track's width, radius and
 *   enlarge scales (stock `resetProgressSize()`).
 * @param style The stock metrics.
 * @param colors The stock colours.
 */
@Composable
fun VerticalSeekBar(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (() -> Unit)? = null,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    showThumb: Boolean = true,
    showProgress: Boolean = true,
    progressFull: Boolean = false,
    style: VerticalSeekBarStyle = VerticalSeekBarDefaults.style(),
    colors: VerticalSeekBarColors = VerticalSeekBarDefaults.colors(),
) {
    require(valueRange.start < valueRange.endInclusive) { "valueRange start should be less than end" }
    require(steps >= 0) { "steps should be >= 0" }

    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val press = remember { Animatable(0f) }
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnValueChangeFinished by rememberUpdatedState(onValueChangeFinished)
    val currentRange by rememberUpdatedState(valueRange)
    val currentSteps by rememberUpdatedState(steps)
    val durationMillis = style.touchEnlargeDurationMillis

    Canvas(
        modifier =
            modifier
                .defaultMinSize(minWidth = style.minWidth)
                .then(
                    if (style.maxHeight == Dp.Unspecified) {
                        Modifier
                    } else {
                        Modifier.heightIn(max = style.maxHeight)
                    },
                ).progressSemantics(value, valueRange, steps)
                .pointerInput(enabled, style, showThumb, progressFull) {
                    if (!enabled) return@pointerInput
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        down.consume()
                        val touchSlop = viewConfiguration.touchSlop
                        var dragging = false
                        var exit = false
                        while (!exit) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (change.pressed) {
                                if (!dragging && abs(change.position.y - down.position.y) > touchSlop) {
                                    dragging = true
                                    scope.launch { press.animateTo(1f, tween(durationMillis)) }
                                }
                                if (dragging) {
                                    reportVerticalProgress(
                                        positionY = change.position.y,
                                        size = this@pointerInput.size,
                                        style = style,
                                        density = density.density,
                                        showThumb = showThumb,
                                        progressFull = progressFull,
                                        range = currentRange,
                                        steps = currentSteps,
                                        onValueChange = currentOnValueChange,
                                    )
                                }
                                change.consume()
                            } else {
                                if (!dragging) {
                                    reportVerticalProgress(
                                        positionY = change.position.y,
                                        size = this@pointerInput.size,
                                        style = style,
                                        density = density.density,
                                        showThumb = showThumb,
                                        progressFull = progressFull,
                                        range = currentRange,
                                        steps = currentSteps,
                                        onValueChange = currentOnValueChange,
                                    )
                                }
                                exit = true
                            }
                        }
                        currentOnValueChangeFinished?.invoke()
                        scope.launch {
                            press.animateTo(0f, tween(durationMillis))
                        }
                    }
                },
    ) {
        val fraction =
            ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start))
                .coerceIn(0f, 1f)
        val metrics =
            verticalSeekBarMetrics(
                width = size.width,
                height = size.height,
                style = style,
                progressFull = progressFull,
                showThumb = showThumb,
                showProgress = showProgress,
                fraction = fraction,
                pressedFraction = press.value,
                density = density.density,
            )
        drawRoundRect(
            color = colors.backgroundColor(enabled),
            topLeft = Offset(metrics.backgroundLeft, metrics.backgroundTop),
            size = Size(metrics.backgroundRight - metrics.backgroundLeft, metrics.backgroundBottom - metrics.backgroundTop),
            cornerRadius = CornerRadius(metrics.backgroundRadius, metrics.backgroundRadius),
        )
        if (showProgress && metrics.progressBottom > metrics.progressTop) {
            drawRoundRect(
                color = colors.progressColor(enabled),
                topLeft = Offset(metrics.progressLeft, metrics.progressTop),
                size = Size(metrics.progressRight - metrics.progressLeft, metrics.progressBottom - metrics.progressTop),
                cornerRadius = CornerRadius(metrics.progressRadius, metrics.progressRadius),
            )
        }
        if (showThumb) {
            drawRoundRect(
                color = colors.thumbColor(enabled),
                topLeft = Offset(metrics.centerX - metrics.thumbWidth / 2f, metrics.thumbCenterY - metrics.thumbWidth / 2f),
                size = Size(metrics.thumbWidth, metrics.thumbWidth),
                cornerRadius = CornerRadius(metrics.thumbRadius, metrics.thumbRadius),
            )
        }
    }
}

/**
 * Geometry of one [VerticalSeekBar] frame, derived from `COUIVerticalSeekBar.draw()` /
 * `setBackgroundRect()` / `setProgressRect()`.
 */
internal data class VerticalSeekBarMetrics(
    val centerX: Float,
    val backgroundLeft: Float,
    val backgroundRight: Float,
    val backgroundTop: Float,
    val backgroundBottom: Float,
    val backgroundRadius: Float,
    val progressLeft: Float,
    val progressRight: Float,
    val progressTop: Float,
    val progressBottom: Float,
    val progressRadius: Float,
    val thumbCenterY: Float,
    val thumbWidth: Float,
    val thumbRadius: Float,
    val travelBottom: Float,
    val travelLength: Float,
)

/**
 * The stock geometry, in pixels:
 *
 * - background rect: `paddingVertical - backgroundRadius` .. `height - paddingVertical + backgroundRadius`,
 *   `backgroundWidth` wide (stock `setBackgroundRect()`);
 * - travel: `height - 2 * paddingVertical`, shortened by `thumbWidth - 2 * thumbRadius` when the thumb is
 *   visible (stock `draw()` f5);
 * - progress rect: from the thumb/travel position to the bottom point, expanded by `thumbWidth / 2` on both
 *   ends when the thumb is visible (stock `drawProgress()`).
 */
internal fun verticalSeekBarMetrics(
    width: Float,
    height: Float,
    style: VerticalSeekBarStyle,
    progressFull: Boolean,
    showThumb: Boolean,
    showProgress: Boolean,
    fraction: Float,
    pressedFraction: Float,
    density: Float,
): VerticalSeekBarMetrics {
    fun Dp.px(): Float = value * density

    val t = pressedFraction.coerceIn(0f, 1f)
    val paddingVertical =
        lerp(
            style.paddingVertical.px(),
            style.pressedPaddingVertical.px() + style.backgroundRadius.px() * style.backgroundRadiusEnlargeScale,
            t,
        )
    val backgroundWidth = lerp(style.backgroundWidth.px(), style.backgroundWidth.px() * style.backgroundEnlargeScale, t)
    val backgroundRadius =
        lerp(style.backgroundRadius.px(), style.backgroundRadius.px() * style.backgroundRadiusEnlargeScale, t)
    val restProgressWidth = (if (progressFull) style.backgroundWidth else style.progressWidth).px()
    val restProgressRadius = (if (progressFull) style.backgroundRadius else style.progressRadius).px()
    val restProgressEnlargeScale =
        if (progressFull) style.backgroundEnlargeScale else style.progressEnlargeScale
    val restProgressRadiusEnlargeScale =
        if (progressFull) style.backgroundRadiusEnlargeScale else style.progressRadiusEnlargeScale
    val progressWidth = lerp(restProgressWidth, restProgressWidth * restProgressEnlargeScale, t)
    val progressRadius = lerp(restProgressRadius, restProgressRadius * restProgressRadiusEnlargeScale, t)
    val thumbWidth = restProgressWidth * restProgressEnlargeScale
    val thumbRadius = restProgressRadius * restProgressRadiusEnlargeScale

    val centerX = width / 2f
    val backgroundLeft = centerX - backgroundWidth / 2f
    val backgroundRight = centerX + backgroundWidth / 2f
    val backgroundTop = paddingVertical - backgroundRadius
    val backgroundBottom = height - paddingVertical + backgroundRadius

    val travelLength =
        if (showThumb) {
            ((height - paddingVertical * 2f) - (thumbWidth - thumbRadius * 2f)).coerceAtLeast(0f)
        } else {
            height - paddingVertical * 2f + progressRadius * 2f
        }
    val travelBottom =
        if (showThumb) {
            height - paddingVertical - thumbWidth / 2f + thumbRadius
        } else {
            height - paddingVertical + progressRadius
        }
    val topPosition = travelBottom - fraction.coerceIn(0f, 1f) * travelLength

    val progressTop = if (showThumb) topPosition - thumbWidth / 2f else topPosition
    val progressBottom = if (showThumb) travelBottom + thumbWidth / 2f else travelBottom
    return VerticalSeekBarMetrics(
        centerX = centerX,
        backgroundLeft = backgroundLeft,
        backgroundRight = backgroundRight,
        backgroundTop = backgroundTop,
        backgroundBottom = backgroundBottom,
        backgroundRadius = backgroundRadius,
        progressLeft = centerX - progressWidth / 2f,
        progressRight = centerX + progressWidth / 2f,
        progressTop = progressTop,
        progressBottom = if (showProgress) progressBottom else progressTop,
        progressRadius = progressRadius,
        thumbCenterY = topPosition,
        thumbWidth = thumbWidth,
        thumbRadius = thumbRadius,
        travelBottom = travelBottom,
        travelLength = travelLength,
    )
}

private fun lerp(
    start: Float,
    stop: Float,
    fraction: Float,
): Float = start + (stop - start) * fraction

/** Maps a touch [positionY] to the value and reports it, snapping to [steps] when present. */
private fun PointerInputScope.reportVerticalProgress(
    positionY: Float,
    size: androidx.compose.ui.unit.IntSize,
    style: VerticalSeekBarStyle,
    density: Float,
    showThumb: Boolean,
    progressFull: Boolean,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
) {
    val metrics =
        verticalSeekBarMetrics(
            width = size.width.toFloat(),
            height = size.height.toFloat(),
            style = style,
            progressFull = progressFull,
            showThumb = showThumb,
            showProgress = true,
            fraction = 0f,
            pressedFraction = 1f,
            density = density,
        )
    val rawFraction =
        if (metrics.travelLength <= 0f) {
            0f
        } else {
            ((metrics.travelBottom - positionY) / metrics.travelLength).coerceIn(0f, 1f)
        }
    val fraction =
        if (steps > 0) {
            ((rawFraction * (steps + 1)).roundToInt().toFloat() / (steps + 1)).coerceIn(0f, 1f)
        } else {
            rawFraction
        }
    onValueChange(range.start + fraction * (range.endInclusive - range.start))
}
