// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A [LinearProgressIndicator] with Rmuix style.
 *
 * @param modifier The modifier to be applied to the indicator.
 * @param progress The current progress value between 0.0f and 1.0f, or null for indeterminate state.
 * @param colors The colors used for the indicator.
 * @param height The height of the indicator.
 */
@Composable
fun LinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    colors: ProgressIndicatorColors = ProgressIndicatorDefaults.progressIndicatorColors(),
    height: Dp = ProgressIndicatorDefaults.DefaultLinearProgressIndicatorHeight,
) {
    val currentBackgroundColor = colors.backgroundColor()
    val currentForegroundColor = colors.foregroundColor(true)
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    if (progress == null) {
        val transition = rememberInfiniteTransition()
        val animatedValue by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 1250, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
        )

        Canvas(
            modifier =
                modifier
                    .semantics { progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate }
                    .fillMaxWidth()
                    .height(height),
        ) {
            drawRoundRect(
                color = currentBackgroundColor,
                size = size,
                cornerRadius = CornerRadius(size.height / 2),
            )

            for (i in 0 until 3) {
                drawIndeterminateSegment(animatedValue, i, currentForegroundColor)
            }
        }
    } else {
        val progressValue = progress.coerceIn(0f, 1f)

        Canvas(
            modifier =
                modifier
                    .semantics { progressBarRangeInfo = ProgressBarRangeInfo(progressValue, 0f..1f) }
                    .fillMaxWidth()
                    .height(height),
        ) {
            val cornerRadius = size.height / 2

            drawRoundRect(
                color = currentBackgroundColor,
                size = size,
                cornerRadius = CornerRadius(cornerRadius),
            )

            // `COUIHorizontalProgressBar.onDraw` paints the fill as ONE full-width rounded rect whose
            // trailing edge sits at the progress point, intersected with the rounded track. That keeps
            // the fill proportional (nothing is drawn at 0) and mirrors to the right edge in RTL.
            val trackPath =
                Path().apply {
                    addRoundRect(RoundRect(Rect(Offset.Zero, size), CornerRadius(cornerRadius)))
                }
            clipPath(trackPath) {
                drawRoundRect(
                    color = currentForegroundColor,
                    topLeft = Offset(linearProgressFillLeft(progressValue, size.width, rtl), 0f),
                    size = size,
                    cornerRadius = CornerRadius(cornerRadius),
                )
            }
        }
    }
}

/**
 * A [CircularProgressIndicator] with Rmuix style.
 *
 * @param modifier The modifier to be applied to the indicator.
 * @param progress The current progress value between 0.0f and 1.0f, or null for indeterminate state.
 * @param colors The colors used for the indicator.
 * @param strokeWidth The width of the circular stroke.
 * @param size The size (diameter) of the circular indicator.
 * @param showShadow Draws the stock `coui_circular_progress_shadow_*` shadow layer (4dp radius, 2dp down,
 *   `#19000000`). Off by default because the stock default progress bar type (0) has no shadow.
 */
@Composable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    state: CircularProgressIndicatorState = CircularProgressIndicatorState.Running,
    colors: ProgressIndicatorColors = ProgressIndicatorDefaults.circularProgressIndicatorColors(),
    strokeWidth: Dp = ProgressIndicatorDefaults.DefaultCircularProgressIndicatorStrokeWidth,
    size: Dp = ProgressIndicatorDefaults.DefaultCircularProgressIndicatorSize,
    // `couiCircularProgressBarType == 1` in the stock; default 0 draws no shadow, so this stays opt-in.
    showShadow: Boolean = false,
) {
    val currentBackgroundColor = colors.backgroundColor()
    val currentForegroundColor = colors.foregroundColor(true)

    if (progress == null) {
        val transition = rememberInfiniteTransition()

        val rotationAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                ),
        )
        val sweepAnim by transition.animateFloat(
            initialValue = 30f,
            targetValue = 120f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        keyframes {
                            durationMillis = 1600
                            120f at 800 using LinearEasing
                            30f at 1600 using LinearEasing
                        },
                    repeatMode = RepeatMode.Restart,
                ),
        )

        Canvas(
            modifier =
                modifier
                    .progressShadow(showShadow)
                    .semantics { progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate }
                    .size(size),
        ) {
            val strokeWidthPx = strokeWidth.toPx()
            val radius = (size.toPx() - strokeWidthPx) / 2
            val center = Offset(size.toPx() / 2, size.toPx() / 2)

            drawCircularBackground(currentBackgroundColor, radius, center, strokeWidthPx)

            drawArc(
                color = currentForegroundColor,
                startAngle = rotationAnim,
                sweepAngle = sweepAnim,
                useCenter = false,
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = Size(2 * radius, 2 * radius),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            )

            drawProgressStateIcon(state, currentForegroundColor)
        }
    } else {
        val progressValue = progress.coerceIn(0f, 1f)

        Canvas(
            modifier =
                modifier
                    .progressShadow(showShadow)
                    .semantics { progressBarRangeInfo = ProgressBarRangeInfo(progressValue, 0f..1f) }
                    .size(size),
        ) {
            val strokeWidthPx = strokeWidth.toPx()
            val radius = (size.toPx() - strokeWidthPx) / 2
            val center = Offset(size.toPx() / 2, size.toPx() / 2)

            drawCircularBackground(currentBackgroundColor, radius, center, strokeWidthPx)

            val minSweepAngle = 0.1f
            val sweepAngle = minSweepAngle + (360f - minSweepAngle) * progressValue

            drawArc(
                color = currentForegroundColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = Size(2 * radius, 2 * radius),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            )

            drawProgressStateIcon(state, currentForegroundColor)
        }
    }
}

