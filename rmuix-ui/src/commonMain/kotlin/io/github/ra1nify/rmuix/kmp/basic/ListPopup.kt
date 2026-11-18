// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.anim.SinOutEasing
import io.github.ra1nify.rmuix.kmp.squircle.addSquircleRect
import io.github.ra1nify.rmuix.kmp.squircle.isSquircleEnabled
import io.github.ra1nify.rmuix.kmp.utils.hairlinePx
import kotlin.math.abs
import kotlin.math.min

private fun PopupPositionProvider.Align.resolve(layoutDirection: LayoutDirection): PopupPositionProvider.Align {
    if (layoutDirection == LayoutDirection.Ltr) return this
    return when (this) {
        PopupPositionProvider.Align.Start -> PopupPositionProvider.Align.End
        PopupPositionProvider.Align.End -> PopupPositionProvider.Align.Start
        PopupPositionProvider.Align.TopStart -> PopupPositionProvider.Align.TopEnd
        PopupPositionProvider.Align.TopEnd -> PopupPositionProvider.Align.TopStart
        PopupPositionProvider.Align.BottomStart -> PopupPositionProvider.Align.BottomEnd
        PopupPositionProvider.Align.BottomEnd -> PopupPositionProvider.Align.BottomStart
    }
}

private const val MAX_ITEMS_FOR_WIDTH = 8
private const val MAX_ITEMS_FOR_HEIGHT = 8

/** Row geometry and the active row of the shared mask drawn by [ListPopupColumn]. */
@Stable
internal class ListPopupSharedMask {
    var rows: List<IntRect> by mutableStateOf(emptyList())
        private set
    var activeIndex: Int? by mutableStateOf(null)
    var pressed: Boolean by mutableStateOf(false)

    fun updateRows(newRows: List<IntRect>) {
        if (rows != newRows) rows = newRows
    }

    fun indexAt(y: Float): Int? = sharedMaskIndexAt(rows, y)
}

/**
 * Index of the row containing [y], or null outside every row.
 *
 * `COUITouchRecyclerView.applySharedMaskBoundsAt` resolves the target row the same way.
 */
internal fun sharedMaskIndexAt(
    rows: List<IntRect>,
    y: Float,
): Int? {
    for (i in rows.indices) {
        val row = rows[i]
        if (y >= row.top && y < row.bottom) return i
    }
    return null
}

/** Rows draw no ripple of their own: the shared mask is the popup's feedback. */
private object NoIndication : IndicationNodeFactory {
    private object EmptyNode : Modifier.Node()

    override fun create(interactionSource: InteractionSource): DelegatableNode = EmptyNode

    override fun equals(other: Any?): Boolean = other === this

    override fun hashCode(): Int = 0
}

/**
 * A column that automatically aligns the width to the widest item.
 *
 * @param content The items
 */
