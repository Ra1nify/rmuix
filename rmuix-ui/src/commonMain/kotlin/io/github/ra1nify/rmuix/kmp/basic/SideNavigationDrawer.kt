@file:Suppress("ktlint:compose:param-order-check")

// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.launch

/**
 * Colours of a [SideNavigationDrawer].
 *
 * @param scrimColor The dimming layer over the content (`coui_color_mask`).
 * @param dividerColor The hairline between the panel and the content (`coui_color_divider`).
 * @param drawerColor The panel background.
 */
@Immutable
data class SideNavigationDrawerColors(
    val scrimColor: Color,
    val dividerColor: Color,
    val drawerColor: Color,
)

/**
 * Defaults for [SideNavigationDrawer], from `COUISideNavigationBar`.
 */
object SideNavigationDrawerDefaults {
    /**
     * `coui_side_navigation_bar_min_width`.
     *
     * Despite its name, `COUISideNavigationBar` uses this dimen as the *fixed* width of its temporary
     * (overlay) mode, which is the mode a phone-size screen gets.
     */
    val Width = 300.dp

    /**
     * `coui_side_navigation_bar_max_width`: on medium/large screens stock uses
     * `min(this, screenWidth * WIDTH_PROPORTION)`.
     */
    val MaxWidth = 280.dp

    /** `COUISideNavigationBar.DRAWER_WIDTH_PROPORTION`. */
    const val WIDTH_PROPORTION = 0.382f

    /** `coui_side_navigation_drawer_divider_width`. */
    val DividerWidth = 0.33.dp

    /**
     * The width of the edge strip that starts an open gesture.
     *
     * Not a COUI dimen: `COUISideNavigationBar` is a `DrawerLayout`, whose edge size comes from the
     * framework's `ViewDragHelper.EDGE_SIZE` (20dp).
     */
    val EdgeWidth = 20.dp

    /** `COUISideNavigationBar`'s open duration is the framework's scroller; these are the settle bounds. */
    const val OPEN_MILLIS = 250
    const val CLOSE_MILLIS = 200

    /**
     * The stock colours: `coui_color_mask` `#33000000` / `#99000000` and `coui_color_divider`
     * `#1F000000` / `#33FFFFFF`.
     */
    @Composable
    fun drawerColors(
        scrimColor: Color = if (isSideDrawerDarkTheme()) Color(0x99000000) else Color(0x33000000),
        dividerColor: Color = if (isSideDrawerDarkTheme()) Color(0x33FFFFFF) else Color(0x1F000000),
        drawerColor: Color = RmuixTheme.colorScheme.surface,
    ): SideNavigationDrawerColors =
        remember(scrimColor, dividerColor, drawerColor) {
            SideNavigationDrawerColors(
                scrimColor = scrimColor,
                dividerColor = dividerColor,
                drawerColor = drawerColor,
            )
        }
}

@Composable
private fun isSideDrawerDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() <
        RmuixTheme.colorScheme.onBackground.luminance()

/**
 * The state of a [SideNavigationDrawer], a `0f` (closed) .. `1f` (open) slide position.
 *
 * [open] and [close] suspend until the animation finishes, mirroring
 * `COUISideNavigationBar.openDrawer` / `closeDrawer`.
 */
@Stable
class SideNavigationDrawerState internal constructor(
    initialOpen: Boolean,
) {
    internal val progress = Animatable(if (initialOpen) 1f else 0f)

    /** Whether the panel is fully open. */
    val isOpen: Boolean
        get() = progress.value >= 1f

    /** Whether the panel is fully closed. */
    val isClosed: Boolean
        get() = progress.value <= 0f

    /** Slides the panel open. */
    suspend fun open() {
        progress.animateTo(1f, tween(durationMillis = SideNavigationDrawerDefaults.OPEN_MILLIS))
    }

    /** Slides the panel closed. */
    suspend fun close() {
        progress.animateTo(0f, tween(durationMillis = SideNavigationDrawerDefaults.CLOSE_MILLIS))
    }
}

/** Remembers a [SideNavigationDrawerState]. */
@Composable
fun rememberSideNavigationDrawerState(initialOpen: Boolean = false): SideNavigationDrawerState =
    remember(initialOpen) { SideNavigationDrawerState(initialOpen) }

