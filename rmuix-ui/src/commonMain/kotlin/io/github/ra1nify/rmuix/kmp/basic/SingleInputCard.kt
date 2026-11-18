// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * Colours of a [SingleInputCard] (`COUICardSingleInputView`).
 *
 * Sources: card `?attr/couiColorCardBackground` (`coui_color_card_background` `#FFFFFF` / `#1AFFFFFF`),
 * title / text `?attr/couiColorPrimaryNeutral` (`#E6000000` / `#E6FFFFFF`), hint / count
 * `?attr/couiColorHintNeutral` (`#42000000` / `#4DFFFFFF`), error `coui_color_error` (`#DB382C` /
 * `#FF6C61`) and the delete / eye icons `?attr/couiColorLabelSecondary` (`#8A000000` / `#8AFFFFFF`).
 */
@Immutable
data class SingleInputCardColors(
    val cardColor: Color,
    val titleColor: Color,
    val textColor: Color,
    val hintColor: Color,
    val countColor: Color,
    val errorColor: Color,
    val iconColor: Color,
    val disabledTextColor: Color,
)

/** Defaults for [SingleInputCard], taken from `COUIInputView` / `coui_single_input_card_view.xml`. */
object SingleInputCardDefaults {
    /** `coui_round_corner_xl` (the `COUIInputListSelectedItemLayout` card radius). */
    val CardCornerRadius = 20.dp

    /** `coui_preference_input_margin_horizontal`: the card's inset from the view bounds. */
    val CardHorizontalMargin = 16.dp

    /** `coui_preference_input_margin_horizontal_tiny` (small screens). */
    val CardHorizontalMarginTiny = 12.dp

    /** The card content paddingStart / paddingEnd in `coui_single_input_card_view.xml`. */
    val ContentPaddingHorizontal = 32.dp

    /** `COUIInputTitleStyle` (`couiTextAppearanceHeadline6`): 16sp medium. */
    val TitleTextSize = 16.sp

    /** `coui_inputView_title_minheight`. */
    val TitleMinHeight = 22.dp

    /** `coui_input_preference_single_title_padding_top`. */
    val TitlePaddingTop = 12.dp

    /** `coui_input_preference_title_padding_bottom`. */
    val TitlePaddingBottom = 4.dp

    /** `Widget.COUI.Input` textAppearance `couiTextBodyL`: 15sp. */
    val TextSize = 15.sp

    /** `coui_input_edit_text_no_title_padding_top` / `_bottom` (no title). */
    val EditTextPaddingVertical = 15.dp

    /** `coui_input_preference_single_title_padding_bottom`: the text bottom padding with a title. */
    val EditTextWithTitlePaddingBottom = 12.dp

    /** `coui_input_button_layout_margin_start`. */
    val ButtonMarginStart = 8.dp

    /** The `ic_edit_text_delete` vector (22dp). */
    val DeleteIconSize = 22.dp

    /** `coui_inputview_password_size`. */
    val PasswordIconSize = 28.dp

    /** `coui_card_input_password_margin_start`. */
    val PasswordMarginStart = 2.dp

    /** `couiTextCaption`: the count and error text size. */
    val CaptionTextSize = 10.sp

    /** `coui_card_single_input_padding_top`: the error text top padding. */
    val ErrorPaddingTop = 4.dp

    /** The error text paddingStart / paddingEnd in `coui_single_input_card_view.xml`. */
    val ErrorPaddingHorizontal = 32.dp

    /** `COUIErrorEditTextHelper.DURATION_HINT_ANIMATOR`: the hint/title colour turns error-red over 217ms. */
    val ErrorHintColorMillis = 217

    /** `ShakeInterpolator.TOTAL_DURATION`: the focused field shakes for 450ms on error. */
    val ErrorShakeMillis = 450

    /** `coui_edit_text_shake_amplitude`: 5.33dp shake amplitude. */
    val ErrorShakeAmplitude = 5.33.dp

    /** `COUIInputView.showErrorMsgAnim()`: the error label fades in over 217ms (`DURATION_HINT_ANIMATOR`). */
    val ErrorTextAppearMillis = 217

