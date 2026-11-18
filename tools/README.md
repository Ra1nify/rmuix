# Stock ColorOS 静态审计脚本

二十二个可复跑的检查，对应 [stock-coloros-review.md](stock-coloros-review.md) 里记录的验证方法。全部是**离线只读**操作：只读 APK 反编译产物与仓库文件，不构建、不运行测试、不碰设备。

## 前置

- 反编译产物在 `C:\dump\settings_decompiled`（`sources/` 与 `resources/res/`）。
- PowerShell 7（`pwsh`）或 Windows PowerShell 5.1 均可；脚本只用基础语法。仓库环境里 `pwsh` 不一定在 PATH 上，可直接用 `powershell.exe -File tools\stock-audit.ps1 -Check X`。
- 可选的 ktlint 检查：`%TEMP%\rmuix_lint.ps1`（本仓未提交该脚本，见「附：lint harness」）。

## 用法

```powershell
# 1) StockValues 里的资源名是否都真实存在于 APK
powershell.exe -File tools\stock-audit.ps1 -Check Tokens

# 2) 文档里引用的 coui 名称是否都真实存在于 APK
powershell.exe -File tools\stock-audit.ps1 -Check DocsTokens

# 3) 文档里"`coui_x` = 12dp"这类严格数值声明是否与 APK 的 dimen 一致
powershell.exe -File tools\stock-audit.ps1 -Check DocsValues

# 4) 中英文档的属性表数值是否一致（按常量名逐行比较）
powershell.exe -File tools\stock-audit.ps1 -Check DocsPair

# 5) 注释里的颜色值是否与 APK 的 colors.xml / styles.xml 一致
powershell.exe -File tools\stock-audit.ps1 -Check DocVsApk

# 6) 函数注释里的颜色值是否与函数体的 Color(0x...) 一致
powershell.exe -File tools\stock-audit.ps1 -Check DocVsCode

# 7) 文档属性表里的默认值是否与 XxxDefaults 的常量一致
powershell.exe -File tools\stock-audit.ps1 -Check DocsDefaults

# 8) 单元测试里的字面量断言是否仍等于代码里的常量
powershell.exe -File tools\stock-audit.ps1 -Check TestsVsCode

# 9) 文档里写的 `XxxDefaults.Member` 是否真的存在（含嵌套类型）
powershell.exe -File tools\stock-audit.ps1 -Check DocsRefs

# 10) StockValues 常量的值是否等于它 KDoc 引用的那一个 APK dimen
powershell.exe -File tools\stock-audit.ps1 -Check StockVsApk

# 11) StockValues KDoc 引用的 token 是否真的被 APK 引用（死资源会被报出来）
powershell.exe -File tools\stock-audit.ps1 -Check TokenLiveness

# 12) docs/dev 表格里 "token + 12dp/12sp" 的行是否与 APK dimen 一致
powershell.exe -File tools\stock-audit.ps1 -Check ResearchValues

# 13) StockValues 里的 Color(0x…) 是否等于它 KDoc 引用的 couiColor* 的官方值
powershell.exe -File tools\stock-audit.ps1 -Check ColorVsApk

# 14) 源码里 XxxDefaults.Member 的引用是否真的存在于该对象（ktlint 查不到这类错）
powershell.exe -File tools\stock-audit.ps1 -Check DefaultsRefs
powershell.exe -File tools\stock-audit.ps1 -Check LintBudget
powershell.exe -File tools\stock-audit.ps1 -Check PlanSync

# 15) dropShadow 的形状是否与同链 squircleBackground 同族（影子/填充两套形状系统）
powershell.exe -File tools\stock-audit.ps1 -Check ShadowShape

# 16) 公开 composable 参数是否只在声明里出现一次（= 静默空转）
powershell.exe -File tools\stock-audit.ps1 -Check UnusedParams

# 17) Defaults 成员引用的常量是否真的在模块内有声明（编译期才能发现的那类错）
powershell.exe -File tools\stock-audit.ps1 -Check DefaultsResolve

# 18) RmuixTheme.colorScheme.X / textStyles.X 的成员名是否真实存在
powershell.exe -File tools\stock-audit.ps1 -Check ThemeMembers

# 19) 任意 `SomeObject.member` 的成员名是否在该对象里声明过
powershell.exe -File tools\stock-audit.ps1 -Check ObjectMembers

# 20) `@Serializable sealed` 层级的每个直接成员是否也标了 `@Serializable`
powershell.exe -File tools\stock-audit.ps1 -Check SerializableScope

# 全部
powershell.exe -File tools\stock-audit.ps1 -Check All
```

