// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.squircle

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * A [Shape] whose silhouette is the continuous-curvature squircle built by [addSquircleRect].
 *
 * Every other API in this module is a `Modifier`, which leaves callers that need a real [Shape] - such as
 * `Modifier.rmuixSpotlight(shape = …)`, `Modifier.clip`, or `Modifier.border` - with a plain
 * `RoundedCornerShape` that does **not** match the squircle background drawn behind it. This class closes
 * that gap so both silhouettes come from the same path.
 *
 * The outline is an [Outline.Generic] path: resolution independent, but not an analytical rounded rectangle.
 * `Modifier.rmuixSpotlight` handles it explicitly - its `clipOutline` maps `Outline.Generic` to
 * `clipPath(outline.path)`, so passing this shape there clips the glow to the squircle silhouette.
 *
 * @param cornerRadius the corner radius; clamped to half the smaller side by [addSquircleRect].
 * @param extension the corner-tile size as a multiple of [cornerRadius]: 1.0 matches a circular arc, 1.1
 *   is the module default continuous corner. Clamped to
 *   [SquircleDefaults.ExtensionMin]..[SquircleDefaults.ExtensionMax].
 * @param squircleEnabled when `false`, the outline is a plain rounded rectangle of the same radius -
 *   forward `isSquircleEnabled()` so the shape degrades exactly like the modifier APIs.
 */
data class SquircleShape(
    val cornerRadius: Dp,
    val extension: Float = SquircleDefaults.Extension,
    val squircleEnabled: Boolean = true,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val radiusPx = with(density) { cornerRadius.toPx() }
        val path =
            Path().apply {
                addSquircleRect(
                    width = size.width,
                    height = size.height,
                    cornerRadius = radiusPx,
                    extension = extension,
                    squircleEnabled = squircleEnabled,
                )
            }
        return Outline.Generic(path)
    }
}
