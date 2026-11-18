// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.glass

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.blur.Backdrop
import io.github.ra1nify.rmuix.kmp.blur.BlurColors
import io.github.ra1nify.rmuix.kmp.blur.LayerBackdrop
import io.github.ra1nify.rmuix.kmp.blur.blendColors
import io.github.ra1nify.rmuix.kmp.blur.blur
import io.github.ra1nify.rmuix.kmp.blur.drawBackdrop
import io.github.ra1nify.rmuix.kmp.blur.rememberLayerBackdrop
import io.github.ra1nify.rmuix.kmp.layout.DialogDefaults
import io.github.ra1nify.rmuix.kmp.overlay.OverlayDialog
import io.github.ra1nify.rmuix.kmp.squircle.squircleClip
import io.github.ra1nify.rmuix.kmp.squircle.squircleSurface
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.window.WindowDialog

/**
 * Captures this composable's content into a [LayerBackdrop] that a glass surface can sample.
 *
 * Apply it to the content that sits *behind* the overlay dialog (`Modifier.layerBackdrop(backdrop)`),
 * then pass the same backdrop to [GlassOverlayDialog]. This is the in-scene route: the dialog and the
 * content must share one composition, which is what `OverlayDialog` provides.
 *
 * @param surfaceColor Optional opaque base painted before the content is recorded. Leave it
 *   [Color.Transparent] when the scene already paints its own background.
 */
@Composable
fun rememberGlassBackdrop(surfaceColor: Color = Color.Transparent): LayerBackdrop =
    rememberLayerBackdrop {
        if (surfaceColor != Color.Transparent) {
            drawRect(surfaceColor)
        }
        drawContent()
    }

/**
 * The [Backdrop] a glass dialog samples when the caller does not pass one explicitly.
 *
 * Provide it once at the root of the scene (next to the content's `Modifier.layerBackdrop`) so nested
 * screens can open a [GlassOverlayDialog] without threading the backdrop through every signature.
 */
val LocalGlassBackdrop = staticCompositionLocalOf<Backdrop?> { null }

/**
 * Paints the ColorOS 17 dialog material behind this composable: the sampled backdrop is blurred by
 * [GlassDefaults.DIALOG_BLUR_RADIUS_PX] pixels and tinted with [colors], clipped to a squircle of `cornerRadius`.
 *
 * Feed it to a dialog's `surface` slot - the slot replaces the solid background, so the panel becomes
 * translucent and the content is still clipped to the stock rounded outline.
 *
 * @param backdrop The backdrop captured by [rememberGlassBackdrop] around the content behind the dialog.
 * @param cornerRadius The dialog corner radius, usually the `DialogSurfaceScope.cornerRadius` value.
 * @param blurRadiusPx Blur radius in pixels, matching the stock `OplusBlurParam` unit.
 * @param colors Material tint layers; defaults to [GlassDefaults.dialogBlurColors].
 * @param enabled When false the backdrop effect is skipped and the content draws normally, which is the
 *   fallback on platforms without runtime shader support.
 */
