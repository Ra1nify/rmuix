// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
internal actual fun platformRadioButtonActivatedColor(): Color = Color.White

@Composable
internal actual fun rememberRadioButtonFeedback(
    interactionSource: InteractionSource,
    colors: RadioButtonFeedbackColors,
    selected: Boolean,
    fallbackRadius: Dp,
): Modifier {
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    val windowFocused = LocalWindowInfo.current.isWindowFocused
    val scope = rememberCoroutineScope()
    val background = remember(scope) { SoftwareRadioButtonBackground(scope) }
    DisposableEffect(background) {
        onDispose { background.dispose() }
    }
    LaunchedEffect(background, selected, focused, hovered, pressed, windowFocused) {
        background.update(RadioButtonBackgroundState(selected, focused, hovered, pressed, windowFocused))
    }
    val color = if (selected) colors.checked else colors.unchecked
    val radiusPixels = radioButtonFallbackRadiusPixels(with(LocalDensity.current) { fallbackRadius.toPx() })
    val ripple = rememberSoftwareControlPressRipple(interactionSource, color, radiusPixels)
    return Modifier
        .drawBehind {
            val opacity = background.opacity.value
            if (opacity > 0f) {
                drawCircle(checkboxSoftwareRippleColor(color, opacity), radiusPixels.toFloat())
            }
        }.then(ripple)
}

@Stable
private class SoftwareRadioButtonBackground(
    private val scope: CoroutineScope,
) {
    private var state: RadioButtonBackgroundState? = null
    private var animationJob: Job? = null
    val opacity = Animatable(0f)

    fun update(nextState: RadioButtonBackgroundState) {
        if (state == nextState) return
        state = nextState
        val target = radioButtonBackgroundTarget(nextState) ?: return
        animationJob?.cancel()
        animationJob =
            scope.launch {
                opacity.snapTo(0f)
                if (target > 0f) opacity.animateTo(target, tween(durationMillis = 80, easing = LinearEasing))
            }
    }

    fun dispose() {
        animationJob?.cancel()
        animationJob = null
    }
}
