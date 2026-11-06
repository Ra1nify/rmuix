// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.util.fastRoundToInt
import io.github.ra1nify.rmuix.kmp.squircle.squircleBackground
import io.github.ra1nify.rmuix.kmp.squircle.squircleBorder
import io.github.ra1nify.rmuix.kmp.squircle.squircleClip
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.overScrollHorizontal

/**
 * A [TabRow] with Rmuix style.
 *
 * @param tabs The text to be displayed in the [TabRow].
 * @param selectedTabIndex The selected tab index of the [TabRow].
 * @param onTabSelected The callback when a tab is selected.
 * @param modifier The modifier to be applied to the [TabRow].
 * @param colors The colors of the [TabRow].
 * @param minWidth The minimum width of the tab in [TabRow].
 * @param maxWidth The maximum width of the tab in [TabRow].
 * @param height The height of the [TabRow].
 * @param cornerRadius The round corner radius of the tab in [TabRow].
 * @param itemSpacing The spacing between tabs in [TabRow].
 * @param contentAlignment The content alignment of the tab in [TabRow].
 * @param listState The [LazyListState] to be used for the [TabRow].
 * @param interactionSource The [MutableInteractionSource] to be used for the [TabRow].
 * @param indication The [Indication] to be used for the [TabRow].
 */
@Composable
fun TabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: TabRowColors = TabRowDefaults.tabRowColors(),
    minWidth: Dp = TabRowDefaults.TabRowMinWidth,
    maxWidth: Dp = TabRowDefaults.TabRowMaxWidth,
    height: Dp = TabRowDefaults.TabRowHeight,
    cornerRadius: Dp = TabRowDefaults.TabRowCornerRadius,
    itemSpacing: Dp = TabRowDefaults.ItemSpacing,
    contentAlignment: Alignment = Alignment.Center,
    listState: LazyListState? = null,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    val currentOnTabSelected by rememberUpdatedState(onTabSelected)
    val hapticFeedback = LocalHapticFeedback.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .height(height)
            .background(color = colors.backgroundColor(false)),
    ) {
        val config = rememberTabRowConfig(
            tabs = tabs,
            minWidth = minWidth,
            maxWidth = maxWidth,
            cornerRadius = cornerRadius,
            spacing = itemSpacing,
            lazyRowAvailableWidth = this.maxWidth,
            listState = listState,
        )
        val density = LocalDensity.current
        val tabWidthPx = with(density) { config.tabWidth.toPx() }
        val spacingPx = with(density) { itemSpacing.toPx() }
        val availableWidth = this.maxWidth
        var lastSettledSelectedTabIndex by remember(config.listState) { mutableIntStateOf(-1) }

        LaunchedEffect(selectedTabIndex, availableWidth, config.listState, config.tabWidth) {
            val centerOffset = (availableWidth - config.tabWidth) / 2
            val offsetPx = with(density) { -centerOffset.toPx() }.fastRoundToInt()
            if (lastSettledSelectedTabIndex < 0 || lastSettledSelectedTabIndex == selectedTabIndex) {
                config.listState.scrollToItem(selectedTabIndex, offsetPx)
            } else {
                config.listState.animateScrollToItem(selectedTabIndex, offsetPx)
            }
            lastSettledSelectedTabIndex = selectedTabIndex
        }

        val indicatorInset = TabRowDefaults.IndicatorInset
        val indicatorHeight = TabRowDefaults.IndicatorHeight

        val scrollOffset by remember {
            derivedStateOf {
                val state = config.listState
                val firstIndex = state.firstVisibleItemIndex
                val firstOffset = state.firstVisibleItemScrollOffset
                firstIndex * (tabWidthPx + spacingPx) + firstOffset
            }
        }

        val hasScrollRange by remember(config.listState) {
            derivedStateOf { config.listState.canScrollBackward || config.listState.canScrollForward }
        }
        val reverseScrolling by remember(config.listState) {
            derivedStateOf { config.listState.layoutInfo.reverseLayout }
        }
        val reverseDirection = ScrollableDefaults.reverseDirection(
            layoutDirection = LocalLayoutDirection.current,
            orientation = Orientation.Horizontal,
            reverseScrolling = reverseScrolling,
        )
        val nestedScrollConnection = remember(config.listState, reverseDirection) {
            TabRowNestedScrollConnection(config.listState, reverseDirection)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
                .overScrollHorizontal(
                    nestedScrollToParent = false,
                    isEnabled = { config.listState.canScrollBackward || config.listState.canScrollForward },
                ),
        ) {
            // ColorOS marks selection with a 2dp underline rather than a filled block: the line spans the
            // tab minus IndicatorInset on both sides and is pinned to the bottom of the row.
            Box(
                Modifier
                    .offset { IntOffset(((selectedTabIndex * (tabWidthPx + spacingPx)) - scrollOffset).fastRoundToInt(), 0) }
                    .width(config.tabWidth)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Box(
                    Modifier
                        .padding(horizontal = indicatorInset)
                        .fillMaxWidth()
                        .height(indicatorHeight)
                        .background(color = colors.indicatorColor()),
                )
            }
            LazyRow(
                state = config.listState,
                modifier = Modifier
                    .fillMaxSize()
                    // ColorOS fades the scrollable edges (tablayout_fade_edge_length = 58dp) so tabs
                    // sliding under the boundary dissolve instead of being cut.
                    .horizontalFadeEdge(TabRowDefaults.FadeEdgeLength, hasScrollRange)
                    // Announce "tab X of Y": this explicit count overrides LazyRow's auto-derived
                    // collectionInfo (the leftmost semantics in the chain wins the property).
                    .semantics { collectionInfo = CollectionInfo(rowCount = 1, columnCount = tabs.size) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                overscrollEffect = null,
                userScrollEnabled = hasScrollRange,
            ) {
                itemsIndexed(tabs) { index, tabText ->
                    TabItem(
                        text = tabText,
                        index = index,
                        isSelected = selectedTabIndex == index,
                        onClick = {
                            // ColorOS fires haptics on tab change (couiTabEnableVibrator), not when
                            // re-tapping the already-selected tab.
                            if (selectedTabIndex != index) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            }
                            currentOnTabSelected.invoke(index)
                        },
                        cornerRadius = config.cornerRadius,
                        width = config.tabWidth,
                        color = colors.contentColor(selectedTabIndex == index),
                        contentAlignment = contentAlignment,
                        interactionSource = interactionSource,
                        indication = indication,
                    )
                }
            }
        }
    }
}

