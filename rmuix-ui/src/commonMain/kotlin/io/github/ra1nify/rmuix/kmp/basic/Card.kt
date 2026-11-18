// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.anim.couiSpring
import io.github.ra1nify.rmuix.kmp.interfaces.HoldDownObserver
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight
import io.github.ra1nify.rmuix.kmp.squircle.SquircleDefaults
import io.github.ra1nify.rmuix.kmp.squircle.SquircleShape
import io.github.ra1nify.rmuix.kmp.squircle.isSquircleEnabled
import io.github.ra1nify.rmuix.kmp.squircle.squircleSurface
import io.github.ra1nify.rmuix.kmp.theme.LocalContentColor
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.PressFeedbackType
import io.github.ra1nify.rmuix.kmp.utils.SinkFeedback
import io.github.ra1nify.rmuix.kmp.utils.TiltFeedback
import io.github.ra1nify.rmuix.kmp.utils.pressable
import io.github.ra1nify.rmuix.kmp.utils.stockPressTargetScale

/**
 * A [Card] component with Rmuix style.
 * Card contain content and actions that relate information about a subject.
 *
 * This [Card] does not handle input events
 *
 * @param modifier The modifier to be applied to the [Card].
 * @param cornerRadius The corner radius of the [Card].
 * @param cornerTier Optional ColorOS corner tier; wins over [cornerRadius] when set.
 * @param cornerScale Which of the three stock radius families a tier resolves to.
 * @param cornerExtension Smooth-corner weight forwarded to `squircleSurface`.
 * @param insideMargin The margin inside the [Card].
 * @param colors [CardColors] that will be used to resolve the color(s) used for the [Card].
 * @param content The [Composable] content of the [Card].
 */
@Composable
@NonRestartableComposable
fun Card(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CardDefaults.CornerRadius,
    /** Optional ColorOS tier; wins over [cornerRadius] when set. */
    cornerTier: CardCornerTier? = null,
    cornerScale: CardCornerScale = CardDefaults.CornerScale,
    cornerExtension: Float = CardDefaults.CornerExtension,
    insideMargin: PaddingValues = CardDefaults.InsideMargin,
    colors: CardColors = CardDefaults.defaultColors(),
    content: @Composable ColumnScope.() -> Unit,
) {
    BasicCard(
        modifier = modifier,
        cornerRadius = cornerTier?.radius(cornerScale) ?: cornerRadius,
        cornerExtension = cornerExtension,
        colors = colors,
    ) {
        Column(
            modifier = Modifier.padding(insideMargin),
            content = content,
        )
    }
}

/**
 * A [Card] component with Rmuix style.
 * Card contain contain content and actions that relate information about a subject.
 *
 * This [Card] handles input events
 *
 * @param modifier The modifier to be applied to the [Card].
 * @param cornerRadius The corner radius of the [Card].
 * @param cornerTier Optional ColorOS corner tier; wins over [cornerRadius] when set.
 * @param cornerScale Which of the three stock radius families a tier resolves to.
 * @param cornerExtension Smooth-corner weight forwarded to `squircleSurface`.
 * @param insideMargin The margin inside the [Card].
 * @param colors [CardColors] that will be used to resolve the color(s) used for the [Card].
 * @param pressFeedbackType The press feedback type of the [Card].
 * @param showIndication Whether to show indication of the [Card].
 * @param holdDownState Whether the [Card] is in a hold-down state.
 * @param onClick The callback to be invoked when the [Card] is clicked.
 * @param onLongPress The callback to be invoked when the [Card] is long pressed.
 * @param content The [Composable] content of the [Card].
 */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CardDefaults.CornerRadius,
    /** Optional ColorOS tier; wins over [cornerRadius] when set. */
    cornerTier: CardCornerTier? = null,
    cornerScale: CardCornerScale = CardDefaults.CornerScale,
    cornerExtension: Float = CardDefaults.CornerExtension,
    insideMargin: PaddingValues = CardDefaults.InsideMargin,
    colors: CardColors = CardDefaults.defaultColors(),
    pressFeedbackType: PressFeedbackType = PressFeedbackType.None,
    showIndication: Boolean = false,
    holdDownState: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLongPress by rememberUpdatedState(onLongPress)

    HoldDownObserver(holdDownState, interactionSource)

    val density = LocalDensity.current
    var cardSize by remember { mutableStateOf(IntSize.Zero) }
    // `COUIPressFeedbackJumpPreference` (`COUICardEntrancePreference`'s base) drives the card with
    // `COUIPressFeedbackHelper(view, 0)`: `PressCalc` maps the measured card area to 0.85..0.97 and the
    // animator uses the `PressParamsExtKt` jelly springs.
    val pressFeedback =
        remember(pressFeedbackType, cardSize, density) {
            when (pressFeedbackType) {
                PressFeedbackType.None -> {
                    null
                }

                PressFeedbackType.Sink -> {
                    val target =
                        if (cardSize.width <= 0 || cardSize.height <= 0) {
                            1f
                        } else {
                            with(density) {
                                stockPressTargetScale(cardSize.width.toDp().value, cardSize.height.toDp().value)
                            }
                        }
                    SinkFeedback(
                        sinkAmount = target,
                        animationSpec = couiSpring(response = 0.25f, bounce = 0f),
                        releaseAnimationSpec = couiSpring(response = 0.5f, bounce = 0.6f),
                    )
                }

                PressFeedbackType.Tilt -> {
                    TiltFeedback()
                }
            }
        }

    val usedInteractionSource = if (pressFeedback != null) interactionSource else null
    val indicationToUse = if (showIndication) LocalIndication.current else null

    val hasOnClick = onClick != null
    val hasLongPress = onLongPress != null
    val isClickable = hasOnClick || hasLongPress
    val clickableModifier =
        remember(isClickable, hasLongPress, interactionSource, indicationToUse) {
            if (isClickable) {
                Modifier.combinedClickable(
                    interactionSource = interactionSource,
                    indication = indicationToUse,
                    onClick = { currentOnClick?.invoke() },
                    onLongClick =
                        if (hasLongPress) {
                            { currentOnLongPress?.invoke() }
                        } else {
                            null
                        },
                )
            } else {
                Modifier
            }
        }

    BasicCard(
        modifier =
            modifier
                .onSizeChanged { cardSize = it }
                .pressable(
                    interactionSource = usedInteractionSource,
                    indication = pressFeedback,
                    delay = null,
                ),
        cornerRadius = cornerTier?.radius(cornerScale) ?: cornerRadius,
        cornerExtension = cornerExtension,
        colors = colors,
    ) {
        Column(
            modifier =
                Modifier
                    .then(clickableModifier)
                    .padding(insideMargin),
            content = content,
        )
    }
}

