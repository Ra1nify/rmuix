// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * Colours of a [CodeInput].
 *
 * @param cellColor The filled cell background (`?attr/couiColorCardBackground`).
 * @param focusStrokeColor The border of the focused cell (`?attr/couiColorPrimary`).
 * @param digitColor The entered digit (`?attr/couiColorPrimaryNeutral`).
 * @param securityDotColor The dot drawn in secure mode (`coui_code_input_security_circle_color`).
 */
@Immutable
data class CodeInputColors(
    val cellColor: Color,
    val focusStrokeColor: Color,
    val digitColor: Color,
    val securityDotColor: Color,
)

/**
 * Defaults for [CodeInput], from `coui_phone_code_layout.xml`, `COUICodeInputView` and
 * `COUICodeInputHelper`.
 */
object CodeInputDefaults {
    /** `couiCodeInputCount` attribute default. */
    val Count = 6

    /** `coui_code_input_cell_width`. */
    val CellWidth = 42.dp

    /** `coui_code_input_cell_height`. */
    val CellHeight = 46.dp

    /** `?attr/couiRoundCornerS` = `coui_round_corner_s`, the radius `CodeItemView` draws with. */
    val CellCornerRadius = 8.dp

    /** `coui_code_input_cell_stroke_width`. */
    val CellStrokeWidth = 1.6.dp

    /** `coui_code_input_cell_text_size` (declared `30dp`, used as a text size, so `30sp`). */
    val CellTextSize = 30.sp

    /** `coui_code_input_cell_security_circle_radius`. */
    val SecurityDotRadius = 5.dp

    /** `coui_code_input_cell_margin_horizontal`: the starting gap for the adaptive clamp. */
    val CellGap = 4.dp

    /** `coui_code_input_cell_min_margin_horizontal`. */
    val MinCellGap = 2.dp

    /** `coui_code_input_cell_max_margin_horizontal`. */
    val MaxCellGap = 8.dp

    /** `coui_code_input_layout_margin_start`, the space the gap formula reserves on both sides. */
    val HorizontalMargin = 16.dp

    /** `COUICodeInputHelper.INBOX_APPEAR_ANIMATOR_DURATION`. */
    const val FOCUS_APPEAR_MILLIS = 100

    /** `COUICodeInputHelper.INBOX_DELAY_ANIMATOR_DURATION`. */
    const val FOCUS_DISAPPEAR_MILLIS = 33

    /** `COUICodeInputHelper.NUMBER_APPEAR_ANIMATOR_DURATION`. */
    const val DIGIT_APPEAR_MILLIS = 100

    /** `COUICodeInputHelper.startNumberScale`. */
    const val DIGIT_APPEAR_SCALE = 0.6f

    /** `COUICodeInputHelper.INBOX_DELAY_ANIMATOR_DURATION`: the focus stroke appears after 33ms. */
    const val FOCUS_APPEAR_DELAY_MILLIS = 33

    /** `COUICodeInputHelper.NUMBER_DELAY_ANIMATOR_DURATION`: the outgoing digit fades after 33ms. */
    const val DIGIT_DISAPPEAR_DELAY_MILLIS = 33

    /**
     * The stock colours.
     *
     * `?attr/couiColorCardBackground` is `#FFFFFF` / `#1AFFFFFF`, `?attr/couiColorPrimaryNeutral` is
     * `#E6000000` / `#E6FFFFFF` and `coui_code_input_security_circle_color` is `#D8000000` /
     * `#D8FFFFFF`. The focus stroke uses [RmuixTheme.colorScheme]'s primary.
     */
    @Composable
    fun codeInputColors(
        cellColor: Color = stockCardBackground(),
        focusStrokeColor: Color = RmuixTheme.colorScheme.primary,
        digitColor: Color = stockPrimaryNeutral(),
        securityDotColor: Color = if (isCodeInputDarkTheme()) Color(0xD8FFFFFF) else Color(0xD8000000),
    ): CodeInputColors =
        remember(cellColor, focusStrokeColor, digitColor, securityDotColor) {
            CodeInputColors(
                cellColor = cellColor,
                focusStrokeColor = focusStrokeColor,
                digitColor = digitColor,
                securityDotColor = securityDotColor,
            )
        }

    @Composable
    private fun stockCardBackground(): Color = if (isCodeInputDarkTheme()) Color(0x1AFFFFFF) else Color(0xFFFFFFFF)

    @Composable
    private fun stockPrimaryNeutral(): Color = if (isCodeInputDarkTheme()) Color(0xE6FFFFFF) else Color(0xE6000000)
}

@Composable
private fun isCodeInputDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() <
        RmuixTheme.colorScheme.onBackground.luminance()

/** How `COUISettings.CodeItemView.setNumber()` drives the digit animators. */
internal enum class CodeInputDigitTransition {
    NONE,
    APPEAR,
    DISAPPEAR,
    REPLACE,
}

