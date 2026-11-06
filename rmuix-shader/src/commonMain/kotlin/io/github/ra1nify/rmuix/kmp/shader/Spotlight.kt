// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.shader

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch

/**
 * ColorOS 17 COUISpotLight AGSL Shader.
 * Replicates genuine ColorOS interactive touch pointer light effect with cubic falloff.
 */
private const val SPOTLIGHT_AGSL = """
    uniform float2 uResolution;
    uniform float2 uPointer;
    uniform float uIntensity;
    uniform float4 uColor;
    uniform float uRadius;

    half4 main(float2 fragCoord) {
        float d = distance(fragCoord, uPointer);
        if (d > uRadius || uIntensity <= 0.0) {
            return half4(0.0);
        }
        float norm = d / uRadius;
        // ColorOS 17 cubic falloff curve
        float glow = pow(1.0 - norm, 2.5) * uIntensity;
        return half4(uColor.rgb * glow, uColor.a * glow);
    }
"""

/**
 * Attaches ColorOS 17 AGSL interactive spotlight to any Compose component.
 *
 * Automatically falls back to Compose Canvas RadialGradient if running on
 * an environment without RuntimeShader hardware acceleration.
 *
 * @param glowColor The color of the spotlight touch glow.
 * @param lightRadius The radius in pixels.
 * @param enabled Whether the spotlight effect is enabled.
 */
@Suppress("ktlint:compose:modifier-composed-check")
fun Modifier.rmuixSpotlight(
    glowColor: Color = Color(0x33FFFFFF),
    lightRadius: Float = 380f,
    enabled: Boolean = true,
): Modifier = composed {
    if (!enabled) return@composed this

    val shader = remember {
        if (isRuntimeShaderSupported()) {
            runCatching { RuntimeShader(SPOTLIGHT_AGSL) }.getOrNull()
        } else {
            null
        }
    }

    var pointerX by remember { mutableFloatStateOf(-1f) }
    var pointerY by remember { mutableFloatStateOf(-1f) }
    val intensity = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    this
        .pointerInput(enabled) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                pointerX = down.position.x
                pointerY = down.position.y
                scope.launch {
                    intensity.animateTo(1f, tween(100, easing = FastOutSlowInEasing))
                }

                do {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull()
                    if (change != null) {
                        pointerX = change.position.x
                        pointerY = change.position.y
                    }
                } while (event.changes.any { it.pressed })

                scope.launch {
                    intensity.animateTo(0f, tween(260, easing = FastOutSlowInEasing))
                }
            }
        }
        .drawWithCache {
            val currentIntensity = intensity.value
            val isSpotlightActive = currentIntensity > 0.001f && pointerX >= 0f && pointerY >= 0f

            val shaderBrush: ShaderBrush? = if (isSpotlightActive && shader != null) {
                runCatching {
                    shader.setFloatUniform("uResolution", size.width, size.height)
                    shader.setFloatUniform("uPointer", pointerX, pointerY)
                    shader.setFloatUniform("uIntensity", currentIntensity)
                    shader.setColorUniform("uColor", glowColor)
                    shader.setFloatUniform("uRadius", lightRadius)
                    shader.asBrush()
                }.getOrNull()
            } else {
                null
            }

            val fallbackBrush: Brush? = if (isSpotlightActive && shaderBrush == null) {
                Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = (glowColor.alpha * currentIntensity).coerceIn(0f, 1f)),
                        glowColor.copy(alpha = (glowColor.alpha * currentIntensity * 0.35f).coerceIn(0f, 1f)),
                        Color.Transparent,
                    ),
                    center = Offset(pointerX, pointerY),
                    radius = lightRadius,
                )
            } else {
                null
            }

            onDrawWithContent {
                if (isSpotlightActive) {
                    val activeBrush = shaderBrush ?: fallbackBrush
                    if (activeBrush != null) {
                        drawRect(brush = activeBrush)
                    }
                }
                drawContent()
            }
        }
}

/**
 * Density-correct Dp overload of [rmuixSpotlight].
 *
 * @param glowColor The color of the spotlight touch glow.
 * @param lightRadius The radius in Dp.
 * @param enabled Whether the spotlight effect is enabled.
 */
@Suppress("ktlint:compose:modifier-composed-check")
fun Modifier.rmuixSpotlight(
    glowColor: Color = Color(0x33FFFFFF),
    lightRadius: Dp,
    enabled: Boolean = true,
): Modifier = composed {
    val radiusPx = with(LocalDensity.current) { lightRadius.toPx() }
    this.rmuixSpotlight(glowColor = glowColor, lightRadius = radiusPx, enabled = enabled)
}
