// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight
import io.github.ra1nify.rmuix.kmp.squircle.squircleSurface
import io.github.ra1nify.rmuix.kmp.theme.LocalContentColor
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * A [Button] component with Rmuix style.
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
 * @param interactionSource The [MutableInteractionSource] to be used for the [Button].
 * @param indication The [Indication] to be used for the [Button].
 * @param content The [Composable] content of the [Button].
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    enableSpotlight: Boolean = true,
    spotlightColor: Color = if (isSystemInDarkTheme()) Color(0x33FFFFFF) else Color(0x1F000000),
    cornerRadius: Dp = ButtonDefaults.CornerRadius,
    minWidth: Dp = ButtonDefaults.MinWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    insideMargin: PaddingValues = ButtonDefaults.InsideMargin,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = LocalIndication.current,
    content: @Composable RowScope.() -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val rowModifier = remember(minWidth, minHeight, insideMargin) {
        Modifier
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
            .padding(insideMargin)
    }
    val containerColor = if (enabled) colors.color else colors.disabledColor
    val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier = modifier
                .semantics { role = Role.Button }
                .then(
                    if (enableSpotlight && enabled) {
                        Modifier.rmuixSpotlight(glowColor = spotlightColor, lightRadius = 140.dp)
                    } else {
                        Modifier
                    },
                )
                .squircleSurface(color = containerColor, cornerRadius = cornerRadius)
                .clickable(
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
 * A [TextButton] component with Rmuix style.
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
 * @param interactionSource The [MutableInteractionSource] to be used for the [TextButton].
 * @param indication The [Indication] to be used for the [TextButton].
 */
@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    enableSpotlight: Boolean = true,
    spotlightColor: Color = if (isSystemInDarkTheme()) Color(0x33FFFFFF) else Color(0x1F000000),
    cornerRadius: Dp = ButtonDefaults.CornerRadius,
    minWidth: Dp = ButtonDefaults.MinWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: TextButtonColors = ButtonDefaults.textButtonColors(),
    insideMargin: PaddingValues = ButtonDefaults.InsideMargin,
    textStyle: TextStyle? = null,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = LocalIndication.current,
) {
    val mappedColors = remember(colors) {
        ButtonColors(
            color = colors.color,
            disabledColor = colors.disabledColor,
            contentColor = colors.textColor,
            disabledContentColor = colors.disabledTextColor,
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
        interactionSource = interactionSource,
        indication = indication,
    ) {
        Text(
            text = text,
            style = textStyle ?: RmuixTheme.textStyles.button,
        )
    }
}

object ButtonDefaults {

    /**
     * The default min width applied for all buttons. Note that you can override it by applying
     * Modifier.widthIn directly on the button composable.
     *
     * Kept from the miuix value: ColorOS's large tier measures 132dp and its small tier 52dp, and 58dp
     * sits between them without forcing wide buttons everywhere.
     */
    val MinWidth = 58.dp

    /**
     * The default min height applied for all buttons. Note that you can override it by applying
     * Modifier.heightIn directly on the button composable.
     *
     * ColorOS large tier: `coui_btn_large_height_min` = 44dp.
     */
    val MinHeight = 44.dp

    /**
     * The default corner radius applied for all buttons.
     *
     * ColorOS `coui_btn_drawable_radius_large` = 21.5dp, which reads as a capsule at the 44dp height.
     */
    val CornerRadius = 21.5.dp

    /**
     * The default inside margin applied for all buttons.
     *
     * ColorOS `coui_btn_padding_horizontal` = 12dp, `coui_btn_padding_vertical` = 8dp.
     */
    val InsideMargin = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

    /**
     * The default [ButtonColors] for all buttons — ColorOS SECONDARY.
     *
     * The container is a translucent neutral (`coui_color_container8` = #14000000 light / #1AFFFFFF
     * dark) rather than a solid colour: ColorOS marks its secondary tier with translucency, the same
     * language the checkbox outline uses. Content stays on the primary text colour.
     */
    @Composable
    fun buttonColors(
        color: Color = if (isSystemInDarkTheme()) Color(0x1AFFFFFF) else Color(0x14000000),
        disabledColor: Color = RmuixTheme.colorScheme.disabledSecondaryVariant,
        contentColor: Color = RmuixTheme.colorScheme.onBackground,
        disabledContentColor: Color = RmuixTheme.colorScheme.disabledOnSecondaryVariant,
    ): ButtonColors = remember(color, disabledColor, contentColor, disabledContentColor) {
        ButtonColors(
            color = color,
            disabledColor = disabledColor,
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
        )
    }

    /**
     * The [ButtonColors] for primary buttons.
     */
    @Composable
    fun buttonColorsPrimary(
        color: Color = RmuixTheme.colorScheme.primary,
        disabledColor: Color = RmuixTheme.colorScheme.disabledPrimaryButton,
        contentColor: Color = RmuixTheme.colorScheme.onPrimary,
        disabledContentColor: Color = RmuixTheme.colorScheme.disabledOnPrimaryButton,
    ): ButtonColors = remember(color, disabledColor, contentColor, disabledContentColor) {
        ButtonColors(
            color = color,
            disabledColor = disabledColor,
            contentColor = contentColor,
            disabledContentColor = disabledContentColor,
        )
    }

    /**
     * The default [TextButtonColors] for all text buttons.
     */
    @Composable
    fun textButtonColors(
        color: Color = RmuixTheme.colorScheme.secondaryVariant,
        disabledColor: Color = RmuixTheme.colorScheme.disabledSecondaryVariant,
        textColor: Color = RmuixTheme.colorScheme.onSecondaryVariant,
        disabledTextColor: Color = RmuixTheme.colorScheme.disabledOnSecondaryVariant,
    ): TextButtonColors = remember(color, disabledColor, textColor, disabledTextColor) {
        TextButtonColors(
            color = color,
            disabledColor = disabledColor,
            textColor = textColor,
            disabledTextColor = disabledTextColor,
        )
    }

    /**
     * The [TextButtonColors] for primary text buttons.
     */
    @Composable
    fun textButtonColorsPrimary(
        color: Color = RmuixTheme.colorScheme.primary,
        disabledColor: Color = RmuixTheme.colorScheme.disabledPrimaryButton,
        textColor: Color = RmuixTheme.colorScheme.onPrimary,
        disabledTextColor: Color = RmuixTheme.colorScheme.disabledOnPrimaryButton,
    ): TextButtonColors = remember(color, disabledColor, textColor, disabledTextColor) {
        TextButtonColors(
            color = color,
            disabledColor = disabledColor,
            textColor = textColor,
            disabledTextColor = disabledTextColor,
        )
    }
}

@Immutable
data class ButtonColors(
    val color: Color,
    val disabledColor: Color,
    val contentColor: Color,
    val disabledContentColor: Color,
)

@Immutable
data class TextButtonColors(
    val color: Color,
    val disabledColor: Color,
    val textColor: Color,
    val disabledTextColor: Color,
)
