// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.squircle

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * `SquircleShape` must produce the same silhouette the `squircleSurface` / `squircleClip` modifiers draw, so
 * that `rmuixSpotlight(shape = …)` and the background it sits on share one outline.
 */
class SquircleShapeTest {
    private val density = Density(1f)

    @Test
    fun createsAGenericOutlineAtTheRequestedSize() {
        val outline =
            SquircleShape(12.dp).createOutline(
                size = Size(100f, 60f),
                layoutDirection = LayoutDirection.Ltr,
                density = density,
            )
        assertIs<Outline.Generic>(outline)
        val bounds = outline.path.getBounds()
        assertEquals(0f, bounds.left)
        assertEquals(0f, bounds.top)
        assertEquals(100f, bounds.right)
        assertEquals(60f, bounds.bottom)
    }

    @Test
    fun fallsBackToARoundedRectWhenSquircleIsDisabled() {
        val outline =
            SquircleShape(12.dp, squircleEnabled = false).createOutline(
                size = Size(100f, 60f),
                layoutDirection = LayoutDirection.Ltr,
                density = density,
            )
        assertIs<Outline.Generic>(outline)
        assertEquals(100f, outline.path.getBounds().right)
    }

    @Test
    fun squircleEnabledIsCarriedIntoTheShape() {
        // `squircleSurface` falls back to a RoundedCornerShape clip when the shader is unavailable, so a
        // caller that shares one SquircleShape with it must be able to flip the flag in lock-step.
        assertTrue(SquircleShape(12.dp).squircleEnabled)
        assertFalse(SquircleShape(12.dp, squircleEnabled = false).squircleEnabled)
    }

    @Test
    fun defaultExtensionIsTheModuleDefault() {
        assertEquals(SquircleDefaults.Extension, SquircleShape(12.dp).extension)
        assertEquals(1.1f, SquircleDefaults.Extension)
    }
}
