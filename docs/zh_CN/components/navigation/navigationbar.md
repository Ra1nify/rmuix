# NavigationBar

## 共享原厂聚光内核

聚光入口现统一复用[共享 Spotlight](../media/spotlight)：使用官方光照 shader、预设和弹簧，移除旧幂次光斑/径向回退，并在内容之后混合。此项是公共引擎替换，不表示本控件的系统材质门控和预设选择已全部验收。Button/Checkbox 的可选聚光仍默认关闭。

BREAKING：聚光默认 tint 改为白色，移除组件内部随意指定的 40/140/380dp 半径；自定义颜色与半径仍可通过公共入口显式覆盖。


`NavigationBar` 是 Rmuix 中的底部导航栏组件，完全遵循 ColorOS 17 度量与视觉规范，用于在应用底部创建导航菜单。支持 2 到 5 个导航项，提供不同的显示模式（仅图标、图标和文本、仅选中项显示文本）。

`FloatingNavigationBar` 是一个悬浮样式的底部导航栏组件，同时支持 ColorOS 17 悬浮单胶囊样式 (`FloatingNavigationBarStyle.ColorOS`) 与旧版默认样式 (`FloatingNavigationBarStyle.Default`)。

这些组件通常与 `Scaffold` 组件结合使用，以便在应用程序的不同页面中保持一致的布局和行为。

<div style="position: relative; height: 300px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../../compose/index.html?id=navigationBar" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `COUINavigationMenuView` / `COUINavigationItemView`、`COUIMaskEffectDrawable` 与 `coui_tool_navigation_*` / `coui_navigation_*` 资源，只覆盖标准底栏；悬浮胶囊另有复核。

- 底栏底色取 `?attr/couiColorBar`（`coui_color_bar` = `#FAFAFA` / `#1E1E1E`）。上一轮误用了 `couiColorSurface`，本轮更正。
- 项目两侧内缩 `coui_tool_navigation_edge_item_padding` = **12dp**；`onMeasure` 把 `宽度 - 2×padding` 平分给可见项。
- 顶部分隔线是 `coui_navigation_shadow_height` = **0.33dp**。
- 每个项目绘制整项状态遮罩（`COUIMaskEffectDrawable`），圆角 `coui_navigation_item_background_radius` = **8dp**，分悬停、焦点、按压三层：悬停 `couiColorHover`（`#14000000` / `#26FFFFFF`）、按压 `couiColorPress`（`#1F000000` / `#33FFFFFF`）、焦点为主色 31/51（按 255 计）。库内用共享的 state-effect 弹簧与 Android `ArgbEvaluator` 线性混色按 hover → focus → press 顺序复现。
- 按压反馈既然由遮罩承担，标准项不再在按下时压暗内容；悬浮胶囊在它自己的复核前仍保留 press alpha 路径。
- 栏与项**同为 56dp**：`coui_navigation_normal_item_height`（被 `COUINavigationItemView` 读取）与 `coui_tool_navigation_item_height`（`coui_navigation_item_layout.xml` 的高度）都是 56dp。54dp 的 `coui_navigation_item_height` 虽存在但**无人引用**，不是项高 —— 之前的修订把它当依据，还凭空加了上下各 1dp 的留白。现在 `ItemHeight` 为 56dp，垂直留白为 0。
  - 未变化的项度量：24dp 图标、图标上边距 9dp、图文间距 2dp、标签 10sp。
- `coui_navigation_item_layout` 给每个项目左右各 2dp 外边距（`coui_navigation_item_half_gap`），相邻项目因此相隔 4dp（`coui_navigation_item_gap`）；行两侧的 12dp 内缩仍只作用于最外侧。
- 两个标签视图都声明 `sans-serif-medium`，选中不是字重变化；旧实现里“选中加粗”是库内约定。
- 反编译的 item 在 `navigationType="enlarge"` 之外**不会**为选中项改样式：`coui_bottom_tool_navigation_item_selector` 没有 selected 分支（常态与选中都解析为 `couiColorLabelPrimary`），布局里的 `navigation_bar_item_active_indicator_view` 在没有 Material 主题提供 indicator 样式时恒为 0×0。Settings 自己用的是 `navigationType="tool"`（底部动作栏），不是 tab 栏。