## 输出约定

每个检查打印 `checked / ok / issues` 三个计数，并列出问题条目。**退出码为 0 表示没有发现问题**，非 0 表示有需要人工确认的条目。

脚本只会报告，**不会修改任何文件**。

> 注意：脚本以 `exit` 结束。用 `& ([scriptblock]::Create(...)) -Check X` 在**同一个宿主进程**里连续跑多个检查时，第一次 `exit` 会终止宿主；需要逐条跑，或直接用 `powershell.exe -File tools\stock-audit.ps1 -Check X` 让每条检查各起一个进程。本仓环境验证过：二十二项检查在 2026-11-13 全部退出 `0`。

## 已知的启发式边界

这些检查是文本启发式，有两类系统性误报，遇到时请人工确认而不是直接改代码：

1. **多值注释**：一个注释块同时描述常态与按压/禁用值时，逐函数的集合比较会把它当成"缺失"。
2. **跨函数窗口**（已修）：`DocVsCode` 向上查找注释时曾会越过上一个函数的声明，把 A 的注释配给 B 的代码；2026-11-12 起遇到声明行即停止。

`Tokens` 检查里，刻意书写的 token 前缀（如 `coui_number_picker_`）会被计入 `prefix` 而不是错误。

`DocsTokens` 检查会跳过：`coui_xxx_*` 这种**通配写法**（负向先行断言）、包路径片段（`couiswitch`）、以及以 `_` 结尾的 token 前缀。2026-11-12 实跑：739 个引用、0 问题（修掉 3 处文档里的名字错误之后）。

`SerializableScope` 看的是**编译不报错、测试也抓不到**的那类错：
`@Serializable sealed interface` 生成的是**封闭多态**序列化器，多态作用域只包含「自己也标了
`@Serializable`」的直接成员；少一个注解，该成员就从作用域里静默消失，直到运行时第一次
`rememberSaveable` 存档才抛 `JsonEncodingException`。该检查按花括号层级取直接成员，
已在 `Route.kt` 上做过正反两次验证（2026-11-16）。

`ColorVsApk` / `DocVsApk` 的 `?attr/couiColor*` 只在 **`Theme.COUI` / `Theme.COUI.Dark`**（及 `Theme.COUI.Main`）里解析：`COUIOverlay.Theme.Blue.*`、`COUIOverlay.Theme.Single.*` 等强调色主题会重复映射同一个属性且排在前面的文件位置，按"第一次出现"取会拿到错的颜色（例如把 `couiColorFocusOutline` 解析成青绿色 `#6636D9D9`）。

`ResearchValues` 只比"首列含 `coui_*` 且某列正好是 `12dp` / `12sp`"的表格行，并且跳过含"不存在 / 未找到 / 无此 / 不确定 / 猜测"的行；正文散文里的数值、以及一行里写了两个 token 的合并行（如 `168 / 256dp`）都不比。

`DocsTokens` 只扫 `docs/components` 与 `docs/zh_CN`；`docs/dev` **不在范围内**（那里常出现"某 token 不存在"这样的否定引用，直接扫会全是噪声）。因此研究文档里的 token 名需要人工核对 —— 2026-11-12 手工核过一次：307 个引用里 11 个在本 APK 中无声明，逐个确认后均为否定引用或已修正。

`TokenLiveness` 会扫描 `com\coui` / `com\oplus` / `com\android\settings` / `com\support` 下约 1.1 万个 Java 文件（约 9 秒）与 `res` 下全部 XML，建立"被引用的资源名"集合，再与本库 KDoc 引用的 token 求差。**它只能发现"在 Java/XML 里没有任何字符串引用"的死资源**：通过 `assets`、反射或运行时拼名引用的资源会被误判，因此新报出的项要先手工确认再拉白。

`TestsVsCode` 现在能解三层：字面量（`val X = 12.dp`）→ 一层别名（`val X = YyyStock`）→ **同对象内的算术表达式**（`val Travel = Width - ThumbPadding * 2 - ThumbSize`）。算术求值前会先把成员名替换成数字并校验"只剩数字与运算符"，不会执行任意代码。

`StockVsApk` 只比较「KDoc 恰好引用一个 `coui_*` dimen」的常量；KDoc 里出现 ` / ` 或多个 token 时跳过（那往往是"同一族多个档位"），文档里写明"derived / half / minus / split / per / − 2"时也跳过（值来自算术推导）。

