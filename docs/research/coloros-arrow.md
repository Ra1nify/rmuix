# ColorOS Arrow 逆向调研

对应 rmuix `rmuix-preference/.../ArrowPreference.kt` 里的箭头尾部图标。

ColorOS 的箭头由 `coui_btn_next`（选择器）提供，经 `COUIPreference` 的 `coui_jump_mark` 属性挂到
每个可跳转的 preference 行上。

## 1. 资源链

```
COUIPreference.coui_jump_mark
  └─ @drawable/coui_btn_next                 (selector, autoMirrored=true)
       ├─ state_pressed  → coui_btn_next_pressed
       ├─ state_enabled=false → coui_btn_next_disabled
       └─ default        → coui_btn_next_normal
```

弹窗里用变体 `coui_pop_up_next`，结构相同。

## 2. 几何（三态完全相同）

```xml
<vector android:width="12.0dp" android:height="24.0dp"
        android:viewportWidth="12.0" android:viewportHeight="24.0">
    <path android:pathData="M5,6 L10.646,11.646
                             C10.842,11.842 10.842,12.158 10.646,12.354
                             L5,18"
          android:strokeWidth="1.4"
          android:strokeLineCap="round" />
</vector>
```

| 元素 | 值 |
| :--- | ---: |
| 画布 | **12dp × 24dp** |
| viewport | 12 × 24（1:1，坐标即 dp） |
| 描边宽度 | **1.4dp** |
| 线帽 | **round** |
| 可镜像 | **是**（`autoMirrored="true"`，RTL 自动翻转） |

### 路径解析

```
起点  (5, 6)
直线  → (10.646, 11.646)     斜向右下
圆角  → (10.646, 12.354)     半径 ~0.354 的圆角拐点（C 曲线）
直线  → (5, 18)              斜向左下
```

即一个 **「>」形」**：从 (5,6) 到尖角 (10.646,11.9) 再到 (5,18)。
垂直中心 y=12，水平跨度 5 → 10.646。

**注意**：箭头高度占 12dp（6→18），但画布是 24dp 高——上下各留 6dp 空白。
这是为了与行高对齐而预留的透明区。

## 3. 颜色（三态）

| 状态 | 资源 | 亮色 | 暗色 |
| :--- | :--- | :--- | :--- |
| normal | `couiColorLabelTertiary` | **#42000000** | **#4DFFFFFF** |
| pressed | `couiColorLabelTertiary` | 同上（**与 normal 相同**） | 同上 |
| disabled | `couiColorLabelQuaternary` | **#14000000** | **#26FFFFFF** |

**pressed 与 normal 颜色完全一致**——按下态只可能靠别的机制（如整行按压反馈）区分，箭头本身不变色。

## 4. ColorOS 的四级中性色阶梯（重要发现）

这次调研把 coui 的完整 label 色阶凑齐了：

| 级别 | 资源 | 亮色 | 暗色 | 典型用途 |
| :--- | :--- | :--- | :--- | :--- |
| primary | `coui_color_label_primary` | `#E6000000` | `#E6FFFFFF` | 标题 |
| secondary | `coui_color_label_secondary` | `#8A000000` | `#8AFFFFFF` | 副标题 |
| **tertiary** | `coui_color_label_tertiary` | **`#42000000`** | **`#4DFFFFFF`** | **箭头 / 未选中描边** |
| quaternary | `coui_color_label_quaternary` | `#14000000` | `#26FFFFFF` | 禁用箭头 / 禁用描边 |

**这四级正好对应 rmuix 的 `RmuiColors` 三档 + disabled 档**，且与之前 Checkbox / RadioButton /
TabRow 用的中性色完全同源。四级是 90% / 54% / 26% / 8% 的透明度阶梯。

## 5. rmuix 现状对比

