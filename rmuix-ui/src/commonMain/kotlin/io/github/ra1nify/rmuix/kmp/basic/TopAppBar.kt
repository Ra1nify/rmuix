// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastRoundToInt
import io.github.ra1nify.rmuix.kmp.basic.TopAppBarState.Companion.Saver
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.hairlineDp
import kotlin.math.abs

/**
 * A [TopAppBar] with ColorOS 17 styled visuals.
 *
 * @param title The title of the [TopAppBar].
 * @param modifier The modifier to be applied to the [TopAppBar].
 * @param color The background color of the [TopAppBar].
 * @param titleColor The color of the title text.
 * @param largeTitle The expanded large title. **Defaults to empty**, which renders the stock static
 *   `COUIToolbar` app bar (single 52dp row, 18sp title, `common_divider_appbar_layout.xml`). Passing
 *   `largeTitle = title` turns the bar into the collapsable `COUICollapsableAppBarLayout` form
 *   (110dp expanded, 32sp title, 24sp collapsed).
 * @param largeTitleColor The color of the expanded large title text.
 * @param subtitle The subtitle displayed below the title bar area.
 * @param subtitleColor The color of the subtitle text.
 * @param navigationIcon The [Composable] content that represents the navigation icon.
 * @param actions The [Composable] content that represents the action icons.
 * @param scrollBehavior The [ScrollBehavior] that controls the behavior of the [TopAppBar].
 * @param defaultWindowInsetsPadding Whether to apply default window insets padding to the [TopAppBar].
 * @param titlePadding The horizontal padding of the [TopAppBar]'s title.
 * @param navigationIconPadding The start padding of the navigation icon.
 * @param actionIconPadding The end padding of the action icons.
 * @param bottomContent The [Composable] content displayed below the title bar area.
 * @param showDivider Draw the 0.33dp `toolbar_divider_height` hairline under the bar. **Off by default**:
 *   the settings app bar inflates `common_divider_appbar_layout.xml`, which sets
 *   `settings:hasDivider="false"`, and ColorOS separates the bar with a continuous gradient blur
 *   ([AppBarBlurHelper], `setGradientBlurEnabled(true)`) rather than a line.
 * @param dividerColor The hairline colour when [showDivider] is on (`?attr/couiColorDivider`).
 */
@Composable
fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = backgroundWithCard(isBasicComponentInDarkTheme()),
    titleColor: Color = RmuixTheme.colorScheme.onSurface,
    largeTitle: String = "",
    largeTitleColor: Color = RmuixTheme.colorScheme.onSurface,
    subtitle: String = "",
    subtitleColor: Color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: ScrollBehavior? = null,
    defaultWindowInsetsPadding: Boolean = true,
    titlePadding: Dp = TopAppBarDefaults.TitlePadding,
    navigationIconPadding: Dp = TopAppBarDefaults.NavigationIconPadding,
    actionIconPadding: Dp = TopAppBarDefaults.ActionIconPadding,
    bottomContent: @Composable () -> Unit = {},
    showDivider: Boolean = false,
    dividerColor: Color = TopAppBarDefaults.dividerColor(),
    largeTitleBlurRadius: Dp = 0.dp,
) {
    val actionsRow =
        @Composable {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                content = actions,
            )
        }

    TopAppBarLayout(
        title = title,
        color = color,
        titleColor = titleColor,
        largeTitle = largeTitle,
        largeTitleColor = largeTitleColor,
        subtitle = subtitle,
        subtitleColor = subtitleColor,
        navigationIcon = navigationIcon,
        actions = actionsRow,
        titlePadding = titlePadding,
        navigationIconPadding = navigationIconPadding,
        actionIconPadding = actionIconPadding,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        bottomContent = bottomContent,
        showDivider = showDivider,
        dividerColor = dividerColor,
        largeTitleBlurRadius = largeTitleBlurRadius,
    )
}

/**
 * A [TopAppBar] whose large title blurs out as the bar collapses.
 *
 * Restores upstream miuix's `BlurTopAppBar`: the same bar as [TopAppBar], with a required
 * [largeTitleBlurRadius]. The blur follows the collapse fraction, so the title is sharp when the
 * bar is expanded and fully blurred once it has faded out.
 *
 * @param title The title of the bar.
 * @param largeTitleBlurRadius How far the large title blurs as it collapses; it reaches this radius
 *   at the point the title has fully faded.
 * @param modifier The modifier applied to the bar.
 * @param color The background color of the bar.
 * @param titleColor The color of the collapsed small title text.
 * @param largeTitle The expanded large title; empty (the default) keeps the stock static toolbar form.
 * @param largeTitleColor The color of the expanded large title text.
 * @param subtitle The subtitle displayed below the title bar area.
 * @param subtitleColor The color of the subtitle text.
 * @param navigationIcon The content of the navigation icon area.
 * @param actions The content of the action icons area.
 * @param scrollBehavior Controls the bar's scroll behaviour.
 * @param defaultWindowInsetsPadding Whether to apply default window insets padding.
 * @param titlePadding The horizontal padding of the title and large title.
 * @param navigationIconPadding The start padding of the navigation icon.
 * @param actionIconPadding The end padding of the action icons.
 * @param bottomContent Content displayed below the title bar area.
 */
