// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import io.github.ra1nify.rmuix.kmp.icon.RmuixIcons
import io.github.ra1nify.rmuix.kmp.icon.basic.Search
import io.github.ra1nify.rmuix.kmp.theme.LocalContentColor
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import io.github.ra1nify.rmuix.kmp.utils.hasFocusReassignBug
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectColor
import io.github.ra1nify.rmuix.kmp.utils.stockStateEffectSpring
import kotlinx.coroutines.delay

/** Viewport of `ic_edit_text_delete_search_view.xml`. */
private const val STOCK_CLEAR_VIEWPORT = 36f

/** Radius of the disc in `ic_edit_text_delete_search_view.xml` (`M18,18m-9,0a9,9 ...`). */
private const val STOCK_CLEAR_DISC_RADIUS = 9f

/** The white cross from `ic_edit_text_delete_search_view.xml`, verbatim. */
private const val STOCK_CLEAR_GLYPH_PATH_DATA =
    "M18,16.868L15.031,13.898L13.899,15.03L16.869,18L13.898,20.97L15.03,22.101L18,19.131L20.97,22.101" +
        "L22.102,20.969L19.132,18L22.101,15.03L20.969,13.899L18,16.868Z"

/** Parsed once: the clear glyph is drawn from the stock path data rather than two lines. */
private val stockClearGlyphPath: Path by lazy {
    PathParser().parsePathString(STOCK_CLEAR_GLYPH_PATH_DATA).toPath()
}

/**
 * A [SearchBar] component with Rmuix style.
 *
 * @param inputField the input field to input a query in the [SearchBar].
 * @param onExpandedChange the callback to be invoked when the [SearchBar]'s expanded state is
 *   changed.
 * @param modifier the [Modifier] to be applied to the [SearchBar].
 * @param insideMargin The margin inside the [SearchBar].
 * @param expanded whether the [SearchBar] is expanded and showing search results.
 * @param outsideEndAction the action to be shown at the end side of the [SearchBar] when it is
 *   expanded.
 * @param content the content to be shown when the [SearchBar] is expanded.
 */
@Composable
fun SearchBar(
    inputField: @Composable () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    insideMargin: DpSize = SearchBarDefaults.InsideMargin,
    expanded: Boolean = false,
    outsideEndAction: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val navigationEventState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(vertical = insideMargin.height, horizontal = insideMargin.width),
            ) {
                inputField()
            }
            AnimatedVisibility(
                visible = expanded,
                // `COUISearchViewAnimate`: the trailing action grows and slides in over
                // `DEFAULT_SEARCH_VIEW_OFFSET_CHANGE_DURATION` (450ms) on `COUIMoveEaseInterpolator`.
                enter =
                    expandHorizontally(animationSpec = SearchBarDefaults.slideSpec()) +
                        slideInHorizontally(
                            animationSpec = SearchBarDefaults.slideSpec(),
                            initialOffsetX = { it },
                        ),
                exit =
                    shrinkHorizontally(animationSpec = SearchBarDefaults.slideSpec()) +
                        slideOutHorizontally(
                            animationSpec = SearchBarDefaults.slideSpec(),
                            targetOffsetX = { it },
                        ),
            ) {
                outsideEndAction?.invoke()
            }
        }

        AnimatedVisibility(
            visible = expanded,
            // `DEFAULT_FADE_DURATION` = 150ms for the results container.
            enter = fadeIn(animationSpec = SearchBarDefaults.fadeSpec()),
            exit = fadeOut(animationSpec = SearchBarDefaults.fadeSpec()),
        ) {
            content()
        }
    }

    NavigationBackHandler(
        state = navigationEventState,
        isBackEnabled = expanded,
        onBackCompleted = {
            currentOnExpandedChange(false)
        },
    )
}

