// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.shader

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal expect fun DrawScope.spotlightCanvasSupported(): Boolean

@Stable
internal class StockSpotlightEffect(
    private val scope: CoroutineScope,
) {
    private val shader = if (isRuntimeShaderSupported()) runCatching { RuntimeShader(STOCK_SPOTLIGHT_SHADER) }.getOrNull() else null
    private val progress = Animatable(0f)
    private val positionX = Animatable(0f)
    private val positionY = Animatable(0f)
    private var parameters by mutableStateOf(spotlightParameters(SpotlightStyle.OpaqueMedium, false))
    private var color by mutableStateOf(Color.White)
    private var extinguishing by mutableStateOf(false)
    private var target = Offset.Zero
    private var progressJob: Job? = null
    private var positionXJob: Job? = null
    private var positionYJob: Job? = null
    private var epoch = 0

    val supported: Boolean
        get() = shader != null

    fun configure(
        parameters: SpotlightParameters,
        color: Color,
    ) {
        this.parameters = parameters
        this.color = color
    }

    fun onInput(
        input: SpotlightInput,
        size: IntSize,
    ) {
        if (!supported) return
        if (input.position != null && size.width > 0 && size.height > 0) {
            move(Offset(input.position.x / size.width, input.position.y / size.height))
        }
        when (input.action) {
            SpotlightAction.Down -> animateProgress(true)
            SpotlightAction.Up, SpotlightAction.Cancel -> animateProgress(false)
            SpotlightAction.Move, SpotlightAction.None -> Unit
        }
    }

    fun release() {
        if (supported) animateProgress(false)
    }

    fun draw(scope: DrawScope) {
        val shader = shader ?: return
        if (progress.value == 0f || !scope.spotlightCanvasSupported()) return
        val parameters = parameters
        val frame = spotlightFrame(progress.value, extinguishing, Offset(positionX.value, positionY.value), scope.size, parameters)
        shader.setFloatUniform("resolution", scope.size.width, scope.size.height)
        shader.setFloatUniform("u_lightPosAndHeight", frame.center.x, frame.center.y, frame.radius, frame.intensity * color.alpha)
        shader.setFloatUniform("u_lightColorAndAmbient", color.red, color.green, color.blue, parameters.ambient)
        shader.setFloatUniform("u_lightingParams", parameters.diffuse, parameters.lightHeight, parameters.noise, parameters.dither)
        with(scope) { drawRect(shader.asBrush(), blendMode = parameters.blendMode) }
    }

    private fun move(position: Offset) {
        target = position
        if (progress.value == 0f) {
            positionXJob = scope.launch(start = CoroutineStart.UNDISPATCHED) { positionX.snapTo(position.x) }
            positionYJob = scope.launch(start = CoroutineStart.UNDISPATCHED) { positionY.snapTo(position.y) }
        } else {
            positionXJob = scope.launch(start = CoroutineStart.UNDISPATCHED) { positionX.animateTo(position.x, spotlightPositionSpring()) }
            positionYJob = scope.launch(start = CoroutineStart.UNDISPATCHED) { positionY.animateTo(position.y, spotlightPositionSpring()) }
        }
    }

    private fun animateProgress(glow: Boolean) {
        epoch++
        val ticket = epoch
        extinguishing = !glow
        progressJob =
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                progress.animateTo(if (glow) 1f else 0f, spotlightProgressSpring(glow))
                if (!glow && ticket == epoch) {
                    extinguishing = false
                    positionXJob?.cancel()
                    positionYJob?.cancel()
                    positionX.snapTo(target.x)
                    positionY.snapTo(target.y)
                }
            }
    }

    fun dispose() {
        epoch++
        progressJob?.cancel()
        positionXJob?.cancel()
        positionYJob?.cancel()
    }
}
