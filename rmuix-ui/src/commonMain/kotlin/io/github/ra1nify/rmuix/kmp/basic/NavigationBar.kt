// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

@file:Suppress("ktlint:compose:param-order-check")

// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastRoundToInt
import io.github.ra1nify.rmuix.kmp.anim.couiSpring
import io.github.ra1nify.rmuix.kmp.anim.folmeSpring
import io.github.ra1nify.rmuix.kmp.shader.drawAGSLMetaBallBridge
import io.github.ra1nify.rmuix.kmp.shader.rememberMetaBallShader
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight
import io.github.ra1nify.rmuix.kmp.squircle.SquircleShape
import io.github.ra1nify.rmuix.kmp.squircle.isSquircleEnabled
import io.github.ra1nify.rmuix.kmp.squircle.squircleBackground
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.Platform
import io.github.ra1nify.rmuix.kmp.utils.platform
import io.github.ra1nify.rmuix.kmp.utils.stockPressTargetScale
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectColor
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectSpring
import kotlinx.coroutines.launch

/** Whether the enclosing [NavigationBar] uses the enlarged (`navigationType=enlarge`) variant. */
internal val LocalNavigationBarEnlarged = staticCompositionLocalOf { false }

/**
 * A [NavigationBar] that with 2 to 5 items styled according to ColorOS 17 metrics.
 *
 * @param enlarged draws the `navigationType=enlarge` variant: 70dp items, and the selected item grows
 *   its icon to 64dp and hides its label. Off by default, which keeps the 56dp normal bar.
 */
@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    color: Color = navigationBarBar(isNavigationBarInDarkTheme()),
    showDivider: Boolean = true,
    defaultWindowInsetsPadding: Boolean = true,
    mode: NavigationBarDisplayMode = NavigationBarDisplayMode.IconAndText,
    enlarged: Boolean = false,
    content: @Composable RowScope.() -> Unit,
) {
    val captionBarPaddings = WindowInsets.captionBar.only(WindowInsetsSides.Bottom).asPaddingValues()
    val captionBarBottomPaddingValue = captionBarPaddings.calculateBottomPadding()

    val animatedCaptionBarHeight by animateDpAsState(
        targetValue = if (captionBarBottomPaddingValue > 0.dp) captionBarBottomPaddingValue else 0.dp,
        animationSpec = tween(durationMillis = 300),
    )

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color),
    ) {
        if (showDivider) {
            HorizontalDivider(thickness = NavigationBarDefaults.DividerHeight)
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    // The items are as tall as the bar: both live dimens
                    // (`coui_navigation_normal_item_height`, `coui_tool_navigation_item_height`) are 56dp.
                    .padding(
                        horizontal = NavigationBarDefaults.EdgePadding,
                        vertical =
                            if (enlarged) {
                                0.dp
                            } else {
                                (NavigationBarDefaults.BarHeight - NavigationBarDefaults.ItemHeight) / 2
                            },
                    ).selectableGroup(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(
                LocalNavigationBarDisplayMode provides mode,
                LocalNavigationBarEnlarged provides enlarged,
            ) {
                content()
            }
        }
        if (defaultWindowInsetsPadding) {
            val navigationBarsPadding =
                if (platform() != Platform.IOS) {
                    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                } else {
                    NavigationBarDefaults.IosBottomPadding
                }
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .layout { measurable, constraints ->
                            val totalHeight = (navigationBarsPadding + animatedCaptionBarHeight).roundToPx()
                            val fixedConstraints = constraints.copy(minHeight = totalHeight, maxHeight = totalHeight)
                            val placeable = measurable.measure(fixedConstraints)
                            layout(placeable.width, totalHeight) {
                                placeable.placeRelative(0, 0)
                            }
                        }.pointerInput(Unit) { detectTapGestures { /* Do nothing to consume the click */ } },
            )
        }
    }
}

/**
 * A [NavigationBarItem] that is suitable for [NavigationBar].
 *
 * @param selectedIcon The icon drawn while the item is selected. The stock item view clears its icon
 *   tint and swaps the artwork instead, so a filled/outline pair is how ColorOS shows selection;
 *   `null` (the default) keeps drawing [icon] in both states.
 */
