# ScrollBar

`ScrollBar` 是 Rmuix 中的可拖拽滚动指示条。它在内容滚动时淡入、在悬停或拖拽时变宽、停止后延迟淡出。`VerticalScrollBar` 与 `HorizontalScrollBar` 共用同一套实现，均由 `ScrollBarAdapter` 驱动。

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `com.coui.appcompat.scrollbar.COUIScrollBar` 与 `coui_scrollbar_*` 资源。

- 滑块宽度是算出来的，不是单个 token：`coui_scrollbar_wight`（14dp）减去两侧 `coui_scrollbar_drawable_default_inset`（5.5dp），常态实宽 **3dp**；按压时 inset 收到 `coui_scrollbar_drawable_pressed_inset`（4dp），实宽 **6dp**。
- 滑块矩形最短长度 `coui_scrollbar_min_height` = **48dp**，外层 `InsetDrawable` 再减去 `coui_scrollbar_margin_top` 8dp 与 `_margin_bottom` 16dp，所以可见最短为 **24dp**。组件默认把这两个 inset 作为 `VerticalTrackPadding`，因此 `ScrollBarDefaults.ThumbMinLength` 取可见的 24dp。
- `coui_scrollbar_color` 为 `#29000000`（亮）/ `#40FFFFFF`（暗）。`StateListDrawable` 的常态与按压分支读的是同一个资源，因此按压只变宽、不改透明度；库内原先在 `onSurface` 上取 0.1 / 0.3。
- 现在由 `scrollBarThumbAlpha(dark)` 按主题取 41/255 或 64/255，淡出仍与动画 `opacity` 相乘。
- 官方把 drawable 包在 `InsetDrawable` 里，上边距 `coui_scrollbar_margin_top` = **8dp**、下边距 `coui_scrollbar_margin_bottom` = **16dp**，所以轨道两端都不会贴边。`ScrollBarDefaults.VerticalTrackPadding` 承载这对非对称值，并成为 `VerticalScrollBar` 的默认 `trackPadding`；想恢复贴边轨道传 `PaddingValues(0.dp)` 即可。`HorizontalScrollBar` 仍保持 `PaddingValues(0.dp)`，因为官方横向滑块没有这对边距。

**BREAKING：** 默认拇指宽 3dp（原 3.64dp），最小可见长度 24dp（官方 48dp 矩形减去 8/16dp 上下 inset；原 36dp），静止与按下不再改变透明度，`VerticalScrollBar` 默认把轨道内缩 8dp/16dp，端部内距为 **5.5dp**（原 3.46dp），有 token 支撑，拖动时收紧到 **4dp**；轨道总宽仍是官方的 **14dp**（原约 9.9dp）。

**端部内距（2026-11-14）：** `EndPadding` 改为 `coui_scrollbar_drawable_default_inset` = **5.5dp**，高亮/拖动时动画到 `coui_scrollbar_drawable_pressed_inset` = **4dp**（`PressedEndPadding`）。连同 3dp 拇指，整条宽度正好 **14dp**，与 `coui_scrollbar_wight` 一致；此前的 3.46dp 来自九宫格实测，没有 token 支撑。

> **尺寸：** 滚动条自身只占 `thumbWidth + 2 * endPadding`（约 10dp），不会撑满你给的空间。
> 用法是叠在内容上：`Box { content; VerticalScrollBar(adapter, Modifier.align(Alignment.CenterEnd).fillMaxHeight()) }`。
> `ScrollBarRenderingTest` 钉住了悬停 6dp 拇指、4dp 按压端距与 14dp 的条宽。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.HorizontalScrollBar
import io.github.ra1nify.rmuix.kmp.basic.ScrollBarAdapter
import io.github.ra1nify.rmuix.kmp.basic.ScrollBarDefaults
import io.github.ra1nify.rmuix.kmp.basic.VerticalScrollBar
import io.github.ra1nify.rmuix.kmp.basic.rememberScrollBarAdapter
```

`ScrollBarAdapter`、`ScrollBarColors`、`ScrollBarDefaults`、`VerticalScrollBar`、`HorizontalScrollBar` 都标注了 `@ExperimentalScrollBarApi`。

## 基本用法

```kotlin
val listState = rememberLazyListState()
val adapter = rememberScrollBarAdapter(listState)

Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().padding(end = 12.dp),
    ) {
        items(100) { index -> Text("Item $index") }
    }
    VerticalScrollBar(
        adapter = adapter,
        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
    )
}
```

`rememberScrollBarAdapter` 接受 `ScrollState`、`LazyListState` 或 `LazyGridState`。

### 水平滚动条

```kotlin
val rowState = rememberScrollState()
val adapter = rememberScrollBarAdapter(rowState)

