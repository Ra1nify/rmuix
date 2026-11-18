// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme

/** `coui_navigation_enlarge_item_height`: the enlarged item is 70dp tall. */
internal val NavigationBarStockEnlargeItemHeight = 70.dp

/** `coui_navigation_enlarge_icon_size`: the selected icon grows to 64dp. */
internal val NavigationBarStockEnlargeIconSize = 64.dp

/** `coui_navigation_enlarge_icon_margin_top`: 9dp above an unselected icon. */
internal val NavigationBarStockEnlargeIconMarginTop = 9.dp

// Values from the Settings APK bottom navigation
// (`com.coui.appcompat.bottomnavigation.COUINavigationMenuView` / `COUINavigationItemView`,
// `COUIMaskEffectDrawable`) and the `coui_tool_navigation_*` / `coui_navigation_*` resources.

/** `coui_tool_navigation_edge_item_padding`: the bar's horizontal inset before the items. */
internal val NavigationBarStockEdgePadding = 12.dp

/** `coui_navigation_shadow_height`: the hairline above the bar. */
internal val NavigationBarStockDividerHeight = 0.33.dp

/** `coui_navigation_item_background_radius`: radius of the full-item state mask. */
internal val NavigationBarStockItemCornerRadius = 8.dp

/**
 * `coui_navigation_item_half_gap`: `coui_navigation_item_layout.xml` puts this 2dp margin on both sides
 * of every item, so two neighbours end up 4dp apart. (The `coui_navigation_item_gap` 4dp dimen exists but
 * nothing reads it — the gap is produced by the two margins, not by that dimen.)
 */
internal val NavigationBarStockItemGap = 2.dp

/**
 * `?attr/couiColorBar` (`coui_color_bar`): `#FAFAFA` / `#1E1E1E`. This is the bar background,
 * not `couiColorSurface`.
 */
internal fun navigationBarBar(dark: Boolean): Color = if (dark) Color(0xFF1E1E1E) else Color(0xFFFAFAFA)

/** `couiColorHover` used by the item's hover mask: `#14000000` / `#26FFFFFF`. */
internal fun navigationBarItemHover(dark: Boolean): Color = if (dark) Color(0x26FFFFFF) else Color(0x14000000)

/** `couiColorPress` used by the item's press mask: `#1F000000` / `#33FFFFFF`. */
internal fun navigationBarItemPress(dark: Boolean): Color = if (dark) Color(0x33FFFFFF) else Color(0x1F000000)

/** The item focus mask reuses the primary accent at 31/51 of 255, as `COUIMaskEffectDrawable` does. */
internal fun navigationBarItemFocus(
    primary: Color,
    dark: Boolean,
): Color = primary.copy(alpha = primary.alpha * (if (dark) 51f else 31f) / 255f)

// --- Floating bar tokens that are recorded but not consumed yet --------------------------------

/** `coui_bottom_floating_navigation_bar_item_bg_horizontal_padding`: 8dp inside the item background. */
internal val FloatingNavItemBackgroundHorizontalPadding = 8.dp

/** `coui_bottom_floating_navigation_bar_item_icon_title_gap_horizontal`: 4dp for horizontal items. */
internal val FloatingNavItemIconTitleGapHorizontal = 4.dp

/** `coui_bottom_floating_navigation_bar_touch_effect_radius`: 40dp press ripple. */
internal val FloatingNavTouchEffectRadius = 40.dp

/** `coui_bottom_floating_navigation_bar_min_height_without_padding_vertical`: 88dp (with the 14dp accessory). */
internal val FloatingNavMinHeightWithoutPaddingVertical = 88.dp

/** `coui_bottom_floating_navigation_bar_large_icon_overflow`: 14dp the selected icon may overflow its box. */
internal val FloatingNavLargeIconOverflow = 14.dp

/** `coui_bottom_floating_navigation_bar_Minimum_spacing_with_slide_view`: 12dp. */
internal val FloatingNavMinSpacingWithSlideView = 12.dp

/**
 * `coui_bottom_floating_navigation_bar_melt_ball_param_extension`: 4dp.
 *
 * `setMeltBallParams()` builds `OplusMaterialMetaBallParams(index, mode, extension, factorX, factorY)`
 * twice - `(2, 0, 4dp, 1.5f, 2.0f)` for the main pill and `(1, 0, 4dp, 1.5f, 2.0f)` for the menu - and then
 * `COUIMaterialEffect.setMetaBallChain(...)` links the two views.
 *
 * The framework side is now fully read (`oplus-framework.jar`, `classes2.dex`):
 *
 * - the 5-arg constructor validates `index` ∈ **[1, 3]** (`MIN_INDEX` / `MAX_INDEX`) and requires
 *   `mode` ∈ {`MODE_X` = 0, `MODE_Y` = 1}, throwing `IllegalArgumentException` otherwise - so `index` is
 *   the **position in the chain**, and `mode` is the axis the blob extends along (both views use `MODE_X`);
 * - `OplusMaterialUtil.setMetaBallParams(view, params)` forwards to
 *   - `RenderNode.setOplusMaterialMetaBallParams(IIIFF)Z` (public, 9 opcodes) forwards to
 *     `nSetOplusMaterialMetaBallParams(JIIIFF)Z`, whose `access_flags` are **266 = `private static
 *     native`** (`ACC_NATIVE` 0x100). The JNI implementation is registered inside
 *     `/system/lib64/libhwui.so` (string `nSetOplusMaterialMetaBallParams`, debug tag
 *     `SurfaceDrawContext_MetaBall`), i.e. the system **hardware-UI renderer**, not an app JNI library.
 *   - Consequently the shape/merge math lives only in that platform library (a fragment of its SkSL is
 *     readable in the `.so`, e.g. the `edgeImprove` luminance pass). A library can neither call it (the
 *     Java entry point sits on the platform `RenderNode` and only configures the platform draw pass) nor
 *     reproduce it numerically - AGSL can approximate but not equal it.
 *
 * This is why the port draws an AGSL meta-ball bridge instead: the stock fusion is a native render-node
 * effect, so the extension and factors are recorded for completeness but cannot be applied here.
 */
internal val FloatingNavMeltBallExtension = 4.dp

/** `MELT_BALL_EXTENSION_FACTOR_X` = 1.5, handed to the native render-node meta ball (see above). */
internal const val FLOATING_NAV_MELT_BALL_FACTOR_X = 1.5f

/** `MELT_BALL_EXTENSION_FACTOR_Y` = 2.0, handed to the native render-node meta ball (see above). */
internal const val FLOATING_NAV_MELT_BALL_FACTOR_Y = 2.0f

/** `..._red_dot_top_overflow`: 4dp. */
internal val FloatingNavRedDotTopOverflow = 4.dp

/** `..._red_dot_right_overflow_1_digit` / `_2_digit` / `_3_digit`: 6dp / 9dp / 10dp. */
internal fun floatingNavRedDotRightOverflow(digits: Int): Dp =
    when {
        digits <= 1 -> 6.dp
        digits == 2 -> 9.dp
        else -> 10.dp
    }

/** Resolves the neutral palette from the current library theme instead of the system theme. */
@Composable
internal fun isNavigationBarInDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()
