# CheckboxPreference

`CheckboxPreference` is a checkbox component in Rmuix that provides a title, summary, and checkbox
control. It supports click interactions and is commonly used in multi-select settings and selection
lists.

<div style="position: relative; height: 293px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=checkboxPreference" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS static review (2026-11-12)

This pass reads `res/layout/coui_preference_widget_checkbox.xml`, `preference_widget_radiobutton.xml` / `settingslib_preference_widget_radiobutton.xml` and `coui_preference_widget_select.xml` of the Settings APK.

- The trailing checkbox in the official widget layout (`coui_preference_widget_checkbox.xml`) carries `android:paddingRight="1dp"`, `android:layout_marginLeft` = 0dp and `android:layout_marginRight` = **-10dp** (`coui_preference_checkbox_margin_left` / `_right`). Both are applied: the end checkbox adds `CheckboxPreferenceDefaults.EndPadding` (1dp) and a negative end margin (`WidgetMarginEnd`) implemented with a custom layout that reports a 10dp narrower box, so the checkbox overhangs the row's 32dp end padding by 9dp (visible right edge = row end - 22dp before its own 1dp padding).
- The radio widget layouts (`preference_widget_radiobutton.xml`, `settingslib_preference_widget_radiobutton.xml`) declare **no** margins or padding, which matches the component's trailing radio (it has none).
- The leading checkbox/radio slots keep their 5dp end padding; the official layouts only describe the trailing widget, and the leading placement is a library extension, so this was left alone.
- `coui_preference_widget_select.xml` shows the stock select row uses a trailing **status text** (`coui_preference_status_text_max_width` 162dp, `support_preference_widget_focus_jump_margin_start` 24dp) plus a select icon (`support_preference_image_padding_start` 16dp); the status column is modelled by `BasicComponent`'s `statusText` (162dp max width, 24dp start margin, right-aligned single line) — see BasicComponent.

**BREAKING:** the default `checkboxLocation` moved from `Start` to **`End`**: `coui_preference.xml` puts

| `WidgetMarginEnd` | `-10dp` end margin of the trailing widget (`coui_preference_checkbox_margin_right`); the widget overhangs the row padding |
`@android:id/widget_frame` last in the row, so the stock widget is trailing. `Start` remains available as a
library extension. The trailing checkbox also gains 1dp of end padding.

**Widget margins:** `coui_preference_widget_checkbox.xml` gives the trailing widget `layout_marginLeft` 0dp and `layout_marginRight` **-10dp**, i.e. it overhangs the row's 32dp content padding toward the screen edge. `CheckboxPreferenceDefaults.WidgetMarginEnd` reproduces the negative margin (reported width shrinks, drawing and touch target shift), and `EndPadding` keeps the widget's own `paddingRight` 1dp.

**Correction:** several COUI widget layouts *are* part of this APK - `coui_preference_widget_checkbox.xml`, `_switch.xml` (and `_switch_compat` / `_switchload`), `_menu.xml` (jump arrow), `_select.xml` (status text + arrow), `_stepper_view.xml`, `_mark.xml`, `_button.xml` and `_status_switching.xml`. The status-text column is already modelled by `BasicComponent`'s `statusText` (`coui_preference_widget_select.xml`: `support_preference_widget_focus_jump_margin_start` 24dp, `coui_preference_status_text_max_width` 162dp); the select widget's 12dp top/bottom `support_preference_widget_focus_jump_padding_*` and the jump-arrow margins (`support_preference_image_margin_start` 4dp / `support_preference_image_padding_start` 16dp) are not applied by the generic `endActions` slot, and its 8dp `EndActionsEndPadding` is a library choice because no dimen describes the gap to the row edge.
## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.preference.CheckboxPreference
import io.github.ra1nify.rmuix.kmp.preference.CheckboxLocation
```

## Basic Usage

CheckboxPreference component provides basic checkbox functionality:

```kotlin
var isChecked by remember { mutableStateOf(false) }

CheckboxPreference(
    title = "Checkbox Option",
    checked = isChecked,
    onCheckedChange = { isChecked = it }
)
```

## Checkbox with Summary

```kotlin
var notificationsEnabled by remember { mutableStateOf(false) }

CheckboxPreference(
    title = "Notifications",
    summary = "Receive push notifications from the app",
    checked = notificationsEnabled,
    onCheckedChange = { notificationsEnabled = it }
)
```

## Component States

### Disabled State

```kotlin
CheckboxPreference(
    title = "Disabled Checkbox",
    summary = "This checkbox is currently unavailable",
    checked = true,
    onCheckedChange = {},
    enabled = false
)
```

## Checkbox Position

CheckboxPreference supports placing the checkbox on the start or end side:

### End Checkbox (Default)

```kotlin
var startChecked by remember { mutableStateOf(false) }

CheckboxPreference(
    title = "End Checkbox",
    summary = "Checkbox is on the end side (default)",
    checked = startChecked,
    onCheckedChange = { startChecked = it },
    checkboxLocation = CheckboxLocation.End // Default value
)
```

### Start Checkbox (library extension)

```kotlin
var endChecked by remember { mutableStateOf(false) }

