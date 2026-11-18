# TabRow

`TabRow` 是 Rmuix 中的导航组件，用于创建可横向滚动的标签页。提供了标准样式和带轮廓（Contour）样式两种变体，适用于内容分类和导航场景。

<div style="position: relative; height: 180px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=tabRow" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `COUITabLayout` / `COUITabLayoutBaseStyle`、`coui_tab_default_color` selector 与 `tablayout_*` 尺寸。

- 标签文字按 `couiTabTextColor` → `coui_tab_default_color`：选中与按压都解析为 `couiColorPrimaryNeutral`（`#E6000000` / `#E6FFFFFF`），常态为 `couiColorSecondNeutral`（`#8A000000` / `#8AFFFFFF`）。库内原先用 `onBackground` / `onSurfaceVariantSummary`。
- 按压态不论是否选中都用 `couiColorPrimaryNeutral`（`coui_tab_default_color`），因此按下一个未选中的标签会把它变深到选中色。
- 选中指示条：`couiTabIndicatorColor` = `couiColorPrimaryNeutral`（`#E6000000` / `#E6FFFFFF`），高 2dp（`tablayout_selected_indicator_height`）。`COUISlidingTabStrip.updateIndicatorPosition()` 取选中标签 `TextView` 的横向范围（indicator padding 为 0），`setIndicatorPosition()` 再把它扩到至少 `DEFAULT_MIN_INDICATOR` = **32dp** 并以标签中心居中。
**指示条几何：** `couiTabIndicatorBackgroundPaddingLeft` / `Right` 都是 `tablayout_background_padding` = **24dp**，但只作用于静态的 `couiTabIndicatorBackgroundColor` 轨道（`TabRowDefaults.IndicatorInset`）。
移动的选中下划线本身 indicator padding 为 0，横向覆盖选中标签、最小 **32dp**（`TabRowDefaults.MinIndicatorWidth`），而不是"tab 宽度减 24dp"。
- 另有一条贯穿整行的静态 2dp 轨道（`couiTabIndicatorBackgroundColor` = `tablayout_indicator_background` = `#0F000000` / `#19FFFFFF`，同样 24dp 内缩、2dp 高）。原实现缺这条，现已画在移动指示条下方。
- 行底色是 `coui_tablayout_bg` → `couiColorBackgroundWithCard`（`#F0F1F2` / `#000000`），不是主题的 `surface`。
- 尺寸：高 40dp（`tablayout_large_layout_height`）；标签间距随宽度响应——**20dp**（`coui_tab_layout_small_tab_spacing`）与 **24dp**（`_medium_tab_spacing`，宽度 ≥600dp 的 medium window class 起）；最小 72dp / 最大 128dp；渐隐边 58dp；文字 16sp（`tablayout_large_text_size`）；`couiTabEnableVibrator` 经 `HapticFeedbackType.SegmentTick` 生效。**更正：** 选中标签用 `sans-serif-medium`（`COUITabView.setSelected()` 在 `mNormalTypeface` / `mSelectedTypeface` 间切换），所以选中会改变字重；此前"选中不变字重 / 16dp 间距"的结论有误。

**BREAKING：** 默认标签文字色与指示条色改变，行底色改为 `couiColorBackgroundWithCard`，并新增静态指示轨道；`TabRowColors` 新增 `indicatorTrackColor`（`Color.Unspecified` 时回退官方细线）。

**按压反馈：** `COUITabView` 给每个 tab 套一层 8dp 圆角的 `COUIMaskEffectDrawable`（`setMaskRect(mTabRect, dpToPx(8), dpToPx(8))` + `setTouched(...)`），按下时画 `?attr/couiColorPress` 遮罩，**文字颜色不变**。本库现在也为按压 / 悬停（`?attr/couiColorHover`）/ 焦点（`?attr/couiColorFocus`）画这层遮罩，都用 0.3s 状态弹簧淡入。共享的行级 `interactionSource` 仍会让所有 tab 同时亮起，这是调用方的选择。

普通芯片已不再待办：[`Chip`](../selection/chip) 已覆盖 `Widget.COUI.Chip`（28dp/12sp 胶囊、`couiColorContainer8` 对 `couiColorPrimary`、`.Suggestion` 28dp / `.Selection` 32dp 变体，以及可选的关闭图形）；芯片组也已移植为 [`ChipGroup`](../selection/chipgroup)（折叠 3 行 + 28dp 箭头芯片），未建模的只剩重排动画与 `singleSelection` / `checkedChip` 属性。

**小号 / 中间标签样式：** `COUISmallTabLayoutStyle` 仍是 40dp 高、16sp 标签（`tablayout_large_text_size`；`COUIMiddleTabLayoutStyle` 只换成 `COUIMiddleTabViewStyle`，同样 16sp，此前写的「14sp + 1dp 指示条」是错的）。真正不同的是：

- `couiTabIndicatorBackgroundHeight` = 0dp（没有静态轨道）
- `couiTabIndicatorHeight` = 2dp（不变）
- `tablayout_small_min_divider` / `tablayout_large_min_divider` 只在样式显式设置时钉住 20dp / 16dp；base style 不设 `couiTabMinDivider`，此时 `parseMinDivider()` 用上面的 20dp / 24dp 响应式值。

这个变体不需要新参数就能表达：

```kotlin
TabRow(
    tabs = listOf("One", "Two", "Three"),
    selectedTabIndex = selected,
    onTabSelected = { selected = it },
    itemSpacing = 20.dp,
    colors = TabRowDefaults.tabRowColors(indicatorTrackColor = Color.Transparent),
)
```

