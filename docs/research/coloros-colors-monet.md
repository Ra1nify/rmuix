# ColorOS 17 颜色体系与 Monet 动态配色 (Color Inspector Research)

本文档响应用户指示，针对 Example App 第三页（`ColorPage.kt` 颜色解析展示器）的 4 种主题模式（普通浅、普通暗、Monet 动态浅、Monet 动态暗）进行系统级调研与层级分类，并核查遗漏的 Color Token 与 COUI 源码主题属性映射。

---

## 1. 四种色彩模式架构 (Four Color Modes Architecture)

ColorOS 17 的色彩体系由**硬编码中性色阶**与基于 HCT / Material You 算法的 **Monet 动态配色**共同驱动：

```
Color System / Scheme
├── 普通模式 (Standard Colors)
│   ├── 普通浅 (Standard Light): 明亮中性底色 + 54%/26% 中性色阶 + COUI 蓝 (#007AFF)
│   └── 普通暗 (Standard Dark): 纯黑背景 (#000000) + 暗色卡片 (#1C1C1E) + 高亮蓝 (#0D84FF)
└── Monet 动态模式 (Dynamic Monet Colors)
    ├── Monet 浅 (Dynamic Light): 基于系统 Seed Color 衍生 Tone 60/98/96 调色板
    └── Monet 暗 (Dynamic Dark): 基于系统 Seed Color 衍生 Tone 80/6/12 调色板
```

### 1.1 普通浅 (Standard Light) 核心映射
- **`primary` (主色)**：`#007AFF`（COUI 默认经典蓝）
- **`onPrimary` (主色上文本)**：`#FFFFFF`
- **`background` (页面背景)**：`#F7F7F7`（浅灰背景）
- **`surfaceContainer` (卡片/分组背景)**：`#FFFFFF`（纯白卡片）
- **`secondaryContainer` (半透明容器)**：`#14000000` (8% 半透明黑，对应 `coui_color_container8`)
- **`onBackground` (主级文本)**：`#FF000000` (100% 纯黑)
- **`onSurfaceVariantSummary` (次级文本)**：`#8A000000` (54% 半透明黑，对应 `couiColorLabelSecondary`)
- **`onSurfaceVariantActions` (三级图标/辅助)**：`#42000000` (26% 半透明黑，对应 `couiColorLabelTertiary`)
- **`dividerLine` (分割线)**：`#0F000000` (6% 半透明黑)

### 1.2 普通暗 (Standard Dark) 核心映射
- **`primary` (主色)**：`#0D84FF`（暗色高亮蓝）
- **`onPrimary` (主色上文本)**：`#FFFFFF`
- **`background` (页面背景)**：`#000000`（纯黑沉浸背景）
- **`surfaceContainer` (卡片/分组背景)**：`#1C1C1E`（深灰卡片）
- **`secondaryContainer` (半透明容器)**：`#1AFFFFFF` (10% 半透明白，对应 `coui_color_container8`)
- **`onBackground` (主级文本)**：`#FFFFFFFF` (100% 纯白)
- **`onSurfaceVariantSummary` (次级文本)**：`#8AFFFFFF` (54% 半透明白)
- **`onSurfaceVariantActions` (三级图标/辅助)**：`#4DFFFFFF` (30% 半透明白)
- **`dividerLine` (分割线)**：`#1AFFFFFF` (10% 半透明白)

### 1.3 Monet 动态浅 (Monet Light) 衍生机制
- 通过 `platformDynamicColors(dark = false)` / `readSystemPaletteInfo` 从 Android `Settings.Secure` 中读取 `theme_customization_overlay_packages` 的 `system_palette` 种子色（如壁纸提取色或自定义 Seed）。
- 映射结果：
  - **`primary`**：Seed Color (Tone 50 / 60)
  - **`background`**：Tone 98
  - **`surfaceContainer`**：Tone 96 / 100
  - **`onBackground`**：Tone 10
  - **`onSurfaceVariantSummary`**：Tone 30/40

### 1.4 Monet 动态暗 (Monet Dark) 衍生机制
- 通过 `platformDynamicColors(dark = true)` 衍生暗色变体：
  - **`primary`**：Seed Color (Tone 80 高亮色)
  - **`background`**：Tone 6
  - **`surfaceContainer`**：Tone 12
  - **`onBackground`**：Tone 90
  - **`onSurfaceVariantSummary`**：Tone 80

---

## 2. COUI 源码主题属性与 Rmuix `Colors.kt` 对照映射表

为了确保 ColorOS 17 系统属性与 Rmuix 颜色系统的完整覆盖，下表列出了 COUI 核心 Theme 属性与 Rmuix `Colors.kt` 的 1:1 对照关系：

