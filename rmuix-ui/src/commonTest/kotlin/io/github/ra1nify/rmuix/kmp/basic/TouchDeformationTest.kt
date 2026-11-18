// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Pins the touch-deformation math that `COUIDeformAnimatorUnit` / `TouchMotionRubberBand` use.
 */
class TouchDeformationTest {
    private val maxDistance = 150f
    private val curve = 0.55f
    private val maxScale = 1.2f

    @Test
    fun rubberBandStartsAtZeroAndSaturates() {
        assertEquals(0f, rubberBand(offset = 0f, curveRatio = curve, maxDistancePx = maxDistance))
        // 1 - 1 / (|o| * curve / max + 1), scaled by max
        val atTen =
            rubberBand(offset = 10f, curveRatio = curve, maxDistancePx = maxDistance)
        assertEquals(maxDistance * (1f - 1f / (1f + 10f * curve / maxDistance)), atTen, 1e-4f)
        assertTrue(atTen in 0f..10f)
        val atHundred =
            rubberBand(offset = 100f, curveRatio = curve, maxDistancePx = maxDistance)
        assertTrue(atHundred > atTen)
        assertTrue(atHundred < maxDistance)
        // sign is preserved, magnitude symmetric
        assertEquals(-atTen, rubberBand(offset = -10f, curveRatio = curve, maxDistancePx = maxDistance), 1e-4f)
    }

    @Test
    fun rubberBandInverseRoundTrips() {
        val offset = 24f
        val applied =
            rubberBand(offset = offset, curveRatio = curve, maxDistancePx = maxDistance)
        val back =
            rubberBandInverse(result = applied, curveRatio = curve, maxDistancePx = maxDistance)
        assertEquals(offset, back, 0.01f)
        // beyond maxDistance the inverse clamps to maxDistance with the sign of the input
        assertEquals(maxDistance, rubberBandInverse(result = maxDistance * 3f, curveRatio = curve, maxDistancePx = maxDistance))
    }

    @Test
    fun directionScaleGrowsMainAxisAndShrinksCrossAxis() {
        val neutral =
            deformDirectionScale(
                distancePx = 0f,
                curveRatio = curve,
                maxDistancePx = maxDistance,
                maxScale = maxScale,
                mainAxisIsX = true,
            )
        assertEquals(TouchDeformScale(1f, 1f), neutral)
        val x =
            deformDirectionScale(
                distancePx = 40f,
                curveRatio = curve,
                maxDistancePx = maxDistance,
                maxScale = maxScale,
                mainAxisIsX = true,
            )
        assertTrue(x.scaleX > 1f, "main axis should stretch, was ${x.scaleX}")
        assertTrue(x.scaleX <= maxScale)
        assertEquals(1f / x.scaleX, x.scaleY, 1e-4f)
        // the same distance along Y swaps the axes
        val y =
            deformDirectionScale(
                distancePx = 40f,
                curveRatio = curve,
                maxDistancePx = maxDistance,
                maxScale = maxScale,
                mainAxisIsX = false,
            )
        assertEquals(x.scaleY, y.scaleX, 1e-4f)
        assertEquals(x.scaleX, y.scaleY, 1e-4f)
    }

    @Test
    fun deformationKeepsTheAreaAndStaysWithinTheCap() {
        val both =
            touchDeformScale(
                dxPx = 30f,
                dyPx = 18f,
                curveRatio = curve,
                maxDistancePx = maxDistance,
                maxScale = maxScale,
            )
        // The diagonal drag stretches along the dominant axis and thins the cross axis, so the area
        // stays constant (the stock `blendAreaConservingScaleFactors` contract).
        assertTrue(both.scaleX > 1f)
        assertTrue(both.scaleY < 1f)
        assertTrue(abs(both.scaleX * both.scaleY - 1f) < 1e-3f)
        assertTrue(both.scaleX <= maxScale + 1e-4f)
        assertTrue(both.scaleY >= 1f / (maxScale + 1e-4f))

        val onlyX =
            touchDeformScale(
                dxPx = 30f,
                dyPx = 0f,
                curveRatio = curve,
                maxDistancePx = maxDistance,
                maxScale = maxScale,
            )
        assertTrue(onlyX.scaleX > 1f)
        assertTrue(abs(onlyX.scaleX * onlyX.scaleY - 1f) < 1e-3f)

        // a diagonal drag is a geometric blend of the two axes, so it stays between them
        assertTrue(both.scaleX <= onlyX.scaleX + 1e-4f)
    }
}
