// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.LocalContentColor
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.hairlinePx
import kotlin.math.roundToInt

/**
 * A [TextField] component with Rmuix style.
 *
 * @param state The [TextFieldState] to be shown in the text field.
 * @param modifier The modifier to be applied to the [TextField].
 * @param insideMargin The margin inside the [TextField].
 * @param colors The [TextFieldColors] applied to the [TextField]. Use
 *   [TextFieldDefaults.textFieldColors] to customize.
 * @param cornerRadius The corner radius of the [TextField].
 * @param label The label to be displayed when the [TextField] is empty.
 * @param useLabelAsPlaceholder Whether to use the label as a placeholder.
 * @param enabled Whether the [TextField] is enabled.
 * @param readOnly Whether the [TextField] is read-only.
 * @param inputTransformation The input transformation to be applied to the [TextField].
 * @param textStyle The text style to be applied to the [TextField].
 * @param keyboardOptions The keyboard options to be applied to the [TextField].
 * @param onKeyboardAction The keyboard action handler for the [TextField].
 * @param lineLimits The line limits for the [TextField].
 * @param leadingIcon The leading icon to be displayed in the [TextField].
 * @param trailingIcon The trailing icon to be displayed in the [TextField].
 * @param onTextLayout The callback to be called when the text layout changes.
 * @param interactionSource The interaction source to be applied to the [TextField].
 * @param cursorBrush The brush to be used for the cursor.
 * @param outputTransformation The output transformation for the text field.
 * @param scrollState The scroll state for the text field.
 */
@Composable
fun TextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    insideMargin: DpSize = TextFieldDefaults.InsideMargin,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    cornerRadius: Dp = TextFieldDefaults.CornerRadius,
    label: String = "",
    useLabelAsPlaceholder: Boolean = false,
    enabled: Boolean = true,
    errorState: Boolean = false,
    lineMode: Boolean = false,
    readOnly: Boolean = false,
    inputTransformation: InputTransformation? = null,
    textStyle: TextStyle = TextFieldDefaults.textStyle(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(colors.borderColor),
    showClearButton: Boolean = false,
    outputTransformation: OutputTransformation? = null,
    scrollState: ScrollState = rememberScrollState(),
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val labelState by remember(label, useLabelAsPlaceholder) {
        derivedStateOf {
            when {
                label.isEmpty() -> LabelAnimState.Hidden
                useLabelAsPlaceholder && state.text.isNotEmpty() -> LabelAnimState.Placeholder
                state.text.isNotEmpty() -> LabelAnimState.Floating
                else -> LabelAnimState.Normal
            }
        }
    }

    val currentOnTextLayout by rememberUpdatedState(onTextLayout)

    val contentColor = LocalContentColor.current
    val resolvedTextStyle =
        remember(textStyle, contentColor) {
            val textColor = textStyle.color.takeOrElse { contentColor }
            textStyle.copy(textColor)
        }

    BasicTextField(
        state = state,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = resolvedTextStyle,
        cursorBrush = cursorBrush,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        lineLimits = lineLimits,
        onTextLayout = currentOnTextLayout,
        interactionSource = interactionSource,
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        scrollState = scrollState,
        decorator =
            TextFieldDecorator { innerTextField ->
                TextFieldChrome(
                    label = label,
                    labelState = labelState,
                    colors = colors,
                    cornerRadius = cornerRadius,
                    insideMargin = insideMargin,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    isFocused = isFocused,
                    enabled = enabled,
                    errorState = errorState,
                    lineMode = lineMode,
                    onClear =
                        if (showClearButton && state.text.isNotEmpty()) {
                            { state.edit { replace(0, length, "") } }
                        } else {
                            null
                        },
                    innerTextField = innerTextField,
                )
            },
    )
}

