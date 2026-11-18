# NavigationRail

`NavigationRail` 是适用于宽屏设备的侧边导航组件，支持固定收起、固定展开和状态控制三种布局。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=navigationRail" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `com.coui.appcompat.material.navigationrail.NavigationRailView` / `NavigationRailItemView` / `NavigationRailMenuView`、COUI 侧边导航样式、`mtrl_navigation_rail_*` / `m3_navigation_rail_*` 尺寸与 `m3_navigation_rail_item_with_indicator_*_tint` selector。

- COUI 的侧边导航就是 Material 的侧边导航套一层 COUI item 布局：`NavigationRailItemView` 只覆写了 `getItemLayoutResId()` / `getItemDefaultMarginResId()`，尺寸全部来自 Material token。
- 收起宽度为 `mtrl_navigation_rail_default_width` = **72dp**（原 80dp）；图标为 `mtrl_navigation_rail_icon_size` = **24dp**（原 28dp）。
- 展开宽度取 `m3_navigation_rail_min_expanded_width` = **220dp**（原 240dp），Material 允许范围是 220dp–360dp。
- 标签字号收起时 `mtrl_navigation_rail_text_size` = 12dp，展开时 `mtrl_navigation_rail_active_text_size` = 14dp（原展开为 16sp）。
- 颜色走 Material selector：`m3_navigation_rail_item_with_indicator_icon_tint` 选中图标用 `colorOnSecondaryContainer`、未选中用 `colorOnSurfaceVariant`；`m3_navigation_rail_item_with_indicator_label_tint` 用 `colorOnSurface` / `colorOnSurfaceVariant`；选中指示块填充 `colorSecondaryContainer`。库内原先图标和文字共用一个 `onSurfaceContainer`、指示块用 `surfaceContainerHigh`，选中态没有任何颜色区分。
- 新增 `NavigationRailDefaults.navigationRailItemColors()` 与 `NavigationRailItem` 的 `colors` 参数来承载这套角色。

**BREAKING：** 展开宽度 240dp → 220dp（`m3_navigation_rail_min_expanded_width`）、图标 28dp → 24dp、展开标签 16sp → 14sp，条目的图标与指示器颜色改为随选中切换（不再固定 `onSurfaceContainer` / `surfaceContainerHigh`）。

折叠宽度**保持 Material 3 的 80dp**：迁移过程中一度改成 72dp，2026-11-14 依据「设置应用用的是 Compose M3」（见下文官方依据一节）改回——72dp 与 M3、与任何 `coui_*` dimen 都对不上。

**仍待复核 / 更正：** COUI 专有尺寸 `coui_navigation_rail_item_text_size`（10dp）只被声明、没有被任何反编译类引用，
很可能是 OPPO 应用层主题在用，本轮不采用。`NavigationRailView.getMaxItemCount()` 返回 **7**，但它是
`NavigationBarMenu` 的**硬上限**：Stock 里添加第 8 个菜单项会抛 `IllegalArgumentException`
（`NavigationBarMenu.addInternal`），并非视觉上限；Settings 的 rail 项目数远小于 7。Compose 的
`content` lambda 无法统计子项数量，因此不强制同样的异常。Material 的展开项 token
（`m3_navigation_rail_expanded_item_spacing` 0dp、`m3_navigation_rail_expanded_leading_trailing_space` 20dp、
`m3_navigation_rail_max_expanded_width` 360dp、`m3_navigation_rail_min_expanded_width` 220dp）属于 APK 内的
Material 组件，不是 ColorOS 专属 rail，故未采用。
## 官方依据：是 Material 3，不是 COUI

设置应用用的是 **Compose Material 3** 的侧栏（`WideNavigationRail_androidKt`、`DefaultModalWideNavigationRailOverride`），
所以权威尺寸是 `m3_navigation_rail_*`，不是 `coui_*`：

| token | 值 | 对应 |
| :--- | ---: | :--- |
| `m3_navigation_rail_min_expanded_width` | **220dp** | `NavigationRailDefaults.ExpandedWidth`（本来就一致） |
| `m3_navigation_rail_max_expanded_width` | 360dp | 展开态上限 |
| `m3_navigation_rail_item_min_height` | 60dp | 项的最小高度 |
| `m3_navigation_rail_item_padding_top` / `_bottom` | 4dp / 12dp | 项的纵向内距 |
| `m3_navigation_rail_item_active_indicator_margin_horizontal` | 4dp | 指示器横向内缩 |
| `m3_navigation_rail_expanded_active_indicator_height` | 56dp | 展开态指示器高度 |
| `m3_navigation_rail_expanded_leading_trailing_space` | 20dp | 展开态内容内缩 |
| `m3_navigation_rail_icon_label_padding` / `_horizontal_padding` | 4dp / 8dp | 图标与文字间距 |
| `m3_navigation_rail_label_padding_horizontal` | 2dp | 文字内缩 |

