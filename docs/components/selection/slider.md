# Slider

`Slider` is a basic interactive component in Rmuix used for selecting values within a continuous range. Users can adjust values by dragging the slider, making it suitable for scenarios such as volume adjustment, brightness control, and progress display.

Rmuix also provides `VerticalSlider` for vertical orientation, `RangeSlider` for selecting a range of values, and
`VerticalSeekBar` for the ColorOS stock vertical seek bar (`COUIVerticalSeekBar`).

<div style="position: relative; height: 200px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=slider" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-11)

This pass covers the continuous horizontal Slider, using the Settings APK's COUISeekBarCore, HorizontalOrientationHandler, StandardAnimationHandler and color selectors. It is not a complete 1:1 acceptance result.

- The background grows from 20dp to 28dp (1.4x); the progress track remains 20dp high.
- Thumb centers keep a fixed 14dp endpoint inset. Background edges grow from a resting 4dp inset to 0dp while pressed. Touch mapping and artwork share the same fixed travel.
- The background spring uses response=0.5, bounce=0. A short press reaches half enlargement before shrinking. Thumb radius is 6dp → 8dp, with press/release bounce=0.3/0 and response=0.5.
- Neutral colors follow the current Rmuix theme, not the system theme. Enabled and disabled backgrounds both use #1F000000 / #33FFFFFF; disabled thumbs use #8AFFFFFF / #29FFFFFF. The default disabled foreground maps primary to 0x4D alpha; pass the host's independent couiColorDisable role through the existing color parameter when needed.
- Disabled horizontal sliders expose no writable accessibility progress action. Enabled actions apply the current steps/snapping configuration and invoke the completion callback.

**BREAKING:** Disabled color defaults, horizontal track layout, press feedback and accessibility progress behavior change to correct differences from the official implementation. Public parameter names, ordering and callability are unchanged.

**Still under review:** Native pixel/large-integer precision, fling, multitouch and frame-level interruption equivalence (the haptic model is aligned: see Haptic Feedback), native texture, shadow, spotlight and glitter. Horizontal showSpotLight now selects the shared track-light renderer when the explicit material flag is on; it remains independent of the standard thumb shadow. VerticalSlider/RangeSlider still use their legacy approximation. showGlitterEffect has no rendering implementation. `steps` + `showKeyPoints` now reproduces the `COUISectionSeekBar` marks (see the section-slider parity section); the only remaining delta there is the 200ms programmatic progress transition and the vendor linear-motor intensity. VerticalSlider stays the miuix-style rotated slider; the stock vertical control is `VerticalSeekBar` (see below). RangeSlider is an extension; this pass established no official implementation supporting the COUIRangeSeekBar name. These capabilities must not be described as stock-complete. **Intent variant:** `COUIIntentSeekBar` (a Kotlin source class, not a resource-driven style) ships three sizes — small 2dp track / 10dp max background / 3dp thumb, default 4 / 6 / 4, large 6 / 10 / 5 — with fixed translucent colours (`#4DFFFFFF` background, `#8AFFFFFF` progress, `#29FFFFFF` secondary) and a thumb that follows the theme (`#FF000000` light / `#FFFFFFFF` dark). This library has no intent variant.

### Click, owner assignment and drag (follow-up)

- External value assignments update directly, without a click spring. A click previews with response=0.3 and invokes onValueChange and onValueChangeFinished only at the endpoint. Accessibility progress follows the drawn preview.
- Drag starts only when horizontal displacement strictly exceeds touch slop. It uses the full pointer coordinate, without subtracting slop or treating vertical-only movement as a drag. Continuous following uses response=0.15. On the ordinary follow-hand route, non-fling release completion is independent of the final following segment and does not switch to the click spring.
- The official non-drag ACTION_CANCEL branch also starts a click transition. Parent-consumed events map to this branch here; ordinary release requires a point inside the control. Composition disposal/configuration invalidation is not a new click and does not issue a delayed commit.
- Controlled-API adaptation: accepted owner echoes preserve follow-hand motion; rejected/transformed requests return to the owner value without treating an active drag as pointer-up. External overrides, disabling and configuration replacement invalidate stale commits. This adapts the Compose ownership model; it does not claim equivalence with every reentrant mutable-View path.

**BREAKING:** Horizontal Slider click onValueChange moves from immediate pointer-up delivery to spring-end commit. Completion no longer fires when the preview has only just started. Cancelling an old click does not commit its target; beginning another click/drag still ends the old click tracking lifecycle. VerticalSlider / RangeSlider do not yet use this lifecycle.

### First-drag transition and integer progress mapping (2026-11-12)

- Only the first movement entering a drag tests whether the target differs from the drawn position by more than one native progress unit. A larger gap enters the response=0.15, bounce=0 transition. Once selected, that path remains latched until release/cancel, even after the spring settles.
- The transition reports truncated integer progress on animation frames instead of immediately reporting the finger endpoint. The ordinary follow-hand route reports nearest progress on pointer updates. Release first snaps to the final pointer coordinate, then reports the nearest integer value and completion.
- The new trailing optional progressRange: IntRange? = null preserves the unquantized Float extension by default, with a nonzero-gap test in its continuous limit. Explicit 0..100 maps valueRange to 100 native progress intervals, enabling the strict one-unit threshold and separate truncation/rounding rules. The library does not guess a 100-step grid from an arbitrary Float range.
- progressRange must have a nonnegative start and a greater end. External assignments, click targets and accessibility writes map to the nearest integer while artwork stays continuous. It adds no ticks and is not steps; the section-slider parity lives in `steps` + `showKeyPoints` (see the section-slider parity section). Combining custom steps/magnets is an extension, not native continuous behavior.

