// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * The shape mode of a [RoundImage] (`COUIRoundImageView.mType`).
 *
 * Stock constants: `CIRCLE = 0`, `ROUND = 1`, `SHADOW = 2`; the layout attribute is `couiType`
 * (`round` / `circle` / `shadow`).
 */
enum class RoundImageType {
    /** `COUIRoundImageView.CIRCLE`: clipped to a circle. */
    Circle,

    /** `COUIRoundImageView.ROUND`: clipped to a rounded rectangle with [RoundImageDefaults.Radius]. */
    Round,

    /** `COUIRoundImageView.SHADOW`: a circle with the stock shadow ring around it. */
    Shadow,
}

/** Colours of a [RoundImage]. */
@Immutable
data class RoundImageColors(
    val borderColor: Color,
    val outCircleColor: Color,
)

/** Defaults for [RoundImage], taken from `COUIRoundImageView`. */
object RoundImageDefaults {
    /**
     * The `ROUND` type corner radius: `couiBorderRadius` defaults to **1dp** in
     * `COUIRoundImageView(Context, AttributeSet)` (`TypedValue.applyDimension(1, 1f, ...)`), and only 19
     * of the 84 `couiType="round"` layouts override it (most with `coui_preference_card_radius` = 12dp).
     *
     * `coui_roundimageview_default_radius` (42dp) is **not** a corner radius: it is `mWidth`, the fallback
     * width of the `CIRCLE` type when the view has not been measured yet (`onMeasure()` sets
     * `mWidth = min(w, h)`, `mRadius = mWidth / 2`). See [CircleFallbackSize].
     */
    val Radius = 1.dp

    /** `coui_roundimageview_default_radius`: fallback circle width when no layout size is given. */
    val CircleFallbackSize = 42.dp

    /** `COUIRoundImageView.mBorderWidth` (2px at the stock density): the optional border stroke. */
    val BorderWidth = 1.dp

    /** `BORDER_CIRCLE_WIDTH` (5px): the shadow ring width budget of the `SHADOW` type. */
    val ShadowRingWidth = 2.dp

    /** `coui_roundimageView_src_width`: the default source size the shadow ring adds around. */
    val ShadowSourceSize = 60.dp

    /** The stock border colour (`coui_border`; the dark counterpart is `#26FFFFFF`). */
    internal fun border(dark: Boolean): Color = if (dark) Color(0x26FFFFFF) else Color(0x26000000)

    /** `coui_roundimageview_outcircle_color` / `_dark`. */
    internal fun outCircle(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1A000000)

    /** The stock colours, keyed on the current theme. */
    @Composable
    fun colors(
        borderColor: Color = border(isRoundImageDarkTheme()),
        outCircleColor: Color = outCircle(isRoundImageDarkTheme()),
    ): RoundImageColors =
        remember(borderColor, outCircleColor) {
            RoundImageColors(borderColor = borderColor, outCircleColor = outCircleColor)
        }
}

@Composable
private fun isRoundImageDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/** The clip shape of [type] (`COUIRoundImageView.updatePath()`): a circle for `CIRCLE` / `SHADOW`, else [radius]. */
internal fun roundImageShape(
    type: RoundImageType,
    radius: Dp,
): Shape =
    when (type) {
        RoundImageType.Circle, RoundImageType.Shadow -> CircleShape
        RoundImageType.Round -> RoundedCornerShape(radius)
    }

/**
 * The ColorOS rounded image: `COUIRoundImageView` (`com.coui.appcompat.imageview`), the 36dp preference icons
 * (`couiType="round"`) used across Settings.
 *
 * The stock view draws its bitmap through a `BitmapShader` clipped to a circle or a
 * `couiBorderRadius` rounded rectangle (**1dp** by default, a `COUIShapePath` smooth-corner percentage
 * radius), with an optional 2px `coui_border` stroke and,
 * for the `SHADOW` type, a ring drawn from `coui_round_image_view_shadow`. This port uses the Compose
 * equivalent: the painter is clipped to the same shape, the optional border is drawn inside it and the
 * `SHADOW` ring is approximated with a drop shadow.
 *
 * @param painter The image to draw.
 * @param contentDescription The accessibility description.
 * @param modifier Applied to the image; the stock size comes from the layout (typically 36dp).
 * @param type The clip mode.
 * @param radius The `ROUND` corner radius.
 * @param showBorder Whether to draw the stock `coui_border` stroke.
 * @param contentScale How the painter fills the bounds.
 * @param colors The border colour.
 */
@Composable
fun RoundImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    type: RoundImageType = RoundImageType.Round,
    radius: Dp = RoundImageDefaults.Radius,
    showBorder: Boolean = false,
    contentScale: ContentScale = ContentScale.Crop,
    colors: RoundImageColors = RoundImageDefaults.colors(),
) {
    val shape = remember(type, radius) { roundImageShape(type, radius) }
    val shadowModifier =
        if (type == RoundImageType.Shadow) {
            Modifier.dropShadow(
                shape = shape,
                shadow =
                    Shadow(
                        radius = RoundImageDefaults.ShadowRingWidth,
                        color = colors.outCircleColor,
                        alpha = 0.1f,
                    ),
            )
        } else {
            Modifier
        }
    val strokeColor = roundImageBorderStrokeColor(showBorder, colors)
    Box(
        modifier =
            modifier
                .then(shadowModifier)
                .clip(shape),
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.matchParentSize(),
        )
        if (strokeColor != null) {
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .border(RoundImageDefaults.BorderWidth, strokeColor, shape),
            )
        }
    }
}

/**
 * `COUIRoundImageView.onDraw()`: when `couiHasBorder` is set the outline is the `mOutCircle` paint
 * (`coui_roundimageview_outcircle_color`), inset by half of `mBorderWidth`. The `mBorderPaint`
 * (`coui_border`) is initialised but **never drawn**, so [showBorder] must not use
 * [RoundImageColors.borderColor].
 */
internal fun roundImageBorderStrokeColor(
    showBorder: Boolean,
    colors: RoundImageColors,
): Color? = if (showBorder) colors.outCircleColor else null
