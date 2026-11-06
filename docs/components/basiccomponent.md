# BasicComponent

`BasicComponent` is a foundational standard component in Rmuix. It provides customizable content areas on the start and end sides, along with a title and summary, making it suitable for building list items, settings items, and other UI elements.

This project builds upon it to provide some extended components, enabling developers to quickly create UI components that conform to design specifications. See the usage of [Extended Components](../components/#extended-components) for details.

<div style="position: relative; height: 350px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=basicComponent" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.BasicComponent
```

## Basic Usage

The BasicComponent can be used to display title and summary information:

```kotlin
BasicComponent(
    title = "Setting Item Title",
    summary = "This is the description text for the setting item"
)
```

## Component Variants

### Clickable Component

```kotlin
BasicComponent(
    title = "Wi-Fi",
    summary = "Connected to MIUI-WiFi",
    onClick = { /* Handle click event */ }
)
```

### With Start Icon

```kotlin
BasicComponent(
    title = "Nickname",
    summary = "A brief introduction",
    startAction = {
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            imageVector = RmuixIcons.Contacts,
            contentDescription = "Avatar Icon",
            tint = RmuixTheme.colorScheme.onBackground
        )
    },
    onClick = { /* Handle click event */ }
)
```

### With End Actions

```kotlin
var isFlightMode by remember { mutableStateOf(false) }

