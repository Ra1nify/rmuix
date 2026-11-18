#! /usr/bin/env markdown

# Snackbar

`Snackbar` 是 Rmuix 中用于在屏幕底部展示简短提示信息的轻量反馈组件。它可以附带操作按钮（例如“撤销”），并支持多种显示时长。

<div style="position: relative; height: 360px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=snackbar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Snackbar
import io.github.ra1nify.rmuix.kmp.basic.SnackbarHost
import io.github.ra1nify.rmuix.kmp.basic.SnackbarHostState
import io.github.ra1nify.rmuix.kmp.basic.SnackbarDuration
import io.github.ra1nify.rmuix.kmp.basic.SnackbarResult
```

## 基本用法

Snackbar 通常与 `Scaffold` 一起使用。你需要创建一个 `SnackbarHostState`，将其传给 `SnackbarHost`，再通过 `showSnackbar` 来显示消息：

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

Scaffold(
    snackbarHost = {
        SnackbarHost(state = snackbarHostState)
    },
) { paddingValues ->
    Box(
        modifier = Modifier
            .padding(paddingValues),
    ) {
        TextButton(
            text = "显示消息",
            onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("这是一条短提示")
                }
            },
        )
    }
}
```

## SnackbarHostState 与 showSnackbar

`SnackbarHostState` 负责管理 Snackbar 消息队列。

### showSnackbar

```kotlin
suspend fun SnackbarHostState.showSnackbar(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration = SnackbarDuration.Short,
): SnackbarResult
```

| 参数名             | 类型             | 说明                             | 默认值                    | 是否必须 |
| ------------------ | ---------------- | -------------------------------- | ------------------------- | -------- |
| message            | String           | 要显示在 Snackbar 中的文本       | -                         | 是       |
| actionLabel        | String?          | 操作按钮的文本（可选）          | null                      | 否       |
| withDismissAction  | Boolean          | 是否显示关闭图标                 | false                     | 否       |
| duration           | SnackbarDuration | Snackbar 显示的时长              | SnackbarDuration.Short    | 否       |

返回值 `SnackbarResult` 用于区分 Snackbar 是被操作按钮触发，还是自然消失或关闭。

### 获取最新或最旧的 Snackbar

```kotlin
suspend fun SnackbarHostState.newestSnackbarData(): SnackbarData?
suspend fun SnackbarHostState.oldestSnackbarData(): SnackbarData?
```

你可以通过返回的 `SnackbarData` 调用 `dismiss()` 或 `performAction()`，以手动关闭最新或最旧的 Snackbar。

## SnackbarHost

`SnackbarHost` 负责根据给定的 `SnackbarHostState` 渲染屏幕上的 Snackbar。

```kotlin
@Composable
fun SnackbarHost(
    state: SnackbarHostState,
    modifier: Modifier = Modifier,
    canSwipeToDismiss: Boolean = true,
    content: @Composable (SnackbarData) -> Unit = { Snackbar(it) },
)
```

| 参数名            | 类型                               | 说明                              | 默认值             | 是否必须 |
| ----------------- | ---------------------------------- | --------------------------------- | ------------------ | -------- |
| state             | SnackbarHostState                  | 管理 Snackbar 队列的状态          | -                  | 是       |
| modifier          | Modifier                           | 应用于宿主容器的修饰符            | Modifier           | 否       |
| canSwipeToDismiss | Boolean                            | 是否允许通过滑动手势关闭 Snackbar | true               | 否       |
| content           | @Composable (SnackbarData) -> Unit | 自定义每一条 Snackbar 的内容      | `{ Snackbar(it) }` | 否       |

大多数情况下，可以直接使用默认的 `content`，即内置的 `Snackbar` 视觉样式。

## Snackbar

`Snackbar` 定义了默认的消息样式。

```kotlin
@Composable
fun Snackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = SnackbarDefaults.CornerRadius,
    colors: SnackbarColors = SnackbarDefaults.snackbarColors(),
    insideMargin: PaddingValues = SnackbarDefaults.InsideMargin,
    icon: (@Composable () -> Unit)? = null,
)
```