/**
 * A [TextField] component with Rmuix style.
 *
 * @param value The input [TextFieldValue] to be shown in the text field.
 * @param onValueChange The callback that is triggered when the input service updates values in
 *   [TextFieldValue]. An updated [TextFieldValue] comes as a parameter of the callback.
 * @param modifier The modifier to be applied to the [TextField].
 * @param insideMargin The margin inside the [TextField].
 * @param colors The [TextFieldColors] applied to the [TextField]. Use
 *   [TextFieldDefaults.textFieldColors] to customize.
 * @param cornerRadius The corner radius of the [TextField].
 * @param label The label to be displayed when the [TextField] is empty.
 * @param useLabelAsPlaceholder Whether to use the label as a placeholder.
 * @param enabled Whether the [TextField] is enabled.
 * @param readOnly Whether the [TextField] is read-only.
 * @param textStyle The text style to be applied to the [TextField].
 * @param keyboardOptions The keyboard options to be applied to the [TextField].
 * @param keyboardActions The keyboard actions to be applied to the [TextField].
 * @param leadingIcon The leading icon to be displayed in the [TextField].
 * @param trailingIcon The trailing icon to be displayed in the [TextField].
 * @param singleLine Whether the text field is single line.
 * @param maxLines The maximum number of lines allowed to be displayed in [TextField].
 * @param minLines The minimum number of lines allowed to be displayed in [TextField]. It is required
 *   that 1 <= [minLines] <= [maxLines].
 * @param visualTransformation The visual transformation to be applied to the [TextField].
 * @param onTextLayout The callback to be called when the text layout changes.
 * @param interactionSource The interaction source to be applied to the [TextField].
 * @param cursorBrush The brush to be used for the cursor.
 */
@Composable
fun TextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    insideMargin: DpSize = TextFieldDefaults.InsideMargin,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    cornerRadius: Dp = TextFieldDefaults.CornerRadius,
    label: String = "",
    useLabelAsPlaceholder: Boolean = false,
    enabled: Boolean = true,
    errorState: Boolean = false,
    lineMode: Boolean = false,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextFieldDefaults.textStyle(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(colors.borderColor),
    showClearButton: Boolean = false,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val labelState =
        remember(value.text, label, useLabelAsPlaceholder) {
            when {
                label.isEmpty() -> LabelAnimState.Hidden
                useLabelAsPlaceholder && value.text.isNotEmpty() -> LabelAnimState.Placeholder
                value.text.isNotEmpty() -> LabelAnimState.Floating
                else -> LabelAnimState.Normal
            }
        }

    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnTextLayout by rememberUpdatedState(onTextLayout)

    val contentColor = LocalContentColor.current
    val resolvedTextStyle =
        remember(textStyle, contentColor) {
            val textColor = textStyle.color.takeOrElse { contentColor }
            textStyle.copy(textColor)
        }

    BasicTextField(
        value = value,
        onValueChange = currentOnValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = resolvedTextStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = currentOnTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = @Composable { innerTextField ->
            TextFieldChrome(
                label = label,
                labelState = labelState,
                colors = colors,
                cornerRadius = cornerRadius,
                insideMargin = insideMargin,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                isFocused = isFocused,
                enabled = enabled,
                errorState = errorState,
                lineMode = lineMode,
                onClear =
                    if (showClearButton && value.text.isNotEmpty()) {
                        { currentOnValueChange(TextFieldValue("")) }
                    } else {
                        null
                    },
                innerTextField = innerTextField,
            )
        },
    )
}

