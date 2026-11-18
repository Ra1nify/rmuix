# TopAppBar

`TopAppBar` 是 Rmuix 中的顶部应用栏组件，用于在界面顶部提供导航、标题和操作按钮。支持大标题模式和普通模式，以及滚动时的动态效果。

此组件通常与 `Scaffold` 组件结合使用，以便在应用程序的不同页面中保持一致的布局和行为。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=topAppBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## ColorOS 官方顶栏的两种形态

ColorOS 17 实际存在两种 app bar 布局，本组件按调用方式一一对应：

| 官方形态 | 布局 / 样式 | 本库对应 |
| :--- | :--- | :--- |
| **静态工具栏**（设置页默认） | `COUIAppBarLayout` / `COUIDividerAppBarLayout` + `COUIToolbar`（`common_appbar_layout.xml`、`common_divider_appbar_layout.xml`）：`toolbar_min_height` 52dp，标题 `textAppearanceSecondTitle` = `TextAppearance.COUI.Toolbar.SecondTitle`（18dp、`sans-serif-medium`、常规字重），**没有展开态大标题** | 不传 `largeTitle` 的 `TopAppBar(title = "...")`，以及 `SmallTopAppBar` |
| **可折叠栏** | `COUICollapsableAppBarLayout` + `COUIAppbarTitleStyle`：收起态 `coui_appbar_title_collapsed_text_size` 24dp **粗体**，展开态 `coui_appbar_title_expanded_text_size` 32dp `sans-serif-semibold`，展开高 110dp，上边距 54dp、左右 24dp | `TopAppBar(title, largeTitle = title)` 与 `BlurTopAppBar` |

**BREAKING：** `largeTitle` 原先默认等于 `title`，于是每条顶栏都会画出"展开态大标题"——标题重复、栏高
恒为 110dp。现在默认 `""`，即官方静态 `COUIToolbar` 形态；需要可折叠大标题时显式传
`largeTitle = title`（或其它文本）。`SmallTopAppBar` 的标题字号也从 24sp 改为官方静态栏的 18sp。

**标题度量**（`topAppBarTitleMetrics`，由 `TopAppBarDefaultsTest` 覆盖）：

| 形态 | 标题 |
| :--- | :--- |
| 静态（无 `largeTitle`） | 18sp Medium（`ToolbarTitleTextSize`） |
| 可折叠（有 `largeTitle`） | 24sp Bold（`TitleTextSize`） |
| 任一形态带 `subtitle` | 18sp Medium（`TitleTextSizeWithSubtitle`） |
| 展开态大标题 | 32sp SemiBold（`LargeTitleTextSize`） |

- `subtitle` 保持 `coui_appbar_subtitle_text_size` = 14dp（等于 `body2`，经 `TopAppBarDefaults.SubtitleTextSize` 应用）。
- `coui_appbar_title_collapsed_with_subtitle_text_size`（18dp）在 dump 中声明但从未被读取，因此"带副标题 18sp"是本库的选择。
**静态工具栏几何（`COUIToolbar`）：** 顶栏自身 padding 为 `toolbar_normal_menu_padding_left` = 8dp
（其中导航按钮另有 `coui_action_bar_navigation_padding_*_material` 各 12dp）与
`toolbar_normal_menu_padding_right` = 11dp；标题在导航按钮之后
`coui_toolbar_gap_between_navigation_and_title` = 4dp（`supportTitleMarginStart` 解析为
`coui_toolbar_support_margin_start` = 0dp）。副标题 13sp、颜色 `couiColorSecondNeutral`。
`coui_toolbar_subtitle_offset_top`（-2.5dp）是**死 token**：`R.dimen` 里声明但 `COUIToolbar` 从不读取，
因此不施加偏移。

- 展开态大标题**不位移、不淡出**：ColorOS 把折叠渐变的起始比例设为 1（`fixCollapsingTitleAlpha()`）并强制文字不透明，
  内容滚动时标题位置与不透明度固定。本库早期会平移/交叉淡入，2026-11-14 已移除，并由 `TopAppBarGeometryTest` 钉住。

