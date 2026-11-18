// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.anim

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins the transcription of `COUISpringForce`: `setBounce` maps the bounce to `dampingRatio = 1 - bounce`
 * and `setResponse` maps the response to `naturalFreq = (2*pi / response)^2`.
 *
 * `com.coui.iola.SpringData.getDampingRatio()` halves the bounce, but no Settings class reads it - the
 * animators copy the raw bounce into `COUISpringForce.setBounce`.
 */
class CouiSpringForceTest {
    @Test
    fun dampingRatioIsOneMinusBounce() {
        assertEquals(1f, couiSpringDamping(0f))
        assertEquals(0.7f, couiSpringDamping(0.3f))
        assertEquals(0.8f, couiSpringDamping(0.2f))
        assertEquals(0.85f, couiSpringDamping(0.15f))
        // `bounce` is clamped to [-1, 1], then the ratio to [0.1, 2] for a valid Compose spring.
        assertEquals(2f, couiSpringDamping(-1f))
        assertEquals(0.1f, couiSpringDamping(2f))
    }

    @Test
    fun stiffnessDividesTheFrameworkConstantByResponseSquared() {
        assertEquals(246.6875f, couiSpringStiffness(0.4f), 0.001f)
        assertEquals(438.5555f, couiSpringStiffness(0.3f), 0.001f)
        assertEquals(157.88f, couiSpringStiffness(0.5f), 0.01f)
        // Response is clamped to [0.1, 1] first: 2 -> 1 (39.47, clamped up to 100), 0.01 -> 0.1 (3947).
        assertEquals(100f, couiSpringStiffness(2f))
        assertEquals(3947f, couiSpringStiffness(0.01f), 0.5f)
    }
}
