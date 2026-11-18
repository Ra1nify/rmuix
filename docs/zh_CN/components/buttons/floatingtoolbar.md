# FloatingToolbar

`FloatingToolbar` 是 Rmuix 中的悬浮工具栏组件，它将其内容渲染在一个具有圆角背景的容器中，可以水平或垂直排列。实际的屏幕放置由父组件（通常是 `Scaffold`）处理。

此组件通常与 `Scaffold` 结合使用，放置在页面的特定位置，提供快捷操作或信息展示。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=floatingToolbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.FloatingToolbar
import io.github.ra1nify.rmuix.kmp.basic.FloatingToolbarDefaults
import io.github.ra1nify.rmuix.kmp.basic.ToolbarPosition // 用于 Scaffold
```

## 基本用法

```kotlin
Scaffold(
    floatingToolbar = {
        FloatingToolbar {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) { // 或 Column
                IconButton(onClick = { /* 操作 1 */ }) {
                    Icon(RmuixIcons.Edit, contentDescription = "编辑")
                }
                IconButton(onClick = { /* 操作 2 */ }) {
                    Icon(RmuixIcons.Delete, contentDescription = "删除")
                }
            }
        }
    },
    floatingToolbarPosition = ToolbarPosition.BottomCenter // 设置位置
)
```

## 属性

### FloatingToolbar 属性

| 属性名                     | 类型                   | 说明                       | 默认值                                 | 是否必须 |
| -------------------------- | ---------------------- | -------------------------- | -------------------------------------- | -------- |
| modifier                   | Modifier               | 应用于工具栏的修饰符       | Modifier                               | 否       |
| color                      | Color                  | 工具栏背景颜色             | FloatingToolbarDefaults.defaultColor() | 否       |
| cornerRadius               | Dp                     | 工具栏圆角半径             | FloatingToolbarDefaults.CornerRadius   | 否       |
| outSidePadding             | PaddingValues          | 工具栏外部的内边距         | FloatingToolbarDefaults.OutSidePadding | 否       |
| shadowElevation            | Dp                     | 工具栏的阴影高度           | FloatingToolbarDefaults.ShadowElevation                                   | 否       |
| showDivider                | Boolean                | 是否显示工具栏周围的分割线 | false                                  | 否       |
| groupMargin                | Dp?                    | 内容的水平内边距；`null` 时按窗口宽度分档   | null                                   | 否       |
| content                    | @Composable () -> Unit | 工具栏内容区域的可组合函数 | -                                      | 是       |

## 随窗口宽度变化的边距

`COUIBottomFloatingToolbar.changePadding()` 由 `COUIResponsiveUtils.isSmall/Medium/LargeScreen` 选择
`coui_floating_toolbar_group_margin_small_screen` / `coui_floating_toolbar_group_margin_medium_screen` / `coui_floating_toolbar_group_margin_large_screen` 作为工具栏自身的左右内距，判定标准就是
Material 的 `WindowWidthSizeClass`（Compact < **600dp**，Medium **600..840dp**，Expanded ≥ **840dp**）。
`groupMargin = null` 即复现该行为：**16dp / 24dp / 40dp**。
`FloatingToolbarDefaults.groupMarginFor(widthDp)` 是纯函数解析器，已知宽度的调用方可直接复用。

### FloatingToolbarDefaults 对象

| 属性名         | 类型                    | 说明           | 值                                      |
| -------------- | ----------------------- | -------------- | --------------------------------------- |
| CornerRadius   | Dp                      | 默认圆角半径   | 50.dp                                   |
| ShadowElevation | Dp                     | 默认投影       | 4.dp                                    |
| defaultColor() | @Composable () -> Color | 默认背景颜色   | RmuixTheme.colorScheme.surfaceContainer |
| OutSidePadding | PaddingValues           | 默认外部内边距 | PaddingValues(12.dp, 8.dp)              |

### ToolbarPosition (用于 Scaffold)

请参考 [Scaffold](../containers/scaffold#toolbarposition-选项) 文档中的 `ToolbarPosition` 选项。

## 进阶用法

### 自定义样式

```kotlin
FloatingToolbar(
    color = RmuixTheme.colorScheme.primaryContainer,
    cornerRadius = 16.dp,
    outSidePadding = PaddingValues(24.dp),
    showDivider = false
) {
    Row(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) { // 或 Column
        IconButton(onClick = { /* 操作 1 */ }) {
            Icon(RmuixIcons.Edit, contentDescription = "编辑", tint = RmuixTheme.colorScheme.onPrimaryContainer)
        }
        IconButton(onClick = { /* 操作 2 */ }) {
            Icon(RmuixIcons.Delete, contentDescription = "删除", tint = RmuixTheme.colorScheme.onPrimaryContainer)
        }
    }
}
```

### 垂直排列内容

```kotlin
FloatingToolbar {
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = { /* 操作 1 */ }) {
            Icon(RmuixIcons.Edit, contentDescription = "编辑")
        }
        IconButton(onClick = { /* 操作 2 */ }) {
            Icon(RmuixIcons.Delete, contentDescription = "删除")
        }
    }
}
```

### 结合 Scaffold 改变位置

```kotlin
Scaffold(
    floatingToolbar = {
        FloatingToolbar {
            // 工具栏内容
        }
    },
    floatingToolbarPosition = ToolbarPosition.CenterEnd // 放置在右侧居中
)
```

## EditToolbar（`COUIBottomFloatingEditToolbar`）

`EditToolbar` 是 ColorOS 的底部悬浮编辑工具条：每个动作对应一个 `COUIBottomFloatingToolbarItemView`，放在同一个
胶囊（`COUIBottomFloatingToolbar`）里。相册的 `BottomFloatingMenuBar`（相册集/时间轴/沉浸页）就是该控件加弹出层
与显示/隐藏弹簧。

```kotlin
EditToolbar(
    items =
        listOf(
            EditToolbarItem(icon = RmuixIcons.Share, title = "分享"),
            EditToolbarItem(icon = RmuixIcons.Edit, title = "编辑", redDot = true),
            EditToolbarItem(icon = RmuixIcons.Delete, title = "删除", enabled = false),
        ),
    onItemClick = { index -> },
)
```

| 官方属性 / 来源 | 参数或默认值 | 依据 |
| :--- | :--- | :--- |
| `coui_bottom_floating_edit_toolbar_min_height_without_padding_vertical` | `MinHeightWithoutVerticalPadding` = 88dp | 资源 |
| 水平内边距 | 小/中/大 = 16dp / 24dp / 40dp | `coui_bottom_floating_edit_toolbar_padding_horizontal_*` |
| 菜单高度 | 小屏 56dp，中/大屏 44dp | `coui_bottom_floating_navigation_bar_height` / `coui_bottom_floating_edit_toolbar_menu_height_medium_large` |
| item 样式 | `horizontalItems = null` 跟随官方 `setHorizontalStyle(!isSmallScreen)`（<600dp 为竖排样式 3，否则横排样式 4） | `COUIBottomFloatingEditToolbar` |
| item 高度 | 48dp（样式 3）/ 40dp（样式 4） | `coui_bottom_floating_navigation_bar_item_height` / `coui_bottom_floating_edit_toolbar_item_height_horizontal` |
| item 最大宽 | 竖排 70dp / 横排 98dp；单 item 148dp / 152dp | `_item_max_width_*` / `_single_item_width_*` |
| 图标 | 24dp，item 最小宽 44dp | `coui_floating_toolbar_item_icon_*`, `_item_width` |
| 图标-文字间距 | 竖排 2dp / 横排 4dp；竖排图标上边距 4dp | `_item_icon_title_gap_*`, `_item_icon_top_margin` |
| 文字 | 竖排 10sp / 横排 14sp（`couiTextButtonM`） | item view `applyEditToolbar*Style()` |
| item 间距 | 8dp | `coui_bottom_floating_navigation_bar_item_space` |
| 红点 | 6dp 圆点或 16dp 数字角标（宽 20dp/26dp，10sp 白字），上溢 4dp、右溢 6-10dp | `COUIHintRedDot` token |

颜色：内容 `?attr/couiColorLabelPrimaryVariant`（`#B8000000` / `#B8FFFFFF`）、禁用
`?attr/couiColorLabelTertiary`（`#42000000` / `#4DFFFFFF`）、红点 `coui_color_additional_red`
（`#EB3B2F` / `#EB493D`）、角标文字 `#FFFFFF`。

已知差异：官方的竖向显示/隐藏弹簧（`TOOLBAR_TRANSLATION_Y_SPRING_RESPONSE` 0.38 / bounce 0.25）与相册弹窗不在本组件内；
胶囊复用 `FloatingToolbar`，显示/隐藏动画由调用方负责。`couiEditToolbarMenu` 菜单资源改由 `items` 传入。
