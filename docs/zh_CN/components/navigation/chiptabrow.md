# ChipTabRow

一排 ColorOS 芯片式标签页（`COUIChipTabLayout` / `Widget.COUI.Chip.ChipTab`）。

> 以下数值全部来自 Settings APK 的样式与 dimen。未构建、未运行、未上机核对。

## 几何

| Token | 值 | 说明 |
| :--- | ---: | :--- |
| `coui_chip_tab_style_height` | 32dp | 芯片的 `android:minHeight` |
| `coui_chip_default_min_width` | 52dp | |
| `coui_chip_default_max_width` | 300dp | |
| `coui_chip_suggest_style_chip_horizontal_padding` | 12dp | 左右各一 |
| `coui_chip_tab_style_chip_icon_size` | 16dp | |
| `coui_chip_suggest_style_chip_icon_end_padding` | 4dp | 图标与文字之间 |
| `coui_chip_tab_count_start_margin` | 2dp | 计数徽标之前 |
| `chipCornerRadius` | -1dp | 哨兵值：`COUIChipTabDrawable` 回退到 `min(width, height) / 2`，即胶囊 |
| `?attr/couiTextButtonM` | `couiTextButtonM` 样式 | **14sp**、`sans-serif-medium`（标签） |
| `?attr/couiTextBodyS` | `couiTextBodyS` 样式 | **13sp**（计数徽标） |

## 颜色

| 状态 | 底色 | 文字 |
| :--- | :--- | :--- |
| 选中 | `?attr/couiColorBackgroundElevated`（`#FFFFFF` / `#1E1E1E`）+ `#40FFFFFF` 叠加 → `#FFFFFF` / 约 `#575757` | `?attr/couiColorLabelPrimary` = `#E6000000` / `#E6FFFFFF` |
| 未选中 | `coui_chip_tab_surface_unchecked_surface`（`#FFFFFF` / `#000000`）+ `?attr/couiColorContainer4`（`#0A000000` / `#1AFFFFFF`）→ `#F5F5F5` / `#1A1A1A` | `?attr/couiColorLabelPrimary` = `#E6000000` / `#E6FFFFFF` |

## 用法

```kotlin
ChipTabRow(
    items = listOf(ChipTabItem("全部"), ChipTabItem("最近", count = "3")),
    selectedIndex = selected,
    onSelect = { selected = it },
)
```

## 选中态的装饰层

| token | 值 | 说明 |
| :--- | ---: | :--- |
| `coui_color_checked_background_gradient_start` | `#40FFFFFF` | 名为 gradient，实际被当作**平涂**叠加；仅在模糊特效关闭时绘制 |
| `coui_color_checked_background_stroke` | `#0A000000` / `#0AFFFFFF` | 描边色；描边特效开启时不绘制 |
| 描边宽度 | `density * 0.6f` = 0.6dp | 内缩半个线宽，圆角同步减小 |

> 模糊分支由 `isBlurEffectEnabled()` 控制；描边在描边特效**未开启**时绘制，而该开关默认关闭，所以官方默认会画 0.6dp 描边。`showCheckedStroke` 因此默认改为 **true**，需要去掉时传 `false`。

## 状态之间的交叉淡入

`COUIChipTabDrawable.drawDualLayerBackground()` 不会在选中/未选中容器之间跳变。两个
`LayerAlphaAnimation` 驱动它 —— `CheckedLayerAlphaAnimation` 与 `UncheckedLayerAlphaAnimation`，
都用 `new COUISpringForce().setResponse(0.2f).setBounce(0f)`，即响应 0.2s 的**临界阻尼弹簧**，
不是固定时长的 tween。未选中层在 alpha 大于零时还会叠一层 `?attr/couiColorContainer4`。

`ChipTabRow` 用 `ChipTabDefaults.colorTransitionSpec()`（由
`ChipTabDefaults.ColorTransitionResponse = 0.2f` 经 `folmeSpring` 构造）传给 `animateColorAsState`
复刻这一效果。`COUIChipTabDrawable` 覆写了 `draw()` 且**不调用**父类画 `checkedBackgroundColor` 的
`drawChipBackground()`，所以样式里的 `checkedBackgroundColor = couiColorPrimary` 并不会被画出来：
选中底色是 `?attr/couiColorBackgroundElevated` 再叠 `#40FFFFFF`，未选中底色是
`coui_chip_tab_surface_unchecked_surface` 再叠 `?attr/couiColorContainer4`。