**BREAKING（已回退）：** `NavigationBarDefaults.ItemHeight` 保持 56dp —— 之前"降为 54dp"的依据是一个无人引用的 dimen，已撤销；默认 `color` 改为 `couiColorBar`，行两侧增加 12dp 内缩，分隔线改为 0.33dp，项目间距 4dp，标签两态都用 medium，标准项改为 8dp 状态遮罩、不再压暗内容；`NavigationBarDefaults` 新增 `EdgePadding`、`DividerHeight`、`ItemCornerRadius`、`ItemGap`。

**两套图标状态：** 官方 item view 用 `setIconTintList(null)` 清掉 tint、靠图标本身在状态间切换，所以 `NavigationBarItem` 新增可选参数 `selectedIcon`（默认 `null`，两种状态都画 `icon`）：

```kotlin
NavigationBarItem(
    selected = selected,
    onClick = { },
    icon = outlineHome,        // 常态图标
    selectedIcon = filledHome, // 选中图标
    label = "Home",
)
```

**图标盒与图形尺寸：** `COUINavigationItemView` 把图标**容器**设成 `coui_navigation_normal_icon_size`（**40dp**），里面的图形本身是 `coui_navigation_icon_size`（**24dp**）。本组件画的是 24dp 图形，看到 40dp 不要误当成图形尺寸。

**enlarge 变体（`navigationType=enlarge`）：** 已实现为 `NavigationBar(enlarged = true)`（2026-11-14）。项高 70dp（`coui_navigation_enlarge_item_height`），选中项画 64dp 图标、无顶部边距并隐藏标签；未选中项保持 24dp 图标、9dp 顶部边距（`coui_navigation_enlarge_icon_margin_top`）与标签。默认关闭，56dp 的常规栏行为不变。

**图标按原色绘制：** `COUINavigationItemView` 调用 `setIconTintList(null)`（第 285 行），floating item
用 `mIcon.setImageDrawable(drawable.mutate())` 画在 `COUIFadeMaskImageView` 上，accessory 直接绘制 drawable——
三者都不应用 `coui_navigation_item_icon_color.xml`（禁用 `#F5F6F7`、按下/选中
`?attr/couiColorPressBackground`、常态 `?attr/couiColorPrimary`）。因此本库不再用
`colors.contentColor(selected)` 给图标着色：状态切换请传 `selectedIcon`（或按主题着色的 artwork）；标签仍用
文本 selector 颜色。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.NavigationBar
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarDefaults
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarItem
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBar
import io.github.ra1nify.rmuix.kmp.basic.FloatingNavigationBarItem
import io.github.ra1nify.rmuix.kmp.basic.FloatingAccessoryButton
import io.github.ra1nify.rmuix.kmp.basic.NavigationBarDisplayMode
```

## 基本用法

### NavigationBar

NavigationBar 组件可用于创建固定在底部的导航菜单：

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("首页", "我的", "设置")
val icons = listOf(RmuixIcons.VerticalSplit, RmuixIcons.Contacts, RmuixIcons.Settings)

Scaffold(
    bottomBar = {
        NavigationBar {
            items.forEachIndexed { index, label ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    icon = icons[index],
                    label = label
                )
            }
        }
    }
)
```

### FloatingNavigationBar

FloatingNavigationBar 组件可用于创建悬浮在底部的胶囊导航栏：支持 2–5 个条目，并可通过 `startAccessory` / `endAccessory` 组合出 ColorOS 17 的四种三岛形态（单岛 / 左岛 / 右岛 / 双岛）。

```kotlin
var selectedIndex by remember { mutableStateOf(0) }
val items = listOf("首页", "我的", "设置")
val icons = listOf(RmuixIcons.VerticalSplit, RmuixIcons.Contacts, RmuixIcons.Settings)

Scaffold(
    bottomBar = {
        FloatingNavigationBar(
            mode = NavigationBarDisplayMode.IconAndText
        ) {
            items.forEachIndexed { index, label ->
                FloatingNavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    icon = icons[index],
                    label = label
                )
            }
        }
    }
)
```

