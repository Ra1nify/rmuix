// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.squircle.SquircleShape
import io.github.ra1nify.rmuix.kmp.squircle.isSquircleEnabled
import io.github.ra1nify.rmuix.kmp.squircle.squircleBackground
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * A [FloatingToolbar] that renders its content in a Card, arranged either horizontally or vertically.
 * The actual placement on screen is handled by the parent, typically Scaffold.
 *
 * @param modifier The modifier to be applied to the [FloatingToolbar].
 * @param color Background color of the [FloatingToolbar].
 * @param cornerRadius Corner radius of the [FloatingToolbar].
 * @param outSidePadding Padding outside the [FloatingToolbar].
 * @param shadowElevation The shadow elevation of the [FloatingToolbar].
 * @param showDivider Whether to show the divider line around the [FloatingToolbar].
 * @param content The [Composable] content of the [FloatingToolbar].
 */
@Composable
@NonRestartableComposable
fun FloatingToolbar(
    modifier: Modifier = Modifier,
    color: Color = FloatingToolbarDefaults.defaultColor(),
    cornerRadius: Dp = FloatingToolbarDefaults.CornerRadius,
    outSidePadding: PaddingValues = FloatingToolbarDefaults.OutSidePadding,
    shadowElevation: Dp = FloatingToolbarDefaults.ShadowElevation,
    showDivider: Boolean = false,
    // null follows the stock window-width class (16 / 24 / 40dp); pass a value to pin it.
    groupMargin: Dp? = null,
    content: @Composable () -> Unit,
) {
    // Shadow and fill must share one silhouette; `squircleBackground` degrades to a rounded rect when
    // the shader path is unavailable, so the shape follows the same condition.
    val squircleEnabled = isSquircleEnabled()
    val shape =
        remember(cornerRadius, squircleEnabled) {
            SquircleShape(cornerRadius, squircleEnabled = squircleEnabled)
        }
    val dividerColor = RmuixTheme.colorScheme.dividerLine
    // COUIBottomFloatingToolbar.changePadding() picks the group margin from
    // COUIResponsiveUtils.{isSmall,isMedium,isLarge}Screen, i.e. material's WindowWidthSizeClass
    // (Compact < 600dp, Medium 600..840dp, Expanded >= 840dp). The stock code passes the toolbar's
    // own measured width; before measurement that width is unknown, so this port uses the window
    // container width, which is the same value for a full-bleed bottom bar.
    val density = LocalDensity.current
    val toolbarWidthDp =
        with(density) {
            LocalWindowInfo.current.containerSize
                .width
                .toDp()
        }
    val resolvedGroupMargin = groupMargin ?: FloatingToolbarDefaults.groupMarginFor(toolbarWidthDp)

    val dividerMod =
        if (showDivider) {
            Modifier
                .squircleBackground(color = dividerColor, cornerRadius = cornerRadius)
                .padding(0.75.dp)
        } else {
            Modifier
        }
    val shadowMod =
        if (shadowElevation > 0.dp) {
            Modifier.dropShadow(
                shape = shape,
                shadow =
                    Shadow(
                        radius = 10.dp,
                        color = Color.Black,
                        alpha = 0.1f,
                    ),
            )
        } else {
            Modifier
        }

    Box(
        modifier =
            modifier
                .padding(outSidePadding)
                .then(dividerMod)
                .then(shadowMod)
                .squircleBackground(color = color, cornerRadius = cornerRadius)
                .pointerInput(Unit) {
                    detectTapGestures { /* Consume click */ }
                },
    ) {
        Box(modifier = Modifier.padding(horizontal = resolvedGroupMargin)) {
            content()
        }
    }
}

object FloatingToolbarDefaults {
    /** coui_floating_toolbar_group_margin_small_screen: 16dp, used for Compact widths. */
    val GroupMarginSmallScreen = 16.dp

    /** coui_floating_toolbar_group_margin_medium_screen: 24dp, used for Medium widths. */
    val GroupMarginMediumScreen = 24.dp

    /** coui_floating_toolbar_group_margin_large_screen: 40dp, used for Expanded widths. */
    val GroupMarginLargeScreen = 40.dp

    /** Material window-width breakpoints the stock COUIResponsiveUtils delegates to. */
    val MediumScreenMinWidth = 600.dp

    /** Material window-width breakpoints the stock COUIResponsiveUtils delegates to. */
    val ExpandedScreenMinWidth = 840.dp

    /**
     * The group margin for [windowWidthDp], mirroring COUIBottomFloatingToolbar.changePadding()
     * (Compact / Medium / Expanded -> 16 / 24 / 40dp).
     */
    fun groupMarginFor(windowWidthDp: Dp): Dp =
        when {
            windowWidthDp < MediumScreenMinWidth -> GroupMarginSmallScreen
            windowWidthDp < ExpandedScreenMinWidth -> GroupMarginMediumScreen
            else -> GroupMarginLargeScreen
        }

    /**
     * Default corner radius of the [FloatingToolbar].
     */
    val CornerRadius = 50.dp

    /** Default drop shadow of the [FloatingToolbar]; used when the caller does not pass one. */
    val ShadowElevation = 4.dp

    /**
     * Default color of the [FloatingToolbar].
     */
    @Composable
    fun defaultColor() = RmuixTheme.colorScheme.surfaceContainer

    /**
     * Default padding outside the [FloatingToolbar].
     */
    val OutSidePadding = PaddingValues(12.dp, 8.dp)
}
