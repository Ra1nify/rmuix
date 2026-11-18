---
title: WindowBottomSheet
requiresScaffoldHost: false
prerequisites:
  - 可以在任何地方使用，不需要 `Scaffold` 或 `RmuixPopupHost`
  - 在窗口层级渲染
hostComponent: None
popupHost: None
---

# WindowBottomSheet

`WindowBottomSheet` 是窗口级的底部抽屉组件。它使用平台 `Dialog` 渲染，不依赖 `Scaffold` 或 `RmuixPopupHost`。支持大屏优化的动效、系统返回手势关闭，以及在内容内部通过组合局部请求关闭。

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=windowBottomSheet" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: tip 提示
该组件不依赖 `Scaffold`，可在任意 Composable 作用域中使用。
:::

## Stock ColorOS 静态复核（2026-11-12）

拖拽指示器改为对齐 `COUIBottomSheetDialog` 实际 inflate 的 panel 资源：

- 指示条：`coui_panel_drag_view.xml` 是 **36×4dp** 圆角条（2dp 圆角），tint 取 `?attr/couiColorControls`（`#29000000` / `#40FFFFFF`）。原先 32→55dp 的宽度与纵向缩放动画没有源码依据，已移除。
- 按压反馈：**70×20dp** 胶囊（`coui_pannel_press_shadow_bg`，16dp 圆角）取 `?attr/couiColorPress`（`#1F000000` / `#33FFFFFF`），在指示条后淡入，对应官方的 `tv_drag_press_bg`；淡化时长是近似值——反编译的触摸监听位于未导出的合成类中。
- 指示区高 **12dp**、上边距 **4dp**（`coui_panel_drag_view_*`）。手势区域刻意比 12dp 的视觉条更高，保证面板仍好拖。

**BREAKING：** `BottomSheetDefaults.dragHandleColor()` 改为解析 `couiColorControls`，不再用 `onSurfaceVariantActions`。

**仍待复核：** 官方弹层背景与对话框一样是模糊背板；拖拽时按 `coui_bottom_sheet_dialog_follow_hand_margin_*`（8dp）收缩的效果也未移植。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.window.WindowBottomSheet
import io.github.ra1nify.rmuix.kmp.theme.LocalDismissState
```

## 基本用法

`WindowBottomSheet` 组件提供了基础的底部抽屉功能：

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

// 可以在任何地方使用
TextButton(
    text = "显示 Window 底部抽屉",
    onClick = { showBottomSheet = true }
)

WindowBottomSheet(
    show = showBottomSheet,
    title = "Window 底部抽屉标题",
    onDismissRequest = { showBottomSheet = false }
) {
    val dismiss = LocalDismissState.current
    Text(text = "这是 Window 底部抽屉的内容")
    TextButton(
        text = "关闭",
        onClick = { dismiss?.invoke() }
    )
}
```

## 属性

### WindowBottomSheet 属性

| 属性名                     | 类型                      | 说明                                         | 默认值                                      | 是否必须 |
| -------------------------- | ------------------------- | -------------------------------------------- | ------------------------------------------- | -------- |
| show                       | Boolean                   | 是否显示底部抽屉                             | -                                           | 是       |
| modifier                   | Modifier                  | 应用于底部抽屉的修饰符                       | Modifier                                    | 否       |
| title                      | String?                   | 底部抽屉的标题                               | null                                        | 否       |
| startAction                | @Composable (() -> Unit)? | 可选的左侧操作按钮(例如关闭按钮)             | null                                        | 否       |
| endAction                  | @Composable (() -> Unit)? | 可选的右侧操作按钮(例如提交按钮)             | null                                        | 否       |
| backgroundColor            | Color                     | 底部抽屉背景色                               | BottomSheetDefaults.backgroundColor() | 否       |
| enableWindowDim            | Boolean                   | 是否启用遮罩层                               | true                                        | 否       |
| cornerRadius               | Dp                        | 顶部圆角半径                                 | BottomSheetDefaults.cornerRadius      | 否       |
| sheetMaxWidth              | Dp                        | 底部抽屉的最大宽度                           | BottomSheetDefaults.maxWidth          | 否       |
| onDismissRequest           | (() -> Unit)?             | 当用户请求关闭（点击遮罩层或返回手势）时触发 | null                                        | 否       |
| onDismissFinished          | (() -> Unit)?             | 关闭动画完成后调用；若关闭过程被中途取消（例如 `show` 被设回 true），则不会触发 | null      | 否       |
| outsideMargin              | DpSize                    | 底部抽屉外部边距                             | BottomSheetDefaults.outsideMargin     | 否       |
| insideMargin               | DpSize                    | 底部抽屉内部内容的边距                       | BottomSheetDefaults.insideMargin      | 否       |
| defaultWindowInsetsPadding | Boolean                   | 是否应用默认窗口插入内边距                   | true                                        | 否       |
| dragHandleColor            | Color                     | 拖拽指示器的颜色                             | BottomSheetDefaults.dragHandleColor() | 否       |
| allowDismiss               | Boolean                   | 是否允许通过拖拽或返回手势关闭抽屉           | true                                        | 否       |
| enableNestedScroll         | Boolean                   | 是否允许内容嵌套滚动                         | true                                        | 否       |
| dimColor                   | Color                     | 底部面板后遮罩层的颜色         | RmuixTheme.colorScheme.windowDimming         | 否       |
| surface                    | `(@Composable BottomSheetSurfaceScope.() -> Modifier)?` | 自定义面板表面（如玻璃材质）。`null` 保持原厂 squircle 背景 | null | 否 |
| content                    | @Composable () -> Unit    | 底部抽屉的内容                               | -                                           | 是       |

### BottomSheetDefaults

#### BottomSheetDefaults 属性

| 属性名        | 类型   | 说明                  |
| ------------- | ------ | --------------------- |
| cornerRadius  | Dp     | 默认圆角半径 (32.dp，coui_round_corner_xxxxl)                           |
| maxWidth      | Dp     | 默认最大宽度 (540.dp)                           |
| outsideMargin | DpSize | 底部抽屉外部默认边距 (DpSize(0.dp, 0.dp))       |
| insideMargin  | DpSize | 底部抽屉内部默认边距 (DpSize(16.dp, 0.dp))      |

#### BottomSheetDefaults 函数

| 函数名            | 返回类型 | 说明                   |
| ----------------- | -------- | ---------------------- |
| backgroundColor() | Color    | 获取默认背景颜色       |
| dragHandleColor() | Color    | 获取默认拖拽指示器颜色 |

## 进阶用法

### 从内容中关闭

您可以使用 `LocalDismissState` 从其内容中关闭底部抽屉：

```kotlin
WindowBottomSheet(
    show = showBottomSheet,
    title = "关闭示例",
    onDismissRequest = { showBottomSheet = false }
) {
    val dismiss = LocalDismissState.current
    
    Button(
        onClick = { dismiss?.invoke() }
    ) {
        Text("关闭底部抽屉")
    }
}
```