> **脚本文件必须是 UTF-8 with BOM**：Windows PowerShell 5.1 会按 ANSI 解码无 BOM 的 `.ps1`，中文标点（如 `、`）会被拆坏并导致整份脚本语法错误。本文件与 `stock-audit.ps1` 均已保存为带 BOM。

**2026-11-12 敏感性核查**：对五个检查各做了一次负向探针（临时插入错误内容 → 确认报错 → 删除 → 确认归零），结果与发现的真实问题如下：

| 检查 | 探针是否触发 | 触发条件 / 发现 |
| :--- | :--- | :--- |
| `DocVsApk` | 是（修好正则后） | **曾漏掉全部 `coui_color_*` 蛇形 token**：正则写成 `coui(?:Color|color_)…`，而实际 token 是 `coui_color_xxx`（`coui` 后面直接跟 `_`），该分支永远匹配不到。已改为 `couiColor…|coui_color_…` |
| `DocsValues` | 是 | 只认 "`token` = 12dp" 这类紧邻写法 |
| `DocsDefaults` | 是（需表格行） | **只检查表格**：`| Member | … | 12.dp |` 且所在小节标题里要有 `XxxDefaults`；正文散文里的 `XxxDefaults.Member = 12dp` 不检查 |
| `DocsRefs` | 是 | 引用不存在的 `XxxDefaults.Member` 会报 |
| `DocsPair` | 是（需表格行） | **只比较表格里"首列是裸标识符且值列是 dp/sp"的行**，页面行数不同不会报 |
| `BreakingReversal` | 是（2026-11-14 探针） | 只认 `数值单位 → 数值单位` 的成对写法（`80dp -> 72dp`）；散文里写成「从 80dp 改成 72dp」这类无箭头的表述抓不到 |
| `TestWritePaths` | 是（2026-11-14 探针） | 只认 `File("相对路径")`；用 `Paths.get`、`File(configDir, ...)` 或拼接字符串的写法不检查；`File("build/...")` 视为合法（模块构建目录）|
| `ResearchHeadings` | 是（2026-11-14 探针） | 只看 `docs/dev/*.md` 的 `##` 标题；编号重复与同名标题才报，`###` 三级标题不检查 |


`DocVsCode` 向上最多找 5 行同色家族注释，遇到**任何声明行**（`fun` / `val` / `var` / `object` / `class`）就停 —— 这一条是 2026-11-12 补的：之前它会越过相邻函数的声明去抓更上面的注释，5 条误报里有 3 条是这个原因。剩下 2 条属另一类：注释里写的是"这个函数出现的所有颜色"（例如同时写了亮/暗与禁用三色，或注释覆盖了相邻两个函数），而代码行只有其中两个。

`DocsValues` 只匹配**紧邻的严格写法**：`` `coui_x` = 12dp `` 与 `` `coui_x` = **12dp** ``，并解析一层 `@dimen` 间接引用。故意不匹配 `2dp（`coui_x`）` 或 `（`coui_x`; 4dp between items）` 这类松散叙述 —— 实测这种窗口匹配会把邻近的无关数字当成 token 的值，产生大量误报（一次原型跑出 6 条，全是假阳性）。剩余 6 个"未匹配"条目是 `XxxDefaults` 表里的 `12.dp` 写法，由 `DocsDefaults` 负责。

## 已验证的实跑结果（2026-11-13）

| 检查 | 规模 | 结果 |
| :--- | :--- | :--- |
| `Tokens` | 384 项 | 0 问题（371 命中 + 13 前缀） |
| `DocsTokens` | 1,022 项 | 0 问题（1,015 命中） |
| `DocsValues` | 113 项 | 0 问题 |
| `DocsPair` | 297 行 | 0 问题 |
| `DocVsApk` | 40 组 | 0 问题 |
| `DocVsCode` | 42 组 | 0 问题 |
| `DocsDefaults` | 76 组 | 0 问题 |
| `TestsVsCode` | 275 条断言 | 0 问题 |
| `DocsRefs` | 789 处引用 | 0 问题 |
| `StockVsApk` | 138 精确 + 3 仅单位 + 0 推导 + 11 多引用 | 0 问题（2026-11-14 起也接受无 `coui_` 前缀的 dimen） |
| `TokenLiveness` | 323 引用 / 36,467 引用点 | 0 问题（36 个死资源在白名单并写明原因） |
| `ResearchValues` | 82 行 | 0 问题 |
| `ColorVsApk` | 59 组 / 20 跳过 | 0 问题 |
| `DefaultsRefs` | 936 引用 / 8 跳过 | 0 问题 |
| `LintBudget` | 166 个库内 Kotlin 文件 / 2 处违规 | 0 问题（基线 2，仅剩 `SlideView` 的两条故意不修） |
| `PlanSync` | 237 项清单 / 237 个脏文件 | 0 问题（清单每次从 `git status` 重建） |
| `ShadowShape` | 7 条同链配对 | 0 问题 |
| `UnusedParams` | 1,725 个参数 | 0 问题 |
| `DefaultsResolve` | 17 处常量引用 | 0 问题 |
| `ThemeMembers` | 245 处引用（53 色 / 16 文字样式） | 0 问题 |
| `ObjectMembers` | 1,014 处引用 / 47 个对象 | 0 问题 |
| `ViewportUnits` | 1 个 24-unit 绘制块 | 0 问题 |
| `BreakingReversal` | 168 篇文档 | 0 问题（新增：BREAKING 的 `A → B` 与正文的 `B → A` 同时出现即报） |
| `TestWritePaths` | 180 个测试文件 | 0 问题（新增：测试里用相对路径写文件会在模块目录留下杂物） |
| `ResearchHeadings` | 38 篇研究文档 | 0 问题（新增：编号标题重复、同名标题重复）|