@Composable
fun BlurTopAppBar(
    title: String,
    largeTitleBlurRadius: Dp,
    modifier: Modifier = Modifier,
    color: Color = backgroundWithCard(isBasicComponentInDarkTheme()),
    titleColor: Color = RmuixTheme.colorScheme.onSurface,
    largeTitle: String = "",
    largeTitleColor: Color = RmuixTheme.colorScheme.onSurface,
    subtitle: String = "",
    subtitleColor: Color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: ScrollBehavior? = null,
    defaultWindowInsetsPadding: Boolean = true,
    titlePadding: Dp = TopAppBarDefaults.TitlePadding,
    navigationIconPadding: Dp = TopAppBarDefaults.NavigationIconPadding,
    actionIconPadding: Dp = TopAppBarDefaults.ActionIconPadding,
    bottomContent: @Composable () -> Unit = {},
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        color = color,
        titleColor = titleColor,
        largeTitle = largeTitle,
        largeTitleColor = largeTitleColor,
        subtitle = subtitle,
        subtitleColor = subtitleColor,
        navigationIcon = navigationIcon,
        actions = actions,
        scrollBehavior = scrollBehavior,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        titlePadding = titlePadding,
        navigationIconPadding = navigationIconPadding,
        actionIconPadding = actionIconPadding,
        bottomContent = bottomContent,
        largeTitleBlurRadius = largeTitleBlurRadius,
    )
}

/**
 * A [SmallTopAppBar] with ColorOS 17 styled visuals.
 */
@Composable
fun SmallTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = backgroundWithCard(isBasicComponentInDarkTheme()),
    titleColor: Color = RmuixTheme.colorScheme.onSurface,
    subtitle: String = "",
    subtitleColor: Color = RmuixTheme.colorScheme.onSurfaceVariantSummary,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: ScrollBehavior? = null,
    defaultWindowInsetsPadding: Boolean = true,
    titlePadding: Dp = TopAppBarDefaults.TitlePadding,
    navigationIconPadding: Dp = TopAppBarDefaults.NavigationIconPadding,
    actionIconPadding: Dp = TopAppBarDefaults.ActionIconPadding,
    bottomContent: @Composable () -> Unit = {},
) {
    SideEffect {
        scrollBehavior?.state?.let { state ->
            if (!state.pinnedBySmallTopAppBar) state.pinnedBySmallTopAppBar = true
            if (state.heightOffsetLimit != 0f) state.heightOffsetLimit = 0f
            if (state.heightOffset != 0f) state.heightOffset = 0f
        }
    }

    val actionsRow =
        @Composable {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                content = actions,
            )
        }

    SmallTopAppBarLayout(
        title = title,
        color = color,
        titleColor = titleColor,
        subtitle = subtitle,
        subtitleColor = subtitleColor,
        navigationIcon = navigationIcon,
        actions = actionsRow,
        titlePadding = titlePadding,
        navigationIconPadding = navigationIconPadding,
        actionIconPadding = actionIconPadding,
        modifier = modifier,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        bottomContent = bottomContent,
    )
}

/**
 * An `ExitUntilCollapsedScrollBehavior` pre-wired with the library's spring. Feed it to `Scaffold`'s
 * `topBar` slot through [TopAppBar]'s `scrollBehavior` so the large title collapses with the scroll.
 * The composable name keeps the `Rmuix` prefix but behaves as a factory, hence the `@Suppress`.
 */
@Suppress("ComposableNaming")
@Composable
fun RmuixScrollBehavior(
    state: TopAppBarState = rememberTopAppBarState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = 2500f),
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay(),
): ScrollBehavior =
    remember(state, canScroll, snapAnimationSpec, flingAnimationSpec) {
        ExitUntilCollapsedScrollBehavior(
            state = state,
            snapAnimationSpec = snapAnimationSpec,
            flingAnimationSpec = flingAnimationSpec,
            canScroll = canScroll,
        )
    }

/**
 * Remembers the [TopAppBarState] used by [RmuixScrollBehavior], saving the height and content offsets
 * across configuration changes. The three arguments mirror Material3's helper so the behaviour can be
 * pre-scrolled; the stock app bar starts fully expanded, hence `0f` offsets.
 */
@Composable
fun rememberTopAppBarState(
    initialHeightOffsetLimit: Float = -Float.MAX_VALUE,
    initialHeightOffset: Float = 0f,
    initialContentOffset: Float = 0f,
): TopAppBarState =
    rememberSaveable(saver = Saver) {
        TopAppBarState(initialHeightOffsetLimit, initialHeightOffset, initialContentOffset)
    }

