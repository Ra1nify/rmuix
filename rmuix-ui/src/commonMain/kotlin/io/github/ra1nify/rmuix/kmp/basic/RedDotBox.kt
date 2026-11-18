// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/** Shape class of the anchor inside a [RedDotBox]: `couiHintRedPoint`'s `anchorViewShapeType`. */
enum class RedDotAnchorShape {
    /** `CIRCLE_TYPE = 1`: the dot is inset by the circle margin and the container keeps the anchor size. */
    Circle,

    /** `RECTANGLE_TYPE = 0`: the container grows by the rectangle/number margin so the dot clears the corner. */
    Rectangle,
}

/** What sits in the top-end corner: `couiHintRedPointMode`'s dot vs. number distinction. */
enum class RedDotAnchorKind {
    /** `POINT_ONLY_MODE` (1) and its stroke variant (4): a plain dot. */
    Dot,

    /** `POINT_NUM_MODE` (2) and its stroke variant (5): a number badge, which uses a wider margin. */
    Number,
}

/**
 * A container that pins a red dot to the top-end corner of its content, ported from
 * `COUIRedDotFrameLayout`.
 *
 * The margins come from the `coui_hint_red_dot_*_icon_topend_margin_*` /
 * `coui_hint_red_dot_*_number_topend_margin_rectangle` dimens and are bucketed by [anchorSize]:
 * below `coui_hint_red_dot_medium_icon_size` (36dp) is the small bucket, 36dp up to
 * `coui_hint_red_dot_large_icon_size` (50dp) is medium, and 50dp and above is large.
 *
 * The stock layout offsets the anchor itself for rectangle anchors
 * (`mMarginTopAndLeftOfAnchorView`), and only offsets the dot for circle anchors
 * (`mMarginTopAndLeftOfReddot`); in RTL both views are placed at that same margin, which this
 * layout mirrors exactly. The container is measured as the anchor plus that margin on both axes.
 *
 * @param redDot the dot or badge to pin into the corner, usually a [RedDot].
 * @param modifier the modifier applied to the container.
 * @param anchorSize the size of the anchor, used to pick the margin bucket (`anchorViewDpSize`).
 * @param anchorShape the anchor's shape class (`anchorViewShapeType`).
 * @param redDotKind whether the corner shows a dot or a number (`couiHintRedPointMode`).
 * @param content the anchored content.
 */
@Composable
fun RedDotBox(
    redDot: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    anchorSize: Dp = RedDotBoxDefaults.AnchorSize,
    anchorShape: RedDotAnchorShape = RedDotAnchorShape.Rectangle,
    redDotKind: RedDotAnchorKind = RedDotAnchorKind.Dot,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val bucket =
        when {
            anchorSize < RedDotStockMediumAnchorSize -> RedDotAnchorBucket.Small
            anchorSize < RedDotStockLargeAnchorSize -> RedDotAnchorBucket.Medium
            else -> RedDotAnchorBucket.Large
        }
    val anchorMargin: Dp
    val dotMargin: Dp
    if (anchorShape == RedDotAnchorShape.Circle) {
        anchorMargin = 0.dp
        dotMargin =
            when (bucket) {
                RedDotAnchorBucket.Small -> RedDotStockSmallCircleMargin
                RedDotAnchorBucket.Medium -> RedDotStockMediumCircleMargin
                RedDotAnchorBucket.Large -> RedDotStockLargeCircleMargin
            }
    } else {
        dotMargin = 0.dp
        anchorMargin =
            if (redDotKind == RedDotAnchorKind.Number) {
                when (bucket) {
                    RedDotAnchorBucket.Small -> RedDotStockSmallNumberMargin
                    RedDotAnchorBucket.Medium -> RedDotStockMediumNumberMargin
                    RedDotAnchorBucket.Large -> RedDotStockLargeNumberMargin
                }
            } else {
                when (bucket) {
                    RedDotAnchorBucket.Small -> RedDotStockSmallRectangleMargin
                    RedDotAnchorBucket.Medium -> RedDotStockMediumRectangleMargin
                    RedDotAnchorBucket.Large -> RedDotStockLargeRectangleMargin
                }
            }
    }
    val anchorMarginPx = with(density) { anchorMargin.roundToPx() }
    val dotMarginPx = with(density) { dotMargin.roundToPx() }

    Layout(
        modifier = modifier,
        content = {
            content()
            redDot()
        },
    ) { measurables, constraints ->
        val anchor = measurables[0].measure(constraints)
        val dot = measurables[1].measure(Constraints())
        val width = anchor.width + anchorMarginPx
        val height = anchor.height + anchorMarginPx
        layout(width, height) {
            if (rtl) {
                // `COUIRedDotFrameLayout.onLayout` places both views at the same margin in RTL.
                anchor.place(anchorMarginPx, anchorMarginPx)
                dot.place(dotMarginPx, dotMarginPx)
            } else {
                anchor.place(0, anchorMarginPx)
                dot.place(width - dot.width - dotMarginPx, dotMarginPx)
            }
        }
    }
}

/** The margin buckets of `COUIRedDotFrameLayout`: below 36dp, 36dp to 49dp, and 50dp and above. */
private enum class RedDotAnchorBucket { Small, Medium, Large }

/** Defaults of [RedDotBox]. */
object RedDotBoxDefaults {
    /** `coui_hint_red_dot_medium_icon_size`: `COUIRedDotFrameLayout`'s default `anchorViewDpSize`. */
    val AnchorSize = RedDotStockMediumAnchorSize
}