/**
 * A text field component with Rmuix style.
 *
 * @param value The text to be displayed in the text field.
 * @param onValueChange The callback to be called when the value changes.
 * @param modifier The modifier to be applied to the [TextField].
 * @param insideMargin The margin inside the [TextField].
 * @param colors The [TextFieldColors] applied to the [TextField]. Use
 *   [TextFieldDefaults.textFieldColors] to customize.
 * @param cornerRadius The corner radius of the [TextField].
 * @param label The label to be displayed when the [TextField] is empty.
 * @param useLabelAsPlaceholder Whether to use the label as a placeholder.
 * @param enabled Whether the [TextField] is enabled.
 * @param readOnly Whether the [TextField] is read-only.
 * @param textStyle The text style to be applied to the [TextField].
 * @param keyboardOptions The keyboard options to be applied to the [TextField].
 * @param keyboardActions The keyboard actions to be applied to the [TextField].
 * @param leadingIcon The leading icon to be displayed in the [TextField].
 * @param trailingIcon The trailing icon to be displayed in the [TextField].
 * @param singleLine Whether the text field is single line.
 * @param maxLines The maximum number of lines allowed to be displayed in [TextField].
 * @param minLines The minimum number of lines allowed to be displayed in [TextField]. It is required
 *   that 1 <= [minLines] <= [maxLines].
 * @param visualTransformation The visual transformation to be applied to the [TextField].
 * @param onTextLayout The callback to be called when the text layout changes.
 * @param interactionSource The interaction source to be applied to the [TextField].
 * @param cursorBrush The brush to be used for the cursor.
 */
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    insideMargin: DpSize = TextFieldDefaults.InsideMargin,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    cornerRadius: Dp = TextFieldDefaults.CornerRadius,
    label: String = "",
    useLabelAsPlaceholder: Boolean = false,
    enabled: Boolean = true,
    errorState: Boolean = false,
    lineMode: Boolean = false,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextFieldDefaults.textStyle(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(colors.borderColor),
    showClearButton: Boolean = false,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val labelState =
        remember(value, label, useLabelAsPlaceholder) {
            when {
                label.isEmpty() -> LabelAnimState.Hidden
                useLabelAsPlaceholder && value.isNotEmpty() -> LabelAnimState.Placeholder
                value.isNotEmpty() -> LabelAnimState.Floating
                else -> LabelAnimState.Normal
            }
        }

    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnTextLayout by rememberUpdatedState(onTextLayout)

    val contentColor = LocalContentColor.current
    val resolvedTextStyle =
        remember(textStyle, contentColor) {
            val textColor = textStyle.color.takeOrElse { contentColor }
            textStyle.copy(textColor)
        }

    BasicTextField(
        value = value,
        onValueChange = currentOnValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = resolvedTextStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = currentOnTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = @Composable { innerTextField ->
            TextFieldChrome(
                label = label,
                labelState = labelState,
                colors = colors,
                cornerRadius = cornerRadius,
                insideMargin = insideMargin,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                isFocused = isFocused,
                enabled = enabled,
                errorState = errorState,
                lineMode = lineMode,
                onClear =
                    if (showClearButton && value.isNotEmpty()) {
                        { currentOnValueChange("") }
                    } else {
                        null
                    },
                innerTextField = innerTextField,
            )
        },
    )
}

private enum class LabelAnimState { Hidden, Placeholder, Normal, Floating }

/** Contains default values used by [TextField]. */
object TextFieldDefaults {
    /** The default corner radius of the [TextField]: `coui_textinput_corner_radius`. */
    val CornerRadius = TextFieldStockCornerRadius

    /** The default inside margin of the [TextField]. */
    val InsideMargin = DpSize(16.dp, 16.dp)

    /** The minimum height of the [TextField]: `coui_edit_text_height_min`. */
    val MinHeight = TextFieldStockMinHeight

    /** The resting border width: `coui_textinput_stroke_width` = 0.33dp. */
    val StrokeWidth = TextFieldStockStrokeWidth

    /** The focused border width: `coui_textinput_focus_stroke_width` = 1dp. */
    val FocusStrokeWidth = TextFieldStockFocusStrokeWidth

    /** Horizontal shake amplitude of the error state: `coui_edit_text_shake_amplitude` = 5.33dp. */
    val ErrorShakeAmplitude = TextFieldStockShakeAmplitude

    /** Time for the label / stroke to ramp to the error colour: 217ms. */
    val ErrorRampMillis = TextFieldStockErrorRampMillis

    /** Duration of the error shake: 450ms. */
    val ErrorShakeMillis = TextFieldStockShakeMillis

    /** Mode 1 grows the focused bottom line over 250ms (`animateToShowBackground()`). */
    val LineGrowMillis = TextFieldStockLineGrowMillis

    /** `coui_textinput_line_padding_top` = 10dp (`getBoundsTop()` / `getModePaddingTop()`). */
    val LinePaddingTop = TextFieldStockLinePaddingTop

