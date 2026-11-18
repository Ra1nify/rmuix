// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pure geometry of [TooltipDefaults.rememberTooltipPositionProvider]: where the popup lands relative
 * to the anchor for every anchor position, including the fallback when the preferred side overflows.
 *
 * The provider measures in pixels with the default 8dp spacing resolving to 8px at density 1, so the
 * expected offsets are plain integers.
 */
@OptIn(ExperimentalTestApi::class)
class TooltipPositionProviderTest {
    private val anchor = IntRect(left = 100, top = 100, right = 200, bottom = 140)
    private val window = IntSize(width = 500, height = 500)
    private val popup = IntSize(width = 120, height = 40)

    @Test
    fun belowClearsTheAnchorAndCentresHorizontally() =
        runComposeUiTest {
            val position = providers()[TooltipAnchorPosition.Below]!!.calculatePosition(anchor, window, LayoutDirection.Ltr, popup)
            assertEquals(IntOffset(90, 148), position, "y = anchor.bottom + 8dp spacing, x centred")
        }

    @Test
    fun aboveClearsTheAnchor() =
        runComposeUiTest {
            val position = providers()[TooltipAnchorPosition.Above]!!.calculatePosition(anchor, window, LayoutDirection.Ltr, popup)
            assertEquals(IntOffset(90, 52), position, "y = anchor.top - popup.height - 8dp")
        }

    @Test
    fun leftAndRightClearTheHorizontalEdges() =
        runComposeUiTest {
            val all = providers()
            // The anchor sits far enough right that both sides have room.
            val centred = IntRect(left = 250, top = 100, right = 350, bottom = 140)
            assertEquals(
                IntOffset(122, 100),
                all[TooltipAnchorPosition.Left]!!.calculatePosition(centred, window, LayoutDirection.Ltr, popup),
                "x = anchor.left - popup.width - 8dp",
            )
            assertEquals(
                IntOffset(338, 100),
                all[TooltipAnchorPosition.Right]!!.calculatePosition(centred, window, LayoutDirection.Ltr, popup),
                "x = anchor.right + 8dp, clamped to the 42dp end margin",
            )
        }

    @Test
    fun leftFallsBackRightWhenThereIsNoRoom() =
        runComposeUiTest {
            val position = providers()[TooltipAnchorPosition.Left]!!.calculatePosition(anchor, window, LayoutDirection.Ltr, popup)
            assertEquals(IntOffset(208, 100), position, "no room on the left -> flip to the right edge")
        }

    @Test
    fun startAndEndFollowTheLayoutDirection() =
        runComposeUiTest {
            val start = providers()[TooltipAnchorPosition.Start]!!
            val centred = IntRect(left = 250, top = 100, right = 350, bottom = 140)
            assertEquals(
                IntOffset(122, 100),
                start.calculatePosition(centred, window, LayoutDirection.Ltr, popup),
                "Start resolves to the inline start edge = left in LTR",
            )
            assertEquals(
                IntOffset(358, 100),
                start.calculatePosition(centred, window, LayoutDirection.Rtl, popup),
                "Start resolves to the inline start edge = right in RTL",
            )
        }

    @Test
    fun belowFallsBackAboveWhenThePopupWouldOverflow() =
        runComposeUiTest {
            val nearBottom = IntRect(100, 440, 200, 480)
            val position =
                providers()[TooltipAnchorPosition.Below]!!
                    .calculatePosition(nearBottom, window, LayoutDirection.Ltr, popup)
            assertEquals(IntOffset(90, 392), position, "no room below -> above the anchor")
        }

    @Test
    fun positionIsClampedToTheWindow() =
        runComposeUiTest {
            val below = providers()[TooltipAnchorPosition.Below]!!
            // Anchor hugging the left edge: the centred popup would start at a negative x.
            val nearLeft = IntRect(left = 0, top = 100, right = 20, bottom = 140)
            assertEquals(
                IntOffset(12, 148),
                below.calculatePosition(nearLeft, window, LayoutDirection.Ltr, popup),
                "the popup keeps `couiToolTipsContainerLayoutMarginStart` 12dp from the edge",
            )
            // Anchor hugging the right edge: the centred popup would overflow by 60px.
            val nearRight = IntRect(left = 480, top = 100, right = 500, bottom = 140)
            assertEquals(
                IntOffset(338, 148),
                below.calculatePosition(nearRight, window, LayoutDirection.Ltr, popup),
                "the popup keeps `couiToolTipsContainerLayoutMarginEnd` 42dp from the edge",
            )
        }

    private fun ComposeUiTest.providers(): Map<TooltipAnchorPosition, PopupPositionProvider> {
        val out = mutableMapOf<TooltipAnchorPosition, PopupPositionProvider>()
        setContent {
            val all =
                listOf(
                    TooltipAnchorPosition.Above,
                    TooltipAnchorPosition.Below,
                    TooltipAnchorPosition.Left,
                    TooltipAnchorPosition.Right,
                    TooltipAnchorPosition.Start,
                    TooltipAnchorPosition.End,
                )
            all.forEach { positioning ->
                val provider = TooltipDefaults.rememberTooltipPositionProvider(positioning)
                SideEffect { out[positioning] = provider }
            }
        }
        waitForIdle()
        return out
    }
}