@Composable
fun RowScope.NavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: NavigationBarItemColors = NavigationBarDefaults.navigationBarItemColors(),
    badge: (@Composable () -> Unit)? = null,
    selectedIcon: ImageVector? = null,
) {
    val enlarged = LocalNavigationBarEnlarged.current
    val itemHeight = if (enlarged) NavigationBarDefaults.EnlargedItemHeight else NavigationBarDefaults.ItemHeight
    val iconSize = if (enlarged && selected) NavigationBarDefaults.EnlargedIconSize else NavigationBarDefaults.IconSize
    // The stock enlarged item hides its label once selected and drops the top inset.
    val showLabel = !(enlarged && selected)
    val iconTopPadding = if (enlarged) NavigationBarDefaults.EnlargedIconTopPadding else NavigationBarDefaults.IconTopPadding
    val interactionSource = remember { MutableInteractionSource() }
    val dark = isNavigationBarInDarkTheme()
    val spring = remember { stockStateEffectSpring() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    val hoverProgress by animateFloatAsState(if (hovered) 1f else 0f, spring, label = "NavigationBarItemHover")
    val focusProgress by animateFloatAsState(if (focused) 1f else 0f, spring, label = "NavigationBarItemFocus")
    val pressProgress by animateFloatAsState(if (pressed) 1f else 0f, spring, label = "NavigationBarItemPress")
    val hoverColor = stockStateEffectColor(navigationBarItemHover(dark), hoverProgress)
    val focusColor = stockStateEffectColor(navigationBarItemFocus(RmuixTheme.colorScheme.primary, dark), focusProgress)
    val pressColor = stockStateEffectColor(navigationBarItemPress(dark), pressProgress)

    // The stock item paints a full-size state mask (`COUIMaskEffectDrawable`, 8dp radius) for
    // hover, focus and press; it does not dim the content on press.
    val tint = colors.contentColor(selected)
    // `coui_navigation_item_layout` sets `sans-serif-medium` on both label views; selection is
    // not expressed as a weight change.
    val fontWeight = FontWeight.Medium
    val mode = LocalNavigationBarDisplayMode.current
    // The stock item view clears its icon tint (`setIconTintList(null)`) and relies on the
    // artwork switching between states; a second vector is the Compose equivalent.
    val displayedIcon = if (selected) selectedIcon ?: icon else icon

    Column(
        modifier =
            modifier
                .height(itemHeight)
                .weight(1f)
                .padding(horizontal = NavigationBarDefaults.ItemGap)
                .drawBehind {
                    val radius = CornerRadius(NavigationBarDefaults.ItemCornerRadius.toPx())
                    if (hoverColor != Color.Transparent) drawRoundRect(hoverColor, cornerRadius = radius)
                    if (focusColor != Color.Transparent) drawRoundRect(focusColor, cornerRadius = radius)
                    if (pressColor != Color.Transparent) drawRoundRect(pressColor, cornerRadius = radius)
                }.selectable(
                    selected = selected,
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Tab,
                    interactionSource = interactionSource,
                    indication = null,
                ),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement =
            if (mode == NavigationBarDisplayMode.IconAndText ||
                mode == NavigationBarDisplayMode.IconWithSelectedLabel
            ) {
                Arrangement.Top
            } else {
                Arrangement.Center
            },
    ) {
        when (mode) {
            NavigationBarDisplayMode.IconAndText -> {
                NavigationItemIcon(
                    badge = badge,
                    modifier = Modifier.padding(top = iconTopPadding),
                ) { iconModifier ->
                    Image(
                        modifier = iconModifier.size(iconSize),
                        imageVector = displayedIcon,
                        contentDescription = null,
                    )
                }
                if (showLabel) {
                    Spacer(modifier = Modifier.height(NavigationBarDefaults.IconTextGap))
                    Text(
                        modifier = Modifier.padding(bottom = NavigationBarDefaults.BottomPadding),
                        text = label,
                        color = tint,
                        textAlign = TextAlign.Center,
                        fontSize = NavigationBarDefaults.LabelFontSize,
                        fontWeight = fontWeight,
                    )
                }
            }

            NavigationBarDisplayMode.IconWithSelectedLabel -> {
                val defaultPadding = (itemHeight - iconSize) / 2
                val iconTopPadding by animateDpAsState(
                    targetValue = if (selected) NavigationBarDefaults.IconTopPadding else defaultPadding,
                    animationSpec = tween(durationMillis = 300),
                    label = "iconTopPadding",
                )
                val textAlpha by animateFloatAsState(
                    targetValue = if (selected && showLabel) 1f else 0f,
                    animationSpec = tween(durationMillis = 300),
                    label = "textAlpha",
                )

                NavigationItemIcon(
                    badge = badge,
                    modifier =
                        Modifier.layout { measurable, constraints ->
                            val topPaddingPx = iconTopPadding.roundToPx()
                            val placeable = measurable.measure(constraints.offset(vertical = -topPaddingPx))
                            layout(placeable.width, placeable.height + topPaddingPx) {
                                placeable.placeRelative(0, topPaddingPx)
                            }
                        },
                ) { iconModifier ->
                    Image(
                        modifier = iconModifier.size(NavigationBarDefaults.IconSize),
                        imageVector = displayedIcon,
                        contentDescription = null,
                    )
                }
                Spacer(modifier = Modifier.height(NavigationBarDefaults.IconTextGap))
                Text(
                    modifier =
                        Modifier
                            .padding(bottom = NavigationBarDefaults.BottomPadding)
                            .graphicsLayer { alpha = textAlpha },
                    text = label,
                    color = tint,
                    textAlign = TextAlign.Center,
                    fontSize = NavigationBarDefaults.LabelFontSize,
                    fontWeight = fontWeight,
                )
            }

            else -> {
                NavigationItemIcon(badge = badge, modifier = Modifier) { iconModifier ->
                    Image(
                        modifier = iconModifier.size(NavigationBarDefaults.IconSize),
                        imageVector = displayedIcon,
                        contentDescription = label,
                    )
                }
            }
        }
    }
}

/**
 * A ColorOS 17 floating navigation bar supporting 2 to 5 items and 4 three-island modes (Single, Left, Right, Both).
 *
 * The capsule's background can be replaced through [surface], which receives the resolved corner radius; the
 * `rmuix-glass` module uses it to draw the ColorOS bottom-bar material over a sampled backdrop. `null` keeps
 * the solid [color] background.
 */
@Suppress("ktlint:compose:modifier-not-used-at-root")
@Composable
fun FloatingNavigationBar(
    modifier: Modifier = Modifier,
    mode: NavigationBarDisplayMode = NavigationBarDisplayMode.IconAndText,
    meltEffectMode: Int = 0,
    startAccessory: (@Composable () -> Unit)? = null,
    endAccessory: (@Composable () -> Unit)? = null,
    startAccessoryVisible: Boolean = true,
    endAccessoryVisible: Boolean = true,
    color: Color = RmuixTheme.colorScheme.surfaceContainer,
    indicatorColor: Color = if (isNavigationBarInDarkTheme()) Color(0xFF2C2C2E) else Color.White,
    cornerRadius: Dp = FloatingNavigationBarDefaults.CornerRadius,
    horizontalAlignment: Alignment.Horizontal = CenterHorizontally,
    horizontalOutSidePadding: Dp = FloatingNavigationBarDefaults.HorizontalOutSidePadding,
    shadowElevation: Dp = FloatingNavigationBarDefaults.ShadowElevation,
    showDivider: Boolean = false,
    defaultWindowInsetsPadding: Boolean = true,
    surface: (@Composable (cornerRadius: Dp) -> Modifier)? = null,
    content: @Composable () -> Unit,
) {
    val navBarBottomPadding =
        WindowInsets.navigationBars
            .only(WindowInsetsSides.Bottom)
            .asPaddingValues()
            .calculateBottomPadding()
    val bottomPaddingValue =
        when (platform()) {
            Platform.IOS -> {
                36.dp
            }

            else -> {
                val naviDistance = FloatingNavigationBarDefaults.NaviDistance
                if (navBarBottomPadding != 0.dp) naviDistance + navBarBottomPadding else FloatingNavigationBarDefaults.GestureNaviDistance
            }
        }

    val captionBarBottomPaddingValue =
        WindowInsets.captionBar
            .only(WindowInsetsSides.Bottom)
            .asPaddingValues()
            .calculateBottomPadding()
    val animatedCaptionBarHeight by animateDpAsState(
        targetValue = if (captionBarBottomPaddingValue > 0.dp) captionBarBottomPaddingValue else 0.dp,
        animationSpec = tween(durationMillis = 300),
    )

    val itemSpacing = FloatingNavigationBarDefaults.ItemSpacing
    val minBarHeight = FloatingNavigationBarDefaults.BarHeight

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = if (horizontalAlignment == Alignment.Start) horizontalOutSidePadding else 0.dp,
                    end = if (horizontalAlignment == Alignment.End) horizontalOutSidePadding else 0.dp,
                ),
    ) {
        Box(
            modifier =
                Modifier
                    .padding(bottom = bottomPaddingValue)
                    .then(
                        if (defaultWindowInsetsPadding) {
                            Modifier.padding(bottom = animatedCaptionBarHeight)
                        } else {
                            Modifier
                        },
                    ).align(horizontalAlignment),
        ) {
            CompositionLocalProvider(LocalNavigationBarDisplayMode provides mode) {
                ColorOSFloatingThreeIslandLayout(
                    startAccessory = startAccessory,
                    endAccessory = endAccessory,
                    startAccessoryVisible = startAccessoryVisible,
                    endAccessoryVisible = endAccessoryVisible,
                    sideSpacing = FloatingNavigationBarDefaults.SideSpacing,
                    meltEffectMode = meltEffectMode,
                    color = color,
                ) {
                    SinglePillBar(
                        modifier = modifier,
                        color = color,
                        surface = surface,
                        cornerRadius = cornerRadius,
                        shadowElevation = shadowElevation,
                        showDivider = showDivider,
                        minBarHeight = minBarHeight,
                        itemSpacing = itemSpacing,
                        indicatorColor = indicatorColor,
                        content = content,
                    )
                }
            }
        }
    }
}

@Composable
private fun SinglePillBar(
    modifier: Modifier = Modifier,
    color: Color,
    surface: (@Composable (cornerRadius: Dp) -> Modifier)?,
    cornerRadius: Dp,
    shadowElevation: Dp,
    showDivider: Boolean,
    minBarHeight: Dp,
    itemSpacing: Dp,
    indicatorColor: Color,
    content: @Composable () -> Unit,
) {
    // `COUIBottomFloatingNavigationBar.changePadding(width)` only keeps the 16dp horizontal padding on
    // compact widths - `isSmallScreen(...) ? mPaddingHorizontal : 0` - so Medium/Expanded windows hug the
    // edges with no inner padding at all. Mirror that with the same 600dp breakpoint used elsewhere.
    val windowWidthDp =
        with(LocalDensity.current) {
            LocalWindowInfo.current.containerSize
                .width
                .toDp()
        }
    val innerHorizontalPadding =
        if (windowWidthDp < FloatingNavigationBarDefaults.CompactWidthMax) {
            FloatingNavigationBarDefaults.HorizontalPadding
        } else {
            0.dp
        }

    ColorOSFloatingNavigationBarContainer(
        modifier = modifier,
        color = color,
        surface = surface,
        cornerRadius = cornerRadius,
        shadowElevation = shadowElevation,
        showDivider = showDivider,
        minBarHeight = minBarHeight,
        innerHorizontalPadding = innerHorizontalPadding,
        itemSpacing = itemSpacing,
        indicatorColor = indicatorColor,
        content = content,
    )
}

/**
 * A custom three-island [Layout] for [FloatingNavigationBar] that positions start accessory, center menu pill, and end accessory with spring collapse/expand animations and Plan C Bezier liquid bridge.
 */
