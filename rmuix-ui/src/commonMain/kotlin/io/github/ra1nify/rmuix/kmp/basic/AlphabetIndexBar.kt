// Copyright 2026, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** Colours of an [AlphabetIndexBar] (`COUITouchSearchView`). */
@Immutable
data class AlphabetIndexBarColors(
    val letterColor: Color,
    val pressedLetterColor: Color,
    val dotColor: Color,
    val pressedDotColor: Color,
    val popupBackgroundColor: Color,
    val popupTextColor: Color,
)

/** Defaults for [AlphabetIndexBar], taken from `COUITouchSearchView` and its resources. */
object AlphabetIndexBarDefaults {
    /**
     * `R.array.special_touchsearch_keys` (`couiFirstIsCharacter="true"`): the letters plus the trailing `#`.
     */
    val Letters: List<String> = ('A'..'Z').map { it.toString() } + "#"

    /** `coui_touchsearch_background_width`. */
    val BackgroundWidth = 39.dp

    /** `coui_touchsearch_each_item_height`. */
    val KeyHeight = 14.dp

    /** `coui_touchsearch_item_spacing`. */
    val KeySpacing = 2.dp

    /** `coui_touchsearch_key_textsize`. */
    val KeyTextSize = 12.sp

    /** `coui_touchsearch_key_dot_textsize`. */
    val DotTextSize = 20.sp

    /** `coui_touchsearch_dot` (`coui_touchsearch_dot` string). */
    val DotText = "\u00b7"

    /** `coui_touchsearch_padding_top` / `_padding_bottom`. */
    val PaddingVertical = 16.dp

    /** `coui_touchsearch_default_height`. */
    val DefaultHeight = 432.dp

    /** `coui_touchsearch_min_height`. */
    val MinHeight = 296.dp

    /** `coui_touchsearch_limit1_height`: the height at which all 26 letters fit (`26 * 16dp`). */
    val LimitAllLettersHeight = 416.dp

    /** `coui_touchsearch_popup_first_default_width` / `_height`: the pressed-letter bubble. */
    val PopupSize = 48.dp

    /** `coui_touchsearch_popupwin_first_textsize` / `couiTextAppearanceHeadline4`. */
    val PopupTextSize = 20.sp

    /** `coui_touchsearch_popupwin_default_offset`. */
    val PopupOffset = 23.dp

    /** `coui_touchsearch_popupwin_margin`. */
    val PopupMargin = 4.dp

    /** `POPUP_WINDOW_APPEAR_DURATION`. */
    val PopupAppearMillis = 350

    /** `POPUP_WINDOW_DISAPPEAR_DURATION`. */
    val PopupDisappearMillis = 300

    /** The stock colours, keyed on the current theme. */
    @Composable
    fun colors(
        letterColor: Color = alphabetIndexLetter(isAlphabetIndexBarDarkTheme()),
        pressedLetterColor: Color = RmuixTheme.colorScheme.primary,
        dotColor: Color = alphabetIndexDot(isAlphabetIndexBarDarkTheme()),
        pressedDotColor: Color = RmuixTheme.colorScheme.primary,
        popupBackgroundColor: Color = alphabetIndexPopupBackground(isAlphabetIndexBarDarkTheme()),
        popupTextColor: Color = alphabetIndexPopupText(isAlphabetIndexBarDarkTheme()),
    ): AlphabetIndexBarColors =
        remember(letterColor, pressedLetterColor, dotColor, pressedDotColor, popupBackgroundColor, popupTextColor) {
            AlphabetIndexBarColors(
                letterColor = letterColor,
                pressedLetterColor = pressedLetterColor,
                dotColor = dotColor,
                pressedDotColor = pressedDotColor,
                popupBackgroundColor = popupBackgroundColor,
                popupTextColor = popupTextColor,
            )
        }
}

@Composable
private fun isAlphabetIndexBarDarkTheme(): Boolean =
    RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

