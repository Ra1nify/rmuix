// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.anim.couiSpring
import kotlin.math.PI

/**
 * `COUIChipGroup`'s group size-change spring, converted from the stock response/bounce form.
 *
 * The stock builds a `COUISpringForce` with `setResponse(0.35f)` / `setBounce(0f)` for
 * `mGroupHeightChangeAnimation` and `mGroupWidthChangeAnimation`. A response of 0.35s maps to
 * `stiffness = (2 * PI / 0.35)^2` (the same response -> stiffness conversion `SliderStockValues` uses
 * for its height spring), and `bounce = 0` is a critically damped spring, i.e. `dampingRatio = 1`.
 */
internal val ChipGroupStockSizeChangeSpring: SpringSpec<IntSize> =
    spring(
        dampingRatio = 1f,
        stiffness = ((2.0 * PI / 0.35) * (2.0 * PI / 0.35)).toFloat(),
    )

/**
 * A ColorOS chip group (`COUIChipGroup`): the chips wrap into rows separated by
 * `coui_chip_group_default_spacing_horizontal` / `_vertical` (8dp), and when they would exceed
 * `collapsedMaxRows` rows only that many rows stay visible while a small collapse chip
 * (`coui_chip_group_collapsable_button_size`, 28dp) is appended to the last visible row.
 *
 * Tapping the chip expands the group (all rows are shown and the chip turns into the expand
 * chevron); tapping it again collapses back. The group's **size** change runs on the stock
 * `mGroupHeightChangeAnimation` spring (`ChipGroupStockSizeChangeSpring`: response 0.35s, bounce 0),
 * so the bounds ease instead of jumping; the individual chips still re-place without per-chip motion
 * (recorded in `docs/dev/coloros-chip-group.md`).
 *
 * Selection is left to the chips themselves here: pass `selected` to each [Chip]. This overload cannot
 * index its children, so it cannot own a checked chip; use the list-based
 * `ChipGroup(items, singleSelection = true, ...)` overload for the stock `singleSelection` /
 * `selectionRequired` / `checkedChip` semantics (and for the per-chip fold animation).
 *
 * @param modifier The modifier to be applied to the group.
 * @param collapsedMaxRows The number of rows kept while collapsed; the stock `collapsedMaxRows`
 *   attribute defaults to [ChipDefaults.CollapsedMaxRows] (3).
 * @param horizontalSpacing Horizontal space between chips.
 * @param verticalSpacing Vertical space between rows.
 * @param content The chips; normally [Chip] calls.
 */
@Composable
fun ChipGroup(
    modifier: Modifier = Modifier,
    collapsedMaxRows: Int = ChipDefaults.CollapsedMaxRows,
    horizontalSpacing: Dp = ChipDefaults.GroupSpacing,
    verticalSpacing: Dp = ChipDefaults.GroupSpacing,
    content: @Composable () -> Unit,
) = ChipGroupLayout(
    modifier = modifier,
    collapsedMaxRows = collapsedMaxRows,
    horizontalSpacing = horizontalSpacing,
    verticalSpacing = verticalSpacing,
    // The content-based overload cannot index the caller's chips, so nothing observes the visible count;
    // [ChipGroup] with a list passes a callback here and animates the chips that fold away.
    onVisibleCountChange = null,
    content = content,
)

