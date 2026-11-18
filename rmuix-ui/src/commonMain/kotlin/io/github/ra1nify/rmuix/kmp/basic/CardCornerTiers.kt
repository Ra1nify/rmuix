// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The ColorOS corner tiers (`couiRoundCorner{XS..XXXXL}`) as three separate radii.
 *
 * `values/dimens.xml` carries **three** draw radii per tier, not one:
 *
 * - `coui_round_corner_<tier>_radius` - the legacy radius;
 * - `coui_round_corner_<tier>_radius_normal_16_1` - the 16.1 "normal" radius;
 * - `coui_round_corner_<tier>_radius_large_16_1` - the 16.1 "large" radius.
 *
 * The legacy `coui_round_corner_<tier>` value is the *display* radius (the number a designer quotes)
 * and is larger than the legacy draw radius for every tier except XS and S.
 *
 * [weight] is `coui_round_corner_<tier>_weight`: `RoundRectDrawable` only takes the smooth-corner path
 * when the weight is non-zero. The shapes do have a smoothness knob now (`SquircleShape.extension`,
 * exposed as `Card(cornerExtension = ...)`), but no source states that the tier weight maps to it
 * verbatim, so the weight is carried here for reference and is **not** consumed by [Card].
 */
enum class CardCornerTier(
    val displayRadius: Dp,
    val legacyRadius: Dp,
    val normalRadius: Dp,
    val largeRadius: Dp,
    val weight: Float,
) {
    XS(displayRadius = 4.dp, legacyRadius = 4.dp, normalRadius = 5.dp, largeRadius = 5.dp, weight = 1.5f),
    S(displayRadius = 8.dp, legacyRadius = 8.dp, normalRadius = 10.dp, largeRadius = 11.dp, weight = 1.5f),
    M(displayRadius = 12.dp, legacyRadius = 9.dp, normalRadius = 15.dp, largeRadius = 17.dp, weight = 0.99f),
    L(displayRadius = 16.dp, legacyRadius = 13.dp, normalRadius = 19.dp, largeRadius = 23.dp, weight = 0.99f),
    XL(displayRadius = 20.dp, legacyRadius = 16.dp, normalRadius = 24.dp, largeRadius = 28.dp, weight = 1.1f),
    XXL(displayRadius = 24.dp, legacyRadius = 19.dp, normalRadius = 29.dp, largeRadius = 34.dp, weight = 1.1f),
    XXXL(displayRadius = 28.dp, legacyRadius = 22.dp, normalRadius = 34.dp, largeRadius = 39.dp, weight = 1.1f),
    XXXXL(displayRadius = 32.dp, legacyRadius = 25.dp, normalRadius = 38.dp, largeRadius = 45.dp, weight = 1.1f),
}

/** Which of the three stock radii a [CardCornerTier] resolves to. */
enum class CardCornerScale {
    /** `coui_round_corner_<tier>_radius` - what pre-16.1 ColorOS drew. */
    LEGACY,

    /** `coui_round_corner_<tier>_radius_normal_16_1`. */
    NORMAL_16_1,

    /** `coui_round_corner_<tier>_radius_large_16_1` - what current ColorOS draws. */
    LARGE_16_1,
}

/** Resolves the draw radius of [tier] under [scale]. */
fun CardCornerTier.radius(scale: CardCornerScale): Dp =
    when (scale) {
        CardCornerScale.LEGACY -> legacyRadius
        CardCornerScale.NORMAL_16_1 -> normalRadius
        CardCornerScale.LARGE_16_1 -> largeRadius
    }
