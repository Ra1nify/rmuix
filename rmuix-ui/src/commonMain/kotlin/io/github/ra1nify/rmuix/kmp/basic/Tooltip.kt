// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BasicTooltipDefaults
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import io.github.ra1nify.rmuix.kmp.squircle.SquircleShape
import io.github.ra1nify.rmuix.kmp.squircle.isSquircleEnabled
import io.github.ra1nify.rmuix.kmp.squircle.squircleBackground
import io.github.ra1nify.rmuix.kmp.theme.LocalContentColor
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.jvm.JvmInline
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds

/**
 * The preferred position of a tooltip relative to its anchor. The tooltip flips to the opposite
 * side when there is not enough room on the preferred side. [Start] / [End] resolve to [Left] /
 * [Right] following the layout direction.
 */
@JvmInline
value class TooltipAnchorPosition private constructor(
    private val value: Int,
) {
    override fun toString(): String =
        when (this) {
            Above -> "Above"
            Below -> "Below"
            Left -> "Left"
            Right -> "Right"
            Start -> "Start"
            End -> "End"
            else -> "Invalid"
        }

    companion object {
        val Above = TooltipAnchorPosition(0)
        val Below = TooltipAnchorPosition(1)
        val Left = TooltipAnchorPosition(2)
        val Right = TooltipAnchorPosition(3)
        val Start = TooltipAnchorPosition(4)
        val End = TooltipAnchorPosition(5)
    }
}

/**
 * Receiver scope for the [TooltipBox] tooltip content ([PlainTooltip] / [RichTooltip]). It exposes
 * the resolved anchor position and the anchor bounds so a caret can be drawn pointing at the anchor.
 */
@Stable
sealed interface TooltipScope {
    /** The preferred [TooltipAnchorPosition] of the tooltip relative to its anchor. */
    val positioning: TooltipAnchorPosition

    /** The anchor bounds in window coordinates, or null before the anchor is placed. */
    fun obtainAnchorBounds(): LayoutCoordinates?
}

private class TooltipScopeImpl(
    override val positioning: TooltipAnchorPosition,
    private val anchorBounds: () -> LayoutCoordinates?,
) : TooltipScope {
    override fun obtainAnchorBounds(): LayoutCoordinates? = anchorBounds()
}

/**
 * The state that controls the visibility of a [TooltipBox].
 *
 * Created via [rememberTooltipState]. When [isPersistent] is false the tooltip is dismissed
 * automatically after a short timeout; when true it stays visible until [dismiss] is called or the
 * user taps outside. A global [MutatorMutex] keeps only one tooltip visible at a time.
 */
@Stable
interface TooltipState {
    /** The transition driving the tooltip's enter / exit animation. */
    val transition: MutableTransitionState<Boolean>

    /** Whether the tooltip is currently visible (including while animating out). */
    val isVisible: Boolean

    /** Whether the tooltip stays visible until explicitly dismissed. */
    val isPersistent: Boolean

    /** Shows the tooltip, suspending until it is dismissed or superseded by another tooltip. */
    suspend fun show(mutatePriority: MutatePriority = MutatePriority.Default)

    /** Dismisses the tooltip. */
    fun dismiss()

    /** Cleans up the coroutine backing [show] when the tooltip leaves the composition. */
    fun onDispose()
}

@OptIn(ExperimentalFoundationApi::class)
@Stable
private class TooltipStateImpl(
    initialIsVisible: Boolean,
    override val isPersistent: Boolean,
    private val mutatorMutex: MutatorMutex,
) : TooltipState {
    override val transition: MutableTransitionState<Boolean> =
        MutableTransitionState(initialIsVisible)

    override val isVisible: Boolean
        // Keep the popup composed while shown or still animating. Gating on currentState would
        // hard-cut the exit when a dismiss interrupts an unfinished enter, since currentState only
        // flips to true once the enter has settled; !isIdle keeps it through the reversal so the
        // exit can animate out.
        get() = transition.targetState || !transition.isIdle

    /** continuation used to clean up */
    private var job: (CancellableContinuation<Unit>)? = null

    /**
     * Show the tooltip associated with the current [TooltipState]. When this method is called, all
     * of the other tooltips associated with [mutatorMutex] will be dismissed.
     *
     * @param mutatePriority [MutatePriority] to be used with [mutatorMutex].
     */
    override suspend fun show(mutatePriority: MutatePriority) {
        val cancellableShow: suspend () -> Unit = {
            suspendCancellableCoroutine { continuation ->
                transition.targetState = true
                job = continuation
            }
        }

        // Show associated tooltip for [TooltipDuration] amount of time
        // or until tooltip is explicitly dismissed depending on [isPersistent].
        mutatorMutex.mutate(mutatePriority) {
            try {
                if (isPersistent || mutatePriority == MutatePriority.UserInput) {
                    cancellableShow()
                } else {
                    withTimeout(BasicTooltipDefaults.TooltipDuration.milliseconds) { cancellableShow() }
                }
            } finally {
                if (mutatePriority != MutatePriority.PreventUserInput) {
                    // timeout or cancellation has occurred and we close out the current tooltip.
                    dismiss()
                }
            }
        }
    }

    /** Dismiss the tooltip associated with this [TooltipState] if it's currently being shown. */
    override fun dismiss() {
        transition.targetState = false
        if (isPersistent) {
            job?.cancel()
        }
    }

    /** Cleans up [mutatorMutex] when the tooltip associated with this state leaves Composition. */
    override fun onDispose() {
        job?.cancel()
    }
}

