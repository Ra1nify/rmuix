// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package utils

import LocalAppState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.basic.ScrollBehavior
import io.github.ra1nify.rmuix.kmp.basic.SmallTopAppBar
import io.github.ra1nify.rmuix.kmp.basic.TopAppBar
import io.github.ra1nify.rmuix.kmp.blur.BlendColorEntry
import io.github.ra1nify.rmuix.kmp.blur.BlurColors
import io.github.ra1nify.rmuix.kmp.blur.BlurDefaults
import io.github.ra1nify.rmuix.kmp.blur.LayerBackdrop
import io.github.ra1nify.rmuix.kmp.blur.ProgressiveBlur
import io.github.ra1nify.rmuix.kmp.blur.isRuntimeShaderSupported
import io.github.ra1nify.rmuix.kmp.blur.progressiveTextureBlur
import io.github.ra1nify.rmuix.kmp.blur.rememberLayerBackdrop
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.overScrollVertical
import io.github.ra1nify.rmuix.kmp.utils.scrollEndHaptic

fun Modifier.pageScrollModifiers(
    enableScrollEndHaptic: Boolean,
    showTopAppBar: Boolean,
    topAppBarScrollBehavior: ScrollBehavior,
): Modifier = this
    .then(if (enableScrollEndHaptic) Modifier.scrollEndHaptic() else Modifier)
    .overScrollVertical()
    .then(if (showTopAppBar) Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection) else Modifier)
    .fillMaxHeight()

@Composable
fun pageContentPadding(
    innerPadding: PaddingValues,
    outerPadding: PaddingValues,
    isWideScreen: Boolean,
    extraTop: Dp = 0.dp,
    extraStart: Dp = 0.dp,
    extraEnd: Dp = 0.dp,
    extraBottom: Dp = 0.dp,
): PaddingValues {
    val topPadding = innerPadding.calculateTopPadding() + extraTop
    val bottomPadding = outerPadding.calculateBottomPadding() + extraBottom + if (isWideScreen) {
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + WindowInsets.captionBar.asPaddingValues()
            .calculateBottomPadding()
    } else {
        0.dp
    }

    return remember(topPadding, bottomPadding, extraStart, extraEnd, extraBottom) {
        PaddingValues(
            top = topPadding,
            start = extraStart,
            end = extraEnd,
            bottom = bottomPadding,
        )
    }
}

@Composable
fun AdaptiveTopAppBar(
    title: String,
    showTopAppBar: Boolean,
    isWideScreen: Boolean,
    scrollBehavior: ScrollBehavior,
    subtitle: String = "",
    color: Color = RmuixTheme.colorScheme.surface,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    bottomContent: @Composable () -> Unit = {},
) {
    if (showTopAppBar) {
        if (isWideScreen) {
            SmallTopAppBar(
                title = title,
                subtitle = subtitle,
                color = color,
                scrollBehavior = scrollBehavior,
                defaultWindowInsetsPadding = false,
                navigationIcon = navigationIcon,
                actions = actions,
                bottomContent = bottomContent,
            )
        } else {
            TopAppBar(
                title = title,
                subtitle = subtitle,
                color = color,
                scrollBehavior = scrollBehavior,
                navigationIcon = navigationIcon,
                actions = actions,
                bottomContent = bottomContent,
            )
        }
    }
}

@Composable
fun rememberBlurBackdrop(): LayerBackdrop? {
    val appState = LocalAppState.current
    if (!appState.enableBlur || !isRuntimeShaderSupported()) return null
    val surfaceColor = RmuixTheme.colorScheme.surface
    return rememberLayerBackdrop {
        drawRect(surfaceColor)
        drawContent()
    }
}

@Composable
fun BlurredBar(
    backdrop: LayerBackdrop?,
    blurEnabled: Boolean,
    content: @Composable () -> Unit,
) {
    val isProgressive = LocalAppState.current.blurStyle == 0
    val blurActive = blurEnabled && backdrop != null && isProgressive
    Box(
        modifier = Modifier,
    ) {
        if (blurActive) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .progressiveTextureBlur(
                        backdrop = backdrop,
                        shape = RectangleShape,
                        gradient = ProgressiveBlur.Top.copy(curve = 2.2f),
                        blurRadius = 10f,
                        colors = barBlurColors(),
                    ),
            )
        }
        content()
    }
}

@Composable
private fun barBlurColors(): BlurColors = BlurDefaults.blurColors(
    blendColors = listOf(
        BlendColorEntry(color = RmuixTheme.colorScheme.surface.copy(alpha = 0.3f)),
    ),
)