/** `coui_touchsearchview_key_text_color_unpressed` (the unpressed state of `coui_touchsearchview_text_color`). */
internal fun alphabetIndexLetter(dark: Boolean): Color = if (dark) Color(0x8CFFFFFF) else Color(0x8C000000)

/** `coui_touchsearchview_dot_color`: `?attr/couiColorControls`. */
internal fun alphabetIndexDot(dark: Boolean): Color = if (dark) Color(0x40FFFFFF) else Color(0x29000000)

/** `coui_touch_search_popup_bg`: `?attr/couiColorBackgroundTop`. */
internal fun alphabetIndexPopupBackground(dark: Boolean): Color = if (dark) Color(0xFF333333) else Color(0xFFFFFFFF)

/** `setPopupWindowTextColor` default: `?attr/couiColorPrimaryNeutral`. */
internal fun alphabetIndexPopupText(dark: Boolean): Color = if (dark) Color(0xE6FFFFFF) else Color(0xE6000000)

/** The key pitch: `coui_touchsearch_each_item_height` + `coui_touchsearch_item_spacing` (16dp). */
internal fun alphabetIndexBarPitchPx(density: Float): Float =
    (AlphabetIndexBarDefaults.KeyHeight + AlphabetIndexBarDefaults.KeySpacing).value * density

/**
 * A limit level of the stock `COUITouchSearchView`: the computed `LetterLimitLevelInfo`
 * (`initHeightRangeSpec()` / `getShowLettersSize()`). Levels 0..5 replace more and more letters with `·` dots;
 * level 0 shows every key and level 6 means nothing fits.
 *
 * @param limitHeightPx height at which this level's layout fits (the stock `limitHeight`).
 */
internal data class AlphabetIndexBarLimitLevel(
    val dotLevel: Int,
    val dotSize: Int,
    val showLetterSize: Int,
    val limitHeightPx: Float,
    val replenishDotValueTheIndex: Int,
)

/** One laid-out key of the stock `COUITouchSearchView.mKey`. */
internal data class AlphabetIndexBarKey(
    val text: String,
    /** Index into the caller's letters for a plain key; `null` for a `·` dot. */
    val letterIndex: Int?,
    /** Letters hidden behind a `·` dot, in order (`mHiddenCharList`). */
    val hiddenLetterIndices: List<Int>,
    val isDot: Boolean,
    val topPx: Float,
    val touchTopPx: Float,
    val touchBottomPx: Float,
)

/** The key list produced by the stock `updateKeys()`; [dotLevel] 6 means the strip is too short for any key. */
internal data class AlphabetIndexBarLayout(
    val keys: List<AlphabetIndexBarKey>,
    val dotLevel: Int,
    val heightNotEnough: Boolean,
)

/** A touch on the strip: the laid-out key, the reported letter and the popup centre. */
internal data class AlphabetIndexBarHit(
    val keyIndex: Int,
    val letterIndex: Int,
    val centerYPx: Float,
)

/**
 * The stock `suffixAdjust` (`initHeightRangeSpec()`): the trailing `#` key of `special_touchsearch_keys` is not
 * counted when fitting all letters (the last key only equals `mStrLastSymbol` when that symbol is empty).
 */
private fun alphabetIndexBarSuffixAdjust(letters: List<String>): Int = if (letters.lastOrNull().isNullOrEmpty()) 0 else 1

/**
 * Stock `getShowLettersSize(count, suffixAdjust, drawableHeight, level)`: the computed level, or `null` when the
 * level cannot show at least 8 keys (`checkLetterLengthSmallLimit`).
 */