**BREAKING:** Horizontal Slider first-drag callbacks follow transition frames rather than immediately reporting a distant finger target. Release while that transition is active no longer retains the previous drawing lag. Existing parameter names and order are unchanged; omitting progressRange introduces no integer grid.

Example: map normalized values to native integer progress without adding visible ticks.

```kotlin
var nativeValue by remember { mutableFloatStateOf(0.5f) }
Slider(
    value = nativeValue,
    onValueChange = { nativeValue = it },
    progressRange = 0..100,
)
```

### Out-of-bounds targets and RTL coordinates (2026-11-12)

- Starting the first-drag transition uses a target clamped to the track. Later pointer updates on that same transition retain fractions from -1 to 2 internally. The drawing and reported values stay inside the actual range; retaining the extra state affects the return motion, not the visible track length.
- Release/cancel snaps an active transition back to the legal final pointer position before reporting the final value. This does not add an overscroll/deformation variant, nor does it implement the separate OEM touch-motion renderer or fling engine.
- RTL input uses the native right anchor (width minus fixed padding), not a mirror of the integer-truncated drawing length. At fractional density these anchors can differ by a fraction of a pixel; the source's distinction is preserved rather than forcing artificial symmetry.
- Custom step/key-point snapping is retained inside the track as an extension; outside targets retain continuous fractions. Integer progress mapping still clamps emitted values to its valid range.

**BREAKING:** Continued first-drag motion outside the track no longer flattens the spring target at the endpoint; return motion and fractional-pixel RTL input change to match the official formulas. Public parameters are unchanged. These static corrections are not a device-level 1:1 acceptance result.

### Continuous fling (2026-11-12)

- Continuous horizontal Slider now keeps moving after release when the physical X velocity is at least 100 raw px/s; velocity is capped at 8000 px/s. Only DOWN/MOVE samples are tracked, not the final UP coordinate. RTL changes progress mapping, not the physical velocity sign.
- The one-dimensional implementation follows the dumped Oplus mode-4 FlingBehavior: velocity is damped by 1/(1+15*dt) before position integration. A separate 2.8Hz, damping-ratio-1 assist spring handles the bounds with four velocity-constraint iterations. It is not a generic exponential decay and does not erase momentum by clamping the internal body.
- Drawing and emitted progress remain legal. onValueChangeFinished is deferred until the fling's velocity and constraint distance are both below the source's raw 0.1-pixel thresholds. Pressing again stops the old fling and completes its tracking lifecycle; controlled-owner overrides/configuration invalidation still cancel stale work without committing an old target.
- Android uses the framework VelocityTracker and captures the view display's nominal period. Original MotionEvent batches are now copied and sampled within the current event scope, including their history and native pointer IDs; only metadata-less fabricated events use synthetic samples. Non-Android uses Compose velocity estimation and the engine's 1/120-second fallback, not an assertion of identical host/platform timing. Platform frame/gesture behavior still requires separate verification.
- This applies only when steps=0 and no key points are configured. Stepped/key-point sliders remain extensions without this continuous fling path. VerticalSlider and RangeSlider are not changed by this pass.

**BREAKING:** Continuous horizontal release can now continue changing value; completion moves from pointer-up to fling end. Public parameters are unchanged. The stock equations and lifecycle have been statically ported, not accepted through a build, executed test or device comparison.

### Raw event history and multiple pointers (2026-11-12)

- Android copies the original DOWN/MOVE event, translates only the copy into the component's local coordinate space and feeds the complete batch to the framework velocity tracker. The borrowed Compose MotionEvent is neither changed nor retained; each copy is recycled immediately. UP and pointer-add/remove events are not added as velocity samples.
- Native pointer IDs are mapped to Compose IDs; the handler follows native pointer index 0 rather than permanently locking onto the initial Compose ID. Additional presses and partial releases do not update progress or finish tracking. The original down coordinate remains the drag origin, matching the source instead of inventing a multi-pointer rebase.
- The Android metadata-less and non-Android fallback paths feed available historical points before the current point. Their velocity windows reset when the selected pointer changes, so a change of finger is not treated as one finger teleporting. This fallback is not asserted to match the native multi-pointer velocity strategy.

**BREAKING:** Lifting the initial finger while another finger remains no longer finishes the gesture or starts fling. Tracking continues until the final release/cancel path. Original batched velocity samples can also change the resulting fling compared with the former one-sample reconstruction. Public parameters are unchanged.

The basic source path is statically restored, not device-accepted. Pointer filtering across child hit paths, transformed layers, fabricated/native event mixing and platform cancellation timing still need separate verification.

### Standard shadow and explicit textured thumb (2026-11-12)