@Composable
fun ListPopupColumn(content: @Composable () -> Unit) {
    val scrollState = rememberScrollState()
    val sharedMask = remember { ListPopupSharedMask() }
    val maskColor by animateColorAsState(
        targetValue =
            when {
                sharedMask.activeIndex == null -> Color.Transparent
                sharedMask.pressed -> listPopupSharedMaskPressedColor(isListPopupInDarkTheme())
                else -> listPopupSharedMaskHoverColor(isListPopupInDarkTheme())
            },
        animationSpec = tween(durationMillis = 100),
        label = "listPopupSharedMaskColor",
    )
    val maskRect = remember { Animatable(Rect.Zero, Rect.VectorConverter) }
    val squircleEnabled = isSquircleEnabled()
    LaunchedEffect(sharedMask.activeIndex) {
        val row = sharedMask.activeIndex?.let { sharedMask.rows.getOrNull(it) }
        val target =
            row?.let {
                Rect(it.left.toFloat(), it.top.toFloat(), it.right.toFloat(), it.bottom.toFloat())
            } ?: Rect.Zero
        // Stock: `COUISpringAnimation` with bounce 0. The mask only ever slides vertically, so it uses
        // `MASK_SPRING_RESPONSE_VERTICAL_ONLY` = 0.15s: k = (2 * PI / 0.15)^2 ~= 1755.
        maskRect.animateTo(
            targetValue = target,
            animationSpec = spring(dampingRatio = 1f, stiffness = 1755f),
        )
    }

    val measurePolicy =
        remember {
            object : MeasurePolicy {
                override fun MeasureScope.measure(
                    measurables: List<Measurable>,
                    constraints: Constraints,
                ): MeasureResult {
                    val minPx = ListPopupStockMinWidth.roundToPx()
                    val maxPx = ListPopupStockMaxWidth.roundToPx()
                    val widthCount = min(MAX_ITEMS_FOR_WIDTH, measurables.size)
                    var maxIntrinsic = 0
                    for (i in 0 until widthCount) {
                        val w = measurables[i].maxIntrinsicWidth(constraints.maxHeight)
                        if (w > maxIntrinsic) maxIntrinsic = w
                    }
                    val parentMin = constraints.minWidth
                    val parentMax = constraints.maxWidth
                    val upper = maxOf(maxPx, parentMin).coerceAtMost(parentMax)
                    val lower = maxOf(minPx, parentMin).coerceAtMost(upper)
                    val listWidth = maxIntrinsic.coerceIn(lower, upper)

                    val childConstraints = constraints.copy(minWidth = listWidth, maxWidth = listWidth, minHeight = 0)

                    val placeables = ArrayList<Placeable>(measurables.size)
                    var listHeight = 0
                    for (i in measurables.indices) {
                        val p = measurables[i].measure(childConstraints)
                        placeables.add(p)
                        listHeight += p.height
                    }

                    val rowBounds = ArrayList<IntRect>(placeables.size)
                    var currentY = 0
                    for (i in placeables.indices) {
                        val p = placeables[i]
                        rowBounds.add(IntRect(0, currentY, listWidth, currentY + p.height))
                        currentY += p.height
                    }
                    sharedMask.updateRows(rowBounds)
                    return layout(listWidth, listHeight) {
                        var y = 0
                        for (i in placeables.indices) {
                            val p = placeables[i]
                            p.placeRelative(0, y)
                            y += p.height
                        }
                    }
                }

                override fun IntrinsicMeasureScope.minIntrinsicHeight(
                    measurables: List<IntrinsicMeasurable>,
                    width: Int,
                ): Int {
                    val minPx = ListPopupStockMinWidth.roundToPx()
                    val maxPx = ListPopupStockMaxWidth.roundToPx()
                    val widthCount = min(MAX_ITEMS_FOR_WIDTH, measurables.size)
                    var maxIntrinsic = 0
                    for (i in 0 until widthCount) {
                        val w = measurables[i].maxIntrinsicWidth(Int.MAX_VALUE)
                        if (w > maxIntrinsic) maxIntrinsic = w
                    }
                    val listWidth = maxIntrinsic.coerceIn(minPx, maxPx)

                    val heightCount = min(MAX_ITEMS_FOR_HEIGHT, measurables.size)
                    var height = 0
                    for (i in 0 until heightCount) {
                        height += measurables[i].minIntrinsicHeight(listWidth)
                    }
                    return height
                }
            }
        }

    Layout(
        // `COUITouchRecyclerView` replaces row ripples with one shared mask.
        content = {
            CompositionLocalProvider(LocalIndication provides NoIndication) {
                content()
            }
        },
        modifier =
            Modifier
                .focusGroup()
                .height(IntrinsicSize.Min)
                .verticalScroll(state = scrollState)
                .padding(vertical = ListPopupDefaults.VerticalContentPadding)
                .pointerInput(sharedMask) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val change = event.changes.firstOrNull() ?: continue
                            val index = sharedMask.indexAt(change.position.y)
                            when (event.type) {
                                PointerEventType.Press -> {
                                    sharedMask.activeIndex = index
                                    sharedMask.pressed = true
                                }

                                PointerEventType.Move -> {
                                    sharedMask.activeIndex = index
                                    sharedMask.pressed = event.changes.any { it.pressed }
                                }

                                PointerEventType.Release -> {
                                    sharedMask.activeIndex = index
                                    sharedMask.pressed = false
                                }

                                PointerEventType.Exit -> {
                                    sharedMask.activeIndex = null
                                }

                                else -> {
                                    // no-op
                                }
                            }
                        }
                    }
                }.drawBehind {
                    val rect = maskRect.value
                    if (rect.width > 0f && maskColor.alpha > 0f) {
                        val inset = ListPopupStockSharedMaskMarginHorizontal.toPx()
                        val radius = ListPopupStockSharedMaskRadius16_1.toPx()
                        val left = rect.left + inset
                        val width = (rect.width - inset * 2f).coerceAtLeast(0f)
                        if (width > 0f) {
                            // The stock mask is drawn through `OplusOutline.setSmoothRoundRect(radius,
                            // weight)`, whose smoothing is native; the squircle is this library's
                            // approximation of the same smooth corner.
                            if (squircleEnabled) {
                                val path = Path()
                                path.addSquircleRect(width, rect.height, radius, squircleEnabled = true)
                                path.translate(Offset(left, rect.top))
                                drawPath(path, maskColor)
                            } else {
                                drawRoundRect(
                                    color = maskColor,
                                    topLeft = Offset(left, rect.top),
                                    size = Size(width, rect.height),
                                    cornerRadius = CornerRadius(radius, radius),
                                )
                            }
                        }
                    }
                },
        measurePolicy = measurePolicy,
    )
}

