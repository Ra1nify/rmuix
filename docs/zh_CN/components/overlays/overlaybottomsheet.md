---
title: OverlayBottomSheet
requiresScaffoldHost: true
prerequisites:
  - 必须在 `Scaffold` 中使用以提供 `RmuixPopupHost`
  - 未在 `Scaffold` 中使用将导致弹出内容无法渲染
  - 支持多个嵌套或并列的 `Scaffold`，无需额外配置
hostComponent: Scaffold
popupHost: RmuixPopupHost
---

# OverlayBottomSheet

`OverlayBottomSheet` 是 Rmuix 中的底部抽屉组件，从屏幕底部滑入显示。持拖拽手势关闭和自定义样式。

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=overlayBottomSheet" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: danger 使用前提
此组件依赖 `Scaffold` 提供的 `RmuixPopupHost` 以显示弹出内容。必须在 `Scaffold` 中使用，否则弹出内容无法正常渲染。
:::

## Stock ColorOS 静态复核（2026-11-12）

拖拽指示器改为对齐 `COUIBottomSheetDialog` 实际 inflate 的 panel 资源：

- 指示条：`coui_panel_drag_view.xml` 是 **36×4dp** 圆角条（2dp 圆角），tint 取 `?attr/couiColorControls`（`#29000000` / `#40FFFFFF`）。原先 32→55dp 的宽度与纵向缩放动画没有源码依据，已移除。
- 按压反馈：**70×20dp** 胶囊（`coui_pannel_press_shadow_bg`，16dp 圆角）取 `?attr/couiColorPress`（`#1F000000` / `#33FFFFFF`），在指示条后淡入，对应官方的 `tv_drag_press_bg`；淡化时长是近似值——反编译的触摸监听位于未导出的合成类中。
- 指示区高 **12dp**、上边距 **4dp**（`coui_panel_drag_view_*`）。手势区域刻意比 12dp 的视觉条更高，保证面板仍好拖。

**BREAKING：** `BottomSheetDefaults.dragHandleColor()` 改为解析 `couiColorControls`，不再用 `onSurfaceVariantActions`。

### 圆角同样是平台分支（2026-11-12）

`COUIPanelPercentFrameLayout.initRadiusAndWeight()` 与对话框一样按分支取圆角，而不是单一 token：

| 分支 | 圆角 |
| --- | --- |
| 不支持平滑圆角 | `couiRoundCornerXXXXL` **32dp**（小屏 `couiRoundCornerXXXL` 28dp），weight 0 |
| 平滑样式为 0 | `couiRoundCornerXXXXLRadius` **25dp**（小屏 22dp），weight 1.1 |
| 平滑样式 1 且 OS ≤ 37 | `couiRoundCornerXXXXL` **32dp**（小屏 28dp），weight 0 |
| 平滑样式 1 且 OS > 37 | `coui_round_corner_xxxxl_radius_large_16_1` **45dp**（小屏 39dp） |

`BottomSheetDefaults.cornerRadius` 取的是 **32dp** 那一支。25dp / 45dp 与对话框用的是同一套圆角尺度，所以两者并不矛盾——它们读同一个 corner 档位，只是落在不同分支。

`updateBottomCornerRadius()` 在某些方向下还会返回 `coui_bottom_sheet_bg_top_corner_radius` / `_bottom_corner_radius`（两者都等于 `coui_round_corner_xxxl` = 28dp）。这两个 dimen **不是圆角值**：`updatePath()` 只判断返回值是否 `== 0`，据此决定画「四角圆角」还是「只圆上两角」，两种情况都用同一个 `mRadius`。因此库内「上圆下方」的模型是对的，28dp 这个数从未真正画到画布上。

**仍待复核：** 官方底部弹层的背景与对话框一样是模糊底，拖动时的 `coui_bottom_sheet_dialog_follow_hand_margin_*`
8dp 收缩尚未移植。官方 dimens 里还有 `coui_bottom_sheet_margin_bottom_default` = 64dp 与
`coui_bottom_sheet_content_horizontal_padding_with_card` = 16dp，它们属于「跟手对话框」那一版（它靠这些边距浮起来），
所以本 overlay 弹层保持 `outsideMargin = 0`、最大宽 540dp ——
`docs/dev/shots/bottomsheet-{light,dark}.png` 就是与真机截图对比的基线。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.overlay.OverlayBottomSheet
```

## 基本用法

OverlayBottomSheet 组件提供了基础的底部抽屉功能:

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "底部抽屉标题",
        onDismissRequest = { showBottomSheet = false }
    ) {
        Text(text = "这是底部抽屉的内容")
    }
}
```