private fun alphabetIndexBarLevelInfo(
    level: Int,
    letterCount: Int,
    suffixAdjust: Int,
    drawableHeightPx: Float,
    pitchPx: Float,
): AlphabetIndexBarLimitLevel? {
    val adjusted = letterCount - suffixAdjust
    if (adjusted < 8 && level != 0) return null
    val d3 = (adjusted - 1).toDouble()
    if (level == 0) {
        return AlphabetIndexBarLimitLevel(0, 0, letterCount, pitchPx * letterCount + drawableHeightPx, -1)
    }
    if (level == 1) {
        val i8 = if (d3 % 2.0 == 0.0) 2 else 1
        val show = letterCount - i8
        if (show - suffixAdjust < 8) return null
        val dotSize = ((show - suffixAdjust) - 1) / 2
        return AlphabetIndexBarLimitLevel(1, dotSize, show, pitchPx * show + drawableHeightPx, 1)
    }
    if (level == 2 || level == 3 || level == 4) {
        val d4 = (level + 1).toDouble()
        val i11 = (d3 % d4).toInt()
        val i12 = (d3 / d4).toInt()
        val show = ((letterCount - (i12 * level)) + i12) - i11
        if (show - suffixAdjust < 8) return null
        return AlphabetIndexBarLimitLevel(level, i12, show, pitchPx * show + drawableHeightPx, i11)
    }
    if (level != 5) return null
    val i14 = drawableHeightPx.toInt() + 8
    if (i14 - suffixAdjust < 8) return null
    return AlphabetIndexBarLimitLevel(
        dotLevel = 5,
        dotSize = 6,
        showLetterSize = i14,
        limitHeightPx = pitchPx * i14 + drawableHeightPx,
        replenishDotValueTheIndex = ((d3 - 1.0) % 6.0).toInt(),
    )
}

/**
 * `initHeightRangeSpec()`: level 0..5 in order, stopping at the first level that cannot show 8 keys; if that
 * level is 5 the stock retries level 5 once more and appends it when valid.
 */
internal fun alphabetIndexBarLimitLevels(
    letterCount: Int,
    pitchPx: Float,
    suffixAdjust: Int = if (letterCount > 0) 1 else 0,
    drawableHeightPx: Float = 0f,
): List<AlphabetIndexBarLimitLevel> {
    if (letterCount <= 0) return emptyList()
    val levels = mutableListOf<AlphabetIndexBarLimitLevel>()
    for (level in 0..5) {
        val info = alphabetIndexBarLevelInfo(level, letterCount, suffixAdjust, drawableHeightPx, pitchPx)
        if (info == null) {
            alphabetIndexBarLevelInfo(5, letterCount, suffixAdjust, drawableHeightPx, pitchPx)?.let(levels::add)
            return levels
        }
        levels.add(info)
    }
    return levels
}

/** `getLetterSizeFromDot(level, dotIndex)`: how many letters one `·` dot hides. */
private fun alphabetIndexBarHiddenLetterCount(
    level: AlphabetIndexBarLimitLevel,
    letterCount: Int,
    dotIndex: Int,
): Int {
    val dotSize = level.dotSize
    if (dotSize <= 0) return 1
    val hidden = letterCount - level.showLetterSize
    val total = (hidden + dotSize).toDouble()
    val base = (total / dotSize).toInt()
    val remainder = total % dotSize
    return when {
        remainder == 0.0 -> base
        remainder == 1.0 -> if (dotIndex == dotSize - 1) base + 1 else base
        dotIndex >= dotSize - level.replenishDotValueTheIndex -> base + 1
        else -> base
    }
}

/**
 * `updateKeys()`: when the strip is tall enough every letter is drawn at the stock 16dp pitch; otherwise
 * `getLimitDotLevel(availableHeight)` selects the level, `findLetterLimitLevelInfo(level)` the layout, and letter
 * groups are drawn as `·` dots whose hidden letters back the popup and the reported key.
 */