### 自定义颜色

两种导航项都支持通过 `NavigationBarDefaults.navigationBarItemColors()` 创建的 `colors`。例如，使用主题主色显示选中项：

```kotlin
val itemColors = NavigationBarDefaults.navigationBarItemColors(
    unselectedContentColor = RmuixTheme.colorScheme.onSurfaceContainer,
    selectedContentColor = RmuixTheme.colorScheme.primary,
)

NavigationBar {
    items.forEachIndexed { index, label ->
        NavigationBarItem(
            selected = selectedIndex == index,
            onClick = { selectedIndex = index },
            icon = icons[index],
            label = label,
            colors = itemColors,
        )
    }
}
```

## 属性

### 拖拽形变

在 `FloatingAccessoryButton` 上按住并拖动时，按钮会沿手指方向拉伸、在交叉轴上变细（面积基本守恒），
与 ColorOS `COUIDeformAnimatorUnit` / `TouchMotionRubberBand` 的算法一致：

```text
橡皮筋:  sign(o) * (1 - 1 / (1 + |o| * curve / max)) * max       curve 0.55，max 150dp
主轴:    最大 1.2 倍，沿橡皮筋曲线增长
交叉轴:  取主轴的倒数（保持面积）
双轴:    以 |dx| / (|dx| + |dy|) 为权重做几何插值
抬手:    回弹弹簧，bounce 0.65 / response 0.42
```

默认值：`DeformMaxScale = 1.2f`、`DeformRubberCurveRatio = 0.55f`、`DeformMaxRubberDistance = 150.dp`、
`HandUpSpringDamping = 0.35f`、`HandUpSpringResponse = 0.42f`。
### NavigationBarDefaults 对象

| 常量 | 类型 | 说明 | 值 |
| --- | --- | --- | --- |
| BarHeight | Dp | 栏高度（`coui_navigation_normal_item_height`） | 56.dp |
| EnlargedItemHeight | Dp | 放大变体的项/栏高（`coui_navigation_enlarge_item_height`） | 70.dp |
| EnlargedIconSize | Dp | 放大变体选中项图标（`coui_navigation_enlarge_icon_size`） | 64.dp |
| EnlargedIconTopPadding | Dp | 未选中项顶部内距（`coui_navigation_enlarge_icon_margin_top`） | 9.dp |
| ItemHeight | Dp | 单项高度（与栏同高 56dp：`coui_navigation_normal_item_height` / `coui_tool_navigation_item_height`） | 56.dp |
| EdgePadding | Dp | 项目两侧内缩（`coui_tool_navigation_edge_item_padding`） | 12.dp |
| DividerHeight | Dp | 顶部分隔线（`coui_navigation_shadow_height`） | 0.33.dp |
| ItemCornerRadius | Dp | 项目状态遮罩圆角（`coui_navigation_item_background_radius`） | 8.dp |
| ItemGap | Dp | 每侧外边距（`coui_navigation_item_half_gap`，项目间 4dp） | 2.dp |
| IconSize | Dp | 图标尺寸（`coui_navigation_icon_size`） | 24.dp |
| LabelFontSize | TextUnit | 标签字号（`coui_navigation_item_text_size`） | 10.sp |
| IconTopPadding | Dp | 图标上边距（`coui_navigation_icon_margin_top`） | 9.dp |
| IconTextGap | Dp | 图文间距（`coui_navigation_text_margin_top`） | 2.dp |
| BadgeDotOffset | DpSize | 点徽标外扩偏移（`coui_navigation_red_dot_offset`） | 1×1dp |
| BadgeNumberOffset | DpSize | 数字徽标外扩偏移（`..._with_number_horizontal/vertical_offset`） | 8×5dp |
| TouchEffectRadius | Dp | 官方按压径向渐变半径（`..._touch_effect_radius`），本库已按此绘制指纹处光斑 | 40.dp |
| MeltBallExtension | Dp | 官方 meta-ball 扩展量（`..._melt_ball_param_extension`），本库尚未应用 | 4.dp |
| MeltBallFactorX / Y | Float | 官方 `MELT_BALL_EXTENSION_FACTOR_{X,Y}`，本库尚未应用 | 1.5 / 2.0 |
| AccessoryMinScale | Float | 卫星按钮收起时的最小缩放（`doAlphaScaleAnim` 到 5000/10000） | 0.5f |
| AccessoryAlphaResponse / Scale+Translation Damping,Response | Float | 展开收起弹簧：alpha response 0.35、scale/translation bounce 0.35→damping 0.65 / response 0.55 | 见默认值 |
| `showSpotLight` | Boolean | 附件按钮按下的 40dp 径向光斑（`touch_effect_radius` + `0x26` 中心 alpha，弹簧 response 0.2） | true | 可选 |
| BottomPadding | Dp | 标签下边距 | 4.dp |