## 属性

### OverlayBottomSheet 属性

| 属性名                     | 类型                      | 说明                                         | 默认值                                     | 是否必须 |
| -------------------------- | ------------------------- | -------------------------------------------- | ------------------------------------------ | -------- |
| show                       | Boolean                   | 是否显示底部抽屉                             | -                                          | 是       |
| modifier                   | Modifier                  | 应用于底部抽屉的修饰符                       | Modifier                                   | 否       |
| title                      | String?                   | 底部抽屉的标题                               | null                                       | 否       |
| startAction                | @Composable (() -> Unit)? | 可选的左侧操作按钮(例如关闭按钮)             | null                                       | 否       |
| endAction                  | @Composable (() -> Unit)? | 可选的右侧操作按钮(例如提交按钮)             | null                                       | 否       |
| backgroundColor            | Color                     | 底部抽屉背景色                               | BottomSheetDefaults.backgroundColor() | 否       |
| enableWindowDim            | Boolean                   | 是否启用遮罩层                               | true                                       | 否       |
| cornerRadius               | Dp                        | 顶部圆角半径                                 | BottomSheetDefaults.cornerRadius      | 否       |
| sheetMaxWidth              | Dp                        | 底部抽屉的最大宽度                           | BottomSheetDefaults.maxWidth          | 否       |
| onDismissRequest           | (() -> Unit)?             | 当用户请求关闭（点击遮罩层或返回手势）时触发 | null                                       | 否       |
| onDismissFinished          | (() -> Unit)?             | 关闭动画完成后调用；若关闭过程被中途取消（例如 `show` 被设回 true），则不会触发 | null     | 否       |
| outsideMargin              | DpSize                    | 底部抽屉外部边距                             | BottomSheetDefaults.outsideMargin     | 否       |
| insideMargin               | DpSize                    | 底部抽屉内部内容的边距                       | BottomSheetDefaults.insideMargin      | 否       |
| defaultWindowInsetsPadding | Boolean                   | 是否应用默认窗口插入内边距                   | true                                       | 否       |
| dragHandleColor            | Color                     | 拖拽指示器的颜色                             | BottomSheetDefaults.dragHandleColor() | 否       |
| allowDismiss               | Boolean                   | 是否允许通过拖拽或返回手势关闭抽屉           | true                                       | 否       |
| enableNestedScroll         | Boolean                   | 是否允许内容嵌套滚动                         | true                                       | 否       |
| renderInRootScaffold       | Boolean                   | 是否在根（最外层）Scaffold 中渲染底部抽屉。为 true 时，覆盖全屏。为 false 时，在当前 Scaffold 的范围内渲染 | true | 否 |
| content                    | @Composable () -> Unit    | 底部抽屉的内容                               | -                                          | 是       |
| dimColor                   | Color                     | 应用显隐与拖动进度前的遮罩颜色 | RmuixTheme.colorScheme.windowDimming | 否 |
| surface                    | (@Composable BottomSheetSurfaceScope.() -> Modifier)? | 替换背景与裁剪，同时用于上拉回弹时的底部延伸区域 | null | 否 |

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

### 自定义表面

可选的 `surface` 插槽替换 `backgroundColor` 和默认裁剪，同时保留拖动条、内容布局、系统插入内边距、滑入动画、嵌套滚动和关闭行为。接收者为 `BottomSheetSurfaceScope`：

| 属性 | 类型 | 说明 |
| --- | --- | --- |
| `cornerRadius` | Dp | 已解析的顶部圆角，底部保持直角 |
| `scrimColor` | State&lt;Color&gt; | 动画中的遮罩颜色；关闭窗口遮罩时为透明 |
| `isOverscroll` | Boolean | 上拉回弹时补出的底部区域为 true，此时圆角为零 |

