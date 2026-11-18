// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Measures the drawn corner radius by walking the 45-degree diagonal of the card's top-left corner:
 * the first card-coloured pixel sits at `r * (1 - 1 / sqrt(2))` from the corner on each axis, so the
 * distance along the diagonal is `r * (sqrt(2) - 1)`.
 */
@OptIn(ExperimentalTestApi::class)
class CardCornerRenderingTest {
    @Test
    fun defaultCardUsesTheDisplayRadius() = runComposeUiTest {
        setContent { CardOnWhite(cornerRadius = 24.dp, tag = "card") }
        waitForIdle()
        val radius = measuredRadius("card")
        assertClose(24f, radius, "CardDefaults.CornerRadius (display radius of couiRoundCornerXXL)")
    }

    @Test
    fun tierResolvesToTheStockRadius() = runComposeUiTest {
        setContent {
            CardOnWhite(
                cornerRadius = 0.dp,
                tag = "card",
                tier = CardCornerTier.XXL,
                scale = CardCornerScale.NORMAL_16_1,
            )
        }
        waitForIdle()
        val radius = measuredRadius("card")
        assertClose(
            CardCornerTier.XXL.normalRadius.value,
            radius,
            "CardCornerTier.XXL normal radius (coui_round_corner_xxl_radius_normal_16_1)",
        )
    }

    @Test
    fun screenshot() = runComposeUiTest {
        setContent {
            val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
            RmuixTheme(controller = controller) {
                Row(
                    Modifier
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    listOf(
                        CardCornerTier.M,
                        CardCornerTier.XL,
                        CardCornerTier.XXL,
                        CardCornerTier.XXXL,
                    ).forEach { tier ->
                        Card(
                            modifier = Modifier.size(90.dp),
                            cornerRadius = 0.dp,
                            cornerTier = tier,
                            cornerScale = CardCornerScale.LARGE_16_1,
                            colors = CardDefaults.defaultColors(color = RmuixTheme.colorScheme.secondaryContainer),
                        ) {}
                    }
                }
            }
        }
        waitForIdle()
        val image = onRoot().captureToImage().toAwtImage()
        var root = java.io.File(System.getProperty("user.dir")).absoluteFile
        while (root.parentFile != null && !java.io.File(root, "settings.gradle.kts").exists()) root = root.parentFile
        java.io.File(root, "docs/dev/shots").mkdirs()
        javax.imageio.ImageIO.write(image, "png", java.io.File(root, "docs/dev/shots/card-corners.png"))
        assertTrue(true)
    }

    @androidx.compose.runtime.Composable
    private fun CardOnWhite(
        cornerRadius: Dp,
        tag: String,
        tier: CardCornerTier? = null,
        scale: CardCornerScale = CardCornerScale.NORMAL_16_1,
    ) {
        val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
        RmuixTheme(controller = controller) {
            Box(Modifier.background(Color.White)) {
                Card(
                    modifier =
                    Modifier
                        .size(120.dp)
                        .testTag(tag),
                    cornerRadius = cornerRadius,
                    cornerTier = tier,
                    cornerScale = scale,
                    colors = CardDefaults.defaultColors(color = Color.Black),
                ) {}
            }
        }
    }

    private fun androidx.compose.ui.test.ComposeUiTest.measuredRadius(tag: String): Float {
        val card = onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        val pixels = onRoot().captureToImage().toPixelMap()
        // Walk the diagonal at half-pixel steps until the opaque card fill starts.
        var distance = 0f
        while (distance < 60f) {
            val x = (card.left + distance).toInt()
            val y = (card.top + distance).toInt()
            if (pixels[x, y].luminance() < 0.2f) break
            distance += 0.5f
        }
        val diagonal = distance * sqrt(2f)
        return diagonal / (sqrt(2f) - 1f)
    }

    private fun assertClose(
        expected: Float,
        actual: Float,
        message: String,
    ) {
        assertTrue(abs(expected - actual) <= 2f, "$message (expected $expected, was $actual)")
    }

    @Test
    fun defaultSurfaceUsesTheStockCardBackground() = runComposeUiTest {
        setContent {
            RmuixTheme {
                Box(Modifier.size(200.dp).background(Color.Black)) {
                    Card(modifier = Modifier.size(120.dp)) {}
                }
            }
        }
        waitForIdle()
        val pixels = onRoot().captureToImage().toPixelMap()
        val center = pixels[pixels.width / 2, pixels.height / 2]
        // Light theme: ?attr/couiColorCardBackground = @color/coui_color_card_background = #FFFFFF.
        assertTrue(center.red > 0.99f && center.green > 0.99f && center.blue > 0.99f, "card fill=$center")
    }
}
