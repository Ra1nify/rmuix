// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.util.VelocityTracker

internal actual class SliderVelocityTracker actual constructor() {
    private val tracker = VelocityTracker()
    private var pointerId: PointerId? = null

    actual fun sample(event: PointerEvent): PointerInputChange? {
        val change = event.changes.firstOrNull() ?: return null
        if (!sliderSamplesComposedVelocity(event.type, event.changes.any { it.previousPressed })) return change
        if (event.type == PointerEventType.Press && pointerId == null) {
            pointerId = change.id
            tracker.addPosition(change.uptimeMillis, change.position)
        } else if (event.type == PointerEventType.Move) {
            if (pointerId != change.id) {
                tracker.resetTracking()
                pointerId = change.id
            }
            sampleSliderHistory(change.historical, change.uptimeMillis, change.position, tracker::addPosition)
        }
        return change
    }

    actual fun calculateVelocity(): Float = sliderFlingVelocity(tracker.calculateVelocity().x)

    actual fun recycle() {
        tracker.resetTracking()
        pointerId = null
    }
}

@Composable
internal actual fun rememberSliderFlingFramePeriod(): Float = SLIDER_FLING_DEFAULT_PERIOD
