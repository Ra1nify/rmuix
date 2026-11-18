// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.TimeSource

@Composable
internal fun rememberSoftwareControlPressRipple(
    interactionSource: InteractionSource,
    color: Color,
    radiusPixels: Int,
): Modifier {
    val ripples = remember { mutableStateListOf<ControlSoftwareRipple>() }
    val activeRipple = remember { mutableStateOf<ControlSoftwareRipple?>(null) }
    LaunchedEffect(interactionSource) {
        val presses = mutableSetOf<PressInteraction.Press>()
        try {
            coroutineScope {
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> {
                            if (presses.isEmpty() && ripples.size < 10) {
                                val ripple = ControlSoftwareRipple(interaction.pressPosition)
                                ripples.add(ripple)
                                activeRipple.value = ripple
                                ripple.enter(this)
                            }
                            presses.add(interaction)
                        }

                        is PressInteraction.Release -> {
                            presses.remove(interaction.press)
                            if (presses.isEmpty()) {
                                activeRipple.value?.let { ripple -> ripple.exit(this) { ripples.remove(ripple) } }
                                activeRipple.value = null
                            }
                        }

                        is PressInteraction.Cancel -> {
                            presses.remove(interaction.press)
                            if (presses.isEmpty()) {
                                activeRipple.value?.let { ripple -> ripple.exit(this) { ripples.remove(ripple) } }
                                activeRipple.value = null
                            }
                        }

                        else -> {
                            // no-op
                        }
                    }
                }
            }
        } finally {
            activeRipple.value = null
            ripples.clear()
        }
    }
    return Modifier
        .pointerInput(interactionSource) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                activeRipple.value?.pressPosition = down.position
                while (true) {
                    val change = awaitPointerEvent(PointerEventPass.Initial).changes.firstOrNull { it.id == down.id } ?: break
                    activeRipple.value?.pressPosition = change.position
                    if (!change.pressed) break
                }
            }
        }.drawBehind {
            val targetRadius = radiusPixels.toFloat()
            for (ripple in ripples) {
                val frame =
                    checkboxSoftwareRippleFrame(
                        ripple.elapsedMillis.value,
                        ripple.releasedAtMillis,
                        ripple.pressPosition,
                        size,
                        targetRadius,
                    )
                drawCircle(checkboxSoftwareRippleColor(color, frame.opacity), frame.radius, frame.center)
            }
        }
}

@Stable
private class ControlSoftwareRipple(
    position: Offset,
) {
    private val enteredAt = TimeSource.Monotonic.markNow()
    private var animationJob: Job? = null
    val elapsedMillis = Animatable(0f)
    var pressPosition by mutableStateOf(position)
    var releasedAtMillis by mutableStateOf<Float?>(null)
        private set

    fun enter(scope: CoroutineScope) {
        animationJob =
            scope.launch {
                elapsedMillis.animateTo(225f, tween(225, easing = LinearEasing))
            }
    }

    fun exit(
        scope: CoroutineScope,
        onFinished: () -> Unit,
    ) {
        if (releasedAtMillis != null) return
        val releasedAt = enteredAt.elapsedNow().inWholeMilliseconds.toFloat()
        releasedAtMillis = releasedAt
        val finishedAt = checkboxSoftwareRippleExitStart(releasedAt) + 150f
        animationJob?.cancel()
        animationJob =
            scope.launch {
                elapsedMillis.snapTo(releasedAt)
                elapsedMillis.animateTo(finishedAt, tween((finishedAt - releasedAt).roundToInt(), easing = LinearEasing))
                onFinished()
            }
    }
}
