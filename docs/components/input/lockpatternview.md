# LockPatternView

The ColorOS pattern lock: `COUILockPatternView` (`com.coui.appcompat.lockview`) draws a 3 × 3 grid of dots and
connects them while you drag.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.LockPatternView
import io.github.ra1nify.rmuix.kmp.basic.LockPatternDisplayMode
```

## Basic Usage

```kotlin
var pattern by remember { mutableStateOf<List<Int>>(emptyList()) }
var wrong by remember { mutableStateOf(false) }
LockPatternView(
    pattern = pattern,
    displayMode = if (wrong) LockPatternDisplayMode.Wrong else LockPatternDisplayMode.Correct,
    onPatternDetected = { detected ->
        pattern = detected
        wrong = detected != expectedPattern
    },
    onPatternCleared = { pattern = emptyList() },
)
```

## Properties

| Name | Type | Description | Default |
| :--- | :--- | :--- | :--- |
| onPatternDetected | (List<Int>) -> Unit | Called on release with the ordered cell indices (`0..8`, row-major) | - |
| modifier | Modifier | Applied to the view; the stock wrap-content size is 312dp × 280dp | Modifier |
| enabled | Boolean | Whether the view accepts input and draws the enabled colours | true |
| pattern | List<Int> | The displayed pattern (single source of truth) | emptyList() |
| displayMode | LockPatternDisplayMode | `Correct` (success colour) / `Wrong` (error colour + 1000ms flicker) | Correct |
| onPatternStart | (() -> Unit)? | Called when a drag starts on a cell | null |
| onPatternCleared | (() -> Unit)? | Called when a touch starts outside any cell | null |
| colors | LockPatternColors | Dot/path/success/error colours and halo alpha | LockPatternDefaults.colors() |
| hapticFeedback | Boolean | Fires haptics per new cell (stock `performHitFeedback()`) | true |
| stealthMode | Boolean | Stock `mInStealthMode`: draw the dots but not the path | false |

## LockPatternDefaults

| Name | Type | Source | Value |
| :--- | :--- | :--- | :--- |
| Width / Height | Dp | `coui_lock_pattern_view_width` / `_height` | 312dp / 280dp |
| HorizontalPadding / VerticalPadding | Dp | `coui_lock_pattern_view_horizontal_padding` / `_vertical_padding` | 21dp / 5dp |
| DotSize | Dp | `lock_pattern_dot_size` | 10dp |
| PathWidth | Dp | `lock_pattern_dot_line_width` | 2dp |
| HitFactor | Float | `COUILockPatternView.mHitFactor` | 0.6 |
| InnerGlowDiameter | Dp | `coui_lock_pattern_inner_circle.png` (90px at xxhdpi) | 30dp |
| OuterDotDiameter | Dp | `coui_lock_pattern_outer_circle.png` | 9dp |
| OuterCircleMaxScale | Float | `startOuterAnimation()` `1f -> 7f` | 7 |
| InnerAnimationMillis | Int | `startInnerAnimation()` | 230 |
| OuterAnimationMillis | Int | `startOuterAnimation()` | 460 |
| WrongAnimationMillis | Int | `startWrongAnimator()` | 1000 |
| MaxTranslateY | Dp | `color_lock_pattern_view_max_translate_y`; only feeds the never-read `cellNumberTranslateY` of `getEnterAnim()` (dead for rendering) | 258dp |
| AnimateCellMillis | Int | `MILLIS_PER_CIRCLE_ANIMATING`: one replayed cell per 700ms | 700 |

## Colours

| Slot | Source | Light | Dark |
| :--- | :--- | :--- | :--- |
| regularColor | `coui_lock_pattern_dot_color` | `#4D000000` | `#66FFFFFF` |
| pathColor | `coui_lock_pattern_path_color` | `#000000` | `#FFFFFF` |
| successColor | `coui_lock_pattern_success_color` | `#000000` | `#FFFFFF` |
| errorColor | `coui_lock_pattern_error_color` | `#EA3447` | `#EA3447` |
| outerCircleMaxAlpha | `coui_lock_pattern_outer_circle_max_alpha` | 0.1 | 0.2 |

## Known gaps

- `DisplayMode.Animate` is modelled: every `MILLIS_PER_CIRCLE_ANIMATING` = **700ms** one pattern cell lights up
  (`COUILockPatternView.onDraw()`: `elapsed % ((size + 1) * 700)`, `cellIndex = elapsed / 700`, fraction
  `elapsed % 700 / 700`), the line catches up with that fraction, and the full pattern holds for one extra
  tick before the loop restarts (`LockPatternDisplayMode.Animate`, `lockPatternAnimateFrame()`); the replay
  uses plain success-coloured dots without the activation glow/ripple. `FingerprintMatch` /
  `FingerprintNoMatch` are not modelled; `Correct` / `Wrong` (with the stock 1000ms wrong-mode alpha flicker) are.
- **Correction:** `color_lock_pattern_view_max_translate_y` (258dp) is not a wrong-mode translate - it only feeds
  `getEnterAnim()`'s per-cell `cellNumberTranslateY`, and `cellNumberAlpha` / `cellNumberTranslateY` are never
  read in `COUILockPatternView.onDraw()`, so there is no visible translation to reproduce.
- The 100ms line catch-up (`startLineEndAnimation`, `mFastOutSlowInInterpolator`) is modelled: a newly activated cell's segment starts at the finger position and interpolates to the cell centre over `LineEndAnimationMillis` = 100ms, matching `onDraw()`'s `lineEndX/Y` branch.
- The per-cell haptic codes (302 / 304 linear-motor, 1 / 300 fallback) map to Compose
  `HapticFeedbackType.SegmentFrequentTick`.
- `ExploreByTouchHelper` virtual accessibility cells are not reproduced.
