// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/** Pins the `COUIToolTips.DetailFloating` preset values. */
@OptIn(ExperimentalTestApi::class)
class TooltipDetailFloatingTest {
    @Test
    fun detailFloatingPresetMatchesTheStyle() {
        val margins = TooltipDefaults.DetailFloatingContainerMargins
        assertEquals(12.dp, margins.calculateLeftPadding(LayoutDirection.Ltr), "detail_floating_text_margin")
        assertEquals(12.dp, margins.calculateRightPadding(LayoutDirection.Ltr), "detail_floating_text_margin")
        assertEquals(12.dp, margins.calculateTopPadding(), "detail_floating_text_margin")
        assertEquals(12.dp, margins.calculateBottomPadding(), "detail_floating_text_margin")

        val inside = TooltipDefaults.DetailFloatingInsideMargin
        assertEquals(16.dp, inside.calculateLeftPadding(LayoutDirection.Ltr), "floating padding left")
        assertEquals(16.dp, inside.calculateRightPadding(LayoutDirection.Ltr), "floating padding right")
        assertEquals(13.dp, inside.calculateTopPadding(), "default background padding top")
        assertEquals(80.dp, TooltipDefaults.DetailFloatingMinWidth, "detail_floating_min_width")
    }

    @Test
    fun asymmetricMarginsMirrorInRtlExactlyOnce() =
        runComposeUiTest {
            var provider: androidx.compose.ui.window.PopupPositionProvider? = null
            setContent {
                provider = TooltipDefaults.rememberTooltipPositionProvider()
            }
            val window = IntSize(500, 300)
            val popup = IntSize(120, 40)
            // An anchor hugging one edge forces the clamp, so the value that comes out *is* the margin on
            // that side. 12dp is the inline start margin, 42dp the inline end margin; resolving with the
            // ambient direction *and* mirroring in calculatePosition swapped the pair twice in RTL.
            val leftEdge = IntRect(left = 0, top = 100, right = 20, bottom = 140)
            val rightEdge = IntRect(left = 480, top = 100, right = 500, bottom = 140)
            assertEquals(
                12,
                provider!!.calculatePosition(leftEdge, window, LayoutDirection.Ltr, popup).x,
                "LTR start margin 12px",
            )
            assertEquals(
                42,
                provider!!.calculatePosition(leftEdge, window, LayoutDirection.Rtl, popup).x,
                "RTL: the inline end margin (42px) now sits on the left",
            )
            assertEquals(
                500 - 42 - 120,
                provider!!.calculatePosition(rightEdge, window, LayoutDirection.Ltr, popup).x,
                "LTR end margin 42px",
            )
            assertEquals(
                500 - 12 - 120,
                provider!!.calculatePosition(rightEdge, window, LayoutDirection.Rtl, popup).x,
                "RTL: the inline start margin (12px) now sits on the right",
            )
        }

    @Test
    fun detailFloatingMarginsClampThePopup() =
        runComposeUiTest {
            var provider: androidx.compose.ui.window.PopupPositionProvider? = null
            setContent {
                val density = LocalDensity.current
                Box(androidx.compose.ui.Modifier) {
                    provider =
                        TooltipDefaults.rememberTooltipPositionProvider(
                            positioning = TooltipAnchorPosition.Below,
                            containerMargins = TooltipDefaults.DetailFloatingContainerMargins,
                        )
                    // keep the density read so the composition stays realistic
                    density.density.hashCode()
                }
            }
            val window = IntSize(500, 300)
            val popup = IntSize(120, 40)
            val anchor = IntRect(left = 0, top = 100, right = 20, bottom = 140)
            val offset = provider!!.calculatePosition(anchor, window, LayoutDirection.Ltr, popup)
            assertEquals(
                IntOffset(12, 148),
                offset,
                "the DetailFloating preset clamps to its 12dp margin instead of the 42dp end margin",
            )
        }
}