/**
 * `COUICodeInputView.CodeItemView.setNumber(str)`:
 *
 * - empty -> digit: `startNumberAnimator(true)` (alpha 0..1, scale 0.6..1, 100ms, no delay);
 * - digit -> empty: `startNumberAnimator(false)` (the **old** digit keeps scale 1 and fades over 100ms
 *   after the 33ms `NUMBER_DELAY_ANIMATOR_DURATION`);
 * - digit -> other digit: no animator, the cell just redraws.
 */
internal fun codeInputDigitTransition(
    previous: String?,
    next: String?,
): CodeInputDigitTransition =
    when {
        previous.isNullOrEmpty() && next.isNullOrEmpty() -> CodeInputDigitTransition.NONE
        previous.isNullOrEmpty() -> CodeInputDigitTransition.APPEAR
        next.isNullOrEmpty() -> CodeInputDigitTransition.DISAPPEAR
        previous != next -> CodeInputDigitTransition.REPLACE
        else -> CodeInputDigitTransition.NONE
    }

/**
 * The ColorOS one-time-code input: `COUICodeInputView` (`coui_phone_code_layout.xml`).
 *
 * A row of boxed cells with a transparent text field layered over them; the field captures the digits
 * while the cells draw them, which is exactly how stock works (`code_container_edittext` sits over
 * `code_container_layout` with a transparent text colour).
 *
 * The cell sizes shrink on narrow screens: stock scales both by `min(screenWidthDp, 360) / 360`, and the
 * gap between cells is derived from the available width and clamped to
 * `coui_code_input_cell_min_margin_horizontal`..`coui_code_input_cell_max_margin_horizontal`.
 *
 * The selected cell is the next one to fill and, once every cell is filled, the last one - the same
 * rule as `COUICodeInputView.updateViewsByCodesChange`.
 *
 * Not modelled: `COUICodeInputHelper`'s number cross-fade (the outgoing digit animating away while the
 * incoming one scales in); this implementation only animates a cell in, and its durations are the
 * helper's own constants.
 *
 * @param value The entered digits.
 * @param onValueChange Called with the new digits whenever they change.
 * @param modifier The modifier to be applied to the input.
 * @param count Number of cells (`couiCodeInputCount`).
 * @param enabled Whether the input accepts edits.
 * @param secure Draw a dot per entered digit instead of the digit (`couiEnableSecurityInput`).
 * @param colors The colours of the cells.
 * @param cellWidth Width of one cell before the narrow-screen scaling.
 * @param cellHeight Height of one cell before the narrow-screen scaling.
 * @param onCompleted Called with `value` once `count` digits have been entered.
 */
