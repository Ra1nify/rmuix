# 浮层与菜单

对话框、底部面板、弹窗与动作菜单，含玻璃材质变体。

## 选型指引

| 场景 | 推荐组件 |
| :--- | :--- |
| 需要 Scaffold（同场景） | Overlay* 系列 |
| 独立窗口 | Window* 系列 |
| 磨砂材质 | Glass* 系列 |
| 动作 / 选择菜单 | Overlay/Window DropdownMenu |
| 两级菜单 | *IconCascadingDropdownMenu |

## 组件

| 组件 | 说明 | 常用场景 |
| :--- | :--- | :--- |
| [OverlayListPopup](./overlays/overlaylistpopup) | 基于 BasicComponent 的列表弹窗组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 选项选择、功能列表 |
| [OverlayCascadingListPopup](./overlays/overlaycascadinglistpopup) | 二级级联列表弹窗组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 子菜单、分组动作面板 |
| [OverlayBottomSheet](./overlays/overlaybottomsheet) | 基于 BasicComponent 的底部抽屉组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 底部抽屉、附加选项 |
| [OverlayDialog](./overlays/overlaydialog) | 基于 BasicComponent 的对话弹窗组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 提示、确认操作 |
| [WindowListPopup](./overlays/windowlistpopup) | 窗口级列表弹窗组件 | 选项选择、功能列表 |
| [WindowCascadingListPopup](./overlays/windowcascadinglistpopup) | 窗口级二级级联列表弹窗组件 | 子菜单、分组动作面板 |
| [WindowBottomSheet](./overlays/windowbottomsheet) | 窗口级底部抽屉组件 | 底部抽屉、附加选项 |
| [WindowDialog](./overlays/windowdialog) | 窗口级对话框组件 | 提示、确认操作 |
| [OverlayDropdownMenu](./overlays/overlaydropdownmenu) | 基于 BasicComponent 的动作菜单组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 动作菜单、多选场景 |
| [OverlayIconDropdownMenu](./overlays/overlayicondropdownmenu) | 基于 IconButton 的动作菜单组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 工具栏动作、更多菜单 |
| [OverlayIconCascadingDropdownMenu](./overlays/overlayiconcascadingdropdownmenu) | 基于 IconButton 的二级级联动作菜单组件（使用 RmuixPopupUtils，需在 `Scaffold` 中使用） | 含子菜单的工具栏动作 |
| [WindowDropdownMenu](./overlays/windowdropdownmenu) | 窗口级动作菜单组件 | 动作菜单、多选场景 |
| [WindowIconDropdownMenu](./overlays/windowicondropdownmenu) | 基于 IconButton 的窗口级动作菜单组件 | 工具栏动作、更多菜单 |
| [WindowIconCascadingDropdownMenu](./overlays/windowiconcascadingdropdownmenu) | 基于 IconButton 的窗口级二级级联动作菜单组件 | 含子菜单的工具栏动作 |
| [GlassOverlayDialog](./overlays/glassdialog) | 带 backdrop 采样模糊的 OverlayDialog | - |
| [GlassWindowDialog](./overlays/glassdialog) | 接平台窗口模糊的 WindowDialog | - |
| [GlassOverlayBottomSheet](./overlays/glassbottomsheet) | 带 backdrop 采样模糊的 OverlayBottomSheet | - |
| [GlassWindowBottomSheet](./overlays/glassbottomsheet) | 接平台窗口模糊的 WindowBottomSheet | - |
| [GlassFloatingNavigationBar](./overlays/glassfloatingnavigationbar) | 带 ColorOS bottom-bar 材质的悬浮胶囊导航栏 | - |

## 组合示例

```kotlin
Scaffold {
    Button(onClick = { showDialog = true }) { Text("打开") }
    OverlayDialog(show = showDialog, onDismiss = { showDialog = false }) {
        Text("对话框内容")
    }
}
```

## 相关类型

[容器与布局](./containers) · [文本与图标](./text-and-icons) · [按钮与工具条](./buttons) · [输入与表单](./input) · [选择与调节](./selection) · [导航](./navigation) · [反馈与状态](./feedback) · [偏好设置](./preferences) · [图像与特效](./media)

包内索引：[基础组件](./basic/) · [扩展组件](./extended/) · [Extend 组件](./extend/) · [Glass 组件](./glass/)