**分隔线默认不画：** 设置应用顶栏加载 `common_divider_appbar_layout.xml`，其中 `settings:hasDivider="false"`，
所以 ColorOS 顶栏下方**没有**发丝线；`COUIDividerAppBarLayout.isDividerShown()` 为
`mHasDivider && !mBlurDividerSuppressed`，即模糊生效时同样抑制分隔线。真正分隔顶栏的是**连续渐变模糊**
（`AppBarBlurHelper.setGradientBlurEnabled(true)`；`GRADIENT_DARK_ALPHA` 0.4、`NORMAL_BLUR_DARK_ALPHA` 0.6，
以及 `GradientBlurConfig(startBlurRadius, endBlurRadius, gradientBlurSpeed, ...)`），叠在顶栏自身的
`coui_toolbar_bg` 实色 `?attr/couiColorBackgroundWithCard` 上。`TopAppBar(showDivider = true)` 仍会在需要旧观感时
画 `toolbar_divider_height` 0.33dp 发丝线（左右 24dp→0dp 内缩、透明度随滚动距离渐变）。

**折叠期的底部内边距：** 可折叠栏把 `coui_appbar_start_padding_bottom`（12dp，
`TopAppBarDefaults.StartPaddingBottom`）随折叠插值到 `coui_appbar_end_padding_bottom`（0dp，
`EndPaddingBottom`）（`topAppBarCollapseBottomPaddingPx`，由滚动状态 `collapsedFraction` 驱动）。官方同时
把分隔线与副标题按 `-(start + (end - start) * fraction)` / `(end - start) * (1 - fraction)` 平移；两者等价于
"视图不动、内边距动画"，本组件即按后者渲染——分隔线放在内边距上方而非最底边。不可折叠的静态 bar 保留库内
固定 `SubtitleBottomPadding`（8dp），因为 padding token 只存在于可折叠布局。

`coui_appbar_title_icon_size`（16dp）是**死 token**：`R.java` 里声明了但反编译代码无人读取，无需建模。
158dp 的"带搜索"变体只是样式——`COUICollapsingToolbarLayoutStyle.WithSearch`
（`coui_appbar_title_expanded_with_search_height` 158dp、`expandedTitleMarginTop` 54dp）在 `styles.xml` 中声明，
但**没有任何布局或代码真正应用它**，移植它属于臆测。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TopAppBar
import io.github.ra1nify.rmuix.kmp.basic.BlurTopAppBar
import io.github.ra1nify.rmuix.kmp.basic.SmallTopAppBar
import io.github.ra1nify.rmuix.kmp.basic.RmuixScrollBehavior
import io.github.ra1nify.rmuix.kmp.basic.rememberTopAppBarState
```

## 基本用法

### 静态 TopAppBar（官方默认形态）

不传 `largeTitle` 就是官方静态 `COUIToolbar` 顶栏：单行 52dp、18sp 标题，没有展开态大标题、也不会随滚动长高。
设置应用用的就是这一形态。

```kotlin
TopAppBar(
    title = "设置",
    navigationIcon = { IconButton(onClick = { /* back */ }) { Icon(RmuixIcons.Back, null) } },
    scrollBehavior = scrollBehavior, // 可选：没有大标题时顶栏不会伸缩
)
```

### 小标题顶部栏

```kotlin
Scaffold(
    topBar = {
        SmallTopAppBar(
            title = "标题",
            navigationIcon = {
                IconButton(onClick = { /* 处理点击事件 */ }) {
                    Icon(RmuixIcons.Back, contentDescription = "返回")
                }
            },
            actions = {
                IconButton(onClick = { /* 处理点击事件 */ }) {
                    Icon(RmuixIcons.More, contentDescription = "更多")
                }
            }
        )
    }
)
```

### 大标题顶部栏

```kotlin
Scaffold(
    topBar = {
        TopAppBar(
            title = "标题",
            largeTitle = "大标题", // 传入即启用可折叠形态；省略则为官方静态栏
            navigationIcon = {
                IconButton(onClick = { /* 处理点击事件 */ }) {
                    Icon(RmuixIcons.Back, contentDescription = "返回")
                }
            },
            actions = {
                IconButton(onClick = { /* 处理点击事件 */ }) {
                    Icon(RmuixIcons.More, contentDescription = "更多")
                }
            }
        )
    }
)
```

### 模糊大标题（BlurTopAppBar）

`BlurTopAppBar` 就是「大标题会随收起而模糊」的 `TopAppBar`：模糊量跟随收起进度，展开时清晰，
在大标题完全淡出的那一刻正好达到 `largeTitleBlurRadius`。

```kotlin
Scaffold(
    topBar = {
        BlurTopAppBar(
            title = "标题",
            largeTitle = "大标题",
            largeTitleBlurRadius = 12.dp,
            scrollBehavior = scrollBehavior,
        )
    }
)
```

`TopAppBar` 本身也有 `largeTitleBlurRadius`（默认 `0.dp`，即不模糊），不换组件也能拿到同样效果。


TopAppBar 支持随内容滚动时改变其显示状态：

```kotlin
val scrollBehavior = RmuixScrollBehavior()

