#! /usr/bin/env markdown

# Tooltip

`Tooltip` 是 Rmuix 中用于简短描述锚点元素的组件。参照 Material 3，统一由 `TooltipBox` 将 tooltip 锚定到内容上；你在它的 `tooltip` 槽里填入 `PlainTooltip`（反色表面短标签）或 `RichTooltip`（带可选标题与操作的 surfaceContainer 卡片）。tooltip 在悬停（光标）或长按（触摸）时显示，也可通过 `TooltipState` 以编程方式显示。

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=tooltip" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `com.coui.appcompat.tooltip.COUIToolTips`、`COUIToolTips` 样式与 `coui_tooltips_*` / `tool_tips_*` 资源。

- 官方提示气泡用 `couiToolTipsBackgroundColor` → `?attr/couiColorTips`（`#0080FF` / `#1A8CFF`）填充，文字是 `couiToolTipsContentTextColor` = `coui_color_white`——即白字蓝底，而不是库内原来的 `onSecondaryVariant` / `secondaryVariant` 反色对。
- 气泡四边都是 13dp 内边距（`coui_tooltips_default_background_padding_left/top/right/bottom`），库内原来是 12dp × 8dp。
- 圆角走 `coui_tooltips_os_16_1_radius_17_dp` = **17dp**（旧分支用 `couiRoundCornerM`），库内原来是 12dp。
- 气泡最宽 `tool_tips_max_width` = **328dp**（原 200dp），文字 `tool_tips_content_text_size` = **13sp**；`TooltipBox(text = …)` 便捷重载现在显式设置这个字号。
- Rich tooltip 继续用 `richTooltipColors()` 那套角色；官方提示控件没有标题/操作变体，因此不对齐。

**BREAKING：** 纯文本提示变成蓝底白字气泡，内边距 13dp、圆角 17dp、最大宽度 328dp、字号 13sp。

**BREAKING：** 普通提示气泡默认绘制箭头。

图片变体（`ImageTooltip`，`tool_tips_image_style_max_width` 260dp + `coui_tooltips_imageview_radius` 8dp）与
删除按钮（`TooltipDismissButton`，`coui_tool_tips_delete_*`）**都已实现**，见下文变体小节。

仍未建模：`couiToolTipsArrowOverflowOffset`（5.7dp；当前以「按窗口边距夹紧」替代，正是官方使用该偏移的目的）。

