// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import io.github.ra1nify.rmuix.kmp.utils.RmuixIndication
import io.github.ra1nify.rmuix.kmp.utils.RmuixOverscrollFactory

/**
 * The Rmuix theme that provides color and text styles for the Rmuix components.
 * This theme supports dynamic color schemes through the [ThemeController].
 *
 * @param controller The [ThemeController] that controls the current color scheme.
 * @param textStyles The text styles for the Rmuix components.
 * @param content The content of the Rmuix theme.
 */
@Composable
fun RmuixTheme(
    controller: ThemeController,
    textStyles: TextStyles = RmuixTheme.textStyles,
    content: @Composable () -> Unit,
) {
    ProvideDisplayCutoutFix()
    val rawColors = controller.currentColors()
    val rmuixColors = remember { rawColors.copy() }.apply { updateColorsFrom(rawColors) }
    val rmuixTextStyles = remember { textStyles.copy() }.apply { updateTextStylesFrom(textStyles) }
    val rmuixIndication = remember(rmuixColors.onBackground) { RmuixIndication(color = rmuixColors.onBackground) }
    CompositionLocalProvider(
        LocalColors provides rmuixColors,
        LocalTextStyles provides rmuixTextStyles,
        LocalIndication provides rmuixIndication,
        LocalContentColor provides rmuixColors.onBackground,
        LocalColorSchemeMode provides controller.colorSchemeMode,
        LocalOverscrollFactory provides RmuixOverscrollFactory,
    ) {
        content()
    }
}

/**
 * The Rmuix theme that provides color and text styles for the Rmuix components.
 * This theme uses the provided [colors] and [textStyles].
 *
 * @param colors The color scheme for the Rmuix components.
 * @param textStyles The text styles for the Rmuix components.
 * @param content The content of the Rmuix theme.
 */
@Composable
fun RmuixTheme(
    colors: Colors = RmuixTheme.colorScheme,
    textStyles: TextStyles = RmuixTheme.textStyles,
    content: @Composable () -> Unit,
) {
    ProvideDisplayCutoutFix()
    val rmuixColors = remember { colors.copy() }.apply { updateColorsFrom(colors) }
    val rmuixTextStyles = remember { textStyles.copy() }.apply { updateTextStylesFrom(textStyles) }
    val rmuixIndication = remember(rmuixColors.onBackground) { RmuixIndication(color = rmuixColors.onBackground) }
    CompositionLocalProvider(
        LocalColors provides rmuixColors,
        LocalTextStyles provides rmuixTextStyles,
        LocalIndication provides rmuixIndication,
        LocalOverscrollFactory provides RmuixOverscrollFactory,
    ) {
        content()
    }
}

object RmuixTheme {
    val colorScheme: Colors
        @Composable @ReadOnlyComposable
        get() = LocalColors.current

    val textStyles: TextStyles
        @Composable @ReadOnlyComposable
        get() = LocalTextStyles.current

    val colorSchemeMode: ColorSchemeMode?
        @Composable @ReadOnlyComposable
        get() = LocalColorSchemeMode.current

    val isDynamicColor: Boolean
        @Composable @ReadOnlyComposable
        get() = when (colorSchemeMode) {
            ColorSchemeMode.MonetSystem,
            ColorSchemeMode.MonetLight,
            ColorSchemeMode.MonetDark,
            -> true

            else -> false
        }
}