@Composable
private fun ColorOSFloatingThreeIslandLayout(
    modifier: Modifier = Modifier,
    startAccessory: (@Composable () -> Unit)?,
    endAccessory: (@Composable () -> Unit)?,
    startAccessoryVisible: Boolean = true,
    endAccessoryVisible: Boolean = true,
    sideSpacing: Dp = FloatingNavigationBarDefaults.SideSpacing,
    meltEffectMode: Int = 0,
    color: Color = RmuixTheme.colorScheme.surfaceContainer,
    content: @Composable () -> Unit,
) {
    // Scale and translation share the stock `initScaleAnim()` / `initTranslationAnim()` spring
    // (bounce 0.35 -> damping 0.65, response 0.55); alpha has its own, faster spring (bounce 0,
    // response 0.35) so the button fades in before it finishes growing.
    val progressSpec =
        folmeSpring<Float>(
            damping = FloatingNavigationBarDefaults.AccessoryScaleDamping,
            response = FloatingNavigationBarDefaults.AccessoryScaleResponse,
        )
    val alphaSpec =
        folmeSpring<Float>(
            damping = 1f,
            response = FloatingNavigationBarDefaults.AccessoryAlphaResponse,
        )
    val startProgress by animateFloatAsState(
        targetValue = if (startAccessory != null && startAccessoryVisible) 1f else 0f,
        animationSpec = progressSpec,
        label = "StartAccessoryProgress",
    )
    val endProgress by animateFloatAsState(
        targetValue = if (endAccessory != null && endAccessoryVisible) 1f else 0f,
        animationSpec = progressSpec,
        label = "EndAccessoryProgress",
    )
    val startAlpha by animateFloatAsState(
        targetValue = if (startAccessory != null && startAccessoryVisible) 1f else 0f,
        animationSpec = alphaSpec,
        label = "StartAccessoryAlpha",
    )
    val endAlpha by animateFloatAsState(
        targetValue = if (endAccessory != null && endAccessoryVisible) 1f else 0f,
        animationSpec = alphaSpec,
        label = "EndAccessoryAlpha",
    )
    val minScale = FloatingNavigationBarDefaults.AccessoryMinScale

    val density = LocalDensity.current
    val spacingPx = with(density) { sideSpacing.roundToPx() }
    // The satellite accessory is a pill the height of the bar, so its corner radius is BarHeight / 2 —
    // i.e. FloatingNavigationBarDefaults.CornerRadius (28dp, coui_bottom_floating_navigation_bar_height
    // 56dp). The bridge geometry below used to repeat the raw 28dp literal twenty times.
    val bridgeRadiusPx = with(density) { FloatingNavigationBarDefaults.CornerRadius.toPx() }
    val liquidPath = remember { Path() }
    val metaBallShader = rememberMetaBallShader()

    Layout(
        modifier =
            modifier.drawBehind {
                if (startAccessory != null && startProgress > 0.01f && startProgress < 0.99f) {
                    if (meltEffectMode == 1 && metaBallShader != null) {
                        drawAGSLMetaBallBridge(
                            shader = metaBallShader,
                            c1x = bridgeRadiusPx * startProgress,
                            c1y = size.height / 2f,
                            r1 = bridgeRadiusPx * startProgress,
                            c2x = bridgeRadiusPx * startProgress + (spacingPx * startProgress) + bridgeRadiusPx,
                            c2y = size.height / 2f,
                            halfWidth2 = bridgeRadiusPx,
                            halfHeight2 = bridgeRadiusPx,
                            r2 = bridgeRadiusPx,
                            progress = startProgress,
                            color = color,
                        )
                    } else {
                        drawBezierLiquidBridge(
                            path = liquidPath,
                            x1 = bridgeRadiusPx * startProgress,
                            y1 = size.height / 2f,
                            r1 = bridgeRadiusPx * startProgress,
                            x2 = bridgeRadiusPx * startProgress + (spacingPx * startProgress) + bridgeRadiusPx,
                            y2 = size.height / 2f,
                            r2 = bridgeRadiusPx,
                            progress = startProgress,
                            color = color,
                        )
                    }
                }
                if (endAccessory != null && endProgress > 0.01f && endProgress < 0.99f) {
                    if (meltEffectMode == 1 && metaBallShader != null) {
                        drawAGSLMetaBallBridge(
                            shader = metaBallShader,
                            c1x = size.width - (bridgeRadiusPx * endProgress),
                            c1y = size.height / 2f,
                            r1 = bridgeRadiusPx * endProgress,
                            c2x = size.width - (bridgeRadiusPx * endProgress + (spacingPx * endProgress) + bridgeRadiusPx),
                            c2y = size.height / 2f,
                            halfWidth2 = bridgeRadiusPx,
                            halfHeight2 = bridgeRadiusPx,
                            r2 = bridgeRadiusPx,
                            progress = endProgress,
                            color = color,
                        )
                    } else {
                        drawBezierLiquidBridge(
                            path = liquidPath,
                            x1 = size.width - (bridgeRadiusPx * endProgress),
                            y1 = size.height / 2f,
                            r1 = bridgeRadiusPx * endProgress,
                            x2 = size.width - (bridgeRadiusPx * endProgress + (spacingPx * endProgress) + bridgeRadiusPx),
                            y2 = size.height / 2f,
                            r2 = bridgeRadiusPx,
                            progress = endProgress,
                            color = color,
                        )
                    }
                }
            },
        content = {
            if (startAccessory != null) {
                Box(
                    modifier =
                        Modifier
                            .layoutId("start_accessory")
                            .graphicsLayer {
                                scaleX = minScale + (1f - minScale) * startProgress
                                scaleY = minScale + (1f - minScale) * startProgress
                                alpha = startAlpha
                            },
                ) {
                    startAccessory()
                }
            }
            Box(modifier = Modifier.layoutId("center_menu")) {
                content()
            }
            if (endAccessory != null) {
                Box(
                    modifier =
                        Modifier
                            .layoutId("end_accessory")
                            .graphicsLayer {
                                scaleX = minScale + (1f - minScale) * endProgress
                                scaleY = minScale + (1f - minScale) * endProgress
                                alpha = endAlpha
                            },
                ) {
                    endAccessory()
                }
            }
        },
    ) { measurables, constraints ->
        val startMeasurable = measurables.firstOrNull { it.layoutId == "start_accessory" }
        val centerMeasurable = measurables.fastFirst { it.layoutId == "center_menu" }
        val endMeasurable = measurables.firstOrNull { it.layoutId == "end_accessory" }

        val startPlaceable = startMeasurable?.measure(constraints.copy(minWidth = 0, minHeight = 0))
        val endPlaceable = endMeasurable?.measure(constraints.copy(minWidth = 0, minHeight = 0))

        val fullStartWidth = startPlaceable?.width ?: 0
        val fullEndWidth = endPlaceable?.width ?: 0

        val currentStartWidth = (fullStartWidth * startProgress).fastRoundToInt()
        val currentEndWidth = (fullEndWidth * endProgress).fastRoundToInt()

        val startSpacing = (spacingPx * startProgress).fastRoundToInt()
        val endSpacing = (spacingPx * endProgress).fastRoundToInt()

        val availableCenterWidth = (constraints.maxWidth - currentStartWidth - currentEndWidth - startSpacing - endSpacing).coerceAtLeast(0)
        val centerPlaceable = centerMeasurable.measure(constraints.copy(minWidth = 0, maxWidth = availableCenterWidth, minHeight = 0))

        val totalWidth = currentStartWidth + startSpacing + centerPlaceable.width + endSpacing + currentEndWidth
        val maxHeight = maxOf(startPlaceable?.height ?: 0, centerPlaceable.height, endPlaceable?.height ?: 0)

        layout(totalWidth, maxHeight) {
            var currentX = 0
            if (startPlaceable != null && startProgress > 0.001f) {
                val startXOffset = currentX + (currentStartWidth - startPlaceable.width) / 2
                startPlaceable.placeRelative(startXOffset, (maxHeight - startPlaceable.height) / 2)
                currentX += currentStartWidth + startSpacing
            }
            centerPlaceable.placeRelative(currentX, (maxHeight - centerPlaceable.height) / 2)
            currentX += centerPlaceable.width + endSpacing

            if (endPlaceable != null && endProgress > 0.001f) {
                val endXOffset = currentX + (currentEndWidth - endPlaceable.width) / 2
                endPlaceable.placeRelative(endXOffset, (maxHeight - endPlaceable.height) / 2)
            }
        }
    }
}

/**
 * Plan C Bezier tangent liquid bridge renderer.
 */
private fun DrawScope.drawBezierLiquidBridge(
    path: Path,
    x1: Float,
    y1: Float,
    r1: Float,
    x2: Float,
    y2: Float,
    r2: Float,
    progress: Float,
    color: Color,
) {
    val dx = x2 - x1
    val dy = y2 - y1
    val dist = kotlin.math.hypot(dx, dy)
    if (dist <= 0.001f) return

    val angle = kotlin.math.atan2(dy, dx)
    val spreadAngle = (1.05f * progress).coerceIn(0.2f, 1.3f)

    val p1x = x1 + r1 * kotlin.math.cos(angle + spreadAngle)
    val p1y = y1 + r1 * kotlin.math.sin(angle + spreadAngle)
    val p4x = x1 + r1 * kotlin.math.cos(angle - spreadAngle)
    val p4y = y1 + r1 * kotlin.math.sin(angle - spreadAngle)

    val p2x = x2 + r2 * kotlin.math.cos(angle + kotlin.math.PI.toFloat() - spreadAngle)
    val p2y = y2 + r2 * kotlin.math.sin(angle + kotlin.math.PI.toFloat() - spreadAngle)
    val p3x = x2 + r2 * kotlin.math.cos(angle + kotlin.math.PI.toFloat() + spreadAngle)
    val p3y = y2 + r2 * kotlin.math.sin(angle + kotlin.math.PI.toFloat() + spreadAngle)

    val midX = (x1 + x2) / 2f
    val midY = (y1 + y2) / 2f
    val neckRadius = (r1 * (1f - progress * 0.4f)).coerceAtLeast(4f)

    val c1x = midX + neckRadius * kotlin.math.cos(angle + kotlin.math.PI.toFloat() / 2f)
    val c1y = midY + neckRadius * kotlin.math.sin(angle + kotlin.math.PI.toFloat() / 2f)

    val c2x = midX + neckRadius * kotlin.math.cos(angle - kotlin.math.PI.toFloat() / 2f)
    val c2y = midY + neckRadius * kotlin.math.sin(angle - kotlin.math.PI.toFloat() / 2f)

    path.reset()
    path.moveTo(p1x, p1y)
    path.quadraticTo(c1x, c1y, p2x, p2y)
    path.lineTo(p3x, p3y)
    path.quadraticTo(c2x, c2y, p4x, p4y)
    path.close()

    drawPath(path = path, color = color)
}