- The ordinary horizontal thumb uses the official style's 4dp shadow radius (converted to integer pixels) and 2dp downward offset, enabled only for an enabled control. It is independent of showSpotLight; the former solid circle expanded by 1.5dp was not this shadow.
- The new trailing optional thumbTextureEnabled=false maps an explicitly resolved material flag or enables a preview. Official gating also depends on a feature provider, blur settings, version/theme and power state. The library does not guess those runtime values or claim automatic OEM resolution.
- On press, texture enters with response=.3 while the white cover exits from 1 with response=.5. On release they return to 0/1 with response=.3. Both use zero bounce. Changing only the material flag while held does not invent a new press.
- Control disabling is separate from material-flag disabling: it does not synthesize a texture release; disabling the material flag resets both alpha channels. Full event recovery when re-enabling before pointer-up still needs further review.
- The three layers are a bottom-to-top white alpha gradient (230/230/140), a #77FFFFFF fill with a white inner-shadow bitmap, and an independent white cover. The inner shadow's 5px blur/.4px offset/10px stroke/12px cache margin are raw pixels, not density-scaled. Cover/group alpha truncates to integers.
- As in the official implementation, the inner bitmap captures the renderer's first nonzero texture frame and subsequently scales to the destination, rather than rebuilding for every radius. Disposal releases it. Texture artwork uses the source's white layers, not custom thumbColor; the ordinary selector is used again when texture opacity returns to zero.

**BREAKING:** Horizontal showSpotLight=false no longer removes the thumb shadow. A custom no-shadow appearance can use the existing sliderColors(thumbShadowColor=Color.Transparent) override. Existing parameter order is unchanged; the added texture parameter defaults off. VerticalSlider/RangeSlider have not migrated to this renderer.

Android uses native Paint/Canvas and a software bitmap; Skia uses corresponding layers and blur filters. Porting APIs does not prove pixel-identical rasterization. Track spotlight/glitter, system material gating, frame-level disabling/configuration transitions and backend raster differences remain under review.

### Shared spotlight reuse (2026-11-12)

Track lighting now calls the same [public Spotlight](../media/spotlight), with the official OpaqueMedium preset. There is no Slider-private shader/animation copy. Both showSpotLight and the explicit thumbTextureEnabled material flag must be enabled; system gating is not guessed. Clipping follows the painted background, and lighting blends after the thumb. Intermediate background heights snap to pixels as in the source; resting/maximum endpoints stay unchanged, while the clip radius keeps the current unsnapped value.


### `coui_seekbar_*` token table (2026-11-12)

`COUISeekBarCore` reads its geometry and colours from `coui_seekbar_*` (the prefix is `seekbar`, not `slider`, which is why an earlier pass wrongly concluded the track had no stock basis):

