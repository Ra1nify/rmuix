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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastRoundToInt
import io.github.ra1nify.rmuix.kmp.basic.TopAppBarState.Companion.Saver
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlin.math.abs

/**
 * A [TopAppBar] with ColorOS 17 styled visuals.
 *
 * @param title The title of the [TopAppBar].
 * @param modifier The modifier to be applied to the [TopAppBar].
 * @param color The background color of the [TopAppBar].
 * @param titleColor The color of the title text.
 * @param largeTitle The large title of the [TopAppBar].
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
 */
@Composable
fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = RmuixTheme.colorScheme.surface,
    titleColor: Color = RmuixTheme.colorScheme.onSurface,
    largeTitle: String = title,
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

    TopAppBarLayout(
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
        scrollBehavior = scrollBehavior,
        modifier = modifier,
        defaultWindowInsetsPadding = defaultWindowInsetsPadding,
        bottomContent = bottomContent,
    )
}

/**
 * A [SmallTopAppBar] with ColorOS 17 styled visuals.
 */
@Composable
fun SmallTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = RmuixTheme.colorScheme.surface,
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

@Suppress("ComposableNaming")
@Composable
fun RmuixScrollBehavior(
    state: TopAppBarState = rememberTopAppBarState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = 2500f),
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay(),
): ScrollBehavior = remember(state, canScroll, snapAnimationSpec, flingAnimationSpec) {
    ExitUntilCollapsedScrollBehavior(
        state = state,
        snapAnimationSpec = snapAnimationSpec,
        flingAnimationSpec = flingAnimationSpec,
        canScroll = canScroll,
    )
}

@Composable
fun rememberTopAppBarState(
    initialHeightOffsetLimit: Float = -Float.MAX_VALUE,
    initialHeightOffset: Float = 0f,
    initialContentOffset: Float = 0f,
): TopAppBarState = rememberSaveable(saver = Saver) {
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
    val TitlePadding = 26.dp
    val NavigationIconPadding = 16.dp
    val ActionIconPadding = 16.dp
    val CollapsedHeight = 52.dp
    val SmallTopAppBarCenterHeight = 50.dp
    val LargeTitleBottomPadding = 4.dp
    val SubtitleBottomPadding = 8.dp
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
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
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

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
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
) {
    val animatedTitleColor by animateColorAsState(
        targetValue = titleColor,
        animationSpec = tween(durationMillis = 50),
    )
    val animatedSubtitleColor by animateColorAsState(
        targetValue = subtitleColor,
        animationSpec = tween(durationMillis = 50),
    )

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
                    fontSize = RmuixTheme.textStyles.title2.fontSize,
                    fontWeight = FontWeight.Bold,
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
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                    )
                }
            }
            Box(Modifier.layoutId("bottomContent")) {
                bottomContent()
            }
        },
        modifier = modifier
            .background(color)
            .then(
                if (defaultWindowInsetsPadding) {
                    Modifier
                        .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                } else {
                    Modifier
                },
            )
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
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
            // Navigation icon
            navigationIconPlaceable.placeRelative(
                x = 0,
                y = verticalCenter - navigationIconPlaceable.height / 2,
            )

            // Title - ColorOS 17 Left/Start Aligned
            val startX = navigationIconPlaceable.width
            titlePlaceable.placeRelative(
                x = startX,
                y = verticalCenter - titlePlaceable.height / 2,
            )

            // Action icons
            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = verticalCenter - actionIconsPlaceable.height / 2,
            )

            // Subtitle - aligned with Title
            subtitlePlaceable?.placeRelative(
                x = startX,
                y = subtitleY,
            )

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
                    fontSize = RmuixTheme.textStyles.title3.fontSize,
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
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                    )
                }
            }
            Box(Modifier.layoutId("bottomContent")) {
                bottomContent()
            }
        },
        modifier = modifier
            .background(color)
            .then(
                if (defaultWindowInsetsPadding) {
                    Modifier
                        .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                } else {
                    Modifier
                },
            )
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
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

private const val TITLE_WIDTH_FRACTION = 0.9