    /** `coui_textinput_line_padding_middle` = 5dp (`getModePaddingTop()`). */
    val LinePaddingMiddle = TextFieldStockLinePaddingMiddle

    /**
     * `coui_textinput_label_cutout_padding`: while the label floats on the top border, it erases
     * the border 4dp to each side.
     */
    val LabelCutoutPadding = TextFieldStockLabelCutoutPadding

    /** The label font size when the label is floating above the text: `coui_textinput_hint_text_size`. */
    internal val LabelFontSizeFloating = TEXT_FIELD_LABEL_FLOATING_SIZE

    /** The label font size when the label is in its normal position. */
    internal val LabelFontSizeNormal = TEXT_FIELD_LABEL_SIZE

    /** The default text style: `couiEditTextTextAppearance` (16sp, regular family). */
    @Composable
    fun textStyle(): TextStyle = RmuixTheme.textStyles.main.copy(fontSize = TextFieldStockTextSize)

    /**
     * Default colors used by the [TextField].
     *
     * @param backgroundColor The box fill. The stock `coui_edittext_default_background` nine-patch
     *   paints only the hairline border, so the default is [Color.Transparent].
     * @param labelColor The color of the floating / inline label.
     * @param borderColor The color of the border when the [TextField] is focused.
     * @param restingBorderColor The hairline border when the field is enabled and not focused.
     * @param disabledBorderColor The hairline border when the field is disabled.
     */
    @Composable
    fun textFieldColors(
        backgroundColor: Color = Color.Transparent,
        labelColor: Color = textFieldLabel(isTextFieldInDarkTheme()),
        borderColor: Color = RmuixTheme.colorScheme.primary,
        restingBorderColor: Color = textFieldStroke(isTextFieldInDarkTheme()),
        disabledBorderColor: Color = textFieldDisabledStroke(isTextFieldInDarkTheme()),
    ): TextFieldColors =
        remember(backgroundColor, labelColor, borderColor, restingBorderColor, disabledBorderColor) {
            TextFieldColors(
                backgroundColor = backgroundColor,
                labelColor = labelColor,
                borderColor = borderColor,
                restingBorderColor = restingBorderColor,
                disabledBorderColor = disabledBorderColor,
            )
        }
}

/**
 * Colors used by a [TextField] in different parts of the component.
 *
 * @param backgroundColor The box fill; [Color.Transparent] leaves the page showing through, as the
 *   stock nine-patch box is border-only.
 * @param labelColor The color of the floating / inline label.
 * @param borderColor The color of the border when the [TextField] is focused.
 * @param restingBorderColor The hairline border when enabled and not focused; [Color.Unspecified]
 *   falls back to the stock divider color.
 * @param disabledBorderColor The hairline border when disabled; [Color.Unspecified] falls back to
 *   the stock disabled stroke color.
 */
@Immutable
data class TextFieldColors(
    val backgroundColor: Color,
    val labelColor: Color,
    val borderColor: Color,
    val restingBorderColor: Color = Color.Unspecified,
    val disabledBorderColor: Color = Color.Unspecified,
)

/**
 * Builds the shared chrome (background, state-driven hairline border, floating label,
 * leading/trailing icons, padding) around the inner text field. Centralizes derived state so the
 * three [TextField] overloads only differ in the [BasicTextField] call.
 */
