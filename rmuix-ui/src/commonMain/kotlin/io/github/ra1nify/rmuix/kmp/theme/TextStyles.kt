// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * The default text styles for the Rmuix components.
 *
 * @param main The main text style.
 * @param paragraph The paragraph text style.
 * @param body1 The body1 text style.
 * @param body2 The body2 text style.
 * @param button The button text style.
 * @param footnote1 The footnote1 text style.
 * @param footnote2 The footnote2 text style.
 * @param headline1 The headline1 text style.
 * @param headline2 The headline2 text style.
 * @param headlineLarge The headlineLarge text style (28sp Medium).
 * @param headlineSmall The headlineSmall text style (18sp Medium).
 * @param subtitle The subtitle text style.
 * @param title1 The title1 text style.
 * @param title2 The title2 text style.
 * @param title3 The title3 text style.
 * @param title4 The title4 text style.
 */
@Stable
class TextStyles(
    main: TextStyle,
    paragraph: TextStyle,
    body1: TextStyle,
    body2: TextStyle,
    button: TextStyle,
    footnote1: TextStyle,
    footnote2: TextStyle,
    headline1: TextStyle,
    headline2: TextStyle,
    headlineSmall: TextStyle,
    subtitle: TextStyle,
    title1: TextStyle,
    title2: TextStyle,
    title3: TextStyle,
    title4: TextStyle,
    headlineLarge: TextStyle = HeadlineLarge,
) {
    var main by mutableStateOf(main, structuralEqualityPolicy())
        internal set
    var paragraph by mutableStateOf(paragraph, structuralEqualityPolicy())
        internal set
    var body1 by mutableStateOf(body1, structuralEqualityPolicy())
        internal set
    var body2 by mutableStateOf(body2, structuralEqualityPolicy())
        internal set
    var button by mutableStateOf(button, structuralEqualityPolicy())
        internal set
    var footnote1 by mutableStateOf(footnote1, structuralEqualityPolicy())
        internal set
    var footnote2 by mutableStateOf(footnote2, structuralEqualityPolicy())
        internal set
    var headline1 by mutableStateOf(headline1, structuralEqualityPolicy())
        internal set
    var headline2 by mutableStateOf(headline2, structuralEqualityPolicy())
        internal set
    var headlineLarge by mutableStateOf(headlineLarge, structuralEqualityPolicy())
        internal set
    var headlineSmall by mutableStateOf(headlineSmall, structuralEqualityPolicy())
        internal set
    var subtitle by mutableStateOf(subtitle, structuralEqualityPolicy())
        internal set
    var title1 by mutableStateOf(title1, structuralEqualityPolicy())
        internal set
    var title2 by mutableStateOf(title2, structuralEqualityPolicy())
        internal set
    var title3 by mutableStateOf(title3, structuralEqualityPolicy())
        internal set
    var title4 by mutableStateOf(title4, structuralEqualityPolicy())
        internal set

    fun copy(
        main: TextStyle = this.main,
        paragraph: TextStyle = this.paragraph,
        body1: TextStyle = this.body1,
        body2: TextStyle = this.body2,
        button: TextStyle = this.button,
        footnote1: TextStyle = this.footnote1,
        footnote2: TextStyle = this.footnote2,
        headline1: TextStyle = this.headline1,
        headline2: TextStyle = this.headline2,
        headlineLarge: TextStyle = this.headlineLarge,
        headlineSmall: TextStyle = this.headlineSmall,
        subtitle: TextStyle = this.subtitle,
        title1: TextStyle = this.title1,
        title2: TextStyle = this.title2,
        title3: TextStyle = this.title3,
        title4: TextStyle = this.title4,
    ): TextStyles =
        TextStyles(
            main = main,
            paragraph = paragraph,
            body1 = body1,
            body2 = body2,
            button = button,
            footnote1 = footnote1,
            footnote2 = footnote2,
            headline1 = headline1,
            headline2 = headline2,
            headlineSmall = headlineSmall,
            subtitle = subtitle,
            title1 = title1,
            title2 = title2,
            title3 = title3,
            title4 = title4,
            headlineLarge = headlineLarge,
        )
}

/**
 * The library's type scale: `main` (body), `paragraph`, `body1` / `body2`, `button`, the two footnotes,
 * the headline ladder (`headline1` … `headlineSmall`), `subtitle`, `title1` … `title4` and
 * `headlineLarge`. Every slot has a stock-sized default, so a call can replace one role and keep the
 * rest; sizes are in sp and weights follow the `sans-serif-medium` conventions of the APK styles.
 */
