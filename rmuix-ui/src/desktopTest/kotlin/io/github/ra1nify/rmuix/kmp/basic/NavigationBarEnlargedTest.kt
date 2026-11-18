// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Check
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Pins the `navigationType=enlarge` variant: 70dp items, and the selected item's icon grows to 64dp
 * (`coui_navigation_enlarge_item_height` / `_icon_size`) while the normal bar stays at 56dp/24dp.
 */
@OptIn(ExperimentalTestApi::class)
class NavigationBarEnlargedTest {
    @Test
    fun enlargedBarUsesSeventyDpItems() =
        runComposeUiTest {
            setContent {
                Box(Modifier.fillMaxWidth()) {
                    NavigationBar(enlarged = true, showDivider = false) {
                        repeat(3) { index ->
                            NavigationBarItem(
                                selected = index == 0,
                                onClick = {},
                                icon = RmuixIcons.Basic.Check,
                                label = "Item $index",
                                modifier = Modifier.testTag("item$index"),
                                colors = NavigationBarDefaults.navigationBarItemColors(selectedContentColor = Color.Black),
                            )
                        }
                    }
                }
            }
            val node = onNodeWithTag("item0").fetchSemanticsNode()
            val heightDp = node.size.height.toFloat() / density.density
            assertTrue(abs(heightDp - 70f) <= 1f, "enlarged items are 70dp tall (was $heightDp)")
        }

    @Test
    fun normalBarStaysFiftySixDp() =
        runComposeUiTest {
            setContent {
                Box(Modifier.fillMaxWidth()) {
                    NavigationBar(showDivider = false) {
                        repeat(3) { index ->
                            NavigationBarItem(
                                selected = index == 0,
                                onClick = {},
                                icon = RmuixIcons.Basic.Check,
                                label = "Item $index",
                                modifier = Modifier.testTag("item$index"),
                                colors = NavigationBarDefaults.navigationBarItemColors(selectedContentColor = Color.Black),
                            )
                        }
                    }
                }
            }
            val node = onNodeWithTag("item0").fetchSemanticsNode()
            val heightDp = node.size.height.toFloat() / density.density
            assertTrue(abs(heightDp - 56f) <= 1f, "the normal item is 56dp tall (was $heightDp)")
        }
}