@Composable
private fun TextFieldChrome(
    label: String,
    labelState: LabelAnimState,
    colors: TextFieldColors,
    cornerRadius: Dp,
    insideMargin: DpSize,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    isFocused: Boolean,
    enabled: Boolean,
    innerTextField: @Composable () -> Unit,
    errorState: Boolean = false,
    lineMode: Boolean = false,
    onClear: (() -> Unit)? = null,
) {
    // Mode 1 grows the focused line from left to right over 250ms (`animateToShowBackground()`).
    val focusLineProgress by animateFloatAsState(
        targetValue = if (isFocused && enabled) 1f else 0f,
        animationSpec = tween(durationMillis = TextFieldDefaults.LineGrowMillis),
        label = "TextFieldLineGrow",
    )
    val dark = isTextFieldInDarkTheme()
    val restingBorderColor = colors.restingBorderColor.takeOrElse { textFieldStroke(dark) }
    val disabledBorderColor = colors.disabledBorderColor.takeOrElse { textFieldDisabledStroke(dark) }
    // `Widget.COUI.EditText` binds `couiStrokeWidth` (0.33dp) plus `couiFocusStrokeWidth` (1dp), and
    // `COUIEditText` reads both, so the focused box thickens as well as recolours. There is no
    // border transition in the source: the width swaps with the state.
    val borderColor =
        when {
            !enabled -> disabledBorderColor
            isFocused -> colors.borderColor
            else -> restingBorderColor
        }

    // `COUIEditText` + `COUIErrorEditTextHelper`: while the error state is active the stroke ramps to
    // `couiColorErrorTextBg` over 217ms and the content shakes horizontally by 5.33dp over 450ms.
    val errorColor = textFieldErrorColor(dark)
    val errorRamp by animateFloatAsState(
        targetValue = if (errorState) 1f else 0f,
        animationSpec = tween(durationMillis = TextFieldDefaults.ErrorRampMillis),
        label = "TextFieldErrorRamp",
    )
    val resolvedBorderColor = lerp(borderColor, errorColor, errorRamp)
    val shakeAmplitudePx = with(LocalDensity.current) { TextFieldDefaults.ErrorShakeAmplitude.toPx() }
    val shake = remember { Animatable(0f) }
    LaunchedEffect(errorState) {
        if (!errorState) {
            shake.snapTo(0f)
            return@LaunchedEffect
        }
        // Keyframes {0, -1, 0.5, -0.5, 0} with per-segment durations 83/133/117/117ms.
        shake.snapTo(0f)
        shake.animateTo(
            targetValue = 0f,
            animationSpec =
                keyframes {
                    durationMillis = TextFieldDefaults.ErrorShakeMillis
                    -shakeAmplitudePx at 83
                    shakeAmplitudePx / 2f at 216
                    -shakeAmplitudePx / 2f at 333
                },
        )
    }
    val labelAnim =
        animateDpAsState(
            when (labelState) {
                LabelAnimState.Floating -> -insideMargin.height / 2
                LabelAnimState.Placeholder, LabelAnimState.Normal -> 0.dp
                LabelAnimState.Hidden -> 0.dp
            },
        )
    val labelFontSize by animateFloatAsState(
        when (labelState) {
            LabelAnimState.Floating -> TextFieldDefaults.LabelFontSizeFloating
            else -> TextFieldDefaults.LabelFontSizeNormal
        },
    )
    val resolvedTrailingIcon: (@Composable () -> Unit)? =
        if (onClear != null || trailingIcon != null) {
            {
                onClear?.let { ClearTextButton(onClick = it, contentDescription = "Clear text") }
                trailingIcon?.invoke()
            }
        } else {
            null
        }
    val hasLeadingIcon = leadingIcon != null
    val hasTrailingIcon = resolvedTrailingIcon != null
    val paddingModifier =
        remember(hasLeadingIcon, hasTrailingIcon, insideMargin) {
            when {
                !hasLeadingIcon && !hasTrailingIcon -> Modifier.padding(insideMargin.width, vertical = insideMargin.height)
                !hasLeadingIcon -> Modifier.padding(start = insideMargin.width).padding(vertical = insideMargin.height)
                !hasTrailingIcon -> Modifier.padding(end = insideMargin.width).padding(vertical = insideMargin.height)
                else -> Modifier.padding(vertical = insideMargin.height)
            }
        }

    TextFieldDecorationBox(
        label = label,
        labelFontSize = labelFontSize,
        labelColor = colors.labelColor,
        labelState = labelState,
        backgroundColor = colors.backgroundColor,
        // `couiFocusStrokeWidth`: the focused box thickens to 1dp.
        strokeWidth =
            if (isFocused && enabled) {
                TextFieldDefaults.FocusStrokeWidth
            } else {
                TextFieldDefaults.StrokeWidth
            },
        borderColor = resolvedBorderColor,
        cornerRadius = cornerRadius,
        paddingModifier = paddingModifier,
        leadingIcon = leadingIcon,
        trailingIcon = resolvedTrailingIcon,
        labelAnim = { labelAnim.value },
        insideMargin = insideMargin,
        innerTextField = {
            Box(modifier = Modifier.offset { IntOffset(shake.value.roundToInt(), 0) }) { innerTextField() }
        },
        lineMode = lineMode,
        focusLineProgress = focusLineProgress,
    )
}