/**
 * A text field to input a query in a search bar with Rmuix style.
 *
 * @param query the query text to be shown in the input field.
 * @param onQueryChange the callback to be invoked when the input service updates the query. An
 *   updated text comes as a parameter of the callback.
 * @param onSearch the callback to be invoked when the input service triggers the
 *   [ImeAction.Search] action. The current [query] comes as a parameter of the callback.
 * @param expanded whether the search bar is expanded and showing search results.
 * @param onExpandedChange the callback to be invoked when the search bar's expanded state is
 *   changed.
 * @param modifier the [Modifier] to be applied to this input field.
 * @param label the label to be shown when the input field is not focused.
 * @param enabled the enabled state of this input field. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param textStyle Style configuration that applies at character level such as color, font etc.
 * @param color the background color of the input field's capsule. Pass [Color.Transparent] when
 *   drawing your own background behind it (e.g. a backdrop blur).
 * @param leadingIcon the leading icon to be displayed at the start of the input field.
 * @param trailingIcon the trailing icon to be displayed at the end of the input field.
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this input field. You can use this to change the search bar's
 *   appearance or preview the search bar in different states. Note that if `null` is provided,
 *   interactions will still happen internally.
 */
@Composable
fun InputField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true,
    textStyle: TextStyle? = null,
    color: Color = searchBarFieldColor(isSearchBarInDarkTheme()),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val currentOnQueryChange by rememberUpdatedState(onQueryChange)
    val currentOnSearch by rememberUpdatedState(onSearch)
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)
    val internalInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val fieldShape = remember { RoundedCornerShape(SearchBarDefaults.InputFieldCornerRadius) }
    // isSearchBarInDarkTheme() is @Composable; the clear-button disc colour is drawn inside a
    // Canvas draw lambda, which is not, so resolve it here.
    val clearIconDark = isSearchBarInDarkTheme()

    val actualLeadingIcon =
        leadingIcon ?: {
            // `coui_search_bar_inner_search_icon_size`: a 36dp box starting
            // `coui_search_bar_inner_search_icon_start_gap` (4dp) from the field edge, with the 20dp
            // magnifier centred inside. That puts its visible edge at 4 + (36 - 20) / 2 = 12dp
            // (`coui_search_view_search_icon_margin`) and its centre at 22dp.
            Box(
                modifier =
                    Modifier
                        .padding(start = SearchBarStockIconStartGap)
                        .size(SearchBarStockIconBoxSize),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(SearchBarDefaults.IconSize),
                    imageVector = RmuixIcons.Basic.Search,
                    tint = searchBarIconColor(isSearchBarInDarkTheme()),
                    contentDescription = "Search",
                )
            }
        }

    val actualTrailingIcon =
        trailingIcon ?: {
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                // `coui_search_view_close_button_selector`: 180ms in, 360ms out.
                enter = fadeIn(animationSpec = tween(SearchBarStockClearEnterFadeMillis)),
                exit = fadeOut(animationSpec = tween(SearchBarStockClearExitFadeMillis)),
            ) {
                Box(
                    modifier =
                        Modifier.padding(
                            start = SearchBarDefaults.TrailingIconStartPadding,
                            end = SearchBarDefaults.TrailingIconEndPadding,
                        ),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    ClearTextButton(
                        onClick = { currentOnQueryChange("") },
                        contentDescription = "Search Cleanup",
                    )
                }
            }
        }

    val focused = internalInteractionSource.collectIsFocusedAsState().value
    // `COUISearchViewAnimate.playPressAnimation()` cross-fades the field background from the normal
    // colour to `coui_search_view_selector_color_pressed` (`#1E000000`) over
    // `DEFAULT_PRESS_FEEDBACK_DURATION` (150ms) with `COUIEaseInterpolator` (0.33, 0, 0.67, 1); the
    // release animation uses the same 150ms. `BasicTextField` already publishes press interactions on
    // this source, so no extra gesture detector is needed.
    val isPressed = internalInteractionSource.collectIsPressedAsState().value
    val fieldBackground by
        animateColorAsState(
            targetValue =
                if (isPressed) {
                    searchBarFieldPressedColor(isSearchBarInDarkTheme()).compositeOver(color)
                } else {
                    color
                },
            animationSpec = tween(durationMillis = SEARCH_ANIMATE_PRESS_MILLIS, easing = SearchBarDefaults.Ease),
            label = "searchFieldPress",
        )
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val textAlpha = remember { Animatable(1f) }

    val textColor = LocalContentColor.current
    val inputTextStyle =
        RmuixTheme.textStyles.main
            .copy(fontWeight = FontWeight.Medium)
            .merge(textStyle)
            .copy(color = textColor)

    val cursorBrush = SolidColor(RmuixTheme.colorScheme.primary)
    val labelText by remember(query, expanded, label) {
        derivedStateOf { if (!(query.isNotEmpty() || expanded)) label else "" }
    }

    // On API 26-27, focus is incorrectly reassigned after clearFocus(), preventing the
    // SearchBar from closing. Workaround: disable the TextField when collapsed and use
    // pointerInput to handle tap-to-expand. https://issuetracker.google.com/issues/433382598
    val workaroundEnabled = !hasFocusReassignBug || expanded
    val expandOnTapModifier =
        if (workaroundEnabled || !enabled) {
            Modifier
        } else {
            Modifier.pointerInput(Unit) { detectTapGestures { currentOnExpandedChange(true) } }
        }

    BasicTextField(
        value = query,
        onValueChange = currentOnQueryChange,
        modifier =
            modifier
                .then(expandOnTapModifier)
                .focusRequester(focusRequester)
                .onFocusChanged { if (it.isFocused) currentOnExpandedChange(true) }
                .semantics {
                    onClick {
                        focusRequester.requestFocus()
                        true
                    }
                },
        enabled = enabled && workaroundEnabled,
        singleLine = true,
        textStyle = inputTextStyle,
        cursorBrush = cursorBrush,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { currentOnSearch(query) }),
        interactionSource = internalInteractionSource,
        decorationBox = { innerTextField ->
            Box(
                modifier =
                    Modifier
                        .background(
                            color = fieldBackground,
                            shape = fieldShape,
                        ),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    actualLeadingIcon()
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .heightIn(min = SearchBarDefaults.InputFieldMinHeight),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        val baseLabelStyle = RmuixTheme.textStyles.main
                        val mergedLabelStyle =
                            remember(baseLabelStyle, textStyle) {
                                baseLabelStyle
                                    .copy(
                                        fontSize = SearchBarDefaults.InputFieldFontSize,
                                        fontWeight = FontWeight.Medium,
                                    ).merge(textStyle)
                            }
                        Text(
                            text = labelText,
                            style = mergedLabelStyle,
                            // `coui_search_view_text_hint_size`: the placeholder is smaller than
                            // the input text.
                            fontSize =
                                if (labelText.isNotEmpty()) {
                                    SearchBarDefaults.InputFieldHintFontSize
                                } else {
                                    SearchBarDefaults.InputFieldFontSize
                                },
                            color = RmuixTheme.colorScheme.onSurfaceContainerHigh,
                        )
                        Box(modifier = Modifier.graphicsLayer { alpha = textAlpha.value }) {
                            innerTextField()
                        }
                    }
                    actualTrailingIcon()
                }
            }
        },
    )

    LaunchedEffect(expanded) {
        if (expanded) {
            // Explicitly request focus when expanded. On API 26-27, the workaround disables
            // the TextField when collapsed, so the initial tap doesn't grant focus — this
            // ensures the keyboard appears after the TextField becomes enabled again.
            focusRequester.requestFocus()
        } else if (focused) {
            delay(100)
            if (query.isNotEmpty()) {
                textAlpha.animateTo(0f)
                currentOnQueryChange("")
                textAlpha.snapTo(1f)
            }
            focusManager.clearFocus()
        }
    }
}

