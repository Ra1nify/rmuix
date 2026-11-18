// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.roundToInt

internal object SliderThumbValues {
    const val SHADOW_RADIUS_DP = 4f
    const val SHADOW_OFFSET_DP = 2f
    const val TEXTURE_PADDING_PX = 12f
    const val INNER_BLUR_PX = 5f
    const val INNER_OFFSET_PX = 0.4f
    const val INNER_STROKE_PX = 10f
    const val GRADIENT_OPAQUE = 0xE6FFFFFF.toInt()
    const val GRADIENT_TRANSPARENT = 0x8CFFFFFF.toInt()
    const val INNER_FILL = 0x77FFFFFF
}

internal fun sliderThumbAlpha(value: Float): Int = (value * 255f).toInt().coerceIn(0, 255)

internal fun sliderThumbShadowRadius(density: Float): Float =
    (SliderThumbValues.SHADOW_RADIUS_DP * density).roundToInt().coerceAtLeast(1).toFloat()

internal fun sliderThumbBitmapSize(diameter: Float): Int = ceil(diameter + SliderThumbValues.TEXTURE_PADDING_PX * 2f).toInt()

internal fun sliderThumbPressChanged(
    previousPressed: Boolean,
    pressed: Boolean,
    materialEnabled: Boolean,
    controlEnabled: Boolean = true,
): Boolean = materialEnabled && controlEnabled && previousPressed != pressed

internal fun sliderThumbMaterialSpring(response: Float): SpringSpec<Float> {
    val frequency = 2.0 * PI / response
    return spring(dampingRatio = 1f, stiffness = (frequency * frequency).toFloat(), visibilityThreshold = 0.0075f)
}

@Immutable
internal data class SliderThumbFrame(
    val center: Offset,
    val radius: Float,
    val enabled: Boolean,
    val color: Color,
    val shadowColor: Color,
    val shadowRadius: Float,
    val shadowOffset: Float,
    val textureEnabled: Boolean,
    val textureAlpha: Float,
    val coverAlpha: Float,
) {
    val textured: Boolean
        get() = textureEnabled && textureAlpha > 0f
}

internal interface SliderThumbRenderer {
    fun draw(
        scope: DrawScope,
        frame: SliderThumbFrame,
    )

    fun dispose()
}

@Composable
internal expect fun rememberSliderThumbRenderer(): SliderThumbRenderer

@Stable
internal class SliderThumbAnimation(
    private val scope: CoroutineScope,
) {
    val texture = Animatable(0f)
    val cover = Animatable(0f)
    var enabled by mutableStateOf(false)
        private set
    private var pressed = false
    private var textureJob: Job? = null
    private var coverJob: Job? = null
    private var retainedTextureVelocity: Float? = null
    private var retainedCoverVelocity: Float? = null

    fun configure(
        enabled: Boolean,
        pressed: Boolean,
        controlEnabled: Boolean,
    ) {
        val pressChanged = sliderThumbPressChanged(this.pressed, pressed, enabled, controlEnabled)
        this.pressed = pressed
        if (this.enabled != enabled) {
            this.enabled = enabled
            if (!enabled) {
                retainedTextureVelocity = texture.velocity
                retainedCoverVelocity = cover.velocity
                textureJob?.cancel()
                coverJob?.cancel()
                textureJob = scope.launch(start = CoroutineStart.UNDISPATCHED) { texture.snapTo(0f) }
                coverJob = scope.launch(start = CoroutineStart.UNDISPATCHED) { cover.snapTo(0f) }
            }
        }
        if (!pressChanged) return
        val textureVelocity = retainedTextureVelocity ?: texture.velocity
        val coverVelocity = retainedCoverVelocity ?: cover.velocity
        retainedTextureVelocity = null
        retainedCoverVelocity = null
        textureJob?.cancel()
        coverJob?.cancel()
        textureJob =
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                val start = texture.value.coerceIn(0f, 1f)
                texture.snapTo(start)
                texture.animateTo(if (pressed) 1f else 0f, sliderThumbMaterialSpring(0.3f), initialVelocity = textureVelocity)
            }
        coverJob =
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                cover.snapTo(if (pressed) 1f else cover.value.coerceIn(0f, 1f))
                cover.animateTo(
                    if (pressed) 0f else 1f,
                    sliderThumbMaterialSpring(if (pressed) 0.5f else 0.3f),
                    initialVelocity = coverVelocity,
                )
            }
    }

    fun dispose() {
        textureJob?.cancel()
        coverJob?.cancel()
    }
}

@Composable
internal fun rememberSliderThumbAnimation(
    enabled: Boolean,
    pressed: Boolean,
    controlEnabled: Boolean,
): SliderThumbAnimation {
    val scope = rememberCoroutineScope()
    val animation = remember(scope) { SliderThumbAnimation(scope) }
    SideEffect { animation.configure(enabled, pressed, controlEnabled) }
    DisposableEffect(animation) {
        onDispose { animation.dispose() }
    }
    return animation
}