@Stable
class TopAppBarState(
    initialHeightOffsetLimit: Float,
    initialHeightOffset: Float,
    initialContentOffset: Float,
) {
    var heightOffsetLimit = initialHeightOffsetLimit

    var heightOffset: Float
        get() = _heightOffset.floatValue
        set(newOffset) {
            _heightOffset.floatValue =
                newOffset.coerceIn(minimumValue = heightOffsetLimit, maximumValue = 0f)
        }

    var contentOffset by mutableFloatStateOf(initialContentOffset)

    internal var pinnedBySmallTopAppBar by mutableStateOf(false)

    /**
     * Measured height of the fully expanded bar, published by the layout.
     * `COUIDividerAppBarLayout.onDividerChanged()` divides the accumulated scroll distance by
     * `getMeasuredHeight()` (`getDividerScrollRange()`), so the divider needs this value too.
     */
    internal var expandedHeightPx by mutableFloatStateOf(0f)

    val collapsedFraction: Float
        get() =
            if (heightOffsetLimit != 0f) {
                heightOffset / heightOffsetLimit
            } else {
                0f
            }

    val overlappedFraction: Float
        get() =
            if (heightOffsetLimit != 0f) {
                1 -
                    (
                        (heightOffsetLimit - contentOffset).coerceIn(
                            minimumValue = heightOffsetLimit,
                            maximumValue = 0f,
                        ) / heightOffsetLimit
                    )
            } else {
                0f
            }

    companion object {
        val Saver: Saver<TopAppBarState, *> =
            listSaver(
                save = {
                    listOf(
                        it.heightOffsetLimit,
                        it.heightOffset,
                        it.contentOffset,
                        it.pinnedBySmallTopAppBar,
                    )
                },
                restore = {
                    TopAppBarState(
                        initialHeightOffsetLimit = it[0] as Float,
                        initialHeightOffset = it[1] as Float,
                        initialContentOffset = it[2] as Float,
                    ).apply {
                        pinnedBySmallTopAppBar = it.getOrNull(3) as? Boolean ?: false
                    }
                },
            )
    }

    private var _heightOffset = mutableFloatStateOf(initialHeightOffset)
}

object TopAppBarDefaults {
    /**
     * `coui_toolbar_gap_between_navigation_and_title` = 4dp: the stock `COUIToolbar` places the title
     * exactly 4dp after the navigation button (its `supportTitleMarginStart` resolves to
     * `coui_toolbar_support_margin_start` = 0dp). Applied as the title/subtitle Box's horizontal padding,
     * so it is the visible gap on the start side.
     */
    val TitlePadding = 4.dp

    /** `toolbar_normal_menu_padding_left` = 8dp: the stock toolbar's own start padding. */
    val NavigationIconPadding = 8.dp

    /** `toolbar_normal_menu_padding_right` = 11dp: the stock toolbar's own end padding. */
    val ActionIconPadding = 11.dp
    val CollapsedHeight = 52.dp
    val SmallTopAppBarCenterHeight = 50.dp
    val LargeTitleBottomPadding = 4.dp

    /**
     * Library value used by **non-collapsing** bars: the gap between the subtitle and the bar's bottom.
     * Collapsable bars read [StartPaddingBottom] / [EndPaddingBottom] instead.
     */
    val SubtitleBottomPadding = 8.dp

    /**
     * `coui_appbar_start_padding_bottom` = 12dp: while the collapsable bar is expanded it keeps 12dp of
     * bottom padding (`COUICollapsableAppBarLayout.mStartPaddingBottom`), matching the subtitle's and the
     * divider's `-(start + (end - start) * fraction)` translation.
     */
    val StartPaddingBottom = 12.dp

    /** `coui_appbar_end_padding_bottom` = 0dp: the padding is gone once the bar is pinned. */
    val EndPaddingBottom = 0.dp

    /**
     * `TextAppearance.COUI.Toolbar.SecondTitle` (`textAppearanceSecondTitle`, the appearance
     * `common_divider_appbar_layout.xml` requests): 18dp, normal `textStyle`, `sans-serif-medium`.
     * This is the stock **static** app bar title and the size [TopAppBar] uses when no large title
     * is passed.
     */
    val ToolbarTitleTextSize = 18.sp

    /**
     * `coui_appbar_title_collapsed_text_size`: the collapsed title of the **collapsable**
     * `COUICollapsableAppBarLayout` form (the bar that also shows [LargeTitleTextSize] while
     * expanded). Only used when a large title is present.
     */
    val TitleTextSize = 24.sp

    /**
     * 18sp once a subtitle is present.
     *
     * `coui_appbar_title_collapsed_with_subtitle_text_size` (18dp) is **declared but never referenced** —
     * no `@dimen` use in `styles.xml` and no `R.dimen` read — so 18sp is a library choice, not a ported
     * value. The live size for the no-subtitle case is `coui_appbar_title_collapsed_text_size` (24dp,
     * applied through `android:textSize`).
     *
     * The only live 18dp title size in this APK belongs to a different component: the Settings homepage
     * header reads `toolbar_title_final_text_size` (18dp) as the *collapsed* step of its scroll-driven
     * title scale (`HomepageHeadScaleWithSearchBhv`), paired with `homepage_header_title_text_size` for the
     * expanded step. That is a homepage behaviour, not `COUICollapsableAppBarLayout`, so it is not used
     * here.
     */
    val TitleTextSizeWithSubtitle = 18.sp