/** Contains default values used by [SearchBar] and [InputField]. */
object SearchBarDefaults {
    /** The default inside margin of the [SearchBar]. */
    val InsideMargin = DpSize(12.dp, 0.dp)

    /** `DEFAULT_SEARCH_VIEW_OFFSET_CHANGE_DURATION` = 450ms on `COUIMoveEaseInterpolator`. */
    val SlideMillis = SEARCH_ANIMATE_OFFSET_MILLIS

    /** `DEFAULT_FADE_DURATION` = 150ms. */
    val FadeMillis = SEARCH_ANIMATE_FADE_MILLIS

    /** `DEFAULT_BUTTON_ALPHA_CHANGE_DURATION` = 350ms. */
    val ButtonAlphaMillis = SEARCH_ANIMATE_BUTTON_ALPHA_MILLIS

    /** `NON_INSTANT_SEARCH_BUTTON_ALPHA_CHANGE_DURATION` = 100ms. */
    val ButtonAlphaInstantMillis = SEARCH_ANIMATE_BUTTON_ALPHA_INSTANT_MILLIS

    /** `COUIMoveEaseInterpolator` = `cubic-bezier(0.3, 0, 0.1, 1)`. */
    val MoveEase = SearchMoveEaseInterpolator

    /** `COUIEaseInterpolator` = `cubic-bezier(0.33, 0, 0.67, 1)`. */
    val Ease = SearchEaseInterpolator