/** The runtime state drawn on top of a [CircularProgressIndicator]. */
enum class CircularProgressIndicatorState {
    /** No overlay: the arc or ring is the only visual. */
    Running,

    /** Two bars in the middle — `COUICircularProgressDrawable.drawPauseIcon()`. */
    Paused,

    /** A 19dp ring with an exclamation — `COUICircularProgressDrawable.drawErrorIcon()`. */
    Error,
}

/**
 * Draws the pause / error overlay exactly as the stock drawable does: both icons are built from
 * primitives (rounded rects and a circle), so no vector asset is needed.
 */
private fun DrawScope.drawProgressStateIcon(
    state: CircularProgressIndicatorState,
    color: Color,
) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    when (state) {
        CircularProgressIndicatorState.Running -> {}

        CircularProgressIndicatorState.Paused -> {
            val barWidth = ProgressIndicatorDefaults.PauseRectWidth.toPx()
            val barHeight = ProgressIndicatorDefaults.PauseRectHeight.toPx()
            val gap = ProgressIndicatorDefaults.PauseGap.toPx()
            val radius = ProgressIndicatorDefaults.PauseRectRadius.toPx()
            drawRoundRect(
                color = color,
                topLeft = Offset(cx - barWidth - gap / 2f, cy - barHeight / 2f),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(radius),
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(cx + gap / 2f, cy - barHeight / 2f),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(radius),
            )
        }

        CircularProgressIndicatorState.Error -> {
            val ringRadius = (ProgressIndicatorDefaults.ErrorDiameter.toPx() - ProgressIndicatorDefaults.ErrorStrokeWidth.toPx()) / 2f
            drawCircle(
                color = color,
                radius = ringRadius,
                center = Offset(cx, cy),
                style = Stroke(width = ProgressIndicatorDefaults.ErrorStrokeWidth.toPx()),
            )
            val stemWidth = ProgressIndicatorDefaults.ErrorStemWidth.toPx()
            val stemHeight = ProgressIndicatorDefaults.ErrorStemHeight.toPx()
            val stemBias = ProgressIndicatorDefaults.ErrorStemBias.toPx()
            drawRect(
                color = color,
                topLeft = Offset(cx - stemWidth / 2f, cy - stemBias),
                size = Size(stemWidth, stemHeight),
            )
            drawCircle(
                color = color,
                radius = ProgressIndicatorDefaults.ErrorDotRadius.toPx(),
                center = Offset(cx, cy + ProgressIndicatorDefaults.ErrorDotBias.toPx()),
            )
        }
    }
}

/**
 * A [InfiniteProgressIndicator] with Rmuix style.
 * The indicator is a circular indicator with an orbiting dot.
 *
 * @param modifier The modifier to be applied to the indicator.
 * @param color The color of the indicator.
 * @param size The size (diameter) of the circular indicator.
 * @param strokeWidth The width of the circular stroke.
 * @param orbitingDotSize The size of the orbiting dot.
 */
@Composable
fun InfiniteProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
    size: Dp = ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorSize,
    strokeWidth: Dp = ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorStrokeWidth,
    orbitingDotSize: Dp = ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorOrbitingDotSize,
) {
    val transition = rememberInfiniteTransition()
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
    )

    Canvas(
        modifier = modifier.size(size),
    ) {
        val strokeWidthPx = strokeWidth.toPx()
        val orbitingDotSizePx = orbitingDotSize.toPx()
        val center = Offset(this.size.width / 2, this.size.height / 2)
        val radius = (size.toPx() - strokeWidthPx) / 2

        drawCircle(
            color = color,
            radius = radius,
            center = center,
            style = Stroke(strokeWidthPx, cap = StrokeCap.Round),
        )

        val orbitRadius = radius - 2 * orbitingDotSizePx
        val angle = rotation * (PI / 180.0).toFloat()
        val dotCenter =
            center +
                Offset(
                    x = orbitRadius * cos(angle),
                    y = orbitRadius * sin(angle),
                )

        drawCircle(
            color = color,
            radius = orbitingDotSizePx,
            center = dotCenter,
        )
    }
}

