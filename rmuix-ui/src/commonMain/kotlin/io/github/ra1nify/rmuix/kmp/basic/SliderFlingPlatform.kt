// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange

internal expect class SliderVelocityTracker() {
    fun sample(event: PointerEvent): PointerInputChange?

    fun calculateVelocity(): Float

    fun recycle()
}

@Composable
internal expect fun rememberSliderFlingFramePeriod(): Float
