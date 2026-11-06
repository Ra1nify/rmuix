// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

@file:OptIn(ExperimentalScrollBarApi::class)

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ra1nify.rmuix.kmp.basic.Card
import io.github.ra1nify.rmuix.kmp.basic.CardDefaults
import io.github.ra1nify.rmuix.kmp.basic.RmuixScrollBehavior
import io.github.ra1nify.rmuix.kmp.basic.Scaffold
import io.github.ra1nify.rmuix.kmp.basic.SmallTitle
import io.github.ra1nify.rmuix.kmp.basic.Text
import io.github.ra1nify.rmuix.kmp.basic.VerticalScrollBar
import io.github.ra1nify.rmuix.kmp.basic.rememberScrollBarAdapter
import io.github.ra1nify.rmuix.kmp.blur.layerBackdrop
import io.github.ra1nify.rmuix.kmp.interfaces.ExperimentalScrollBarApi
import io.github.ra1nify.rmuix.kmp.squircle.squircleBackground
import io.github.ra1nify.rmuix.kmp.squircle.squircleBorder
import io.github.ra1nify.rmuix.kmp.theme.Colors
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.theme.darkColorScheme
import io.github.ra1nify.rmuix.kmp.theme.lightColorScheme
import io.github.ra1nify.rmuix.kmp.theme.platformDynamicColors
import utils.AdaptiveTopAppBar
import utils.BlurredBar
import utils.pageContentPadding
import utils.pageScrollModifiers
import utils.rememberBlurBackdrop

private val CamelCaseRegex = Regex("([A-Z])")
private val ColorBlockCornerRadius = 12.dp

@Immutable
private data class ColorCategoryData(
    val title: String,
    val blocks: List<ColorBlockData>,
)

@Immutable
private data class ColorBlockData(
    val name: String,
    val displayName: String,
    val hexString: String,
    val alphaText: String,
    val color: Color,
    val textColor: Color,
)

private fun Color.toHexArgbString(): String {
    val a = (alpha * 255f).toInt().coerceIn(0, 255)
    val r = (red * 255f).toInt().coerceIn(0, 255)
    val g = (green * 255f).toInt().coerceIn(0, 255)
    val b = (blue * 255f).toInt().coerceIn(0, 255)
    val argb = (a shl 24) or (r shl 16) or (g shl 8) or b
    return "#" + argb.toUInt().toString(16).uppercase().padStart(8, '0')
}

