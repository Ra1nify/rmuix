#! /usr/bin/env markdown

# Snackbar

`Snackbar` is a lightweight feedback component in Rmuix used to display brief messages at the bottom of the screen. It can optionally provide actions such as “Undo” and supports different display durations.

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=snackbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Snackbar
import io.github.ra1nify.rmuix.kmp.basic.SnackbarHost
import io.github.ra1nify.rmuix.kmp.basic.SnackbarHostState
import io.github.ra1nify.rmuix.kmp.basic.SnackbarDuration
import io.github.ra1nify.rmuix.kmp.basic.SnackbarResult
```

## Basic Usage

The Snackbar is usually used together with `Scaffold`. You create a `SnackbarHostState`, pass it to `SnackbarHost`, and then call `showSnackbar` to display messages:a

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

Scaffold(
    snackbarHost = {
        SnackbarHost(state = snackbarHostState)
    },
) { paddingValues ->
    Box(
        modifier = Modifier
            .padding(paddingValues),
    ) {
        TextButton(
            text = "Show message",
            onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("This is a short message")
                }
            },
        )
    }
}
```

## Leading icon

The base snackbar (`coui_snack_bar_item.xml`) draws a **30dp** icon (`coui_snack_bar_icon_width` /
`coui_snack_bar_icon_height`) with a 16dp gap before the message and 16dp top/bottom margins, so a
single-line bar with an icon grows to icon + 32dp. The notification variant's 58dp horizontal slot
(`coui_notification_snack_bar_*`) is a different widget and is not used here; pass a custom leading
composable if that variant is needed.

## SnackbarHostState and showSnackbar

`SnackbarHostState` manages a queue of Snackbar messages.

### showSnackbar

```kotlin
suspend fun SnackbarHostState.showSnackbar(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult
```

| Parameter Name    | Type             | Description                              | Default Value          | Required |
| ----------------- | ---------------- | ---------------------------------------- | ---------------------- | -------- |
| message           | String           | Text shown in the Snackbar               | -                      | Yes      |
| actionLabel       | String?          | Optional label for the action button     | null                   | No       |
| withDismissAction | Boolean          | Whether to show a dismiss icon           | false                  | No       |
| duration          | SnackbarDuration | Duration that the Snackbar stays visible | SnackbarDuration.Short | No       |

The return value `SnackbarResult` indicates whether the Snackbar was dismissed or the action was performed.

### Getting the oldest or newest Snackbar

```kotlin
suspend fun SnackbarHostState.newestSnackbarData(): SnackbarData?
suspend fun SnackbarHostState.oldestSnackbarData(): SnackbarData?
```

These helpers allow you to manually dismiss the newest or oldest visible Snackbar via `dismiss()` or `performAction()`.

## SnackbarHost

`SnackbarHost` is responsible for rendering the Snackbars based on the given `SnackbarHostState`.

```kotlin
@Composable
fun SnackbarHost(
    state: SnackbarHostState,
    modifier: Modifier = Modifier,
    canSwipeToDismiss: Boolean = true,
    content: @Composable (SnackbarData) -> Unit = { Snackbar(it) },
)
```

| Parameter Name    | Type                               | Description                                | Default Value        | Required |
| ----------------- | ---------------------------------- | ------------------------------------------ | -------------------- | -------- |
| state             | SnackbarHostState                  | State that holds the Snackbar queue        | -                    | Yes      |
| modifier          | Modifier                           | Modifier applied to the host container     | Modifier             | No       |
| canSwipeToDismiss | Boolean                            | Whether Snackbars can be dismissed by swipe | true                | No       |
| content           | @Composable (SnackbarData) -> Unit | Custom content for each Snackbar item      | `{ Snackbar(it) }` | No       |

In most cases you can keep the default `content` which uses the built‑in `Snackbar` composable.

## Snackbar

`Snackbar` defines the default visual style for messages.

```kotlin
@Composable
fun Snackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = SnackbarDefaults.CornerRadius,
    colors: SnackbarColors = SnackbarDefaults.snackbarColors(),
    insideMargin: PaddingValues = SnackbarDefaults.InsideMargin,
)
```

| Parameter Name | Type           | Description                                | Default Value                     | Required |
| -------------- | -------------- | ------------------------------------------ | --------------------------------- | -------- |
| data           | SnackbarData   | Data describing message and actions        | -                                 | Yes      |
| modifier       | Modifier       | Modifier applied to the Snackbar container | Modifier                          | No       |
| cornerRadius   | Dp             | Corner radius of the Snackbar              | SnackbarDefaults.CornerRadius     | No       |

> Single vs wrapped lines are detected from the `onTextLayout` line count (mirroring `COUISnackBar.adjustLayout()` to `isVertical()`): a single-line bar uses the measured height / 2 (stock `getOutline` uses `view.getHeight() / 2f`; the bar is `wrap_content`, ~39dp tall, not a fixed 48dp), while a wrapped bar keeps 16dp. Passing any other `cornerRadius` disables the switch; `CornerRadiusSingleLine` is a legacy placeholder kept for source compatibility.
| colors         | SnackbarColors | Color configuration for the Snackbar       | SnackbarDefaults.snackbarColors() | No       |
| insideMargin   | PaddingValues  | Retained for compatibility; no longer applied | SnackbarDefaults.InsideMargin  | No       |
| icon           | @Composable (() -> Unit)? | Optional leading glyph, 30dp + 16dp gap | null                    | No       |

### SnackbarColors and SnackbarDefaults

`SnackbarColors` defines the color set used by the Snackbar:

```kotlin
data class SnackbarColors(
    val containerColor: Color,
    val contentColor: Color,
    val actionContentColor: Color,
    val dismissActionContentColor: Color,
    val actionContainerColor: Color,
)
```

