// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

// Values from the `COUISearchViewAnimate` layout and style of the Settings APK
// (`coui_search_bar_normal_background_height`, `coui_search_view_corner`, `coui_search_view_icon_size`,
// `coui_search_view_search_icon_margin`, the toolbar start/end paddings and the search text sizes).

/** `coui_search_bar_normal_background_height`: the field is 40dp tall. */
internal val SearchBarStockFieldHeight = 40.dp

/**
 * `coui_search_view_corner`: the field background (`coui_searchview_corner_rect_bg`) uses an 18dp
 * radius, not a full capsule.
 */
internal val SearchBarStockFieldCornerRadius = 18.dp

/** `coui_search_view_input_text_size`: 15sp — the size `COUISearchViewAnimate` reads. (The sibling `coui_search_view_text_size`, also 15sp, is never read.) */
internal val SearchBarStockTextSize = 15.sp

/** `coui_search_view_text_hint_size`: 12sp, but nothing in this APK reads that dimen — treat 12sp as a visual default, not a derived value. */
internal val SearchBarStockHintTextSize = 12.sp

/**
 * Derived: the default magnifier is the 20dp `coui_search_view_icon_normal` vector, centred inside the
 * `coui_search_bar_inner_search_icon_size` 36dp box - `coui_search_bar_inner_search_icon_start_gap`
 * (4dp) + (36 - 20) / 2 (8dp) = 12dp from the field edge. The 24dp `coui_search_view_icon_size` is only
 * the cap for custom icons (`mIconMaxWidth` / `mIconMaxHeight`), not the drawn size.
 */
internal val SearchBarStockIconSize = 20.dp

/**
 * `coui_search_view_icon_size`: 24dp, stored as `mIconMaxWidth` / `mIconMaxHeight` and used only for
 * custom icons. **The stock zoom is a double density conversion**: `getDimensionPixelSize` already
 * returns px, and `COUISearchBar` then calls
 *
 * ```java
 * zoomDrawable(drawable, (int) (mIconMaxWidth * density), (int) (mIconMaxHeight * density))
 * ```
 *
 * which makes `Bitmap.createScaledBitmap` scale a custom icon to `24dp x density` px (about 75dp at
 * density 3.125) instead of 24dp. This port caps a custom icon at 24dp and deliberately does not
 * reproduce that conversion.
 */
internal val SearchBarStockIconMaxSize = 24.dp

/** `coui_search_bar_inner_search_icon_size`: the 36dp box the magnifier is centred in; the default leading icon is laid out in it. */
internal val SearchBarStockIconBoxSize = 36.dp

/** `coui_search_bar_inner_search_icon_start_gap`: the icon box starts 4dp from the field edge. */
internal val SearchBarStockIconStartGap = 4.dp

/** `coui_search_view_search_icon_margin`: the icon starts 12dp from the field edge. */
internal val SearchBarStockIconMargin = 12.dp

/**
 * The bar keeps 16dp on both sides. `coui_search_view_margin_left` / `_right` (both 16dp) exist but are
 * **never referenced**; the live equivalents are `coui_search_view_padding_start_in_toolbar` and
 * `coui_search_view_padding_end_in_toolbar`, both 16dp.
 */
internal val SearchBarStockSideMargin = 16.dp

/**
 * `coui_search_view_selector_color_normal` (read as `mNormalBackgroundColor` by `COUISearchBar`): the
 * field is a translucent neutral, not an opaque surface role. The resource is declared once per
 * qualifier, so `#14000000` applies to the day theme and `#1AFFFFFF` to the night theme.
 */
internal fun searchBarFieldColor(dark: Boolean): Color = if (dark) Color(0x1AFFFFFF) else Color(0x14000000)

/**
 * `coui_search_view_selector_color_pressed`: the pressed layer of the same field - `#1E000000` in the day
 * theme, `#33FFFFFF` at night.
 *
 * Consumed by `InputField`: pressing the field cross-fades its background to this colour for 150ms
 * stock value.
 */
internal fun searchBarFieldPressedColor(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1E000000)

/**
 * `ic_edit_text_delete_search_view`: the clear button is an **18dp** circle (`M18,18m-9,0a9,9 ...` in a
 * 36dp viewport) drawn inside the same 36dp box. The circle is `?attr/couiColorHintNeutral`; the cross is
 * a solid white **filled** path spanning 8.204dp (13.898..22.102 in the viewport) whose arms are 1.6dp
 * thick and cut at 45 degrees. Both shapes are drawn from the stock path data - see `SearchBar.kt`.
 */