/**
 * Creates and remembers a [TooltipState].
 *
 * @param initialIsVisible whether the tooltip is initially visible.
 * @param isPersistent whether the tooltip stays visible until explicitly dismissed.
 * @param mutatorMutex the [MutatorMutex] used to ensure only one tooltip is shown at a time.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberTooltipState(
    initialIsVisible: Boolean = false,
    isPersistent: Boolean = false,
    mutatorMutex: MutatorMutex = BasicTooltipDefaults.GlobalMutatorMutex,
): TooltipState =
    remember(isPersistent, mutatorMutex) {
        TooltipStateImpl(initialIsVisible, isPersistent, mutatorMutex)
    }

/**
 * Spring driving the tooltip's exit scale. A spring (not a tween) honors the inbound velocity left
 * by the enter spring when a dismiss interrupts an unsettled enter, so the reversal stays smooth; a
 * tween would discard that velocity and produce a stall-and-reverse hitch.
 */
private val TooltipExitScaleSpec: FiniteAnimationSpec<Float> =
    spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium, visibilityThreshold = 0.0001f)

/**
 * A tooltip box that anchors a [tooltip] (a [PlainTooltip] or [RichTooltip]) to its [content].
 *
 * The tooltip is shown on hover (desktop / web) or long press (touch), or programmatically via
 * [TooltipState.show]. It is built on the foundation tooltip primitives, so triggering, the global
 * single-tooltip behavior, and the auto-dismiss timeout match the platform behavior.
 *
 * @param positionProvider the [PopupPositionProvider] positioning the tooltip, usually from
 *   [TooltipDefaults.rememberTooltipPositionProvider].
 * @param tooltip the tooltip content.
 * @param state the [TooltipState] controlling visibility.
 * @param modifier the modifier applied to the anchor wrapper.
 * @param focusable whether the tooltip is focusable; true for interactive rich tooltips so an
 *   outside tap dismisses them and their actions are reachable.
 * @param enableUserInput whether hover / long press on the anchor shows the tooltip.
 * @param content the anchor content.
 */
@Composable
fun TooltipBox(
    positionProvider: PopupPositionProvider,
    tooltip: @Composable TooltipScope.() -> Unit,
    state: TooltipState,
    modifier: Modifier = Modifier,
    focusable: Boolean = false,
    enableUserInput: Boolean = true,
    content: @Composable () -> Unit,
) {
    var anchorBounds by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val positioning = (positionProvider as? TooltipPopupPositionProvider)?.positioning ?: TooltipAnchorPosition.Below
    val scope = remember(positioning) { TooltipScopeImpl(positioning) { anchorBounds } }

    TooltipBoxImpl(
        positionProvider = positionProvider,
        tooltip = {
            AnimatedVisibility(
                visibleState = state.transition,
                enter =
                    fadeIn(ListPopupDefaults.AlphaEnterAnimationSpec) +
                        scaleIn(initialScale = 0.9f, animationSpec = ListPopupDefaults.FractionAnimationSpec),
                exit =
                    fadeOut(ListPopupDefaults.AlphaExitAnimationSpec) +
                        scaleOut(targetScale = 0.9f, animationSpec = TooltipExitScaleSpec),
            ) {
                scope.tooltip()
            }
        },
        state = state,
        modifier = modifier.onGloballyPositioned { anchorBounds = it },
        focusable = focusable,
        enableUserInput = enableUserInput,
        content = content,
    )
}

private const val TOOLTIP_LONG_PRESS_LABEL = "Show tooltip"
private const val TOOLTIP_PANE_TITLE = "Tooltip"

/**
 * Rmuix's tooltip primitive. Forked from the experimental foundation `BasicTooltipBox` so the mouse
 * hover gesture keeps a persistent tooltip open: when the cursor leaves the anchor the tooltip is
 * dismissed only if it is not persistent. This lets the cursor travel onto a persistent [RichTooltip]
 * to reach its action, matching the Material 3 tooltip behavior.
 */
@Composable
private fun TooltipBoxImpl(
    positionProvider: PopupPositionProvider,
    tooltip: @Composable () -> Unit,
    state: TooltipState,
    modifier: Modifier = Modifier,
    focusable: Boolean = false,
    enableUserInput: Boolean = true,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Box(
        modifier =
            modifier
                .tooltipGestures(enableUserInput, state)
                .tooltipAnchorSemantics(enableUserInput, state, scope),
    ) {
        if (state.isVisible) {
            TooltipPopup(
                positionProvider = positionProvider,
                state = state,
                focusable = focusable,
                content = tooltip,
            )
        }
        content()
    }

    DisposableEffect(state) { onDispose { state.onDispose() } }
}

@Composable
private fun TooltipPopup(
    positionProvider: PopupPositionProvider,
    state: TooltipState,
    focusable: Boolean,
    content: @Composable () -> Unit,
) {
    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = { if (state.isVisible) state.dismiss() },
        properties = PopupProperties(focusable = focusable),
    ) {
        Box(
            modifier =
                Modifier.semantics {
                    liveRegion = LiveRegionMode.Assertive
                    paneTitle = TOOLTIP_PANE_TITLE
                },
        ) {
            content()
        }
    }
}

