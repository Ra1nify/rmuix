# RedDot

`RedDot` 是 ColorOS 的提示红点 / 徽标（`COUIHintRedDot`），支持 6dp 小圆点、数字计数与短文本三种形态，使用 ColorOS 的 additional red 色。

## Stock ColorOS 静态复核（2026-11-12）

本轮读 Settings APK 的 `com.coui.appcompat.reddot.COUIHintRedDot` / `COUIHintRedDotHelper` 与 `Widget.COUI.COUIHintRedDot` 样式。

- 底色：`?attr/couiHintRedDotColor` 解析到 `@color/coui_color_additional_red`（亮色 `#EB3B2F` / 暗色 `#EB493D`）。组件原先注释写的是 `coui_color_primary_text_red`（`#DB382C`），没有任何样式引用它；实际用的是主题 `error` 角色（`#E94634` / `#F12522`）。
- 文字：`couiHintRedDotTextColor` = `coui_color_white`，`couiHintTextSize` = 10dp。数字徽标原先 10.5sp、文本徽标 10sp，现统一解析为 `10sp`。
- 几何本来就对：数字/文本徽标高 `couiHeight` 16dp、圆点直径 `couiDotDiameter` 6dp、圆角 `couiCornerRadius` = `?attr/couiRoundCornerFULL`（胶囊，即高度的一半）。
- `coui_hint_red_dot_rect_radius`（2.67dp）**只被赋值、从未被读取**（`mDotCornerRadius` 赋值后不再使用；圆点半径是 `height / 2`），因此它不是任何半径的来源。

- 可选描边（`withStroke`）使用 `coui_dot_stroke_width` = **1dp**、颜色 `coui_color_white`；此前是 1.5dp。

**BREAKING：** 默认底色改为 `coui_color_additional_red`（随主题），不再是主题 `error` 角色；数字徽标字号 10.5sp → 10sp。

`withStroke` 同时应用 `coui_hint_red_dot_mode_stroke_extra_diameter`（**2dp**）：官方描边模式（`POINT_ONLY_MODE_STROKE` / `POINT_NUM_MODE_STROKE`）会把圆点每个方向放大 2dp，好让白色的 `red_dot_stroke_circle` 环套在外面——所以描边圆点是 8dp 而不是 6dp，描边数字徽标是 18dp 而不是 16dp。

**尺寸：** 小/中/大三档点径（6/8/12dp）与小号文字（9dp）都以常量暴露；`RedDot` 本身只画默认尺寸，按锚点尺寸选档的逻辑在 `RedDotBox` 里。

`COUIRedDotFrameLayout` 已移植为 **`RedDotBox`**（见下文，2026-11-14）。

**字号 token 以 `dp` 声明、本库按 `sp` 渲染。** dump 里这些 `*_text_size` 都是以 `dp` 声明的（COUI 用 `getDimensionPixelSize` 读取并以 `COMPLEX_UNIT_PX` 应用，所以实际字高随密度变化）。本库在同一数值上
映射为与密度无关的 `sp`，即 1x 下的等效值。

## 引入

```kotlin
import io.github.ra1nify.rmuix.kmp.basic.RedDot
import io.github.ra1nify.rmuix.kmp.basic.RedDotDefaults
import io.github.ra1nify.rmuix.kmp.basic.RedDotMode
```

## 基本用法

### 纯圆点

```kotlin
RedDot(mode = RedDotMode.PointOnly)
```

### 数字

```kotlin
RedDot(mode = RedDotMode.PointWithNum(count = 5))   // 显示数字；1000 以上显示三个点
```

### 文本

```kotlin
RedDot(mode = RedDotMode.PointWithText(text = "NEW"))
```

### 描边

```kotlin
RedDot(
    mode = RedDotMode.PointWithNum(count = 12),
    withStroke = true,
    strokeColor = RmuixTheme.colorScheme.background,
)
```

## 把红点钉到角落（`RedDotBox`）

`RedDotBox` 对应 `COUIRedDotFrameLayout`：把红点或数字徽标钉到内容的右上角（RTL 为左上角），
边距取自 `coui_hint_red_dot_*_icon_topend_margin_*` / `coui_hint_red_dot_*_number_topend_margin_rectangle`。

```kotlin
RedDotBox(
    redDot = { RedDot(mode = RedDotMode.PointOnly) },
    anchorSize = 36.dp,
    anchorShape = RedDotAnchorShape.Rectangle,
    redDotKind = RedDotAnchorKind.Dot,
) {
    Icon(imageVector = RmuixIcons.Basic.Search, contentDescription = "Search")
}
```