/**
 * A ColorOS 17 56dp satellite accessory button used in [FloatingNavigationBar].
 */
@Composable
fun FloatingAccessoryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    label: String? = null,
    enabled: Boolean = true,
    color: Color = RmuixTheme.colorScheme.surfaceContainer,
    shadowElevation: Dp = FloatingNavigationBarDefaults.ShadowElevation,
    badge: (@Composable () -> Unit)? = null,
    badgeOffset: DpSize = DpSize.Zero,
    showSpotLight: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // The stock accessory (`COUIBottomFloatingAccessoryView`) has no badge-offset code at all, so this
    // defaults to no displacement; pass `NavigationBarDefaults.BadgeDotOffset` / `BadgeNumberOffset` to
    // line a badge up with the navigation items.
    //
    // Press feedback: `COUIBottomFloatingAccessoryView` paints a radial gradient at the finger. Its centre
    // is `rgb | TOUCH_EFFECT_CENTER_ALPHA` (`0x26`), its edge is the same rgb at full alpha, its radius is
    // `coui_bottom_floating_navigation_bar_touch_effect_radius` (40dp) and the paint alpha springs from 0
    // to `TOUCH_EFFECT_ALPHA_OPAQUE` with `TOUCH_EFFECT_ALPHA_SPRING_RESPONSE` = 0.2 and bounce 0.
    // The scale is `COUITouchMotionAnimator.setPressScaleEnable(true)`'s PressCalc target for the button size
    // with the `PressParamsExtKt` jelly springs.
    var buttonSize by remember { mutableStateOf(IntSize.Zero) }
    val accessoryDensity = LocalDensity.current
    val scaleTarget =
        if (isPressed && buttonSize.width > 0 && buttonSize.height > 0) {
            with(accessoryDensity) {
                stockPressTargetScale(buttonSize.width.toDp().value, buttonSize.height.toDp().value)
            }
        } else {
            1f
        }
    val scale by animateFloatAsState(
        targetValue = scaleTarget,
        animationSpec = if (isPressed) couiSpring(response = 0.25f, bounce = 0f) else couiSpring(response = 0.5f, bounce = 0.6f),
        label = "AccessoryButtonScale",
    )
    val touchGlowAlpha = remember { Animatable(0f) }
    val touchGlowScope = rememberCoroutineScope()
    val touchGlowSpring = remember { folmeSpring<Float>(damping = 1f, response = 0.2f) }
    var touchPosition by remember { mutableStateOf<Offset?>(null) }
    val touchGlowRadius = with(LocalDensity.current) { FloatingNavigationBarDefaults.TouchEffectRadius.toPx() }

    // Touch deformation: while the finger drags, the button stretches along the drag direction and
    // thins on the cross axis so its area stays roughly constant (`COUIDeformAnimatorUnit`). The
    // displacement is measured from the press-down point and released with the hand-up spring.
    val deformMaxPx = with(LocalDensity.current) { FloatingNavigationBarDefaults.DeformMaxRubberDistance.toPx() }
    var deformDrag by remember { mutableStateOf(Offset.Zero) }
    var isDeforming by remember { mutableStateOf(false) }
    val deformScale =
        remember(isDeforming, deformDrag) {
            if (isDeforming) {
                touchDeformScale(
                    dxPx = deformDrag.x,
                    dyPx = deformDrag.y,
                    curveRatio = FloatingNavigationBarDefaults.DeformRubberCurveRatio,
                    maxDistancePx = deformMaxPx,
                    maxScale = FloatingNavigationBarDefaults.DeformMaxScale,
                )
            } else {
                TouchDeformScale.None
            }
        }
    val handUpSpec =
        remember {
            folmeSpring<Float>(
                damping = FloatingNavigationBarDefaults.HandUpSpringDamping,
                response = FloatingNavigationBarDefaults.HandUpSpringResponse,
            )
        }
    val deformScaleX by animateFloatAsState(deformScale.scaleX, handUpSpec, label = "AccessoryDeformX")
    val deformScaleY by animateFloatAsState(deformScale.scaleY, handUpSpec, label = "AccessoryDeformY")

    // Shadow, spotlight and fill must share one silhouette; `squircleBackground` degrades to a rounded
    // rect when the shader path is unavailable, so the shape follows the same condition.
    val squircleEnabled = isSquircleEnabled()
    val shape =
        SquircleShape(
            FloatingAccessoryButtonDefaults.CornerRadius,
            squircleEnabled = squircleEnabled,
        )
    val contentColor =
        if (enabled) {
            RmuixTheme.colorScheme.onSurface
        } else {
            RmuixTheme.colorScheme.onSurfaceContainer.copy(alpha = 0.4f)
        }

    Box(
        modifier =
            modifier
                .size(FloatingAccessoryButtonDefaults.Size)
                .onSizeChanged { buttonSize = it }
                .graphicsLayer {
                    scaleX = scale * deformScaleX
                    scaleY = scale * deformScaleY
                }.then(
                    if (shadowElevation > 0.dp) {
                        Modifier.dropShadow(
                            shape = shape,
                            shadow =
                                Shadow(
                                    radius = FloatingNavigationBarDefaults.ShadowRadius,
                                    color = Color.Black,
                                    alpha = 0.2f,
                                ),
                        )
                    } else {
                        Modifier
                    },
                ).rmuixSpotlight(
                    shape = shape,
                    darkTheme = RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance(),
                    enabled = enabled,
                ).squircleBackground(color = color, cornerRadius = FloatingAccessoryButtonDefaults.CornerRadius)
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val start = down.position
                        deformDrag = Offset.Zero
                        isDeforming = true
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull() ?: break
                            deformDrag = change.position - start
                            if (!change.pressed) break
                        }
                        isDeforming = false
                        deformDrag = Offset.Zero
                    }
                }.drawWithContent {
                    drawContent()
                    val glowCenter = touchPosition
                    if (showSpotLight && glowCenter != null) {
                        val alpha = touchGlowAlpha.value
                        if (alpha > 0.001f) {
                            drawCircle(
                                brush =
                                    Brush.radialGradient(
                                        colors = listOf(AccessoryTouchEffectCenter, AccessoryTouchEffectEdge),
                                        center = glowCenter,
                                        radius = touchGlowRadius,
                                    ),
                                radius = touchGlowRadius,
                                center = glowCenter,
                                alpha = alpha,
                            )
                        }
                    }
                }.pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        touchPosition = down.position
                        touchGlowScope.launch { touchGlowAlpha.animateTo(1f, touchGlowSpring) }
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull() ?: break
                            touchPosition = change.position
                            if (!change.pressed) break
                        }
                        touchGlowScope.launch { touchGlowAlpha.animateTo(0f, touchGlowSpring) }
                    }
                }.selectable(
                    selected = false,
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Button,
                    interactionSource = interactionSource,
                    indication = null,
                ),
        contentAlignment = Alignment.Center,
    ) {
        NavigationItemIcon(badge = badge) { iconModifier ->
            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (icon != null) {
                    Image(
                        modifier = iconModifier.size(FloatingAccessoryButtonDefaults.IconSize),
                        imageVector = icon,
                        contentDescription = label,
                    )
                }
                if (label != null) {
                    Text(
                        text = label,
                        color = contentColor,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

/** `TOUCH_EFFECT_CENTER_ALPHA` (0x26) applied to white: the centre of the press glow. */
private val AccessoryTouchEffectCenter = Color(0x26FFFFFF)

/** The outer stop of the press glow - `resolveTouchEffectLightColor()` returns white (`-1`). */
private val AccessoryTouchEffectEdge = Color.White

object FloatingAccessoryButtonDefaults {
    /** ColorOS 17 accessory button size (56dp). */
    val Size = 56.dp

    /** ColorOS 17 accessory button corner radius (28dp capsule). */
    val CornerRadius = 28.dp

    /** ColorOS 17 accessory button icon size (24dp). */
    val IconSize = 24.dp
}

@Stable
internal class FloatingItemRegistry {
    val items = mutableListOf<FloatingItemInfo>()

    fun register(
        selected: Boolean,
        index: Int,
        onClick: () -> Unit,
    ) {
        while (items.size <= index) {
            items.add(FloatingItemInfo(false) {})
        }
        items[index] = FloatingItemInfo(selected, onClick)
    }

    fun clear() {
        items.clear()
    }
}

internal data class FloatingItemInfo(
    val selected: Boolean,
    val onClick: () -> Unit,
)

internal class FloatingItemScope(
    val registry: FloatingItemRegistry,
) {
    var currentIndex = 0
}

internal val LocalFloatingItemScope = compositionLocalOf<FloatingItemScope?> { null }
internal val LocalFloatingItemHoverIndex = compositionLocalOf { -1 }

/**
 * A custom [Layout] container for ColorOS 17 [FloatingNavigationBar] that measures items, renders a sliding spring indicator, and handles 4D liquid drag & deformation.
 */
@Composable
private fun ColorOSFloatingNavigationBarContainer(
    modifier: Modifier = Modifier,
    color: Color,
    surface: (@Composable (cornerRadius: Dp) -> Modifier)?,
    cornerRadius: Dp,
    shadowElevation: Dp,
    showDivider: Boolean,
    minBarHeight: Dp,
    innerHorizontalPadding: Dp,
    itemSpacing: Dp,
    indicatorColor: Color,
    content: @Composable () -> Unit,
) {
    val registry = remember { FloatingItemRegistry() }
    val scope = remember { FloatingItemScope(registry) }
    scope.currentIndex = 0

    var draggedHoverIndex by remember { mutableIntStateOf(-1) }
    var itemXPositions by remember { mutableStateOf(IntArray(0)) }
    var itemWidths by remember { mutableStateOf(IntArray(0)) }

    var targetX by remember { mutableFloatStateOf(0f) }
    var targetWidth by remember { mutableFloatStateOf(0f) }
    var targetHeight by remember { mutableFloatStateOf(0f) }
    var isSelectedFound by remember { mutableStateOf(false) }

    // Liquid Touch Motion States (Translation & Area-Conserving Scale Deformation)
    var targetRubberX by remember { mutableFloatStateOf(0f) }
    var targetRubberY by remember { mutableFloatStateOf(0f) }
    var targetDeformScaleX by remember { mutableFloatStateOf(1f) }
    var targetDeformScaleY by remember { mutableFloatStateOf(1f) }
    var isBarDragging by remember { mutableStateOf(false) }

    // COUITouchMotionAnimator uses `iolDragSpring` while the finger is down and `iolHandUpSpring`
    // after release, so the settle spec switches at the gesture boundary.
    val touchMotionSpec =
        if (isBarDragging) {
            folmeSpring<Float>(
                damping = FloatingNavigationBarDefaults.DragSpringDamping,
                response = FloatingNavigationBarDefaults.DragSpringResponse,
            )
        } else {
            folmeSpring<Float>(
                damping = FloatingNavigationBarDefaults.HandUpSpringDamping,
                response = FloatingNavigationBarDefaults.HandUpSpringResponse,
            )
        }
    val animatedRubberX by animateFloatAsState(
        targetValue = targetRubberX,
        animationSpec = touchMotionSpec,
        label = "BarRubberX",
    )
    val animatedRubberY by animateFloatAsState(
        targetValue = targetRubberY,
        animationSpec = touchMotionSpec,
        label = "BarRubberY",
    )
    val animatedScaleX by animateFloatAsState(
        targetValue = targetDeformScaleX,
        animationSpec = touchMotionSpec,
        label = "BarScaleX",
    )
    val animatedScaleY by animateFloatAsState(
        targetValue = targetDeformScaleY,
        animationSpec = touchMotionSpec,
        label = "BarScaleY",
    )

    val animatedX by animateFloatAsState(
        targetValue = targetX,
        animationSpec = folmeSpring(damping = 0.78f, response = 0.45f),
        label = "IndicatorX",
    )
    val animatedWidth by animateFloatAsState(
        targetValue = targetWidth,
        animationSpec = folmeSpring(damping = 0.78f, response = 0.45f),
        label = "IndicatorWidth",
    )

    val isDark = isNavigationBarInDarkTheme()
    val squircleEnabled = isSquircleEnabled()
    val shape = SquircleShape(cornerRadius, squircleEnabled = squircleEnabled)
    val density = LocalDensity.current
    val spacingPx = with(density) { itemSpacing.roundToPx() }
    // COUIRubberAnimatorUnit: TouchMotionRubberBand.apply(offset, rubberCurveRatio 0.05, maxStretchDistance 28dp)
    // COUIDeformAnimatorUnit: deform rubber curve 0.55, max 150dp, max scale 1.2
    val stretchMaxPx = with(density) { FloatingNavigationBarDefaults.StretchMaxDistance.toPx() }
    val deformMaxPx = with(density) { FloatingNavigationBarDefaults.DeformMaxRubberDistance.toPx() }
    val horizontalPaddingPx = with(density) { innerHorizontalPadding.toPx() }
    val itemBgPadPx = with(density) { FloatingNavigationBarDefaults.ItemBackgroundHorizontalPadding.roundToPx() }

    CompositionLocalProvider(
        LocalFloatingItemScope provides scope,
        LocalFloatingItemHoverIndex provides draggedHoverIndex,
    ) {
        Box(
            modifier =
                Modifier
                    .graphicsLayer {
                        translationX = animatedRubberX
                        translationY = animatedRubberY
                        scaleX = animatedScaleX
                        scaleY = animatedScaleY
                    }.then(modifier)
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val downX = down.position.x
                            val downY = down.position.y

                            isBarDragging = true
                            val downIndex = calculateHoveredIndex(downX, itemXPositions, itemWidths, horizontalPaddingPx)
                            if (downIndex != -1) {
                                draggedHoverIndex = downIndex
                            }

                            do {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull()
                                if (change != null && change.pressed) {
                                    val dx = change.position.x - downX
                                    val dy = change.position.y - downY

                                    // COUIRubberAnimatorUnit (translation) + COUIDeformAnimatorUnit (scale),
                                    // both shared with the satellite accessory buttons via TouchDeformation.kt.
                                    targetRubberX =
                                        rubberBand(
                                            offset = dx,
                                            curveRatio = FloatingNavigationBarDefaults.StretchCurveRatio,
                                            maxDistancePx = stretchMaxPx,
                                        )
                                    targetRubberY =
                                        rubberBand(
                                            offset = dy,
                                            curveRatio = FloatingNavigationBarDefaults.StretchCurveRatio,
                                            maxDistancePx = stretchMaxPx,
                                        )
                                    val deform =
                                        touchDeformScale(
                                            dxPx = dx,
                                            dyPx = dy,
                                            curveRatio = FloatingNavigationBarDefaults.DeformRubberCurveRatio,
                                            maxDistancePx = deformMaxPx,
                                            maxScale = FloatingNavigationBarDefaults.DeformMaxScale,
                                        )
                                    targetDeformScaleX = deform.scaleX
                                    targetDeformScaleY = deform.scaleY

                                    val hoveredIndex =
                                        calculateHoveredIndex(change.position.x, itemXPositions, itemWidths, horizontalPaddingPx)
                                    if (hoveredIndex != -1) {
                                        draggedHoverIndex = hoveredIndex
                                    }
                                }
                            } while (event.changes.any { it.pressed })

                            // Release finger / Hand up
                            isBarDragging = false
                            targetRubberX = 0f
                            targetRubberY = 0f
                            targetDeformScaleX = 1f
                            targetDeformScaleY = 1f

                            val finalIndex = draggedHoverIndex
                            draggedHoverIndex = -1

                            if (finalIndex != -1 && finalIndex < registry.items.size) {
                                registry.items[finalIndex].onClick.invoke()
                            }
                        }
                    }.selectableGroup()
                    .defaultMinSize(minHeight = minBarHeight)
                    .then(
                        if (showDivider) {
                            Modifier
                                .squircleBackground(
                                    color = RmuixTheme.colorScheme.dividerLine,
                                    cornerRadius = cornerRadius,
                                ).padding(0.75.dp)
                        } else {
                            Modifier
                        },
                    ).then(
                        if (shadowElevation > 0.dp) {
                            Modifier.dropShadow(
                                shape = shape,
                                shadow =
                                    Shadow(
                                        radius = FloatingNavigationBarDefaults.ShadowRadius,
                                        color = Color.Black,
                                        alpha = 0.2f,
                                    ),
                            )
                        } else {
                            Modifier
                        },
                    ).then(
                        surface?.invoke(cornerRadius)
                            ?: Modifier.squircleBackground(color = color, cornerRadius = cornerRadius),
                    ).padding(horizontal = innerHorizontalPadding),
        ) {
            Layout(
                content = {
                    Box(
                        modifier =
                            Modifier
                                .layoutId("coloros_indicator")
                                .then(
                                    if (!isDark) {
                                        Modifier.dropShadow(
                                            shape =
                                                SquircleShape(
                                                    FloatingNavigationBarDefaults.IndicatorCornerRadius,
                                                    squircleEnabled = squircleEnabled,
                                                ),
                                            shadow =
                                                Shadow(
                                                    radius = 3.dp,
                                                    color = Color.Black,
                                                    alpha = 0.08f,
                                                ),
                                        )
                                    } else {
                                        Modifier
                                    },
                                ).squircleBackground(
                                    color = indicatorColor,
                                    cornerRadius = FloatingNavigationBarDefaults.IndicatorCornerRadius,
                                ),
                    )
                    content()
                },
            ) { measurables, constraints ->
                val indicatorMeasurable = measurables.fastFirst { it.layoutId == "coloros_indicator" }
                val itemMeasurables = measurables.filter { it.layoutId != "coloros_indicator" }

                val itemPlaceables =
                    itemMeasurables.map {
                        it.measure(constraints.copy(minWidth = 0, minHeight = 0))
                    }

                var totalWidth = 0
                var maxHeight = 0
                val xPositions = IntArray(itemPlaceables.size)
                val widths = IntArray(itemPlaceables.size)

                for (i in itemPlaceables.indices) {
                    val placeable = itemPlaceables[i]
                    xPositions[i] = totalWidth
                    widths[i] = placeable.width
                    totalWidth += placeable.width
                    if (i < itemPlaceables.size - 1) {
                        totalWidth += spacingPx
                    }
                    if (placeable.height > maxHeight) {
                        maxHeight = placeable.height
                    }
                }

                itemXPositions = xPositions
                itemWidths = widths

                val activeIndex =
                    if (draggedHoverIndex != -1 && draggedHoverIndex < itemPlaceables.size) {
                        draggedHoverIndex
                    } else {
                        itemMeasurables.indexOfFirst { it.layoutId == "floating_item_selected" }
                    }

                if (activeIndex != -1 && activeIndex < itemPlaceables.size) {
                    val selPlaceable = itemPlaceables[activeIndex]
                    targetX = xPositions[activeIndex].toFloat() - itemBgPadPx
                    targetWidth = selPlaceable.width.toFloat() + itemBgPadPx * 2
                    targetHeight = selPlaceable.height.toFloat()
                    if (!isSelectedFound) isSelectedFound = true
                }

                val indicatorHeightPx = (targetHeight - 8f).coerceAtLeast(0f).fastRoundToInt()
                val indicatorPlaceable =
                    if (isSelectedFound && animatedWidth > 0f) {
                        indicatorMeasurable.measure(
                            Constraints.fixed(
                                width = animatedWidth.fastRoundToInt(),
                                height = if (indicatorHeightPx > 0) indicatorHeightPx else 40.dp.roundToPx(),
                            ),
                        )
                    } else {
                        null
                    }

                layout(totalWidth, maxHeight) {
                    indicatorPlaceable?.placeRelative(
                        x = animatedX.fastRoundToInt(),
                        y = (maxHeight - indicatorPlaceable.height) / 2,
                    )
                    for (i in itemPlaceables.indices) {
                        itemPlaceables[i].placeRelative(
                            x = xPositions[i],
                            y = (maxHeight - itemPlaceables[i].height) / 2,
                        )
                    }
                }
            }
        }
    }
}

private fun calculateHoveredIndex(
    x: Float,
    xPositions: IntArray,
    widths: IntArray,
    horizontalPaddingPx: Float,
): Int {
    val localX = x - horizontalPaddingPx
    for (i in xPositions.indices) {
        val startX = xPositions[i].toFloat()
        val endX = startX + widths[i]
        if (localX in startX..endX) {
            return i
        }
    }
    return -1
}

/**
 * A [FloatingNavigationBarItem] that is suitable for [FloatingNavigationBar].
 */
@Composable
fun FloatingNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    colors: NavigationBarItemColors = NavigationBarDefaults.floatingNavigationBarItemColors(),
    badge: (@Composable () -> Unit)? = null,
    badgeOffset: DpSize = NavigationBarDefaults.BadgeDotOffset,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val mode = LocalNavigationBarDisplayMode.current

    val itemScope = LocalFloatingItemScope.current
    val itemHoverIndex = LocalFloatingItemHoverIndex.current

    val itemIndex =
        remember(itemScope) {
            itemScope?.currentIndex?.also { itemScope.currentIndex++ } ?: -1
        }

    if (itemScope != null && itemIndex != -1) {
        itemScope.registry.register(selected, itemIndex, onClick)
    }

    val isHovered = itemHoverIndex != -1 && itemHoverIndex == itemIndex
    val isEffectivelySelected = if (itemHoverIndex != -1) isHovered else selected

    // `COUIBottomFloatingToolbarItemView` has no touch-motion animator: the stock item has no press scale,
    // only the content-color selector.
    val tint = colors.contentColor(isEffectivelySelected, isPressed)
    val fontWeight = if (isEffectivelySelected) FontWeight.Bold else FontWeight.Normal
    val tag = if (isEffectivelySelected) "floating_item_selected" else "floating_item_unselected"

    Column(
        modifier =
            modifier
                .layoutId(tag)
                .widthIn(max = FloatingNavigationBarDefaults.ItemMaxWidth)
                .height(FloatingNavigationBarDefaults.ItemHeight)
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Tab,
                    interactionSource = interactionSource,
                    indication = null,
                ).padding(
                    vertical = FloatingNavigationBarDefaults.ItemIconTopMargin,
                    horizontal =
                        if (mode == NavigationBarDisplayMode.TextOnly) {
                            FloatingNavigationBarDefaults.ItemPadding
                        } else {
                            FloatingNavigationBarDefaults.ItemHorizontalPadding
                        },
                ),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when (mode) {
            NavigationBarDisplayMode.TextOnly -> {
                Text(
                    text = label,
                    color = tint,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = if (isEffectivelySelected) FontWeight.Bold else FontWeight.Medium,
                )
            }

            NavigationBarDisplayMode.IconOnly -> {
                if (icon != null) {
                    NavigationItemIcon(badge = badge, badgeOffset = badgeOffset) { iconModifier ->
                        Image(
                            modifier = iconModifier.size(FloatingNavigationBarDefaults.IconSize),
                            imageVector = icon,
                            contentDescription = label,
                        )
                    }
                }
            }

            NavigationBarDisplayMode.IconWithSelectedLabel -> {
                if (icon != null) {
                    NavigationItemIcon(badge = badge, badgeOffset = badgeOffset) { iconModifier ->
                        Image(
                            modifier = iconModifier.size(FloatingNavigationBarDefaults.IconSize),
                            imageVector = icon,
                            contentDescription = label,
                        )
                    }
                }
                if (isEffectivelySelected) {
                    Spacer(modifier = Modifier.height(FloatingNavigationBarDefaults.IconTextGap))
                    Text(
                        text = label,
                        color = tint,
                        textAlign = TextAlign.Center,
                        fontSize = NavigationBarDefaults.LabelFontSize,
                        fontWeight = fontWeight,
                    )
                }
            }

            else -> {
                if (icon != null) {
                    NavigationItemIcon(badge = badge, badgeOffset = badgeOffset) { iconModifier ->
                        Image(
                            modifier = iconModifier.size(FloatingNavigationBarDefaults.IconSize),
                            imageVector = icon,
                            contentDescription = label,
                        )
                    }
                    Spacer(modifier = Modifier.height(FloatingNavigationBarDefaults.IconTextGap))
                }
                Text(
                    text = label,
                    color = tint,
                    textAlign = TextAlign.Center,
                    fontSize = NavigationBarDefaults.LabelFontSize,
                    fontWeight = fontWeight,
                )
            }
        }
    }
}

