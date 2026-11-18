// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class DividerDefaultsTest {
    @Test
    fun dividerUsesTheStockHairline() {
        // content_divider_width
        assertEquals(0.33.dp, DividerDefaults.Thickness)
        // couiColorDivider
        assertEquals(Color(0x1F000000), dividerStockColor(false))
        assertEquals(Color(0x33FFFFFF), dividerStockColor(true))
    }

    @Test
    fun dividerSpansTheFullWidthByDefault() {
        // A standalone ColorOS divider is not indented; card-list rows inset theirs instead
        // (BasicComponentGroupDefaults.DividerStart / DividerEnd = 32dp).
        assertEquals(0.dp, DividerDefaults.StartIndent)
        assertEquals(0.dp, DividerDefaults.EndIndent)
    }
}
