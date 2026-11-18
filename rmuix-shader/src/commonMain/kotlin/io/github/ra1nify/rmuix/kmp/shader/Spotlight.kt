// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.shader

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

/**
 * Draws the shared COUI light shader over content using a source-backed preset.
 * A NaN radius selects the preset's raw-pixel radius. Explicit radii remain pixel overrides.
 * Unsupported renderers omit the effect rather than substituting a radial gradient.
 */
@Suppress("ktlint:compose:modifier-composed-check")
fun Modifier.rmuixSpotlight(
    glowColor: Color = Color.White,
    lightRadius: Float = Float.NaN,
    shape: Shape? = null,
    enabled: Boolean = true,
    style: SpotlightStyle = SpotlightStyle.OpaqueMedium,
    darkTheme: Boolean? = null,
): Modifier =
    composed {
        if (!enabled) return@composed this
        require(
            lightRadius.isNaN() || (lightRadius.isFinite() && lightRadius > 0f),
        ) { "lightRadius must be positive or NaN for the preset" }
        val dark = darkTheme ?: isSystemInDarkTheme()
        val parameters =
            remember(style, dark, lightRadius) {
                val preset = spotlightParameters(style, dark)
                if (lightRadius.isNaN()) preset else preset.copy(endRadius = lightRadius)
            }
        val scope = rememberCoroutineScope()
        val effect = remember(scope) { StockSpotlightEffect(scope) }
        SideEffect { effect.configure(parameters, glowColor) }
        DisposableEffect(effect) {
            onDispose { effect.dispose() }
        }
        if (!effect.supported) return@composed this

        this
            .pointerInput(effect) {
                val tracker = SpotlightPointerTracker()
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    if (down.isConsumed) return@awaitEachGesture
                    tracker.reset()
                    var finished = false
                    try {
                        var event = currentEvent
                        while (true) {
                            if (event.changes.any { it.isConsumed }) {
                                effect.release()
                                finished = true
                                break
                            }
                            val input = tracker.sample(event)
                            effect.onInput(input, size)
                            if (input.action == SpotlightAction.Up || input.action == SpotlightAction.Cancel) {
                                finished = true
                                break
                            }
                            if (event.changes.none { it.pressed }) break
                            event = awaitPointerEvent(PointerEventPass.Initial)
                        }
                    } finally {
                        tracker.reset()
                        if (!finished) effect.release()
                    }
                }
            }.drawWithCache {
                val outline = shape?.createOutline(size, layoutDirection, this)
                onDrawWithContent {
                    drawContent()
                    if (outline != null) {
                        clipOutline(outline) { effect.draw(this) }
                    } else {
                        clipRect { effect.draw(this) }
                    }
                }
            }
    }

private inline fun DrawScope.clipOutline(
    outline: Outline,
    block: DrawScope.() -> Unit,
) {
    when (outline) {
        is Outline.Rectangle -> clipRect(outline.rect.left, outline.rect.top, outline.rect.right, outline.rect.bottom, block = block)
        is Outline.Rounded -> clipPath(Path().apply { addRoundRect(outline.roundRect) }, block = block)
        is Outline.Generic -> clipPath(outline.path, block = block)
    }
}

/** Dp radius override of the same shared light implementation. */
@Suppress("ktlint:compose:modifier-composed-check")
fun Modifier.rmuixSpotlight(
    glowColor: Color = Color.White,
    lightRadius: Dp,
    shape: Shape? = null,
    enabled: Boolean = true,
    style: SpotlightStyle = SpotlightStyle.OpaqueMedium,
    darkTheme: Boolean? = null,
): Modifier =
    composed {
        val radius = with(LocalDensity.current) { lightRadius.toPx() }
        this.rmuixSpotlight(glowColor, radius, shape, enabled, style, darkTheme)
    }