唯一的 `coui_*` 只有 `coui_navigation_rail_item_text_size`（10dp），且没有类读取它。

**折叠宽度：** M3 的 `NavigationRailWidth` 是 **80dp**。`NavigationRailDefaults.MinWidth` 于 2026-11-14 从 72dp 改为 **80dp**——
72dp 与 M3、与任何 dump token 都对不上。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.NavigationRail
import io.github.ra1nify.rmuix.kmp.basic.NavigationRailItem
import io.github.ra1nify.rmuix.kmp.basic.rememberNavigationRailState
```

## 基本用法

默认使用固定收起布局：

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("主页", "个人", "设置")
val icons = listOf(RmuixIcons.VerticalSplit, RmuixIcons.Contacts, RmuixIcons.Settings)

Row {
    NavigationRail {
        items.forEachIndexed { index, label ->
            NavigationRailItem(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                icon = icons[index],
                label = label
            )
        }
    }
    // 内容区域
}
```

## 布局模式

使用固定布局重载，通过 `expanded` 选择布局：

```kotlin
NavigationRail(expanded = true) {
    // NavigationRailItem 内容
}
```

使用状态重载可显示切换按钮，并在两种布局间动画切换：

```kotlin
val railState = rememberNavigationRailState()

NavigationRail(state = railState) {
    // NavigationRailItem(...)
}
```

可以通过 `railState.expand()`、`railState.collapse()` 或 `railState.toggle()` 控制布局。省略
`state` 和 `expanded` 时使用固定收起布局。

## 属性

### NavigationRail 重载

`NavigationRail` 提供两个重载，二选一使用：

#### 固定布局

| 参数 | 类型 | 说明 | 默认值 | 是否必须 |
| ---- | ---- | ---- | ------ | -------- |
| expanded | Boolean | 是否使用展开布局 | false | 否 |

#### 可切换布局

| 参数 | 类型 | 说明 | 默认值 | 是否必须 |
| ---- | ---- | ---- | ------ | -------- |
| state | NavigationRailState | 控制布局并显示切换按钮 | - | 是 |
| expandContentDescription | String | 收起时切换按钮的描述 | NavigationRailDefaults.ExpandContentDescription | 否 |
| collapseContentDescription | String | 展开时切换按钮的描述 | NavigationRailDefaults.CollapseContentDescription | 否 |

### NavigationRail 公共参数

| 属性名                     | 类型                                   | 说明                                          | 默认值                            | 是否必须 |
| -------------------------- | -------------------------------------- | --------------------------------------------- | --------------------------------- | -------- |
| modifier                   | Modifier                               | 应用于 NavigationRail 的修饰符                | Modifier                          | 否       |
| header                     | @Composable (ColumnScope.() -> Unit)?  | 头部内容（通常是 FAB 或 Logo）                | null                              | 否       |
| color                      | Color                                  | NavigationRail 的背景颜色                     | RmuixTheme.colorScheme.surface    | 否       |
| showDivider                | Boolean                                | 是否在 NavigationRail 和内容之间显示分割线    | true                              | 否       |
| defaultWindowInsetsPadding | Boolean                                | 是否对 NavigationRail 应用默认的窗口边距      | true                              | 否       |
| minWidth                   | Dp                                     | NavigationRail 收起时的最小宽度               | NavigationRailDefaults.MinWidth   | 否       |
| expandedWidth              | Dp                                     | NavigationRail 展开时的宽度                   | NavigationRailDefaults.ExpandedWidth | 否    |
| scrollState                | ScrollState                            | 内容列的滚动状态                              | rememberScrollState()             | 否       |
| content                    | @Composable ColumnScope.()             | NavigationRail 的内容                         | -                                 | 是       |

### NavigationRailItem 属性