@Composable
private fun ChipGroupLayout(
    collapsedMaxRows: Int,
    horizontalSpacing: Dp,
    verticalSpacing: Dp,
    onVisibleCountChange: ((Int) -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val horizontalGapPx = with(density) { horizontalSpacing.roundToPx() }
    val verticalGapPx = with(density) { verticalSpacing.roundToPx() }
    Layout(
        content = {
            content()
            ChipGroupToggle(
                expanded = expanded,
                onClick = { expanded = !expanded },
            )
        },
        // `mGroupHeightChangeAnimation` / `mGroupWidthChangeAnimation` animate the group's bounds when
        // the chip count changes, so collapsing or expanding eases instead of jumping.
        // `clipToBounds` keeps the fading chips of the folded rows inside the (animated) box, the way a
        // ViewGroup clips its children while the height animates.
        modifier = modifier.animateContentSize(animationSpec = ChipGroupStockSizeChangeSpring).clipToBounds(),
    ) { measurables, constraints ->
        val maxWidth = constraints.maxWidth
        // A group inside a horizontally scrollable parent gets an unbounded width; lay everything out on
        // one row (the stock ChipGroup cannot wrap either) instead of computing an infinite layout size.
        val unbounded = maxWidth == Constraints.Infinity
        val placeables = measurables.map { it.measure(Constraints(maxWidth = maxWidth)) }
        val button = placeables.last()
        val items = placeables.dropLast(1)
        // Greedy row packing, mirroring COUIChipGroup's measure pass.
        val rows = mutableListOf<MutableList<Int>>()
        var cursor = 0
        items.forEachIndexed { index, placeable ->
            if (rows.isEmpty() || (!unbounded && cursor > 0 && cursor + placeable.width > maxWidth)) {
                rows.add(mutableListOf())
                cursor = 0
            }
            rows.last().add(index)
            cursor += placeable.width + horizontalGapPx
        }
        val overflowing = rows.size > collapsedMaxRows
        val showButton = overflowing
        val placedRows =
            if (overflowing && !expanded) {
                rows.take(collapsedMaxRows).toMutableList()
            } else {
                rows.toMutableList()
            }
        // Collapsed: the button replaces trailing chips of the last visible row. Expanded: the button
        // opens a new row when it does not fit, so no chip is ever hidden in the expanded state.
        var buttonOnNewRow = false
        if (showButton && placedRows.isNotEmpty()) {
            val lastRow = placedRows.last()

            fun usedWidth(row: List<Int>): Int =
                row.sumOf { items[it].width } + (if (row.isEmpty()) 0 else (row.size - 1) * horizontalGapPx)
            if (usedWidth(lastRow) + horizontalGapPx + button.width > maxWidth) {
                if (!expanded) {
                    while (lastRow.isNotEmpty() && usedWidth(lastRow) + horizontalGapPx + button.width > maxWidth) {
                        lastRow.removeAt(lastRow.lastIndex)
                    }
                } else {
                    buttonOnNewRow = true
                }
            }
        }
        val rowHeights =
            placedRows.map { row ->
                (row.maxOfOrNull { items[it].height } ?: 0).coerceAtLeast(
                    if (showButton && !buttonOnNewRow && row === placedRows.last()) button.height else 0,
                )
            }
        val buttonRowExtra =
            if (showButton && buttonOnNewRow) {
                button.height + (if (placedRows.isNotEmpty()) verticalGapPx else 0)
            } else {
                0
            }
        val rowsHeight =
            rowHeights.sum() + (if (rowHeights.size > 1) verticalGapPx * (rowHeights.size - 1) else 0)
        val totalHeight = rowsHeight + buttonRowExtra
        val contentWidth =
            placedRows.maxOfOrNull { row ->
                row.sumOf { items[it].width } + (if (row.isEmpty()) 0 else (row.size - 1) * horizontalGapPx) +
                    if (showButton && !buttonOnNewRow && row === placedRows.last()) horizontalGapPx + button.width else 0
            } ?: 0
        val layoutWidth = if (unbounded) contentWidth else contentWidth.coerceAtMost(maxWidth).coerceAtLeast(0)
        val layoutHeight = totalHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
        // Report the visible chip count before laying out: the list overload reads it to fade the folded
        // chips, and the measurement lambda must still return the `MeasureResult` from `layout`.
        onVisibleCountChange?.invoke(placedRows.sumOf { it.size })
        layout(width = layoutWidth, height = layoutHeight) {
            // `COUIChipGroup` lays its chips out from the inline start; mirror the x axis in RTL so the
            // first chip sits on the right edge.
            val rtl = layoutDirection == LayoutDirection.Rtl

            fun placeInline(
                placeable: Placeable,
                x: Int,
                y: Int,
            ) {
                val left = if (rtl) layoutWidth - x - placeable.width else x
                placeable.place(left, y)
            }

            var y = 0
            placedRows.forEachIndexed { rowIndex, row ->
                var x = 0
                row.forEach { index ->
                    val placeable = items[index]
                    placeInline(placeable, x, y + (rowHeights[rowIndex] - placeable.height) / 2)
                    x += placeable.width + horizontalGapPx
                }
                if (showButton && !buttonOnNewRow && rowIndex == placedRows.lastIndex) {
                    placeInline(button, x, y + (rowHeights[rowIndex] - button.height) / 2)
                }
                y += rowHeights[rowIndex] + verticalGapPx
            }
            if (showButton && buttonOnNewRow) {
                placeInline(button, 0, if (placedRows.isEmpty()) 0 else y)
            }
            // Rows past the collapsed height are still laid out at their expanded positions: the caller's
            // chips fade out there (`mAlphaAnimation` / `mScaleAnimation` in the stock), and the group's
            // `clipToBounds` hides whatever falls outside the animated box.
            var hiddenY = y
            rows.drop(placedRows.size).forEach { row ->
                val rowHeight = row.maxOfOrNull { items[it].height } ?: 0
                var x = 0
                row.forEach { index ->
                    val placeable = items[index]
                    placeInline(placeable, x, hiddenY + (rowHeight - placeable.height) / 2)
                    x += placeable.width + horizontalGapPx
                }
                hiddenY += rowHeight + verticalGapPx
            }
        }
    }
}

/**
 * One chip inside the list-based [ChipGroup].
 *
 * @param label The chip label.
 * @param enabled Whether the chip can be clicked.
 * @param icon Optional leading slot (see [Chip]'s `icon`).
 * @param onClick Extra work to run when the chip is clicked, after the group updates its selection.
 */
@Stable
class ChipGroupItem(
    val label: String,
    val enabled: Boolean = true,
    val icon: (@Composable () -> Unit)? = null,
    val onClick: (() -> Unit)? = null,
)

/**
 * A [ChipGroup] over a list of chips, with the stock `COUIChipGroup` selection attributes.
 *
 * Unlike the content-based overload this one can index its chips, so it can (a) own the checked chip
 * (`singleSelection` / `selectionRequired` / `checkedChip`) and (b) animate the chips that fold away:
 * the stock runs `mAlphaAnimation` (response 0.2s, bounce 0) and `mScaleAnimation` (scale 0.8 -> 1.0,
 * response 0.3s, bounce 0) whenever a chip leaves or enters the visible rows. Only the alpha and scale
 * are reproduced here; the stock also slides each chip with `mHorizontal/VerticalAnimation`
 * (response 0.3s), which needs a placement animation this layout does not run yet.
 *
 * @param items The chips, in visual order.
 * @param modifier The modifier to be applied to the group.
 * @param collapsedMaxRows The number of rows kept while collapsed.
 * @param horizontalSpacing Horizontal space between chips.
 * @param verticalSpacing Vertical space between rows.
 * @param singleSelection Whether at most one chip may be checked at a time.
 * @param selectionRequired Whether one chip must stay checked; ignoring it when [singleSelection] is off
 *   matches the stock, where the attribute only affects the single-selection path.
 * @param checkedChip The checked index. `null` lets the group keep the state itself; passing a value makes
 *   the group controlled and [onCheckedChange] the only way it changes.
 * @param onCheckedChange Called with the new checked index (`null` when nothing is checked).
 */
@Composable
fun ChipGroup(
    items: List<ChipGroupItem>,
    modifier: Modifier = Modifier,
    collapsedMaxRows: Int = ChipDefaults.CollapsedMaxRows,
    horizontalSpacing: Dp = ChipDefaults.GroupSpacing,
    verticalSpacing: Dp = ChipDefaults.GroupSpacing,
    singleSelection: Boolean = false,
    selectionRequired: Boolean = false,
    checkedChip: Int? = null,
    onCheckedChange: ((Int?) -> Unit)? = null,
) {
    // Uncontrolled state only; a non-null `checkedChip` turns the group into a controlled component.
    var uncheckedState by remember { mutableStateOf<Int?>(null) }
    val checked = checkedChip ?: uncheckedState
    val visibleCount = remember { mutableIntStateOf(Int.MAX_VALUE) }
    val chipFadeSpring = remember { couiSpring<Float>(response = 0.2f, bounce = 0f) }
    val chipScaleSpring = remember { couiSpring<Float>(response = 0.3f, bounce = 0f) }
    ChipGroupLayout(
        modifier = modifier,
        collapsedMaxRows = collapsedMaxRows,
        horizontalSpacing = horizontalSpacing,
        verticalSpacing = verticalSpacing,
        onVisibleCountChange = { count -> if (visibleCount.intValue != count) visibleCount.intValue = count },
    ) {
        items.forEachIndexed { index, item ->
            key(index) {
                val visible = index < visibleCount.intValue
                val alpha by animateFloatAsState(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = chipFadeSpring,
                    label = "chipAlpha",
                )
                val scale by animateFloatAsState(
                    targetValue = if (visible) 1f else 0.8f,
                    animationSpec = chipScaleSpring,
                    label = "chipScale",
                )
                Chip(
                    onClick = {
                        if (singleSelection && item.enabled) {
                            val next =
                                when {
                                    checked != index -> index
                                    selectionRequired -> checked
                                    else -> null
                                }
                            if (next != checked) {
                                if (checkedChip == null) uncheckedState = next
                                onCheckedChange?.invoke(next)
                            }
                        }
                        item.onClick?.invoke()
                    },
                    label = item.label,
                    modifier =
                        Modifier.graphicsLayer {
                            this.alpha *= alpha
                            scaleX *= scale
                            scaleY *= scale
                        },
                    selected = singleSelection && checked == index,
                    enabled = item.enabled,
                    icon = item.icon,
                )
            }
        }
    }
}

/** The stock collapse / expand chip: a 28dp unchecked suggestion chip that only carries a chevron. */
@Composable
private fun ChipGroupToggle(
    expanded: Boolean,
    onClick: () -> Unit,
) {
    val dark = isChipInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier =
            Modifier
                .size(ChipDefaults.GroupCollapseButtonSize)
                .clip(CircleShape)
                .background(chipUncheckedContainerColor(dark))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                ).semantics { contentDescription = if (expanded) "Collapse" else "Expand" },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(ChipDefaults.GroupCollapseGlyphSize)) {
            // Expanded shows the collapse (up) chevron, collapsed shows the expand (down) one - the stock
            // `createCollapsableChip(true)` chip carries `ic_chip_collapse` and performs the collapse.
            val path = if (expanded) chipGroupCollapsePath() else chipGroupExpandPath()
            val chevronScale = size.minDimension / CHIP_GROUP_CHEVRON_VIEWPORT
            scale(chevronScale) {
                drawPath(
                    path = path,
                    color = chipUncheckedContentColor(dark),
                    style = Stroke(width = CHIP_GROUP_CHEVRON_STROKE, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            }
        }
    }
}

/** Parsed once from `ic_chip_collapse.xml` (`M12,10L8,6L4,10`). */
private val chipGroupCollapsePathCache: Path by lazy {
    PathParser().parsePathString("M12,10L8,6L4,10").toPath()
}

/** Parsed once from `ic_chip_expand.xml` (`M4,6L8,10L12,6`). */
private val chipGroupExpandPathCache: Path by lazy {
    PathParser().parsePathString("M4,6L8,10L12,6").toPath()
}

private fun chipGroupCollapsePath(): Path = chipGroupCollapsePathCache

private fun chipGroupExpandPath(): Path = chipGroupExpandPathCache
