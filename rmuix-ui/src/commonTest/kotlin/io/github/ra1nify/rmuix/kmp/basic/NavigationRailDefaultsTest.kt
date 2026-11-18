// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigationRailDefaultsTest {
    @Test
    fun collapsedRailMatchesTheCouiNavigationRailTokens() {
        // `coui_navigation_rail_min_width` = 72dp, which is `coui_navigation_rail_menu_width` (64dp)
        // plus the 4dp `coui_side_navigation_item_gap` on both sides.
        assertEquals(72.dp, NavigationRailDefaults.MinWidth)
        assertEquals(64.dp, NavigationRailDefaults.MenuWidth)
        assertEquals(
            NavigationRailDefaults.MinWidth,
            NavigationRailDefaults.MenuWidth + 4.dp * 2,
            "the rail is the item plus its horizontal gaps",
        )
        // `coui_side_navigation_icon_size`
        assertEquals(24.dp, NavigationRailDefaults.IconSize)
        // `coui_side_navigation_rail_item_text_size`
        assertEquals(10.sp, NavigationRailDefaults.LabelFontSize)
        // `coui_navigation_rail_text_margin_top`
        assertEquals(2.dp, NavigationRailDefaults.IconTextSpacing)
        // `coui_side_navigation_item_background_radius`
        assertEquals(8.dp, NavigationRailDefaults.ItemBackgroundRadius)
        // `coui_navigation_margin_vertical`
        assertEquals(12.dp, NavigationRailDefaults.ItemVerticalPadding)
    }

    @Test
    fun expandedRailKeepsTheMaterialTokens() {
        // COUI has no expanded rail; these two stay on the M3 values.
        assertEquals(220.dp, NavigationRailDefaults.ExpandedWidth)
        assertEquals(14.sp, NavigationRailDefaults.ExpandedLabelFontSize)
        assertEquals(16.dp, NavigationRailDefaults.ExpandedItemCornerRadius)
    }
}
