# SimpleLock

A ColorOS PIN indicator: `COUISimpleLock` (`com.coui.appcompat.lockview`, `coui-support-lockview`) draws a row
of 10dp circles and fills them as the PIN is typed.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.SimpleLock
```

## Basic Usage

```kotlin
var pin by remember { mutableStateOf("") }
SimpleLock(filledCount = pin.length)
Text(text = "Enter your PIN")
```

Six-digit codes use `length = SimpleLockDefaults.SixCircles`:

```kotlin
SimpleLock(filledCount = pin.length, length = SimpleLockDefaults.SixCircles)
```

## Component States

| State | Appearance |
| :--- | :--- |
| Empty dot | `coui_simple_lock_outlined_rectangle_icon`: `#19000000` light / `#66FFFFFF` dark |
| Filled dot | `coui_simple_lock_filled_rectangle_icon`: `#FF000000` light / `#E6FFFFFF` dark |
| Newly filled dot | spring pulse (`SPRING_RESPONSE` 0.35, `SPRING_BOUNCE` 0.5, `CIRCLE_MAX_SCALE` 1.2) |
| Failed | `failed = true` runs the 800ms shake + per-dot fall/fade (`SHAKE_X_POINT`, `DELAY_FOUR` / `DELAY_SIX`) |

## Properties

| Name | Type | Description | Default |
| :--- | :--- | :--- | :--- |
| filledCount | Int | Number of filled circles (clamped to `0..length`) | - |
| modifier | Modifier | Modifier applied to the indicator | Modifier |
| length | Int | Total code length; stock ships `FOURCIRCLE` and `SIXCIRCLE` | SimpleLockDefaults.FourCircles (4) |
| colors | SimpleLockColors | Filled and outlined dot colours | SimpleLockDefaults.colors() |
| animated | Boolean | Pulses the newly filled dot | true |
| failed | Boolean | Runs the stock failed shake/fall animation on the rising edge | false |
| reserveFailureSpace | Boolean | Reserves the stock `mDrawableHeight + 150px` fall room (row pinned to the top) | false |

## SimpleLockDefaults

| Name | Type | Source | Value |
| :--- | :--- | :--- | :--- |
| DotSize | Dp | `coui_simple_lock_*_rectangle_icon` `<size>` | 10dp |
| DotInset | Dp | drawable `<padding>` on each side | 6dp |
| DotBoxSize | Dp | drawable intrinsic size (size + padding) | 22dp |
| DotGap | Dp | `couiRectanglePadding` (`Widget.COUI.COUISimpleLock`) | 10dp |
| FourCircles | Int | `FOURCIRCLE` | 4 |
| SixCircles | Int | `SIXCIRCLE` / `SIXCIRCLETYPE` | 6 |
| FillSpringResponse | Float | `COUISimpleLock.SPRING_RESPONSE` | 0.35 |
| FillSpringBounce | Float | `COUISimpleLock.SPRING_BOUNCE` | 0.5 |
| FillMaxScale | Float | `COUISimpleLock.CIRCLE_MAX_SCALE` | 1.2 |
| FailureMillis | Int | `SHAKE_AND_FALL_TIME` | 800 |
| ShakePointsPx | FloatArray | `SHAKE_X_POINT` (raw px) | 0, 30, -28, 14, -8, 4, -3, 0 |
| FallDistancePx | Float | `FALL_SPEED` | 250 |
| FallFadeDistancePx | Float | `FALL_HEIGHT` | 150 |
| FallRoomPx | Float | `onMeasure()` glow/fall room | 150 |
| DelayFour / DelaySix | FloatArray | `DELAY_FOUR` / `DELAY_SIX` (raw px) | 0, 38.5, 91, 63 / 0, 38.5, 91, 63, 38.5, 70 |

## Known gaps

- The filled↔outlined and clear-all morphs are modelled: each dot draws the outlined base plus a filled
  overlay whose alpha and radius follow a per-dot fraction. A deleted dot (and every dot of a clear-all)
  cross-fades over `MORPHING_FILLED_TO_OUTLINED_TIME` = **230ms**; a new dot animates in with the stock
  `SPRING_RESPONSE` 0.35 / `SPRING_BOUNCE` 0.5 spring, whose overshoot is the `CIRCLE_MAX_SCALE` pulse.
  The failed shake/fall is now modelled: `failed = true` shakes the row with the stock
  `SHAKE_X_POINT` keyframes and drops each filled dot with `FALL_SPEED` = 250 raw px, fading it over
  `FALL_HEIGHT` = 150 raw px after its `DELAY_FOUR` / `DELAY_SIX` offset (800ms, linear). The outlined dots
  only shake, as in `drawFailedAnimation()`.
- The glow effect (`couiGlowEffectDrawable` + `mGlowProgressAnimators`) is not reproduced.
- `COUISimpleLock.onMeasure()` reserves `mDrawableHeight + 150` raw pixels; the library keeps its compact
  [SimpleLockDefaults.DotBoxSize] height by default and reserves the room only with
  `reserveFailureSpace = true` (raw 150px converted at the current density, row pinned to the top).