/**
 * A helper composable that renders an icon with an optional badge.
 */
@Composable
@Suppress("ktlint:compose:content-slot-reused")
internal fun NavigationItemIcon(
    badge: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
    badgeOffset: DpSize = NavigationBarDefaults.BadgeDotOffset,
    content: @Composable (Modifier) -> Unit,
) {
    if (badge != null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.TopEnd,
        ) {
            content(Modifier)
            // `COUINavigationItemView.mDefRedDotOffset` (1dp, dots) and
            // `mDefRedDotTextOffsetHorizontal/Vertical` (8dp / 5dp, numbered badges) push the badge
            // outward from the icon's top-end corner: +x toward the end, -y upward.
            Box(
                modifier =
                    Modifier.offset(
                        x = badgeOffset.width,
                        y = -badgeOffset.height,
                    ),
            ) {
                badge()
            }
        }
    } else {
        content(modifier)
    }
}

object NavigationBarDefaults {
    /** The bar height (ColorOS 17 `coui_navigation_normal_item_height`): 56dp. */
    val BarHeight = 56.dp

    /** `coui_navigation_enlarge_item_height`: item and bar height of the enlarged variant. */
    val EnlargedItemHeight = NavigationBarStockEnlargeItemHeight

    /** `coui_navigation_enlarge_icon_size`: the icon the selected item grows into. */
    val EnlargedIconSize = NavigationBarStockEnlargeIconSize