@Composable
private fun TextFieldDecorationBox(
    label: String,
    labelFontSize: Float,
    labelColor: Color,
    labelState: LabelAnimState,
    backgroundColor: Color,
    strokeWidth: Dp,
    borderColor: Color,
    cornerRadius: Dp,
    innerTextField: @Composable () -> Unit,
    paddingModifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    labelAnim: () -> Dp = { 0.dp },
    insideMargin: DpSize = TextFieldDefaults.InsideMargin,
    lineMode: Boolean = false,
    focusLineProgress: Float = 0f,
) {
    Box(
        modifier =
            Modifier
                .heightIn(min = TextFieldDefaults.MinHeight)
                .drawBehind {
                    // Mode 1 (`couiBackgroundMode = 1`) draws a bottom-only stroke instead of a box:
                    // `mStrokeWidth` (0.33dp) across the full width, plus a `mStrokeWidthFocused` (1dp)
                    // line that grows left to right over 250ms while the field has focus.
                    if (lineMode) {
                        // 0.33dp rounds to a sub-pixel line at 1x densities; keep at least one pixel.
                        val resting = hairlinePx(TextFieldDefaults.StrokeWidth)
                        val focused = hairlinePx(TextFieldDefaults.FocusStrokeWidth)
                        drawRect(
                            color = borderColor,
                            topLeft = Offset(0f, size.height - resting),
                            size = Size(size.width, resting),
                        )
                        val progress = focusLineProgress.coerceIn(0f, 1f)
                        if (progress > 0f) {
                            drawRect(
                                color = borderColor,
                                topLeft = Offset(0f, size.height - focused),
                                size = Size(size.width * progress, focused),
                            )
                        }
                        return@drawBehind
                    }
                    val radius = cornerRadius.toPx()
                    if (backgroundColor != Color.Unspecified) {
                        drawRoundRect(color = backgroundColor, cornerRadius = CornerRadius(radius))
                    }
                    val stroke = hairlinePx(strokeWidth)
                    val inset = stroke / 2f
                    drawRoundRect(
                        color = borderColor,
                        topLeft = Offset(inset, inset),
                        size = Size(size.width - stroke, size.height - stroke),
                        cornerRadius = CornerRadius((radius - inset).coerceAtLeast(0f)),
                        style = Stroke(width = stroke),
                    )
                },
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()
            Box(
                modifier = Modifier.weight(1f).then(paddingModifier),
                contentAlignment = Alignment.TopStart,
            ) {
                if (labelState != LabelAnimState.Hidden && labelState != LabelAnimState.Placeholder) {
                    Text(
                        text = label,
                        fontSize = labelFontSize.sp,
                        color = labelColor,
                        fontWeight = FontWeight.Medium,
                        modifier =
                            Modifier
                                .offset { IntOffset(0, labelAnim().roundToPx()) }
                                .then(
                                    // `coui_textinput_label_cutout_padding`: the floating label cuts
                                    // the border open around itself.
                                    if (labelState == LabelAnimState.Floating && backgroundColor != Color.Unspecified) {
                                        Modifier
                                            .background(backgroundColor)
                                            .padding(horizontal = TextFieldDefaults.LabelCutoutPadding)
                                    } else {
                                        Modifier
                                    },
                                ),
                        textAlign = TextAlign.Start,
                    )
                }
                Box(
                    modifier = Modifier.offset(y = if (labelState == LabelAnimState.Floating) insideMargin.height / 2 else 0.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    innerTextField()
                }
            }
            trailingIcon?.invoke()
        }
    }
}
