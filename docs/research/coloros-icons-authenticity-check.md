# ColorOS 图标库二次比对与 MIUIX 官方矢量回退（Fallback）方案

本文档响应用户指示（“打个断点”，二次审查图标库源）：分析 ColorOS 17 原厂（`SystemUI.apk` / `Settings.apk`）矢量资源提取，并制定回退至 **MIUIX 官方可变矢量字体库 (`misymbol_vf.ttf`)** 的完美替代方案。

---

## 1. ColorOS 17 SystemUI 原厂矢量提取二次审查结论

经对 ColorOS `SystemUI.apk` 和 `Settings.apk` 解包提取发现：
1. **单字重限制**：ColorOS `SystemUI.apk` 状态栏与设置项中的 Android Vector Drawables (`<vector>`) 多为单字重固定路径，**不具备 5 档可变字重（Light, Normal, Regular, Medium, Demibold）** 路径数据。
2. **失真风险**：如果通过纯代码强制给单字重 XML 叠加笔画粗细，会在转角和圆弧处产生几何锯齿与重叠失真。

---

## 2. MIUIX 官方可变矢量库 (`misymbol_vf.ttf`) 回退方案

为确保全量 170+ 个图标在 5 档字重（Light, Normal, Regular, Medium, Demibold）下均具备 100% 生产级的美观度与光滑度，采取以下方案：

1. **真实 Bezier 矢量数据替换**：
   - 采用 MIUIX 官方矢量符号字体 (`misymbol_vf.ttf`) 导出的高精度 Bezier 贝塞尔曲线 Vector Path（包含完整的 `MoveTo`, `QuadTo`, `CurveTo`, `Close`）。
   - 彻底废弃任何粗糙的自绘/临时路径，确保图标极度美观规整。

2. **来源副标题标注 (`来自 MIUIX`)**：
   - 按照用户要求，在 **Example App 示例页 (`IconPage.kt`)、`AllIcons.kt` 与图标指南文档** 中，对源自 MIUIX 官方矢量库的图标标注 **`来源: MIUIX` / `(来自 MIUIX)`**。
   - 对源自 ColorOS 原厂控件的图标标注 **`来源: ColorOS`**。

---

## 3. 展示页面 (`IconPage.kt`) 副标题卡片设计

在 `IconPage.kt` 图标展示列表中，每个图标卡片除了展示图标名称外，新增一行小字副标题说明来源：

```
+-----------------------------------+
|  [ Icon Preview ]                 |
|  Folder                            |
|  来源: MIUIX                      |
+-----------------------------------+
```

---

## 4. 断点思考与确认事项

1. **方案总结**：
   - 使用 MIUIX 官方 `misymbol_vf.ttf` 的精密 Bezier 矢量数据（保证 5 档字重光滑无瑕疵）。
   - 在 Example App 图标页中为所有图标添加副标题说明来源（`来自 MIUIX` / `来自 ColorOS`）。
2. **暂停等待确认**：
   - 本调研文档已归档在 `docs/research/coloros-icons-authenticity-check.md`，等待你的最终确认即可启动转换替换！
