// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.HistoricalChange
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerId

internal fun sliderIgnoresPointerChange(
    type: PointerEventType,
    pointersStillPressed: Boolean,
): Boolean = type == PointerEventType.Press || (type == PointerEventType.Release && pointersStillPressed)

internal fun sliderSamplesComposedVelocity(
    type: PointerEventType,
    pointersPreviouslyPressed: Boolean,
): Boolean = type == PointerEventType.Move || (type == PointerEventType.Press && !pointersPreviouslyPressed)

internal fun sampleSliderHistory(
    historical: List<HistoricalChange>,
    timeMillis: Long,
    position: Offset,
    addSample: (Long, Offset) -> Unit,
) {
    historical.forEach { addSample(it.uptimeMillis, it.position) }
    addSample(timeMillis, position)
}

internal class SliderPointerIdentity {
    private val pointerIds = mutableMapOf<Int, PointerId>()

    fun down(
        nativeId: Int,
        pointerId: PointerId,
    ) {
        pointerIds[nativeId] = pointerId
    }

    fun pointer(nativeId: Int): PointerId? = pointerIds[nativeId]

    fun up(nativeId: Int) {
        pointerIds.remove(nativeId)
    }

    fun clear() {
        pointerIds.clear()
    }
}
