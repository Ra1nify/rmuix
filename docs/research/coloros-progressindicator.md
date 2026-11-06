# ColorOS 17 ProgressIndicator 视觉度量与设计语言

本文档记录 `LinearProgressIndicator`、`CircularProgressIndicator` 及 `InfiniteProgressIndicator` 在迁移至 ColorOS 17 视觉风格时的几何度量、动画轨道规格、配色方案及 API 兼容性评估。

---

## 1. 概述与设计定位

`rmuix` 提供了三类进度指示器：
1. `LinearProgressIndicator`：线性进度条，支持确定态 (Progress 0..1) 与平滑平移的非确定态 (Indeterminate)。
2. `CircularProgressIndicator`：环形进度条，带有底色圆轨与弧度动画。
3. `InfiniteProgressIndicator`：无限循环的小球轨道指示器，适用于微型加载场景。

对比 COUI 反编译源码（`com.coui.appcompat.progressbar.COUIHorizontalProgressBar` 与 `COUICircularProgressBar`）及资源表 `dimens.xml`，ColorOS 17 的进度条遵循全端通用的半透明中性轨道与主色高亮进度体。

---

## 2. 几何度量 (Geometry Metrics)

| 度量属性 | rmuix 现有值 | ColorOS 17 COUI 源码规范 | 说明 |
| :--- | :--- | :--- | :--- |
| **线性进度条高度 (`LinearHeight`)** | 6.dp | **6.dp** | 圆角端头 (`CornerRadius(size.height / 2)`) 线性进度条 |
| **环形进度条尺寸 (`CircularSize`)** | 30.dp | **30.dp** (`coui_circular_progress_medium_length`) | 环形进度条直径 |
| **环形进度条线宽 (`CircularStroke`)** | 4.dp | **4.dp** (`coui_circular_progress_medium_stroke_width` ~ 3-5dp) | 环形轨道线宽 |
| **无限指示器尺寸 (`InfiniteSize`)** | 20.dp | **20.dp** | 微型加载小球轨道尺寸 |
| **无限指示器线宽与小球尺寸** | 2.dp | **2.dp** | 轨线宽度与轨道小球半径 |

---

## 3. 颜色体系 (Color OS Tokens)

根据 `STAGING.md` 最高准则（一比一复刻 ColorOS 17 视觉与行为），`ProgressIndicatorDefaults.progressIndicatorColors()` 配色方案如下：

| 色彩属性 | 原 rmuix 映射 | ColorOS 17 规范映射 | 亮色值 | 暗色值 |
| :--- | :--- | :--- | :--- | :--- |
| **`foregroundColor`** (进度高亮) | `primary` | **`primary`** (`couiColorPrimary`) | 主题主色 | 主题主色 |
| **`disabledForegroundColor`** | `disabledPrimarySlider` | **`disabledPrimarySlider`** | 禁用态主色 | 禁用态主色 |
| **`backgroundColor`** (背景轨道) | `secondaryContainer` | **`secondaryContainer`** (`coui_progressbar_background_color_normal`) | `#1F000000` (12%) | `#1FFFFFFF` (12%) |

### 设计洞察
* **轨道背景**：ColorOS 的 `coui_progressbar_background_color_normal` 采用 12% 半透明中性色（`#1F000000` 亮色 / `#1FFFFFFF` 暗色），完美对应 `RmuixTheme.colorScheme.secondaryContainer`，在浅色与深色模式下均展现自然沉浸的半透明轨道。
* **进度高亮**：主色 `primary`（`#007AFF` / `#0D84FF`）保持一致的颜色点亮。

---

## 4. API 兼容性评估 (`STAGING.md`)

按照 `STAGING.md` 规范，本修改为**完全向下兼容的视觉重构**：

1. **公开函数签名**：
   - `LinearProgressIndicator`
   - `CircularProgressIndicator`
   - `InfiniteProgressIndicator`
   三者的参数列表、顺序、类型与默认值 100% 保持向上兼容。

2. **`ProgressIndicatorDefaults` 与 `ProgressIndicatorColors`**：
   - 常量与工厂函数无变动，物理度量与配色全面匹配 ColorOS 17 规范。

---

## 5. 后续行动计划

1. 提交本调研文档：
   - Commit Message: `docs: research ColorOS ProgressIndicator metrics and track specs`
2. 交付用户（你）进行阶段验收。
3. 确认后，执行代码改动与组件文档同步，交付最终验收。