## 不变式：每个 `checked` 都必须大于 0

上面那张「各检查规模与结果」表同时是**基线**：每个检查的 `checked` / 规模列都有具体数字。
如果某次运行某个检查打印出 `checked=0`，说明它的**正则或路径失效了**（不是「没问题」）——
历史上 `DocVsApk` 就因为正则写错而永远匹配不到，报告永远是 0 问题。

两个相关的保护：

1. 环境不满足而跳过的检查会累计到 `$script:skippedChecks`，末尾打印
   `N check(s) were SKIPPED ... the run is not a full verification.`；
2. `PlanSync` 找不到第九节的文件清单时**报错**而不是跳过。

## 附：lint harness

仓库本身没有提交 ktlint 运行脚本；本轮复核使用的是临时脚本 `%TEMP%\rmuix_lint.ps1`，它封装了 ktlint 1.8.0 + Compose rules 0.6.8，支持：

```powershell
$sb = [scriptblock]::Create((Get-Content -Raw -LiteralPath "$env:TEMP\rmuix_lint.ps1"))
& $sb -Root 'C:\R1p0\rmuix' -Mode lint   'path/to/File.kt'   # 只报告
& $sb -Root 'C:\R1p0\rmuix' -Mode format 'path/to/File.kt'   # base64 结果写入 %TEMP%\rmuix_lint.out
```

`-Mode format` 的输出是「绝对路径 \t base64(格式化后的全文)」，写回时需要自行解码并按 CRLF 落地。

### `LintBudget` 的解析失败检测

`ktlint` 遇到语法错误会抛 `KtLintParseException` 并**停止**，此时违规数会变小 —— 与"改好了"无法区分。第 162 轮起脚本把 stderr 落盘并单独检查：

```text
LintBudget files=156 violations=0 baseline=2 rules=0
  ktlint could not parse a file (1 stderr lines); the violation count above is not a census
    Exception in thread "main" ... KtLintParseException: 79:12 Expecting ')'
```

这样"语法坏掉"与"违规减少"是两种不同的输出。验证方法：临时在任一 `.kt` 末尾追加 `fun broken( {`，检查应报出上面两行；恢复后应回到 `violations=2`。

### `DocsRefs` 的边界：泛型函数

`DocsRefs` 校验文档里 `XxxDefaults.member` 的引用能不能在对应 `object` 里找到。成员提取用一条正则，
**最初不认泛型函数**：

```kotlin
fun <T> slideSpec(): TweenSpec<T> = ...
```

`fun\s+(\w+)` 匹配到的是 `<T>`，函数名被吞掉，于是文档里写 `SearchBarDefaults.slideSpec()` 会被判为
「成员不存在」。第 175 与第 179 轮各踩一次（`colorTransitionSpec()`、`slideSpec()`）。

现已改为 `(?:<[^>\r\n]+>\s*)?(\w+)`，允许函数名前的类型参数。验证方式：

1. 文档写一个**不存在**的泛型函数名 → 仍报 `searchbar.md: SearchBarDefaults.slideSpecBogus`（说明没放水）；
2. 写真实存在的 → 0 问题。

> 仍然只支持单层泛型参数（`<T>`、`<T : Any>`），嵌套尖括号或函数类型参数不在支持范围内。
### `ShadowShape`：影子与填充必须同族

