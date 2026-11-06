# ColorOS 17 BreadcrumbBar 视觉度量与设计语言

本文档记录 `BreadcrumbBar`（面包屑路径导航栏）在迁移至 ColorOS 17 视觉风格时的几何度量、颜色体系、中性色阶（Neutral Ladder）映射及 API 兼容性评估。

---

## 1. 概述与设计定位

`BreadcrumbBar` 在 `rmuix` 中用于展示多级路径导航（例如文件管理器目录、多级分类选择）。

ColorOS 17 中虽然没有单独命名为 `COUIBreadcrumbBar` 的独立 View，但其路径导航与标签/胶囊控件遵循统一的 **ColorOS 17 Tag/Chip 几何规范** 及 **中性色阶（Neutral Translucent Ladder）**：
- 未选中/次要元素统一采用**半透明中性色**（如 `#14000000` / `#1AFFFFFF` 容器，`#8A000000` / `#8AFFFFFF` 次要文字），不使用纯实心背景。
- 分隔图标采用**三级中性色**（`#42000000` / `#4DFFFFFF`），视觉轻盈不抢眼。
- 高亮/当前路径项采用主题主色或高亮容器。

---

## 2. 几何度量 (Geometry Metrics)

| 度量属性 | rmuix 现有值 | ColorOS 17 规范值 | 说明 |
| :--- | :--- | :--- | :--- |
| **胶囊高度 (`ItemHeight`)** | 32.dp | **32.dp** | 符合 ColorOS 胶囊/标签 Small 档 32dp 标准高度 |
| **胶囊圆角 (`CircleShape`)** | 16.dp (半高) | **CircleShape** | 全胶囊形状（高度的一半） |
| **胶囊内部水平边距 (`ItemHorizontalPadding`)** | 10.dp | **10.dp** | 文字与胶囊左右端弧线的内边距 |
| **单项最大宽度 (`ItemMaxWidth`)** | 160.dp | **160.dp** | 限制长路径段宽度，超出使用 Ellipsis 截断 |
| **外层容器边距 (`InsideMargin`)** | `12.dp` 水平 / `8.dp` 垂直 | `12.dp` 水平 / `8.dp` 垂直 | `BreadcrumbBar` 整体 Padding |
| **分隔符图标尺寸** | 10.dp × 16.dp | 10.dp × 16.dp | `RmuixIcons.Basic.ArrowRight` |
| **分隔符水平边距** | 4.dp | 4.dp | 位于图标左右，即胶囊间净间距 18dp (10 + 4 + 4) |
| **字体字号** | 14sp (`body2`) | 14sp (`body2`) | 高亮项使用 `FontWeight.Medium`，未高亮使用 `FontWeight.Normal` |

---

## 3. 颜色体系与中性色阶映射 (Color OS Neutral Ladder)

ColorOS 17 强调系统的中性色阶一致性，`BreadcrumbBarColors` 的默认值调整如下：

| 色彩属性 | 原 rmuix 映射 | ColorOS 17 规范映射 | 亮色值 | 暗色值 |
| :--- | :--- | :--- | :--- | :--- |
| **`color`** (未高亮文字) | `onBackground.copy(0.55f)` | `secondaryText` (`couiColorSecondNeutral`) | `#8A000000` (54%) | `#8AFFFFFF` (54%) |
| **`highlightColor`** (高亮文字) | `primary` | `primary` | 主题主色 | 主题主色 |
| **`disabledColor`** (禁用文字) | `disabledOnSecondaryVariant` | `disabledOnSecondaryVariant` | `#33000000` (20%) | `#3DFFFFFF` (24%) |
| **`separatorColor`** (分隔图标) | `onSurfaceVariantActions` | `tertiaryText` (`couiColorThirdNeutral`) | `#42000000` (26%) | `#4DFFFFFF` (30%) |
| **`backgroundColor`** (未高亮胶囊背景) | `onBackground.copy(0.1f)` | `coui_color_container8` (8%/10% 半透明) | `#14000000` (8%) | `#1AFFFFFF` (10%) |
| **`highlightBackgroundColor`** (高亮胶囊背景)| `primary.copy(0.2f)` | `primary.copy(0.15f)` (主色半透明) | 主色 15% | 主色 15% |
| **`disabledBackgroundColor`** | `disabledSecondaryVariant` | `disabledSecondaryVariant` | `#14000000` (8%) | `#26FFFFFF` (15%) |

### 设计洞察
1. **未高亮胶囊容器**：改用 ColorOS 通用的 8%/10% 半透明容器色（即 Button SECONDARY 与 Switch 轨道禁用态所用的 `coui_color_container8`），取代固定的 `0.1f` 硬编码 alpha，在深浅色模式下更加贴合 ColorOS 沉浸感。
2. **分隔图标**：使用 `tertiaryText`（第三级中性色 `#42000000` / `#4DFFFFFF`），保持分隔箭头视觉退后，突出路径胶囊文本。

---

## 4. API 兼容性评估 (`STAGING.md`)

按照 `STAGING.md` 规范，本修改为**完全向下兼容的视觉重构**：

1. **`BreadcrumbBar` 签名**：
   ```kotlin
   @Composable
   fun BreadcrumbBar(
       items: List<BreadcrumbItem>,
       onItemClick: (Int) -> Unit,
       modifier: Modifier = Modifier,
       highlightIndex: Int = items.lastIndex,
       enabled: Boolean = true,
       colors: BreadcrumbBarColors = BreadcrumbBarDefaults.breadcrumbBarColors(),
       insideMargin: PaddingValues = BreadcrumbBarDefaults.InsideMargin,
       itemMaxWidth: Dp = BreadcrumbBarDefaults.ItemMaxWidth,
       listState: LazyListState? = null,
       interactionSource: MutableInteractionSource? = null,
       indication: Indication? = LocalIndication.current,
   )
   ```
   所有参数的顺序、名称、类型及默认值语义保持 100% 一致。

2. **数据类型**：
   - `BreadcrumbItem` 保持不变。
   - `BreadcrumbBarColors` 数据类保持不变。
   - `List<BreadcrumbItem>.joinToPath()` 扩展函数保持不变。

3. **默认值声明**：
   `breadcrumbBarColors()` 默认颜色梯阶升级为 ColorOS 17 半透明中性色阶，属于符合迁移目的的纯视觉调整，无需声明破坏性变更。

---

## 5. 后续行动计划

1. 提交本调研文档：
   - Commit Message: `docs: research ColorOS BreadcrumbBar metrics and neutral ladder`
2. 交付用户（你）进行阶段验收。
3. 确认后，执行代码改动与组件文档同步，交付最终验收。