    /**
     * `coui_appbar_subtitle_text_size`: 14dp, the subtitle size of the **collapsable** app bar form.
     * The static `COUIToolbar` form uses [ToolbarSubtitleTextSize] (13sp) instead.
     */
    val SubtitleTextSize = 14.sp

    /**
     * `coui_toolbar_subtitle_text_size` (13dp) through
     * `TextAppearance.COUI.AppCompatSupport.Toolbar.SubTitle`: the static toolbar's subtitle size.
     *
     * `coui_toolbar_subtitle_offset_top` (-2.5dp) is a **dead token** for this class: it is declared but
     * `COUIToolbar` never reads it, so no offset is applied.
     */
    val ToolbarSubtitleTextSize = 13.sp

    /** `coui_appbar_title_expanded_text_size`: the expanded large title is 32dp. */
    val LargeTitleTextSize = 32.sp

    /** `coui_appbar_title_expanded_height`: the bar is 110dp tall while the large title shows. */
    val LargeTitleExpandedHeight = 110.dp

    /** `coui_appbar_title_expanded_margin_top`: the large title starts 54dp from the bar's top. */
    val LargeTitleMarginTop = 54.dp

    /** `coui_appbar_title_expanded_margin_start/end`: 24dp on both sides. */
    val LargeTitleMarginHorizontal = 24.dp

    /** `toolbar_divider_height`: the hairline under the bar is 0.33dp. */
    val DividerHeight = 0.33.dp

    /** `coui_appbar_divider_expanded_margin_horizontal`: 24dp while the large title is expanded. */
    val DividerExpandedMargin = 24.dp

    /** `coui_appbar_divider_collapsed_margin_horizontal`: 0dp once the bar is collapsed. */
    val DividerCollapsedMargin = 0.dp

    /** `?attr/couiColorDivider`: `#1F000000` / `#33FFFFFF`, the same selector [Divider] uses. */
    @Composable
    fun dividerColor(): Color = dividerStockColor(isDividerInDarkTheme())
}

@Stable
interface ScrollBehavior {
    val state: TopAppBarState
    val isPinned: Boolean
    val snapAnimationSpec: AnimationSpec<Float>?
    val flingAnimationSpec: DecayAnimationSpec<Float>?
    val nestedScrollConnection: NestedScrollConnection
}

private class ExitUntilCollapsedScrollBehavior(
    override val state: TopAppBarState,
    override val snapAnimationSpec: AnimationSpec<Float>?,
    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
    val canScroll: () -> Boolean = { true },
) : ScrollBehavior {
    override val isPinned: Boolean = false
    override var nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (!canScroll() || available.y > 0) return Offset.Zero
                val prevHeightOffset = state.heightOffset
                state.heightOffset += available.y
                return if (prevHeightOffset != state.heightOffset) {
                    available.copy(x = 0f)
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (!canScroll()) return Offset.Zero
                state.contentOffset += consumed.y

                if (available.y < 0f || consumed.y < 0f) {
                    val oldHeightOffset = state.heightOffset
                    state.heightOffset += consumed.y
                    return Offset(0f, state.heightOffset - oldHeightOffset)
                }

                if (available.y > 0f) {
                    val oldHeightOffset = state.heightOffset
                    state.heightOffset += available.y
                    return Offset(0f, state.heightOffset - oldHeightOffset)
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity,
            ): Velocity {
                if (available.y > 0) {
                    state.contentOffset = 0f
                }
                val superConsumed = super.onPostFling(consumed, available)
                return superConsumed +
                    settleAppBar(state, available.y, flingAnimationSpec, snapAnimationSpec)
            }
        }
}

private suspend fun settleAppBar(
    state: TopAppBarState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?,
): Velocity {
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(initialValue = 0f, initialVelocity = velocity).animateDecay(
            flingAnimationSpec,
        ) {
            val delta = value - lastValue
            val initialHeightOffset = state.heightOffset
            state.heightOffset = initialHeightOffset + delta
            val consumed = abs(initialHeightOffset - state.heightOffset)
            lastValue = value
            remainingVelocity = this.velocity
            if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
        }
    }
    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 && state.heightOffset > state.heightOffsetLimit) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                if (state.collapsedFraction < 0.5f) {
                    0f
                } else {
                    state.heightOffsetLimit
                },
                animationSpec = snapAnimationSpec,
            ) {
                state.heightOffset = value
            }
        }
    }
    return Velocity(0f, velocity - remainingVelocity)
}