> **指示条颜色：** 浅色主题下 `indicatorColor` 默认是白色，在默认的白色胶囊上几乎不可见，选中状态主要靠
> 文字字重与图标颜色体现。需要滑动指示块时请显式传色（官方浮动栏用的是半透明遮罩层而不是实色药丸，
> 因此没有可默认的 token）。

### FloatingNavigationBar 属性

| 属性名                     | 类型                      | 说明                               | 默认值                                  | 是否必须 |
| -------------------------- | ------------------------- | ---------------------------------- | --------------------------------------- | -------- |
| modifier                   | Modifier                  | 应用于导航栏的修饰符               | Modifier                                | 否       |
| mode                       | NavigationBarDisplayMode | 条目布局（`IconOnly` / `IconAndText` / `IconWithSelectedLabel`） | NavigationBarDisplayMode.IconAndText | 否 |
| enlarged                   | Boolean | `navigationType=enlarge`：项高 70dp、选中项图标 64dp、隐藏标签 | `false` | 否 |
| meltEffectMode             | Int                      | 为 `1` 时绘制胶囊与卫星按钮之间的 SkSL 融球桥接 | 0 | 否 |
| startAccessory             | (@Composable () -> Unit)? | 左侧卫星按钮（见 `FloatingAccessoryButton`） | null | 否 |
| endAccessory               | (@Composable () -> Unit)? | 右侧卫星按钮 | null | 否 |
| startAccessoryVisible      | Boolean                  | 展开 / 收起左侧卫星按钮 | true | 否 |
| endAccessoryVisible        | Boolean                  | 展开 / 收起右侧卫星按钮 | true | 否 |
| indicatorColor             | Color                    | 滑动选中指示块颜色 | 亮色白 / 暗色 `#2C2C2E` | 否 |
| color                      | Color                     | 导航栏背景颜色                     | RmuixTheme.colorScheme.surfaceContainer | 否       |
| cornerRadius               | Dp                        | 导航栏的圆角半径                   | FloatingNavigationBarDefaults.CornerRadius | 否   |
| horizontalAlignment        | Alignment.Horizontal      | 导航栏在其父容器中的水平对齐方式   | CenterHorizontally                      | 否       |
| horizontalOutSidePadding   | Dp                        | 导航栏外部的水平内边距             | FloatingNavigationBarDefaults.HorizontalOutSidePadding | 否       |
| shadowElevation            | Dp                        | 导航栏的阴影高度                   | FloatingNavigationBarDefaults.ShadowElevation          | 否       |
| showDivider                | Boolean                   | 是否显示导航栏周围的分割线         | false                                   | 否       |
| defaultWindowInsetsPadding | Boolean                   | 是否应用默认窗口嵌入边距           | true                                    | 否       |
| content                    | @Composable () -> Unit    | 导航栏的内容                       | -                                       | 是       |

### FloatingNavigationBarDefaults 对象

FloatingNavigationBarDefaults 对象提供了 ColorOS 17 悬浮单胶囊默认度量值。

#### 常量