| COUI 原生主题属性 (Theme Attr) | 语义说明 | 对应 Rmuix `Colors` 字段 | 浅色模式值 | 暗色模式值 |
| :--- | :--- | :--- | :--- | :--- |
| `couiColorPrimary` | 应用强调主色 | `primary` | `#007AFF` | `#0D84FF` |
| `couiColorContainerTheme` | 主色半透明背景容器 | `primaryContainer` | `#15007AFF` | `#260D84FF` |
| `couiColorLabelPrimary` | 一级主要文本 | `onBackground` / `onSurface` | `#FF000000` (100%) | `#FFFFFFFF` (100%) |
| `couiColorLabelSecondary` | 二级次要文本/Summary | `onSurfaceVariantSummary` | `#8A000000` (54%) | `#8AFFFFFF` (54%) |
| `couiColorLabelTertiary` | 三级辅助文本/图标/描边 | `onSurfaceVariantActions` | `#42000000` (26%) | `#4DFFFFFF` (30%) |
| `couiColorLabelQuaternary` | 四级禁用文本/禁用描边 | `disabledOnSecondaryVariant` | `#14000000` (8%) | `#26FFFFFF` (15%) |
| `coui_color_container8` | 半透明中性色容器 | `secondaryContainer` | `#14000000` (8%) | `#1AFFFFFF` (10%) |
| `couiColorBackgroundWithCard` | 卡片与列表分组背景 | `surfaceContainer` | `#FFFFFF` | `#1C1C1E` |
| `couiColorBackground` | 页面最底层背景 | `background` / `surface` | `#F7F7F7` | `#000000` |
| `couiColorDivider` | 列表/卡片分割线 | `dividerLine` | `#0F000000` (6%) | `#1AFFFFFF` (10%) |

---

## 3. 遗漏 Token 检查与第三页颜色解析展示器 (`ColorPage.kt`) 重构方案

### 3.1 遗漏 Token 检查结果
经核对 `Colors.kt`（全量 53 个字段）与 `ColorPage.kt` 中的 `ColorsPreview` 字段列表，发现：
- 遗漏 Token：**`sliderBackground`** 之前在 `ColorPage.kt` 中漏掉了展示。重构时需要将其补齐，确保全量 53 个 Color Token 100% 覆盖。

### 3.2 第三页颜色解析展示器 (`ColorPage.kt`) 分类展示重构设计

为大幅提升颜色检查与设计比对的便利性，将全量 53 个 Token 重构为 **5 大语义分类模块**，并扩展色值详情：

1. **主色调与品牌色 (Primary & Brand)**:
   - `primary`, `onPrimary`, `primaryContainer`, `onPrimaryContainer`, `primaryVariant`, `onPrimaryVariant`, `disabledPrimary`, `disabledOnPrimary`, `disabledPrimaryButton`, `disabledOnPrimaryButton`, `disabledPrimarySlider`
2. **中性文本梯阶 (Neutral Text Ladder)**:
   - `onBackground`, `onBackgroundVariant`, `onSurface`, `onSurfaceSecondary`, `onSurfaceVariantSummary` (54%), `onSurfaceVariantActions` (26%/30%), `disabledOnSurface`, `disabledOnSecondary`, `disabledOnSecondaryVariant`
3. **表面与卡片容器 (Surface & Card Containers)**:
   - `background`, `surface`, `surfaceVariant`, `surfaceContainer`, `surfaceContainerHigh`, `surfaceContainerHighest`, `secondary`, `onSecondary`, `secondaryVariant`, `onSecondaryVariant`, `secondaryContainer`, `onSecondaryContainer`, `secondaryContainerVariant`, `onSecondaryContainerVariant`, `tertiaryContainer`, `onTertiaryContainer`, `tertiaryContainerVariant`
4. **功能与辅助色彩 (Functional, Slider & Window)**:
   - `error`, `onError`, `errorContainer`, `onErrorContainer`, `windowDimming`, `sliderKeyPoint`, `sliderKeyPointForeground`, `sliderBackground`
5. **分割线与外框 (Divider & Outline)**:
   - `dividerLine`, `outline`

### 3.3 色彩小块 (`ColorBlock`) 的信息增强
在色彩小块中显示以下信息：
- **Token 属性名**（如 `primary` / `secondaryContainer`）
- **16 进制颜色 Hex 代码**（如 `#007AFF` / `#1F000000`）
- **Alpha 不透明度**（如 `100%` / `54%` / `12%`）
- **点击交互**：点击任意色彩小块可复制 Hex 色值到剪贴板。

---

## 4. 总结与后续指导

1. 完成了 COUI 主题属性与 Rmuix 53 个 Color Token 的 1:1 对照检查，并找出了 `ColorPage.kt` 遗漏的 `sliderBackground` Token。
2. 提出了 5 大语义分类模块与带 Hex 编码/Alpha 不透明度的 Color Inspector 展示方案。