**`COUIToolTips.DetailFloating`** 不再作为独立组件，而是以预设形式暴露：
`TooltipDefaults.detailFloatingContainerColor()` = `couiColorBackgroundTop` **#FFFFFF** / **#333333**，
`detailFloatingContentColor()` = `couiColorSecondNeutral` **#8A000000** / **#8AFFFFFF**，
`DetailFloatingInsideMargin` = 横向 16dp + 默认纵向 13dp，
`DetailFloatingContainerMargins` = `detail_floating_text_margin` **四边 12dp**（传给
`rememberTooltipPositionProvider(containerMargins = …)`），以及 `DetailFloatingMinWidth` = **80dp**（仅记录，`PlainTooltip` 没有最小宽度槽位）。以上均由 `TooltipDetailFloatingTest` 钉住。
## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TooltipBox
import io.github.ra1nify.rmuix.kmp.basic.RichTooltipBox
import io.github.ra1nify.rmuix.kmp.basic.PlainTooltip
import io.github.ra1nify.rmuix.kmp.basic.RichTooltip
import io.github.ra1nify.rmuix.kmp.basic.TooltipState
import io.github.ra1nify.rmuix.kmp.basic.rememberTooltipState
import io.github.ra1nify.rmuix.kmp.basic.TooltipDefaults
import io.github.ra1nify.rmuix.kmp.basic.TooltipAnchorPosition
```

## 基本用法

为纯图标控件加标签，最快的方式是 `TooltipBox` 的 `text` 便捷重载：

```kotlin
TooltipBox(text = "搜索") {
    IconButton(onClick = { /* ... */ }) {
        Icon(
            imageVector = RmuixIcons.Basic.Search,
            contentDescription = "搜索",
        )
    }
}
```

## TooltipBox

`TooltipBox` 将 tooltip 锚定到 `content`。它的 `tooltip` 槽是一个 `TooltipScope` lambda，用 `PlainTooltip` 或 `RichTooltip` 填充。

```kotlin
@Composable
fun TooltipBox(
    positionProvider: PopupPositionProvider,
    tooltip: @Composable TooltipScope.() -> Unit,
    state: TooltipState,
    modifier: Modifier = Modifier,
    focusable: Boolean = false,
    enableUserInput: Boolean = true,
    content: @Composable () -> Unit,
)
```

| 参数名           | 类型                                | 说明                                                       | 默认值   | 是否必须 |
| ---------------- | ----------------------------------- | ---------------------------------------------------------- | -------- | -------- |
| positionProvider | PopupPositionProvider               | 定位 tooltip，见 `TooltipDefaults.rememberTooltipPositionProvider` | -  | 是       |
| tooltip          | @Composable TooltipScope.() -> Unit | tooltip 内容（`PlainTooltip` / `RichTooltip`）            | -        | 是       |
| state            | TooltipState                        | 控制显隐的状态                                            | -        | 是       |
| modifier         | Modifier                            | 应用于锚点包裹层的修饰符                                  | Modifier | 否       |
| focusable        | Boolean                             | tooltip 是否 focusable（可交互的 rich tooltip 需为 true） | false    | 否       |
| enableUserInput  | Boolean                             | 悬停 / 长按锚点是否显示 tooltip                          | true     | 否       |
| content          | @Composable () -> Unit              | 锚点内容                                                  | -        | 是       |

```kotlin
TooltipBox(
    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
    tooltip = { PlainTooltip { Text("添加到收藏") } },
    state = rememberTooltipState(),
) {
    IconButton(onClick = { /* ... */ }) {
        Icon(imageVector = RmuixIcons.Basic.Check, contentDescription = "添加到收藏")
    }
}
```

## PlainTooltip

`PlainTooltip` 是渲染在 Rmuix 反色表面上的短标签、不可交互。它是 `TooltipScope` 扩展，用于 `TooltipBox` 的 `tooltip` 槽内。

```kotlin
@Composable
fun TooltipScope.PlainTooltip(
    modifier: Modifier = Modifier,
    caretShape: Shape? = TooltipDefaults.caretShape(),
    maxWidth: Dp = TooltipDefaults.PlainTooltipMaxWidth,
    cornerRadius: Dp = TooltipDefaults.PlainTooltipCornerRadius,
    containerColor: Color = TooltipDefaults.plainTooltipContainerColor,
    contentColor: Color = TooltipDefaults.plainTooltipContentColor,
    insideMargin: PaddingValues = TooltipDefaults.PlainTooltipInsideMargin,
    content: @Composable () -> Unit,
)
```

默认情况下 plain tooltip 采用反色表面（浅色主题为深色气泡，深色主题为浅色气泡）。`caretShape` 默认就是 `TooltipDefaults.caretShape()`，即默认绘制官方箭头；要无箭头气泡请传 `caretShape = null`。

## RichTooltip

`RichTooltip` 是带可选标题与操作的持久、可交互卡片，渲染在普通 `surfaceContainer` 上。它是 `TooltipScope` 扩展。

```kotlin
@Composable
fun TooltipScope.RichTooltip(
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
    caretShape: Shape? = null,
    maxWidth: Dp = TooltipDefaults.RichTooltipMaxWidth,
    cornerRadius: Dp = TooltipDefaults.RichTooltipCornerRadius,
    colors: RichTooltipColors = TooltipDefaults.richTooltipColors(),
    insideMargin: PaddingValues = TooltipDefaults.RichTooltipInsideMargin,
    text: @Composable () -> Unit,
)
```

rich tooltip 可交互，因此需在 `TooltipBox` 上设 `focusable = true`（便捷的 `RichTooltipBox` 已替你设好），以便点外部关闭、操作可达。

```kotlin
val tooltipState = rememberTooltipState(isPersistent = true)
val scope = rememberCoroutineScope()

