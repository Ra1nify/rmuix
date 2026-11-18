// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.LocalContentColor
import io.github.ra1nify.rmuix.kmp.utils.PressFeedbackType

/**
 * Defaults for [LoadingButton], read from `COUILoadingButton` and the `coui_loading_btn_*` resources
 * in the ColorOS settings dump.
 */
object LoadingButtonDefaults {
    /** `coui_loading_btn_circle_radius`: the radius of each loading dot. */
    val DotRadius = 1.dp

    /** `coui_loading_btn_circle_spacing`: the gap between two loading dots. */
    val DotSpacing = 2.dp

    /** `DOT_START_ALPHA`: the dimmest a dot gets (20%). */
    const val DOT_START_ALPHA = 51f

    /** `DOT_MID_ALPHA`: the halfway step (50%). */
    const val DOT_MID_ALPHA = 127.5f

    /** `DOT_END_ALPHA`: a fully lit dot (100%). */
    const val DOT_END_ALPHA = 255f

    /** The three dots are staggered by this much. */
    const val DOT_STAGGER_MILLIS = 333

    /** Total length of one animation pass, after which it repeats. */
    const val CYCLE_MILLIS = 1332
}

/**
 * The alpha of one loading dot at [progressMillis] into the cycle.
 *
 * `COUILoadingButton` drives each dot with four `ValueAnimator`s: `START -> MID` over 133ms,
 * `MID -> END` over 67ms, `END -> MID` over 67ms starting at 467ms, and `MID -> START` over 133ms
 * starting at 533ms. Between 200ms and 467ms the value holds at [LoadingButtonDefaults.DOT_END_ALPHA],
 * which is what makes the dots read as a travelling highlight. Dots are staggered by
 * [LoadingButtonDefaults.DOT_STAGGER_MILLIS].
 *
 * @param progressMillis Position in the cycle, wrapped modulo [LoadingButtonDefaults.CYCLE_MILLIS].
 * @param dotIndex 0-based dot index.
 */
internal fun loadingDotAlpha(
    progressMillis: Float,
    dotIndex: Int,
): Float {
    val cycle = LoadingButtonDefaults.CYCLE_MILLIS.toFloat()
    val offset = progressMillis - dotIndex * LoadingButtonDefaults.DOT_STAGGER_MILLIS
    val t = ((offset % cycle) + cycle) % cycle
    val start = LoadingButtonDefaults.DOT_START_ALPHA
    val mid = LoadingButtonDefaults.DOT_MID_ALPHA
    val end = LoadingButtonDefaults.DOT_END_ALPHA
    return when {
        t < 133f -> start + (mid - start) * (t / 133f)
        t < 200f -> mid + (end - mid) * ((t - 133f) / 67f)
        t < 467f -> end
        t < 533f -> end + (mid - end) * ((t - 467f) / 67f)
        t < 666f -> mid + (start - mid) * ((t - 533f) / 133f)
        else -> start
    }
}

/**
 * A [Button] that swaps its content for the ColorOS three-dot loading indicator.
 *
 * `COUILoadingButton` has two rendering paths: when its loading *text* fits it draws the text plus a
 * literal `"..."` clipped into three bands, one alpha per band; otherwise it draws three circles.
 * Only the circle path is reproduced here - it does not depend on font metrics, so it behaves the
 * same on every platform. The dots take the button's content colour, exactly like the stock uses the
 * button's `TextPaint`.
 *
 * The button stays clickable while loading, matching `COUIButton`: the stock's loading state only
 * changes what is drawn.
 *
 * @param onClick Called when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param loading Whether to draw the loading indicator instead of [content].
 * @param enabled Whether the button can be clicked.
 * @param enableSpotlight Whether the spotlight effect is enabled.
 * @param spotlightColor The spotlight colour.
 * @param cornerRadius The corner radius of the button.
 * @param minWidth The minimum width of the button.
 * @param minHeight The minimum height of the button.
 * @param colors The button colours.
 * @param insideMargin The internal padding of the button.
 * @param pressFeedbackType The press feedback type.
 * @param interactionSource An optional hoisted interaction source.
 * @param indication An optional indication.
 * @param loadingContent Replaces the three dots while [loading]; use it for a spinner instead.
 * @param content The button content.
 */
@Composable
fun LoadingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    enabled: Boolean = true,
    enableSpotlight: Boolean = false,
    spotlightColor: Color = Color.White,
    cornerRadius: Dp = ButtonDefaults.CornerRadius,
    minWidth: Dp = ButtonDefaults.MinWidth,
    minHeight: Dp = ButtonDefaults.MinHeight,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    insideMargin: PaddingValues = ButtonDefaults.InsideMargin,
    pressFeedbackType: PressFeedbackType = PressFeedbackType.Sink,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    loadingContent: (@Composable () -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        enableSpotlight = enableSpotlight,
        spotlightColor = spotlightColor,
        cornerRadius = cornerRadius,
        minWidth = minWidth,
        minHeight = minHeight,
        colors = colors,
        insideMargin = insideMargin,
        pressFeedbackType = pressFeedbackType,
        interactionSource = interactionSource,
        indication = indication,
    ) {
        when {
            !loading -> content()
            loadingContent != null -> loadingContent()
            else -> LoadingDots()
        }
    }
}

/**
 * The three loading dots: `coui_loading_btn_circle_radius` (1dp) circles separated by
 * `coui_loading_btn_circle_spacing` (2dp), 10dp in total.
 */
@Composable
private fun LoadingDots() {
    val color = LocalContentColor.current
    val transition = rememberInfiniteTransition()
    val progress by
        transition.animateFloat(
            initialValue = 0f,
            targetValue = LoadingButtonDefaults.CYCLE_MILLIS.toFloat(),
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = LoadingButtonDefaults.CYCLE_MILLIS,
                            easing = LinearEasing,
                        ),
                    repeatMode = RepeatMode.Restart,
                ),
        )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LoadingButtonDefaults.DotSpacing),
    ) {
        repeat(3) { index ->
            val alpha = (loadingDotAlpha(progress, index) / LoadingButtonDefaults.DOT_END_ALPHA).coerceIn(0f, 1f)
            Canvas(modifier = Modifier.size(LoadingButtonDefaults.DotRadius * 2)) {
                drawCircle(
                    color = color,
                    radius = size.minDimension / 2f,
                    alpha = alpha,
                )
            }
        }
    }
}
