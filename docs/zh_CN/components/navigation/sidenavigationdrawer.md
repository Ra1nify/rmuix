# SideNavigationDrawer

ColorOS 的侧边抽屉：`COUISideNavigationBar`（`com.coui.appcompat.sidenavigation`），`DrawerLayout` 的子类。

从行首边缘滑入的面板，带遮罩和一条分隔细线。拖动面板可关闭；从行首 20dp 的边缘条拖出可打开；松手后吸附到
更近的一侧。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.SideNavigationDrawer
import io.github.ra1nify.rmuix.kmp.basic.rememberSideNavigationDrawerState
```

## 基本用法

```kotlin
val state = rememberSideNavigationDrawerState()
val scope = rememberCoroutineScope()

SideNavigationDrawer(
    state = state,
    drawerContent = {
        Column(Modifier.padding(16.dp)) {
            Text("相册")
            Text("照片")
        }
    },
) {
    // 抽屉后面的界面
}

// 从按钮 / 返回键里打开
scope.launch { state.open() }
```

## 参数

| 参数名        | 类型                       | 说明                     | 默认值 |
| :------------ | :------------------------- | :----------------------- | :----- |
| modifier      | Modifier                   | 应用到抽屉               | Modifier |
| state         | SideNavigationDrawerState  | 开合滑动位置             | rememberSideNavigationDrawerState() |
| width         | Dp                         | 面板宽度                 | 300dp |
| colors        | SideNavigationDrawerColors | 遮罩、分隔线、面板颜色    | SideNavigationDrawerDefaults.drawerColors() |
| drawerContent | @Composable () -> Unit      | 面板内容                 | - |
| content       | @Composable () -> Unit      | 面板下方的内容           | - |

`SideNavigationDrawerState` 提供 `isOpen`、`isClosed`，以及挂起的 `open()` / `close()`。

## SideNavigationDrawerDefaults

| 名称             | 类型  | 来源                                                       | 值 |
| :--------------- | :---- | :--------------------------------------------------------- | :---- |
| Width            | Dp    | `coui_side_navigation_bar_min_width`（临时模式宽度）          | 300dp |
| MaxWidth         | Dp    | `coui_side_navigation_bar_max_width`                        | 280dp |
| WIDTH_PROPORTION | Float | `COUISideNavigationBar.DRAWER_WIDTH_PROPORTION`              | 0.382 |
| DividerWidth     | Dp    | `coui_side_navigation_drawer_divider_width`                 | 0.33dp |
| EdgeWidth        | Dp    | 框架的 `ViewDragHelper.EDGE_SIZE`（非 COUI dimen）            | 20dp |
| OPEN_MILLIS      | Int   | 吸附时长；dump 里的 scroller 不可读                          | 250 |
| CLOSE_MILLIS     | Int   | 吸附时长                                                    | 200 |

## 颜色

| 槽位         | 来源                      | 浅色        | 深色        |
| :----------- | :----------------------- | :---------- | :---------- |
| scrimColor   | `coui_color_mask`         | `#33000000` | `#99000000` |
| dividerColor | `coui_color_divider`      | `#1F000000` | `#33FFFFFF` |
| drawerColor  | 主题 surface              | - | - |

## 宽度行为

`coui_side_navigation_bar_min_width` **不是**下限：`COUISideNavigationBar` 把它当作临时（浮层）模式的固定宽度，
也就是手机尺寸屏幕实际拿到的宽度。在中等/大屏上官方改用
`min(coui_side_navigation_bar_max_width, 屏幕宽 × 0.382)`。本组件只实现了临时模式分支，另一分支的数值在
`SideNavigationDrawerDefaults` 里暴露给需要的调用方。

## 已知差异

- 中等/大屏的固定宽度分支不会自动生效。
- `isParentChildHierarchy`（两级抽屉层级）未建模。
- 遮罩与吸附曲线来自 `DrawerLayout` 的框架 scroller，dump 读不到，这里用普通 tween。