/**
 * A [BasicCard] component.
 *
 * @param modifier The modifier to be applied to the [BasicCard].
 * @param colors [CardColors] that will be used to resolve the color(s) used for the [BasicCard].
 * @param cornerRadius The corner radius of the [BasicCard].
 * @param cornerTier Optional ColorOS corner tier; wins over [cornerRadius] when set.
 * @param cornerScale Which of the three stock radius families a tier resolves to.
 * @param cornerExtension Smooth-corner weight forwarded to `squircleSurface`.
 * @param content The [Composable] content of the [BasicCard].
 */
@Composable
private fun BasicCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.defaultColors(),
    cornerRadius: Dp = CardDefaults.CornerRadius,
    /** Optional ColorOS tier; wins over [cornerRadius] when set. */
    cornerTier: CardCornerTier? = null,
    cornerScale: CardCornerScale = CardDefaults.CornerScale,
    cornerExtension: Float = CardDefaults.CornerExtension,
    content: @Composable () -> Unit,
) {
    val resolvedCornerRadius = cornerTier?.radius(cornerScale) ?: cornerRadius

    val squircleEnabled = isSquircleEnabled()

    val spotlightDark = RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

    // `squircleSurface` falls back to a plain `RoundedCornerShape` clip when the shader path is
    // unavailable, so the spotlight outline has to degrade with it or the two layers split again.

    CompositionLocalProvider(
        LocalContentColor provides colors.contentColor,
    ) {
        Box(
            modifier =
                modifier
                    .rmuixSpotlight(
                        darkTheme = spotlightDark,
                        // Same squircle silhouette as the background below - a RoundedCornerShape here would
                        // leave the spotlight on a circular-arc outline while the card itself is continuous.
                        shape = SquircleShape(resolvedCornerRadius, cornerExtension, squircleEnabled),
                        enabled = true,
                    ).semantics(mergeDescendants = false) {
                        isTraversalGroup = true
                    }.squircleSurface(
                        color = colors.color,
                        cornerRadius = resolvedCornerRadius,
                        extension = cornerExtension,
                    ),
            propagateMinConstraints = true,
        ) {
            content()
        }
    }
}

object CardDefaults {
    /**
     * The radius a [Card] uses by default: `?attr/couiRoundCornerXL` = `coui_round_corner_xl` = 20dp,
     * the radius the stock card surfaces use (`COUICardListSelectedItemLayout` and the card background
     * drawables). Pass [cornerTier] for the exact stock tier when a screen uses another one.
     */
    val CornerRadius = 20.dp

    /** Which of the three stock radii a [CardCornerTier] resolves to by default. */
    val CornerScale = CardCornerScale.LARGE_16_1

    /**
     * Corner-tile size as a multiple of the radius, forwarded to `squircleSurface`.
     *
     * The tier weights are `coui_round_corner_<tier>_weight` = 1.5 (XS/S), 0.99 (M/L) and 1.1 (XL..XXXXL),
     * carried by [CardCornerTier.weight]. They describe the same "corner tile as a multiple of the radius"
     * concept as this factor, but the stock also has a separate
     * `coui_popup_list_window_shared_mask_smooth_round_corner_weight` = 3 for the popup list's shared mask,
     * and no source states that the tier weight can be forwarded verbatim. The default therefore stays
     * [SquircleDefaults.Extension] (1.1, which happens to equal the XL..XXXXL weight) until a device
     * comparison settles the mapping.
     */
    val CornerExtension = SquircleDefaults.Extension

    /**
     * The default margin inside the [Card].
     */
    val InsideMargin = PaddingValues(0.dp)

    /**
     * The default colors width of the [Card].
     */
    @Composable
    fun defaultColors(
        // The stock card surfaces all use `?attr/couiColorCardBackground` (#FFFFFF / #1AFFFFFF);
        // `?attr/couiColorLabelPrimary` is the text colour the card preferences use.
        color: Color = preferenceCardContainer(isCardInDarkTheme()),
        contentColor: Color = preferenceTitle(isCardInDarkTheme()),
    ): CardColors =
        remember(color, contentColor) {
            CardColors(
                color = color,
                contentColor = contentColor,
            )
        }
}

@Composable
private fun isCardInDarkTheme(): Boolean = RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

@Immutable
data class CardColors(
    val color: Color,
    val contentColor: Color,
)