| 常量名                   | 类型  | 说明                       | 默认值 (ColorOS 17) |
| ------------------------ | ----- | -------------------------- | ------------------- |
| BarHeight                | Dp    | 胶囊高度                   | 56.dp               |
| ItemHeight               | Dp    | 单项高度                   | 48.dp               |
| ItemMaxWidth             | Dp    | 单项最大宽度               | 70.dp               |
| ItemPadding              | Dp    | 纯文字项的左右内边距（`coui_bottom_floating_navigation_bar_item_padding`） | 12.dp |
| ItemIconTopMargin        | Dp    | 项内上下内边距（`coui_bottom_floating_navigation_bar_item_icon_top_margin`） | 4.dp |
| CornerRadius             | Dp    | 胶囊圆角半径               | 28.dp               |
| HorizontalOutSidePadding | Dp    | 外部水平内边距             | 36.dp               |
| HorizontalPadding        | Dp    | 内部水平内边距             | 16.dp               |
| ItemSpacing              | Dp    | 项目间距                   | 8.dp                |
| IconSize                 | Dp    | 图标尺寸                   | 24.dp               |
| IconTextGap              | Dp    | 图文间距                   | 2.dp                |
| NaviDistance             | Dp    | 距离底部导航条空隙         | 8.dp                |
| GestureNaviDistance | Dp | 无导航条 inset 时距底部的高度 | 36.dp |
| ItemHorizontalPadding | Dp | 横向（图标 + 文字）条目的内边距 | 4.dp |
| ItemBackgroundHorizontalPadding | Dp | 选中背景的水平外扩（`..._item_bg_horizontal_padding`） | 8.dp |
| SideSpacing | Dp | 胶囊与卫星按钮的间距（`..._Minimum_spacing_with_slide_view`） | 12.dp |
| MinHeightWithoutVerticalPadding | Dp | 不含垂直内边距的栏高（`..._min_height_without_padding_vertical`） | 88.dp |
| LargeIconSize / LargeIconOverflow | Dp | 选中大图标及其允许溢出量（`..._large_icon_size` / `_large_icon_overflow`） | 40.dp / 14.dp |
| IconTitleGapHorizontal | Dp | 横向条目的图文间距（`..._item_icon_title_gap_horizontal`） | 4.dp |
| IconPadding | Dp | 条目内图标的内边距 | 10.dp |
| IndicatorCornerRadius | Dp | 滑动选中指示块圆角 | 20.dp |
| ShadowRadius | Dp | 胶囊阴影柔化半径 | 10.dp |
| StretchMaxDistance / StretchCurveRatio | Dp / Float | 拖动橡皮筋（`maxStretchDistance` / `rubberCurveRatio`） | 28.dp / 0.05f |
| DeformMaxRubberDistance / DeformRubberCurveRatio / DeformMaxScale | Dp / Float | 形变（`maxDeformRubberDistance` / `deformRubberCurveRatio` / `maxScale`） | 150.dp / 0.55f / 1.2f |
| DragSpringDamping / DragSpringResponse | Float | 拖动弹簧（官方 `bounce 0.15`，damping = 1 - bounce） | 0.85f / 0.15f |
| HandUpSpringDamping / HandUpSpringResponse / HandUpSpringLargeAreaResponse | Float | 抬手弹簧（官方 `bounce 0.65`） | 0.35f / 0.42f / 0.45f |
| HandUpMaxInitialVelocity | Float | 抬手初速度上限 | 1000f |
| AccessoryMinScale / AccessoryAlphaResponse / AccessoryAlphaResponseWithoutMelt | Float | 卫星按钮收起缩放与 alpha 弹簧 | 0.5f / 0.35f / 0.2f |
| AccessoryScaleDamping / AccessoryScaleResponse | Float | 卫星按钮缩放弹簧（官方 `bounce 0.35`） | 0.65f / 0.55f |
| AccessoryTranslationDamping / AccessoryTranslationResponse / AccessoryTranslationMinVisibleChange | Float | 卫星按钮位移动弹簧 | 0.65f / 0.55f / 0.001f |
| MenuMaskCenterColor / MenuMaskEdgeColor / MenuShadowColor | Color | 官方 `mask_*` / `menu_shadow` 色值 | `#00FFFFFF` / `#33FFFFFF` / `#8A000000` |
| SelectedPressedAlpha / UnselectedPressedAlpha / UnselectedAlpha | Float | 内容 alpha 乘数（按压 / 常态） | 0.5f / 0.6f / 0.4f |
| IosBottomPadding | Dp | iOS 底部内边距 | 20.dp |