    /** `coui_navigation_enlarge_icon_margin_top`: top inset of an unselected icon. */
    val EnlargedIconTopPadding = NavigationBarStockEnlargeIconMarginTop

    /**
     * Outward offset of a dot badge from the item icon's top-end corner.
     *
     * `coui_navigation_red_dot_offset`: 1dp, read by `COUINavigationItemView` as `mDefRedDotOffset`.
     */
    val BadgeDotOffset = DpSize(1.dp, 1.dp)

    /**
     * Outward offset of a numbered badge.
     *
     * `coui_navigation_red_dot_with_number_horizontal_offset` (8dp) and
     * `coui_navigation_red_dot_with_number_vertical_offset` (5dp), read as
     * `mDefRedDotTextOffsetHorizontal` / `mDefRedDotTextOffsetVertical`.
     */
    val BadgeNumberOffset = DpSize(8.dp, 5.dp)

    /**
     * Bottom padding used on iOS, where [WindowInsets.navigationBars] reports nothing: 20dp.
     *
     * Not a ColorOS value — it only keeps the bar off the home indicator on platforms without insets.
     */
    val IosBottomPadding = 20.dp

    /**
     * The item height: 56dp, the same as [BarHeight].
     *
     * Two live dimens agree on 56dp — `coui_navigation_normal_item_height` (read by
     * `COUINavigationItemView.mNormalItemHeight`) and `coui_tool_navigation_item_height` (the height of
     * `coui_navigation_item_layout.xml`) — so the item fills the bar. The 54dp
     * `coui_navigation_item_height` dimen is **declared but never referenced** anywhere in the APK; the
     * earlier 54dp value here was attributed to it by mistake.
     */
    val ItemHeight = 56.dp