    /** `COUIInputView.hideErrorMsgAnim()`: the error label fades out over 283ms. */
    val ErrorTextDisappearMillis = 283

    /** The stock colours, keyed on the current theme. */
    @Composable
    fun colors(
        cardColor: Color = if (isSingleInputDarkTheme()) Color(0x1AFFFFFF) else Color(0xFFFFFFFF),
        titleColor: Color = singleInputPrimaryNeutral(isSingleInputDarkTheme()),
        textColor: Color = singleInputPrimaryNeutral(isSingleInputDarkTheme()),
        hintColor: Color = singleInputHintNeutral(isSingleInputDarkTheme()),
        countColor: Color = singleInputHintNeutral(isSingleInputDarkTheme()),
        errorColor: Color = singleInputError(isSingleInputDarkTheme()),
        iconColor: Color = singleInputLabelSecondary(isSingleInputDarkTheme()),
        disabledTextColor: Color = singleInputDisabledNeutral(isSingleInputDarkTheme()),
    ): SingleInputCardColors =
        remember(cardColor, titleColor, textColor, hintColor, countColor, errorColor, iconColor, disabledTextColor) {
            SingleInputCardColors(
                cardColor = cardColor,
                titleColor = titleColor,
                textColor = textColor,
                hintColor = hintColor,
                countColor = countColor,
                errorColor = errorColor,
                iconColor = iconColor,
                disabledTextColor = disabledTextColor,
            )
        }
}

