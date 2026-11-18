# ProgressIndicator

`ProgressIndicator` 是 Rmuix 中的进度指示组件，用于展示操作的进度状态。提供了线性进度条、环形进度条和无限旋转指示器三种样式，适用于不同场景下的加载和进度展示需求。

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=progressIndicator" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `COUIHorizontalProgressBar`、`COUICircularProgressBar`、`coui_progressbar_*_selector` 颜色列表、`Widget.COUI.COUICircularProgressBar` 与 `coui_circular_progress_*` 尺寸。

- 线性轨道：`couiHorizontalProgressBarBackgroundColor` → `coui_progressbar_background_selector` → `?attr/couiColorDivider`（`#1F000000` / `#33FFFFFF`）。库内原先用 `secondaryContainer`（`#F0F0F0` / `#434343`）。
- 线性填充：`coui_progressbar_progress_selector` → `?attr/couiColorPrimary`（`#0080FF` / `#1A8CFF`）。库内保持 accent 角色（`RmuixTheme.colorScheme.primary`），色值差异属于调色板问题，不是组件 bug。
- 线性填充几何：`COUIHorizontalProgressBar.onDraw` 画的是**一整条与轨道等宽的圆角矩形**，只把它的尾端对到进度点，再与圆角轨道求交（0 时什么都不画，1 时正好铺满轨道）。库内现在同样实现，并在 RTL 下镜像到右端；此前 0 时也会画一个最小胶囊。
- 环形：`Widget.COUI.COUICircularProgressBar` 的 `couiCircularProgressBarColor` = `couiColorHintNeutral`（`#42000000` / `#4DFFFFFF`）、`couiCircularProgressBarTrackColor` = `couiColorDivider`，也就是说官方是**中性色环 + 分隔线轨道**，不是 accent。`CircularProgressIndicator` 因此改用 `circularProgressIndicatorColors()`。
- 环形预设：中号 30dp / 3dp 描边，大号 40dp / 5dp（`coui_circular_progress_medium_length` 等）。`Widget.COUI.COUICircularProgressBar` 声明 `couiCircularProgressBarSize = large_size`，所以 stock 默认是**大号**；组件默认尺寸/描边随之由中号改为 40dp / 5dp。
- 官方控件是 `ProgressBar` 子类，`state_enabled` 驱动 selector；但库内没有 `enabled` 参数，禁用色目前用不上。

**BREAKING：** 线性轨道改为 `couiColorDivider`；`CircularProgressIndicator` 默认色改为中性环 + 轨道；默认环形预设改为 stock 的**大号**（40dp / 5dp），不再是中号 30dp / 3dp。`ProgressIndicatorDefaults` 新增 `circularProgressIndicatorColors()`。

- 环形预设现在成对明确：组件默认是**大号**（40dp / 5dp，即 stock 的 `large_size`），以 `ProgressIndicatorDefaults.LargeCircularSize` / `LargeCircularStrokeWidth` 暴露；中号（30dp / 3dp）仍保留为内部 stock 值，供需要的调用方使用。

**仍待复核：** 禁用 selector（`couiProgressbarBackgroundColorDisabled` / `couiProgressbarProgressColorDisabled`）在 APK 的主题里只声明未赋值，禁用色无法定论；没有 `enabled` 参数；大号 40dp / 5dp 预设已移植；环形的暂停/错误图标也已移植（`CircularProgressIndicatorState.Paused` / `.Error`，用官方尺寸的原语绘制）；源码里投影是**可选项**（`COUICircularProgressBar.setIsDrawShadow(mProgressBarType == 1)`，默认 type = 0），所以默认环形不画投影；对应 token 为 `coui_circular_progress_shadow_radius` 4dp、`_y_bias` 2dp、`#19000000`，尚未移植。`COUICompProgressIndicator`（lottie 加载器 + 10sp 说明文字）同样未移植。
## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.LinearProgressIndicator // 线性进度条
import io.github.ra1nify.rmuix.kmp.basic.CircularProgressIndicator // 环形进度条
import io.github.ra1nify.rmuix.kmp.basic.InfiniteProgressIndicator // 无限旋转指示器
```

## 基本用法

### 线性进度条

线性进度条可用于展示操作的进度：



```kotlin
// 确定进度的线性进度条
var progress by remember { mutableStateOf(0.3f) }