BasicComponent(
    title = "Flight Mode",
    endActions = {
        Switch(
            checked = isFlightMode,
            onCheckedChange = { isFlightMode = it }
        )
    }
)
```

### Custom Content Variant

In addition to the title and summary variant, BasicComponent also provides an overload that accepts custom content. This is useful when you want full control over the layout inside the component while still reusing the container and interactions:

```kotlin
BasicComponent(
    startAction = {
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            imageVector = RmuixIcons.Contacts,
            contentDescription = "Avatar Icon",
            tint = RmuixTheme.colorScheme.onBackground
        )
    },
    endActions = {
        Text("Details")
    }
) {
    Text(
        text = "Custom Title",
        style = RmuixTheme.textStyles.headline1
    )
    Text(
        text = "Custom content description",
        style = RmuixTheme.textStyles.body2
    )
}
```

## Component States

### Disabled State

```kotlin
BasicComponent(
    title = "Mobile Network",
    summary = "SIM card not inserted",
    enabled = false
)
```

## Properties

### BasicComponent Properties (title and summary variant)

| Property Name     | Type                               | Description                                           | Default Value                         | Required |
| ----------------- | ---------------------------------- | ----------------------------------------------------- | ------------------------------------- | -------- |
| modifier          | Modifier                           | Modifier for the component                            | Modifier                              | No       |
| title             | String?                            | Title of the component                                | null                                  | No       |
| titleColor        | BasicComponentColors               | Title color configuration                             | BasicComponentDefaults.titleColor()   | No       |
| summary           | String?                            | Summary of the component                              | null                                  | No       |
| summaryColor      | BasicComponentColors               | Summary color configuration                           | BasicComponentDefaults.summaryColor() | No       |
| assignment        | String?                            | Optional third line, drawn under the summary          | null                                  | No       |
| assignmentColor   | BasicComponentColors               | Assignment line color configuration                   | BasicComponentDefaults.assignmentColor() | No    |
| startAction       | @Composable (() -> Unit)?          | Composable content on the start side of the component | null                                  | No       |
| endActions        | @Composable (RowScope.() -> Unit)? | Composable content on the end side of the component   | null                                  | No       |
| bottomAction      | @Composable (() -> Unit)?          | Composable content at the bottom of the component     | null                                  | No       |
| insideMargin      | PaddingValues                      | Internal padding of the component                     | BasicComponentDefaults.InsideMargin   | No       |
| onClick           | (() -> Unit)?                      | Callback triggered when the component is clicked      | null                                  | No       |
| onClickLabel      | String?                            | Accessibility label describing the click action       | null                                  | No       |
| role              | Role?                              | Semantic role for accessibility services              | null                                  | No       |
| holdDownState     | Boolean                            | Whether the component is in the pressed state         | false                                 | No       |
| enabled           | Boolean                            | Whether the component is enabled                      | true                                  | No       |
| iconBadge         | Boolean                            | Show a hint dot on the top-end corner of startAction  | false                                 | No       |
| iconBadgeCount    | Int                                | Number inside the icon badge; 0 renders a plain dot   | 0                                     | No       |
| endBadge          | Boolean                            | Show a hint dot before endActions                     | false                                 | No       |
| iconMarginTiered  | Boolean                            | Pick the icon's vertical margin from the ColorOS tiers | true                                 | No       |
| interactionSource | MutableInteractionSource?          | Interaction source of the component                   | null                                  | No       |

### BasicComponent Properties (custom content variant)

| Property Name     | Type                               | Description                                           | Default Value                       | Required |
| ----------------- | ---------------------------------- | ----------------------------------------------------- | ----------------------------------- | -------- |
| modifier          | Modifier                           | Modifier for the component                            | Modifier                            | No       |
| startAction       | @Composable (() -> Unit)?          | Composable content on the start side of the component | null                                | No       |
| endActions        | @Composable (RowScope.() -> Unit)? | Composable content on the end side of the component   | null                                | No       |
| bottomAction      | @Composable (() -> Unit)?          | Composable content at the bottom of the component     | null                                | No       |
| insideMargin      | PaddingValues                      | Internal padding of the component                     | BasicComponentDefaults.InsideMargin | No       |
| onClick           | (() -> Unit)?                      | Callback triggered when the component is clicked      | null                                | No       |
| onClickLabel      | String?                            | Accessibility label describing the click action       | null                                | No       |
| role              | Role?                              | Semantic role for accessibility services              | null                                | No       |
| holdDownState     | Boolean                            | Whether the component is in the pressed state         | false                               | No       |
| enabled           | Boolean                            | Whether the component is enabled                      | true                                | No       |
| interactionSource | MutableInteractionSource?          | Interaction source of the component                   | null                                | No       |
| content           | @Composable ColumnScope.() -> Unit | Composable content of the component                   | -                                   | Yes      |

### BasicComponentDefaults Object

The BasicComponentDefaults object provides default values and color configurations for the BasicComponent.

#### BasicComponentDefaults Constants

| Constant Name      | Type          | Description                                              | Default Value                        |
| ------------------ | ------------- | -------------------------------------------------------- | ------------------------------------ |
| MinHeight          | Dp            | Minimum row height                                       | 48.dp                                |
| InsideMargin       | PaddingValues | Internal padding of the component                        | PaddingValues(horizontal = 32.dp, vertical = 10.dp) |
| IconTextGap        | Dp            | Gap between the leading action and the text column       | 16.dp                                |
| IconMarginDefault  | Dp            | Icon vertical margin when no tier matches                | 14.dp                                |
| IconMargin24       | Dp            | 24dp icon, single line                                   | 12.dp                                |
| IconMargin24Multiline | Dp         | 24dp icon, wrapped text                                  | 20.dp                                |
| IconMargin32       | Dp            | 32dp icon, single line                                   | 10.dp                                |
| IconMargin32Multiline | Dp         | 32dp icon, wrapped text                                  | 16.dp                                |
| IconMargin36       | Dp            | 36dp icon, single line                                   | 12.dp                                |
| IconMargin36Multiline | Dp         | 36dp icon, wrapped text                                  | 14.dp                                |
| IconMargin50       | Dp            | 50dp icon, both cases                                    | 8.dp                                 |
| BadgeSize          | Dp            | Plain hint dot diameter                                  | 6.dp                                 |
| BadgeCornerRadius  | Dp            | Count badge corner radius                                | 2.67.dp                              |
| BadgeTextPadding   | Dp            | Horizontal padding inside a count badge                  | 2.dp                                 |
| BadgeGap           | Dp            | Gap between the end badge and trailing content           | 4.dp                                 |

#### BasicComponentDefaults Methods

| Method Name       | Type                 | Description                                        |
| ----------------- | -------------------- | -------------------------------------------------- |
| titleColor()      | BasicComponentColors | Creates title color configuration                  |
| summaryColor()    | BasicComponentColors | Creates summary color configuration                |
| assignmentColor() | BasicComponentColors | Creates assignment line color configuration        |
| iconVerticalMargin() | Dp                | Picks the icon vertical margin from the tier table |

### BasicComponentColors Class

Used to configure the color states of the component.

| Property Name | Type  | Description             |
| ------------- | ----- | ----------------------- |
| color         | Color | Color in normal state   |
| disabledColor | Color | Color in disabled state |

## Advanced Usage

### Complex Layout Component

```kotlin
BasicComponent(
    title = "Volume",
    summary = "Media Volume: 70%",
    startAction = {
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            imageVector = RmuixIcons.Play,
            contentDescription = "Volume Icon",
            tint = RmuixTheme.colorScheme.onBackground
        )
    },
    endActions = {
        IconButton(onClick = { /* Decrease volume */ }) {
            Icon(
                imageVector = RmuixIcons.Remove,
                contentDescription = "Decrease Volume",
                tint = RmuixTheme.colorScheme.onBackground
            )
        }
        Text("70%")
        IconButton(onClick = { /* Increase volume */ }) {
            Icon(
                imageVector = RmuixIcons.Add,
                contentDescription = "Increase Volume",
                tint = RmuixTheme.colorScheme.onBackground
            )
        }
    }
)
```

### Custom Style Component

```kotlin
BasicComponent(
    title = "Custom Component",
    summary = "Using custom colors",
    titleColor = BasicComponentColors(
        color = Color.Blue,
        disabledColor = Color.Gray
    ),
    summaryColor = BasicComponentColors(
        color = Color.DarkGray,
        disabledColor = Color.LightGray
    ),
    insideMargin = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    onClick = { /* Handle option click */ }
)
```

### Usage in a List

```kotlin
val options = listOf("Option 1", "Option 2", "Option 3", "Option 4")

Column {
    options.forEach { option ->
        BasicComponent(
            title = option,
            onClick = { /* Handle option click */ }
        )
    }
}
```
