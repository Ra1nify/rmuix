// Copyright 2025, compose-rmuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

package io.github.ra1nify.rmuix.kmp.basic

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import io.github.ra1nify.rmuix.kmp.anim.couiSpring
import io.github.ra1nify.rmuix.kmp.theme.RmuixTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * A [Switch] component with ColorOS 17 styled visuals.
 *
 * Existing call sites remain source-compatible. Default colors follow the stock COUI theme.
 *
 * Geometry and colours come from the ColorOS `COUISwitch` implementation:
 *
 * | token | value |
 * | :--- | ---: |
 * | track | 44 x 24 dp |
 * | thumb | 18 dp |
 * | track inset | 3 dp |
 * | travel | 20 dp |
 *
 * The thumb carries a soft paint-style shadow (`setShadowLayer(8px, 0, 4px, 9.8% black)` in the original,
 * translated to a Compose [dropShadow]) while enabled.
 *
 * @param checked The checked state of the [Switch].
 * @param onCheckedChange The callback to be called when the state of the [Switch] changes.
 * @param modifier The modifier to be applied to the [Switch].
 * @param colors The [SwitchColors] of the [Switch].
 * @param enabled Whether the [Switch] is enabled.
 * @param loading Whether the switch shows the stock loading spinner instead of the thumb; taps
 *   are ignored while it runs (mirrors `COUISwitch.setLoading()`).
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: SwitchColors = SwitchDefaults.switchColors(),
    enabled: Boolean = true,
    // Appended last so every current call site keeps compiling. Mirrors COUISwitch.setLoading().
    loading: Boolean = false,
) {
    val currentOnCheckedChange by rememberUpdatedState(onCheckedChange)
    val currentChecked by rememberUpdatedState(checked)
    val currentEnabled by rememberUpdatedState(enabled)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val density = LocalDensity.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val hapticFeedback = LocalHapticFeedback.current
    val currentHapticFeedback by rememberUpdatedState(hapticFeedback)
    val animationScope = rememberCoroutineScope()
    val motion =
        remember(density, isRtl, animationScope) {
            val travelPixels =
                with(density) {
                    SwitchDefaults.Width.roundToPx() - SwitchDefaults.ThumbPadding.roundToPx() * 2 - SwitchDefaults.ThumbSize.roundToPx()
                }
            SwitchMotionState(checked, travelPixels.toFloat(), isRtl, animationScope)
        }
    val hasCallback = onCheckedChange != null

    DisposableEffect(motion) {
        onDispose { motion.dispose() }
    }

    LaunchedEffect(checked, enabled, hasCallback, motion, motion.phase) {
        motion.synchronize(checked, enabled && hasCallback)
    }

    val capsuleShape = CircleShape

    val thumbColor = if (checked) colors.checkedThumbColor(enabled) else colors.uncheckedThumbColor(enabled)

    val isDark = isSwitchInDarkTheme()
    val maskSpring = remember { spring<Float>(dampingRatio = 1f, stiffness = ((2 * PI / 0.3) * (2 * PI / 0.3)).toFloat()) }
    val pressProgress =
        animateFloatAsState(
            if (enabled && hasCallback &&
                (isPressed || motion.phase == SwitchMotionPhase.Drag)
            ) {
                1f
            } else {
                0f
            },
            maskSpring,
        )
    val hoverProgress = animateFloatAsState(if (enabled && hasCallback && isHovered) 1f else 0f, maskSpring)
    // `COUISwitch.initStartLoadingAnimator()`: the thumb collapses over 433ms while the spinner fades in
    // (0 -> 1) and grows (0.5 -> 1) over 550ms; it then rotates 360 degrees every 800ms.
    val thumbCollapse =
        animateFloatAsState(
            targetValue = if (loading) 0f else 1f,
            animationSpec = tween(durationMillis = SwitchDefaults.LoadingShrinkMillis, easing = LinearEasing),
            label = "switchLoadingThumb",
        )
    val loadingAlpha =
        animateFloatAsState(
            targetValue = if (loading) 1f else 0f,
            animationSpec = tween(durationMillis = SwitchDefaults.LoadingFadeMillis, easing = LinearEasing),
            label = "switchLoadingAlpha",
        )
    val loadingScale =
        animateFloatAsState(
            targetValue = if (loading) 1f else SwitchDefaults.LoadingScaleStart,
            animationSpec = tween(durationMillis = SwitchDefaults.LoadingFadeMillis, easing = LinearEasing),
            label = "switchLoadingScale",
        )
    val infiniteTransition = rememberInfiniteTransition(label = "switchLoading")
    val loadingRotation =
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(SwitchDefaults.LoadingSpinMillis, easing = LinearEasing)),
            label = "switchLoadingRotation",
        )
    val loadingPath = remember { PathParser().parsePathString(SwitchDefaults.LOADING_PATH_DATA).toPath() }
    val pressColor = if (isDark) Color(0x33FFFFFF) else Color(0x1F000000)
    val hoverColor = if (isDark) Color(0x26FFFFFF) else Color(0x14000000)
    val toggleableModifier =
        if (hasCallback) {
            remember(checked, enabled, loading, interactionSource, motion) {
                Modifier.toggleable(
                    value = checked,
                    onValueChange = { v ->
                        if (motion.phase != SwitchMotionPhase.Attach) {
                            currentOnCheckedChange?.invoke(v)
                            currentHapticFeedback.performHapticFeedback(
                                if (v) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff,
                            )
                        }
                    },
                    enabled = enabled && !loading,
                    role = Role.Switch,
                    interactionSource = interactionSource,
                    indication = null,
                )
            }
        } else {
            Modifier.semantics {
                role = Role.Switch
                toggleableState = ToggleableState(checked)
                if (!enabled) disabled()
            }
        }

    val draggableModifier =
        if (enabled && hasCallback) {
            Modifier.pointerInput(motion) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    if (down.isConsumed) return@awaitEachGesture
                    if (motion.phase == SwitchMotionPhase.Attach) {
                        down.consume()
                        return@awaitEachGesture
                    }
                    if (!motion.canStartDrag) return@awaitEachGesture
                    val tracker = VelocityTracker()
                    tracker.addPosition(down.uptimeMillis, down.position)
                    var dragging = false
                    try {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (change.isConsumed) break
                            if (!change.pressed) {
                                if (dragging) change.consume()
                                break
                            }
                            val displacement = change.position - down.position
                            if (!dragging && displacement.getDistance() > viewConfiguration.touchSlop) {
                                dragging = motion.beginDrag()
                            }
                            if (dragging) {
                                tracker.addPosition(change.uptimeMillis, change.position)
                                motion.dragTo(displacement)
                                change.consume()
                            }
                        }
                    } finally {
                        if (dragging) {
                            motion.attach(tracker.calculateVelocity(Velocity(12000f, 12000f))) { target ->
                                if (currentEnabled && currentOnCheckedChange != null && target != currentChecked) {
                                    currentOnCheckedChange?.invoke(target)
                                    currentHapticFeedback.performHapticFeedback(
                                        if (target) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Modifier
        }

    Box(
        modifier =
            modifier
                .wrapContentSize(Alignment.Center)
                .size(SwitchDefaults.Width, SwitchDefaults.Height)
                .clip(capsuleShape)
                .drawBehind {
                    val progress = motion.progress
                    val trackColor = colors.trackColor(enabled, checked, progress)
                    drawRect(
                        if (enabled) {
                            switchStateMaskColor(pressColor, pressProgress.value).compositeOver(
                                switchStateMaskColor(hoverColor, hoverProgress.value).compositeOver(trackColor),
                            )
                        } else {
                            trackColor
                        },
                    )
                }.hoverable(interactionSource, enabled = enabled && hasCallback)
                .then(toggleableModifier)
                .then(draggableModifier),
    ) {
        val thumbWidth = SwitchDefaults.ThumbSize * if (motion.phase == SwitchMotionPhase.Toggle) switchThumbScaleX(motion.progress) else 1f
        val thumbShadow =
            if (enabled) {
                remember(density) {
                    Shadow(
                        radius = with(density) { 8f.toDp() },
                        color = Color.Black,
                        alpha = 25f / 255f,
                        offset = DpOffset(0.dp, with(density) { 4f.toDp() }),
                    )
                }
            } else {
                null
            }
        Box(
            modifier =
                Modifier
                    .size(thumbWidth, SwitchDefaults.ThumbSize)
                    .align(AbsoluteAlignment.CenterLeft)
                    .absoluteOffset {
                        IntOffset(SwitchDefaults.ThumbPadding.roundToPx() + motion.physicalTranslation.toInt(), 0)
                    }.then(
                        if (thumbShadow != null) {
                            Modifier.dropShadow(shape = CircleShape, shadow = thumbShadow)
                        } else {
                            Modifier
                        },
                    ).graphicsLayer {
                        // `circleScale` animates 1 -> 0 over 433ms while the spinner takes over.
                        scaleX = thumbCollapse.value
                        scaleY = thumbCollapse.value
                        transformOrigin = TransformOrigin.Center
                    }.drawBehind {
                        drawRoundRect(
                            color = thumbColor,
                            cornerRadius = CornerRadius(size.height / 2f),
                        )
                    },
        )
        // The spinner replaces the collapsed thumb: official `coui_icon_loading` (a 270-degree open
        // circle, stroke 1.4 in a 24-unit viewport) drawn at the switch's own loading size.
        val loadingGlyphColor = RmuixTheme.colorScheme.onSurfaceVariantSummary
        Canvas(modifier = Modifier.matchParentSize()) {
            val alpha = loadingAlpha.value
            if (alpha <= 0.001f) return@Canvas
            val glyphScale = SwitchDefaults.LoadingSize.toPx() / 24f * loadingScale.value
            val center =
                Offset(
                    x = SwitchDefaults.ThumbPadding.toPx() + SwitchDefaults.ThumbSize.toPx() / 2f + motion.physicalTranslation,
                    y = size.height / 2f,
                )
            withTransform({
                rotate(loadingRotation.value, pivot = center)
                translate(center.x - 12f * glyphScale, center.y - 12f * glyphScale)
                scale(glyphScale, glyphScale, pivot = Offset.Zero)
            }) {
                drawPath(
                    path = loadingPath,
                    color = loadingGlyphColor.copy(alpha = alpha),
                    style = Stroke(width = 1.4f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
            }
        }
    }
}

object SwitchDefaults {
    /**
     * Track width: ColorOS `bar_width` = 44dp.
     */
    val Width: Dp = 44.dp

    /**
     * Track height: ColorOS `bar_height` = 24dp.
     */
    val Height: Dp = 24.dp

    /**
     * Thumb diameter: ColorOS `outer_circle_width` = 18dp.
     */
    val ThumbSize: Dp = 18.dp

    /**
     * Inset between the thumb and the track edge: ColorOS `circle_padding` = 3dp.
     *
     * Horisontal and vertical insets coincide: `(24 - 18) / 2 = 3`.
     */
    val ThumbPadding: Dp = 3.dp

    /**
     * Thumb travel distance, derived exactly as ColorOS does:
     * `max(barWidth - circlePadding * 2 - outerCircleWidth, 0)` = `44 - 6 - 18` = 20dp.
     */
    val Travel: Dp = Width - ThumbPadding * 2 - ThumbSize

    /**
     * Diameter of the loading spinner: the 18dp thumb circle.
     *
     * The stock `mLoadingDrawable` is `switch_loading.png` (40x40px at xxhdpi, intrinsic 13.33dp), but
     * `COUISwitch.drawLoading()` calls `setBounds(mOuterCircleRectF)` where `outerCircleWidth` = 18dp,
     * so the bitmap is stretched to the thumb circle (times `mLoadingScale`, 0.5 -> 1). The vector glyph
     * is therefore drawn at the same 18dp bounds.
     */
    val LoadingSize: Dp = ThumbSize

    /** `circleScale` 1 -> 0: `COUISwitch.initStartLoadingAnimator()` uses 433ms. */
    val LoadingShrinkMillis: Int = 433

    /** `loadingAlpha` / `loadingScale` fade: 550ms in the same animator. */
    val LoadingFadeMillis: Int = 550

    /** `loadingRotation` 0 -> 360 repeats every 800ms. */
    val LoadingSpinMillis: Int = 800

    /** `loadingScale` starts at 0.5 and grows to 1. */
    val LoadingScaleStart: Float = 0.5f

    /**
     * The spinner outline: `coui_icon_loading.xml` - a 270-degree open circle, three quarter arcs from
     * (21.5, 12) round to (12, 21.5), stroked at 1.4 in the 24-unit viewport.
     */
    val LOADING_PATH_DATA: String =
        "M21.5,12C21.5,6.753 17.247,2.5 12,2.5C6.753,2.5 2.5,6.753 2.5,12C2.5,17.247 6.753,21.5 12,21.5"

    /** Resting x offset of the thumb when off. */
    val ThumbOffsetUnchecked: Dp = ThumbPadding

    /** Resting x offset of the thumb when on. */
    val ThumbOffsetChecked: Dp = ThumbPadding + Travel

    /**
     * Thumb shadow, matching ColorOS `setShadowLayer(8f, 0f, 4f, argb(25, 0, 0, 0))`.
     *
     * The original works in raw pixels (8px radius, 4px downward offset at density 3.125), i.e. roughly
     * 2.56dp / 1.28dp. Compose's [Shadow] takes dp and accepts an offset, so the dy is preserved at 1.3dp.
     */
    val thumbShadow: Shadow =
        Shadow(
            radius = 2.5.dp,
            color = Color.Black,
            alpha = 0.098f,
            offset = DpOffset(0.dp, 1.3.dp),
        )

    /**
     * The default colors for the [Switch].
     *
     * The checked track follows the Rmuix primary colour, which is the same relationship ColorOS has
     * between its switch track and `couiColorPrimary` (the app accent). Neutral colours reproduce the
     * stock COUI pairs: `#29000000` / `#40FFFFFF` for the unchecked track and `#8AFFFFFF` /
     * `#29FFFFFF` for a disabled thumb in light / dark.
     */
    @Composable
    fun switchColors(
        checkedThumbColor: Color = Color.White,
        uncheckedThumbColor: Color = Color.White,
        disabledCheckedThumbColor: Color =
            if (isSwitchInDarkTheme()) {
                Color(0x29FFFFFF)
            } else {
                Color(0x8AFFFFFF)
            },
        disabledUncheckedThumbColor: Color =
            if (isSwitchInDarkTheme()) {
                Color(0x29FFFFFF)
            } else {
                Color(0x8AFFFFFF)
            },
        checkedTrackColor: Color = RmuixTheme.colorScheme.primary,
        uncheckedTrackColor: Color =
            if (isSwitchInDarkTheme()) {
                Color(0x40FFFFFF)
            } else {
                Color(0x29000000)
            },
        disabledCheckedTrackColor: Color = Color(RmuixTheme.colorScheme.primary.toArgb() and 0x4DFFFFFF),
        disabledUncheckedTrackColor: Color =
            if (isSwitchInDarkTheme()) {
                Color(0x26FFFFFF)
            } else {
                Color(0x14000000)
            },
    ): SwitchColors =
        remember(
            checkedThumbColor,
            uncheckedThumbColor,
            disabledCheckedThumbColor,
            disabledUncheckedThumbColor,
            checkedTrackColor,
            uncheckedTrackColor,
            disabledCheckedTrackColor,
            disabledUncheckedTrackColor,
        ) {
            SwitchColors(
                checkedThumbColor = checkedThumbColor,
                uncheckedThumbColor = uncheckedThumbColor,
                disabledCheckedThumbColor = disabledCheckedThumbColor,
                disabledUncheckedThumbColor = disabledUncheckedThumbColor,
                checkedTrackColor = checkedTrackColor,
                uncheckedTrackColor = uncheckedTrackColor,
                disabledCheckedTrackColor = disabledCheckedTrackColor,
                disabledUncheckedTrackColor = disabledUncheckedTrackColor,
            )
        }
}

@Immutable
data class SwitchColors(
    private val checkedThumbColor: Color,
    private val uncheckedThumbColor: Color,
    private val disabledCheckedThumbColor: Color,
    private val disabledUncheckedThumbColor: Color,
    private val checkedTrackColor: Color,
    private val uncheckedTrackColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledUncheckedTrackColor: Color,
) {
    @Stable
    internal fun checkedThumbColor(enabled: Boolean): Color = if (enabled) checkedThumbColor else disabledCheckedThumbColor

    @Stable
    internal fun uncheckedThumbColor(enabled: Boolean): Color = if (enabled) uncheckedThumbColor else disabledUncheckedThumbColor

    @Stable
    internal fun checkedTrackColor(enabled: Boolean): Color = if (enabled) checkedTrackColor else disabledCheckedTrackColor

    @Stable
    internal fun uncheckedTrackColor(enabled: Boolean): Color = if (enabled) uncheckedTrackColor else disabledUncheckedTrackColor

    @Stable
    internal fun trackColor(
        enabled: Boolean,
        checked: Boolean,
        progress: Float,
    ): Color =
        if (enabled) {
            switchTrackColor(uncheckedTrackColor, checkedTrackColor, progress)
        } else if (checked) {
            disabledCheckedTrackColor
        } else {
            disabledUncheckedTrackColor
        }
}

@Composable
private fun isSwitchInDarkTheme(): Boolean = RmuixTheme.colorScheme.background.luminance() < RmuixTheme.colorScheme.onBackground.luminance()

internal enum class SwitchMotionPhase {
    Idle,
    Toggle,
    Drag,
    Attach,
}

@Stable
internal class SwitchMotionState(
    initialChecked: Boolean,
    private val travelPixels: Float,
    private val isRtl: Boolean,
    private val scope: CoroutineScope,
) {
    private val position = Animatable(if (initialChecked) travelPixels else 0f)
    private var animationJob: Job? = null
    private var modelChecked = initialChecked
    private var dragOrigin = 0f
    private var maxDragDistance = 0f

    var phase by mutableStateOf(SwitchMotionPhase.Idle)
        private set

    val canStartDrag: Boolean
        get() = phase == SwitchMotionPhase.Idle && !position.isRunning

    val physicalTranslation: Float
        get() = if (isRtl) travelPixels - position.value else position.value

    val progress: Float
        get() {
            if (travelPixels <= 0f) return 0f
            if (phase == SwitchMotionPhase.Toggle) return position.value / travelPixels
            val physicalProgress = physicalTranslation.toInt() / travelPixels
            return if (isRtl) 1f - physicalProgress else physicalProgress
        }

    fun synchronize(
        checked: Boolean,
        interactive: Boolean,
    ) {
        val changed = modelChecked != checked
        modelChecked = checked
        if (changed || (!interactive && (phase == SwitchMotionPhase.Drag || phase == SwitchMotionPhase.Attach))) {
            animateTo(if (checked) travelPixels else 0f, SwitchMotionPhase.Toggle)
        } else if (phase == SwitchMotionPhase.Idle && position.value != if (checked) travelPixels else 0f) {
            animateTo(if (checked) travelPixels else 0f, SwitchMotionPhase.Toggle)
        }
    }

    fun beginDrag(): Boolean {
        if (!canStartDrag) return false
        dragOrigin = position.value
        maxDragDistance = 0f
        phase = SwitchMotionPhase.Drag
        return true
    }

    fun dispose() {
        animationJob?.cancel()
        animationJob = null
    }

    fun dragTo(displacement: Offset) {
        if (phase != SwitchMotionPhase.Drag) return
        maxDragDistance = max(maxDragDistance, max(abs(displacement.x), abs(displacement.y)))
        val target = switchDragPosition(dragOrigin, displacement.x, travelPixels, isRtl)
        if (target == position.targetValue) return
        if (abs(target - position.value) < 1f && !position.isRunning) return
        animateTo(target, SwitchMotionPhase.Drag)
    }

    fun attach(
        velocity: Velocity,
        onAttached: (Boolean) -> Unit,
    ) {
        if (phase != SwitchMotionPhase.Drag) return
        val logicalVelocity = if (isRtl) -velocity.x else velocity.x
        val target =
            switchTargetChecked(
                progress = if (travelPixels > 0f) position.value / travelPixels else 0f,
                velocity = logicalVelocity,
                distancePixels = maxDragDistance,
                isRtl = isRtl,
                verticalVelocity = velocity.y,
            )
        val targetPosition = if (target) travelPixels else 0f
        animateTo(
            target = targetPosition,
            nextPhase = SwitchMotionPhase.Attach,
            initialVelocity = if (abs(targetPosition - position.value) < 1f) 0f else switchAttachmentVelocity(logicalVelocity),
            onFinished = { onAttached(target) },
        )
    }

    private fun animateTo(
        target: Float,
        nextPhase: SwitchMotionPhase,
        initialVelocity: Float = position.velocity,
        onFinished: (() -> Unit)? = null,
    ) {
        animationJob?.cancel()
        phase = nextPhase
        animationJob =
            scope.launch {
                position.animateTo(
                    targetValue = target,
                    animationSpec = switchMotionSpring(nextPhase),
                    initialVelocity = initialVelocity,
                )
                animationJob = null
                if (nextPhase != SwitchMotionPhase.Drag) {
                    phase = SwitchMotionPhase.Idle
                    onFinished?.invoke()
                }
            }
    }
}

internal fun switchMotionSpring(phase: SwitchMotionPhase): SpringSpec<Float> {
    // `COUISwitchParams`: `toggleSpring = SpringData(bounce 0.3, response 0.4)`, `responseDrag = 0.3`,
    // `bounceDrag = 0` (from the emit constructor defaults), `responseAttach = 0.4`,
    // `bounceAttach = 0.2`. `applyToggleSpringParams` copies the raw bounce into
    // `COUISpringForce.setBounce`, whose damping is `1 - bounce`, so toggle/attach land on 0.7 / 0.8
    // (`SpringData.getDampingRatio()` would halve them, but no Settings class calls it).
    val (response, bounce) =
        when (phase) {
            SwitchMotionPhase.Drag -> 0.3f to 0f
            SwitchMotionPhase.Attach -> 0.4f to 0.2f
            else -> 0.4f to 0.3f
        }
    return couiSpring(
        response = response,
        bounce = bounce,
        visibilityThreshold = if (phase == SwitchMotionPhase.Toggle) 0.075f else 0.75f,
    )
}

internal fun switchThumbScaleX(progress: Float): Float =
    if (progress < 0.34725848f) {
        1f + progress / 0.34725848f * 0.29999995f
    } else {
        1.3f - (progress - 0.34725848f) / 0.65274155f * 0.29999995f
    }

internal fun switchDragPosition(
    origin: Float,
    displacementPixels: Float,
    travelPixels: Float,
    isRtl: Boolean,
): Float {
    val direction = if (isRtl) -1f else 1f
    return (origin + displacementPixels * direction).coerceIn(0f, travelPixels)
}

internal fun switchAttachmentVelocity(velocity: Float): Float = velocity.coerceIn(-1000f, 1000f)

internal fun switchTargetChecked(
    progress: Float,
    velocity: Float,
    distancePixels: Float,
    isRtl: Boolean,
    verticalVelocity: Float = 0f,
): Boolean =
    when {
        abs(distancePixels) >= 4f && abs(velocity) >= 1000f -> velocity > 0f
        abs(distancePixels) >= 4f && abs(verticalVelocity) >= 1000f -> !isRtl
        progress == 0.5f -> isRtl
        else -> progress > 0.5f
    }

internal fun switchStateMaskColor(
    color: Color,
    progress: Float,
): Color {
    val fraction = progress.coerceIn(0f, 1f)
    val argb = color.toArgb()

    // `COUIMaskEffectDrawable` -> `StateEffectAnimator.setProgress()` -> Android `ArgbEvaluator`:
    // encoded channels are interpolated linearly from transparent to `color` (no gamma correction).
    fun channel(shift: Int): Int = ((argb ushr shift and 0xFF) * fraction + 0.5f).toInt().coerceIn(0, 255)
    return Color(
        (channel(24) shl 24) or (channel(16) shl 16) or (channel(8) shl 8) or channel(0),
    )
}

internal fun switchTrackColor(
    uncheckedColor: Color,
    checkedColor: Color,
    progress: Float,
): Color {
    val fraction = progress.coerceIn(0f, 1f)
    val uncheckedArgb = uncheckedColor.toArgb()
    val checkedArgb = checkedColor.toArgb()

    fun channel(shift: Int): Int {
        val start = uncheckedArgb ushr shift and 0xFF
        val end = checkedArgb ushr shift and 0xFF
        return ((1f - fraction) * start + fraction * end).toInt()
    }
    return Color((channel(24) shl 24) or (channel(16) shl 16) or (channel(8) shl 8) or channel(0))
}
