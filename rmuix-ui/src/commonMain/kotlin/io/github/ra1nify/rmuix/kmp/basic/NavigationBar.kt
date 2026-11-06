@file:Suppress("ktlint:compose:param-order-check")

// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastRoundToInt
import io.github.ra1nify.rmuix.kmp.anim.folmeSpring
import io.github.ra1nify.rmuix.kmp.shader.rmuixSpotlight
import io.github.ra1nify.rmuix.kmp.squircle.squircleBackground
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.Platform
import io.github.ra1nify.rmuix.kmp.utils.platform

/**
 * A [NavigationBar] that with 2 to 5 items styled according to ColorOS 17 metrics.
 */
@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    color: Color = RmuixTheme.colorScheme.surface,
    showDivider: Boolean = true,
    defaultWindowInsetsPadding: Boolean = true,
    mode: NavigationBarDisplayMode = NavigationBarDisplayMode.IconAndText,
    content: @Composable RowScope.() -> Unit,
) {
    val captionBarPaddings = WindowInsets.captionBar.only(WindowInsetsSides.Bottom).asPaddingValues()
    val captionBarBottomPaddingValue = captionBarPaddings.calculateBottomPadding()

    val animatedCaptionBarHeight by animateDpAsState(
        targetValue = if (captionBarBottomPaddingValue > 0.dp) captionBarBottomPaddingValue else 0.dp,
        animationSpec = tween(durationMillis = 300),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color),
    ) {
        if (showDivider) {
            HorizontalDivider()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(LocalNavigationBarDisplayMode provides mode) {
                content()
            }
        }
        if (defaultWindowInsetsPadding) {
            val navigationBarsPadding = if (platform() != Platform.IOS) {
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            } else {
                20.dp
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .layout { measurable, constraints ->
                        val totalHeight = (navigationBarsPadding + animatedCaptionBarHeight).roundToPx()
                        val fixedConstraints = constraints.copy(minHeight = totalHeight, maxHeight = totalHeight)
                        val placeable = measurable.measure(fixedConstraints)
                        layout(placeable.width, totalHeight) {
                            placeable.placeRelative(0, 0)
                        }
                    }
                    .pointerInput(Unit) { detectTapGestures { /* Do nothing to consume the click */ } },
            )
        }
    }
}

/**
 * A [NavigationBarItem] that is suitable for [NavigationBar].
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
) {
    val itemHeight = NavigationBarDefaults.ItemHeight
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val tint = colors.contentColor(selected, isPressed)
    val fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
    val mode = LocalNavigationBarDisplayMode.current

    Column(
        modifier = modifier
            .height(itemHeight)
            .weight(1f)
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
            )
            .then(if (badge != null) Modifier.badgeBounds() else Modifier),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = if (mode == NavigationBarDisplayMode.IconAndText || mode == NavigationBarDisplayMode.IconWithSelectedLabel) Arrangement.Top else Arrangement.Center,
    ) {
        when (mode) {
            NavigationBarDisplayMode.IconAndText -> {
                NavigationItemIcon(
                    badge = badge,
                    modifier = Modifier.padding(top = NavigationBarDefaults.IconTopPadding),
                ) { iconModifier ->
                    Image(
                        modifier = iconModifier.size(NavigationBarDefaults.IconSize),
                        imageVector = icon,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(tint),
                    )
                }
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

            NavigationBarDisplayMode.IconWithSelectedLabel -> {
                val defaultPadding = (itemHeight - NavigationBarDefaults.IconSize) / 2
                val iconTopPadding by animateDpAsState(
                    targetValue = if (selected) NavigationBarDefaults.IconTopPadding else defaultPadding,
                    animationSpec = tween(durationMillis = 300),
                    label = "iconTopPadding",
                )
                val textAlpha by animateFloatAsState(
                    targetValue = if (selected) 1f else 0f,
                    animationSpec = tween(durationMillis = 300),
                    label = "textAlpha",
                )

                NavigationItemIcon(
                    badge = badge,
                    modifier = Modifier.layout { measurable, constraints ->
                        val topPaddingPx = iconTopPadding.roundToPx()
                        val placeable = measurable.measure(constraints.offset(vertical = -topPaddingPx))
                        layout(placeable.width, placeable.height + topPaddingPx) {
                            placeable.placeRelative(0, topPaddingPx)
                        }
                    },
                ) { iconModifier ->
                    Image(
                        modifier = iconModifier.size(NavigationBarDefaults.IconSize),
                        imageVector = icon,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(tint),
                    )
                }
                Spacer(modifier = Modifier.height(NavigationBarDefaults.IconTextGap))
                Text(
                    modifier = Modifier
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
                        imageVector = icon,
                        contentDescription = label,
                        colorFilter = ColorFilter.tint(tint),
                    )
                }
            }
        }
    }
}

/**
 * A floating navigation bar supporting 2 to 5 items and 4 three-island modes (Single, Left, Right, Both).
 */
