// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Stable
internal class ButtonRubberFeedback(
    private val scope: CoroutineScope,
) {
    val translationX = Animatable(0f)
    val translationY = Animatable(0f)
    private var size = IntSize.Zero
    private var density = 1f
    private var enabled = true
    private var touching = false
    private var origin = Offset.Zero
    private var resumeVelocityX = 0f
    private var resumeVelocityY = 0f
    private var targetX = 0f
    private var targetY = 0f
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
            jobX?.cancel()
            jobY?.cancel()
            targetX = 0f
            targetY = 0f
            scope.launch {
                translationX.snapTo(0f)
                translationY.snapTo(0f)
            }
        }
    }

    fun updateSize(size: IntSize) {
        this.size = size
    }

    fun begin(position: Offset) {
        if (!enabled) return
        val effective = Offset(buttonRubberInverse(translationX.value, density), buttonRubberInverse(translationY.value, density))
        origin = position - effective
        resumeVelocityX = translationX.velocity
        resumeVelocityY = translationY.velocity
        jobX?.cancel()
        jobY?.cancel()
        targetX = translationX.value
        targetY = translationY.value
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
        val nextX = buttonRubberOffset(offset.x, density)
        val nextY = buttonRubberOffset(offset.y, density)
        if (targetX != nextX) {
            val velocity = if (jobX?.isActive == true) translationX.velocity else resumeVelocityX
            resumeVelocityX = 0f
            targetX = nextX
            animateX(nextX, buttonRubberDragSpring(), velocity)
        }
        if (targetY != nextY) {
            val velocity = if (jobY?.isActive == true) translationY.velocity else resumeVelocityY
            resumeVelocityY = 0f
            targetY = nextY
            animateY(nextY, buttonRubberDragSpring(), velocity)
        }
    }

    fun end(velocity: Velocity) {
        if (!enabled || !touching) return
        touching = false
        val parametersX = buttonRubberHandUp(size.width.toFloat(), size.height.toFloat(), density, velocity.x, translationX.value)
        val parametersY = buttonRubberHandUp(size.width.toFloat(), size.height.toFloat(), density, velocity.y, translationY.value)
        targetX = 0f
        targetY = 0f
        animateX(0f, buttonRubberReturnSpring(parametersX), parametersX.velocity)
        animateY(0f, buttonRubberReturnSpring(parametersY), parametersY.velocity)
    }

    private fun animateX(
        target: Float,
        spring: SpringSpec<Float>,
        velocity: Float,
    ) {
        jobX =
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                translationX.animateTo(target, spring, initialVelocity = velocity)
            }
    }

    private fun animateY(
        target: Float,
        spring: SpringSpec<Float>,
        velocity: Float,
    ) {
        jobY =
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                translationY.animateTo(target, spring, initialVelocity = velocity)
            }
    }

    fun dispose() {
        touching = false
        jobX?.cancel()
        jobY?.cancel()
    }
}
