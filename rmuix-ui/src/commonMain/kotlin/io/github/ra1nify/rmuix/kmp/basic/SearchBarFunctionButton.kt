// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.anim.couiSpring
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.SinkFeedback
import io.github.ra1nify.rmuix.kmp.utils.stockPressTargetScale

/**
 * The stock search bar's functional button (`SearchBarFunctionButton` in `COUISearchBar`).
 *
 * A 40dp capsule in `?attr/couiColorBar` with the label centred, 16dp side padding and a 152dp width
 * cap. The stock bar puts it outside the field, separated from it by a `coui_search_bar_functional_button_divider_width`
 * (1dp x 16dp) divider, and passes it through `SearchBar`'s `outsideEndAction` slot:
 *
 * ```kotlin
 * SearchBar(
 *     inputField = { InputField(query, onQueryChange, onSearch, expanded, onExpandedChange) },
 *     onExpandedChange = {},
 *     expanded = expanded,
 *     outsideEndAction = { SearchBarFunctionButton(text = "Cancel", onClick = { expanded = false }) },
 * ) { }
 * ```
 *
 * @param text The button label; the stock default string is `coui_search_view_cancel` ("Cancel").
 * @param onClick Invoked when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param enabled Whether the button accepts input.
 * @param colors The [SearchBarFunctionButtonColors] of the button.
 */
@Composable
fun SearchBarFunctionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SearchBarFunctionButtonColors = SearchBarFunctionButtonDefaults.functionButtonColors(),
) {
    val interactionSource = remember { MutableInteractionSource() }
    val density = LocalDensity.current
    var buttonSize by remember { mutableStateOf(IntSize.Zero) }
    // `COUIViewTouchAnimator` (`SearchBarFunctionButton`'s `mTouchAnimator`) uses the default
    // `PressParams`; its target scale is the PressCalc area mapping of the measured button size.
    val pressFeedback =
        remember(buttonSize, density) {
            val target =
                if (buttonSize.width <= 0 || buttonSize.height <= 0) {
                    1f
                } else {
                    with(density) {
                        stockPressTargetScale(buttonSize.width.toDp().value, buttonSize.height.toDp().value)
                    }
                }
            SearchBarFunctionButtonDefaults.pressFeedback(target)
        }
    Box(
        modifier =
            modifier
                .height(SearchBarFunctionButtonDefaults.Height)
                .widthIn(max = SearchBarFunctionButtonDefaults.MaxWidth)
                .onSizeChanged { buttonSize = it }
                .clip(RoundedCornerShape(percent = 50))
                .background(colors.containerColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = pressFeedback,
                    enabled = enabled,
                    onClick = onClick,
                ).padding(horizontal = SearchBarFunctionButtonDefaults.HorizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = RmuixTheme.textStyles.button,
            fontSize = SearchBarFunctionButtonDefaults.TextSize,
            color = colors.textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Immutable
data class SearchBarFunctionButtonColors(
    val containerColor: Color,
    val textColor: Color,
)

object SearchBarFunctionButtonDefaults {
    /** `coui_search_bar_normal_background_height`: 40dp. */
    val Height = SearchBarStockFunctionButtonHeight

    /** `coui_search_bar_functional_button_text_padding_horizontal`: 16dp. */
    val HorizontalPadding = SearchBarStockFunctionButtonTextPadding

    /** `coui_search_bar_functional_button_max_width`: 152dp. */
    val MaxWidth = SearchBarStockFunctionButtonMaxWidth

    /** `couiTextAppearanceButton`: 14sp sans-serif-medium. */
    val TextSize = SearchBarStockFunctionButtonTextSize

    /** Padding of the label; the capsule's height and width come from [Height] / [MaxWidth]. */
    val InsideMargin = PaddingValues(horizontal = HorizontalPadding)

    /**
     * `COUIViewTouchAnimator` press feedback: the PressCalc target scale for the measured button and the
     * `PressParamsExtKt` jelly springs (press response 0.25 / bounce 0, release response 0.5 / bounce 0.6).
     */
    internal fun pressFeedback(sinkAmount: Float) =
        SinkFeedback(
            sinkAmount = sinkAmount,
            animationSpec = couiSpring(response = 0.25f, bounce = 0f),
            releaseAnimationSpec = couiSpring(response = 0.5f, bounce = 0.6f),
        )

    @Composable
    fun functionButtonColors(
        containerColor: Color = searchBarFunctionButtonBackground(isSearchBarInDarkTheme()),
        textColor: Color = RmuixTheme.colorScheme.onBackground,
    ): SearchBarFunctionButtonColors =
        remember(containerColor, textColor) {
            SearchBarFunctionButtonColors(containerColor = containerColor, textColor = textColor)
        }
}
