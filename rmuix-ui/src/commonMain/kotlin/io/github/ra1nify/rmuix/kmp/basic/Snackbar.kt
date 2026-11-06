// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.zIndex
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Close
import io.github.ra1nify.rmuix.kmp.squircle.squircleBackground
import io.github.ra1nify.rmuix.kmp.theme.LocalContentColor
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.milliseconds

/**
 * Possible durations of the [Snackbar].
 */
sealed interface SnackbarDuration {
    /** Show the Snackbar for a short period of time. */
    data object Short : SnackbarDuration

    /** Show the Snackbar for a long period of time. */
    data object Long : SnackbarDuration

    /** Show the Snackbar indefinitely until dismissed. */
    data object Indefinite : SnackbarDuration

    /** Show the Snackbar for a custom period of time. */
    data class Custom(val durationMillis: kotlin.Long) : SnackbarDuration {
        init {
            require(durationMillis > 0) { "durationMillis must be greater than 0" }
        }
    }
}

/**
 * Possible results of the [Snackbar].
 */
enum class SnackbarResult {
    /** The Snackbar was dismissed. */
    Dismissed,

    /** The Snackbar's action was performed. */
    ActionPerformed,
}

/**
 * Visuals for a [Snackbar].
 *
 * @param message text to be shown in the Snackbar
 * @param actionLabel optional action label to be shown in the Snackbar
 * @param withDismissAction whether to show a dismiss action in the Snackbar
 * @param duration duration of the Snackbar
 */
@Immutable
data class SnackbarVisuals(
    val message: String,
    val actionLabel: String?,
    val withDismissAction: Boolean,
    val duration: SnackbarDuration,
)

/**
 * Interface representing the data of a [Snackbar].
 */
interface SnackbarData {
    /** Visuals of the Snackbar. */
    val visuals: SnackbarVisuals

    /** Dismiss the Snackbar. */
    suspend fun dismiss()

    /** Perform the action of the Snackbar. */
    suspend fun performAction()
}

private enum class SnackbarSwipeToDismissValue {
    StartToEnd,
    EndToStart,
    Settled,
}

/** Gap kept between the newest snackbar and the bottom edge of the [SnackbarHost]. */
private val HostBottomPadding = 12.dp

/**
 * State of the [SnackbarHost].
 *
 * It allows to show a [Snackbar] with a message and an optional action.
 */
@Stable
class SnackbarHostState {
    private val entries = mutableStateListOf<SnackbarEntry>()
    internal val currentSnackbars: List<SnackbarEntry> get() = entries
    suspend fun newestSnackbarData(): SnackbarData? = mutex.withLock {
        entries.firstOrNull { it.visible }?.data
    }

    suspend fun oldestSnackbarData(): SnackbarData? = mutex.withLock {
        entries.lastOrNull { it.visible }?.data
    }

    private val mutex = Mutex()
    private var idCounter = 0L

    internal suspend fun removeEntry(entry: SnackbarEntry) {
        mutex.withLock {
            entries.remove(entry)
        }
    }

    /**
     * Shows a [Snackbar] with the provided [message].
     *
     * @param message text to be shown in the Snackbar
     * @param actionLabel optional action label to be shown in the Snackbar
     * @param withDismissAction whether to show a dismiss action in the Snackbar
     * @param duration duration of the Snackbar
     * @return result of the Snackbar
     */
    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ): SnackbarResult {
        val result = CompletableDeferred<SnackbarResult>()
        val visuals = SnackbarVisuals(message, actionLabel, withDismissAction, duration)

        mutex.withLock {
            val currentId = ++idCounter
            val data = object : SnackbarData {
                override val visuals = visuals
                private val snackbarMutex = Mutex()
                private var completed = false

                override suspend fun dismiss() {
                    snackbarMutex.withLock {
                        if (completed) return
                        completed = true
                    }
                    if (!result.isCompleted) result.complete(SnackbarResult.Dismissed)
                    clear()
                }

                override suspend fun performAction() {
                    snackbarMutex.withLock {
                        if (completed) return
                        completed = true
                    }
                    if (!result.isCompleted) result.complete(SnackbarResult.ActionPerformed)
                    clear()
                }

                private suspend fun clear() {
                    this@SnackbarHostState.mutex.withLock {
                        val index = entries.indexOfFirst { it.id == currentId }
                        if (index != -1) entries[index] = entries[index].copy(visible = false)
                    }
                }
            }
            val entry = SnackbarEntry(currentId, data)
            entries.add(0, entry)
        }

        return result.await()
    }

    @Immutable
    internal data class SnackbarEntry(
        val id: Long,
        val data: SnackbarData,
        val visible: Boolean = true,
    )
}