You can create a color configuration via `SnackbarDefaults.snackbarColors`:

```kotlin
// ColorOS renders the bar as a pale raised card carrying dark text.
val colors = SnackbarDefaults.snackbarColors(
    containerColor = Color(0xFFFFFFFF),        // dark theme: Color(0xFF333333)
    contentColor = Color(0xE6000000),          // dark theme: Color(0xE6FFFFFF)
    actionContentColor = Color(0xFF0080FF),    // ?attr/couiColorPrimaryTextOnPopup; dark: 0xFF5C9DFF
    dismissActionContentColor = Color(0x8A000000),
    actionContainerColor = Color.Transparent,  // the action is a plain label, no pill
)
```

The bar uses ColorOS `couiColorSurfaceTop` (#FFFFFF light / #333333 dark), not an inverse surface tone.

The action is a plain label in ColorOS: `actionContainerColor` is unused by the default layout, and `actionContentColor` is the label colour (`couiColorPrimaryTextOnPopup`).

#### Constants

`SnackbarDefaults` also exposes the default dimensions and paddings used by `Snackbar`:

| Constant Name                          | Type          | Description                                        | Default Value                                         |
| -------------------------------------- | ------------- | -------------------------------------------------- | ----------------------------------------------------- |
| CornerRadius                           | Dp            | Corner radius of the bar                           | 16.dp                                                 |
| CornerRadiusSingleLine                 | Dp            | Legacy placeholder; the single-line radius follows the measured height / 2 | 24.dp |
| MaxWidth                               | Dp            | Maximum width of the bar                           | 328.dp                                                |

> The stock bar has no fixed width: `COUISnackBar.getMaxWidth()` rebuilds a `ResponsiveUIModel` for the visible display frame, picks `MarginType.MARGIN_SMALL` and returns `calculateGridWidth(6)` - six grid columns, which changes with the window. The library value 328dp is only an approximation.
| MarginBottom                           | Dp            | Gap between the bar and the screen bottom          | 32.dp                                                 |
| ContentHorizontalPadding               | Dp            | Horizontal padding inside the bar                  | 16.dp                                                 |
| ContentVerticalPadding                 | Dp            | Vertical padding inside the bar                    | 10.dp                                                 |
| ContentVerticalPaddingMultiline        | Dp            | Vertical padding above a wrapped message           | 12.dp                                                 |
| ContentVerticalPaddingMultilineBottom  | Dp            | Vertical padding below a wrapped message           | 8.dp                                                  |
| IconSize                               | Dp            | Size of the leading status icon                    | 30.dp                                                 |
| IconVerticalMargin                     | Dp            | Vertical inset of the leading icon                 | 16.dp                                                 |
| IconWidth                              | Dp            | Leading icon width (`coui_snack_bar_icon_width`)   | 30.dp                                                 |
| IconHeight                             | Dp            | Leading icon height (`coui_snack_bar_icon_height`) | 30.dp                                                 |
| IconEndPadding                         | Dp            | Gap after the leading icon                         | 16.dp                                                 |
| ActionMaxWidth                         | Dp            | Upper bound on the action label's width            | 110.dp                                                |
| ActionMarginEnd                        | Dp            | Trailing gap after the action                      | 4.dp                                                  |
| ActionVerticalMargin                   | Dp            | Vertical padding around the action                 | 10.dp                                                 |
| ShadowRadius                           | Dp            | Drop shadow beneath the bar                        | 26.dp                                                 |
| InsideMargin                           | PaddingValues | Retained for compatibility; no longer applied      | PaddingValues(all = 12.dp)                            |
| OuterPadding                           | PaddingValues | Default outer padding around the Snackbar          | PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp) |

## SnackbarDuration and SnackbarResult

### SnackbarDuration

`SnackbarDuration` controls how long the Snackbar stays visible:

```kotlin
sealed interface SnackbarDuration {
    data object Short : SnackbarDuration
    data object Long : SnackbarDuration
    data object Indefinite : SnackbarDuration
    data class Custom(val durationMillis: Long) : SnackbarDuration
}
```

| Option     | Description                           | Duration         |
| ---------- | ------------------------------------- | ---------------- |
| Short      | Short message                         | About 4 seconds  |
| Long       | Longer message                        | About 10 seconds |
| Indefinite | Stays until dismissed or action fired | Until dismissed  |
| Custom     | Custom duration in milliseconds       | User‑specified  |

### SnackbarResult

`SnackbarResult` describes how the Snackbar was completed:

```kotlin
enum class SnackbarResult {
    Dismissed,
    ActionPerformed,
}
```

## Advanced Usage

### Snackbar with action

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

TextButton(
    text = "Show action",
    onClick = {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "This message has an action",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short,
            )
            when (result) {
                SnackbarResult.ActionPerformed -> { /* handle undo */ }
                SnackbarResult.Dismissed -> { /* handle timeout */ }
            }
        }
    },
)
```

### Dismissible and indefinite Snackbar

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

TextButton(
    text = "Show indefinite",
    onClick = {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = "Indefinite message, dismiss manually",
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite,
            )
        }
    },
)

TextButton(
    text = "Dismiss oldest",
    onClick = {
        scope.launch {
            snackbarHostState.oldestSnackbarData()?.dismiss()
        }
    },
)
```

> Touching the bar **pauses** the auto-dismiss timer and releasing restarts the **full** duration (`COUISnackBar.onTouchEvent()`: `ACTION_DOWN`/`ACTION_MOVE` remove the runnable, `ACTION_UP`/`ACTION_CANCEL` re-post it with `getDuration()`). There is **no click-to-dismiss** in the stock widget.