| 项 | rmuix | ColorOS | 差 |
| :--- | ---: | ---: | ---: |
| 图标尺寸 | **10 × 16 dp** | **12 × 24 dp** | +2 / +8 |
| 路径 | 自有 `ArrowRight` 图标 | `coui_btn_next` 的「>」路径 | 形状不同 |
| 描边宽度 | 图标内置（~1.65） | **1.4dp** | — |
| 颜色（normal） | `onSurfaceVariantActions` = **`#66000000` / `#66FFFFFF`** | `#42000000` / `#4DFFFFFF` | **rmuix 偏深（40% vs 26%）** |
| 颜色（disabled） | `disabledOnSecondaryVariant` | `#14000000` / `#26FFFFFF` | 需核对 |
| RTL | **有**（`graphicsLayer scaleX = -1`） | `autoMirrored=true` | 等价 |
| 右内边距 | **8dp**（`padding(end = 8.dp)`） | — | — |

### 关键差异

1. **尺寸**：rmuix 用 `size(width = 10.dp, height = 16.dp)` **强行缩放**矢量图标，
   比例 10:16 = 0.625；ColorOS 的 12:24 = 0.5。两者都窄高，但 rmuix 更宽。
2. **形状**：rmuix 的 `ArrowRight` 是折线 2.479→8.035 的「」，ColorOS 是 6→11.9→18 的「>」。
   视觉上相似，但 ColorOS 的尖角更靠右、开口更大。
3. **描边**：ColorOS 明确 1.4dp + round cap。rmuix 的图标是 fill 型（`lineTo` 闭合），不是 stroke。
   这是**渲染方式的根本差异**。

## 6. 实施建议

### 需要改的

```kotlin
// 尺寸：10x16 → 12x24
.size(width = 12.dp, height = 24.dp)      // 原 10 x 16

// 颜色：确认 rmuix token 对应关系
val color = RmuixTheme.colorScheme.???      // 需映射到 #42000000 / #4DFFFFFF
val disabledColor = RmuixTheme.colorScheme.???  // 映射到 #14000000 / #26FFFFFF
```

### 需要新建的

rmuix 的 `ArrowRight` 是 **fill 折线**，ColorOS 是 **1.4dp stroke + round cap**。
要精确还原需要新建一个 stroke 版图标，或在 `ArrowPreference` 里直接 `Canvas` 绘制：

```kotlin
Canvas(modifier = Modifier.size(12.dp, 24.dp)) {
    val s = size.width / 12f   // viewport 12
    drawPath(
        path = Path().apply {
            moveTo(5f * s, 6f * s)
            lineTo(10.646f * s, 11.646f * s)
            quadraticTo(10.842f * s, 12f * s, 10.646f * s, 12.354f * s)
            lineTo(5f * s, 18f * s)
        },
        color = color,
        style = Stroke(width = 1.4f * s, cap = StrokeCap.Round),
    )
}
```

### API 兼容

`ArrowPreference` 签名不变。`EndActionColors` 的默认值需核对是否等于 ColorOS 的
`#42000000`/`#4DFFFFFF`，不等则调整（属默认值变更，需声明）。

## 7. 已确认

| 项 | 结论 |
| :--- | :--- |
| rmuix `onSurfaceVariantActions` | **`#66000000` / `#66FFFFFF`**（40%），比 ColorOS 的 26% 深 |
| 12dp 宽度够不够 | 路径 x 最大 10.842，剩 1.16dp 余量，够 |
| ColorOS 箭头间距 | `support_preference_red_dot_jump_icon_margin_start = 16dp` |

## 8. 待确认

- [ ] **是否新建 stroke 版图标**：`RmuixIcons.Basic.ArrowRight` 是 fill 折线，ColorOS 是 stroke。
      新建会改动图标库；直接用 Canvas 画则不动图标库
- [ ] **颜色是否改**：`onSurfaceVariantActions` 从 40% 降到 26%，属默认值变更需声明
- [ ] **右内边距 8dp vs ColorOS 16dp**：是否调整

## 9. 影响面

```
ArrowPreference 调用点：example/demo 若干
ArrowUpDown 是另一个图标，本轮不动
```

arrow 只在 `ArrowPreference` 内部使用，影响面小。但要注意：如果改 `RmuixIcons.Basic.ArrowRight`
会影响其他用到该图标的地方。