@Stable
interface PopupPositionProvider {
    /**
     * Calculate the position (offset) of Popup
     *
     * @param anchorBounds Bounds of the anchored (parent) component
     * @param windowBounds Bounds of the safe area of window (excluding the [WindowInsets.Companion.statusBars],
     *   [WindowInsets.Companion.navigationBars] and [WindowInsets.Companion.captionBar])
     * @param layoutDirection [LayoutDirection]
     * @param popupContentSize Actual size of the popup content
     * @param popupMargin (Extra) Margins for the popup content. See [PopupPositionProvider.getMargins]
     * @param alignment Alignment of the popup (relative to the window). See [PopupPositionProvider.Align]
     */
    fun calculatePosition(
        anchorBounds: IntRect,
        windowBounds: IntRect,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
        popupMargin: IntRect,
        alignment: Align,
    ): IntOffset

    /**
     * (Extra) Margins for the popup content.
     */
    fun getMargins(): PaddingValues

    /**
     * Position relative to the window, not relative to the anchor!
     */
    enum class Align {
        Start,
        End,
        TopStart,
        TopEnd,
        BottomStart,
        BottomEnd,
    }
}

/**
 * The hairline the stock menu draws between rows: 0.5dp tall in `?attr/couiColorDivider`, inset by
 * 20dp on both sides — or 52dp on the start side when the rows carry icons.
 *
 * Mirrors `DefaultAdapter.getDefaultDividerView()` / `getDefaultDividerDrawable()`.
 *
 * @param modifier Modifier applied to the divider.
 * @param withIcon Whether the surrounding rows have leading icons.
 * @param color Hairline colour; defaults to the stock divider role for the current theme.
 */
@Composable
fun ListPopupDivider(
    modifier: Modifier = Modifier,
    withIcon: Boolean = false,
    color: Color = listPopupDividerColor(isListPopupInDarkTheme()),
) {
    val start =
        if (withIcon) ListPopupDefaults.DividerStartWithIcon else ListPopupDefaults.DividerMarginHorizontal
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                // `coui_popup_list_divider_height` is 0.5dp; at 1x density a 0.5dp box rounds to nothing,
                // so the stroke keeps the repo's hairline convention (at least one physical pixel).
                .height(ListPopupDefaults.DividerHeight)
                .padding(start = start, end = ListPopupDefaults.DividerMarginHorizontal)
                .drawBehind { drawRect(color, size = Size(size.width, hairlinePx(ListPopupDefaults.DividerHeight))) },
    )
}

/**
 * The block the stock menu inserts between groups: a full-width 4dp strip in
 * `coui_popup_list_group_divider_color` (`#14000000` in both themes).
 *
 * @param modifier Modifier applied to the strip.
 * @param color Strip colour; the stock value has no night variant.
 */
@Composable
fun ListPopupGroupDivider(
    modifier: Modifier = Modifier,
    color: Color = ListPopupDefaults.GroupDividerColor,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(ListPopupDefaults.GroupDividerHeight)
                .background(color = color),
    )
}

/**
 * The group divider of a mixed menu (`MixedPopupListAdapter`): a
 * `coui_popup_list_group_divider_total_height` **12dp** row whose
 * `coui_popup_list_divider_height` **0.5dp** line in `?attr/couiColorDivider` is inset by
 * `(12 - 0.5) / 2 = 5.75dp` vertically and `coui_popup_list_default_divider_margin_horizontal` (20dp)
 * horizontally. Unlike [ListPopupGroupDivider] (the 4dp `coui_popup_list_group_divider_color` strip), this
 * one is what the stock adapter inflates between menu groups when the list mixes item types.
 *
 * @param modifier The modifier to be applied to the row.
 * @param color The colour of the divider line.
 */
@Composable
fun ListPopupMixedGroupDivider(
    modifier: Modifier = Modifier,
    color: Color = listPopupDividerColor(isListPopupInDarkTheme()),
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(ListPopupDefaults.MixedGroupDividerTotalHeight)
                .drawBehind {
                    val left = ListPopupDefaults.DividerMarginHorizontal.toPx()
                    drawRect(
                        color = color,
                        topLeft = Offset(left, ListPopupDefaults.MixedGroupDividerVerticalInset.toPx()),
                        size = Size(size.width - left * 2f, hairlinePx(ListPopupDefaults.DividerHeight)),
                    )
                },
    )
}

object ListPopupDefaults {
    /** Default animation spec driving the scale/clip-reveal fraction progress of the popup. */
    val FractionAnimationSpec = spring(dampingRatio = 0.82f, stiffness = 362.5f, visibilityThreshold = 0.0001f)

    /** Default animation spec driving the popup content alpha while entering. */
    val AlphaEnterAnimationSpec = tween<Float>(durationMillis = 200)

    /** Default animation spec driving the popup content alpha while exiting. */
    val AlphaExitAnimationSpec = tween<Float>(durationMillis = 150)