fun defaultTextStyles(
    main: TextStyle = Main,
    paragraph: TextStyle = Paragraph,
    body1: TextStyle = Body1,
    body2: TextStyle = Body2,
    button: TextStyle = Button,
    footnote1: TextStyle = Footnote1,
    footnote2: TextStyle = Footnote2,
    headline1: TextStyle = Headline1,
    headline2: TextStyle = Headline2,
    headlineLarge: TextStyle = HeadlineLarge,
    headlineSmall: TextStyle = HeadlineSmall,
    subtitle: TextStyle = Subtitle,
    title1: TextStyle = Title1,
    title2: TextStyle = Title2,
    title3: TextStyle = Title3,
    title4: TextStyle = Title4,
): TextStyles =
    TextStyles(
        main = main,
        paragraph = paragraph,
        body1 = body1,
        body2 = body2,
        button = button,
        footnote1 = footnote1,
        footnote2 = footnote2,
        headline1 = headline1,
        headline2 = headline2,
        headlineSmall = headlineSmall,
        subtitle = subtitle,
        title1 = title1,
        title2 = title2,
        title3 = title3,
        title4 = title4,
        headlineLarge = headlineLarge,
    )

/** 17sp. **No official counterpart** — the ColorOS body ladder stops at 16sp (`couiTextBodyXL`). */
private val Main: TextStyle
    get() =
        TextStyle(
            fontSize = 17.sp,
        )

/** 17sp with 1.2 line height. **No official counterpart**; official body styles use 1.0–1.1. */
private val Paragraph: TextStyle
    get() =
        TextStyle(
            fontSize = 17.sp,
            lineHeight = 1.2f.em,
        )

/** 16sp — `couiTextBodyXL`. */
private val Body1: TextStyle
    get() =
        TextStyle(
            fontSize = 16.sp,
        )

/** 14sp — `couiTextBodyM`. */
private val Body2: TextStyle
    get() =
        TextStyle(
            fontSize = 14.sp,
        )

/** 16sp medium — official `couiTextButtonL` is 16sp but **not** medium; see coloros-typography.md. */
private val Button: TextStyle
    get() =
        TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )

/** 13sp — `couiTextBodyS`. */
private val Footnote1: TextStyle
    get() =
        TextStyle(
            fontSize = 13.sp,
        )

/** 12sp — `couiTextBodyXS`. */
private val Footnote2: TextStyle
    get() =
        TextStyle(
            fontSize = 12.sp,
        )

/** 17sp. **No official counterpart** (official has no 17sp step). */
private val Headline1: TextStyle
    get() =
        TextStyle(
            fontSize = 17.sp,
        )

/** 16sp — `couiTextHeadlineXS` (official draws it medium). */
private val Headline2: TextStyle
    get() =
        TextStyle(
            fontSize = 16.sp,
        )

/**
 * 28sp headline, ColorOS `couiTextHeadlineL`.
 *
 * Used by expanded top bar titles, large headers, and primary hero sections.
 */
private val HeadlineLarge: TextStyle
    get() =
        TextStyle(
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
        )

/**
 * 18sp headline, ColorOS `couiTextHeadlineS`.
 *
 * Sits between [Headline1] (17sp) and the title ramp; used by surfaces whose heading is deliberately
 * larger than a list title but smaller than a page title - the alert dialog title, for instance.
 */
private val HeadlineSmall: TextStyle
    get() =
        TextStyle(
            fontSize = 18.sp,
        )

/** 14sp bold. Officials use medium; there is no bold step in the ladder. */
private val Subtitle: TextStyle
    get() =
        TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )

/** 32sp — `couiTextDisplayS`. */
private val Title1: TextStyle
    get() =
        TextStyle(
            fontSize = 32.sp,
        )

/** 24sp — `couiTextDisplayXS` (yes, smaller than `couiTextHeadlineL`; the ladder is not monotonic). */
private val Title2: TextStyle
    get() =
        TextStyle(
            fontSize = 24.sp,
        )

/** 20sp — `couiTextHeadlineM`. */
private val Title3: TextStyle
    get() =
        TextStyle(
            fontSize = 20.sp,
        )

/** 18sp — `couiTextHeadlineS`, which duplicates [HeadlineSmall]. */
private val Title4: TextStyle
    get() =
        TextStyle(
            fontSize = 18.sp,
        )

@Stable
internal fun TextStyles.updateTextStylesFrom(other: TextStyles) {
    main = other.main
    paragraph = other.paragraph
    body1 = other.body1
    body2 = other.body2
    button = other.button
    footnote1 = other.footnote1
    footnote2 = other.footnote2
    headline1 = other.headline1
    headline2 = other.headline2
    headlineLarge = other.headlineLarge
    headlineSmall = other.headlineSmall
    subtitle = other.subtitle
    title1 = other.title1
    title2 = other.title2
    title3 = other.title3
    title4 = other.title4
}

internal val LocalTextStyles = staticCompositionLocalOf { defaultTextStyles() }