`dropShadow(shape = …)` 与 `.squircleBackground(…)` 串在同一条 modifier 链上时，如果影子用
`RoundedCornerShape` 而填充用 squircle，两者的轮廓在圆角较大时会错开 —— 同一处卡片／弹窗出现**两套形状系统**。

检查方式（文本启发式）：

1. 找每个 `.dropShadow(`；
2. 取其后 25 行作为窗口；
3. 窗口里有 `squircleBackground(` 或 `squircleSurface(` → 这一对需要形状同族；
4. 影子的 `shape =` 若是 `RoundedCornerShape(`，或指向文件内 `val shapeXxx = RoundedCornerShape(` 的变量 → 报错。
5. 同一条链上 `.clip(` 的实参若是 `RoundedCornerShape(`（或上述变量）→ 报错（会把 squircle 裁回圆弧）。
6. 文件里出现 squircle 表面时，每个 `SquircleShape(` 调用（取其后 5 行）必须含 `squircleEnabled`。

已验证它能报出真实问题：

| 故意制造的缺陷 | 检查输出 |
| :--- | :--- |
| `Snackbar.kt` 的 `SquircleShape` 改回 `RoundedCornerShape` | `Snackbar.kt:474 dropShadow uses a RoundedCornerShape in a squircle chain` |
| `Card.kt` 的 `SquircleShape(...)` 去掉 `squircleEnabled` | `Card.kt:211 SquircleShape without squircleEnabled in a squircle file` |
| `NavigationRail.kt` 的 `.clip(...)` 改回 `RoundedCornerShape` | `NavigationRail.kt:481 clip uses a RoundedCornerShape in a squircle chain` |

改回后均为 0 问题。

第二类检查（`SquircleShape` 必须带 `squircleEnabled`）针对**退化开关漏传**：`squircleBackground` 在 shader
不可用时会退化为圆角矩形，若同一处的 `SquircleShape` 仍是默认 `true`，影子和聚光就会与填充分叉。

> **边界**：25 行窗口是启发式的，它无法证明「这两行属于同一条 modifier 链」；跨文件的共享 `Shape` 变量不在检测范围；
> 反过来，一个文件里恰好相邻但无关的 `dropShadow` 与 `squircleBackground` 可能被误报 —— 需要人工确认。
### `UnusedParams`：只出现一次的参数等于静默空转

公开 `@Composable` 的参数如果**在文件里只出现一次**（就是声明本身），说明函数体从不读它 —— 调用方传了也没有任何效果。
这比"没有这个参数"更糟：它看起来能用。

检查方式：只扫 `rmuix-ui/src/commonMain` 与 `rmuix-preference/src/commonMain` 里**上一行是 `@Composable`** 的 `fun`，
解析参数列表（每行 4 空格以上缩进的 `name: Type`），统计该名字在**整个文件**里的出现次数，`<= 1` 即报错。

已验证：给 `TooltipDismissButton` 加一个 `unusedProbeParam: Boolean = false` →
`Tooltip.kt:583 parameter 'unusedProbeParam' is declared but never used`；删除后 0 问题。

> **边界**：只在**同一文件**内计数，因此"参数只在别的文件里被使用"（几乎不存在）或"参数名在注释里也出现"都会干扰计数；
> 接口方法被排除（它们没有 `@Composable` 前置行），但**非 composable 的普通函数不在检查范围**。
### `ThemeMembers`：主题成员名的拼写检查

`RmuixTheme.colorScheme.<member>` 与 `RmuixTheme.textStyles.<member>` 的成员名写错，是**编译错误**，
而 ktlint、`DocsRefs`、`TestsVsCode` 都看不见。检查方式：从 `Colors.kt` / `TextStyles.kt` 读出
`var x by mutableStateOf(...)` 的成员名集合，再扫两个模块里所有 `colorScheme.X` / `textStyles.X`。

这一项**第一次跑就抓到一个真错误**：`NavigationRail.kt` 用了 `colorScheme.onSurfaceVariant`，
而 `Colors` 里只有 `onSurface` / `onSurfaceSecondary` / `onSurfaceVariantSummary` /
`onSurfaceVariantActions` —— **`onSurfaceVariant` 不存在**。已按官方 `couiColorLabelSecondary`
（`#8A000000` / `#8AFFFFFF`，约 54%）对齐到 `onSurfaceVariantSummary`（60% / 50%）。

负向验证：注入 `colorScheme.notARealMember` → `Badge.kt:264 colorScheme.notARealMember is not a Colors member`；
删除后 0 问题。

> **边界**：`textStyles.copy(...)` 这类对**同名局部变量**的方法调用会被误报，已对 `copy` / `equals` /
> `hashCode` / `toString` 单独放行；其它以同名局部变量调用自定义方法的情况仍可能误报。
### `ViewportUnits`：24 单位视口里不能再乘一次密度