    /** Default animation spec driving the background dim while entering. */
    val DimEnterAnimationSpec = tween<Float>(durationMillis = 300, easing = SinOutEasing)

    /** Default animation spec driving the background dim while exiting. */
    val DimExitAnimationSpec = tween<Float>(durationMillis = 150, easing = SinOutEasing)

    /** Default animation spec used to settle the popup back to its resting state after a gesture. */
    val ResetAnimationSpec = spring(dampingRatio = 0.82f, stiffness = 362.5f, visibilityThreshold = 0.0001f)

    /**
     * `coui_popup_list_padding_vertical`: 8dp of padding above and below the item list, applied by
     * `COUIPopupListWindow.applyMenuContainerPaddingForRecyclerView()`.
     */
    val VerticalContentPadding = ListPopupStockVerticalPadding

    /** `coui_popup_list_divider_height`: 0.5dp hairline between rows. */
    val DividerHeight = ListPopupStockDividerHeight

    /** `coui_popup_list_default_divider_margin_horizontal`: 20dp inset on both sides. */
    val DividerMarginHorizontal = ListPopupStockDividerMarginHorizontal

    /** `coui_popup_list_default_divider_margin_start_with_icon`: 52dp start inset for icon rows. */
    val DividerStartWithIcon = ListPopupStockDividerStartWithIcon

    /** `coui_popup_list_group_divider_height`: 4dp block between groups. */
    val GroupDividerHeight = ListPopupStockGroupDividerHeight

    /** `coui_popup_list_group_divider_color`: `#14000000` in both themes. */
    val GroupDividerColor = ListPopupStockGroupDividerColor

    /** `coui_popup_list_group_divider_total_height`: the mixed list's 12dp divider row. */
    val MixedGroupDividerTotalHeight = ListPopupStockMixedGroupDividerTotalHeight

    /** `(total height - 0.5dp line) / 2`: the mixed divider's 5.75dp vertical inset. */
    val MixedGroupDividerVerticalInset = ListPopupStockMixedGroupDividerVerticalInset

    /**
     * Default minimum width of the popup (`coui_popup_list_window_min_width` = 168dp). Also
     * matches the lower clamp inside [ListPopupColumn]'s width measurement.
     */
    val MinWidth = ListPopupStockMinWidth

    /**
     * Default maximum width of the popup (`coui_popup_list_window_max_width` = 256dp), i.e. the
     * upper clamp inside [ListPopupColumn]'s width measurement.
     */
    val MaxWidth = ListPopupStockMaxWidth

    /**
     * Default minimum height the popup will occupy when measured. Used as the floor for the
     * effective `maxHeight` and `minHeight` constraints during placement.
     */
    val MinPopupHeight = 50.dp

    /**
     * Creates a [PopupPositionProvider] that anchors the popup directly below (or above when there
     * is no room) the anchor, used by dropdown-style list popups.
     *
     * @param verticalMargin The extra vertical margin between the popup and the anchor.
     * @param horizontalMargin The extra horizontal margin applied to the popup.
     */
    fun dropdownPositionProvider(
        verticalMargin: Dp = 8.dp,
        horizontalMargin: Dp = 0.dp,
    ): PopupPositionProvider =
        object : PopupPositionProvider {
            private val margins = PaddingValues(horizontal = horizontalMargin, vertical = verticalMargin)

            override fun calculatePosition(
                anchorBounds: IntRect,
                windowBounds: IntRect,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize,
                popupMargin: IntRect,
                alignment: PopupPositionProvider.Align,
            ): IntOffset {
                val offsetX =
                    if (alignment.resolve(layoutDirection) == PopupPositionProvider.Align.End) {
                        anchorBounds.right - popupContentSize.width - popupMargin.right
                    } else {
                        anchorBounds.left + popupMargin.left
                    }
                val offsetY =
                    if (windowBounds.bottom - anchorBounds.bottom > popupContentSize.height) {
                        // Show below
                        anchorBounds.bottom + popupMargin.bottom
                    } else if (anchorBounds.top - windowBounds.top > popupContentSize.height) {
                        // Show above
                        anchorBounds.top - popupContentSize.height - popupMargin.top
                    } else {
                        // Middle
                        anchorBounds.top + anchorBounds.height / 2 - popupContentSize.height / 2
                    }
                return IntOffset(
                    x =
                        offsetX.coerceIn(
                            windowBounds.left,
                            (windowBounds.right - popupContentSize.width - popupMargin.right).coerceAtLeast(windowBounds.left),
                        ),
                    y =
                        offsetY.coerceIn(
                            (windowBounds.top + popupMargin.top).coerceAtMost(
                                windowBounds.bottom - popupContentSize.height - popupMargin.bottom,
                            ),
                            windowBounds.bottom - popupContentSize.height - popupMargin.bottom,
                        ),
                )
            }

            override fun getMargins(): PaddingValues = margins
        }