    /** Horizontal inset before the items (ColorOS 17 coui_tool_navigation_edge_item_padding). */
    val EdgePadding = NavigationBarStockEdgePadding

    /** The hairline above the bar (ColorOS 17 coui_navigation_shadow_height). */
    val DividerHeight = NavigationBarStockDividerHeight

    /** Radius of the item's hover/focus/press mask (ColorOS 17 coui_navigation_item_background_radius). */
    val ItemCornerRadius = NavigationBarStockItemCornerRadius

    /** Half of the 4dp gap between neighbouring items (ColorOS 17 coui_navigation_item_half_gap). */
    val ItemGap = NavigationBarStockItemGap

    /** The default icon size (ColorOS 17 coui_navigation_icon_size). */
    val IconSize = 24.dp

    /** The default label font size (ColorOS 17 coui_navigation_item_text_size). */
    val LabelFontSize = 10.sp

    /** The default top padding for the icon (ColorOS 17 coui_navigation_icon_margin_top). */
    val IconTopPadding = 9.dp

    /** The default gap between icon and text (ColorOS 17 coui_navigation_text_margin_top). */
    val IconTextGap = 2.dp

    /** The default bottom padding for the label. */
    val BottomPadding = 4.dp

    /** The alpha value for the selected item when pressed. */
    val SelectedPressedAlpha = 0.5f

    /** The alpha value for an unselected item when pressed. */
    val UnselectedPressedAlpha = 0.6f

    /** The alpha value for an unselected item. */
    val UnselectedAlpha = 0.4f

    /**
     * The default colors for [NavigationBarItem].
     */
    @Composable
    fun navigationBarItemColors(
        unselectedContentColor: Color = RmuixTheme.colorScheme.onSurfaceContainer,
        selectedContentColor: Color = RmuixTheme.colorScheme.primary,
    ): NavigationBarItemColors =
        remember(unselectedContentColor, selectedContentColor) {
            NavigationBarItemColors(
                unselectedContentColor = unselectedContentColor,
                selectedContentColor = selectedContentColor,
            )
        }

    /**
     * The default colors for [FloatingNavigationBarItem].
     */
    @Composable
    fun floatingNavigationBarItemColors(
        unselectedContentColor: Color = RmuixTheme.colorScheme.onSurfaceContainer,
        selectedContentColor: Color = RmuixTheme.colorScheme.onSurface,
    ): NavigationBarItemColors =
        remember(unselectedContentColor, selectedContentColor) {
            NavigationBarItemColors(
                unselectedContentColor = unselectedContentColor,
                selectedContentColor = selectedContentColor,
            )
        }
}

/**
 * Base content colors shared by [NavigationBarItem] and [FloatingNavigationBarItem].
 */
@Immutable
data class NavigationBarItemColors(
    private val unselectedContentColor: Color,
    private val selectedContentColor: Color,
) {
    @Stable
    internal fun contentColor(
        selected: Boolean,
        isPressed: Boolean,
    ): Color =
        when {
            isPressed -> {
                if (selected) {
                    selectedContentColor.copy(alpha = selectedContentColor.alpha * NavigationBarDefaults.SelectedPressedAlpha)
                } else {
                    unselectedContentColor.copy(alpha = unselectedContentColor.alpha * NavigationBarDefaults.UnselectedPressedAlpha)
                }
            }

            selected -> {
                selectedContentColor
            }

            else -> {
                unselectedContentColor.copy(alpha = unselectedContentColor.alpha * NavigationBarDefaults.UnselectedAlpha)
            }
        }

    /** Content colour for the standard bar, whose press feedback is the item state mask. */
    @Stable
    internal fun contentColor(selected: Boolean): Color =
        if (selected) {
            selectedContentColor
        } else {
            unselectedContentColor.copy(alpha = unselectedContentColor.alpha * NavigationBarDefaults.UnselectedAlpha)
        }
}

/** Contains default values used by [FloatingNavigationBar] and [FloatingNavigationBarItem]. */
object FloatingNavigationBarDefaults {
    /** `coui_bottom_floating_navigation_bar_height`: 56dp. */
    val BarHeight = 56.dp

    /** `coui_bottom_floating_navigation_bar_item_height`: 48dp. */
    val ItemHeight = 48.dp

    /** `coui_bottom_floating_navigation_bar_item_max_width`: 70dp. */
    val ItemMaxWidth = 70.dp

    /** `coui_bottom_floating_navigation_bar_item_padding`: 12dp for text-only items. */
    val ItemPadding = 12.dp

    /**
     * `coui_bottom_floating_navigation_bar_item_icon_top_margin`: 4dp. The library applies it
     * symmetrically as the item's vertical content padding.
     */
    val ItemIconTopMargin = 4.dp

    /**
     * No stock token: derived as half the 56dp bar height, i.e. the bar is a pill. Kept configurable
     * because the official class computes its outline from the measured height.
     */
    val CornerRadius = 28.dp

    /**
     * Horizontal margin around the whole bar when it is **not** centered - `Start` / `End` alignment.
     *
     * No stock token: `COUIBottomFloatingNavigationBar` only sizes itself to `content + padding`, and the
     * class that adds it decides the outer margin. `coui_bottom_floating_navigation_bar_padding_horizontal`
     * (16dp) is the bar's **inner** padding, not this margin, so it must not be used as its source; 36dp
     * is a local choice. Centered (the default) uses 0.
     */
    val HorizontalOutSidePadding = 36.dp

    /** No stock token: the official bar draws its shadow through the melt-ball outline, not an elevation. */
    val ShadowElevation = 1.dp

    /**
     * `coui_bottom_floating_navigation_bar_padding_horizontal`: 16dp. `changePadding()` keeps it only on
     * compact widths (`COUIResponsiveUtils.isSmallScreen` -> `WindowWidthSizeClass.Compact`), so larger
     * windows get 0.
     */
    val HorizontalPadding = 16.dp

    /** Material `WindowWidthSizeClass` compact/medium boundary, as used by `COUIResponsiveUtils`. */
    val CompactWidthMax = 600.dp

    /**
     * Horizontal content padding of an icon item: **no stock token**. The similarly named
     * `coui_bottom_floating_navigation_bar_item_bg_horizontal_padding` (8dp) is the *background mask
     * outset* read in `applyBackgroundMaskRect`, not this padding, so 4dp stays a layout choice.
     */
    val ItemHorizontalPadding = 4.dp

    /** `coui_bottom_floating_navigation_bar_item_space`: 8dp. */
    val ItemSpacing = 8.dp

    /**
     * Gap between the main pill and a satellite accessory button.
     *
     * `COUIBottomFloatingAccessoryNavigationBar.mSideViewSpacing` reads
     * `coui_bottom_floating_navigation_bar_Minimum_spacing_with_slide_view` (**12dp**) and adds it
     * to the accessory size in `computeSideReserveForConfiguredIcons()` /
     * `layoutPositionsMediumScreen()`. The token name mentions slide views, but the accessory bar
     * uses it as the pill-to-accessory gap.
     */
    val SideSpacing = 12.dp

