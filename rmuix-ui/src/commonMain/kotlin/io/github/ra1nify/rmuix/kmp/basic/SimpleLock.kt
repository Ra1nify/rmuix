// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.min

/**
 * Colours of a [SimpleLock].
 *
 * The stock `COUISimpleLock` draws `coui_simple_lock_filled_rectangle_icon` (`#FF000000` light /
 * `#E6FFFFFF` dark) and `coui_simple_lock_outlined_rectangle_icon` (`#19000000` / `#66FFFFFF`); both are
 * 10dp circles, the "outlined" one being a 10% (light) / 40% (dark) translucent dot, not a ring.
 */
@Immutable
data class SimpleLockColors(
    val filledColor: Color,
    val outlinedColor: Color,
) {
    internal fun dotColor(filled: Boolean): Color = if (filled) filledColor else outlinedColor
}

/** Defaults for [SimpleLock], taken from `coui_simple_lock_*` resources in the ColorOS settings dump. */
object SimpleLockDefaults {
    /** `coui_simple_lock_*_rectangle_icon` intrinsic dot diameter (`<size>` 10dp). */
    val DotSize = 10.dp

    /** The drawable's `<padding>` on each side; the dot's laid-out box is [DotSize] + 2 * [DotInset]. */
    val DotInset = 6.dp

    /** The laid-out dot box (`getIntrinsicWidth()` of the stock drawable). */
    val DotBoxSize = 22.dp

    /** `couiRectanglePadding`: the gap between dot boxes (`Widget.COUI.COUISimpleLock` sets 10dp). */
    val DotGap = 10.dp

    /** `FOURCIRCLE`: the default code length. */
    val FourCircles = 4

    /** `SIXCIRCLE` / `SIXCIRCLETYPE`: the 6-digit code length. */
    val SixCircles = 6

    /** `COUISimpleLock.SPRING_RESPONSE` = 0.35f: the fill pulse's spring response. */
    val FillSpringResponse = 0.35f

    /** `COUISimpleLock.SPRING_BOUNCE` = 0.5f: the fill pulse's spring bounce. */
    val FillSpringBounce = 0.5f

    /** `CIRCLE_MAX_SCALE` = 1.2f: the fill pulse overshoot. */
    val FillMaxScale = 1.2f

    /**
     * `MORPHING_OUTLINED_TO_FILLED_TIME` / `MORPHING_FILLED_TO_OUTLINED_TIME` = 230ms: how long a deleted dot
     * takes to cross-fade back to the outlined state (the add path uses the [FillSpringResponse] spring).
     */
    val MorphMillis = 230

    /** `SHAKE_AND_FALL_TIME` = 800ms: the failed shake/fall animation. */
    val FailureMillis = 800

    /** `SHAKE_X_POINT` = 0, 30, -28, 14, -8, 4, -3, 0 raw px, interpolated linearly. */
    val ShakePointsPx = floatArrayOf(0f, 30f, -28f, 14f, -8f, 4f, -3f, 0f)

    /** `FALL_SPEED` = 250: the fall translation is 0..250 raw px over the 800ms. */
    val FallDistancePx = 250f

    /** `FALL_HEIGHT` = 150: the per-dot fall distance over which the filled dot fades out. */
    val FallFadeDistancePx = 150f

    /** `getDelayFallHeight()` never exceeds [FallFadeDistancePx], so 150 raw px of room is enough. */
    val FallRoomPx = 150f

    /** `DELAY_FOUR`: per-dot start delay (raw px) of the fall for a 4-dot lock. */
    val DelayFour = floatArrayOf(0f, 38.5f, 91f, 63f)

    /** `DELAY_SIX`: per-dot start delay (raw px) of the fall for a 6-dot lock. */
    val DelaySix = floatArrayOf(0f, 38.5f, 91f, 63f, 38.5f, 70f)

    /** The stock colours, keyed on the current theme. */
    @Composable
    fun colors(
        filledColor: Color = simpleLockFilled(isSimpleLockDarkTheme()),
        outlinedColor: Color = simpleLockOutlined(isSimpleLockDarkTheme()),
    ): SimpleLockColors =
        remember(filledColor, outlinedColor) {
            SimpleLockColors(filledColor = filledColor, outlinedColor = outlinedColor)
        }
}

@Composable
private fun isSimpleLockDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/** `coui_simple_lock_filled_rectangle_icon_color` / `_dark_color`. */
internal fun simpleLockFilled(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xFF000000)

/** `coui_simple_lock_outlined_rectangle_icon_color` / `_dark_color`. */
internal fun simpleLockOutlined(dark: Boolean): Color = if (dark) Color(0x66FFFFFF) else Color(0x19000000)

