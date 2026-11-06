# ColorOS 17 Spinner & Dropdown 视觉度量与设计语言

本文档记录 `Dropdown`、`OverlaySpinnerPreference` / `WindowSpinnerPreference` 以及 `OverlayDropdownPreference` / `WindowDropdownPreference` 在迁移至 ColorOS 17 视觉风格时的几何度量、弹窗规格、配色方案及 API 兼容性评估。

---

## 1. 概述与设计定位

`rmuix` 中的下拉选择/菜单体系由两层架构组成：
1. **基础组件层 (`rmuix-ui`)**：`DropdownImpl` 与 `DropdownDefaults`，提供弹窗/对话框模式下的选项行绘制、勾选与子菜单指示器。
2. **偏好设置层 (`rmuix-preference`)**：`OverlaySpinnerPreference` / `WindowSpinnerPreference` 及其 Dropdown 变体，将 `BasicComponent` 与浮动 Popup / Dialog 菜单绑定。

对比 COUI 反编译源码（`com.coui.appcompat.poplist.*`、`COUIListPreference`）及资源表 `dimens.xml` (`coui_popup_list_*`)，ColorOS 17 对浮动选择菜单与下拉偏好设置项有着统一的几何物理规范与色彩准则。

---

## 2. 几何度量 (Geometry Metrics)

| 度量属性 | rmuix 现有值 | ColorOS 17 COUI 源码规范 | 说明 |
| :--- | :--- | :--- | :--- |
| **弹窗圆角 (`ContentRadius`)** | 12.dp | **16.dp** (`coui_popup_list_window_content_radius` / `@dimen/coui_round_corner_xl`) | 浮动 Popup 的边缘圆角 |
| **弹窗最小宽度 (`MinWidth`)** | 150.dp | **168.dp** (`coui_popup_list_window_min_width`) | 下拉菜单最小宽度 |
| **弹窗最大宽度 (`MaxWidth`)** | 216.dp | **256.dp** (`coui_popup_list_window_max_width`) | 下拉菜单最大宽度 |
| **项最小高度 (`MinHeight`)** | 40.dp | **40.dp** (`coui_popup_list_window_item_min_height`) | 选项行最小高度 |
| **无图标标题边距 (`TitleMarginNoIcon`)**| 16.dp | **16.dp** (`coui_popup_list_window_item_title_margin_with_no_icon`) | 无 Leading Icon 时的内边距 |
| **图标间距 (`IconEndPadding`)** | 12.dp | **12.dp** (`coui_popup_list_window_item_icon_margin_right`) | Leading Icon 与标题的间距 |
| **勾选图标右边距 (`CheckIconPadding`)**| 12.dp | **24.dp** (`coui_popup_list_window_item_title_margin_right`) | 选项标题与 Trailing Checkmark 的间距 |
| **选项字号** | 16sp | **16sp** (`coui_popup_list_window_item_title_text_size`) | 选项标题字号 |

---

## 3. 颜色体系与选中态 (Colors & Selection Tokens)

根据 `STAGING.md` 最高准则（一比一复刻 ColorOS 17 视觉与行为），`DropdownDefaults` 默认配色方案升级如下：

| 色彩属性 | 原 rmuix 映射 | ColorOS 17 规范映射 | 亮色值 | 暗色值 |
| :--- | :--- | :--- | :--- | :--- |
| **`contentColor`** (普通文字) | `onSurfaceContainer` | **`onSurfaceContainer`** | `#FF000000` | `#FFFFFFFF` |
| **`summaryColor`** (普通副标题) | `onSurfaceVariantSummary` | **`onSurfaceVariantSummary`** | `#8A000000` (54%) | `#8AFFFFFF` (54%) |
| **`containerColor`** (普通容器背景) | `surfaceContainer` / `Transparent` | **`surfaceContainer`** / **`Transparent`** | 背景容器色 | 背景容器色 |
| **`selectedContentColor`** (选中文字) | `primary` / `onTertiaryContainer` | **`primary`** | 主题主色 | 主题主色 |
| **`selectedSummaryColor`** (选中副标题) | `primary` / `onTertiaryContainer` | **`primary`** | 主题主色 | 主题主色 |
| **`selectedContainerColor`** (选中容器背景)| `surfaceContainer` / `tertiaryContainer` | **`primary.copy(alpha = 0.12f)`** | 主色 12% 半透明 | 主色 12% 半透明 |
| **`selectedIndicatorColor`** (勾选图标) | `primary` / `onTertiaryContainer` | **`primary`** | 主题主色 | 主题主色 |

### 设计洞察
1. **选中态主色亮起**：原 `rmuix` 在 Dialog 模式下使用了 M3 风格的 `tertiaryContainer` / `onTertiaryContainer`。ColorOS 17 统一使用**主题主色 (`primary`)** 点亮选中的文字与勾选 Icon，并配以 12% 半透明主色容器背景，保持全组件主色调视觉一致。
2. **勾选指示器间距**：右侧 Checkmark 图标在 ColorOS 中拥有 24dp 的右侧留白，确保与偏好设置行右侧边距完美对齐。

---

## 4. API 兼容性评估 (`STAGING.md`)

按照 `STAGING.md` 规范，本修改为**完全向下兼容的视觉重构**：

1. **`OverlaySpinnerPreference` 与 `WindowSpinnerPreference` 签名**：
   函数重载、参数列表、名称及默认值语义 100% 保持一致。
2. **`DropdownImpl` 与 `DropdownDefaults`**：
   - `dropdownColors()` 与 `dialogDropdownColors()` 默认参数全面更新为 ColorOS 17 的主色选中方案。
   - 保留全量 `@Deprecated` 的 `SpinnerDefaults`、`SpinnerColors` 与 `SpinnerEntry` 别名。

---

## 5. 后续行动计划

1. 提交本调研文档：
   - Commit Message: `docs: research ColorOS Spinner and Dropdown poplist metrics`
2. 交付用户（你）进行阶段验收。
3. 确认后，执行代码改动与组件文档同步，交付最终验收。