| Library value | COUI token | Value |
| --- | --- | --- |
| `SliderDefaults.MinHeight` | `coui_seekbar_view_min_height` | 36dp |
| `CapsuleTrackHeight` | `coui_seekbar_background_height` | 20dp |
| `IntentTrackHeight` | `coui_seekbar_intent_height_default` | 4dp |
| `ThumbRestingRadius` | `coui_seekbar_thumb_radius` | 6dp |
| `ThumbExpandedRadius` | `coui_seekbar_thumb_max_radius` | 8dp |
| `ThumbInScaleRadius` | `coui_seekbar_thumb_in_scale_radius` | 3.67dp |
| `ThumbShadowSize` | `coui_seekbar_thumb_shadow_size` | 2dp |
| background colour | `coui_seekbar_background_selector` → `couiColorContainer12` | #1F000000 / #33FFFFFF |
| progress colour | `coui_seekbar_progress_selector` → `couiColorContainerTheme` | theme primary (#0080FF / #1A8CFF) |
| thumb colour | `coui_seekbar_thumb_selector` | white; disabled `coui_seekbar_thumb_disable_color` #8AFFFFFF / #29FFFFFF |

`SliderDefaultsTest` pins the seven geometry values. The progress colour intentionally follows the library's theme `primary` instead of the bare `#0080FF` / `#1A8CFF` (same convention as every other accent in this library).
## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Slider
import io.github.ra1nify.rmuix.kmp.basic.VerticalSlider
import io.github.ra1nify.rmuix.kmp.basic.RangeSlider
```

## Basic Usage

### `COUISectionSeekBar` (section slider) parity (2026-11-17)

`COUISectionSeekBar` extends `COUISeekBar`; its style only overrides `couiSeekBarPhysicsEnable=false`, and the
section look comes from `SectionProgressRenderer` + `SectionAnimationHandler`. `Slider(steps = max - 1,
showKeyPoints = true)` reproduces it:

| Stock piece | Evidence | Library equivalent |
| :--- | :--- | :--- |
| Marks at every section, both ends included | `SectionProgressRenderer.markPositionAt()` loops `index = 0..max` | `stepsToTickFractions()` returns `steps + 2` fractions (0 … 1) |
| Mark radius 3dp | `coui_section_seekbar_tick_mark_radius` (fallback `DEFAULT_SECTION_MARK_RADIUS = 3f`) | `SliderDefaults.TickMarkRadius = 3.dp` |
| Active / inactive colours | `coui_seekbar_active_mark_selector` = `?attr/couiColorLabelOnColor` (#FFFFFF); `coui_seekbar_inactive_mark_selector` = `?attr/couiColorLabelTertiary` (#42000000 / #4Dffffff); disabled #8AFFFFFF / #29FFFFFF and #42000000 / #29FFFFFF (`values-night`) | `SliderColors.keyPointForegroundColor` / `keyPointColor` and their `*Disabled` pairs, asserted by `SliderSectionMarkTest` |
| Marks hidden while the thumb covers them | `drawMarkIfVisible()`: draw only when `mark + r <= thumbLeft || mark - r >= thumbRight` | `sliderSectionMarkIsVisible()` used by the horizontal, vertical and range renderers |
| Physics off | style `couiSeekBarPhysicsEnable=false` | `SliderMotion` gets `physicsEnabled = steps == 0 && keyPoints.isNullOrEmpty()`, so discrete sliders skip the fling |
| Value snaps to the nearest section | `SectionAnimationHandler.snapToNearestSection()`, `SNAP_POSITION_THRESHOLD = 0.5f` | `resolveValueFromFraction()` rounds to the nearest step when `steps > 0` |
| Snap animation 100ms drag / 150ms release, `PathInterpolator(0,0,0.25,1)` | `DRAG_SNAP_DURATION` / `RELEASE_SNAP_DURATION` | `sliderSectionSnapSpec(dragging)` (100ms / 150ms) instead of the continuous spring |
| Section haptics | `VibrationFeedbackHandler.setUseSectionStyleFeedback(true)` | `SliderHapticEffect.Step`: one tick per key-point crossing |

Known deltas: the stock `setProgress(progress, animate = true, …)` path uses a 200ms
`PROGRESS_TRANSITION_DURATION`; Compose owns the value, so there is no equivalent entry point.
`handleFastMoveSnap()` / `onDirectionReversed()` re-anchor the same nearest-section target the library already
rounds to. `coui_section_seekbar_progress_padding_horizontal` (12dp) is a **dead token**: it appears only in
`R.java` and no code or layout reads it.

### VerticalSeekBar (`COUIVerticalSeekBar`)

The stock vertical seek bar (`com.coui.appcompat.seekbar.COUIVerticalSeekBar`) is a **separate control**, not a
rotated `VerticalSlider`: a rounded track that grows out of a bottom padding, a progress bar that fills from
the bottom, an optional square thumb, and a press streak that widens track, progress and padding. systemui uses
it for the Quick Settings brightness/volume sliders (`qs_toggle_slider_container_layout.xml`,
`vertical_volume_row.xml`, 22 layout references).

```kotlin
var value by remember { mutableFloatStateOf(0.4f) }
VerticalSeekBar(
    value = value,
    onValueChange = { value = it },
    modifier = Modifier.size(36.dp, 180.dp),
)

// systemui vertical volume row: 46dp pill, no thumb, progressFull
VerticalSeekBar(
    value = value,
    onValueChange = { value = it },
    showThumb = false,
    progressFull = true,
    style =
        VerticalSeekBarStyle(
            minWidth = 46.dp,
            backgroundWidth = 46.dp,
            backgroundRadius = 23.dp,
            paddingVertical = 23.dp,
            backgroundEnlargeScale = 0.16f,
            progressEnlargeScale = 0.16f,
            backgroundRadiusEnlargeScale = 0.27f,
            progressRadiusEnlargeScale = 0.27f,
        ),
    modifier = Modifier.size(46.dp, 180.dp),
)
```

| Stock attribute | Parameter / default | Source |
| :--- | :--- | :--- |
| `couiVerticalSeekBarMinWidth` | `VerticalSeekBarStyle.minWidth` = 36dp | `coui_vertical_seekbar_view_min_width` |
| `couiVerticalSeekBarMaxHeight` | `VerticalSeekBarStyle.maxHeight` = unspecified (stock `0` = uncapped) | `onMeasure()` |
| `couiVerticalSeekBarBackgroundWidth` | `VerticalSeekBarStyle.backgroundWidth` = 4dp = radius × 2 | `ensureSize()`/attr fallback |
| `couiVerticalSeekBarBackgroundRadius` | `VerticalSeekBarStyle.backgroundRadius` = 2dp | `coui_seekbar_background_radius` |
| `couiVerticalSeekBarProgressWidth` | `VerticalSeekBarStyle.progressWidth` = 4dp = radius × 2 | attr fallback |
| `couiVerticalSeekBarProgressRadius` | `VerticalSeekBarStyle.progressRadius` = 2dp | `coui_seekbar_progress_radius` |
| `couiVerticalSeekBarProgressPaddingVertical` | `VerticalSeekBarStyle.paddingVertical` = 10dp | `coui_vertical_seekbar_progress_padding_vertical` |
| pressed padding | `VerticalSeekBarStyle.pressedPaddingVertical` = 4dp; target `pressedPadding + radius × radiusEnlargeScale` | `coui_vertical_seekbar_progress_pressed_padding_vertical` |
| `couiVerticalSeekBarBackGroundEnlargeScale` | `VerticalSeekBarStyle.backgroundEnlargeScale` = 6.0 | `ensureSize()` |
| `couiVerticalSeekBarProgressEnlargeScale` | `VerticalSeekBarStyle.progressEnlargeScale` = 4.0 | `ensureSize()` |
| `couiVerticalSeekBarBackGroundRadiusEnlargeScale` | `VerticalSeekBarStyle.backgroundRadiusEnlargeScale` = 6.0 | attr default = background scale |
| `couiVerticalSeekBarProgressRadiusEnlargeScale` | `VerticalSeekBarStyle.progressRadiusEnlargeScale` = 4.0 | attr default = progress scale |
| `couiVerticalSeekBarShowThumb` | `showThumb` = true; thumb is `progressWidth × progressEnlargeScale` square, radius `progressRadius × progressRadiusEnlargeScale` | `ensureSize()`/`drawThumb()` |
| `couiVerticalSeekBarShowProgress` | `showProgress` = true | `drawActiveTrack()` |
| `couiVerticalSeekBarProgressFull` | `progressFull` = false; `true` makes progress inherit the track width, radius and scales | `resetProgressSize()` |
| `TOUCH_ANIMATION_ENLARGE_DURATION` / `RELEASE_ANIM_DURATION` | `VerticalSeekBarStyle.touchEnlargeDurationMillis` = 183 | constants |

Colours follow the stock selectors: track `?attr/couiColorContainer12` (`#1F000000` / `#33FFFFFF`), progress
`?attr/couiColorContainerTheme` (`RmuixTheme.colorScheme.primary`), thumb `coui_seekbar_thumb_color`
(`#FFFFFF`), disabled thumb `#8AFFFFFF` / `#29FFFFFF`.

Known deltas: the stock deformation engine (`couiVerticalSeekBarDeformation`, `olivelink`-style stretch/glow),
physics/fling (`couiVerticalSeekBarPhysicsEnable`), the `couiVerticalSeekBarShowText` value label, the vibrator
hooks (`EnableVibrator` / `AdaptiveVibrator`), the click transition animation and the smooth-round-corner
weights (`OplusPath`/`OplusCanvas`) are not modelled. `couiVerticalSeekBarBackgroundHighlightColor`,
`couiVerticalSeekBarProgressScaleRadius` and `couiVerticalSeekBarThumbOutRadius` are **dead attributes**: the
class never reads them (the thumb radius is always derived from `progressRadius × progressRadiusEnlargeScale`).

### Slider

```kotlin
var sliderValue by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = sliderValue,
    onValueChange = { sliderValue = it }
)
```

### VerticalSlider

```kotlin
var sliderValue by remember { mutableFloatStateOf(0.5f) }

VerticalSlider(
    value = sliderValue,
    onValueChange = { sliderValue = it },
    modifier = Modifier.height(200.dp)
)
```

### RangeSlider

```kotlin
var rangeValue by remember { mutableStateOf(0.2f..0.8f) }

RangeSlider(
    value = rangeValue,
    onValueChange = { rangeValue = it }
)
```

## Component States

### Disabled State

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = value,
    onValueChange = { value = it },
    enabled = false
)
```

## Haptic Feedback

**BREAKING:** the default `SliderDefaults.DefaultHapticEffect` changes from `SliderHapticEffect.Edge` to
`SliderHapticEffect.Continuous`: the ColorOS seek bar fires one tick for every value that changes while
dragging (`VibrationFeedbackHandler` -> `performHapticFeedback(305)`, waveform 152 on a linear motor) and a
heavier feedback when a thumb reaches either end (306 / waveform 154). `Edge` and `Step` stay available, and
`Step` now ticks once per key point **crossing** instead of every frame while the finger sits in the
neighbourhood of a key point.

**Behaviour fix (RangeSlider):** a press **outside** the selected window now grabs the nearest thumb instead
of dragging the whole range; presses between the thumbs still move the window. This mirrors roui's
`RouiRangeSlider`, which realOS already validated on device.


Slider supports haptic feedback, which can be customized through the `hapticEffect` parameter. See [SliderHapticEffect](./slider#sliderhapticeffect) for details.

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = value,
    onValueChange = { value = it },
    hapticEffect = SliderHapticEffect.Step
)
```

