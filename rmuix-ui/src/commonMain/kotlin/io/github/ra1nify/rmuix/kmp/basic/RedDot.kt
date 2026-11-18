// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.squircle.squircleSurface

/**
 * ColorOS 17 Hint Red Dot & Badge Modes (`COUIHintRedDot`).
 */
sealed interface RedDotMode {
    /** Pure circular 6dp micro red dot without text. */
    data object PointOnly : RedDotMode

    /** Height 16dp capsule with number counter (e.g. 1..99, or 99+). */
    data class PointWithNum(
        val count: Int,
    ) : RedDotMode

    /** Height 16dp capsule with text label (e.g. "NEW", "HOT"). */
    data class PointWithText(
        val text: String,
    ) : RedDotMode
}

/**
 * ColorOS 17 Official Red Dot / Badge Indicator (`COUIHintRedDot`).
 * Reconstructed from `com.coui.appcompat.reddot.COUIHintRedDot` and `Widget.COUI.COUIHintRedDot`:
 * - Height: `coui_height` 16dp for Num/Text mode; `coui_dot_diameter` 6dp for PointOnly mode.
 * - Corner Radius: `?attr/couiRoundCornerFULL`, i.e. a pill (height / 2 = 8dp).
 * - Color: `?attr/couiHintRedDotColor` → `@color/coui_color_additional_red` (`#EB3B2F` / `#EB493D`).
 * - Text: `couiHintTextSize` 10dp, `couiHintRedDotTextColor` white.
 */
@Composable
fun RedDot(
    mode: RedDotMode,
    modifier: Modifier = Modifier,
    color: Color = redDotDefaultColor(),
    withStroke: Boolean = false,
    strokeColor: Color = RedDotStockTextColor,
    strokeWidth: Dp = RedDotDefaults.StrokeWidth,
) {
    // `COUIHintRedDot` stroke modes add `coui_hint_red_dot_mode_stroke_extra_diameter` (2dp) so the white
    // `red_dot_stroke_circle` ring fits around the dot.
    val strokeExtra = if (withStroke) RedDotDefaults.StrokeExtraDiameter else 0.dp
    when (mode) {
        is RedDotMode.PointOnly -> {
            Box(
                modifier =
                    modifier
                        .semantics { contentDescription = "Unread notification" }
                        .size(RedDotDefaults.PointSize + strokeExtra)
                        .clip(CircleShape)
                        .background(color)
                        .then(if (withStroke) Modifier.border(strokeWidth, strokeColor, CircleShape) else Modifier),
            )
        }

        is RedDotMode.PointWithNum -> {
            if (mode.count <= 0) return
            // `drawNumber`: below 1000 the stock badge prints the plain number (100-999 included, the
            // width tier grows to 26dp), and from 1000 on it draws three dots instead of digits.
            val displayText = if (mode.count < 1000) mode.count.toString() else null
            // `COUIHintRedDotHelper.getBgWidth`: 16 / 20 / 26dp for 1 / 2 / 3 digits, and back to 20dp
            // once the count reaches four digits (the stock badge clamps instead of growing).
            val minWidth =
                when {
                    mode.count < 10 -> RedDotDefaults.SmallWidth
                    mode.count < 100 -> RedDotDefaults.MediumWidth
                    mode.count < 1000 -> RedDotDefaults.LargeWidth
                    else -> RedDotDefaults.MediumWidth
                }

            Box(
                modifier =
                    modifier
                        .semantics { contentDescription = "${displayText ?: "many"} unread items" }
                        .height(RedDotDefaults.BadgeHeight + strokeExtra)
                        .widthIn(min = minWidth + strokeExtra)
                        .squircleSurface(color = color, cornerRadius = RedDotDefaults.BadgeRadius)
                        .then(if (withStroke) Modifier.border(strokeWidth, strokeColor, CircleShape) else Modifier)
                        .padding(horizontal = RedDotDefaults.BadgeHorizontalPadding),
                contentAlignment = Alignment.Center,
            ) {
                if (displayText != null) {
                    Text(
                        text = displayText,
                        color = RedDotStockTextColor,
                        fontSize = RedDotStockTextSize,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                    )
                } else {
                    RedDotEllipsisDots()
                }
            }
        }

        is RedDotMode.PointWithText -> {
            if (mode.text.isBlank()) return
            Box(
                modifier =
                    modifier
                        .semantics { contentDescription = mode.text }
                        .height(RedDotDefaults.BadgeHeight + strokeExtra)
                        .widthIn(min = RedDotDefaults.TextBadgeMinWidth + strokeExtra)
                        .squircleSurface(color = color, cornerRadius = RedDotDefaults.BadgeRadius)
                        .then(if (withStroke) Modifier.border(strokeWidth, strokeColor, CircleShape) else Modifier)
                        .padding(horizontal = RedDotDefaults.TextBadgeHorizontalPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = mode.text,
                    color = RedDotStockTextColor,
                    fontSize = RedDotStockTextSize,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}

/** The stock `1000+` rendering: three dots of `couiEllipsisDiameter` spaced by `couiEllipsisSpacing`. */
@Composable
private fun RedDotEllipsisDots() {
    val dot = RedDotStockEllipsisDotSize
    val spacing = RedDotStockEllipsisSpacing
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) {
            Box(
                modifier =
                    Modifier
                        .size(dot)
                        .clip(CircleShape)
                        .background(RedDotStockTextColor),
            )
        }
    }
}

object RedDotDefaults {
    /** coui_dot_stroke_width: 1dp, drawn in coui_color_white when [withStroke] is on. */
    val StrokeWidth: Dp = RedDotStockStrokeWidth

    /** `coui_hint_red_dot_mode_stroke_extra_diameter`: struck modes grow the dot by 2dp. */
    val StrokeExtraDiameter: Dp = RedDotStockStrokeExtraDiameter
    val PointSize: Dp = RedDotStockPointSize
    val BadgeHeight: Dp = RedDotStockBadgeHeight
    val BadgeRadius: Dp = RedDotStockBadgeRadius

    /** `coui_small_width`: one digit, i.e. a 16dp circle. */
    val SmallWidth: Dp = RedDotStockSmallWidth

    /** `coui_medium_width`: two digits, or a clamped count of four or more. */
    val MediumWidth: Dp = RedDotStockMediumWidth

    /** `coui_large_width`: three digits. */
    val LargeWidth: Dp = RedDotStockLargeWidth

    /** @deprecated use [SmallWidth]. */
    @Deprecated("Use SmallWidth, MediumWidth or LargeWidth.", ReplaceWith("SmallWidth"))
    val SingleDigitMinWidth: Dp = SmallWidth

    /** @deprecated the stock badge has three width tiers, not two. */
    @Deprecated("Use SmallWidth, MediumWidth or LargeWidth.", ReplaceWith("MediumWidth"))
    val MultiDigitMinWidth: Dp = MediumWidth

    /** Minimum width for text badges (`coui_medium_width`, so a word starts from 20dp). */
    val TextBadgeMinWidth: Dp = MediumWidth
    val BadgeHorizontalPadding: Dp = 4.5.dp
    val TextBadgeHorizontalPadding: Dp = 6.dp
}