/**
 * Shows the tooltip on long press (touch / stylus) and on mouse hover. The mouse
 * [PointerEventType.Exit] branch dismisses only non-persistent tooltips, so a persistent tooltip
 * stays open while the cursor moves from the anchor onto the tooltip to reach its action.
 */
private fun Modifier.tooltipGestures(
    enabled: Boolean,
    state: TooltipState,
): Modifier =
    if (enabled) {
        this
            .pointerInput(state) {
                coroutineScope {
                    awaitEachGesture {
                        val pass = PointerEventPass.Initial
                        val inputType = awaitFirstDown(pass = pass).type
                        if (inputType == PointerType.Touch || inputType == PointerType.Stylus) {
                            try {
                                withTimeout(viewConfiguration.longPressTimeoutMillis) {
                                    waitForUpOrCancellation(pass = pass)
                                }
                            } catch (_: PointerEventTimeoutCancellationException) {
                                // Long press detected: show the tooltip and consume the following up
                                // event so the anchor's own click handling does not also fire.
                                launch { state.show(MutatePriority.UserInput) }
                                val changes = awaitPointerEvent(pass = pass).changes
                                for (i in changes.indices) {
                                    changes[i].consume()
                                }
                            }
                        }
                    }
                }
            }.pointerInput(state) {
                coroutineScope {
                    awaitPointerEventScope {
                        val pass = PointerEventPass.Main
                        while (true) {
                            val event = awaitPointerEvent(pass)
                            if (event.changes[0].type == PointerType.Mouse) {
                                when (event.type) {
                                    PointerEventType.Enter -> {
                                        launch { state.show(MutatePriority.UserInput) }
                                    }

                                    PointerEventType.Exit -> {
                                        if (!state.isPersistent) state.dismiss()
                                    }
                                }
                            }
                        }
                    }
                }
            }
    } else {
        this
    }

private fun Modifier.tooltipAnchorSemantics(
    enabled: Boolean,
    state: TooltipState,
    scope: CoroutineScope,
): Modifier =
    if (enabled) {
        this.semantics(mergeDescendants = true) {
            onLongClick(
                label = TOOLTIP_LONG_PRESS_LABEL,
                action = {
                    scope.launch { state.show() }
                    true
                },
            )
        }
    } else {
        this
    }

/**
 * A plain tooltip that briefly describes an anchor with a short text label, using the Rmuix inverse
 * surface style.
 *
 * @param modifier the modifier applied to the tooltip.
 * @param caretShape when non-null, draws a caret pointing at the anchor; null (default) keeps the
 *   Rmuix caret-less style.
 * @param maxWidth the maximum width of the tooltip.
 * @param cornerRadius the corner radius of the tooltip.
 * @param containerColor the container color of the tooltip.
 * @param contentColor the content color of the tooltip.
 * @param insideMargin the margin inside the tooltip.
 * @param content the tooltip content, usually a short [Text].
 */
@Composable
fun TooltipScope.PlainTooltip(
    modifier: Modifier = Modifier,
    // The stock plain tip always draws the `coui_white_sharp_middle_arrow_up` caret; pass null to hide it.
    caretShape: Shape? = TooltipDefaults.caretShape(),
    maxWidth: Dp = TooltipDefaults.PlainTooltipMaxWidth,
    cornerRadius: Dp = TooltipDefaults.PlainTooltipCornerRadius,
    containerColor: Color = TooltipDefaults.plainTooltipContainerColor,
    contentColor: Color = TooltipDefaults.plainTooltipContentColor,
    insideMargin: PaddingValues = TooltipDefaults.PlainTooltipInsideMargin,
    content: @Composable () -> Unit,
) {
    TooltipSurface(
        caretShape = caretShape,
        maxWidth = maxWidth,
        cornerRadius = cornerRadius,
        containerColor = containerColor,
        insideMargin = insideMargin,
        modifier =
            modifier.semantics(mergeDescendants = false) {
                isTraversalGroup = true
                liveRegion = LiveRegionMode.Polite
            },
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            content()
        }
    }
}

/**
 * An image tooltip (`COUIToolTips.Image`): a 260dp-wide bubble whose top is a 132dp image area.
 *
 * The stock layout sets `android:scaleType="centerCrop"`, so the image is cropped to fill the
 * 132dp band rather than letterboxed. Everything else is inherited from the icon variant
 * (`COUIToolTips.Image` has an empty style that parents `COUIToolTips.Icon`).
 *
 * @param image the image content, drawn into a 132dp-tall box.
 * @param modifier the modifier applied to the tooltip.
 * @param caretShape when non-null, draws a caret pointing at the anchor. **Only null-ness is read**; the
 *   silhouette always comes from the stock vector.
 * @param maxWidth the maximum width; [TooltipDefaults.ImageTooltipMaxWidth] by default.
 * @param cornerRadius the corner radius of the bubble.
 * @param containerColor the bubble colour; [TooltipDefaults.iconTooltipContainerColor] by default.
 * @param contentColor the text colour; [TooltipDefaults.iconTooltipContentColor] by default.
 * @param insideMargin the margin inside the bubble.
 * @param content the tooltip text.
 */