Scaffold(
    topBar = {
        TopAppBar(
            title = "标题",
            largeTitle = "大标题", // 传入即启用可折叠形态；省略则为官方静态栏
            scrollBehavior = scrollBehavior
        )
    }
) { paddingValues ->
    // 内容区域需要考虑 padding
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            // 如需添加越界回弹效果，则应在绑定滚动行为之前添加
            .overScrollVertical()
            // 绑定 TopAppBar 滚动事件
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(top = paddingValues.calculateTopPadding())
    ) {
        // 列表内容
    }
}
```

## 自定义样式

### 自定义颜色

```kotlin
TopAppBar(
    title = "标题",
    color = RmuixTheme.colorScheme.primary,
    titleColor = RmuixTheme.colorScheme.onPrimary,
    largeTitleColor = RmuixTheme.colorScheme.onPrimary
)
```

### 自定义内容边距

```kotlin
TopAppBar(
    title = "标题",
    titlePadding = 32.dp
)
```

### 自定义图标边距

```kotlin
TopAppBar(
    title = "标题",
    navigationIconPadding = 12.dp,
    actionIconPadding = 12.dp
)
```

## 属性

### TopAppBar 属性

| 属性名                     | 类型                            | 说明                           | 默认值                            | 是否必须 |
| -------------------------- | ------------------------------- | ------------------------------ | --------------------------------- | -------- |
| title                      | String                          | 顶部栏标题                     | -                                 | 是       |
| modifier                   | Modifier                        | 应用于顶部栏的修饰符           | Modifier                          | 否       |
| color                      | Color                           | 顶部栏背景颜色                 | `backgroundWithCard(...)`：`#F0F1F2` / `#000000` | 否       |
| titleColor                 | Color                           | 折叠时小标题文字颜色           | RmuixTheme.colorScheme.onSurface  | 否       |
| largeTitle                 | String                          | 展开态大标题；留空为官方静态工具栏形态（传入即切换为可折叠形态） | "" | 否 |
| largeTitleColor            | Color                           | 展开时大标题文字颜色           | RmuixTheme.colorScheme.onSurface  | 否       |
| subtitle                   | String                          | 显示在标题栏下方的副标题文本   | ""                                | 否       |
| subtitleColor              | Color                           | 副标题文字颜色                 | RmuixTheme.colorScheme.onSurfaceVariantSummary  | 否       |
| navigationIcon             | @Composable () -> Unit          | 导航图标区域的可组合函数       | {}                                | 否       |
| actions                    | @Composable RowScope.() -> Unit | 操作按钮区域的可组合函数       | {}                                | 否       |
| scrollBehavior             | ScrollBehavior?                 | 控制顶部栏滚动行为             | null                              | 否       |
| defaultWindowInsetsPadding | Boolean                         | 是否应用默认窗口边距           | true                              | 否       |
| titlePadding          | Dp                              | 水平内容边距                   | TopAppBarDefaults.TitlePadding | 否       |
| navigationIconPadding      | Dp                              | 导航图标的起始边距             | TopAppBarDefaults.NavigationIconPadding | 否       |
| actionIconPadding          | Dp                              | 操作图标的末尾边距             | TopAppBarDefaults.ActionIconPadding | 否       |
| bottomContent              | @Composable () -> Unit          | 显示在标题栏下方的可组合内容 | {}                                | 否       |
| showDivider                | Boolean                         | 是否绘制原厂 0.33dp 分隔线     | **false** | 否       |
| dividerColor               | Color                           | 分隔线颜色（`couiColorDivider`） | TopAppBarDefaults.dividerColor() | 否       |
| largeTitleBlurRadius       | Dp                              | 大标题随收起产生的模糊量，`0.dp` 保持清晰 | 0.dp | 否 |