/**
 * Convert [SnackbarDuration] to milliseconds, taking into account accessibility settings.
 */
internal fun SnackbarDuration.toMillis(
    hasAction: Boolean,
    accessibilityManager: AccessibilityManager?,
): Long {
    val original = when (this) {
        SnackbarDuration.Indefinite -> Long.MAX_VALUE
        SnackbarDuration.Long -> 10000L
        SnackbarDuration.Short -> 4000L
        is SnackbarDuration.Custom -> durationMillis
    }
    if (accessibilityManager == null) {
        return original
    }
    return accessibilityManager.calculateRecommendedTimeoutMillis(
        originalTimeoutMillis = original,
        containsIcons = true,
        containsText = true,
        containsControls = hasAction,
    )
}

/**
 * Host for [Snackbar]s to be shown.
 *
 * @param state state of the [SnackbarHost]
 * @param modifier modifier to be applied to the [SnackbarHost]
 * @param canSwipeToDismiss flag of can be dismissed by swipe of the current [SnackbarHost]
 * @param content content of the [SnackbarHost]
 */
@Composable
fun SnackbarHost(
    state: SnackbarHostState,
    modifier: Modifier = Modifier,
    canSwipeToDismiss: Boolean = true,
    content: @Composable (SnackbarData) -> Unit = { Snackbar(it) },
) {
    val bottomPaddingPx = with(LocalDensity.current) { HostBottomPadding.roundToPx() }
    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        LazyColumn(
            reverseLayout = true,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = HostBottomPadding),
        ) {
            itemsIndexed(state.currentSnackbars, key = { _, entry -> entry.id }) { index, entry ->
                val visibleState = remember { MutableTransitionState(false) }
                val accessibilityManager = LocalAccessibilityManager.current

                val anchoredDraggableState = remember {
                    AnchoredDraggableState(
                        initialValue = SnackbarSwipeToDismissValue.Settled,
                    )
                }

                visibleState.targetState = entry.visible

                if (!visibleState.targetState && visibleState.isIdle) {
                    LaunchedEffect(entry) { state.removeEntry(entry) }
                }

                LaunchedEffect(entry) {
                    val duration = entry.data.visuals.duration.toMillis(
                        entry.data.visuals.actionLabel != null,
                        accessibilityManager,
                    )
                    delay(duration.milliseconds)

                    if (anchoredDraggableState.currentValue != SnackbarSwipeToDismissValue.Settled) return@LaunchedEffect
                    entry.data.dismiss()
                }

                LaunchedEffect(anchoredDraggableState.currentValue) {
                    if (anchoredDraggableState.currentValue != SnackbarSwipeToDismissValue.Settled) {
                        entry.data.dismiss()
                    }
                }

                AnimatedVisibility(
                    modifier = Modifier
                        .onSizeChanged { size ->
                            val width = size.width.toFloat()

                            val anchors = DraggableAnchors {
                                SnackbarSwipeToDismissValue.Settled at 0f
                                SnackbarSwipeToDismissValue.StartToEnd at width
                                SnackbarSwipeToDismissValue.EndToStart at -width
                            }
                            anchoredDraggableState.updateAnchors(anchors)
                        }
                        .anchoredDraggable(
                            state = anchoredDraggableState,
                            orientation = Orientation.Horizontal,
                            enabled = entry.visible && canSwipeToDismiss,
                            flingBehavior = AnchoredDraggableDefaults.flingBehavior(
                                state = anchoredDraggableState,
                                positionalThreshold = { distance: Float -> distance * 0.5f },
                            ),
                        )
                        .offset {
                            val offset = try {
                                anchoredDraggableState.requireOffset()
                            } catch (_: IllegalStateException) {
                                0f
                            }
                            IntOffset(offset.fastRoundToInt(), 0)
                        }
                        .zIndex((state.currentSnackbars.size - index).toFloat()),
                    visibleState = visibleState,
                    // No animateItem (placement lag gets clipped), no alpha on moving parts
                    // (fading layers clip to their bounds), and the exit's clip applies to enter.
                    enter = slideInVertically(initialOffsetY = { bottomPaddingPx }) + expandVertically(
                        expandFrom = Alignment.Top,
                        clip = false,
                    ),
                    exit = if (index == 0) {
                        slideOutVertically(targetOffsetY = { bottomPaddingPx }) + shrinkVertically(
                            shrinkTowards = Alignment.Top,
                            clip = false,
                        )
                    } else {
                        fadeOut(spring(stiffness = Spring.StiffnessMedium)) + shrinkVertically(
                            shrinkTowards = Alignment.Top,
                            clip = false,
                        )
                    },
                ) {
                    content(entry.data)
                }
            }
        }
    }
}