@Composable
fun TooltipScope.ImageTooltip(
    image: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    caretShape: Shape? = null,
    maxWidth: Dp = TooltipDefaults.ImageTooltipMaxWidth,
    cornerRadius: Dp = TooltipDefaults.PlainTooltipCornerRadius,
    containerColor: Color = TooltipDefaults.iconTooltipContainerColor,
    contentColor: Color = TooltipDefaults.iconTooltipContentColor,
    insideMargin: PaddingValues = TooltipDefaults.PlainTooltipInsideMargin,
    content: @Composable () -> Unit,
) {
    TooltipSurface(
        caretShape = caretShape,
        maxWidth = maxWidth,
        cornerRadius = cornerRadius,
        containerColor = containerColor,
        insideMargin = insideMargin,
        modifier =
            modifier.semantics(mergeDescendants = false) {
                isTraversalGroup = true
                liveRegion = LiveRegionMode.Polite
            },
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Column {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(TooltipDefaults.ImageTooltipImageHeight)
                            .clip(RoundedCornerShape(TooltipDefaults.ImageTooltipImageRadius)),
                ) {
                    image()
                }
                content()
            }
        }
    }
}

/**
 * The stock dismiss control of `coui_tool_tips_layout.xml`: an 18dp glyph (`coui_tool_tips_delete_icon`)
 * 12dp from the bubble edge. The dimen pair `coui_tool_tips_delete_icon_size` (44dp) and
 * `..._icon_padding` (12dp) is **never referenced**, so the stock hit area is the 18dp glyph itself.
 *
 * The glyph is the stock vector itself - a 30% white disc plus a solid white cross - parsed from the
 * path data rather than approximated.
 *
 * @param onDismiss invoked when the control is tapped.
 * @param modifier the modifier applied to the touch target.
 */
@Composable
fun TooltipDismissButton(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val disc = remember { PathParser().parsePathString(TOOLTIP_DISMISS_DISC_PATH_DATA).toPath() }
    val cross = remember { PathParser().parsePathString(TOOLTIP_DISMISS_CROSS_PATH_DATA).toPath() }
    Box(
        modifier =
            modifier
                .size(TooltipDefaults.DismissSize)
                .clickable(role = Role.Button, onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(TooltipDefaults.DismissSize)) {
            val scale = size.width / 18f
            scale(scale, scale, pivot = Offset.Zero) {
                drawPath(disc, color = Color.White.copy(alpha = 0.3f))
                drawPath(cross, color = Color.White)
            }
        }
    }
}

/**
 * A rich tooltip that explains an anchor with an optional [title], body [text], and an optional
 * [action], using the Rmuix surface-container card style.
 *
 * @param modifier the modifier applied to the tooltip.
 * @param title the optional title shown above the body.
 * @param action the optional action shown below the body.
 * @param caretShape when non-null, draws a caret pointing at the anchor; null (default) keeps the
 *   Rmuix caret-less style.
 * @param maxWidth the maximum width of the tooltip.
 * @param cornerRadius the corner radius of the tooltip.
 * @param colors the [RichTooltipColors] of the tooltip.
 * @param insideMargin the margin inside the tooltip.
 * @param text the body content of the tooltip.
 */
@Composable
fun TooltipScope.RichTooltip(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    caretShape: Shape? = null,
    maxWidth: Dp = TooltipDefaults.RichTooltipMaxWidth,
    cornerRadius: Dp = TooltipDefaults.RichTooltipCornerRadius,
    colors: RichTooltipColors = TooltipDefaults.richTooltipColors(),
    insideMargin: PaddingValues = TooltipDefaults.RichTooltipInsideMargin,
    text: @Composable () -> Unit,
) {
    TooltipSurface(
        caretShape = caretShape,
        maxWidth = maxWidth,
        cornerRadius = cornerRadius,
        containerColor = colors.containerColor,
        insideMargin = insideMargin,
        modifier =
            modifier.semantics(mergeDescendants = false) {
                isTraversalGroup = true
            },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (title != null) {
                CompositionLocalProvider(LocalContentColor provides colors.titleContentColor) {
                    title()
                }
            }
            CompositionLocalProvider(LocalContentColor provides colors.contentColor) {
                text()
            }
            if (action != null) {
                Box(modifier = Modifier.align(Alignment.End)) {
                    CompositionLocalProvider(LocalContentColor provides colors.actionContentColor) {
                        action()
                    }
                }
            }
        }
    }
}

/** Viewport width of `coui_white_sharp_middle_arrow_up.xml`. */
private const val STOCK_ARROW_WIDTH = 20f

/** Viewport height of `coui_white_sharp_middle_arrow_up.xml`. */
private const val STOCK_ARROW_HEIGHT = 10.1f

/** `android:pathData` of `coui_white_sharp_middle_arrow_up.xml`, verbatim. */
private const val STOCK_ARROW_PATH_DATA =
    "M0,10.1A5.19,5.19 0,0 0,4.5 7.5L8.27,1a2,2 0,0 1,3.46 0L15.5,7.5A5.19,5.19 0,0 0,20 10.1Z"

/** Parsed once: the caret is drawn from the stock path data rather than approximated. */
private val stockArrowPath: Path by lazy { PathParser().parsePathString(STOCK_ARROW_PATH_DATA).toPath() }