### SmallTopAppBar 属性

| 属性名                     | 类型                            | 说明                     | 默认值                            | 是否必须 |
| -------------------------- | ------------------------------- | ------------------------ | --------------------------------- | -------- |
| title                      | String                          | 顶部栏标题               | -                                 | 是       |
| modifier                   | Modifier                        | 应用于顶部栏的修饰符     | Modifier                          | 否       |
| color                      | Color                           | 顶部栏背景颜色           | `backgroundWithCard(...)`：`#F0F1F2` / `#000000` | 否       |
| titleColor                 | Color                           | 标题文字颜色             | RmuixTheme.colorScheme.onSurface  | 否       |
| subtitle                   | String                          | 显示在标题栏下方的副标题文本 | ""                                | 否       |
| subtitleColor              | Color                           | 副标题文字颜色           | RmuixTheme.colorScheme.onSurfaceVariantSummary  | 否       |
| navigationIcon             | @Composable () -> Unit          | 导航图标区域的可组合函数 | {}                                | 否       |
| actions                    | @Composable RowScope.() -> Unit | 操作按钮区域的可组合函数 | {}                                | 否       |
| scrollBehavior             | ScrollBehavior?                 | 控制顶部栏滚动行为       | null                              | 否       |
| defaultWindowInsetsPadding | Boolean                         | 是否应用默认窗口边距     | true                              | 否       |
| titlePadding          | Dp                              | 水平内容边距             | TopAppBarDefaults.TitlePadding | 否       |
| navigationIconPadding      | Dp                              | 导航图标的起始边距       | TopAppBarDefaults.NavigationIconPadding | 否       |
| actionIconPadding          | Dp                              | 操作图标的末尾边距       | TopAppBarDefaults.ActionIconPadding | 否       |
| bottomContent              | @Composable () -> Unit          | 显示在标题栏下方的可组合内容 | {}                                | 否       |

### TopAppBarDefaults 对象

TopAppBarDefaults 对象提供了 TopAppBar 和 SmallTopAppBar 组件的默认值。

#### 常量

| 常量名                    | 类型 | 说明                              | 默认值 |
| ------------------------- | ---- | --------------------------------- | ------ |
| TitlePadding    | Dp   | 导航图标与标题之间的间距（`coui_toolbar_gap_between_navigation_and_title`） | 4.dp |
| NavigationIconPadding      | Dp   | 工具栏起始内边距、导航图标之前（`toolbar_normal_menu_padding_left`） | 8.dp |
| ActionIconPadding          | Dp   | 工具栏末尾内边距、操作图标之后（`toolbar_normal_menu_padding_right`） | 11.dp |
| CollapsedHeight            | Dp   | TopAppBar 折叠时的高度           | 52.dp  |
| SmallTopAppBarCenterHeight | Dp   | SmallTopAppBar 布局的垂直中心高度 | 50.dp  |
| LargeTitleBottomPadding    | Dp   | 无副标题时大标题下方的底部边距   | 4.dp   |
| SubtitleBottomPadding      | Dp   | **静态** bar 副标题下方的底部边距（可折叠 bar 用 `StartPaddingBottom` / `EndPaddingBottom`） | 8.dp   |
| StartPaddingBottom         | Dp   | `coui_appbar_start_padding_bottom`：可折叠 bar 展开时的底部内边距 | 12.dp |
| EndPaddingBottom           | Dp   | `coui_appbar_end_padding_bottom`：折叠后的底部内边距 | 0.dp |
| ToolbarTitleTextSize       | TextUnit | 静态栏标题字号（`TextAppearance.COUI.Toolbar.SecondTitle` / `textAppearanceSecondTitle`） | 18.sp |
| TitleTextSize              | TextUnit | 可折叠形态的收起态标题字号（`coui_appbar_title_collapsed_text_size`） | 24.sp |
| TitleTextSizeWithSubtitle  | TextUnit | 有副标题时的收起态标题字号（`coui_appbar_title_collapsed_with_subtitle_text_size`） | 18.sp |
| SubtitleTextSize           | TextUnit | 可折叠形态副标题字号（`coui_appbar_subtitle_text_size`） | 14.sp |
| ToolbarSubtitleTextSize    | TextUnit | 静态工具栏形态副标题字号（`coui_toolbar_subtitle_text_size`，经 `TextAppearance.COUI.AppCompatSupport.Toolbar.SubTitle`） | 13.sp |
| LargeTitleTextSize         | TextUnit | 展开态大标题字号（`coui_appbar_title_expanded_text_size`） | 32.sp |
| DividerHeight              | Dp   | 分隔线高度（`toolbar_divider_height`） | 0.33.dp |
| DividerExpandedMargin      | Dp   | 展开时分隔线左右内缩（`coui_appbar_divider_expanded_margin_horizontal`） | 24.dp |
| DividerCollapsedMargin     | Dp   | 收起时分隔线左右内缩（`coui_appbar_divider_collapsed_margin_horizontal`） | 0.dp |