@Composable
fun ColorPage(
    padding: PaddingValues,
) {
    val appState = LocalAppState.current
    val isWideScreen = LocalIsWideScreen.current
    val topAppBarScrollBehavior = RmuixScrollBehavior()

    val backdrop = rememberBlurBackdrop()
    val blurActive = backdrop != null
    val barColor = if (blurActive) Color.Transparent else RmuixTheme.colorScheme.surface

    val lightColors = remember { lightColorScheme() }
    val darkColors = remember { darkColorScheme() }
    val dynLight = platformDynamicColors(dark = false)
    val dynDark = platformDynamicColors(dark = true)

    Scaffold(
        topBar = {
            BlurredBar(backdrop, blurActive) {
                AdaptiveTopAppBar(
                    title = "Color",
                    showTopAppBar = appState.showTopAppBar,
                    isWideScreen = isWideScreen,
                    scrollBehavior = topAppBarScrollBehavior,
                    color = barColor,
                )
            }
        },
    ) { innerPadding ->
        val lazyListState = rememberLazyListState()
        val contentPadding = pageContentPadding(innerPadding, padding, isWideScreen)
        Box(modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.pageScrollModifiers(
                    appState.enableScrollEndHaptic,
                    appState.showTopAppBar,
                    topAppBarScrollBehavior,
                ),
                contentPadding = contentPadding,
            ) {
                item(key = "current") {
                    SmallTitle("Current Theme Colors")
                    Card(
                        modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp),
                        colors = CardDefaults.defaultColors(color = RmuixTheme.colorScheme.surfaceContainer),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        ColorsPreview(RmuixTheme.colorScheme)
                    }
                }
                item(key = "light") {
                    SmallTitle("Light Theme Colors")
                    Card(
                        modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp),
                        colors = CardDefaults.defaultColors(color = lightColors.surfaceContainer),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        ColorsPreview(lightColors)
                    }
                }
                item(key = "dynamic_light") {
                    SmallTitle("Dynamic Light Colors")
                    Card(
                        modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp),
                        colors = CardDefaults.defaultColors(color = dynLight.surfaceContainer),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        ColorsPreview(dynLight)
                    }
                }
                item(key = "dark") {
                    SmallTitle("Dark Theme Colors")
                    Card(
                        modifier = Modifier.padding(horizontal = 12.dp).padding(bottom = 12.dp),
                        colors = CardDefaults.defaultColors(color = darkColors.surfaceContainer),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        ColorsPreview(darkColors)
                    }
                }
                item(key = "dynamic_dark") {
                    SmallTitle("Dynamic Dark Colors")
                    Card(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        colors = CardDefaults.defaultColors(color = dynDark.surfaceContainer),
                        cornerRadius = 16.dp,
                        insideMargin = PaddingValues(horizontal = 16.dp),
                    ) {
                        ColorsPreview(dynDark)
                    }
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
            VerticalScrollBar(
                adapter = rememberScrollBarAdapter(lazyListState),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                trackPadding = contentPadding,
            )
        }
    }
}

