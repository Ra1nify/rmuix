// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import android.view.MotionEvent
import android.view.VelocityTracker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.platform.LocalView

internal actual class SliderVelocityTracker actual constructor() {
    private val tracker = VelocityTracker.obtain()
    private val pointerIds = SliderPointerIdentity()
    private var downTime: Long? = null
    private var nativeSampling = false
    private var fallbackPointer: PointerId? = null

    actual fun sample(event: PointerEvent): PointerInputChange? {
        val nativeEvent = event.motionEvent ?: return sampleComposed(event)
        if (nativeEvent.actionMasked == MotionEvent.ACTION_DOWN) pointerIds.clear()
        if (nativeEvent.actionMasked == MotionEvent.ACTION_DOWN || nativeEvent.actionMasked == MotionEvent.ACTION_POINTER_DOWN) {
            val newPointer = event.changes.firstOrNull { it.pressed && !it.previousPressed }
            if (newPointer != null) pointerIds.down(nativeEvent.getPointerId(nativeEvent.actionIndex), newPointer.id)
        }
        val pointerId = pointerIds.pointer(nativeEvent.getPointerId(0))
        val change = event.changes.firstOrNull { it.id == pointerId } ?: event.changes.firstOrNull() ?: return null
        if (nativeEvent.action == MotionEvent.ACTION_DOWN || nativeEvent.action == MotionEvent.ACTION_MOVE) {
            if (!nativeSampling) {
                tracker.clear()
                nativeSampling = true
                fallbackPointer = null
            }
            if (nativeEvent.action == MotionEvent.ACTION_DOWN) downTime = nativeEvent.downTime
            val copy = MotionEvent.obtain(nativeEvent)
            try {
                copy.offsetLocation(change.position.x - nativeEvent.x, change.position.y - nativeEvent.y)
                tracker.addMovement(copy)
            } finally {
                copy.recycle()
            }
        }
        if (nativeEvent.actionMasked == MotionEvent.ACTION_POINTER_UP) pointerIds.up(nativeEvent.getPointerId(nativeEvent.actionIndex))
        if (nativeEvent.action == MotionEvent.ACTION_UP || nativeEvent.action == MotionEvent.ACTION_CANCEL) pointerIds.clear()
        return change
    }

    actual fun calculateVelocity(): Float {
        tracker.computeCurrentVelocity(1000, 8000f)
        return sliderFlingVelocity(tracker.xVelocity)
    }

    actual fun recycle() {
        tracker.recycle()
        pointerIds.clear()
        fallbackPointer = null
        downTime = null
    }

    private fun sampleComposed(event: PointerEvent): PointerInputChange? {
        val change = event.changes.firstOrNull() ?: return null
        if (!sliderSamplesComposedVelocity(event.type, event.changes.any { it.previousPressed })) return change
        if (nativeSampling || fallbackPointer != change.id) {
            tracker.clear()
            nativeSampling = false
            fallbackPointer = change.id
            downTime = null
        }
        if (event.type == PointerEventType.Press && downTime == null) {
            addSyntheticPosition(change.uptimeMillis, change.position)
        } else if (event.type == PointerEventType.Move) {
            sampleSliderHistory(change.historical, change.uptimeMillis, change.position, ::addSyntheticPosition)
        }
        return change
    }

    private fun addSyntheticPosition(
        timeMillis: Long,
        position: Offset,
    ) {
        val action = if (downTime == null) MotionEvent.ACTION_DOWN else MotionEvent.ACTION_MOVE
        val start = downTime ?: timeMillis.also { downTime = it }
        val copy = MotionEvent.obtain(start, timeMillis, action, position.x, position.y, 0)
        try {
            tracker.addMovement(copy)
        } finally {
            copy.recycle()
        }
    }
}

@Composable
internal actual fun rememberSliderFlingFramePeriod(): Float {
    val view = LocalView.current
    return remember(view) {
        val rate = view.display?.refreshRate
        if (rate != null && rate.isFinite() && rate > 0f) 1f / rate else SLIDER_FLING_DEFAULT_PERIOD
    }
}