@Composable
private fun isSingleInputDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/** `?attr/couiColorPrimaryNeutral`. */
internal fun singleInputPrimaryNeutral(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/** `?attr/couiColorHintNeutral`. */
internal fun singleInputHintNeutral(dark: Boolean): Color = if (dark) Color(0x4DFFFFFF) else Color(0x42000000)

/** `coui_color_error` / `_dark`. */
internal fun singleInputError(dark: Boolean): Color = if (dark) Color(0xFFFF6C61) else Color(0xFFDB382C)

/** `?attr/couiColorLabelSecondary`. */
internal fun singleInputLabelSecondary(dark: Boolean): Color = if (dark) Color(0x8AFFFFFF) else Color(0x8A000000)

/** `?attr/couiColorDisabledNeutral`. */
internal fun singleInputDisabledNeutral(dark: Boolean): Color = if (dark) Color(0x4DFFFFFF) else Color(0x42000000)

/**
 * The stock input count text: `length/maxCount`, clamped to `maxCount` (the stock TextWatcher rewrites the
 * count to `max/max` once the limit is reached and truncates the text).
 */
internal fun singleInputCountText(
    length: Int,
    maxCount: Int,
): String = "${min(length, maxCount)}/$maxCount"

/** Whether the stock count turns into the error colour (the `length >= mMaxCount` branch). */
internal fun singleInputCountExceeded(
    length: Int,
    maxCount: Int,
): Boolean = maxCount > 0 && length >= maxCount

/**
 * The ColorOS card single input: `COUICardSingleInputView` (`com.coui.appcompat.edittext`), the email binding
 * card used by Settings (`preference_email_single_edit_layout.xml`, `EmailInputPreferenceCategory`).
 *
 * It is a rounded card (`coui_round_corner_xl` = 20dp, inset `coui_preference_input_margin_horizontal` = 16dp)
 * with an optional 16sp title, a 15sp single-line text field, an optional `length/max` count, the
 * `ic_edit_text_delete` clear button (shown while focused and non-empty) and, in password mode, the eye toggle
 * (`coui_btn_eye_on` / `coui_btn_eye_off`). An error message is rendered below the card (`couiTextCaption`,
 * `coui_color_error`, 4dp top padding).
 *
 * @param value The text value.
 * @param onValueChange Called with the new value; when [maxCount] > 0 the value is truncated like the stock
 *   TextWatcher.
 * @param modifier Applied to the view; the stock width used by Settings is 328dp.
 * @param title Optional 16sp title above the text field.
 * @param hint The hint shown while [value] is empty.
 * @param enabled Whether the card accepts input.
 * @param password Enables the password eye toggle (`couiEnablePassword`).
 * @param passwordVisible Whether the password starts visible (the stock `couiPasswordType`: type 0 shows it).
 * @param showCount Whether to show the `length/maxCount` count (`couiEnableInputCount`).
 * @param maxCount The character limit (`couiInputMaxCount`); 0 disables truncation.
 * @param quickDelete Whether to show the clear button (`quickDelete`).
 * @param errorText The error message (`showError`); empty hides it.
 * @param colors The card colours.
 * @param keyboardOptions The keyboard options of the text field.
 * @param keyboardActions The keyboard actions of the text field.
 */
@Composable
fun SingleInputCard(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    hint: String = "",
    enabled: Boolean = true,
    password: Boolean = false,
    passwordVisible: Boolean = false,
    showCount: Boolean = false,
    maxCount: Int = 0,
    quickDelete: Boolean = true,
    errorText: String? = null,
    colors: SingleInputCardColors = SingleInputCardDefaults.colors(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    var focused by remember { mutableStateOf(false) }
    var visible by remember(password) { mutableStateOf(passwordVisible) }
    val showError = !errorText.isNullOrEmpty()
    // `COUIErrorEditTextHelper` error animation: hint colour 217ms + ShakeInterpolator 450ms.
    val errorHintProgress = remember { Animatable(if (showError) 1f else 0f) }
    val errorShakeElapsed = remember { Animatable(0f) }
    var errorAnimationInitialised by remember { mutableStateOf(false) }
    var previousShowError by remember { mutableStateOf(showError) }
    // `showErrorMsgAnim()` 217ms / `hideErrorMsgAnim()` 283ms: the label stays composed while fading out.
    val errorTextAlpha = remember { Animatable(if (showError) 1f else 0f) }
    var displayedErrorText by remember { mutableStateOf(errorText) }
    LaunchedEffect(errorText) {
        val message = errorText
        if (!message.isNullOrEmpty()) {
            displayedErrorText = message
            errorTextAlpha.animateTo(
                1f,
                tween(durationMillis = SingleInputCardDefaults.ErrorTextAppearMillis),
            )
        } else if (displayedErrorText != null) {
            errorTextAlpha.animateTo(
                0f,
                tween(durationMillis = SingleInputCardDefaults.ErrorTextDisappearMillis),
            )
            displayedErrorText = null
        }
    }
    LaunchedEffect(showError) {
        if (!errorAnimationInitialised) {
            errorAnimationInitialised = true
            previousShowError = showError
            return@LaunchedEffect
        }
        if (showError && !previousShowError) {
            errorHintProgress.snapTo(0f)
            errorShakeElapsed.snapTo(0f)
            coroutineScope {
                launch {
                    errorHintProgress.animateTo(
                        1f,
                        tween(
                            durationMillis = SingleInputCardDefaults.ErrorHintColorMillis,
                            easing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f),
                        ),
                    )
                }
                launch {
                    errorShakeElapsed.animateTo(
                        SingleInputCardDefaults.ErrorShakeMillis.toFloat(),
                        tween(durationMillis = SingleInputCardDefaults.ErrorShakeMillis, easing = LinearEasing),
                    )
                }
            }
        } else if (!showError) {
            errorHintProgress.animateTo(
                0f,
                tween(
                    durationMillis = SingleInputCardDefaults.ErrorHintColorMillis,
                    easing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f),
                ),
            )
        }
        previousShowError = showError
    }
    val density = LocalDensity.current
    val shakeOffset =
        if (showError && focused) {
            with(density) {
                (
                    singleInputErrorShakeFactor(errorShakeElapsed.value) *
                        SingleInputCardDefaults.ErrorShakeAmplitude.toPx()
                ).toDp()
            }
        } else {
            0.dp
        }
    val textColor = if (enabled) colors.textColor else colors.disabledTextColor
    val titleColor = if (enabled) colors.titleColor else colors.disabledTextColor
    val iconColor = if (enabled) colors.iconColor else colors.disabledTextColor
    val countColor =
        if (singleInputCountExceeded(value.length, maxCount)) colors.errorColor else colors.countColor
    val deleteVisible = quickDelete && enabled && focused && value.isNotEmpty()
    val passwordIcon =
        if (visible) {
            singleInputEyeOnIcon
        } else {
            singleInputEyeOffIcon
        }

    Column(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SingleInputCardDefaults.CardHorizontalMargin)
                    .clip(RoundedCornerShape(SingleInputCardDefaults.CardCornerRadius))
                    .background(colors.cardColor),
        ) {
            Column(modifier = Modifier.padding(horizontal = SingleInputCardDefaults.ContentPaddingHorizontal)) {
                if (title != null) {
                    Text(
                        text = title,
                        modifier =
                            Modifier
                                .padding(
                                    top = SingleInputCardDefaults.TitlePaddingTop,
                                    bottom = SingleInputCardDefaults.TitlePaddingBottom,
                                ).heightIn(min = SingleInputCardDefaults.TitleMinHeight),
                        color = titleColor,
                        fontSize = SingleInputCardDefaults.TitleTextSize,
                        maxLines = 1,
                    )
                }
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                top = if (title == null) SingleInputCardDefaults.EditTextPaddingVertical else 0.dp,
                                bottom =
                                    if (title == null) {
                                        SingleInputCardDefaults.EditTextPaddingVertical
                                    } else {
                                        SingleInputCardDefaults.EditTextWithTitlePaddingBottom
                                    },
                            ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicTextField(
                        modifier =
                            Modifier
                                .weight(1f)
                                .offset(x = shakeOffset)
                                .onFocusChanged { focused = it.isFocused },
                        value = value,
                        onValueChange = { new ->
                            onValueChange(if (maxCount > 0) new.take(maxCount) else new)
                        },
                        enabled = enabled,
                        singleLine = true,
                        textStyle =
                            TextStyle(
                                color = textColor,
                                fontSize = SingleInputCardDefaults.TextSize,
                            ),
                        cursorBrush = SolidColor(textColor),
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        visualTransformation =
                            if (password && !visible) PasswordVisualTransformation() else VisualTransformation.None,
                        decorationBox = { innerTextField ->
                            Box {
                                if (value.isEmpty()) {
                                    Text(
                                        text = hint,
                                        color =
                                            lerp(
                                                colors.hintColor,
                                                colors.errorColor,
                                                errorHintProgress.value,
                                            ),
                                        fontSize = SingleInputCardDefaults.TextSize,
                                        maxLines = 1,
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )
                    if (showCount && maxCount > 0) {
                        Text(
                            text = singleInputCountText(value.length, maxCount),
                            modifier = Modifier.padding(start = SingleInputCardDefaults.ButtonMarginStart),
                            color = countColor,
                            fontSize = SingleInputCardDefaults.CaptionTextSize,
                            maxLines = 1,
                        )
                    }
                    if (deleteVisible) {
                        Spacer(Modifier.width(SingleInputCardDefaults.ButtonMarginStart))
                        Icon(
                            imageVector = singleInputDeleteIcon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier =
                                Modifier
                                    .size(SingleInputCardDefaults.DeleteIconSize)
                                    .clickable { onValueChange("") },
                        )
                    }
                    if (password) {
                        Spacer(Modifier.width(SingleInputCardDefaults.PasswordMarginStart))
                        Icon(
                            imageVector = passwordIcon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier =
                                Modifier
                                    .size(SingleInputCardDefaults.PasswordIconSize)
                                    .clickable(enabled = enabled) { visible = !visible },
                        )
                    }
                }
            }
        }
        displayedErrorText?.let { shownError ->
            Text(
                text = shownError,
                modifier =
                    Modifier
                        .padding(
                            top = SingleInputCardDefaults.ErrorPaddingTop,
                            start =
                                SingleInputCardDefaults.CardHorizontalMargin + SingleInputCardDefaults.ErrorPaddingHorizontal,
                            end =
                                SingleInputCardDefaults.CardHorizontalMargin + SingleInputCardDefaults.ErrorPaddingHorizontal,
                        ).graphicsLayer { alpha = errorTextAlpha.value },
                color = colors.errorColor,
                fontSize = SingleInputCardDefaults.CaptionTextSize,
            )
        }
    }
}

// ic_edit_text_delete.xml (22dp): circle with an x.
private val singleInputDeleteIcon: ImageVector by lazy {
    ImageVector
        .Builder("SingleInputDelete", 22.dp, 22.dp, 22f, 22f)
        .apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero,
            ) {
                moveTo(11f, 20f)
                curveTo(15.971f, 20f, 20f, 15.971f, 20f, 11f)
                curveTo(20f, 6.029f, 15.971f, 2f, 11f, 2f)
                curveTo(6.029f, 2f, 2f, 6.029f, 2f, 11f)
                curveTo(2f, 15.971f, 6.029f, 20f, 11f, 20f)
                close()
                moveTo(8.596f, 7.464f)
                lineTo(11f, 9.868f)
                lineTo(13.404f, 7.465f)
                curveTo(13.716f, 7.152f, 14.223f, 7.152f, 14.535f, 7.465f)
                curveTo(14.848f, 7.777f, 14.848f, 8.283f, 14.535f, 8.596f)
                lineTo(12.132f, 11f)
                lineTo(14.536f, 13.404f)
                curveTo(14.848f, 13.716f, 14.848f, 14.223f, 14.536f, 14.535f)
                curveTo(14.224f, 14.848f, 13.717f, 14.848f, 13.405f, 14.535f)
                lineTo(11f, 12.131f)
                lineTo(8.595f, 14.536f)
                curveTo(8.283f, 14.848f, 7.777f, 14.848f, 7.464f, 14.536f)
                curveTo(7.152f, 14.223f, 7.152f, 13.717f, 7.464f, 13.404f)
                lineTo(9.869f, 11f)
                lineTo(7.465f, 8.595f)
                curveTo(7.152f, 8.283f, 7.152f, 7.777f, 7.465f, 7.464f)
                curveTo(7.777f, 7.152f, 8.284f, 7.152f, 8.596f, 7.464f)
                close()
            }
        }.build()
}