TooltipBox(
    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
    tooltip = {
        RichTooltip(
            title = { Text("新功能") },
            action = {
                TextButton(text = "知道了", onClick = { tooltipState.dismiss() })
            },
        ) {
            Text("Rich tooltip 支持标题、说明文字与一个操作。")
        }
    },
    state = tooltipState,
    focusable = true,
) {
    IconButton(onClick = { scope.launch { tooltipState.show() } }) {
        Icon(imageVector = RmuixIcons.Basic.Check, contentDescription = "新功能")
    }
}
```

## TooltipState

`TooltipState` 控制 tooltip 的显隐。全局 `MutatorMutex` 保证同一时刻只有一个 tooltip 可见。

```kotlin
@Stable
interface TooltipState {
    val transition: MutableTransitionState<Boolean>
    val isVisible: Boolean
    val isPersistent: Boolean
    suspend fun show(mutatePriority: MutatePriority = MutatePriority.Default)
    fun dismiss()
    fun onDispose()
}

@Composable
fun rememberTooltipState(
    initialIsVisible: Boolean = false,
    isPersistent: Boolean = false,
    mutatorMutex: MutatorMutex = BasicTooltipDefaults.GlobalMutatorMutex,
): TooltipState
```

| 参数名           | 类型         | 说明                                  | 默认值                                  | 是否必须 |
| ---------------- | ------------ | ------------------------------------- | --------------------------------------- | -------- |
| initialIsVisible | Boolean      | tooltip 是否初始可见                  | false                                   | 否       |
| isPersistent     | Boolean      | tooltip 是否一直显示直到被关闭        | false                                   | 否       |
| mutatorMutex     | MutatorMutex | 保证同一时刻只显示一个 tooltip 的互斥锁 | BasicTooltipDefaults.GlobalMutatorMutex | 否     |

当 `isPersistent` 为 false（plain tooltip）时，tooltip 在短暂超时后自动消失；为 true（rich tooltip）时，会一直显示直到 `dismiss()`、点外部或按返回键。`show()` 是 suspend 函数，需在协程中调用（例如 `scope.launch { state.show() }`）。

## TooltipDefaults

`TooltipDefaults` 提供定位 provider、颜色、caret 与尺寸常量。

### 定位 provider

```kotlin
@Composable
fun TooltipDefaults.rememberTooltipPositionProvider(
    positioning: TooltipAnchorPosition = TooltipAnchorPosition.Below,
    spacingBetweenTooltipAndAnchor: Dp = TooltipDefaults.SpacingBetweenTooltipAndAnchor,
): PopupPositionProvider
```

`TooltipAnchorPosition` 选择优先方位：`Above`、`Below`、`Left`、`Right`、`Start`、`End`。tooltip 在交叉轴上居中，空间不足时翻转到对侧。
气泡在水平与垂直方向都会夹紧，并保留官方容器边距：`couiToolTipsContainerLayoutMarginStart` **12dp**、
`_MarginEnd` **42dp**、`_MarginTop` **12dp**、`_MarginBottom` **12dp**（`COUIToolTips` 样式），
以 `TooltipDefaults.ContainerMarginStart` / `ContainerMarginEnd` / `ContainerMarginTop` / `ContainerMarginBottom` 暴露；start/end 是行内边，RTL 下互换。
所以贴近屏幕边缘时气泡会向内滑动，并保留官方留白，而不是贴着边或被裁掉。

`Start` / `End` 指的是锚点的**行内**起点/终点，因此 LTR 下解析为 `Left`、RTL 下解析为 `Right`
（`TooltipPositionProviderTest` 钉住）。

### 颜色

```kotlin
// plain tooltip（反色表面）
val plainContainer = TooltipDefaults.plainTooltipContainerColor   // couiColorTips: #0080FF / #1A8CFF
val plainContent = TooltipDefaults.plainTooltipContentColor       // 白色

