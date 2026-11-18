// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class NumberPickerGroupRenderingTest {
    @Test
    fun oneHairlineRunsAcrossTheColumnGap() =
        runComposeUiTest {
            setContent {
                RmuixTheme {
                    Box(Modifier.size(320.dp, 200.dp).background(Color.White)) {
                        NumberPickerGroup(modifier = Modifier.testTag("group")) {
                            NumberPicker(3, {}, range = 1..30, modifier = Modifier.width(96.dp).testTag("y"))
                            NumberPicker(6, {}, range = 1..12, modifier = Modifier.width(96.dp).testTag("m"))
                            NumberPicker(15, {}, range = 1..31, modifier = Modifier.width(62.dp).testTag("d"))
                        }
                    }
                }
            }
            waitForIdle()
            val group = onNodeWithTag("group").fetchSemanticsNode().boundsInRoot
            val year = onNodeWithTag("y").fetchSemanticsNode().boundsInRoot
            // A single picker is 135dp of rows plus 12dp padding on each side.
            assertTrue(abs(group.height - 159f) <= 1.5f, "group height should be 159dp, was ${group.height}")
            val pixels = onRoot().captureToImage().toPixelMap()
            val lineY = (group.center.y - 22.5f).roundToInt()
            // The gap between the year and month columns: a per-column line would be inset 16dp here.
            val boundaryX = (year.right - 2).roundToInt()
            val dark =
                ((lineY - 2)..(lineY + 2)).any { y -> pixels[boundaryX, y].red < 0.98f }
            assertTrue(dark, "the group hairline must run through the column gap at x=$boundaryX")
        }

    @Test
    fun columnsStayLeftToRightInRtl() =
        runComposeUiTest {
            setContent {
                RmuixTheme {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        NumberPickerGroup {
                            NumberPicker(3, {}, range = 1..30, modifier = Modifier.width(62.dp).testTag("first"))
                            NumberPicker(6, {}, range = 1..12, modifier = Modifier.width(62.dp).testTag("second"))
                        }
                    }
                }
            }
            waitForIdle()
            val first = onNodeWithTag("first").fetchSemanticsNode().boundsInRoot
            val second = onNodeWithTag("second").fetchSemanticsNode().boundsInRoot
            assertTrue(first.left < second.left, "the first column must stay leftmost in RTL")
        }
}
