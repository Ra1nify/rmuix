# ColorOS Dropdown 逆向调研

对应 rmuix：
- `rmuix-ui/.../basic/Dropdown.kt`（536 行，数据模型 + Defaults）
- `rmuix-preference/.../popup/DropdownEntriesContent.kt`（条目渲染）
- `rmuix-preference/.../menu/*DropdownMenu.kt`（Overlay / Window 两套）
- `rmuix-preference/.../preference/*DropdownPreference.kt`

ColorOS 实现：`com.coui.appcompat.poplist.COUIPopupMenuRootView` +
`res/layout/coui_popup_list_window_*.xml` + `coui_popup_list_window_*` 尺寸。

## 1. 结构

`coui_popup_list_window_item.xml`（单个条目）：

```xml
<LinearLayout orientation="horizontal"
    minHeight="coui_popup_list_window_item_min_height"
    paddingStart/End="coui_popup_list_window_item_icon_margin_left"
    paddingTop/Bottom="coui_popup_list_window_item_padding_top_and_bottom">

    <ImageView id="popup_list_window_item_icon" 20x20dp
               marginEnd="coui_popup_list_window_item_icon_margin_right" />

    <LinearLayout orientation="vertical" weight="1">
        <TextView id="popup_list_window_item_title"      couiTextAppearanceBodyL />
        <TextView id="popup_list_window_item_description" couiTextAppearanceDescription />
    </LinearLayout>

    <Space id="title_end_gap" 8dp />
    <COUILinearLayout id="hint_layout" couiMaxWidth="40dp" />
    <Space id="hint_end_gap" 4dp />
    <CheckBox id="popup_list_window_item_state_icon" 24x24dp visibility="gone" />
</LinearLayout>
```

**关键**：ColorOS 用 **CheckBox** 表示选中，不是 rmuix 的对勾图标。

## 2. 窗口几何

| 资源 | 值 | 说明 |
| :--- | ---: | :--- |
| `coui_popup_list_window_content_radius` | 12.0dp | 声明值（**实际未用于背景**） |
| **`coui_round_corner_m_radius`** | **9.0dp** | **实际圆角**（背景 drawable 用的） |
| `coui_round_corner_m_weight` | 0.99 | squircle 曲率 |
| `coui_popup_list_window_min_width` | **168.0dp** | 最小宽 |
| `coui_popup_list_window_max_width` | **256.0dp** | 最大宽 |
| `coui_popup_list_window_width_with_icon` | **196.0dp** | 带图标时的宽 |
| `coui_popup_list_window_margin_horizontal` | 16.0dp | 水平边距 |
| `coui_popup_list_window_margin_top` | 19.0dp | 顶部边距 |
| `coui_popup_list_window_margin_bottom_new` | **16.0dp** | 底部边距 |
| `coui_popup_list_window_default_vertical_gap_to_anchor` | **8.0dp** | 与锚点间距 |
| `coui_popup_list_window_min_gap_to_top` | 12.0dp | 距顶最小间距 |
| `coui_popup_list_window_arrow_width/height` | 24.0dp | 箭头 |
| `coui_popup_list_window_vertical_overlap_between_main_and_sub_menu` | **14.0dp** | 主/子菜单重叠 |
| `coui_popup_list_window_vertical_mask_radius` | 20.0dp | 遮罩圆角 |
| `coui_popup_list_window_max_height` | — | 见下 |

## 3. 条目尺寸（核心）

| 资源 | 值 |
| :--- | ---: |
| **`coui_popup_list_window_item_min_height`** | **40.0dp** |
| `coui_popup_list_window_item_padding_top_and_bottom` | **8.0dp** |
| `coui_popup_list_window_item_icon_margin_left` | **20.0dp** |
| `coui_popup_list_window_item_icon_margin_right` | **12.0dp** |
| `coui_popup_list_window_item_icon_extra_width` | 8.0dp |
| `coui_popup_list_window_item_title_margin_left` | 12.0dp |
| `coui_popup_list_window_item_title_margin_right` | 24.0dp |
| `coui_popup_list_window_item_title_margin_with_no_icon` | **16.0dp** |
| `coui_popup_list_window_item_max_width` | 248.0dp |
| `coui_popup_list_window_item_checkbox_margin_end` | **18.0dp** |
| 图标尺寸 | **20 × 20 dp** |
| CheckBox 尺寸 | **24 × 24 dp** |

