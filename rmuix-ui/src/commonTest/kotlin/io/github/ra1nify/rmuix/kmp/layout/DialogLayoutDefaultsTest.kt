// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.layout

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class DialogLayoutDefaultsTest {
    @Test
    fun cornerModeResolvesTheFourStockRadii() {
        // COUIAlertDialogMaxLinearLayout.setOutLineProvider() has four branches on the XXXXL tier.
        assertEquals(32.dp, DialogDefaults.cornerRadius(DialogDefaults.CornerMode.Legacy))
        assertEquals(25.dp, DialogDefaults.cornerRadius(DialogDefaults.CornerMode.Os15Smooth))
        assertEquals(32.dp, DialogDefaults.cornerRadius(DialogDefaults.CornerMode.Os16NonSmooth))
        assertEquals(45.dp, DialogDefaults.cornerRadius(DialogDefaults.CornerMode.LargeOS161))
        // Smooth corners are off by default (anim_level defaults to 3), so the dialog renders 32dp.
        assertEquals(32.dp, DialogDefaults.CornerRadius)
        assertEquals(DialogDefaults.CornerMode.Os16NonSmooth, DialogDefaults.DefaultCornerMode)
    }

    @Test
    fun centeredDialogMetricsMatchTheStockDefinition() {
        assertEquals(392.dp, DialogDefaults.MaxWidth)
        // Smooth corners are off by default (anim_level defaults to 3), so the dialog renders 32dp.
        assertEquals(32.dp, DialogDefaults.CornerRadius)
        assertEquals(DpSize(16.dp, 32.dp), DialogDefaults.outsideMargin)
    }

    @Test
    fun bottomSheetUsesThePlainCornerTokenNotTheDialogRadius() {
        assertEquals(32.dp, BottomSheetDefaults.cornerRadius)
    }

    @Test
    fun centerDialogSpacingMatchesTheStockLayout() {
        // coui_alert_dialog_title_margin_bottom / coui_alert_dialog_scroll_padding_bottom_message_tallDialog
        assertEquals(6.dp, DialogDefaults.TitleMarginBottom)
        assertEquals(16.dp, DialogDefaults.MessageScrollPaddingBottom)
        // coui_center_alert_dialog_button_panel.xml: coui_alert_dialog_button_height 44dp.
        assertEquals(44.dp, DialogDefaults.ButtonBarHeight)
        // COUIAlertDialogBottomButtonSecondary: coui_alert_dialog_button_horizontal_padding 12dp and
        // coui_bottom_alert_dialog_vertical_button_padding_vertical_new 4dp.
        assertEquals(12.dp, DialogDefaults.ButtonHorizontalPadding)
        assertEquals(4.dp, DialogDefaults.ButtonVerticalPadding)
    }

    @Test
    fun dialogScrollCapsMatchTheBuilderDimens() {
        // coui_alert_dialog_builder_title_scroll_max_height / _content_max_height
        assertEquals(80.dp, DialogDefaults.TitleScrollMaxHeight)
        assertEquals(80.dp, DialogDefaults.MessageScrollMaxHeight)
    }

    @Test
    fun dialogSurfaceMatchesTheBackgroundDrawable() {
        // coui_alert_dialog_background -> ?attr/couiColorSurface = #FFFFFF / #1E1E1E.
        assertEquals(Color(0xFFFFFFFF), dialogSurface(false))
        assertEquals(Color(0xFF1E1E1E), dialogSurface(true))
    }

    @Test
    fun dialogTextNeutralsMatchTheDialogStyles() {
        assertEquals(Color(0xE6000000), dialogTitle(false))
        assertEquals(Color(0xE6FFFFFF), dialogTitle(true))
        assertEquals(Color(0x8A000000), dialogMessage(false))
        assertEquals(Color(0x8AFFFFFF), dialogMessage(true))
    }

    @Test
    fun dragHandleFollowsThePanelDragViewResources() {
        assertEquals(36.dp, BottomSheetHandleWidth)
        assertEquals(4.dp, BottomSheetHandleHeight)
        assertEquals(2.dp, BottomSheetHandleRadius)
        assertEquals(12.dp, BottomSheetHandleStripHeight)
        assertEquals(4.dp, BottomSheetHandleStripTopMargin)
        assertEquals(70.dp, BottomSheetHandlePressWidth)
        assertEquals(20.dp, BottomSheetHandlePressHeight)
        assertEquals(16.dp, BottomSheetHandlePressRadius)
        assertEquals(Color(0x29000000), bottomSheetHandle(false))
        assertEquals(Color(0x40FFFFFF), bottomSheetHandle(true))
        assertEquals(Color(0x1F000000), bottomSheetHandlePress(false))
        assertEquals(Color(0x33FFFFFF), bottomSheetHandlePress(true))
    }
}
