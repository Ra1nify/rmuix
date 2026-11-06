# NavigationBar

`NavigationBar` 是 Rmuix 中的底部导航栏组件，完全遵循 ColorOS 17 度量与视觉规范，用于在应用底部创建导航菜单。支持 2 到 5 个导航项，提供不同的显示模式（仅图标、图标和文本、仅选中项显示文本）。

`FloatingNavigationBar` 是一个悬浮样式的底部导航栏组件，同时支持 ColorOS 17 悬浮单胶囊样式 (`FloatingNavigationBarStyle.ColorOS`) 与旧版默认样式 (`FloatingNavigationBarStyle.Default`)。

这些组件通常与 `Scaffold` 组件结合使用，以便在应用程序的不同页面中保持一致的布局和行为。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=navigationBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.NavigationBar
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarDefaults
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarItem
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBar
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBarItem
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBarStyle
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarDisplayMode
```

## 基本用法

### NavigationBar

NavigationBar 组件可用于创建固定在底部的导航菜单：

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("首页", "我的", "设置")
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

FloatingNavigationBar 组件可用于创建悬浮在底部的单胶囊导航菜单：

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("首页", "我的", "设置")
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

### 自定义颜色

两种导航项都支持通过 `NavigationBarDefaults.navigationBarItemColors()` 创建的 `colors`。例如，使用主题主色显示选中项：

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

## 属性

### FloatingNavigationBar 属性

| 属性名                     | 类型                      | 说明                               | 默认值                                  | 是否必须 |
| -------------------------- | ------------------------- | ---------------------------------- | --------------------------------------- | -------- |
| modifier                   | Modifier                  | 应用于导航栏的修饰符               | Modifier                                | 否       |
| style                      | FloatingNavigationBarStyle | 样式变体 (`ColorOS` 或 `Default`)  | FloatingNavigationBarStyle.ColorOS      | 否       |
| color                      | Color                     | 导航栏背景颜色                     | RmuixTheme.colorScheme.surfaceContainer | 否       |
| cornerRadius               | Dp                        | 导航栏的圆角半径                   | FloatingNavigationBarDefaults.CornerRadius | 否   |
| horizontalAlignment        | Alignment.Horizontal      | 导航栏在其父容器中的水平对齐方式   | CenterHorizontally                      | 否       |
| horizontalOutSidePadding   | Dp                        | 导航栏外部的水平内边距             | FloatingNavigationBarDefaults.HorizontalOutSidePadding | 否       |
| shadowElevation            | Dp                        | 导航栏的阴影高度                   | FloatingNavigationBarDefaults.ShadowElevation          | 否       |
| showDivider                | Boolean                   | 是否显示导航栏周围的分割线         | false                                   | 否       |
| defaultWindowInsetsPadding | Boolean                   | 是否应用默认窗口嵌入边距           | true                                    | 否       |
| content                    | @Composable () -> Unit    | 导航栏的内容                       | -                                       | 是       |

### FloatingNavigationBarDefaults 对象

FloatingNavigationBarDefaults 对象提供了 ColorOS 17 悬浮单胶囊默认度量值。

#### 常量

| 常量名                   | 类型  | 说明                       | 默认值 (ColorOS 17) |
| ------------------------ | ----- | -------------------------- | ------------------- |
| BarHeight                | Dp    | 胶囊高度                   | 56.dp               |
| ItemHeight               | Dp    | 单项高度                   | 48.dp               |
| ItemMaxWidth             | Dp    | 单项最大宽度               | 70.dp               |
| CornerRadius             | Dp    | 胶囊圆角半径               | 28.dp               |
| HorizontalOutSidePadding | Dp    | 外部水平内边距             | 36.dp               |
| HorizontalPadding        | Dp    | 内部水平内边距             | 16.dp               |
| ItemSpacing              | Dp    | 项目间距                   | 8.dp                |
| IconSize                 | Dp    | 图标尺寸                   | 24.dp               |
| IconTextGap              | Dp    | 图文间距                   | 2.dp                |
| NaviDistance             | Dp    | 距离底部导航条空隙         | 8.dp                |