/**
 * A Snackbar is a temporary message that appears at the bottom of the screen.
 *
 * @param data data of the [Snackbar]
 * @param modifier modifier to be applied to the [Snackbar]
 * @param cornerRadius corner radius of the [Snackbar]
 * @param colors colors of the [Snackbar]
 * @param insideMargin retained for source compatibility. The bar now pads its content with
 *   [SnackbarDefaults.ContentHorizontalPadding] and [SnackbarDefaults.ContentVerticalPadding] to match
 *   ColorOS `coui_snack_bar_child_margin_*`, so this value no longer takes effect.
 */
@Composable
fun Snackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = SnackbarDefaults.CornerRadius,
    colors: SnackbarColors = SnackbarDefaults.snackbarColors(),
    insideMargin: PaddingValues = SnackbarDefaults.InsideMargin,
) {
    val visuals = data.visuals
    val scope = rememberCoroutineScope()

    CompositionLocalProvider(
        LocalContentColor provides colors.contentColor,
    ) {
        Box(
            modifier = modifier
                .semantics(mergeDescendants = false) {
                    isTraversalGroup = true
                    liveRegion = LiveRegionMode.Polite
                }
                .padding(SnackbarDefaults.OuterPadding)
                .dropShadow(
                    shape = RoundedCornerShape(cornerRadius),
                    shadow = Shadow(
                        radius = SnackbarDefaults.ShadowRadius,
                        color = Color.Black,
                        alpha = 0.93f,
                    ),
                )
                .squircleBackground(color = colors.containerColor, cornerRadius = cornerRadius)
                .pointerInput(Unit) {
                    detectTapGestures { /* Consume click */ }
                },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .widthIn(max = SnackbarDefaults.MaxWidth)
                    .padding(
                        horizontal = SnackbarDefaults.ContentHorizontalPadding,
                        vertical = SnackbarDefaults.ContentVerticalPadding,
                    ),
            ) {
                Text(
                    text = visuals.message,
                    color = colors.contentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                if (!visuals.actionLabel.isNullOrEmpty()) {
                    val onAction by rememberUpdatedState(data::performAction)
                    // ColorOS draws the action as a plain label, not a filled pill: no container, 14sp
                    // medium, capped at 110dp wide with 4dp of air after it.
                    Text(
                        text = visuals.actionLabel,
                        color = colors.actionContentColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(
                                start = 12.dp,
                                end = SnackbarDefaults.ActionMarginEnd,
                            )
                            .widthIn(max = SnackbarDefaults.ActionMaxWidth)
                            .clickable(
                                indication = null,
                                interactionSource = null,
                            ) { scope.launch { onAction() } }
                            .padding(vertical = SnackbarDefaults.ActionVerticalMargin),
                    )
                }

                if (visuals.withDismissAction) {
                    val onDismiss by rememberUpdatedState(data::dismiss)
                    Icon(
                        imageVector = RmuixIcons.Basic.Close,
                        contentDescription = "Dismiss",
                        tint = colors.dismissActionContentColor,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(20.dp)
                            .clickable(
                                indication = null,
                                interactionSource = null,
                            ) {
                                scope.launch { onDismiss() }
                            },
                    )
                }
            }
        }
    }
}

/**
 * Colors for [Snackbar].
 *
 * @param containerColor container color of the Snackbar
 * @param contentColor content color of the Snackbar
 * @param actionContentColor content color of the action label pill of the Snackbar
 * @param dismissActionContentColor content color of the dismiss action of the Snackbar
 * @param actionContainerColor container color of the action label pill of the Snackbar; pair it
 *   with [actionContentColor] so the label keeps enough contrast against the pill
 */
@Immutable
data class SnackbarColors(
    val containerColor: Color,
    val contentColor: Color,
    val actionContentColor: Color,
    val dismissActionContentColor: Color,
    val actionContainerColor: Color,
)

/**
 * Defaults for [Snackbar].
 */
object SnackbarDefaults {
    /**
     * Corner radius of the bar.
     *
     * ColorOS sets its outline on the `l` step of the corner scale: nominal 16dp, radius 13dp, weight
     * 0.99. The `coui_snack_bar_radius` dimen of 16dp is declared but referenced nowhere.
     */
    val CornerRadius = 13.dp

