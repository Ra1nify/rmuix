# ColorOS 17 图标库 (Icon Package / RmuixIcons) 重构调研与分组设计

本文档响应用户指示，对 ColorOS 17 原厂图标体系（`OplusSymbols` 5 档字重矢量符号集）与 `rmuix-icons` 图标库架构进行深入对比，并评估引入 `Default` / `Outline` / `Filled` 分组架构的必要性与重构方案。

> **注意**：本调研文档遵循“先调研、不修改代码”的原则，为后续 `rmuix-icons` 模块的升级提供架构设计依据。

---

## 1. ColorOS 17 原厂图标 5 档字重体系 (5-Weight Style Scale)

ColorOS 17 采用了 `OplusSymbols` 可变矢量符号字体，使其图标能够与系统主字体 `OPlus Sans` 的字重粗细进行 1:1 动态匹配：

```
ColorOS 17 OplusSymbols (5-Weight Scale)
├── Light    (字重 250): 极细线条，适用于轻量提示与透明按钮
├── Normal   (字重 305): 细线条
├── Regular  (字重 330): 标准线条（默认形态）
├── Medium   (字重 430): 中粗线条，适用于卡片标题与选中文本
└── Demibold (字重 500): 半粗线条，适用于强调按钮与高亮状态
```

`rmuix-core` 的 `RmuixIcons` 对象中已预留了这 5 种字重分类（`RmuixIcons.Light`、`RmuixIcons.Normal`、`RmuixIcons.Regular`、`RmuixIcons.Medium`、`RmuixIcons.Demibold`）。重构时需对照 ColorOS `OplusSymbols` 矢量 Path 补齐每个图标对应的 5 档字重变体。

---

## 2. 图标分组架构评估 (`Default` / `Outline` / `Filled`)

### 2.1 现状与痛点分析
目前 `rmuix-icons` 将线性图标与实心图标平铺在同一个命名空间下（如 `Folder` 与 `FolderFill`、`Notes` 与 `NotesFill`、`Favorites` 与 `FavoritesFill`）：
- **痛点 1 (代码可读性与智能补全)**：开发者在 IDE 中敲击 `RmuixIcons.` 时，大量后缀带有 `Fill` 的图标与线性图标混杂在一起，无法按线/面形态快速筛选。
- **痛点 2 (组件状态切换模式)**：在 `NavigationBar`、`NavigationRail` 或 `TabRow` 等支持选中/未选中状态切换的组件中，ColorOS 的标准规范为：**未选中显示 Outline (空心线型)，选中显示 Filled (实心面型)**。如果无分组，状态切换代码写起来不够优雅。

### 2.2 推荐的分组架构设计 (Grouping Architecture)

强烈建议增加分组功能！在 `RmuixIcons` 中引入 **`Outline` (空心/线性)** 与 **`Filled` (实心/面型)** 两个一级语义分组，并提供 **`Default`** 别名：

```kotlin
object RmuixIcons {
    // 1. 一级形态分组 (Style Groups)
    object Outline
    object Filled
    object Default // 指向 Outline

    // 2. 5 档字重子组 (Weight Subgroups)
    object Light
    object Normal
    object Regular
    object Medium
    object Demibold
}
```

#### 调用示例对照：

| 使用场景 | 代码写法 | 语义说明 |
| :--- | :--- | :--- |
| **标准直接调用** | `RmuixIcons.Folder` | 默认获取 `Outline.Regular.Folder`（完全向下兼容） |
| **显式线性/空心** | `RmuixIcons.Outline.Folder` | 明确指定为空心线型图标（默认 Regular 字重） |
| **显式实心/面型** | `RmuixIcons.Filled.Folder` | 明确指定为实心面型图标（默认 Regular 字重） |
| **带字重的空心/实心**| `RmuixIcons.Outline.Medium.Folder` / `RmuixIcons.Filled.Demibold.Folder` | 精确控制图标的形变与线宽 |

#### 状态切换效果：
```kotlin
// 导航栏/选项卡 选中与未选中状态切换
Icon(
    imageVector = if (selected) RmuixIcons.Filled.Home else RmuixIcons.Outline.Home,
    contentDescription = "Home",
)
```

---

## 3. 向下兼容性保障 (`STAGING.md`)

按照 `STAGING.md` 规范，本重构为**完全向下兼容的架构增强**：

1. **原调用保持可用**：
   - 现有的 `RmuixIcons.Folder` / `RmuixIcons.FolderFill` 保持扩展属性映射，旧代码无需任何修改即可继续运行。
2. **渐进式迁移**：
   - 优先推荐新代码使用 `RmuixIcons.Outline.Folder` 与 `RmuixIcons.Filled.Folder` 分组范式。

---

## 4. 后续重构实施路线图 (Refactoring Roadmap)

1. **第一阶段 (基础架构)**：
   - 在 `rmuix-core` 的 `RmuixIcons.kt` 中扩充 `Outline` 与 `Filled` 对象。
2. **第二阶段 (图标分类与生成)**：
   - 整理 `rmuix-icons` 中的图标，将带 `Fill` 后缀的矢量提取并映射到 `Filled` 分组中，线型图标映射到 `Outline` / `Default` 中。
   - 对照 ColorOS `OplusSymbols` 补充缺失图标的 5 档字重 SVG/Vector Path。
3. **第三阶段 (展示页增强)**：
   - 在 Example App 的 `IconPage.kt` 中新增 `Outline` vs `Filled` Tab 切换与 5 档字重预览。

---

调研已完成。提交本调研文档：
- Commit Message: `docs: research ColorOS 1:1 icon scale and Outline/Filled grouping architecture`