### ScrollBehavior

RmuixScrollBehavior 是用于控制顶部栏滚动行为的配置对象。

#### rememberTopAppBarState

用于创建和记住 TopAppBarState：

```kotlin
val scrollBehavior = RmuixScrollBehavior(
    state = rememberTopAppBarState(),
    snapAnimationSpec = spring(stiffness = 2500f),
    canScroll = { true }
)
```

| 参数名             | 类型                        | 默认值                     | 说明                       |
| ------------------ | --------------------------- | -------------------------- | -------------------------- |
| state              | TopAppBarState              | rememberTopAppBarState()   | 控制滚动状态的状态对象     |
| canScroll          | () -> Boolean               | { true }                   | 控制是否允许滚动的回调     |
| snapAnimationSpec  | AnimationSpec\<Float>?      | spring(stiffness = 2500f)  | 定义顶部栏滚动后的吸附动画 |
| flingAnimationSpec | DecayAnimationSpec\<Float>? | rememberSplineBasedDecay() | 定义顶部栏滑动的衰减动画   |

## 进阶用法

### 处理窗口边距

```kotlin
TopAppBar(
    title = "标题",
    largeTitle = "大标题",
    defaultWindowInsetsPadding = false // 自行处理窗口嵌入边距
)
```

### 自定义滚动行为动画

```kotlin
var isScrollingEnabled by remember { mutableStateOf(true) }
val scrollBehavior = RmuixScrollBehavior(
    snapAnimationSpec = tween(durationMillis = 100),
    flingAnimationSpec = rememberSplineBasedDecay(),
    canScroll = { isScrollingEnabled } // 可以动态控制是否允许滚动
)

TopAppBar(
    title = "标题",
    largeTitle = "大标题",
    scrollBehavior = scrollBehavior
)
```

### 大标题和小标题结合使用

```kotlin
var useSmallTopBar by remember { mutableStateOf(false) }

Box(modifier = Modifier.fillMaxSize()) {
    if (useSmallTopBar) {
        SmallTopAppBar(
            title = "精简模式",
            navigationIcon = {
                IconButton(onClick = { useSmallTopBar = false }) {
                    Icon(
                        imageVector = RmuixIcons.Back,
                        contentDescription = "切换到大标题",
                        tint = RmuixTheme.colorScheme.onBackground
                    )
                }
            }
        )
    } else {
        TopAppBar(
            title = "标题",
            largeTitle = "展开模式",
            navigationIcon = {
                IconButton(onClick = { useSmallTopBar = true }) {
                    Icon(
                        imageVector = RmuixIcons.Back,
                        contentDescription = "切换到小标题",
                        tint = RmuixTheme.colorScheme.onBackground
                    )
                }
            }
        )
    }
}
```