    /** Default dropdown [PopupPositionProvider] created by [dropdownPositionProvider]. */
    val DropdownPositionProvider: PopupPositionProvider = dropdownPositionProvider()

    /**
     * Position provider for a cascading sub-menu.
     *
     * `PopupMenuLocateHelper` has two sub-menu rules:
     *
     * - **compact windows** (`isCurrentContainerSmallScreen()` = `WindowTotalSizeClass.Compact`): the
     *   sub-menu top is the row top **minus** `coui_popup_list_window_vertical_overlap_between_main_and_sub_menu`
     *   (14dp);
     * - **every other window**: the sub-menu top is the row top, with no overlap at all.
     *
     * Horizontally both rules overlap by `coui_popup_list_window_horizontal_overlap_between_main_and_sub_menu`
     * (14dp): the sub-menu starts at `mainMenu.right - overlap`, and only when that would push it past the
     * available left edge does it flip to `mainMenu.left + overlap - subMenuWidth`.
     *
     * @param verticalOverlapPx The vertical overlap, already in pixels; 0 for non-compact windows.
     * @param horizontalOverlapPx The horizontal overlap, already in pixels.
     */
    fun cascadingSubmenuPositionProvider(
        verticalOverlapPx: Int,
        horizontalOverlapPx: Int = verticalOverlapPx,
    ): PopupPositionProvider =
        object : PopupPositionProvider {
            private val margins = PaddingValues(vertical = 0.dp)

            override fun calculatePosition(
                anchorBounds: IntRect,
                windowBounds: IntRect,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize,
                popupMargin: IntRect,
                alignment: PopupPositionProvider.Align,
            ): IntOffset {
                // LTR: `getOffsetX` of `mSubMenuLocateRule` - start at `mainMenu.right - overlap`, and only
                // flip to `mainMenu.left + overlap - subMenuWidth` when that would pass the left edge.
                // RTL mirrors it: start at `mainMenu.right - overlap` while it fits before the right edge,
                // else flip to `mainMenu.left + overlap - subMenuWidth`.
                val flippedX = anchorBounds.left + horizontalOverlapPx - popupContentSize.width
                val offsetX =
                    if (layoutDirection == LayoutDirection.Rtl) {
                        if (anchorBounds.right - horizontalOverlapPx + popupContentSize.width < windowBounds.right) {
                            anchorBounds.right - horizontalOverlapPx
                        } else {
                            flippedX
                        }
                    } else if (flippedX > windowBounds.left) {
                        flippedX
                    } else {
                        anchorBounds.right - horizontalOverlapPx
                    }
                val offsetY = anchorBounds.top - verticalOverlapPx
                return IntOffset(
                    x = offsetX.coerceIn(windowBounds.left, (windowBounds.right - popupContentSize.width).coerceAtLeast(windowBounds.left)),
                    y =
                        offsetY.coerceIn(
                            windowBounds.top + popupMargin.top,
                            (windowBounds.bottom - popupContentSize.height - popupMargin.bottom).coerceAtLeast(
                                windowBounds.top + popupMargin.top,
                            ),
                        ),
                )
            }

            override fun getMargins(): PaddingValues = margins
        }

    /** `coui_popup_list_window_horizontal_overlap_between_main_and_sub_menu` = 14dp. */
    val CascadingSubmenuHorizontalOverlap = 14.dp

    /** `coui_popup_list_window_vertical_overlap_between_main_and_sub_menu` = 14dp (compact windows only). */
    val CascadingSubmenuVerticalOverlap = 14.dp

    /**
     * Remembers [cascadingSubmenuPositionProvider].
     *
     * @param compactWindow Pass `true` on a compact window (`WindowTotalSizeClass.Compact`), the only case
     *   in which the stock helper lifts the sub-menu by the vertical overlap.
     */
    @Composable
    fun rememberCascadingSubmenuPositionProvider(compactWindow: Boolean = false): PopupPositionProvider {
        val density = LocalDensity.current
        val horizontalPx = with(density) { CascadingSubmenuHorizontalOverlap.roundToPx() }
        val verticalPx = if (compactWindow) with(density) { CascadingSubmenuVerticalOverlap.roundToPx() } else 0
        return remember(horizontalPx, verticalPx) {
            cascadingSubmenuPositionProvider(
                verticalOverlapPx = verticalPx,
                horizontalOverlapPx = horizontalPx,
            )
        }
    }

