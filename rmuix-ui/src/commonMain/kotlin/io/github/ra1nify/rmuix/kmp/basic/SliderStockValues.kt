// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.first
import kotlin.math.PI
import kotlin.math.roundToInt

internal const val SLIDER_BACKGROUND_ENLARGE_SCALE = 1.4f

/** `coui_seekbar_progress_padding_horizontal`: 14dp of dead space at each end of the horizontal track. */
internal val SLIDER_PADDING_HORIZONTAL = 14.dp

/**
 * The ends pull in to 4dp while dragging.
 *
 * `coui_seekbar_progress_pressed_padding_horizontal` (4dp) is **declared but never referenced** -
 * no `R.dimen` read and no `@dimen` use - so 4dp is the library value that matches the declared dimen.
 */
internal val SLIDER_PRESSED_PADDING_HORIZONTAL = 4.dp

/**
 * The horizontal slider never exceeds 480dp.
 *
 * `coui_seekbar_view_max_width` (480dp) is **declared but never referenced** anywhere in the APK,
 * so the cap is a library choice that matches the declared dimen.
 */
internal val SLIDER_VIEW_MAX_WIDTH = 480.dp

/**
 * `coui_vertical_seekbar_progress_padding_vertical`: 10dp at each end of the vertical track.
 *
 * `COUIVerticalSeekBar.getNormalSeekBarHeightFloat()` confirms the meaning - it returns
 * `(height - paddingTop - paddingBottom) - (mPaddingVertical * 2f)`, i.e. the value is subtracted
 * from both ends and is *not* the track radius.
 */
internal val SLIDER_PADDING_VERTICAL = 10.dp

/**
 * `coui_vertical_seekbar_progress_pressed_padding_vertical`: 4dp while dragging.
 *
 * The stock widget does not jump to this value: `ensureSize()` computes
 * `mVerticalPaddingScale = (pressed + mBackgroundRadius * mBackgroundRadiusEnlargeScale) /
 * mPaddingVertical` and animates the padding by that scale. This library animates straight to 4dp,
 * which matches the scale only when the background radius term is 6dp; the exact scale needs the
 * widget attributes and is not modelled yet.
 */
internal val SLIDER_PRESSED_PADDING_VERTICAL = 4.dp

/**
 * The vertical slider never exceeds 480dp.
 *
 * `coui_vertical_seekbar_view_max_height` (480dp) is **declared but never referenced**, so the cap
 * is a library choice that matches the declared dimen.
 */
internal val SLIDER_VIEW_MAX_HEIGHT = 480.dp

internal val SliderHeightStiffness = ((2.0 * PI / 0.5) * (2.0 * PI / 0.5)).toFloat()

/**
 * `SectionAnimationHandler.DRAG_SNAP_DURATION` = **100ms**: while dragging, the section thumb glides to
 * the nearest section instead of following the finger 1:1. The stock interpolator is
 * `PathInterpolatorCompat.create(0f, 0f, 0.25f, 1f)`.
 */
internal const val SLIDER_SECTION_DRAG_SNAP_DURATION_MS = 100

/** `SectionAnimationHandler.RELEASE_SNAP_DURATION` = **150ms**: the settle after the fingers lift. */
internal const val SLIDER_SECTION_RELEASE_SNAP_DURATION_MS = 150

/** `SectionAnimationHandler.DRAG_SNAP_INTERPOLATOR`: `PathInterpolatorCompat.create(0, 0, 0.25, 1)`. */
internal val SliderSectionSnapEasing = CubicBezierEasing(0f, 0f, 0.25f, 1f)

/**
 * Snap animation for the section-style slider (`COUISectionSeekBar`). The stock style sets
 * `couiSeekBarPhysicsEnable=false` and animates every section change with the 100ms drag / 150ms release
 * path curve instead of the continuous spring used elsewhere.
 */
internal fun sliderSectionSnapSpec(dragging: Boolean): TweenSpec<Float> =
    tween(
        durationMillis = if (dragging) SLIDER_SECTION_DRAG_SNAP_DURATION_MS else SLIDER_SECTION_RELEASE_SNAP_DURATION_MS,
        easing = SliderSectionSnapEasing,
    )

