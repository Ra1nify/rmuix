# SearchBar

`SearchBar` 是 Rmuix 中用于用户输入搜索内容的组件。它提供了一个直观且易用的搜索界面，支持展开/收起状态切换以及搜索建议展示。

<div style="position: relative; height: 250px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=searchBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `COUISearchViewAnimate` 布局与样式、`coui_searchview_corner_rect_bg` drawable 以及 `coui_search*` 尺寸。

- 输入框高度为 `coui_search_bar_normal_background_height` = **40dp**（库内原 45dp）。
- 输入框背景是圆角矩形而不是胶囊：`coui_searchview_corner_rect_bg` 使用 `coui_search_view_corner` = **18dp**。`InputField` 现在用 `RoundedCornerShape(18.dp)` 取代 `CircleShape`。
- 输入文字 `coui_search_view_input_text_size` = **15sp**（库内原 17sp），占位符 `coui_search_view_text_hint_size` = **12sp**；组件现在会在显示占位符时套用该字号。
- 实际绘制的是 `coui_search_view_icon_normal` 这个 **20dp** 矢量，而不是 `coui_search_view_icon_size` 的 24dp（后者只是 `mIconMaxWidth` / `mIconMaxHeight`，留给自定义图标的上限）。它居中放在 `coui_search_bar_inner_search_icon_size` = **36dp** 的方框里：`coui_search_bar_inner_search_icon_start_gap` 4dp + `(36 - 20) / 2` = 8dp，可见边缘正好离输入框 12dp。`SearchBarDefaults.IconStartMargin` 仍取 12dp，两侧留白 `coui_search_view_margin_left/right` = **16dp** 不变。

**BREAKING：** 输入框默认值由 45dp / 胶囊 / 17sp 改为 40dp / 18dp 圆角 / 15sp，占位符以 12sp 渲染。

输入框填充**不是**不透明的 surface 角色：`COUISearchBar` 用 `coui_search_view_selector_color_normal` 赋给 `mNormalBackgroundColor`，亮色 `#14000000`、暗色 `#1AFFFFFF`；按压态 `coui_search_view_selector_color_pressed` 为 `#1E000000` / `#33FFFFFF`。组件原默认是主题 `surfaceContainerHigh`，现改为 `searchBarFieldColor(dark)`（另提供 `searchBarFieldPressedColor(dark)` 供按压层使用）。drawable 提到的 `couiSearchviewHintBgColorNormal` / `…Disabled` 在本 APK 中仍未赋值。

### 功能按钮

官方搜索栏尾部的文字按钮（`SearchBarFunctionButton`）已提供：**40dp** 胶囊
（`coui_search_bar_normal_background_height`）、背景 `?attr/couiColorBar`（`#FAFAFA` / `#1E1E1E`）、左右 **16dp**
内边距（`coui_search_bar_functional_button_text_padding_horizontal`）、最大宽 **152dp**
（`coui_search_bar_functional_button_max_width`）、**14sp** sans-serif-medium（`couiTextAppearanceButton`）、
默认文案 `coui_search_view_cancel`（"Cancel"），按压走 `COUIViewTouchAnimator`：按实测按钮尺寸取
`PressCalc.calculateUniformScaleBreakdown()` 目标（40dp 高时约 0.89），弹簧为 `PressParamsExtKt` 的 jelly 默认值
（按下 response 0.25 / bounce 0，回弹 response 0.5 / bounce 0.6）。通过 `SearchBar` 的
`outsideEndAction` 传入：

```kotlin
SearchBar(
    inputField = { InputField(query, onQueryChange, onSearch, expanded, onExpandedChange) },
    onExpandedChange = { expanded = it },
    expanded = expanded,
    outsideEndAction = {
        SearchBarFunctionButton(text = "Cancel", onClick = { expanded = false })
    },
) { /* 结果 */ }
```