    /** A [PopupPositionProvider] that anchors the popup to a corner of the anchor for context menus. */
    val ContextMenuPositionProvider =
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowBounds: IntRect,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize,
                popupMargin: IntRect,
                alignment: PopupPositionProvider.Align,
            ): IntOffset {
                val offsetX: Int
                val offsetY: Int
                when (alignment.resolve(layoutDirection)) {
                    PopupPositionProvider.Align.TopStart -> {
                        offsetX = anchorBounds.left + popupMargin.left
                        offsetY = anchorBounds.bottom + popupMargin.top
                    }

                    PopupPositionProvider.Align.TopEnd -> {
                        offsetX = anchorBounds.right - popupContentSize.width - popupMargin.right
                        offsetY = anchorBounds.bottom + popupMargin.top
                    }

                    PopupPositionProvider.Align.BottomStart -> {
                        offsetX = anchorBounds.left + popupMargin.left
                        offsetY = anchorBounds.top - popupContentSize.height - popupMargin.bottom
                    }

                    PopupPositionProvider.Align.BottomEnd -> {
                        offsetX = anchorBounds.right - popupContentSize.width - popupMargin.right
                        offsetY = anchorBounds.top - popupContentSize.height - popupMargin.bottom
                    }

                    else -> {
                        // Fallback
                        offsetX =
                            if (alignment.resolve(layoutDirection) == PopupPositionProvider.Align.End) {
                                anchorBounds.right - popupContentSize.width - popupMargin.right
                            } else {
                                anchorBounds.left + popupMargin.left
                            }
                        offsetY =
                            if (windowBounds.bottom - anchorBounds.bottom > popupContentSize.height) {
                                // Show below
                                anchorBounds.bottom + popupMargin.bottom
                            } else if (anchorBounds.top - windowBounds.top > popupContentSize.height) {
                                // Show above
                                anchorBounds.top - popupContentSize.height - popupMargin.top
                            } else {
                                // Middle
                                anchorBounds.top + anchorBounds.height / 2 - popupContentSize.height / 2
                            }
                    }
                }
                return IntOffset(
                    x =
                        offsetX.coerceIn(
                            windowBounds.left,
                            (windowBounds.right - popupContentSize.width - popupMargin.right).coerceAtLeast(windowBounds.left),
                        ),
                    y =
                        offsetY.coerceIn(
                            (windowBounds.top + popupMargin.top).coerceAtMost(
                                windowBounds.bottom - popupContentSize.height - popupMargin.bottom,
                            ),
                            windowBounds.bottom - popupContentSize.height - popupMargin.bottom,
                        ),
                )
            }

            override fun getMargins(): PaddingValues = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
        }
}

internal fun safeTransformOrigin(
    x: Float,
    y: Float,
): TransformOrigin {
    val safeX = if (x.isNaN() || x < 0f) 0f else x
    val safeY = if (y.isNaN() || y < 0f) 0f else y
    return TransformOrigin(safeX, safeY)
}

/**
 * Describes how the popup is placed relative to its anchor, used to drive the directional reveal
 * and transform origin.
 *
 * @property showBelow Whether the popup is shown below the anchor.
 * @property showAbove Whether the popup is shown above the anchor.
 * @property isRightAligned Whether the popup is aligned to the right edge of the anchor.
 */
@Immutable
data class PopupLayoutPosition(
    val showBelow: Boolean,
    val showAbove: Boolean,
    val isRightAligned: Boolean,
)

/**
 * The resolved layout information for a list popup, produced by [rememberListPopupLayoutInfo].
 *
 * @property windowBounds Bounds of the safe area of the window the popup is placed within.
 * @property popupMargin The (extra) margins applied around the popup content.
 * @property effectiveTransformOrigin The transform origin in window coordinates used to scale the popup from its anchor corner.
 * @property localTransformOrigin The transform origin local to the popup content used by its [graphicsLayer] scaling.
 * @property popupLayoutPosition The resolved [PopupLayoutPosition] describing how the popup is placed relative to its anchor.
 */
@Immutable
data class ListPopupLayoutInfo(
    val windowBounds: IntRect,
    val popupMargin: IntRect,
    val effectiveTransformOrigin: TransformOrigin,
    val localTransformOrigin: TransformOrigin,
    val popupLayoutPosition: PopupLayoutPosition,
)

/**
 * Computes and remembers the [ListPopupLayoutInfo] for a list popup from its anchor and content size.
 *
 * @param alignment The [PopupPositionProvider.Align] of the popup relative to the window.
 * @param popupPositionProvider The [PopupPositionProvider] that computes the popup offset and margins.
 * @param parentBounds The bounds of the anchor (parent) component in window coordinates.
 * @param popupContentSize The measured size of the popup content; [IntSize.Zero] before it is measured.
 */