@Suppress("ktlint:compose:modifier-not-used-at-root")
@Composable
fun FloatingNavigationBar(
    modifier: Modifier = Modifier,
    style: FloatingNavigationBarStyle = FloatingNavigationBarStyle.ColorOS,
    startAccessory: (@Composable () -> Unit)? = null,
    endAccessory: (@Composable () -> Unit)? = null,
    startAccessoryVisible: Boolean = true,
    endAccessoryVisible: Boolean = true,
    color: Color = RmuixTheme.colorScheme.surfaceContainer,
    indicatorColor: Color = if (isSystemInDarkTheme()) Color(0xFF2C2C2E) else Color.White,
    cornerRadius: Dp = if (style == FloatingNavigationBarStyle.ColorOS) FloatingNavigationBarDefaults.CornerRadius else FloatingToolbarDefaults.CornerRadius,
    horizontalAlignment: Alignment.Horizontal = CenterHorizontally,
    horizontalOutSidePadding: Dp = FloatingNavigationBarDefaults.HorizontalOutSidePadding,
    shadowElevation: Dp = FloatingNavigationBarDefaults.ShadowElevation,
    showDivider: Boolean = false,
    defaultWindowInsetsPadding: Boolean = true,
    content: @Composable () -> Unit,
) {
    val navBarBottomPadding = WindowInsets.navigationBars.only(WindowInsetsSides.Bottom).asPaddingValues().calculateBottomPadding()
    val bottomPaddingValue = when (platform()) {
        Platform.IOS -> 36.dp

        else -> {
            val naviDistance = if (style == FloatingNavigationBarStyle.ColorOS) FloatingNavigationBarDefaults.NaviDistance else 26.dp
            if (navBarBottomPadding != 0.dp) naviDistance + navBarBottomPadding else 36.dp
        }
    }

    val captionBarBottomPaddingValue = WindowInsets.captionBar.only(WindowInsetsSides.Bottom).asPaddingValues().calculateBottomPadding()
    val animatedCaptionBarHeight by animateDpAsState(
        targetValue = if (captionBarBottomPaddingValue > 0.dp) captionBarBottomPaddingValue else 0.dp,
        animationSpec = tween(durationMillis = 300),
    )

    val itemSpacing = if (style == FloatingNavigationBarStyle.ColorOS) {
        FloatingNavigationBarDefaults.ItemSpacing
    } else {
        12.dp
    }

    val minBarHeight = if (style == FloatingNavigationBarStyle.ColorOS) {
        FloatingNavigationBarDefaults.BarHeight
    } else {
        52.dp
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (horizontalAlignment == Alignment.Start) horizontalOutSidePadding else 0.dp,
                end = if (horizontalAlignment == Alignment.End) horizontalOutSidePadding else 0.dp,
            ),
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = bottomPaddingValue)
                .then(
                    if (defaultWindowInsetsPadding) {
                        Modifier.padding(bottom = animatedCaptionBarHeight)
                    } else {
                        Modifier
                    },
                )
                .align(horizontalAlignment),
        ) {
            if (style == FloatingNavigationBarStyle.ColorOS) {
                ColorOSFloatingThreeIslandLayout(
                    startAccessory = startAccessory,
                    endAccessory = endAccessory,
                    startAccessoryVisible = startAccessoryVisible,
                    endAccessoryVisible = endAccessoryVisible,
                    sideSpacing = FloatingNavigationBarDefaults.SideSpacing,
                ) {
                    SinglePillBar(
                        modifier = modifier,
                        color = color,
                        cornerRadius = cornerRadius,
                        shadowElevation = shadowElevation,
                        showDivider = showDivider,
                        minBarHeight = minBarHeight,
                        itemSpacing = itemSpacing,
                        indicatorColor = indicatorColor,
                        content = content,
                    )
                }
            } else {
                SinglePillBar(
                    modifier = modifier,
                    color = color,
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

@Composable
private fun SinglePillBar(
    modifier: Modifier,
    color: Color,
    cornerRadius: Dp,
    shadowElevation: Dp,
    showDivider: Boolean,
    minBarHeight: Dp,
    itemSpacing: Dp,
    indicatorColor: Color,
    content: @Composable () -> Unit,
) {
    val innerHorizontalPadding = FloatingNavigationBarDefaults.HorizontalPadding

    ColorOSFloatingNavigationBarContainer(
        modifier = modifier,
        color = color,
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
 * A custom three-island [Layout] for [FloatingNavigationBar] that positions start accessory, center menu pill, and end accessory with spring collapse/expand animations.
 */
@Composable
private fun ColorOSFloatingThreeIslandLayout(
    modifier: Modifier = Modifier,
    startAccessory: (@Composable () -> Unit)?,
    endAccessory: (@Composable () -> Unit)?,
    startAccessoryVisible: Boolean = true,
    endAccessoryVisible: Boolean = true,
    sideSpacing: Dp = FloatingNavigationBarDefaults.SideSpacing,
    content: @Composable () -> Unit,
) {
    val startProgress by animateFloatAsState(
        targetValue = if (startAccessory != null && startAccessoryVisible) 1f else 0f,
        animationSpec = folmeSpring(damping = 0.78f, response = 0.42f),
        label = "StartAccessoryProgress",
    )
    val endProgress by animateFloatAsState(
        targetValue = if (endAccessory != null && endAccessoryVisible) 1f else 0f,
        animationSpec = folmeSpring(damping = 0.78f, response = 0.42f),
        label = "EndAccessoryProgress",
    )

    val density = LocalDensity.current
    val spacingPx = with(density) { sideSpacing.roundToPx() }

    Layout(
        modifier = modifier,
        content = {
            if (startAccessory != null) {
                Box(
                    modifier = Modifier
                        .layoutId("start_accessory")
                        .graphicsLayer {
                            scaleX = startProgress
                            scaleY = startProgress
                            alpha = startProgress
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
                    modifier = Modifier
                        .layoutId("end_accessory")
                        .graphicsLayer {
                            scaleX = endProgress
                            scaleY = endProgress
                            alpha = endProgress
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
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = folmeSpring(damping = 0.8f, response = 0.35f),
        label = "AccessoryButtonScale",
    )

    val shape = RoundedCornerShape(FloatingAccessoryButtonDefaults.CornerRadius)
    val contentColor = if (enabled) {
        RmuixTheme.colorScheme.onSurface
    } else {
        RmuixTheme.colorScheme.onSurfaceContainer.copy(alpha = 0.4f)
    }

    Box(
        modifier = modifier
            .size(FloatingAccessoryButtonDefaults.Size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (shadowElevation > 0.dp) {
                    Modifier.dropShadow(
                        shape = shape,
                        shadow = Shadow(
                            radius = 10.dp,
                            color = Color.Black,
                            alpha = 0.2f,
                        ),
                    )
                } else {
                    Modifier
                },
            )
            .rmuixSpotlight(
                glowColor = if (isSystemInDarkTheme()) Color(0x33FFFFFF) else Color(0x1F000000),
                lightRadius = 40.dp,
                enabled = enabled,
            )
            .squircleBackground(color = color, cornerRadius = FloatingAccessoryButtonDefaults.CornerRadius)
            .selectable(
                selected = false,
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null,
            )
            .then(if (badge != null) Modifier.badgeBounds() else Modifier),
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
                        colorFilter = ColorFilter.tint(contentColor),
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

    fun register(selected: Boolean, index: Int, onClick: () -> Unit) {
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

internal class FloatingItemScope(val registry: FloatingItemRegistry) {
    var currentIndex = 0
}

internal val LocalFloatingItemScope = compositionLocalOf<FloatingItemScope?> { null }
internal val LocalFloatingItemHoverIndex = compositionLocalOf { -1 }

/**
 * ColorOS rubber band translation formula from COUITouchMotionAnimator / TouchMotionRubberBand.
 */
private fun couiRubberBand(delta: Float, maxDistance: Float, curveRatio: Float = 0.55f): Float {
    if (maxDistance <= 0f) return 0f
    val magnitude = kotlin.math.abs(delta)
    if (magnitude <= 0f) return 0f
    val ratio = curveRatio.coerceIn(0f, 0.999f)
    val a = magnitude * ratio / maxDistance
    val damped = maxDistance * (1f - 1f / (1f + a))
    return kotlin.math.sign(delta) * damped
}

/**
 * ColorOS area-conserving deformation scale formula from COUIDeformAnimatorUnit.
 */
private fun couiAreaConservingScales(
    dx: Float,
    dy: Float,
    maxDistance: Float,
    maxScale: Float = 1.06f,
    curveRatio: Float = 0.55f,
): Pair<Float, Float> {
    val wSum = kotlin.math.abs(dx) + kotlin.math.abs(dy)
    if (wSum <= 0.0001f) return 1f to 1f

    val ratio = curveRatio.coerceIn(0f, 0.999f)
    val ceiling = maxDistance * ratio / (1f + ratio)
    if (ceiling <= 0f) return 1f to 1f

    val tx = (kotlin.math.abs(dx) / ceiling).coerceIn(0f, 1f)
    val ty = (kotlin.math.abs(dy) / ceiling).coerceIn(0f, 1f)

    val sx = 1f + (maxScale - 1f) * tx
    val sy = 1f + (maxScale - 1f) * ty
    val wx = kotlin.math.abs(dx) / wSum
    val wy = kotlin.math.abs(dy) / wSum

    val outX = kotlin.math.exp(wx * kotlin.math.ln(sx) + wy * kotlin.math.ln(1f / sy))
    val outY = kotlin.math.exp(wx * kotlin.math.ln(1f / sx) + wy * kotlin.math.ln(sy))
    return outX to outY
}

/**
 * A custom [Layout] container for ColorOS 17 [FloatingNavigationBar] that measures items, renders a sliding spring indicator, and handles 4D liquid drag & deformation.
 */
@Composable
private fun ColorOSFloatingNavigationBarContainer(
    modifier: Modifier = Modifier,
    color: Color,
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

    val animatedRubberX by animateFloatAsState(
        targetValue = targetRubberX,
        animationSpec = folmeSpring(damping = 0.75f, response = 0.40f),
        label = "BarRubberX",
    )
    val animatedRubberY by animateFloatAsState(
        targetValue = targetRubberY,
        animationSpec = folmeSpring(damping = 0.75f, response = 0.40f),
        label = "BarRubberY",
    )
    val animatedScaleX by animateFloatAsState(
        targetValue = targetDeformScaleX,
        animationSpec = folmeSpring(damping = 0.75f, response = 0.40f),
        label = "BarScaleX",
    )
    val animatedScaleY by animateFloatAsState(
        targetValue = targetDeformScaleY,
        animationSpec = folmeSpring(damping = 0.75f, response = 0.40f),
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

    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(cornerRadius)
    val density = LocalDensity.current
    val spacingPx = with(density) { itemSpacing.roundToPx() }
    val maxStretchPx = with(density) { 14.dp.toPx() }
    val horizontalPaddingPx = with(density) { innerHorizontalPadding.toPx() }

    CompositionLocalProvider(
        LocalFloatingItemScope provides scope,
        LocalFloatingItemHoverIndex provides draggedHoverIndex,
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = animatedRubberX
                    translationY = animatedRubberY
                    scaleX = animatedScaleX
                    scaleY = animatedScaleY
                }
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val downX = down.position.x
                        val downY = down.position.y

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

                                // Calculate COUI rubber band translation & area conserving deformation
                                targetRubberX = couiRubberBand(dx, maxStretchPx)
                                targetRubberY = couiRubberBand(dy, maxStretchPx)
                                val (sx, sy) = couiAreaConservingScales(dx, dy, maxStretchPx)
                                targetDeformScaleX = sx
                                targetDeformScaleY = sy

                                val hoveredIndex = calculateHoveredIndex(change.position.x, itemXPositions, itemWidths, horizontalPaddingPx)
                                if (hoveredIndex != -1) {
                                    draggedHoverIndex = hoveredIndex
                                }
                            }
                        } while (event.changes.any { it.pressed })

                        // Release finger / Hand up
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
                }
                .selectableGroup()
                .defaultMinSize(minHeight = minBarHeight)
                .then(
                    if (showDivider) {
                        Modifier
                            .squircleBackground(
                                color = RmuixTheme.colorScheme.dividerLine,
                                cornerRadius = cornerRadius,
                            )
                            .padding(0.75.dp)
                    } else {
                        Modifier
                    },
                )
                .then(
                    if (shadowElevation > 0.dp) {
                        Modifier.dropShadow(
                            shape = shape,
                            shadow = Shadow(
                                radius = 10.dp,
                                color = Color.Black,
                                alpha = 0.2f,
                            ),
                        )
                    } else {
                        Modifier
                    },
                )
                .squircleBackground(color = color, cornerRadius = cornerRadius)
                .then(modifier)
                .padding(horizontal = innerHorizontalPadding),
        ) {
            Layout(
                content = {
                    Box(
                        modifier = Modifier
                            .layoutId("coloros_indicator")
                            .then(
                                if (!isDark) {
                                    Modifier.dropShadow(
                                        shape = RoundedCornerShape(20.dp),
                                        shadow = Shadow(
                                            radius = 3.dp,
                                            color = Color.Black,
                                            alpha = 0.08f,
                                        ),
                                    )
                                } else {
                                    Modifier
                                },
                            )
                            .squircleBackground(
                                color = indicatorColor,
                                cornerRadius = 20.dp,
                            ),
                    )
                    content()
                },
            ) { measurables, constraints ->
                val indicatorMeasurable = measurables.fastFirst { it.layoutId == "coloros_indicator" }
                val itemMeasurables = measurables.filter { it.layoutId != "coloros_indicator" }

                val itemPlaceables = itemMeasurables.map {
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

                val activeIndex = if (draggedHoverIndex != -1 && draggedHoverIndex < itemPlaceables.size) {
                    draggedHoverIndex
                } else {
                    itemMeasurables.indexOfFirst { it.layoutId == "floating_item_selected" }
                }

                if (activeIndex != -1 && activeIndex < itemPlaceables.size) {
                    val selPlaceable = itemPlaceables[activeIndex]
                    targetX = xPositions[activeIndex].toFloat()
                    targetWidth = selPlaceable.width.toFloat()
                    targetHeight = selPlaceable.height.toFloat()
                    if (!isSelectedFound) isSelectedFound = true
                }

                val indicatorHeightPx = (targetHeight - 8f).coerceAtLeast(0f).fastRoundToInt()
                val indicatorPlaceable = if (isSelectedFound && animatedWidth > 0f) {
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

private fun calculateHoveredIndex(x: Float, xPositions: IntArray, widths: IntArray, horizontalPaddingPx: Float): Int {
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
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: NavigationBarItemColors = NavigationBarDefaults.floatingNavigationBarItemColors(),
    badge: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val itemScope = LocalFloatingItemScope.current
    val itemHoverIndex = LocalFloatingItemHoverIndex.current

    val itemIndex = remember(itemScope) {
        itemScope?.currentIndex?.also { itemScope.currentIndex++ } ?: -1
    }

    if (itemScope != null && itemIndex != -1) {
        itemScope.registry.register(selected, itemIndex, onClick)
    }

    val isHovered = itemHoverIndex != -1 && itemHoverIndex == itemIndex
    val isEffectivelySelected = if (itemHoverIndex != -1) isHovered else selected

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = folmeSpring(damping = 0.8f, response = 0.35f),
        label = "FloatingItemScale",
    )

    val tint = colors.contentColor(isEffectivelySelected, isPressed)
    val fontWeight = if (isEffectivelySelected) FontWeight.Bold else FontWeight.Normal
    val tag = if (isEffectivelySelected) "floating_item_selected" else "floating_item_unselected"

    Column(
        modifier = modifier
            .layoutId(tag)
            .widthIn(max = FloatingNavigationBarDefaults.ItemMaxWidth)
            .height(FloatingNavigationBarDefaults.ItemHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
            )
            .padding(vertical = 4.dp)
            .then(if (badge != null) Modifier.badgeBounds() else Modifier),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        NavigationItemIcon(badge = badge) { iconModifier ->
            Image(
                modifier = iconModifier.size(FloatingNavigationBarDefaults.IconSize),
                imageVector = icon,
                contentDescription = label,
                colorFilter = ColorFilter.tint(tint),
            )
        }
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

/**
 * A helper composable that renders an icon with an optional badge.
 */
@Composable
internal fun NavigationItemIcon(
    badge: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit,
) {
    if (badge != null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.TopEnd,
        ) {
            content(Modifier)
            badge()
        }
    } else {
        content(modifier)
    }
}

object NavigationBarDefaults {
    /** The default item height (ColorOS 17 coui_navigation_normal_item_height). */
    val ItemHeight = 56.dp

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
    ): NavigationBarItemColors = remember(unselectedContentColor, selectedContentColor) {
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
    ): NavigationBarItemColors = remember(unselectedContentColor, selectedContentColor) {
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
    internal fun contentColor(selected: Boolean, isPressed: Boolean): Color = when {
        isPressed -> if (selected) {
            selectedContentColor.copy(alpha = selectedContentColor.alpha * NavigationBarDefaults.SelectedPressedAlpha)
        } else {
            unselectedContentColor.copy(alpha = unselectedContentColor.alpha * NavigationBarDefaults.UnselectedPressedAlpha)
        }

        selected -> selectedContentColor

        else -> unselectedContentColor.copy(alpha = unselectedContentColor.alpha * NavigationBarDefaults.UnselectedAlpha)
    }
}

/** Contains default values used by [FloatingNavigationBar] and [FloatingNavigationBarItem]. */
object FloatingNavigationBarDefaults {
    /** ColorOS 17 floating bar height. */
    val BarHeight = 56.dp

    /** ColorOS 17 floating item height. */
    val ItemHeight = 48.dp

    /** ColorOS 17 item max width. */
    val ItemMaxWidth = 70.dp

    /** ColorOS 17 floating bar corner radius. */
    val CornerRadius = 28.dp

    /** ColorOS 17 horizontal outside padding. */
    val HorizontalOutSidePadding = 36.dp

    /** ColorOS 17 shadow elevation. */
    val ShadowElevation = 1.dp

    /** ColorOS 17 horizontal inside padding. */
    val HorizontalPadding = 16.dp

    /** ColorOS 17 spacing between items. */
    val ItemSpacing = 8.dp

    /** ColorOS 17 side spacing between main pill and satellite accessory buttons. */
    val SideSpacing = 8.dp

    /** ColorOS 17 standard icon size. */
    val IconSize = 24.dp

    /** ColorOS 17 large icon size. */
    val LargeIconSize = 40.dp

    /** ColorOS 17 gap between icon and title. */
    val IconTextGap = 2.dp

    /** ColorOS 17 floating distance above bottom navigation bar. */
    val NaviDistance = 8.dp

    /** The padding around the icon in legacy [FloatingNavigationBar]. */
    val IconPadding = 10.dp

    /** The alpha value for the selected item when pressed. */
    val SelectedPressedAlpha = 0.5f

    /** The alpha value for an unselected item when pressed. */
    val UnselectedPressedAlpha = 0.6f

    /** The alpha value for an unselected item. */
    val UnselectedAlpha = 0.4f
}

/**
 * Style variant for [FloatingNavigationBar].
 */
enum class FloatingNavigationBarStyle {
    /** ColorOS 17 single-pill floating navigation bar style. */
    ColorOS,

    /** Default / legacy floating navigation bar style. */
    Default,
}

enum class NavigationBarDisplayMode {
    IconAndText,
    IconOnly,
    IconWithSelectedLabel,
}

val LocalNavigationBarDisplayMode = compositionLocalOf { NavigationBarDisplayMode.IconAndText }

@Immutable
data class NavigationItem(
    val label: String,
    val icon: ImageVector,
)