/**
 * Draws a single animated segment for the indeterminate linear progress indicator.
 */
private fun DrawScope.drawIndeterminateSegment(
    animatedValue: Float,
    segmentIndex: Int,
    color: Color,
) {
    val position = animatedValue - segmentIndex * (0.45f + 0.55f)
    val adjustedPos = (position % 1f + 1f) % 1f
    val cornerRadius = CornerRadius(size.height / 2)

    if (adjustedPos < 1f - 0.45f) {
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * adjustedPos, 0f),
            size = Size(size.width * 0.45f, size.height),
            cornerRadius = cornerRadius,
        )
    } else {
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * adjustedPos, 0f),
            size = Size(size.width * (1f - adjustedPos), size.height),
            cornerRadius = cornerRadius,
        )
        val remainingWidth = adjustedPos + 0.45f - 1f
        if (remainingWidth > 0) {
            drawRoundRect(
                color = color,
                size = Size(size.width * remainingWidth, size.height),
                cornerRadius = cornerRadius,
            )
        }
    }
}

/**
 * Draws a circular background ring used by [CircularProgressIndicator].
 */
private fun DrawScope.drawCircularBackground(
    color: Color,
    radius: Float,
    center: Offset,
    strokeWidthPx: Float,
) {
    drawCircle(
        color = color,
        radius = radius,
        center = center,
        style = Stroke(width = strokeWidthPx),
    )
}

/**
 * Inline offset of the stock linear fill: one full-width rounded rect whose trailing edge lands on the
 * progress point. LTR shifts it left by `(1 - progress) * width`; RTL shifts it right by the same amount.
 * The fill is then clipped by the rounded track, so 0 paints nothing and 1 exactly covers the track.
 */
internal fun linearProgressFillLeft(
    progress: Float,
    widthPx: Float,
    rtl: Boolean,
): Float = if (rtl) (1f - progress) * widthPx else (progress - 1f) * widthPx

object ProgressIndicatorDefaults {
    /** The default height of [LinearProgressIndicator]. */
    val DefaultLinearProgressIndicatorHeight = 6.dp

    /**
     * The default stroke width of [CircularProgressIndicator]: 5dp.
     *
     * `Widget.COUI.COUICircularProgressBar` declares `couiCircularProgressBarSize = large_size`, so the
     * widget default is the large preset (40dp / 5dp), not the medium one.
     */
    val DefaultCircularProgressIndicatorStrokeWidth = ProgressIndicatorStockCircularLargeStrokeWidth

    /** The default size of [CircularProgressIndicator]: the stock default large preset, 40dp. */
    val DefaultCircularProgressIndicatorSize = ProgressIndicatorStockCircularLargeSize

    /**
     * `coui_circular_progress_large_length`: the large preset is 40dp across, meant to be used with
     * [LargeCircularStrokeWidth].
     */
    val LargeCircularSize = ProgressIndicatorStockCircularLargeSize

    /** `coui_circular_progress_large_stroke_width`: the large preset's 5dp ring. */
    val LargeCircularStrokeWidth = ProgressIndicatorStockCircularLargeStrokeWidth

    /** `coui_circular_progress_pause_icon_rect_width`: 4dp per pause bar. */
    val PauseRectWidth = ProgressIndicatorStockPauseRectWidth

    /** `coui_circular_progress_pause_icon_rect_height`: 16dp tall. */
    val PauseRectHeight = ProgressIndicatorStockPauseRectHeight

    /** `coui_circular_progress_pause_icon_rect_radius`: 1dp rounded ends. */
    val PauseRectRadius = ProgressIndicatorStockPauseRectRadius

    /** `coui_circular_progress_pause_icon_rect_gap`: 4dp between the bars. */
    val PauseGap = ProgressIndicatorStockPauseGap

    /** `coui_circular_progress_error_diameter`: the error ring is 19dp. */
    val ErrorDiameter = ProgressIndicatorStockErrorDiameter

    /** `coui_circular_progress_error_stroke_width`: 1.5dp. */
    val ErrorStrokeWidth = ProgressIndicatorStockErrorStrokeWidth

    /** `coui_circular_progress_error_icon_rect_width`: the exclamation stem is 1.6dp wide. */
    val ErrorStemWidth = ProgressIndicatorStockErrorStemWidth