动画状态应在绘制阶段读取。自定义表面可能分别用于 sheet 主体和回弹延伸区域，后者应关闭边缘描边。

### 自定义样式底部抽屉

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示自定义样式底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "自定义样式",
        backgroundColor = RmuixTheme.colorScheme.surfaceVariant,
        dragHandleColor = RmuixTheme.colorScheme.primary,
        outsideMargin = DpSize(16.dp, 0.dp),
        insideMargin = DpSize(32.dp, 16.dp),
        onDismissRequest = { showBottomSheet = false }
    ) {
        Column {
            Text("自定义样式的底部抽屉")
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                text = "关闭",
                onClick = { showBottomSheet = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```

### 带列表内容的底部抽屉

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }
var selectedItem by remember { mutableStateOf("") }

Scaffold {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(
            text = "显示选择列表",
            onClick = { showBottomSheet = true }
        )
        
        Text("已选择: $selectedItem")
    }

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "选择项目",
        onDismissRequest = { showBottomSheet = false }
    ) {
        LazyColumn {
            items(20) { index ->
                Text(
                    text = "项目 ${index + 1}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedItem = "项目 ${index + 1}"
                            showBottomSheet = false
                        }
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}
```

### 不使用遮罩层

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示无遮罩底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "无遮罩层",
        enableWindowDim = false,
        onDismissRequest = { showBottomSheet = false }
    ) {
        Text("这个底部抽屉没有背景遮罩层")
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            text = "关闭",
            onClick = { showBottomSheet = false },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

### 带操作按钮的底部抽屉

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示带操作按钮的底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "操作面板",
        startAction = {
            TextButton(
                text = "取消",
                onClick = { showBottomSheet = false }
            )
        },
        endAction = {
            TextButton(
                text = "确认",
                onClick = { 
                    // 处理确认操作
                    showBottomSheet = false 
                },
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        },
        onDismissRequest = { showBottomSheet = false }
    ) {
        Text("带有自定义标题栏操作按钮的内容")
        Spacer(modifier = Modifier.height(16.dp))
        Text("左右两侧的操作按钮显示在标题栏中")
    }
}
```

### 带表单的底部抽屉

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }
var textFieldValue by remember { mutableStateOf("") }
var switchState by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示表单底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "设置表单",
        onDismissRequest = { showBottomSheet = false }
    ) {
        Card(
            color = RmuixTheme.colorScheme.secondaryContainer,
        ) {
            TextField(
                modifier = Modifier.padding(vertical = 12.dp),
                value = textFieldValue,
                label = "输入内容",
                maxLines = 1,
                onValueChange = { textFieldValue = it }
            )
            
            SwitchPreference(
                title = "开关选项",
                checked = switchState,
                onCheckedChange = { switchState = it }
            )
        }
        
        Spacer(Modifier.height(12.dp))
        
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                text = "取消",
                onClick = { showBottomSheet = false },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                text = "确认",
                onClick = { showBottomSheet = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        }
    }
}
```

### 自适应内容高度

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示自适应高度底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "自适应高度",
        onDismissRequest = { showBottomSheet = false }
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text("高度会自动适应内容")
            Spacer(modifier = Modifier.height(16.dp))
            Text("可以添加任意多的内容")
            Spacer(modifier = Modifier.height(16.dp))
            Text("但不会覆盖到状态栏区域")
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                text = "关闭",
                onClick = { showBottomSheet = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```

### 禁止关闭的底部抽屉

```kotlin
var showBottomSheet by remember { mutableStateOf(false) }

Scaffold {
    TextButton(
        text = "显示禁止关闭的底部抽屉",
        onClick = { showBottomSheet = true }
    )

    OverlayBottomSheet(
        show = showBottomSheet,
        title = "禁止关闭",
        allowDismiss = false,
        onDismissRequest = { showBottomSheet = false }
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text("这个底部抽屉无法通过拖拽或返回手势关闭")
            Spacer(modifier = Modifier.height(16.dp))
            Text("您必须通过下面的按钮显式关闭它")
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                text = "关闭",
                onClick = { showBottomSheet = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
```