## Properties

### Slider Properties

| Property Name         | Type                              | Description                                                                                                                                                                    | Default Value                      | Required |
| --------------------- | --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------- | -------- |
| value                 | Float                             | Current value of the slider. If outside of valueRange provided, value will be coerced to this range                                                                            | -                                  | Yes      |
| onValueChange | (Float) -> Unit | Click commits at preview end; continuous drag reports changed values | - | Yes |
| modifier              | Modifier                          | Modifier applied to the slider                                                                                                                                                 | Modifier                           | No       |
| enabled               | Boolean                           | Whether the slider is enabled                                                                                                                                                  | true                               | No       |
| valueRange            | ClosedFloatingPointRange\<Float\> | Range of values that this slider can take. The passed value will be coerced to this range                                                                                      | 0f..1f                             | No       |
| steps                 | Int                               | Amount of discrete allowable values. If 0, the slider will behave continuously. Must not be negative                                                                           | 0                                  | No       |
| onValueChangeFinished | (() -> Unit)? | Click/tracking end; continuous fling defers completion until rest or a new press stops it | null | No |
| reverseDirection      | Boolean                           | Controls the direction of slider. When false (default), follows LayoutDirection (L-R in LTR, R-L in RTL). When true, reverses the direction.                                   | false                              | No       |
| height                | Dp                                | Height of the slider                                                                                                                                                           | SliderDefaults.MinHeight           | No       |
| colors                | SliderColors                      | Color configuration of the slider                                                                                                                                              | SliderDefaults.sliderColors()      | No       |
| hapticEffect          | SliderDefaults.SliderHapticEffect | Type of haptic feedback                                                                                                                                                        | SliderDefaults.DefaultHapticEffect | No       |
| showKeyPoints         | Boolean                           | Whether to show section/key-point marks. Uses `keyPoints` when set, otherwise the `steps` positions; no marks without either                                                                                      | false                              | No       |
| keyPoints             | List\<Float\>?                    | Custom key point values to display on the slider. If null, uses step positions from steps parameter. Values should be within valueRange                                        | null                               | No       |
| magnetThreshold       | Float                             | Magnetic snap threshold as a fraction (0.0 to 1.0). When slider value is within this distance from a key point, it will snap to that point. Only applies when keyPoints is set | 0.02f                              | No       |
| trackHeight | Dp | Base track thickness; horizontal progress does not grow with its background | SliderDefaults.CapsuleTrackHeight | No |
| showGlitterEffect | Boolean | Velocity-driven sweep over the active track; direction flips for RTL (official `GlitterEffectRenderer`) | false | No |
| showSpotLight | Boolean | Shared track spotlight, gated by the explicit material flag; independent of thumb shadow | true | No |
| progressRange | IntRange? | Map Float values to explicit native integer progress; null means no quantization or new ticks | null | No |
| thumbTextureEnabled | Boolean | Explicit thumb texture flag; does not infer OEM system gating | false | No |
| intentSize        | IntentSize?               | Thin "intent" variant: `Small` (2/10/3/8dp), `Default` (4/6/4/5dp), `Large` (6/10/5/8dp) - track height / stretched background height / thumb radius / max thumb radius, from `coui_seekbar_intent_*`; `null` keeps the capsule | null |

