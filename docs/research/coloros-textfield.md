# ColorOS 17 TextField 视觉度量与设计语言

本文档记录 `TextField` 在迁移至 ColorOS 17 视觉风格时的几何度量、动画边框规格、配色方案及 API 兼容性评估。

---

## 1. 概述与设计定位

`rmuix` 中的 `TextField` 提供了现代 Compose 的双重 API 重载（`TextFieldState` 1.7+ 新架构与传统 `TextFieldValue` 兼容模式），外观上由 Squircle 连续圆角背景、平滑焦线框 (Focus Border) 以及动态浮动标签 (Floating Label) 构成。

对比 COUI 反编译源码（`com.coui.appcompat.edittext.COUIEditText` 与 `COUIInputView`）及资源表 `dimens.xml`，ColorOS 17 的文本输入框采用了极简的半透明圆角容器与强对比的 Focus 主色高亮框。

---

## 2. 几何度量 (Geometry Metrics)

| 度量属性 | rmuix 现有值 | ColorOS 17 COUI 源码规范 | 说明 |
| :--- | :--- | :--- | :--- |
| **容器圆角 (`CornerRadius`)** | 16.dp | **16.dp** (`coui_round_corner_xl` = 16.0dp) | Squircle 连续圆角 |
| **内容内边距 (`InsideMargin`)** | `DpSize(16.dp, 16.dp)` | **`DpSize(16.dp, 16.dp)`** (`coui_input_lock_screen_pwd_card_padding`) | 文本输入框默认上下左右内边距 |
| **焦点边框宽度 (`BorderWidth`)**| 2.dp | **2.dp** (`couiFocusStrokeWidth`) | 输入框获得焦点时的平滑过度高亮框线宽 |
| **正文字号** | 16sp (`main`) | **16sp** (`coui_edittext_text_size`) | 输入框正文文本字号 |
| **浮动标签字号** | Normal 17dp / Floating 10dp | Normal 17dp / Floating 10dp | 浮动 Label 在无输入与有输入时的过渡字号 |

---

## 3. 颜色体系 (Color OS Tokens)

根据 `STAGING.md` 最高准则（一比一复刻 ColorOS 17 视觉与行为），`TextFieldDefaults.textFieldColors()` 默认配色方案更新如下：

| 色彩属性 | 原 rmuix 映射 | ColorOS 17 规范映射 | 亮色值 | 暗色值 |
| :--- | :--- | :--- | :--- | :--- |
| **`backgroundColor`** (背景容器) | `secondaryContainer` | **`secondaryContainer`** (`coui_color_container8`) | `#14000000` (8%) | `#1AFFFFFF` (10%) |
| **`labelColor`** (提示词/标签) | `onSecondaryContainer` | **`onSurfaceVariantSummary`** (`couiColorLabelSecondary`) | `#8A000000` (54%) | `#8AFFFFFF` (54%) |
| **`borderColor`** (焦点高亮框) | `primary` | **`primary`** (`couiColorFocus`) | 主题主色 | 主题主色 |

### 设计洞察
* **Label 文字映射**：`labelColor` 从 `onSecondaryContainer` 改为映射 ColorOS 二级中性色 `onSurfaceVariantSummary` (`couiColorLabelSecondary`)，在浅色与深色模式下获得符合 ColorOS 17 规范的 54% 适度文本透明度。
* **背景与边框**：`secondaryContainer` 提供 8%/10% 半透明中性色容器，`primary` 保持焦线的主色高亮点亮效果。

---

## 4. API 兼容性评估 (`STAGING.md`)

按照 `STAGING.md` 规范，本修改为**完全向下兼容的视觉重构**：

1. **`TextField` 重载函数签名**：
   支持 `TextFieldState` 与 `TextFieldValue` 的两套公开函数签名、参数列表、顺序与类型 100% 保持兼容。
2. **`TextFieldDefaults` 与 `TextFieldColors`**：
   - 包含 `CornerRadius` (16.dp)、`InsideMargin` (`DpSize(16.dp, 16.dp)`) 与 `BorderWidth` (2.dp) 在内的常量保持不变。
   - `textFieldColors()` 的 `labelColor` 默认值优化为 `onSurfaceVariantSummary`。

---

## 5. 后续行动计划

1. 提交本调研文档：
   - Commit Message: `docs: research ColorOS TextField metrics and input specs`
2. 交付用户（你）进行阶段验收。
3. 确认后，执行代码改动与组件文档同步，交付最终验收。
