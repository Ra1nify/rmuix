// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/** The display state of a [LockPatternView] (`COUILockPatternView.DisplayMode`). */
enum class LockPatternDisplayMode {
    /** The pattern is showing normally (stock `DisplayMode.Correct`: success colour). */
    Correct,

    /** The pattern failed (`DisplayMode.Wrong`: error colour + the stock 1000ms alpha flicker). */
    Wrong,

    /**
     * Replays the pattern like `DisplayMode.Animate`: each cell lights up over
     * [LockPatternDefaults.AnimateCellMillis] (700ms, `MILLIS_PER_CIRCLE_ANIMATING`) with the connecting line
     * catching up, then the full pattern holds for one extra tick before the loop restarts.
     */
    Animate,
}

/**
 * Colours of a [LockPatternView].
 *
 * Stock sources: `couiRegularColor` (`coui_lock_pattern_dot_color` `#4D000000` / `#66FFFFFF`),
 * `couiPathColor` (`coui_lock_pattern_path_color`), `couiSuccessColor`, `couiErrorColor` (`#EA3447`) and
 * `couiOuterCircleMaxAlpha` (0.1 light / 0.2 dark).
 */
@Immutable
data class LockPatternColors(
    val regularColor: Color,
    val pathColor: Color,
    val successColor: Color,
    val errorColor: Color,
    val outerCircleMaxAlpha: Float,
) {
    internal fun currentColor(mode: LockPatternDisplayMode): Color =
        when (mode) {
            LockPatternDisplayMode.Correct, LockPatternDisplayMode.Animate -> successColor
            LockPatternDisplayMode.Wrong -> errorColor
        }
}

/** Defaults for [LockPatternView], taken from `COUILockPatternView` and its `Widget.COUI.*` styles. */
object LockPatternDefaults {
    /** `coui_lock_pattern_view_width`: the wrap-content width in `onMeasure()`. */
    val Width = 312.dp

    /** `coui_lock_pattern_view_height`: the wrap-content height in `onMeasure()`. */
    val Height = 280.dp

    /** `coui_lock_pattern_view_horizontal_padding` (`android:paddingStart/End` of the style). */
    val HorizontalPadding = 21.dp

    /** `coui_lock_pattern_view_vertical_padding` (`android:paddingTop/Bottom` of the style). */
    val VerticalPadding = 5.dp

    /** `lock_pattern_dot_size`: the regular dot's diameter (`cellState.radius = dotSize / 2`). */
    val DotSize = 10.dp

    /** `lock_pattern_dot_line_width`: the path stroke width. */
    val PathWidth = 2.dp

    /** `COUILockPatternView.mHitFactor`: the hit square is `square * 0.6`. */
    val HitFactor = 0.6f

    /** `coui_lock_pattern_inner_circle.png` (90px at xxhdpi): the 30dp activation glow. */
    val InnerGlowDiameter = 30.dp

    /** `coui_lock_pattern_outer_circle.png` (90px at xxhdpi, ~9dp content): the ripple source dot. */
    val OuterDotDiameter = 9.dp

    /** `startOuterAnimation()`: `outerCircleScale` animates `1f -> 7f`. */
    val OuterCircleMaxScale = 7f

    /** `startInnerAnimation()`: 230ms. */
    val InnerAnimationMillis = 230

    /** `startOuterAnimation()`: 460ms. */
    val OuterAnimationMillis = 460

    /** `startLineEndAnimation()`: 100ms with the view's `mFastOutSlowInInterpolator`. */
    val LineEndAnimationMillis = 100

    /** `startWrongAnimator()`: 1000ms keyframes. */
    val WrongAnimationMillis = 1000

    /**
     * `color_lock_pattern_view_max_translate_y` = 258dp. It only feeds `getEnterAnim()`'s per-cell
     * `cellNumberTranslateY`, and `cellNumberAlpha` / `cellNumberTranslateY` are never read in
     * `COUILockPatternView.onDraw()`, so the enter animation has no visible translation - the token is dead
     * for rendering (it is **not** a wrong-mode shake).
     */
    val MaxTranslateY = 258.dp