> The end padding has **two stock paths**: the capsule form uses the `COUISeekBarCore` formula `(backgroundHeight * enlargeScale) / 2`, which grows with the stretched background, while the intent form reads the absolute `coui_seekbar_progress_padding_horizontal` (14dp) and 4dp pressed. `Slider` picks the path from whether `intentSize` is null; both are implemented.

### VerticalSlider Properties

| Property Name         | Type                              | Description                                                                                                                                                                    | Default Value                      | Required |
| --------------------- | --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------- | -------- |
| value                 | Float                             | Current value of the slider                                                                                                                                                    | -                                  | Yes      |
| onValueChange         | (Float) -> Unit                   | Callback when value changes                                                                                                                                                    | -                                  | Yes      |
| modifier              | Modifier                          | Modifier applied to the slider                                                                                                                                                 | Modifier                           | No       |
| enabled               | Boolean                           | Whether the slider is enabled                                                                                                                                                  | true                               | No       |
| valueRange            | ClosedFloatingPointRange\<Float\> | Range of values that this slider can take                                                                                                                                      | 0f..1f                             | No       |
| steps                 | Int                               | Amount of discrete allowable values                                                                                                                                            | 0                                  | No       |
| onValueChangeFinished | (() -> Unit)?                     | Called when value change has ended                                                                                                                                             | null                               | No       |
| reverseDirection      | Boolean                           | Controls the direction of slider. When false, increases bottom to top. When true, increases top to bottom                                                                      | false                              | No       |
| width                 | Dp                                | Width of the vertical slider                                                                                                                                                   | SliderDefaults.MinHeight           | No       |
| colors                | SliderColors                      | Color configuration of the slider                                                                                                                                              | SliderDefaults.sliderColors()      | No       |
| effect                | Boolean                           | Whether to show special effects                                                                                                                                                | false                              | No       |
| hapticEffect          | SliderDefaults.SliderHapticEffect | Type of haptic feedback                                                                                                                                                        | SliderDefaults.DefaultHapticEffect | No       |
| showKeyPoints         | Boolean                           | Whether to show section/key-point marks. Uses `keyPoints` when set, otherwise the `steps` positions; no marks without either                                                                                      | false                              | No       |
| keyPoints             | List\<Float\>?                    | Custom key point values to display on the slider. If null, uses step positions from steps parameter. Values should be within valueRange                                        | null                               | No       |
| magnetThreshold       | Float                             | Magnetic snap threshold as a fraction (0.0 to 1.0). When slider value is within this distance from a key point, it will snap to that point. Only applies when keyPoints is set | 0.02f                              | No       |
| trackWidth | Dp | Base track thickness; horizontal progress does not grow with its background | SliderDefaults.CapsuleTrackHeight | No |
| showSpotLight | Boolean | Approximate shadow toggle, not native spotlight | true | No |

### RangeSlider Properties

