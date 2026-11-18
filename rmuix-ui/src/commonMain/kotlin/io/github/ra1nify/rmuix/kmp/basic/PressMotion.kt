// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.launch

/**
 * ColorOS 17 dynamic press motion: a pressable area that scales down on touch-down and springs back
 * on release, optionally firing a click (and a haptic).
 *
 * Reconstructed from `com.coui.appcompat.animation.touchanimation.PressEngineConfig`:
 * - press down: quick compression (`0.82` damping / `1600` stiffness), default target `0.97f`;
 * - release or cancel: bouncy elastic return (`0.65` damping / `380` stiffness).
 *
 * Unlike [androidx.compose.foundation.clickable] this draws **no** indication, so it is intended for
 * cards, colour swatches and other custom surfaces that already have their own visual feedback.
 *
 * ```kotlin
 * Box(
 *     Modifier
 *         .size(48.dp)
 *         .pressable(pressedScale = 0.92f, onClick = { select(index) })
 * )
 * ```
 *
 * @param enabled Whether press feedback and the click are active.
 * @param pressedScale Scale applied while pressed. Use `1f` to disable the visual feedback.
 * @param pressDampingRatio Damping ratio of the press-down animation.
 * @param pressStiffness Stiffness of the press-down animation.
 * @param releaseDampingRatio Damping ratio of the release animation (lower = bouncier).
 * @param releaseStiffness Stiffness of the release animation.
 * @param haptic Haptic fired on click. `null` (default) means no haptic.
 * @param onClick Click handler. When `null` the modifier only provides press feedback.
 *
 * The implementation is a `Modifier.composed` factory so the press `Animatable` stays scoped to the
 * composed element; the compose rule flags the factory shape, not a leak here.
 */
@Suppress("ktlint:compose:modifier-composed-check")
fun Modifier.pressable(
    enabled: Boolean = true,
    pressedScale: Float = 0.97f,
    pressDampingRatio: Float = 0.82f,
    pressStiffness: Float = 1600f,
    releaseDampingRatio: Float = 0.65f,
    releaseStiffness: Float = 380f,
    haptic: HapticFeedbackType? = null,
    onClick: (() -> Unit)? = null,
): Modifier =
    composed {
        if (!enabled) return@composed this

        val scale = remember { Animatable(1f) }
        val scope = rememberCoroutineScope()
        val hapticFeedback = LocalHapticFeedback.current
        val currentOnClick by rememberUpdatedState(onClick)
        val currentHaptic by rememberUpdatedState(haptic)

        val clickModifier =
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        currentHaptic?.let { hapticFeedback.performHapticFeedback(it) }
                        currentOnClick?.invoke()
                    },
                )
            } else {
                Modifier
            }

        this
            .scale(scale.value)
            .pointerInput(enabled, pressedScale) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    scope.launch {
                        scale.animateTo(
                            targetValue = pressedScale,
                            animationSpec =
                                spring(
                                    dampingRatio = pressDampingRatio,
                                    stiffness = pressStiffness,
                                ),
                        )
                    }
                    // 按住期间保持压缩；抬起或被取消（滑出/多指）都要回弹
                    do {
                        val event = awaitPointerEvent()
                    } while (event.changes.any { it.pressed })
                    scope.launch {
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec =
                                spring(
                                    dampingRatio = releaseDampingRatio,
                                    stiffness = releaseStiffness,
                                ),
                        )
                    }
                }
            }.then(clickModifier)
    }