/**
 * A [TabRowWithContour] with Rmuix style.
 *
 * @param tabs The text to be displayed in the [TabRow].
 * @param selectedTabIndex The selected tab index of the [TabRow].
 * @param onTabSelected The callback when a tab is selected.
 * @param modifier The modifier to be applied to the [TabRow].
 * @param colors The colors of the [TabRow].
 * @param minWidth The minimum width of the tab in [TabRow].
 * @param maxWidth The maximum width of the tab in [TabRow].
 * @param height The height of the [TabRow].
 * @param cornerRadius The round corner radius of the tab in [TabRow].
 * @param itemSpacing The spacing between tabs in [TabRow].
 * @param contentAlignment The content alignment of the tab in [TabRow].
 * @param listState The [LazyListState] to be used for the [TabRow].
 * @param interactionSource The [MutableInteractionSource] to be used for the [TabRow].
 * @param indication The [Indication] to be used for the [TabRow].
 */
@Composable
fun TabRowWithContour(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: TabRowColors = TabRowDefaults.tabRowColors(),
    minWidth: Dp = TabRowDefaults.TabRowWithContourMinWidth,
    maxWidth: Dp = TabRowDefaults.TabRowWithContourMaxWidth,
    height: Dp = TabRowDefaults.TabRowWithContourHeight,
    cornerRadius: Dp = TabRowDefaults.TabRowWithContourCornerRadius,
    itemSpacing: Dp = 5.dp,
    contentAlignment: Alignment = Alignment.Center,
    listState: LazyListState? = null,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    val currentOnTabSelected by rememberUpdatedState(onTabSelected)
    val contourPadding = 5.dp
    val outerCornerRadius = cornerRadius + contourPadding

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .height(height),
    ) {
        val lazyRowAvailableWidth = this.maxWidth - (contourPadding * 2)
        val config = rememberTabRowConfig(
            tabs = tabs,
            minWidth = minWidth,
            maxWidth = maxWidth,
            cornerRadius = cornerRadius,
            spacing = itemSpacing,
            lazyRowAvailableWidth = lazyRowAvailableWidth,
            listState = listState,
        )
        val density = LocalDensity.current
        val tabWidthPx = with(density) { config.tabWidth.toPx() }
        val spacingPx = with(density) { itemSpacing.toPx() }
        val indicatorOffset = remember { Animatable(0f) }
        val availableWidth = this.maxWidth
        var lastSettledSelectedTabIndex by remember(config.listState) { mutableIntStateOf(-1) }

        LaunchedEffect(selectedTabIndex, tabWidthPx, spacingPx) {
            val target = selectedTabIndex * (tabWidthPx + spacingPx)
            if (lastSettledSelectedTabIndex < 0 || lastSettledSelectedTabIndex == selectedTabIndex) {
                indicatorOffset.snapTo(target)
            } else {
                indicatorOffset.animateTo(target, tween(200, easing = LinearEasing))
            }
        }

        LaunchedEffect(selectedTabIndex, availableWidth, config.listState, config.tabWidth) {
            val centerOffset = (availableWidth - (contourPadding * 2) - config.tabWidth) / 2
            val offsetPx = with(density) { -centerOffset.toPx() }.fastRoundToInt()
            if (lastSettledSelectedTabIndex < 0 || lastSettledSelectedTabIndex == selectedTabIndex) {
                config.listState.scrollToItem(selectedTabIndex, offsetPx)
            } else {
                config.listState.animateScrollToItem(selectedTabIndex, offsetPx)
            }
            lastSettledSelectedTabIndex = selectedTabIndex
        }

        val indicatorInset = TabRowDefaults.IndicatorInset
        val indicatorHeight = TabRowDefaults.IndicatorHeight

        val scrollOffset by remember {
            derivedStateOf {
                val state = config.listState
                val firstIndex = state.firstVisibleItemIndex
                val firstOffset = state.firstVisibleItemScrollOffset
                firstIndex * (tabWidthPx + spacingPx) + firstOffset
            }
        }

        val hasScrollRange by remember(config.listState) {
            derivedStateOf { config.listState.canScrollBackward || config.listState.canScrollForward }
        }
        val reverseScrolling by remember(config.listState) {
            derivedStateOf { config.listState.layoutInfo.reverseLayout }
        }
        val reverseDirection = ScrollableDefaults.reverseDirection(
            layoutDirection = LocalLayoutDirection.current,
            orientation = Orientation.Horizontal,
            reverseScrolling = reverseScrolling,
        )
        val nestedScrollConnection = remember(config.listState, reverseDirection) {
            TabRowNestedScrollConnection(config.listState, reverseDirection)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .squircleBackground(color = colors.backgroundColor(false), cornerRadius = outerCornerRadius)
                .padding(contourPadding)
                .nestedScroll(nestedScrollConnection)
                .overScrollHorizontal(
                    nestedScrollToParent = false,
                    isEnabled = { config.listState.canScrollBackward || config.listState.canScrollForward },
                ),
        ) {
            Box(
                Modifier
                    .offset { IntOffset((indicatorOffset.value - scrollOffset).fastRoundToInt(), 0) }
                    .width(config.tabWidth)
                    .fillMaxHeight()
                    .squircleBackground(color = colors.backgroundColor(true), cornerRadius = config.cornerRadius),
            )
            LazyRow(
                state = config.listState,
                modifier = Modifier
                    .fillMaxSize()
                    // Announce "tab X of Y": this explicit count overrides LazyRow's auto-derived
                    // collectionInfo (the leftmost semantics in the chain wins the property).
                    .semantics { collectionInfo = CollectionInfo(rowCount = 1, columnCount = tabs.size) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                overscrollEffect = null,
                userScrollEnabled = hasScrollRange,
            ) {
                itemsIndexed(tabs) { index, tabText ->
                    TabItemWithContour(
                        text = tabText,
                        index = index,
                        isSelected = selectedTabIndex == index,
                        onClick = { currentOnTabSelected.invoke(index) },
                        cornerRadius = config.cornerRadius,
                        width = config.tabWidth,
                        color = colors.contentColor(selectedTabIndex == index),
                        contentAlignment = contentAlignment,
                        interactionSource = interactionSource,
                        indication = indication,
                    )
                }
            }
        }
    }
}