    /** `MILLIS_PER_CIRCLE_ANIMATING`: one replayed cell per 700ms in `DisplayMode.Animate`. */
    val AnimateCellMillis = 700

    /** The stock colours, keyed on the current theme. */
    @Composable
    fun colors(
        regularColor: Color = lockPatternRegular(isLockPatternDarkTheme()),
        pathColor: Color = lockPatternPath(isLockPatternDarkTheme()),
        successColor: Color = lockPatternSuccess(isLockPatternDarkTheme()),
        errorColor: Color = Color(0xFFEA3447),
        outerCircleMaxAlpha: Float = if (isLockPatternDarkTheme()) 0.2f else 0.1f,
    ): LockPatternColors =
        remember(regularColor, pathColor, successColor, errorColor, outerCircleMaxAlpha) {
            LockPatternColors(
                regularColor = regularColor,
                pathColor = pathColor,
                successColor = successColor,
                errorColor = errorColor,
                outerCircleMaxAlpha = outerCircleMaxAlpha,
            )
        }
}

@Composable
private fun isLockPatternDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/** `coui_lock_pattern_dot_color` / `_dark_color`. */
internal fun lockPatternRegular(dark: Boolean): Color = if (dark) Color(0x66FFFFFF) else Color(0x4D000000)

/** `coui_lock_pattern_path_color` / `_dark_color`. */
internal fun lockPatternPath(dark: Boolean): Color = if (dark) Color(0xFFFFFFFF) else Color(0xFF000000)

/** `coui_lock_pattern_success_color` / `_dark_color`. */
internal fun lockPatternSuccess(dark: Boolean): Color = if (dark) Color(0xFFFFFFFF) else Color(0xFF000000)

/**
 * The ColorOS pattern lock: `COUILockPatternView` (`com.coui.appcompat.lockview`).
 *
 * A 3 × 3 grid of 90dp square cells (default 312dp × 280dp view with 21dp / 5dp paddings). Each cell shows the
 * 10dp regular dot; dragging connects the cells with a 2dp round path. When a cell is activated, the stock
 * 30dp inner glow fades in (230ms) and a 9dp outer dot ripples out to 7× (460ms). [pattern] is the displayed
 * pattern (single source of truth), [displayMode] selects the success / error colour, and an error pattern
 * plays the stock 1000ms alpha flicker.
 *
 * @param onPatternDetected Called on release with the ordered cell indices (`0..8`, row-major).
 * @param modifier Applied to the view; the stock wrap-content size is
 *   `LockPatternDefaults.Width x LockPatternDefaults.Height`.
 * @param enabled Whether the view accepts input and draws the enabled colours.
 * @param pattern The pattern to display, e.g. the last `onPatternDetected` result.
 * @param displayMode The stock `DisplayMode`: `Correct` (success colour) or `Wrong` (error colour + flicker).
 * @param onPatternStart Called when a drag starts on a cell.
 * @param onPatternCleared Called when a touch starts outside any cell.
 * @param colors The pattern colours.
 * @param hapticFeedback Whether each new cell fires haptic feedback (stock `performHitFeedback()`).
 * @param stealthMode Stock `mInStealthMode`: draw the dots but not the path.
 */
