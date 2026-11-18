// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.shader

import android.view.MotionEvent
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerId

internal actual class SpotlightPointerTracker actual constructor() {
    private val identities = mutableMapOf<Int, PointerId>()
    private val fallback = ComposedSpotlightPointer()
    private var selected = -1

    actual fun sample(event: PointerEvent): SpotlightInput {
        val composed = fallback.sample(event)
        val native = event.motionEvent ?: return composed
        val action = native.actionMasked
        if (action == MotionEvent.ACTION_DOWN) identities.clear()
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            val added = event.changes.firstOrNull { it.pressed && !it.previousPressed }
            if (added != null) identities[native.getPointerId(native.actionIndex)] = added.id
        }

        fun position(pointerId: Int) = event.changes.firstOrNull { it.id == identities[pointerId] }?.position
        return when (action) {
            MotionEvent.ACTION_DOWN -> {
                selected = native.getPointerId(0)
                SpotlightInput(SpotlightAction.Down, position(selected))
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                selected = native.getPointerId(native.actionIndex)
                SpotlightInput(SpotlightAction.Move, position(selected))
            }

            MotionEvent.ACTION_MOVE -> {
                SpotlightInput(SpotlightAction.Move, position(selected))
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val leaving = native.getPointerId(native.actionIndex)
                val input =
                    if (leaving == selected) {
                        val index = if (native.actionIndex == 0) 1 else 0
                        if (index < native.pointerCount) selected = native.getPointerId(index)
                        SpotlightInput(SpotlightAction.Move, position(selected))
                    } else {
                        SpotlightInput(SpotlightAction.None)
                    }
                identities.remove(leaving)
                input
            }

            MotionEvent.ACTION_UP -> {
                SpotlightInput(SpotlightAction.Up, position(selected)).also { reset() }
            }

            MotionEvent.ACTION_CANCEL -> {
                SpotlightInput(SpotlightAction.Cancel).also { reset() }
            }

            else -> {
                SpotlightInput(SpotlightAction.None)
            }
        }
    }

    actual fun reset() {
        selected = -1
        identities.clear()
        fallback.reset()
    }
}

internal actual fun DrawScope.spotlightCanvasSupported(): Boolean = drawContext.canvas.nativeCanvas.isHardwareAccelerated