**标签页搜索栏：** 早先记为「`COUITabLayout` 可承载搜索视图，未移植」。2026-11-14 核对：`COUITabLayout.java` 里**没有任何搜索相关代码**，本 APK 也没有任何布局把 `TabLayout` 与搜索框放在一起——没有可移植的对象，已从复核清单移除。
## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.TabRow // 标准样式
```

## 基本用法

### 标准样式

```kotlin
val tabs = listOf("推荐", "关注", "热门", "精选")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRow(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it }
)
```


### 记住滚动位置

```kotlin
val tabs = listOf("标签1", "标签2", "标签3", "标签4", "标签5")
var selectedTabIndex by remember { mutableStateOf(3) }
val tabListState = rememberLazyListState()

TabRow(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    listState = tabListState,
)
```

## 属性

### TabRow 属性

| 属性名            | 类型                      | 说明                 | 默认值                            | 是否必须 |
| ----------------- | ------------------------- | -------------------- | --------------------------------- | -------- |
| tabs              | List\<String>             | 标签文本列表         | -                                 | 是       |
| selectedTabIndex  | Int                       | 当前选中的标签索引   | -                                 | 是       |
| onTabSelected     | (Int) -> Unit             | 标签选中时的回调函数 | -                                 | 是       |
| modifier          | Modifier                  | 应用于标签行的修饰符 | Modifier                          | 否       |
| colors            | TabRowColors              | 标签行的颜色配置     | TabRowDefaults.tabRowColors()     | 否       |
| minWidth          | Dp                        | 每个标签的最小宽度   | TabRowDefaults.TabRowMinWidth     | 否       |
| maxWidth          | Dp                        | 每个标签的最大宽度   | TabRowDefaults.TabRowMaxWidth     | 否       |
| height            | Dp                        | 标签行的高度         | TabRowDefaults.TabRowHeight       | 否       |
| cornerRadius      | Dp                        | 标签的圆角半径       | TabRowDefaults.TabRowCornerRadius | 否       |
| itemSpacing       | Dp                        | 标签之间的间距       | TabRowDefaults.ItemSpacing        | 否       |
| contentAlignment  | Alignment                 | 标签内容的对齐方式   | Alignment.Center                  | 否       |
| listState         | LazyListState?            | 标签列表的外部滚动状态 | null                            | 否       |
| interactionSource | MutableInteractionSource? | 标签项的交互源       | null                              | 否       |
| indication        | Indication?               | 标签项的点击反馈效果 | null                              | 否       |
| enabled           | Boolean                   | 整行禁用（`COUITabLayout.setEnabled(false)`：指示条换 `#FFD9D9D9`、文字降为 `couiColorDisabledNeutral` `#42000000`/`#4DFFFFFF`、所有 tab 点击失效） | true                              | No       |


### TabRowDefaults 对象

TabRowDefaults 对象提供了 TabRow 组件的默认配置。

#### 常量

| 常量名                        | 类型 | 值    | 说明                         |
| ----------------------------- | ---- | ----- | ---------------------------- |
| TabRowHeight                  | Dp   | 40.dp | 标准样式的默认高度           |
| TabRowCornerRadius            | Dp   | 0.dp | 标准样式的默认圆角半径       |
| TabRowMinWidth                | Dp   | 72.dp | 标准样式的每个标签最小宽度   |
| TabRowMaxWidth                | Dp   | 128.dp | 标准样式的每个标签最大宽度   |
| IndicatorHeight               | Dp   | 2.dp   | 下划线指示器厚度                             |
| IndicatorInset                | Dp   | 24.dp  | 静态指示轨道距行两端的间距 |
| MinIndicatorWidth             | Dp   | 32.dp  | 移动下划线的最小宽度（`DEFAULT_MIN_INDICATOR`） |
| ItemSpacing                   | Dp   | 20.dp  | medium window class 以下的标签间距 |
| MediumItemSpacing             | Dp   | 24.dp  | 宽度 ≥600dp 时的标签间距（`coui_tab_layout_medium_tab_spacing`） |
| FadeEdgeLength                | Dp   | 58.dp  | 横向淡出边缘宽度                              |

#### 方法

| 方法名         | 类型         | 说明                     |
| -------------- | ------------ | ------------------------ |
| tabRowColors() | TabRowColors | 创建默认颜色配置（常态/选中文字、指示条、指示轨道） |

### TabRowColors 类

| 属性名                  | 类型  | 说明                   |
| ----------------------- | ----- | ---------------------- |
| backgroundColor         | Color | 标签的默认背景色       |
| contentColor            | Color | 标签的默认内容色       |
| selectedBackgroundColor | Color | 选中状态时标签的背景色 |
| selectedContentColor    | Color | 选中状态时标签的内容色 |
| indicatorColor          | Color | 选中指示条颜色 |
| indicatorTrackColor     | Color | 整行下方的静态 2dp 轨道（`Color.Unspecified` 时回退官方细线） |

## 进阶用法

### 自定义颜色

```kotlin
val tabs = listOf("最新", "热门", "关注")
var selectedTabIndex by remember { mutableStateOf(0) }

TabRow(
    tabs = tabs,
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    colors = TabRowDefaults.tabRowColors(
        backgroundColor = Color.LightGray.copy(alpha = 0.5f),
        contentColor = Color.Gray,
        selectedBackgroundColor = RmuixTheme.colorScheme.primary,
        selectedContentColor = Color.White
    )
)
```


### 与 Pager 结合使用

```kotlin
val tabs = listOf("页面1", "页面2", "页面3")
val pagerState = rememberPagerState { tabs.size }
val scope = rememberCoroutineScope()

Surface {
    Column {
        TabRow(
            tabs = tabs,
            selectedTabIndex = pagerState.currentPage,
            onTabSelected = { index ->
                scope.launch { pagerState.animateScrollToPage(index) }
            }
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("页面内容 ${page + 1}")
            }
        }
    }
}
```