@Composable
fun ColorsPreview(colors: Colors) {
    val categories = remember(colors) {
        val primaryGroup = listOf(
            "primary" to colors.primary,
            "onPrimary" to colors.onPrimary,
            "primaryContainer" to colors.primaryContainer,
            "onPrimaryContainer" to colors.onPrimaryContainer,
            "primaryVariant" to colors.primaryVariant,
            "onPrimaryVariant" to colors.onPrimaryVariant,
            "disabledPrimary" to colors.disabledPrimary,
            "disabledOnPrimary" to colors.disabledOnPrimary,
            "disabledPrimaryButton" to colors.disabledPrimaryButton,
            "disabledOnPrimaryButton" to colors.disabledOnPrimaryButton,
            "disabledPrimarySlider" to colors.disabledPrimarySlider,
        )

        val neutralTextGroup = listOf(
            "onBackground" to colors.onBackground,
            "onBackgroundVariant" to colors.onBackgroundVariant,
            "onSurface" to colors.onSurface,
            "onSurfaceSecondary" to colors.onSurfaceSecondary,
            "onSurfaceVariantSummary" to colors.onSurfaceVariantSummary,
            "onSurfaceVariantActions" to colors.onSurfaceVariantActions,
            "disabledOnSurface" to colors.disabledOnSurface,
            "disabledOnSecondary" to colors.disabledOnSecondary,
            "disabledOnSecondaryVariant" to colors.disabledOnSecondaryVariant,
        )

        val surfaceGroup = listOf(
            "background" to colors.background,
            "surface" to colors.surface,
            "surfaceVariant" to colors.surfaceVariant,
            "surfaceContainer" to colors.surfaceContainer,
            "surfaceContainerHigh" to colors.surfaceContainerHigh,
            "surfaceContainerHighest" to colors.surfaceContainerHighest,
            "secondary" to colors.secondary,
            "onSecondary" to colors.onSecondary,
            "secondaryVariant" to colors.secondaryVariant,
            "onSecondaryVariant" to colors.onSecondaryVariant,
            "secondaryContainer" to colors.secondaryContainer,
            "onSecondaryContainer" to colors.onSecondaryContainer,
            "secondaryContainerVariant" to colors.secondaryContainerVariant,
            "onSecondaryContainerVariant" to colors.onSecondaryContainerVariant,
            "tertiaryContainer" to colors.tertiaryContainer,
            "onTertiaryContainer" to colors.onTertiaryContainer,
            "tertiaryContainerVariant" to colors.tertiaryContainerVariant,
        )

        val functionalGroup = listOf(
            "error" to colors.error,
            "onError" to colors.onError,
            "errorContainer" to colors.errorContainer,
            "onErrorContainer" to colors.onErrorContainer,
            "windowDimming" to colors.windowDimming,
            "sliderKeyPoint" to colors.sliderKeyPoint,
            "sliderKeyPointForeground" to colors.sliderKeyPointForeground,
            "sliderBackground" to colors.sliderBackground,
        )

        val dividerOutlineGroup = listOf(
            "dividerLine" to colors.dividerLine,
            "outline" to colors.outline,
        )

        val categoryRawList: List<Pair<String, List<Pair<String, Color>>>> = listOf(
            "Primary & Brand" to primaryGroup,
            "Neutral Text Ladder" to neutralTextGroup,
            "Surface & Containers" to surfaceGroup,
            "Functional, Slider & Window" to functionalGroup,
            "Divider & Outline" to dividerOutlineGroup,
        )

        val allColorMap = (primaryGroup + neutralTextGroup + surfaceGroup + functionalGroup + dividerOutlineGroup).toMap()

        categoryRawList.map { categoryPair ->
            val catTitle = categoryPair.first
            val items = categoryPair.second
            val blocks = items.map { itemPair ->
                val name = itemPair.first
                val color = itemPair.second
                val (surfaceColor, textColor) = when {
                    name.startsWith("on") && allColorMap.containsKey(name.removePrefix("on").replaceFirstChar { it.lowercase() }) -> {
                        color to allColorMap[name.removePrefix("on").replaceFirstChar { it.lowercase() }]!!
                    }

                    !name.startsWith("on") && allColorMap.containsKey("on" + name.replaceFirstChar { it.uppercase() }) -> {
                        color to allColorMap["on" + name.replaceFirstChar { it.uppercase() }]!!
                    }

                    else -> color to (if (color.luminance() > 0.5) Color.Black else Color.White)
                }
                val displayName = name.replace(CamelCaseRegex, " $1").trim().replaceFirstChar { it.uppercase() }
                val hexString = color.toHexArgbString()
                val alphaText = "${(color.alpha * 100f).toInt()}%"
                ColorBlockData(
                    name = name,
                    displayName = displayName,
                    hexString = hexString,
                    alphaText = alphaText,
                    color = surfaceColor,
                    textColor = textColor,
                )
            }
            ColorCategoryData(catTitle, blocks)
        }
    }

    Column(
        modifier = Modifier.padding(vertical = 12.dp),
    ) {
        categories.forEach { category ->
            Text(
                text = category.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurfaceVariantSummary,
                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp),
            )
            category.blocks.chunked(2).forEach { rowBlocks ->
                Row(
                    modifier = Modifier.padding(bottom = 12.dp),
                ) {
                    rowBlocks.forEachIndexed { idx, block ->
                        ColorBlock(
                            block = block,
                            modifier = Modifier
                                .weight(1f)
                                .then(if (idx < rowBlocks.lastIndex) Modifier.padding(end = 12.dp) else Modifier),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorBlock(
    block: ColorBlockData,
    modifier: Modifier = Modifier,
) {
    @Suppress("DEPRECATION")
    val clipboardManager = LocalClipboardManager.current
    val hapticFeedback = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .squircleBackground(block.color, ColorBlockCornerRadius)
            .squircleBorder(1.dp, block.textColor.copy(alpha = 0.3f), ColorBlockCornerRadius)
            .clickable {
                clipboardManager.setText(AnnotatedString(block.hexString))
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = block.displayName,
                color = block.textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = block.hexString,
                color = block.textColor.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Alpha: ${block.alphaText}",
                color = block.textColor.copy(alpha = 0.65f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}
