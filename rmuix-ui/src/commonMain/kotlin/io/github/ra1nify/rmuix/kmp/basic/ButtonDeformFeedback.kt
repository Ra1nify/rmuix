// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Stable
internal class ButtonDeformFeedback(
    private val scope: CoroutineScope,
    private val configuration: ButtonTouchMotionConfig,
) {
    val scaleX = Animatable(1f)
    val scaleY = Animatable(1f)
    var anchor by mutableStateOf(configuration.deformAnchor)
        private set
    private var autoAnchor: ButtonDeformAnchor? = null
    private var enabled = true
    private var touching = false
    private var size = IntSize.Zero
    private var density = 1f
    private var origin = Offset.Zero
    private var targetX = 1f
    private var targetY = 1f
    private var resumeVelocityX = 0f
    private var resumeVelocityY = 0f
    private var jobX: Job? = null
    private var jobY: Job? = null

    fun configure(
        enabled: Boolean,
        density: Float,
    ) {
        val reset = this.enabled && !enabled
        this.enabled = enabled
        this.density = density
        if (reset) {
            touching = false
            autoAnchor = null
            anchor = configuration.deformAnchor
            targetX = 1f
            targetY = 1f
            jobX?.cancel()
            jobY?.cancel()
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                scaleX.snapTo(1f)
                scaleY.snapTo(1f)
            }
        }
    }

    fun updateSize(size: IntSize) {
        this.size = size
    }

    fun begin(position: Offset) {
        if (!enabled) return
        val effective =
            buttonDeformEffectiveOffset(ButtonDeformScale(scaleX.value, scaleY.value), size.width.toFloat(), size.height.toFloat(), density)
        origin = position - effective
        resumeVelocityX = scaleX.velocity
        resumeVelocityY = scaleY.velocity
        jobX?.cancel()
        jobY?.cancel()
        targetX = scaleX.value
        targetY = scaleY.value
        touching = true
    }

    fun switchPointer(
        previousPosition: Offset,
        nextPosition: Offset,
    ) {
        if (!enabled || !touching) return
        origin = buttonRubberRebaseOrigin(origin, previousPosition, nextPosition)
    }

    fun move(position: Offset) {
        if (!enabled || !touching) return
        val offset = position - origin
        if (!buttonDragPassesDeadZone(offset)) return
        if (configuration.autoDeformAnchor) {
            buttonResolveAutoAnchor(offset, autoAnchor)?.let {
                autoAnchor = it
                anchor = it
            }
        }
        val next = buttonDeformScale(offset, size.width.toFloat(), size.height.toFloat(), density)
        if (targetX != next.horizontal) {
            val velocity = if (jobX?.isActive == true) scaleX.velocity else resumeVelocityX
            resumeVelocityX = 0f
            targetX = next.horizontal
            animateX(targetX, buttonDeformSpring(0.15f, 0.15f), velocity)
        }
        if (targetY != next.vertical) {
            val velocity = if (jobY?.isActive == true) scaleY.velocity else resumeVelocityY
            resumeVelocityY = 0f
            targetY = next.vertical
            animateY(targetY, buttonDeformSpring(0.15f, 0.15f), velocity)
        }
    }

    fun end(velocity: Velocity) {
        if (!enabled || !touching) return
        touching = false
        targetX = 1f
        targetY = 1f
        if (buttonDeformAtRest(scaleX.value)) {
            jobX?.cancel()
            jobX = scope.launch(start = CoroutineStart.UNDISPATCHED) { scaleX.snapTo(1f) }
        } else {
            val parameters = buttonDeformHandUp(size.width.toFloat(), size.height.toFloat(), density, velocity.x, scaleX.value)
            animateX(1f, buttonDeformSpring(parameters.response, parameters.bounce), parameters.velocity)
        }
        if (buttonDeformAtRest(scaleY.value)) {
            jobY?.cancel()
            jobY = scope.launch(start = CoroutineStart.UNDISPATCHED) { scaleY.snapTo(1f) }
        } else {
            val parameters = buttonDeformHandUp(size.width.toFloat(), size.height.toFloat(), density, velocity.y, scaleY.value)
            animateY(1f, buttonDeformSpring(parameters.response, parameters.bounce), parameters.velocity)
        }
    }

    private fun animateX(
        target: Float,
        spring: SpringSpec<Float>,
        velocity: Float,
    ) {
        jobX = scope.launch(start = CoroutineStart.UNDISPATCHED) { scaleX.animateTo(target, spring, initialVelocity = velocity) }
    }

    private fun animateY(
        target: Float,
        spring: SpringSpec<Float>,
        velocity: Float,
    ) {
        jobY = scope.launch(start = CoroutineStart.UNDISPATCHED) { scaleY.animateTo(target, spring, initialVelocity = velocity) }
    }

    fun dispose() {
        touching = false
        jobX?.cancel()
        jobY?.cancel()
    }
}
