---
title: WindowDialog
requiresScaffoldHost: false
prerequisites:
  - 可以在任何地方使用，不需要 `Scaffold` 或 `RmuixPopupHost`
  - 在窗口层级渲染
hostComponent: None
popupHost: None
---

# WindowDialog

`WindowDialog` 是一个窗口级对话框组件。它基于平台 `Dialog` 渲染，无需依赖 `Scaffold` 或 `RmuixPopupHost`。支持大屏优化动效、系统返回手势关闭，以及在内容内部通过组合局部触发关闭。

<div style="position: relative; height: 240px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=windowDialog" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

::: tip 提示
该组件不依赖 `Scaffold`，可在任意 Composable 作用域中使用。
:::

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `coui_center_alert_dialog_layout.xml` 族、`COUIDialogTextAppearance.*` 样式与 `COUIAlertDialogBuilder` 使用的圆角属性。只覆盖居中 alert dialog 与底部弹层的圆角；材质层仍是偏差项。

- 最大宽度 `coui_dialog_max_width` = **392dp**（原 328dp）；外边距 `coui_dialog_layout_margin_horizontal` / `_vertical` = **16dp / 32dp**（原 16dp / 24dp）。
- 圆角来自 `?attr/couiRoundCornerXXXXLRadius`，即 `coui_round_corner_xxxxl_radius` = **25dp**，G2 squircle 权重 1.1；不是原先用的无后缀 `coui_round_corner_xxxxl`（32dp）。
- 标题按 `COUIAlertDialogTitleStyle` → `couiColorLabelPrimary`（`#E6000000` / `#E6FFFFFF`）；正文按 `COUIAlertDialogMessageStyle` → `couiColorLabelSecondary`（`#8A000000` / `#8AFFFFFF`）。字号本就正确（标题 18sp medium、正文 13sp）。
- 底部弹层是另一个组件（`COUIBottomSheetDialog`），读无后缀的 `coui_round_corner_xxxxl` = **32dp**；两者不再被描述成同一个 token。

**BREAKING：** `DialogDefaults.MaxWidth` 328→392dp、`CornerRadius` 32→25dp、`outsideMargin` 16/24→16/32，默认标题/正文颜色改变；`BottomSheetDefaults.cornerRadius` 22→32dp。

**滚动上限：**标题高于 `DialogDefaults.TitleScrollMaxHeight`（80dp，
`coui_alert_dialog_builder_title_scroll_max_height`）或正文高于 `DialogDefaults.MessageScrollMaxHeight`
（80dp，`coui_alert_dialog_builder_content_max_height`）时在自己的区域内滚动，对应官方的
`COUIMaxHeightScrollView` / `COUIMaxHeightNestedScrollView`。官方的渐变边缘（`requiresFadingEdge`）未绘制。

**居中对话框布局：**Window 对话框与 Overlay 版共用 `DialogContentLayout`，因此官方的满宽文字
按钮栏（`confirmButtonText` / `dismissButtonText` / `confirmButtonDestructive`）与
`?attr/couiColorSurface` 背景同样生效（`coui_center_alert_dialog_layout.xml`）。
**仍待复核：** 官方对话框背景是模糊+着色的背板（`COUIBackgroundBlurBuilder`，200px 模糊，`coui_dialog_list_mix_blur_light` `#DC909090` / dark `#8C777777`，blend `#509F9F9F` / `#B2292929`）；库内仍是实心主题表面。按 OS 版本切到 45dp（`coui_round_corner_xxxxl_radius_large_16_1`）的分支未实现运行时选择。
## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.window.WindowDialog
import io.github.ra1nify.rmuix.kmp.theme.LocalDismissState
```

## 基本用法

```kotlin
var showDialog by remember { mutableStateOf(false) }

TextButton(
    text = "打开",
    onClick = { showDialog = true }
)