**仍待复核：** 周边的 `searchBackground`（`coui_search_view_search_background`）仍完全透明；展开/收起动画编排
（`COUISearchViewAnimate`，100KB）未移植；输入框与按钮之间 1dp × 16dp 的
`coui_search_bar_functional_button_divider_width` / `_height`（1dp x 16dp）分隔线按官方实现
（`SearchBarBackgroundView` 内部绘制）由调用方组合；使用组件的 `length` 参数：
`VerticalDivider(thickness = 1.dp, length = 16.dp)`，见[功能按钮几何](#功能按钮几何)。

**清除按钮按压反馈：** `SearchBarIconButton` 用整圆的 `COUIMaskEffectDrawable`（`?attr/couiColorPress`，`#1F000000` / `#33FFFFFF`）而不是 ripple；清除按钮现在也如此。

### 输入框高度与外层容器高度（2026-11-12）

`homepage_toolbar.xml` 用 `coui_search_view_height` = **52dp**（`layout_height` 与 `minHeight`）给整个动画搜索视图定高，而输入框本身是 `coui_search_bar_normal_background_height` = `coui_search_view_wrapper_height` = **40dp**；相差的 12dp 是输入框上下留出的呼吸空间。库内只实现输入框，所以暴露 40dp 是对的——52dp 属于 `SearchBar` 并不替代的那层容器。

同族还有几个高度类 token（库内均未使用）：`coui_search_view_animate_height` 50dp（展开激活态）、`coui_search_view_min_height` 33dp、`coui_search_view_collapsed_min_height` 1px。

### 功能按钮几何

`SearchBarFunctionButton` 已实现按钮本体（`coui_search_bar_normal_background_height` 40dp、
`coui_search_bar_functional_button_max_width` 152dp、`coui_search_bar_functional_button_text_padding_horizontal`
16dp、`couiTextAppearanceButton` 14sp）。按钮**周边**的间距仍归调用方，因为 `SearchBar` 的
`outsideEndAction` 是一个通用插槽：

| Token | 值 | 说明 |
| --- | --- | --- |
| `coui_search_bar_functional_button_start_gap` | 12dp | 输入框与按钮之间的间距 |
| `coui_search_bar_functional_button_end_gap_compat` / `_medium` / `_expanded` | 4dp / 12dp / 28dp | 响应式尾部间距 |
| `coui_search_bar_functional_button_offset_distance` | 60dp | 滑入位移 |
| `coui_search_bar_functional_button_divider_width` / `_height` | 1dp / 16dp | 可选分隔线 |
| `coui_search_bar_edittext_end_gap` | 4dp | 文本与输入框边缘的间距 |
| `coui_search_bar_animation_translate_extra` | 5dp | 展开动画的额外位移 |

需要官方排布的调用方在 `outsideEndAction` 里写
`Row { Spacer(12.dp); VerticalDivider(thickness = 1.dp, length = 16.dp); SearchBarFunctionButton(...) }` 即可。

### 清除按钮的真实几何（2026-11-12）

尾部清除按钮不是"带颜色的图标"。`COUISearchBar` 通过 `configImageViewDrawable` 直接 inflate `ic_edit_text_delete_search_view`（36dp 视口），所以画出来的就是这个矢量本身：

- **18dp** 圆（`M18,18m-9,0a9,9 …`），填充 `?attr/couiColorHintNeutral`（`#42000000` / `#4DFFFFFF`）；
- **纯白填充**叉形（`#ffffff`），跨度 **8.204dp**（36dp 视口内 13.898..22.102）、笔画厚 **1.6dp**，两端是 45° 斜切 —— 之前画的是两条方头线段（8.2dp 长、1.13dp 粗），更细且端头是直角；
- 整个 **36dp** 方框是触摸区域。

`InputField` 原先画的是 `RmuixIcons.Basic.SearchCleanup`，tint 取 `onSurfaceContainerHighest`；现在改为用 `Canvas` 按 `SearchBarStockClearIconSize` / `SearchBarStockClearGlyphLength` / `SearchBarStockClearGlyphThickness` 与 `searchBarClearIconBackground(dark)` 画出圆与叉。

**BREAKING：** 默认尾部图标改为官方 18dp 圆 + 白叉，触摸区为 36dp 方框；淡入淡出改用选择器的 `enterFadeDuration` 180ms / `exitFadeDuration` 360ms；放大镜 tint 由 `onSurfaceContainerHigh` 改为 `couiColorSecondNeutral`（`#8A000000` / `#8AFFFFFF`）。

前置放大镜的 tint 来自 `coui_search_view_icon_normal` 的 `?attr/couiColorSecondNeutral`，即 `coui_color_secondary_neutral`（`#8A000000` / `#8AFFFFFF`），因此统一走 `searchBarIconColor(dark)`，不再取 surface 的内容色角色。
## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.SearchBar
import io.github.ra1nify.rmuix.kmp.basic.InputField
```

## 基本用法

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }

SearchBar(
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { /* 处理搜索操作 */ },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it }
) {
    // 搜索结果内容
    Column {
        // 在这里添加搜索建议或结果
    }
}
```

## 属性

### SearchBar 属性

| 属性名               | 类型                                 | 说明            | 默认值                 | 是否必须 |
|-------------------|------------------------------------|---------------|---------------------|------|
| inputField        | @Composable () -> Unit             | 搜索输入框组件       | -                   | 是    |
| onExpandedChange  | (Boolean) -> Unit                  | 展开状态变化的回调     | -                   | 是    |
| modifier          | Modifier                           | 应用于搜索栏的修饰符    | Modifier            | 否    |
| insideMargin      | DpSize                             | 内部边距          | SearchBarDefaults.InsideMargin | 否    |
| expanded          | Boolean                            | 是否展开显示搜索结果    | false               | 否    |
| outsideEndAction. | @Composable (() -> Unit)?          | 展开时显示在右侧的操作组件 | null                | 否    |
| content           | @Composable ColumnScope.() -> Unit | 展开时显示的内容      | -                   | 是    |

### InputField 属性

| 属性名               | 类型                        | 说明            | 默认值      | 是否必须 |
|-------------------|---------------------------|---------------|----------|------|
| query             | String                    | 搜索框中的文本内容     | -        | 是    |
| onQueryChange     | (String) -> Unit          | 文本内容变化时的回调    | -        | 是    |
| onSearch          | (String) -> Unit          | 执行搜索操作时的回调    | -        | 是    |
| expanded          | Boolean                   | 是否处于展开状态      | -        | 是    |
| onExpandedChange  | (Boolean) -> Unit         | 展开状态变化的回调     | -        | 是    |
| modifier          | Modifier                  | 应用于输入框的修饰符    | Modifier | 否    |
| label             | String                    | 搜索框为空时显示的提示文本 | ""       | 否    |
| enabled           | Boolean                   | 是否启用搜索框       | true     | 否    |
| textStyle         | TextStyle                 | 搜索框中文本的样式     | null     | 否    |
| color             | Color                     | 输入框胶囊的背景色，自绘背景（如背景模糊）时可传 Color.Transparent | `searchBarFieldColor(dark)`：亮色 `#14000000` / 暗色 `#1AFFFFFF` | 否    |
| leadingIcon       | @Composable (() -> Unit)? | 前置图标          | 默认放大镜    | 否    |
| trailingIcon      | @Composable (() -> Unit)? | 后置图标          | 默认清除按钮   | 否    |
| interactionSource | MutableInteractionSource? | 交互源           | null     | 否    |

### SearchBarDefaults 对象

SearchBarDefaults 对象提供了 SearchBar 和 InputField 组件的默认值。

#### 常量

| 常量名                   | 类型     | 说明                        | 默认值              |
| ------------------------ | -------- | --------------------------- | ------------------- |
| InsideMargin             | DpSize   | SearchBar 的内部边距        | DpSize(12.dp, 0.dp) |
| InputFieldMinHeight      | Dp       | InputField 的最小高度（`coui_search_bar_normal_background_height`） | 40.dp |
| InputFieldFontSize       | TextUnit | InputField 标签字号（`coui_search_view_input_text_size`） | 15.sp |
| InputFieldHintFontSize   | TextUnit | 占位符字号（`coui_search_view_text_hint_size`） | 12.sp |
| InputFieldCornerRadius   | Dp       | 输入框圆角（`coui_search_view_corner`） | 18.dp |
| IconSize                 | Dp       | 搜索图标尺寸（`coui_search_view_icon_normal` 矢量本身 20dp；`coui_search_view_icon_size` 24dp 只是自定义图标上限） | 20.dp |
| IconStartMargin          | Dp       | 搜索图标起始留白（`coui_search_view_search_icon_margin`） | 12.dp |
| SideMargin               | Dp       | 搜索栏两侧留白（`coui_search_view_margin_left/right`） | 16.dp |
| LeadingIconStartPadding  | Dp       | 默认前置图标的可见起始边距；布局由原厂 4dp 盒间距 + (36 - 20) / 2 推出 | 12.dp |
| LeadingIconEndPadding    | Dp       | 仅为源码兼容保留；原厂 36dp 图标盒已取代它，不再影响布局 | 8.dp |
| TrailingIconStartPadding | Dp       | 默认后置图标的起始内边距    | 8.dp                |
| TrailingIconEndPadding   | Dp       | 默认后置图标的结束内边距    | 16.dp               |

## 进阶用法

### 带图标的搜索栏

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }

SearchBar(
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { /* 处理搜索操作 */ },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            leadingIcon = {
                Icon(
                    modifier = Modifier.padding(start = 12.dp, end = 8.dp),
                    imageVector = RmuixIcons.Search,
                    contentDescription = "搜索"
                )
            }
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it }
) {
    // 搜索结果内容
}
```

### 带搜索建议的搜索栏

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }
val suggestions = listOf("建议 1", "建议 2", "建议 3")

SearchBar(
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { /* 处理搜索操作 */ },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it }
) {
    Column {
        suggestions.forEach { suggestion ->
            BasicComponent(
                title = suggestion,
                onClick = {
                    searchText = suggestion
                    expanded = false
                }
            )
        }
    }
}
```

### 带取消按钮的搜索栏

```kotlin
var searchText by remember { mutableStateOf("") }
var expanded by remember { mutableStateOf(false) }

SearchBar(
    modifier = Modifier.padding(horizontal = 12.dp),
    inputField = {
        InputField(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = { expanded = false },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            label = "搜索"
        )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it },
    outsideEndAction = {
        Text(
            modifier = Modifier
                .padding(start = 12.dp)
                .clickable(
                    interactionSource = null,
                    indication = null
                ) {
                    expanded = false
                    searchText = ""
                },
            text = "取消",
            color = RmuixTheme.colorScheme.primary
        )
    }
) {
    // 搜索结果内容
}
```

