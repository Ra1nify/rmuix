// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.preference

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class PreferenceWidgetLocationRenderingTest {
    @Test
    fun checkpointWidgetIsTrailingByDefault() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Box(Modifier.size(400.dp, 80.dp).background(Color.White)) {
                        CheckboxPreference(
                            title = "Sync contacts",
                            checked = true,
                            onCheckedChange = {},
                            modifier = Modifier.testTag("row"),
                        )
                    }
                }
            }
            waitForIdle()
            val row = onNodeWithTag("row").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            val y = row.center.y.roundToInt()

            fun hasBlue(
                fromX: Int,
                toX: Int,
            ): Boolean =
                (fromX until toX).any { x ->
                    val c = pixels[x, y]
                    c.blue > c.red + 0.15f
                }
            // `coui_preference.xml` inflates the widget into the trailing `widget_frame`.
            assertTrue(hasBlue((row.right - 60f).roundToInt(), row.right.roundToInt()), "the checkbox must be trailing")
            assertTrue(!hasBlue(row.left.roundToInt(), (row.left + 60f).roundToInt()), "the leading slot must stay empty")
        }


    @Test
    fun trailingCheckboxOverhangsTheRowPaddingByItsNegativeEndMargin() =
        runComposeUiTest {
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Box(Modifier.size(400.dp, 80.dp).background(Color.White)) {
                        CheckboxPreference(
                            title = "Sync contacts",
                            checked = true,
                            onCheckedChange = {},
                            modifier = Modifier.testTag("row"),
                        )
                    }
                }
            }
            waitForIdle()
            val row = onNodeWithTag("row").fetchSemanticsNode().boundsInRoot
            val pixels = onRoot().captureToImage().toPixelMap()
            val y = row.center.y.roundToInt()
            // The checked box is the blue region; its right edge must be
            // row.right - support_preference_title_padding_end(32dp) + marginRight(10dp) - paddingRight(1dp).
            val expectedRight = row.right - 23f
            val blueRight =
                (row.left.roundToInt() until row.right.roundToInt())
                    .lastOrNull { x ->
                        val c = pixels[x, y]
                        c.blue > c.red + 0.15f
                    }
                    ?.toFloat() ?: -1f
            assertTrue(blueRight > 0f, "the trailing checkbox must be drawn")
            assertEquals(expectedRight, blueRight, 2f, "the checkbox must overhang the row padding")
        }
}