// rich tooltip（surface container）
val richColors = TooltipDefaults.richTooltipColors(
    containerColor = RmuixTheme.colorScheme.surfaceContainer,
    contentColor = RmuixTheme.colorScheme.onSurfaceContainerVariant,
    titleContentColor = RmuixTheme.colorScheme.onSurfaceContainer,
    actionContentColor = RmuixTheme.colorScheme.primary,
)
```

`RichTooltipColors` 是包含 `containerColor`、`contentColor`、`titleContentColor`、`actionContentColor` 的数据类。

### Caret

```kotlin
fun TooltipDefaults.caretShape(): Shape
val TooltipDefaults.caretSize: DpSize // 20 x 10.1 dp (`coui_white_sharp_middle_arrow_up.xml`)
```

`PlainTooltip` 默认绘制官方箭头（`Above` / `Below` 方位）；`RichTooltip` 仍默认无箭头，需要时显式传入。

### 常量

| 常量名                         | 类型          | 说明                     | 默认值                                             |
| ------------------------------ | ------------- | ------------------------ | -------------------------------------------------- |
| SpacingBetweenTooltipAndAnchor | Dp            | tooltip 与锚点之间的间距 | 8.dp                                               |
| PlainTooltipMaxWidth           | Dp            | plain tooltip 的最大宽度（`tool_tips_max_width`） | 328.dp |
| PlainTooltipCornerRadius       | Dp            | plain tooltip 的圆角半径（`coui_tooltips_os_16_1_radius_17_dp`） | 17.dp |
| PlainTooltipInsideMargin       | PaddingValues | plain tooltip 的内边距（`coui_tooltips_default_background_padding_*`） | PaddingValues(all = 13.dp) |
| PlainTooltipTextSize         | TextUnit      | plain tooltip 的字号（`tool_tips_content_text_size`） | 13.sp |
| RichTooltipMaxWidth            | Dp            | rich tooltip 的最大宽度  | 320.dp                                             |
| RichTooltipCornerRadius        | Dp            | rich tooltip 的圆角半径  | 16.dp                                              |
| RichTooltipInsideMargin        | PaddingValues | rich tooltip 的内边距    | PaddingValues(all = 16.dp)                         |
| RichTooltipActionCornerRadius  | Dp            | rich tooltip 操作按钮的圆角半径 | 8.dp                                          |
| RichTooltipActionInsideMargin  | PaddingValues | rich tooltip 操作按钮的内边距   | PaddingValues(horizontal = 12.dp, vertical = 6.dp) |
| caretSize                  | DpSize        | caret 的尺寸 | DpSize(20.dp, 10.1.dp) |

## 便捷重载

常见场景下可以省去定位 provider 与内容组合函数。

### Plain tooltip

```kotlin
@Composable
fun TooltipBox(
    text: String,
    modifier: Modifier = Modifier,
    state: TooltipState = rememberTooltipState(isPersistent = false),
    enabled: Boolean = true,
    positioning: TooltipAnchorPosition = TooltipAnchorPosition.Below,
    containerColor: Color = TooltipDefaults.plainTooltipContainerColor,
    contentColor: Color = TooltipDefaults.plainTooltipContentColor,
    containerMargins: PaddingValues = TooltipDefaults.ContainerMargins,
    content: @Composable () -> Unit,
)
```

### Rich tooltip

```kotlin
@Composable
fun RichTooltipBox(
    text: String,
    modifier: Modifier = Modifier,
    state: TooltipState = rememberTooltipState(isPersistent = true),
    title: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    positioning: TooltipAnchorPosition = TooltipAnchorPosition.Below,
    colors: RichTooltipColors = TooltipDefaults.richTooltipColors(),
    containerMargins: PaddingValues = TooltipDefaults.ContainerMargins,
    content: @Composable () -> Unit,
)
```

`RichTooltipBox` 是持久且 focusable 的。通过长按 / 悬停触发，或提升 `state` 并在锚点自身的点击里调用 `state.show()`。操作会先调用 `onActionClick` 再关闭 tooltip。

```kotlin
val richState = rememberTooltipState(isPersistent = true)
val scope = rememberCoroutineScope()

