// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class SideNavigationDrawerDefaultsTest {
    @Test
    fun drawerMatchesCouiSideNavigationBar() {
        // coui_side_navigation_bar_min_width is the *temporary* (phone) width, not a lower bound
        assertEquals(300.dp, SideNavigationDrawerDefaults.Width)
        // coui_side_navigation_bar_max_width caps the medium/large branch
        assertEquals(280.dp, SideNavigationDrawerDefaults.MaxWidth)
        // COUISideNavigationBar.DRAWER_WIDTH_PROPORTION
        assertEquals(0.382f, SideNavigationDrawerDefaults.WIDTH_PROPORTION)
        // coui_side_navigation_drawer_divider_width
        assertEquals(0.33.dp, SideNavigationDrawerDefaults.DividerWidth)
        // ViewDragHelper.EDGE_SIZE, not a COUI dimen
        assertEquals(20.dp, SideNavigationDrawerDefaults.EdgeWidth)
    }

    @Test
    fun settleDurationsAreDeclared() {
        assertEquals(250, SideNavigationDrawerDefaults.OPEN_MILLIS)
        assertEquals(200, SideNavigationDrawerDefaults.CLOSE_MILLIS)
    }
}
