# HANDOFF — ColorOS 迁移工作交接

给接手的 agent：这份文档是**唯一需要先读的东西**。读完你应该能独立继续迁移下一个控件。

---

## 1. 这个项目在做什么

`rmuix` 是 `compose-miuix-ui` 的 fork。目标是把库里的控件**逐个**改造成 ColorOS 17 的视觉与度量。

**不是重写，是替换。** 外观可以变，公开接口不能断（详见 `docs/STAGING.md` 与 AGENTS.md 的
`## Staging` 一节）。

## 2. 硬性规则（违反会被要求返工）

| 规则 | 说明 |
| :--- | :--- |
| **一次一个控件** | 不要批量改。改完一个、验证、提交，再动下一个 |
| **源码优先，dump 辅助** | 先从 `com.coui.appcompat.*` 反编译源码读实现，dump 只用于验证推断或消除歧义 |
| **新参数一律可选** | 默认值必须还原成 miuix 的现有表现。功能性开关默认「关」 |
| **不保留新旧双轨** | 没有「旧视觉/新视觉」开关。改造就是替换 |
| **不改 API 顺序/命名** | 需要改签名时保留 `@Deprecated` 重载 |
| **提交前必跑** | `./gradlew compileKotlinDesktop spotlessCheck` |
| **不动 BasicComponent 的 Layout** | AGENTS.md 明确禁止改成 `Row + weight(1f)`，会导致文字竖排 |

## 3. 完整工作流（每个控件重复）

```
① 调研
   - 读 rmuix 当前实现（rmuix-ui/.../basic/Xxx.kt）
   - 从 coui 源码找对照（见第 4 节路径）
   - 提取：几何尺寸、圆角、描边、颜色（亮/暗）、动画参数
   - 写 docs/research/coloros-<控件>.md
   - 提交：docs: research ColorOS <控件> geometry and colours

② 实施（用户确认后）
   - 改绘制/几何/颜色
   - 新增 Colors 字段（带默认值）
   - 编译 → spotlessApply → spotlessCheck
   - 提交：library: rebuild <控件> on ColorOS <特征> metrics

③ 同步
   - docs/components/<控件>.md（EN）
   - docs/zh_CN/components/<控件>.md（ZH）
   - docs/demo/<控件>Demo.kt（如 API 变了）
   - example/shared/.../component/<控件>Section.kt（如 API 变了）
```

**② 和 ③ 必须在同一个 commit 里**（AGENTS.md 要求）。

## 4. 参考资料位置

### 反编译源码（主力）

```
C:\Users\aka\Desktop\Portal\Projekt\camera\
├── decompiled\sources\com\coui\appcompat\    ← COUI 控件实现（首选）
│   ├── checkbox\COUICheckBox.java
│   ├── button\COUIButton.java
│   ├── couiswitch\COUISwitch.java
│   ├── preference\                           ← 所有 preference 类
│   └── animation\                            ← 插值器、SpringDragHelper
├── apktool_out\res\
│   ├── values\dimens.xml                     ← 所有尺寸
│   ├── values\styles.xml                     ← 所有样式
│   ├── drawable\coui_btn_*.xml               ← 矢量图（含精确路径坐标）
│   └── layout\coui_preference*.xml           ← 布局结构
└── apktool_out\res\values-night\             ← 暗色覆盖
```

### Settings.apk 资源表（颜色解析）

```bash
cd /c/Users/aka/Desktop/Portal/Projekt/settings_src
# 已导出的资源表
grep -A3 "color/coui_color_xxx\$" /tmp/settings_res.txt
# 如需重新导出
AAPT2="/c/Users/aka/.gradle/caches/8.11.1/transforms/4ffd4fff6a6e71982aca18e72078f54d/transformed/aapt2-8.7.3-12006047-windows/aapt2.exe"
"$AAPT2" dump resources Settings.apk > /tmp/settings_res.txt
```

**为什么用 Settings 而不是 camera**：Settings 的控件库最全，颜色资源更完整。

### 已有调研文档

```
docs/research/
├── coloros-basic-component.md    ← 已完成
├── coloros-checkbox.md           ← 已完成
├── coloros-radiobutton.md        ← 已完成
├── coloros-button.md             ← 已完成
├── coloros-switch.md             ← 已完成
├── coloros-bar-spec.md           ← 顶栏/底栏规格
└── dumps/                        ← 真机 dump + meta
```

## 5. 已完成 / 待办

| 控件 | 状态 | commit |
| :--- | :--- | :--- |
| Switch | ✅ 完成 | `9116b738` |
| BasicComponent | ✅ 完成 | `b87f7961` |
| Checkbox | ✅ 完成 | `bd4f3587` |
| RadioButton | ✅ 完成 | `e541d267` |
| Button | ✅ 完成 | `d3c5bdf9` |
| **Slider** | ⬜ 待调研 | — |
| **TextField** | ⬜ 待调研 | — |
| **ProgressIndicator** | ⬜ 待调研 | — |
| **TabRow** | ⬜ 待调研 | — |
| **Card** | ⬜ 待调研 | — |
| **SwitchPreference 等** | ⬜ 待确认是否跟随 | — |

## 6. 已确立的关键设计语言（新控件要遵循）

这些是跨控件一致性，别破坏：

