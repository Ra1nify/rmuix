# ColorOS 17 Switch (开关) 1:1 深度逆向与动效公式

来源：`camera/decompiled/sources/com/coui/appcompat/couiswitch/COUISwitch.java`（反编译源码分析）。

---

## 1. 核心几南尺寸 (Geometry Metrics)

| 物理属性 | ColorOS 源码值 | 说明 |
| :--- | ---: | :--- |
| **`bar_width` (轨道宽度)** | **44.0dp** | `switchMinWidth` |
| **`bar_height` (轨道高度)** | **24.0dp** | 轨道高度 |
| **`outer_circle_width` (滑块直径)** | **18.0dp** | 静止状态下的滑块正圆直径 |
| **`circle_padding` (滑块与轨道边距)** | **3.0dp** | 滑块在轨道内的上下左右外留白 `(24 - 18) / 2 = 3dp` |
| **`Travel` (滑块水平行程)** | **20.0dp** | `44 - 3*2 - 18 = 20dp` |
| **未选中 Off 偏移** | **3.0dp** | `ThumbPadding` |
| **选中 On 偏移** | **23.0dp** | `ThumbPadding + Travel` (3 + 20) |

---

## 2. 核心 1:1 动效公式：滑块 1.3× 动态拉伸形变 (`setCircleScaleX`)

ColorOS Switch 的关键液态形变体验源于 `COUISwitch.java`（第 224 行）中的 **`ScaleX` 动态水平拉伸公式**：

当开关在 Toggle 或拖拽滑动过程中，滑动进度 $f \in [0.0, 1.0]$：

$$\text{ScaleX}(f) = \begin{cases} 1.0 + \frac{f}{0.34725848} \times 0.3, & \text{if } f < 0.34725848 \\[8pt] 1.3 - \frac{f - 0.34725848}{0.65274155} \times 0.3, & \text{if } f \ge 0.34725848 \end{cases}$$

### 动效阶段拆解：
1. **前半段（前 34.7% 行程）**：滑块水平宽度由 `1.0f`（18dp 正圆）平滑拉伸至 **`1.3f`（23.4dp 动态胶囊）**。
2. **后半段（后 65.3% 行程）**：随着接近目标终点，滑块宽度由 **`1.3f`（23.4dp）平滑收缩回 `1.0f`（18dp 正圆）**。

这种在滑行中途**暂态拉伸为 23.4dp 胶囊**的物理弹性形变，是 ColorOS 17 Switch 标志性的液态手感来源！

---

## 3. 颜色体系与暗色模式

| 元素 | 亮色 (Light Mode) | 暗色 (Dark Mode) |
| :--- | :--- | :--- |
| **`checkedTrackColor`** | `RmuixTheme.colorScheme.primary` (主题主色) | `RmuixTheme.colorScheme.primary` |
| **`uncheckedTrackColor`** | `#1F000000` (12% 半透明黑) | `#33FFFFFF` (20% 半透明白) |
| **`disabledCheckedTrackColor`**| `RmuixTheme.colorScheme.disabledPrimary` | `RmuixTheme.colorScheme.disabledPrimary` |
| **`disabledUncheckedTrackColor`**| `#14000000` (8% 半透明黑) | `#26FFFFFF` (15% 半透明白) |
| **`checkedThumbColor`** | `#FFFFFF` | `#FFFFFF` |
| **`uncheckedThumbColor`** | `#FFFFFF` | `#FFFFFF` |
| **`disabledThumbColor`** | `#8AFFFFFF` | `#29FFFFFF` |

---

## 4. 阴影与描边细节 (Shadow & Border)

1. **滑块阴影 (`setShadowLayer`)**：
   - 对应 `Shadow(radius = 2.5.dp, color = Color.Black, alpha = 0.098f, offset = DpOffset(0.dp, 1.3.dp))`。
   - 仅在 `enabled = true` 时绘制，禁用态移除阴影。
2. **未选中轨描边**：
   - 1.3dp 半透明中性色描边框 (`#1F000000` / `#33FFFFFF`)。

---

## 5. 与 rmuix 现有 Switch 的 1:1 差距与改动清单

| 项目 | rmuix 现实现 | ColorOS 1:1 目标 | 改动方案 |
| :--- | :--- | :--- | :--- |
| **滑块水平拉伸** | 无（固定 18dp 正圆平移） | **滑行中途拉伸至 1.3× (23.4dp)** | **实现 `ScaleX` 动态拉伸公式**，将 18dp 圆点在滑动中途拉伸为 23.4dp 胶囊 |
| **按下无谓放大** | 有 (1.127f 整体放大) | 无此特性 | **移除 1.127f 按压放大**（ColorOS 是沿滑动方向拉伸，非整体放大） |
| ** hover 悬浮** | 有 hover 变色 | 无 hover | **移除 hoverable** |
| **轨道与滑块尺寸**| 44dp × 24dp, Thumb 18dp | 44dp × 24dp, Thumb 18dp | 保持 44×24dp & 18dp |

---

## 6. 确认与后续步骤

* 提交本 1:1 深度调研文档：
  - Commit Message: `docs: research ColorOS Switch 1.3x thumb stretch and motion physics`
* 交付用户（你）进行 Step 1 验收。