    /** The expand / collapse spec of the trailing action and the results container. */
    fun <T> slideSpec(): TweenSpec<T> = tween(durationMillis = SlideMillis, easing = MoveEase)

    /** The fade spec of the results container (`DEFAULT_FADE_DURATION`). */
    fun <T> fadeSpec(): TweenSpec<T> = tween(durationMillis = FadeMillis, easing = MoveEase)

    /** The default minimum height of the [InputField] (`coui_search_bar_normal_background_height`). */
    val InputFieldMinHeight = SearchBarStockFieldHeight

    /** The corner radius of the field (`coui_search_view_corner` = 18dp). */
    val InputFieldCornerRadius = SearchBarStockFieldCornerRadius

    /** The default font size for the [InputField] label (`coui_search_view_input_text_size`). */
    val InputFieldFontSize = SearchBarStockTextSize

    /** The font size of the placeholder (`coui_search_view_text_hint_size`). */
    val InputFieldHintFontSize = SearchBarStockHintTextSize

    /** The default leading search icon size (`coui_search_view_icon_size`). */
    val IconSize = SearchBarStockIconSize

    /** The start margin of the search icon (`coui_search_view_search_icon_margin`). */
    val IconStartMargin = SearchBarStockIconMargin

    /** The horizontal margin the bar keeps on both sides (live tokens: `coui_search_view_padding_start_in_toolbar` / `_end_in_toolbar`, both 16dp). */
    val SideMargin = SearchBarStockSideMargin

    /**
     * The magnifier's visible start margin (`coui_search_view_search_icon_margin`, 12dp). The stock
     * layout gets it from the `coui_search_bar_inner_search_icon_size` 36dp box, which starts
     * `coui_search_bar_inner_search_icon_start_gap` (4dp) from the field edge: 4dp + (36 - 20) / 2 =
     * 12dp. The layout applies the 4dp gap and the 36dp box, so this value documents the result.
     */
    val LeadingIconStartPadding = 12.dp

    /**
     * Retained for source compatibility. The default leading icon now sits in the stock 36dp icon box
     * (`coui_search_bar_inner_search_icon_size`) instead of using a trailing inset, so this value no
     * longer affects the layout.
     */
    val LeadingIconEndPadding = 8.dp

    /** The start padding for the default trailing icon. */
    val TrailingIconStartPadding = 8.dp

    /** The end padding for the default trailing icon. */
    val TrailingIconEndPadding = 16.dp
}