internal fun sliderProgressSpring(
    dragging: Boolean,
    visibilityThreshold: Float = Spring.DefaultDisplacementThreshold,
): SpringSpec<Float> {
    val response = if (dragging) 0.15 else 0.3
    val angularFrequency = 2.0 * PI / response
    return spring(dampingRatio = 1f, stiffness = (angularFrequency * angularFrequency).toFloat(), visibilityThreshold = visibilityThreshold)
}

internal fun sliderCanReleaseBackground(fraction: Float): Boolean = fraction >= 0.5f

internal fun sliderPaintedBackgroundHeight(
    current: Float,
    resting: Float,
    maximum: Float,
): Float = if (current == resting || current == maximum) current else current.roundToInt().toFloat()

@Immutable
internal data class SliderTrackGeometry(
    val start: Float,
    val length: Float,
    val backgroundStart: Float,
    val backgroundEnd: Float,
    val touchEnd: Float = start + length,
) {
    fun position(
        fraction: Float,
        reverse: Boolean,
    ): Float = start + (if (reverse) 1f - fraction else fraction).coerceIn(0f, 1f) * length

    fun fraction(
        position: Float,
        reverse: Boolean,
    ): Float = rawFraction(position, reverse).coerceIn(0f, 1f)

    fun rawFraction(
        position: Float,
        reverse: Boolean,
    ): Float {
        if (length <= 0f) return 0f
        return if (reverse) (touchEnd - position) / length else (position - start) / length
    }
}

internal fun sliderTrackGeometry(
    width: Float,
    trackHeight: Float,
    backgroundHeight: Float,
    padding: Float? = null,
): SliderTrackGeometry {
    // Two stock paths disagree on where the end padding comes from:
    // - `COUISeekBarCore` recomputes it as `(barHeight * backgroundEnlargeScale) / 2` - the *base*
    //   height, so the inset is constant while the background stretches (the thumb must not move when
    //   the capsule grows);
    // - `COUIIntentSeekBar` reads the absolute `coui_seekbar_progress_padding_horizontal` (14dp).
    // `padding == null` keeps the first behaviour (capsule), an explicit value serves the second.
    val rawPadding = padding ?: (trackHeight * SLIDER_BACKGROUND_ENLARGE_SCALE / 2f)
    val resolved = rawPadding.coerceIn(0f, width.coerceAtLeast(0f) / 2f)
    return SliderTrackGeometry(
        start = resolved,
        length = (width - resolved * 2f).coerceAtLeast(0f).toInt().toFloat(),
        backgroundStart = resolved - backgroundHeight / 2f,
        backgroundEnd = width - resolved + backgroundHeight / 2f,
        touchEnd = width - resolved,
    )
}

