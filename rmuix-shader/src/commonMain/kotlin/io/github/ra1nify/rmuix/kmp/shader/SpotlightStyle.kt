// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.shader

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import kotlin.math.PI

enum class SpotlightStyle {
    Capsule,
    Circle,
    RoundedRectangle,
    Switch,
    ChipTab,
    OpaqueSmall,
    OpaqueMedium,
    OpaqueSecondary,
    TransparentMedium,
}

@Immutable
internal data class SpotlightParameters(
    val blendMode: BlendMode,
    val endIntensity: Float,
    val startRadius: Float,
    val endRadius: Float,
    val lightHeight: Float,
    val ambient: Float,
    val diffuse: Float,
    val noise: Float = 0f,
    val dither: Float = 0f,
)

internal fun spotlightParameters(
    style: SpotlightStyle,
    dark: Boolean,
): SpotlightParameters =
    when (style) {
        SpotlightStyle.Capsule -> {
            SpotlightParameters(BlendMode.Overlay, 1f, 100f, 400f, 80f, 0.1f, 0.5f)
        }

        SpotlightStyle.Circle -> {
            SpotlightParameters(BlendMode.Plus, 0.2f, 100f, 600f, 200f, 0.1f, 0.5f)
        }

        SpotlightStyle.RoundedRectangle -> {
            if (dark) {
                SpotlightParameters(BlendMode.Plus, 0.4f, 0f, 600f, 100f, 0.1f, 0.5f, 0.02f, 1f)
            } else {
                SpotlightParameters(BlendMode.Plus, 1.3f, 100f, 600f, 120f, 0.2f, 0.2f, 0.08f, 1f)
            }
        }

        SpotlightStyle.Switch -> {
            SpotlightParameters(BlendMode.Overlay, 0.8f, 100f, 200f, 100f, 0.1f, 0.5f)
        }

        SpotlightStyle.ChipTab -> {
            SpotlightParameters(if (dark) BlendMode.SrcOver else BlendMode.Plus, if (dark) 0.3f else 0.2f, 100f, 600f, 200f, 0.1f, 0.5f)
        }

        SpotlightStyle.OpaqueSmall -> {
            SpotlightParameters(BlendMode.Overlay, if (dark) 0.5f else 0.6f, 100f, 500f, 200f, 0.4f, 0.4f, 0.08f)
        }

        SpotlightStyle.OpaqueMedium -> {
            SpotlightParameters(BlendMode.Overlay, if (dark) 0.8f else 0.6f, 100f, 500f, 200f, 0.4f, 0.4f, 0.08f)
        }

        SpotlightStyle.OpaqueSecondary -> {
            SpotlightParameters(
                if (dark) BlendMode.Plus else BlendMode.Overlay,
                if (dark) 0.15f else 1f,
                100f,
                500f,
                200f,
                0.4f,
                0.4f,
                0.08f,
            )
        }

        SpotlightStyle.TransparentMedium -> {
            SpotlightParameters(BlendMode.Overlay, if (dark) 0.3f else 0.5f, 100f, 500f, 200f, 0.4f, 0.4f, 0.08f)
        }
    }

@Immutable
internal data class SpotlightFrame(
    val center: Offset,
    val radius: Float,
    val intensity: Float,
)

internal fun spotlightFrame(
    progress: Float,
    extinguishing: Boolean,
    position: Offset,
    size: Size,
    parameters: SpotlightParameters,
): SpotlightFrame {
    val fraction = progress.coerceIn(0f, 1f)
    val radius =
        if (progress == 0f) {
            0f
        } else {
            parameters.startRadius + (parameters.endRadius - parameters.startRadius) * fraction +
                if (extinguishing && progress > 0f) parameters.endRadius * 2f * (1f - fraction) else 0f
        }
    return SpotlightFrame(Offset(position.x * size.width, position.y * size.height), radius, parameters.endIntensity * fraction)
}

internal fun spotlightProgressSpring(glow: Boolean): SpringSpec<Float> {
    val response = if (glow) 0.15 else 0.4
    val frequency = 2.0 * PI / response
    return spring(dampingRatio = 1f, stiffness = (frequency * frequency).toFloat(), visibilityThreshold = 0.000075f)
}

internal fun spotlightPositionSpring(): SpringSpec<Float> {
    val frequency = 2.0 * PI / 0.35
    return spring(dampingRatio = 0.85f, stiffness = (frequency * frequency).toFloat(), visibilityThreshold = 0.000075f)
}
