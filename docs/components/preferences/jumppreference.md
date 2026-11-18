# JumpPreference

`JumpPreference` is a directional indicator component in Rmuix, typically used for navigation or displaying additional content. It provides a title, summary, and right arrow icon with click interaction support, commonly used in settings, menu items, or list items.

<div style="position: relative; height: 280px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=arrowPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass reads `res/layout/coui_preference_widget_jump.xml` and the `support_preference_widget_focus_jump_*` / `support_preference_image_margin_start` dimens of the Settings APK.

- The stock jump widget pads itself 12dp top/bottom (`support_preference_widget_focus_jump_padding_top/bottom`); the row's 48dp minimum height already covers that, so it is not reproduced.
- Its chevron carries `android:layout_marginStart="@dimen/support_preference_image_margin_start"` = **4dp**. `JumpPreference` previously put an arbitrary 16dp end padding on the custom end-action row instead; the arrow now uses `JumpPreferenceDefaults.ArrowMarginStart` (4dp) and the invented 16dp padding is gone.
- The same layout can show an optional status line (`coui_statusText1`: 162dp cap, 24dp start, `PreferenceStatusText.Tiny`) before the chevron. `JumpPreference` and `JumpPreference` now accept `statusText` / `statusTextColor` for that slot.

**BREAKING:** the gap between custom end actions and the chevron drops from 16dp to 4dp; `JumpPreferenceDefaults` gained `ArrowMarginStart`.

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.preference.JumpPreference
```

## Basic Usage

The JumpPreference component provides basic click navigation functionality:

```kotlin
JumpPreference(
    title = "Setting Item",
    onClick = { /* Handle click event */ }
)
```

## Arrow with Summary

```kotlin
JumpPreference(
    title = "Wireless Network",
    summary = "Connected to WIFI-HOME",
    onClick = { /* Handle click event */ }
)
```

## Component States

### Disabled State

```kotlin
JumpPreference(
    title = "Disabled Item",
    summary = "This item is currently unavailable",
    enabled = false,
    onClick = { /* Won't be triggered */ }
)
```

### Hold Down State

JumpPreference supports controlling the hold-down state through the `holdDownState` parameter, typically used for visual feedback when displaying popup dialogs:

```kotlin
var showDialog by remember { mutableStateOf(false) }

Scaffold {
    JumpPreference(
        title = "Open Dialog",
        summary = "Click to show dialog",
        onClick = { showDialog = true },
        holdDownState = showDialog
    )
    // Define dialog elsewhere
    OverlayDialog(
        title = "Dialog",
        show = showDialog,
        onDismissRequest = { showDialog = false }
    ) {
        // Dialog content
    }
}
```

## Properties

### JumpPreference Properties

| Property Name | Type                            | Description                      | Default Value                         | Required |
| ------------- | ------------------------------- | -------------------------------- | ------------------------------------- | -------- |
| title         | String                          | Arrow item title                 | -                                     | Yes      |
| modifier      | Modifier                        | Modifier applied to component    | Modifier                              | No       |
| titleColor    | BasicComponentColors            | Title text color configuration   | BasicComponentDefaults.titleColor()   | No       |
| summary       | String?                         | Arrow item summary description   | null                                  | No       |
| summaryColor  | BasicComponentColors            | Summary text color configuration | BasicComponentDefaults.summaryColor() | No       |
| startAction   | @Composable (() -> Unit)?       | Custom start side content        | null                                  | No       |
| endActions    | @Composable RowScope.() -> Unit | Custom end side content (slot)   | {}                                    | No       |
| bottomAction  | @Composable (() -> Unit)?       | Custom bottom content            | null                                  | No       |
| insideMargin  | PaddingValues                   | Internal content padding         | BasicComponentDefaults.InsideMargin   | No       |
| onClick       | (() -> Unit)?                   | Callback triggered on click      | null                                  | No       |
| holdDownState | Boolean                         | Whether component is held down   | false                                 | No       |
| enabled       | Boolean                         | Whether component is interactive | true                                  | No       |

### JumpPreferenceDefaults Object

The JumpPreferenceDefaults object provides default color configuration for the trailing arrow icon.

#### JumpPreferenceDefaults Methods

| Method Name       | Type              | Description                                         |
| ----------------- | ----------------- | --------------------------------------------------- |
| endActionColors   | EndActionColors   | Returns tint colors used by the trailing arrow icon |

### Arrow Tint

- The trailing arrow icon is always shown and tinted automatically.
- Enabled tint is `#42000000` in light mode and `#4DFFFFFF` in dark - ColorOS `couiColorLabelTertiary`
  (26% alpha), the same neutral the checkbox and radio outlines use.
- Disabled tint is `#14000000` / `#26FFFFFF` - ColorOS `couiColorLabelQuaternary` (8% alpha).
- The arrow keeps the stock 4dp start margin (`support_preference_image_margin_start` from
  `coui_preference_widget_jump.xml`); the widget itself has no trailing padding.

## Advanced Usage

### With Start Icon

```kotlin
JumpPreference(
    title = "Personal Information",
    summary = "View and edit your profile",
    startAction = {
        Icon(
            imageVector = RmuixIcons.Contacts,
            contentDescription = "Personal Icon",
            tint = RmuixTheme.colorScheme.onBackground,
            modifier = Modifier.padding(end = 16.dp)
        )
    },
    onClick = { /* Handle click event */ }
)
```

### With End Actions

```kotlin
JumpPreference(
    title = "Storage Space",
    summary = "Manage app storage space",
    endActions = {
        Text("12.5 GB")
    },
    onClick = { /* Handle click event */ }
)
```

### Using with Dialog

```kotlin
var showDialog by remember { mutableStateOf(false) }
var language by remember { mutableStateOf("Simplified Chinese") }

Scaffold {
JumpPreference(
    title = "Language Settings",
    summary = "Select app display language",
    endActions = {
        Text(language)
    },
    onClick = { showDialog = true },
    holdDownState = showDialog
)
    OverlayDialog(
        title = "Select Language",
        show = showDialog,
        onDismissRequest = { showDialog = false }
    ) {
        // Dialog content
        Card {
            JumpPreference(
                title = "Simplified Chinese",
                onClick = {
                    language = "Simplified Chinese"
                    showDialog = false
                }
            )
            JumpPreference(
                title = "English",
                onClick = {
                    language = "English"
                    showDialog = false
                }
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                text = "Cancel",
                onClick = { showDialog = false },
                modifier = Modifier.weight(1f).padding(top = 8.dp)
            )
        }
    }
}
```
