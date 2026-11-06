# NavigationBar

`NavigationBar` is a bottom navigation bar component in Rmuix styled according to ColorOS 17 specifications. It supports 2 to 5 navigation items, offering different display modes (icon only, icon and text, icon with selected label).

`FloatingNavigationBar` is a floating-style bottom navigation bar component, supporting both ColorOS 17 single-pill style (`FloatingNavigationBarStyle.ColorOS`) and legacy default style (`FloatingNavigationBarStyle.Default`).

These components are typically used in conjunction with the `Scaffold` component to maintain consistent layout and behavior across different pages in the application.

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../compose/index.html?id=navigationBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Import

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.NavigationBar
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarDefaults
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarItem
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBar
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBarItem
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBarStyle
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarDisplayMode
```

## Basic Usage

### NavigationBar

The NavigationBar component can be used to create bottom navigation menus fixed to the bottom:

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Home", "Profile", "Settings")
val icons = listOf(RmuixIcons.VerticalSplit, RmuixIcons.Contacts, RmuixIcons.Settings)

Scaffold(
    bottomBar = {
        NavigationBar {
            items.forEachIndexed { index, label ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    icon = icons[index],
                    label = label
                )
            }
        }
    }
)
```

### FloatingNavigationBar

The FloatingNavigationBar component can be used to create floating navigation menus at the bottom:

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("Home", "Profile", "Settings")
val icons = listOf(RmuixIcons.VerticalSplit, RmuixIcons.Contacts, RmuixIcons.Settings)

Scaffold(
    bottomBar = {
        FloatingNavigationBar(
            style = FloatingNavigationBarStyle.ColorOS
        ) {
            items.forEachIndexed { index, label ->
                FloatingNavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    icon = icons[index],
                    label = label
                )
            }
        }
    }
)
```

### Custom Colors

Both item components accept `colors` from `NavigationBarDefaults.navigationBarItemColors()`. For example, use the theme's primary color for the selected item:

```kotlin
val itemColors = NavigationBarDefaults.navigationBarItemColors(
    unselectedContentColor = RmuixTheme.colorScheme.onSurfaceContainer,
    selectedContentColor = RmuixTheme.colorScheme.primary,
)

NavigationBar {
    items.forEachIndexed { index, label ->
        NavigationBarItem(
            selected = selectedIndex == index,
            onClick = { selectedIndex = index },
            icon = icons[index],
            label = label,
            colors = itemColors,
        )
    }
}
```

Colors are base colors: their alpha is multiplied by `0.4` when unselected, `0.6` when unselected and pressed, and `0.5` when selected and pressed. Selected content defaults to `RmuixTheme.colorScheme.primary`.

## Properties

### NavigationBar Properties

| Property Name              | Type                     | Description                         | Default Value                         | Required |
| -------------------------- | ------------------------ | ----------------------------------- | ------------------------------------- | -------- |
| modifier                   | Modifier                 | Modifier applied to the nav bar     | Modifier                              | No       |
| color                      | Color                    | Background color of the nav bar     | RmuixTheme.colorScheme.surface        | No       |
| showDivider                | Boolean                  | Show top divider line or not        | true                                  | No       |
| defaultWindowInsetsPadding | Boolean                  | Apply default window insets padding | true                                  | No       |
| mode                       | NavigationBarDisplayMode | Display mode for items              | NavigationBarDisplayMode.IconAndText  | No       |
| content                    | @Composable RowScope.()  | The content of the nav bar          | -                                     | Yes      |

### FloatingNavigationBar Properties

| Property Name              | Type                     | Description                             | Default Value                           | Required |
| -------------------------- | ------------------------ | --------------------------------------- | --------------------------------------- | -------- |
| modifier                   | Modifier                 | Modifier applied to the nav bar         | Modifier                                | No       |
| style                      | FloatingNavigationBarStyle | Style variant (`ColorOS` or `Default`) | FloatingNavigationBarStyle.ColorOS     | No       |
| color                      | Color                    | Background color of the nav bar         | RmuixTheme.colorScheme.surfaceContainer | No       |
| cornerRadius               | Dp                       | Corner radius of the nav bar            | FloatingNavigationBarDefaults.CornerRadius | No |
| horizontalAlignment        | Alignment.Horizontal     | Horizontal alignment within its parent  | CenterHorizontally                      | No       |
| horizontalOutSidePadding   | Dp                       | Horizontal padding outside the nav bar  | FloatingNavigationBarDefaults.HorizontalOutSidePadding | No       |
| shadowElevation            | Dp                       | The shadow elevation of the nav bar     | FloatingNavigationBarDefaults.ShadowElevation          | No       |
| showDivider                | Boolean                  | Show divider line around the nav bar    | false                                   | No       |
| defaultWindowInsetsPadding | Boolean                  | Apply default window insets padding     | true                                    | No       |
| content                    | @Composable () -> Unit   | The content of the nav bar              | -                                       | Yes      |

### FloatingNavigationBarDefaults Object

The FloatingNavigationBarDefaults object provides ColorOS 17 default values for FloatingNavigationBar and FloatingNavigationBarItem components.

#### Constants

| Constant Name            | Type  | Description                                   | Default Value |
| ------------------------ | ----- | --------------------------------------------- | ------------- |
| BarHeight                | Dp    | ColorOS 17 floating bar height                | 56.dp         |
| ItemHeight               | Dp    | ColorOS 17 item height                        | 48.dp         |
| ItemMaxWidth             | Dp    | ColorOS 17 item max width                     | 70.dp         |
| CornerRadius             | Dp    | ColorOS 17 corner radius                      | 28.dp         |
| HorizontalOutSidePadding | Dp    | Horizontal outside padding                    | 36.dp         |
| ShadowElevation          | Dp    | Shadow elevation                              | 1.dp          |
| HorizontalPadding        | Dp    | Horizontal padding inside the bar             | 16.dp         |
| ItemSpacing              | Dp    | Spacing between items                         | 8.dp          |
| IconSize                 | Dp    | Icon size                                     | 24.dp         |
| IconTextGap              | Dp    | Gap between icon and title                    | 2.dp          |
| NaviDistance             | Dp    | Distance above bottom navigation inset        | 8.dp          |
