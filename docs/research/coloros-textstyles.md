# ColorOS 17 排版系统 (Typography / TextStyles) 深度调研与 CRUD 评估

本文档响应用户指示，对比 ColorOS 17 COUI 原厂排版体系（`com.support.appcompat.R$style` 与 `styles.xml` 中的 `couiText*` 系列）与 Rmuix `TextStyles.kt` 中的字号与字重缩放，进行系统级 CRUD（增删改查）评估。

> **注意**：本调研文档遵循“先调研、不修改代码”的原则，为后续 `TextStyles.kt` 的优化升级提供精确参数与评估结论。

---

## 1. ColorOS 17 原厂排版梯度全景 (`couiText*` Scale)

从反编译资源 `camera/apktool_out/res/values/styles.xml`（第 10830 - 10925 行）提取出 ColorOS 17 完整的 `couiText*` 字体梯度体系：

```
ColorOS 17 Typography Scale (couiText*)
├── Display (巨幅展示)
│   ├── couiTextDisplayL  : 72.0sp (Medium)
│   ├── couiTextDisplayM  : 36.0sp (FontWeight 600)
│   ├── couiTextDisplayS  : 32.0sp (FontWeight 600)
│   └── couiTextDisplayXS : 24.0sp (FontWeight 600)
├── Headline (标题层级)
│   ├── couiTextHeadlineL : 28.0sp (FontWeight Medium)
│   ├── couiTextHeadlineM : 20.0sp (FontWeight Medium)
│   ├── couiTextHeadlineS : 18.0sp (FontWeight 550 / Medium)
│   └── couiTextHeadlineXS: 16.0sp (FontWeight Medium)
├── Body (正文与列表)
│   ├── couiTextBodyXL    : 16.0sp (正文/列表标题)
│   ├── couiTextBodyL     : 15.0sp (二级正文)
│   ├── couiTextBodyM     : 14.0sp (卡片正文/Subtitle)
│   ├── couiTextBodyS     : 13.0sp (Summary 摘要)
│   └── couiTextBodyXS    : 12.0sp (Description 描述)
└── Button & Tag (按钮与标签)
    ├── couiTextButtonL   : 16.0sp (FontWeight Medium)
    ├── couiTextButtonM   : 14.0sp (FontWeight Medium)
    ├── couiTextButtonS   : 12.0sp (FontWeight Medium)
    └── couiTextCaption/Tag: 10.0sp (FontWeight Medium)
```

---

## 2. 与 Rmuix `TextStyles.kt` 1:1 对照及 CRUD 评估

| 语义名称 | Rmuix 现有字号/字重 | ColorOS COUI 源码对应项 | 评估动作 (CRUD) | 拟改动方案 |
| :--- | :--- | :--- | :--- | :--- |
| **`title1`** | 32sp | `couiTextDisplayS` (32sp) | **【R - Read 保持】** | 32sp (完全匹配) |
| **`title2`** | 24sp | `couiTextDisplayXS` (24sp) | **【R - Read 保持】** | 24sp (完全匹配) |
| **`title3`** | 20sp | `couiTextHeadlineM` (20sp) | **【R - Read 保持】** | 20sp (完全匹配) |
| **`title4`** | 18sp | `couiTextHeadlineS` (18sp) | **【R - Read 保持】** | 18sp (完全匹配) |
| **`headlineLarge`**| - (缺失) | `couiTextHeadlineL` (28sp) | **【C - Create 新增】**| **新增 28sp Medium**（对齐 ColorOS 大标题） |
| **`headlineSmall`**| 18sp | `couiTextHeadlineS` (18sp 550) | **【R - Read 保持】** | 18sp (粗体/弹窗标题) |
| **`headline1`** | 17sp | `couiTextHeadlineXS` (16sp) | **【R - Read 保持/兼容】** | 保持 17sp / 兼容 MIUI 旧调用 |
| **`headline2`** | 16sp | `couiTextHeadlineXS` (16sp) | **【R - Read 保持】** | 16sp (列表主要标题) |
| **`body1`** | 16sp | `couiTextBodyXL` (16sp) | **【R - Read 保持】** | 16sp (正文大) |
| **`body2`** | 14sp | `couiTextBodyM` (14sp) | **【R - Read 保持】** | 14sp (正文中) |
| **`button`** | 17sp | `couiTextButtonL` (16sp Medium) | **【U - Update 修改】**| **17sp 降为 16sp (Medium)**，对齐 COUI 按钮字号 |
| **`footnote1`** | 13sp | `couiTextBodyS` (13sp) | **【R - Read 保持】** | 13sp (列表摘要 Summary) |
| **`footnote2`** | 11sp | `couiTextBodyXS` (12sp) | **【U - Update 修改】**| **11sp 升为 12sp**，对齐 COUI 描述字号 |
| **`subtitle`** | 14sp Bold | `couiTextBodyM` (14sp Medium) | **【R - Read 保持】** | 14sp Bold |

---

## 3. CRUD 总结与建议

1. **【U - 修改】`button` 字号由 17sp 调整为 16sp (Medium Weight)**：
   - ColorOS 17 大号按钮标准字号为 16sp（`couiTextButtonL`），中号为 14sp（`couiTextButtonM`）。将 `textStyles.button` 调为 16sp 更贴合 ColorOS 按钮比例。
2. **【U - 修改】`footnote2` 字号由 11sp 调整为 12sp**：
   - ColorOS 17 最小正文描述字号为 12sp（`couiTextBodyXS` / `couiTextDescription`），提升 11sp 到 12sp 能提高小字的可读性。
3. **【C - 新增】`headlineLarge` (28sp)**：
   - 在 `TextStyles` 中补齐 ColorOS 的 `couiTextHeadlineL` (28sp) 梯度，以便大标题及 TopAppBar 展开态使用。
4. **【D - 删除】无**：
   - 保持现有所有属性不删除，保证 100% 接口与应用层兼容。

---

## 4. 后续行动计划

1. 提交本调研文档：
   - Commit Message: `docs: research ColorOS typography scale and TextStyles CRUD evaluation`
2. 交付用户（你）进行阶段验收。
3. 确认后，执行 `TextStyles.kt` 与 `TextStylePage.kt` 代码改动，交付最终验收。
