#! /usr/bin/env markdown

# ChipGroup

`ChipGroup` 对应 ColorOS 的芯片组（`COUIChipGroup`）：芯片按行折行、间距 8dp；内容超过若干行时折叠到固定行数，
并在末尾追加一个小的箭头芯片用于展开。

<div style="position: relative; height: 280px; border-radius: 10px; overflow: hidden; border: 1px solid #777;">
    <iframe id="demoIframe" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: none;" src="../../compose/index.html?id=chipGroup" title="Demo" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin"></iframe>
</div>

## 原厂 ColorOS 静态复核（2026-11-14）

`COUIChipGroup` = Material 的 `ChipGroup` + 折叠行为。已核实（见 `docs/dev/coloros-chip-group.md`）：

- `coui_chip_group_default_spacing_horizontal` = 8dp
- `coui_chip_group_default_spacing_vertical` = 8dp
- `collapsedMaxRows` 属性默认值 = 3（`DEFAULT_MINIMUM_COLLAPSED_MAX_ROWS`）
- `coui_chip_group_collapsable_button_size` = 28dp，且 `.Collapsable` 样式把 16dp 的 chevron 放在
`chipIconStartPadding` 6dp + `chipIconEndPadding` 6dp 之间（6 + 16 + 6 = 28dp，即居中）——
与居中 `Canvas` 的几何一致，`ChipGroupRenderingTest` 已钉住
- 折叠/展开芯片用 `Widget.COUI.Chip.Suggestion.Collapsable`（28dp、`chipIconSize` = 16dp、`closeIconSize` = 0dp），
  图形是 `ic_chip_collapse`（上箭头）/ `ic_chip_expand`（下箭头），16 单位 viewport、0.9333 描边、
  颜色 `?attr/couiColorLabelPrimary`

放在横向滚动容器里时宽度是无界的，此时组会保持在**单行**（符合滚动容器的预期），既不会换行也不会算出无限尺寸。

行方向跟随布局方向：RTL 下第一个芯片贴在右边、每行向左排（`COUIChipGroup` 也是通过 `isRTL()` 这么做的）。

**本版保留的差异：** 折叠/展开时**组尺寸**已按官方 `mGroupHeightChangeAnimation` 弹簧动画（response 0.35s、bounce 0，由 `ChipGroupStockSizeChangeSpring` 承载）；逐颗芯片仍是直接重排，没有官方的 view transition。
`singleSelection` / `selectionRequired` / `checkedChip` 未建模（给每个 [Chip] 传 `selected` 即可）。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.Chip
import io.github.ra1nify.rmuix.kmp.basic.ChipDefaults
import io.github.ra1nify.rmuix.kmp.basic.ChipGroup
```

## 基本用法

```kotlin
ChipGroup {
    repeat(20) { index ->
        Chip(onClick = {}, label = "Chip $index")
    }
}
```

超过 [ChipDefaults.CollapsedMaxRows] 行时只显示该行数 + 箭头芯片；点下箭头展开，点出现后的上箭头再折叠。

## ChipGroup

```kotlin
@Composable
fun ChipGroup(
    modifier: Modifier = Modifier,
    collapsedMaxRows: Int = ChipDefaults.CollapsedMaxRows,
    horizontalSpacing: Dp = ChipDefaults.GroupSpacing,
    verticalSpacing: Dp = ChipDefaults.GroupSpacing,
    content: @Composable () -> Unit,
)
```

| 参数名            | 类型       | 说明                     | 默认值                        | 必填 |
| ----------------- | ---------- | ------------------------ | ----------------------------- | ---- |
| modifier          | Modifier   | 应用于芯片组的修饰符     | Modifier                      | 否   |
| collapsedMaxRows  | Int        | 折叠时保留的行数         | ChipDefaults.CollapsedMaxRows | 否   |
| horizontalSpacing | Dp         | 芯片之间的间距           | ChipDefaults.GroupSpacing     | 否   |
| verticalSpacing   | Dp         | 行间距                   | ChipDefaults.GroupSpacing     | 否   |
| content           | @Composable () -> Unit | 芯片内容     | -                             | 是   |

## ChipDefaults 新增项

| 属性名                 | 类型 | 说明                | 默认值 |
| ---------------------- | ---- | ------------------- | ------ |
| CollapsedMaxRows       | Int  | 折叠时保留的行数    | 3      |
| GroupCollapseButtonSize | Dp  | 箭头芯片尺寸        | 28.dp  |
| GroupCollapseGlyphSize | Dp   | 箭头图形尺寸        | 16.dp  |

## 列表式重载（选择语义 + 逐芯片动画）

`ChipGroup(items = listOf(ChipGroupItem(...)))` 对应 `COUIChipGroup` 里那些内容 lambda 无法表达的属性，因为只有列表形式才能索引到每一颗芯片：

| 参数 | `COUIChipGroup` 属性 | 行为 |
| :--- | :--- | :--- |
| `singleSelection` | `singleSelection` | 同一时刻最多一颗芯片处于 `selected` |
| `selectionRequired` | `selectionRequired` | 已选中的芯片不能取消；`singleSelection` 为 false 时忽略 |
| `checkedChip` | `checkedChip` | 传 `null` 由组内部维护；传值则为受控组件 |
| `onCheckedChange` | `setOnCheckedStateChangeListener` | 回调新的索引（清空时为 `null`） |

因为组现在知道有几颗芯片可见，它也复刻了逐芯片的显隐动画：官方用 `mAlphaAnimation`（response 0.2s、bounce 0）与 `mScaleAnimation`（缩放 0.8 → 1.0、response 0.3s、bounce 0）。被折叠的芯片仍然按展开态位置参与布局，组会裁切掉溢出部分，因此它们是**淡出**而不是瞬间消失。官方的 `mHorizontalAnimation` / `mVerticalAnimation`（平移到新位置）尚未复刻。