RichTooltipBox(
    title = "Rich tooltip",
    text = "Rich tooltip 支持标题、说明文字与一个操作。",
    actionText = "知道了",
    onActionClick = { /* ... */ },
    state = richState,
) {
    TextButton(
        text = "显示 rich tooltip",
        onClick = { scope.launch { richState.show() } },
    )
}
```

> **图标变体**（`COUIToolTips.Icon`）的 token 已记录：`TooltipDefaults.IconTooltipIconSize`(40dp)、`IconTooltipIconMarginTop`(2dp)、`IconTooltipIconMarginStart`(12dp)、`IconTooltipScrollMarginTop`(12dp)、`iconTooltipContainerColor`（`?attr/couiColorBackgroundTop` = `#FFFFFF` / `#333333`）与 `iconTooltipContentColor`（`?attr/couiColorLabelSecondary` = `#8A000000` / `#8AFFFFFF`）。变体布局本身尚未组合。

## 图片变体

`COUIToolTips.Image` 的样式是空的（父级 `COUIToolTips.Icon`），差异在布局与宽度上限。`ImageTooltip(image = { ... }) { Text(...) }` 实现了它：

| token | 值 | 说明 |
| :--- | ---: | :--- |
| `tool_tips_image_style_max_width` | 260dp | 默认气泡上限是 328dp |
| `coui_tool_tips_image_no_edges_style_layout.xml` | 132dp | 图片区高度 |
| `android:scaleType` | `centerCrop` | 图片被**裁切填充**，不是留边 |
| `coui_tooltips_imageview_radius` | 8dp | 图片本身的圆角 |

> 研究笔记里写这个布局"未指定 `scaleType`"—— 实际指定了，值是 `centerCrop`。已作为更正记录。

## 关闭控件

`coui_tool_tips_layout.xml` 在 `end|center_vertical` 放了一个 18dp 的 `ImageView`（`coui_tool_tips_delete_icon`），尾部边距 12dp。`TooltipDismissButton(onDismiss = { ... })` 复刻它，用官方 path data 直接绘制矢量（30% 白圆 + 实白叉）。

| token | 值 | 说明 |
| :--- | ---: | :--- |
| `coui_tool_tips_delete_background_size` | 18dp | 图标大小，也就是点击区 |
| `coui_tool_tips_delete_icon_size` | 44dp | **只声明、从未被引用** |
| `coui_tool_tips_delete_icon_padding` | 12dp | **只声明、从未被引用** |
| `layout_marginEnd` | 12dp | |

> 之前研究笔记里的 44dp「触摸区」在官方布局里不存在 —— 18dp 的图标本身就是点击区。已作为更正记录。

## `caretShape` 是开关，不是形状

`PlainTooltip` / `RichTooltip` / `ImageTooltip` 都接受 `caretShape: Shape?`，但**只读取它是否为 null**。`TooltipSurface` 判断 `caretShape != null` 后，自己绘制官方的 `coui_white_sharp_middle_arrow_up.xml` 矢量（20 × 10.1dp，圆弧肩、2dp 圆头）—— 传入自定义形状会被接受并**静默忽略**。

这是已知的 API 瑕疵，记录而非掩盖：参数本应改为 `showCaret: Boolean`，但现在改会破坏所有既有调用点。