/**
 * The ColorOS PIN indicator: `COUISimpleLock` (`com.coui.appcompat.lockview`).
 *
 * Draws `length` 10dp circles in 22dp boxes with `couiRectanglePadding` = 10dp between them; the first
 * [filledCount] circles use the filled colour and the rest the translucent outlined colour. A newly filled
 * circle pulses with the stock spring (`SPRING_RESPONSE` 0.35, `SPRING_BOUNCE` 0.5, `CIRCLE_MAX_SCALE` 1.2).
 *
 * @param filledCount How many circles are filled (the current input length).
 * @param modifier Applied to the indicator; the default minimum width is the stock row width.
 * @param length The total code length, [SimpleLockDefaults.FourCircles] by default; stock also ships
 *   [SimpleLockDefaults.SixCircles] (`SIXCIRCLETYPE`).
 * @param colors The dot colours.
 * @param animated Whether a newly filled dot pulses (stock's add animation).
 * @param failed Runs the stock failed shake/fall animation when it becomes `true`: the row shakes with
 *   `SHAKE_X_POINT` and every filled dot falls and fades with the per-dot `DELAY_FOUR` / `DELAY_SIX`
 *   offsets over 800ms (`SHAKE_AND_FALL_TIME`). The outlined dots only shake.
 * @param reserveFailureSpace Reserves the stock `onMeasure()` room for the fall (`mDrawableHeight + 150`
 *   raw pixels) and pins the row to the top of the card. The stock view always measures that way; the
 *   default keeps this library's compact 22dp indicator, so pass `true` for the pixel-exact layout.
 */
@Composable
fun SimpleLock(
    filledCount: Int,
    modifier: Modifier = Modifier,
    length: Int = SimpleLockDefaults.FourCircles,
    colors: SimpleLockColors = SimpleLockDefaults.colors(),
    animated: Boolean = true,
    failed: Boolean = false,
    reserveFailureSpace: Boolean = false,
) {
    require(length > 0) { "length should be > 0" }
    val count = filledCount.coerceIn(0, length)
    val density = LocalDensity.current
    val metrics =
        remember(length, density) {
            simpleLockMetrics(
                width = 0f,
                count = length,
                dotBoxSize = with(density) { SimpleLockDefaults.DotBoxSize.toPx() },
                gap = with(density) { SimpleLockDefaults.DotGap.toPx() },
            )
        }
    val minWidth = with(density) { metrics.totalWidth.toDp() }
    val failureProgress = remember { Animatable(0f) }
    LaunchedEffect(failed) {
        if (failed) {
            failureProgress.snapTo(0f)
            failureProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = SimpleLockDefaults.FailureMillis, easing = LinearEasing),
            )
        } else {
            failureProgress.snapTo(0f)
        }
    }
    val failureRoom = with(density) { SimpleLockDefaults.FallRoomPx.toDp() }
    val rowHeight = SimpleLockDefaults.DotBoxSize + if (reserveFailureSpace) failureRoom else 0.dp
    // Per-dot filled fraction: `COUISimpleLock` cross-fades each dot between the filled and outlined
    // drawables (230ms on delete/clear-all, the 0.35/0.5 spring on add, which also drives the pulse scale).
    val fractions =
        remember(length) {
            MutableList(length) { index -> Animatable(if (index < count) 1f else 0f) }
        }
    var animationInitialised by remember { mutableStateOf(false) }
    LaunchedEffect(count, length, animated) {
        if (!animationInitialised) {
            animationInitialised = true
            return@LaunchedEffect
        }
        coroutineScope {
            for (index in 0 until length) {
                val target = if (index < count) 1f else 0f
                val current = fractions[index].value
                when {
                    target > current && animated -> {
                        launch {
                            fractions[index].animateTo(
                                targetValue = 1f,
                                animationSpec =
                                    spring(
                                        dampingRatio = SimpleLockDefaults.FillSpringBounce,
                                        stiffness = springStiffnessForResponse(SimpleLockDefaults.FillSpringResponse),
                                    ),
                            )
                        }
                    }

                    target < current && animated -> {
                        launch {
                            fractions[index].animateTo(0f, tween(durationMillis = SimpleLockDefaults.MorphMillis))
                        }
                    }

                    else -> {
                        fractions[index].snapTo(target)
                    }
                }
            }
        }
    }
    Canvas(
        modifier =
            modifier.defaultMinSize(
                minWidth = minWidth,
                minHeight = rowHeight,
            ),
    ) {
        val row =
            simpleLockMetrics(
                width = size.width,
                count = length,
                dotBoxSize = SimpleLockDefaults.DotBoxSize.toPx(),
                gap = SimpleLockDefaults.DotGap.toPx(),
            )
        val radius = SimpleLockDefaults.DotSize.toPx() / 2f
        val failing = failed && failureProgress.value > 0f
        // `drawFailedAnimation()` translates every dot by `SHAKE_X_POINT` and only the filled dots fall
        // (`getDelayFallHeight()`); the stock view pins the row to the top of its +150px tall bounds.
        val shakeX = if (failing) simpleLockShakeX(failureProgress.value) else 0f
        val fallY = if (failing) simpleLockFallDistancePx(failureProgress.value) else 0f
        val centerY = if (reserveFailureSpace) SimpleLockDefaults.DotBoxSize.toPx() / 2f else size.height / 2f
        for (index in 0 until length) {
            val centerX = row.dotCenterX(index) + shakeX
            val fraction = fractions[index].value
            if (failing) {
                // Outlined dots are the base layer for every cell and do not fall.
                drawCircle(color = colors.outlinedColor, radius = radius, center = Offset(centerX, centerY))
                if (index < count) {
                    val delayFall = simpleLockDelayFallPx(index, fallY, length)
                    val alpha = simpleLockFallAlpha(delayFall)
                    if (alpha > 0f) {
                        drawCircle(
                            color = colors.filledColor.copy(alpha = colors.filledColor.alpha * alpha),
                            radius = radius,
                            center = Offset(centerX, centerY + delayFall),
                        )
                    }
                }
                continue
            }
            val center = Offset(centerX, centerY)
            if (fraction < 1f) {
                // The outlined drawable is always the base layer (`drawPreviousState()`).
                drawCircle(color = colors.outlinedColor, radius = radius, center = center)
            }
            if (fraction > 0f) {
                // The add spring overshoots 1 (the pulse scale); the filled layer fades with the fraction.
                drawCircle(
                    color = colors.filledColor.copy(alpha = colors.filledColor.alpha * min(fraction, 1f)),
                    radius = radius * fraction,
                    center = center,
                )
            }
        }
    }
}

