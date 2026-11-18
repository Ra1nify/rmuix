// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Stable
internal class SliderOwnerValue(
    initialValue: Float,
) {
    var observedValue by mutableFloatStateOf(initialValue)
        private set
    var expectedValue by mutableStateOf<Float?>(null)
        private set

    fun expect(value: Float) {
        expectedValue = value
    }

    fun reconcile(value: Float): Boolean {
        val expected = expectedValue
        val reset = if (expected == null) value != observedValue else value != expected
        observedValue = value
        expectedValue = null
        return reset
    }
}

@Stable
internal class SliderMotion(
    initialValue: Float,
    private val mapping: SliderProgressMapping,
    private val reverse: Boolean,
    private val density: Float,
    private val framePeriod: Float,
    private val physicsEnabled: Boolean,
    private val scope: CoroutineScope,
    private val onValueChange: (Float) -> Unit,
    private val onValueChangeFinished: () -> Unit,
) {
    private val owner = SliderOwnerValue(initialValue)
    private val progress = Animatable(initialValue)
    private val dragTransition = SliderDragTransition()
    private val rangeSpan = mapping.valueRange.endInclusive - mapping.valueRange.start
    private var animationJob: Job? = null
    private var generation = 0
    private var clicking by mutableStateOf(false)
    private var flingValue by mutableStateOf<Float?>(null)
    private var enabled = true
    private var lastReportedValue by mutableFloatStateOf(initialValue)

    val value: Float
        get() = flingValue ?: progress.value
    val expectedValue: Float?
        get() = owner.expectedValue
    val reportedValue: Float
        get() = lastReportedValue
    val accessibilityValue: Float
        get() = if (clicking) mapping.valueAtProgress(progress.value, round = false) else lastReportedValue
    var isPressed by mutableStateOf(false)
        private set
    var isDragging by mutableStateOf(false)
        private set

    fun synchronize(
        value: Float,
        enabled: Boolean,
        expectedAtComposition: Float?,
    ) {
        if (owner.expectedValue != expectedAtComposition) return
        val externalChange = owner.reconcile(value)
        val disabled = this.enabled && !enabled
        this.enabled = enabled
        if (externalChange || disabled) {
            interrupt(notifyClick = false)
            if (disabled) {
                isPressed = false
                isDragging = false
            }
            lastReportedValue = value
            snapTo(value)
        }
    }

    fun press() {
        if (!enabled) return
        if (flingValue != null) {
            val current = value
            interrupt(notifyClick = false, notifyFling = true)
            snapTo(current)
        }
        isPressed = true
    }

    fun releasePress() {
        isPressed = false
    }

    fun beginDrag() {
        if (!enabled) return
        interrupt(notifyClick = true)
        isDragging = true
        dragTransition.begin()
        lastReportedValue = owner.observedValue
    }

    fun dragTo(
        value: Float,
        trackLength: Float,
        rawFraction: Float,
    ) {
        if (!enabled || !isDragging || trackLength <= 0f) return
        val target = mapping.transitionTarget(value, rawFraction, dragTransition.active)
        if (dragTransition.route(progress.value, value, mapping)) {
            val ticket = generation
            animationJob =
                scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    progress.animateTo(
                        target,
                        if (physicsEnabled) {
                            sliderProgressSpring(dragging = true, visibilityThreshold = sliderClickThreshold(rangeSpan, trackLength))
                        } else {
                            // Section style (`COUISectionSeekBar`): 100ms path snap, not a spring.
                            sliderSectionSnapSpec(dragging = true)
                        },
                    ) {
                        if (ticket == generation && isDragging && enabled) {
                            reportChangedValue(mapping.valueAtProgress(this.value, round = false))
                        }
                    }
                }
        } else {
            reportChangedValue(mapping.valueAtProgress(value, round = true))
            animationJob =
                scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    progress.animateTo(
                        value,
                        if (physicsEnabled) {
                            sliderProgressSpring(dragging = true, visibilityThreshold = sliderFollowThreshold(rangeSpan))
                        } else {
                            sliderSectionSnapSpec(dragging = true)
                        },
                    )
                }
        }
    }

    fun endDrag(
        value: Float,
        trackLength: Float,
        rawVelocity: Float,
    ) {
        if (!enabled || !isDragging) return
        val transitionWasActive = dragTransition.active
        val releaseVelocity = progress.velocity
        val renderedValue = this.value
        val flingVelocity = if (physicsEnabled) sliderFlingVelocity(rawVelocity) else 0f
        interrupt(notifyClick = false)
        isDragging = false
        isPressed = false
        reportChangedValue(mapping.valueAtProgress(value, round = true))
        if (flingVelocity != 0f && trackLength > 0f) {
            startFling(
                mapping.valueAtProgress(value, round = true),
                if (transitionWasActive) value else renderedValue,
                trackLength,
                flingVelocity,
            )
            return
        }
        if (transitionWasActive && physicsEnabled) {
            snapTo(value)
        } else {
            animationJob =
                scope.launch(start = CoroutineStart.UNDISPATCHED) {
                    progress.animateTo(
                        value,
                        if (physicsEnabled) {
                            sliderProgressSpring(dragging = true, visibilityThreshold = sliderFollowThreshold(rangeSpan))
                        } else {
                            // `SectionAnimationHandler.RELEASE_SNAP_DURATION` = 150ms.
                            sliderSectionSnapSpec(dragging = false)
                        },
                        initialVelocity = if (physicsEnabled) releaseVelocity else 0f,
                    )
                }
        }
        onValueChangeFinished()
    }

    private fun startFling(
        value: Float,
        initialDrawValue: Float,
        trackLength: Float,
        velocity: Float,
    ) {
        val physics =
            SliderFlingPhysics(
                startPixels = mapping.flingStartPixels(value, trackLength, reverse),
                velocityPixels = velocity,
                lengthPixels = trackLength,
                density = density,
                period = framePeriod,
            )
        val ticket = generation
        flingValue = initialDrawValue
        animationJob =
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                var drawn = initialDrawValue
                do {
                    withFrameNanos {
                        if (ticket == generation && enabled) {
                            physics.step()
                            drawn = mapping.flingValueAtPixels(physics.positionPixels, trackLength, reverse)
                            flingValue = drawn
                            reportChangedValue(mapping.valueAtProgress(drawn, round = true))
                        }
                    }
                } while (!physics.isSteady && ticket == generation && enabled)
                if (ticket == generation && enabled) {
                    progress.snapTo(drawn)
                    flingValue = null
                    onValueChangeFinished()
                }
            }
    }

    fun click(
        value: Float,
        trackLength: Float,
    ) {
        if (!enabled || trackLength <= 0f) return
        interrupt(notifyClick = true)
        isDragging = false
        clicking = true
        val targetValue = mapping.valueAtProgress(value, round = true)
        val ticket = generation
        animationJob =
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                progress.animateTo(
                    targetValue,
                    sliderProgressSpring(dragging = false, visibilityThreshold = sliderClickThreshold(rangeSpan, trackLength)),
                )
                if (ticket == generation && enabled) {
                    clicking = false
                    owner.expect(targetValue)
                    lastReportedValue = targetValue
                    onValueChange(targetValue)
                    onValueChangeFinished()
                }
            }
    }

    fun setProgress(value: Float) {
        if (!enabled) return
        interrupt(notifyClick = false)
        isDragging = false
        isPressed = false
        val targetValue = mapping.valueAtProgress(value, round = true)
        snapTo(targetValue)
        reportChangedValue(targetValue)
        onValueChangeFinished()
    }

    fun cancelGesture() {
        isPressed = false
        if (isDragging) {
            interrupt(notifyClick = false)
            isDragging = false
            snapTo(owner.observedValue)
        }
    }

    fun dispose() {
        interrupt(notifyClick = false)
    }

    private fun reportChangedValue(value: Float) {
        if (value == lastReportedValue) return
        lastReportedValue = value
        owner.expect(value)
        onValueChange(value)
    }

    private fun snapTo(value: Float) {
        animationJob =
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                progress.snapTo(value)
            }
    }

    private fun interrupt(
        notifyClick: Boolean,
        notifyFling: Boolean = false,
    ) {
        generation++
        dragTransition.stop()
        animationJob?.cancel()
        animationJob = null
        val wasClicking = clicking
        val wasFlinging = flingValue != null
        clicking = false
        flingValue = null
        if (wasClicking && notifyClick) onValueChangeFinished()
        if (wasFlinging && notifyFling) onValueChangeFinished()
    }
}

@Composable
internal fun rememberSliderMotion(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    reverse: Boolean,
    trackHeight: Dp,
    progressRange: kotlin.ranges.IntRange?,
    physicsEnabled: Boolean,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)?,
): SliderMotion {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val framePeriod = rememberSliderFlingFramePeriod()
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnValueChangeFinished by rememberUpdatedState(onValueChangeFinished)
    val motion =
        remember(scope, valueRange, reverse, trackHeight, density, progressRange, physicsEnabled, framePeriod) {
            SliderMotion(
                initialValue = value,
                mapping = SliderProgressMapping(valueRange, progressRange),
                reverse = reverse,
                density = density.density,
                framePeriod = framePeriod,
                physicsEnabled = physicsEnabled,
                scope = scope,
                onValueChange = { currentOnValueChange(it) },
                onValueChangeFinished = { currentOnValueChangeFinished?.invoke() },
            )
        }
    val expected = motion.expectedValue
    SideEffect { motion.synchronize(value, enabled, expected) }
    DisposableEffect(motion) {
        onDispose { motion.dispose() }
    }
    return motion
}
