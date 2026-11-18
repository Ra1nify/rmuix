// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.glass

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.blur.Backdrop
import io.github.ra1nify.rmuix.kmp.blur.BlurColors
import io.github.ra1nify.rmuix.kmp.blur.blendColors
import io.github.ra1nify.rmuix.kmp.blur.blur
import io.github.ra1nify.rmuix.kmp.blur.drawBackdrop
import io.github.ra1nify.rmuix.kmp.layout.BottomSheetDefaults
import io.github.ra1nify.rmuix.kmp.overlay.OverlayBottomSheet
import io.github.ra1nify.rmuix.kmp.squircle.squircleClip
import io.github.ra1nify.rmuix.kmp.squircle.squircleSurface
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.window.WindowBottomSheet

/**
 * Paints the ColorOS 17 bottom-sheet material behind this composable: the sampled backdrop is blurred by
 * [GlassDefaults.BOTTOM_SHEET_BLUR_RADIUS_PX] pixels and tinted with [colors].
 *
 * The sheet only rounds its top corners (`BottomSheetSurfaceScope.cornerRadius`; the bottom edge is
 * square), so the clip and the sampled shape follow that geometry.
 *
 * @param backdrop The backdrop captured around the content behind the sheet.
 * @param cornerRadius The top-corner radius, usually the `BottomSheetSurfaceScope.cornerRadius` value.
 * @param blurRadiusPx Blur radius in pixels, matching the stock `OplusBlurParam` unit.
 * @param colors Material tint layers; defaults to [GlassDefaults.bottomSheetBlurColors].
 * @param enabled When false the backdrop effect is skipped and the content draws normally.
 */
@Composable
fun Modifier.glassBottomSheetSurface(
    backdrop: Backdrop,
    cornerRadius: Dp,
    blurRadiusPx: Float = GlassDefaults.BOTTOM_SHEET_BLUR_RADIUS_PX,
    colors: BlurColors = GlassDefaults.bottomSheetBlurColors(),
    enabled: Boolean = true,
): Modifier {
    val shape: Shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
    return this
        .squircleClip(
            topStart = cornerRadius,
            topEnd = cornerRadius,
            bottomEnd = 0.dp,
            bottomStart = 0.dp,
        ).drawBackdrop(
            backdrop = backdrop,
            shape = { shape },
            effects = {
                blur(radiusX = blurRadiusPx, radiusY = blurRadiusPx)
                blendColors(colors)
            },
            enabled = enabled,
        )
}

/**
 * [OverlayBottomSheet] with the ColorOS 17 blurred sheet material.
 *
 * Like [GlassOverlayDialog], the overlay variant shares the host scene, so the [Backdrop] installed through
 * [LocalGlassBackdrop] (or passed explicitly) can be sampled directly. Every layout, drag, dismissal and
 * content parameter behaves exactly like [OverlayBottomSheet].
 */
