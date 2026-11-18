// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.blur

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import io.github.ra1nify.rmuix.kmp.shader.isRenderEffectSupported
import kotlin.math.roundToInt

/**
 * Whether any backdrop effect can be rendered on this platform.
 *
 * Two tiers exist: the full material pipeline needs runtime shaders (`isRuntimeShaderSupported`, Android 13+
 * or skiko), while a plain Gaussian blur can already run on `RenderEffect` (`isRenderEffectSupported`,
 * Android 12+ / skiko). When neither is available, callers should paint their solid/translucent fallback.
 */
fun isBackdropEffectSupported(): Boolean = isRuntimeShaderSupported() || isRenderEffectSupported()

/**
 * A render-effect-only fallback blur for platforms without runtime shaders (Android 12 / 12L).
 *
 * The full [drawBackdrop] pipeline is built on AGSL shaders, so it is skipped below Android 13. This
 * modifier keeps a real blurred backdrop on those devices by recording the [Backdrop] into a graphics layer
 * and applying a plain [BlurEffect]; [tintColors] are then painted on top in order (only `SrcOver` layers
 * are reproducible without a shader).
 *
 * Callers should clip the surface themselves (the glass components already apply `squircleClip`), and should
 * only use this when [isBackdropEffectSupported] is true but [isRuntimeShaderSupported] is false.
 *
 * @param backdrop The sampled backdrop; apply `Modifier.layerBackdrop(backdrop)` to the content behind.
 * @param blurRadiusPx Blur radius in pixels, matching the stock `OplusBlurParam` unit. Note that the platform
 *   blur's exact falloff differs from the shader pipeline's multi-tap Gaussian, so the visual result is an
 *   approximation.
 * @param tintColors Layers painted over the blurred backdrop, in order.
 * @param enabled When false the modifier is a no-op and the content draws normally.
 */
@Composable
fun Modifier.legacyBackdropBlur(
    backdrop: Backdrop,
    blurRadiusPx: Float,
    tintColors: List<BlendColorEntry> = emptyList(),
    enabled: Boolean = true,
): Modifier {
    if (!enabled) return this
    val layer = rememberGraphicsLayer()
    val density = LocalDensity.current
    var coordinates: LayoutCoordinates? by remember { mutableStateOf(null) }
    val tint = remember(tintColors) { tintColors }

    return this
        .onPlaced { coordinates = it }
        .drawWithContent {
            val width = size.width.roundToInt().coerceAtLeast(1)
            val height = size.height.roundToInt().coerceAtLeast(1)
            layer.record(size = IntSize(width, height)) {
                with(backdrop) {
                    drawBackdrop(
                        density = density,
                        coordinates = coordinates,
                    )
                }
            }
            layer.renderEffect = BlurEffect(blurRadiusPx, blurRadiusPx, TileMode.Clamp)
            drawLayer(layer)
            for (entry in tint) {
                drawRect(color = entry.color, topLeft = Offset.Zero)
            }
            drawContent()
        }
}