`drawWithCache` 里 `withTransform({ scale(size.width / 24f, ...) })` 之后，绘制坐标已经是**源矢量的视口单位**，
不是 dp。此时写 `CheckboxDefaults.UncheckedBoxSize.toPx()` 会按密度再换算一次：16.4dp 在 24dp 的盒子里变成
51dp，未选中态的**外框**因此套在内容外面好几圈 —— 内容（勾、横线）用的是无量纲字面量，所以看起来"框变大、里面正常"。

第 196 轮把 `16.4f / 3.2f / 3.8f / 1.3f` 这些字面量替换成 `Dp` token（为了满足"不留魔法数"），
替换时改成了 `.toPx()`，于是引入回归；真机预览一眼可见，而 ktlint、单测与其余检查全部看不见（token 值是对的，错的只是单位）。

检查方式有两条规则，因为缺陷有两种形态：

1. **窗口规则**：找 `scale(size.width / <n>f` 的每一处，扫其后 120 行，出现 `.toPx()` 即报错。
2. **辅助函数规则**：把窗口里出现的每个 `name(` 与同文件内 `fun name(` 的声明配对，再扫这些函数**整个函数体**。
   因为 `drawCheckboxResting` 的调用点在第 178 行、函数体在第 421 行、缺陷在第 453 行 —— 只靠窗口会漏掉一半。

两次都用**真实缺陷**做过负向验证：

| 故意保留的缺陷 | 检查输出 |
| :--- | :--- |
| 动画分支 `UncheckedBoxSize.toPx()` | `Checkbox.kt:192 .toPx() inside a 24-unit viewport scale (use .value)` |
| 静止分支 `IndeterminateTopInset.toPx()`（在 271 行之外） | ``Checkbox.kt:453 .toPx() inside drawCheckboxResting, a helper of the 24-unit viewport block (use .value)`` |

改回 `.value` 后均为 0 问题。

> **边界**：只认 `scale(size.width / Nf)` 这种**常量**视口；用变量（如 `scale(unit / 3f, ...)`）缩放的块不在检测范围。
> 辅助函数规则只认**同文件内**的顶层 `fun`，跨文件的绘制辅助、lambda 参数、同名重载会漏报或报错行号偏移；
> 120 行窗口也无法证明"这些 `.toPx()` 与缩放属于同一个 lambda"。

### `ObjectMembers`：任意对象的成员名拼写检查

`SomeObject.member` 里 `member` 写错同样是编译错误。做法：收集本仓库所有**顶层 `object X {`**，
按花括号配对取出对象体，从中提取 `val` / `var` / `fun`（含泛型 `fun <T>`）/ 嵌套 `class` /
`enum class` / `data class` / `interface` 的名字作为成员集合；再扫两个模块里全部 `X.<member>` 引用。

规模：44 个对象、892 处引用、0 问题。

负向验证：`SliderDefaults.notARealMember` →
`Badge.kt SliderDefaults.notARealMember is not a member of SliderDefaults`；删除后 0 问题。

> **边界**：只收集**顶层的** `object`，不收集 `companion object` 与类内嵌套对象；带超类型的 `object`
> 其成员可能来自父类型，本检查会误报，因此实践中应先确认；`blur` / 扩展属性等语法不在提取范围。
### 写代码与改文件的两条硬规则（第 196 轮事故之后）

**一、加 import 必须和加符号在同一次编辑里完成，并对照“符号 → 包名”自查一遍。**

当时的二十一项检查全绿时，`:rmuix-ui:compileAndroidMain` 仍然报了约四十个错误，其中 8 个文件是纯 import 缺失
（`luminance`、`Dp` 与 `dp` 同时需要、`sp`、`TweenSpec`、`isSquircleEnabled`、`Immutable`、`RoundedCornerShape`、
`PathParser`）。注意 `PathParser` 的正确包是 `androidx.compose.ui.graphics.vector.PathParser`；写成
`androidx.compose.ui.graphics.PathParser` 会变成一条**新的**编译错误而不是修好。

**二、批量改写 `.kt` 必须先断言“行数守恒”。**

第 196 轮我用一个脚本改 import，脚本先把新 import 追加到数组末尾，再取“最后一个 import 的下标”重建文件，
于是 import 之后的整个文件体被丢弃：`SearchBar.kt` 335 → 75 行、`Tooltip.kt` 909 → 100 行、
`TopAppBar.kt` 732 → 61 行、`NavigationRail.kt` 735 → 73 行，另有两个未跟踪文件同样被截断。
**它们全部通过 ktlint 与当时的二十一项检查**（语法完整，只是内容没了），唯一信号是 `git diff --stat` 的 `-281/-865`。

