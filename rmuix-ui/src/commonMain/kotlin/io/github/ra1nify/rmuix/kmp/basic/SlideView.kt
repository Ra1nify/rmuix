// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.LocalContentColor
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * ColorOS 17 List Item Slide-To-Reveal Action Container (`COUISlideView`).
 *
 * Wraps list item content and enables horizontal swipe-to-reveal gesture to expose
 * trailing contextual actions (e.g. Delete, Mark as Read, Pin).
 *
 * @param modifier The modifier to be applied to the slide container.
 * @param enabled Whether the slide gesture is enabled.
 * @param state The [SlideViewState] controlling the open/close state.
 * @param actions The actions content revealed behind the item when swiped left.
 * @param content The main list item content.
 */
@Composable
fun SlideView(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: SlideViewState = rememberSlideViewState(),
    actions: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    var actionsWidthPx by remember { mutableIntStateOf(0) }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clipToBounds(),
    ) {
        // Actions layer revealed behind the foreground content
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .onSizeChanged { size ->
                        actionsWidthPx = size.width
                    },
            contentAlignment = Alignment.CenterEnd,
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                content = actions,
            )
        }

        // Foreground content sliding horizontally
        val draggableState =
            rememberDraggableState { delta ->
                if (actionsWidthPx > 0) {
                    val newOffset = (state.offsetX.value + delta).coerceIn(-actionsWidthPx.toFloat() * 1.25f, 0f)
                    coroutineScope.launch {
                        state.offsetX.snapTo(newOffset)
                    }
                }
            }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(state.offsetX.value.roundToInt(), 0) }
                    .then(
                        if (enabled) {
                            Modifier.draggable(
                                orientation = Orientation.Horizontal,
                                state = draggableState,
                                onDragStopped = { velocity ->
                                    val target =
                                        if (actionsWidthPx > 0) {
                                            val threshold = actionsWidthPx * 0.4f
                                            if (-state.offsetX.value > threshold || velocity < -500f) {
                                                -actionsWidthPx.toFloat()
                                            } else {
                                                0f
                                            }
                                        } else {
                                            0f
                                        }
                                    val willOpen = target != 0f
                                    if (willOpen != state.isOpened) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                    coroutineScope.launch {
                                        state.offsetX.animateTo(
                                            targetValue = target,
                                            animationSpec = spring(dampingRatio = 0.75f, stiffness = 300f),
                                        )
                                        state.isOpened = willOpen
                                    }
                                },
                            )
                        } else {
                            Modifier
                        },
                    ).then(
                        if (state.isOpened) {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    coroutineScope.launch {
                                        state.close()
                                    }
                                },
                            )
                        } else {
                            Modifier
                        },
                    ),
        ) {
            content()
        }
    }
}

/**
 * Standard action button item placed inside [SlideView]'s actions slot.
 *
 * @param onClick The callback invoked when the action button is clicked.
 * @param modifier The modifier to be applied to the action button.
 * @param backgroundColor The background color of the action button.
 * @param contentColor The content/icon color of the action button.
 * @param width The width of the action button (default 68dp).
 * @param content The composable content (icon and/or label).
 */
@Composable
fun RowScope.SlideAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    contentColor: Color = Color.White,
    width: Dp = SlideViewDefaults.ActionWidth,
    content: @Composable () -> Unit,
) {
    androidx.compose.runtime.CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier =
                modifier
                    .fillMaxHeight()
                    .width(width)
                    .background(backgroundColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

/**
 * State object to observe and control [SlideView] expansion.
 */
@Stable
class SlideViewState internal constructor(
    initialOpened: Boolean = false,
) {
    internal val offsetX = Animatable(if (initialOpened) -200f else 0f)

    var isOpened by mutableStateOf(initialOpened)
        internal set

    suspend fun open(widthPx: Float) {
        isOpened = true
        offsetX.animateTo(-widthPx, spring(dampingRatio = 0.75f, stiffness = 300f))
    }

    suspend fun close() {
        isOpened = false
        offsetX.animateTo(0f, spring(dampingRatio = 0.75f, stiffness = 300f))
    }
}

/**
 * Remembers a [SlideViewState] for [SlideView], optionally starting with the trailing actions revealed.
 * The state is not saveable: an open row is a transient gesture, so rotation closes it.
 */
@Composable
fun rememberSlideViewState(initialOpened: Boolean = false): SlideViewState = remember { SlideViewState(initialOpened) }

/**
 * Default values for [SlideView].
 *
 * **No official counterpart for the gesture.** The Settings APK has no swipe-to-reveal row: `SlideView`,
 * `Slidable` and `SwipeLayout` all return nothing, and the only `coui_slide_*` token
 * (the 56dp `coui_slide_selector_item_height`, itself never referenced) belongs to `COUISlideSelectPreference`, a preference row with
 * status text and click policies — not a swipe container. The geometry below is a library choice.
 */
object SlideViewDefaults {
    /** Default action item width (68dp in ColorOS 17). */
    val ActionWidth: Dp = 68.dp

    /** Danger action background color (e.g. Delete). */
    val DeleteColor: Color
        @Composable get() = RmuixTheme.colorScheme.error

    /** Primary action background color (e.g. Pin, Flag). */
    val PrimaryColor: Color
        @Composable get() = RmuixTheme.colorScheme.primary

    /** Neutral action background color (e.g. More, Archive). */
    val NeutralColor: Color
        @Composable get() = if (isSlideViewInDarkTheme()) Color(0xFF3A3A3C) else Color(0xFF8E8E93)
}

/** Resolves the neutral palette from the library theme instead of the OS theme. */
@Composable
private fun isSlideViewInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