@Composable
fun GlassOverlayBottomSheet(
    show: Boolean,
    modifier: Modifier = Modifier,
    backdrop: Backdrop? = LocalGlassBackdrop.current,
    title: String? = null,
    startAction: (@Composable () -> Unit)? = null,
    endAction: (@Composable () -> Unit)? = null,
    enableWindowDim: Boolean = true,
    cornerRadius: Dp = BottomSheetDefaults.cornerRadius,
    sheetMaxWidth: Dp = BottomSheetDefaults.maxWidth,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    outsideMargin: DpSize = BottomSheetDefaults.outsideMargin,
    insideMargin: DpSize = BottomSheetDefaults.insideMargin,
    defaultWindowInsetsPadding: Boolean = true,
    dragHandleColor: Color = BottomSheetDefaults.dragHandleColor(),
    allowDismiss: Boolean = true,
    enableNestedScroll: Boolean = true,
    renderInRootScaffold: Boolean = true,
    dimColor: Color = RmuixTheme.colorScheme.windowDimming,
    blurRadiusPx: Float = GlassDefaults.BOTTOM_SHEET_BLUR_RADIUS_PX,
    colors: BlurColors? = null,
    content: @Composable () -> Unit,
) {
    OverlayBottomSheet(
        show = show,
        modifier = modifier,
        title = title,
        startAction = startAction,
        endAction = endAction,
        enableWindowDim = enableWindowDim,
        cornerRadius = cornerRadius,
        sheetMaxWidth = sheetMaxWidth,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        outsideMargin = outsideMargin,
        insideMargin = insideMargin,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        dragHandleColor = dragHandleColor,
        allowDismiss = allowDismiss,
        enableNestedScroll = enableNestedScroll,
        renderInRootScaffold = renderInRootScaffold,
        dimColor = dimColor,
        surface = {
            if (backdrop != null) {
                Modifier.glassBottomSheetSurface(
                    backdrop = backdrop,
                    cornerRadius = this.cornerRadius,
                    blurRadiusPx = blurRadiusPx,
                    colors = colors ?: GlassDefaults.bottomSheetBlurColors(),
                )
            } else {
                val tint = if (isGlassInDarkTheme()) GlassDefaults.BottomSheetMixDark else GlassDefaults.BottomSheetMixLight
                Modifier.squircleSurface(color = tint, cornerRadius = this.cornerRadius)
            }
        },
        content = content,
    )
}

/**
 * [WindowBottomSheet] with the ColorOS 17 sheet material.
 *
 * The window variant cannot sample the activity scene, so it asks the platform for a window blur where one
 * exists (Android 12+) and paints the stock translucent tint over it; other targets keep the tint only.
 */
@Composable
fun GlassWindowBottomSheet(
    show: Boolean,
    modifier: Modifier = Modifier,
    backdrop: Backdrop? = null,
    title: String? = null,
    startAction: (@Composable () -> Unit)? = null,
    endAction: (@Composable () -> Unit)? = null,
    enableWindowDim: Boolean = true,
    cornerRadius: Dp = BottomSheetDefaults.cornerRadius,
    sheetMaxWidth: Dp = BottomSheetDefaults.maxWidth,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    outsideMargin: DpSize = BottomSheetDefaults.outsideMargin,
    insideMargin: DpSize = BottomSheetDefaults.insideMargin,
    defaultWindowInsetsPadding: Boolean = true,
    dragHandleColor: Color = BottomSheetDefaults.dragHandleColor(),
    allowDismiss: Boolean = true,
    enableNestedScroll: Boolean = true,
    dimColor: Color = RmuixTheme.colorScheme.windowDimming,
    blurRadiusPx: Float = GlassDefaults.BOTTOM_SHEET_BLUR_RADIUS_PX,
    colors: BlurColors? = null,
    content: @Composable () -> Unit,
) {
    ApplyWindowBackgroundBlur(radiusPx = blurRadiusPx.toInt())
    WindowBottomSheet(
        show = show,
        modifier = modifier,
        title = title,
        startAction = startAction,
        endAction = endAction,
        enableWindowDim = enableWindowDim,
        cornerRadius = cornerRadius,
        sheetMaxWidth = sheetMaxWidth,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        outsideMargin = outsideMargin,
        insideMargin = insideMargin,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        dragHandleColor = dragHandleColor,
        allowDismiss = allowDismiss,
        enableNestedScroll = enableNestedScroll,
        dimColor = dimColor,
        surface = {
            val tint = if (isGlassInDarkTheme()) GlassDefaults.BottomSheetMixDark else GlassDefaults.BottomSheetMixLight
            if (backdrop != null) {
                Modifier.glassBottomSheetSurface(
                    backdrop = backdrop,
                    cornerRadius = this.cornerRadius,
                    blurRadiusPx = blurRadiusPx,
                    colors = colors ?: GlassDefaults.bottomSheetBlurColors(),
                )
            } else {
                Modifier.squircleSurface(color = tint, cornerRadius = this.cornerRadius)
            }
        },
        content = content,
    )
}
