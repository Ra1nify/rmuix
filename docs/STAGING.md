# Staging 规范

本仓库从 compose-miuix-ui fork 而来，正在逐步把控件换成 ColorOS 17 的视觉与度量。

**核心原则：外观可以变，接口不能断。**

---

### 全局架构设定：平滑超椭圆 (Squircle) 强制默认化

ColorOS 17 系统底层（`OplusPath` / `COUIShapePath`）100% 默认全域启用平滑超椭圆（Squircle，连续曲率圆角）。
本库遵照原生设计事实，作出以下全局架构调整：
1. **取消用户级 Squircle 开关**：不再提供开关控制超椭圆退化为普通圆角，示例 Demo 设置页中的 `Enable Squircle Shapes` 开关已移除。
2. **硬件/平台自动降级机制**：在支持 RuntimeShader (`isRuntimeShaderSupported()`) 的平台/硬件（如 Android SDK >= 33, Skiko Desktop/Wasm 等）上**无条件全局开启 Squircle 渲染**；在不支持 RuntimeShader 的旧平台/设备上由 `isSquircleEnabled()` 自动优雅降级为 `RoundedCornerShape` / Path 矩形，保障零崩溃。

---

### 全局架构设定：TopAppBar 左上角固定布局与 AGSL 模式降级

1. **TopAppBar 标题左上角固定对齐**：完全对齐 ColorOS 17 原生 `COUIToolbar`（`GravityCompat.START`），标题与副标题统一置于左上角/左侧，去除了居中摆放与双标题位移逻辑（`smallTitleTranslationY`），保持顶栏文字稳定不动。
2. **TopAppBar Style 二级选项与降级**：
   - `Progressive`（渐进式渐变模糊）：在支持 AGSL 的设备上展现 ColorOS 17 自上而下衰减的渐进式渐变模糊（Top-to-Bottom Gradient Blur）。
   - `Solid`（实色背景）：对应不支持 AGSL 的设备或非模糊需求，顶栏不进行模糊计算，退回标准的 Surface 实色背景，保障低端设备性能。

---

### 全局架构设定：底栏组件体系 (NavigationBar Modes)

1. **模式 1：贴底导航栏 (`NavigationBar`)**：对齐 ColorOS 17 官方 `COUINavigationView` 度量，容器高度 56dp，图标 24dp，顶边距 9dp，图文间距 2dp，选中项高亮为 `RmuixTheme.colorScheme.primary` 主品牌色。
2. **模式 2：单胶囊悬浮导航栏 (`FloatingNavigationBar`)**：新增 `FloatingNavigationBarStyle.ColorOS` 样式变体，对齐 ColorOS 17 官方 `COUIBottomFloatingNavigationBar` 胶囊度量（胶囊高 56dp，Item 高 48dp，Item 限宽 70dp，项间距 8dp，内边距 16dp，离屏底空隙 8dp + Insets），同时保留 `FloatingNavigationBarStyle.Default` 传统样式以维持 100% 接口向下兼容。

---

## 0. 优先级顺序（最高准则）

**一比一复刻 ColorOS 是最终目标**，兼容 miuix 是达成目标过程中的约束。两者冲突时，按下列顺序决策：

```
1. 视觉与行为一比一复刻 ColorOS      ← 最终目标，不可让步
2. 最大化复用 miuix 的接口            ← 有冲突时让步于 1
3. 把 breaking 降到最小               ← 有冲突时让步于 1、2
```

换句话说：**在一比一复刻 ColorOS 的前提下，尽可能复用、兼容 miuix 的接口。**
不是「为了不 breaking 而保留 miuix 的样子」。

具体到取舍：

| 情况 | 做法 |
| :--- | :--- |
| miuix 的参数能表达 ColorOS 的效果 | 复用，不加新参数 |
| miuix 的参数语义与 ColorOS 冲突 | **改默认值对齐 ColorOS**，保留参数名，声明 BREAKING |
| ColorOS 有 miuix 没有的能力 | 新增可选参数，默认值 = ColorOS 的表现 |
| miuix 有 ColorOS 没有的能力 | 保留参数（不删），但默认表现对齐 ColorOS |

只有一种情况可以保留 miuix 原状：**该差异不影响 ColorOS 复刻**（例如纯 API 层面的便利参数）。

## 1. 接口兼容优先

改造任何一个控件时，**已有的公开签名必须保持可调用**（在不违反第 0 节的前提下）。

```kotlin
// 改造前
fun BasicComponent(
    title: String? = null,
    summary: String? = null,
    ...
)

// 改造后：只许新增，不许删改
fun BasicComponent(
    title: String? = null,
    summary: String? = null,
    assignment: String? = null,        // 新增，有默认值
    iconBadge: Boolean = false,        // 新增，有默认值
    ...
)
```

