// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Immutable
import kotlin.math.abs
import kotlin.math.roundToInt

@Immutable
internal data class SliderProgressMapping(
    val valueRange: ClosedFloatingPointRange<Float>,
    val progressRange: kotlin.ranges.IntRange?,
) {
    private val valueSpan = valueRange.endInclusive - valueRange.start

    fun flingStartPixels(
        value: Float,
        length: Float,
        reverse: Boolean,
    ): Float {
        val fraction = ((value.coerceIn(valueRange) - valueRange.start) / valueSpan).coerceIn(0f, 1f)
        if (!reverse) return fraction * length
        val range = progressRange ?: return (1f - fraction) * length
        val span = (range.last - range.first).toFloat()
        val progress = range.first + fraction * span
        return (range.last - progress + range.first) * (length / span)
    }

    fun flingValueAtPixels(
        position: Float,
        length: Float,
        reverse: Boolean,
    ): Float {
        val fraction = (if (reverse) (length - position) / length else position / length).coerceIn(0f, 1f)
        return valueRange.start + fraction * valueSpan
    }

    fun transitionTarget(
        boundedTarget: Float,
        rawFraction: Float,
        alreadyActive: Boolean,
    ): Float {
        if (!alreadyActive || rawFraction in 0f..1f) return boundedTarget.coerceIn(valueRange)
        return valueRange.start + rawFraction.coerceIn(-1f, 2f) * valueSpan
    }

    fun shouldStartTransition(
        from: Float,
        to: Float,
    ): Boolean {
        val range = progressRange ?: return from != to
        val progressSpan = (range.last - range.first).toFloat()
        return abs(to - from) * progressSpan > valueSpan
    }

    fun valueAtProgress(
        value: Float,
        round: Boolean,
    ): Float {
        val coerced = value.coerceIn(valueRange)
        val range = progressRange ?: return coerced
        val progressSpan = (range.last - range.first).toFloat()
        val progress = range.first + (coerced - valueRange.start) / valueSpan * progressSpan
        val selected = (if (round) progress.roundToInt() else progress.toInt()).coerceIn(range.first, range.last)
        return valueRange.start + (selected - range.first) / progressSpan * valueSpan
    }
}

internal class SliderDragTransition {
    var active = false
        private set
    private var pending = false

    fun begin() {
        active = false
        pending = true
    }

    fun route(
        from: Float,
        to: Float,
        mapping: SliderProgressMapping,
    ): Boolean {
        if (active) return true
        if (!pending) return false
        pending = false
        active = mapping.shouldStartTransition(from, to)
        return active
    }

    fun stop() {
        active = false
        pending = false
    }
}