LinearProgressIndicator(progress = progress)
```

```kotlin
// 不确定进度的线性进度条
LinearProgressIndicator()
```


### 环形进度条

环形进度条适用于需要节省空间的场景：

```kotlin
// 确定进度的环形进度条
var progress by remember { mutableStateOf(0.7f) }

CircularProgressIndicator(progress = progress)
```

```kotlin
// 不确定进度的环形进度条
CircularProgressIndicator()
```

### 无限旋转指示器

无限旋转指示器适用于无法确定操作时长的场景：

```kotlin
InfiniteProgressIndicator()
```

## 组件状态

所有进度指示器组件都支持确定进度和不确定进度两种状态：

### 确定进度状态

当提供具体的进度值（0.0-1.0之间的浮点数）时，进度指示器会显示确切的进度：

```kotlin
var progress by remember { mutableStateOf(0.6f) }

LinearProgressIndicator(progress = progress)
CircularProgressIndicator(progress = progress)
```

### 不确定进度状态

当进度值为 null 时，进度指示器会显示动画效果，表示操作正在进行但无法确定具体进度：

```kotlin
LinearProgressIndicator(progress = null)
CircularProgressIndicator(progress = null)
```

### 圆形进度阴影（2026-11-14）

`CircularProgressIndicator(showShadow = true)` 会绘制官方阴影层：
`coui_circular_progress_shadow_radius` 4dp、`_x_bias` 0dp、`_y_bias` 2dp、`_color` `#19000000`。
**默认关闭**：官方 drawable 只在 `couiCircularProgressBarType == 1` 时装阴影层
（`setIsDrawShadow(1 == mProgressBarType)`），而该类型默认是 0——官方默认同样没有阴影。
`ProgressIndicatorDefaults.ShadowRadius` / `ShadowYBias` / `ShadowColor` 暴露这三个值；
`ProgressShadowRenderingTest` 钉住了「阴影会压暗圆环下方的像素」。

## 属性

### LinearProgressIndicator 属性

| 属性名   | 类型                    | 说明                           | 默认值                                                         | 是否必须 |
| -------- | ----------------------- | ------------------------------ | -------------------------------------------------------------- | -------- |
| modifier | Modifier                | 应用于进度条的修饰符           | Modifier                                                       | 否       |
| progress | Float?                  | 当前进度值，null表示不确定状态 | null                                                           | 否       |
| colors   | ProgressIndicatorColors | 进度条的颜色配置               | ProgressIndicatorDefaults.progressIndicatorColors()            | 否       |
| height   | Dp                      | 进度条的高度                   | ProgressIndicatorDefaults.DefaultLinearProgressIndicatorHeight | 否       |

### CircularProgressIndicator 属性

| 属性名      | 类型                    | 说明                           | 默认值                                                                | 是否必须 |
| ----------- | ----------------------- | ------------------------------ | --------------------------------------------------------------------- | -------- |
| modifier    | Modifier                | 应用于进度条的修饰符           | Modifier                                                              | 否       |
| progress    | Float?                  | 当前进度值，null表示不确定状态 | null                                                                  | 否       |
| colors      | ProgressIndicatorColors | 进度条的颜色配置               | ProgressIndicatorDefaults.circularProgressIndicatorColors()           | 否       |
| strokeWidth | Dp                      | 环形进度条的描边宽度           | ProgressIndicatorDefaults.DefaultCircularProgressIndicatorStrokeWidth | 否       |
| size        | Dp                      | 环形进度条的大小               | ProgressIndicatorDefaults.DefaultCircularProgressIndicatorSize        | 否       |

### InfiniteProgressIndicator 属性

| 属性名          | 类型     | 说明                 | 默认值                                                                    | 是否必须 |
| --------------- | -------- | -------------------- | ------------------------------------------------------------------------- | -------- |
| modifier        | Modifier | 应用于进度条的修饰符 | Modifier                                                                  | 否       |
| color           | Color    | 进度指示器的颜色     | Color.Gray                                                                | 否       |
| size            | Dp       | 进度指示器的大小     | ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorSize            | 否       |
| strokeWidth     | Dp       | 环形轨道的描边宽度   | ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorStrokeWidth     | 否       |
| orbitingDotSize | Dp       | 环绕点的大小         | ProgressIndicatorDefaults.DefaultInfiniteProgressIndicatorOrbitingDotSize | 否       |