internal fun alphabetIndexBarLayout(
    heightPx: Float,
    paddingVerticalPx: Float,
    letters: List<String>,
    pitchPx: Float,
    cellHeightPx: Float,
    itemSpacingPx: Float,
    suffixAdjust: Int = alphabetIndexBarSuffixAdjust(letters),
): AlphabetIndexBarLayout {
    if (letters.isEmpty()) return AlphabetIndexBarLayout(emptyList(), 0, false)
    val available = (heightPx - paddingVerticalPx * 2f).coerceAtLeast(0f)
    val levels = alphabetIndexBarLimitLevels(letters.size, pitchPx, suffixAdjust)
    val defaultLimitHeight = levels.firstOrNull()?.limitHeightPx ?: 0f
    val step = cellHeightPx + itemSpacingPx
    if (defaultLimitHeight <= available) {
        var top = paddingVerticalPx
        val keys =
            letters.mapIndexed { index, letter ->
                val key =
                    AlphabetIndexBarKey(
                        text = letter,
                        letterIndex = index,
                        hiddenLetterIndices = emptyList(),
                        isDot = false,
                        topPx = top,
                        touchTopPx = top,
                        touchBottomPx = top + step,
                    )
                top += step
                key
            }
        return AlphabetIndexBarLayout(keys, 0, false)
    }
    val dotLevel = levels.firstOrNull { available >= it.limitHeightPx }?.dotLevel ?: 6
    if (dotLevel == 6) return AlphabetIndexBarLayout(emptyList(), 6, true)
    val info = levels.first { it.dotLevel == dotLevel }
    val keys = mutableListOf<AlphabetIndexBarKey>()
    var top = paddingVerticalPx
    var characterIndex = 0
    var normalCount = 0
    var dotIndex = 0
    while (characterIndex < letters.size) {
        val touchBottom = top + step
        if (normalCount == 0 || dotIndex >= info.dotSize) {
            keys.add(
                AlphabetIndexBarKey(
                    text = letters[characterIndex],
                    letterIndex = characterIndex,
                    hiddenLetterIndices = emptyList(),
                    isDot = false,
                    topPx = top,
                    touchTopPx = top,
                    touchBottomPx = touchBottom,
                ),
            )
            normalCount++
            top += step
        } else {
            val hidden = mutableListOf<Int>()
            val count = alphabetIndexBarHiddenLetterCount(info, letters.size, dotIndex)
            dotIndex++
            normalCount = if (info.dotLevel >= 5) normalCount + 1 else 0
            repeat(count) {
                hidden.add(characterIndex)
                characterIndex++
            }
            if (hidden.isEmpty()) break
            keys.add(
                AlphabetIndexBarKey(
                    text = AlphabetIndexBarDefaults.DotText,
                    letterIndex = null,
                    hiddenLetterIndices = hidden,
                    isDot = true,
                    topPx = top,
                    touchTopPx = top,
                    touchBottomPx = touchBottom,
                ),
            )
            top += step
            characterIndex--
        }
        characterIndex++
    }
    return AlphabetIndexBarLayout(keys, dotLevel, true)
}

/** Stock `getWillDisplayY()` / `getKeyIndicesWithDots()` resolution of one key at [y]. */
private fun alphabetIndexBarResolveHit(
    layout: AlphabetIndexBarLayout,
    keyIndex: Int,
    y: Float,
): AlphabetIndexBarHit {
    val key = layout.keys[keyIndex]
    if (!key.isDot) {
        val letter = key.letterIndex ?: keyIndex.coerceIn(0, layout.keys.lastIndex)
        return AlphabetIndexBarHit(keyIndex, letter, key.topPx + (key.touchBottomPx - key.topPx) / 2f)
    }
    val hidden = key.hiddenLetterIndices
    val cell = ((key.touchBottomPx - key.touchTopPx) / hidden.size).coerceAtLeast(1f)
    val sub = (ceil(((y - key.touchTopPx) / cell).toDouble()).toInt() - 1).coerceIn(0, hidden.lastIndex)
    val centerY = min(key.touchTopPx + cell * (sub + 1), key.touchBottomPx)
    return AlphabetIndexBarHit(keyIndex, hidden[sub], centerY)
}

/** `getKeyIndices()` + `getKeyIndicesWithDots()`: the reported letter at [y]. */
internal fun alphabetIndexBarHitAt(
    layout: AlphabetIndexBarLayout,
    y: Float,
): AlphabetIndexBarHit? {
    val keys = layout.keys
    if (keys.isEmpty()) return null
    if (keys.size == 1 || (keys.size > 1 && keys[1].topPx > y)) return alphabetIndexBarResolveHit(layout, 0, y)
    if (y > keys.last().topPx) return alphabetIndexBarResolveHit(layout, keys.lastIndex, y)
    var hit: AlphabetIndexBarHit? = null
    for ((index, key) in keys.withIndex()) {
        hit = alphabetIndexBarResolveHit(layout, index, y)
        if (y <= key.touchBottomPx) return hit
    }
    return hit
}