### 垂直节奏

```
minHeight 40dp
padding 8 + 8 = 16dp
→ 文字区可用高度 = 40 - 16 = 24dp（单行 16sp 刚好）
```

## 4. 列表容器

| 资源 | 值 |
| :--- | ---: |
| `coui_popup_list_padding_vertical` | **8.0dp** |
| `coui_popup_list_divider_height` | **0.5dp** |
| `coui_popup_list_default_divider_margin_horizontal` | 20.0dp |
| `coui_popup_list_default_divider_margin_start_with_icon` | **52.0dp** |
| `coui_popup_list_group_divider_height` | 4.0dp |
| `coui_popup_list_group_divider_total_height` | 12.0dp |
| `coui_popup_list_fading_edge_length` | 12.0dp |
| `coui_popup_list_window_fade_edge_length` | 60.0dp |

**分隔线左缩进 52dp**（有图标时）= 20（左内边距）+ 20（图标）+ 12（图标右间距）——正好对到文字起点。

## 4.5 背景与圆角（重要校正）

`coui_popup_window_background.xml`：

```xml
<shape android:shape="rectangle">
    <solid android:color="?couiColorBackgroundTop" />
    <corners android:radius="@dimen/coui_round_corner_m_radius" />
    <smooth-weight android:gradientRadius="@dimen/coui_round_corner_m_weight" />
</shape>
```

| 项 | 值 |
| :--- | ---: |
| 背景色 | `couiColorBackgroundTop` = **`#FFFFFFFF`** / **`#FF333333`** |
| 圆角 | `coui_round_corner_m_radius` = **9.0dp**（不是 12dp） |
| 曲率 | `coui_round_corner_m_weight` = **0.99** |
| 描边/阴影 | **无**（纯 shape） |

**注意**：`coui_popup_list_window_content_radius = 12dp` 在 public.xml 里声明了但**没有
被背景 drawable 引用**，属于未使用的资源。实际圆角是 `m` 档的 **9dp**。

这与 Dialog 的规律一致：**看 `_radius`，不是名义值也不是名字里带 radius 的其它资源**。

暗色背景 `#FF333333` 比 Dialog 的 `#FF1E1E1E` 更亮——是"抬升表面"而非纯黑。

## 5. 文字

| 元素 | 样式 | 字号 |
| :--- | :--- | ---: |
| 标题 | `couiTextAppearanceBodyL` | **16.0sp** |
| 描述 | `couiTextAppearanceDescription` | **12.0sp** |

字重：`sans-serif-regular` / `textFontWeight=400`（普通，不因选中加粗）。

## 6. rmuix 现状对比

| 项 | rmuix | ColorOS | 差 |
| :--- | ---: | ---: | ---: |
| 条目最小高 | **56dp** | **40dp** | **−16（显著）** |
| 上下内边距 | — | **8dp** | — |
| 左内边距 | `InsideHorizontalPadding = 20dp` | **20dp** | ✓ 一致 |
| 图标尺寸 | `IconMinSize = 26dp` | **20dp** | −6 |
| 图标右间距 | `IconEndPadding = 12dp` | **12dp** | ✓ 一致 |
| 选中图标 | **对勾**（`CheckIconSize = 20dp`） | **CheckBox 24dp** | 形态不同 |
| 选中图标左间距 | `CheckIconStartPadding = 12dp` | margin_end 18dp | — |
| 最小宽 | **200dp** | **168dp** | −32 |
| 最大宽 | — | **256dp** | — |
| 圆角 | — | **12dp** | — |
| 标题字号 | 由内容定 | **16sp** | — |
| 描述字号 | — | **12sp** | — |

### 最关键的差异

1. **条目高度 56 → 40dp**：rmuix 的条目明显更胖
2. **选中标记**：rmuix 用对勾，ColorOS 用 CheckBox（24dp 方块）
3. **最小宽度** 200 → 168dp

## 7. rmuix 的独有结构

- `DropdownEntry`（分组）+ `DropdownItem`（条目）两层模型
- `DropdownColors` 七字段（content/summary/container + 三个 selected + indicator）
- `dialogDropdownColors` 变体（选中态用 `tertiaryContainer`）
- Overlay / Window 两套实现

ColorOS 的分组用 `coui_popup_list_group_divider_height = 4dp` + `total_height = 12dp` 的间隔条实现。

## 8. 实施建议

### 数值对齐

