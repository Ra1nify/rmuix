// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The stock switch is direction aware (`SwitchMotionState.isRtl` flips the travel sign), so an
 * unchecked thumb sits at the inline start: left in LTR, right in RTL.
 */
@OptIn(ExperimentalTestApi::class)
class SwitchRtlThumbTest {
    @Test
    fun uncheckedThumbSitsAtTheInlineStart() =
        runComposeUiTest {
            setContent { SwitchRow(checked = false, direction = LayoutDirection.Ltr, tag = "ltr") }
            waitForIdle()
            val ltr = thumbCentre("ltr")
            val ltrBox = onNodeWithTag("ltr").fetchSemanticsNode().boundsInRoot
            assertTrue(ltr < ltrBox.center.x, "LTR unchecked thumb is on the left (was $ltr)")

            setContent { SwitchRow(checked = false, direction = LayoutDirection.Rtl, tag = "rtl") }
            waitForIdle()
            val rtl = thumbCentre("rtl")
            val rtlBox = onNodeWithTag("rtl").fetchSemanticsNode().boundsInRoot
            assertTrue(rtl > rtlBox.center.x, "RTL unchecked thumb is on the right (was $rtl)")
        }

    @Test
    fun checkedThumbMovesToTheInlineEnd() =
        runComposeUiTest {
            setContent { SwitchRow(checked = true, direction = LayoutDirection.Ltr, tag = "ltr") }
            waitForIdle()
            val ltr = thumbCentre("ltr")
            val ltrBox = onNodeWithTag("ltr").fetchSemanticsNode().boundsInRoot
            assertTrue(ltr > ltrBox.center.x, "LTR checked thumb is on the right (was $ltr)")

            setContent { SwitchRow(checked = true, direction = LayoutDirection.Rtl, tag = "rtl") }
            waitForIdle()
            val rtl = thumbCentre("rtl")
            val rtlBox = onNodeWithTag("rtl").fetchSemanticsNode().boundsInRoot
            assertTrue(rtl < rtlBox.center.x, "RTL checked thumb is on the left (was $rtl)")
        }

    @androidx.compose.runtime.Composable
    private fun SwitchRow(
        checked: Boolean,
        direction: LayoutDirection,
        tag: String,
    ) {
        val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
        RmuixTheme(controller = controller) {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                Box(Modifier.background(Color.White)) {
                    Column {
                        Switch(
                            checked = checked,
                            onCheckedChange = {},
                            modifier = Modifier.testTag(tag),
                        )
                    }
                }
            }
        }
    }

    private fun ComposeUiTest.thumbCentre(tag: String): Float {
        val box = onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        val pixels = onRoot().captureToImage().toPixelMap()
        val y = box.center.y.toInt()
        val white =
            (box.left.toInt() until box.right.toInt()).filter { x ->
                pixels[x, y].luminance() > 0.95f
            }
        assertTrue(white.isNotEmpty(), "the white thumb must be visible on the track")
        return (white.first() + white.last()) / 2f
    }
}
