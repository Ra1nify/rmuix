// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight
import io.github.ra1nify.rmuix.kmp.theme.LocalContentColor
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.PressFeedbackType

/**
 * A large-tier COUI button baseline using measured dimensions and color selectors.
 *
 * The uniform press component follows the official area/aspect mapping. Native Oplus contour,
 * material rendering, rubber translation and nonuniform deformation are not yet certified.
 *
 * @param onClick The callback when the [Button] is clicked.
 * @param modifier The modifier to be applied to the [Button].
 * @param enabled Whether the [Button] is enabled.
 * @param enableSpotlight Whether to enable touch spotlight glow.
 * @param spotlightColor The color of the touch spotlight glow.
 * @param cornerRadius The corner radius of the [Button].
 * @param minWidth The minimum width of the [Button].
 * @param minHeight The minimum height of the [Button].
 * @param colors The [ButtonColors] of the [Button].
 * @param insideMargin The margin inside the [Button].
 * @param pressFeedbackType Physical press compression animation type (defaults to stock size-dependent spring compression).
 * @param interactionSource The [MutableInteractionSource] to be used for the [Button].
 * @param indication The [Indication] to be used for the [Button].
 * @param content The [Composable] content of the [Button].
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    enableSpotlight: Boolean = false,
    spotlightColor: Color = Color.White,
    cornerRadius: Dp = ButtonDefaults.CornerRadius,
    minWidth: Dp = ButtonDefaults.MinWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    insideMargin: PaddingValues = ButtonDefaults.InsideMargin,
    pressFeedbackType: PressFeedbackType = PressFeedbackType.Sink,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    content: @Composable RowScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    val rowModifier =
        remember(minWidth, minHeight, insideMargin) {
            Modifier
                .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
                .padding(insideMargin)
        }
    val containerColor = if (enabled) colors.color else colors.disabledColor
    val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier =
                modifier
                    .semantics { role = Role.Button }
                    .stockButtonSurface(
                        interactionSource = interactionSource,
                        enabled = enabled,
                        pressFeedbackType = pressFeedbackType,
                        cornerRadius = cornerRadius,
                        containerColor = containerColor,
                        colors = colors,
                        dark = isButtonInDarkTheme(),
                    ).then(
                        if (enableSpotlight && enabled) {
                            Modifier.rmuixSpotlight(
                                glowColor = spotlightColor,
                                darkTheme = isButtonInDarkTheme(),
                            )
                        } else {
                            Modifier
                        },
                    ).clickable(
                        interactionSource = interactionSource,
                        indication = indication,
                        enabled = enabled,
                        onClick = onClick,
                    ),
            propagateMinConstraints = true,
        ) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content,
            )
        }
    }
}

/**
 * A [TextButton] component with ColorOS 17 style.
 *
 * @param text The text of the [TextButton].
 * @param onClick The callback when the [TextButton] is clicked.
 * @param modifier The modifier to be applied to the [TextButton].
 * @param enabled Whether the [TextButton] is enabled.
 * @param enableSpotlight Whether to enable touch spotlight glow.
 * @param spotlightColor The color of the touch spotlight glow.
 * @param cornerRadius The corner radius of the [TextButton].
 * @param minWidth The minimum width of the [TextButton].
 * @param minHeight The minimum height of the [TextButton].
 * @param colors The [TextButtonColors] of the [TextButton].
 * @param insideMargin The margin inside the [TextButton].
 * @param pressFeedbackType Physical press compression animation type.
 * @param interactionSource The [MutableInteractionSource] to be used for the [TextButton].
 * @param indication The [Indication] to be used for the [TextButton].
 */