| Property Name         | Type                                        | Description                                                                                                                                                                    | Default Value                      | Required |
| --------------------- | ------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------- | -------- |
| value                 | ClosedFloatingPointRange\<Float\>           | Current values of the RangeSlider. If either value is outside of valueRange, it will be coerced                                                                                | -                                  | Yes      |
| onValueChange         | (ClosedFloatingPointRange\<Float\>) -> Unit | Lambda in which values should be updated                                                                                                                                       | -                                  | Yes      |
| modifier              | Modifier                                    | Modifier applied to the slider                                                                                                                                                 | Modifier                           | No       |
| enabled               | Boolean                                     | Whether the slider is enabled                                                                                                                                                  | true                               | No       |
| valueRange            | ClosedFloatingPointRange\<Float\>           | Range of values that Range Slider values can take                                                                                                                              | 0f..1f                             | No       |
| steps                 | Int                                         | Amount of discrete allowable values                                                                                                                                            | 0                                  | No       |
| onValueChangeFinished | (() -> Unit)?                               | Called when value change has ended                                                                                                                                             | null                               | No       |
| height                | Dp                                          | Height of the slider                                                                                                                                                           | SliderDefaults.MinHeight           | No       |
| colors                | SliderColors                                | Color configuration of the slider                                                                                                                                              | SliderDefaults.sliderColors()      | No       |
| hapticEffect          | SliderDefaults.SliderHapticEffect           | Type of haptic feedback                                                                                                                                                        | SliderDefaults.DefaultHapticEffect | No       |
| showKeyPoints         | Boolean                                     | Whether to show section/key-point marks. Uses `keyPoints` when set, otherwise the `steps` positions; no marks without either                                                                                      | false                              | No       |
| keyPoints             | List\<Float\>?                              | Custom key point values to display on the slider. If null, uses step positions from steps parameter. Values should be within valueRange                                        | null                               | No       |
| magnetThreshold       | Float                                       | Magnetic snap threshold as a fraction (0.0 to 1.0). When slider value is within this distance from a key point, it will snap to that point. Only applies when keyPoints is set | 0.02f                              | No       |
| trackHeight | Dp | Base track thickness; horizontal progress does not grow with its background | SliderDefaults.CapsuleTrackHeight | No |
| showSpotLight | Boolean | Approximate shadow toggle, not native spotlight | true | No |
| minGap | Float | Minimum value gap between range endpoints | 0f | No |

### SliderDefaults Object

The SliderDefaults object provides default configurations for the Slider component.

#### Constants

| Constant Name       | Type               | Default Value           | Description                  |
| ------------------- | ------------------ | ----------------------- | ---------------------------- |
| MinHeight | Dp | 36.dp | Default touch container thickness |
| TickMarkRadius | Dp | 3.dp | Extension tick mark radius |
| CapsuleTrackHeight | Dp | 20.dp | Base track thickness |
| ThumbRestingRadius | Dp | 6.dp | Resting thumb radius |
| ThumbExpandedRadius | Dp | 8.dp | Interacting thumb radius |
| IntentTrackHeight | Dp | 4.dp | Intent-style metric, not the default track |
| ThumbInScaleRadius | Dp | 3.67.dp | Retained Intent-style metric |
| ThumbShadowSize | Dp | 2.dp | Base resource metric; not the default style shadow of 4dp |
| DefaultHapticEffect | SliderHapticEffect | SliderHapticEffect.Continuous | Default haptic feedback type (ColorOS model: one tick per changed value, heavier at the ends) |

### SliderHapticEffect

| Value | Description                  |
| ----- | ---------------------------- |
| None       | No haptic feedback |
| Edge       | Only when a thumb reaches either end of `valueRange` |
| Step       | One tick per key point / step crossing, plus the end feedback |
| Continuous | One tick for every value that changes while dragging, plus a heavier feedback at either end - the ColorOS seek bar model (`VibrationFeedbackHandler`: 305 while dragging, 306 at the ends, 152 / 154 on a linear motor) |

#### Methods

| Method Name    | Return Type  | Description                         |
| -------------- | ------------ | ----------------------------------- |
| sliderColors() | SliderColors | Creates default color configuration |

### SliderColors Class

