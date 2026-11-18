// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.glass

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBar
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBarDefaults
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarDisplayMode
import io.github.ra1nify.rmuix.kmp.blur.Backdrop
import io.github.ra1nify.rmuix.kmp.blur.ColorOSGlassType
import io.github.ra1nify.rmuix.kmp.blur.ColorOSLiquidGlassDefaults
import io.github.ra1nify.rmuix.kmp.blur.colorOSLiquidGlass
import io.github.ra1nify.rmuix.kmp.blur.drawBackdrop
import io.github.ra1nify.rmuix.kmp.squircle.squircleClip
import io.github.ra1nify.rmuix.kmp.squircle.squircleSurface
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * [FloatingNavigationBar] with the ColorOS glass material on the pill.
 *
 * The material comes from `rmuix-blur`'s `colorOSLiquidGlass(ColorOSGlassType.BottomBar)`, i.e. the
 * ColorOS bottom-bar preset built from `COUIMaterialBlurEffect`'s two blend layers, so no new colour is
 * invented here. The pill keeps the stock geometry (capsule radius, shadow, indicator) and only its
 * background changes.
 *
 * The bar samples the [Backdrop] installed through [LocalGlassBackdrop] (or passed explicitly). Without a
 * backdrop it degrades to the bar's solid [color], exactly like the other glass components.
 *
 * @param backdrop Backdrop captured around the scene behind the bar; defaults to the locally installed one.
 * @param color Solid fallback colour used when no backdrop is available.
 */
@Composable
fun GlassFloatingNavigationBar(
    modifier: Modifier = Modifier,
    backdrop: Backdrop? = LocalGlassBackdrop.current,
    mode: NavigationBarDisplayMode = NavigationBarDisplayMode.IconAndText,
    meltEffectMode: Int = 0,
    startAccessory: (@Composable () -> Unit)? = null,
    endAccessory: (@Composable () -> Unit)? = null,
    startAccessoryVisible: Boolean = true,
    endAccessoryVisible: Boolean = true,
    color: Color = RmuixTheme.colorScheme.surfaceContainer,
    indicatorColor: Color = if (isGlassInDarkTheme()) Color(0xFF2C2C2E) else Color.White,
    cornerRadius: Dp = FloatingNavigationBarDefaults.CornerRadius,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    horizontalOutSidePadding: Dp = FloatingNavigationBarDefaults.HorizontalOutSidePadding,
    shadowElevation: Dp = FloatingNavigationBarDefaults.ShadowElevation,
    showDivider: Boolean = false,
    defaultWindowInsetsPadding: Boolean = true,
    content: @Composable () -> Unit,
) {
    val dark = isGlassInDarkTheme()
    FloatingNavigationBar(
        modifier = modifier,
        mode = mode,
        meltEffectMode = meltEffectMode,
        startAccessory = startAccessory,
        endAccessory = endAccessory,
        startAccessoryVisible = startAccessoryVisible,
        endAccessoryVisible = endAccessoryVisible,
        color = Color.Transparent,
        indicatorColor = indicatorColor,
        cornerRadius = cornerRadius,
        horizontalAlignment = horizontalAlignment,
        horizontalOutSidePadding = horizontalOutSidePadding,
        shadowElevation = shadowElevation,
        showDivider = showDivider,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        surface = { pillCornerRadius ->
            if (backdrop == null) {
                Modifier.squircleSurface(color = color, cornerRadius = pillCornerRadius)
            } else {
                val shape: Shape = RoundedCornerShape(pillCornerRadius)
                Modifier
                    .squircleClip(pillCornerRadius)
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { shape },
                        effects = { colorOSLiquidGlass(ColorOSGlassType.BottomBar, dark) },
                    )
            }
        },
        content = content,
    )
}
