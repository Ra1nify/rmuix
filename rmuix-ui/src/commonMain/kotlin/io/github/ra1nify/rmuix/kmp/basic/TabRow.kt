// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.takeOrElse
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
import androidx.compose.ui.text.rememberTextMeasurer
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
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectColor
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectSpring
import kotlin.math.max

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
    enabled: Boolean = true,
) {
    val currentOnTabSelected by rememberUpdatedState(onTabSelected)
    val hapticFeedback = LocalHapticFeedback.current
    val dark = isTabRowInDarkTheme()

    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxWidth()
                .then(modifier)
                .height(height)
                .background(color = colors.backgroundColor(false)),
    ) {
        // `COUISlidingTabStrip.parseMinDivider()`: `coui_tab_layout_small_tab_spacing` (20dp) below the
        // medium window width class, `coui_tab_layout_medium_tab_spacing` (24dp) from 600dp up. A caller
        // that passes its own spacing keeps it.
        val effectiveItemSpacing =
            if (itemSpacing == TabRowDefaults.ItemSpacing && this.maxWidth >= TabRowStockMediumWindowWidth) {
                TabRowDefaults.MediumItemSpacing
            } else {
                itemSpacing
            }
        val config =
            rememberTabRowConfig(
                tabs = tabs,
                minWidth = minWidth,
                maxWidth = maxWidth,
                cornerRadius = cornerRadius,
                spacing = effectiveItemSpacing,
                lazyRowAvailableWidth = this.maxWidth,
                listState = listState,
            )
        val density = LocalDensity.current
        val tabWidthPx = with(density) { config.tabWidth.toPx() }
        val spacingPx = with(density) { effectiveItemSpacing.toPx() }
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
        // `COUISlidingTabStrip.updateIndicatorPosition()` spans the selected TextView (+ its padding,
        // which is 0 by default) and `setIndicatorPosition()` then widens it to at least 32dp, centred.
        // Measure the label with the selected (Medium) typeface so the underline matches the stock text.
        val textMeasurer = rememberTextMeasurer()
        val selectedTabText = tabs.getOrNull(selectedTabIndex).orEmpty()
        val indicatorTextStyle = RmuixTheme.textStyles.body1.copy(fontWeight = FontWeight.Medium)
        val indicatorWidthPx =
            remember(selectedTabText, indicatorTextStyle, textMeasurer, density) {
                val textWidthPx = textMeasurer.measure(selectedTabText, indicatorTextStyle, maxLines = 1).size.width
                max(textWidthPx.toFloat(), with(density) { TabRowDefaults.MinIndicatorWidth.toPx() })
            }
        val indicatorWidth = with(density) { indicatorWidthPx.toDp() }

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
        val reverseDirection =
            ScrollableDefaults.reverseDirection(
                layoutDirection = LocalLayoutDirection.current,
                orientation = Orientation.Horizontal,
                reverseScrolling = reverseScrolling,
            )
        val nestedScrollConnection =
            remember(config.listState, reverseDirection) {
                TabRowNestedScrollConnection(config.listState, reverseDirection)
            }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection),
        ) {
            // `couiTabIndicatorBackgroundColor`: a static 2dp track under the moving indicator,
            // inset by the same 24dp (`tablayout_background_padding`) as the indicator itself.
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = indicatorInset)
                    .height(indicatorHeight)
                    .background(color = colors.indicatorTrackColor(tabRowIndicatorTrack(dark))),
            )
            // ColorOS marks selection with a 2dp underline rather than a filled block: the line spans the
            // selected label (min 32dp) centred over it and is pinned to the bottom of the row.
            Box(
                Modifier
                    // `Modifier.offset` mirrors the x offset in RTL (`placeRelativeWithLayer`), so the
                    // inline-start offset is correct for both directions without any manual flip.
                    .offset {
                        IntOffset(
                            (
                                selectedTabIndex * (tabWidthPx + spacingPx) +
                                    (tabWidthPx - indicatorWidthPx) / 2f -
                                    scrollOffset
                            ).fastRoundToInt(),
                            0,
                        )
                    }.width(indicatorWidth)
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(indicatorHeight)
                        .background(color = colors.indicatorColor(rowEnabled = enabled)),
                )
            }
            LazyRow(
                state = config.listState,
                modifier =
                    Modifier
                        .fillMaxSize()
                        // ColorOS fades the scrollable edges (tablayout_fade_edge_length = 58dp) so tabs
                        // sliding under the boundary dissolve instead of being cut.
                        .horizontalFadeEdge(TabRowDefaults.FadeEdgeLength, hasScrollRange)
                        // Announce "tab X of Y": this explicit count overrides LazyRow's auto-derived
                        // collectionInfo (the leftmost semantics in the chain wins the property).
                        .semantics { collectionInfo = CollectionInfo(rowCount = 1, columnCount = tabs.size) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(effectiveItemSpacing),
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
                        colors = colors,
                        rowEnabled = enabled,
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
 * Fades the horizontal edges of a scrollable so content dissolves at the boundary instead of clipping.
 *
 * Mirrors ColorOS `android:fadingEdge="horizontal"` on its tab layout. Uses DstIn so the content is
 * genuinely masked rather than painted over with a solid colour - which matters here because the row
 * sits on a translucent container.
 */
private fun Modifier.horizontalFadeEdge(
    fadeLength: Dp,
    enabled: Boolean,
): Modifier =
    if (!enabled) {
        this
    } else {
        // `DstIn` applies to the whole destination, so without an offscreen layer the areas the two
        // gradients do not cover would be cleared as well - that erased the entire row instead of just
        // the edges.
        this
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                val fadePx = fadeLength.toPx().coerceAtMost(size.width / 2f)
                drawContent()
                // Each gradient is clamped to its own band: a `drawRect` spanning the whole node would
                // let the gradient's out-of-range edge (transparent) erase everything outside the band.
                drawRect(
                    brush =
                        Brush.horizontalGradient(
                            0f to Color.Black,
                            1f to Color.Transparent,
                            startX = 0f,
                            endX = fadePx,
                        ),
                    size = Size(fadePx, size.height),
                    blendMode = BlendMode.DstIn,
                )
                drawRect(
                    brush =
                        Brush.horizontalGradient(
                            0f to Color.Transparent,
                            1f to Color.Black,
                            startX = size.width - fadePx,
                            endX = size.width,
                        ),
                    topLeft = Offset(size.width - fadePx, 0f),
                    size = Size(fadePx, size.height),
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
    colors: TabRowColors,
    rowEnabled: Boolean = true,
    contentAlignment: Alignment = Alignment.Center,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
) {
    // `coui_tab_default_color` paints a pressed tab as `couiColorPrimaryNeutral` regardless of
    // selection. That needs a per-tab interaction source; a shared row-level source would light
    // every tab at once, so the press tint is only applied when the tab owns its source.
    val ownSource = remember { MutableInteractionSource() }
    val effectiveSource = interactionSource ?: ownSource
    val pressed by effectiveSource.collectIsPressedAsState()
    val hovered by effectiveSource.collectIsHoveredAsState()
    val focused by effectiveSource.collectIsFocusedAsState()
    val tint =
        when {
            interactionSource == null && pressed && rowEnabled -> colors.pressedContentColor()
            else -> colors.contentColor(selected = isSelected, rowEnabled = rowEnabled)
        }
    // `COUITabView.setMaskRect(mTabRect, dpToPx(8), dpToPx(8))` paints the row background and the
    // press / hover / focus layers as one 8dp-rounded mask behind the label.
    val dark = isTabRowInDarkTheme()
    val maskSpring = remember { stockStateEffectSpring() }
    val pressMask by
        animateFloatAsState(
            targetValue = if (pressed && rowEnabled) 1f else 0f,
            animationSpec = maskSpring,
            label = "tabPressMask",
        )
    val hoverMask by
        animateFloatAsState(
            targetValue = if (hovered && rowEnabled) 1f else 0f,
            animationSpec = maskSpring,
            label = "tabHoverMask",
        )
    val focusMask by
        animateFloatAsState(
            targetValue = if (focused && rowEnabled) 1f else 0f,
            animationSpec = maskSpring,
            label = "tabFocusMask",
        )
    val primary = RmuixTheme.colorScheme.primary
    Box(
        modifier =
            Modifier
                .fillMaxHeight()
                .width(width)
                .drawBehind {
                    val corner = CornerRadius(tabRowPressMaskCornerRadius.toPx())
                    drawRoundRect(color = colors.backgroundColor(isSelected), cornerRadius = corner)
                    if (hoverMask > 0f) {
                        drawRoundRect(
                            color = stockStateEffectColor(tabRowHover(dark), hoverMask),
                            cornerRadius = corner,
                        )
                    }
                    if (pressMask > 0f) {
                        drawRoundRect(
                            color = stockStateEffectColor(tabRowPress(dark), pressMask),
                            cornerRadius = corner,
                        )
                    }
                    if (focusMask > 0f) {
                        drawRoundRect(
                            color = stockStateEffectColor(tabRowFocus(primary, dark), focusMask),
                            cornerRadius = corner,
                        )
                    }
                }
                // ColorOS tabs are plain rectangles: no outline on the unselected state, and the selection
                // is carried by the underline plus the text colour rather than by a corner radius.
                .then(if (indication != null) Modifier.squircleClip(cornerRadius) else Modifier)
                .selectable(
                    selected = isSelected,
                    onClick = onClick,
                    role = Role.Tab,
                    interactionSource = effectiveSource,
                    indication = indication,
                ).semantics {
                    collectionItemInfo =
                        CollectionItemInfo(
                            rowIndex = 0,
                            rowSpan = 1,
                            columnIndex = index,
                            columnSpan = 1,
                        )
                }.padding(horizontal = 12.dp),
        contentAlignment = contentAlignment,
    ) {
        Text(
            text = text,
            color = tint,
            // `COUITabView.setSelected()` swaps `mNormalTypeface` (regular) for `mSelectedTypeface`
            // (`Typeface.create("sans-serif-medium", 0)`), so the selected label is medium weight.
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            fontSize = RmuixTheme.textStyles.body1.fontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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
    val tabWidth =
        remember(tabs.size, minWidth, maxWidth, lazyRowAvailableWidth, spacing) {
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
            idealWidth < minWidth -> {
                minWidth
            }

            idealWidth > maxWidth -> {
                val totalMaxWidth = maxWidth * tabCount + totalSpacing
                if (totalMaxWidth < availableWidth) {
                    idealWidth
                } else {
                    maxWidth
                }
            }

            else -> {
                idealWidth
            }
        }
    }
}

object TabRowDefaults {
    /**
     * The default height of the [TabRow].
     */
    val TabRowHeight = 40.dp

    /**
     * The default corner radius of the [TabRow].
     */
    val TabRowCornerRadius = 0.dp

    /**
     * Underline thickness: ColorOS `tablayout_selected_indicator_height` = 2dp.
     */
    val IndicatorHeight = 2.dp

    /**
     * Inset of the static indicator track from each row edge: `couiTabIndicatorBackgroundPaddingLeft` /
     * `Right` are both `tablayout_background_padding` = 24dp. The moving underline is inset 0 and spans the
     * selected label instead.
     */
    val IndicatorInset = 24.dp

    /**
     * `COUISlidingTabStrip.setIndicatorPosition()`: the underline is centred on the selected label and
     * widened to at least `Math.max(width, dpToPx(32))` (`DEFAULT_MIN_INDICATOR`).
     */
    val MinIndicatorWidth = 32.dp

    /**
     * Gap between tabs below the medium window width class: `coui_tab_layout_small_tab_spacing` = 20dp
     * (the base style leaves `couiTabMinDivider` unset, so `parseMinDivider()` picks this responsively).
     */
    val ItemSpacing = 20.dp

    /** Gap between tabs from 600dp up: `coui_tab_layout_medium_tab_spacing` = 24dp. */
    val MediumItemSpacing = 24.dp

    /**
     * Width of the horizontal fading edge: ColorOS `tablayout_fade_edge_length` = 58dp.
     */
    val FadeEdgeLength = 58.dp

    /**
     * The default minimum width of the [TabRow].
     */
    val TabRowMinWidth = 72.dp

    /**
     * The default maximum width of the tab in [TabRow].
     */
    val TabRowMaxWidth = 128.dp

    /**
     * The default colors for the [TabRow].
     */
    @Composable
    fun tabRowColors(
        backgroundColor: Color = tabRowBackground(isTabRowInDarkTheme()),
        contentColor: Color = tabRowIdleContent(isTabRowInDarkTheme()),
        selectedBackgroundColor: Color = Color.Transparent,
        selectedContentColor: Color = tabRowSelectedContent(isTabRowInDarkTheme()),
        indicatorColor: Color = tabRowIndicator(isTabRowInDarkTheme()),
        indicatorTrackColor: Color = tabRowIndicatorTrack(isTabRowInDarkTheme()),
        disabledIndicatorColor: Color = tabRowIndicatorDisabled(),
        disabledContentColor: Color = tabRowDisabledContent(isTabRowInDarkTheme()),
    ): TabRowColors =
        remember(
            backgroundColor,
            contentColor,
            selectedBackgroundColor,
            selectedContentColor,
            indicatorColor,
            indicatorTrackColor,
            disabledIndicatorColor,
            disabledContentColor,
        ) {
            TabRowColors(
                backgroundColor = backgroundColor,
                contentColor = contentColor,
                selectedBackgroundColor = selectedBackgroundColor,
                selectedContentColor = selectedContentColor,
                indicatorColor = indicatorColor,
                indicatorTrackColor = indicatorTrackColor,
                disabledIndicatorColor = disabledIndicatorColor,
                disabledContentColor = disabledContentColor,
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
    private val indicatorTrackColor: Color = Color.Unspecified,
    private val disabledIndicatorColor: Color = Color.Unspecified,
    private val disabledContentColor: Color = Color.Unspecified,
) {
    @Stable
    internal fun backgroundColor(selected: Boolean): Color = if (selected) selectedBackgroundColor else backgroundColor

    /** `coui_tab_default_color` paints a pressed tab as `couiColorPrimaryNeutral`, i.e. the selected colour. */
    @Stable
    internal fun pressedContentColor(): Color = selectedContentColor

    @Stable
    internal fun contentColor(selected: Boolean): Color = if (selected) selectedContentColor else contentColor

    /**
     * Content colour for the current row state.
     *
     * `COUITabLayout` builds `createColorStateList(defaultColor, mTabTextDisabledColor, selectedColor)`
     * with `mTabTextDisabledColor = ?attr/couiColorDisabledNeutral`, and `setEnabled(false)` disables every
     * tab view - so a disabled row resolves to that neutral regardless of selection.
     */
    @Stable
    internal fun contentColor(
        selected: Boolean,
        rowEnabled: Boolean,
    ): Color =
        if (!rowEnabled && disabledContentColor.isSpecified) {
            disabledContentColor
        } else {
            contentColor(selected)
        }

    @Stable
    internal fun indicatorColor(): Color = indicatorColor

    /**
     * The indicator colour for the current row state.
     *
     * `COUITabLayout.setEnabled(false)` swaps the moving indicator to `couiTabIndicatorDisableColor`
     * (`#FFD9D9D9`) and calls `enableTab(i, false)` on every tab, so disabling is row-level.
     */
    @Stable
    internal fun indicatorColor(rowEnabled: Boolean): Color =
        if (rowEnabled) {
            indicatorColor
        } else if (disabledIndicatorColor.isSpecified) {
            disabledIndicatorColor
        } else {
            // Keep the moving indicator visible if a caller overrides the colours without providing a
            // disabled colour.
            indicatorColor
        }

    /** Static track under the moving indicator; falls back to the stock hairline when unspecified. */
    @Stable
    internal fun indicatorTrackColor(fallback: Color): Color = indicatorTrackColor.takeOrElse { fallback }
}
