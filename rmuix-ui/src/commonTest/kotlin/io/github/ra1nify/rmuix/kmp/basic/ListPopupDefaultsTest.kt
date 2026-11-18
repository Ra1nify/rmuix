// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.PopupPositionProvider
import kotlin.test.Test
import kotlin.test.assertEquals

class ListPopupDefaultsTest {
    @Test
    fun sharedMaskTokensMatchTheTouchRecyclerView() {
        // COUITouchRecyclerView: vertical 28dp (OS > 37 + blur) / 20dp; horizontal 17|12dp icon-only,
        // 23|16dp with text; 8dp horizontal inset; smooth weight 3; 40dp icon-only row height.
        assertEquals(28.dp, ListPopupStockSharedMaskRadius16_1)
        assertEquals(20.dp, ListPopupStockSharedMaskRadius)
        assertEquals(17.dp, ListPopupStockSharedMaskRadiusIconOnly16_1)
        assertEquals(12.dp, ListPopupStockSharedMaskRadiusIconOnly)
        assertEquals(23.dp, ListPopupStockSharedMaskRadiusWithText16_1)
        assertEquals(16.dp, ListPopupStockSharedMaskRadiusWithText)
        assertEquals(8.dp, ListPopupStockSharedMaskMarginHorizontal)
        assertEquals(8.dp, ListPopupStockSharedMaskMarginVertical)
        assertEquals(3f, LIST_POPUP_SHARED_MASK_SMOOTH_WEIGHT)
        assertEquals(40.dp, ListPopupStockHorizontalIconOnlyHeight)
    }

    @Test
    fun cascadingSubmenuOverlapsTheMainMenuBy14dpOnBothAxes() {
        // PopupMenuLocateHelper.15, LTR branch: the stock rule prefers the flipped placement
        // `mainMenu.left + overlap - subMenuWidth` whenever it still fits inside the window's left edge,
        // and only falls back to `mainMenu.right - overlap` when it does not.
        assertEquals(14.dp, ListPopupDefaults.CascadingSubmenuHorizontalOverlap)
        assertEquals(14.dp, ListPopupDefaults.CascadingSubmenuVerticalOverlap)
        val provider = ListPopupDefaults.cascadingSubmenuPositionProvider(verticalOverlapPx = 14, horizontalOverlapPx = 14)
        val anchor = IntRect(left = 400, top = 200, right = 600, bottom = 260)
        val window = IntRect(left = 0, top = 0, right = 1080, bottom = 2000)
        val offset =
            provider.calculatePosition(
                anchorBounds = anchor,
                windowBounds = window,
                layoutDirection = LayoutDirection.Ltr,
                popupContentSize = IntSize(width = 200, height = 400),
                popupMargin = IntRect(0, 0, 0, 0),
                alignment = PopupPositionProvider.Align.End,
            )
        // anchor top 200 - 14 vertical overlap
        assertEquals(186, offset.y)
        // flipped placement: anchor left 400 + 14 - submenu width 200
        assertEquals(214, offset.x)
    }

    @Test
    fun cascadingSubmenuMirrorsItsHorizontalEdgeInRtl() {
        // RTL keeps the same two candidates but checks the right edge instead of the left one.
        val provider = ListPopupDefaults.cascadingSubmenuPositionProvider(verticalOverlapPx = 0, horizontalOverlapPx = 14)
        val anchor = IntRect(left = 400, top = 200, right = 600, bottom = 260)
        val window = IntRect(left = 0, top = 0, right = 1080, bottom = 2000)
        val offset =
            provider.calculatePosition(
                anchorBounds = anchor,
                windowBounds = window,
                layoutDirection = LayoutDirection.Rtl,
                popupContentSize = IntSize(width = 200, height = 400),
                popupMargin = IntRect(0, 0, 0, 0),
                alignment = PopupPositionProvider.Align.End,
            )
        // 600 - 14 + 200 = 786 < 1080, so the sub-menu starts at the right edge minus the overlap.
        assertEquals(586, offset.x)
    }