因此脚本里必须写成：

```powershell
if ($out.Count -lt $lines.Count * 0.95) { throw "rewrite shrank $path: $($lines.Count) -> $($out.Count)" }
```

新建 `.kt` 则先 `git add -N <path>` 登记意图，否则截断后无任何副本可恢复（`%TEMP%\rmuix-backup-*` 是唯一兜底）。

**三、`import-ordering` 的实际要求是“ASCII 序 + `kotlin.*` 排最后”。**

我先后按“序数”“忽略大小写”“IntelliJ 混合（忽略大小写 + 次序数）”排了三次，每次都在另一些文件上产生新违规。
成立的那一版是：把 `import kotlin.` 开头的行单独取出排在末尾，其余按 `[System.StringComparer]::Ordinal` 排序。
排完 `LintBudget` 应从 8 违规回到基线 2（两条都是 `SlideView.kt` 的 `compose:param-order-check`）。

### 白名单新增：`coui_icon_loading`

第 208 轮把 `coui_icon_loading.xml`（`drawable/` 里 24 视口、1.4 描边的 270 度开口圆）作为 Switch loading 态的图形来源引用，但**没有任何 java/xml 读它** —— `TokenLiveness` 因此报「cited but no java/xml reference exists」。

它和其余死资源不同：资源本身存在且有明确几何（不是"值存在但没人用"），只是官方把它当作位图 `switch_loading.png` 的矢量孪生。已加入白名单并写明原因，避免把这次引用判成假归因。
### `TokenLiveness` 的边界：只查"有没有文本引用"

`TokenLiveness` 判定一个 token 是活的，标准是**它的名字在 java/xml 里出现过**。这抓不住"**赋给字段后再也不读**"的情况。

实例（第 164 轮）：`coui_textinput_line_padding` 在 `COUIEditText` 第 540 行被赋给 `mLinePadding`，而 `mLinePadding` 此后从未被读取 —— 是死资源，但 `TokenLiveness` 认为它是活的（`cited=226 / issues=0`）。

判断这类 token 需要**逐字段追引用**，无法用文本匹配完成。发现方式只能是人工读一遍赋值点。
### `-ExtraDump`：为什么还要扫 Gallery 的 dump

`TokenLiveness` 默认扫 `-Dump`（`C:\dump\settings_decompiled`）；ColorOS 17 的浮动导航栏 **accessory 版**
（`com.coui.appcompat.bottomfloatingaccessorynavigationbar.*`）只出现在 Gallery 里，Settings 没打进去。
因此脚本新增 `-ExtraDump`（默认 `C:\dump\gallery3d_decompiled`），额外收集它 `sources\com\coui` 的 Java 引用
与 `resources\res` 的 `@type/name` 引用。

注意：只认**引用**，不认 `<color name="...">` 这种定义，所以"有定义、没读者"的 token 仍会被报出来。
第 63 轮就是靠这条抓出 `..._menu_layer_1_color` / `..._menu_layer_2_color` 在 Gallery 里根本没有读者，
于是没有把它们写进 `FloatingNavigationBarDefaults`。


### 复核"只被引用一次"的 token（第 166 轮的一次审计）

`TokenLiveness` 只判"有没有引用"。更细的一层是"**只被引用一次**" —— 若那一次正好是"赋值给一个从不读取的字段"，该 token 就是死的。

第 166 轮的做法与结果：

1. 用一次遍历统计全部 `R.dimen|color.<name>` 与 `@dimen|color/<name>` 的出现次数（java + xml，排除 `R.java` / `public.xml` / `dimens.xml` / `colors.xml`）；
2. 与本库引用的 268 个 token 求交，得到 **88 个"只被引用一次"**；
3. 抽查其中 10 个（`coui_dot_diameter`、`coui_large_width`、`coui_medium_width`、`coui_small_width`、`coui_color_bar`、`coui_navigation_shadow_height`、`coui_switch_padding`、`coui_snack_bar_shadow_size`、`coui_tab_default_color`、`coui_stepper_text_color_select`）—— **全部是活的**：有的在 XML 里被引用，有的是 `setLayoutParams` / `setElevationToView` / `mPadding = ...` 的直接实参。