/** Geometry of one [SimpleLock] row, derived from `COUISimpleLock.onMeasure()` / `drawPreviousState()`. */
internal data class SimpleLockMetrics(
    val dotBoxSize: Float,
    val dotGap: Float,
    val totalWidth: Float,
    val height: Float,
    val startX: Float,
) {
    /** Centre of the dot at [index], measured from the row's left edge. */
    fun dotCenterX(index: Int): Float = startX + index * (dotBoxSize + dotGap) + dotBoxSize / 2f
}

/**
 * Lays the dots out in the available [width]:
 *
 * - `mRectanglesWidth = drawableWidth * count + couiRectanglePadding * (count - 1)`;
 * - `mCodeImageStart = (measuredWidth - mRectanglesWidth) / 2`.
 */
internal fun simpleLockMetrics(
    width: Float,
    count: Int,
    dotBoxSize: Float,
    gap: Float,
): SimpleLockMetrics {
    val totalWidth = dotBoxSize * count + gap * (count - 1).coerceAtLeast(0)
    val startX = if (width > 0f) (width - totalWidth) / 2f else 0f
    return SimpleLockMetrics(
        dotBoxSize = dotBoxSize,
        dotGap = gap,
        totalWidth = totalWidth,
        height = dotBoxSize,
        startX = startX,
    )
}

/**
 * `createFailedAnimator()`: `ValueAnimator.ofFloat(SHAKE_X_POINT)` with a linear interpolator, i.e. the
 * 8 points are spread evenly over [SimpleLockDefaults.FailureMillis].
 */
internal fun simpleLockShakeX(progress: Float): Float {
    val points = SimpleLockDefaults.ShakePointsPx
    val t = progress.coerceIn(0f, 1f) * (points.size - 1)
    val index = t.toInt().coerceIn(0, points.size - 2)
    val fraction = t - index
    return points[index] + (points[index + 1] - points[index]) * fraction
}

/** `ValueAnimator.ofFloat(0f, 250f)`: the linear fall translation over the 800ms. */
internal fun simpleLockFallDistancePx(progress: Float): Float = progress.coerceIn(0f, 1f) * SimpleLockDefaults.FallDistancePx

/**
 * `getDelayFallHeight()`: the 4-dot / 6-dot locks delay each dot by `DELAY_FOUR` / `DELAY_SIX` raw px;
 * other lengths fall together.
 */
internal fun simpleLockDelayFallPx(
    index: Int,
    fallPx: Float,
    length: Int,
): Float {
    val delays =
        when (length) {
            SimpleLockDefaults.FourCircles -> SimpleLockDefaults.DelayFour
            SimpleLockDefaults.SixCircles -> SimpleLockDefaults.DelaySix
            else -> return fallPx
        }
    return (fallPx - (delays.getOrNull(index) ?: 0f)).coerceAtLeast(0f)
}

/** `drawFilledRectangleShakeAndFall()`: `alpha = 1 - delayFall / FALL_HEIGHT`, clamped to 0. */
internal fun simpleLockFallAlpha(delayFallPx: Float): Float = (1f - delayFallPx / SimpleLockDefaults.FallFadeDistancePx).coerceIn(0f, 1f)

/** `response` seconds -> stiffness: `(2π / response)²`, matching the library's other stock springs. */
private fun springStiffnessForResponse(response: Float): Float {
    val angularFrequency = 2.0 * PI / response
    return (angularFrequency * angularFrequency).toFloat()
}