/**
 * The ColorOS side navigation drawer: `COUISideNavigationBar`
 * (`com.coui.appcompat.sidenavigation`).
 *
 * A panel that slides in from the start edge over a dimming scrim, framed by a hairline divider - the
 * shape `COUISideNavigationBar` draws on top of `DrawerLayout`. The default width is stock's
 * temporary-mode width (`coui_side_navigation_bar_min_width`, 300dp), which is what a phone-size screen
 * receives; the medium/large branch
 * (`min(coui_side_navigation_bar_max_width, screenWidth * 0.382)`) is documented in
 * [SideNavigationDrawerDefaults] but not applied.
 *
 * Dragging the panel closes it, and dragging from a 20dp strip at the start edge opens it; releasing
 * settles to whichever side is nearer, like `DrawerLayout`. The settle curves are not specified by the
 * dump (the framework scroller is opaque), so plain [tween]s are used.
 *
 * @param modifier The modifier to be applied to the drawer.
 * @param state The drawer state.
 * @param width The panel width.
 * @param colors The colours of the drawer.
 * @param drawerContent The content of the sliding panel.
 * @param content The content underneath.
 */
@Composable
fun SideNavigationDrawer(
    modifier: Modifier = Modifier,
    state: SideNavigationDrawerState = rememberSideNavigationDrawerState(),
    width: Dp = SideNavigationDrawerDefaults.Width,
    colors: SideNavigationDrawerColors = SideNavigationDrawerDefaults.drawerColors(),
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val widthPx = with(density) { width.toPx() }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val interactionSource = remember { MutableInteractionSource() }

    /** Applies a drag of [signedPx] to the slide position, clamped to the panel's travel. */
    fun dragBy(signedPx: Float) {
        val target = (state.progress.value * widthPx + signedPx).coerceIn(0f, widthPx)
        scope.launch { state.progress.snapTo(target / widthPx) }
    }

    fun settle() {
        scope.launch {
            if (state.progress.value >= 0.5f) state.open() else state.close()
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        content()
        // Scrim: drawn in the draw phase so drag frames do not recompose the tree.
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .drawBehind {
                        val fraction = state.progress.value
                        if (fraction > 0f) {
                            drawRect(color = colors.scrimColor.copy(alpha = colors.scrimColor.alpha * fraction))
                        }
                    }.then(
                        // Tapping the scrim closes the drawer, like `DrawerLayout`'s scrim.
                        if (state.isOpen) {
                            Modifier.clickable(
                                interactionSource = interactionSource,
                                indication = null,
                            ) { scope.launch { state.close() } }
                        } else {
                            Modifier
                        },
                    ),
        )
        Column(
            modifier =
                Modifier
                    .align(if (rtl) Alignment.CenterEnd else Alignment.CenterStart)
                    .width(width)
                    .fillMaxHeight()
                    .graphicsLayer {
                        val fraction = state.progress.value
                        translationX =
                            if (rtl) {
                                widthPx * (1f - fraction)
                            } else {
                                -widthPx * (1f - fraction)
                            }
                    }.background(colors.drawerColor)
                    .drawBehind {
                        val divider = SideNavigationDrawerDefaults.DividerWidth.toPx()
                        drawRect(
                            color = colors.dividerColor,
                            topLeft = Offset(if (rtl) 0f else size.width - divider, 0f),
                            size = Size(divider, size.height),
                        )
                    }.draggable(
                        orientation = Orientation.Horizontal,
                        state =
                            rememberDraggableState { delta ->
                                dragBy(if (rtl) -delta else delta)
                            },
                        onDragStopped = { settle() },
                    ),
        ) {
            drawerContent()
        }
        if (state.isClosed) {
            // The start-edge strip that starts the open gesture.
            Row(
                modifier =
                    Modifier
                        .align(if (rtl) Alignment.CenterEnd else Alignment.CenterStart)
                        .width(SideNavigationDrawerDefaults.EdgeWidth)
                        .fillMaxHeight()
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state =
                                rememberDraggableState { delta ->
                                    dragBy(if (rtl) -delta else delta)
                                },
                            onDragStopped = { settle() },
                        ),
            ) {}
        }
    }
}
