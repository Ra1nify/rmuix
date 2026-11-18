// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Delete
import io.github.ra1nify.rmuix.kmp.theme.LocalTextStyles
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.launch

/**
 * Colours of a [NumericKeyboard].
 *
 * The stock `COUINumericKeyboard` resolves its colours from `couiNumPressColor`, `couiNumberColor`,
 * `couiWordTextNormalColor` and `couiCircleMaxAlpha`; the `Widget.COUI.COUINumericKeyboard` style is the
 * white-on-dark variant (#FFFFFF digits, #B3FFFFFF letters, 0.12 circle alpha) and the `.Dark` styles flip to
 * `coui_color_label_primary_dark`. This library follows the theme, like the rest of the components.
 */
@Immutable
data class NumericKeyboardColors(
    val numberColor: Color,
    val pressCircleColor: Color,
    val deleteColor: Color,
    val finishColor: Color,
)

/** Defaults for [NumericKeyboard], taken from `COUINumericKeyboard` and `coui_*_numeric_keyboard_*`. */
object NumericKeyboardDefaults {
    /** `coui_numeric_keyboard_view_width`: the wrap-content width in `onMeasure()`. */
    val Width = 248.dp

    /** `coui_numeric_keyboard_view_height`: the wrap-content height in `onMeasure()`. */
    val Height = 326.dp

    /** `coui_numeric_keyboard_view_size`: `mViewSize`, used for both `mCellWidth` and `mCellHeight`. */
    val CellSize = 68.dp

    /** `number_keyboard_number_size` / `couiNumberTextSize`: the digit paint size. */
    val NumberTextSize = 32.dp

    /**
     * `'wght'` of the digit paint: `COUINumericKeyboard` requests
     * `FONT_VARIATION_DEFAULT` (550) + `font_variation_default_plus` (`@integer/font_variation_default_plus`
     * = 200) = **750** by default (`getTypeface()` / `updateNumberTextTypeface()`); the system's
     * `font_variation_settings` offset is not portable, so 750 is the stock default used here.
     */
    val NumberFontWeight = FontWeight(750)

    /** `coui_numeric_keyboard_number_offset_y` / `couiNumberOffsetY`. */
    val NumberOffsetY = 1.dp

    /** `coui_number_keyboard_finish_text_size`: the finish label's paint size. */
    val FinishTextSize = 16.dp

    /** The stock delete vector (`coui_number_keyboard_delete.xml`) is 24dp. */
    val DeleteIconSize = 24.dp

    /** `couiCircleMaxAlpha` in `Widget.COUI.COUINumericKeyboard`. */
    val CircleMaxAlpha = 0.12f

    /** `ALPHA_DELAY` 166 / `ALPHA_DURATION` 167 / `FADE_ANIMATOR_TIME` 160: press fade-in time. */
    val PressDurationMillis = 160

    /** `CELL_COLUMN_COUNT`. */
    val ColumnCount = 3

    /** `CELL_ROW_COUNT`. */
    val RowCount = 4

    /** `KEYCODE_0_COLUMN` / `KEYCODE_0_ROW`: the zero key position. */
    val ZeroColumn = 1

    /** Row of the zero key. */
    val ZeroRow = 3

    /** The stock colours, keyed on the current theme. */
    @Composable
    fun colors(
        numberColor: Color = numericKeyboardNumberColor(isNumericKeyboardDarkTheme()),
        pressCircleColor: Color = numberColor.copy(alpha = CircleMaxAlpha),
        deleteColor: Color = numberColor,
        finishColor: Color = numberColor,
    ): NumericKeyboardColors =
        remember(numberColor, pressCircleColor, deleteColor, finishColor) {
            NumericKeyboardColors(
                numberColor = numberColor,
                pressCircleColor = pressCircleColor,
                deleteColor = deleteColor,
                finishColor = finishColor,
            )
        }
}