边距分档与官方 `init()` 一致：

| `anchorSize` | 档位 | 圆形边距 | 矩形红点边距 | 矩形数字边距 |
| :--- | :--- | ---: | ---: | ---: |
| `< 36dp` | 小 | 1dp | 1dp | 6dp |
| `36dp` – `49dp` | 中 | 2dp | 2dp | 3dp |
| `>= 50dp` | 大 | 2dp | 4dp | 6dp |

容器尺寸 = 内容尺寸 + 两个方向各一份边距；矩形锚点由**锚点自身**让位（官方 `mMarginTopAndLeftOfAnchorView`），
圆形锚点则是让**红点**内缩（`mMarginTopAndLeftOfReddot`）。RTL 下两者都用同一个边距，与
`COUIRedDotFrameLayout.onLayout` 完全一致。

### RedDotBox 属性

| 属性名 | 类型 | 说明 | 默认值 | 必填 |
| ------------- | ---- | ----------- | ------------- | -------- |
| redDot | @Composable () -> Unit | 要钉到角落的红点/徽标 | - | 是 |
| modifier | Modifier | 作用于容器的修饰符 | Modifier | 否 |
| anchorSize | Dp | 锚点尺寸，决定边距档位（`anchorViewDpSize`） | `RedDotBoxDefaults.AnchorSize`（36dp） | 否 |
| anchorShape | RedDotAnchorShape | `Rectangle` 或 `Circle`（`anchorViewShapeType`） | `Rectangle` | 否 |
| redDotKind | RedDotAnchorKind | `Dot` 或 `Number`（`couiHintRedPointMode`） | `Dot` | 否 |
| content | @Composable () -> Unit | 被锚定的内容 | - | 是 |

## 属性

| 属性名      | 类型        | 说明                                                     | 默认值                    | 必填 |
| ----------- | ----------- | -------------------------------------------------------- | ------------------------- | ---- |
| mode        | RedDotMode  | `PointOnly`、`PointWithNum(count)` 或 `PointWithText(text)` | -                       | 是   |
| modifier    | Modifier    | 应用于红点的修饰符                                       | Modifier                  | 否   |
| color       | Color       | 背景色（默认 `coui_color_additional_red`）                | redDotDefaultColor()      | 否   |
| withStroke  | Boolean     | 是否绘制描边                                             | false                     | 否   |
| strokeColor | Color       | 描边颜色                                                 | Color.White               | 否   |
| strokeWidth | Dp          | 描边宽度                                                 | RedDotDefaults.StrokeWidth | 否  |

## RedDotDefaults 对象

| 常量名 | 类型 | 值 | 说明 |
| ------ | ---- | -- | ---- |
| BadgeHeight   | Dp   | 16.dp | 数字/文本徽标高度（`coui_height`） |
| BadgeRadius   | Dp   | 8.dp  | 胶囊圆角，高度的一半（`couiRoundCornerFULL`） |
| PointSize     | Dp   | 6.dp  | 纯圆点直径（`coui_dot_diameter`） |
| StrokeWidth   | Dp   | 1.dp   | 描边宽度（`coui_dot_stroke_width`，`coui_color_white`） |
| SmallWidth           | Dp | 16.dp | 单个数字的最小宽度（`coui_small_width`） |
| MediumWidth          | Dp | 20.dp | 两位数字的最小宽度；1000 以上也回落到此值（`coui_medium_width`） |
| LargeWidth           | Dp | 26.dp | 三位数字的最小宽度（`coui_large_width`） |
| SingleDigitMinWidth  | Dp | 16.dp | **已废弃** —— 用 `SmallWidth` |
| MultiDigitMinWidth   | Dp | 20.dp | **已废弃** —— 用 `MediumWidth` / `LargeWidth` |
| TextBadgeMinWidth    | Dp | 20.dp | 文本徽标的最小宽度 |
| BadgeHorizontalPadding | Dp | 4.5.dp | 数字徽标的水平内边距 |
| TextBadgeHorizontalPadding | Dp | 6.dp | 文本徽标的水平内边距 |

## 进阶用法

### 自定义颜色

```kotlin
RedDot(
    mode = RedDotMode.PointOnly,
    color = RmuixTheme.colorScheme.primary,
)
```
