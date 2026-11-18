// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.shader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * SkSL source for the ColorOS 17 MetaBall liquid melt effect between the satellite button and the center pill.
 *
 * The source is written once and runs through both backends of [RuntimeShader]: AGSL on Android (API 33+)
 * and Skiko `RuntimeEffect` on desktop / iOS / macOS / js / wasm, so the effect is **not** Android-only.
 */
private const val METABALL_SHADER = """
    uniform float2 uResolution;
    uniform float2 uCenter1;
    uniform float uRadius1;
    uniform float2 uCenter2;
    uniform float2 uHalfSize2;
    uniform float uRadius2;
    uniform float4 uColor;
    uniform float uProgress;

    float sdCircle(float2 p, float r) {
        return length(p) - r;
    }

    float sdRoundedBox(float2 p, float2 b, float r) {
        float2 q = abs(p) - b + float2(r);
        return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
    }

    float smin(float a, float b, float k) {
        float h = clamp(0.5 + 0.5 * (b - a) / k, 0.0, 1.0);
        return mix(b, a, h) - k * h * (1.0 - h);
    }

    half4 main(float2 fragCoord) {
        float d1 = sdCircle(fragCoord - uCenter1, uRadius1);
        float d2 = sdRoundedBox(fragCoord - uCenter2, uHalfSize2, uRadius2);

        // Smooth minimum factor k dynamically based on transition progress
        float k = 18.0 * (1.0 - abs(uProgress - 0.5) * 2.0);
        if (k <= 0.001) {
            return half4(0.0);
        }

        float d = smin(d1, d2, k);
        float alpha = 1.0 - smoothstep(-0.75, 0.75, d);

        if (alpha <= 0.001) {
            return half4(0.0);
        }

        return half4(uColor.rgb * alpha, uColor.a * alpha);
    }
"""

/**
 * Remembers a cross-platform SkSL [RuntimeShader] instance for the MetaBall liquid melt effect.
 */
@Composable
fun rememberMetaBallShader(): RuntimeShader? =
    remember {
        if (isRuntimeShaderSupported()) {
            runCatching { RuntimeShader(METABALL_SHADER) }.getOrNull()
        } else {
            null
        }
    }

/**
 * Draws the MetaBall liquid melt bridge between a circle and a rounded box.
 *
 * Runs on every [isRuntimeShaderSupported] backend; the name keeps `AGSL` for source compatibility.
 */
fun DrawScope.drawAGSLMetaBallBridge(
    shader: RuntimeShader,
    c1x: Float,
    c1y: Float,
    r1: Float,
    c2x: Float,
    c2y: Float,
    halfWidth2: Float,
    halfHeight2: Float,
    r2: Float,
    progress: Float,
    color: Color,
) {
    if (progress <= 0.01f || progress >= 0.99f) return

    runCatching {
        shader.setFloatUniform("uResolution", size.width, size.height)
        shader.setFloatUniform("uCenter1", c1x, c1y)
        shader.setFloatUniform("uRadius1", r1)
        shader.setFloatUniform("uCenter2", c2x, c2y)
        shader.setFloatUniform("uHalfSize2", halfWidth2, halfHeight2)
        shader.setFloatUniform("uRadius2", r2)
        shader.setColorUniform("uColor", color)
        shader.setFloatUniform("uProgress", progress)

        val brush: ShaderBrush = shader.asBrush()
        drawRect(brush = brush)
    }
}
