// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.icon.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons

/** The magnifier ColorOS draws inside a search bar. */
val RmuixIcons.Basic.Search: ImageVector
    get() = searchIcon

private val searchIcon: ImageVector by lazy {
    ImageVector
        .Builder("Search", 20.dp, 20.dp, 20f, 20f)
        .apply {
            // path { } takes a PathBuilder lambda, not a node list; the stock path data is a node
            // list, so it goes through ddPath instead.
            addPath(
                pathData = addPathNodes(STOCK_SEARCH_PATH),
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd,
            )
        }.build()
}

/**
 * `android:pathData` of `coui_search_view_icon_normal.xml`, verbatim.
 *
 * The stock magnifier is a single `evenOdd` fill in a 20dp x 20dp viewport: a ring 1.4dp wide
 * (outer circle 1.422 -> 14.96) plus a round-capped handle running to (18.348, 18.348).
 */
private const val STOCK_SEARCH_PATH =
    "M2.822,8.191C2.822,5.226 5.226,2.822 8.191,2.822C11.156,2.822 13.56,5.226 13.56,8.191C13.56,11.156 " +
        "11.156,13.56 8.191,13.56C5.226,13.56 2.822,11.156 2.822,8.191ZM8.191,1.422C4.452,1.422 1.422,4.452 " +
        "1.422,8.191C1.422,11.929 4.452,14.96 8.191,14.96C9.735,14.96 11.159,14.442 12.298,13.571L17.075,18.348C" +
        "17.426,18.699 17.996,18.699 18.347,18.348C18.699,17.997 18.699,17.427 18.347,17.075L13.571,12.299C14.442" +
        ",11.16 14.96,9.736 14.96,8.191C14.96,4.452 11.929,1.422 8.191,1.422Z"