@Composable
fun rememberListPopupLayoutInfo(
    alignment: PopupPositionProvider.Align,
    popupPositionProvider: PopupPositionProvider,
    parentBounds: IntRect,
    popupContentSize: IntSize,
): ListPopupLayoutInfo {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val layoutDirection = LocalLayoutDirection.current
    val displayCutout = WindowInsets.displayCutout
    val statusBars = WindowInsets.statusBars
    val navigationBars = WindowInsets.navigationBars
    val captionBar = WindowInsets.captionBar

    val margins = popupPositionProvider.getMargins()
    val popupMargin =
        remember(layoutDirection, density, margins) {
            with(density) {
                IntRect(
                    left = margins.calculateLeftPadding(layoutDirection).roundToPx(),
                    top = margins.calculateTopPadding().roundToPx(),
                    right = margins.calculateRightPadding(layoutDirection).roundToPx(),
                    bottom = margins.calculateBottomPadding().roundToPx(),
                )
            }
        }

    val containerSize = windowInfo.containerSize

    val windowBounds =
        remember(
            layoutDirection,
            density,
            displayCutout,
            statusBars,
            navigationBars,
            captionBar,
            containerSize,
        ) {
            with(density) {
                IntRect(
                    left = displayCutout.getLeft(this, layoutDirection),
                    top = statusBars.getTop(this),
                    right = containerSize.width - displayCutout.getRight(this, layoutDirection),
                    bottom = containerSize.height - navigationBars.getBottom(this) - captionBar.getBottom(this),
                )
            }
        }

    val predictedTransformOrigin =
        remember(alignment, popupMargin, parentBounds, layoutDirection, containerSize) {
            val xInWindow =
                when (alignment.resolve(layoutDirection)) {
                    PopupPositionProvider.Align.End,
                    PopupPositionProvider.Align.TopEnd,
                    PopupPositionProvider.Align.BottomEnd,
                    -> parentBounds.right - popupMargin.right

                    else -> parentBounds.left + popupMargin.left
                }
            val yInWindow =
                when (alignment.resolve(layoutDirection)) {
                    PopupPositionProvider.Align.BottomEnd, PopupPositionProvider.Align.BottomStart -> {
                        parentBounds.top - popupMargin.bottom
                    }

                    else -> {
                        parentBounds.bottom + popupMargin.bottom
                    }
                }
            safeTransformOrigin(
                xInWindow / containerSize.width.toFloat(),
                yInWindow / containerSize.height.toFloat(),
            )
        }

    val calculatedOffset =
        remember(
            popupContentSize,
            windowBounds,
            parentBounds,
            alignment,
            layoutDirection,
            popupMargin,
            popupPositionProvider,
        ) {
            if (popupContentSize == IntSize.Zero) {
                IntOffset.Zero
            } else {
                popupPositionProvider.calculatePosition(
                    parentBounds,
                    windowBounds,
                    layoutDirection,
                    popupContentSize,
                    popupMargin,
                    alignment,
                )
            }
        }

    val popupLayoutPosition =
        remember(
            popupContentSize,
            windowBounds,
            parentBounds,
            alignment,
            calculatedOffset,
            layoutDirection,
        ) {
            if (popupContentSize == IntSize.Zero) {
                val isRightAligned =
                    when (alignment.resolve(layoutDirection)) {
                        PopupPositionProvider.Align.End,
                        PopupPositionProvider.Align.TopEnd,
                        PopupPositionProvider.Align.BottomEnd,
                        -> true

                        else -> false
                    }
                PopupLayoutPosition(showBelow = true, showAbove = false, isRightAligned = isRightAligned)
            } else {
                val popupCenterY = calculatedOffset.y + popupContentSize.height / 2
                val anchorCenterY = parentBounds.top + parentBounds.height / 2
                val showBelow = popupCenterY > anchorCenterY
                val showAbove = popupCenterY < anchorCenterY

                val distLeft = abs(calculatedOffset.x - parentBounds.left)
                val distRight = abs((calculatedOffset.x + popupContentSize.width) - parentBounds.right)
                val isRightAligned = distRight < distLeft

                PopupLayoutPosition(showBelow = showBelow, showAbove = showAbove, isRightAligned = isRightAligned)
            }
        }

    val effectiveTransformOrigin =
        remember(
            popupContentSize,
            calculatedOffset,
            popupLayoutPosition,
            containerSize,
            predictedTransformOrigin,
        ) {
            if (popupContentSize == IntSize.Zero) {
                predictedTransformOrigin
            } else {
                val (showBelow, showAbove, isRightAligned) = popupLayoutPosition
                val cornerX =
                    if (isRightAligned) {
                        (calculatedOffset.x + popupContentSize.width).toFloat()
                    } else {
                        calculatedOffset.x.toFloat()
                    }

                val showMiddle = !showBelow && !showAbove
                val topLeftY = calculatedOffset.y
                val cornerY =
                    when {
                        showMiddle -> (topLeftY + popupContentSize.height / 2f)
                        showBelow -> topLeftY.toFloat()
                        showAbove -> (topLeftY + popupContentSize.height).toFloat()
                        else -> topLeftY.toFloat()
                    }

                safeTransformOrigin(
                    cornerX / containerSize.width.toFloat(),
                    cornerY / containerSize.height.toFloat(),
                )
            }
        }

    val localTransformOrigin =
        remember(popupLayoutPosition) {
            val (showBelow, showAbove, isRightAligned) = popupLayoutPosition
            val showMiddle = !showBelow && !showAbove

            TransformOrigin(
                pivotFractionX = if (isRightAligned) 1f else 0f,
                pivotFractionY =
                    when {
                        showMiddle -> 0.5f
                        showBelow -> 0f
                        showAbove -> 1f
                        else -> 0f
                    },
            )
        }

    return ListPopupLayoutInfo(
        windowBounds = windowBounds,
        popupMargin = popupMargin,
        effectiveTransformOrigin = effectiveTransformOrigin,
        localTransformOrigin = localTransformOrigin,
        popupLayoutPosition = popupLayoutPosition,
    )
}

