// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

@Composable
internal actual fun rememberSliderThumbRenderer(): SliderThumbRenderer {
    val renderer = remember { AndroidSliderThumbRenderer() }
    DisposableEffect(renderer) {
        onDispose { renderer.dispose() }
    }
    return renderer
}

private class AndroidSliderThumbRenderer : SliderThumbRenderer {
    private val plain = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gradient = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val innerFill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = SliderThumbValues.INNER_FILL }
    private val innerShadow =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = SliderThumbValues.INNER_STROKE_PX
            color = 0
            setShadowLayer(SliderThumbValues.INNER_BLUR_PX, 0f, SliderThumbValues.INNER_OFFSET_PX, -1)
        }
    private val cover = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = -1 }
    private val bounds = RectF()
    private val bitmapBounds = RectF()
    private val clip = Path()
    private var gradientDiameter = 0f
    private var bitmap: Bitmap? = null

    override fun draw(
        scope: DrawScope,
        frame: SliderThumbFrame,
    ) {
        if (frame.radius <= 0f || !frame.radius.isFinite()) return
        scope.drawIntoCanvas { wrapped ->
            val canvas = wrapped.nativeCanvas
            val diameter = frame.radius * 2f
            canvas.save()
            try {
                canvas.translate(frame.center.x - frame.radius, frame.center.y - frame.radius)
                bounds.set(0f, 0f, diameter, diameter)
                if (!frame.textured) {
                    plain.color = frame.color.toArgb()
                    if (frame.enabled && frame.shadowRadius > 0f) {
                        plain.setShadowLayer(frame.shadowRadius, 0f, frame.shadowOffset, frame.shadowColor.toArgb())
                    }
                    canvas.drawRoundRect(bounds, frame.radius, frame.radius, plain)
                    plain.clearShadowLayer()
                    return@drawIntoCanvas
                }
                val alpha = sliderThumbAlpha(frame.textureAlpha)
                if (alpha > 0) {
                    canvas.saveLayerAlpha(bounds, alpha)
                    try {
                        if (gradientDiameter != diameter || gradient.shader == null) {
                            gradientDiameter = diameter
                            gradient.shader =
                                LinearGradient(
                                    0f,
                                    diameter,
                                    0f,
                                    0f,
                                    intArrayOf(
                                        SliderThumbValues.GRADIENT_OPAQUE,
                                        SliderThumbValues.GRADIENT_OPAQUE,
                                        SliderThumbValues.GRADIENT_TRANSPARENT,
                                    ),
                                    floatArrayOf(0f, 0.2f, 1f),
                                    Shader.TileMode.CLAMP,
                                )
                        }
                        canvas.drawRoundRect(bounds, frame.radius, frame.radius, gradient)
                        val texture = bitmap ?: createBitmap(diameter, frame.radius).also { bitmap = it }
                        val padding = SliderThumbValues.TEXTURE_PADDING_PX
                        bitmapBounds.set(-padding, -padding, diameter + padding, diameter + padding)
                        canvas.drawBitmap(texture, null, bitmapBounds, bitmapPaint)
                    } finally {
                        canvas.restore()
                    }
                }
                cover.alpha = sliderThumbAlpha(frame.coverAlpha)
                if (cover.alpha > 0) canvas.drawRoundRect(bounds, frame.radius, frame.radius, cover)
            } finally {
                canvas.restore()
            }
        }
    }

    private fun createBitmap(
        diameter: Float,
        radius: Float,
    ): Bitmap {
        val size = sliderThumbBitmapSize(diameter)
        val image = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        val padding = SliderThumbValues.TEXTURE_PADDING_PX
        val rect = RectF(padding, padding, diameter + padding, diameter + padding)
        canvas.drawRoundRect(rect, radius, radius, innerFill)
        clip.reset()
        clip.addOval(rect, Path.Direction.CCW)
        canvas.save()
        canvas.clipPath(clip)
        canvas.drawOval(rect, innerShadow)
        canvas.restore()
        return image
    }

    override fun dispose() {
        bitmap?.recycle()
        bitmap = null
        gradient.shader = null
    }
}