    @Test
    fun cascadingSubmenuDoesNotLiftOnRegularWindows() {
        // Non-compact windows use the `mSubMenuLocateRule` branch that returns the anchor top unchanged.
        val provider = ListPopupDefaults.cascadingSubmenuPositionProvider(verticalOverlapPx = 0, horizontalOverlapPx = 14)
        val anchor = IntRect(left = 400, top = 200, right = 600, bottom = 260)
        val window = IntRect(left = 0, top = 0, right = 1080, bottom = 2000)
        val offset =
            provider.calculatePosition(
                anchorBounds = anchor,
                windowBounds = window,
                layoutDirection = LayoutDirection.Ltr,
                popupContentSize = IntSize(width = 200, height = 400),
                popupMargin = IntRect(0, 0, 0, 0),
                alignment = PopupPositionProvider.Align.End,
            )
        assertEquals(200, offset.y)
    }

    @Test
    fun cascadingSubmenuLiftsOnCompactWindows() {
        // PopupMenuLocateHelper.getOffsetY() places the sub-menu at
        // `subMenuAnchorTopAfterMainMenuRelocated - mVerticalOverlapBetweenMainAndSubMenu`, and the overlap
        // is `coui_popup_list_window_vertical_overlap_between_main_and_sub_menu` = 14dp. The provider works
        // in pixels, so a 14dp overlap at density 1 is 14px.
        assertEquals(14.dp, ListPopupDefaults.CascadingSubmenuHorizontalOverlap)
        assertEquals(14.dp, ListPopupDefaults.CascadingSubmenuVerticalOverlap)
        // Horizontal overlap is disabled explicitly so the assertion isolates the vertical lift.
        val provider = ListPopupDefaults.cascadingSubmenuPositionProvider(verticalOverlapPx = 14, horizontalOverlapPx = 0)
        val anchor = IntRect(left = 100, top = 200, right = 300, bottom = 260)
        val window = IntRect(left = 0, top = 0, right = 1080, bottom = 2000)
        val offset =
            provider.calculatePosition(
                anchorBounds = anchor,
                windowBounds = window,
                layoutDirection = LayoutDirection.Ltr,
                popupContentSize = IntSize(width = 200, height = 400),
                popupMargin = IntRect(0, 0, 0, 0),
                alignment = PopupPositionProvider.Align.End,
            )
        // anchor top 200 - 14 overlap
        assertEquals(186, offset.y)
        // to the right of the row
        assertEquals(300, offset.x)
    }

    @Test
    fun windowWidthsMatchThePopupListTokens() {
        // coui_popup_list_window_min_width / coui_popup_list_window_max_width
        assertEquals(168.dp, ListPopupStockMinWidth)
        assertEquals(256.dp, ListPopupStockMaxWidth)
        // coui_popup_window_background.xml
        assertEquals(12.dp, ListPopupStockCornerRadius)
        assertEquals(Color(0xFFFFFFFF), listPopupBackground(false))
        assertEquals(Color(0xFF333333), listPopupBackground(true))
        // coui_popup_list_window_width_with_icon (submenu menus)
        assertEquals(196.dp, ListPopupStockWidthWithIcon)
        // The defaults object feeds the surface and the column clamp.
        assertEquals(168.dp, ListPopupDefaults.MinWidth)
        assertEquals(256.dp, ListPopupDefaults.MaxWidth)
        // coui_popup_list_padding_vertical: the stock menu pads its list by 8dp top and bottom.
        assertEquals(8.dp, ListPopupStockVerticalPadding)
        assertEquals(8.dp, ListPopupDefaults.VerticalContentPadding)
        // Divider tokens: 0.5dp hairline inset 20dp / 52dp, plus a 4dp group strip in #14000000.
        assertEquals(0.5.dp, ListPopupDefaults.DividerHeight)
        assertEquals(20.dp, ListPopupDefaults.DividerMarginHorizontal)
        assertEquals(52.dp, ListPopupDefaults.DividerStartWithIcon)
        assertEquals(4.dp, ListPopupDefaults.GroupDividerHeight)
        assertEquals(Color(0x14000000), ListPopupDefaults.GroupDividerColor)
        assertEquals(Color(0x1F000000), listPopupDividerColor(false))
        assertEquals(Color(0x33FFFFFF), listPopupDividerColor(true))
    }
}