**结论：没有新增死资源。** 已知的两个死资源（`coui_textinput_line_padding`、`coui_hint_red_dot_rect_radius`）都是"赋值给从不读取的字段"这一种形态，靠第 165 轮的字段扫描已找出。

这条审计因此**没有做成检查项** —— 88 个命中里 0 个真问题，噪声率过高。
## `stock-value-crosscheck.ps1`：临时核对，不是门槛

`stock-audit.ps1` 的 25 项检查全都在验证"文本里写的和 APK 里的是否一致"，但有一类值天生**不该相等**：本地常量常是 APK 数值的推导结果（`14dp - 2 × 5.5dp = 3dp`）、画布公式的记录（`mSelectedValueWidth` 18dp），或来源 dimen 其实是死资源（`coui_number_picker_view_padding_vertical` 无人读）。

`tools\stock-value-crosscheck.ps1` 就是为这类情况写的**建议性**报告：

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File tools\stock-value-crosscheck.ps1
```

它对每个 `internal val X = N.dp|sp` 取**紧邻上方 KDoc 里出现的全部 `coui_*`**（不再向更远处嗅探，否则会把邻居的 token 算进来，产生大量假差异），逐个别名比对后取最接近的一个，再分类：

| 分类 | 含义 |
| :--- | :--- |
| `exact` | KDoc 引用的 token 与常量相等 |
| `derived` | KDoc 说明了推导（`minus` / `plus` / `divided` / `half` / `/ 2` / `×`） |
| `unread` | KDoc 说明该 token 没有读者（`never read` / `nothing reads` / `never referenced`） |
| `local` | KDoc 说明这是本地选择（`no stock token` / `library choice`） |
| `check` | 以上都不是 —— 唯一值得人看一眼的一类 |

默认永远 `exit 0`；加 `-Strict` 时只要有 `check` 就 `exit 1`。**不要**把它并入 `stock-audit.ps1`：它的判定依赖 KDoc 措辞，作为门槛会制造噪声。

当前基线：`files=19 exact=25 derived=4 unread=0 local=0 check=0`。覆盖率天然有限：多数 `*StockValues.kt` 的 KDoc 引用的是 **attr**（`couiSmallWidth` 等）而不是 dimen，脚本只比对能在 `dimens.xml` 里查到的 `coui_*` 名字，因此只覆盖约 1/4 的常量。
## 探测系统框架（`oplus-framework.jar` + dexsql）

有些控件的行为不在 `settings_decompiled` 里，而在 **框架**（`/system/framework/oplus-framework.jar`）。这类问题以前只能"记录但不可解"，现在可以查：

```powershell
# 1) 拉取（静默读取，不改设备状态）
adb -s <serial> pull /system/framework/oplus-framework.jar $env:TEMP\oplus-fw\oplus-framework.jar

# 2) 查类 / 成员（dexsql：C:\2chain\dexsql\dexsql-windows-amd64.exe）
dexsql query --input $jar --sql "SELECT java_name FROM types WHERE java_name LIKE '%MetaBall%'"
dexsql query --input $jar --sql "SELECT name, type_descriptor FROM fields WHERE class_descriptor = 'Lcom/oplus/view/material/OplusMaterialMetaBallParams;'"
dexsql query --input $jar --sql "SELECT name, descriptor, param_descriptors FROM methods WHERE class_descriptor = '<descriptor>'"

# 3) 反汇编类或单个方法（smali，含常量与校验分支）
dexsql decode --input $jar --file-id 1 --class 'Lcom/oplus/view/material/OplusMaterialMetaBallParams;'
dexsql decode --input $jar --file-id 1 --method 'Lcom/oplus/view/material/OplusMaterialUtil;->setMetaBallParams(Landroid/graphics/RenderNode;Lcom/oplus/view/material/OplusMaterialMetaBallParams;)Z'
```

要点：

- jar 里是 `classes*.dex`，`--file-id` 用于在多 dex 中消歧；`query` 里先用 `SELECT file_id ...` 确认；
- `decode` 输出的是 smali，**没有 java 源码**，但足以读出常量、参数校验与调用链；
- 若调用链最终落到 `RenderNode.setOplusMaterialMetaBallParams(...)` 这类 **native** 方法，说明该行为由 hwui/Skia 原生实现，**无法在 Compose 侧等价复刻**，应当在文档里定为已知差异，而不是继续猜参数。

已用这条路径确认：浮动导航栏的 meta-ball（`index` ∈ [1,3]、`mode` ∈ {MODE_X=0, MODE_Y=1}、extension 4dp、factors 1.5/2.0）最终由 `RenderNode.setOplusMaterialMetaBallParams` 原生绘制。
