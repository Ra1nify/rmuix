# SegmentButton

ColorOS 的分段控件（`COUISegmentButtonLayout`）：胶囊形轨道 + 内容自适应分段，选中段下方有滑动的指示块。

圆角来自 `getDrawableRadius()`，它对轨道与指示块都返回 `(bottom - top) / 2`——所以两者都是胶囊：
轨道半径是轨道高度的一半，指示块半径是它自身高度（`高度 - 2 × 内距`）的一半。分段宽度按官方
`COUISegmentButtonLayout.onMeasure()` / `calculateChildrenWidths()`：文字自然宽 + 12dp 内距（最小
`coui_segment_min_width` 52dp），剩余空间由超过平均值的标签均分——只有所有标签都短于平均值时各段才等宽。
指示块使用官方弹簧
（response 0.4 / bounce 0.1）。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.SegmentButton
```

## 基本用法

```kotlin
var index by remember { mutableIntStateOf(0) }
SegmentButton(
    options = listOf("日", "周", "月"),
    selectedIndex = index,
    onSelect = { index = it },
)
```

## 参数

| 参数名        | 类型                | 说明                       | 默认值 |
| :------------ | :------------------ | :------------------------- | :----- |
| options       | List<String>        | 分段标签                   | - |
| selectedIndex | Int                 | 选中项（超出会被夹取）      | - |
| onSelect      | (Int) -> Unit       | 回调被点击的分段下标        | - |
| modifier      | Modifier            | 应用到控件                 | Modifier |
| enabled       | Boolean             | 是否可选中                 | true |
| tiny          | Boolean             | 使用 32dp 的 Tiny 尺寸      | false |
| height        | Dp                  | 轨道高度                   | 40dp / 32dp（tiny） |
| colors        | SegmentButtonColors | 轨道、指示块与文字颜色      | SegmentButtonDefaults.colors() |

## SegmentButtonDefaults

| 名称                    | 类型 | 说明                                        | 值 |
| :---------------------- | :--- | :------------------------------------------ | :---- |
| Height                  | Dp   | `SegmentButton` 样式 `layout_height`         | 40dp |
| TinyHeight              | Dp   | `SegmentButton.Tiny` 样式 `layout_height`    | 32dp |
| Padding                 | Dp   | `coui_segment_padding`                       | 4dp |
| TinyPadding             | Dp   | `coui_segment_padding_tiny`                  | 2dp |
| MinSegmentWidth         | Dp   | `coui_segment_min_width`                     | 52dp |
| SegmentHorizontalPadding| Dp   | `coui_segment_btn_padding_horizontal`        | 12dp |
| TextSize                | TextUnit | `couiTextButtonM` 字号                   | 14sp |
| WidthSpringResponse     | Float | `COUISegmentButtonLayout.WIDTH_SPRING_RESPONSE` | 0.6 |
| WidthSpringBounce       | Float | `COUISegmentButtonLayout.WIDTH_SPRING_BOUNCE`   | 0.5 |
| IndicatorShadowOffset   | Dp   | `coui_segment_shadow_offset`                   | 2dp |
| IndicatorShadowEndColor | Color | `coui_segment_button_shadow_end_color`        | `#03000000` |

### 颜色

| 用途 | 浅色 | 深色 | 来源 |
| :--- | :--- | :--- | :--- |
| 轨道 | `#0F000000` | `#1FFFFFFF` | `coui_color_segment_button_background` |
| 指示块 | `#B2FFFFFF` | `#33FFFFFF` | `coui_color_segment_button_indicator` |
| 指示块阴影 | `#0D000000` | `#14000000` | `coui_segment_button_shadow_color` |
| 指示块阴影终点 | `#03000000` | （深色使用纯色起点色） | `coui_segment_button_shadow_end_color` |
| 选中 / 未选文字 | `?attr/couiColorLabelPrimary`（两者相同） | 同 | `loadAttr()` |

浅色主题下指示块是近白实心块压在 6% 黑的轨道上，深色主题则相反。这是资源原值，不是取反错误——
区分选中态靠的是指示块，而不是文字颜色。

## 指示块阴影

`COUISegmentButtonLayout.drawIndicator(canvas, TRUE, ...)` 会在指示块之前**始终**画一层阴影：矩形从指示块顶部下方的
`coui_segment_shadow_offset` = **2dp** 开始，左右沿用指示块边界，一直延伸到容器底部，并从路径中挖掉指示块本身。
浅色用径向渐变 `#0D000000 -> #03000000`（中心在指示块中心，半径 = 指示块宽度 / 2）；深色直接画 `#14000000` 实色。
库内已按同样方式绘制（底边跟随 padding 后的内容区，距容器最底部 4dp）。

## 未复刻

- 焦散阴影边缘（`mDrawMaterialEffect` / `OplusMaterialEffect`）及其 1dp 的 `coui_segment_shadow_radius`；
- 短变长的额外宽度动画已建模（`animateIndicatorToPosition()`）：分段宽度按官方 children 分配规则实现；
  切换时指示块宽度走动态弹簧（`WIDTH_SPRING_VELOCITY` 1000px/s；短→长固定 0.5 bounce，长→短动态
  0.5..0.1，response 经 `calculateDynamicWidthParam()` 计算 0.6..0.4），位置弹簧 0.4/0.1（tiny 的短→长
  动态 bounce 0.1..0.03），高度弹簧 `HEIGHT_SPRING_*` 0.6/0.5、-500px/s，并按 `0.9 * 高度` 夹紧形成下沉。
  Compose 弹簧无法限制过冲，因此绘制宽度夹在 `目标宽度 + coui_segment_regular_extra_width` 7dp
  （tiny：`coui_segment_small_extra_width` 5dp）——对应官方 `getEffectiveMaxExtraWidthForShortToLong()`；
  `coui_segment_switch_distance` 5dp 也由该额外宽度预算覆盖。响应式宽度上限已实现：
  `getResponsiveWidth()` 按窗口 total size class 映射到居中的 4 / 6 / 8 列跨度
  （`LayoutGridSystem`：compact 4 列 / 16dp 边距 / 8dp gutter；medium 与 expanded-portrait
  8 列 / 24dp / 8dp；plain expanded 12 列 / 40dp / 12dp），控件宽度限制为 `min(可用宽度, 该跨度)`；
- 拖拽选择已建模：手指跨过分段时选中当前分段，并在分段变化时调用 `performHapticFeedback(302)`
  （映射为 Compose `HapticFeedbackType.SegmentFrequentTick`）；抬手选中落点分段但不振动，与
  `dispatchTouchEvent()` 一致。点击走同一手势检测器，分段仍保留 `Role.RadioButton` / 选中态 /
  `onClick` 语义以支持无障碍。