@Composable
fun CodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    count: Int = CodeInputDefaults.Count,
    enabled: Boolean = true,
    secure: Boolean = false,
    colors: CodeInputColors = CodeInputDefaults.codeInputColors(),
    cellWidth: Dp = CodeInputDefaults.CellWidth,
    cellHeight: Dp = CodeInputDefaults.CellHeight,
    onCompleted: ((String) -> Unit)? = null,
) {
    val digits = remember(value, count) { value.filter { it.isDigit() }.take(count) }
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val screenWidthDp =
        with(density) {
            windowInfo.containerSize.width
                .toDp()
                .value
        }
    // `setCodeItemWidth`: both dimensions scale by min(screenWidthDp, 360) / 360.
    val scale = remember(screenWidthDp) { (minOf(screenWidthDp, 360f) / 360f).coerceIn(0f, 1f) }
    val scaledCellWidth = cellWidth * scale
    val scaledCellHeight = cellHeight * scale
    val focusedIndex = digits.length.coerceAtMost(count - 1)

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // `getCellMarginHorizontal`: spread what is left over the `2 * count - 2` gaps, then clamp.
        val rawGap =
            with(density) {
                val free = maxWidth - scaledCellWidth * count - CodeInputDefaults.HorizontalMargin * 2
                val gaps = (count * 2 - 2).coerceAtLeast(1)
                (free / gaps).toPx()
            }
        val gap =
            with(density) {
                rawGap.toDp().coerceIn(CodeInputDefaults.MinCellGap, CodeInputDefaults.MaxCellGap)
            }
        Box(contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                // Stock puts `mCellMarginHorizontal` on both `marginStart` and `marginEnd` of every
                // interior cell, so the visible gap between two cells is twice the computed value.
                horizontalArrangement = Arrangement.spacedBy(gap * 2, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(count) { index ->
                    CodeInputCell(
                        digit = digits.getOrNull(index)?.toString(),
                        selected = enabled && index == focusedIndex,
                        secure = secure,
                        colors = colors,
                        cellWidth = scaledCellWidth,
                        cellHeight = scaledCellHeight,
                    )
                }
            }
            // Stock keeps a fully transparent `EditText` over the cells and drives them from its
            // `TextWatcher`; the Compose equivalent is an invisible `BasicTextField` in the same box.
            BasicTextField(
                value = digits,
                onValueChange = { raw ->
                    val next = raw.filter { it.isDigit() }.take(count)
                    if (next != digits) {
                        onValueChange(next)
                        if (next.length == count) onCompleted?.invoke(next)
                    }
                },
                modifier = Modifier.matchParentSize(),
                enabled = enabled,
                singleLine = true,
                textStyle = TextStyle(color = Color.Transparent),
                cursorBrush = SolidColor(Color.Transparent),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
    }
}

@Composable
private fun CodeInputCell(
    digit: String?,
    selected: Boolean,
    secure: Boolean,
    colors: CodeInputColors,
    cellWidth: Dp,
    cellHeight: Dp,
) {
    // INBOX_APPEAR/INBOX_DELAY: the focus stroke fades over 100ms; appearing waits the 33ms delay.
    val strokeAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec =
            tween(
                durationMillis = CodeInputDefaults.FOCUS_APPEAR_MILLIS,
                delayMillis = if (selected) CodeInputDefaults.FOCUS_APPEAR_DELAY_MILLIS else 0,
            ),
        label = "codeInputStroke",
    )
    // NUMBER_APPEAR/NUMBER_DELAY: the digit animators. Security cells bypass them (`mIsEnableSecurity`
    // guards `setNumber`), so their dot appears/disappears instantly.
    val digitAlpha = remember { Animatable(if (digit != null) 1f else 0f) }
    val digitScale = remember { Animatable(1f) }
    var displayedDigit by remember { mutableStateOf(digit) }
    var initialized by remember { mutableStateOf(false) }
    LaunchedEffect(digit, secure) {
        if (!initialized) {
            initialized = true
            return@LaunchedEffect
        }
        val previous = displayedDigit
        if (secure) {
            displayedDigit = digit
            digitAlpha.snapTo(if (digit != null) 1f else 0f)
            digitScale.snapTo(1f)
            return@LaunchedEffect
        }
        when (codeInputDigitTransition(previous, digit)) {
            CodeInputDigitTransition.NONE -> {
                Unit
            }

            CodeInputDigitTransition.APPEAR -> {
                displayedDigit = digit
                digitScale.snapTo(CodeInputDefaults.DIGIT_APPEAR_SCALE)
                digitAlpha.snapTo(0f)
                digitAlpha.animateTo(1f, tween(durationMillis = CodeInputDefaults.DIGIT_APPEAR_MILLIS))
                digitScale.animateTo(1f, tween(durationMillis = CodeInputDefaults.DIGIT_APPEAR_MILLIS))
            }

            CodeInputDigitTransition.DISAPPEAR -> {
                // The old digit keeps scale 1 and fades out after the 33ms delay.
                digitScale.snapTo(1f)
                digitAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec =
                        tween(
                            durationMillis = CodeInputDefaults.DIGIT_APPEAR_MILLIS,
                            delayMillis = CodeInputDefaults.DIGIT_DISAPPEAR_DELAY_MILLIS,
                        ),
                )
                displayedDigit = null
            }

            CodeInputDigitTransition.REPLACE -> {
                displayedDigit = digit
                digitAlpha.snapTo(1f)
                digitScale.snapTo(1f)
            }
        }
    }

    Box(
        modifier =
            Modifier
                .size(width = cellWidth, height = cellHeight)
                .drawBehind {
                    drawRoundRect(
                        color = colors.cellColor,
                        cornerRadius = CornerRadius(CodeInputDefaults.CellCornerRadius.toPx()),
                    )
                    if (strokeAlpha > 0f) {
                        val strokePx = CodeInputDefaults.CellStrokeWidth.toPx()
                        // `CodeItemView.onDraw` insets the stroke by half its width.
                        drawRoundRect(
                            color = colors.focusStrokeColor.copy(alpha = strokeAlpha),
                            topLeft = Offset(strokePx / 2f, strokePx / 2f),
                            size = Size(size.width - strokePx, size.height - strokePx),
                            cornerRadius = CornerRadius(CodeInputDefaults.CellCornerRadius.toPx()),
                            style = Stroke(width = strokePx),
                        )
                    }
                },
        contentAlignment = Alignment.Center,
    ) {
        if (secure) {
            if (digit != null) {
                Box(
                    modifier =
                        Modifier
                            .size(CodeInputDefaults.SecurityDotRadius * 2)
                            .drawBehind {
                                drawCircle(color = colors.securityDotColor)
                            },
                )
            }
        } else {
            displayedDigit?.let { shown ->
                Text(
                    text = shown,
                    modifier = Modifier.scaled(digitScale.value),
                    fontSize = CodeInputDefaults.CellTextSize,
                    color = colors.digitColor.copy(alpha = digitAlpha.value),
                    maxLines = 1,
                )
            }
        }
    }
}

/** Scales the content around its centre, matching the `Canvas.scale` the stock cell applies. */
private fun Modifier.scaled(scale: Float): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0) {
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin.Center
            }
        }
    }