/**
 * The scaling, fading and clip-revealing container that hosts a list popup's content.
 *
 * @param popupContentSize The last reported size of the content, compared against the latest
 *   measurement to avoid redundant [onPopupContentSizeChange] callbacks.
 * @param onPopupContentSizeChange Called when the measured content size changes.
 * @param fractionProgress Provides the current scale/clip-reveal fraction (0 → 1) of the popup.
 * @param alphaProgress Provides the current alpha (0 → 1) of the popup content.
 * @param popupLayoutPosition The [PopupLayoutPosition] describing the popup's spawn direction.
 * @param localTransformOrigin The transform origin local to the content used while scaling.
 * @param modifier The modifier to be applied to the popup container.
 * @param content The content of the popup.
 */
@Composable
fun ListPopupContent(
    popupContentSize: IntSize,
    onPopupContentSizeChange: (IntSize) -> Unit,
    fractionProgress: () -> Float,
    alphaProgress: () -> Float,
    popupLayoutPosition: PopupLayoutPosition,
    localTransformOrigin: TransformOrigin,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    // `coui_popup_window_background.xml`: 12dp corners (`coui_round_corner_m`) filled with
    // `couiColorBackgroundTop` (`#FFFFFF` / `#333333`).
    val cornerRadius = ListPopupStockCornerRadius
    val backgroundColor = listPopupBackground(isListPopupInDarkTheme())

    Box(
        // The layer must precede the user modifier: draw modifiers before a graphicsLayer don't follow its transform (issue #373).
        modifier =
            Modifier
                .graphicsLayer {
                    val fraction = fractionProgress()
                    val scale = 0.15f + 0.85f * fraction
                    scaleX = scale
                    scaleY = scale
                    alpha = alphaProgress()
                    transformOrigin = localTransformOrigin
                }.then(modifier)
                .onGloballyPositioned { coordinates ->
                    val size = coordinates.size
                    if (popupContentSize != size) onPopupContentSizeChange(size)
                }.popupClipReveal(fractionProgress, popupLayoutPosition, cornerRadius, isSquircleEnabled())
                .background(color = backgroundColor),
    ) {
        content()
    }
}

/**
 * Directional clip-reveal used during popup enter/exit. The visible band grows along the popup's
 * spawn direction encoded by [popupLayoutPosition] as [fractionProgress] moves 0 → 1: from the top
 * when shown below the anchor, from the bottom when shown above, and outwards from the center
 * otherwise. The band itself is shaped as a squircle (via [addSquircleRect]) so the four corners
 * stay aligned with the surrounding [squircleSurface] / [squircleClip] during reveal — when
 * [squircleEnabled] is `false`, [addSquircleRect] falls back to a plain rounded rectangle to
 * match the squircle modifiers' fallback rendering.
 */
fun Modifier.popupClipReveal(
    fractionProgress: () -> Float,
    popupLayoutPosition: PopupLayoutPosition,
    cornerRadius: Dp,
    squircleEnabled: Boolean,
): Modifier =
    drawWithCache {
        val path = Path()
        val showBelow = popupLayoutPosition.showBelow
        val showAbove = popupLayoutPosition.showAbove
        onDrawWithContent {
            // Clamp — source spring overshoots; an oversized reveal path would cut downstream content.
            val progress = fractionProgress().coerceIn(0f, 1f)
            if (progress <= 0f) return@onDrawWithContent

            val height = size.height
            val visibleHeight = height * progress
            if (visibleHeight <= 0f) return@onDrawWithContent

            val clipStart =
                when {
                    showBelow -> 0f
                    showAbove -> height * (1f - progress)
                    else -> height * (0.5f - 0.5f * progress)
                }

            path.rewind()
            path.addSquircleRect(
                width = size.width,
                height = visibleHeight,
                cornerRadius = cornerRadius.toPx(),
                squircleEnabled = squircleEnabled,
            )
            if (clipStart == 0f) {
                clipPath(path) {
                    this@onDrawWithContent.drawContent()
                }
            } else {
                translate(top = clipStart) {
                    clipPath(path) {
                        translate(top = -clipStart) {
                            this@onDrawWithContent.drawContent()
                        }
                    }
                }
            }
        }
    }
