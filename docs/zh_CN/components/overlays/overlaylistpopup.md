---
title: OverlayListPopup
requiresScaffoldHost: true
prerequisites:
  - 必须在 `Scaffold` 内使用以提供 `RmuixPopupHost`
  - 在 `Scaffold` 外使用会导致弹窗内容不渲染
  - 支持多个嵌套或并列的 `Scaffold`，无需额外配置
hostComponent: Scaffold
popupHost: RmuixPopupHost
---

# OverlayListPopup

`OverlayListPopup` 是 Rmuix 中的弹出列表组件，用于显示包含多个选项的弹出菜单。它提供了一个轻量级的、浮动的临时列表，适用于各种下拉菜单、上下文菜单等场景。

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=overlayListPopup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger 前置条件
此组件依赖于 `Scaffold` 提供的 `RmuixPopupHost` 来渲染弹窗内容。必须在 `Scaffold` 内部使用，否则弹窗内容将无法正常渲染。
:::

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `coui_popup_list_window_*` 尺寸。

- 弹窗宽度范围是 `coui_popup_list_window_min_width` = **168dp** 到 `coui_popup_list_window_max_width` = **256dp**；`ListPopupColumn` 里写死的是 200dp / 288dp，`ListPopupDefaults.MinWidth` 也是 200dp（而文档正引用它）。
- `COUIPopupListWindow.getMainMenuMinWidth()`：菜单**带子菜单**时用 `coui_popup_list_window_width_with_icon` = **196dp**（子菜单箭头就是这里的"图标"），否则用 `coui_popup_list_window_min_width` = 168dp；以 `ListPopupStockWidthWithIcon` 暴露给需要该预设的调用方，列表本身仍按内容测量而不强制。
- 弹窗背景来自 `coui_popup_window_background.xml`：圆角 **12dp**（`coui_round_corner_m`），填充 `couiColorBackgroundTop`（`#FFFFFF` / `#333333`）。此前用的是 9dp 圆角与主题 `surfaceContainer`。
- `ListPopupDefaults` 新增 `MaxWidth`，让表面默认值与列宽钳制来自同一组常量。

**BREAKING：** 弹窗默认宽度变窄（168dp … 256dp，原 200dp … 288dp），圆角由 9dp 改为 12dp，背景由主题 `surfaceContainer` 改为官方 `#FFFFFF` / `#333333`。

**级联重叠已实现：** 水平恒为 `coui_popup_list_window_horizontal_overlap_between_main_and_sub_menu`（14dp，子菜单起点 = 主菜单右边 − 14dp，左侧放不下时翻到左边）；垂直**仅在紧凑窗口**再上移 `coui_popup_list_window_vertical_overlap_between_main_and_sub_menu`（14dp），常规窗口子菜单顶部与行顶对齐。见 `ListPopupDefaults.rememberCascadingSubmenuPositionProvider(compactWindow)`。**仍待复核：** 行级 token（`coui_popup_list_window_item_min_height` 40dp、`…_item_title_text_size` 16sp、`…_item_icon_margin_left` 20dp、`…_item_title_margin_left` 12dp / right 24dp）属于 OPPO 的菜单行，内容由调用方提供，本容器不负责；竖直共享遮罩**已实现**：`ListPopupColumn` 用一层共享遮罩替换行自身的 ripple（对应 `COUITouchRecyclerView`）——按压/悬停高亮，16.1 分支半径 28dp、左右内缩 8dp、颜色取 `?attr/couiColorPress` / `?attr/couiColorHover`，指针在行间移动时高亮跟着滑动（遮罩按 `coui_popup_list_window_shared_mask_smooth_round_corner_weight` 在可用时画 squircle 路径，否则退回普通圆角矩形）。`coui_popup_list_padding_vertical` **已实现**：列内容现在上下各留 8dp（`ListPopupDefaults.VerticalContentPadding`），对应 `COUIPopupListWindow.applyMenuContainerPaddingForRecyclerView()`。分隔线已提供为 `ListPopupDivider`（0.5dp 细线，`?attr/couiColorDivider`，两侧 20dp 内缩，`withIcon = true` 时起始 52dp）与 `ListPopupGroupDivider`（4dp 色块，`#14000000`，无夜间变体）；容器内容由调用方提供，因此分隔线也由调用方放置。混合列表的分组分隔线**已提供**：`ListPopupMixedGroupDivider` 对应 `MixedPopupListAdapter` —— 行高 `coui_popup_list_group_divider_total_height` **12dp**，`coui_popup_list_divider_height` **0.5dp** 的线（`?attr/couiColorDivider`）上下各内缩 `(12 - 0.5) / 2 = 5.75dp`，左右内缩 `coui_popup_list_default_divider_margin_horizontal`（20dp）。两条分隔线都按 hairline（至少 1 物理像素）绘制：0.5dp 的盒子在 1x 密度下会被取整成 0，之前 `height(0.5.dp)` 的写法在桌面端根本看不见。分隔线全集至此齐了；级联重叠由 `rememberCascadingSubmenuPositionProvider` 负责。
## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.overlay.OverlayListPopup
import io.github.ra1nify.rmuix.kmp.basic.ListPopupColumn
```

## 基本用法

OverlayListPopup 组件可用于创建简单的下拉菜单：

```kotlin
var showPopup by remember { mutableStateOf(false) }
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("选项 1", "选项 2", "选项 3")

