// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt

@Composable
internal fun rememberNativeControlRipple(
    interactionSource: InteractionSource,
    drawable: RippleDrawable,
    colors: ColorStateList,
    radiusPixels: Int? = null,
    checked: Boolean = false,
    includeHoverAndFocus: Boolean = false,
    windowFocused: Boolean = false,
): Modifier {
    val ripple = remember(drawable) { NativeControlRipple(drawable) }
    SideEffect {
        ripple.configure(colors, radiusPixels)
        ripple.setControlState(checked, windowFocused)
    }
    DisposableEffect(ripple) {
        onDispose { ripple.dispose() }
    }
    LaunchedEffect(interactionSource, ripple, includeHoverAndFocus) {
        val presses = mutableSetOf<PressInteraction.Press>()
        val hovers = mutableSetOf<HoverInteraction.Enter>()
        val focuses = mutableSetOf<FocusInteraction.Focus>()
        try {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        if (presses.isEmpty()) ripple.move(interaction.pressPosition)
                        presses.add(interaction)
                    }

                    is PressInteraction.Release -> {
                        presses.remove(interaction.press)
                    }

                    is PressInteraction.Cancel -> {
                        presses.remove(interaction.press)
                    }

                    is HoverInteraction.Enter -> {
                        if (includeHoverAndFocus) hovers.add(interaction)
                    }

                    is HoverInteraction.Exit -> {
                        hovers.remove(interaction.enter)
                    }

                    is FocusInteraction.Focus -> {
                        if (includeHoverAndFocus) focuses.add(interaction)
                    }

                    is FocusInteraction.Unfocus -> {
                        focuses.remove(interaction.focus)
                    }

                    else -> {
                        return@collect
                    }
                }
                ripple.setTransientState(presses.isNotEmpty(), hovers.isNotEmpty(), focuses.isNotEmpty())
            }
        } finally {
            ripple.setTransientState(pressed = false, hovered = false, focused = false)
        }
    }
    return Modifier
        .pointerInput(ripple) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                ripple.move(down.position)
                while (true) {
                    val change = awaitPointerEvent(PointerEventPass.Initial).changes.firstOrNull { it.id == down.id } ?: break
                    ripple.move(change.position)
                    if (!change.pressed) break
                }
            }
        }.drawBehind {
            ripple.draw(this)
        }
}

private class NativeControlRipple(
    initialDrawable: RippleDrawable,
) : Drawable.Callback {
    private val drawableState = mutableStateOf(initialDrawable, neverEqualPolicy())
    private val handler = Handler(Looper.getMainLooper())
    private val scheduleToken = Any()
    private var configuredColors: ColorStateList? = null
    private var configuredRadius: Int? = null
    private var checked = false
    private var pressed = false
    private var hovered = false
    private var focused = false
    private var windowFocused = false
    private val drawable: RippleDrawable
        get() = drawableState.value

    init {
        drawable.callback = this
        drawable.state = intArrayOf(android.R.attr.state_enabled)
    }

    fun configure(
        colors: ColorStateList,
        radius: Int?,
    ) {
        if (configuredColors !== colors) {
            drawable.setColor(colors)
            configuredColors = colors
        }
        if (radius != null && configuredRadius != radius) {
            drawable.radius = radius
            configuredRadius = radius
        }
    }

    fun setControlState(
        checked: Boolean,
        windowFocused: Boolean,
    ) {
        if (this.checked == checked && this.windowFocused == windowFocused) return
        this.checked = checked
        this.windowFocused = windowFocused
        updateState()
    }

    fun setTransientState(
        pressed: Boolean,
        hovered: Boolean,
        focused: Boolean,
    ) {
        if (this.pressed == pressed && this.hovered == hovered && this.focused == focused) return
        this.pressed = pressed
        this.hovered = hovered
        this.focused = focused
        updateState()
    }

    private fun updateState() {
        drawable.state =
            intArrayOf(
                android.R.attr.state_enabled,
                if (checked) android.R.attr.state_checked else -android.R.attr.state_checked,
                if (pressed) android.R.attr.state_pressed else -android.R.attr.state_pressed,
                if (hovered) android.R.attr.state_hovered else -android.R.attr.state_hovered,
                if (focused) android.R.attr.state_focused else -android.R.attr.state_focused,
                if (windowFocused) android.R.attr.state_window_focused else -android.R.attr.state_window_focused,
            )
    }

    fun move(position: Offset) {
        val bounds = drawable.bounds
        val hotspot = if (position.x.isFinite() && position.y.isFinite()) position else Offset(bounds.exactCenterX(), bounds.exactCenterY())
        drawable.setHotspot(hotspot.x, hotspot.y)
    }

    fun draw(scope: DrawScope) {
        val ripple = drawable
        ripple.setBounds(0, 0, scope.size.width.roundToInt(), scope.size.height.roundToInt())
        scope.drawIntoCanvas { canvas -> ripple.draw(canvas.nativeCanvas) }
    }

    fun dispose() {
        drawable.setVisible(false, false)
        drawable.state = intArrayOf()
        drawable.jumpToCurrentState()
        drawable.callback = null
        handler.removeCallbacksAndMessages(scheduleToken)
    }

    override fun invalidateDrawable(who: Drawable) {
        drawableState.value = drawableState.value
    }

    override fun scheduleDrawable(
        who: Drawable,
        what: Runnable,
        whenMillis: Long,
    ) {
        handler.postAtTime(what, scheduleToken, whenMillis)
    }

    override fun unscheduleDrawable(
        who: Drawable,
        what: Runnable,
    ) {
        handler.removeCallbacks(what, scheduleToken)
    }
}
