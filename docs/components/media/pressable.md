# Modifier.pressable

`Modifier.pressable` adds ColorOS 17's dynamic press motion to any surface: the content scales down
while it is pressed and springs back on release or cancellation.

It is the modifier form of the press feedback used by cards, colour swatches and other custom
clickable areas that draw their own background. `Modifier.clickable` would add a ripple those
surfaces do not want, so this modifier attaches the **click without any indication**.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.pressable
```

## Basic Usage

```kotlin
Box(
    Modifier
        .size(48.dp)
        .pressable(pressedScale = 0.92f, onClick = { select(index) })
)
```

## Properties

| Property Name       | Type                 | Description                                                       | Default Value | Required |
| ------------------- | -------------------- | ----------------------------------------------------------------- | ------------- | -------- |
| enabled             | Boolean              | Whether press feedback and the click are active                   | true          | No       |
| pressedScale        | Float                | Scale applied while pressed; use `1f` for a click-only surface    | 0.97f         | No       |
| pressDampingRatio   | Float                | Damping ratio of the press-down spring                            | 0.82f         | No       |
| pressStiffness      | Float                | Stiffness of the press-down spring                                | 1600f         | No       |
| releaseDampingRatio | Float                | Damping ratio of the release spring (lower = bouncier)            | 0.65f         | No       |
| releaseStiffness    | Float                | Stiffness of the release spring                                   | 380f          | No       |
| haptic              | HapticFeedbackType?  | Haptic fired on click; `null` disables it                        | null          | No       |
| onClick             | (() -> Unit)?        | Click handler; `null` provides press feedback only                | null          | No       |

## Notes

- Spring values mirror `com.coui.appcompat.animation.touchanimation.PressEngineConfig`: press-down
  uses `0.82` damping / `1600` stiffness, release uses `0.65` / `380` (the bounce).
- No ripple or other indication is drawn. If you need one, compose this modifier with
  `Modifier.clickable` yourself, or use `Button` / `IconButton`, which already draw their own
  feedback.
- The click lambda is read through `rememberUpdatedState`, so a captured state does not go stale
  during a long press.
- Releasing, dragging out of bounds or a multi-touch cancellation all animate back to `1f`; the
  pressed state is never left behind.
