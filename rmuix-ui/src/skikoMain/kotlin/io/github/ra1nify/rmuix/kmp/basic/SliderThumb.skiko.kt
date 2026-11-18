// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.skiaCanvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color4f
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.FilterTileMode
import org.jetbrains.skia.Gradient
import org.jetbrains.skia.Image
import org.jetbrains.skia.MaskFilter
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PaintMode
import org.jetbrains.skia.RRect
import org.jetbrains.skia.Rect
import org.jetbrains.skia.SamplingMode
import org.jetbrains.skia.Shader

@Composable
internal actual fun rememberSliderThumbRenderer(): SliderThumbRenderer {
    val renderer = remember { SkiaSliderThumbRenderer() }
    DisposableEffect(renderer) {
        onDispose { renderer.dispose() }
    }
    return renderer
}

private class SkiaSliderThumbRenderer : SliderThumbRenderer {
    private val plain = Paint().apply { isAntiAlias = true }
    private val shadow = Paint().apply { isAntiAlias = true }
    private val gradient = Paint().apply { isAntiAlias = true }
    private val layer = Paint()
    private val bitmapPaint = Paint().apply { isAntiAlias = true }
    private val innerFill =
        Paint().apply {
            isAntiAlias = true
            color = SliderThumbValues.INNER_FILL
        }
    private val innerBlur = MaskFilter.makeBlur(FilterBlurMode.NORMAL, MaskFilter.convertRadiusToSigma(SliderThumbValues.INNER_BLUR_PX))
    private val innerShadow =
        Paint().apply {
            isAntiAlias = true
            mode = PaintMode.STROKE
            strokeWidth = SliderThumbValues.INNER_STROKE_PX
            color = -1
            maskFilter = innerBlur
        }
    private val cover =
        Paint().apply {
            isAntiAlias = true
            color = -1
        }
    private var gradientDiameter = 0f
    private var gradientShader: Shader? = null
    private var shadowRadius = 0f
    private var shadowFilter: MaskFilter? = null
    private var image: Image? = null

    override fun draw(
        scope: DrawScope,
        frame: SliderThumbFrame,
    ) {
        if (frame.radius <= 0f || !frame.radius.isFinite()) return
        scope.drawIntoCanvas { wrapped ->
            val canvas = wrapped.skiaCanvas
            val diameter = frame.radius * 2f
            val bounds = RRect.makeXYWH(0f, 0f, diameter, diameter, frame.radius)
            canvas.save()
            try {
                canvas.translate(frame.center.x - frame.radius, frame.center.y - frame.radius)
                if (!frame.textured) {
                    if (frame.enabled && frame.shadowRadius > 0f) {
                        if (shadowRadius != frame.shadowRadius || shadowFilter == null) {
                            shadow.maskFilter = null
                            shadowFilter?.close()
                            shadowRadius = frame.shadowRadius
                            shadowFilter = MaskFilter.makeBlur(FilterBlurMode.NORMAL, MaskFilter.convertRadiusToSigma(shadowRadius))
                            shadow.maskFilter = shadowFilter
                        }
                        shadow.color = frame.shadowColor.toArgb()
                        canvas.drawCircle(frame.radius, frame.radius + frame.shadowOffset, frame.radius, shadow)
                    }
                    plain.color = frame.color.toArgb()
                    canvas.drawRRect(bounds, plain)
                    return@drawIntoCanvas
                }
                layer.alpha = sliderThumbAlpha(frame.textureAlpha)
                if (layer.alpha > 0) {
                    canvas.saveLayer(Rect.makeXYWH(0f, 0f, diameter, diameter), layer)
                    try {
                        if (gradientDiameter != diameter || gradientShader == null) {
                            gradient.shader = null
                            gradientShader?.close()
                            gradientDiameter = diameter
                            gradientShader =
                                Shader.makeLinearGradient(
                                    0f,
                                    diameter,
                                    0f,
                                    0f,
                                    Gradient(
                                        Gradient.Colors(
                                            arrayOf(
                                                Color4f(SliderThumbValues.GRADIENT_OPAQUE),
                                                Color4f(SliderThumbValues.GRADIENT_OPAQUE),
                                                Color4f(SliderThumbValues.GRADIENT_TRANSPARENT),
                                            ),
                                            floatArrayOf(0f, 0.2f, 1f),
                                            FilterTileMode.CLAMP,
                                        ),
                                    ),
                                )
                            gradient.shader = gradientShader
                        }
                        canvas.drawRRect(bounds, gradient)
                        val texture = image ?: createImage(diameter, frame.radius).also { image = it }
                        val padding = SliderThumbValues.TEXTURE_PADDING_PX
                        canvas.drawImageRect(
                            texture,
                            Rect.makeXYWH(0f, 0f, texture.width.toFloat(), texture.height.toFloat()),
                            Rect.makeXYWH(-padding, -padding, diameter + padding * 2f, diameter + padding * 2f),
                            SamplingMode.LINEAR,
                            bitmapPaint,
                            false,
                        )
                    } finally {
                        canvas.restore()
                    }
                }
                cover.alpha = sliderThumbAlpha(frame.coverAlpha)
                if (cover.alpha > 0) canvas.drawRRect(bounds, cover)
            } finally {
                canvas.restore()
            }
        }
    }

    private fun createImage(
        diameter: Float,
        radius: Float,
    ): Image {
        val bitmap = Bitmap()
        try {
            val size = sliderThumbBitmapSize(diameter)
            check(bitmap.allocN32Pixels(size, size))
            val canvas = Canvas(bitmap)
            try {
                canvas.clear(0)
                val padding = SliderThumbValues.TEXTURE_PADDING_PX
                val rect = RRect.makeXYWH(padding, padding, diameter, diameter, radius)
                canvas.drawRRect(rect, innerFill)
                canvas.save()
                canvas.clipRRect(rect, false)
                canvas.translate(0f, SliderThumbValues.INNER_OFFSET_PX)
                canvas.drawOval(Rect.makeXYWH(padding, padding, diameter, diameter), innerShadow)
                canvas.restore()
            } finally {
                canvas.close()
            }
            return Image.makeFromBitmap(bitmap)
        } finally {
            bitmap.close()
        }
    }

    override fun dispose() {
        image?.close()
        image = null
        gradient.shader = null
        gradientShader?.close()
        gradientShader = null
        shadow.maskFilter = null
        shadowFilter?.close()
        shadowFilter = null
        innerShadow.maskFilter = null
        innerBlur.close()
        plain.close()
        shadow.close()
        gradient.close()
        layer.close()
        bitmapPaint.close()
        innerFill.close()
        innerShadow.close()
        cover.close()
    }
}