    /** `coui_circular_progress_error_icon_rect_height`: 5.25dp tall. */
    val ErrorStemHeight = ProgressIndicatorStockErrorStemHeight

    /** `coui_circular_progress_error_icon_rect_bias`: the stem starts 4.25dp above the centre. */
    val ErrorStemBias = ProgressIndicatorStockErrorStemBias

    /** `coui_circular_progress_error_icon_circle_radius`: the dot is r=1.1dp. */
    val ErrorDotRadius = ProgressIndicatorStockErrorDotRadius

    /** `coui_circular_progress_error_icon_circle_bias`: the dot sits 3.1dp below the centre. */
    val ErrorDotBias = ProgressIndicatorStockErrorDotBias

    /**
     * `coui_circular_progress_shadow_radius`: 4dp.
     *
     * Only drawn when `showShadow = true` on [CircularProgressIndicator]; the stock drawable installs its shadow
     * layer only for `couiCircularProgressBarType == 1` (`setIsDrawShadow(1 == mProgressBarType)`, default 0), so
     * the component's default matches the stock default and the shadow stays opt-in.
     */
    val ShadowRadius = ProgressIndicatorStockShadowRadius

    /** `coui_circular_progress_shadow_y_bias`: 2dp down, used together with [ShadowRadius]. */
    val ShadowYBias = ProgressIndicatorStockShadowYBias

    /** `coui_circular_progress_shadow_color`: `#19000000`. */
    val ShadowColor = Color(ProgressIndicatorStockShadowColor)

    /** The default stroke width of [InfiniteProgressIndicator]. */
    val DefaultInfiniteProgressIndicatorStrokeWidth = 2.dp

    /** The default radius width of the orbiting dot in [InfiniteProgressIndicator]. */
    val DefaultInfiniteProgressIndicatorOrbitingDotSize = 2.dp

    /** The default size of [InfiniteProgressIndicator]. */
    val DefaultInfiniteProgressIndicatorSize = 20.dp

    /**
     * The default [ProgressIndicatorColors] used by [LinearProgressIndicator] and [CircularProgressIndicator].
     */
    @Composable
    fun progressIndicatorColors(
        foregroundColor: Color = RmuixTheme.colorScheme.primary,
        disabledForegroundColor: Color = RmuixTheme.colorScheme.disabledPrimarySlider,
        backgroundColor: Color = progressIndicatorTrack(isProgressIndicatorInDarkTheme()),
    ): ProgressIndicatorColors =
        remember(foregroundColor, disabledForegroundColor, backgroundColor) {
            ProgressIndicatorColors(
                foregroundColor = foregroundColor,
                disabledForegroundColor = disabledForegroundColor,
                backgroundColor = backgroundColor,
            )
        }

    /**
     * Colors for [CircularProgressIndicator]: the stock ring is `couiColorHintNeutral` over a
     * `couiColorDivider` track, not the accent.
     */
    @Composable
    fun circularProgressIndicatorColors(
        foregroundColor: Color = progressIndicatorRing(isProgressIndicatorInDarkTheme()),
        disabledForegroundColor: Color = RmuixTheme.colorScheme.disabledPrimarySlider,
        backgroundColor: Color = progressIndicatorTrack(isProgressIndicatorInDarkTheme()),
    ): ProgressIndicatorColors =
        remember(foregroundColor, disabledForegroundColor, backgroundColor) {
            ProgressIndicatorColors(
                foregroundColor = foregroundColor,
                disabledForegroundColor = disabledForegroundColor,
                backgroundColor = backgroundColor,
            )
        }
}

@Immutable
data class ProgressIndicatorColors(
    private val foregroundColor: Color,
    private val disabledForegroundColor: Color,
    private val backgroundColor: Color,
) {
    @Stable
    internal fun foregroundColor(enabled: Boolean): Color = if (enabled) foregroundColor else disabledForegroundColor

    @Stable
    internal fun backgroundColor(): Color = backgroundColor
}

/**
 * Draws the stock circular-progress shadow layer:
 * `coui_circular_progress_shadow_radius` 4dp, `_x_bias` 0dp, `_y_bias` 2dp, `_color` `#19000000`.
 */
private fun Modifier.progressShadow(showShadow: Boolean): Modifier =
    if (showShadow) {
        dropShadow(
            shape = CircleShape,
            shadow =
                Shadow(
                    radius = ProgressIndicatorDefaults.ShadowRadius,
                    color = ProgressIndicatorDefaults.ShadowColor,
                    offset = DpOffset(x = 0.dp, y = ProgressIndicatorDefaults.ShadowYBias),
                ),
        )
    } else {
        this
    }