/**
 * The ColorOS alphabet fast-scroll bar: `COUITouchSearchView` (`com.coui.appcompat.touchsearchview`), used by the
 * Settings foldable app list (`folded_app_list_layout.xml`, `couiFirstIsCharacter="true"`).
 *
 * The stock view is a 39dp-wide strip of 12sp letters with a 14dp key height and 2dp spacing, 16dp vertical
 * padding and the pressed letter shown in a 48dp bubble (`coui_touch_search_popup_bg`, 20sp). Pressing or
 * dragging reports the key through [onLetterSelected]; a long press reports it through [onLetterLongPressed].
 * When the strip is too short for all letters the stock code replaces letter groups with `·` dots
 * (`LIMIT_DOT_LEVEL*`): `updateKeys()` picks a `LetterLimitLevelInfo`, draws `showLetterSize` rows and hides the
 * remaining letters behind each dot; the reported key and popup still show the hidden letter.
 *
 * @param onLetterSelected Called with the letter when the pressed key changes.
 * @param modifier Applied to the strip; the stock height is 432dp (min 296dp).
 * @param letters The keys (`special_touchsearch_keys`: A-Z plus the trailing `#`).
 * @param onLetterLongPressed Called when a key is held for the long-press timeout.
 * @param popupEnabled Whether to show the pressed-letter bubble.
 * @param colors The strip and popup colours.
 * @param hapticFeedback Whether a key change fires the stock vibration (`performFeedback()`: the adaptive
 *   linear-motor feedback is unportable, so the `performHapticFeedback(308)` path is mapped to
 *   `HapticFeedbackType.SegmentTick`; 302 only serves as the stock fallback).
 */