| 特征 | 值 | 适用 |
| :--- | ---: | :--- |
| 控件总尺寸 | 24dp | Checkbox / RadioButton |
| 描边宽度 | **1.3dp** | Checkbox / RadioButton |
| 未选中描边色 | `#42000000` / `#4DFFFFFF` | Checkbox / RadioButton |
| 禁用描边色 | `#14000000` / `#26FFFFFF` | Checkbox / RadioButton / Switch 禁用轨道 |
| 「次要」的语义 | **半透明中性色**，不是实心 | Button SECONDARY / Checkbox 描边 |
| 行高 | 48dp | BasicComponent |
| 页面水平内边距 | 32dp | BasicComponent |

**核心洞察**：ColorOS 的「次要/未选中」一律用**半透明中性色**，从不实心。
rmuix 原来多处用实心 `secondary`，这是最主要的系统性偏差。

## 7. 踩过的坑（务必避开）

| 坑 | 后果 | 正确做法 |
| :--- | :--- | :--- |
| **`RmuixTheme.colorScheme.isDark`** | 不存在，编译失败 | 用 `isSystemInDarkTheme()` |
| **`RmuixTheme.density`** | 不存在 | 用 `LocalDensity.current`（composable 作用域，不能在 `Layout` 的 measure lambda 里） |
| **`Stroke.Fill`** | 不存在 | 不传 `style` 就是填充 |
| **`import androidx.compose.material3.Text`** | 解析错 | `Text` 就在 `basic` 同包，不需要 import |
| **`Placeable.lineCount()`** | 不存在 | 用高度估算或参数推断 |
| **`Color.value.toLong()`** | 与 `Color(Long)` 不配对，丢色变 0 | 用 `toArgb()` / `Color(int)` |
| **PowerShell 改文件** | `Set-Content -Encoding UTF8` 会双编码中文成乱码 | **用 `edit`/`write` 工具，或 Python 显式 `encoding="utf-8"`** |
| **Spotless 参数顺序** | `modifier` 必须在其他可选参数前 | 参照现有的 `RmuixHintBadge(modifier, count)` |
| **一次改多个控件** | 出问题难定位 | 严格一次一个 |

### 关于 PowerShell

**不要**用 `Get-Content -Raw` + `Set-Content` 往返改中文文件，会把 GBK 字节写进去。
用 `read`/`edit`/`write` 工具，或用 Python：

```python
import io
t = io.open(path, encoding="utf-8").read()
t = t.replace(old, new)
io.open(path, "w", encoding="utf-8", newline="\n").write(t)
```

## 8. 精确替换的实用技巧

改 Java/Kotlin 文件时，**锚点匹配经常因为空格/换行不同而失败**。可靠做法：

1. 先用 `sed -n 'A,Bp' file | cat -A` 看**精确字节**（`$` 是行尾，能看出空格）
2. 用正则做块替换，而非长字符串匹配：

```python
import re
m = re.search(r"    /\*\*\n.*?\n    val MinHeight = 40\.dp", t, re.DOTALL)
assert m, "anchor"
t = t[:m.start()] + new_block + t[m.end():]
```

3. 改完**立刻 grep 验证**数值是否正确、有没有误伤别处（我曾用 `replace("16.dp", "21.5.dp")`
   污染了同一行里的 `PaddingValues(horizontal = 16.dp)`）

## 9. 验证流程

```bash
# 快速编译（1-2 分钟）
./gradlew compileKotlinDesktop

# 格式（必须过，CI 会拒）
./gradlew spotlessCheck
# 只在报错时用
./gradlew spotlessApply

# 文档乱码检查
# CJK 乱码的三个典型字（GBK 被当 UTF-8 读的产物）
grep -c "<U+FFFD>\|<U+FFFD>\|<U+FFFD>" docs/components/*.md docs/zh_CN/components/*.md
# 也可以直接看是否有替换字符
grep -c "\xe9\x88\xa5\|\xe9\x8e\xbf" docs/components/*.md 2>/dev/null || echo "0 乱码"
```

**不要跑全量构建**（`assemble`），用户明确说过不需要，且网络环境不一定能拉到依赖。

## 10. 用户偏好（重要）

- 中文交流
- **要求一次做到位**，不接受「新旧并存」的过渡态
- 先说结论/评估，再动手
- 调研阶段**不要**直接改代码，等确认
- 提交信息用英文，遵循 AGENTS.md 的 `<scope>: <summary>` 格式，subject ≤ 72 字符
- 遇到不确定的设计取舍，**列出选项 + 推荐**，让用户拍板

## 11. 下一个建议：Slider

理由：与 Switch 同族（拖动手势），ColorOS 有独特的「气泡 thumb」。调研入口：

```
camera/decompiled/sources/com/coui/appcompat/seekbar/   ← 找 COUISeekBar
camera/apktool_out/res/values/dimens.xml                ← coui_seekbar_* 尺寸
```

已知线索（之前在 Button 调研时顺带看到）：

```
coui_seekbar_thumb_radius = 6.0dp
coui_seekbar_thumb_max_radius = 8.0dp
coui_seekbar_thumb_out_radius = 6.0dp
coui_seekbar_thumb_in_scale_radius = 3.67dp
coui_seekbar_thumb_shadow_size = 2.0dp
coui_seekbar_intent_thumb_radius_default = 4.0dp
```

---

**接手后第一步**：读 `docs/STAGING.md` + 本文档 + 最近 3 个 commit 的 message，然后从 Slider 的
① 调研开始。