WindowDialog(
    title = "WindowDialog",
    summary = "一个基础的窗口级对话框示例",
    show = showDialog,
    onDismissRequest = { showDialog = false }
) {
    val dismiss = LocalDismissState.current
    TextButton(
        text = "确认",
        onClick = { dismiss?.invoke() },
        modifier = Modifier.fillMaxWidth()
    )
}
```

## 属性

### WindowDialog 属性

| 属性名                     | 类型                   | 说明                                           | 默认值                                 | 是否必须 |
| -------------------------- | ---------------------- | ---------------------------------------------- | -------------------------------------- | -------- |
| show                       | Boolean                | 是否显示对话框                                 | -                                      | 是       |
| modifier                   | Modifier               | 根内容修饰符                                   | Modifier                               | 否       |
| title                      | String?                | 对话框标题                                     | null                                   | 否       |
| titleColor                 | Color                  | 标题颜色                                       | DialogDefaults.titleColor()      | 否       |
| summary                    | String?                | 对话框摘要                                     | null                                   | 否       |
| summaryColor               | Color                  | 摘要颜色                                       | DialogDefaults.summaryColor()    | 否       |
| backgroundColor            | Color                  | 对话框背景色                                   | DialogDefaults.backgroundColor() | 否       |
| enableWindowDim            | Boolean                | 是否启用遮罩层                                 | true                                   | 否       |
| onDismissRequest           | (() -> Unit)?          | 当用户请求关闭（点击遮罩层或返回手势）时触发   | null                                   | 否       |
| onDismissFinished          | (() -> Unit)?          | 关闭动画完成后调用；若关闭过程被中途取消（例如 `show` 被设回 true），则不会触发 | null      | 否       |
| outsideMargin              | DpSize                 | 相对窗口边缘的外部边距                         | DialogDefaults.outsideMargin     | 否       |
| insideMargin               | DpSize                 | 对话框内容内部边距                             | DialogDefaults.insideMargin      | 否       |
| defaultWindowInsetsPadding | Boolean                | 是否应用默认窗口插入内边距（输入法/导航/标题） | true                                   | 否       |
| maxWidth                   | Dp                     | 对话框最大宽度                                 | DialogDefaults.MaxWidth          | 否       |
| largeScreen                | Boolean?               | 覆盖大屏呈现样式（居中缩放淡入而非底部滑入）。为 null 时根据窗口尺寸自动检测 | null | 否 |
| cornerRadius               | Dp?                    | 覆盖对话框圆角。为 null 时使用 DialogDefaults.CornerRadius，居中与底部贴边样式相同 | null | 否 |
| dimColor                   | Color                  | 对话框后遮罩层的颜色           | RmuixTheme.colorScheme.windowDimming   | 否       |
| icon | (@Composable () -> Unit)? | 标题上方靠左的图标槽，对应官方 `android:id/icon` | null | 否 |
| surface                    | `(@Composable DialogSurfaceScope.() -> Modifier)?` | 自定义面板表面（如玻璃材质）。`null` 保持原厂 squircle 背景 | null | 否 |
| confirmButtonText          | String?                | 确认按钮文字；非空时在内容下方画一行右对齐的文字按钮（`COUIAlertDialog` 形式） | null | No |
| onConfirm                  | (() -> Unit)?          | 确认按钮回调；为空时回退到 `onDismissRequest` | null | No |
| confirmButtonDestructive | Boolean | 确认按钮改用错误色（`COUIAlertDialogBottomButtonSecondary.Last`） | false | 否 |
| dismissButtonText          | String?                | 取消按钮文字 | null | No |
| onDismiss                  | (() -> Unit)?          | 取消按钮回调；为空时回退到 `onDismissRequest` | null | No |
| content                    | @Composable () -> Unit | 对话框内容                                     | -                                      | 是       |

### DialogDefaults

#### 属性

| 属性名        | 类型   | 说明               | 默认值 |
| ------------- | ------ | ------------------ | -------- |
| MaxWidth      | Dp     | 对话框默认最大宽度 | 392.dp |
| CornerRadius  | Dp     | 默认圆角（`coui_round_corner_xxxxl_radius`） | 25.dp |
| outsideMargin | DpSize | 对话框外部默认边距 | DpSize(16.dp, 32.dp) |
| insideMargin  | DpSize | 对话框内部默认边距 | DpSize(24.dp, 24.dp) |

#### 函数

| 函数名            | 返回类型 | 说明                   |
| ----------------- | -------- | ---------------------- |
| titleColor()      | Color    | 获取默认标题颜色       |
| summaryColor()    | Color    | 获取默认摘要颜色       |
| backgroundColor() | Color    | 获取默认对话框背景颜色 |

### LocalDismissState

提供一个 `(() -> Unit)?` 函数，用于从内容内部关闭当前弹窗。这是所有弹出组件提供的统一关闭状态。

```kotlin
val dismiss = LocalDismissState.current
TextButton(
    text = "关闭",
    onClick = { dismiss?.invoke() }
)
```