    /** Corner radius while the bar is a single line and collapsed. */
    val CornerRadiusSingleLine = 24.dp

    /** Maximum width of the bar: ColorOS `coui_snack_bar_max_width` = 328dp. */
    val MaxWidth = 328.dp

    /** Gap between the bar and the bottom of the screen: `coui_snack_bar_margin_bottom` = 32dp. */
    val MarginBottom = 32.dp

    /** Horizontal padding inside the bar: `coui_snack_bar_child_margin_horizontal_*` = 16dp. */
    val ContentHorizontalPadding = 16.dp

    /** Vertical padding inside the bar: `coui_snack_bar_child_margin_vertical` = 10dp. */
    val ContentVerticalPadding = 10.dp

    /** Vertical padding once the message wraps: 12dp above with 8dp below. */
    val ContentVerticalPaddingMultiline = 12.dp
    val ContentVerticalPaddingMultilineBottom = 8.dp

    /** Size of the leading status icon: `coui_snack_bar_icon_width/height` = 30dp. */
    val IconSize = 30.dp

    /** Vertical inset of the leading icon: `coui_snack_bar_icon_margin_top_horizontal` = 16dp. */
    val IconVerticalMargin = 16.dp

    /** Upper bound on the action's width: `coui_snack_bar_action_max_width` = 110dp. */
    val ActionMaxWidth = 110.dp

    /** Trailing gap after the action: `coui_snack_bar_action_margin_horizontal_end` = 4dp. */
    val ActionMarginEnd = 4.dp

    /** Vertical padding around the action: `coui_snack_bar_action_margin_vertical` = 10dp. */
    val ActionVerticalMargin = 10.dp

    /** Drop shadow beneath the bar: `coui_snack_bar_shadow_size` = 26dp. */
    val ShadowRadius = 26.dp

    /**
     * The default inside margin.
     *
     * Kept for source compatibility; the bar now pads its own content with [ContentHorizontalPadding]
     * and [ContentVerticalPadding] to match `coui_snack_bar_child_margin_*`.
     */
    val InsideMargin = PaddingValues(all = 12.dp)

    /** The default outer padding around the Snackbar. */
    val OuterPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp)

    /**
     * The default corner radius of the action label pill.
     *
     * @deprecated The ColorOS action is a plain label with no pill behind it. Kept so callers passing it
     * to a custom action still compile.
     */
    @Deprecated(
        message = "The action is a plain label in ColorOS and has no pill container.",
        replaceWith = ReplaceWith("SnackbarDefaults.ActionMaxWidth"),
    )
    val ActionCornerRadius = 50.dp

    /**
     * The default inside margin of the action label pill.
     *
     * @deprecated The action is a plain label in ColorOS; use [ActionVerticalMargin].
     */
    @Deprecated(
        message = "The action is a plain label in ColorOS; use ActionVerticalMargin.",
        replaceWith = ReplaceWith("SnackbarDefaults.ActionVerticalMargin"),
    )
    val ActionInsideMargin = PaddingValues(horizontal = 12.dp, vertical = 0.dp)

    /**
     * The default [SnackbarColors].
     *
     * ColorOS renders the bar as a pale raised card - `couiColorSurfaceTop`, #FFFFFFFF light and
     * #FF333333 dark - carrying dark text, rather than the inverted dark-on-light treatment miuix used.
     * The action inherits `couiColorPrimaryTextOnPopup` and sits on no container of its own.
     *
     * @param actionContainerColor retained for callers rendering a custom action; unused by the default
     *   layout, which draws the action as a plain label.
     */
    @Composable
    fun snackbarColors(
        containerColor: Color = if (isSystemInDarkTheme()) Color(0xFF333333) else Color(0xFFFFFFFF),
        contentColor: Color = if (isSystemInDarkTheme()) Color(0xE6FFFFFF) else Color(0xE6000000),
        actionContentColor: Color = if (isSystemInDarkTheme()) Color(0xE6FFFFFF) else Color(0xE6000000),
        dismissActionContentColor: Color = if (isSystemInDarkTheme()) Color(0x8AFFFFFF) else Color(0x8A000000),
        actionContainerColor: Color = Color.Transparent,
    ): SnackbarColors = remember(
        containerColor,
        contentColor,
        actionContentColor,
        dismissActionContentColor,
        actionContainerColor,
    ) {
        SnackbarColors(
            containerColor = containerColor,
            contentColor = contentColor,
            actionContentColor = actionContentColor,
            dismissActionContentColor = dismissActionContentColor,
            actionContainerColor = actionContainerColor,
        )
    }
}