- 参数顺序不得变动已有项
- 参数名不得变动
- 默认值语义不得变动（除非明确记录为 breaking change）
- 需要改签名时，保留旧签名为 `@Deprecated` 重载，委托到新实现

## 2. 新增参数一律可选，空值走默认

**所有新接口都必须是可选参数，且默认值还原成 miuix 的现有表现。**

```kotlin
// ✅ 正确
fun BasicComponent(
    assignment: String? = null,   // null = 不显示，与 miuix 现状一致
    showRedDot: Boolean = false,  // false = 不显示，与 miuix 现状一致
)

// ❌ 错误
fun BasicComponent(
    assignment: String,           // 必填，破坏兼容
    showRedDot: Boolean,          // 必填，破坏兼容
)
```

### 具体到本项目

| 新增能力 | 参数 | 默认值 | 默认表现 |
| :--- | :--- | :--- | :--- |
| assignment 第三行 | `assignment: String? = null` | `null` | 不显示，行高 = miuix 现状 |
| 图标红点 | `iconBadge: Boolean = false` | `false` | 不显示 |
| 尾部红点 | `endBadge: Boolean = false` | `false` | 不显示 |
| 图标垂直边距分档 | `iconMarginTiered: Boolean = true` | `true` | 自动分档 |

**只有纯视觉改进可以改变默认表现**（比如行高 56 → 48dp），因为那是这次改造的目的。
**新增的功能性参数，默认必须是「关」。**

## 3. 默认值表现对照

新增参数为默认值时，控件必须渲染成和 miuix 原版**结构一致**的样子：

```
miuix BasicComponent(title, summary)
  → 高度、行数、内容布局与改造前一致（除了 ColorOS 视觉度量）

ColorOS BasicComponent(title, summary, assignment = null, iconBadge = false)
  → assignment 不占位，红点不占位
  → 视觉上与只有两面行文字时一致
```

行数变化是**唯一允许的默认差异**：ColorOS 的行高（48dp）和 miuix（56dp）不同，
这是改造目标，不是兼容性破坏。

## 4. 行为差异必须声明

任何无法兼容的改动，在提交信息和文档中明确写出：

```
BREAKING: xxx 参数默认值从 A 改为 B，原因是 xxx。
```

本次改造已声明的：

| 组件 | 变更 | 影响 |
| :--- | :--- | :--- |
| `Switch` | `switchColors().uncheckedTrackColor` 默认从 `colorScheme.secondary` 改为 `#1F000000`/`#33FFFFFF` | 依赖该默认值的调用方视觉会变 |
| `Squircle` | 全局移除 `enableSquircle` 用户选择开关，由平台 RuntimeShader 校验支持情况自动静默决定全域 Squircle 渲染或硬件降级 | 删除了可选择的纯圆角模式开关 |
| `TopAppBar` | 顶栏标题文本完全对齐 ColorOS 17 官方 `COUIToolbar` 左上角/左侧对齐（Left/Start Aligned） | 顶栏标题对齐从居中调整为左上角 |
| `FloatingNavigationBar` | 扩充支持 `FloatingNavigationBarStyle.ColorOS` 变体（56dp 胶囊高，48dp Item 高，8dp 间距，16dp 胶囊边距，8dp 悬浮间距，Spring 缩放按压），同时保留 `FloatingNavigationBarStyle.Default` 传统样式 | 默认切换为 ColorOS 17 胶囊造型 |

## 5. 改造流程

```
1. 调研（源码优先，dump 辅助）
   - 从 com.coui.appcompat.* 读实现
   - dump 只用于验证源码推断
   - 结论写进 docs/research/coloros-*.md

2. 设计 API 变更
   - 列出新增参数及默认值
   - 确认默认值 = miuix 现状
   - 确认无参数被删除或改名

3. 实现
   - 一次改完，不留「新旧并存」的分支
   - 保留 AGENTS.md 的硬约束（如 BasicComponent 的自定义 Layout）

4. 同步
   - docs/components/*.md + docs/zh_CN/components/*.md
   - docs/demo/*.kt
   - example/shared/.../component/*Section.kt

5. 验证
   ./gradlew compileKotlinDesktop spotlessCheck
```

## 6. 不做什么

- **不做兼容层**：不保留「旧视觉 / 新视觉」开关。改造就是替换。
- **不做猜测**：没有源码或 dump 证据的数值不写进代码。
- **不批量改造**：一次一个控件，改完验证，再动下一个。