Scaffold {
    Box {
        TextButton(
            text = "点击显示菜单",
            onClick = { showPopup = true }
        )
        OverlayListPopup(
            show = showPopup,
            alignment = PopupPositionProvider.Align.Start,
            onDismissRequest = { showPopup = false } // 关闭弹窗菜单
        ) {
            ListPopupColumn {
                items.forEachIndexed { index, string ->
                    DropdownImpl(
                        text = string,
                        optionSize = items.size,
                        isSelected = selectedIndex == index,
                        index = index,
                        onSelectedIndexChange = {
                            selectedIndex = index
                            showPopup = false // 关闭弹窗菜单
                        }
                    )
                }
            }
        }
    }
}
```

## 组件状态

### 不同的对齐方式

OverlayListPopup 可以设置不同的对齐选项：

```kotlin
var showPopup by remember { mutableStateOf(false) }

OverlayListPopup(
    show = showPopup,
    onDismissRequest = { showPopup = false }, // 关闭弹窗菜单
    alignment = PopupPositionProvider.Align.Start
) {
    ListPopupColumn {
        // 自定义内容
    }
}
```

### 禁用窗口变暗

```kotlin
var showPopup by remember { mutableStateOf(false) }

OverlayListPopup(
    show = showPopup,
    onDismissRequest = { showPopup = false } // 关闭弹窗菜单
    enableWindowDim = false // 禁用变暗层
) {
    ListPopupColumn {
        // 自定义内容
    }
}
```

## 属性

### OverlayListPopup

| 属性名                | 类型                        | 说明                                 | 默认值                                     |
| --------------------- | --------------------------- | ------------------------------------ | ------------------------------------------ |
| show                  | Boolean                     | 是否显示弹窗                         | -                                          |
| popupModifier         | Modifier                    | 应用于弹窗容器的修饰符               | Modifier                                   |
| popupPositionProvider | PopupPositionProvider       | 提供弹窗的位置计算逻辑               | ListPopupDefaults.DropdownPositionProvider |
| alignment             | PopupPositionProvider.Align | 指定弹窗相对于锚点的对齐方式         | PopupPositionProvider.Align.Start          |
| enableWindowDim       | Boolean                     | 是否在弹窗显示时使背景变暗           | true                                       |
| onDismissRequest      | (() -> Unit)?               | 当用户请求关闭（例如点击外部）时触发 | null                                       |
| onDismissFinished     | (() -> Unit)?               | 关闭动画完成后调用；若关闭过程被中途取消（例如 `show` 被设回 true），则不会触发 | null    |
| maxHeight             | Dp?                         | 弹窗内容的最大高度                   | null                                       |
| minWidth              | Dp                          | 弹窗内容的最小宽度                   | ListPopupDefaults.MinWidth                 |
| renderInRootScaffold  | Boolean                     | 是否在根（最外层）Scaffold 中渲染弹窗。为 true 时，弹窗覆盖全屏。为 false 时，在当前 Scaffold 的范围内渲染并进行位置补偿 | true |
| content               | @Composable () -> Unit      | 要在弹窗内显示的内容                 | -                                          |

### ListPopupColumn

| 属性名  | 类型                   | 说明                   | 默认值 |
| ------- | ---------------------- | ---------------------- | ------ |
| content | @Composable () -> Unit | 要在列内显示的列表内容 | -      |

该列负责弹层的按压反馈：它绘制原厂共享遮罩并关闭行自身的 ripple，按压/悬停的行由这一层滑动高亮表示。

### DropdownImpl

`DropdownImpl` 可作为 `ListPopupColumn` 内的标准选项行使用。设置 `enabled = false` 可以禁用某一行；禁用行不可点击，并使用禁用文本颜色。

```kotlin
DropdownImpl(
    text = "禁用选项",
    optionSize = items.size,
    isSelected = false,
    index = 1,
    enabled = false,
    onSelectedIndexChange = {}
)
```

| 属性名                | 类型           | 说明               | 默认值                            |
| --------------------- | -------------  | ------------------ | --------------------------------- |
| text                  | String         | 选项显示文本       | -                                 |
| optionSize            | Int            | 选项总数           | -                                 |
| isSelected            | Boolean        | 此选项是否被选中   | -                                 |
| index                 | Int            | 此选项的索引       | -                                 |
| dropdownColors        | DropdownColors | 选项颜色配置       | DropdownDefaults.dropdownColors() |
| enabled               | Boolean        | 此选项是否可点击   | true                              |
| onSelectedIndexChange | (Int) -> Unit  | 点击此选项时的回调 | -                                 |

### PopupPositionProvider.Align

| 值          | 说明                         |
| ----------- | ---------------------------- |
| Start       | 将弹窗对齐到锚点的起始端     |
| End         | 将弹窗对齐到锚点的结束端     |
| TopStart    | 将弹窗对齐到锚点的顶部起始端 |
| TopEnd      | 将弹窗对齐到锚点的顶部结束端 |
| BottomStart | 将弹窗对齐到锚点的底部起始端 |
| BottomEnd   | 将弹窗对齐到锚点的底部结束端 |