```kotlin
object DropdownDefaults {
    val MinHeight = 40.dp              // 56 → 40
    val MinWidth = 168.dp              // 200 → 168
    val MaxWidth = 256.dp              // 新增
    val CheckIconSize = 20.dp          // 保持（图标尺寸）
    val IconMinSize = 20.dp            // 26 → 20
    val InsideHorizontalPadding = 20.dp  // 保持
    val IconEndPadding = 12.dp         // 保持
    val VerticalPadding = 8.dp         // 新增（条目上下）
    val ContentCornerRadius = 12.dp    // 新增（菜单圆角）
    val VerticalGapToAnchor = 8.dp     // 新增
    val DividerStartIndentWithIcon = 52.dp  // 新增
}
```

### 需要决策的点

1. **选中标记：对勾 → CheckBox？**
   rmuix 用对勾，ColorOS 用 CheckBox。这是形态变更，比数值改动大。
2. **描述文字 12sp**：rmuix 有 `summary` 概念但字号由内容定。
3. **圆角 12dp**：rmuix 目前无圆角参数（可能在 popup 层实现）。
4. **分组间隔 4dp/12dp**：rmuix 用 `DropdownEntry` 分组，视觉上是空白间隔。

## 9. 已确认

| 项 | 结论 |
| :--- | :--- |
| 背景色 | `#FFFFFFFF` / `#FF333333`（`couiColorBackgroundTop`） |
| 实际圆角 | **9dp**（`coui_round_corner_m_radius`），不是 12dp |
| 曲率 | 0.99 |
| 描边/阴影 | 无 |
| `content_radius = 12dp` | 声明但未使用 |

## 9.5 待确认

- [ ] **选中标记是否改成 CheckBox**（形态变更，需要决策）
- [ ] 条目高度 56 → 40dp（数值，可直接改）
- [ ] 分组间隔的视觉对应（rmuix 用 `DropdownEntry`，ColorOS 用 4dp/12dp 间隔条）
- [ ] `coui_popup_list_window_max_height` 未找到，可能用 `coui_dialog_max_height` 或按屏幕算

## 9.6 实施记录

### 已实现

| 改动 | 内容 |
| :--- | :--- |
| 条目高度 | `MinHeight` 56 → **40dp** |
| 垂直内边距 | `FirstLastVerticalPadding` 20 → **8dp**，`MiddleVerticalPadding` 12 → **8dp** |
| 最小宽度 | `MinWidth` 200 → **168dp** |
| 图标尺寸 | `IconMinSize` 26 → **20dp** |
| 选中标记 | 对勾 → **Checkbox**（24dp，`ToggleableState.On`） |
| 选中标记间距 | `CheckIconStartPadding` 12 → **18dp**（对应 `checkbox_margin_end`） |
| 选中图标尺寸 | `CheckIconSize` 20 → **24dp** |
| 分组分隔线 | 1.5dp 细线 → **4dp 实体带**，占 12dp 总高 |
| popup 圆角 | 16 → **9dp**（`ListPopup` + `CascadingListPopupLayout`） |

### 新增常量

```kotlin
val MaxWidth: Dp = 256.dp
val WidthWithIcon: Dp = 196.dp
val ContentCornerRadius: Dp = 9.dp
val VerticalGapToAnchor: Dp = 8.dp
val GroupDividerHeight: Dp = 4.dp
val GroupDividerTotalHeight: Dp = 12.dp
val DividerThickness: Dp = 0.5.dp
val DividerStartIndentWithIcon: Dp = 52.dp
val DividerStartIndent: Dp = 20.dp
```

### 实现细节

**Checkbox 替换**：选中时渲染 `Checkbox(state = ToggleableState.On, onClick = null)`，
未选中时渲染同尺寸 `Spacer` 占位，保证文字列宽度不跳动。

**分组分隔线**：`DropdownEntriesContent.kt` 两处（popup 与 dialog）都改为
`thickness = GroupDividerHeight (4dp)`，垂直 padding = `(12 - 4) / 2 = 4dp`。

## 10. 影响面

```
Dropdown 调用点：
  rmuix-preference/menu/    6 个文件
  rmuix-preference/popup/   2 个文件
  rmuix-preference/preference/ 2 个文件（Dropdown 变体）
  example/demo              8 个 demo
```

**影响面较大**——Dropdown 是 rmuix 里文件最多的组件族（20 个文件）。
