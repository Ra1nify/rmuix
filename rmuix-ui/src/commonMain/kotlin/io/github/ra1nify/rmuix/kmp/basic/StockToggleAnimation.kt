// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.state.ToggleableState

@Immutable
internal data class StockToggleTransition(
    val from: ToggleableState,
    val to: ToggleableState,
    val enabled: Boolean,
) {
    val durationMillis: Int
        get() = stockToggleTransitionDuration(from, to)
}

internal fun stockToggleTransitionDuration(
    from: ToggleableState,
    to: ToggleableState,
): Int = if (from == ToggleableState.Indeterminate && to == ToggleableState.On) 351 else 300

internal fun stockToggleTransition(
    from: ToggleableState,
    to: ToggleableState,
    wasEnabled: Boolean,
    enabled: Boolean,
    animateDisabledPartTransitions: Boolean,
): StockToggleTransition? {
    if (from == to || wasEnabled != enabled) return null
    if (!enabled && (!animateDisabledPartTransitions || from == ToggleableState.Off || to == ToggleableState.Off)) return null
    return StockToggleTransition(from, to, enabled)
}

@Stable
internal class StockToggleAnimation(
    initialState: ToggleableState,
    initiallyEnabled: Boolean,
    private val animateDisabledPartTransitions: Boolean,
) {
    private var selectedState = initialState
    private var selectedEnabled = initiallyEnabled
    val progress = Animatable(1f)
    var transition by mutableStateOf<StockToggleTransition?>(null)
        private set

    suspend fun select(
        state: ToggleableState,
        enabled: Boolean,
    ) {
        if (selectedState == state && selectedEnabled == enabled) return
        val next = stockToggleTransition(selectedState, state, selectedEnabled, enabled, animateDisabledPartTransitions)
        selectedState = state
        selectedEnabled = enabled
        transition = next
        if (next == null) {
            progress.snapTo(1f)
        } else {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(durationMillis = next.durationMillis, easing = LinearEasing))
        }
    }
}

@Composable
internal fun rememberStockToggleAnimation(
    state: ToggleableState,
    enabled: Boolean,
    animateDisabledPartTransitions: Boolean = false,
): StockToggleAnimation {
    val animation =
        remember(animateDisabledPartTransitions) {
            StockToggleAnimation(state, enabled, animateDisabledPartTransitions)
        }
    LaunchedEffect(animation, state, enabled) {
        animation.select(state, enabled)
    }
    return animation
}