@Composable
fun Modifier.glassDialogSurface(
    backdrop: Backdrop,
    cornerRadius: Dp,
    blurRadiusPx: Float = GlassDefaults.DIALOG_BLUR_RADIUS_PX,
    colors: BlurColors = GlassDefaults.dialogBlurColors(),
    enabled: Boolean = true,
): Modifier {
    val shape: Shape = RoundedCornerShape(cornerRadius)
    // `drawBackdrop` degrades internally: AGSL pipeline on Android 13+, RenderEffect blur on Android 12,
    // and tint-only below that (see the capability tiers in the blur module).
    return this
        .squircleClip(cornerRadius)
        .drawBackdrop(
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
 * [OverlayDialog] with the ColorOS 17 blurred dialog material.
 *
 * The overlay variant draws inside the host scene, so a [LayerBackdrop] taken around the content behind
 * it can be sampled directly - this is the exact-material path. Use [rememberGlassBackdrop] +
 * `Modifier.layerBackdrop(backdrop)` on the app content, then open this dialog with the same backdrop.
 *
 * All layout, typography, button-bar and dismissal parameters behave exactly like [OverlayDialog]; only
 * the surface changes.
 */
@Composable
fun GlassOverlayDialog(
    show: Boolean,
    modifier: Modifier = Modifier,
    backdrop: Backdrop? = LocalGlassBackdrop.current,
    title: String? = null,
    titleColor: Color = DialogDefaults.titleColor(),
    summary: String? = null,
    summaryColor: Color = DialogDefaults.summaryColor(),
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    outsideMargin: DpSize = DialogDefaults.outsideMargin,
    insideMargin: DpSize = DialogDefaults.insideMargin,
    defaultWindowInsetsPadding: Boolean = true,
    renderInRootScaffold: Boolean = true,
    maxWidth: Dp = DialogDefaults.MaxWidth,
    largeScreen: Boolean? = null,
    cornerRadius: Dp? = null,
    dimColor: Color = RmuixTheme.colorScheme.windowDimming,
    confirmButtonText: String? = null,
    onConfirm: (() -> Unit)? = null,
    confirmButtonDestructive: Boolean = false,
    dismissButtonText: String? = null,
    onDismiss: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    blurRadiusPx: Float = GlassDefaults.DIALOG_BLUR_RADIUS_PX,
    colors: BlurColors? = null,
    content: @Composable () -> Unit,
) {
    OverlayDialog(
        show = show,
        modifier = modifier,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        outsideMargin = outsideMargin,
        insideMargin = insideMargin,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        renderInRootScaffold = renderInRootScaffold,
        maxWidth = maxWidth,
        largeScreen = largeScreen,
        cornerRadius = cornerRadius,
        dimColor = dimColor,
        confirmButtonText = confirmButtonText,
        onConfirm = onConfirm,
        confirmButtonDestructive = confirmButtonDestructive,
        dismissButtonText = dismissButtonText,
        onDismiss = onDismiss,
        icon = icon,
        surface = {
            if (backdrop != null) {
                Modifier.glassDialogSurface(
                    backdrop = backdrop,
                    cornerRadius = this.cornerRadius,
                    blurRadiusPx = blurRadiusPx,
                    colors = colors ?: GlassDefaults.dialogBlurColors(),
                )
            } else {
                // No backdrop was provided or installed: keep the stock tint so the panel still reads as
                // glass instead of falling back to an opaque miuix surface.
                val tint = if (isGlassInDarkTheme()) GlassDefaults.DialogMixDark else GlassDefaults.DialogMixLight
                Modifier.squircleSurface(color = tint, cornerRadius = this.cornerRadius)
            }
        },
        content = content,
    )
}

/**
 * [WindowDialog] with the ColorOS 17 dialog material.
 *
 * A window dialog lives in its own platform window, so an in-scene [LayerBackdrop] cannot see the
 * activity behind it. This variant therefore asks the platform for a background blur where one exists
 * (Android 12+ `Window.setBackgroundBlurRadius`, wired in `ApplyWindowBackgroundBlur`) and paints the
 * stock translucent tint on top. Platforms without window blur fall back to the tint alone, which is why
 * the content behind it is **not** blurred there - the layout and colours stay stock, the material does
 * not.
 */
@Composable
fun GlassWindowDialog(
    show: Boolean,
    modifier: Modifier = Modifier,
    title: String? = null,
    titleColor: Color = DialogDefaults.titleColor(),
    summary: String? = null,
    summaryColor: Color = DialogDefaults.summaryColor(),
    enableWindowDim: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    onDismissFinished: (() -> Unit)? = null,
    outsideMargin: DpSize = DialogDefaults.outsideMargin,
    insideMargin: DpSize = DialogDefaults.insideMargin,
    defaultWindowInsetsPadding: Boolean = true,
    maxWidth: Dp = DialogDefaults.MaxWidth,
    largeScreen: Boolean? = null,
    cornerRadius: Dp? = null,
    dimColor: Color = RmuixTheme.colorScheme.windowDimming,
    confirmButtonText: String? = null,
    onConfirm: (() -> Unit)? = null,
    confirmButtonDestructive: Boolean = false,
    dismissButtonText: String? = null,
    onDismiss: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    backdrop: Backdrop? = null,
    blurRadiusPx: Float = GlassDefaults.DIALOG_BLUR_RADIUS_PX,
    colors: BlurColors? = null,
    content: @Composable () -> Unit,
) {
    ApplyWindowBackgroundBlur(radiusPx = blurRadiusPx.toInt())
    WindowDialog(
        show = show,
        modifier = modifier,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        enableWindowDim = enableWindowDim,
        onDismissRequest = onDismissRequest,
        onDismissFinished = onDismissFinished,
        outsideMargin = outsideMargin,
        insideMargin = insideMargin,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        maxWidth = maxWidth,
        largeScreen = largeScreen,
        cornerRadius = cornerRadius,
        dimColor = dimColor,
        confirmButtonText = confirmButtonText,
        onConfirm = onConfirm,
        confirmButtonDestructive = confirmButtonDestructive,
        dismissButtonText = dismissButtonText,
        onDismiss = onDismiss,
        icon = icon,
        surface = {
            val tint = if (isGlassInDarkTheme()) GlassDefaults.DialogMixDark else GlassDefaults.DialogMixLight
            if (backdrop != null) {
                Modifier
                    .glassDialogSurface(
                        backdrop = backdrop,
                        cornerRadius = this.cornerRadius,
                        blurRadiusPx = blurRadiusPx,
                        colors = colors ?: GlassDefaults.dialogBlurColors(),
                    )
            } else {
                Modifier.squircleSurface(color = tint, cornerRadius = this.cornerRadius)
            }
        },
        content = content,
    )
}

/**
 * Applies the platform's own background blur to the dialog window this composable is hosted in.
 *
 * Android 12+ exposes `Window.setBackgroundBlurRadius`; the Android implementation sets it together
 * with `FLAG_BLUR_BEHIND`. Other targets have no equivalent and keep the translucent tint only.
 *
 * @param radiusPx Blur radius in pixels, matching the stock `coui_dialog_blur_radius` unit.
 */
@Composable
internal expect fun ApplyWindowBackgroundBlur(radiusPx: Int)