Box(modifier = Modifier.fillMaxWidth()) {
    Row(modifier = Modifier.horizontalScroll(rowState)) {
        repeat(20) { Text("Cell $it") }
    }
    HorizontalScrollBar(
        adapter = adapter,
        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
    )
}
```

## 属性

### VerticalScrollBar / HorizontalScrollBar

| 属性名        | 类型              | 说明                                       | 默认值                              | 必填 |
| ------------- | ----------------- | ------------------------------------------ | ----------------------------------- | ---- |
| adapter       | ScrollBarAdapter  | 与可滚动组件通信的适配器                   | -                                   | 是   |
| modifier      | Modifier          | 应用于滚动条的修饰符                       | Modifier                            | 否   |
| reverseLayout | Boolean           | 反转滚动与布局方向                         | false                               | 否   |
| trackPadding  | PaddingValues     | 轨道的额外内边距，用于避开内容 padding     | `VerticalScrollBar`：`ScrollBarDefaults.VerticalTrackPadding`（上 8dp / 下 16dp）；`HorizontalScrollBar`：`PaddingValues(0.dp)` | 否 |
| colors        | ScrollBarColors   | 滚动条颜色配置                             | ScrollBarDefaults.scrollBarColors() | 否   |
| thumbWidth    | Dp                | 滑块宽度                                   | ScrollBarDefaults.ThumbWidth        | 否   |
| cornerRadius  | Dp                | 圆角，`Dp.Unspecified` 时取滑块宽度的一半  | ScrollBarDefaults.CornerRadius      | 否   |
| thumbMinLength| Dp                | 滑块最短长度                               | ScrollBarDefaults.ThumbMinLength    | 否   |
| endPadding    | Dp                | 距末端的留白                               | ScrollBarDefaults.EndPadding        | 否   |

### ScrollBarDefaults 对象

| 常量名                     | 类型 | 值    | 说明 |
| -------------------------- | ---- | ----- | ---- |
| ThumbWidth                 | Dp   | 3.dp  | 常态滑块宽（`coui_scrollbar_wight` 14dp 减两侧 5.5dp inset） |
| DragThumbWidth             | Dp   | 6.dp  | 悬停/拖拽时滑块宽（两侧 4dp inset） |
| ThumbMinLength             | Dp   | 24.dp | 滑块最短可见长度（`coui_scrollbar_min_height` 48dp 减去 8/16dp inset） |
| EndPadding                 | Dp   | 5.5.dp | 距末端的留白（`coui_scrollbar_drawable_default_inset`） |
| PressedEndPadding          | Dp   | 4.dp   | 高亮/拖动时的端距（`coui_scrollbar_drawable_pressed_inset`） |
| CornerRadius               | Dp   | Dp.Unspecified | 圆角，未指定时取滑块宽度一半 |
| TouchTargetWidth           | Dp   | 48.dp | 触摸目标宽度 |
| ThumbAlpha                 | Float| 41f / 255f | 亮色滑块透明度（`coui_scrollbar_color` `#29000000`） |
| DragThumbAlpha             | Float| 41f / 255f | 按压态透明度，与 `ThumbAlpha` 相同 |
| FadeDelayMillis            | Int  | 1000  | 开始淡出前的延迟 |
| FadeDurationMillis         | Int  | 500   | 淡出时长 |
| DragAnimationDurationMillis| Int  | 150   | 滑块宽度动画时长 |

### ScrollBarColors 类

| 属性名     | 类型  | 说明                                                         |
| ---------- | ----- | ------------------------------------------------------------ |
| thumbColor | Color | 滑块颜色，`Color.Unspecified` 时用主题 `onSurface` 配官方透明度 |
| trackColor | Color | 轨道颜色，`Color.Unspecified` 时不显示轨道                     |

## 进阶用法

### 自定义颜色

```kotlin
VerticalScrollBar(
    adapter = adapter,
    colors = ScrollBarDefaults.scrollBarColors(
        thumbColor = RmuixTheme.colorScheme.primary,
        trackColor = RmuixTheme.colorScheme.surfaceContainer,
    ),
)
```

显式传入 `thumbColor` 时使用它自身的 alpha，不再套用官方按主题分档的透明度。

### 跳过内容内边距

```kotlin
VerticalScrollBar(
    adapter = adapter,
    trackPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
)
```