@Composable
private fun isNumericKeyboardDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/** `?attr/couiColorLabelPrimary` (`coui_color_label_primary` / `_dark`). */
internal fun numericKeyboardNumberColor(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/**
 * The ColorOS numeric keypad: `COUINumericKeyboard` (`com.coui.appcompat.lockview`).
 *
 * A 3 × 4 grid of 68dp keys: digits 1-9, `0` on row 4 column 2 and two side keys. The side keys map to the
 * stock `SideStyle`s: the trailing key shows the 24dp delete vector when [onDelete] is provided (stock
 * `mDeleteStyle`, `SIDE_TYPE_DELETE`), the leading key can show a finish label (stock `mFinishStyle`,
 * `SIDE_TYPE_FINISH`). Pressing a key shows a circular ripple at `couiCircleMaxAlpha` = 0.12 for 160ms.
 *
 * @param onNumber Called with the digit (`0..9`) of the tapped key.
 * @param modifier Applied to the keypad; the stock wrap-content size is
 *   `NumericKeyboardDefaults.Width x NumericKeyboardDefaults.Height`.
 * @param enabled Whether the keypad reacts to input and draws the enabled colours.
 * @param onDelete When non-null, the trailing side key draws the delete vector and calls this.
 * @param showDelete Whether the delete key is visible (stock callers switch the right style to `null`).
 * @param onFinish When non-null, the leading side key shows [finishText] and calls this.
 * @param finishText The finish label (stock `R.string.coui_numeric_keyboard_sure`, 16dp).
 * @param colors The keypad colours.
 */
@Composable
fun NumericKeyboard(
    onNumber: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onDelete: (() -> Unit)? = null,
    showDelete: Boolean = true,
    onFinish: (() -> Unit)? = null,
    finishText: String = "OK",
    colors: NumericKeyboardColors = NumericKeyboardDefaults.colors(),
) {
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    val textStyles = LocalTextStyles.current
    val numberStyle =
        remember(textStyles.main, density) {
            textStyles.main.copy(
                fontSize = with(density) { NumericKeyboardDefaults.NumberTextSize.toSp() },
                fontWeight = NumericKeyboardDefaults.NumberFontWeight,
                color = colors.numberColor,
            )
        }
    val finishStyle =
        remember(textStyles.main, density) {
            textStyles.main.copy(
                fontSize = with(density) { NumericKeyboardDefaults.FinishTextSize.toSp() },
                fontWeight = FontWeight.Medium,
                color = colors.finishColor,
            )
        }
    val deletePainter = rememberVectorPainter(RmuixIcons.Basic.Delete)
    val scope = rememberCoroutineScope()
    var pressedIndex by remember { mutableIntStateOf(-1) }
    val pressAlpha = remember { Animatable(0f) }

    Canvas(
        modifier =
            modifier.pointerInput(enabled, onNumber, onDelete, showDelete, onFinish, finishText) {
                if (!enabled) return@pointerInput
                detectTapGestures(
                    onPress = { offset ->
                        val metrics = numericKeyboardMetrics(size.width.toFloat(), size.height.toFloat(), density.density)
                        val index = metrics.cellIndexAt(offset)
                        pressedIndex = index
                        scope.launch { pressAlpha.animateTo(1f, tween(NumericKeyboardDefaults.PressDurationMillis)) }
                        tryAwaitRelease()
                        pressAlpha.animateTo(0f, tween(NumericKeyboardDefaults.PressDurationMillis))
                        pressedIndex = -1
                    },
                    onTap = { offset ->
                        val metrics = numericKeyboardMetrics(size.width.toFloat(), size.height.toFloat(), density.density)
                        when (val index = metrics.cellIndexAt(offset)) {
                            in 0..11 -> {
                                when (index) {
                                    9 -> if (onFinish != null) onFinish.invoke()
                                    11 -> if (onDelete != null && showDelete) onDelete.invoke()
                                    else -> onNumber(metrics.digitAt(index) ?: return@detectTapGestures)
                                }
                            }
                        }
                    },
                )
            },
    ) {
        val metrics = numericKeyboardMetrics(size.width, size.height, density.density)
        val cell = metrics.cellSize
        val alpha = if (enabled) 1f else 0.4f
        for (row in 0 until NumericKeyboardDefaults.RowCount) {
            for (column in 0 until NumericKeyboardDefaults.ColumnCount) {
                val index = row * NumericKeyboardDefaults.ColumnCount + column
                val centerX = metrics.columnCenters[column]
                val centerY = metrics.rowCenters[row]
                if (index == pressedIndex) {
                    drawCircle(
                        color = colors.pressCircleColor.copy(alpha = colors.pressCircleColor.alpha * pressAlpha.value * alpha),
                        radius = cell / 2f,
                        center = Offset(centerX, centerY),
                    )
                }
                val digit = metrics.digitAt(index)
                when {
                    digit != null -> {
                        val layout = measurer.measure(digit.toString(), numberStyle)
                        drawText(
                            textLayoutResult = layout,
                            topLeft =
                                Offset(
                                    centerX - layout.size.width / 2f,
                                    centerY - layout.size.height / 2f - NumericKeyboardDefaults.NumberOffsetY.toPx(),
                                ),
                            alpha = alpha,
                        )
                    }

                    index == 9 && onFinish != null && finishText.isNotEmpty() -> {
                        val layout = measurer.measure(finishText, finishStyle)
                        drawText(
                            textLayoutResult = layout,
                            topLeft = Offset(centerX - layout.size.width / 2f, centerY - layout.size.height / 2f),
                            alpha = alpha,
                        )
                    }

                    index == 11 && onDelete != null && showDelete -> {
                        val iconSize = NumericKeyboardDefaults.DeleteIconSize.toPx()
                        translate(left = centerX - iconSize / 2f, top = centerY - iconSize / 2f) {
                            with(deletePainter) {
                                draw(
                                    size = Size(iconSize, iconSize),
                                    colorFilter = ColorFilter.tint(colors.deleteColor.copy(alpha = alpha)),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Geometry of a [NumericKeyboard], derived from `COUINumericKeyboard.onSizeChanged()`. */
internal data class NumericKeyboardMetrics(
    val cellSize: Float,
    val horizontalSpacing: Float,
    val verticalSpacing: Float,
    val columnCenters: List<Float>,
    val rowCenters: List<Float>,
) {
    /** Row-major cell index ([row * 3 + column]) under [offset], or `-1` outside the grid. */
    fun cellIndexAt(offset: Offset): Int {
        for (row in rowCenters.indices) {
            for (column in columnCenters.indices) {
                val dx = offset.x - columnCenters[column]
                val dy = offset.y - rowCenters[row]
                if (dx >= -cellSize / 2f && dx <= cellSize / 2f && dy >= -cellSize / 2f && dy <= cellSize / 2f) {
                    return row * 3 + column
                }
            }
        }
        return -1
    }

    /**
     * The digit displayed at the row-major [index]: cells 0-8 are `1..9`, cell 10 is `0` and the side keys
     * (row 4 column 1/3, indices 9 and 11) return `null`.
     */
    fun digitAt(index: Int): Int? =
        when (index) {
            in 0..8 -> index + 1
            10 -> 0
            else -> null
        }
}

/**
 * The stock layout:
 *
 * - `mCellWidth = mCellHeight = mViewSize` (`coui_numeric_keyboard_view_size`, 68dp);
 * - `mHorizontalSpacing = (width - 3 * cell) / 2`;
 * - `mVerticalSpacing = (height - 4 * cell) / 3`;
 * - `getCenterXForColumn(i) = paddingLeft + cell / 2 + cell * i + horizontalSpacing * i` (same for rows).
 */
internal fun numericKeyboardMetrics(
    width: Float,
    height: Float,
    density: Float,
): NumericKeyboardMetrics {
    val cell = NumericKeyboardDefaults.CellSize.value * density
    val horizontalSpacing = ((width - cell * 3f) / 2f).coerceAtLeast(0f)
    val verticalSpacing = ((height - cell * 4f) / 3f).coerceAtLeast(0f)
    return NumericKeyboardMetrics(
        cellSize = cell,
        horizontalSpacing = horizontalSpacing,
        verticalSpacing = verticalSpacing,
        columnCenters = List(3) { index -> cell / 2f + (cell + horizontalSpacing) * index },
        rowCenters = List(4) { index -> cell / 2f + (cell + verticalSpacing) * index },
    )
}