@Composable
private fun TooltipScope.TooltipSurface(
    caretShape: Shape?,
    maxWidth: Dp,
    cornerRadius: Dp,
    containerColor: Color,
    insideMargin: PaddingValues,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val drawCaret =
        caretShape != null &&
            (positioning == TooltipAnchorPosition.Above || positioning == TooltipAnchorPosition.Below)
    val caretOnTop = positioning == TooltipAnchorPosition.Below
    val caretSize = TooltipDefaults.caretSize
    // squircleBackground falls back to a rounded rect when the shader path is unavailable, so the
    // shadow has to degrade with it.
    val squircleEnabled = isSquircleEnabled()
    var selfBounds by remember { mutableStateOf<LayoutCoordinates?>(null) }

    Box(
        modifier =
            modifier
                .widthIn(max = maxWidth)
                .onGloballyPositioned { selfBounds = it }
                .then(
                    if (drawCaret) {
                        Modifier.drawBehind {
                            val anchor = obtainAnchorBounds() ?: return@drawBehind
                            val self = selfBounds ?: return@drawBehind
                            if (!anchor.isAttached || !self.isAttached) return@drawBehind
                            val anchorCenterX = anchor.positionInWindow().x + anchor.size.width / 2f
                            val selfX = self.positionInWindow().x
                            val caretHalf = caretSize.width.toPx() / 2f
                            val cr = cornerRadius.toPx()
                            val centerX =
                                (anchorCenterX - selfX)
                                    .coerceIn(cr + caretHalf, size.width - cr - caretHalf)
                            val caretHeight = caretSize.height.toPx()
                            // The stock arrow is a filled vector with arc-rounded shoulders and a 2dp rounded
                            // tip (`M0,10.1A5.19,5.19 0,0 0,4.5 7.5L8.27,1a2,2 0,0 1,3.46 0L15.5,7.5A5.19,5.19
                            // 0,0 0,20 10.1Z`), not a straight-edged triangle, so the silhouette is drawn
                            // from the stock path data instead of three points.
                            val arrowScaleX = caretSize.width.toPx() / STOCK_ARROW_WIDTH
                            val arrowScaleY = caretHeight / STOCK_ARROW_HEIGHT
                            withTransform({
                                translate(
                                    left = centerX - caretHalf,
                                    top = if (caretOnTop) 0f else size.height - caretHeight,
                                )
                                scale(scaleX = arrowScaleX, scaleY = arrowScaleY, pivot = Offset.Zero)
                                if (!caretOnTop) {
                                    rotate(
                                        degrees = 180f,
                                        pivot = Offset(STOCK_ARROW_WIDTH / 2f, STOCK_ARROW_HEIGHT / 2f),
                                    )
                                }
                            }) {
                                drawPath(stockArrowPath, color = containerColor)
                            }
                        }
                    } else {
                        Modifier
                    },
                ),
    ) {
        Box(
            modifier =
                Modifier
                    .padding(
                        top = if (drawCaret && caretOnTop) caretSize.height else 0.dp,
                        bottom = if (drawCaret && !caretOnTop) caretSize.height else 0.dp,
                    ).dropShadow(
                        // The fill below is a squircle; a RoundedCornerShape shadow would cast a
                        // circular-arc silhouette under a continuous-curvature bubble.
                        shape = SquircleShape(cornerRadius, squircleEnabled = squircleEnabled),
                        shadow = Shadow(radius = 10.dp, color = Color.Black, alpha = 0.1f),
                    ).squircleBackground(color = containerColor, cornerRadius = cornerRadius)
                    .padding(insideMargin),
        ) {
            content()
        }
    }
}

/**
 * A plain tooltip convenience that wraps [TooltipBox] + [PlainTooltip] for a short text label.
 *
 * @param text the text label of the tooltip.
 * @param modifier the modifier applied to the anchor wrapper.
 * @param state the [TooltipState] controlling visibility.
 * @param enabled whether hover / long press on the anchor shows the tooltip.
 * @param positioning the preferred [TooltipAnchorPosition] of the tooltip.
 * @param containerColor the container color of the tooltip.
 * @param contentColor the content color of the tooltip.
 * @param content the anchor content.
 */