| Property Name           | Type  | Description                                   |
| ----------------------- | ----- | --------------------------------------------- |
| foregroundColor         | Color | Foreground color of the slider                |
| disabledForegroundColor | Color | Foreground color when disabled                |
| backgroundColor         | Color | Background color of the slider                |
| disabledBackgroundColor | Color | Background color when disabled                |
| thumbColor              | Color | Thumb color                                   |
| disabledThumbColor      | Color | Thumb color when disabled                     |
| keyPointColor           | Color | Tick colour right of the thumb (`?attr/couiColorLabelTertiary`: #42000000 / #4DFFFFFF) |
| keyPointForegroundColor | Color | Tick colour left of the thumb (`?attr/couiColorLabelOnColor`: #FFFFFF) |
| keyPointDisabledColor   | Color | Disabled tick right of the thumb (`coui_seekbar_inactive_mark_disable_color`: #42000000 / #29FFFFFF) |
| keyPointForegroundDisabledColor | Color | Disabled tick left of the thumb (`coui_seekbar_active_mark_disable_color`: #8AFFFFFF / #29FFFFFF) |
| thumbShadowColor | Color | Thumb shadow color: #1A000000 / #40000000 in light/dark |
| glitterMinColor | Color | Reserved glitter color; not currently rendered |
| glitterMaxColor | Color | Reserved glitter color; not currently rendered |

## Advanced Usage

### Custom Value Range

```kotlin
var temperature by remember { mutableFloatStateOf(25f) }

Column {
    Text("Temperature: ${temperature.roundToInt()}°C")
    Slider(
        value = temperature,
        onValueChange = { temperature = it },
        valueRange = 16f..32f
    )
}
```

### Discrete Steps

```kotlin
var rating by remember { mutableFloatStateOf(3f) }

Column {
    Text("Rating: ${rating.roundToInt()}/5")
    Slider(
        value = rating,
        onValueChange = { rating = it },
        valueRange = 1f..5f,
        steps = 3  // Creates 5 discrete values: 1, 2, 3, 4, 5
    )
}
```

### Custom Colors

```kotlin
var volume by remember { mutableFloatStateOf(0.7f) }

Slider(
    value = volume,
    onValueChange = { volume = it },
    colors = SliderDefaults.sliderColors(
        foregroundColor = Color.Red,
        backgroundColor = Color.LightGray
    )
)
```

### Custom Height

```kotlin
var brightness by remember { mutableFloatStateOf(0.8f) }

Slider(
    value = brightness,
    onValueChange = { brightness = it },
    height = 40.dp
)
```

### Slider with Haptic Feedback

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

Slider(
    value = value,
    onValueChange = { value = it },
    hapticEffect = SliderDefaults.SliderHapticEffect.Step
)
```

### VerticalSlider with Reverse Direction

```kotlin
var volume by remember { mutableFloatStateOf(0.5f) }

VerticalSlider(
    value = volume,
    onValueChange = { volume = it },
    modifier = Modifier.height(200.dp),
    reverseDirection = true  // Top to bottom
)
```

### RangeSlider for Price Filter

```kotlin
var priceRange by remember { mutableStateOf(100f..500f) }

Column {
    Text("Price: $${priceRange.start.roundToInt()} - $${priceRange.endInclusive.roundToInt()}")
    RangeSlider(
        value = priceRange,
        onValueChange = { priceRange = it },
        valueRange = 0f..1000f,
        steps = 99  // 101 discrete values from 0 to 1000
    )
}
```

### Complete Example with Value Change Callback

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }
var finalValue by remember { mutableFloatStateOf(0.5f) }

Column {
    Text("Current: $value")
    Text("Final: $finalValue")
    Slider(
        value = value,
        onValueChange = { value = it },
        onValueChangeFinished = { finalValue = value },
        valueRange = 0f..100f,
        steps = 99
    )
}
```

### Slider with Custom Key Points

```kotlin
var value by remember { mutableFloatStateOf(50f) }

Column {
    Text("Value: ${value.roundToInt()}")
    Slider(
        value = value,
        onValueChange = { value = it },
        valueRange = 0f..100f,
        showKeyPoints = true,
        keyPoints = listOf(0f, 25f, 50f, 75f, 100f),
        magnetThreshold = 0.05f,  // 5% snap threshold
        hapticEffect = SliderDefaults.SliderHapticEffect.Step
    )
}
```

### RangeSlider with Key Points

```kotlin
var range by remember { mutableStateOf(20f..80f) }

Column {
    Text("Range: ${range.start.roundToInt()} - ${range.endInclusive.roundToInt()}")
    RangeSlider(
        value = range,
        onValueChange = { range = it },
        valueRange = 0f..100f,
        showKeyPoints = true,
        keyPoints = listOf(0f, 20f, 40f, 60f, 80f, 100f),
        magnetThreshold = 0.03f
    )
}
```

> The stock "bubble" is not a bubble: `seekbar.TextDrawable` paints a **transparent** box (`getPaint().setColor(0)`) and only draws right-aligned text. Width is a hard-coded **144 px** (not dp - `getIntrinsicWidth()` returns a raw pixel count), text 12sp, box 16dp + 6dp bottom margin, 4dp end padding; `COUISeekBarCore.setValueForLabel()` anchors it at the right end of the track, above it. Exposed as `SliderDefaults.ValueLabelWidthPx` / `ValueLabelTextSize` / `ValueLabelHeight` / `ValueLabelMarginBottom` / `ValueLabelPaddingEnd`; the overlay itself is not drawn yet.
## Glitter effect

`showGlitterEffect = true` reproduces `GlitterEffectRenderer`. It is **velocity driven**, not a constant
gradient: every drag re-seeds a spring whose start value is the current driver, whose target is `0`, and
whose initial velocity is `|dragVelocity|` (`response = 0.6`, `bounce = 0`). The visible alpha is
`1 - exp(-(ln 85 / 360) * driver)`, i.e. the sweep flashes while the finger moves fast and fades as the
spring settles.

The overlay is a horizontal gradient across the active track, `#00FFFFFF` -> `#A0FFFFFF`
(`coui_seekbar_glitter_effect_{min,max}_color`), reversed for RTL. Only the horizontal `Slider` draws it;
`VerticalSlider` and `RangeSlider` no longer carry the parameter.

## Section seekbar (`COUISectionSeekBar`)

ColorOS ships the tick-mark seekbar as its own class, `COUISectionSeekBar` (a `COUISeekBar` subclass used
for font size, display zoom, colour temperature, RAM expansion and similar stepped settings). It does not
need a separate component here: `showKeyPoints` + `keyPoints` + `magnetThreshold` already draw the same
tick marks and snap to them.

The stock numbers it contributes are:

| Item | Value | Source |
| :--- | :--- | :--- |
| Tick radius | **3dp** | `coui_section_seekbar_tick_mark_radius` |
| Tick covered by progress | **#FFFFFF** | `coui_seekbar_active_mark_selector` -> `?attr/couiColorLabelOnColor` |
| Tick not covered yet | **#42000000 / #4DFFFFFF** | `coui_seekbar_inactive_mark_selector` -> `?attr/couiColorLabelTertiary` |
| Disabled covered tick | **#8AFFFFFF / #29FFFFFF** | `coui_seekbar_active_mark_disable_color` |
| Disabled uncovered tick | **#42000000 / #29FFFFFF** | `coui_seekbar_inactive_mark_disable_color` |

Stock draws one tick per integer step of the range and hides the ones under the thumb; here the ticks come
from `keyPoints` (or the step positions when `keyPoints` is null) and the thumb simply covers them.