@OptIn(ExperimentalTime::class)
@Composable
fun AlphabetIndexBar(
    onLetterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    letters: List<String> = AlphabetIndexBarDefaults.Letters,
    onLetterLongPressed: ((String) -> Unit)? = null,
    popupEnabled: Boolean = true,
    colors: AlphabetIndexBarColors = AlphabetIndexBarDefaults.colors(),
    hapticFeedback: Boolean = true,
) {
    val density = LocalDensity.current
    val viewConfiguration = LocalViewConfiguration.current
    val haptics = LocalHapticFeedback.current
    val measurer = rememberTextMeasurer()
    val letterStyle =
        remember {
            TextStyle(
                fontSize = AlphabetIndexBarDefaults.KeyTextSize,
                fontWeight = FontWeight.Medium,
            )
        }
    val dotStyle =
        remember {
            TextStyle(
                fontSize = AlphabetIndexBarDefaults.DotTextSize,
                fontWeight = FontWeight.Medium,
            )
        }
    val paddingPx = with(density) { AlphabetIndexBarDefaults.PaddingVertical.toPx() }
    val pitchPx = alphabetIndexBarPitchPx(density.density)
    val cellHeightPx = with(density) { AlphabetIndexBarDefaults.KeyHeight.toPx() }
    val itemSpacingPx = with(density) { AlphabetIndexBarDefaults.KeySpacing.toPx() }
    var pressedKeyIndex by remember { mutableStateOf<Int?>(null) }
    var pressedLetterIndex by remember { mutableStateOf<Int?>(null) }
    var pressedCenterY by remember { mutableFloatStateOf(0f) }
    var sizePx by remember { mutableFloatStateOf(0f) }

    fun layoutFor(height: Float) =
        alphabetIndexBarLayout(
            heightPx = height,
            paddingVerticalPx = paddingPx,
            letters = letters,
            pitchPx = pitchPx,
            cellHeightPx = cellHeightPx,
            itemSpacingPx = itemSpacingPx,
        )

    fun applyHit(hit: AlphabetIndexBarHit?) {
        if (hit == null) {
            pressedKeyIndex = null
            pressedLetterIndex = null
            return
        }
        pressedKeyIndex = hit.keyIndex
        pressedCenterY = hit.centerYPx
        if (hit.letterIndex != pressedLetterIndex) {
            pressedLetterIndex = hit.letterIndex
            // `invalidateTouchBarText()` -> `performFeedback()`: 308 (mapped), then 302 fallback.
            if (hapticFeedback) haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
            onLetterSelected(letters[hit.letterIndex])
        }
    }

    Box(
        modifier =
            modifier
                .width(AlphabetIndexBarDefaults.BackgroundWidth)
                .defaultMinSize(minHeight = AlphabetIndexBarDefaults.MinHeight)
                .pointerInput(letters, pitchPx, popupEnabled, hapticFeedback) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val height = size.height.toFloat()
                        var current = alphabetIndexBarHitAt(layoutFor(height), down.position.y)
                        applyHit(current)
                        val longPressAt = Clock.System.now().toEpochMilliseconds() + viewConfiguration.longPressTimeoutMillis
                        var longPressFired = false
                        while (true) {
                            val remaining = (longPressAt - Clock.System.now().toEpochMilliseconds()).coerceAtLeast(0L)
                            val event =
                                if (remaining > 0L && !longPressFired) {
                                    withTimeoutOrNull(remaining) { awaitPointerEvent() }
                                } else {
                                    awaitPointerEvent()
                                }
                            if (event == null) {
                                longPressFired = true
                                pressedLetterIndex?.let { onLetterLongPressed?.invoke(letters[it]) }
                                continue
                            }
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) {
                                change.consume()
                                break
                            }
                            change.consume()
                            val hit = alphabetIndexBarHitAt(layoutFor(height), change.position.y)
                            if (hit?.let { it.keyIndex to it.letterIndex } != current?.let { it.keyIndex to it.letterIndex }) {
                                current = hit
                                applyHit(hit)
                            }
                        }
                        pressedKeyIndex = null
                        pressedLetterIndex = null
                    }
                },
    ) {
        Canvas(Modifier.fillMaxSize()) {
            sizePx = size.height
            val layout = layoutFor(size.height)
            for ((index, key) in layout.keys.withIndex()) {
                val isPressed = index == pressedKeyIndex
                val style = if (key.isDot) dotStyle else letterStyle
                val color =
                    when {
                        key.isDot && isPressed -> colors.pressedDotColor
                        key.isDot -> colors.dotColor
                        isPressed -> colors.pressedLetterColor
                        else -> colors.letterColor
                    }
                val layoutResult = measurer.measure(key.text, style.copy(color = color))
                val centerY = key.topPx + (key.touchBottomPx - key.touchTopPx) / 2f
                drawText(
                    textLayoutResult = layoutResult,
                    topLeft = Offset((size.width - layoutResult.size.width) / 2f, centerY - layoutResult.size.height / 2f),
                )
            }
        }
        if (popupEnabled && pressedLetterIndex != null) {
            val popupSizePx = with(density) { AlphabetIndexBarDefaults.PopupSize.toPx() }
            val marginPx = with(density) { AlphabetIndexBarDefaults.PopupMargin.toPx() }
            val offsetX = with(density) { AlphabetIndexBarDefaults.PopupOffset.toPx() }.roundToInt()
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(-(popupSizePx + marginPx + offsetX).roundToInt(), (pressedCenterY - popupSizePx / 2f).roundToInt()),
                properties = PopupProperties(focusable = false, clippingEnabled = false),
                onDismissRequest = {},
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(AlphabetIndexBarDefaults.PopupSize)
                            .clip(CircleShape)
                            .background(colors.popupBackgroundColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = letters[pressedLetterIndex!!],
                        color = colors.popupTextColor,
                        fontSize = AlphabetIndexBarDefaults.PopupTextSize,
                    )
                }
            }
        }
    }
}
