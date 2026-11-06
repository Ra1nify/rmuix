#! /usr/bin/env markdown

# Snackbar

`Snackbar` is a lightweight feedback component in Rmuix used to display brief messages at the bottom of the screen. It can optionally provide actions such as “Undo” and supports different display durations.

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=snackbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
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
| colors         | SnackbarColors | Color configuration for the Snackbar       | SnackbarDefaults.snackbarColors() | No       |
| insideMargin   | PaddingValues  | Retained for compatibility; no longer applied | SnackbarDefaults.InsideMargin  | No       |

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
    actionContentColor = Color(0xE6000000),    // dark theme: Color(0xE6FFFFFF)
    dismissActionContentColor = Color(0x8A000000),
    actionContainerColor = Color.Transparent,  // the action is a plain label, no pill
)
```

By default the Snackbar uses an inverse surface tone (a dark bar in light theme, a light bar in dark theme) so it stands out over the content behind it.

The action label is rendered as a filled pill; `actionContainerColor` is its background and `actionContentColor` its text color, so override them together to keep enough contrast.

#### Constants

`SnackbarDefaults` also exposes the default dimensions and paddings used by `Snackbar`:

| Constant Name                          | Type          | Description                                        | Default Value                                         |
| -------------------------------------- | ------------- | -------------------------------------------------- | ----------------------------------------------------- |
| CornerRadius                           | Dp            | Corner radius of the bar                           | 13.dp                                                 |
| CornerRadiusSingleLine                 | Dp            | Corner radius while collapsed to a single line     | 24.dp                                                 |
| MaxWidth                               | Dp            | Maximum width of the bar                           | 328.dp                                                |
| MarginBottom                           | Dp            | Gap between the bar and the screen bottom          | 32.dp                                                 |
| ContentHorizontalPadding               | Dp            | Horizontal padding inside the bar                  | 16.dp                                                 |
| ContentVerticalPadding                 | Dp            | Vertical padding inside the bar                    | 10.dp                                                 |
| ContentVerticalPaddingMultiline        | Dp            | Vertical padding above a wrapped message           | 12.dp                                                 |
| ContentVerticalPaddingMultilineBottom  | Dp            | Vertical padding below a wrapped message           | 8.dp                                                  |
| IconSize                               | Dp            | Size of the leading status icon                    | 30.dp                                                 |
| IconVerticalMargin                     | Dp            | Vertical inset of the leading icon                 | 16.dp                                                 |
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
