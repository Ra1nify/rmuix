// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.ColorSchemeMode
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.ThemeController
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The small / middle tab style is documented as expressible with existing parameters:
 * `itemSpacing = tablayout_small_min_divider` (20dp) plus a transparent indicator track.
 */
@OptIn(ExperimentalTestApi::class)
class TabRowSmallStyleRenderingTest {
    @Test
    fun smallTabStyleCombinationRenders() =
        runComposeUiTest {
            var selected = 1
            setContent {
                val controller = remember { ThemeController(colorSchemeMode = ColorSchemeMode.Light) }
                RmuixTheme(controller = controller) {
                    Box(Modifier.width(400.dp)) {
                        TabRow(
                            tabs = listOf("One", "Two", "Three"),
                            selectedTabIndex = selected,
                            onTabSelected = { selected = it },
                            itemSpacing = 20.dp,
                            colors = TabRowDefaults.tabRowColors(indicatorTrackColor = Color.Transparent),
                            modifier = Modifier.testTag("tabs"),
                        )
                    }
                }
            }
            waitForIdle()
            val row = onNodeWithTag("tabs").fetchSemanticsNode().boundsInRoot
            assertEquals(40f, row.height, 0.5f, "the small layout keeps the 40dp tab height")
        }
}