### ProgressIndicatorDefaults 对象

ProgressIndicatorDefaults 对象提供了进度指示器组件的默认值和颜色配置。

#### 常量

| 常量名                                          | 类型 | 默认值 | 说明                     |
| ----------------------------------------------- | ---- | ------ | ------------------------ |
| DefaultLinearProgressIndicatorHeight            | Dp   | 6.dp   | 线性进度条的默认高度     |
| DefaultCircularProgressIndicatorStrokeWidth     | Dp   | 5.dp   | 环形进度条的默认描边宽度（大号预设） |
| DefaultCircularProgressIndicatorSize            | Dp   | 40.dp  | 环形进度条的默认大小（大号预设） |
| LargeCircularSize                               | Dp   | 40.dp  | 大号环形预设（`coui_circular_progress_large_length`） |
| LargeCircularStrokeWidth                        | Dp   | 5.dp   | 大号预设描边（`coui_circular_progress_large_stroke_width`） |
| DefaultInfiniteProgressIndicatorStrokeWidth     | Dp   | 2.dp   | 无限指示器的默认描边宽度 |
| DefaultInfiniteProgressIndicatorOrbitingDotSize | Dp   | 2.dp   | 无限指示器默认环绕点大小 |
| DefaultInfiniteProgressIndicatorSize            | Dp   | 20.dp  | 无限指示器的默认大小     |

#### 方法

| 方法名                    | 类型                    | 说明                         |
| ------------------------- | ----------------------- | ---------------------------- |
| progressIndicatorColors() | ProgressIndicatorColors | 创建线性进度条颜色（accent 填充 + 分隔线轨道） |
| circularProgressIndicatorColors() | ProgressIndicatorColors | 创建环形进度条颜色（中性提示色环 + 分隔线轨道） |

### ProgressIndicatorColors 类

| 属性名                  | 类型  | 说明                         |
| ----------------------- | ----- | ---------------------------- |
| foregroundColor         | Color | 进度指示器的前景色           |
| disabledForegroundColor | Color | 禁用状态时进度指示器的前景色 |
| backgroundColor         | Color | 进度指示器的背景色           |

## 进阶用法

### 自定义颜色的线性进度条

```kotlin
var progress by remember { mutableStateOf(0.4f) }

LinearProgressIndicator(
    progress = progress,
    colors = ProgressIndicatorDefaults.progressIndicatorColors(
        foregroundColor = Color.Red,
        backgroundColor = Color.LightGray
    )
)
```

### 调整大小的环形进度条

```kotlin
var progress by remember { mutableStateOf(0.75f) }

CircularProgressIndicator(
    progress = progress,
    size = 50.dp,
    strokeWidth = 6.dp
)
```

### 结合按钮使用的加载状态

```kotlin
var isLoading by remember { mutableStateOf(false) }
val scope = rememberCoroutineScope()

Button(
    onClick = {
        isLoading = true
        // 模拟操作
        scope.launch {
            delay(2000)
            isLoading = false
        }
    },
    enabled = !isLoading
) {
     AnimatedVisibility(
        visible = isLoading
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(end = 8.dp),
            size = 20.dp,
            strokeWidth = 4.dp
        )
    }
    Text("提交")
}
```

### 自定义无限旋转指示器

```kotlin
InfiniteProgressIndicator(
    color = RmuixTheme.colorScheme.primary,
    size = 40.dp,
    strokeWidth = 3.dp,
    orbitingDotSize = 4.dp
)
```

### 结合卡片使用的加载状态

```kotlin
var isLoading by remember { mutableStateOf(true) }

Card(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(16.dp)
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("加载中...")
            }
        } else {
            Text("内容已加载")
        }
    }
}
// 控制加载状态
LaunchedEffect(Unit) {
    delay(3000)
    isLoading = false
}
```
