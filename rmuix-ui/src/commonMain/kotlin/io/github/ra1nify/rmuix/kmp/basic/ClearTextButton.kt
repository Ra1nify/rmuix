// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectColor
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectSpring

/** Viewport of `ic_edit_text_delete_search_view.xml`, also the touch box. */
private const val CLEAR_VIEWPORT = 36f

/** Radius of the disc in `ic_edit_text_delete_search_view.xml` (`M18,18m-9,0a9,9 ...`). */
private const val CLEAR_DISC_RADIUS = 9f

/** The white cross from `ic_edit_text_delete_search_view.xml`, verbatim. */
private const val CLEAR_GLYPH_PATH_DATA =
    "M18,16.868L15.031,13.898L13.899,15.03L16.869,18L13.898,20.97L15.03,22.101L18,19.131L20.97,22.101" +
        "L22.102,20.969L19.132,18L22.101,15.03L20.969,13.899L18,16.868Z"

/** Parsed once: the clear glyph is drawn from the stock path data rather than two lines. */
private val clearGlyphPath: Path by lazy {
    PathParser().parsePathString(CLEAR_GLYPH_PATH_DATA).toPath()
}

/**
 * The stock clear button used by the search bar and by [TextField]'s `showClearButton`:
 * `ic_edit_text_delete_search_view` - an 18dp `couiColorHintNeutral` disc with a white cross, centred in
 * a 36dp touch box - plus the circular `COUIMaskEffectDrawable` press mask in `?attr/couiColorPress`
 * (`SearchBarIconButton` masks the button with an oval of its own size).
 *
 * @param onClick Invoked when the button is clicked.
 * @param modifier The modifier to be applied to the 36dp touch box.
 * @param contentDescription The accessibility description of the button.
 */
@Composable
internal fun ClearTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Clear",
) {
    val dark = isSearchBarInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressProgress by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = stockStateEffectSpring(),
        label = "clearTextPress",
    )
    val pressColor = stockStateEffectColor(searchBarIconButtonPressColor(dark), pressProgress)
    Box(
        modifier =
            modifier
                .size(SearchBarStockClearIconBoxSize)
                .clip(CircleShape)
                .drawBehind {
                    if (pressColor != Color.Transparent) drawCircle(pressColor)
                }.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ).semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        // The vector is 36dp with an 18dp disc in it; stock renders it at natural size, so the
        // canvas must be the 36dp box (an 18dp canvas would halve the disc to 9dp).
        Canvas(modifier = Modifier.size(SearchBarStockClearIconBoxSize)) {
            val scale = size.minDimension / CLEAR_VIEWPORT
            withTransform({ scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero) }) {
                drawCircle(
                    color = searchBarClearIconBackground(dark),
                    radius = CLEAR_DISC_RADIUS,
                    center = Offset(CLEAR_VIEWPORT / 2f, CLEAR_VIEWPORT / 2f),
                )
                drawPath(clearGlyphPath, color = Color.White)
            }
        }
    }
}