internal val SearchBarStockClearIconSize = 18.dp
internal val SearchBarStockClearIconBoxSize = 36.dp

/**
 * `coui_search_view_close_button_selector` declares `enterFadeDuration = 180` and
 * `exitFadeDuration = 360`. Note the selector itself is an **orphan drawable** - no layout or code
 * in this APK references it - so the durations document the intended fade rather than an observed one. */
internal val SearchBarStockClearEnterFadeMillis = 180
internal val SearchBarStockClearExitFadeMillis = 360

/** `couiColorSecondNeutral` (`coui_color_secondary_neutral`): the magnifier tint, `#8A000000` / `#8AFFFFFF`. */
internal fun searchBarIconColor(dark: Boolean): Color = if (dark) Color(0x8AFFFFFF) else Color(0x8A000000)

/** `couiColorHintNeutral`: `#42000000` / `#4DFFFFFF`, the same neutral as the progress ring. */
internal fun searchBarClearIconBackground(dark: Boolean): Color = if (dark) Color(0x4DFFFFFF) else Color(0x42000000)

/**
 * `?attr/couiColorPress`: the circular press mask `SearchBarIconButton` paints with
 * `COUIMaskEffectDrawable` (the mask is an oval of the button's size), `#1F000000` / `#33FFFFFF`.
 */
internal fun searchBarIconButtonPressColor(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

/**
 * `coui_search_bar_normal_background_height`: the functional button is as tall as the search field,
 * 40dp (`SearchBarFunctionButton` reads it as `mInstantSearchButtonHeight`).
 */
internal val SearchBarStockFunctionButtonHeight = 40.dp

/** `coui_search_bar_functional_button_text_padding_horizontal`: 16dp. */
internal val SearchBarStockFunctionButtonTextPadding = 16.dp

/** `coui_search_bar_functional_button_max_width`: 152dp, the capsule's width cap. */
internal val SearchBarStockFunctionButtonMaxWidth = 152.dp

/** `couiTextAppearanceButton`: 14sp sans-serif-medium (`mFunctionalButton.setTextAppearance(...)`). */
internal val SearchBarStockFunctionButtonTextSize = 14.sp

/** `?attr/couiColorBar`: the capsule background, `#FAFAFA` / `#1E1E1E`. */
internal fun searchBarFunctionButtonBackground(dark: Boolean): Color = if (dark) Color(0xFF1E1E1E) else Color(0xFFFAFAFA)

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isSearchBarInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

// `COUISearchViewAnimate` expand / collapse timings and curves. All four interpolators in that class are
// `PathInterpolator`s, so they map to `CubicBezierEasing` one to one.

/** `DEFAULT_FADE_DURATION` = 150ms. */
internal const val SEARCH_ANIMATE_FADE_MILLIS = 150

/** `DEFAULT_PRESS_FEEDBACK_DURATION` = 150ms, driven by `COUIEaseInterpolator` (0.33, 0, 0.67, 1). */
internal const val SEARCH_ANIMATE_PRESS_MILLIS = 150

/** `DEFAULT_BUTTON_ALPHA_CHANGE_DURATION` = 350ms. */
internal const val SEARCH_ANIMATE_BUTTON_ALPHA_MILLIS = 350

/** `NON_INSTANT_SEARCH_BUTTON_ALPHA_CHANGE_DURATION` = 100ms. */
internal const val SEARCH_ANIMATE_BUTTON_ALPHA_INSTANT_MILLIS = 100

/** `DEFAULT_BUTTON_MOVE_DURATION` = 450ms. */
internal const val SEARCH_ANIMATE_BUTTON_MOVE_MILLIS = 450

/** `DEFAULT_SEARCH_VIEW_OFFSET_CHANGE_DURATION` = 450ms. */
internal const val SEARCH_ANIMATE_OFFSET_MILLIS = 450

/** `DEFAULT_SEARCH_VIEW_SCALE_CHANGE_DURATION` = 450ms. */
internal const val SEARCH_ANIMATE_SCALE_MILLIS = 450

/**
 * `COUIMoveEaseInterpolator` = `cubic-bezier(0.3, 0.0, 0.1, 1.0)`; used by the offset, scale and button
 * alpha animations.
 */
internal val SearchMoveEaseInterpolator = CubicBezierEasing(0.3f, 0f, 0.1f, 1f)

/** `COUIEaseInterpolator` = `cubic-bezier(0.33, 0.0, 0.67, 1.0)`; used by the press feedback. */
internal val SearchEaseInterpolator = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)
