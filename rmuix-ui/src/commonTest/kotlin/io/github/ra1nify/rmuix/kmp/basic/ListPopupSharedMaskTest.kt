// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.IntRect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ListPopupSharedMaskTest {
    private val rows =
        listOf(
            IntRect(0, 0, 200, 40),
            IntRect(0, 40, 200, 80),
            IntRect(0, 80, 200, 120),
        )

    @Test
    fun resolvesTheRowUnderThePointer() {
        assertEquals(0, sharedMaskIndexAt(rows, 0f))
        assertEquals(0, sharedMaskIndexAt(rows, 39.9f))
        assertEquals(1, sharedMaskIndexAt(rows, 40f))
        assertEquals(2, sharedMaskIndexAt(rows, 119.9f))
        assertNull(sharedMaskIndexAt(rows, -1f))
        assertNull(sharedMaskIndexAt(rows, 120f))
        assertNull(sharedMaskIndexAt(emptyList(), 10f))
    }
}