| 属性名 | 类型        | 说明             | 默认值 | 是否必须 |
| ------ | ----------- | ---------------- | ------ | -------- |
| selected | Boolean     | 是否选中         | -      | 是       |
| onClick  | () -> Unit  | 点击时的回调     | -      | 是       |
| icon     | ImageVector | 该项的图标       | -      | 是       |
| label    | String      | 该项的标签文本   | -      | 是       |
| modifier | Modifier    | 应用于 NavigationRailItem 的修饰符 | Modifier | 否       |
| enabled  | Boolean     | 是否启用该项     | true     | 否       |
| colors   | NavigationRailItemColors | 图标、标签与选中指示块的颜色 | NavigationRailDefaults.navigationRailItemColors() | 否 |
| badge    | (@Composable () -> Unit)? | 显示在该项图标上的可选徽章，通常是一个 `RedDot` | null | 否 |

### NavigationRailDefaults 对象

NavigationRailDefaults 对象提供了 NavigationRail 和 NavigationRailItem 组件的默认值。

#### 常量

| 常量名                             | 类型     | 说明                         | 默认值 |
| ---------------------------------- | -------- | ---------------------------- | ------ |
| MinWidth                           | Dp       | 收起态的轨道宽度（`coui_navigation_rail_min_width`） | 72.dp |
| MenuWidth                          | Dp       | 单个项目的宽度（`coui_navigation_rail_menu_width`） | 64.dp |
| ExpandedWidth                      | Dp       | 展开态的轨道宽度（仅 M3，COUI 没有展开态） | 220.dp |
| VerticalPadding                    | Dp       | 内容垂直内边距               | 24.dp  |
| HeaderSpacing                      | Dp       | 头部后的间距                 | 24.dp  |
| IconSize                           | Dp       | 图标尺寸（`mtrl_navigation_rail_icon_size`） | 24.dp |
| IconTextSpacing                    | Dp       | 图标与文字之间的间距（`coui_navigation_rail_text_margin_top`） | 2.dp |
| ItemVerticalPadding                | Dp       | 每个项目的垂直内边距         | 12.dp  |
| LabelFontSize                      | TextUnit | 标签字号（`coui_side_navigation_rail_item_text_size`） | 10.sp |
| ExpandedLabelFontSize              | TextUnit | 展开时的标签字号（`mtrl_navigation_rail_active_text_size`） | 14.sp |
| ExpandedItemHorizontalMargin       | Dp       | 展开项与边缘之间的外边距     | 12.dp  |
| ExpandedItemCornerRadius           | Dp       | 选中药丸的圆角半径           | 16.dp  |
| CollapsedIndicatorVerticalPadding  | Dp       | 收起项指示器围绕图标的内边距 | 4.dp   |
| ExpandedItemContentHorizontalPadding | Dp     | 展开项内部的水平内边距       | 14.dp  |
| ExpandedItemContentVerticalPadding | Dp       | 展开项内部的垂直内边距       | 14.dp  |
| ExpandedItemIconTextSpacing        | Dp       | 展开项图标与文字间距         | 16.dp  |
| ExpandContentDescription           | String   | 收起时切换按钮的无障碍描述   | "Expand navigation rail" |
| CollapseContentDescription         | String   | 展开时切换按钮的无障碍描述   | "Collapse navigation rail" |

#### 方法

| 方法名 | 类型 | 说明 |
| --- | --- | --- |
| navigationRailItemColors() | NavigationRailItemColors | 创建官方项目颜色：图标 `colorOnSecondaryContainer` / `colorOnSurfaceVariant`，标签 `colorOnSurface` / `colorOnSurfaceVariant`，指示块 `colorSecondaryContainer` |

把返回值传给 `NavigationRailItem(colors = …)` 即可逐项覆盖。
### NavigationRailState

通过 `rememberNavigationRailState(initialValue)` 创建，并传入 `NavigationRail` 以让导航栏支持展开/收起。

| 成员         | 类型                | 说明                           |
| ------------ | ------------------- | ------------------------------ |
| currentValue | NavigationRailValue | 当前的展开状态值               |
| isExpanded   | Boolean             | 导航栏当前是否处于展开状态     |
| expand()     | fun                 | 展开导航栏                     |
| collapse()   | fun                 | 收起导航栏                     |
| toggle()     | fun                 | 在收起与展开之间切换           |

### NavigationRailValue

| 值        | 描述                              |
| --------- | --------------------------------- |
| Collapsed | 导航栏收起到 `MinWidth`。         |
| Expanded  | 导航栏展开到 `ExpandedWidth`。    |