@Composable
fun TooltipBox(
    text: String,
    modifier: Modifier = Modifier,
    state: TooltipState = rememberTooltipState(isPersistent = false),
    enabled: Boolean = true,
    positioning: TooltipAnchorPosition = TooltipAnchorPosition.Below,
    containerColor: Color = TooltipDefaults.plainTooltipContainerColor,
    contentColor: Color = TooltipDefaults.plainTooltipContentColor,
    // `COUIToolTips` container margins; pass `DetailFloatingContainerMargins` for the
    // `COUIToolTips.DetailFloating` preset (12dp on all four sides).
    containerMargins: PaddingValues = TooltipDefaults.ContainerMargins,
    content: @Composable () -> Unit,
) {
    TooltipBox(
        positionProvider =
            TooltipDefaults.rememberTooltipPositionProvider(
                positioning = positioning,
                containerMargins = containerMargins,
            ),
        tooltip = {
            PlainTooltip(containerColor = containerColor, contentColor = contentColor) {
                Text(
                    text = text,
                    color = contentColor,
                    style = RmuixTheme.textStyles.body2,
                    fontSize = TooltipDefaults.PlainTooltipTextSize,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        state = state,
        modifier = modifier,
        focusable = false,
        enableUserInput = enabled,
        content = content,
    )
}

/**
 * A rich tooltip convenience that wraps [TooltipBox] + [RichTooltip] with an optional title and a
 * single text action. The action invokes [onActionClick] then dismisses the tooltip.
 *
 * The tooltip is persistent: trigger it via long press / hover, or hoist [state] and call
 * [TooltipState.show] from the anchor's own click. It is dismissed by an outside tap, a back press,
 * or the action.
 *
 * @param text the body text of the tooltip.
 * @param modifier the modifier applied to the anchor wrapper.
 * @param state the [TooltipState] controlling visibility.
 * @param title the optional title shown above the body.
 * @param actionText the optional action label.
 * @param onActionClick called when the action is clicked, before the tooltip is dismissed.
 * @param enabled whether hover / long press on the anchor shows the tooltip.
 * @param positioning the preferred [TooltipAnchorPosition] of the tooltip.
 * @param colors the [RichTooltipColors] of the tooltip.
 * @param content the anchor content.
 */
@Composable
fun RichTooltipBox(
    text: String,
    modifier: Modifier = Modifier,
    state: TooltipState = rememberTooltipState(isPersistent = true),
    title: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    positioning: TooltipAnchorPosition = TooltipAnchorPosition.Below,
    colors: RichTooltipColors = TooltipDefaults.richTooltipColors(),
    containerMargins: PaddingValues = TooltipDefaults.ContainerMargins,
    content: @Composable () -> Unit,
) {
    TooltipBox(
        positionProvider =
            TooltipDefaults.rememberTooltipPositionProvider(
                positioning = positioning,
                containerMargins = containerMargins,
            ),
        tooltip = {
            RichTooltip(
                title =
                    title?.let {
                        {
                            Text(
                                text = it,
                                color = colors.titleContentColor,
                                style = RmuixTheme.textStyles.subtitle,
                            )
                        }
                    },
                action =
                    actionText?.let {
                        {
                            TextButton(
                                text = it,
                                onClick = {
                                    onActionClick?.invoke()
                                    state.dismiss()
                                },
                                minWidth = 0.dp,
                                minHeight = 0.dp,
                                cornerRadius = TooltipDefaults.RichTooltipActionCornerRadius,
                                colors =
                                    ButtonDefaults.textButtonColors(
                                        color = Color.Transparent,
                                        textColor = colors.actionContentColor,
                                    ),
                                insideMargin = TooltipDefaults.RichTooltipActionInsideMargin,
                            )
                        }
                    },
                colors = colors,
            ) {
                Text(
                    text = text,
                    color = colors.contentColor,
                    style = RmuixTheme.textStyles.body2,
                )
            }
        },
        state = state,
        modifier = modifier,
        focusable = true,
        enableUserInput = enabled,
        content = content,
    )
}

/**
 * Colors for [RichTooltip].
 *
 * @param containerColor container color of the rich tooltip.
 * @param contentColor color of the rich tooltip body text.
 * @param titleContentColor color of the rich tooltip title.
 * @param actionContentColor color of the rich tooltip action label.
 */
@Immutable
data class RichTooltipColors(
    val containerColor: Color,
    val contentColor: Color,
    val titleContentColor: Color,
    val actionContentColor: Color,
)

/** The stock `COUIToolTips` container margins, resolved against the layout direction. */
private data class ContainerMargins(
    val start: Int,
    val end: Int,
    val top: Int,
    val bottom: Int,
)

/**
 * Positions a tooltip on the preferred side of its anchor, centered on the cross axis, flipping to
 * the opposite side when there is no room and clamped to the stock container margins.
 */
private class TooltipPopupPositionProvider(
    val positioning: TooltipAnchorPosition,
    private val spacing: Int,
    private val margins: ContainerMargins,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val resolved = positioning.resolve(layoutDirection)
        val centerX = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
        val centerY = anchorBounds.top + (anchorBounds.height - popupContentSize.height) / 2

        var x = centerX
        var y = centerY
        when (resolved) {
            TooltipAnchorPosition.Below -> {
                val below = anchorBounds.bottom + spacing
                val above = anchorBounds.top - popupContentSize.height - spacing
                y = if (below + popupContentSize.height <= windowSize.height || above < 0) below else above
            }

            TooltipAnchorPosition.Above -> {
                val above = anchorBounds.top - popupContentSize.height - spacing
                val below = anchorBounds.bottom + spacing
                y = if (above >= 0 || below + popupContentSize.height > windowSize.height) above else below
            }

            TooltipAnchorPosition.Left -> {
                val left = anchorBounds.left - popupContentSize.width - spacing
                val right = anchorBounds.right + spacing
                x = if (left >= 0 || right + popupContentSize.width > windowSize.width) left else right
            }

            TooltipAnchorPosition.Right -> {
                val right = anchorBounds.right + spacing
                val left = anchorBounds.left - popupContentSize.width - spacing
                x = if (right + popupContentSize.width <= windowSize.width || left < 0) right else left
            }

            else -> {
                y = anchorBounds.bottom + spacing
            }
        }

        // Start/end are inline edges, so they swap in RTL; top/bottom never do.
        val startMargin = if (layoutDirection == LayoutDirection.Rtl) margins.end else margins.start
        val endMargin = if (layoutDirection == LayoutDirection.Rtl) margins.start else margins.end
        val maxX = max(startMargin, windowSize.width - endMargin - popupContentSize.width)
        val maxY = max(margins.top, windowSize.height - margins.bottom - popupContentSize.height)
        x = x.coerceIn(startMargin, maxX)
        y = y.coerceIn(margins.top, maxY)
        return IntOffset(x, y)
    }
}

private fun TooltipAnchorPosition.resolve(layoutDirection: LayoutDirection): TooltipAnchorPosition =
    when (this) {
        TooltipAnchorPosition.Start -> {
            if (layoutDirection == LayoutDirection.Ltr) TooltipAnchorPosition.Left else TooltipAnchorPosition.Right
        }

        TooltipAnchorPosition.End -> {
            if (layoutDirection == LayoutDirection.Ltr) TooltipAnchorPosition.Right else TooltipAnchorPosition.Left
        }

        else -> {
            this
        }
    }

/**
 * Defaults for [TooltipBox], [PlainTooltip] and [RichTooltip].
 */
object TooltipDefaults {
    /** The default spacing between a tooltip and its anchor. */
    val SpacingBetweenTooltipAndAnchor = 8.dp

    /**
     * Size of the caret: the viewport of `coui_white_sharp_middle_arrow_up.xml` (20dp x 10.1dp).
     *
     * `COUIToolTips` renders the caret as an `ImageView` background built from that vector (or its
     * `coui_blue_sharp_middle_arrow_up` sibling) and rotates the same drawable 180 degrees for the
     * downward direction; `couiToolTipsArrowOverflowOffset` (5.7dp) only matters when the arrow has to
     * slide along the panel edge.
     */
    val caretSize = DpSize(20.dp, 10.1.dp)

    /** The default maximum width of a plain tooltip. */
    val PlainTooltipMaxWidth = TooltipStockMaxWidth

    /** The default corner radius of a plain tooltip. */
    val PlainTooltipCornerRadius = TooltipStockCornerRadius

    /** The default margin inside a plain tooltip. */
    val PlainTooltipInsideMargin = PaddingValues(all = TooltipStockInsideMargin)

    /** The default text size inside a plain tooltip (`tool_tips_content_text_size`). */
    val PlainTooltipTextSize = TooltipStockTextSize

    /** The default container color of a plain tooltip: `couiColorTips` (`#0080FF` / `#1A8CFF`). */
    val plainTooltipContainerColor: Color
        @Composable get() = tooltipBackground(isTooltipInDarkTheme())

    /** The default content color of a plain tooltip: `coui_color_white`. */
    val plainTooltipContentColor: Color
        @Composable get() = TooltipStockContentColor

    /** `coui_tool_tips_icon_style_layout.xml`: the icon slot is 40dp square. */
    val IconTooltipIconSize = TooltipStockIconSize

    /** `layout_marginTop` of the icon slot: 2dp. */
    val IconTooltipIconMarginTop = TooltipStockIconMarginTop

    /** `layout_marginStart` of the icon slot: 12dp. */
    val IconTooltipIconMarginStart = TooltipStockIconMarginStart

    /** `coui_tooltips_icon_scroll_margin_top`: 12dp above the scrolling text. */
    val IconTooltipScrollMarginTop = TooltipStockIconScrollMarginTop

    /** `?attr/couiColorBackgroundTop` = `coui_color_background_top`: `#FFFFFF` / `#333333`. */
    val iconTooltipContainerColor: Color
        @Composable get() = tooltipIconBackground(isTooltipInDarkTheme())

    /** `couiColorLabelSecondary`: `#8A000000` / `#8AFFFFFF`. */
    val iconTooltipContentColor: Color
        @Composable get() = tooltipIconContent(isTooltipInDarkTheme())

    /** `tool_tips_image_style_max_width`: the image variant caps at 260dp. */
    val ImageTooltipMaxWidth = TooltipStockImageMaxWidth

    /** `coui_tool_tips_image_no_edges_style_layout.xml`: the image band is 132dp tall. */
    val ImageTooltipImageHeight = TooltipStockImageHeight

    /** `coui_tooltips_imageview_radius`: 8dp on the image itself. */
    val ImageTooltipImageRadius = TooltipStockImageRadius

    /** `coui_tool_tips_delete_background_size`: the 18dp dismiss glyph. */
    val DismissSize = TooltipStockDismissSize

    /** `layout_marginEnd` of the dismiss control: 12dp. */
    val DismissMarginEnd = TooltipStockDismissMarginEnd

    /** The default maximum width of a rich tooltip. */
    val RichTooltipMaxWidth = 320.dp

    /** The default corner radius of a rich tooltip. */
    val RichTooltipCornerRadius = 16.dp

    /** The default margin inside a rich tooltip. */
    val RichTooltipInsideMargin = PaddingValues(all = 16.dp)

    /** The default corner radius of a rich tooltip action button. */
    val RichTooltipActionCornerRadius = 8.dp

    /** The default margin inside a rich tooltip action button. */
    val RichTooltipActionInsideMargin = PaddingValues(horizontal = 12.dp, vertical = 6.dp)

    /** Returns a [Shape] that, when passed as `caretShape`, enables the Rmuix tooltip caret. */
    fun caretShape(): Shape = TooltipCaretShape

    /** `couiToolTipsContainerLayoutMarginStart`: the bubble keeps 12dp from the inline start edge. */
    val ContainerMarginStart: Dp = TooltipStockContainerMarginStart

    /** `couiToolTipsContainerLayoutMarginEnd`: the bubble keeps 42dp from the inline end edge. */
    val ContainerMarginEnd: Dp = TooltipStockContainerMarginEnd

    /** `couiToolTipsContainerLayoutMarginTop`: 12dp from the top edge. */
    val ContainerMarginTop: Dp = TooltipStockContainerMarginTop

    /** `couiToolTipsContainerLayoutMarginBottom`: 12dp from the bottom edge. */
    val ContainerMarginBottom: Dp = TooltipStockContainerMarginBottom

    /** The four container margins as one value, in inline start/end order. */
    val ContainerMargins: PaddingValues =
        PaddingValues(
            start = ContainerMarginStart,
            end = ContainerMarginEnd,
            top = ContainerMarginTop,
            bottom = ContainerMarginBottom,
        )

    /**
     * `COUIToolTips.DetailFloating`: the container is the opaque `couiColorBackgroundTop`
     * (`#FFFFFF` / `#333333`) and the text `couiColorSecondNeutral` (`#8A000000` / `#8AFFFFFF`).
     */
    @Composable
    fun detailFloatingContainerColor(darkTheme: Boolean = isTooltipInDarkTheme()): Color =
        if (darkTheme) TooltipStockDetailFloatingContainerDark else TooltipStockDetailFloatingContainerLight

    /** @see detailFloatingContainerColor */
    @Composable
    fun detailFloatingContentColor(darkTheme: Boolean = isTooltipInDarkTheme()): Color =
        if (darkTheme) TooltipStockDetailFloatingContentDark else TooltipStockDetailFloatingContentLight

    /** `coui_tooltips_floating_background_padding_left/right` 16dp with the 13dp default vertical padding. */
    val DetailFloatingInsideMargin: PaddingValues =
        PaddingValues(horizontal = TooltipStockDetailFloatingPaddingHorizontal, vertical = 13.dp)

    /** `detail_floating_text_margin`: all four container margins collapse to 12dp. */
    val DetailFloatingContainerMargins: PaddingValues = PaddingValues(TooltipStockDetailFloatingMargin)

    /** `detail_floating_min_width`: 80dp; not enforced by [PlainTooltip], recorded for callers. */
    val DetailFloatingMinWidth: Dp = TooltipStockDetailFloatingMinWidth

    /** Default [TooltipColors] for [RichTooltip], keyed on the current theme. */
    @Composable
    fun richTooltipColors(
        containerColor: Color = RmuixTheme.colorScheme.surfaceContainer,
        contentColor: Color = RmuixTheme.colorScheme.onSurfaceContainerVariant,
        titleContentColor: Color = RmuixTheme.colorScheme.onSurfaceContainer,
        actionContentColor: Color = RmuixTheme.colorScheme.primary,
    ): RichTooltipColors =
        remember(containerColor, contentColor, titleContentColor, actionContentColor) {
            RichTooltipColors(
                containerColor = containerColor,
                contentColor = contentColor,
                titleContentColor = titleContentColor,
                actionContentColor = actionContentColor,
            )
        }

    /**
     * Returns a [PopupPositionProvider] that positions a tooltip on the [positioning] side of its
     * anchor.
     *
     * @param positioning the preferred [TooltipAnchorPosition].
     * @param spacingBetweenTooltipAndAnchor the spacing between the tooltip and the anchor.
     */
    @Composable
    fun rememberTooltipPositionProvider(
        positioning: TooltipAnchorPosition = TooltipAnchorPosition.Below,
        spacingBetweenTooltipAndAnchor: Dp = SpacingBetweenTooltipAndAnchor,
        containerMargins: PaddingValues = ContainerMargins,
    ): PopupPositionProvider {
        val density = LocalDensity.current
        val spacingPx = with(density) { spacingBetweenTooltipAndAnchor.roundToPx() }
        // `COUIToolTips` style defaults: 12dp start / 42dp end / 12dp top / 12dp bottom.
        // Resolve the inline start/end without applying the current direction: `calculatePosition`
        // receives its own `layoutDirection` and mirrors the pair there. Resolving with the ambient
        // direction here AND mirroring there swapped the 12/42 pair twice in RTL.
        val margins =
            with(density) {
                ContainerMargins(
                    start = containerMargins.calculateLeftPadding(LayoutDirection.Ltr).roundToPx(),
                    end = containerMargins.calculateRightPadding(LayoutDirection.Ltr).roundToPx(),
                    top = containerMargins.calculateTopPadding().roundToPx(),
                    bottom = containerMargins.calculateBottomPadding().roundToPx(),
                )
            }
        return remember(positioning, spacingPx, margins) {
            TooltipPopupPositionProvider(positioning, spacingPx, margins)
        }
    }
}

private object TooltipCaretShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val path =
            Path().apply {
                moveTo(size.width / 2f, 0f)
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                close()
            }
        return Outline.Generic(path)
    }
}