@Composable
private fun TopAppBarLayout(
    title: String,
    color: Color,
    titleColor: Color,
    largeTitle: String,
    largeTitleColor: Color,
    subtitle: String,
    subtitleColor: Color,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    titlePadding: Dp,
    navigationIconPadding: Dp,
    actionIconPadding: Dp,
    scrollBehavior: ScrollBehavior?,
    modifier: Modifier = Modifier,
    defaultWindowInsetsPadding: Boolean = true,
    bottomContent: @Composable () -> Unit = {},
    showDivider: Boolean = false,
    dividerColor: Color = TopAppBarDefaults.dividerColor(),
    largeTitleBlurRadius: Dp = 0.dp,
) {
    // Titles are clipped to the bar viewport, while shadows from the controls and bottom content may
    // still draw outside it (upstream `clipTopAppBarTitle`).
    val titleClip = remember { TopAppBarTitleClip() }
    val layoutDirection = LocalLayoutDirection.current
    // `toolbar_divider_height` is 0.33dp - sub-pixel on purpose. Android densities round it up to one
    // pixel, but a 1x desktop density would round it to zero and lose the line, so clamp to 1px.
    val dividerThickness = with(LocalDensity.current) { hairlineDp(TopAppBarDefaults.DividerHeight) }

    val animatedTitleColor by animateColorAsState(
        targetValue = titleColor,
        animationSpec = tween(durationMillis = 50),
    )
    val animatedSubtitleColor by animateColorAsState(
        targetValue = subtitleColor,
        animationSpec = tween(durationMillis = 50),
    )

    // Two stock forms: the static `COUIToolbar` (18sp medium) when no large title is passed, and the
    // collapsable `COUICollapsableAppBarLayout` (24sp bold collapsed, 32sp semibold expanded) when one is.
    // A subtitle row drops both to 18sp.
    val titleMetrics = topAppBarTitleMetrics(hasLargeTitle = largeTitle.isNotEmpty(), hasSubtitle = subtitle.isNotEmpty())

    Layout(
        {
            Box(
                Modifier
                    .layoutId("navigationIcon")
                    .padding(start = navigationIconPadding),
            ) {
                navigationIcon()
            }
            Box(
                Modifier
                    .layoutId("title")
                    .clipTopAppBarTitle(titleClip) { titleClip.titleOrigin }
                    .padding(horizontal = titlePadding),
            ) {
                Text(
                    text = title,
                    color = animatedTitleColor,
                    fontSize = titleMetrics.textSize,
                    fontWeight = titleMetrics.fontWeight,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false,
                    // ColorOS keeps the toolbar title at full alpha (`fixCollapsingTitleAlpha()` sets the
                    // collapse fade to start at fraction 1 and forces the text paint opaque), so no
                    // scroll-driven alpha here.
                    modifier = Modifier,
                )
            }
            Box(
                Modifier
                    .layoutId("actionIcons")
                    .padding(end = actionIconPadding),
            ) {
                actions()
            }
            if (subtitle.isNotEmpty()) {
                Box(
                    Modifier
                        .layoutId("subtitle")
                        .clipTopAppBarTitle(titleClip) { titleClip.subtitleOrigin }
                        .padding(horizontal = titlePadding),
                ) {
                    Text(
                        text = subtitle,
                        color = animatedSubtitleColor,
                        style = RmuixTheme.textStyles.body2,
                        fontSize = topAppBarSubtitleTextSize(hasLargeTitle = largeTitle.isNotEmpty()),
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                    )
                }
            }
            Box(Modifier.layoutId("bottomContent")) {
                bottomContent()
            }
            // `coui_appbar_divider_layout.xml`: a 0.33dp hairline on the bar's bottom edge. Its side
            // insets shrink from 24dp to 0dp and its alpha ramps 0 -> 1 as the content scrolls by the
            // bar's own height (`COUIDividerAppBarLayout.onDividerChanged()`:
            // `fraction = totalScrollDy / getMeasuredHeight()`), not with the collapse progress.
            if (showDivider) {
                Box(
                    Modifier
                        .layoutId("divider")
                        .height(dividerThickness)
                        .graphicsLayer {
                            alpha =
                                topAppBarDividerAlpha(
                                    topAppBarDividerFraction(
                                        scrollDistancePx = -(scrollBehavior?.state?.contentOffset ?: 0f),
                                        appBarHeightPx = scrollBehavior?.state?.expandedHeightPx ?: 0f,
                                    ),
                                )
                        }.background(dividerColor),
                )
            }
            if (largeTitle.isNotEmpty()) {
                // `coui_appbar_title_expanded_*`: the expanded title sits 54dp from the top with
                // 24dp side margins and fades out as the bar collapses.
                Box(
                    Modifier
                        .layoutId("largeTitle")
                        .clipTopAppBarTitle(titleClip) { titleClip.largeTitleOrigin }
                        .padding(
                            start = TopAppBarDefaults.LargeTitleMarginHorizontal,
                            end = TopAppBarDefaults.LargeTitleMarginHorizontal,
                        ),
                ) {
                    Text(
                        text = largeTitle,
                        color = largeTitleColor,
                        fontSize = TopAppBarDefaults.LargeTitleTextSize,
                        // `COUIAppbarTitleStyle.Expanded` uses `sans-serif-semibold`.
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                        // The expanded title keeps its position and opacity while the bar collapses
                        // (ColorOS disables the collapsing fade); only the opt-in blur variant reacts.
                        modifier = Modifier.largeTitleBlurEffect(scrollBehavior, largeTitleBlurRadius),
                    )
                }
            }
        },
        modifier =
            modifier
                .background(color)
                .then(
                    if (defaultWindowInsetsPadding) {
                        Modifier
                            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                    } else {
                        Modifier
                    },
                ).windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                .pointerInput(Unit) {
                    detectTapGestures { /* Consume click */ }
                },
    ) { measurables, constraints ->
        val navigationIconPlaceable =
            measurables
                .fastFirst { it.layoutId == "navigationIcon" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val actionIconsPlaceable =
            measurables
                .fastFirst { it.layoutId == "actionIcons" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val maxTitleWidth =
            if (constraints.maxWidth == Constraints.Infinity) {
                constraints.maxWidth
            } else {
                (constraints.maxWidth - navigationIconPlaceable.width - actionIconsPlaceable.width)
                    .coerceAtLeast(0)
            }
        val titleMaxWidth =
            if (maxTitleWidth == Constraints.Infinity) {
                maxTitleWidth
            } else {
                ((maxTitleWidth * TITLE_WIDTH_FRACTION).toFloat()).fastRoundToInt()
            }

        val titlePlaceable =
            measurables
                .fastFirst { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = titleMaxWidth, minHeight = 0))

        val subtitlePlaceable =
            measurables
                .firstOrNull { it.layoutId == "subtitle" }
                ?.measure(constraints.copy(minWidth = 0, maxWidth = titleMaxWidth, minHeight = 0))

        val bottomContentPlaceable =
            measurables
                .fastFirst { it.layoutId == "bottomContent" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val largeTitlePlaceable =
            measurables
                .firstOrNull { it.layoutId == "largeTitle" }
                ?.measure(constraints.copy(minWidth = 0, minHeight = 0))

        val subtitleHeight = subtitlePlaceable?.height ?: 0
        val collapsedHeight = TopAppBarDefaults.CollapsedHeight.roundToPx()
        val verticalCenter = TopAppBarDefaults.SmallTopAppBarCenterHeight.roundToPx() / 2
        val subtitleY = verticalCenter + titlePlaceable.height / 2
        // A collapsable bar keeps `coui_appbar_start_padding_bottom` as bottom padding while expanded and
        // loses it when pinned (`COUICollapsableAppBarLayout.adjustPaddingBottom()`); a static bar keeps the
        // library's fixed subtitle gap.
        val collapsePaddingPx =
            if (scrollBehavior != null) {
                topAppBarCollapseBottomPaddingPx(
                    startPx = TopAppBarDefaults.StartPaddingBottom.roundToPx(),
                    endPx = TopAppBarDefaults.EndPaddingBottom.roundToPx(),
                    fraction = scrollBehavior.state.collapsedFraction,
                )
            } else {
                0
            }
        val subtitleBottomPadding =
            if (subtitlePlaceable != null) {
                TopAppBarDefaults.SubtitleBottomPadding.roundToPx() + collapsePaddingPx
            } else {
                collapsePaddingPx
            }
        val contentTop = maxOf(collapsedHeight, subtitleY + subtitleHeight + subtitleBottomPadding)
        val collapsedLayoutHeight = contentTop + bottomContentPlaceable.height
        // `coui_appbar_title_expanded_height` (110dp) is the expanded height when a large title is
        // present; the title itself starts 54dp from the top.
        val expandedContentHeight =
            if (largeTitlePlaceable != null) {
                maxOf(
                    TopAppBarDefaults.LargeTitleExpandedHeight.roundToPx(),
                    TopAppBarDefaults.LargeTitleMarginTop.roundToPx() + largeTitlePlaceable.height,
                ) + bottomContentPlaceable.height
            } else {
                collapsedLayoutHeight
            }

        // `ExitUntilCollapsedScrollBehavior` only has a visible effect once the bar consumes its
        // height offset; without this the behaviour computes offsets nobody reads.
        val heightOffset = scrollBehavior?.state?.heightOffset ?: 0f
        // `collapsedLayoutHeight` already includes `bottomContent`; clamping to the bare
        // `collapsedHeight` would drop the tabs/handles as soon as the bar is pinned.
        val layoutHeight = topAppBarLayoutHeight(expandedContentHeight, collapsedLayoutHeight, heightOffset)
        if (scrollBehavior != null) {
            scrollBehavior.state.heightOffsetLimit =
                topAppBarHeightOffsetLimit(expandedContentHeight, collapsedLayoutHeight)
            scrollBehavior.state.expandedHeightPx = expandedContentHeight.toFloat()
            // A small bar that pinned this state forced the offsets to 0; the large bar takes the
            // state back and restores the offset from the content's scroll position.
            if (scrollBehavior.state.pinnedBySmallTopAppBar) {
                scrollBehavior.state.pinnedBySmallTopAppBar = false
                scrollBehavior.state.heightOffset =
                    if (scrollBehavior.state.contentOffset < 0f) scrollBehavior.state.heightOffsetLimit else 0f
            }
            if (scrollBehavior.state.heightOffset < scrollBehavior.state.heightOffsetLimit) {
                scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffsetLimit
            }
        }

        val dividerFraction =
            topAppBarDividerFraction(
                scrollDistancePx = -(scrollBehavior?.state?.contentOffset ?: 0f),
                appBarHeightPx = expandedContentHeight.toFloat(),
            )
        val dividerMarginPx =
            topAppBarDividerMarginPx(
                expandedMarginPx = TopAppBarDefaults.DividerExpandedMargin.toPx(),
                collapsedMarginPx = TopAppBarDefaults.DividerCollapsedMargin.toPx(),
                fraction = dividerFraction,
            )
        val dividerWidth =
            if (constraints.maxWidth == Constraints.Infinity) {
                0
            } else {
                (constraints.maxWidth - 2 * dividerMarginPx).fastRoundToInt().coerceAtLeast(0)
            }
        val dividerPlaceable =
            if (showDivider) {
                measurables
                    .firstOrNull { it.layoutId == "divider" }
                    ?.measure(constraints.copy(minWidth = dividerWidth, maxWidth = dividerWidth))
            } else {
                null
            }

        titleClip.viewportSize = IntSize(constraints.maxWidth, layoutHeight)

        layout(constraints.maxWidth, layoutHeight) {
            // Navigation icon
            navigationIconPlaceable.placeRelative(
                x = 0,
                y = verticalCenter - navigationIconPlaceable.height / 2,
            )

            // Title - ColorOS 17 Left/Start Aligned
            val startX = navigationIconPlaceable.width
            val titleY = verticalCenter - titlePlaceable.height / 2
            titleClip.titleOrigin =
                IntOffset(
                    if (layoutDirection == LayoutDirection.Ltr) startX else constraints.maxWidth - startX - titlePlaceable.width,
                    titleY,
                )
            titlePlaceable.placeRelative(
                x = startX,
                y = titleY,
            )

            // Action icons
            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = verticalCenter - actionIconsPlaceable.height / 2,
            )

            // The expanded title slides up with the collapsing bar and is clipped by the layout.
            // Its Box already carries `coui_appbar_title_expanded_margin_start/end`, so placing it at the
            // container's start would inset it twice (the stock title sits 24dp from the edge).
            // `coui_appbar_title_expanded_margin_top` is a fixed 54dp: ColorOS does not slide the text
            // with the scroll offset.
            val largeTitleY = TopAppBarDefaults.LargeTitleMarginTop.roundToPx()
            largeTitlePlaceable?.let { placeable ->
                titleClip.largeTitleOrigin = IntOffset(0, largeTitleY)
                placeable.placeRelative(x = 0, y = largeTitleY)
            }

            // The divider rides the bar's bottom edge (`COUIDividerAppBarLayout`), minus the collapsable
            // bar's bottom padding - the stock implementation translates it by the same amount
            // (`translateDivider(-(start + (end - start) * fraction))`), which keeps it in place while the
            // padding animates.
            dividerPlaceable?.placeRelative(
                x = dividerMarginPx.fastRoundToInt(),
                y = layoutHeight - dividerPlaceable.height - collapsePaddingPx,
            )

            // Subtitle - aligned with Title
            subtitlePlaceable?.let { placeable ->
                titleClip.subtitleOrigin =
                    IntOffset(
                        if (layoutDirection == LayoutDirection.Ltr) startX else constraints.maxWidth - startX - placeable.width,
                        subtitleY,
                    )
                placeable.placeRelative(x = startX, y = subtitleY)
            }

            // Bottom content
            bottomContentPlaceable.placeRelative(x = 0, y = contentTop)
        }
    }
}

@Composable
private fun SmallTopAppBarLayout(
    title: String,
    color: Color,
    titleColor: Color,
    subtitle: String,
    subtitleColor: Color,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    titlePadding: Dp,
    navigationIconPadding: Dp,
    actionIconPadding: Dp,
    modifier: Modifier = Modifier,
    defaultWindowInsetsPadding: Boolean = true,
    bottomContent: @Composable () -> Unit = {},
) {
    val animatedTitleColor by animateColorAsState(
        targetValue = titleColor,
        animationSpec = tween(durationMillis = 50),
    )
    val animatedSubtitleColor by animateColorAsState(
        targetValue = subtitleColor,
        animationSpec = tween(durationMillis = 50),
    )

    // The pinned bar is the stock static `COUIToolbar`: `textAppearanceSecondTitle` = 18sp,
    // `sans-serif-medium` (`TextAppearance.COUI.Toolbar.SecondTitle`). A subtitle keeps the same size.
    val titleSize = TopAppBarDefaults.ToolbarTitleTextSize

    Layout(
        {
            Box(
                Modifier
                    .layoutId("navigationIcon")
                    .padding(start = navigationIconPadding),
            ) {
                navigationIcon()
            }
            Box(
                Modifier
                    .layoutId("title")
                    .padding(horizontal = titlePadding),
            ) {
                Text(
                    text = title,
                    color = animatedTitleColor,
                    maxLines = 1,
                    fontSize = titleSize,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false,
                )
            }
            Box(
                Modifier
                    .layoutId("actionIcons")
                    .padding(end = actionIconPadding),
            ) {
                actions()
            }
            if (subtitle.isNotEmpty()) {
                Box(
                    Modifier
                        .layoutId("subtitle")
                        .padding(horizontal = titlePadding),
                ) {
                    Text(
                        text = subtitle,
                        color = animatedSubtitleColor,
                        style = RmuixTheme.textStyles.body2,
                        // The pinned bar is the static `COUIToolbar` form: 13sp subtitle.
                        fontSize = TopAppBarDefaults.ToolbarSubtitleTextSize,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                    )
                }
            }
            Box(Modifier.layoutId("bottomContent")) {
                bottomContent()
            }
        },
        modifier =
            modifier
                .background(color)
                .then(
                    if (defaultWindowInsetsPadding) {
                        Modifier
                            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                    } else {
                        Modifier
                    },
                ).windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTapGestures { /* Consume click */ }
                },
    ) { measurables, constraints ->
        val navigationIconPlaceable =
            measurables
                .fastFirst { it.layoutId == "navigationIcon" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val actionIconsPlaceable =
            measurables
                .fastFirst { it.layoutId == "actionIcons" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val maxTitleWidth =
            if (constraints.maxWidth == Constraints.Infinity) {
                constraints.maxWidth
            } else {
                (constraints.maxWidth - navigationIconPlaceable.width - actionIconsPlaceable.width)
                    .coerceAtLeast(0)
            }
        val titleMaxWidth =
            if (maxTitleWidth == Constraints.Infinity) {
                maxTitleWidth
            } else {
                ((maxTitleWidth * TITLE_WIDTH_FRACTION).toFloat()).fastRoundToInt()
            }

        val titlePlaceable =
            measurables
                .fastFirst { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = titleMaxWidth, minHeight = 0))

        val subtitlePlaceable =
            measurables
                .firstOrNull { it.layoutId == "subtitle" }
                ?.measure(constraints.copy(minWidth = 0, maxWidth = titleMaxWidth, minHeight = 0))

        val bottomContentPlaceable =
            measurables
                .fastFirst { it.layoutId == "bottomContent" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val subtitleHeight = subtitlePlaceable?.height ?: 0
        val collapsedHeight = TopAppBarDefaults.CollapsedHeight.roundToPx()
        val verticalCenter = TopAppBarDefaults.SmallTopAppBarCenterHeight.roundToPx() / 2
        val subtitleY = verticalCenter + titlePlaceable.height / 2
        val subtitleBottomPadding = if (subtitlePlaceable != null) TopAppBarDefaults.SubtitleBottomPadding.roundToPx() else 0
        val contentTop = maxOf(collapsedHeight, subtitleY + subtitleHeight + subtitleBottomPadding)
        val layoutHeight = contentTop + bottomContentPlaceable.height

        layout(constraints.maxWidth, layoutHeight) {
            navigationIconPlaceable.placeRelative(
                x = 0,
                y = verticalCenter - navigationIconPlaceable.height / 2,
            )

            val startX = navigationIconPlaceable.width
            titlePlaceable.placeRelative(
                x = startX,
                y = verticalCenter - titlePlaceable.height / 2,
            )

            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = verticalCenter - actionIconsPlaceable.height / 2,
            )

            subtitlePlaceable?.placeRelative(
                x = startX,
                y = subtitleY,
            )

            bottomContentPlaceable.placeRelative(x = 0, y = contentTop)
        }
    }
}

/**
 * Blurs the large title by up to [blurRadius] as the bar collapses. The radius follows the same
 * fraction as [largeTitleCollapseEffect]'s alpha, so the title reaches full blur exactly when it
 * has faded out; a bar without a scroll behaviour stays sharp.
 *
 * Ported from upstream miuix's `BlurTopAppBar` (`largeTitleBlurRadius`).
 */
private fun Modifier.largeTitleBlurEffect(
    scrollBehavior: ScrollBehavior?,
    blurRadius: Dp,
): Modifier {
    if (blurRadius <= 0.dp) return this
    val state = scrollBehavior?.state ?: return this
    return this.graphicsLayer {
        val limit = state.heightOffsetLimit
        val fraction = if (limit != 0f) (state.heightOffset / limit).coerceIn(0f, 1f) else 0f
        val radius = blurRadius.toPx() * fraction
        // Assigned on every path: the layer keeps its last effect, so a radius that drops back to
        // zero would otherwise leave the title blurred for good.
        renderEffect = if (radius > 0.1f) BlurEffect(radius, radius, TileMode.Decal) else null
    }
}

private const val TITLE_WIDTH_FRACTION = 0.9

/** Viewport geometry published by the layout and read only while drawing the title layers. */
private class TopAppBarTitleClip {
    var viewportSize: IntSize by mutableStateOf(IntSize.Zero)
    var titleOrigin: IntOffset by mutableStateOf(IntOffset.Zero)
    var largeTitleOrigin: IntOffset by mutableStateOf(IntOffset.Zero)
    var subtitleOrigin: IntOffset by mutableStateOf(IntOffset.Zero)
}

/** Keeps the original bar clip around transformed titles without clipping control shadows. */
private fun Modifier.clipTopAppBarTitle(
    clip: TopAppBarTitleClip,
    origin: () -> IntOffset,
): Modifier =
    drawWithContent {
        val position = origin()
        clipRect(
            left = -position.x.toFloat(),
            top = -position.y.toFloat(),
            right = clip.viewportSize.width - position.x.toFloat(),
            bottom = clip.viewportSize.height - position.y.toFloat(),
        ) {
            this@drawWithContent.drawContent()
        }
    }