## 展开 / 收起时序

`COUISearchViewAnimate` 用四个 `PathInterpolator` 和一组 `DEFAULT_*_DURATION` 常量驱动过渡。`SearchBar` 现在用它们替代框架默认值：

| 常量 | 值 | 用途 |
| :--- | ---: | :--- |
| `DEFAULT_SEARCH_VIEW_OFFSET_CHANGE_DURATION` | 450ms | 尾部动作滑入 |
| `DEFAULT_SEARCH_VIEW_SCALE_CHANGE_DURATION` | 450ms | **已覆盖**：尽管叫 scale，这个动画从不改 `scaleX`/`scaleY`，而是在 `COUIMoveEaseInterpolator` 下把 `marginEnd` 从 `shrinkWidth` 写到 `originWidth`，也就是和 `slideSpec()` 同为 450ms 的宽度动画 |
| `DEFAULT_BUTTON_MOVE_DURATION` | 450ms | 按钮位移 |
| `DEFAULT_BUTTON_ALPHA_CHANGE_DURATION` | 350ms | 按钮透明度 |
| `NON_INSTANT_SEARCH_BUTTON_ALPHA_CHANGE_DURATION` | 100ms | 非瞬时按钮透明度 |
| `DEFAULT_FADE_DURATION` | 150ms | 结果容器淡入淡出 |
| `DEFAULT_PRESS_FEEDBACK_DURATION` | 150ms | 按压反馈 |

两条曲线都是普通 `PathInterpolator`：

| 类 | 控制点 | Compose 对应 |
| :--- | :--- | :--- |
| `COUIMoveEaseInterpolator` | `cubic-bezier(0.3, 0.0, 0.1, 1.0)` | `SearchBarDefaults.MoveEase` |
| `COUIEaseInterpolator` | `cubic-bezier(0.33, 0.0, 0.67, 1.0)` | `SearchBarDefaults.Ease` |

`SearchBarDefaults.slideSpec()`（450ms、MoveEase）驱动尾部动作的生长 / 收缩 / 滑动，`SearchBarDefaults.fadeSpec()`（150ms）驱动结果容器。
