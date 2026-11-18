// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.shader.isRuntimeShaderSupported
import io.github.ra1nify.rmuix.kmp.squircle.addSquircleRect
import io.github.ra1nify.rmuix.kmp.utils.PressFeedbackType
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectColor
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectSpring
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
internal fun Modifier.stockButtonSurface(
    interactionSource: InteractionSource,
    enabled: Boolean,
    pressFeedbackType: PressFeedbackType,
    cornerRadius: Dp,
    containerColor: Color,
    colors: ButtonColors,
    dark: Boolean,
): Modifier {
    val scope = rememberCoroutineScope()
    val touch = remember(scope) { ButtonTouchFeedback(scope) }
    val rubber = remember(scope) { ButtonRubberFeedback(scope) }
    val motionConfig = LocalButtonTouchMotionConfig.current
    val deform = remember(scope, motionConfig) { ButtonDeformFeedback(scope, motionConfig) }
    val physical = enabled && pressFeedbackType == PressFeedbackType.Sink
    val density = LocalDensity.current
    val squircle = isRuntimeShaderSupported()
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val spring = remember { stockStateEffectSpring() }
    val hover = animateFloatAsState(if (enabled && hovered) 1f else 0f, spring, label = "ButtonHover")
    val focus = animateFloatAsState(if (enabled && focused) 1f else 0f, spring, label = "ButtonFocus")
    val hoverColor = if (colors.hoverColor == Color.Unspecified) buttonHoverColor(dark) else colors.hoverColor
    val pressColor = if (colors.pressColor == Color.Unspecified) buttonPressColor(dark) else colors.pressColor
    val focusColor = if (colors.focusOutlineColor == Color.Unspecified) buttonFocusOutlineColor(dark) else colors.focusOutlineColor
    SideEffect {
        touch.configure(enabled, pressFeedbackType == PressFeedbackType.Sink, density.density)
        rubber.configure(physical, density.density)
        deform.configure(physical, density.density)
    }
    DisposableEffect(touch, rubber) {
        onDispose {
            touch.dispose()
            rubber.dispose()
        }
    }
    DisposableEffect(deform) { onDispose { deform.dispose() } }
    return this
        .graphicsLayer {
            val uniform = if (physical) touch.scale.value else 1f
            val horizontal = if (physical) buttonCombinedScale(uniform, deform.scaleX.value) else 1f
            val vertical = if (physical) buttonCombinedScale(uniform, deform.scaleY.value) else 1f
            val compensation =
                if (physical) {
                    buttonAnchorTranslation(
                        deform.anchor,
                        size.width,
                        size.height,
                        horizontal,
                        vertical,
                    )
                } else {
                    Offset.Zero
                }
            scaleX = horizontal
            scaleY = vertical
            translationX = if (physical) rubber.translationX.value + compensation.x else 0f
            translationY = if (physical) rubber.translationY.value + compensation.y else 0f
        }.onSizeChanged {
            touch.updateSize(it)
            rubber.updateSize(it)
            deform.updateSize(it)
        }.pointerInput(touch, rubber, deform, enabled, physical) {
            if (!enabled) return@pointerInput
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                if (down.isConsumed) return@awaitEachGesture
                val trackers = mutableMapOf(down.id to VelocityTracker().apply { addPointerInputChange(down) })
                var activePointer = down.id
                var lastPosition = down.position
                var releaseVelocity = Velocity.Zero
                var released = false
                rubber.begin(down.position)
                deform.begin(down.position)
                touch.setPressed(true)
                try {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        if (physical) {
                            for (change in event.changes) {
                                trackers.getOrPut(change.id) { VelocityTracker() }.addPointerInputChange(change)
                            }
                        }
                        val active = event.changes.firstOrNull { it.id == activePointer }
                        val added = event.changes.lastOrNull { it.pressed && !it.previousPressed }
                        var switched = false
                        if (added != null && added.id != activePointer) {
                            rubber.switchPointer(active?.position ?: lastPosition, added.position)
                            deform.switchPointer(active?.position ?: lastPosition, added.position)
                            activePointer = added.id
                            lastPosition = added.position
                            switched = true
                        } else if (active == null || !active.pressed) {
                            val next = event.changes.firstOrNull { it.pressed && it.id != activePointer }
                            if (next != null) {
                                rubber.switchPointer(active?.position ?: lastPosition, next.position)
                                deform.switchPointer(active?.position ?: lastPosition, next.position)
                                activePointer = next.id
                                lastPosition = next.position
                                switched = true
                            } else {
                                released = active != null && !active.pressed
                                if (physical && released) releaseVelocity = trackers[activePointer]?.calculateVelocity() ?: Velocity.Zero
                                break
                            }
                        }
                        val current = event.changes.firstOrNull { it.id == activePointer }
                        if (current != null) {
                            lastPosition = current.position
                            if (!switched && event.type == PointerEventType.Move) {
                                rubber.move(current.position)
                                deform.move(current.position)
                            }
                        }
                        val final = awaitPointerEvent(PointerEventPass.Final)
                        if (final.changes.any {
                                it.pressed && it.previousPressed && it.position != it.previousPosition && it.isConsumed
                            }
                        ) {
                            break
                        }
                    }
                } finally {
                    rubber.end(if (released) releaseVelocity else Velocity.Zero)
                    deform.end(if (released) releaseVelocity else Velocity.Zero)
                    touch.setPressed(false)
                }
            }
        }.drawWithCache {
            val radius = buttonResolvedRadius(size.height, cornerRadius.toPx())
            val path = Path().apply { addSquircleRect(size.width, size.height, radius, squircleEnabled = squircle) }
            val stroke = Stroke(width = 3.dp.toPx().toInt() * 2f)
            onDrawWithContent {
                drawPath(path, containerColor)
                if (enabled) {
                    clipPath(path) {
                        drawPath(path, stockStateEffectColor(hoverColor, hover.value))
                        drawPath(path, stockStateEffectColor(pressColor, touch.mask.value))
                    }
                    clipPath(path, ClipOp.Difference) {
                        drawPath(path, stockStateEffectColor(focusColor, focus.value), style = stroke)
                    }
                }
                clipPath(path) { this@onDrawWithContent.drawContent() }
            }
        }
}

