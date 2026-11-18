// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.shader

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange

internal enum class SpotlightAction { Down, Move, Up, Cancel, None }

@Immutable
internal data class SpotlightInput(
    val action: SpotlightAction,
    val position: Offset? = null,
)

internal expect class SpotlightPointerTracker() {
    fun sample(event: PointerEvent): SpotlightInput

    fun reset()
}

internal class ComposedSpotlightPointer {
    private var selected: PointerId? = null

    fun sample(event: PointerEvent): SpotlightInput = sample(event.type, event.changes)

    fun sample(
        type: PointerEventType,
        changes: List<PointerInputChange>,
    ): SpotlightInput {
        if (type == PointerEventType.Press) {
            val change = changes.firstOrNull { it.pressed && !it.previousPressed } ?: return SpotlightInput(SpotlightAction.None)
            selected = change.id
            return SpotlightInput(if (changes.any { it.previousPressed }) SpotlightAction.Move else SpotlightAction.Down, change.position)
        }
        val change = changes.firstOrNull { it.id == selected }
        if (type == PointerEventType.Move) return SpotlightInput(SpotlightAction.Move, change?.position)
        if (type == PointerEventType.Release) {
            if (changes.none { it.pressed }) {
                selected = null
                return SpotlightInput(SpotlightAction.Up, change?.position)
            }
            if (change != null && !change.pressed) {
                val replacement = if (changes.firstOrNull()?.id == change.id) changes.getOrNull(1) else changes.firstOrNull()
                selected = replacement?.id
                return SpotlightInput(SpotlightAction.Move, replacement?.position)
            }
        }
        return SpotlightInput(SpotlightAction.None)
    }

    fun reset() {
        selected = null
    }
}
