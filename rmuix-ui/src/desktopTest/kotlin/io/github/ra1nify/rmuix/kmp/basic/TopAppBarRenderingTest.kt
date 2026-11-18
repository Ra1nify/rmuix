// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class TopAppBarRenderingTest {
    @Test
    fun dividerIsOffByDefaultLikeTheSettingsAppBar() =
        runComposeUiTest {
            val state = TopAppBarState(0f, 0f, 0f)
            setContent {
                CompositionLocalProvider(LocalDensity provides Density(2f)) {
                    RmuixTheme {
                        Box(Modifier.size(400.dp, 300.dp).background(Color.White)) {
                            TopAppBar(
                                title = "Title",
                                largeTitle = "Large Title",
                                color = Color.White,
                                modifier = Modifier.testTag("bar"),
                                scrollBehavior = RmuixScrollBehavior(state),
                                defaultWindowInsetsPadding = false,
                            )
                        }
                    }
                }
            }
            waitForIdle()
            runOnIdle {
                state.heightOffset = state.heightOffsetLimit
                state.contentOffset = -state.expandedHeightPx
            }
            waitForIdle()
            val collapsed = onNodeWithTag("bar").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            // `common_divider_appbar_layout.xml` sets `settings:hasDivider="false"`: even fully
            // collapsed, a default bar must show no hairline (ColorOS uses a gradient blur instead).
            val y = (collapsed.bottom - 1).roundToInt()
            assertTrue(
                minRed(pixels, y) > 0.97f,
                "default bar must not draw a divider, darkest red=${minRed(pixels, y)}",
            )
        }

    @Test
    fun dividerIsInvisibleExpandedAndSpansTheCollapsedBar() =
        runComposeUiTest {
            val state = TopAppBarState(0f, 0f, 0f)
            setContent {
                // Density 2 keeps the 0.33dp hairline at a whole pixel.
                CompositionLocalProvider(LocalDensity provides Density(2f)) {
                    RmuixTheme {
                        Box(Modifier.size(400.dp, 300.dp).background(Color.White)) {
                            TopAppBar(
                                title = "Title",
                                largeTitle = "Large Title",
                                color = Color.White,
                                modifier = Modifier.testTag("bar"),
                                scrollBehavior = RmuixScrollBehavior(state),
                                defaultWindowInsetsPadding = false,
                                // The divider is off by default (the settings app bar sets
                                // `settings:hasDivider="false"`); this test pins the opt-in path.
                                showDivider = true,
                            )
                        }
                    }
                }
            }
            waitForIdle()
            val expanded = onNodeWithTag("bar").fetchSemanticsNode().boundsInRoot
            val expandedPixels = onRoot().captureToImage().toPixelMap()
            // The bar is 110dp expanded and the divider's alpha is 0 there.
            assertTrue(expanded.height > 200f, "expanded bar should be tall: ${expanded.height}")
            assertTrue(
                minRed(expandedPixels, (expanded.bottom - 2).roundToInt()) > 0.97f,
                "expanded bar must not draw a divider",
            )

            // Halfway down the inset has lerped to 12dp and the alpha to 0.5: the fraction is the
            // accumulated scroll divided by the expanded bar height.
            runOnIdle {
                state.heightOffset = state.heightOffsetLimit * 0.5f
                state.contentOffset = -state.expandedHeightPx * 0.5f
            }
            waitForIdle()
            val halfway = onNodeWithTag("bar").fetchSemanticsNode().boundsInRoot
            val halfwayPixels = onRoot().captureToImage().toPixelMap()
            // A collapsable bar keeps `coui_appbar_start_padding_bottom` while expanded and interpolates it
            // to `_end_padding_bottom` as it collapses (`COUICollapsableAppBarLayout.adjustPaddingBottom()`),
            // so halfway the divider sits 6dp (12px at density 2) above the bar's bottom edge.
            val collapsePaddingPx = 12
            val halfwayY = (halfway.bottom - 1 - collapsePaddingPx).roundToInt()
            // 24dp expanded inset x (1 - 0.5) = 12dp = 24px at density 2; sample just past it.
            val insetPx = halfway.left.roundToInt() + 30
            assertTrue(
                halfwayPixels[halfway.left.roundToInt() + 2, halfwayY].red > 0.97f,
                "half-collapsed divider must keep its side inset",
            )
            assertTrue(
                halfwayPixels[insetPx, halfwayY].red < 0.95f,
                "half-collapsed divider must already be drawn past the inset",
            )
            // The strip below the divider is the collapsable bar's bottom padding, so it carries no line.
            assertTrue(
                halfwayPixels[insetPx, (halfway.bottom - 1).roundToInt()].red > 0.97f,
                "the bottom padding below the divider must stay unpainted",
            )

            runOnIdle {
                state.heightOffset = state.heightOffsetLimit
                state.contentOffset = -state.expandedHeightPx
            }
            waitForIdle()
            val collapsed = onNodeWithTag("bar").fetchSemanticsNode().boundsInRoot
            val collapsedPixels = onRoot().captureToImage().toPixelMap()
            assertTrue(collapsed.height < expanded.height, "bar must collapse: ${collapsed.height}")
            // Collapsed: full-bleed 0.33dp line in couiColorDivider (#1F000000) over white.
            val y = (collapsed.bottom - 1).roundToInt()
            val darkest = minRed(collapsedPixels, y)
            assertTrue(darkest < 0.95f, "collapsed bar must draw the divider, darkest red=$darkest")
            // It runs to both edges once collapsed (margins lerp to 0dp).
            val left = collapsed.left.roundToInt()
            val right = collapsed.right.roundToInt()
            assertTrue(
                collapsedPixels[left + 1, y].red < 0.95f && collapsedPixels[right - 2, y].red < 0.95f,
                "collapsed divider must reach both edges",
            )
        }

    @Test
    fun dividerSurvivesOneExDensity() =
        runComposeUiTest {
            val state = TopAppBarState(-1000f, -1000f, -1000f)
            setContent {
                RmuixTheme {
                    Box(Modifier.size(400.dp, 300.dp).background(Color.White)) {
                        TopAppBar(
                            title = "Title",
                            largeTitle = "Large Title",
                            color = Color.White,
                            modifier = Modifier.testTag("bar"),
                            showDivider = true,
                            scrollBehavior = RmuixScrollBehavior(state),
                            defaultWindowInsetsPadding = false,
                        )
                    }
                }
            }
            waitForIdle()
            val bar = onNodeWithTag("bar").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            // 0.33dp rounds to zero pixels at density 1; the clamp keeps a 1px hairline.
            val y = (bar.bottom - 1).roundToInt()
            val darkest = minRed(pixels, y)
            assertTrue(darkest < 0.95f, "1x density must still show the divider, darkest red=$darkest")
        }

    @Test
    fun blurredLargeTitleStaysInsideTheBar() =
        runComposeUiTest {
            val state = TopAppBarState(-1000f, 0f, 0f)
            setContent {
                CompositionLocalProvider(LocalDensity provides Density(2f)) {
                    RmuixTheme {
                        Box(Modifier.size(400.dp, 300.dp).background(Color.White)) {
                            BlurTopAppBar(
                                title = "",
                                largeTitle = "MMMMMMMM",
                                largeTitleColor = Color.Magenta,
                                largeTitleBlurRadius = 40.dp,
                                color = Color.Transparent,
                                modifier = Modifier.testTag("bar"),
                                scrollBehavior = RmuixScrollBehavior(state),
                                defaultWindowInsetsPadding = false,
                            )
                        }
                    }
                }
            }
            waitForIdle()
            runOnIdle { state.heightOffset = state.heightOffsetLimit * 0.2f }
            waitForIdle()
            val bar = onNodeWithTag("bar").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            val columns = bar.left.roundToInt() until bar.right.roundToInt()
            val hasTitle =
                (bar.top.roundToInt() until bar.bottom.roundToInt()).any { y ->
                    columns.any { x -> pixels[x, y].red > pixels[x, y].green + 0.01f }
                }
            assertTrue(hasTitle, "the partially collapsed title must still be visible")
            val bottom = bar.bottom.roundToInt()
            val overflowBelow =
                (bottom until bottom + 30).any { y ->
                    columns.any { x -> pixels[x, y].red > pixels[x, y].green + 0.01f }
                }
            assertTrue(!overflowBelow, "the blurred title must be clipped to the bar")
        }

    private fun minRed(
        pixels: androidx.compose.ui.graphics.PixelMap,
        y: Int,
    ): Float {
        var min = 1f
        for (x in 2 until pixels.width - 2) {
            val red = pixels[x, y].red
            if (red < min) min = red
        }
        return min
    }
}