internal fun sliderContainer12(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

internal fun sliderDisabledThumb(dark: Boolean): Color = if (dark) Color(0x29FFFFFF) else Color(0x8AFFFFFF)

internal fun sliderThumbShadow(dark: Boolean): Color = if (dark) Color(0x40000000) else Color(0x1A000000)

/**
 * `?attr/couiColorLabelTertiary` through `coui_seekbar_inactive_mark_selector`: the tick marks that are
 * not covered by the progress yet (`COUISectionSeekBar`).
 */
internal fun sliderKeyPoint(dark: Boolean): Color = if (dark) Color(0x4DFFFFFF) else Color(0x42000000)

/** `?attr/couiColorLabelOnColor` (`coui_color_label_on_color` `#FFFFFF`): the ticks left of the thumb. */
internal fun sliderKeyPointForeground(dark: Boolean): Color = Color(0xFFFFFFFF)

/** `coui_seekbar_inactive_mark_disable_color`: `#42000000` / `#29FFFFFF`. */
internal fun sliderKeyPointDisabled(dark: Boolean): Color = if (dark) Color(0x29FFFFFF) else Color(0x42000000)

/** `coui_seekbar_active_mark_disable_color`: `#8AFFFFFF` / `#29FFFFFF`. */
internal fun sliderKeyPointForegroundDisabled(dark: Boolean): Color = if (dark) Color(0x29FFFFFF) else Color(0x8AFFFFFF)

@Composable
internal fun rememberSliderBackgroundHeight(
    trackHeight: Dp,
    interacting: Boolean,
    maxHeight: Dp? = null,
): Dp {
    var enlarge by remember { mutableStateOf(false) }
    val fraction =
        animateFloatAsState(
            targetValue = if (enlarge) 1f else 0f,
            animationSpec = spring(dampingRatio = 1f, stiffness = SliderHeightStiffness),
            label = "SliderBackgroundEnlarge",
        )
    LaunchedEffect(interacting) {
        if (interacting) {
            enlarge = true
        } else {
            if (enlarge) snapshotFlow { fraction.value }.first(::sliderCanReleaseBackground)
            enlarge = false
        }
    }
    // `COUIIntentSeekBar` caps the stretch at `..._max_background_height_{size}`; the capsule
    // default keeps the proportional scale it has always used.
    if (maxHeight != null) {
        return trackHeight + (maxHeight - trackHeight) * fraction.value.coerceIn(0f, 1f)
    }
    return trackHeight * (1f + (SLIDER_BACKGROUND_ENLARGE_SCALE - 1f) * fraction.value.coerceIn(0f, 1f))
}

internal fun sliderClickThreshold(
    rangeSpan: Float,
    trackLength: Float,
): Float = rangeSpan * 0.75f / trackLength.coerceAtLeast(1f)

internal fun sliderStartsHorizontalDrag(
    displacement: Float,
    touchSlop: Float,
): Boolean = kotlin.math.abs(displacement) > touchSlop

internal fun sliderFollowThreshold(rangeSpan: Float): Float = rangeSpan * 0.75f / 1000f

/**
 * The stock value label (`seekbar.TextDrawable`).
 *
 * It is **not** a floating bubble: the drawable paints a transparent rect
 * (`getPaint().setColor(0)`) and only draws text, right-aligned, inside a fixed 144px box:
 *
 * | token | value |
 * | --- | ---: |
 * | `coui_seekbar_popup_text_size_small` | 12sp |
 * | `coui_seekbar_popup_text_height` | 16dp |
 * | `coui_seekbar_popup_text_margin_bottom` | 6dp |
 * | `coui_seekbar_popup_text_padding_end` | 4dp |
 * | intrinsic width | 144px (hard-coded `this.WIDTH = 144`) |
 *
 * `COUISeekBarCore.setValueForLabel()` anchors the box at the **right end of the track, above it**:
 * `setBounds(width - end, -intrinsicHeight, width - end - intrinsicWidth, 0)` in LTR, mirrored in RTL.
 *
 * The width is a **raw pixel** constant, not a dp value: `getIntrinsicWidth()` returns `144` directly and
 * `draw()` mixes it with `mPaddingEnd`, which is a pixel offset. At density 3 that is 48dp, so exposing it
 * as `144.dp` would be three times too wide.
 */
internal const val SLIDER_VALUE_LABEL_WIDTH_PX = 144

/** `coui_seekbar_popup_text_size_small`: 12sp. */
internal val SLIDER_VALUE_LABEL_TEXT_SIZE = 12.sp

/**
 * `coui_seekbar_popup_text_height` = 16dp. The drawable's intrinsic height is this plus
 * `coui_seekbar_popup_text_margin_bottom` (6dp); the margin is added separately at the call site.
 */
internal val SLIDER_VALUE_LABEL_HEIGHT = 16.dp

/** `coui_seekbar_popup_text_margin_bottom`: 6dp below the 16dp text box. */
internal val SLIDER_VALUE_LABEL_MARGIN_BOTTOM = 6.dp

/** `coui_seekbar_popup_text_padding_end`: 4dp between the text and the box edge. */
internal val SLIDER_VALUE_LABEL_PADDING_END = 4.dp

/**
 * The text a `COUISeekBar` shows in its value label: the current value rendered with **no** fraction
 * digits beyond what the value actually carries (the stock `TextDrawable` receives a pre-formatted
 * `String` from `setValueForLabel()`, so this mirrors a plain decimal rendering).
 */
internal fun sliderValueLabelText(value: Float): String {
    val rounded = kotlin.math.round(value)
    return if (kotlin.math.abs(value - rounded) < 0.005f) rounded.toInt().toString() else value.toString()
}
