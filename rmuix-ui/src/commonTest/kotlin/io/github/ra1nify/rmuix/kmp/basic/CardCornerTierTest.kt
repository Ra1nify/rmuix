// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.squircle.SquircleDefaults
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks [CardCornerTier] to the three radius families in `values/dimens.xml`.
 *
 * Every tier carries `coui_round_corner_<tier>_radius`, `_radius_normal_16_1`, `_radius_large_16_1`
 * and `_weight`; the legacy display value `coui_round_corner_<tier>` is kept separately because it is
 * not what the widget draws.
 */
class CardCornerTierTest {
    @Test
    fun cornerExtensionDefaultsToTheSquircleDefault() {
        // The stock "smooth round corner weight" (3) has no known conversion to `extension`, so the
        // default must stay the squircle module's own default rather than a guessed mapping.
        assertEquals(SquircleDefaults.Extension, CardDefaults.CornerExtension)
        assertEquals(1.1f, CardDefaults.CornerExtension)
    }

    @Test
    fun defaultCardRadiusIsTheStockXlRadius() {
        // ?attr/couiRoundCornerXL = coui_round_corner_xl = 20dp, the radius stock card surfaces use.
        assertEquals(20.dp, CardDefaults.CornerRadius)
    }

    @Test
    fun legacyRadiiMatchTheStockDimens() {
        assertEquals(4.dp, CardCornerTier.XS.legacyRadius)
        assertEquals(8.dp, CardCornerTier.S.legacyRadius)
        assertEquals(9.dp, CardCornerTier.M.legacyRadius)
        assertEquals(13.dp, CardCornerTier.L.legacyRadius)
        assertEquals(16.dp, CardCornerTier.XL.legacyRadius)
        assertEquals(19.dp, CardCornerTier.XXL.legacyRadius)
        assertEquals(22.dp, CardCornerTier.XXXL.legacyRadius)
        assertEquals(25.dp, CardCornerTier.XXXXL.legacyRadius)
    }

    @Test
    fun normalSixteenOneRadiiMatchTheStockDimens() {
        assertEquals(5.dp, CardCornerTier.XS.normalRadius)
        assertEquals(10.dp, CardCornerTier.S.normalRadius)
        assertEquals(15.dp, CardCornerTier.M.normalRadius)
        assertEquals(19.dp, CardCornerTier.L.normalRadius)
        assertEquals(24.dp, CardCornerTier.XL.normalRadius)
        assertEquals(29.dp, CardCornerTier.XXL.normalRadius)
        assertEquals(34.dp, CardCornerTier.XXXL.normalRadius)
        assertEquals(38.dp, CardCornerTier.XXXXL.normalRadius)
    }

    @Test
    fun largeSixteenOneRadiiMatchTheStockDimens() {
        assertEquals(5.dp, CardCornerTier.XS.largeRadius)
        assertEquals(11.dp, CardCornerTier.S.largeRadius)
        assertEquals(17.dp, CardCornerTier.M.largeRadius)
        assertEquals(23.dp, CardCornerTier.L.largeRadius)
        assertEquals(28.dp, CardCornerTier.XL.largeRadius)
        assertEquals(34.dp, CardCornerTier.XXL.largeRadius)
        assertEquals(39.dp, CardCornerTier.XXXL.largeRadius)
        assertEquals(45.dp, CardCornerTier.XXXXL.largeRadius)
    }

    @Test
    fun displayRadiiMatchTheStockDimens() {
        assertEquals(4.dp, CardCornerTier.XS.displayRadius)
        assertEquals(8.dp, CardCornerTier.S.displayRadius)
        assertEquals(12.dp, CardCornerTier.M.displayRadius)
        assertEquals(16.dp, CardCornerTier.L.displayRadius)
        assertEquals(20.dp, CardCornerTier.XL.displayRadius)
        assertEquals(24.dp, CardCornerTier.XXL.displayRadius)
        assertEquals(28.dp, CardCornerTier.XXXL.displayRadius)
        assertEquals(32.dp, CardCornerTier.XXXXL.displayRadius)
    }

    @Test
    fun weightAndScaleResolutionMatchTheStockDimens() {
        assertEquals(1.5f, CardCornerTier.XS.weight)
        assertEquals(1.5f, CardCornerTier.S.weight)
        assertEquals(0.99f, CardCornerTier.M.weight)
        assertEquals(0.99f, CardCornerTier.L.weight)
        assertEquals(1.1f, CardCornerTier.XL.weight)
        assertEquals(1.1f, CardCornerTier.XXL.weight)
        assertEquals(1.1f, CardCornerTier.XXXL.weight)
        assertEquals(1.1f, CardCornerTier.XXXXL.weight)
        // The resolver picks the right family per scale.
        assertEquals(13.dp, CardCornerTier.L.radius(CardCornerScale.LEGACY))
        assertEquals(19.dp, CardCornerTier.L.radius(CardCornerScale.NORMAL_16_1))
        assertEquals(23.dp, CardCornerTier.L.radius(CardCornerScale.LARGE_16_1))
        assertEquals(CardCornerScale.LARGE_16_1, CardDefaults.CornerScale)
    }
}
