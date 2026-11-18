// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.shader

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEvent

internal actual class SpotlightPointerTracker actual constructor() {
    private val fallback = ComposedSpotlightPointer()

    actual fun sample(event: PointerEvent): SpotlightInput = fallback.sample(event)

    actual fun reset() {
        fallback.reset()
    }
}

internal actual fun DrawScope.spotlightCanvasSupported(): Boolean = true