@Stable
private class ButtonTouchFeedback(
    private val scope: CoroutineScope,
) {
    private var size = IntSize.Zero
    val scale = Animatable(1f)
    val mask = Animatable(0f)
    private var enabled = true
    private var scaleEnabled = true
    private var density = 1f
    private var pressed = false
    private var pendingMaskExit = false
    private var scaleJob: Job? = null
    private var maskJob: Job? = null

    fun updateSize(size: IntSize) {
        this.size = size
    }

    fun configure(
        enabled: Boolean,
        scaleEnabled: Boolean,
        density: Float,
    ) {
        val resetMask = !enabled && this.enabled
        val resetScale = (!enabled && this.enabled) || (!scaleEnabled && this.scaleEnabled)
        this.enabled = enabled
        this.scaleEnabled = scaleEnabled
        this.density = density
        if (resetMask) {
            pressed = false
            pendingMaskExit = false
            maskJob?.cancel()
            scope.launch { mask.snapTo(0f) }
        }
        if (resetScale) {
            scaleJob?.cancel()
            scope.launch { scale.snapTo(1f) }
        }
    }

    fun setPressed(pressed: Boolean) {
        if (!enabled || this.pressed == pressed) return
        this.pressed = pressed
        if (pressed) {
            pendingMaskExit = false
            animateMask(1f)
        } else if (mask.value > 0.7f || maskJob?.isActive != true) {
            pendingMaskExit = false
            animateMask(0f)
        } else {
            pendingMaskExit = true
        }
        if (scaleEnabled) {
            val width = size.width / density
            val height = size.height / density
            val velocity = if (pressed) 0f else scale.velocity
            scaleJob?.cancel()
            scaleJob =
                scope.launch {
                    scale.animateTo(
                        if (pressed) buttonPressScale(width, height) else 1f,
                        buttonScaleSpring(pressed, width, height),
                        initialVelocity = velocity,
                    )
                }
        }
    }

    private fun animateMask(target: Float) {
        val velocity = mask.velocity
        maskJob?.cancel()
        maskJob =
            scope.launch {
                mask.animateTo(target, stockStateEffectSpring(), initialVelocity = velocity) {
                    if (pendingMaskExit && value > 0.7f) {
                        pendingMaskExit = false
                        animateMask(0f)
                    }
                }
            }
    }

    fun dispose() {
        scaleJob?.cancel()
        maskJob?.cancel()
    }
}