CheckboxPreference(
    title = "Start Checkbox",
    summary = "Checkbox is on the start side (library extension)",
    checked = endChecked,
    onCheckedChange = { endChecked = it },
    checkboxLocation = CheckboxLocation.Start
)
```

## Properties

### CheckboxPreference Properties

| Property Name    | Type                               | Description                          | Default Value                         | Required |
|------------------|------------------------------------|--------------------------------------|---------------------------------------|----------|
| title            | String                             | Title of the checkbox item           | -                                     | Yes      |
| checked          | Boolean                            | Checkbox checked state               | -                                     | Yes      |
| onCheckedChange  | ((Boolean) -> Unit)?               | Callback when checkbox state changes | -                                     | No       |
| modifier         | Modifier                           | Modifier applied to component        | Modifier                              | No       |
| titleColor       | BasicComponentColors               | Title text color configuration       | BasicComponentDefaults.titleColor()   | No       |
| summary          | String?                            | Summary description                  | null                                  | No       |
| summaryColor     | BasicComponentColors               | Summary text color configuration     | BasicComponentDefaults.summaryColor() | No       |
| checkboxColors   | CheckboxColors                     | Checkbox control color configuration | CheckboxDefaults.checkboxColors()     | No       |
| startAction      | @Composable (() -> Unit)?          | Custom content after checkbox        | null                                  | No       |
| endActions       | @Composable (RowScope.() -> Unit)? | Custom content before checkbox       | null                                  | No       |
| checkboxLocation | CheckboxLocation                   | Checkbox position                    | CheckboxLocation.End                | No       |
| bottomAction     | @Composable (() -> Unit)?          | Custom bottom content                | null                                  | No       |
| insideMargin     | PaddingValues                      | Internal content padding             | BasicComponentDefaults.InsideMargin   | No       |
| holdDownState    | Boolean                            | Whether the component is held down   | false                                 | No       |
| enabled          | Boolean                            | Whether component is interactive     | true                                  | No       |

## Advanced Usage

### With Right Actions

```kotlin
var backupEnabled by remember { mutableStateOf(false) }

CheckboxPreference(
    title = "Auto Backup",
    summary = "Periodically backup your data",
    checked = backupEnabled,
    onCheckedChange = { backupEnabled = it },
    endActions = {
        Text(
            text = if (backupEnabled) "Enabled" else "Disabled",
            color = RmuixTheme.colorScheme.onSurfaceVariantActions,
            modifier = Modifier.padding(end = 6.dp)
        )
    }
)
```

### Nested Checkboxes

```kotlin
var allSelected by remember { mutableStateOf(false) }
var option1 by remember { mutableStateOf(false) }
var option2 by remember { mutableStateOf(false) }
var option3 by remember { mutableStateOf(false) }

Column {
    CheckboxPreference(
        title = "Select All",
        checked = allSelected,
        onCheckedChange = { newState ->
            allSelected = newState
            option1 = newState
            option2 = newState
            option3 = newState
        }
    )
    CheckboxPreference(
        title = "Option 1",
        checked = option1,
        onCheckedChange = {
            option1 = it
            allSelected = option1 && option2 && option3
        },
        modifier = Modifier.padding(start = 24.dp)
    )
    CheckboxPreference(
        title = "Option 2",
        checked = option2,
        onCheckedChange = {
            option2 = it
            allSelected = option1 && option2 && option3
        },
        modifier = Modifier.padding(start = 24.dp)
    )
    CheckboxPreference(
        title = "Option 3",
        checked = option3,
        onCheckedChange = {
            option3 = it
            allSelected = option1 && option2 && option3
        },
        modifier = Modifier.padding(start = 24.dp)
    )
}
```

### Custom Colors

```kotlin
var customChecked by remember { mutableStateOf(false) }

CheckboxPreference(
    title = "Custom Colors",
    titleColor = BasicComponentDefaults.titleColor(
        color = RmuixTheme.colorScheme.primary
    ),
    summary = "Checkbox with custom colors",
    summaryColor = BasicComponentDefaults.summaryColor(
        color = RmuixTheme.colorScheme.secondary
    ),
    checked = customChecked,
    onCheckedChange = { customChecked = it },
    checkboxColors = CheckboxDefaults.checkboxColors(
        checkedForegroundColor = Color.Red,
        checkedBackgroundColor = RmuixTheme.colorScheme.secondary
    )
)
```

### Using with Dialog

```kotlin
var showDialog by remember { mutableStateOf(false) }
var privacyOption by remember { mutableStateOf(false) }
var analyticsOption by remember { mutableStateOf(false) }

Scaffold {
    JumpPreference(
        title = "Privacy Settings",
        onClick = { showDialog = true },
        holdDownState = showDialog
    )

    OverlayDialog(
        title = "Privacy Settings",
        show = showDialog,
        onDismissRequest = { showDialog = false } // Close dialog
    ) {
        Card {
            CheckboxPreference(
                title = "Privacy Policy",
                summary = "Agree to the privacy policy terms",
                checked = privacyOption,
                onCheckedChange = { privacyOption = it }
            )

            CheckboxPreference(
                title = "Analytics Data",
                summary = "Allow collection of anonymous usage data to improve services",
                checked = analyticsOption,
                onCheckedChange = { analyticsOption = it }
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            TextButton(
                text = "Cancel",
                onClick = { showDialog = false }, // Close dialog
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(16.dp))
            TextButton(
                text = "Confirm",
                onClick = { showDialog = false }, // Close dialog
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary() // Use theme colors
            )
        }
    }
}
```
