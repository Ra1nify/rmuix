// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ButtonDeformTest {
    @Test
    fun maximumScaleUsesRawPixelAreaAndItsNativeFloor() {
        assertEquals(1.2f, buttonDeformMaximumScale(196f, 196f))
        assertEquals(1.03f, buttonDeformMaximumScale(4000f, 4000f))
        assertTrue(buttonDeformMaximumScale(400f, 400f) in 1.03f..1.2f)
        assertTrue(buttonDeformMaximumScale(396f, 132f) < buttonDeformMaximumScale(132f, 44f))
    }

    @Test
    fun axisStretchAndReciprocalCompressionHaveAreaConservingTargets() {
        val horizontal = buttonDeformScale(Offset(100f, 0f), 132f, 44f, 1f)
        val vertical = buttonDeformScale(Offset(0f, -100f), 132f, 44f, 1f)
        assertTrue(horizontal.horizontal > 1f)
        assertTrue(horizontal.vertical < 1f)
        assertEquals(1f, horizontal.horizontal * horizontal.vertical, 0.00001f)
        assertEquals(horizontal.horizontal, vertical.vertical, 0.00001f)
        assertEquals(horizontal.vertical, vertical.horizontal, 0.00001f)
    }

    @Test
    fun diagonalTargetsBlendInLogSpaceByDisplacementWeights() {
        val balanced = buttonDeformScale(Offset(100f, 100f), 132f, 44f, 1f)
        val weighted = buttonDeformScale(Offset(120f, 40f), 132f, 44f, 1f)
        assertEquals(1f, balanced.horizontal, 0.00001f)
        assertEquals(1f, balanced.vertical, 0.00001f)
        assertTrue(weighted.horizontal > weighted.vertical)
        assertEquals(1f, weighted.horizontal * weighted.vertical, 0.00001f)
    }

    @Test
    fun regrabUsesTheDominantDeformAxisWithoutInventingItsPreviousSign() {
        val scale = buttonDeformScale(Offset(-100f, 0f), 132f, 44f, 1f)
        val offset = buttonDeformEffectiveOffset(scale, 132f, 44f, 1f)
        assertEquals(100f, offset.x, 0.001f)
        assertEquals(0f, offset.y)
        assertEquals(Offset.Zero, buttonDeformEffectiveOffset(ButtonDeformScale(1f, 1f), 132f, 44f, 1f))
    }

    @Test
    fun collectorAddsScaleDeviationsInsteadOfMultiplyingThem() {
        assertEquals(1f, buttonCombinedScale(0.9f, 1.1f), 0.000001f)
        assertEquals(0.97f, buttonCombinedScale(0.92f, 1.05f), 0.000001f)
    }

    @Test
    fun pivotTranslationUsesTheCombinedScaleAndActualPixelSize() {
        val left = buttonAnchorTranslation(ButtonDeformAnchor.LeftCenter, 200f, 60f, 0.9f, 0.8f)
        val right = buttonAnchorTranslation(ButtonDeformAnchor.RightCenter, 200f, 60f, 0.9f, 0.8f)
        assertEquals(-10f, left.x, 0.00001f)
        assertEquals(10f, right.x, 0.00001f)
        assertEquals(0f, 100f - 100f * 0.9f + left.x, 0.00001f)
        assertEquals(200f, 100f + 100f * 0.9f + right.x, 0.00001f)
        val centered = buttonAnchorTranslation(ButtonDeformAnchor.Center, 200f, 60f, 0.9f, 0.8f)
        // Tolerance form: the centre anchor can resolve to -0f, which `Offset` equality treats as
        // different from `0f` even though the drawn position is identical.
        assertEquals(0f, centered.x, 0f)
        assertEquals(0f, centered.y, 0f)
    }

    @Test
    fun autoAnchorKeepsNativeAxisPriorityAndRemainsOffByDefault() {
        assertFalse(ButtonTouchMotionConfig().autoDeformAnchor)
        assertEquals(ButtonDeformAnchor.Center, ButtonTouchMotionConfig().deformAnchor)
        assertEquals(ButtonDeformAnchor.LeftCenter, buttonResolveAutoAnchor(Offset(10f, 3f), null))
        assertEquals(ButtonDeformAnchor.RightCenter, buttonResolveAutoAnchor(Offset(-10f, 3f), null))
        assertEquals(ButtonDeformAnchor.BottomCenter, buttonResolveAutoAnchor(Offset(1f, -10f), null))
        assertEquals(ButtonDeformAnchor.TopCenter, buttonResolveAutoAnchor(Offset(10f, 10f), null))
        assertEquals(ButtonDeformAnchor.LeftCenter, buttonResolveAutoAnchor(Offset(10f, 10f), ButtonDeformAnchor.LeftCenter))
        assertNull(buttonResolveAutoAnchor(Offset.Zero, ButtonDeformAnchor.LeftCenter))
    }

    @Test
    fun scaleReturnUsesScaleDeltaRestMagnitudeAndItsOwnSnapThreshold() {
        assertTrue(buttonDeformAtRest(1.0019f))
        assertFalse(buttonDeformAtRest(1.0021f))
        val parameters = buttonDeformHandUp(132f, 44f, 1f, 1000f, 1.05f)
        assertEquals(0.0005357143f, parameters.velocity, 0.000001f)
        assertEquals(0.65f, parameters.bounce)
        assertEquals(0.000375f, buttonDeformSpring(0.15f, 0.15f).visibilityThreshold!!, 0.00000001f)
    }
}