@Composable
fun LockPatternView(
    onPatternDetected: (List<Int>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    pattern: List<Int> = emptyList(),
    displayMode: LockPatternDisplayMode = LockPatternDisplayMode.Correct,
    onPatternStart: (() -> Unit)? = null,
    onPatternCleared: (() -> Unit)? = null,
    colors: LockPatternColors = LockPatternDefaults.colors(),
    hapticFeedback: Boolean = true,
    stealthMode: Boolean = false,
) {
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val currentOnDetected by rememberUpdatedState(onPatternDetected)
    val currentOnStart by rememberUpdatedState(onPatternStart)
    val currentOnCleared by rememberUpdatedState(onPatternCleared)
    val currentColors by rememberUpdatedState(colors)
    val currentPattern by rememberUpdatedState(pattern)
    val currentMode by rememberUpdatedState(displayMode)

    var gesturePattern by remember { mutableStateOf<List<Int>>(emptyList()) }
    var gestureInProgress by remember { mutableStateOf(false) }
    var touchPoint by remember { mutableStateOf<Offset?>(null) }
    val innerAlphas = remember { MutableList(9) { Animatable(0f) } }
    val rippleProgress = remember { MutableList(9) { Animatable(0f) } }
    // `CellState.lineEndX/Y`: the 100ms catch-up from the finger position to the cell centre
    // (`startLineEndAnimation`). 1f means "settled at the centre".
    val lineEndProgress = remember { MutableList(9) { Animatable(1f) } }
    val lineEndStart = remember { MutableList(9) { Offset.Zero } }
    val latestTouchPoint by rememberUpdatedState(touchPoint)
    val errorAlpha = remember { Animatable(1f) }
    // Elapsed time of the `DisplayMode.Animate` replay; -1 when the mode is not animate.
    var animateElapsedMillis by remember { mutableLongStateOf(-1L) }

    // Activate the per-cell animations for the cells of the displayed / in-progress pattern.
    LaunchedEffect(pattern, gesturePattern) {
        if (displayMode == LockPatternDisplayMode.Animate) {
            // The replay lights the cells with plain success-coloured dots; no activation ripple.
            for (index in 0 until 9) {
                innerAlphas[index].snapTo(0f)
                rippleProgress[index].snapTo(0f)
            }
            return@LaunchedEffect
        }
        val active = (gesturePattern.ifEmpty { pattern }).toSet()
        for (index in 0 until 9) {
            if (index in active) {
                if (innerAlphas[index].targetValue < 1f) {
                    scope.launch {
                        rippleProgress[index].snapTo(0f)
                        rippleProgress[index].animateTo(1f, tween(LockPatternDefaults.OuterAnimationMillis, easing = LinearOutSlowInEasing))
                    }
                    innerAlphas[index].animateTo(1f, tween(LockPatternDefaults.InnerAnimationMillis))
                    // `startLineEndAnimation()`: from the current finger position to the cell centre.
                    lineEndStart[index] = latestTouchPoint ?: Offset.Zero
                    lineEndProgress[index].snapTo(0f)
                    lineEndProgress[index].animateTo(
                        targetValue = 1f,
                        animationSpec = tween(LockPatternDefaults.LineEndAnimationMillis, easing = FastOutSlowInEasing),
                    )
                }
            } else {
                innerAlphas[index].snapTo(0f)
                rippleProgress[index].snapTo(0f)
                lineEndProgress[index].snapTo(1f)
            }
        }
    }
    // The stock wrong-mode flicker: pathAlpha keyframes 1 -> .35 -> 1 -> .15 -> .5 -> 0 over 1000ms.
    LaunchedEffect(displayMode, pattern) {
        if (displayMode == LockPatternDisplayMode.Wrong) {
            errorAlpha.snapTo(1f)
            errorAlpha.animateTo(
                targetValue = 0f,
                animationSpec =
                    androidx.compose.animation.core.keyframes {
                        durationMillis = LockPatternDefaults.WrongAnimationMillis
                        1f at 0
                        0.35f at 200
                        1f at 400
                        0.15f at 600
                        0.5f at 800
                        0f at 1000
                    },
            )
        } else {
            errorAlpha.snapTo(1f)
        }
    }
    // `DisplayMode.Animate`: `MILLIS_PER_CIRCLE_ANIMATING` = 700ms per cell, looping once the whole pattern
    // has been shown plus one extra tick.
    LaunchedEffect(displayMode, pattern) {
        if (displayMode != LockPatternDisplayMode.Animate || pattern.isEmpty()) {
            animateElapsedMillis = -1L
            return@LaunchedEffect
        }
        var startNanos = -1L
        while (true) {
            withInfiniteAnimationFrameNanos { frameNanos ->
                if (startNanos < 0L) startNanos = frameNanos
                animateElapsedMillis = (frameNanos - startNanos) / 1_000_000L
            }
        }
    }

    Canvas(
        modifier =
            modifier
                .defaultMinSize(minWidth = LockPatternDefaults.Width, minHeight = LockPatternDefaults.Height)
                .pointerInput(enabled, stealthMode) {
                    if (!enabled) return@pointerInput
                    detectDragGestures(
                        onDragStart = { start ->
                            val metrics = lockPatternMetrics(size.width.toFloat(), size.height.toFloat(), density)
                            val hit = metrics.cellAt(start)
                            gesturePattern = if (hit != null) listOf(hit) else emptyList()
                            gestureInProgress = hit != null
                            touchPoint = start
                            if (hit == null) currentOnCleared?.invoke() else currentOnStart?.invoke()
                            if (hit != null) {
                                if (hapticFeedback) haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                            }
                        },
                        onDragEnd = {
                            if (gestureInProgress) currentOnDetected(currentPatternResolved(gesturePattern, currentPattern))
                            gestureInProgress = false
                            touchPoint = null
                        },
                        onDragCancel = {
                            gestureInProgress = false
                            touchPoint = null
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val metrics = lockPatternMetrics(size.width.toFloat(), size.height.toFloat(), density)
                            touchPoint = change.position
                            val hit = metrics.cellAt(change.position) ?: return@detectDragGestures
                            val previous = gesturePattern.lastOrNull()
                            if (previous == null) {
                                gesturePattern = listOf(hit)
                                gestureInProgress = true
                                if (hapticFeedback) haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                                return@detectDragGestures
                            }
                            if (hit == previous) return@detectDragGestures
                            val additions = mutableListOf<Int>()
                            metrics.skippedCellBetween(previous, hit)?.let { additions.add(it) }
                            additions.add(hit)
                            gesturePattern = gesturePattern + additions.filter { it !in gesturePattern }
                            if (hapticFeedback) haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                        },
                    )
                },
    ) {
        val metrics = lockPatternMetrics(size.width, size.height, density)
        val mode = if (gestureInProgress) null else displayMode
        // `DisplayMode.Animate`: only the cells replayed so far are active, and the line reaches part-way
        // towards the next cell (`mInProgressX/Y` in the stock `onDraw()`).
        val animateFrame =
            if (mode == LockPatternDisplayMode.Animate && animateElapsedMillis >= 0L && pattern.isNotEmpty()) {
                lockPatternAnimateFrame(pattern.size, animateElapsedMillis)
            } else {
                null
            }
        val active =
            when {
                gesturePattern.isNotEmpty() -> gesturePattern
                animateFrame != null -> pattern.take(animateFrame.drawnCellCount)
                else -> pattern
            }
        val animatePartialTarget =
            animateFrame?.takeIf { it.drawnCellCount in 1 until pattern.size }?.let { frame ->
                val from = metrics.centerOf(pattern[frame.drawnCellCount - 1])
                val to = metrics.centerOf(pattern[frame.drawnCellCount])
                Offset(
                    x = from.x + (to.x - from.x) * frame.fractionToNextCell,
                    y = from.y + (to.y - from.y) * frame.fractionToNextCell,
                )
            }
        val pathAlpha = if (mode == LockPatternDisplayMode.Wrong) errorAlpha.value else 1f
        val currentColor =
            when {
                mode == null -> colors.regularColor
                else -> colors.currentColor(mode)
            }
        val dotRadius = LockPatternDefaults.DotSize.toPx() / 2f
        val innerRadius = LockPatternDefaults.InnerGlowDiameter.toPx() / 2f
        val outerRadius = LockPatternDefaults.OuterDotDiameter.toPx() / 2f
        val enabledAlpha = if (enabled) 1f else 0.4f

        // Regular dots for all nine cells.
        for (row in 0 until 3) {
            for (column in 0 until 3) {
                val center = Offset(metrics.columnCenters[column], metrics.rowCenters[row])
                val index = row * 3 + column
                if (index !in active) {
                    drawCircle(
                        color = colors.regularColor.copy(alpha = colors.regularColor.alpha * enabledAlpha),
                        radius = dotRadius,
                        center = center,
                    )
                }
            }
        }
        // Path (stock `mCurrentPath`).
        if (!stealthMode && active.isNotEmpty()) {
            val path = Path()
            val first = metrics.centerOf(active.first())
            path.moveTo(first.x, first.y)
            for (index in active.drop(1)) {
                // `onDraw()`: a cell whose `lineEndX/Y` animator is still running ends the segment at the
                // interpolated finger -> centre position instead of the centre.
                val center = metrics.centerOf(index)
                val progress = lineEndProgress[index].value
                val end =
                    if (progress < 1f) {
                        lockPatternLineEndPosition(start = lineEndStart[index], end = center, progress = progress)
                    } else {
                        center
                    }
                path.lineTo(end.x, end.y)
            }
            if (gestureInProgress) {
                val last = metrics.centerOf(active.last())
                path.moveTo(last.x, last.y)
                touchPoint?.let { path.lineTo(it.x, it.y) }
            } else if (animatePartialTarget != null) {
                val last = metrics.centerOf(active.last())
                path.moveTo(last.x, last.y)
                path.lineTo(animatePartialTarget.x, animatePartialTarget.y)
            }
            drawPath(
                path = path,
                color = currentColor.copy(alpha = currentColor.alpha * pathAlpha * enabledAlpha),
                style =
                    Stroke(
                        width = LockPatternDefaults.PathWidth.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
            )
        }
        if (animateFrame != null) {
            // The replay lights drawn cells with plain success-coloured dots; it does not run the
            // activation glow/ripple animations.
            for (index in active) {
                drawCircle(
                    color = currentColor.copy(alpha = currentColor.alpha * pathAlpha * enabledAlpha),
                    radius = dotRadius,
                    center = metrics.centerOf(index),
                )
            }
            return@Canvas
        }
        // Inner glow + outer ripple for active cells.
        for (index in active) {
            val center = metrics.centerOf(index)
            val inner = innerAlphas[index].value
            val ripple = rippleProgress[index].value
            if (inner > 0f) {
                drawCircle(
                    brush =
                        Brush.radialGradient(
                            colors = listOf(currentColor.copy(alpha = inner * pathAlpha * enabledAlpha), Color.Transparent),
                            center = center,
                            radius = innerRadius,
                        ),
                    radius = innerRadius,
                    center = center,
                )
            }
            if (ripple > 0f && ripple < 1f) {
                val scale = 1f + (LockPatternDefaults.OuterCircleMaxScale - 1f) * ripple
                val rippleAlpha = max(0f, sin((PI * ripple).toFloat())) * colors.outerCircleMaxAlpha
                if (rippleAlpha > 0f) {
                    drawCircle(
                        color = currentColor.copy(alpha = rippleAlpha * enabledAlpha),
                        radius = outerRadius * scale,
                        center = center,
                    )
                }
            }
        }
    }
}

/** `startLineEndAnimation()`: linear interpolation from the finger position to the cell centre. */
internal fun lockPatternLineEndPosition(
    start: Offset,
    end: Offset,
    progress: Float,
): Offset {
    val fraction = progress.coerceIn(0f, 1f)
    return Offset(
        x = start.x + (end.x - start.x) * fraction,
        y = start.y + (end.y - start.y) * fraction,
    )
}

private fun currentPatternResolved(
    gesture: List<Int>,
    displayed: List<Int>,
): List<Int> = gesture.ifEmpty { displayed }

/** One frame of the `DisplayMode.Animate` replay. */
@Immutable
internal data class LockPatternAnimateFrame(
    /** Cells of the pattern that are fully lit up. */
    val drawnCellCount: Int,
    /** Progress (0..1) of the line towards the next cell; only meaningful while a next cell exists. */
    val fractionToNextCell: Float,
)

/**
 * `COUILockPatternView.onDraw()`'s `DisplayMode.Animate` timing:
 * `elapsed % ((size + 1) * 700)`, `cellIndex = elapsed / 700`, and the per-cell fraction `elapsed % 700 / 700`.
 * The pattern therefore replays one cell every [cellMillis] and holds the full pattern for one extra tick.
 */
internal fun lockPatternAnimateFrame(
    patternSize: Int,
    elapsedMillis: Long,
    cellMillis: Long = LockPatternDefaults.AnimateCellMillis.toLong(),
): LockPatternAnimateFrame {
    if (patternSize <= 0 || cellMillis <= 0L) return LockPatternAnimateFrame(0, 0f)
    val duration = (patternSize + 1) * cellMillis
    val elapsed = ((elapsedMillis % duration) + duration) % duration
    val cellIndex = (elapsed / cellMillis).toInt().coerceIn(0, patternSize)
    val fraction = (elapsed % cellMillis).toFloat() / cellMillis.toFloat()
    return LockPatternAnimateFrame(drawnCellCount = cellIndex, fractionToNextCell = fraction)
}

/** Geometry of a [LockPatternView], derived from `COUILockPatternView.onSizeChanged()`. */
internal data class LockPatternMetrics(
    val squareWidth: Float,
    val squareHeight: Float,
    val horizontalPadding: Float,
    val verticalPadding: Float,
    val hitFactor: Float,
) {
    val columnCenters: List<Float>
        get() = List(3) { column -> horizontalPadding + column * squareWidth + squareWidth / 2f }

    val rowCenters: List<Float>
        get() = List(3) { row -> verticalPadding + row * squareHeight + squareHeight / 2f }

    fun centerOf(index: Int): Offset = Offset(columnCenters[index % 3], rowCenters[index / 3])

    /** `getColumnHit()` / `getRowHit()`: a `square * 0.6` square centred on the cell. */
    fun cellAt(position: Offset): Int? {
        val column = hitAxis(position.x, horizontalPadding, squareWidth)
        val row = hitAxis(position.y, verticalPadding, squareHeight)
        return if (column >= 0 && row >= 0) row * 3 + column else null
    }

    /**
     * `detectAndAddHit()`: when the move jumps over a cell by two in a row or column, the skipped middle cell
     * is added first.
     */
    fun skippedCellBetween(
        from: Int,
        to: Int,
    ): Int? {
        val rowDelta = (to / 3) - (from / 3)
        val columnDelta = (to % 3) - (from % 3)
        val skippedRow = if (kotlin.math.abs(rowDelta) == 2 && kotlin.math.abs(columnDelta) != 1) (from / 3) + rowDelta / 2 else from / 3
        val skippedColumn =
            if (kotlin.math.abs(columnDelta) == 2 &&
                kotlin.math.abs(rowDelta) != 1
            ) {
                (from % 3) + columnDelta / 2
            } else {
                from % 3
            }
        val skipped = skippedRow * 3 + skippedColumn
        return skipped.takeIf { it != from && it != to }
    }

    private fun hitAxis(
        value: Float,
        padding: Float,
        square: Float,
    ): Int {
        val size = hitFactor * square
        val start = padding + (square - size) / 2f
        for (index in 0 until 3) {
            val begin = index * square + start
            if (value >= begin && value <= begin + size) return index
        }
        return -1
    }
}

/**
 * The stock layout:
 *
 * - `mSquareWidth = (width - paddingLeft - paddingRight) / 3` (same for height);
 * - `getCenterXForColumn(i) = paddingLeft + i * square + square / 2`;
 * - hit boxes are `square * mHitFactor` (0.6) centred on the cell.
 */
internal fun lockPatternMetrics(
    width: Float,
    height: Float,
    density: Float,
): LockPatternMetrics {
    val horizontalPadding = LockPatternDefaults.HorizontalPadding.value * density
    val verticalPadding = LockPatternDefaults.VerticalPadding.value * density
    return LockPatternMetrics(
        squareWidth = ((width - horizontalPadding * 2f) / 3f).coerceAtLeast(0f),
        squareHeight = ((height - verticalPadding * 2f) / 3f).coerceAtLeast(0f),
        horizontalPadding = horizontalPadding,
        verticalPadding = verticalPadding,
        hitFactor = LockPatternDefaults.HitFactor,
    )
}