| 参数名       | 类型           | 说明                     | 默认值                            | 是否必须 |
| ------------ | -------------- | ------------------------ | --------------------------------- | -------- |
| data         | SnackbarData   | 包含消息与操作信息的数据 | -                                 | 是       |
| modifier     | Modifier       | 应用于 Snackbar 容器的修饰符 | Modifier                      | 否       |
| cornerRadius | Dp             | Snackbar 的圆角半径      | SnackbarDefaults.CornerRadius     | 否       |

> 单行/换行通过 `onTextLayout` 的 `lineCount` 自动判断（对应 `COUISnackBar.adjustLayout()` 的 `isVertical()`）：单行时圆角 = 实测高度 / 2（官方 `getOutline` 用 `view.getHeight() / 2f`，bar 是 `wrap_content`、约 39dp，并没有固定 48dp），换行时保持 16dp；传入其它 `cornerRadius` 会关闭自动切换。`CornerRadiusSingleLine` 仅为源码兼容保留的旧占位值。
| colors       | SnackbarColors | Snackbar 的颜色配置      | SnackbarDefaults.snackbarColors() | 否       |
| insideMargin | PaddingValues  | 仅为兼容保留，已不生效    | SnackbarDefaults.InsideMargin    | 否       |
| icon | @Composable (() -> Unit)? | 可选前置图标：30dp + 16dp 间距 | null |

基础 snackbar（`coui_snack_bar_item.xml`）的前置图标是 **30dp**（`coui_snack_bar_icon_width`/`_height`），
后面留 16dp 间距，上下各 16dp margin，所以带图标的单行 bar 高度为图标 + 32dp。通知变体的 58dp 横向槽
（`coui_notification_snack_bar_*`）是另一个控件，这里不使用；需要时请自己传前置 composable。

### SnackbarColors 与 SnackbarDefaults

`SnackbarColors` 定义了 Snackbar 使用的颜色集合：

```kotlin
data class SnackbarColors(
    val containerColor: Color,
    val contentColor: Color,
    val actionContentColor: Color,
    val dismissActionContentColor: Color,
    val actionContainerColor: Color,
)
```

可以通过 `SnackbarDefaults.snackbarColors` 创建颜色配置：

```kotlin
// ColorOS 把条渲染成浅色浮起卡片，配深色文字
val colors = SnackbarDefaults.snackbarColors(
    containerColor = Color(0xFFFFFFFF),        // 暗色主题用 Color(0xFF333333)
    contentColor = Color(0xE6000000),          // 暗色主题用 Color(0xE6FFFFFF)
    actionContentColor = Color(0xFF0080FF),    // ?attr/couiColorPrimaryTextOnPopup；深色 0xFF5C9DFF
    dismissActionContentColor = Color(0x8A000000),
    actionContainerColor = Color.Transparent,  // 操作是纯文字，没有胶囊
)
```

bar 使用 ColorOS 的 `couiColorSurfaceTop`（浅色 #FFFFFF / 深色 #333333），不是反色 surface。

ColorOS 的 action 是纯文字标签：默认布局不使用 `actionContainerColor`，`actionContentColor` 即文字颜色（`couiColorPrimaryTextOnPopup`）。

#### 常量

`SnackbarDefaults` 同时提供 `Snackbar` 使用的默认尺寸与内外边距：

| 常量名                                | 类型          | 说明                       | 默认值                                                |
| ------------------------------------- | ------------- | -------------------------- | ----------------------------------------------------- |
| CornerRadius                          | Dp            | 条的圆角半径               | 16.dp                                                 |
| CornerRadiusSingleLine | Dp | 旧占位值；单行圆角现在按实测高度 / 2 计算 | 24.dp |
| MaxWidth                              | Dp            | 条的最大宽度               | 328.dp                                                |