// coui_btn_eye_on.xml (34dp viewport).
private val singleInputEyeOnIcon: ImageVector by lazy {
    ImageVector
        .Builder("SingleInputEyeOn", 34.dp, 34.dp, 34f, 34f)
        .apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                moveTo(6f, 17.001f)
                curveTo(12f, 27.716f, 21.999f, 27.716f, 27.999f, 17.001f)
                curveTo(21.999f, 6.286f, 12f, 6.286f, 6f, 17.001f)
                close()
                moveTo(13.983f, 17.003f)
                curveTo(13.983f, 15.337f, 15.333f, 13.987f, 16.999f, 13.987f)
                curveTo(18.665f, 13.987f, 20.015f, 15.337f, 20.015f, 17.003f)
                curveTo(20.015f, 18.669f, 18.665f, 20.019f, 16.999f, 20.019f)
                curveTo(15.333f, 20.019f, 13.983f, 18.669f, 13.983f, 17.003f)
                close()
                moveTo(16.999f, 11.983f)
                curveTo(14.227f, 11.983f, 11.979f, 14.231f, 11.979f, 17.003f)
                curveTo(11.979f, 19.775f, 14.227f, 22.023f, 16.999f, 22.023f)
                curveTo(19.771f, 22.023f, 22.019f, 19.775f, 22.019f, 17.003f)
                curveTo(22.019f, 14.231f, 19.771f, 11.983f, 16.999f, 11.983f)
                close()
            }
        }.build()
}

