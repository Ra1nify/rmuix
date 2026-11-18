// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import kotlin.math.abs

internal const val SLIDER_FLING_DEFAULT_PERIOD = 1f / 120f

internal fun sliderFlingVelocity(velocity: Float): Float {
    if (!velocity.isFinite() || abs(velocity) < 100f) return 0f
    return velocity.coerceIn(-8000f, 8000f)
}

internal class SliderFlingPhysics(
    startPixels: Float,
    velocityPixels: Float,
    lengthPixels: Float,
    density: Float,
    private val period: Float,
) {
    private val ratio = density * 55f + 0.5f
    private val length = lengthPixels / ratio
    private val accuracy = 0.1f / ratio
    private var position = startPixels / ratio
    private var velocity = velocityPixels / ratio
    private var assistPosition = position
    private var assistVelocity = velocity
    private var target = position.coerceIn(0f, length)
    private var fixSide = position < 0f || position > length
    private var impulse = 0f

    val positionPixels: Float
        get() = position * ratio
    val velocityPixels: Float
        get() = velocity * ratio
    val isSteady: Boolean
        get() = abs(velocity) < accuracy && abs(target - position) < accuracy

    init {
        require(lengthPixels > 0f && lengthPixels.isFinite())
        require(density > 0f && density.isFinite())
        require(period > 0f && period.isFinite())
    }

    fun step() {
        val angularFrequency = 2.8f * 6.2831855f
        val damping = 2f * angularFrequency
        val stiffnessStep = angularFrequency * angularFrequency * period
        val gamma = 1f / (period * (damping + stiffnessStep))
        val beta = stiffnessStep * gamma
        val diagonal = 1f + gamma
        val inverseDeterminant = 1f / (diagonal * diagonal)
        val effectiveMass = diagonal * inverseDeterminant
        val positionBias = (assistPosition - target) * beta
        val dampingFactor = 1f / (15f * period + 1f)

        assistVelocity *= dampingFactor
        assistVelocity += impulse
        repeat(4) {
            val deltaImpulse = -(impulse * gamma + positionBias + assistVelocity) * effectiveMass
            impulse += deltaImpulse
            assistVelocity += deltaImpulse
        }
        assistPosition += assistVelocity * period
        velocity *= dampingFactor
        position += velocity * period

        val overBounds = position < 0f || position > length
        if (fixSide) {
            position = assistPosition
        } else {
            target = position.coerceIn(0f, length)
        }
        fixSide = overBounds
        assistPosition = position
    }
}