> 官方没有固定宽度：`COUISnackBar.getMaxWidth()` 会按可见显示区重建 `ResponsiveUIModel`，取 `MarginType.MARGIN_SMALL` 后返回 `calculateGridWidth(6)` —— 即**六列栅格宽**，随窗口变化。本库的 328dp 只是近似值。
| MarginBottom                          | Dp            | 与屏幕底部的间距           | 32.dp                                                 |
| ContentHorizontalPadding              | Dp            | 条内部水平内边距           | 16.dp                                                 |
| ContentVerticalPadding                | Dp            | 条内部垂直内边距           | 10.dp                                                 |
| ContentVerticalPaddingMultiline       | Dp            | 多行时上方内边距           | 12.dp                                                 |
| ContentVerticalPaddingMultilineBottom | Dp            | 多行时下方内边距           | 8.dp                                                  |
| IconSize                              | Dp            | 前置状态图标尺寸           | 30.dp                                                 |
| IconVerticalMargin | Dp | 前置图标上下内距 | 16.dp |
| IconWidth | Dp | 前置图标宽度（`coui_snack_bar_icon_width`） | 30.dp |
| IconHeight | Dp | 前置图标高度（`coui_snack_bar_icon_height`） | 30.dp |
| IconEndPadding | Dp | 图标后的间距 | 16.dp |
| ActionMaxWidth                        | Dp            | 操作文字的最大宽度         | 110.dp                                                |
| ActionMarginEnd                       | Dp            | 操作文字之后的间距         | 4.dp                                                  |
| ActionVerticalMargin                  | Dp            | 操作文字的垂直内边距       | 10.dp                                                 |
| ShadowRadius                          | Dp            | 条的投影半径               | 26.dp                                                 |
| InsideMargin                          | PaddingValues | 仅为兼容保留，已不生效     | PaddingValues(all = 12.dp)                            |
| OuterPadding                          | PaddingValues | Snackbar 的默认外边距      | PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp) |

## SnackbarDuration 与 SnackbarResult

### SnackbarDuration

`SnackbarDuration` 用于控制 Snackbar 显示的时长：

```kotlin
sealed interface SnackbarDuration {
    data object Short : SnackbarDuration
    data object Long : SnackbarDuration
    data object Indefinite : SnackbarDuration
    data class Custom(val durationMillis: Long) : SnackbarDuration
}
```

| 选项        | 说明                         | 显示时长        |
| ----------- | ---------------------------- | --------------- |
| Short       | 短提示                       | 约 4 秒         |
| Long        | 较长提示                     | 约 10 秒        |
| Indefinite  | 一直显示，需手动关闭         | 直到手动关闭    |
| Custom      | 自定义毫秒级时长             | 用户自定义      |

### SnackbarResult

`SnackbarResult` 用于描述 Snackbar 的完成方式：

```kotlin
enum class SnackbarResult {
    Dismissed,
    ActionPerformed,
}
```

## 进阶用法

### 带操作按钮的 Snackbar

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

TextButton(
    text = "显示带操作的提示",
    onClick = {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "这条消息包含操作按钮",
                actionLabel = "撤销",
                duration = SnackbarDuration.Short,
            )
            when (result) {
                SnackbarResult.ActionPerformed -> { /* 处理撤销 */ }
                SnackbarResult.Dismissed -> { /* 超时或被关闭 */ }
            }
        }
    },
)
```

### 可关闭且无限时长的 Snackbar

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val scope = rememberCoroutineScope()

TextButton(
    text = "显示无限时长提示",
    onClick = {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = "无限时长提示，需要手动关闭",
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite,
            )
        }
    },
)

TextButton(
    text = "关闭最早的一条",
    onClick = {
        scope.launch {
            snackbarHostState.oldestSnackbarData()?.dismiss()
        }
    },
)
```

> 按住弹条会**暂停**自动消失计时，松手后从**完整时长**重新开始（`COUISnackBar.onTouchEvent()`：`ACTION_DOWN`/`ACTION_MOVE` 移除 runnable，`ACTION_UP`/`ACTION_CANCEL` 用 `getDuration()` 重新投递）。官方**没有点击消除**。