private class TabRowNestedScrollConnection(
    private val listState: LazyListState,
    private val reverseDirection: Boolean,
) : NestedScrollConnection {
    private fun canConsume(delta: Float): Boolean {
        val logicalDelta = if (reverseDirection) -delta else delta
        return when {
            logicalDelta > 0f -> listState.canScrollForward
            logicalDelta < 0f -> listState.canScrollBackward
            else -> false
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset = if (canConsume(available.x)) Offset(available.x, 0f) else Offset.Zero

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity,
    ): Velocity = if (canConsume(available.x)) Velocity(available.x, 0f) else Velocity.Zero
}

/**
 * Fades the horizontal edges of a scrollable so content dissolves at the boundary instead of clipping.
 *
 * Mirrors ColorOS `android:fadingEdge="horizontal"` on its tab layout. Uses DstIn so the content is
 * genuinely masked rather than painted over with a solid colour - which matters here because the row
 * sits on a translucent container.
 */
private fun Modifier.horizontalFadeEdge(fadeLength: Dp, enabled: Boolean): Modifier = if (!enabled) {
    this
} else {
    this.drawWithContent {
        val fadePx = fadeLength.toPx().coerceAtMost(size.width / 2f)
        drawContent()
        drawRect(
            brush = Brush.horizontalGradient(
                0f to Color.Black,
                1f to Color.Transparent,
                startX = 0f,
                endX = fadePx,
            ),
            blendMode = BlendMode.DstIn,
        )
        drawRect(
            brush = Brush.horizontalGradient(
                0f to Color.Transparent,
                1f to Color.Black,
                startX = size.width - fadePx,
                endX = size.width,
            ),
            blendMode = BlendMode.DstIn,
        )
    }
}

@Composable
private fun TabItem(
    text: String,
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    cornerRadius: Dp,
    width: Dp,
    color: Color = Color.Unspecified,
    contentAlignment: Alignment = Alignment.Center,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            // ColorOS tabs are plain rectangles: no outline on the unselected state, and the selection
            // is carried by the underline plus the text colour rather than by a corner radius.
            .then(if (indication != null) Modifier.squircleClip(cornerRadius) else Modifier)
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = indication,
            )
            .semantics {
                collectionItemInfo = CollectionItemInfo(
                    rowIndex = 0,
                    rowSpan = 1,
                    columnIndex = index,
                    columnSpan = 1,
                )
            }
            .padding(horizontal = 12.dp),
        contentAlignment = contentAlignment,
    ) {
        Text(
            text = text,
            color = color,
            // ColorOS distinguishes the selected tab by colour only - no weight change.
            fontWeight = FontWeight.Normal,
            fontSize = RmuixTheme.textStyles.body1.fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TabItemWithContour(
    text: String,
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    cornerRadius: Dp,
    width: Dp,
    color: Color = Color.Unspecified,
    contentAlignment: Alignment = Alignment.Center,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            // Clip only when an indication is supplied, to skip squircleClip's offscreen layer by default.
            .then(if (indication != null) Modifier.squircleClip(cornerRadius) else Modifier)
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = indication,
            )
            .semantics {
                collectionItemInfo = CollectionItemInfo(
                    rowIndex = 0,
                    rowSpan = 1,
                    columnIndex = index,
                    columnSpan = 1,
                )
            },
        contentAlignment = contentAlignment,
    ) {
        Text(
            text = text,
            color = color,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = RmuixTheme.textStyles.body2.fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Base configuration for TabRow implementations.
 */
private data class TabRowConfig(
    val tabWidth: Dp,
    val cornerRadius: Dp,
    val listState: LazyListState,
)

/**
 * Prepare common TabRow configuration.
 * @param lazyRowAvailableWidth The actual width available for the LazyRow's content (tabs + inter-tab spacing).
 */
@Composable
private fun rememberTabRowConfig(
    tabs: List<String>,
    minWidth: Dp,
    maxWidth: Dp,
    cornerRadius: Dp,
    spacing: Dp,
    lazyRowAvailableWidth: Dp,
    listState: LazyListState? = null,
): TabRowConfig {
    val resolvedListState = listState ?: rememberLazyListState()
    val tabWidth = remember(tabs.size, minWidth, maxWidth, lazyRowAvailableWidth, spacing) {
        calculateTabWidth(tabs.size, minWidth, maxWidth, spacing, lazyRowAvailableWidth)
    }
    return TabRowConfig(tabWidth, cornerRadius, resolvedListState)
}

private fun calculateTabWidth(
    tabCount: Int,
    minWidth: Dp,
    maxWidth: Dp,
    spacing: Dp,
    availableWidth: Dp,
): Dp {
    if (tabCount == 0) return minWidth

    val totalSpacing = if (tabCount > 1) (tabCount - 1) * spacing else 0.dp
    val contentWidth = availableWidth - totalSpacing

    return if (contentWidth <= 0.dp) {
        minWidth
    } else {
        val idealWidth = contentWidth / tabCount
        when {
            idealWidth < minWidth -> minWidth

            idealWidth > maxWidth -> {
                val totalMaxWidth = maxWidth * tabCount + totalSpacing
                if (totalMaxWidth < availableWidth) {
                    idealWidth
                } else {
                    maxWidth
                }
            }

            else -> idealWidth
        }
    }
}

object TabRowDefaults {

    /**
     * The default height of the [TabRow].
     */
    val TabRowHeight = 40.dp

    /**
     * The default height of the [TabRowWithContour].
     */
    val TabRowWithContourHeight = 45.dp

    /**
     * The default corner radius of the [TabRow].
     */
    val TabRowCornerRadius = 0.dp

    /**
     * Underline thickness: ColorOS `tablayout_selected_indicator_height` = 2dp.
     */
    val IndicatorHeight = 2.dp

    /**
     * Inset of the underline from each tab edge: ColorOS `tablayout_background_padding` = 24dp.
     *
     * The line therefore spans the label width plus this inset on both sides, which is why it reads as
     * sitting under the text rather than spanning the whole tab.
     */
    val IndicatorInset = 24.dp

    /**
     * Gap between tabs: ColorOS `tablayout_large_min_divider` = 16dp.
     */
    val ItemSpacing = 16.dp

    /**
     * Width of the horizontal fading edge: ColorOS `tablayout_fade_edge_length` = 58dp.
     */
    val FadeEdgeLength = 58.dp

    /**
     * The default corner radius of the [TabRowWithContour].
     */
    val TabRowWithContourCornerRadius = 8.dp

    /**
     * The default minimum width of the [TabRow].
     */
    val TabRowMinWidth = 72.dp

    /**
     * The default minimum width of the [TabRowWithContour].
     */
    val TabRowWithContourMinWidth = 62.dp

    /**
     * The default maximum width of the tab in [TabRow].
     */
    val TabRowMaxWidth = 128.dp

    /**
     * The default minimum width of the tab in [TabRowWithContour].
     */
    val TabRowWithContourMaxWidth = 84.dp

    /**
     * The default colors for the [TabRow] and [TabRowWithContour].
     */
    @Composable
    fun tabRowColors(
        backgroundColor: Color = RmuixTheme.colorScheme.surface,
        contentColor: Color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
        selectedBackgroundColor: Color = Color.Transparent,
        selectedContentColor: Color = RmuixTheme.colorScheme.onBackground,
        indicatorColor: Color = RmuixTheme.colorScheme.onBackground,
    ): TabRowColors = remember(
        backgroundColor,
        contentColor,
        selectedBackgroundColor,
        selectedContentColor,
        indicatorColor,
    ) {
        TabRowColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            selectedBackgroundColor = selectedBackgroundColor,
            selectedContentColor = selectedContentColor,
            indicatorColor = indicatorColor,
        )
    }
}

@Immutable
data class TabRowColors(
    private val backgroundColor: Color,
    private val contentColor: Color,
    private val selectedBackgroundColor: Color,
    private val selectedContentColor: Color,
    private val indicatorColor: Color,
) {
    @Stable
    internal fun backgroundColor(selected: Boolean): Color = if (selected) selectedBackgroundColor else backgroundColor

    @Stable
    internal fun contentColor(selected: Boolean): Color = if (selected) selectedContentColor else contentColor

    @Stable
    internal fun indicatorColor(): Color = indicatorColor
}