// coui_btn_eye_off.xml (34dp viewport).
private val singleInputEyeOffIcon: ImageVector by lazy {
    ImageVector
        .Builder("SingleInputEyeOff", 34.dp, 34.dp, 34f, 34f)
        .apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            ) {
                moveTo(6.706f, 6.734f)
                curveTo(6.315f, 7.124f, 6.315f, 7.757f, 6.706f, 8.148f)
                lineTo(10.245f, 11.686f)
                curveTo(8.702f, 12.992f, 7.263f, 14.773f, 6f, 17.028f)
                curveTo(10.299f, 24.705f, 16.652f, 26.882f, 22.115f, 23.557f)
                lineTo(25.824f, 27.266f)
                curveTo(26.215f, 27.657f, 26.848f, 27.657f, 27.239f, 27.266f)
                curveTo(27.629f, 26.876f, 27.629f, 26.243f, 27.239f, 25.852f)
                lineTo(8.12f, 6.734f)
                curveTo(7.73f, 6.343f, 7.096f, 6.343f, 6.706f, 6.734f)
                close()
                moveTo(19.772f, 21.214f)
                lineTo(18.306f, 19.748f)
                curveTo(17.911f, 19.939f, 17.468f, 20.046f, 16.999f, 20.046f)
                curveTo(15.333f, 20.046f, 13.983f, 18.695f, 13.983f, 17.029f)
                curveTo(13.983f, 16.561f, 14.09f, 16.118f, 14.28f, 15.722f)
                lineTo(12.814f, 14.256f)
                curveTo(12.286f, 15.051f, 11.979f, 16.004f, 11.979f, 17.029f)
                curveTo(11.979f, 19.802f, 14.227f, 22.049f, 16.999f, 22.049f)
                curveTo(18.024f, 22.049f, 18.978f, 21.742f, 19.772f, 21.214f)
                close()
                moveTo(16.819f, 14.019f)
                lineTo(20.01f, 17.209f)
                curveTo(20.013f, 17.15f, 20.015f, 17.09f, 20.015f, 17.029f)
                curveTo(20.015f, 15.364f, 18.665f, 14.013f, 16.999f, 14.013f)
                curveTo(16.939f, 14.013f, 16.879f, 14.015f, 16.819f, 14.019f)
                close()
                moveTo(22.019f, 17.029f)
                curveTo(22.019f, 17.679f, 21.896f, 18.3f, 21.671f, 18.87f)
                lineTo(24.496f, 21.696f)
                curveTo(25.759f, 20.473f, 26.941f, 18.917f, 27.999f, 17.028f)
                curveTo(23.937f, 9.774f, 18.044f, 7.431f, 12.798f, 9.997f)
                lineTo(15.158f, 12.358f)
                curveTo(15.728f, 12.133f, 16.349f, 12.009f, 16.999f, 12.009f)
                curveTo(19.771f, 12.009f, 22.019f, 14.257f, 22.019f, 17.029f)
                close()
            }
        }.build()
}

