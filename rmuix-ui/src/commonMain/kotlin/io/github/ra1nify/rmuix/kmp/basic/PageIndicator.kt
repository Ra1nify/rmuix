// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/**
 * Defaults for [PageIndicator], taken from `COUIPageIndicator` and the `coui_page_indicator_*`
 * resources in the ColorOS settings dump.
 */
object PageIndicatorDefaults {
    /** `coui_page_indicator_dot_size`. */
    val DotSize = 6.dp

    /** `coui_page_indicator_dot_size_medium`. */
    val DotSizeMedium = 4.5.dp

    /** `coui_page_indicator_dot_size_small`. */
    val DotSizeSmall = 2.5.dp

    /** `coui_page_indicator_dot_spacing`. */
    val Spacing = 3.dp

    /** `coui_page_indicator_dot_stroke_width`. */
    val StrokeWidth = 1.dp

    /**
     * `Widget.COUI.COUIPageIndicator`: `dotColor` = `?attr/couiColorHintNeutral`
     * (`#42000000` / `#4DFFFFFF`) and `traceDotColor` = `@color/coui_color_label_primary`
     * (`#E6000000` / `#E6FFFFFF`). The Dark style overrides them with the `_dark` variants of the same
     * two colours.
     */
    @Composable
    fun colors(): PageIndicatorColors {
        val dark = isPageIndicatorInDarkTheme()
        return remember(dark) {
            PageIndicatorColors(
                selectedColor = pageIndicatorSelected(dark),
                unselectedColor = pageIndicatorUnselected(dark),
            )
        }
    }
}

/** `@color/coui_color_label_primary`: the selected dot, `#E6000000` / `#E6FFFFFF`. */
internal fun pageIndicatorSelected(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/** `?attr/couiColorHintNeutral`: the unselected dot, `#42000000` / `#4DFFFFFF`. */
internal fun pageIndicatorUnselected(dark: Boolean): Color = if (dark) Color(0x4DFFFFFF) else Color(0x42000000)

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isPageIndicatorInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/** Colours of a [PageIndicator]. */
@Immutable
data class PageIndicatorColors(
    val selectedColor: Color,
    val unselectedColor: Color,
)

/**
 * The ColorOS page dots (`COUIPageIndicator`): one circle per page, spaced by
 * `coui_page_indicator_dot_spacing` (3dp).
 *
 * Only the flat variant is reproduced. The stock widget also shrinks the dots when more than six
 * pages exist (`MAX_VISIBLE_DOT_NUMBER` 6, `DEFAULT_MAXIMUM_LARGE_DOT_NUMBER` 4,
 * `DEFAULT_MAXIMUM_MEDIUM_DOT_NUMBER` 2, level sizes `9 / 3 / 5 / 5 / 7`); that graduated sizing is
 * not ported yet.
 *
 * @param count Number of pages; renders nothing when it is not positive.
 * @param selectedIndex The highlighted page, clamped into range.
 * @param modifier The modifier to be applied to the indicator.
 * @param dotSize Diameter of a dot; the stock default is 6dp.
 * @param spacing Space between two dots.
 * @param colors Selected and unselected dot colours.
 */
@Composable
fun PageIndicator(
    count: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp = PageIndicatorDefaults.DotSize,
    spacing: Dp = PageIndicatorDefaults.Spacing,
    colors: PageIndicatorColors = PageIndicatorDefaults.colors(),
) {
    if (count <= 0) return
    val selected = selectedIndex.coerceIn(0, count - 1)
    val density = LocalDensity.current
    val dotPx = with(density) { dotSize.toPx() }
    val spacingPx = with(density) { spacing.toPx() }
    val totalWidth = with(density) { (dotPx * count + spacingPx * (count - 1)).toDp() }
    Canvas(modifier = modifier.size(width = totalWidth, height = dotSize)) {
        repeat(count) { index ->
            val centerX = index * (dotPx + spacingPx) + dotPx / 2f
            drawCircle(
                color = if (index == selected) colors.selectedColor else colors.unselectedColor,
                radius = dotPx / 2f,
                center = Offset(centerX, dotPx / 2f),
            )
        }
    }
}