    /** Horizontal padding of the selected item background (`..._item_bg_horizontal_padding`, 8dp). */
    val ItemBackgroundHorizontalPadding = 8.dp

    /**
     * Minimum bar height without vertical padding
     * (`..._min_height_without_padding_vertical`, 88dp = the 56dp bar plus 16dp top and bottom).
     *
     * Recorded only: the stock bar reserves 16dp of vertical padding around the 56dp capsule; this
     * library lays the capsule out without that padding, so the minimum height is [BarHeight].
     */
    val MinHeightWithoutVerticalPadding = 88.dp

    /**
     * How far a 40dp large icon may overflow the item
     * (`..._large_icon_overflow`, 14dp = (40 − 24) / 2 − 1dp of slack).
     *
     * Recorded only: `COUIBottomFloatingNavigationBar` widens the draw region by this amount
     * (`mMenuView.getTop() - mLargeIconOverflow`); this library has no large-icon mode yet.
     */
    val LargeIconOverflow = 14.dp

    /**
     * Horizontal gap between icon and title (`..._item_icon_title_gap_horizontal`, 4dp).
     *
     * Recorded only: it applies to the horizontal icon+title layout of
     * `COUIBottomFloatingToolbarItemView`; this library stacks icon over title in every mode.
     */
    val IconTitleGapHorizontal = 4.dp

    /**
     * Scale a satellite accessory collapses to.
     *
     * `AccessorySideExpansionController.doAlphaScaleAnim()` animates the scale value to
     * `10000` (1.0) when expanding and to `5000` (**0.5**) when collapsing.
     */
    val AccessoryMinScale = 0.5f

    /**
     * Response of the accessory alpha spring when the melt ball is supported.
     *
     * `initAlphaAnim()`: `setBounce(0f)` and
     * `setResponse(mHost.isSupportMeltBall() ? 0.35f : 0.2f)`.
     */
    val AccessoryAlphaResponse = 0.35f

    /** Alpha response on devices without melt-ball support (`AccessoryAlphaResponse` fallback). */
    val AccessoryAlphaResponseWithoutMelt = 0.2f

    /**
     * Damping ratio of the accessory scale spring.
     *
     * `initScaleAnim()`: bounce `0.35` / response `0.55` with melt ball, `0.3` / `0.5` without;
     * `folmeSpring` takes the damping ratio, which is `1 - bounce`.
     */
    val AccessoryScaleDamping = 0.65f

    /** Response of the accessory scale spring (melt-ball variant). */
    val AccessoryScaleResponse = 0.55f

    /** Damping ratio of the accessory translationX spring (`initTranslationAnim()`). */
    val AccessoryTranslationDamping = 0.65f

    /** Response of the accessory translationX spring (melt-ball variant). */
    val AccessoryTranslationResponse = 0.55f

    /** `setMinimumVisibleChange(0.001f)` on the stock translationX spring. */
    val AccessoryTranslationMinVisibleChange = 0.001f

    /**
     * `coui_bottom_floating_navigation_bar_mask_center_color`: `#00FFFFFF`.
     *
     * The stock melt mask fades from this (fully transparent) centre to [MenuMaskEdgeColor].
     */
    val MenuMaskCenterColor = Color(0x00FFFFFF)

    /** `coui_bottom_floating_navigation_bar_mask_edge_color`: `#33FFFFFF` (20% white). */
    val MenuMaskEdgeColor = Color(0x33FFFFFF)

    /** `coui_bottom_floating_navigation_bar_menu_shadow_color`: `#8A000000`. */
    val MenuShadowColor = Color(0x8A000000)

    /**
     * Maximum stretch factor of the touch deformation.
     *
     * `CommonParams` default `iolMaxScale = 1.2f`; `COUIDeformAnimatorUnit.calculateMaxScale()`
     * narrows it further for large surfaces (`calculateScaleWithK`), which this port does not model.
     */
    val DeformMaxScale = 1.2f

    /** `TouchMotionTierParams.iolDeformRubberCurveRatio`: 0.55. */
    val DeformRubberCurveRatio = 0.55f

    /** `TouchMotionTierParams.iolMaxDeformRubberDistance`: 150dp. */
    val DeformMaxRubberDistance = 150.dp

    /** `TouchMotionTierParams.iolMaxStretchDistance`: 28dp. */
    val StretchMaxDistance = 28.dp

    /** `TouchMotionTierParams.iolRubberCurveRatio`: 0.05. */
    val StretchCurveRatio = 0.05f

    /** `TouchMotionTierParams.iolDragSpring`: bounce 0.15 / response 0.15 -> damping 0.85. */
    val DragSpringDamping = 0.85f

    /** `TouchMotionTierParams.iolDragSpring` response. */
    val DragSpringResponse = 0.15f

    /** `TouchMotionTierParams.iolHandUpSpring`: bounce 0.65 / response 0.42 -> damping 0.35. */
    val HandUpSpringDamping = 0.35f

    /** `TouchMotionTierParams.iolHandUpSpring` response. */
    val HandUpSpringResponse = 0.42f

    /** `TouchMotionTierParams.iolHandUpSpringLargeArea`: bounce 0.65 / response 0.45. */
    val HandUpSpringLargeAreaResponse = 0.45f

    /** `TouchMotionTierParams.iolHandUpMaxInitialVelocity`: 1000. */
    val HandUpMaxInitialVelocity = 1000f

    /**
     * `coui_bottom_floating_navigation_bar_melt_ball_param_extension`: 4dp, the meta-ball extension the
     * stock bar hands to `OplusMaterialMetaBallParams(..., 4dp, 1.5f, 2.0f)`. Recorded only: the AGSL
     * bridge this port draws does not yet model the extension or the two factors.
     */
    val MeltBallExtension = FloatingNavMeltBallExtension

    /** `MELT_BALL_EXTENSION_FACTOR_X` = 1.5 (recorded, not applied). */
    val MeltBallFactorX = FLOATING_NAV_MELT_BALL_FACTOR_X

    /** `MELT_BALL_EXTENSION_FACTOR_Y` = 2.0 (recorded, not applied). */
    val MeltBallFactorY = FLOATING_NAV_MELT_BALL_FACTOR_Y

    /**
     * `coui_bottom_floating_navigation_bar_touch_effect_radius`: 40dp radius of the radial press
     * gradient that `COUIBottomFloatingAccessoryView` paints at the finger (`mTouchEffectRadius`).
     *
     * Recorded only - this port renders a 0.96x scale on press instead of the radial sweep.
     */
    val TouchEffectRadius = FloatingNavTouchEffectRadius

    /** No floating-bar token: 24dp is the generic COUI icon size, not a navigation-specific dimens. */
    val IconSize = 24.dp

    /** `coui_bottom_floating_navigation_bar_large_icon_size`: 40dp. */
    val LargeIconSize = 40.dp

    /** `coui_bottom_floating_navigation_bar_item_icon_title_gap_vertical`: 2dp. */
    val IconTextGap = 2.dp

    /**
     * 8dp between the floating bar and the system navigation area when that inset is non-zero.
     *
     * The `coui_bottom_floating_navigation_bar_menu_to_screen_navi_distance` dimen is 8dp but has no
     * reader in the APK, so this value is a library choice that happens to match it.
     */
    val NaviDistance = 8.dp

    /**
     * Fallback bottom distance used when the system navigation inset is 0 (gesture navigation).
     *
     * No stock token - the official bar delegates its placement to the host. Kept as a named value so the
     * 36dp in the layout code is no longer a bare literal.
     */
    val GestureNaviDistance = 36.dp

    /** No stock token; legacy value used by the in-app bar before the floating tokens were read. */
    val IconPadding = 10.dp

    /**
     * Rounding of the moving colour indicator: 20dp.
     *
     * No `coui_navigation_*` dimen carries this value (`coui_navigation_item_background_radius` is 8dp for
     * the item mask), so it is a library value chosen to read as a pill inside the 54dp item.
     */
    val IndicatorCornerRadius = 20.dp

    /**
     * Blur radius of the bar's drop shadow: 10dp.
     *
     * No stock token: ColorOS draws the bar shadow through the melt-ball outline and a
     * `coui_navigation_shadow_height` 0.33dp hairline, not an elevation.
     */
    val ShadowRadius = 10.dp

    /** The alpha value for the selected item when pressed. */
    val SelectedPressedAlpha = 0.5f

    /** The alpha value for an unselected item when pressed. */
    val UnselectedPressedAlpha = 0.6f

    /** The alpha value for an unselected item. */
    val UnselectedAlpha = 0.4f
}

enum class NavigationBarDisplayMode {
    IconAndText,
    IconOnly,
    IconWithSelectedLabel,
    TextOnly,
}

val LocalNavigationBarDisplayMode = compositionLocalOf { NavigationBarDisplayMode.IconAndText }

@Immutable
data class NavigationItem(
    val label: String,
    val icon: ImageVector,
)
