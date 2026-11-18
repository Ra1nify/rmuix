# LockPatternView

ColorOS 的图案锁：官方 `COUILockPatternView`（`com.coui.appcompat.lockview`）绘制 3 × 3 点阵，拖动时把点连成路径。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.LockPatternView
import io.github.ra1nify.rmuix.kmp.basic.LockPatternDisplayMode
```

## 基本用法

```kotlin
var pattern by remember { mutableStateOf<List<Int>>(emptyList()) }
var wrong by remember { mutableStateOf(false) }
LockPatternView(
    pattern = pattern,
    displayMode = if (wrong) LockPatternDisplayMode.Wrong else LockPatternDisplayMode.Correct,
    onPatternDetected = { detected ->
        pattern = detected
        wrong = detected != expectedPattern
    },
    onPatternCleared = { pattern = emptyList() },
)
```

## 参数

| 参数名 | 类型 | 说明 | 默认值 |
| :--- | :--- | :--- | :--- |
| onPatternDetected | (List<Int>) -> Unit | 抬手时回调有序单元格下标（`0..8`，行优先） | - |
| modifier | Modifier | 应用到视图；官方 wrap-content 尺寸 312dp × 280dp | Modifier |
| enabled | Boolean | 是否接受输入并使用可用态颜色 | true |
| pattern | List<Int> | 展示的图案（单一数据源） | emptyList() |
| displayMode | LockPatternDisplayMode | `Correct`（成功色）/ `Wrong`（错误色 + 1000ms 闪烁） | Correct |
| onPatternStart | (() -> Unit)? | 在点子上开始拖动时回调 | null |
| onPatternCleared | (() -> Unit)? | 在点外按下时回调 | null |
| colors | LockPatternColors | 点/路径/成功/错误颜色与光晕 alpha | LockPatternDefaults.colors() |
| hapticFeedback | Boolean | 每加入新点触发一次触感（官方 `performHitFeedback()`） | true |
| stealthMode | Boolean | 官方 `mInStealthMode`：只画点、不画路径 | false |

## LockPatternDefaults

| 名称 | 类型 | 来源 | 值 |
| :--- | :--- | :--- | :--- |
| Width / Height | Dp | `coui_lock_pattern_view_width` / `_height` | 312dp / 280dp |
| HorizontalPadding / VerticalPadding | Dp | `coui_lock_pattern_view_horizontal_padding` / `_vertical_padding` | 21dp / 5dp |
| DotSize | Dp | `lock_pattern_dot_size` | 10dp |
| PathWidth | Dp | `lock_pattern_dot_line_width` | 2dp |
| HitFactor | Float | `COUILockPatternView.mHitFactor` | 0.6 |
| InnerGlowDiameter | Dp | `coui_lock_pattern_inner_circle.png`（xxhdpi 90px） | 30dp |
| OuterDotDiameter | Dp | `coui_lock_pattern_outer_circle.png` | 9dp |
| OuterCircleMaxScale | Float | `startOuterAnimation()` `1f -> 7f` | 7 |
| InnerAnimationMillis | Int | `startInnerAnimation()` | 230 |
| OuterAnimationMillis | Int | `startOuterAnimation()` | 460 |
| WrongAnimationMillis | Int | `startWrongAnimator()` | 1000 |
| MaxTranslateY | Dp | `color_lock_pattern_view_max_translate_y`；只喂给 `getEnterAnim()` 里从未被绘制的 `cellNumberTranslateY`（渲染上为死值） | 258dp |
| AnimateCellMillis | Int | `MILLIS_PER_CIRCLE_ANIMATING`：每 700ms 回放一格 | 700 |

## 颜色

| 槽位 | 来源 | 浅色 | 深色 |
| :--- | :--- | :--- | :--- |
| regularColor | `coui_lock_pattern_dot_color` | `#4D000000` | `#66FFFFFF` |
| pathColor | `coui_lock_pattern_path_color` | `#000000` | `#FFFFFF` |
| successColor | `coui_lock_pattern_success_color` | `#000000` | `#FFFFFF` |
| errorColor | `coui_lock_pattern_error_color` | `#EA3447` | `#EA3447` |
| outerCircleMaxAlpha | `coui_lock_pattern_outer_circle_max_alpha` | 0.1 | 0.2 |

## 已知差异

- `DisplayMode.Animate` 已实现：每 `MILLIS_PER_CIRCLE_ANIMATING` = **700ms** 点亮一个图案格（`COUILockPatternView.onDraw()`：`elapsed % ((size + 1) * 700)`、`cellIndex = elapsed / 700`、分数 `elapsed % 700 / 700`），连线按该分数追赶，完整图案多停留一拍后循环（`LockPatternDisplayMode.Animate`、`lockPatternAnimateFrame()`）；回放使用成功色实心点，不播放激活光晕/涟漪。`FingerprintMatch` / `FingerprintNoMatch` 未建模；`Correct` / `Wrong`（含 1000ms 错误闪烁）已实现。
- **更正：** `color_lock_pattern_view_max_translate_y`（258dp）与错误态无关——它只喂给 `getEnterAnim()` 的逐格
  `cellNumberTranslateY`，而 `cellNumberAlpha` / `cellNumberTranslateY` 在 `COUILockPatternView.onDraw()` 中从未
  被读取，因此不存在需要复现的可见位移。
- 100ms 连线追赶（`startLineEndAnimation`，`mFastOutSlowInInterpolator`）已建模：新激活格子的线段从手指位置插值到格子中心，时长 `LineEndAnimationMillis` = 100ms，对应 `onDraw()` 的 `lineEndX/Y` 分支。
- 触感代码（线性马达 302 / 304，普通 1 / 300）映射为 Compose `HapticFeedbackType.SegmentFrequentTick`。
- `ExploreByTouchHelper` 的虚拟无障碍单元格未复现。
