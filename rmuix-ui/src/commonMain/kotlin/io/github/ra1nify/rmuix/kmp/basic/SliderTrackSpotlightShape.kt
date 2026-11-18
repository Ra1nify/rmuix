// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

@Immutable
internal class SliderTrackSpotlightShape(
    private val trackHeight: Dp,
    private val animatedHeight: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val resting = with(density) { trackHeight.toPx() }
        val current = with(density) { animatedHeight.toPx() }
        val maximum = with(density) { (trackHeight * SLIDER_BACKGROUND_ENLARGE_SCALE).toPx() }
        val painted = sliderPaintedBackgroundHeight(current, resting, maximum)
        val geometry = sliderTrackGeometry(size.width, resting, painted)
        val centerY = (size.height.toInt() / 2).toFloat()
        return Outline.Rounded(
            RoundRect(
                Rect(geometry.backgroundStart, centerY - painted / 2f, geometry.backgroundEnd, centerY + painted / 2f),
                // The track is a capsule, so the radius is half the **painted** height. `current` is the
                // animated height and `painted` is it rounded mid-animation, so they differ by <0.5px.
                CornerRadius(current / 2f),
            ),
        )
    }
}