@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    enableSpotlight: Boolean = false,
    spotlightColor: Color = Color.White,
    cornerRadius: Dp = ButtonDefaults.CornerRadius,
    minWidth: Dp = ButtonDefaults.MinWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: TextButtonColors = ButtonDefaults.textButtonColors(),
    insideMargin: PaddingValues = ButtonDefaults.InsideMargin,
    textStyle: TextStyle? = null,
    pressFeedbackType: PressFeedbackType = PressFeedbackType.Sink,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    val mappedColors =
        remember(colors) {
            ButtonColors(
                color = colors.color,
                disabledColor = colors.disabledColor,
                contentColor = colors.textColor,
                disabledContentColor = colors.disabledTextColor,
                hoverColor = colors.hoverColor,
                pressColor = colors.pressColor,
                focusOutlineColor = colors.focusOutlineColor,
            )
        }
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        enableSpotlight = enableSpotlight,
        spotlightColor = spotlightColor,
        cornerRadius = cornerRadius,
        minWidth = minWidth,
        minHeight = minHeight,
        colors = mappedColors,
        insideMargin = insideMargin,
        pressFeedbackType = pressFeedbackType,
        interactionSource = interactionSource,
        indication = indication,
    ) {
        Text(
            text = text,
            style = textStyle ?: RmuixTheme.textStyles.button,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

object ButtonDefaults {
    /**
     * Minimum width applied for buttons.
     * ColorOS large-tier minimum width is 132dp.
     */
    val MinWidth = 132.dp

    /**
     * Minimum height applied for buttons.
     * ColorOS large tier: `coui_btn_large_height_min` = 44dp.
     */
    val MinHeight = 44.dp

    /**
     * Corner radius applied for buttons.
     * The stock -1dp sentinel resolves the radius from half the current height.
     */
    val CornerRadius = (-1).dp

    /**
     * Inside margin applied for buttons.
     * ColorOS `coui_btn_padding_horizontal` = 12dp, `coui_btn_padding_vertical` = 8dp.
     */
    val InsideMargin = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

    /**
     * Default [ButtonColors] for secondary / tinted buttons — ColorOS SECONDARY.
     * The enabled container composites container8 over the elevated background; disabled stays translucent.
     */
    @Composable
    fun buttonColors(
        color: Color = buttonSecondaryContainer(isButtonInDarkTheme()),
        disabledColor: Color = buttonContainer8(isButtonInDarkTheme()),
        contentColor: Color = buttonLabelPrimary(isButtonInDarkTheme()),
        disabledContentColor: Color = buttonDisabledSecondaryLabel(isButtonInDarkTheme()),
        hoverColor: Color = Color.Unspecified,
        pressColor: Color = Color.Unspecified,
        focusOutlineColor: Color = Color.Unspecified,
    ): ButtonColors =
        remember(color, disabledColor, contentColor, disabledContentColor, hoverColor, pressColor, focusOutlineColor) {
            ButtonColors(
                color = color,
                disabledColor = disabledColor,
                contentColor = contentColor,
                disabledContentColor = disabledContentColor,
                hoverColor = hoverColor,
                pressColor = pressColor,
                focusOutlineColor = focusOutlineColor,
            )
        }

    /**
     * [ButtonColors] for primary / filled buttons — ColorOS PRIMARY.
     */
    @Composable
    fun buttonColorsPrimary(
        color: Color = RmuixTheme.colorScheme.primary,
        disabledColor: Color = RmuixTheme.colorScheme.primary.copy(alpha = 77f / 255f),
        contentColor: Color = Color.White,
        disabledContentColor: Color = buttonDisabledPrimaryLabel(isButtonInDarkTheme()),
        hoverColor: Color = Color.Unspecified,
        pressColor: Color = Color.Unspecified,
        focusOutlineColor: Color = Color.Unspecified,
    ): ButtonColors =
        remember(color, disabledColor, contentColor, disabledContentColor, hoverColor, pressColor, focusOutlineColor) {
            ButtonColors(
                color = color,
                disabledColor = disabledColor,
                contentColor = contentColor,
                disabledContentColor = disabledContentColor,
                hoverColor = hoverColor,
                pressColor = pressColor,
                focusOutlineColor = focusOutlineColor,
            )
        }

    /**
     * [ButtonColors] for application-defined destructive buttons (not a native COUIButton type).
     */
    @Composable
    fun buttonColorsDestructive(
        color: Color = RmuixTheme.colorScheme.error,
        disabledColor: Color = RmuixTheme.colorScheme.error.copy(alpha = 0.38f),
        contentColor: Color = RmuixTheme.colorScheme.onError,
        disabledContentColor: Color = RmuixTheme.colorScheme.onError.copy(alpha = 0.6f),
        hoverColor: Color = Color.Unspecified,
        pressColor: Color = Color.Unspecified,
        focusOutlineColor: Color = Color.Unspecified,
    ): ButtonColors =
        remember(color, disabledColor, contentColor, disabledContentColor, hoverColor, pressColor, focusOutlineColor) {
            ButtonColors(
                color = color,
                disabledColor = disabledColor,
                contentColor = contentColor,
                disabledContentColor = disabledContentColor,
                hoverColor = hoverColor,
                pressColor = pressColor,
                focusOutlineColor = focusOutlineColor,
            )
        }

    /**
     * Default [TextButtonColors] for secondary text buttons.
     */
    @Composable
    fun textButtonColors(
        color: Color = Color.Transparent,
        disabledColor: Color = Color.Transparent,
        textColor: Color = buttonLabelPrimary(isButtonInDarkTheme()),
        disabledTextColor: Color = buttonDisabledTextLabel(isButtonInDarkTheme()),
        hoverColor: Color = Color.Unspecified,
        pressColor: Color = Color.Unspecified,
        focusOutlineColor: Color = Color.Unspecified,
    ): TextButtonColors =
        remember(color, disabledColor, textColor, disabledTextColor, hoverColor, pressColor, focusOutlineColor) {
            TextButtonColors(
                color = color,
                disabledColor = disabledColor,
                textColor = textColor,
                disabledTextColor = disabledTextColor,
                hoverColor = hoverColor,
                pressColor = pressColor,
                focusOutlineColor = focusOutlineColor,
            )
        }

    /**
     * [TextButtonColors] for primary text buttons.
     */
    @Composable
    fun textButtonColorsPrimary(
        color: Color = Color.Transparent,
        disabledColor: Color = Color.Transparent,
        textColor: Color = RmuixTheme.colorScheme.primary,
        disabledTextColor: Color = RmuixTheme.colorScheme.disabledPrimaryButton,
        hoverColor: Color = Color.Unspecified,
        pressColor: Color = Color.Unspecified,
        focusOutlineColor: Color = Color.Unspecified,
    ): TextButtonColors =
        remember(color, disabledColor, textColor, disabledTextColor, hoverColor, pressColor, focusOutlineColor) {
            TextButtonColors(
                color = color,
                disabledColor = disabledColor,
                textColor = textColor,
                disabledTextColor = disabledTextColor,
                hoverColor = hoverColor,
                pressColor = pressColor,
                focusOutlineColor = focusOutlineColor,
            )
        }
}

@Immutable
data class ButtonColors(
    val color: Color,
    val disabledColor: Color,
    val contentColor: Color,
    val disabledContentColor: Color,
    val hoverColor: Color = Color.Unspecified,
    val pressColor: Color = Color.Unspecified,
    val focusOutlineColor: Color = Color.Unspecified,
)

@Immutable
data class TextButtonColors(
    val color: Color,
    val disabledColor: Color,
    val textColor: Color,
    val disabledTextColor: Color,
    val hoverColor: Color = Color.Unspecified,
    val pressColor: Color = Color.Unspecified,
    val focusOutlineColor: Color = Color.Unspecified,
)

@Composable
private fun isButtonInDarkTheme(): Boolean = RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
