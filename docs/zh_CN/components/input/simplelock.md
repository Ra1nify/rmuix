# SimpleLock

ColorOS 的 PIN 输入指示器：官方 `COUISimpleLock`（`com.coui.appcompat.lockview`）用一行 10dp 圆点表示
已输入的密码位数。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.SimpleLock
```

## 基本用法

```kotlin
var pin by remember { mutableStateOf("") }
SimpleLock(filledCount = pin.length)
Text(text = "请输入密码")
```

6 位密码使用 `length = SimpleLockDefaults.SixCircles`：

```kotlin
SimpleLock(filledCount = pin.length, length = SimpleLockDefaults.SixCircles)
```

## 组件状态

| 状态 | 外观 |
| :--- | :--- |
| 未填充圆点 | `coui_simple_lock_outlined_rectangle_icon`：亮色 `#19000000` / 暗色 `#66FFFFFF` |
| 已填充圆点 | `coui_simple_lock_filled_rectangle_icon`：亮色 `#FF000000` / 暗色 `#E6FFFFFF` |
| 新填充圆点 | 弹簧脉冲（`SPRING_RESPONSE` 0.35、`SPRING_BOUNCE` 0.5、`CIRCLE_MAX_SCALE` 1.2） |
| 失败态 | `failed = true` 播放 800ms 抖动 + 逐点下落/淡出（`SHAKE_X_POINT`、`DELAY_FOUR` / `DELAY_SIX`） |

## 参数

| 参数名 | 类型 | 说明 | 默认值 |
| :--- | :--- | :--- | :--- |
| filledCount | Int | 已填充圆点数（自动收敛到 `0..length`） | - |
| modifier | Modifier | 应用到指示器 | Modifier |
| length | Int | 总位数；官方提供 `FOURCIRCLE` 与 `SIXCIRCLE` | SimpleLockDefaults.FourCircles（4） |
| colors | SimpleLockColors | 已填充/未填充颜色 | SimpleLockDefaults.colors() |
| animated | Boolean | 新填充圆点是否脉冲 | true |
| failed | Boolean | 上升沿播放官方失败抖动/下落动画 | false |
| reserveFailureSpace | Boolean | 预留官方 `mDrawableHeight + 150px` 下落空间（行贴顶） | false |

## SimpleLockDefaults

| 名称 | 类型 | 来源 | 值 |
| :--- | :--- | :--- | :--- |
| DotSize | Dp | `coui_simple_lock_*_rectangle_icon` 的 `<size>` | 10dp |
| DotInset | Dp | drawable 每侧 `<padding>` | 6dp |
| DotBoxSize | Dp | drawable 内在尺寸（size + padding） | 22dp |
| DotGap | Dp | `couiRectanglePadding`（`Widget.COUI.COUISimpleLock`） | 10dp |
| FourCircles | Int | `FOURCIRCLE` | 4 |
| SixCircles | Int | `SIXCIRCLE` / `SIXCIRCLETYPE` | 6 |
| FillSpringResponse | Float | `COUISimpleLock.SPRING_RESPONSE` | 0.35 |
| FillSpringBounce | Float | `COUISimpleLock.SPRING_BOUNCE` | 0.5 |
| FillMaxScale | Float | `COUISimpleLock.CIRCLE_MAX_SCALE` | 1.2 |
| FailureMillis | Int | `SHAKE_AND_FALL_TIME` | 800 |
| ShakePointsPx | FloatArray | `SHAKE_X_POINT`（裸 px） | 0, 30, -28, 14, -8, 4, -3, 0 |
| FallDistancePx | Float | `FALL_SPEED` | 250 |
| FallFadeDistancePx | Float | `FALL_HEIGHT` | 150 |
| FallRoomPx | Float | `onMeasure()` 光晕/下落预留 | 150 |
| DelayFour / DelaySix | FloatArray | `DELAY_FOUR` / `DELAY_SIX`（裸 px） | 0, 38.5, 91, 63 / 0, 38.5, 91, 63, 38.5, 70 |

## 已知差异

- 填充↔描边与清空动画已建模：每个点先画描边底层，再叠加按逐点进度控制 alpha 与半径的填充层。删除的点
  （以及清空时所有点）用 `MORPHING_FILLED_TO_OUTLINED_TIME` = **230ms** 交叉淡化；新点用官方
  `SPRING_RESPONSE` 0.35 / `SPRING_BOUNCE` 0.5 弹簧进入，其过冲即 `CIRCLE_MAX_SCALE` 脉冲。
  失败抖动/下落已建模：`failed = true` 时整行按 `SHAKE_X_POINT` 抖动，已填充圆点以 `FALL_SPEED` = 250
  裸 px 下落、在 `FALL_HEIGHT` = 150 裸 px 内淡出，并按 `DELAY_FOUR` / `DELAY_SIX` 逐点延迟（800ms 线性）。
  未填充圆点只抖动，与 `drawFailedAnimation()` 一致。
- 光晕效果（`couiGlowEffectDrawable` + `mGlowProgressAnimators`）未复现。
- 官方 `onMeasure()` 预留 `mDrawableHeight + 150` 裸像素；库内默认保持紧凑的
  [SimpleLockDefaults.DotBoxSize] 高度，仅在 `reserveFailureSpace = true` 时按当前密度换算预留 150px
  （行贴顶）。