// `COUIErrorEditTextHelper.ShakeInterpolator`: 0, -1, 0.5, -0.5, 0 scaled by the 5.33dp amplitude, with
// `COUIEaseInterpolator` (cubic-bezier(0.33, 0, 0.67, 1)) between the keyframes.
private val SingleInputShakeSegmentMillis = intArrayOf(83, 133, 117, 117)
private val SingleInputShakeOffsets = floatArrayOf(0f, -1f, 0.5f, -0.5f, 0f)

/** `ShakeInterpolator.TOTAL_DURATION` (450ms). */
internal const val SINGLE_INPUT_ERROR_SHAKE_MILLIS = 450

/** `COUIEaseInterpolator`: cubic-bezier(0.33, 0, 0.67, 1). */
internal fun singleInputEaseInterpolation(t: Float): Float = CubicBezierEasing(0.33f, 0f, 0.67f, 1f).transform(t.coerceIn(0f, 1f))

/**
 * `ShakeInterpolator.getInterpolation()` expressed in pixels of the 5.33dp
 * (`coui_edit_text_shake_amplitude`) amplitude: the value is the keyframe offset `-1..0.5` times the
 * amplitude, interpolated by [SingleInputShakeSegmentMillis] and [singleInputEaseInterpolation].
 */
internal fun singleInputErrorShakeFactor(elapsedMillis: Float): Float {
    if (elapsedMillis <= 0f) return 0f
    var start = 0f
    for (index in SingleInputShakeSegmentMillis.indices) {
        val duration = SingleInputShakeSegmentMillis[index].toFloat()
        val end = start + duration
        if (elapsedMillis <= end) {
            val local = ((elapsedMillis - start) / duration).coerceIn(0f, 1f)
            val eased = singleInputEaseInterpolation(local)
            return SingleInputShakeOffsets[index] * (1f - eased) + SingleInputShakeOffsets[index + 1] * eased
        }
        start = end
    }
    return 0f
}
